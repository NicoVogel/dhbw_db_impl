package dhbw.db.impl.instance.manager;

public interface CRUD<T> {

	/**
	 * override the id of the object. return the object if it worked or null if it
	 * didnt
	 * 
	 * @param obj
	 * @return
	 */
	public T create(T obj);

	/**
	 * search for an element by id. return the found element or null if it doesn't
	 * exist
	 * 
	 * @param id
	 * @return
	 */
	public T read(int id);

	/**
	 * true if it worked
	 * 
	 * @param obj
	 * @return
	 */
	public boolean update(T obj);

	/**
	 * true if it worked
	 * 
	 * @param obj
	 * @return
	 */
	public boolean delete(int id);

}
