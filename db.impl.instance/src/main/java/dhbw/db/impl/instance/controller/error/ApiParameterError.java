package dhbw.db.impl.instance.controller.error;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApiParameterError extends ApiSubError {

	private String parameterName;
	private String input;
	private String expectedFormat;
	private String className;

	public ApiParameterError(String message, Class<?> clazz, String parameterName, String input,
			String expectedFormat) {
		super(message);
		this.className = clazz.getName();
		this.parameterName = parameterName;
		this.input = input;
		this.expectedFormat = expectedFormat;
	}

}
