package com.example.YONDU.repository.pt;

import com.example.YONDU.entity.pt.PtPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PtPackageRepository extends JpaRepository<PtPackage, Integer> {
    /**
     * 특정 회원이 구매한 PT 패키지 조회
     */
    List<PtPackage> findByMemberIdentifier(String memberIdentifier);
    List<PtPackage> findByTrainerIdentifier(String trainerIdentifier);
}