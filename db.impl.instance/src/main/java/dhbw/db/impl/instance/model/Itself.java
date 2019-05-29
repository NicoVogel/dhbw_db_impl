package dhbw.db.impl.instance.model;

public interface Itself<T> {

	/**
	 * returns itself without any change. is useful for streams
	 * 
	 * @return
	 */
	public T self();

}
