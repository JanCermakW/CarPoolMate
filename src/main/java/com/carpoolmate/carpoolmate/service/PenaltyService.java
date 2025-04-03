package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.Penalty;

import java.util.List;

public interface PenaltyService {
    public String applyPenalty(Penalty penalty);
    public List<Penalty> getPenaltiesForUser(Long userId);
}
