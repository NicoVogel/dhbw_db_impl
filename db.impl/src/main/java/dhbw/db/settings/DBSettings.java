package dhbw.db.settings;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DBSettings {

	@Value("${dhbw.db.files.path}")
	private String dataFolder;
	@Value("${dhbw.db.files.artist}")
	private String dataArtist;
	@Value("${dhbw.db.files.album}")
	private String dataAlbum;
	@Value("${dhbw.db.files.albumHasArtist}")
	private String dataAlbumHasArtist;
	@Value("${dhbw.db.files.extenstion")
	private String extenstion;

	public String getArtistFile() {
		return getFile(this.dataArtist, "");
	}

	public String getArtistFile(int index) {
		return getFile(this.dataArtist, Integer.toString(index));
	}

	public String 

	private String getFile(String name, String str) {
		return String.format("%s%s%s.%s", this.dataFolder, name, str, this.extenstion);
	}

}
