package dhbw.db.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBIO {

	public static final String ARTIST = "artist.csv";
	public static final String ALBUM = "album.csv";
	public static final String ALBUM_HAS_ARTIST = "albumhasartist.csv";

	private ObjectMapper objectMapper = new ObjectMapper();

	public <T> List<T> loadCsvObjectList(Class<T> type, String fileName) {
		try {
			CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
			CsvMapper mapper = new CsvMapper();
			File file = new ClassPathResource(fileName).getFile();
			MappingIterator<T> readValues = mapper.reader(type).with(bootstrapSchema).readValues(file);
			return readValues.readAll();
		} catch (Exception e) {
			log.error("Error occurred while loading object list from file " + fileName, e);
			return Collections.emptyList();
		}
	}

	public <T> T streamReadFile(String filename, Class<T> clazz, StreamReadCommand<T> command) {
		try (Stream<String> stream = Files.lines(Paths.get(filename))) {
			return stream.map(str -> {
				T obj = null;
				try {
					obj = objectMapper.readValue(str, clazz);
				} catch (IOException e) {
					return null;
				}
				return obj;
			}).filter(obj -> {
				if (obj == null || command.evaluate(obj) == false) {
					return false;
				}
				return true;
			}).findFirst().get();
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

	private <T> void writeToFile(String filename, List<T> objs, boolean append) {
		try (FileWriter fw = new FileWriter(filename, append);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			for (T t : objs) {
				out.write(objectMapper.writeValueAsString(t));
			}
		} catch (IOException e) {
			// exception handling left as an exercise for the reader
		}
	}

}
