package com.simbirsoft.document.service;

import com.simbirsoft.document.exception.ResourceNotFoundException;
import com.simbirsoft.document.model.Document;
import com.simbirsoft.document.repository.DocumentRepository;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    public Document createDocument(Document document) {
        document.setCreatedAt(LocalDateTime.now());
        Document savedDocument = documentRepository.save(document);
        saveToElasticsearch(savedDocument);
        return savedDocument;
    }

    private void saveToElasticsearch(Document document) {
        IndexRequest request = new IndexRequest("documents")
                .id(document.getId().toString())
                .source(Map.of(
                        "accountId", document.getAccountId(),
                        "content", document.getContent(),
                        "createdAt", document.getCreatedAt()
                ), XContentType.JSON);
        try {
            IndexResponse response = elasticsearchClient.index(request, RequestOptions.DEFAULT);
            System.out.println("Indexed with ID: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document updateDocument(Long id, Document document) {
        Document existingDocument = documentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        existingDocument.setAccountId(document.getAccountId());
        existingDocument.setContent(document.getContent());
        existingDocument.setCreatedAt(document.getCreatedAt());
        Document updatedDocument = documentRepository.save(existingDocument);
        updateInElasticsearch(updatedDocument);
        return updatedDocument;
    }

    private void updateInElasticsearch(Document document) {
        UpdateRequest request = new UpdateRequest("documents", document.getId().toString())
                .doc(Map.of(
                        "accountId", document.getAccountId(),
                        "content", document.getContent(),
                        "createdAt", document.getCreatedAt()
                ), XContentType.JSON);
        try {
            UpdateResponse response = elasticsearchClient.update(request, RequestOptions.DEFAULT);
            System.out.println("Updated with ID: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
        deleteFromElasticsearch(id);
    }

    private void deleteFromElasticsearch(Long id) {
        DeleteRequest request = new DeleteRequest("documents", id.toString());
        try {
            DeleteResponse response = elasticsearchClient.delete(request, RequestOptions.DEFAULT);
            System.out.println("Deleted with ID: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
