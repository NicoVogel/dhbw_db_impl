package dhbw.db.impl.instance.controller.error;

import lombok.Getter;

@Getter
public class ParameterMissmatchException extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Class<?> type;
	private String parameterName;
	private String input;
	private String expectedFormat;

	public ParameterMissmatchException(String message, Class<?> type, String parameterName, String input,
			String expectedFormat) {
		super(message);
		this.type = type;
		this.parameterName = parameterName;
		this.input = input;
		this.expectedFormat = expectedFormat;
	}

}
