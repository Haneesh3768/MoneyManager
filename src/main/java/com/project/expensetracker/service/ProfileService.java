package com.project.expensetracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.expensetracker.dto.AuthDTO;
import com.project.expensetracker.dto.ProfileDTO;
import com.project.expensetracker.entity.ProfileEntity;
import com.project.expensetracker.repository.ProfileRepository;
import com.project.expensetracker.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

	private final ProfileRepository profileRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	
	@Value("{app.acitvation.url}")
	private String activationUrl;
	
	public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        //send activation email
        String activationLink =activationUrl+"/api/activate?token=" +newProfile.getActivationToken();
        String subject = "Activate your Expense Tracker Account";
        String body = "Click on the following activation link to acivate your account: "+activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        
        return toDto(newProfile);

	}

    public ProfileEntity toEntity(ProfileDTO profileDTO){
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullname(profileDTO.getFullname())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();

	}

    public ProfileDTO toDto(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullname(profileEntity.getFullname())
                .email(profileEntity.getEmail())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }
   
    public boolean activateProfile(String activationToken) {
    	return profileRepository.findByActivationToken(activationToken)
                .map(profileEntity -> {
                    profileEntity.setIsActive(true);
                    profileRepository.save(profileEntity);
                    return  true;
                })
                .orElse(false); 
    }
    
    public boolean isAccountActive(String email) {
    	return profileRepository.findByEmail(email)
    			.map(ProfileEntity::getIsActive)
    			.orElse(false);
    }
    
    public ProfileEntity getCurrentProfile() {
    	Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
    
    	return profileRepository.findByEmail(authentication.getName())
    			.orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: "+authentication.getName()));    	
    }
    
    public ProfileDTO getPublicProfile(String email) {
    	ProfileEntity currentUser = null;
    	if(email == null) {
    		currentUser = getCurrentProfile();
    	}else {
    		currentUser = profileRepository.findByEmail(email)
    		.orElseThrow(() -> new UsernameNotFoundException("Profile Not found with email: "+email));
    	}
    	
    	return ProfileDTO.builder()
    			.id(currentUser.getId())
    			.fullname(currentUser.getFullname())
    			.email(currentUser.getEmail())
    			.profileImageUrl(currentUser.getProfileImageUrl())
    			.createdAt(currentUser.getCreatedAt())
    			.updatedAt(currentUser.getUpdatedAt())
    			.build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            System.out.println("Attempting login for: " + authDTO.getEmail());
            System.out.println("Raw password entered: " + authDTO.getPassword());

            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword())
            );

            System.out.println("Authentication success for user: " + auth.getName());

            String token = jwtUtil.generateToken(authDTO.getEmail());

            return Map.of(
                "token", token,
                "user", getPublicProfile(authDTO.getEmail())
            );
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Invalid email or password");
        }
    }


}
