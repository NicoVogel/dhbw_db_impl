package dhbw.db;

import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import dhbw.db.io.DBIO;
import dhbw.db.model.AlbumTO;
import dhbw.db.model.ArtistTO;

@SpringBootApplication
public class DBApplication {

	public static void main(String[] args) {
		SpringApplication.run(DBApplication.class, args);
	}
	
	@PostConstruct
	public void setup() {
		DBIO io = getDBIO();
		String album = getPathOrEmpty(DBIO.ALBUM);
		String artist = getPathOrEmpty(DBIO.ARTIST);
		
		if(io.needsInizialisation()) {
			List<ArtistTO> artists = io.loadCsvObjectList(ArtistTO.class, artist);
			List<AlbumTO> albums = io.loadCsvObjectList(AlbumTO.class, album);
			
			
		}
	}
	
	@Bean
	public DBIO getDBIO() {
		return new DBIO();
	}
	
	private String getPathOrEmpty(String filename) {
		URL u = getClass().getResource(filename);
		if(u == null) {
			return "";
		}
		return u.getFile();
	}
	
}
