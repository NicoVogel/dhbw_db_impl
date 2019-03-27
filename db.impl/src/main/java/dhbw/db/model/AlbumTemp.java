package dhbw.db.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlbumTemp {

	private int id;
	private String name;
	private String[] artistNames;
	private int year;
}
