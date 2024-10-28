package com.simbirsoft.timetable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TimetableApplication {
    public static void main(String[] args) {
        SpringApplication.run(TimetableApplication.class, args);
    }
}