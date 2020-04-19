package com.usf.studentmatching.utils;

import com.usf.studentmatching.controller.WeekDay;
import com.usf.studentmatching.model.Project;
import com.usf.studentmatching.model.StudentInterest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.abs;

public class MatchingAlgorithm {

    private static int SKILL_POINTS = 25;

    public static
    void matchPreferences(Map<Integer, Set<StudentPreferences>> studentPreferenceMap,
                                           List<StudentInterest> stInterest, Project project) {
        int numOfStudents = stInterest.size();

        List<StudentPreferences> studentPreferencesList = new ArrayList<>();
        stInterest.forEach(
                st -> {
                    if (st.getStudentInterestPrimaryKey().getProjectId() != project.getProjectid())
                        return;
                    //StudentPreferences studentPreferences = getStudentPoints(project, st);
                    //studentPreferencesList.add(studentPreferences);
                    studentPreferencesList.add(getStudentPoints(project, st));
                }
        );
        //sorting the list
        Collections.sort(studentPreferencesList);
        Set<StudentPreferences> finalGroup = formCompatableTeams(project, studentPreferencesList);
        studentPreferenceMap.put(Integer.valueOf(project.getProjectid()), finalGroup);
    }

    private static StudentPreferences  getStudentPoints(Project project, StudentInterest st) {
        StudentPreferences studentPreferences = new StudentPreferences();
        studentPreferences.setFirstName(st.getFirtname());
        studentPreferences.setLastName(st.getLastname());
        studentPreferences.setSkills(st.getStudentskills());
        studentPreferences.setMode(st.getMode());
        studentPreferences.setWeekDay(st.getDayOfWeek());
        studentPreferences.setStudentId(st.getStudentInterestPrimaryKey().getStudentId());
        studentPreferences.setTotalPoints(CompareSkill(st, project) + ComparePreference(st, project));
        return studentPreferences;
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

    private static Set<StudentPreferences>  formCompatableTeams(Project project, List<StudentPreferences> stPreferences) {
        CopyOnWriteArrayList<String> projSkillList = new CopyOnWriteArrayList<>();
        List<StudentPreferences> studentPreferences = getWeekOfDay(project, stPreferences);
        String[] prSkills = project.getProjectskills().toUpperCase().split(",");
        for (String skill : prSkills) {
            projSkillList.add(skill.trim());
        }
        Set<StudentPreferences> finalGroupStudentsWithSameDay = new LinkedHashSet<>();
        Map<String, Set<StudentPreferences>> stringSetMap = new ConcurrentHashMap<>();
        int maxReq = project.getMax();
        for (StudentPreferences st: studentPreferences) {
            String[] stSkills = st.getSkills().toUpperCase().split(",");
            for (String skill: stSkills) {
                for (String prSkill: projSkillList) {
                    if (skill.trim().equals(prSkill.trim())) {
                        projSkillList.remove(prSkill);
                        finalGroupStudentsWithSameDay.add(st);
                        for (WeekDay weekDay : st.getWeekDay()) {
                            stringSetMap.put(weekDay.dayOfWeek + weekDay.getTimeOfDay().trim().toUpperCase(), finalGroupStudentsWithSameDay);
                        }
                        st.setIgnore(true);
                        if (projSkillList.size() < 1) break;
                    }
                }
            }
        }
        if (projSkillList.size() > 0)
            return new LinkedHashSet<>();

        int counter = 0;
        stringSetMap.forEach((k,v) -> {
            if (v.size() < project.getMin()) {
                stringSetMap.remove(k);
            }
        });
        Set<StudentPreferences> finalGroupStudents = new LinkedHashSet<>();
        if (stringSetMap.size() > 0) {
            stringSetMap.forEach((k,v) -> {
                v.forEach(s -> finalGroupStudents.add(s));
            });
        }
        return finalGroupStudents;
    }

    private static List<StudentPreferences> getWeekOfDay(Project project, List<StudentPreferences> st) {
        Map<String, Set<StudentPreferences>> weekDayPreferencesMap =
                new HashMap<>();

        for (StudentPreferences std: st) {
            WeekDay[] wk = std.getWeekDay();
            for (WeekDay w : wk) {
                Set<StudentPreferences> setStudents =
                        weekDayPreferencesMap.get(w.getTimeOfDay().trim().toUpperCase() + w.getDayOfWeek());
                if( setStudents == null) {
                    Set<StudentPreferences> s = new HashSet<>();
                    s.add(std);
                    weekDayPreferencesMap.put(w.getTimeOfDay().trim().toUpperCase()
                            + w.getDayOfWeek(), s);
                } else {
                    setStudents.add(std);
                    weekDayPreferencesMap.put(w.getTimeOfDay().trim().toUpperCase()
                            + w.getDayOfWeek(), setStudents);
                }
            }
        }
        List<StudentPreferences> studentPreferences = new ArrayList<>();
        weekDayPreferencesMap.forEach( (k, v) -> {
            if(v.size() >= project.getMin()) {
                for (StudentPreferences ss : v) {
                    studentPreferences.add(ss);
                }
            }
        });
        return studentPreferences;
    }

}
