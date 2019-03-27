package dhbw.db;

import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import dhbw.db.io.DBIO;
import dhbw.db.model.AlbumTO;
import dhbw.db.model.Artist;

@SpringBootApplication
public class DBApplication {

	public static void main(String[] args) {
		SpringApplication.run(DBApplication.class, args);
	}

	@PostConstruct
	public void setup() {
		DBIO io = getDBIO();
		String album = "C:\\temp\\db\\init\\album.csv";
		String artist = "C:\\temp\\db\\init\\artist.csv";

		if (io.needsInizialisation()) {
			List<Artist> artists = io.loadCsvArtist(artist);
			List<AlbumTO> albums = io.loadCsvAlbum(album);

			System.out.println(artist);
			System.out.println(album);
		}
	}

	@Bean
	public DBIO getDBIO() {
		return new DBIO();
	}

	private String getPathOrEmpty(String filename) {
		URL u = getClass().getResource(filename);
		if (u == null) {
			return "";
		}
		return u.getFile();
	}

}
