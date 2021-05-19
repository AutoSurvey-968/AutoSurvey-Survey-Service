package com.revature.autosurvey.surveys.data;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.autosurvey.surveys.beans.Survey;
@Repository
public interface SurveyRepo extends ReactiveCassandraRepository<Survey, Long>{

}
