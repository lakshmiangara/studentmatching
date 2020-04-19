package com.usf.studentmatching.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usf.studentmatching.model.Project;
import com.usf.studentmatching.model.StudentInterestPrimaryKey;
import com.usf.studentmatching.repository.ProjectRepository;
import com.usf.studentmatching.repository.StudentInterestRepository;
import com.usf.studentmatching.utils.MatchingAlgorithm;
import com.usf.studentmatching.utils.StudentInputValidation;
import com.usf.studentmatching.utils.StudentPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.*;
import java.util.List;

@RestController
public class StudentSelection {

    private final static Logger log = LoggerFactory.getLogger(StudentSelection.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StudentInterestRepository studentInterestRepository;

    @Autowired
    private StudentInputValidation studentInputValidation;

     //GET method
    @RequestMapping(method = RequestMethod.GET, value = "/list/project")
    public List<Project> getAllProjects() {
        log.info("-- getAllProjects --");
        List<Project> projectsList = new ArrayList<Project>();
        projectRepository.findAll().forEach(p -> projectsList.add(p));
        return projectsList;
    }

    //POST method
    @RequestMapping(method = RequestMethod.POST, value = "/list/studentinterest", produces = MediaType.APPLICATION_JSON_VALUE)
    public String postStudentInterest(@RequestBody StudentInterest studentInterest) {
        log.info("-- postStudentInterest --");
        String status = null;
        if (studentInputValidation.validate(studentInterest, projectRepository)) {
            StudentInterestPrimaryKey studentInterestPrimaryKey =
                    new StudentInterestPrimaryKey(studentInterest.getProjectid(),
                            studentInterest.getStudentid());
            com.usf.studentmatching.model.StudentInterest st =
                    new com.usf.studentmatching.model.StudentInterest(
                            studentInterestPrimaryKey,
                            studentInterest.getStudentskills(),
                            new Date(),
                            studentInterest.getWeekDay(),
                            studentInterest.getFirstName(),
                            studentInterest.getLastName(),
                            studentInterest.getMode(),
                            studentInterest.getPreference()
                    );
            try {
                studentInterestRepository.save(st);
            } catch (Exception e) {
                status = e.getMessage();
                log.error("Failed to save {} ", e.getMessage());
                return status;
            }
            status = "Saved Successful";
            log.info("Save is {} ", status);
        } else {
            status = "Failed to Save. Please enter all the required values";
            log.error("Save is {} ", status);
        }
        return status;
    }

    //GET

    /**
     *
     * @param projectId is optional. If project is provided only for that project we do compatible team else for all
     *                  projects in database we build compatible teams.
     * @return Map with projectid and Set of students who are compatible for the project.
     */
    @RequestMapping(method = RequestMethod.GET, value="/list/studentMatching")
    public Map<Integer, Set<StudentPreferences>> getStudentMatching(@RequestParam(required = false) Integer projectId) {
        List<com.usf.studentmatching.model.StudentInterest> stInterest = new ArrayList<>();
        Map<Integer, Set<StudentPreferences>> studentPreferenceMap =  new HashMap<>();
        if (projectId != null) {
            Optional<Project> project = projectRepository.findById(projectId);
            studentInterestRepository.findAll().forEach(st -> stInterest.add(st));
            MatchingAlgorithm.matchPreferences(studentPreferenceMap, stInterest, project.get());
            return studentPreferenceMap;
        }  else {
           projectRepository.findAll().forEach(project -> {
               studentInterestRepository.findAll().forEach(st -> stInterest.add(st));
               MatchingAlgorithm.matchPreferences(studentPreferenceMap, stInterest, project);
           });
           return studentPreferenceMap;
        }
    }



}
