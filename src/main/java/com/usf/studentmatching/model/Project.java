package com.usf.studentmatching.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

@AllArgsConstructor
@Setter @Getter
@Table("projects")
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    @PrimaryKey
    private @NonNull int projectid;

    private @NonNull String projectname;

    private @NonNull String projectskills;

    private @NonNull int min;

    private @NonNull int max;

}
