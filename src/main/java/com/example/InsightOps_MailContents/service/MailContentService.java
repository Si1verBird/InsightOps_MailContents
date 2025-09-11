package com.example.InsightOps_mailcontents.service;

import com.example.InsightOps_mailcontents.dto.AssigneeInfo;
import com.example.InsightOps_mailcontents.dto.MailGenerateResponse;
import com.example.InsightOps_mailcontents.dto.VocAnalysisResponse;
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
            
            // 2. Admin API를 통해 category_id로 담당자 정보 조회
            AssigneeInfo assigneeInfo = adminApiClient.getAssigneeInfo(categoryId);
            
            // 3. Normalization API를 통해 category_id로 VOC 분석 결과 조회
            List<VocAnalysisResponse> vocAnalysisResults;
            try {
                vocAnalysisResults = normalizationApiClient.getVocAnalysisByCategoryId(categoryId);
            } catch (Exception e) {
                System.err.println("Normalization API 호출 실패, 기본 메일 생성: " + e.getMessage());
                vocAnalysisResults = List.of(); // 빈 리스트로 처리하여 기본 메일 생성
            }

            if (vocAnalysisResults.isEmpty()) {
                // VOC 데이터가 없을 때 기본 메일 생성
                String fullAssigneeName = assigneeInfo.getAssigneeTeam() + " " + assigneeInfo.getAssigneeName();
                String defaultSubject = "[개선 리포트] " + categoryName + " 관련 개선 리포트 전달";
                String defaultContent = "안녕하세요, " + fullAssigneeName + " 님.\n\n" + categoryName + " 관련 개선 리포트를 전달드립니다.\n\n" +
                    "현재 해당 카테고리에 대한 VOC 분석 데이터가 준비 중입니다.\n\n" +
                    "더 자세한 데이터는 대시보드에서 확인 가능합니다.\n\n감사합니다.";
                return new MailGenerateResponse(defaultSubject, defaultContent);
            }

            // 4. OpenAI GPT API 호출을 위한 프롬프트 생성
            String prompt = buildPrompt(categoryName, assigneeInfo, vocAnalysisResults);

            // 5. GPT API 호출
            OpenAiService service = new OpenAiService(openaiApiKey);
            
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .messages(List.of(new ChatMessage("user", prompt)))
                .maxTokens(2000)
                .temperature(0.7)
                .build();

            String gptResponse = service.createChatCompletion(completionRequest)
                .getChoices().get(0).getMessage().getContent();

            // 6. GPT 응답에서 제목과 본문 분리
            return parseGptResponse(gptResponse, categoryName);

        } catch (Exception e) {
            throw new RuntimeException("메일 콘텐츠 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String categoryName, AssigneeInfo assigneeInfo, List<VocAnalysisResponse> vocAnalysisResults) {
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 정보를 바탕으로 담당자에게 전달할 개선 리포트 메일을 작성해주세요. 담당자 팀과 이름을 첫줄에 명시해주세요.\n\n");
        
        // 담당자 정보 추가
        String assigneeTeam = assigneeInfo.getAssigneeTeam();
        String assigneeName = assigneeInfo.getAssigneeName();
        String fullAssigneeName = assigneeTeam + " " + assigneeName;
        prompt.append("**담당자 정보:**\n");
        prompt.append("- 담당자: ").append(assigneeTeam).append(" ").append(assigneeName).append(" 님\n\n");
        
        prompt.append("**메일 제목 규칙:**\n");
        prompt.append("형식: [개선 리포트] ").append(categoryName).append(" 관련 개선 리포트 전달\n\n");
        
        // VOC 분석 데이터 상세 정보 추가
        prompt.append("**분석 데이터:**\n");
        for (int i = 0; i < vocAnalysisResults.size(); i++) {
            VocAnalysisResponse.Analysis analysis = vocAnalysisResults.get(i).getAnalysis();
            if (analysis != null) {
                prompt.append("VOC 사례 ").append(i + 1).append(":\n");
                prompt.append("- 문제 상황: ").append(analysis.getProblemSituation()).append("\n");
                prompt.append("- 해결 방법: ").append(analysis.getSolutionApproach()).append("\n");
                prompt.append("- 기대 효과: ").append(analysis.getExpectedOutcome()).append("\n\n");
            }
        }
        
        prompt.append("**메일 본문 구조:**\n");
        prompt.append("1. 서론: ").append(fullAssigneeName).append(" 님께 인사 및 메일 주제 간략 설명\n");
        prompt.append("2. 본론:\n");
        prompt.append("   (1) 현재 상황 분석: 위 VOC 사례들의 문제 상황을 각각 상세히 설명\n");
        prompt.append("   (2) 추천 해결 방법: 각 VOC 사례별 해결 방법을 구체적으로 제시\n");
        prompt.append("   (3) 기대 효과: 각 해결 방법 적용 시 예상되는 개별 효과 설명\n");
        prompt.append("   (4) 종합 임팩트 분석: 전체적인 개선 효과 및 정량적 지표 제시\n");
        prompt.append("       - 각 문제 해결 시 예상되는 정량적 효과를 반드시 포함\n");
        prompt.append("       - 예시: 불만 민원 30% 감소, 처리시간 평균 3분 단축, 고객 만족도 25% 향상\n");
        prompt.append("   (5) 추가 분석 및 권장사항: GPT가 VOC 데이터를 바탕으로 추가적인 인사이트와 개선 제안을 생성\n");
        prompt.append("3. 결론: '더 자세한 데이터는 대시보드에서 확인 가능' 문구 포함 및 마무리 인사\n\n");
        
        prompt.append("**요구사항:**\n");
        prompt.append("- 격식체 사용\n");
        prompt.append("- 담당자에게 전달하는 보고서/리포트 스타일\n");
        prompt.append("- 각 VOC 사례를 개별적으로 분석하고 설명\n");
        prompt.append("- 추가 분석에서는 데이터 패턴, 트렌드, 근본 원인 분석 등을 포함\n");
        prompt.append("- 실행 가능한 구체적인 개선 방안 제시\n");
        prompt.append("- 응답 형식: 반드시 JSON 형태로 응답\n");
        prompt.append("- JSON 형식: {\"subject\": \"메일 제목\", \"content\": \"메일 본문\"}\n");
        prompt.append("- JSON 외의 다른 텍스트는 포함하지 말 것\n");
        
        return prompt.toString();
    }

    private MailGenerateResponse parseGptResponse(String gptResponse, String categoryName) {
        try {
            String jsonContent = extractJsonFromResponse(gptResponse);
            
            // JSON 응답 파싱
            MailGenerateResponse response = objectMapper.readValue(jsonContent, MailGenerateResponse.class);
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
    
    private String extractJsonFromResponse(String gptResponse) {
        // 마크다운 코드 블록 제거 (```json ... ``` 형태)
        if (gptResponse.contains("```json")) {
            int startIndex = gptResponse.indexOf("```json") + 7; // "```json" 이후부터
            int endIndex = gptResponse.lastIndexOf("```");
            if (startIndex < endIndex) {
                return gptResponse.substring(startIndex, endIndex).trim();
            }
        }
        
        // 일반 코드 블록 제거 (``` ... ``` 형태)
        if (gptResponse.contains("```")) {
            int startIndex = gptResponse.indexOf("```") + 3;
            int endIndex = gptResponse.lastIndexOf("```");
            if (startIndex < endIndex) {
                String content = gptResponse.substring(startIndex, endIndex).trim();
                // 첫 줄이 "json"인 경우 제거
                if (content.startsWith("json\n")) {
                    content = content.substring(5);
                }
                return content;
            }
        }
        
        // 코드 블록이 없는 경우 원본 반환
        return gptResponse.trim();
    }
}
