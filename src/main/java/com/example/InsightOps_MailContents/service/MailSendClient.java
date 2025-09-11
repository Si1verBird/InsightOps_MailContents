package com.example.InsightOps_MailContents.service;

import com.example.InsightOps_MailContents.dto.MailSendRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Service
public class MailSendClient {

    @Value("${mailsend.api.base-url}")
    private String mailSendApiBaseUrl;

    private final RestTemplate restTemplate;

    public MailSendClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 메일 발송 서비스에 메일 전송 요청을 보냅니다.
     * @param categoryId 카테고리 ID
     * @param subject 메일 제목
     * @param content 메일 내용
     * @return 발송 성공 여부
     */
    public boolean sendMail(String categoryId, String subject, String content) {
        try {
            String url = mailSendApiBaseUrl + "/api/sendmail";
            
            // 요청 본문 생성
            MailSendRequest request = new MailSendRequest(categoryId, subject, content);
            
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // HTTP 엔티티 생성
            HttpEntity<MailSendRequest> entity = new HttpEntity<>(request, headers);
            
            // POST 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            // 성공 여부 확인 (2xx 상태 코드)
            return response.getStatusCode().is2xxSuccessful();
            
        } catch (Exception e) {
            System.err.println("메일 발송 API 호출 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 메일 발송 API 호출 (비동기)
     * 메일 발송 실패 시에도 메인 프로세스에 영향을 주지 않도록 처리
     */
    public void sendMailAsync(String categoryId, String subject, String content) {
        // 별도 스레드에서 메일 발송 처리
        new Thread(() -> {
            try {
                boolean success = sendMail(categoryId, subject, content);
                if (success) {
                    System.out.println("메일 발송 성공: category_id=" + categoryId);
                } else {
                    System.err.println("메일 발송 실패: category_id=" + categoryId);
                }
            } catch (Exception e) {
                System.err.println("비동기 메일 발송 중 오류: " + e.getMessage());
            }
        }).start();
    }
}
