package com.fsoft.fsa.kindergarten.service.imp;
import com.fsoft.fsa.kindergarten.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;


@Service
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "dev")
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendEmail(String to,
                          String subject,
                          Map<String, String> templateModel,
                          String pathToAttachment) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        Resource resource = new ClassPathResource(pathToAttachment);
        String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));

        for (Map.Entry<String, String> entry : templateModel.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        helper.setText(content, true);

        mailSender.send(message);
    }
}
