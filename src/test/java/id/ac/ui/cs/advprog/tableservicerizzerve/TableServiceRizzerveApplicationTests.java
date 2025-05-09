package id.ac.ui.cs.advprog.tableservicerizzerve;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TableServiceRizzerveApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void applicationContextTest() {
		TableServiceRizzerveApplication.main(new String[] {});
	}
}
