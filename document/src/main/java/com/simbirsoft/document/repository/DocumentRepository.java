package com.simbirsoft.document.repository;

import com.simbirsoft.document.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}