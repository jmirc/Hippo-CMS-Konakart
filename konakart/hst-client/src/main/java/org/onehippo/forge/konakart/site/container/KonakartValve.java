package org.onehippo.forge.konakart.site.container;

import com.konakart.al.KKAppEng;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.Valve;
import org.hippoecm.hst.core.container.ValveContext;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.hst.utils.KKComponentUtils;
import org.onehippo.forge.konakart.site.security.KKUser;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

public class KonakartValve implements Valve {

    public static Logger log = LoggerFactory.getLogger(KonakartValve.class);

    public final static String REDIRECT_LOGOUT_URL = "/j_spring_security_logout";

    @Override
    public void initialize() throws ContainerException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void invoke(ValveContext context) throws ContainerException {

        HttpServletRequest request = context.getServletRequest();
        HttpServletResponse response = context.getServletResponse();
        HstRequestContext requestContext = context.getRequestContext();

        Session jcrSession;

        try {
            jcrSession = context.getRequestContext().getSession();
        } catch (RepositoryException e) {
            throw new IllegalStateException("Failed to retrieve the Jcr Session", e);
        }

        // Pre-load the checkout activities
        try {
            HippoModuleConfig.getConfig().preLoadActivityList(jcrSession);
        } catch (RepositoryException e) {
            throw new IllegalStateException("Failed to load the list of activities. ", e);
        }

        // Initialize internal Konakart Engine configuration
        KKEngine.init(jcrSession);

        // Retrieve the Konakart client
        KKAppEng kkAppEng = KKServiceHelper.getKKEngineService().getKKAppEng(request);

        // Initialize the Konakart Admin Client
        try {
            KKAdminEngine.init(jcrSession);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize the Konakart Admin Client", e);
        }

        KKStoreConfig kkStoreConfig = KKComponentUtils.getKKStoreConfig(context.getRequestContext(), jcrSession);

        request.getSession().setAttribute(KKStoreConfig.KK_STORE_CONFIG, kkStoreConfig);

        // Initialize the konakart client if it has not been created
        if (kkAppEng == null) {
            // Initialize Konakart Engine
            kkAppEng = KKServiceHelper.getKKEngineService().initKKEngine(request, response, jcrSession, kkStoreConfig);
        }

        // Validate the current konakart session
        KKServiceHelper.getKKEngineService().validKKSession(request, response);

        // Set the konakart client
        request.setAttribute(KKAppEng.KONAKART_KEY, kkAppEng);


        // At this stage, the Konakart client engine is created and is configured.
        // The authentication is a two-phase process. The first phase uses the current JAAS authentication using
        // the KonakartLoginModule. This phase checks if the customer exists and set the roles
        // The second phase valids the username/password against Konakart.
        Principal userPrincipal = request.getUserPrincipal();

        if (userPrincipal instanceof Authentication) {

            Authentication authentication = (Authentication) userPrincipal;
            KKUser kkUser = (KKUser) authentication.getPrincipal();

            if (kkUser.isRememberMeAuthentication()) {
                int customerId = kkUser.getCustomerId();

                // The Login should work because the validation of the password has been done during the login process
                // by the KonakartLoginModule.
                if (!KKServiceHelper.getKKEngineService().loginByAdmin(request, response, customerId)) {
                    logout(request, response, requestContext);
                    return;
                }
            } else {
                // Invalid username and password
                String username =  userPrincipal.getName();
                String password = String.valueOf(authentication.getCredentials());

                // The Login should work because the validation of the password has been done during the login process
                // by the KonakartLoginModule.
                if (!KKServiceHelper.getKKEngineService().logIn(request, response,
                        username, password)) {
                    logout(request, response, requestContext);
                    return;
                }
            }
        } else {
            // No user principal has been found. The user has been logged out.
            // We need to log-out from Konakart
            KKServiceHelper.getKKEngineService().logOut(request, response);
        }

        // Instantiate the next context
        context.invokeNext();
    }



    private void logout(HttpServletRequest request, HttpServletResponse response, HstRequestContext requestContext) {
        try {
            HstLinkCreator linkCreator = requestContext.getHstLinkCreator();
            HstLink link = linkCreator.create(REDIRECT_LOGOUT_URL, requestContext.getResolvedMount().getMount());

            request.getRequestDispatcher(link.getPath()).forward(request, response);
        } catch (IOException e) {
            log.error("Failed to redirect to the path - " + REDIRECT_LOGOUT_URL);
        } catch (ServletException e) {
            log.error("Failed to redirect to the path - " + REDIRECT_LOGOUT_URL);
        }
    }
}
