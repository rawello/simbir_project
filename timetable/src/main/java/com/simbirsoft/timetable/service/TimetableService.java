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
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteDoctorTimetable(Long id) {
        timetableRepository.deleteByDoctorId(id);
    }

    public void deleteHospitalTimetable(Long id) {
        timetableRepository.deleteByHospitalId(id);
    }

    public List<Timetable> getHospitalTimetable(Long id, String from, String to) {
        return timetableRepository.findByHospitalIdAndFromBetween(id, LocalDateTime.parse(from), LocalDateTime.parse(to));
    }

    public List<Timetable> getDoctorTimetable(Long id, String from, String to) {
        return timetableRepository.findByDoctorIdAndFromBetween(id, LocalDateTime.parse(from), LocalDateTime.parse(to));
    }

    public List<Timetable> getHospitalRoomTimetable(Long id, String room, String from, String to) {
        return timetableRepository.findByHospitalIdAndRoomAndFromBetween(id, room, LocalDateTime.parse(from), LocalDateTime.parse(to));
    }

    public List<String> getAppointments(Long id) {
        Timetable timetable = timetableRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Timetable not found"));
        List<String> appointments = new ArrayList<>();
        LocalDateTime from = timetable.getFrom();
        LocalDateTime to = timetable.getTo();
        while (from.isBefore(to)) {
            appointments.add(from.toString());
            from = from.plusMinutes(30);
        }
        return appointments;
    }

    public void bookAppointment(Long id, String time) {
        // здесь реализация записи на приём
    }

    public void cancelAppointment(Long id) {
        // здесь реализация отмены записи на приём
    }
}