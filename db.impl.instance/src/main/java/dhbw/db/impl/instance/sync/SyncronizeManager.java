package dhbw.db.impl.instance.sync;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import dhbw.db.impl.instance.manager.FileManager;
import lombok.extern.slf4j.Slf4j;

@Service
@EnableBinding(Sink.class)
@Slf4j
public class SyncronizeManager implements SyncDBs {

	@Autowired
	private FileManager fm;

	@Autowired
	private SyncSource syncSource;

	private Set<UUID> myIDs = new HashSet<>();

	@StreamListener(target = Processor.INPUT)
	public void processReload(UUID id) {
		if (myIDs.remove(id) == false) {
			log.info("reload Data, sender {}", id);
			fm.reloadData();
		}
	}

	@Override
	public void sync() {
		UUID syncNum = UUID.randomUUID();
		this.myIDs.add(syncNum);
		log.info("send reload, id {}", syncNum);
		this.syncSource.dbUpdate().send(MessageBuilder.withPayload(syncNum).build());
	}

}
