package com.fsoft.fsa.kindergarten.service;

import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.Map;

public interface EmailService {
    @Async
    void sendEmail(String to,
                   String subject,
                   Map<String, String> templateModel,
                   String pathToAttachment) throws MessagingException, IOException;
}
