package dhbw.db.rest;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistTO {

	private String name;
	private int year;
	private String country;
	private List<AlbumTO> albums = new ArrayList<>();

}
