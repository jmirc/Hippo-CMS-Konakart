package org.onehippo.forge.konakart.site.service;

import com.konakart.al.KKAppEng;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface KKEngineService {

    /**
     * Retrieve the Konakart client or null if it is not found into the session
     *
     * @param servletRequest http servlet request
     * @return the KonaKart client engine instance
     */
    KKAppEng getKKAppEng(HttpServletRequest servletRequest);

    /**
     * Retrieve the Konakart client or null if it is not found into the session
     *
     * @param request http request
     * @return the KonaKart client engine instance
     */
    KKAppEng getKKAppEng(HstRequest request);

    /**
     * Sets the variable KKEngine to the KKEngine instance saved in the session. If cannot be found,
     * then it is instantiated and attached.
     *
     * @param servletRequest  http servlet request
     * @param servletResponse http servlet response
     * @param kkStoreConfig   contains configurations set for the current selected store. See hst:mount (konakart:storeName)
     * @return Returns a KonaKart client engine instance
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          thrown if the Konakart engine can't be initialized
     */
    KKAppEng initKKEngine(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
                          KKStoreConfig kkStoreConfig) throws HstComponentException;

    /**
     * Checks to see whether we are logged in.
     *
     * @param servletRequest  the http servlet request
     * @param servletResponse the http servlet response
     * @return Returns the CustomerId if logged in. Otherwise a negative number.
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          .
     */
    int validKKSession(@Nonnull HttpServletRequest servletRequest, @Nonnull HttpServletResponse servletResponse) throws HstComponentException;

    /**
     * Log a user to Konakart
     *
     * @param request  the Hst request
     * @param response the Hst response
     * @param username the username
     * @param password the password
     * @return true if the user is logged-in, false otherwise
     */
    boolean loggedIn(HstRequest request, HstResponse response, String username, String password);


}