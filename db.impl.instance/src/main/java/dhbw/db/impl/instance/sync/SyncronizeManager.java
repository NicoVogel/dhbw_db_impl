package dhbw.db.impl.instance.sync;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import dhbw.db.impl.instance.manager.FileManager;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SyncronizeManager implements SyncDBs {

	private static final String EXCHANGE_NAME = "sync.fanout";
	private static final String QUEUE_NAME = "sync.queue";

	@Autowired
	private RabbitTemplate template;

	@Autowired
	private FileManager fm;

	private Set<UUID> myIDs = new HashSet<>();

	@Override
	public void sync() {
		UUID syncNum = UUID.randomUUID();
		this.myIDs.add(syncNum);
		this.template.convertAndSend(EXCHANGE_NAME, "", syncNum);
		log.info("send reload, id {}", syncNum);
	}

	@RabbitListener(queues = "sync.queue")
	public void processReload(UUID id) {
		log.info("receive rabbit messsage");
		if (this.myIDs.remove(id) == false) {
			log.info("reload Data, sender {}", id);
			fm.reloadData();
		}
	}

	@Bean
	public Queue firstQueue() {
		return new Queue(QUEUE_NAME);
	}

	@Bean
	public FanoutExchange fanout() {
		return new FanoutExchange(EXCHANGE_NAME);
	}

}
