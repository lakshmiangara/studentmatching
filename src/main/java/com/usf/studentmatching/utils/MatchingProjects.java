package com.usf.studentmatching.utils;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class MatchingProjects {

    private Map<Integer, List<StudentPreferences>> projectMatchingOrder;

}
