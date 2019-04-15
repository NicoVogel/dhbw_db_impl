package dhbw.db.impl.instance;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.stereotype.Service;

@Service
@EnableBinding(Source.class)
public class SyncronizeManager {

}
