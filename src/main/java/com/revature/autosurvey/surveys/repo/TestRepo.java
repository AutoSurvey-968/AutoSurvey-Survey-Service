package com.revature.autosurvey.surveys.repo;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.autosurvey.surveys.beans.Test;
@Repository
public interface TestRepo extends ReactiveCassandraRepository<Test, Long>{

}
