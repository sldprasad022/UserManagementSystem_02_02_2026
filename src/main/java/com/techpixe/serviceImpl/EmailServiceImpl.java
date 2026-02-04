package com.techpixe.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.techpixe.entity.User;
import com.techpixe.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    // ---------------- Registration OTP ----------------
    @Override
    public void sendRegistrationOtp(String email, String otp, int expiryMinutes) 
    {
        sendHtmlMail(email,"OTP for Email Verification",buildRegistrationOtpTemplate(email, otp, expiryMinutes));  
    }

    // ---------------- Forgot Password OTP ----------------
    @Override
    public void sendForgotPasswordOtp(User user, String otp, int expiryMinutes) 
    {
        sendTextMail(user.getEmail(),"OTP for Password Reset",buildForgotPasswordTemplate(user, otp, expiryMinutes));   
    }

    // =====================================================
    // Common Mail Senders
    // =====================================================

    private void sendHtmlMail(String to, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromMail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private void sendTextMail(String to, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, false);

            helper.setFrom(fromMail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    // =====================================================
    // Templates
    // =====================================================

    private String buildRegistrationOtpTemplate(String email,String otp,int expiryMinutes) {

        return String.format("""
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial;">
                <h2>Email Verification</h2>
                <p>Email: <b>%s</b></p>
                <p>Your OTP:</p>
                <h1 style="color:#e91e63;">%s</h1>
                <p>This OTP is valid for <b>%d minutes</b>.</p>
                <p>If you didn’t request this, ignore this email.</p>
            </body>
            </html>
        """, email, otp, expiryMinutes);
    }

    private String buildForgotPasswordTemplate(User user,String otp,int expiryMinutes) {

        return """
            Hello %s,

            Your OTP for password reset is: %s

            This OTP is valid for %d minutes.

            If you did not request this, please ignore this email.

            Regards,
            Support Team
            """.formatted(
                user.getUserName(),
                otp,
                expiryMinutes
        );
    }
}
