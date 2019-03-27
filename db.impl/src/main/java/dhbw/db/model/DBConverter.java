package dhbw.db.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DBConverter {

	private DBConverter() {

	}
	
	public static List<AlbumHasArtist> createConnection(List<Artist> artists, List<AlbumTO> albums){
		List<AlbumHasArtist> connection = new ArrayList<>();
		
		for (AlbumTO albumTO : albums) {
			for (String name : albumTO.getArtistNames()) {
				Optional<Artist> optArtist = artists.stream().filter(x -> x.getName().equals(name)).findFirst();
				if(optArtist.isPresent() == false) {
					// TODO no parent
					continue;
				}
				connection.add(new AlbumHasArtist(optArtist.get().getId(), albumTO.getId()));
			}
		}
		
		return connection;
	}

	public static List<Album> convertToDBType(List<AlbumTO> tos){
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
	public static AlbumTO convertToAlbum(String[] data, int nameIndex, int artistIndex, int yearIndex) {

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

		return new AlbumTO(0, name, artistNames, year);
	}

}
