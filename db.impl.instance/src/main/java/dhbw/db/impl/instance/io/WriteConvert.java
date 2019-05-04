package dhbw.db.impl.instance.io;

import org.apache.commons.csv.CSVPrinter;

@FunctionalInterface
public interface WriteConvert<T> {

	public void convert(CSVPrinter csvPrinter, T object);

}
