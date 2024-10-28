package com.simbirsoft.timetable.repository;

import com.simbirsoft.timetable.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
}