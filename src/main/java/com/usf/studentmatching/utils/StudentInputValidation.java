package com.usf.studentmatching.utils;

import com.usf.studentmatching.controller.StudentInterest;
import com.usf.studentmatching.model.Project;
import com.usf.studentmatching.repository.ProjectRepository;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StudentInputValidation {

    public Boolean validate(StudentInterest studentInterest, ProjectRepository projectRepository) {
        if (validateProjectId(studentInterest.getProjectid(), projectRepository) &&
                validateText(studentInterest.getStudentid()) &&
                validateText(studentInterest.getStudentskills()) &&
                validateText(studentInterest.getMode()) ) {
            switch (studentInterest.getMode()) {
                case "Sync":
                case "Async":
                    break;
                default:
                    return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private Boolean validateProjectId(int value, ProjectRepository projectRepository) {
        Optional<Project> prj = projectRepository.findById(Integer.valueOf(value));
        return prj.isPresent();
    }

    private Boolean validateText(String value) {
        return !StringUtil.isNullOrEmpty(value);
    }

}