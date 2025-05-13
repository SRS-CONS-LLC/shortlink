package com.srscons.shortlink.tracklink;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/index")
public class VisitLogController {

    private final VisitLogService visitLogService;

@GetMapping("/{shortCode}")
    public ResponseEntity<String> logVisit(
            @PathVariable String shortCode,
            HttpServletRequest request){

    visitLogService.saveVisit(shortCode, request);

    return ResponseEntity.ok("Visit logged successfully.");

}
    }
