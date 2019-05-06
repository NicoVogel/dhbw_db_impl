package dhbw.db.impl.instance.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dhbw.db.impl.instance.manager.AlbumHandler;
import dhbw.db.impl.instance.manager.FileManager;

@RestController
@RequestMapping("/performance")
public class PerformanceController {

	@Autowired
	private FileManager fm;

	private AlbumHandler album() {
		return fm.editAlbum();
	}

	@GetMapping
	public Map<Integer, Long> releasedAlbumsPerYear() {
		return album().releasedAlbumsPerYear();
	}

	@GetMapping("/index")
	public Map<Integer, Long> releasedAlbumsPerYearIndex() {
		return album().releasedAlbumsPerYearIndex();
	}

	@PostMapping("/reload")
	public int reloadData() {
		return fm.reloadData();
	}
}
