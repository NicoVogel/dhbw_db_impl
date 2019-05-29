package dhbw.db.impl.instance.controller.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class ApiSubError {

	private String message;
}
