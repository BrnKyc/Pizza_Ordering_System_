package whz.pti.eva.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import whz.pti.eva.security.service.CustomAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity (debug = true)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/h2-console/**").requestMatchers("/console/**");
	}
	
	
    @Bean
 	PasswordEncoder passwordEncoder() {
 		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
 	}
	

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http.csrf(csrf -> csrf.disable());
		http.authorizeHttpRequests(
				authorize -> authorize
						.requestMatchers("/register/**", "/", "/pizzas","/home", "/css/**", "/js/**" ,"/images/**").permitAll()
						.requestMatchers("/cart/**", "/customer/**","/order/**","pizza")
						.authenticated()
						.requestMatchers("/users/**").hasAuthority("ADMIN")
						.requestMatchers("/admin/**").hasAuthority("ADMIN")
						.anyRequest()
						.authenticated())
				.formLogin(formLogin -> formLogin
						.successHandler(new CustomAuthenticationSuccessHandler())
						.loginPage("/login")
						.loginProcessingUrl("/login")
						.defaultSuccessUrl("/role-based-redirect", true)
						.failureUrl("/login?error")
						.permitAll())
				.logout(logout -> logout
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.logoutUrl("/logout") 
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID"))
				.rememberMe(rememberMe -> rememberMe.tokenValiditySeconds(1209600));

		return http.build();
	}
    

    
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder());
//    }
}
