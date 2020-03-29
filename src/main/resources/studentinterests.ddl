create table studentmatching.studentinterests
(
    projectid     int,
    studentid     text,
    createdate    timestamp,
    dayofweek     text,
    firtname      text,
    lastname      text,
    mode          text,
    studentpref   int,
    studentskills text,
    primary key (projectid, studentid)
)