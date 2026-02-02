package com.techpixe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techpixe.entity.LoginAudit;

public interface LoginAuditRepository extends JpaRepository<LoginAudit, Long>
{

}
