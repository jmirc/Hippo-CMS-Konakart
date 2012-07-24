package org.onehippo.forge.konakart.site.security.impl;

import com.konakart.util.Security;
import com.konakartadmin.app.AdminCustomer;
import com.konakartadmin.blif.AdminCustomerMgrIf;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.konakart.site.security.KKUser;
import org.onehippo.forge.konakart.site.security.KKUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Nonnull;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

public class KKUserDetailsServiceImpl implements KKUserDetailsService {

    public static final Logger log = LoggerFactory.getLogger(KKUserDetailsServiceImpl.class);
    public static final String DEFAULT_ROLE = "everybody";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        return createUserDetails(username, "none", false);
    }

    @Nonnull
    @Override
    public UserDetails loadUserByUsernameAndPassword(String username, String password) throws UsernameNotFoundException, DataAccessException {
        return createUserDetails(username, password, true);
    }

    /**
     * Create the associated UserDetails
     * @param username the username
     * @param password the password
     * @param checkPassword true if the password needs to be checked,
     *                      false if the remember services is activated and the password will not be validated.
     * @return the UserDetails
     * @throws UsernameNotFoundException thrown if the user is not found
     */
    protected UserDetails createUserDetails(String username, String password, boolean checkPassword) throws UsernameNotFoundException {
        try {
            AdminCustomer adminCustomer = retrieveUserFromKonakart(username);

            if (checkPassword) {
                if (!Security.checkPassword(adminCustomer.getPassword(), password)) {
                    throw new BadCredentialsException("Failed to login against Konakart for the user: " + username);
                }
            }

            boolean accountNonExpired = true;
            boolean credentialsNonExpired = true;
            boolean accountNonLocked = true;

            Collection<? extends GrantedAuthority> authorities = getGrantedAuthoritiesOfUser(username);

            if (checkPassword) {
                return new KKUser(adminCustomer, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
            } else {
                return new KKUser(adminCustomer, true, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
            }

        }  catch (NoSuchAlgorithmException e) {
            log.error("Failed to validate the password for the user - " + username, e);
        }

        throw new UsernameNotFoundException("Failed to find existing user with the username: " + username);
    }


    /**
     * Retrieve the Konakart's user
     *
     * @param username the username
     * @return the Konakart's user
     */
    @Nonnull
    protected AdminCustomer retrieveUserFromKonakart(final String username) throws UsernameNotFoundException {
        try {
            AdminCustomerMgrIf adminCustMgr = KKAdminEngine.getInstance().getFactory().getAdminCustMgr(true);

            AdminCustomer adminCustomer = adminCustMgr.getCustomerForEmail(username);

            if (adminCustomer != null) {
                return adminCustomer;
            }
        } catch (Exception e) {
            log.error("Failed to find existing user with the username: " + username, e);
        }

        throw new UsernameNotFoundException("Failed to find existing user with the username: " + username);
    }


    /**
     * Retrieve the list of roles associated for this user.
     *
     * @param username the username
     * @return the list of roles
     */
    @Nonnull
    protected Collection<? extends GrantedAuthority> getGrantedAuthoritiesOfUser(String username) {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new GrantedAuthorityImpl(DEFAULT_ROLE));
        return authorities;
    }

}
