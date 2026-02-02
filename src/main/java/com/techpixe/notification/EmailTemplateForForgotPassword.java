package com.techpixe.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.techpixe.entity.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailTemplateForForgotPassword 
{
	@Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMail;
    
    private static final int OTP_VALIDITY_MINUTES = 2;
    
    public void sendForgotPasswordOtp(User user, String otp) 
    {

        try 
        {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromMail);
            helper.setTo(user.getEmail());
            helper.setSubject("OTP for Password Reset");

            String body =
                    "Hello " + user.getUserName() + ",\n\n" +
                    "Your OTP for password reset is: " + otp + "\n\n" +
                    "This OTP is valid for " + OTP_VALIDITY_MINUTES + " minutes.\n\n" +
                    "If you did not request this, please ignore this email.\n\n" +
                    "Regards,\nSupport Team";

            helper.setText(body);
            javaMailSender.send(message);

        } 
        catch (MessagingException e) 
        {
            throw new RuntimeException("Failed to send OTP email");
        }
    }
    
}
