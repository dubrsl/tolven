package test.org.tolven.ldap;

import java.util.Hashtable;

import javax.naming.Context;

import junit.framework.TestCase;

import org.tolven.ldap.DefaultLdapManager;
import org.tolven.ldap.LdapException;
import org.tolven.ldap.PasswordExpiring;
import org.tolven.ldap.PasswordPolicy;

public class TestPasswords extends TestCase {
    private DefaultLdapManager pwm;
    private char[] initialPwd = null; // Password created when new user was created.
    private static final String testUser = "billy";
    private static final String testPassword = "erwt0!1@TEW";
    private static final String anotherTestPassword = "3!ggoW0zqqm";
    private static final String bogusPassword = "boguspassword";
    private static final String shortPassword = "b";
    private static final String repeatedPassword = "bbbbbbbbbbbbbb";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldaps://localhost:1636");
        DefaultLdapManager pwm = new DefaultLdapManager();
        pwm.setEnvironment(env);

        //pwm.adminCreateUser(testUser);
        initialPwd = pwm.resetPassword(testUser);
        System.out.println("Created new user, password is " + initialPwd);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // Make sure that our test user is gone
        //pwm.adminDeleteUser(testUser);
    }

    /**
     * Simple create user and delete user test
     */
    // public void testSetupLdap() {
    // pwm.adminSetup();
    // }

    /**
     * Create a user, use the user's generated password to check the password.
     */
    public void testPasswordCheck() {
        pwm.checkPassword(testUser, initialPwd);
    }

    /**
     * Create a user, ignore the user's generated password and use a bogus
     * password which should fail.
     */
    public void testWrongPassword() {
        try {
            pwm.checkPassword(testUser, bogusPassword.toCharArray());
            fail();
        } catch (LdapException e) {
            if (e.getLDAPErrorCode() != LdapException.INVALID_CREDENTIALS) {
                throw e;
            }
        }
    }

    /**
     * Change to a password that is too short, which should fail.
     */
    public void testShortPasswordChange() {
        try {
                pwm.changePassword(testUser, initialPwd, shortPassword.toCharArray());
            fail();
        } catch (LdapException e) {
            if (e.getLDAPErrorCode() != LdapException.PASSWORD_VALIDATION) {
                throw e;
            }
            PasswordPolicy pp = pwm.getPasswordPolicy();
            System.out.println("Password change failed: " + pp.getFormattedReason());
        }
    }

    /**
     * Change to a password that is too short, which should fail.
     */
    public void testRepeatedPasswordChange() {
        try {
            pwm.changePassword(testUser, initialPwd, repeatedPassword.toCharArray());
            fail();
        } catch (LdapException e) {
            if (e.getLDAPErrorCode() != LdapException.PASSWORD_VALIDATION) {
                throw e;
            }
            PasswordPolicy pp = pwm.getPasswordPolicy();
            System.out.println("Password change failed: " + pp.getFormattedReason());
        }
    }

    /**
     * Create a user, the user then changes their password (as required), and
     * then displays the user's attributes. Delete the user when done.
     */
    public void testDisplayAttributes() {
        pwm.changePassword(testUser, initialPwd, testPassword.toCharArray());
    }

    /**
     * Create a user, do not change the password (as required), and then try to
     * display the user's attributes. This should fail because the only thing
     * allowed after a reset password is for the user to change their password.
     * Delete the user when done.
     */
    public void testDisplayAttributesWithoutChangePassword() {
        try {
            //pwm.displayAttributes(testUser, initialPwd);
            fail();
        } catch (LdapException e) {
            // We're expecting this error, but not others
            if (e.getLDAPErrorCode() == LdapException.PASSWORD_MUST_CHANGE) {
                return;
            } else {
                throw e;
            }
        }
    }

    /**
     * Create a user and then the sys admin displays the user's attributes (this
     * does not require a password change). Delete the user when done.
     */
    public void testAdminDisplayAttributes() {
        //pwm.adminDisplayAttributes(testUser);
    }

    /**
     * The admin resets the users password to a supplied password rather than
     * depending on LDAP to generate a password.
     */
    public void testPasswordResetToSpecificName() {
        pwm.resetPassword(testUser);
        pwm.checkPassword(testUser, testPassword.toCharArray());
        pwm.changePassword(testUser, testPassword.toCharArray(), testPassword.toCharArray());
    }

    /**
     * Create a user, the user then changes their password (as required), and
     * then displays the user's attributes. Delete the user when done.
     */
    public void testPasswordExpiring() {
        try {
            pwm.adminDisplayConfigAttributes("cn=Default Password Policy,cn=Password Policies,cn=config");
            pwm.changePassword(testUser, initialPwd, testPassword.toCharArray());
            for (int x = 0; x < 10; x++) {
                try {
                    Thread.sleep(10 * 1000);
                    pwm.checkPassword(testUser, testPassword.toCharArray());
                    PasswordExpiring passwordExpiring = pwm.getPasswordExpiring();
                    if (passwordExpiring != null) {
                        System.out.println("Password expires in " + passwordExpiring.getFormattedExpiration());
                    }
                    if (pwm.isPasswordExpired()) {
                        System.out.println("Password has expired, changing password now");
                        pwm.changePassword(testUser, testPassword.toCharArray(), anotherTestPassword.toCharArray());
                        System.out.println("Checking new password");
                        pwm.checkPassword(testUser, anotherTestPassword.toCharArray());
                        return;
                    }
                    pwm.printControls();
                } catch (LdapException e) {
                    if (e.getLDAPErrorCode() != LdapException.INVALID_CREDENTIALS) {
                        throw e;
                    }
                    System.out.println(testUser + " login failed: " + e.getCause().getMessage());
                    fail();
                }
            }

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
