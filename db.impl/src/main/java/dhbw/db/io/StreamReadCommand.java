package dhbw.db.io;

@FunctionalInterface
public interface StreamReadCommand<T> {

	/**
	 * 
	 * @param object
	 * @return true if there is a match
	 */
	boolean evaluate(T object);
}
