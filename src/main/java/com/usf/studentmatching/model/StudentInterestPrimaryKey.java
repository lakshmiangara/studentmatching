package com.usf.studentmatching.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

/**
 * Purpose: This class defines the primary key for studentintersts table in cassandra database.
 */

@AllArgsConstructor
@PrimaryKeyClass
@Setter @Getter
@EqualsAndHashCode
public class StudentInterestPrimaryKey implements Serializable {

    @PrimaryKeyColumn(name="projectid", ordinal=0, type= PrimaryKeyType.PARTITIONED)
    private int projectId;

    @PrimaryKeyColumn(name="studentid", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String studentId;

}
