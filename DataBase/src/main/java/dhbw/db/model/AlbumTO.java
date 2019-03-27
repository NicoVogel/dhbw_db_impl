package dhbw.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlbumTO {

	private String name;
	private String artistName;
	private int year;
}
