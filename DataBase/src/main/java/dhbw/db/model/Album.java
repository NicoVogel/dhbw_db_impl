package dhbw.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Album {

	private int id;
	private String name;
	private int artistId;
	private int year;
}
