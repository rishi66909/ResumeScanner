package com.resumescanner.repository;

import com.resumescanner.entity.ResumeScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeScanRepository extends JpaRepository<ResumeScan, Long> {

    List<ResumeScan> findAllByOrderByScannedAtDesc();

    @Query("SELECT r FROM ResumeScan r WHERE r.matchScore >= :minScore ORDER BY r.matchScore DESC")
    List<ResumeScan> findByMatchScoreGreaterThanEqual(Double minScore);

    List<ResumeScan> findByFileNameContainingIgnoreCase(String fileName);
}