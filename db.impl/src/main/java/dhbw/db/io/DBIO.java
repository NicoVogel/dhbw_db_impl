package dhbw.db.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import dhbw.db.model.AlbumTemp;
import dhbw.db.model.Artist;
import dhbw.db.model.DBConverter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DBIO {

	private static final String ARTIST = "artist.json";
	private static final String ALBUM = "album.json";
	private static final String ALBUM_HAS_ARTIST = "albumhasartist.json";

	@Value("${dhbw.db.data}")
	private String dataFolder;

	private ObjectMapper objectMapper = new ObjectMapper();
	@Setter
	private int artistID = 1;
	@Setter
	private int albumID = 1;

	public String getArtistFilePath() {
		return this.dataFolder + DBIO.ARTIST;
	}

	public String getAlbumFilePath() {
		return this.dataFolder + DBIO.ALBUM;
	}

	public String getAlbumHasArtistFilePath() {
		return this.dataFolder + DBIO.ALBUM_HAS_ARTIST;
	}

	public int getNextArtistID() {
		return artistID++;
	}

	public int getNextAlbumID() {
		return albumID++;
	}

	public List<Artist> loadCsvArtist(String fileName) {
		try {
			List<String[]> csv = loadCsvObjectList(fileName);
			if (csv.size() == 0) {
				// TODO info
				return new ArrayList<>();
			}
			String[] first = csv.get(0);
			csv.remove(0);

			int nameIndex = getIndexByName(first, "name");
			int yearIndex = getIndexByName(first, "year");
			int countryIndex = getIndexByName(first, "country");

			return csv.stream().map(x -> DBConverter.convertToArtist(x, nameIndex, yearIndex, countryIndex))
					.collect(Collectors.toList());
		} catch (IOException e) {
			// TODO error
			return new ArrayList<>();
		}
	}

	public List<AlbumTemp> loadCsvAlbum(String fileName) {
		try {
			List<String[]> csv = loadCsvObjectList(fileName);
			if (csv.size() == 0) {
				// TODO info
				return new ArrayList<>();
			}
			String[] first = csv.get(0);
			csv.remove(0);

			int nameIndex = getIndexByName(first, "name");
			int yearIndex = getIndexByName(first, "year");
			int artistIndex = getIndexByName(first, "artist");
			return csv.stream().map(x -> DBConverter.convertToAlbum(x, nameIndex, artistIndex, yearIndex))
					.collect(Collectors.toList());
		} catch (IOException e) {
			// TODO log
			return new ArrayList<>();
		}
	}

	public boolean doesFileExist(String filename) {
		File tempFile = new File(filename);
		return tempFile.exists();
	}

	public boolean needsInizialisation() {
		return doesFileExist(ARTIST) == false || doesFileExist(ALBUM) == false || doesFileExist(ALBUM_HAS_ARTIST);
	}

	private int mapCount = 0;
	private int filterCount = 0;

	private int getNextFilter() {
		return filterCount++;
	}

	private int getNextMap() {
		return mapCount++;
	}

	public <T> T streamReadFile(String filename, Class<T> clazz, StreamReadCommand<T> command) {
		mapCount = 0;
		filterCount = 0;

		try (Stream<String> stream = Files.lines(Paths.get(filename))) {
			Optional<T> val = stream.map(str -> {
				T obj = null;
				System.out.println(String.format("map: %d", getNextMap()));
				try {
					obj = objectMapper.readValue(str, clazz);
				} catch (IOException e) {
					return null;
				}
				return obj;
			}).filter(obj -> {
				System.out.println(String.format("filter: %d", getNextFilter()));
				if (obj == null || command.evaluate(obj) == false) {
					return false;
				}
				return true;
			}).findFirst();
			if (val.isPresent()) {
				return val.get();
			}
			return null;
		} catch (IOException e) {

		}
		return null;
	}

	public <T> void overrideFile(String filename, List<T> objs) {
		writeToFile(filename, objs, false);
	}

	public <T> void appendFile(String filename, T obj) {
		LinkedList<T> list = new LinkedList<>();
		list.add(obj);
		appendFile(filename, list);
	}

	public <T> void appendFile(String filename, List<T> objs) {
		writeToFile(filename, objs, true);
	}

	private List<String[]> loadCsvObjectList(String fileName) throws IOException {
		Reader reader = new FileReader(new File(fileName));
		CSVReader csvReader = new CSVReader(reader);
		List<String[]> list = new ArrayList<>();
		list = csvReader.readAll();
		reader.close();
		csvReader.close();
		return list.stream().map(x -> DBConverter.trimArray(x)).collect(Collectors.toList());
	}

	private int getIndexByName(String[] header, String name) {
		for (int i = 0; i < header.length; i++) {
			if (header[i].contains(name)) {
				return i;
			}
		}
		return 0;
	}

	private <T> void writeToFile(String filename, List<T> objs, boolean append) {
		try (FileWriter fw = new FileWriter(filename, append);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			for (T t : objs) {
				out.write(objectMapper.writeValueAsString(t) + "\n");
			}
		} catch (IOException e) {
			// exception handling left as an exercise for the reader
		}
	}

}
