package com.usf.studentmatching.utils;

import com.usf.studentmatching.controller.WeekDay;
import com.usf.studentmatching.model.Project;
import com.usf.studentmatching.model.StudentInterest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.abs;

/**
 * Purpose: This class takes a given <b>projectid</b> as input and list of all <b>student preferences</b> as the other
 * input
 * a.) First, the logic iterates over the studentpreference list matching the student preference projectid to a
 *     given projectid. All the matching preferences will be grouped together in a collection set
 * b.) On this collection set the application groups by day of the week and time of the day.The group by uses map
 *     with day of the week and time of the day as key and students with those preferences as values.
 * c.) Application iterates over the map to check the values(set of student preferences) are greater than project
 *     minimum number of students. If the set of students are less than project minimum number, the application ignore
 *     these students.
 * d.) From the above step c. the application has students grouped together based on day of the week and time of the day.
 *     Now with in this group, the application applies the logic to find compatable students.
 * e.) In the compatable logic, the application ensures that compatable students together have all the necessary project
 *     skills to form a group.
 * Note: Minimum number of students required to form a group is taken into consideration. But the maximum allowable
 * students for a given project is not taken into consideration.
 *
 */

public class MatchingAlgorithm {

    private static int SKILL_POINTS = 25;

    /**
     *  Purpose: This method iterates over studentpreference and tries to match student preference for a given projectid.
     *           All the students whose preferences match for the given projectid will be grouped into a List.
     *           On this list for this given projetid, logic will be applied to find compatible teams.
     * @param studentPreferenceMap using is as referenceMap which hold projectId and Compatible StudentPreference as a Set.
     * @param stInterest List of all students in studentInterest table
     * @param project - Given project from projects table (Single project).
     */
    public static void matchPreferences(Map<Integer, Set<StudentPreferences>> studentPreferenceMap,
                                           List<StudentInterest> stInterest, Project project) {

        List<StudentPreferences> studentPreferencesList = new ArrayList<>();
        //build list of students whose preferences match to the project id.
        //If a student selects a project id, he will be added to this list.
        stInterest.forEach(
                st -> {
                    if (st.getStudentInterestPrimaryKey().getProjectId() != project.getProjectid())
                        return;
                    studentPreferencesList.add(getStudentPoints(project, st));
                }
        );
        //sorting the list
        Collections.sort(studentPreferencesList);
        //compatable teams formation
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

    /**
     * Purpose: First calls getWeekOfDayMatchingStudents() to get a group by list. On this list applies
     *          logic to find compatible teams.
     * @param project which hold data from static database table, single project.
     * @param stPreferences list which is nothing but of list of
     *                      students whose preference matched with the given project.
     *                      This parameter will hold all students who selected the choice for a specific
     *                      project. This project is in the first parameter.
     * @return studentpreference collection set who can form compatible teams.
     */
    private static Set<StudentPreferences>  formCompatableTeams(Project project,
                                                                List<StudentPreferences> stPreferences) {

        return getProjectsWithCompatibleSkills(project, getWeekOfDayMatchingStudents(project, stPreferences));
    }


    /**
     * @param project which hold data from static database table. Singe project.
     * @param st list which is nothing but of list of
     *           students whose preference matched with the given project.
     * @return Map collection set, where key is the DayofWeek and TimeOfDay with value as set of studentpreferences.
     * Example:
     *       Map("MONDAYMORNING", Set(StudentPreference(studentid="U123",firstName="John",lastName="Aston"...),
     *                               StudentPreference(studentid="U124",firstName="John",lastName="McNeal"...))
     *       Map("FRIDAYMORNING", Set(StudentPreference(studentid="U345",firstName="Jason",lastName="Aston"...),
     *                             StudentPreference(studentid="U674",firstName="Danny",lastName="McNeal"...))
     */
    private static Map<String, Set<StudentPreferences>> getWeekOfDayMatchingStudents(Project project,
                                                                         List<StudentPreferences> st) {
        Map<String, Set<StudentPreferences>> weekDayPreferencesMap =
                new ConcurrentHashMap<>();

        //Grouping students by WeekDay and time of day as map.
        for (StudentPreferences std: st) {
            WeekDay[] wk = std.getWeekDay();
            for (WeekDay w : wk) {
                Set<StudentPreferences> setStudents =
                        weekDayPreferencesMap.get(w.getDayOfWeek() + w.getTimeOfDay().trim().toUpperCase());
                if( setStudents == null) {
                    Set<StudentPreferences> s = new HashSet<>();
                    s.add(std);
                    weekDayPreferencesMap.put(w.getDayOfWeek() + w.getTimeOfDay().trim().toUpperCase(), s);
                } else {
                    setStudents.add(std);
                }
            }
        }
        List<StudentPreferences> studentPreferences = new ArrayList<>();
        //check for the given weekday and timing preferences if number of students are greater
        //than the project minimum size. If not, then we can discard these students as the requirement
        //does not match.
        weekDayPreferencesMap.forEach( (k, v) -> {
            if(v.size() < project.getMin()) {
                weekDayPreferencesMap.remove(k);
            }
        });
        return weekDayPreferencesMap;
    }

    /**
     * Purpose: With in same DayOfWeek and time of day, the logic is applied to see if a group can be formed which will
     *         consist of all the skills required for the project to be executed.
     * @param project which hold data from static database table
     * @param studentWithSameWeek Map of students whichin same DayofWeek and Day of time.
     * @return collection set of students who can form a compatible team.
     *
     * Example: Project required Skill is : Java, Scala, Python, NodeJS. From the example below only <b>FridayMorning</b> group collection
     *           set students must be selected as there team has all the skills required for the given project id requirement.
     *           This team is called "Compatible team".
     *       MONDAYEVENING GROUP of STUDENTS are with Skills like below
     *       Map("MONDAYMORNING", Set(StudentPreference(studentid="U123",skills="Java", firstName="John",lastName="Aston"...),
     *                               StudentPreference(studentid="U124",skills="Java",firstName="John",lastName="McNeal"...),
     *                               StudentPreference(studentid="U125",skills="Python",firstName="Aston",lastName="McNeal"...),
     *                               StudentPreference(studentid="U126",skills="NodeJS",firstName="Chris",lastName="McNeal"...))
     *       FRIDAYMORNING GROUP of STUDENTS are with Skills like below
     *       Map("FRIDAYMORNING", Set(StudentPreference(studentid="U345",skills="Java", firstName="Jason",lastName="Aston"...),
     *                               StudentPreference(studentid="U676",skills="Python",firstName="Danny",lastName="McNeal"...),
     *                               StudentPreference(studentid="U677",skills="Scala",firstName="Richie",lastName="McNeal"...),
     *                               StudentPreference(studentid="U678",skills="NodeJS",firstName="Tom",lastName="McNeal"...))
     *
     */
    private static Set<StudentPreferences> getProjectsWithCompatibleSkills(Project project,
                                                                           Map<String, Set<StudentPreferences>> studentWithSameWeek) {

        List<String> projSkillList = new ArrayList<>();
        String[] prSkills = skillTokens(project.getProjectskills());
        for (String skill : prSkills) {
            projSkillList.add(skill.trim());
        }

        Set<StudentPreferences> studentsWithCompatibleSkills = new LinkedHashSet<>();
        Map<String, Set<StudentPreferences>> stringSetMap = new HashMap<>();
        Set<StudentPreferences> finalGroup = new LinkedHashSet<>();
        //Invoking method on a given DayOfweek and time of day collection set students to see if they can
        //form compatible team.
        studentWithSameWeek.forEach((k, v) -> {
            List<String> copyProjSkillList = new CopyOnWriteArrayList<>(projSkillList);
            findMatch(copyProjSkillList, v, project).forEach(st -> finalGroup.add(st));

        });
        return finalGroup;
    }

    private static String[] skillTokens(String skills) {
        return skills.toUpperCase().split(",");
    }

/*    private static void findCompatibleSkillTeams(Set<StudentPreferences> studentsWithCompatibleSkills,
                                          Map<String, Set<StudentPreferences>> stringSetMap,
                                          List<String> projSkillList,
                                          Set<StudentPreferences> studentWithSameWeek) {

        Set<StudentPreferences> setStudents = new HashSet<>();
        for(StudentPreferences st: studentWithSameWeek) {
            for( WeekDay weekDay: st.getWeekDay()) {
                setStudents.add(st);
                stringSetMap.put(weekDay.dayOfWeek + weekDay.getTimeOfDay().trim().toUpperCase(),
                        setStudents);
            }
        }

    }*/

    /**
     * Purpose: For given studentList call findSubSet() method finds the compatible teams.
     * @param projSkillList list of skills required for the given project id.
     * @param setSt list of students for a given specific DayOfWeek and Time of Day
     * @param project Single project for which match logic is happening
     * @return Set of students who are compatible to execute the project.
     */
    private static Set<StudentPreferences> findMatch(List<String> projSkillList,
                                                     Set<StudentPreferences> setSt, Project project) {

        Set<StudentPreferences> finalStudentPreferences = new LinkedHashSet<>();
        //List of students who gets returned are added to finalStudentPreference LinkedList.
        findSubSet(projSkillList, setSt, project).forEach(st -> finalStudentPreferences.add(st));
        return finalStudentPreferences;
    }

    private static boolean skillExists(String skill, List<String> projList) {
        boolean matchFound = false;
        for (String prjSkill: projList) {
            if (skill.equals(prjSkill)) {
                matchFound = true;
                break;
            }
        }
        return matchFound;
    }

    /**
     * Purpose: To find compatible teams.
     *          The projectList is stored as a CopyOnWriteList so that whenever the student matches the project skill, the particular skill
     *          is deleted from the list. Application will make sure the project list if empty before comforming a compatible team.
     *          If a single student has all the skills required, very first iteration will remove the skills from the
     *          list. If the number of students required to execute the project is less than project min size, then we
     *          re-populate the skill list so others with matching skills can be added.
     * @param projSkillList - List of skills required to execute the project.
     * @param setSt - Set of students who are all with same DayOfWeek and Day of Time.
     * @param project - Singe project id.
     * @return - Set of students who can form the compatible team to execute the project.
     */
    private static Set<StudentPreferences> findSubSet(List<String> projSkillList,
                                                      Set<StudentPreferences> setSt, Project project) {
        Set<StudentPreferences> finalStudentPreferences = new LinkedHashSet<>();
        Set<StudentPreferences> standByPreferences = new HashSet<>();
        List<String> extraList = new ArrayList<>(projSkillList);
        boolean allMatch = false;
        for (StudentPreferences st : setSt) {
            String skills[] = skillTokens(st.getSkills());
            for (String skill : skills) {
                if (skillExists(skill, projSkillList)) {
                    finalStudentPreferences.add(st);
                    st.setIgnore(true);
                    projSkillList.remove(skill);
                    //If skills got empty and finalstudentpreferencelist is less than min req to forrm a group,
                    //we copy back original skills so remaining students can be compared.
                    if (projSkillList.size() < 1 && finalStudentPreferences.size() < project.getMin()) {
                        allMatch = true;
                        projSkillList.add(project.getProjectskills());
                    }
                } else if(skillExists(skill, extraList)) {
                    standByPreferences.add(st);
                }
            }
        }
        if (!allMatch && projSkillList.size() > 0)
            return new HashSet<>();
        else {
            standByPreferences.forEach(e -> finalStudentPreferences.add(e));
            return finalStudentPreferences;
        }
    }
}
