package dhbw.db.model;

public class DBConverter {

	private DBConverter() {

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
