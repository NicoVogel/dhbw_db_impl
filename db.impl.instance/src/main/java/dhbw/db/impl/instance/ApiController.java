package dhbw.db.impl.instance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ApiController {

	@Value("${eureka.instance.instance-id}")
	private String instanceId;

	@GetMapping
	public String getTest() {
		return "response from " + instanceId;
	}

}
