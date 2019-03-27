package dhbw.db.io;

@FunctionalInterface
 interface StremReadCommand<T> {

	boolean evaluate(T object);
}
