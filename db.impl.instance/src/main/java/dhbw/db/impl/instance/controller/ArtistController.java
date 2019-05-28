package dhbw.db.impl.instance.controller;

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
import dhbw.db.impl.instance.manager.ArtistHandler;
import dhbw.db.impl.instance.manager.FileManager;
import dhbw.db.impl.instance.model.Artist;
import dhbw.db.impl.instance.sync.SyncDBs;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/artist")
@Slf4j
public class ArtistController {

	@Autowired
	private FileManager fm;
	@Autowired
	private SyncDBs syncDB;

	private ArtistHandler artist() {
		return fm.editArtist();
	}

	@PostMapping
	public int create(@RequestBody Artist artist) {
		if (artist.getName().isEmpty()) {
			throw new ParameterMissmatchException("cannot create a new artist, the name is empty", Artist.class, "name",
					"", "");
		}
		if (artist.getYear() < 0) {
			throw new ParameterMissmatchException("cannot create a new artist, the year is below 0", Artist.class,
					"year", Integer.toString(artist.getYear()), "greather than 0");
		}
		int id = artist().create(artist).getId();
		this.syncDB.sync();
		return id;
	}

	@GetMapping("/{id}")
	public Artist read(@PathVariable int id) {
		return artist().read(id);
	}

	@PutMapping("/{id}")
	public void update(@PathVariable int id, @RequestBody Artist artist) {
		artist.setId(id);
		if (artist().update(artist) == false) {
			throw new DataNotFoundException(String.format("update failed, no artist found for ID %d.", id),
					Artist.class, id);
		}
		this.syncDB.sync();
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable int id) {
		if (artist().delete(id) == false) {
			log.debug(String.format("tried to delete an artist where the id %d doesn't exist", id));
		}
		this.syncDB.sync();
	}

}
