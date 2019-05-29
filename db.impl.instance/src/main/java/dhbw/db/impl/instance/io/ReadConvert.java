package dhbw.db.impl.instance.io;

import org.apache.commons.csv.CSVRecord;

@FunctionalInterface
public interface ReadConvert<T> {

	public T convert(CSVRecord record);

}
