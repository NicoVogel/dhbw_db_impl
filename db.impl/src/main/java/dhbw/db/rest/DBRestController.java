package dhbw.db.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dhbw.db.io.DBIO;
import dhbw.db.model.Album;
import dhbw.db.model.AlbumHasArtist;
import dhbw.db.model.Artist;
import dhbw.db.model.DBConverter;

@RestController
@RequestMapping("/")
public class DBRestController {

	@Autowired
	private DBIO io;

	@GetMapping("/q1")
	public ArtistTO getAllAlbumOfArtist(@RequestParam String name) {

		Artist match = io.findFirst(io.getArtistFilePath(), Artist.class, artist -> artist.getName().equals(name));

		if (match == null) {
			return new ArtistTO();
		}

		List<AlbumHasArtist> connectionMatch = io.findAll(io.getAlbumHasArtistFilePath(), AlbumHasArtist.class,
				x -> x.getArtistId() == match.getId());

		List<Album> album = io.findAll(io.getAlbumFilePath(), Album.class, x -> {

			Optional<AlbumHasArtist> val = connectionMatch.stream().filter(y -> y.getAlbumId() == x.getId())
					.findFirst();

			if (val.isPresent() == false) {
				return false;
			}
			connectionMatch.remove(val.get());
			return true;
		});

		List<AlbumTO> albumTo = album.stream().map(DBConverter::convert).collect(Collectors.toList());
		return DBConverter.convert(match, albumTo);
	}

}