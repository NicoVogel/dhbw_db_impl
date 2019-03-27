package dhbw.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import dhbw.db.io.DBIO;

@SpringBootApplication
public class DBApplication {

	public static void main(String[] args) {
		SpringApplication.run(DBApplication.class, args);
	}
	
	@Bean
	public DBIO getDBIO() {
		return new DBIO();
	}
}
