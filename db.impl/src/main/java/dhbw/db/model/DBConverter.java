package dhbw.db.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import dhbw.db.rest.AlbumTO;
import dhbw.db.rest.ArtistTO;

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
		List<AlbumHasArtist> connection = new ArrayList<>();

		for (AlbumTemp albumTO : albums) {
			for (String name : albumTO.getArtistNames()) {
				Optional<Artist> optArtist = artists.stream().filter(x -> x.getName().equals(name)).findFirst();
				if (optArtist.isPresent() == false) {
					// TODO no parent
					continue;
				}
				connection.add(new AlbumHasArtist(optArtist.get().getId(), albumTO.getId()));
			}
		}

		return connection;
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

}
