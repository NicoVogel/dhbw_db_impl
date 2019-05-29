package dhbw.db.impl.instance.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dhbw.db.impl.instance.controller.error.DataNotFoundException;
import dhbw.db.impl.instance.controller.error.ParameterMissmatchException;
import dhbw.db.impl.instance.manager.AlbumHandler;
import dhbw.db.impl.instance.manager.ArtistHandler;
import dhbw.db.impl.instance.manager.FileManager;
import dhbw.db.impl.instance.model.Album;
import dhbw.db.impl.instance.model.Artist;
import dhbw.db.impl.instance.sync.SyncDBs;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/album")
@Slf4j
public class AlbumController {

	@Autowired
	private FileManager fm;
	@Autowired
	private SyncDBs syncDB;

	private AlbumHandler album() {
		return this.fm.editAlbum();
	}

	private ArtistHandler artist() {
		return this.fm.editArtist();
	}

	@PostMapping
	public int create(@RequestBody AlbumTO album) {
		if (album.getName().isEmpty()) {
			throw new ParameterMissmatchException("cannot create a new album, the name is empty", Album.class, "name",
					"", "");
		}
		if (album.getYear() < 0) {
			throw new ParameterMissmatchException("cannot create a new album, the year is below 0", Album.class, "year",
					Integer.toString(album.getYear()), "greather than 0");
		}
		if (album.getArtists().isEmpty()) {
			throw new ParameterMissmatchException("cannot create a new album, there needs to be at least one artist id",
					Album.class, "artists", "0", "graether than 0");
		}

		album().create(album);
		List<Artist> rollback = new ArrayList<>();
		for (Integer artistId : album.getArtists()) {
			Artist artist = artist().read(artistId);
			if (artist == null) {
				rollback(album, rollback);
				throw new DataNotFoundException(
						String.format("cannot create a new album where the artist with id %d does not exist", artistId),
						Album.class, artistId);
			}
			album().addArtistToAlbum(album, artist);
			rollback.add(artist);
		}
		this.syncDB.sync();
		return album.getId();
	}

	private void rollback(Album album, List<Artist> artists) {
		album().delete(album.getId());
		for (Artist artist : artists) {
			album().removeArtistFromAlbum(album, artist);
		}
	}

	@GetMapping("/{id}")
	public Album read(@PathVariable int id) {
		return album().read(id);
	}

	@PutMapping("/{id}")
	public void update(@PathVariable int id, @RequestBody Album album) {
		album.setId(id);
		if (album().update(album) == false) {
			throw new DataNotFoundException(String.format("update failed, no album found for ID %d.", id), Album.class,
					id);
		}
		this.syncDB.sync();
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable int id) {
		if (album().delete(id) == false) {
			log.debug(String.format("tried to delete an artist where the id %d doesn't exist", id));
		}
		this.syncDB.sync();
	}

	@PostMapping("/{id}/artist")
	public void addArtist(@PathVariable int id, @RequestBody int artistId) {

		Album album = album().read(id);
		if (album == null) {
			throw new DataNotFoundException(
					String.format("could not add an artist to album, because the album with id %d does not exist", id),
					Album.class, id);
		}

		Artist artist = artist().read(artistId);
		if (artist == null) {
			throw new DataNotFoundException(String
					.format("could not add an artist to album, because the artist with id %d does not exist", artistId),
					Artist.class, artistId);
		}
		album().addArtistToAlbum(album, artist);
		this.syncDB.sync();
	}

	@DeleteMapping("/{id}/artist/{artistid}")
	public void removeArtist(@PathVariable int id, @PathVariable int artistId) {

		Album album = album().read(id);
		if (album == null) {
			throw new DataNotFoundException(String
					.format("could not remove an artist from album, because the album with id %d does not exist", id),
					Album.class, id);
		}

		Artist artist = artist().read(artistId);
		if (artist == null) {
			throw new DataNotFoundException(
					String.format("could not remove an artist from album, because the artist with id %d does not exist",
							artistId),
					Artist.class, artistId);
		}
		album().removeArtistFromAlbum(album, artist);
		this.syncDB.sync();
	}
}
