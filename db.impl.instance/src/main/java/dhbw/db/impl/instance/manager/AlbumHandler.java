package dhbw.db.impl.instance.manager;

import java.util.List;
import java.util.Map;

import dhbw.db.impl.instance.model.Album;
import dhbw.db.impl.instance.model.Artist;

public interface AlbumHandler extends CRUD<Album> {

	/**
	 * true if it worked, false if the relation already exists
	 * 
	 * @param album
	 * @param artist
	 * @return
	 */
	public boolean addArtistToAlbum(Album album, Artist artist);

	/**
	 * true if relation was deleted, false if nothing changed
	 * 
	 * @param album
	 * @param artist
	 * @return
	 */
	public boolean removeArtistFromAlbum(Album album, Artist artist);

	public List<Artist> getArtists(Album album);

	public Map<Integer, Long> releasedAlbumsPerYear();

	public Map<Integer, Long> releasedAlbumsPerYearIndex();

}
