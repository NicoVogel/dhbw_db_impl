package dhbw.db.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dhbw.db.io.DBIO;
import dhbw.db.model.Album;

@RestController
@RequestMapping("/")
public class DBRestController {

	private static final char VALUE_SEPEARATOR = ',';

	@Autowired
	private DBIO io;

	@GetMapping("/q1")
	public Album getAllAlbumOfArtist(@RequestParam String artistname) {
		return null;
	}

}