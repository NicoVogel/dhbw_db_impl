package dhbw.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Artist {

	int id;
	private String name;
	private int year;
	private String country;
}
