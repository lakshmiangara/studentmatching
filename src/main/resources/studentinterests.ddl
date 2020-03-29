create table studentmatching.studentinterests
(
    projectid       int,
    studentid       text,
    studentSkills   text,
    firtName        text,
    lastName        text,
    dayOfWeek       text,
    timeOfDay       text,
    mode            text,
    createDate      timestamp,
    PRIMARY KEY     (projectid, studentid, studentSkills)
)