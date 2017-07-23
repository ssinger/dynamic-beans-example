package ssinger.info.dynamic_beans_example;

import java.sql.Timestamp;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class Repository {

	private JdbcTemplate template;
	
	public Repository(DataSource ds) {
		template = new JdbcTemplate(ds);
	}
	public Date doQuery() {
		return template.queryForObject("select now()", Timestamp.class);
	}
}
