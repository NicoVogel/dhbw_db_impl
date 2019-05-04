package dhbw.db.impl.instance.manager.impl;

import java.io.IOException;
import java.util.List;

import dhbw.db.impl.instance.io.CsvManager;
import dhbw.db.impl.instance.io.WriteConvert;
import dhbw.db.impl.instance.manager.AlbumHandler;
import dhbw.db.impl.instance.model.Album;
import dhbw.db.impl.instance.model.Artist;
import dhbw.db.impl.instance.model.Tupel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;

@RequiredArgsConstructor
@Slf4j
public class AlbumCRUD implements AlbumHandler {

	@NonNull
	private CsvManager fileIO;

	@NonNull
	private DataProvider dataProvider;

	private WriteConvert<Album> writer = (printer, data) -> {
		try {
			printer.printRecord(data.getName(), data.getYear());
		} catch (IOException e) {
			log.error("unable to add a element to file; Data: %s", data);
		}
	};

	@Override
	public Album create(Album obj) {
		return CRUDImpl.create(obj, this.dataProvider.getAlbumIDProvider(), this.dataProvider.getAlbums(), this.fileIO,
				this.dataProvider.getAlbumFilename(), this.writer);
	}

	@Override
	public Album read(int id) {
		return CRUDImpl.read(id, this.dataProvider.getAlbums());
	}

	@Override
	public boolean update(Album obj) {
		return CRUDImpl.update(obj, this.dataProvider.getAlbums(), this.fileIO, this.dataProvider.getAlbumFilename(),
				this.writer);
	}

	@Override
	public boolean delete(int id) {
		return CRUDImpl.delete(id, this.dataProvider.getAlbums());
	}

	@Override
	public boolean addArtistToAlbum(Album album, Artist artist) {
		return this.dataProvider.getRelation().add(new Tupel<>(album, artist));
	}

	@Override
	public boolean removeArtistFromAlbum(Album album, Artist artist) {
		return this.dataProvider.getRelation().remove(new Tupel<>(album, artist));
	}

	@Override
	public List<Artist> getArtists(Album album) {
		return StreamEx.of(this.dataProvider.getRelation()).filter(x -> x.getFirst().equals(album))
				.map(Tupel::getSecond).toList();
	}

}
