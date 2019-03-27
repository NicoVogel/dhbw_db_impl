package dhbw.db;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import dhbw.db.io.DBIO;
import dhbw.db.model.Album;
import dhbw.db.model.AlbumHasArtist;
import dhbw.db.model.AlbumTemp;
import dhbw.db.model.Artist;
import dhbw.db.model.DBConverter;

@SpringBootApplication
public class DBApplication {

	public static void main(String[] args) {
		SpringApplication.run(DBApplication.class, args);
	}

	@Value("${dhbw.db.album}")
	private String album;
	@Value("${dhbw.db.artist}")
	private String artist;

	@PostConstruct
	public void setup() {
		if (this.album.isEmpty() && this.artist.isEmpty()) {
			return;
		} else if (this.album.isEmpty()) {
			// TODO artist is missing
			return;
		} else if (this.artist.isEmpty()) {
			// TODO album is missing
			return;
		}

		DBIO io = getDBIO();

		List<AlbumTemp> albumTOs = io.loadCsvAlbum(album);
		List<Artist> artists = io.loadCsvArtist(artist);

		albumTOs.stream().forEach(x -> x.setId(io.getNextAlbumID()));
		artists.stream().forEach(x -> x.setId(io.getNextArtistID()));
		List<AlbumHasArtist> connectoin = DBConverter.createConnection(artists, albumTOs);

		List<Album> albums = DBConverter.convertToDBType(albumTOs);

		io.overrideFile(io.getAlbumFilePath(), albums);
		io.overrideFile(io.getArtistFilePath(), artists);
		io.overrideFile(io.getAlbumHasArtistFilePath(), connectoin);

	}

	@Bean
	public DBIO getDBIO() {
		return new DBIO();
	}

}
