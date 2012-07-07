package org.onehippo.forge.konakart.common.util;

import org.hippoecm.repository.PasswordHelper;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {


    public static String KONAKART_DEFAULT_PASSWORD = "KONAKART_DEFAULT_PASSWORD";

    /**
     * Generate password hash from string.
     *
     * @return the hash
     * @throws javax.jcr.RepositoryException, the wrapper encoding errors
     */
    public static String createSyncKonakartPassword() throws RepositoryException {
        return createPasswordHash(KONAKART_DEFAULT_PASSWORD);
    }

    /**
     * Generate password hash from string.
     *
     * @param password the password
     * @return the hash
     * @throws javax.jcr.RepositoryException, the wrapper encoding errors
     */
    public static String createPasswordHash(final String password) throws RepositoryException {
        try {
            return PasswordHelper.getHash(password.toCharArray());
        } catch (NoSuchAlgorithmException e) {
            throw new RepositoryException("Unable to hash password", e);
        } catch (IOException e) {
            throw new RepositoryException("Unable to hash password", e);
        }
    }

}
