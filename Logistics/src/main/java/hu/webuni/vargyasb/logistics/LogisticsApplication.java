package hu.webuni.vargyasb.logistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hu.webuni.vargyasb.logistics.service.InitDbService;

@SpringBootApplication
public class LogisticsApplication implements CommandLineRunner{

	@Autowired
	InitDbService initDbService;
	
	public static void main(String[] args) {
		SpringApplication.run(LogisticsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		initDbService.clearDB();
		initDbService.insertTestData();
	}

}
