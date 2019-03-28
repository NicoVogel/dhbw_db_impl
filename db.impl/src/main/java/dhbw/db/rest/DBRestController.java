package dhbw.db.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

@RestController
@RequestMapping("/")
public class DBRestController {

	@Autowired
	private DBIO io;

	@GetMapping("/q1")
	public ArtistTO getAllAlbumOfArtist(@RequestParam String name) {

		// find and artist with the given name
		Artist match = io.findFirst(io.getArtistFilePath(), Artist.class, artist -> artist.getName().equals(name));

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
		return DBConverter.convert(match, albumsto);
	}

	@GetMapping("/q2")
	public List<ArtistTO> getNewestAlbumOfEachArtist() {

		List<ArtistTO> result = new ArrayList<>();

		// get all artists
		List<Artist> artists = io.findAll(io.getArtistFilePath(), Artist.class, artist -> true);
		if (artists.isEmpty()) {
			return result;
		}

		// separate the artist ids to use the contains method with speed of 1,
		// because the set is a hashset
		Set<Integer> artistIds = artists.stream().map(x -> x.getId()).collect(Collectors.toSet());

		// read only AlbumHasArtist which where the artist is in the artistIds set
		// (should be all)
		List<AlbumHasArtist> connectionMatch = io.findAll(io.getAlbumHasArtistFilePath(), AlbumHasArtist.class,
				x -> artistIds.contains(x.getArtistId()));
		if (connectionMatch.isEmpty()) {
			return result;
		}

		// albumId, list<artistID>
		Map<Integer, List<Integer>> albumToArtistMap = new HashMap<>();
		connectionMatch.stream().forEach(
				x -> albumToArtistMap.computeIfAbsent(x.getAlbumId(), v -> new ArrayList<>()).add(x.getArtistId()));

		// will contain the albums for each artistid
		Map<Integer, List<Album>> mapAlbum = new HashMap<>();

		// read all albums from file and save them in the mapAlbum if they currently
		// read album has a value in the albumToArtistMap (adds it to each value of the
		// albumToArtistMap).
		io.findAll(io.getAlbumFilePath(), Album.class, x -> {
			List<Integer> artistIdOfAlbum = albumToArtistMap.get(x.getId());
			if (artistIdOfAlbum != null) {
				artistIdOfAlbum.stream()
						.forEach(artistId -> mapAlbum.computeIfAbsent(artistId, k -> new ArrayList<>()).add(x));
			}
			// because the output is not used, no value should be returned
			return false;
		});

		for (Entry<Integer, List<Album>> entity : mapAlbum.entrySet()) {

			Artist match = artists.stream().filter(x -> x.getId() == entity.getKey()).findFirst().get();

			// sort the values and get the biggest
			Album album = entity.getValue().stream().sorted((x, y) -> y.getYear() - x.getYear()).findFirst().get();
			List<AlbumTO> albumsto = new ArrayList<>();
			albumsto.add(DBConverter.convert(album));
			// convert to end type and add
			result.add(DBConverter.convert(match, albumsto));
		}

		return result;
	}

}