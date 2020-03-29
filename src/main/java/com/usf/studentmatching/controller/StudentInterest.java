package com.usf.studentmatching.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.time.DayOfWeek;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class StudentInterest {
    private int projectid;
    private String studentid;
    private String studentskills;
    private WeekDay[] weekDay;
    private String firstName;
    private String lastName;
    private String mode;
    private int preference;


    public String getWeekDay() {
        ObjectMapper mapper = new ObjectMapper();
        String str = null;
        try {
            str = mapper.writeValueAsString(weekDay);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return str;
    }
}

