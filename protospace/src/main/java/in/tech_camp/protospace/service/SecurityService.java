package in.tech_camp.protospace.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class SecurityService {

  /**
   * 新規登録後に自動ログインする。
   * HttpServletRequest#login により Spring Security が認証・セッション保存を行う。
   */
  public void autoLogin(String email, String password) {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    try {
      request.login(email, password);
    } catch (ServletException e) {
      SecurityContextHolder.getContext().setAuthentication(null);
      throw new RuntimeException("Auto login failed after registration", e);
    }
  }
}