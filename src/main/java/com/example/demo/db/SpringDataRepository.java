package com.example.demo.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRepository extends JpaRepository<HibMsg, Long> {
}
