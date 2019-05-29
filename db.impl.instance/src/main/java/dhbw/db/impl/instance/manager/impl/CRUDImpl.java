package dhbw.db.impl.instance.manager.impl;

import java.util.Map;

import dhbw.db.impl.instance.controller.error.DataNotFoundException;
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

	public static <T extends Updater<T> & Identifier> boolean update(T value, Class<T> type, Map<Integer, T> objects,
			CsvManager fileIO, String filename, WriteConvert<T> writer) {
		return update(value, type, objects, fileIO, filename, writer, read(value.getId(), objects));
	}

	public static <T extends Updater<T> & Identifier> boolean update(T value, Class<T> type, Map<Integer, T> objects,
			CsvManager fileIO, String filename, WriteConvert<T> writer, T original) {

		if (original.update(value) == false) {
			throw new DataNotFoundException(
					String.format("update failed, no %s found for ID %d.", type.getName(), value.getId()), type,
					value.getId());
		}
		// return fileIO.appendLine(filename, data, writeConverter)
		return fileIO.updateLines(filename, original, objects.values(), writer);
	}

	public static <T extends Identifier> T read(int id, Map<Integer, T> objects) {
		return objects.get(id);
	}

	public static <T> T delete(int id, Class<T> type, Map<Integer, T> objects, CsvManager fileIO, String filename,
			WriteConvert<T> writer) {
		T obj = objects.get(id);
		if (obj == null) {
			throw new DataNotFoundException(String.format("delete failed, no %s found for ID %d.", type.getName(), id),
					type, id);
		}
		T previous = null;
		for (T cur : objects.values()) {
			if (cur == obj)
				break;
			previous = cur;
		}
		objects.remove(obj);

		if (fileIO.updateLines(filename, previous, objects.values(), writer) == false) {
			throw new DataNotFoundException("There was an error while writing the file", type, id);
		}
		return obj;
	}
}
