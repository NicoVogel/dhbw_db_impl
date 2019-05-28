package dhbw.db.impl.instance.sync;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SyncSource {

	@Output("dbUpdateChannel")
	MessageChannel dbUpdate();

}