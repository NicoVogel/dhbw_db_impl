package dhbw.db.impl.instance.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Artist implements Identifier, Updater<Artist>, Itself<Artist> {

	private int id;
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

	@Override
	public boolean update(Artist object) {
		if (object == null || this.id == object.id) {
			return false;
		}
		this.name = object.name;
		this.year = object.year;
		this.country = object.country;
		return true;
	}

	public int getNameHash() {
		return this.name.hashCode();
	}

	@Override
	public Artist self() {
		return this;
	}

}
