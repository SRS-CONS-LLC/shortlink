package com.srscons.shortlink.shortener.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.srscons.shortlink.shortener.config.CloudflareProperties;
import com.srscons.shortlink.common.exception.ShortLinkException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileUploadService {

    private final CloudflareProperties cloudflareProperties;
    private final RestTemplate restTemplate;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String saveFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new ShortLinkException("Invalid file: no extension found.");
        }

        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new ShortLinkException("File type not allowed: " + fileExtension);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ShortLinkException("File size exceeds the 5MB limit.");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(cloudflareProperties.getApiToken());
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartInputStreamFileResource(file));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String url = "https://api.cloudflare.com/client/v4/accounts/" +
                    cloudflareProperties.getAccountId() + "/images/v1";

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    JsonNode.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String imageId = response.getBody().path("result").path("id").asText();
                return cloudflareProperties.getDeliveryUrl() + "/" + imageId + "/public";
            } else {
                throw new ShortLinkException("Cloudflare image upload failed: " + response);
            }

        } catch (IOException e) {
            throw new ShortLinkException("Cloudflare CDN upload failed", e);
        }
    }


}