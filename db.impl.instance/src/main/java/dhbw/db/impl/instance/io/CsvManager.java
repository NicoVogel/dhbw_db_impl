package dhbw.db.impl.instance.io;

import java.util.Set;

public interface CsvManager {

	public <T> Set<T> readLines(String filename, ReadConvert<T> readConverter);

	public <T> boolean appendLine(String filename, T data, WriteConvert<T> writeConverter);

	public <T> boolean updateLines(String filename, T startEntity, Iterable<T> iterator,
			WriteConvert<T> writeConverter);

}
