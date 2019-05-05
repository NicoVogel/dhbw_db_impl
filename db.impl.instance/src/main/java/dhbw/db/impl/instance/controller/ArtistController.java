package dhbw.db.impl.instance.controller;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/artist", consumes = "application/json", produces = "application/json")
@Slf4j
public class ArtistController {

	@Autowired
	private FileManager fm;

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
		return artist().create(artist).getId();
	}

	@GetMapping("/{id}")
	public Artist read(@PathParam("id") int id) {
		return artist().read(id);
	}

	@PutMapping("/{id}")
	public void update(@PathParam("id") int id, @RequestBody Artist artist) {
		artist.setId(id);
		if (artist().update(artist) == false) {
			throw new DataNotFoundException(String.format("update failed, no artist found for ID %d.", id),
					Artist.class, id);
		}
	}

	@DeleteMapping("/{id}")
	public void delete(@PathParam("id") int id) {
		if (artist().delete(id) == false) {
			log.debug(String.format("tried to delete an artist where the id %d doesn't exist", id));
		}
	}

}