package com.techpixe.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techpixe.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
	boolean existsByEmail(String email);
	
	Optional<User> findByEmail(String email);
	
	Page<User> findAll(Pageable pageable);
	
	void deleteByEmailVerifiedFalseAndCreatedAtBefore(LocalDateTime time);
	
}
