package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.entity.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {
}
