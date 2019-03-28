package dhbw.db.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Artist {

	int id;
	private String name;
	private int year;
	private String country;

	@JsonCreator
	public Artist() {

	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj instanceof Artist == false) {
			return false;
		}
		Artist a = (Artist) obj;
		return this.id == a.id;
	}

}
