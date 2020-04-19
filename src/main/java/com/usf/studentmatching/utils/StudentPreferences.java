package com.usf.studentmatching.utils;

import com.usf.studentmatching.controller.WeekDay;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudentPreferences implements Comparable<StudentPreferences> {

    private String studentId;
    private String firstName;
    private String lastName;
    private WeekDay[] weekDay;
    private String mode;
    private String skills;
    private boolean ignore;
    private int totalPoints;


    @Override
    public int compareTo(StudentPreferences o) {
        return Integer.valueOf(o.getTotalPoints()).compareTo(this.getTotalPoints());
    }
}
