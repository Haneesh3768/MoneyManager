package com.project.expensetracker.service;

import org.springframework.stereotype.Service;

import com.project.expensetracker.entity.ProfileEntity;
import com.project.expensetracker.repository.ProfileRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final ProfileRepository profileRepository;
	private final EmailService emailService;
	private final ExpenseService expenseService;
	
	@Value("${money.manager.frontend.url}")
	private String frontendUrl;
	
	
	@Scheduled(cron = "0 0 22 * * *",zone = "IST")
	public void sendDailyIncomeExpenseRemainder() {
		log.info("Job started: sendDailyIncomeExpenseRemainder()");
		List<ProfileEntity> profiles = profileRepository.findAll();
		for(ProfileEntity profile : profiles) {
			String body = "Hi " + profile.getFullname() + ",\n\n" +
                    "This is your daily reminder to review your income and expenses for today.\n" +
                    "Please check your account summary and make sure your records are up-to-date.\n\n" +
                    "You can view your dashboard here: " + frontendUrl + "\n\n" +
                    "Keep tracking and managing your finances efficiently!\n\n" +
                    "Best regards,\n" +
                    "Money Manager Team";
			emailService.sendEmail(profile.getEmail(), "Daily remainder : Add your income and expenses", body);
		}
	}
}
