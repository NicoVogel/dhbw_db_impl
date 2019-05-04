
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
public class Album implements Identifier, Updater<Album>, Itself<Album> {

	private int id;
	private String name;
	private int year;

	@JsonCreator
	public Album() {

	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj instanceof Album == false) {
			return false;
		}
		Album a = (Album) obj;
		return this.id == a.id;
	}

	@Override
	public boolean update(Album object) {
		if (object == null || this.id != object.id) {
			return false;
		}
		this.name = object.name;
		this.year = object.year;
		return true;
	}

	@Override
	public Album self() {
		return this;
	}

}
