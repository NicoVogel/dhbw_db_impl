package dhbw.db.model;

import java.util.List;
import java.util.stream.Collectors;

public class Converter {

	private Converter () {
		
	}
	
	
	public static List<Artist> convert(List<ArtistTO> tos){
		return tos.stream().map(x -> new Artist(0, x.getName(), x.getYear(), x.getCountry())).collect(Collectors.toList());
	}
	
	
}
