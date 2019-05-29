package dhbw.db.impl.instance.controller.error;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ApiError {

	private String message;
	private List<ApiSubError> subErrors;

	public ApiError(String message) {
		this.message = message;
	}

	public ApiError(String message, ApiSubError... errors) {
		this(message);
		for (ApiSubError apiSubError : errors) {
			getSubErrors().add(apiSubError);
		}
	}

	public List<ApiSubError> getSubErrors() {
		if (this.subErrors == null) {
			this.subErrors = new ArrayList<>();
		}
		return this.subErrors;
	}

}
