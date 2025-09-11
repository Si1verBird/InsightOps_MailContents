package com.example.InsightOps_MailContents.controller;

import com.example.InsightOps_MailContents.dto.MailGenerateRequest;
import com.example.InsightOps_MailContents.dto.MailGenerateResponse;
import com.example.InsightOps_MailContents.service.MailContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    private static final Logger logger = LoggerFactory.getLogger(MailController.class);

    @Autowired
    private MailContentService mailContentService;

    /**
     * 메일 내용 생성 API
     * POST api/mail/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<MailGenerateResponse> generateMail(
            @RequestBody MailGenerateRequest request) {
        
        logger.info("=== POST /api/mail/generate 요청 수신 ===");
        logger.info("요청 데이터: {}", request != null ? request.getCategoryId() : "null");
        
        try {
            // 입력 검증
            if (request.getCategoryId() == null || request.getCategoryId().trim().isEmpty()) {
                logger.warn("입력 검증 실패: category_id가 null 또는 빈 문자열");
                return ResponseEntity.badRequest().build();
            }

            logger.info("입력 검증 성공: category_id = {}", request.getCategoryId().trim());

            // 메일 콘텐츠 생성
            MailGenerateResponse response = mailContentService.generateMailContent(
                request.getCategoryId().trim());

            logger.info("메일 콘텐츠 생성 성공");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // 비즈니스 로직 오류 (데이터 없음 등)
            logger.error("RuntimeException in generateMail: category_id={}", 
                request != null ? request.getCategoryId() : "null", e);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            // 시스템 오류
            logger.error("Exception in generateMail: category_id={}", 
                request != null ? request.getCategoryId() : "null", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 헬스 체크 API
     * GET api/mail/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Mail Content Service is running!");
    }
}
