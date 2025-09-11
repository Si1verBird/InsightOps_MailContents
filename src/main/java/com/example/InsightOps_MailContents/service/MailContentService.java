package com.example.InsightOps_MailContents.service;

import com.example.InsightOps_MailContents.dto.AssigneeInfo;
import com.example.InsightOps_MailContents.dto.MailGenerateResponse;
import com.example.InsightOps_MailContents.dto.VocAnalysisResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailContentService {

    private static final Logger logger = LoggerFactory.getLogger(MailContentService.class);

    @Autowired
    private AdminApiClient adminApiClient;

    @Autowired
    private NormalizationApiClient normalizationApiClient;

    @Autowired
    private MailSendClient mailSendClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 템플릿 기반 메일 생성 (기존 LLM 생성 내용을 정적 템플릿으로 보존)
     */
    private String generateMailWithTemplate(String categoryName, AssigneeInfo assigneeInfo, List<VocAnalysisResponse> vocAnalysisResults) {
        StringBuilder mailContent = new StringBuilder();
        
        // 서론
        String fullAssigneeName = assigneeInfo.getAssigneeTeam() + " " + assigneeInfo.getAssigneeName();
        mailContent.append(fullAssigneeName).append(" 님께,\n\n");
        mailContent.append("안녕하십니까? ").append(categoryName).append(" 관련 개선 리포트를 전달드리고자 합니다.\n\n");
        mailContent.append("이번 리포트에서는 최근 고객의 목소리(VOC)를 바탕으로 몇 가지 사례를 분석하고, ");
        mailContent.append("그에 따른 개선 방안 및 기대 효과를 정리하였습니다.\n\n");
        
        // VOC 사례 분석 (최대 3개, 원본 VOC 데이터 그대로 표출)
        mailContent.append("1. VOC 사례 분석:\n");
        int count = 1;
        int maxVocCount = Math.min(3, vocAnalysisResults.size());
        for (int i = 0; i < maxVocCount; i++) {
            VocAnalysisResponse voc = vocAnalysisResults.get(i);
            VocAnalysisResponse.Analysis analysis = voc.getAnalysis();
            
            mailContent.append("   (").append(count).append(") **사례 ").append(count).append("**:\n");
            mailContent.append("       - 문제 상황: ").append(analysis.getProblemSituation()).append("\n");
            mailContent.append("       - 해결 방법: ").append(analysis.getSolutionApproach()).append("\n");
            mailContent.append("       - 기대 효과: ").append(analysis.getExpectedOutcome()).append("\n\n");
            count++;
        }
        
        // 종합 임팩트 분석 (기존 LLM 생성 내용을 정적 템플릿으로 보존)
        mailContent.append("2. 종합 임팩트 분석:\n");
        mailContent.append("   - 위 사례들에서 각 문제 해결 시 고객 만족도 20% 향상, 상담 시간 15% 단축, 고객 유지율 10% 향상 등의 정량적 효과를 기대할 수 있습니다.\n");
        mailContent.append("   - 전체적인 고객 서비스 품질 개선을 통해 고객 충성도 증대 및 재이용률 향상을 기대할 수 있습니다.\n");
        mailContent.append("   - 상담사의 업무 효율성 증대로 인한 운영 비용 절감 효과도 예상됩니다.\n\n");
        
        // 추가 분석 및 권장사항 (기존 LLM 생성 내용을 정적 템플릿으로 보존)
        mailContent.append("3. 추가 분석 및 권장사항:\n");
        mailContent.append("   - 고객 문의 패턴 분석: 특정 시간대 및 요일에 문의가 집중되는 경향을 파악하여 상담사 배치 최적화 필요\n");
        mailContent.append("   - 상담 품질 개선을 위한 정기적인 교육 프로그램 강화 및 상담 매뉴얼 업데이트 권장\n");
        mailContent.append("   - 고객 경험 개선을 위한 프로세스 최적화 및 자동화 도구 도입 검토\n");
        mailContent.append("   - VOC 데이터 기반 예방적 서비스 개선을 위한 모니터링 체계 구축\n");
        mailContent.append("   - 고객 피드백 수집 및 분석 프로세스 개선을 통한 지속적인 서비스 품질 향상\n");
        mailContent.append("   - 부서 간 협업 강화를 통한 통합적 고객 서비스 제공 방안 수립\n\n");
        
        // 결론 (템플릿)
        mailContent.append("더 자세한 데이터는 대시보드에서 확인 가능하며, 링크는 다음과 같습니다: ");
        mailContent.append("https://insightops-dashboard-frontend-bwgfajhzevc8g3er.koreacentral-01.azurewebsites.net\n\n");
        mailContent.append("감사합니다.");
        
        return mailContent.toString();
    }

    public MailGenerateResponse generateMailContent(String categoryId) {
        logger.info("=== 메일 콘텐츠 생성 시작 ===");
        logger.info("요청받은 category_id: {}", categoryId);
        
        try {
            // 1. Admin API를 통해 category_id로 category_name 조회
            logger.info("1단계: Admin API에서 카테고리명 조회 시작");
            String categoryName;
            try {
                categoryName = adminApiClient.getCategoryNameById(categoryId);
                logger.info("1단계 성공: 조회된 카테고리명 = {}", categoryName);
            } catch (Exception e) {
                logger.error("1단계 실패: Admin API 카테고리명 조회 오류 - {}", e.getMessage(), e);
                throw new RuntimeException("Admin API에서 카테고리명 조회 실패", e);
            }
            
            // 2. Admin API를 통해 category_id로 담당자 정보 조회
            logger.info("2단계: Admin API에서 담당자 정보 조회 시작");
            AssigneeInfo assigneeInfo;
            try {
                assigneeInfo = adminApiClient.getAssigneeInfo(categoryId);
                logger.info("2단계 성공: 담당자 정보 조회됨 - {} {}", 
                    assigneeInfo.getAssigneeTeam(), assigneeInfo.getAssigneeName());
            } catch (Exception e) {
                logger.error("2단계 실패: Admin API 담당자 정보 조회 오류 - {}", e.getMessage(), e);
                throw new RuntimeException("Admin API에서 담당자 정보 조회 실패", e);
            }
            
            // 3. Normalization API를 통해 category_id로 VOC 분석 결과 조회
            logger.info("3단계: Normalization API에서 VOC 데이터 조회 시작");
            List<VocAnalysisResponse> vocAnalysisResults;
            try {
                vocAnalysisResults = normalizationApiClient.getVocAnalysisByCategoryId(categoryId);
                logger.info("3단계 성공: VOC 데이터 조회됨 - 총 {}개", vocAnalysisResults.size());
            } catch (Exception e) {
                logger.error("3단계 실패: Normalization API VOC 데이터 조회 오류 - {}", e.getMessage(), e);
                logger.info("VOC 데이터 조회 실패로 빈 리스트로 처리하여 기본 메일 생성");
                vocAnalysisResults = List.of(); // 빈 리스트로 처리하여 기본 메일 생성
            }

            if (vocAnalysisResults.isEmpty()) {
                logger.info("4단계: VOC 데이터가 없어 기본 메일 생성");
                // VOC 데이터가 없을 때 기본 메일 생성
                String fullAssigneeName = assigneeInfo.getAssigneeTeam() + " " + assigneeInfo.getAssigneeName();
                String defaultSubject = "[개선 리포트] " + categoryName + " 관련 개선 리포트 전달";
                String defaultContent = "안녕하세요, " + fullAssigneeName + " 님.\n\n" + categoryName + " 관련 개선 리포트를 전달드립니다.\n\n" +
                    "현재 해당 카테고리에 대한 VOC 분석 데이터가 준비 중입니다.\n\n" +
                    "더 자세한 데이터는 대시보드에서 확인 가능합니다.\n\n감사합니다.";
                
                MailGenerateResponse defaultResponse = new MailGenerateResponse(defaultSubject, defaultContent);
                
                // 기본 메일도 발송 서비스에 자동 전송 (비동기)
                logger.info("기본 메일 자동 발송 요청");
                mailSendClient.sendMailAsync(categoryId, defaultSubject, defaultContent);
                
                logger.info("=== 기본 메일 생성 완료 ===");
                return defaultResponse;
            }

            // 4. 메일 내용 생성 - 완전 템플릿 기반 (LLM 제거)
            logger.info("4단계: 메일 내용 생성 시작 (템플릿 기반)");
            
            String mailContent;
            try {
                mailContent = generateMailWithTemplate(categoryName, assigneeInfo, vocAnalysisResults);
                logger.info("4단계 성공: 메일 내용 생성 완료 (길이: {}자)", mailContent.length());
            } catch (Exception e) {
                logger.error("4단계 실패: 메일 내용 생성 오류 - {}", e.getMessage(), e);
                throw new RuntimeException("메일 내용 생성 실패", e);
            }

            // 5. 메일 응답 객체 생성
            logger.info("5단계: 메일 응답 객체 생성");
            MailGenerateResponse response = new MailGenerateResponse();
            String subject = "[개선 리포트] " + categoryName + " 관련 개선 리포트 전달";
            response.setSubject(subject);
            response.setContent(mailContent);
            
            logger.info("5단계 성공: 메일 응답 객체 생성 완료");
            logger.info("생성된 메일 제목: {}", response.getSubject());
            logger.info("생성된 메일 본문 길이: {}자", response.getContent().length());

            // 6. 메일 발송 서비스에 자동 전송 (비동기)
            logger.info("6단계: 메일 자동 발송 요청");
            try {
                mailSendClient.sendMailAsync(categoryId, response.getSubject(), response.getContent());
                logger.info("6단계 성공: 메일 발송 요청 완료");
            } catch (Exception e) {
                logger.error("6단계 실패: 메일 발송 요청 오류 - {}", e.getMessage(), e);
                // 메일 발송 실패는 전체 프로세스를 중단시키지 않음
            }

            logger.info("=== 메일 콘텐츠 생성 완료 ===");
            return response;

        } catch (Exception e) {
            logger.error("=== 메일 콘텐츠 생성 중 최종 오류 발생 ===");
            logger.error("오류 타입: {}", e.getClass().getSimpleName());
            logger.error("오류 메시지: {}", e.getMessage());
            logger.error("스택 트레이스:", e);
            throw new RuntimeException("메일 콘텐츠 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}