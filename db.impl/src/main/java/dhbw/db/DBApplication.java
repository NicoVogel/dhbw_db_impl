package dhbw.db;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import dhbw.db.io.DBIO;
import dhbw.db.model.Album;
import dhbw.db.model.AlbumHasArtist;
import dhbw.db.model.AlbumTemp;
import dhbw.db.model.Artist;
import dhbw.db.model.DBConverter;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class DBApplication {

	public static void main(String[] args) {
		SpringApplication.run(DBApplication.class, args);
	}

	@Value("${dhbw.db.album}")
	private String album;
	@Value("${dhbw.db.artist}")
	private String artist;
	@Autowired
	private DBIO io;

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

		log.info("start loadfing data files, which will override the current database content");

		List<AlbumTemp> albumTOs = io.loadCsvAlbum(album);
		List<Artist> artists = io.loadCsvArtist(artist);

		log.info("done");
		log.info("start generate ids for the new contents");

		albumTOs.stream().forEach(x -> x.setId(io.getNextAlbumID()));
		artists.stream().forEach(x -> x.setId(io.getNextArtistID()));

		log.info("done");
		log.info("start generate thired table AlbumHasArtist");

		List<AlbumHasArtist> connectoin = DBConverter.createConnection(artists, albumTOs);

		log.info("done");
		log.info("start converstion of the album data to database format");

		List<Album> albums = DBConverter.convertToDBType(albumTOs);

		log.info("done");
		log.info("start saving the database data to the intended files");

		log.info(String.format("artist: %d, album: %d, connection: %d", artists.size(), albums.size(),
				connectoin.size()));

		io.overrideFile(io.getAlbumFilePath(), albums);
		io.overrideFile(io.getArtistFilePath(), artists);
		io.overrideFile(io.getAlbumHasArtistFilePath(), connectoin);

		log.info("done");
	}

}
