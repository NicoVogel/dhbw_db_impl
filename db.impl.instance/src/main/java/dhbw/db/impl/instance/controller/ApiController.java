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

@RestController
@RequestMapping("/")
public class ApiController {

	@Autowired
	private FileManager fm;

	private ArtistHandler artist() {
		return fm.editArtist();
	}

	@GetMapping("/q1")
	public List<Album> getAlbums(@RequestParam int id) {
		Artist artist = artist().read(id);
		if (artist == null) {
			throw new DataNotFoundException(
					String.format("cannot read albums from an artist, because the artist id %d does not exist", id),
					Artist.class, id);
		}
		return artist().getAlbums(artist);
	}

	@GetMapping("/q2")
	public List<Tupel<Artist, Integer>> getLastReleaseDateOfEachArtist() {
		return artist().getLastReleaseDateOfEachArtist();
	}

	@GetMapping("/q3")
	public int getFoundingYear(@RequestParam int id) {
		Artist artist = artist().read(id);
		if (artist == null) {
			throw new DataNotFoundException(
					String.format("cannot read artist founding year, because the artist id %d does not exist", id),
					Artist.class, id);
		}
		return artist.getYear();
	}

	@GetMapping("/q4")
	public List<Artist> getArtistsWhichHaveNoReleases() {
		return artist().getArtistsWhichHaveNoReleases();
	}

}
