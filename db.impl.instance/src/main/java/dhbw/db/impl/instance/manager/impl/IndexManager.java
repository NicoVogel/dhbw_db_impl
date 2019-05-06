package dhbw.db.impl.instance.manager.impl;

import java.util.HashMap;
import java.util.Map;

import dhbw.db.impl.instance.model.Album;

public class IndexManager {

	private static int PERFORMENCE_FROM = 1990;
	private static int PERFORMENCE_TO = 2010;
	private Map<Integer, Long> index;

	public Map<Integer, Long> getIndex() {
		if (this.index == null) {
			this.index = new HashMap<>();
		}
		return this.index;
	}

	public void addIndex(Album result) {
		int year = result.getYear();

		if (isInYearRange(year)) {
			Long value = getIndex().containsKey(year) ? getIndex().get(year) : 0l;
			getIndex().put(year, value + 1);
		}
	}

	public void updateIndex(Album old, Album update) {
		if (old.getYear() != update.getYear()) {
			addIndex(update);
			removeIndex(old);
		}
	}

	public void removeIndex(Album result) {
		int year = result.getYear();

		if (isInYearRange(year)) {
			Long value = getIndex().containsKey(year) ? getIndex().get(year) : 0l;
			getIndex().put(year, value - 1);
		}
	}

	public boolean isInYearRange(int value) {
		return value >= PERFORMENCE_FROM && value <= PERFORMENCE_TO;
	}
}
