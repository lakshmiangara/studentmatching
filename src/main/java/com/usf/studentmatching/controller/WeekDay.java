package com.usf.studentmatching.controller;

import lombok.*;

import java.time.DayOfWeek;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class WeekDay {
    public DayOfWeek dayOfWeek;
    public String timeOfDay;
}
