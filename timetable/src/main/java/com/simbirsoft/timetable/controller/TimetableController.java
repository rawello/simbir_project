package com.simbirsoft.timetable.controller;

import com.simbirsoft.timetable.model.Timetable;
import com.simbirsoft.timetable.service.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    @Autowired
    private TimetableService timetableService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Timetable> createTimetable(@RequestBody Timetable timetable) {
        return ResponseEntity.ok(timetableService.createTimetable(timetable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Timetable> updateTimetable(@PathVariable Long id, @RequestBody Timetable timetable) {
        return ResponseEntity.ok(timetableService.updateTimetable(id, timetable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteTimetable(@PathVariable Long id) {
        timetableService.deleteTimetable(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/doctor/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteDoctorTimetable(@PathVariable Long id) {
        timetableService.deleteDoctorTimetable(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/hospital/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteHospitalTimetable(@PathVariable Long id) {
        timetableService.deleteHospitalTimetable(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hospital/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Timetable>> getHospitalTimetable(@PathVariable Long id, @RequestParam String from, @RequestParam String to) {
        return ResponseEntity.ok(timetableService.getHospitalTimetable(id, from, to));
    }

    @GetMapping("/doctor/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Timetable>> getDoctorTimetable(@PathVariable Long id, @RequestParam String from, @RequestParam String to) {
        return ResponseEntity.ok(timetableService.getDoctorTimetable(id, from, to));
    }

    @GetMapping("/hospital/{id}/room/{room}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DOCTOR')")
    public ResponseEntity<List<Timetable>> getHospitalRoomTimetable(@PathVariable Long id, @PathVariable String room, @RequestParam String from, @RequestParam String to) {
        return ResponseEntity.ok(timetableService.getHospitalRoomTimetable(id, room, from, to));
    }

    @GetMapping("/{id}/appointments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getAppointments(@PathVariable Long id) {
        return ResponseEntity.ok(timetableService.getAppointments(id));
    }

    @PostMapping("/{id}/appointments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> bookAppointment(@PathVariable Long id, @RequestBody Map<String, String> appointment) {
        timetableService.bookAppointment(id, appointment.get("time"));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/appointment/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') || #id == authentication.principal.id")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        timetableService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }
}