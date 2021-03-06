package dhbw.db.impl.instance.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esotericsoftware.minlog.Log;

import dhbw.db.impl.instance.io.CsvManager;
import dhbw.db.impl.instance.manager.AlbumHandler;
import dhbw.db.impl.instance.manager.ArtistHandler;
import dhbw.db.impl.instance.manager.FileManager;
import dhbw.db.impl.instance.model.Album;
import dhbw.db.impl.instance.model.Artist;
import dhbw.db.impl.instance.model.Tupel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;

@RequiredArgsConstructor
@Slf4j
public class FileManagerImpl implements FileManager, DataProvider {

	private static int START_ID = 1;
	private static int NO_ID = -1;

	@Getter
	@NonNull
	private String artistFilename;
	@Getter
	@NonNull
	private String albumFilename;
	@NonNull
	private CsvManager fileIO;

	private IndexManager index;
	private ArtistHandler artistCrud;
	private AlbumHandler albumCrud;
	private int albumID = START_ID;
	private int artistID = START_ID;
	private Map<Integer, Album> albums;
	private Map<Integer, Artist> artists;
	private Set<Tupel<Album, Artist>> relation;
	private Object synchronizerObject = new Object();
	private boolean isreloading = false;

	private List<Long> times;
	private long last;

	public void getData() {
	}

	private void initTimeMessure() {
		this.times = new ArrayList<>();
		this.last = System.currentTimeMillis();
	}

	private void nextTime() {
		long current = System.currentTimeMillis();
		this.times.add(current - this.last);
		this.last = current;
	}

	@Override
	public synchronized int reloadData() {
		this.isreloading = true;
		initTimeMessure();

		// get data
		Set<Artist> artists = readArtists();
		nextTime();

		Set<Tupel<Album, String[]>> albumExtended = readAlbum();
		nextTime();

		// fix and convert data
		findAndRemoveDuplicateArtistsByName(artists);
		Set<Tupel<Album, Artist>> relation = relationAndRemoveAmbiguousAlbums(artists, albumExtended);
		nextTime();

		// set artists ids
		this.artistID = START_ID;
		StreamEx.of(artists).forEach(x -> x.setId(getArtistIDProviderNoWait().getNextID()));

		// set album ids
		this.albumID = START_ID;
		StreamEx.of(albumExtended).map(Tupel::getFirst).forEach(x -> x.setId(getAlbumIDProviderNoWait().getNextID()));

		// override original data
		this.artists = StreamEx.of(artists).toMap(Artist::getId, Artist::self);
		this.albums = StreamEx.of(albumExtended).map(Tupel::getFirst).toMap(Album::getId, Album::self);
		this.relation = relation;

		// fill index
		StreamEx.of(albumExtended).forEach(x -> getIndex().addIndex(x.getFirst()));
		nextTime();

		log.info(String.format("read artist (%d), read album (%d), build relation (%d), last steps (%d)",
				this.times.get(0), this.times.get(1), this.times.get(2), this.times.get(3)));

		int albumCount = this.albums.size();
		int artistCount = this.artists.size();
		int result = Math.max(albumCount, artistCount);
		this.isreloading = false;
		synchronized (this.synchronizerObject) {
			this.synchronizerObject.notifyAll();
		}
		return result;
	}

	@Override
	public ArtistHandler editArtist() {
		waitIfReloading();
		if (this.artistCrud == null) {
			this.artistCrud = new ArtistCRUD(this.fileIO, this);
		}
		return this.artistCrud;
	}

	@Override
	public AlbumHandler editAlbum() {
		waitIfReloading();
		return editAlbumNoWait();
	}

	private AlbumHandler editAlbumNoWait() {
		if (this.albumCrud == null) {
			AlbumCRUD obj = new AlbumCRUD(this.fileIO, this);
			this.index = obj.getIndex();
			this.albumCrud = obj;
		}
		return this.albumCrud;
	}

	private IndexManager getIndex() {
		if (this.index == null) {
			editAlbumNoWait();
		}
		return this.index;
	}

	@Override
	public IDProvider getAlbumIDProvider() {
		waitIfReloading();
		return getAlbumIDProviderNoWait();
	}

	private IDProvider getAlbumIDProviderNoWait() {
		return () -> this.albumID++;
	}

	@Override
	public IDProvider getArtistIDProvider() {
		waitIfReloading();
		return getArtistIDProviderNoWait();
	}

	private IDProvider getArtistIDProviderNoWait() {
		return () -> this.artistID++;
	}

	@Override
	public Map<Integer, Album> getAlbums() {
		waitIfReloading();
		if (this.albums == null) {
			this.albums = new HashMap<>();
		}
		return this.albums;
	}

	@Override
	public Map<Integer, Artist> getArtists() {
		waitIfReloading();
		if (this.artists == null) {
			this.artists = new HashMap<>();
		}
		return this.artists;
	}

	@Override
	public Set<Tupel<Album, Artist>> getRelation() {
		waitIfReloading();
		if (this.relation == null) {
			this.relation = new HashSet<>();
		}
		return this.relation;
	}

	private void waitIfReloading() {
		if (this.isreloading) {
			try {
				log.info("wait for file reload");
				synchronized (this.synchronizerObject) {
					this.synchronizerObject.wait();
				}
				log.info("continue processing");
			} catch (InterruptedException e) {
				log.error("wait was interruped", e);
			}
		}
	}

	private Integer tryParse(String val, LogMessage errorMessage) {
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			log.info(errorMessage.process(), e);
		}
		return null;
	}

	private Set<Artist> readArtists() {
		return this.fileIO.readLines(this.artistFilename, reader -> {
			String name = reader.get(0);
			String country = reader.get(2);
			Integer year = tryParse(reader.get(1), () -> String.format("could not read artist year from %s", name));
			if (year == null) {
				return null;
			}
			return new Artist(NO_ID, name, year, country);
		});
	}

	private Set<Tupel<Album, String[]>> readAlbum() {
		return this.fileIO.readLines(this.albumFilename, reader -> {
			String name = reader.get(0);
			String[] relation = reader.get(2).split(",");
			Integer year = tryParse(reader.get(1), () -> String.format("could not read album year from %s", name));
			if (year == null) {
				return null;
			}
			Album a = new Album(NO_ID, name, year);
			return new Tupel<>(a, relation);
		});
	}

	private void findAndRemoveDuplicateArtistsByName(Set<Artist> artists) {

		Collection<List<Artist>> groupedValues = StreamEx.of(artists).groupingBy(Artist::getName).values();
		StreamEx<List<Artist>> duplicateIterator = StreamEx.of(groupedValues).filter(x -> x.size() > 1);

		for (List<Artist> duplicateName : duplicateIterator) {
			Log.debug(String.format("There exist at least two artists with the same name '%s', amount %d",
					duplicateName.get(0).getName(), duplicateName.size()));
			for (Artist artist : duplicateName) {
				if (log.isDebugEnabled()) {
					log.debug(String.format("remove duplikate artist %s", artist));
				}
				artists.remove(artist);
			}
		}
	}

	private Set<Tupel<Album, Artist>> relationAndRemoveAmbiguousAlbums(Set<Artist> artists,
			Set<Tupel<Album, String[]>> albumExtended) {

		// preparations
		Map<Integer, Artist> artistNameHash = StreamEx.of(artists).toMap(Artist::getNameHash, Artist::self);
		Set<Tupel<Album, Artist>> relations = new HashSet<>();
		List<Tupel<Album, String[]>> removalList = new ArrayList<>();

		for (Tupel<Album, String[]> tupel : albumExtended) {

			// preparations to evaluate album
			Album album = tupel.getFirst();
			List<Artist> albumRelations = new ArrayList<>();
			boolean validRun = true;

			for (String artistName : tupel.getSecond()) {
				Artist artist = artistNameHash.get(artistName.hashCode());

				// check if valid -> define album as not valid and add it to the removal list
				if (artist == null) {
					log.debug(String.format("the album '%s' does have a artist named '%s' wich does not exist",
							album.getName(), artistName));
					validRun = false;
					removalList.add(tupel);
					break;
				}
				albumRelations.add(artist);
			}

			// if its not valid check next
			if (validRun == false) {
				continue;
			}

			// add all relations of the current album
			for (Artist artist : albumRelations) {
				relations.add(new Tupel<>(album, artist));
			}
		}

		// remove invalid albums
		albumExtended.removeAll(removalList);
		return relations;
	}

	@FunctionalInterface
	private interface LogMessage {
		String process();
	}
}
