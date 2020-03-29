package com.usf.studentmatching.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usf.studentmatching.controller.WeekDay;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
@Table("studentinterests")
public class StudentInterest {
    @PrimaryKey
    private StudentInterestPrimaryKey studentInterestPrimaryKey;
    private @NonNull String studentskills;
    private @NonNull Date createdate;
    private @NonNull String dayofweek;
    private @NonNull String firtname;
    private @NonNull String lastname;
    private @NonNull String mode;
    private  int studentpref;


    public WeekDay[] getDayOfWeek() {
        ObjectMapper mapper = new ObjectMapper();
        List st = null;
        try {
            st = Arrays.asList(mapper.readValue(this.dayofweek, WeekDay[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (WeekDay[])st.stream().toArray(WeekDay[]::new);
    }
}

