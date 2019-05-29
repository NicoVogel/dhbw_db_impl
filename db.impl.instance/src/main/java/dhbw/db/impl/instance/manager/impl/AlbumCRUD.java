package dhbw.db.impl.instance.manager.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import dhbw.db.impl.instance.io.CsvManager;
import dhbw.db.impl.instance.io.WriteConvert;
import dhbw.db.impl.instance.manager.AlbumHandler;
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
public class AlbumCRUD implements AlbumHandler {

	@NonNull
	private CsvManager fileIO;

	@NonNull
	private DataProvider dataProvider;

	@Getter
	private IndexManager index = new IndexManager();

	private WriteConvert<Album> writer = (printer, data) -> {
		try {
			printer.printRecord(data.getName(), data.getYear());
		} catch (IOException e) {
			log.error("unable to add a element to file; Data: %s", data);
		}
	};

	@Override
	public Album create(Album obj) {
		Album result = CRUDImpl.create(obj, this.dataProvider.getAlbumIDProvider(), this.dataProvider.getAlbums(),
				this.fileIO, this.dataProvider.getAlbumFilename(), this.writer);

		if (result != null) {
			this.index.addIndex(result);
		}
		return result;

	}

	@Override
	public Album read(int id) {
		return CRUDImpl.read(id, this.dataProvider.getAlbums());
	}

	@Override
	public boolean update(Album obj) {
		Album original = read(obj.getId());
		boolean result = CRUDImpl.update(obj, Album.class, this.dataProvider.getAlbums(), this.fileIO,
				this.dataProvider.getAlbumFilename(), this.writer, original);
		if (result) {
			this.index.updateIndex(original, obj);
		}
		return result;
	}

	@Override
	public boolean delete(int id) {
		Album deleted = CRUDImpl.delete(id, Album.class, this.dataProvider.getAlbums(), this.fileIO,
				this.dataProvider.getAlbumFilename(), this.writer);
		boolean result = deleted != null;
		if (result) {
			this.index.removeIndex(deleted);
		}
		return result;
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

	@Override
	public Map<Integer, Long> releasedAlbumsPerYear() {
		return StreamEx.of(this.dataProvider.getAlbums().values()).map(Album::getYear)
				.filter(x -> this.index.isInYearRange(x)).groupingBy(Function.identity(), Collectors.counting());
	}

	@Override
	public Map<Integer, Long> releasedAlbumsPerYearIndex() {
		return this.index.getIndex();
	}

}
