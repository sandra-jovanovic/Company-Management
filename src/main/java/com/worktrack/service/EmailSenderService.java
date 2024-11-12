package com.worktrack.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmail(String to,
                          String subject,
                          String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sandra.jovanovic643@gmail.com");
        message.setTo(to);
        message.setText(body);
        message.setSubject(subject);

        emailSender.send(message);

        System.out.println("Mail sent successfully...");
    }
}
