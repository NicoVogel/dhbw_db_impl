package dhbw.db.impl.instance.model;

public interface Updater<T> {

	/**
	 * update all values with the data from parameter object
	 * 
	 * @param object
	 * @return true if it worked, false it the parameter is null or the id of both
	 *         do not match
	 */
	public boolean update(T object);

}
