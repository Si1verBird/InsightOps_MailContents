package com.example.InsightOps_MailContents.service;

import com.example.InsightOps_MailContents.dto.AdminApiResponse;
import com.example.InsightOps_MailContents.dto.AssigneeApiResponse;
import com.example.InsightOps_MailContents.dto.AssigneeInfo;
import com.example.InsightOps_MailContents.dto.ConsultingCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class AdminApiClient {

    @Value("${admin.api.base-url}")
    private String adminApiBaseUrl;

    private final RestTemplate restTemplate;

    public AdminApiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Admin 서비스에서 consulting_category 테이블 데이터를 조회합니다.
     */
    public List<ConsultingCategory> getConsultingCategories() {
        try {
            String url = adminApiBaseUrl + "/api/admin/consulting_category";
            
            AdminApiResponse response = restTemplate.getForObject(url, AdminApiResponse.class);
            
            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData().getData();
            } else {
                throw new RuntimeException("Admin API에서 유효하지 않은 응답을 받았습니다.");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Admin API 호출 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * category_id로 consulting_category 정보를 조회합니다.
     */
    public Optional<ConsultingCategory> getConsultingCategoryById(String categoryId) {
        try {
            List<ConsultingCategory> categories = getConsultingCategories();
            
            return categories.stream()
                .filter(category -> categoryId.equals(category.getCategoryId()))
                .findFirst();
                
        } catch (Exception e) {
            throw new RuntimeException("Category ID로 카테고리 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * category_id로 consulting_category를 조회합니다.
     */
    public String getCategoryNameById(String categoryId) {
        Optional<ConsultingCategory> category = getConsultingCategoryById(categoryId);
        
        if (category.isPresent()) {
            return category.get().getConsultingCategory();
        } else {
            throw new RuntimeException("해당 category_id에 대한 카테고리를 찾을 수 없습니다: " + categoryId);
        }
    }

    /**
     * category_id로 담당자 정보를 조회합니다.
     */
    public AssigneeInfo getAssigneeInfo(String categoryId) {
        try {
            String url = adminApiBaseUrl + "/api/admin/assignee?category_id=" + categoryId;
            
            AssigneeApiResponse response = restTemplate.getForObject(url, AssigneeApiResponse.class);
            
            if (response != null && response.isSuccess() && response.getData() != null) {
                List<AssigneeInfo> assignees = response.getData().getData();
                if (!assignees.isEmpty()) {
                    return assignees.get(0); // 첫 번째 담당자 정보 반환
                }
            }
            
            // 담당자 정보가 없을 경우 기본값 반환
            AssigneeInfo defaultAssignee = new AssigneeInfo();
            defaultAssignee.setAssigneeTeam("담당팀");
            defaultAssignee.setAssigneeName("담당자");
            return defaultAssignee;
            
        } catch (Exception e) {
            System.err.println("담당자 정보 조회 중 오류 발생: " + e.getMessage());
            // 오류 발생 시 기본값 반환
            AssigneeInfo defaultAssignee = new AssigneeInfo();
            defaultAssignee.setAssigneeTeam("담당팀");
            defaultAssignee.setAssigneeName("담당자");
            return defaultAssignee;
        }
    }
}
