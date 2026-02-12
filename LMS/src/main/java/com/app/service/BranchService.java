package com.app.service;

import com.app.entity.Branch;
import java.util.List;

public interface BranchService {

    Branch saveBranch(Branch branch);

    List<Branch> getAllBranches();
}
