package dhbw.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Artist {
	
	private int id;
	private String name;
	private String country;
}
