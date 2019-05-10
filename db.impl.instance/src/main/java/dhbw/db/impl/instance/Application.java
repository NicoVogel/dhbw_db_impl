package dhbw.db.impl.instance;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dhbw.db.impl.instance.controller.error.ApiError;
import dhbw.db.impl.instance.controller.error.ApiNotFoundError;
import dhbw.db.impl.instance.controller.error.ApiParameterError;
import dhbw.db.impl.instance.controller.error.DataNotFoundException;
import dhbw.db.impl.instance.controller.error.ParameterMissmatchException;
import dhbw.db.impl.instance.io.CsvManager;
import dhbw.db.impl.instance.io.impl.CsvManagerImpl;
import dhbw.db.impl.instance.manager.FileManager;
import dhbw.db.impl.instance.manager.impl.FileManagerImpl;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	private FileManager fm;

	@PostConstruct
	public void init() {
		fm.reloadData();
	}

	@Value("${app.artistfile}")
	private String artistFile;
	@Value("${app.albumfile}")
	private String albumFile;
	@Value("${app.retry}")
	private int retry;

	@Bean
	public FileManager getFileManager(@Value("${app.artistfile}") String artistFile,
			@Value("${app.albumfile}") String albumFile, @Value("${app.retry}") int retry) {
		return new FileManagerImpl(artistFile, albumFile, getCsvManager(retry));
	}

	@Bean
	public CsvManager getCsvManager(@Value("${app.retry}") int retry) {
		return new CsvManagerImpl(retry);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(DataNotFoundException ex) {
		ApiError error = new ApiError(ex.getMessage(),
				new ApiNotFoundError(ex.getMessage(), ex.getType().getName(), ex.getId()));
		log.debug(error.toString());
		return new ResponseEntity<ApiError>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ParameterMissmatchException.class)
	public ResponseEntity<ApiError> handleParameterError(ParameterMissmatchException ex) {
		ApiError error = new ApiError(ex.getMessage(), new ApiParameterError(ex.getMessage(), ex.getType(),
				ex.getParameterName(), ex.getInput(), ex.getExpectedFormat()));
		log.debug(error.toString());
		return new ResponseEntity<ApiError>(error, HttpStatus.NOT_FOUND);
	}

}
