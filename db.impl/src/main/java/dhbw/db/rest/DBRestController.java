package dhbw.db.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/")
public class DBRestController {

	@Autowired
	private DBIO io;

	@GetMapping("/q1")
	public ArtistTO getAllAlbumOfArtist(@RequestParam String name) {

		long start = System.currentTimeMillis();
		// find and artist with the given name
		int nameHash = name.hashCode();
		Artist match = io.findFirst(io.getArtistFilePath(), Artist.class, artist -> artist.getNameHash() == nameHash);

		if (match == null) {
			return new ArtistTO();
		}

		// get all album ids which he produced, by checkout the AlbumHasArtist file and
		// only write the album ids into the Set (the set is a hashset)
		Set<Integer> albumIds = io.findCustom(io.getAlbumHasArtistFilePath(), AlbumHasArtist.class,
				x -> x.getArtistId() == match.getId(),
				stream -> stream.map(x -> x.getAlbumId()).collect(Collectors.toSet()));

		// get album values for the ids which are in the set. if remove returns true,
		// the album is selected
		List<Album> album = io.findAll(io.getAlbumFilePath(), Album.class, x -> albumIds.remove(x.getId()));

		// convert the album into the returned datatype
		List<AlbumTO> albumsto = album.stream().map(DBConverter::convert).collect(Collectors.toList());
		ArtistTO result = DBConverter.convert(match, albumsto);
		log.debug(String.format("q1 time: %d", System.currentTimeMillis() - start));
		return result;
	}

	@GetMapping("/q2")
	public List<ArtistTO> getNewestAlbumOfEachArtist() {

		long start = System.currentTimeMillis();
		List<ArtistTO> result = new ArrayList<>();

		// get all artists
		List<Artist> artists = io.findAll(io.getArtistFilePath(), Artist.class, artist -> true);
		if (artists.isEmpty()) {
			return result;
		}
		Map<Integer, List<AlbumHasArtist>> connectionMatch = io.findCustom(io.getAlbumHasArtistFilePath(),
				AlbumHasArtist.class, x -> true, x -> x.collect(Collectors.groupingBy(y -> y.getArtistId())));

		Map<Integer, Album> albums = io.findCustom(io.getAlbumFilePath(), Album.class, x -> true,
				x -> x.collect(Collectors.toMap(y -> y.getId(), y -> y)));

		for (Artist artist : artists) {
			List<AlbumHasArtist> connections = connectionMatch.get(artist.getId());
			if (connections == null) {
				continue;
			}
			List<AlbumTO> artistAlbum = new ArrayList<>();
			for (AlbumHasArtist albumHasArtist : connections) {
				Album album = albums.get(albumHasArtist.getAlbumId());
				if (album != null) {
					artistAlbum.add(DBConverter.convert(album));
				}
			}
			result.add(DBConverter.convert(artist, artistAlbum));
		}

		log.debug(String.format("q2 time: %d", System.currentTimeMillis() - start));
		return result;
	}

}