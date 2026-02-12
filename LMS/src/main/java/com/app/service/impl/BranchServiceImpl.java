package com.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.entity.Branch;
import com.app.repository.BranchRepository;
import com.app.service.BranchService;


@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public Branch saveBranch(Branch branch) {
        return branchRepository.save(branch);
    }

    @Override
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }
}
