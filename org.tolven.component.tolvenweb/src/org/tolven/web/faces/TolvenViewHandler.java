package org.tolven.web.faces;

import java.io.IOException;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class TolvenViewHandler extends ViewHandler {

    private ViewHandler originalViewHandler = null;

    public TolvenViewHandler() {
    }

    public TolvenViewHandler(ViewHandler originalViewHandler) {
        super();
        this.originalViewHandler = originalViewHandler;
    }

    public Locale calculateLocale(FacesContext facesContext) {
        ExternalContext ctx = facesContext.getExternalContext();
        HttpServletRequest request = ((HttpServletRequest) ctx.getRequest());
        if(!request.isRequestedSessionIdValid()) {
            return Locale.getDefault();
        }
        HttpSession session = (HttpSession) ctx.getSession(false);
        if (session == null) {
            return Locale.getDefault();
        } else {
            return getLocale(request);
        }
    }

    public String calculateCharacterEncoding(FacesContext facesContext) {
        return originalViewHandler.calculateCharacterEncoding(facesContext);
    }

    public String calculateRenderKitId(FacesContext facesContext) {
        return originalViewHandler.calculateRenderKitId(facesContext);
    }

    public UIViewRoot createView(FacesContext facesContext, String viewName) {
        return originalViewHandler.createView(facesContext, viewName);
    }

    public String getActionURL(FacesContext facesContext, String s) {
        return originalViewHandler.getActionURL(facesContext, s);

    }

    public String getResourceURL(FacesContext facesContext, String s) {
        return originalViewHandler.getResourceURL(facesContext, s);
    }

    public void renderView(FacesContext facesContext, UIViewRoot uiViewRoot) throws IOException, FacesException {
        originalViewHandler.renderView(facesContext, uiViewRoot);
    }

    public void initView(FacesContext facesContext) {
        originalViewHandler.initView(facesContext);
    }

    public UIViewRoot restoreView(FacesContext facesContext, String viewName) {
        return originalViewHandler.restoreView(facesContext, viewName);
    }

    public void writeState(FacesContext facesContext) throws IOException {
        originalViewHandler.writeState(facesContext);
    }

    private static Locale getLocale(HttpServletRequest request) {
        /*
        String accountUserLocale = TolvenSSO.getInstance().getSessionProperty(ResourceBundleHelper.USER_LOCALE, request);
        if (accountUserLocale != null && accountUserLocale.length() == 0) {
            accountUserLocale = null;
        }
        String accountLocale = TolvenSSO.getInstance().getSessionProperty(ResourceBundleHelper.ACCOUNT_LOCALE, request);
        if (accountLocale != null && accountLocale.length() == 0) {
            accountLocale = null;
        }
        return ResourceBundleHelper.getLocale(accountUserLocale, accountLocale);
        */
        return Locale.getDefault();
    }
    
}
