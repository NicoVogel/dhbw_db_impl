package dhbw.db.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Artist {

	int id;
	private String name;
	private int year;
	private String country;

	@JsonCreator
	public Artist() {

	}
}
