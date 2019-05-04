package dhbw.db.impl.instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.stereotype.Service;

import dhbw.db.impl.instance.manager.FileManager;

@Service
@EnableBinding(Source.class)
public class SyncronizeManager {

	@Autowired
	private FileManager fm;

}
