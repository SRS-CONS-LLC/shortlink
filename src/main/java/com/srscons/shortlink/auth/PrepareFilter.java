package com.srscons.shortlink.auth;

import com.google.api.services.youtube.model.Channel;
import com.srscons.shortlink.auth.entity.UserEntity;
import com.srscons.shortlink.auth.exception.InvalidTokenException;
import com.srscons.shortlink.auth.repository.UserRepository;
import com.srscons.shortlink.auth.service.GoogleAuthService;
import com.srscons.shortlink.auth.util.CONSTANTS;
import com.srscons.shortlink.auth.util.CookieUtil;
import com.srscons.shortlink.auth.util.UrlAntMatcher;
import com.srscons.shortlink.util.ListUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class PrepareFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final GoogleAuthService googleAuthService;
    private final UserRepository userRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public PrepareFilter(GoogleAuthService googleAuthService,  UserRepository userRepository) {
        this.googleAuthService = googleAuthService;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return pathMatcher.match("/public/**", request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            boolean isLoggedIn = prepareUser(request, response);
            if(!isLoggedIn) {
                if (UrlAntMatcher.of(request.getRequestURI())
                        .notContainsAny("/public/**", "/login", "/oauth2/**", "/logout", "/index", "/")) {
                    response.sendRedirect("/");
                    return;
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Prepare Filter", e);
            throw new RuntimeException(e);
        }
    }

    private boolean prepareUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = CookieUtil.getCookieValue(request, CONSTANTS.ACCESS_TOKEN);
        if (Strings.isBlank(token)) {
            return false;
        }

        GoogleAuthService.GoogleUserInfo googleUser = googleAuthService.getGoogleUserInfo(token);
        if (googleUser == null) {
            CookieUtil.deleteCookies(request, response);
            return false;
        }

        Optional<UserEntity> user = userRepository.findByEmail(googleUser.getEmail());
        if (user.isEmpty()) {
            return false;
        }

        request.setAttribute(CONSTANTS.LOGGED_IN_USER, user.get());

        return true;
    }


}
