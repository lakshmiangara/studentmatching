package com.usf.studentmatching.controller;

import com.usf.studentmatching.model.Project;
import com.usf.studentmatching.model.StudentInterestPrimaryKey;
import com.usf.studentmatching.repository.ProjectRepository;
import com.usf.studentmatching.repository.StudentInterestRepository;
import com.usf.studentmatching.utils.MatchingAlgorithm;
import com.usf.studentmatching.utils.StudentInputValidation;
import com.usf.studentmatching.utils.StudentPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.List;

/**
 * The purpose of this class is to accept GET, POST methods for student selection project
 * GET: There are 2 get methods. One GET method displays all the projects in the static table called projects.
 *      The other GET method returns the list of compatible teams for the given student preference list.
 */
@RestController
public class StudentSelection {

    private final static Logger log = LoggerFactory.getLogger(StudentSelection.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StudentInterestRepository studentInterestRepository;

    @Autowired
    private StudentInputValidation studentInputValidation;

    /**
     * GET method: getting list of projects from static database table called "projects" .
     * @return List of projects which are available for student selection.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/list/project")
    public List<Project> getAllProjects() {
        log.info("-- getAllProjects --");
        List<Project> projectsList = new ArrayList<Project>();
        projectRepository.findAll().forEach(p -> projectsList.add(p));
        return projectsList;
    }

    /**
     * POST method: after validation, student input values are saved to the "studentinterests" database table. if students
     *              input values are not validated, It will throw error message.
     *              student inputs are captured as array of stundent input objects. On each object is saved seperately
     *              in the DB. For each project and student id the entry is made in DB.
     * @param : array of StudentInterest objects, where each object represent student preference for the given projectid
     * @return String value with successful or failed message.
     */

    @RequestMapping(method = RequestMethod.POST, value = "/list/studentinterest", produces = MediaType.APPLICATION_JSON_VALUE)
    public String postStudentInterest(@RequestBody StudentInterest[] stInterest) {
        log.info("-- postStudentInterest --");
        String status = null;
        //Take in the array of input objects.
        for(StudentInterest studentInterest: stInterest) {
            //invoke validation to see all required values are present
            if (studentInputValidation.validate(studentInterest, projectRepository)) {
                //create a key using projectid and student id
                StudentInterestPrimaryKey studentInterestPrimaryKey =
                        new StudentInterestPrimaryKey(studentInterest.getProjectid(),
                                studentInterest.getStudentid());
                //all student preferences
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
        }
        return status;
    }

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
        //If projectid which is optional provided, then the studentpreferences will be made
        //for that projectid alone.
        if (projectId != null) {
            Optional<Project> project = projectRepository.findById(projectId);
            studentInterestRepository.findAll().forEach(st -> stInterest.add(st));

            //call to matchPreferences function that written in MatchingAlgorithm class
            MatchingAlgorithm.matchPreferences(studentPreferenceMap, stInterest, project.get());
            return studentPreferenceMap;
        }  else {
            //pull all students with preferences
            studentInterestRepository.findAll().forEach(st -> stInterest.add(st));
            //pull all projects and pass student preferences as list.
            //The iteration will check for each project id the student interests and builds and
            //map object with projectid and studentpreference which is an compatible list.
            projectRepository.findAll().forEach(project -> {
                MatchingAlgorithm.matchPreferences(studentPreferenceMap, stInterest, project);
            });
           return studentPreferenceMap;
        }
    }



}
