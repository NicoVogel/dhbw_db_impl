package dhbw.db.impl.instance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dhbw.db.impl.instance.controller.error.DataNotFoundException;
import dhbw.db.impl.instance.manager.ArtistHandler;
import dhbw.db.impl.instance.manager.FileManager;
import dhbw.db.impl.instance.model.Album;
import dhbw.db.impl.instance.model.Artist;
import dhbw.db.impl.instance.model.Tupel;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
@Slf4j
public class ApiController {

	@Autowired
	private FileManager fm;

	private ArtistHandler artist() {
		return fm.editArtist();
	}

	@GetMapping("/q1")
	public List<Album> getAlbums(@RequestParam int id) {
		long start = System.currentTimeMillis();
		Artist artist = artist().read(id);
		if (artist == null) {
			throw new DataNotFoundException(
					String.format("cannot read albums from an artist, because the artist id %d does not exist", id),
					Artist.class, id);
		}
		List<Album> result = artist().getAlbums(artist);
		log.info("done with q1, time: {}", System.currentTimeMillis() - start);
		return result;
	}

	@GetMapping("/q2")
	public List<Tupel<Artist, Integer>> getLastReleaseDateOfEachArtist() {
		long start = System.currentTimeMillis();
		List<Tupel<Artist, Integer>> result = artist().getLastReleaseDateOfEachArtist();
		log.info("done with q2, time: {}", System.currentTimeMillis() - start);
		return result;
	}

	@GetMapping("/q3")
	public int getFoundingYear(@RequestParam int id) {
		long start = System.currentTimeMillis();
		Artist artist = artist().read(id);
		if (artist == null) {
			throw new DataNotFoundException(
					String.format("cannot read artist founding year, because the artist id %d does not exist", id),
					Artist.class, id);
		}
		int year = artist.getYear();
		log.info("done with q3, time: {}", System.currentTimeMillis() - start);
		return year;
	}

	@GetMapping("/q4")
	public List<Artist> getArtistsWhichHaveNoReleases() {
		long start = System.currentTimeMillis();
		List<Artist> result = artist().getArtistsWhichHaveNoReleases();
		log.info("done with q4, time: {}", System.currentTimeMillis() - start);
		return result;
	}

}
