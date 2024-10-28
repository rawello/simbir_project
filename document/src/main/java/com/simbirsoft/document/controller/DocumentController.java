package com.simbirsoft.document.controller;

import com.simbirsoft.document.model.Document;
import com.simbirsoft.document.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/account/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR') || #id == authentication.principal.id")
    public ResponseEntity<List<Document>> getAccountHistory(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getAccountHistory(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR') || #id == authentication.principal.id")
    public ResponseEntity<Document> getHistoryById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getHistoryById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DOCTOR')")
    public ResponseEntity<Document> createHistory(@RequestBody Document document) {
        return ResponseEntity.ok(documentService.createHistory(document));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DOCTOR')")
    public ResponseEntity<Document> updateHistory(@PathVariable Long id, @RequestBody Document document) {
        return ResponseEntity.ok(documentService.updateHistory(id, document));
    }
}