package com.example.InsightOps_MailContents.service;

import com.example.InsightOps_MailContents.dto.VocAnalysisResponse;
import com.example.InsightOps_MailContents.dto.NormalizationApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Arrays;

@Service
public class NormalizationApiClient {

    @Value("${normalization.api.base-url}")
    private String normalizationApiBaseUrl;

    private final RestTemplate restTemplate;

    public NormalizationApiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Normalization API에서 category_id로 VOC 분석 결과를 조회합니다.
     * @param categoryId 카테고리 ID
     * @return VOC 분석 결과 리스트 (최대 3개)
     */
    public List<VocAnalysisResponse> getVocAnalysisByCategoryId(String categoryId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(normalizationApiBaseUrl)
                    .path("/api/normalization/voc_normalized")
                    .queryParam("category_id", categoryId)
                    .queryParam("limit", 3)  // 최대 3개 제한
                    .toUriString();

            NormalizationApiResponse response = restTemplate.getForObject(url, NormalizationApiResponse.class);
            
            if (response != null && response.isSuccess()) {
                return response.getData();
            } else {
                throw new RuntimeException("Normalization API에서 유효하지 않은 응답을 받았습니다.");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Normalization API 호출 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 단일 VOC 분석 결과를 조회합니다.
     * @param categoryId 카테고리 ID
     * @return 첫 번째 VOC 분석 결과
     */
    public VocAnalysisResponse getFirstVocAnalysisByCategoryId(String categoryId) {
        List<VocAnalysisResponse> analyses = getVocAnalysisByCategoryId(categoryId);
        
        if (analyses.isEmpty()) {
            throw new RuntimeException("해당 category_id에 대한 VOC 분석 결과가 없습니다: " + categoryId);
        }
        
        return analyses.get(0);
    }
}
