package id.ac.ui.cs.advprog.tableservicerizzerve;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
class TableServiceRizzerveApplicationTests {

	@Test
	void applicationContextTest() {
		assertDoesNotThrow(() -> {
			TableServiceRizzerveApplication.main(new String[] {"--server.port=8099"});
		});
	}
}