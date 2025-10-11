package com.project.expensetracker.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.project.expensetracker.dto.ExpenseDTO;
import com.project.expensetracker.entity.ProfileEntity;
import com.project.expensetracker.repository.ProfileRepository;

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
		log.info("Job completed: sendDailyExpensesRemainder()");
	}
	
	@Scheduled(cron = "0 * * * * *",zone = "IST")
	//@Scheduled(cron = "0 0 23 * * *",zone = "IST")
	public void sendDailyExpenseSummary() {
		log.info("Job started: sendDailyExpenseSummary()");
		List<ProfileEntity> profiles = profileRepository.findAll();
		for(ProfileEntity profile : profiles) {
			List<ExpenseDTO> todaysExpenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());
			if(todaysExpenses.isEmpty()) {
				StringBuilder table = new StringBuilder();
				table.append("<table style='border-collapse:collapse;width:100%;'>")
				     .append("<tr style='background-color:#0112AC;color:white;text-align:left;'>")
				     .append("<th style='padding:10px;border:1px solid #ddd;'>#</th>")
		             .append("<th style='padding:10px;border:1px solid #ddd;'>Expense Name</th>")
		             .append("<th style='padding:10px;border:1px solid #ddd;'>Amount (₹)</th>")
		             .append("<th style='padding:10px;border:1px solid #ddd;'>Category</th>")
		             .append("<th style='padding:10px;border:1px solid #ddd;'>Date</th>")
		             .append("</tr>");
				int index = 1;
				for (ExpenseDTO expenseDTO : todaysExpenses) {
				    table.append("<tr>")
				         .append("<td style='padding:10px;border:1px solid #ddd;text-align:center;'>")
				         .append(index++)
				         .append("</td>")

				         .append("<td style='padding:10px;border:1px solid #ddd;'>")
				         .append(expenseDTO.getName())
				         .append("</td>")

				         .append("<td style='padding:10px;border:1px solid #ddd;text-align:right;'>")
				         .append(expenseDTO.getAmount())
				         .append("</td>")

				         .append("<td style='padding:10px;border:1px solid #ddd;'>")
				         .append(expenseDTO.getCategoryId() != null ? expenseDTO.getCategortName() : "N/A")
				         .append("</td>")

				         .append("<td style='padding:10px;border:1px solid #ddd;'>")
				         .append(expenseDTO.getDate())
				         .append("</td>")
				         .append("</tr>");
				}
				table.append("</table>");
				String body ="Hi"+profile.getFullname()+",<br/><br/> Here is a summary of your expenses for today:<br/><br/>"+table+"<br/><br/>Best regards, <br/>Money Manager Team";
				emailService.sendEmail(profile.getEmail(), "Your daily Expense Summary", body);
				log.info("Job completed: sendDailyExpenseSummary()");

			}
		}
	}
}
