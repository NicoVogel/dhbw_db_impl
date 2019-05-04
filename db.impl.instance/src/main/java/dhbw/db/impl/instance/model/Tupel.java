package dhbw.db.impl.instance.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tupel<A, B> {

	private A first;
	private B second;
}
