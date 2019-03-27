package dhbw.db.io;

@FunctionalInterface
public interface StreamReadCommand<T> {

	/**
	 * 
	 * @param object
	 * @return true if the object is found
	 */
	boolean evaluate(T object);
}
