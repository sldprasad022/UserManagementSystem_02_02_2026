package com.techpixe.scheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.techpixe.repository.UserRepository;

@Component
public class UserCleanupScheduler 
{
	@Autowired
	private UserRepository userRepository;
	
	@Scheduled(cron = "0 0 */6 * * *") // every 6 hours
	public void removeUnverifiedUsers() 
	{
	    LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
	    userRepository.deleteByEmailVerifiedFalseAndCreatedAtBefore(cutoff);
	}
}
