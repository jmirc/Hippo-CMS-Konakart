package org.onehippo.forge.konakart.site.security;

import com.konakartadmin.app.AdminCustomer;
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
    private AdminCustomer adminCustomer;

    public KKUser(AdminCustomer adminCustomer, boolean accountNonExpired, boolean credentialsNonExpired,
                  boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(adminCustomer.getEmailAddr(), adminCustomer.getPassword(), adminCustomer.isEnabled(), accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.adminCustomer = adminCustomer;
    }

    public KKUser(AdminCustomer adminCustomer, boolean rememberMeAuthentication, boolean accountNonExpired, boolean credentialsNonExpired,
                  boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(adminCustomer.getEmailAddr(), adminCustomer.getPassword(), adminCustomer.isEnabled(), accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.adminCustomer = adminCustomer;
        this.isRememberMeAuthentication = rememberMeAuthentication;
    }


    public boolean isRememberMeAuthentication() {
        return isRememberMeAuthentication;
    }

    public int getCustomerId() {
        return adminCustomer.getId();
    }

    public AdminCustomer getAdminCustomer() {
        return adminCustomer;
    }
}
