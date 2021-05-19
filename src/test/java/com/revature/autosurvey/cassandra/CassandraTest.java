package com.revature.autosurvey.cassandra;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

public class CassandraTest {

		@Test
		public void getSystemSchema() {
			String result = "";
			
			DriverConfigLoader loader = DriverConfigLoader.fromClasspath("cassandra.conf");
			try (CqlSession session = CqlSession.builder()
					.withConfigLoader(loader)
					.build()) {
				ResultSet rs = session.execute("select * from system_schema.keyspaces");
				Row row = rs.one();
				result = row.getString("keyspace_name");
			}
			
			Assert.isTrue(result.equals("system_schema"), "Result should be schema, but instead is " + result);
		}
}
