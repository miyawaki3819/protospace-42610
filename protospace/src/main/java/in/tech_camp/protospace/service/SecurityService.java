package in.tech_camp.protospace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

  @Autowired
  private UserDetailsService userDetailsService;

  public void autoLogin(String email) {

    UserDetails userDetails =
        userDetailsService.loadUserByUsername(email);

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );

    SecurityContextHolder.getContext()
        .setAuthentication(authentication);
  }
}