package dhbw.db.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dhbw.db.rest.AlbumTO;
import dhbw.db.rest.ArtistTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBConverter {

	private DBConverter() {

	}

	public static ArtistTO convert(Artist a, List<AlbumTO> list) {
		ArtistTO to = new ArtistTO();
		to.setCountry(a.getCountry());
		to.setName(a.getName());
		to.setYear(a.getYear());
		to.setAlbums(list);
		return to;
	}

	public static AlbumTO convert(Album a) {
		AlbumTO to = new AlbumTO();
		to.setName(a.getName());
		to.setYear(a.getYear());
		return to;
	}

	public static List<AlbumHasArtist> createConnection(List<Artist> artists, List<AlbumTemp> albums) {
		Map<Integer, AlbumHasArtist> connection = new HashMap<>();

		Map<Integer, List<Artist>> nameHashMap = artists.stream()
				.collect(Collectors.groupingBy(x -> x.getName().hashCode()));

		Set<AlbumTemp> invalidInput = new HashSet<>();

		for (AlbumTemp albumTO : albums) {
			for (String name : albumTO.getArtistNames()) {
				int hash = name.trim().hashCode();
				List<Artist> matches = nameHashMap.get(hash);
				if (matches == null) {
					/*
					 * log.debug(String.format("Album cannot be added (%d): no artist found",
					 * albumTO.getId()));
					 */
					log.debug(String.format("NOT FOUND: albumId: %d, artistNameHash: %d\t\t,artistName: '%s'",
							albumTO.getId(), hash, name));
					invalidInput.add(albumTO);
					break;
				} else if (matches.size() > 1) {
					/*
					 * log.debug(String.format(
					 * "Album cannot be added (%d): multiple artists have the same name, cannnot asigned to a specific artist"
					 * , albumTO.getId()));
					 */
					log.debug(String.format("AMBIGUOUS: albumId: %d, artistNameHash: %d\t\t,artistName: '%s'",
							albumTO.getId(), hash, name));
					invalidInput.add(albumTO);
					break;
				}
				AlbumHasArtist con = new AlbumHasArtist(matches.get(0).getId(), albumTO.getId());
				int conHash = String.format("%d-%d", con.getAlbumId(), con.getArtistId()).hashCode();
				connection.computeIfAbsent(conHash, k -> con);
			}
		}

		log.info(String.format("%d albums where not added", invalidInput.size()));
		invalidInput.stream().forEach(x -> albums.remove(x));

		return new ArrayList<>(connection.values());
	}

	public static List<Album> convertToDBType(List<AlbumTemp> tos) {
		return tos.stream().map(x -> new Album(x.getId(), x.getName(), x.getYear())).collect(Collectors.toList());
	}

	public static Artist convertToArtist(String[] data, int nameIndex, int yearIndex, int countryIndex) {

		if (data == null) {
			// TODO error
		} else if (data.length < 3) {
			// TODO error
		}

		String name = data[nameIndex];
		int year = 0;
		String country = data[countryIndex];
		try {
			year = Integer.parseInt(data[yearIndex]);
		} catch (NumberFormatException e) {
			// TODO error
		}

		return new Artist(0, name, year, country);
	}

	public static String[] trimArray(String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].trim();
		}
		return arr;
	}

	public static AlbumTemp convertToAlbum(String[] data, int nameIndex, int artistIndex, int yearIndex) {

		if (data == null) {
			// TODO error
		} else if (data.length < 3) {
			// TODO error
		}

		String name = data[nameIndex];
		int year = 0;
		String[] artistNames = trimArray(data[artistIndex].split(","));
		try {
			year = Integer.parseInt(data[yearIndex]);
		} catch (NumberFormatException e) {
			// TODO error
		}

		return new AlbumTemp(0, name, artistNames, year);
	}

	public static long hash(String string) {
		long h = 1125899906842597L; // prime
		int len = string.length();

		for (int i = 0; i < len; i++) {
			h = 31 * h + string.charAt(i);
		}
		return h;
	}

}
