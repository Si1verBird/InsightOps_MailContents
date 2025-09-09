package com.example.InsightOps_mailcontents.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "InsightOps MailContents Service is running! ✅";
    }

    @GetMapping("/api")
    public String api() {
        return "Welcome to InsightOps MailContents Service API 🚀";
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        String currentTime = java.time.LocalDateTime.now().toString();
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
                "<title>InsightOps MailContents Service-- 수정 테스트용!!!</title>" +
                "<meta charset=\"UTF-8\">" +
            "</head>" +
            "<body>" +
                "<h1>🚀 InsightOps MailContents Service</h1>" +
                "<h2>✅ Status: Running</h2>" +
                "<h3>📋 Version: 0.0.3</h3>" +
                "<hr>" +
                "<p><strong>CI/CD 테스트용 간단한 웹 애플리케이션</strong></p>" +
                "<p>메일 콘텐츠 관리를 위한 마이크로서비스입니다.</p>" +
                "<br>" +
                "<h4>📡 API Endpoints:</h4>" +
                "<ul>" +
                    "<li><a href=\"/health\">Health Check</a> - 서비스 상태 확인</li>" +
                    "<li><a href=\"/api\">API Info</a> - API 정보</li>" +
                "</ul>" +
                "<br>" +
                "<p><em>🎯 CI/CD 파이프라인으로 자동 배포됨</em></p>" +
                "<p><small>Build Time: " + currentTime + "</small></p>" +
            "</body>" +
            "</html>";
    }
}
