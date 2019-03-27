package dhbw.db.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dhbw.db.io.DBIO;
import dhbw.db.model.Artist;

@RestController
@RequestMapping("/")
public class DBRestController {

	@Autowired
	private DBIO io;

	@GetMapping("/q1")
	public Artist getAllAlbumOfArtist(@RequestParam String artistname) {

		Artist match = io.streamReadFile(io.getArtistFilePath(), Artist.class,
				(artist) -> artist.getName().equals(artistname));

		return match;
	}

}