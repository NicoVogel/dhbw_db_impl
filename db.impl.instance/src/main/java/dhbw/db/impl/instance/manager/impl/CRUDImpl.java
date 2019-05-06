package dhbw.db.impl.instance.manager.impl;

import java.util.Map;

import dhbw.db.impl.instance.io.CsvManager;
import dhbw.db.impl.instance.io.WriteConvert;
import dhbw.db.impl.instance.model.Identifier;
import dhbw.db.impl.instance.model.Updater;

// Total hours wasted here: 1

public class CRUDImpl {

	public static <T extends Identifier> T create(T obj, IDProvider provider, Map<Integer, T> objects,
			CsvManager fileIO, String filename, WriteConvert<T> writer) {

		obj.setId(provider.getNextID());
		objects.put(obj.getId(), obj);
		if (fileIO.appendLine(filename, obj, writer)) {
			return obj;
		}
		return null;
	}

	public static <T extends Updater<T> & Identifier> boolean update(T value, Map<Integer, T> objects,
			CsvManager fileIO, String filename, WriteConvert<T> writer) {
		return update(value, objects, fileIO, filename, writer, read(value.getId(), objects));
	}

	public static <T extends Updater<T> & Identifier> boolean update(T value, Map<Integer, T> objects,
			CsvManager fileIO, String filename, WriteConvert<T> writer, T original) {

		if (!original.update(value)) {
			return false;
		}
		return fileIO.appendLine(filename, original, writer);
	}

	public static <T extends Identifier> T read(int id, Map<Integer, T> objects) {
		return objects.get(id);
	}

	public static <T> T delete(int id, Map<Integer, T> objects) {
		return objects.remove(id);
	}
}
