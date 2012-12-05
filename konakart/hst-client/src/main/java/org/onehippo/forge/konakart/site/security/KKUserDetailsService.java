package org.onehippo.forge.konakart.site.security;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Nonnull;

public interface KKUserDetailsService extends UserDetailsService {

  /**
   * @param username the username
   * @param password the password
   * @return a fully populated user record (never null)
   * @throws UsernameNotFoundException
   * @throws DataAccessException
   */
  @Nonnull
  UserDetails loadUserByUsernameAndPassword(final String username, final String password)
      throws UsernameNotFoundException, DataAccessException;
}
