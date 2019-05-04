package dhbw.db.impl.instance.manager.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dhbw.db.impl.instance.io.CsvManager;
import dhbw.db.impl.instance.io.WriteConvert;
import dhbw.db.impl.instance.manager.ArtistHandler;
import dhbw.db.impl.instance.model.Album;
import dhbw.db.impl.instance.model.Artist;
import dhbw.db.impl.instance.model.Tupel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

@RequiredArgsConstructor
@Slf4j
public class ArtistCRUD implements ArtistHandler {

	@NonNull
	private CsvManager fileIO;

	@NonNull
	private DataProvider dataProvider;

	private WriteConvert<Artist> writer = (printer, data) -> {
		try {
			printer.printRecord(data.getName(), data.getYear(), data.getCountry());
		} catch (IOException e) {
			log.error("unable to add a element to file; Data: %s", data);
		}
	};

	@Override
	public Artist create(Artist obj) {
		return CRUDImpl.create(obj, this.dataProvider.getArtistIDProvider(), this.dataProvider.getArtists(),
				this.fileIO, this.dataProvider.getArtistFilename(), this.writer);
	}

	@Override
	public Artist read(int id) {
		return CRUDImpl.read(id, this.dataProvider.getArtists());
	}

	@Override
	public boolean update(Artist obj) {
		return CRUDImpl.update(obj, this.dataProvider.getArtists(), this.fileIO, this.dataProvider.getArtistFilename(),
				this.writer);
	}

	@Override
	public boolean delete(int id) {
		return CRUDImpl.delete(id, this.dataProvider.getArtists());
	}

	@Override
	public List<Album> getAlbums(Artist artist) {
		return StreamEx.of(this.dataProvider.getRelation()).filter(x -> x.getSecond().equals(artist))
				.map(Tupel::getFirst).toList();
	}

	@Override
	public List<Tupel<Artist, Integer>> getLastReleaseDateOfEachArtist() {
		Map<Artist, Set<Album>> getAlbumsOfEachArtist = StreamEx.of(this.dataProvider.getRelation())
				.groupingBy(Tupel::getSecond, Collectors.mapping(Tupel::getFirst, Collectors.toSet()));

		return EntryStream.of(getAlbumsOfEachArtist).nonNullValues()
				.mapToValue((key, val) -> StreamEx.of(val).mapToInt(Album::getYear).min().orElse(-1))
				.mapKeyValue((key, val) -> new Tupel<>(key, val)).toList();
	}

	@Override
	public List<Artist> getArtistsWhichHaveNoReleases() {
		Set<Artist> haveRelation = StreamEx.of(this.dataProvider.getRelation()).map(Tupel::getSecond).distinct()
				.toSet();
		return StreamEx.of(this.dataProvider.getArtists().values()).filter(x -> haveRelation.contains(x) == false)
				.toList();
	}

	@Override
	public Artist getByName(String name) {
		return StreamEx.of(this.dataProvider.getArtists().values()).findFirst(x -> x.getName().equals(name))
				.orElse(null);
	}

}
