package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	//지연로딩 세팅. 엔티티를 직접 노출하지 않으니, 실사용은 안함
	@Bean
	Hibernate5Module hibernate5Module() {
		//지연로딩 null
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		//지연로딩 실행 강제세팅
//		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return hibernate5Module;
	}
}
