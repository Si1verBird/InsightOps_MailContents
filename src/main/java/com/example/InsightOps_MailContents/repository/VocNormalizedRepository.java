package com.example.InsightOps_mailcontents.repository;

import com.example.InsightOps_mailcontents.entity.VocNormalized;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocNormalizedRepository extends JpaRepository<VocNormalized, String> {

    /**
     * consulting_category에 해당하는 레코드 3개를 조회합니다.
     * consulting_category 순으로 정렬하여 가져옵니다.
     */
    @Query("SELECT v FROM VocNormalized v WHERE v.consultingCategory = :consultingCategory ORDER BY v.consultingCategory ASC")
    List<VocNormalized> findTop3ByConsultingCategory(@Param("consultingCategory") String consultingCategory);
}