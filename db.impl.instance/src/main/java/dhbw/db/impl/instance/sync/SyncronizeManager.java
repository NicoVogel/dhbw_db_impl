package dhbw.db.impl.instance.sync;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import dhbw.db.impl.instance.manager.FileManager;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SyncronizeManager implements SyncDBs {

	@Autowired
	private RabbitTemplate template;

	@Autowired
	private FanoutExchange fanout;

	@Value("${jsa.rabbitmq.exchange}")
	private String exchange;

	@Autowired
	private FileManager fm;

	private Set<UUID> myIDs = new HashSet<>();

	@Override
	public void sync() {
		UUID syncNum = UUID.randomUUID();
		this.myIDs.add(syncNum);
		this.template.convertAndSend(fanout.getName(), "", syncNum);
		log.info("send reload, id {}", syncNum);
	}

	@RabbitListener(queues = "#{firstQueue.name}")
	public void processReload(UUID id) {
		if (myIDs.remove(id) == false) {
			log.info("reload Data, sender {}", id);
			fm.reloadData();
		}
	}

	@Bean
	public Queue firstQueue() {
		return new Queue("jsa.queue.1");
	}

	@Bean
	public FanoutExchange fanout() {
		return new FanoutExchange("tut.fanout");
	}

}
