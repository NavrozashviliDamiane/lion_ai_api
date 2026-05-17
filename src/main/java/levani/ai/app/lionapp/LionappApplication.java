package levani.ai.app.lionapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories
public class LionappApplication {

	public static void main(String[] args) {
		SpringApplication.run(LionappApplication.class, args);
	}

}
