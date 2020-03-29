package com.usf.studentmatching.repository;

import com.usf.studentmatching.model.StudentInterest;
import com.usf.studentmatching.model.StudentInterestPrimaryKey;
import org.springframework.data.repository.CrudRepository;

public interface StudentInterestRepository extends CrudRepository<StudentInterest, StudentInterestPrimaryKey> {
}
