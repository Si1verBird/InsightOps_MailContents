package com.example.InsightOps_mailcontents.service;

import com.example.InsightOps_mailcontents.dto.MailGenerateResponse;
import com.example.InsightOps_mailcontents.dto.VocAnalysisResponse;
import com.example.InsightOps_mailcontents.service.AdminApiClient;
import com.example.InsightOps_mailcontents.service.NormalizationApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailContentService {

    @Autowired
    private AdminApiClient adminApiClient;

    @Autowired
    private NormalizationApiClient normalizationApiClient;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MailGenerateResponse generateMailContent(String categoryId) {
        try {
            // 1. Admin API를 통해 category_id로 category_name 조회
            String categoryName = adminApiClient.getCategoryNameById(categoryId);
            
            // 2. Normalization API를 통해 category_id로 VOC 분석 결과 조회
            List<VocAnalysisResponse> vocAnalysisResults = normalizationApiClient
                .getVocAnalysisByCategoryId(categoryId);

            if (vocAnalysisResults.isEmpty()) {
                // VOC 데이터가 없을 때 기본 메일 생성
                String defaultSubject = "[개선 리포트] " + categoryName + " 관련 개선 리포트 전달";
                String defaultContent = "안녕하세요.\n\n" + categoryName + " 관련 개선 리포트를 전달드립니다.\n\n" +
                    "현재 해당 카테고리에 대한 VOC 분석 데이터가 준비 중입니다.\n\n" +
                    "더 자세한 데이터는 대시보드에서 확인 가능합니다.\n\n감사합니다.";
                return new MailGenerateResponse(defaultSubject, defaultContent);
            }

            // 3. OpenAI GPT API 호출을 위한 프롬프트 생성
            String prompt = buildPrompt(categoryName, vocAnalysisResults);

            // 4. GPT API 호출
            OpenAiService service = new OpenAiService(openaiApiKey);
            
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .messages(List.of(new ChatMessage("user", prompt)))
                .maxTokens(1500)
                .temperature(0.7)
                .build();

            String gptResponse = service.createChatCompletion(completionRequest)
                .getChoices().get(0).getMessage().getContent();

            // 5. GPT 응답에서 제목과 본문 분리
            return parseGptResponse(gptResponse, categoryName);

        } catch (Exception e) {
            throw new RuntimeException("메일 콘텐츠 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String categoryName, List<VocAnalysisResponse> vocAnalysisResults) {
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 정보를 바탕으로 담당자에게 전달할 개선 리포트 메일을 작성해주세요.\n\n");
        
        prompt.append("**메일 제목 규칙:**\n");
        prompt.append("형식: [개선 리포트] ").append(categoryName).append(" 관련 개선 리포트 전달\n\n");
        
        prompt.append("**메일 본문 구조:**\n");
        prompt.append("1. 서론: 담당자 인사 및 메일 주제 간략 설명\n");
        prompt.append("2. 본론:\n");
        prompt.append("   (1) 현재 상황:\n");
        
        for (int i = 0; i < vocAnalysisResults.size(); i++) {
            VocAnalysisResponse.Analysis analysis = vocAnalysisResults.get(i).getAnalysis();
            if (analysis != null) {
                prompt.append("   - ").append(analysis.getProblemSituation()).append("\n");
            }
        }
        
        prompt.append("   (2) 추천 해결 방법:\n");
        for (int i = 0; i < vocAnalysisResults.size(); i++) {
            VocAnalysisResponse.Analysis analysis = vocAnalysisResults.get(i).getAnalysis();
            if (analysis != null) {
                prompt.append("   - ").append(analysis.getSolutionApproach()).append("\n");
            }
        }
        
        prompt.append("   (3) 기대 효과:\n");
        for (int i = 0; i < vocAnalysisResults.size(); i++) {
            VocAnalysisResponse.Analysis analysis = vocAnalysisResults.get(i).getAnalysis();
            if (analysis != null) {
                prompt.append("   - ").append(analysis.getExpectedOutcome()).append("\n");
            }
        }
        
        prompt.append("   (4) 임팩트 분석:\n");
        prompt.append("   - 각 문제 해결 시 예상되는 정량적 효과를 반드시 포함해주세요 ");
        prompt.append("(예: 불만 민원 50건 감소, 처리시간 단축 2분, 고객 만족도 30% 향상)\n");
        
        prompt.append("3. 결론: '더 자세한 데이터는 대시보드에서 확인 가능' 문구 포함 및 마무리 인사\n\n");
        
        prompt.append("**요구사항:**\n");
        prompt.append("- 격식체 사용\n");
        prompt.append("- 담당자에게 전달하는 보고서/리포트 스타일\n");
        prompt.append("- 응답 형식: 반드시 JSON 형태로 응답\n");
        prompt.append("- JSON 형식: {\"subject\": \"메일 제목\", \"content\": \"메일 본문\"}\n");
        prompt.append("- JSON 외의 다른 텍스트는 포함하지 말 것\n");
        
        return prompt.toString();
    }

    private MailGenerateResponse parseGptResponse(String gptResponse, String categoryName) {
        try {
            // JSON 응답 파싱
            MailGenerateResponse response = objectMapper.readValue(gptResponse, MailGenerateResponse.class);
            return response;
            
        } catch (Exception e) {
            System.err.println("JSON 파싱 오류: " + e.getMessage());
            System.err.println("GPT 응답: " + gptResponse);
            
            // JSON 파싱 실패 시 기본값 반환
            String defaultSubject = "[개선 리포트] " + categoryName + " 관련 개선 리포트 전달";
            String defaultContent = "GPT 응답 파싱에 실패했습니다. 원본 응답: " + gptResponse;
            return new MailGenerateResponse(defaultSubject, defaultContent);
        }
    }
}
