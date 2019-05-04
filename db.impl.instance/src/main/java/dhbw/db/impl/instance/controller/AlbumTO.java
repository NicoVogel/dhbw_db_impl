package dhbw.db.impl.instance.controller;

import java.util.ArrayList;
import java.util.List;

import dhbw.db.impl.instance.model.Album;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlbumTO extends Album {

	private List<Integer> artists;

	public List<Integer> getArtists() {
		if (this.artists == null) {
			this.artists = new ArrayList<>();
		}
		return this.artists;
	}
}
