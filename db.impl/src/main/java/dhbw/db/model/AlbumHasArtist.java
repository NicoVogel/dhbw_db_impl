package dhbw.db.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class AlbumHasArtist implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4033702399766454479L;
	private int artistId;
	private int albumId;

	@JsonCreator
	public AlbumHasArtist() {

	}

}
