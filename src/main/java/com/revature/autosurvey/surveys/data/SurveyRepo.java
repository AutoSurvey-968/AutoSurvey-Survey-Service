package com.revature.autosurvey.surveys.data;

import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.autosurvey.surveys.beans.Survey;

import reactor.core.publisher.Mono;
@Repository
public interface SurveyRepo extends ReactiveCassandraRepository<Survey, UUID> {
	@AllowFiltering
	Mono<Survey> getByUuid(UUID uuid);
}
