package com.simbirsoft.timetable.service;

import com.simbirsoft.timetable.exception.ResourceNotFoundException;
import com.simbirsoft.timetable.model.Timetable;
import com.simbirsoft.timetable.repository.TimetableRepository;
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
public class TimetableService {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    public Timetable createTimetable(Timetable timetable) {
        Timetable savedTimetable = timetableRepository.save(timetable);
        saveToElasticsearch(savedTimetable);
        return savedTimetable;
    }

    private void saveToElasticsearch(Timetable timetable) {
        IndexRequest request = new IndexRequest("timetables")
                .id(timetable.getId().toString())
                .source(Map.of(
                        "hospitalId", timetable.getHospitalId(),
                        "doctorId", timetable.getDoctorId(),
                        "from", timetable.getFrom(),
                        "to", timetable.getTo(),
                        "room", timetable.getRoom()
                ), XContentType.JSON);
        try {
            IndexResponse response = elasticsearchClient.index(request, RequestOptions.DEFAULT);
            System.out.println("Indexed with ID: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Timetable> getAllTimetables() {
        return timetableRepository.findAll();
    }

    public Timetable updateTimetable(Long id, Timetable timetable) {
        Timetable existingTimetable = timetableRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Timetable not found"));
        existingTimetable.setHospitalId(timetable.getHospitalId());
        existingTimetable.setDoctorId(timetable.getDoctorId());
        existingTimetable.setFrom(timetable.getFrom());
        existingTimetable.setTo(timetable.getTo());
        existingTimetable.setRoom(timetable.getRoom());
        Timetable updatedTimetable = timetableRepository.save(existingTimetable);
        updateInElasticsearch(updatedTimetable);
        return updatedTimetable;
    }

    private void updateInElasticsearch(Timetable timetable) {
        UpdateRequest request = new UpdateRequest("timetables", timetable.getId().toString())
                .doc(Map.of(
                        "hospitalId", timetable.getHospitalId(),
                        "doctorId", timetable.getDoctorId(),
                        "from", timetable.getFrom(),
                        "to", timetable.getTo(),
                        "room", timetable.getRoom()
                ), XContentType.JSON);
        try {
            UpdateResponse response = elasticsearchClient.update(request, RequestOptions.DEFAULT);
            System.out.println("Updated with ID: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteTimetable(Long id) {
        timetableRepository.deleteById(id);
        deleteFromElasticsearch(id);
    }

    private void deleteFromElasticsearch(Long id) {
        DeleteRequest request = new DeleteRequest("timetables", id.toString());
        try {
            DeleteResponse response = elasticsearchClient.delete(request, RequestOptions.DEFAULT);
            System.out.println("Deleted with ID: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
