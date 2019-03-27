package dhbw.db.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dhbw.db.io.DBIO;
import dhbw.db.model.Album;

@RestController
@RequestMapping("/")
public class DBRestController {

	@Autowired
	private DBIO io;
	
	@GetMapping("/")
	public Album getAll() {
		return null;
	}
	
}
