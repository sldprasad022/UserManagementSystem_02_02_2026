package com.techpixe.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailOTPVerificationForRegistration 
{
	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String fromMail;

	private static final int OTP_VALIDITY_MINUTES = 2;
	
	public void sendEmailOtp(String toEmail, String otp)
	 {
	        try 
	        {
	            MimeMessage message = javaMailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	            helper.setFrom(fromMail);
	            helper.setTo(toEmail);
	            helper.setSubject("OTP for Email Verification");
	            // 👉 Template method call
	            helper.setText(buildEmailOtpTemplate(toEmail, otp), true);

	            javaMailSender.send(message);
	        } 
	        catch (MessagingException e) 
	        {
	            throw new RuntimeException("Failed to send OTP email", e);
	        }
	    }

	    private String buildEmailOtpTemplate(String email, String otp)
	    {

	        return String.format("""
	            <!DOCTYPE html>
	            <html lang="en">
	            <head>
	                <meta charset="UTF-8">
	                <style>
	                    body {
	                        font-family: Arial, sans-serif;
	                        background-color: #f4f6f8;
	                        padding: 20px;
	                    }
	                    .container {
	                        max-width: 600px;
	                        margin: auto;
	                        background: #ffffff;
	                        padding: 20px;
	                        border-radius: 8px;
	                        box-shadow: 0 0 10px rgba(0,0,0,0.1);
	                    }
	                    .header {
	                        text-align: center;
	                        color: #2e7d32;
	                    }
	                    .otp {
	                        font-size: 26px;
	                        font-weight: bold;
	                        color: #e91e63;
	                        text-align: center;
	                        margin: 20px 0;
	                    }
	                    .footer {
	                        margin-top: 30px;
	                        font-size: 14px;
	                        color: #777;
	                        text-align: center;
	                    }
	                </style>
	            </head>
	            <body>
	                <div class="container">
	                    <h2 class="header">Email Verification</h2>
	                    <p>Hello,</p>
	                    <p>We received a request to verify the email address:</p>
	                    <p><b>%s</b></p>

	                    <p>Please use the below One-Time Password (OTP):</p>
	                    <div class="otp">%s</div>

	                    <p>This OTP is valid for <b>2 minutes</b>.</p>
	                    <p>If you did not request this, please ignore this email.</p>

	                    <div class="footer">
	                        <p>— Techpixe</p>
	                    </div>
	                </div>
	            </body>
	            </html>
	        """, email, otp);
	    }
}
