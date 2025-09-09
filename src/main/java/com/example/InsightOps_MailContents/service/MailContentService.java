package com.example.InsightOps_mailcontents.service;

import com.example.InsightOps_mailcontents.dto.AnalysisResult;
import com.example.InsightOps_mailcontents.dto.MailGenerateResponse;
import com.example.InsightOps_mailcontents.entity.VocNormalized;
import com.example.InsightOps_mailcontents.repository.VocNormalizedRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class MailContentService {

    @Autowired
    private VocNormalizedRepository vocNormalizedRepository;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MailGenerateResponse generateMailContent(String categorySmall) {
        try {
            // 1. DB에서 category_small에 해당하는 레코드 3개 조회
            List<VocNormalized> vocRecords = vocNormalizedRepository
                .findTop3ByCategorySmall(categorySmall);

            if (vocRecords.isEmpty()) {
                throw new RuntimeException("해당 카테고리에 대한 데이터가 없습니다: " + categorySmall);
            }

            // 2. analysis_result JSON에서 필요한 필드 추출
            List<AnalysisResult> analysisResults = new ArrayList<>();

            for (VocNormalized record : vocRecords) {
                try {
                    AnalysisResult result = objectMapper.readValue(
                        record.getAnalysisResult(), AnalysisResult.class);
                    analysisResults.add(result);
                } catch (Exception e) {
                    System.err.println("JSON 파싱 오류: " + e.getMessage());
                    // 파싱 실패 시 기본값 추가
                    AnalysisResult defaultResult = new AnalysisResult();
                    defaultResult.setProblemSituation("데이터 파싱 오류로 인한 기본값");
                    defaultResult.setSolutionApproach("데이터 파싱 오류로 인한 기본값");
                    defaultResult.setExpectedOutcome("데이터 파싱 오류로 인한 기본값");
                    analysisResults.add(defaultResult);
                }
            }

            // 3. OpenAI GPT API 호출을 위한 프롬프트 생성
            String prompt = buildPrompt(categorySmall, analysisResults);

            // 4. GPT API 호출
            OpenAiService service = new OpenAiService(openaiApiKey);
            
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(new ChatMessage("user", prompt)))
                .maxTokens(1500)
                .temperature(0.7)
                .build();

            String gptResponse = service.createChatCompletion(completionRequest)
                .getChoices().get(0).getMessage().getContent();

            // 5. GPT 응답에서 제목과 본문 분리
            return parseGptResponse(gptResponse, categorySmall);

        } catch (Exception e) {
            throw new RuntimeException("메일 콘텐츠 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String categorySmall, List<AnalysisResult> analysisResults) {
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 정보를 바탕으로 담당자에게 전달할 개선 리포트 메일을 작성해주세요.\n\n");
        
        prompt.append("**메일 제목 규칙:**\n");
        prompt.append("형식: [개선 리포트] ").append(categorySmall).append(" 관련 개선 리포트 전달\n\n");
        
        prompt.append("**메일 본문 구조:**\n");
        prompt.append("1. 서론: 담당자 인사 및 메일 주제 간략 설명\n");
        prompt.append("2. 본론:\n");
        prompt.append("   (1) 현재 상황:\n");
        
        for (int i = 0; i < analysisResults.size(); i++) {
            prompt.append("   - ").append(analysisResults.get(i).getProblemSituation()).append("\n");
        }
        
        prompt.append("   (2) 추천 해결 방법:\n");
        for (int i = 0; i < analysisResults.size(); i++) {
            prompt.append("   - ").append(analysisResults.get(i).getSolutionApproach()).append("\n");
        }
        
        prompt.append("   (3) 기대 효과:\n");
        for (int i = 0; i < analysisResults.size(); i++) {
            prompt.append("   - ").append(analysisResults.get(i).getExpectedOutcome()).append("\n");
        }
        
        prompt.append("   (4) 임팩트 분석:\n");
        prompt.append("   - 각 문제 해결 시 예상되는 정량적 효과를 반드시 포함해주세요 ");
        prompt.append("(예: 불만 민원 50건 감소, 처리시간 단축 2분, 고객 만족도 30% 향상)\n");
        
        prompt.append("3. 결론: '더 자세한 데이터는 대시보드에서 확인 가능' 문구 포함 및 마무리 인사\n\n");
        
        prompt.append("**요구사항:**\n");
        prompt.append("- 격식체 사용\n");
        prompt.append("- 담당자에게 전달하는 보고서/리포트 스타일\n");
        prompt.append("- 응답 형식: 제목과 본문을 구분하여 작성\n");
        prompt.append("- 제목은 '제목: '으로 시작\n");
        prompt.append("- 본문은 '본문: '으로 시작\n");
        
        return prompt.toString();
    }

    private MailGenerateResponse parseGptResponse(String gptResponse, String categorySmall) {
        try {
            String[] parts = gptResponse.split("본문:");
            
            String subject;
            String content;
            
            if (parts.length >= 2) {
                // GPT 응답에서 제목과 본문 분리
                subject = parts[0].replace("제목:", "").trim();
                content = parts[1].trim();
            } else {
                // 분리 실패 시 기본값 사용
                subject = "[개선 리포트] " + categorySmall + " 관련 개선 리포트 전달";
                content = gptResponse;
            }
            
            return new MailGenerateResponse(subject, content);
            
        } catch (Exception e) {
            // 파싱 실패 시 기본값 반환
            String defaultSubject = "[개선 리포트] " + categorySmall + " 관련 개선 리포트 전달";
            return new MailGenerateResponse(defaultSubject, gptResponse);
        }
    }
}
