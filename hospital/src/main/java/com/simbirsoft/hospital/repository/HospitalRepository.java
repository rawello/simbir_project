package com.simbirsoft.hospital.repository;

import com.simbirsoft.hospital.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
}