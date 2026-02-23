package in.tech_camp.protospace;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestDatabaseConfiguration.class)
class ProtospaceApplicationTests {

	@Test
	void contextLoads() {
	}

}
