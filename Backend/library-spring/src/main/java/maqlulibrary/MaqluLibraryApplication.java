package maqlulibrary;

import maqlulibrary.utilities.FillDB;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MaqluLibraryApplication {
	public MaqluLibraryApplication(FillDB fillDB) {
		this.fillDB = fillDB;
	}

	public static void main(String[] args) {
		SpringApplication.run(MaqluLibraryApplication.class, args);
	}

	final
	FillDB fillDB;

	@Bean
	CommandLineRunner runner(){
		return args -> fillDB.FillDB();
	}

}
