package com.usf.studentmatching.utils;

import com.usf.studentmatching.model.Project;
import com.usf.studentmatching.model.StudentInterest;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.abs;

public class MatchingAlgorithm {

    private static int SKILL_POINTS = 25;

    public static
    Map<Integer, List<StudentPreferences>> matchPreferences(Map<Integer, List<StudentPreferences>> studentPreferenceMap,
                                           List<StudentInterest> stInterest, Project project) {
        int numOfStudents = stInterest.size();

        List<StudentPreferences> studentPreferencesList = new ArrayList<>();
        stInterest.forEach(
                st -> {

                    if (st.getStudentInterestPrimaryKey().getProjectId() != project.getProjectid())
                        return;
                    StudentPreferences studentPreferences = new StudentPreferences();
                    studentPreferences.setFirstName(st.getFirtname());
                    studentPreferences.setLastName(st.getLastname());
                    studentPreferences.setSkills(st.getStudentskills());
                    studentPreferences.setMode(st.getMode());
                    studentPreferences.setWeekDay(st.getDayOfWeek());
                    studentPreferences.setStudentId(st.getStudentInterestPrimaryKey().getStudentId());
                    studentPreferences.setTotalPoints(CompareSkill(st, project) + ComparePreference(st, project));
                    studentPreferencesList.add(studentPreferences);
                }
        );
        Collections.sort(studentPreferencesList);
        List<StudentPreferences> finalGroup = formCompatableTeams(project, studentPreferencesList);
        return (Map<Integer, List<StudentPreferences>>)
                studentPreferenceMap.put(Integer.valueOf(project.getProjectid()), finalGroup);
    }

    private static int CompareSkill(StudentInterest st, Project project) {
        String stSkills = st.getStudentskills().toUpperCase();
        String prSkills = project.getProjectskills().toUpperCase();
        int points = 0;
        //Logic to split skills into tokens. Assign for skill match certain points.
        //For full match, a student will get full credit.
        String[] projTokens = prSkills.split(",");
        String[] stdTokens = stSkills.split(",");
        for (String stdToken : stdTokens) {
            for (String projToken : projTokens) {
                if (stdToken.trim().equals(projToken.trim()))
                    points += SKILL_POINTS;
            }
        }
        return points;
    }

    private static int ComparePreference(StudentInterest st, Project project) {
        return abs(st.getStudentpref() - 11) * 50;
    }

    private static List<StudentPreferences>  formCompatableTeams(Project project, List<StudentPreferences> studentPreferences) {
        CopyOnWriteArrayList<String> projSkillList = new CopyOnWriteArrayList<>();
        SortedSet<String> projSkillSet = new TreeSet<>();
        String[] prSkills = project.getProjectskills().toUpperCase().split(",");
        for (String skill : prSkills) {
            projSkillList.add(skill.trim());
            projSkillSet.add(skill.trim());
        }
        List<StudentPreferences> finalGroupStudents = new ArrayList<>();
        int maxReq = project.getMax();
        for (StudentPreferences st: studentPreferences) {
            String[] stSkills = st.getSkills().toUpperCase().split(",");
            for (String skill: stSkills) {
                for (String prSkill: projSkillList) {
                    if (skill.trim().equals(prSkill.trim())) {
                        projSkillList.remove(prSkill);
                        finalGroupStudents.add(st);
                    }
                }
            }
        }
        return finalGroupStudents;
    }
}
