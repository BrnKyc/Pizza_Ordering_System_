package whz.pti.eva.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityFilterChainLogger implements CommandLineRunner {

	private final ApplicationContext applicationContext;

	public SecurityFilterChainLogger(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void run(String... args) throws Exception {
		// Zugriff auf die SecurityFilterChain-Bean
		SecurityFilterChain securityFilterChain = applicationContext.getBean(SecurityFilterChain.class);

		// Ausgabe der Filter innerhalb der SecurityFilterChain
		System.out.println("Aktuelle SecurityFilterChain-Filter:");
		securityFilterChain.getFilters().forEach(filter -> System.out.println(filter.getClass().getName()));
	}
}

