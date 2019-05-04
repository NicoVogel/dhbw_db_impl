package dhbw.db.impl.instance.manager;

import java.util.List;

import dhbw.db.impl.instance.model.Album;
import dhbw.db.impl.instance.model.Artist;
import dhbw.db.impl.instance.model.Tupel;

public interface ArtistHandler extends CRUD<Artist> {

	/**
	 * Grundlegende Queries an das DBMS (1)
	 * 
	 * @return
	 */
	public List<Album> getAlbums(Artist artist);

	/**
	 * Grundlegende Queries an das DBMS (2)
	 * 
	 * @return
	 */
	public List<Tupel<Artist, Integer>> getLastReleaseDateOfEachArtist();

	/**
	 * Grundlegende Queries an das DBMS (4)
	 * 
	 * @return
	 */
	public List<Artist> getArtistsWhichHaveNoReleases();

	/**
	 * find artist by its name
	 * 
	 * @param name
	 * @return
	 */
	public Artist getByName(String name);

}
