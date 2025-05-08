package com.srscons.shortlink.auth.controller;

import com.srscons.shortlink.auth.entity.TokenEntity;
import com.srscons.shortlink.auth.entity.UserEntity;
import com.srscons.shortlink.auth.repository.UserRepository;
import com.srscons.shortlink.auth.service.GoogleAuthService;
import com.srscons.shortlink.auth.util.CONSTANTS;
import com.srscons.shortlink.auth.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GoogleAuthRestController {

    private final GoogleAuthService googleAuthService;
    private final UserRepository userRepository;

    public GoogleAuthRestController(GoogleAuthService googleAuthService, UserRepository userRepository) {
        this.googleAuthService = googleAuthService;
        this.userRepository = userRepository;
    }

    @GetMapping("/oauth2/authorization/google")
    public void authenticate(HttpServletResponse response) throws IOException {
        response.sendRedirect(googleAuthService.getAuthUrl());
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CookieUtil.deleteCookies(request, response);

        response.sendRedirect("/");
    }

    @GetMapping("/login/oauth2/code/google")
    public void handleGoogleCallback(@RequestParam("code") String code, HttpServletResponse response) throws Exception {
        final String accessToken = googleAuthService.getAccessToken(code);
        final GoogleAuthService.GoogleUserInfo googleUserInfo = googleAuthService.getGoogleUserInfo(accessToken);

        final UserEntity user = userRepository.findByEmail(googleUserInfo.getEmail()).orElseGet(() ->
                UserEntity.builder()
                        .name(googleUserInfo.getName())
                        .surname(googleUserInfo.getSurname())
                        .fullName(googleUserInfo.getFullName())
                        .build());

        user.setSub(googleUserInfo.getSub())
                .setEmail(googleUserInfo.getEmail())
                .setProviderId(googleUserInfo.getProvider())
                .setPicture(googleUserInfo.getPicture());

        final List<TokenEntity> tokens = user.getTokens() == null ? new ArrayList<>() : user.getTokens();
        tokens.add(new TokenEntity(accessToken, user));

        user.setTokens(tokens);

        userRepository.save(user);

        CookieUtil.setCookie(response, CONSTANTS.ACCESS_TOKEN, accessToken, 60 * 60 * 24 * 30);

        response.sendRedirect("/dashboard");
    }

}
