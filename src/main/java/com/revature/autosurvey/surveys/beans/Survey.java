package com.revature.autosurvey.surveys.beans;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;


@Data
@Table("survey")
public class Survey {
	@PrimaryKeyColumn(name = "uuid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private UUID uuid;
	@Column
	private LocalDateTime createdOn;
	@PrimaryKeyColumn(name="title", ordinal=1, type=PrimaryKeyType.CLUSTERED)
	private String title;
	@Column
	private String description;
	@Column
	private String confirmation;
	@Column
	private String version;
	@Column
	@JsonIgnore
	@CassandraType(type=Name.LIST, typeArguments = {Name.TEXT})
	private List<String> mappedQuestions;
	@Transient
	private List<Question> questions;

	public Survey() {
		super();
		this.uuid = Uuids.timeBased();
		this.createdOn = LocalDateTime.now();
		this.title = "";
		this.description = "";
		this.confirmation = "";
		this.version = "";
		this.questions = new ArrayList<>();
	}
}
