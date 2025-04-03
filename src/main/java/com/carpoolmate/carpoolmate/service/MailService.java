package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.MailStructure;

public interface MailService {
    public void sendMail(String mail, MailStructure mailStructure);
}
