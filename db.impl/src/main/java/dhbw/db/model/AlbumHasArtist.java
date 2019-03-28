package dhbw.db.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class AlbumHasArtist {

	private int artistId;
	private int albumId;

	@JsonCreator
	public AlbumHasArtist() {

	}

}
