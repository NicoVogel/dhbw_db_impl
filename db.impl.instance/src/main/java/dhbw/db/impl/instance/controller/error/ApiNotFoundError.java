package dhbw.db.impl.instance.controller.error;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data()
@EqualsAndHashCode(callSuper = true)
public class ApiNotFoundError extends ApiSubError {

	private String className;
	private int id;

	public ApiNotFoundError(String message, String className, int id) {
		super(message);
		this.className = className;
		this.id = id;
	}

}
