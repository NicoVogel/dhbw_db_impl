package dhbw.db.impl.instance.io.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

	private static final int FILE_GROWTH_LOCK_BUFFER = 1000;
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
		return generalWrite(filename, (randomAccessFile, unchangedPrinter, overridePrinter, file, unchanged) -> {

			// get the file channel where the lock will be placed
			FileChannel fileChannel = randomAccessFile.getChannel();

			// lock end of file
			FileLock lock = fileChannel.lock(file.length(), FILE_GROWTH_LOCK_BUFFER, false);

			// goto end of file
			randomAccessFile.seek(file.length());

			// convert the data and write it into the override printer
			writeConverter.convert(overridePrinter, data);
			log.info("appended file {}", filename);
			return lock;
		});
	}

	@Override
	public <T> boolean updateLines(String filename, T startEntity, Iterable<T> iterator,
			WriteConvert<T> writeConverter) {
		return generalWrite(filename, (randomAccessFile, unchangedPrinter, overridePrinter, file, unchanged) -> {

			// calculate starting position
			for (T t : iterator) {
				if (t == startEntity)
					break;
				writeConverter.convert(unchangedPrinter, t);
			}
			int unchangedDataLength = getStringBytes(unchanged).length;

			// get the file channel where the lock will be placed
			FileChannel fileChannel = randomAccessFile.getChannel();

			// lock from the changed element until end of file + growth buffer
			FileLock lock = fileChannel.lock(unchangedDataLength,
					file.length() - unchangedDataLength + FILE_GROWTH_LOCK_BUFFER, false);

			// goto end of file
			randomAccessFile.seek(unchangedDataLength);

			for (T t : iterator) {
				writeConverter.convert(overridePrinter, t);
			}
			log.info("updated file {}", filename);
			return lock;
		});
	}

	private <T> boolean generalWrite(String filename, WriteOperation<T> operation) {
		int counter = 0;

		do {
			StringBuilder unchanged = new StringBuilder();
			StringBuilder override = new StringBuilder();
			File file = new File(filename);
			try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
					CSVPrinter unchangedPrinter = new CSVPrinter((Appendable) unchanged, this.format);
					CSVPrinter overridePrinter = new CSVPrinter((Appendable) override, this.format)) {

				// fill printer and lock file
				FileLock lock = operation.operation(randomAccessFile, unchangedPrinter, overridePrinter, file,
						unchanged);

				// make sure the new data printer is done writing
				overridePrinter.flush();

				// get new data
				byte[] overrideData = getStringBytes(override);

				// write new data
				randomAccessFile.write(overrideData);

				// release lock
				lock.release();
				return true;

			} catch (IOException e) {
				log.error("error while editing CSV file: " + filename, e);
			}
			counter++;
		} while (counter <= this.retrys);

		return false;
	}

	private byte[] getStringBytes(StringBuilder data) {
		return data.toString().getBytes(StandardCharsets.UTF_8);
	}

	@FunctionalInterface
	private interface WriteOperation<T> {
		FileLock operation(RandomAccessFile randomAccessFile, CSVPrinter unchangedPrinter, CSVPrinter overridePrinter,
				File file, StringBuilder unchanged) throws IOException;
	}

}
