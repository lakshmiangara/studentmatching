package com.usf.studentmatching.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usf.studentmatching.model.Project;
import com.usf.studentmatching.model.StudentInterestPrimaryKey;
import com.usf.studentmatching.repository.ProjectRepository;
import com.usf.studentmatching.repository.StudentInterestRepository;
import com.usf.studentmatching.utils.MatchingAlgorithm;
import com.usf.studentmatching.utils.StudentInputValidation;
import com.usf.studentmatching.utils.StudentPreferences;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class StudentSelection {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StudentInterestRepository studentInterestRepository;

    @Autowired
    private StudentInputValidation studentInputValidation;
     //GET method
    @RequestMapping(method = RequestMethod.GET, value = "/list/project")
    public List<Project> getAllProjects() {
        List<Project> projectsList = new ArrayList<Project>();
        projectRepository.findAll().forEach(p -> projectsList.add(p));
        return projectsList;
    }
    //POST method
    @RequestMapping(method = RequestMethod.POST, value = "/list/studentinterest")
    public String postStudentInterest(@RequestBody StudentInterest[] studentInterest) {
        String status = null;
        for (int i=0; i < studentInterest.length; i++) {
            if (studentInputValidation.validate(studentInterest[i], projectRepository)) {
                StudentInterestPrimaryKey studentInterestPrimaryKey =
                        new StudentInterestPrimaryKey(studentInterest[i].getProjectid(),
                                studentInterest[i].getStudentid());

                com.usf.studentmatching.model.StudentInterest st =
                        new com.usf.studentmatching.model.StudentInterest(
                                studentInterestPrimaryKey,
                                studentInterest[i].getStudentskills(),
                                new Date(),
                                studentInterest[i].getWeekDay(),
                                studentInterest[i].getFirstName(),
                                studentInterest[i].getLastName(),
                                studentInterest[i].getMode(),
                                studentInterest[i].getPreference()
                                );
                studentInterestRepository.save(st);
                status = "Successful";
            } else {
                status = "Please enter all the required values";
            }
        }
        return status;
    }


    @RequestMapping(method = RequestMethod.GET, value="/list/studentMatching")
    public List<StudentPreferences> getStudentMatching(@RequestParam(required = false) Integer projectId) {
        if (projectId != null || projectId.intValue() > 0) {
            Optional<Project> project = projectRepository.findById(projectId);
            List<com.usf.studentmatching.model.StudentInterest> stInterest = new ArrayList<>();
            studentInterestRepository.findAll().forEach(st -> stInterest.add(st));
            Map<Integer, List<StudentPreferences>> studentPreferenceMap =  new HashMap<>();
            MatchingAlgorithm.matchPreferences(studentPreferenceMap, stInterest, project.get());
            return studentPreferenceMap.get(projectId);
        }  else {
            return null;
        }
    }

    private Boolean validateProjectId(String value) {
        return !StringUtil.isNullOrEmpty(value);
    }


}
