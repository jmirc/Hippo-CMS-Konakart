package org.onehippo.forge.konakart.site.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * When the user uses the remember me feature, the UserDetailsService is called without a password.
 * In this case, the customer Id needs to be set to allow the KonakartValve to use it for authentication.
 * The CustomerMgr.loginByAdmin will be used.
 */
public class KKUser extends User {

    private boolean isRememberMeAuthentication = false;
    private int customerId;


    public KKUser(String username, String password, boolean enabled, boolean accountNonExpired,
                  boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public KKUser(int customerId, String username, String password, boolean enabled, boolean accountNonExpired,
                  boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.isRememberMeAuthentication = true;
        this.customerId = customerId;
    }


    public boolean isRememberMeAuthentication() {
        return isRememberMeAuthentication;
    }

    public int getCustomerId() {
        return customerId;
    }
}
