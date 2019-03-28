package dhbw.db.io;

import java.util.stream.Stream;

@FunctionalInterface
public interface StreamReadFileEval<TYP, RES> {

	RES eval(Stream<TYP> stream);
}
