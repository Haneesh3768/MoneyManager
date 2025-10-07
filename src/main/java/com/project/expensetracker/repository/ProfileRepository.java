package com.project.expensetracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.expensetracker.entity.ProfileEntity;
@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer>
{
	//select * from profile_table where email= ?1
	Optional<ProfileEntity> findByEmail(String email);
	Optional<ProfileEntity> findByActivationToken(String activationToken);
	

}
