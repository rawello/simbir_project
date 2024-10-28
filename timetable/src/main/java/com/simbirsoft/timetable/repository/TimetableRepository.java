package com.simbirsoft.timetable.repository;

import com.simbirsoft.timetable.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByHospitalIdAndFromBetween(Long hospitalId, LocalDateTime from, LocalDateTime to);
    List<Timetable> findByDoctorIdAndFromBetween(Long doctorId, LocalDateTime from, LocalDateTime to);
    List<Timetable> findByHospitalIdAndRoomAndFromBetween(Long hospitalId, String room, LocalDateTime from, LocalDateTime to);
    void deleteByDoctorId(Long doctorId);
    void deleteByHospitalId(Long hospitalId);
}