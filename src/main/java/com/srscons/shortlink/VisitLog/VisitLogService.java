package com.srscons.shortlink.VisitLog;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VisitLogService {

    private final VisitLogRepository visitLogRepository;

    public void saveVisit(String shortCode, HttpServletRequest request){
        VisitLogEntity visitLogEntity = VisitLogEntity.builder()
                .shortCode(shortCode)
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .referer(request.getHeader("Referer"))
                .acceptLanguage(request.getHeader("Accept-Language"))
                .visitedAt(LocalDateTime.now()).build();

         visitLogRepository.save(visitLogEntity);


    }

}
