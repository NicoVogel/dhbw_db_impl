package dhbw.db.impl.instance.controller.error;

import lombok.Getter;

@Getter
public class DataNotFoundException extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Class<?> type;
	private int id;

	public DataNotFoundException(String message, Class<?> type, int id) {
		super(message);
		this.id = id;
		this.type = type;

	}

}
