package org.onehippo.forge.konakart.site.security;

import com.konakart.util.Security;
import com.konakartadmin.app.AdminCustomer;
import com.konakartadmin.app.AdminCustomerSearch;
import com.konakartadmin.app.AdminCustomerSearchResult;
import com.konakartadmin.appif.KKAdminIf;
import com.konakartadmin.blif.AdminCustomerMgrIf;
import org.hippoecm.hst.security.impl.DefaultLoginModule;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.konakart.common.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

public class KonakartLoginModule extends DefaultLoginModule {

    protected static final Logger log = LoggerFactory.getLogger(KonakartLoginModule.class);

    /**
     * @see javax.security.auth.spi.LoginModule#login()
     */
    @Override
    public boolean login() throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException("Error: no CallbackHandler available "
                    + "to garner authentication information from the user");
        }

        try {
            // Setup default callback handlers.
            Callback[] callbacks = new Callback[] { new NameCallback("Username: "),
                    new PasswordCallback("Password: ", false) };

            callbackHandler.handle(callbacks);

            username = ((NameCallback) callbacks[0]).getName();
            String password = new String(((PasswordCallback) callbacks[1]).getPassword());

            ((PasswordCallback) callbacks[1]).clearPassword();

            success = false;

            try {
                // We will also check if the user's password is correct against Konakart
                AdminCustomer adminCustomer = retrieveUserFromKonakart(username);
                if (!Security.checkPassword(adminCustomer.getPassword(), password)) {
                    throw new SecurityException("Failed to login against Konakart for the user: " + username);
                }

                // We need to pass the generated password that has been created during the customers' synchronization process.
                // @see org.onehippo.forge.konakart.cms.replication.utils.NodeHelper.createOrRetrieveCustomer
                // The password will be set using the org.onehippo.forge.konakart.common.util.SecurityUtils.createSyncKonakartPassword method
                user = getAuthenticationProvider().authenticate(adminCustomer.getEmailAddr(), SecurityUtils.KONAKART_DEFAULT_PASSWORD.toCharArray());

                // Force to save the real password. This will be used by the Konakart Valve to finalize
                // the authentication process againt Konakart. The user's password is saved by Konakart
                subject.getPrivateCredentials().add(createSubjectRepositoryCredentials(username, password.toCharArray()));

            } catch (SecurityException se) {
                if (se.getCause() != null) {
                    if (log.isDebugEnabled()) {
                        log.warn("Failed to authenticate: " + se.getCause(), se.getCause());
                    } else {
                        log.warn("Failed to authenticate: " + se.getCause());
                    }
                } else {
                    log.warn("Failed to authenticate: " + se);
                }

                throw new FailedLoginException("Authentication failed");
            }

            success = true;
            callbacks[0] = null;
            callbacks[1] = null;

            return (true);
        } catch (LoginException ex) {
            throw ex;
        } catch (Exception ex) {
            success = false;
            throw new LoginException(ex.getMessage());
        }
    }


    /**
     *  Retrieve the Konakart's user
     * @param username the username
     * @return the Konakart's user
     */
    protected AdminCustomer retrieveUserFromKonakart(String username) throws Exception {
        AdminCustomerMgrIf adminCustMgr = KKAdminEngine.getInstance().getFactory().getAdminCustMgr(true);

        AdminCustomer adminCustomer = adminCustMgr.getCustomerForEmail(username);

        if (adminCustomer != null) {
            return adminCustomer;
        }

        throw new LoginException("Failed to find existing user with the username: " + username);
    }
}
