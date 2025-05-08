package com.srscons.shortlink.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.srscons.shortlink.auth.GoogleProperties;
import com.srscons.shortlink.auth.exception.InvalidTokenException;
import com.srscons.shortlink.auth.util.Resources;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;

@Slf4j
@Service
public class GoogleAuthService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final GoogleProperties googleProperties;
    private final GoogleClientSecrets googleCredentials;

    public GoogleAuthService(GoogleProperties googleProperties) throws IOException {
        this.googleProperties = googleProperties;
        this.googleCredentials = GoogleClientSecrets.load(JSON_FACTORY, Resources.resourceAsReader(googleProperties.getAppLoginSecretPath()));
    }

    public String getAuthUrl() {
        return googleCredentials.getDetails().getAuthUri() +
                "?client_id=" + googleCredentials.getDetails().getClientId()
                + "&redirect_uri=" + googleCredentials.getDetails().getRedirectUris().get(0)
                + "&response_type=code"
                + "&scope=" + googleProperties.getScope()
                + "&access_type=online";
    }

    public String getAccessToken(String code) throws Exception {
        HttpRequest tokenRequest = HttpRequest.newBuilder()
                .uri(URI.create(googleCredentials.getDetails().getTokenUri()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "code=" + code +
                                "&client_id=" + googleCredentials.getDetails().getClientId() +
                                "&client_secret=" + googleCredentials.getDetails().getClientSecret() +
                                "&redirect_uri=" + googleCredentials.getDetails().getRedirectUris().get(0) +
                                "&grant_type=authorization_code"
                ))
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> tokenResponse = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());

            // Parse the response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tokenJson = mapper.readTree(tokenResponse.body());
            return tokenJson.get("access_token").asText();
        }
    }

    public GoogleUserInfo getGoogleUserInfo(String accessToken) {
        HttpRequest userInfoRequest = HttpRequest.newBuilder()
                .uri(URI.create(googleProperties.getUserInfoUri()))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpResponse<String> userInfoResponse = client.send(userInfoRequest, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode userInfo = mapper.readTree(userInfoResponse.body());

            // You can now extract user info
            String email = userInfo.get("email").asText();
            String name = userInfo.get("given_name").asText();
            String surname = userInfo.get("family_name").asText();
            String sub = userInfo.get("sub").asText();
            String fullName = userInfo.get("name").asText();
            String picture = userInfo.get("picture").asText();
            String provider = "google";

            return GoogleUserInfo.builder()
                    .name(name)
                    .surname(surname)
                    .email(email)
                    .fullName(fullName)
                    .provider(provider)
                    .sub(sub)
                    .picture(picture)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    public GoogleIdToken.Payload verifyToken(String token) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), JSON_FACTORY)
                    .setAudience(Collections.singletonList(googleCredentials.getDetails().getClientId()))
                    .build();

            final GoogleIdToken idToken = verifier.verify(token);
            if (idToken == null) {
                throw new InvalidTokenException("Invalid token:" + token);
            }

            return idToken.getPayload();
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid token:" + token, e);
        }
    }

    @Data
    @Builder
    public static class GoogleUserInfo {

        private String email;
        private String name;
        private String surname;
        private String fullName;
        private String sub;
        private String provider;
        private String picture;

    }

}
