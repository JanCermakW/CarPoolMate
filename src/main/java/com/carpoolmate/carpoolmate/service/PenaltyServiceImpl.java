package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.Penalty;
import com.carpoolmate.carpoolmate.repository.PenaltyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PenaltyServiceImpl implements PenaltyService{

    @Autowired
    private PenaltyRepository penaltyRepository;

    @Override
    public String applyPenalty(Penalty penalty) {
        // Apply penalty based on review, criteria, etc.
        penaltyRepository.save(penalty);
        return "Penalty applied successfully";
    }

    @Override
    public List<Penalty> getPenaltiesForUser(Long userId) {
        return penaltyRepository.findByUserId(userId);
    }
}
