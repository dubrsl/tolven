package org.tolven.restful;

import java.io.InputStream;
import java.security.Principal;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.tolven.api.APIXMLUtil;
import org.tolven.api.accountuser.XAccountUser;
import org.tolven.api.accountuser.XAccountUserFactory;
import org.tolven.api.facade.accountuser.XFacadeAccountUserFactory;
import org.tolven.api.facade.accountuser.XFacadeAccountUsers;
import org.tolven.api.security.GeneralSecurityFilter;
import org.tolven.api.security.Vestibule;
import org.tolven.api.security.VestibuleException;
import org.tolven.app.MenuLocal;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.ActivationLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.sso.TolvenSSO;
import org.tolven.util.ExceptionFormatter;

import com.sun.jersey.core.util.MultivaluedMapImpl;

@Path("vestibule")
@ManagedBean
public class VestibuleResources {

    public static final String DEFAULT_ACCOUNT_HOME = "/private/application.jsf";

    @Context
    private HttpServletRequest request;

    @EJB
    private ActivationLocal activationBean;

    @EJB
    private AccountDAOLocal accountBean;

    @EJB
    private MenuLocal menuBean;

    @EJB
    private TolvenPropertiesLocal propertyBean;

    private javax.naming.Context ctx;
    private static List<String> vestibuleJNDINames = new ArrayList<String>();

    private Logger logger = Logger.getLogger(VestibuleResources.class);

    public VestibuleResources() {
    }

    protected Response prepareUserAccountList(String username) throws Exception {
        TolvenUser tolvenUser = activationBean.findUser(username);
        if (tolvenUser == null) {
            return Response.status(Status.NOT_FOUND).entity("TolvenUser not found").build();
        }
        List<AccountUser> accountUsers = activationBean.findUserAccounts(tolvenUser);
        XFacadeAccountUsers uas = XFacadeAccountUserFactory.createXFacadeAccountUsers(accountUsers, tolvenUser, (Date) request.getAttribute("tolvenNow"));
        Response response = Response.ok().entity(uas).build();
        return response;

    }

    /**
     * Enter the Vestibule
     * @return
     */
    @Path("enter")
    @GET
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response enterVestibule() {
        logger.info("Vestibule entered: " + request.getUserPrincipal());
        boolean inAccount = "account".equals(TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.USER_CONTEXT, request));
        if (inAccount) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Cannot access vestibule while in an account").build();
        }
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        Date now = (Date) request.getAttribute("tolvenNow");
        TolvenUser user = activationBean.findUser(request.getUserPrincipal().getName());
        XAccountUser xAccountUser = XAccountUserFactory.createXAccountUser(user, now);
        String xAccountUserXML = APIXMLUtil.marshalXAccountUser(xAccountUser);
        List<AccountUser> accountUsers = activationBean.findUserAccounts(user);
        XFacadeAccountUsers xFacadeAccountUsers = XFacadeAccountUserFactory.createXFacadeAccountUsers(accountUsers, user, now);
        String xFacadeAccountUsersXML = APIXMLUtil.marshalXFacadeAccountUsers(xFacadeAccountUsers);
        MultivaluedMap<String, String> mvMap = new MultivaluedMapImpl();
        mvMap.putSingle(GeneralSecurityFilter.ACCOUNTUSER, xAccountUserXML);
        mvMap.putSingle(GeneralSecurityFilter.ACCOUNTUSERS, xFacadeAccountUsersXML);
        mvMap.putSingle(GeneralSecurityFilter.VESTIBULE_PASS, "true");
        mvMap.putSingle(GeneralSecurityFilter.USER_CONTEXT, "vestibule");
        AccountUser defaultAccountUser = activationBean.findDefaultAccountUser(user);
        if (defaultAccountUser == null) {
            TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.VESTIBULE_PASS, "true", request);
            TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.USER_CONTEXT, "vestibule", request);
            TolvenSSO.getInstance().updateAccountUserTimestamp(request);
            if (logger.isDebugEnabled()) {
                logger.debug("TOLVEN_PERF: @Path(vestibule/enter): " + (System.currentTimeMillis() - start));
            }
            return Response.ok(mvMap).build();
        } else {
            TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, Long.toString(defaultAccountUser.getId()), request);
            return exitVestibule();
        }
    }

    /**
     * Exit the Vestibule
     * @return
     */
    @Path("exit")
    @GET
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response exitVestibule() {
        boolean inAccount = "account".equals(TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.USER_CONTEXT, request));
        if (inAccount) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("You are not in the Vestibule").build();
        }
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        List<Vestibule> vestibules = getVestibules();
        try {
            for (Vestibule vestibule : vestibules) {
                String vestibuleRedirect = vestibule.validate(request);
                if (vestibuleRedirect != null) {
                    MultivaluedMap<String, String> map = new MultivaluedMapImpl();
                    map.putSingle(GeneralSecurityFilter.VESTIBULE_REDIRECT, vestibuleRedirect);
                    return Response.ok(map).build();
                }
            }
            MultivaluedMap<String, String> mvMap = exitVestibule(vestibules);
            logger.info("Vestibule exit: " + request.getUserPrincipal());
            if (logger.isDebugEnabled()) {
                logger.debug("TOLVEN_PERF: @Path(vestibule/exit): " + (System.currentTimeMillis() - start));
            }
            return Response.ok(mvMap).build();
        } catch (VestibuleException ex) {
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, request);
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.ACCOUNTUSER_ID, request);
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.ACCOUNT_ID, request);
            TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.USER_CONTEXT, "vestibule", request);
            for (Vestibule vestibule : vestibules) {
                try {
                    vestibule.abort(request);
                } catch (Exception e) {
                    //something has already gone wrong, do the best to clean up
                }
            }
            if (ex.getRedirect() == null) {
                return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Logout: " + ExceptionFormatter.toSimpleString(ex, "\\n")).build();
            } else {
                return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Logout & redirect: " + ex.getRedirect() + " " + ExceptionFormatter.toSimpleString(ex, "\\n")).build();
            }
        } catch (Exception ex) {
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, request);
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.ACCOUNTUSER_ID, request);
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.ACCOUNT_ID, request);
            TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.USER_CONTEXT, "vestibule", request);
            for (Vestibule vestibule : vestibules) {
                try {
                    vestibule.abort(request);
                } catch (Exception e) {
                    //something has already gone wrong, do the best to clean up
                }
            }
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    private MultivaluedMap<String, String> exitVestibule(List<Vestibule> vestibules) throws VestibuleException {
        String proposedAccountUserIdString = TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, request);
        if (proposedAccountUserIdString == null || proposedAccountUserIdString.length() == 0) {
            throw new RuntimeException(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID + " not set by vestibule");
        }
        Long accountUserId = Long.parseLong(proposedAccountUserIdString);
        AccountUser accountUser = activationBean.findAccountUser(accountUserId);
        if (accountUser == null) {
            throw new RuntimeException("accountUser does not exist with Id: " + accountUserId);
        }
        String userKeysOptional = propertyBean.getProperty(GeneralSecurityFilter.USER_KEYS_OPTIONAL);
        if (!Boolean.parseBoolean(userKeysOptional)) {
            String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
            PrivateKey privateKey = TolvenSSO.getInstance().getUserPrivateKey(request, keyAlgorithm);
            if (privateKey == null) {
                throw new RuntimeException("User requires a UserPrivateKey to log into account: " + accountUser.getAccount().getId());
            }
        }
        // Save ACCOUNTUSER in session for subsequent request so the security filters can intercept appropriately
        TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.ACCOUNTUSER_ID, String.valueOf(accountUser.getId()), request);
        TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.ACCOUNT_ID, String.valueOf(accountUser.getAccount().getId()), request);
        for (Vestibule vestibule : vestibules) {
            vestibule.exit(request);
        }
        TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, request);
        TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNT_HOME, request);
        TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.VESTIBULE_PASS, request);
        /*
         * SAFETY CHECK HERE - Don't trust the accountUserId alone, it must match user.
         */
        TolvenUser user = activationBean.findUser(request.getUserPrincipal().getName());
        if (accountUser.getUser().getId() != user.getId()) {
            throw new RuntimeException("ACCOUNTUSER DOES NOT BELONG TO USER");
        }
        // Record the time when the user logged into this particular account
        Date now = (Date) request.getAttribute("tolvenNow");
        accountUser.setLastLoginTime(now);
        String proposedDefaultAccount = TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.PROPOSED_DEFAULT_ACCOUNT, request);
        if ("true".equals(proposedDefaultAccount)) {
            activationBean.setDefaultAccountUser(accountUser);
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.PROPOSED_DEFAULT_ACCOUNT, request);
        }
        Account account = accountUser.getAccount();
        if (account.isDisableAutoRefresh() == null || account.isDisableAutoRefresh() == false) {
            menuBean.updateMenuStructure(account);
        }
        XAccountUser xAccountUser = XAccountUserFactory.createXAccountUser(accountUser, now);
        String xAccountUserXML = APIXMLUtil.marshalXAccountUser(xAccountUser);
        List<AccountUser> accountUsers = activationBean.findUserAccounts(accountUser.getUser());
        XFacadeAccountUsers xFacadeAccountUsers = XFacadeAccountUserFactory.createXFacadeAccountUsers(accountUsers, accountUser.getUser(), now);
        String xFacadeAccountUsersXML = APIXMLUtil.marshalXFacadeAccountUsers(xFacadeAccountUsers);
        String accountHome = (String) TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNT_HOME, request);
        if (accountHome == null || accountHome.length() == 0) {
            accountHome = DEFAULT_ACCOUNT_HOME;
        }
        MultivaluedMap<String, String> mvMap = new MultivaluedMapImpl();
        mvMap.putSingle(GeneralSecurityFilter.ACCOUNTUSER, xAccountUserXML);
        mvMap.putSingle(GeneralSecurityFilter.ACCOUNTUSERS, xFacadeAccountUsersXML);
        mvMap.putSingle(GeneralSecurityFilter.USER_CONTEXT, "account");
        mvMap.putSingle(GeneralSecurityFilter.VESTIBULE_REDIRECT, accountHome);
        TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.USER_CONTEXT, "account", request);
        TolvenSSO.getInstance().updateAccountUserTimestamp(request);
        return mvMap;
    }

    /**
     * Get a list of accounts that the current user is allowed to select
     * @return
     */
    @Path("accountList")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getUserAccountListXML() throws Exception {
        Principal principal = request.getUserPrincipal();
        return prepareUserAccountList(principal.getName());
    }

    /**
     * Get a list of accounts that the specified user is a member of
     * @return
     */
    @Path("accountList/{username}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getUserAccountListXML(@PathParam("username") String username) throws Exception {
        return prepareUserAccountList(username);
    }

    /**
     * Select an account
     * @param account
     */
    @Path("selectAccount")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response selectAccount(@FormParam("accountId") String accountId) {
        Principal principal = request.getUserPrincipal();
        AccountUser accountUser = accountBean.findAccountUser(principal.getName(), Long.parseLong(accountId));
        if (accountUser == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountUser not found").build();
        }
        // Now make sure that the accountUser we selected matches this user
        TolvenUser tolvenUser = activationBean.findUser(principal.getName());
        if (accountUser.getUser().getId() != tolvenUser.getId()) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountUser not associated with this user").build();
        }
        TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, String.valueOf(accountUser.getId()), request);
        return exitVestibule();
    }

    /**
     * Select an accountUser
     * @param accountUser
     */
    @Path("selectAccountUser")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response selectAccountUser(@FormParam("accountUserId") String accountUserId) {
        Principal principal = request.getUserPrincipal();
        TolvenUser tolvenUser = activationBean.findUser(principal.getName());
        TolvenSSO.getInstance().removeSessionProperty("accountUserId", request);
        AccountUser accountUser = activationBean.findAccountUser(Long.parseLong(accountUserId));
        if (accountUser == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountUser not found").build();
        }
        // Now make sure that the accountUser we selected matches this user
        if (accountUser.getUser() != tolvenUser) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountUser not associated with this user").build();
        }
        // Save ACCOUNTUSER_ID in session for subsequent requests
        TolvenSSO.getInstance().setSessionProperty("accountUserId", String.valueOf(accountUser.getId()), request);
        TolvenSSO.getInstance().setSessionProperty("accountId", String.valueOf(accountUser.getAccount().getId()), request);
        return Response.ok().type(MediaType.TEXT_PLAIN).entity("Account " + accountUser.getAccount().getId() + " selected  based on accountUserId " + accountUser.getId()).build();
    }

    /**
     * Help selectAccount will be intercepted in the Vestibule to redirect the user to select an account from available accounts, if any
     * 
     * @return
     */
    @Path("help/selectAccount")
    @GET
    @Consumes
    @Produces
    public Response helpSelectAccount() {
        return Response.ok().build();
    }

    private javax.naming.Context getContext() {
        if (ctx == null) {
            try {
                ctx = new InitialContext();
            } catch (Exception ex) {
                throw new RuntimeException("Could not create InitialContext", ex);
            }
        }
        return ctx;
    }

    @Path("test")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response test() {
        return Response.ok().build();
    }

    private List<Vestibule> getVestibules() {
        List<Vestibule> vestibules = new ArrayList<Vestibule>();
        for (String vestibuleJNDIName : vestibuleJNDINames) {
            try {
                Vestibule vestibule = (Vestibule) getContext().lookup(vestibuleJNDIName);
                vestibules.add(vestibule);
            } catch (Exception ex) {
                throw new RuntimeException("Could lookup: " + vestibuleJNDIName, ex);
            }
        }
        return vestibules;
    }

    static {
        String propertyFileName = VestibuleResources.class.getSimpleName() + ".properties";
        InputStream in = VestibuleResources.class.getResourceAsStream(propertyFileName);
        if (in != null) {
            Properties properties = new Properties();
            try {
                properties.load(in);
            } catch (Exception ex) {
                throw new RuntimeException("Could not load vestibuleJNDINames from: " + propertyFileName, ex);
            }
            String vestibuleJNDINamesString = properties.getProperty("vestibuleJNDINames");
            if (vestibuleJNDINamesString == null) {
                vestibuleJNDINames = new ArrayList<String>();
            } else {
                vestibuleJNDINames = Arrays.asList(vestibuleJNDINamesString.split(","));
            }
        }
    }
}
