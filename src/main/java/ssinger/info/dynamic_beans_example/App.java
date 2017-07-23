package ssinger.info.dynamic_beans_example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * Hello world!
 *
 */
@Configuration
@SpringBootApplication
public class App implements CommandLineRunner {

	
	@Autowired 
	TestBean testBean;

    public static void main( String[] args )
    {
        SpringApplication.run(App.class);
        
    }

	@Override
	public void run(String... arg0) throws Exception {
		testBean.doStuff();
		
	}

    
    @Bean
    TestBean testBean() {
    	return new TestBean();
    }
   
    
}

