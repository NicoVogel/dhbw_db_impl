package dhbw.db.impl.instance.io.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import dhbw.db.impl.instance.io.CsvManager;
import dhbw.db.impl.instance.io.ReadConvert;
import dhbw.db.impl.instance.io.WriteConvert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CsvManagerImpl implements CsvManager {

	private final int retrys;
	private CSVFormat format = CSVFormat.DEFAULT.withCommentMarker('#').withIgnoreHeaderCase().withTrim();

	@Override
	public <T> Set<T> readLines(String filename, ReadConvert<T> readConverter) {

		Set<T> result = new HashSet<>();
		int counter = 0;

		do {
			try (Reader reader = Files.newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8);
					CSVParser parser = new CSVParser(reader, this.format)) {
				for (CSVRecord csvRecord : parser) {
					result.add(readConverter.convert(csvRecord));
				}
				if (log.isInfoEnabled()) {
					log.info("read {} elements from {}", result.size(), filename);
				}
				return result;
			} catch (IOException e) {
				log.error("error while parsing CSV file: " + filename, e);
			}
			counter++;
		} while (result.isEmpty() && counter <= this.retrys);

		return result;
	}

	@Override
	public <T> boolean appendLine(String filename, T data, WriteConvert<T> writeConverter) {
		return generalWrite(filename, StandardOpenOption.APPEND, (printer) -> {
			writeConverter.convert(printer, data);
			log.info("appended file {}", filename);
		});
	}

	@Override
	public <T> boolean updateLines(String filename, Iterable<T> iterator, WriteConvert<T> writeConverter) {
		return generalWrite(filename, StandardOpenOption.WRITE, (printer) -> {
			for (T t : iterator) {
				writeConverter.convert(printer, t);
			}
			log.info("updated file {}", filename);
		});
	}

	private <T> boolean generalWrite(String filename, OpenOption openOption, WriteOperation<T> operation) {
		boolean success;
		int counter = 0;

		do {
			success = true;

			try (Writer writer = Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_8, openOption);
					CSVPrinter printer = new CSVPrinter(writer, this.format)) {

				operation.operation(printer);
				printer.flush();
				return success;

			} catch (IOException e) {
				log.error("error while editing CSV file: " + filename, e);
				success = false;
			}
			counter++;
		} while (success == false && counter <= this.retrys);

		return success;
	}

	@FunctionalInterface
	private interface WriteOperation<T> {
		void operation(CSVPrinter printer);
	}

}
