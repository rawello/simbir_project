package com.simbirsoft.hospital.service;

import com.simbirsoft.hospital.exception.ResourceNotFoundException;
import com.simbirsoft.hospital.model.Hospital;
import com.simbirsoft.hospital.repository.HospitalRepository;
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
import java.util.List;
import java.util.Map;

@Service
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    public Hospital createHospital(Hospital hospital) {
        Hospital savedHospital = hospitalRepository.save(hospital);
        saveToElasticsearch(savedHospital);
        return savedHospital;
    }

    private void saveToElasticsearch(Hospital hospital) {
        IndexRequest request = new IndexRequest("hospitals")
                .id(hospital.getId().toString())
                .source(Map.of(
                        "name", hospital.getName(),
                        "address", hospital.getAddress(),
                        "contactPhone", hospital.getContactPhone()
                ), XContentType.JSON);
        try {
            IndexResponse response = elasticsearchClient.index(request, RequestOptions.DEFAULT);
            System.out.println("Indexed with ID: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public Hospital updateHospital(Long id, Hospital hospital) {
        Hospital existingHospital = hospitalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
        existingHospital.setName(hospital.getName());
        existingHospital.setAddress(hospital.getAddress());
        existingHospital.setContactPhone(hospital.getContactPhone());
        Hospital updatedHospital = hospitalRepository.save(existingHospital);
        updateInElasticsearch(updatedHospital);
        return updatedHospital;
    }

    private void updateInElasticsearch(Hospital hospital) {
        UpdateRequest request = new UpdateRequest("hospitals", hospital.getId().toString())
                .doc(Map.of(
                        "name", hospital.getName(),
                        "address", hospital.getAddress(),
                        "contactPhone", hospital.getContactPhone()
                ), XContentType.JSON);
        try {
            UpdateResponse response = elasticsearchClient.update(request, RequestOptions.DEFAULT);
            System.out.println("Updated with ID: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteHospital(Long id) {
        hospitalRepository.deleteById(id);
        deleteFromElasticsearch(id);
    }

    private void deleteFromElasticsearch(Long id) {
        DeleteRequest request = new DeleteRequest("hospitals", id.toString());
        try {
            DeleteResponse response = elasticsearchClient.delete(request, RequestOptions.DEFAULT);
            System.out.println("Deleted with ID: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
