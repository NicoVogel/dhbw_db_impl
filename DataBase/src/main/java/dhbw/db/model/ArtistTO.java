package dhbw.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArtistTO {

	private String name;
	private int year;
	private String country;
}
