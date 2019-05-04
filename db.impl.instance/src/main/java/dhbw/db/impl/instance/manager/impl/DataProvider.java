package dhbw.db.impl.instance.manager.impl;

import java.util.Map;
import java.util.Set;

import dhbw.db.impl.instance.model.Album;
import dhbw.db.impl.instance.model.Artist;
import dhbw.db.impl.instance.model.Tupel;

/*package*/ interface DataProvider {

	public IDProvider getAlbumIDProvider();

	public IDProvider getArtistIDProvider();

	public Map<Integer, Album> getAlbums();

	public Map<Integer, Artist> getArtists();

	public Set<Tupel<Album, Artist>> getRelation();

	public String getAlbumFilename();

	public String getArtistFilename();

}
