package com.example.InsightOps_mailcontents.controller;

import com.example.InsightOps_mailcontents.dto.MailGenerateRequest;
import com.example.InsightOps_mailcontents.dto.MailGenerateResponse;
import com.example.InsightOps_mailcontents.service.MailContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private MailContentService mailContentService;

    /**
     * 메일 내용 생성 API
     * POST /mail/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<MailGenerateResponse> generateMail(
            @RequestBody MailGenerateRequest request) {
        
        try {
            // 입력 검증
            if (request.getCategoryId() == null || request.getCategoryId().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // 메일 콘텐츠 생성
            MailGenerateResponse response = mailContentService.generateMailContent(
                request.getCategoryId().trim());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // 비즈니스 로직 오류 (데이터 없음 등)
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            // 시스템 오류
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 헬스 체크 API
     * GET /mail/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Mail Content Service is running!");
    }
}
