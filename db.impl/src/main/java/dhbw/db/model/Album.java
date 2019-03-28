package dhbw.db.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Album implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8887889012255995457L;
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

}
