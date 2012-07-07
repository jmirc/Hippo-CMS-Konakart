package org.onehippo.forge.konakart.site.container;

import com.konakart.al.KKAppEng;
import org.apache.cxf.common.util.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.container.*;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Set;

public class KonakartValve implements Valve {


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

        // Initialize the konakart client if it has not been created
        // TODO: how to handle multiple stores??????
        if (kkAppEng == null) {
            // Set the current store config
            Mount resolvedMount = context.getRequestContext().getResolvedMount().getMount();
            String storeName = resolvedMount.getProperty(KKCndConstants.KONAKART_CONFIG_STORE_NAME);

            //  Set the default one
            if (StringUtils.isEmpty(storeName)) {
                storeName = KKCheckoutConstants.DEF_STORE_ID;
            }


            // Check if the store config has been created under /hippo-configuration/cms-services/KonakartSynchronizationService
            KKStoreConfig kkStoreConfig;
            try {
                kkStoreConfig = HippoModuleConfig.getConfig().getStoreConfigByName(jcrSession, storeName);

                request.setAttribute(KKStoreConfig.KK_STORE_CONFIG, kkStoreConfig);
            } catch (RepositoryException e) {
                throw new IllegalStateException("Failed to load the storeConfig. Please verify if a new storeConfig named "
                        + storeName + " within /hippo-configuration/cms-services/KonakartSynchronizationService");
            }

            // Initialize Konakart Engine
            kkAppEng = KKServiceHelper.getKKEngineService().initKKEngine(request, response, kkStoreConfig);
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

        if (userPrincipal != null) {
            SimpleCredentials simpleCredentials = null;

            HttpSession session = request.getSession(false);
            Subject subject = (session != null ? (Subject) session.getAttribute(ContainerConstants.SUBJECT_ATTR_NAME) : null);

            if (subject != null) {
                Set<SimpleCredentials> privateCredentialsSet = subject.getPrivateCredentials(SimpleCredentials.class);

                if (!privateCredentialsSet.isEmpty()) {
                    simpleCredentials = privateCredentialsSet.iterator().next();
                }
            }

            if (simpleCredentials == null && session != null) {
                simpleCredentials = (SimpleCredentials) session.getAttribute(ContainerConstants.SUBJECT_REPO_CREDS_ATTR_NAME);

            }

            if (simpleCredentials != null) {
                // Invalid username and password
                if (!KKServiceHelper.getKKEngineService().logIn(request, response,
                        simpleCredentials.getUserID(), String.valueOf(simpleCredentials.getPassword()))) {

                    try {
                        LoginContext loginContext = new LoginContext("HSTSITE", subject);
                        loginContext.logout();
                    } catch (LoginException e) {
                        // Failed to logout
                        throw new ContainerSecurityNotAuthenticatedException("Not authenticated yet.");
                    }
                }
            }
        } else {
            // No user principal has been found. The user has been logged out.
            // We need to log-out from Konakart
            KKServiceHelper.getKKEngineService().logout(request);
        }

        // Instantiate the next context
        context.invokeNext();
    }
}
