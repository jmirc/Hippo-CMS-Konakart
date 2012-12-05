package org.onehippo.forge.konakart.site.security;

import org.onehippo.forge.konakart.site.security.impl.KKUserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class KKAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  public static final Logger log = LoggerFactory.getLogger(KKAuthenticationProvider.class);

  private KKUserDetailsService kkUserDetailsService;

  public void setKkUserDetailsService(KKUserDetailsService kkUserDetailsService) {
    this.kkUserDetailsService = kkUserDetailsService;
  }

  public KKUserDetailsService getKkUserDetailsService() {
    if (kkUserDetailsService == null) {
      kkUserDetailsService = new KKUserDetailsServiceImpl();
    }

    return kkUserDetailsService;
  }

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    if (authentication.getCredentials() == null) {
      log.debug("Authentication failed: no credentials provided");

      throw new BadCredentialsException(messages.getMessage(
          "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"),
          null);
    }
  }

  @Override
  protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    UserDetails loadedUser;

    try {
      String password = authentication.getCredentials().toString();
      loadedUser = getKkUserDetailsService().loadUserByUsernameAndPassword(username, password);
    } catch (DataAccessException repositoryProblem) {
      throw new AuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
    }

    if (loadedUser == null) {
      throw new AuthenticationServiceException(
          "UserDetailsService returned null, which is an interface contract violation");
    }

    return loadedUser;

  }
}
