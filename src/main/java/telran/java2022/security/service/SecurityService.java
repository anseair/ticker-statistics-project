package telran.java2022.security.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityService {
	
	@Bean
	public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.httpBasic();
		httpSecurity.csrf().disable();
		httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		httpSecurity.addFilterBefore(new CustomFilter(), BasicAuthenticationFilter.class);
		httpSecurity.authorizeRequests(authorize -> authorize
				.mvcMatchers("/account/register/**", "/financials/ticker/*/*/**", "/financials/tickers/**").permitAll()
				.mvcMatchers("/account/user/*/role/*/**", "/financials/download/*/**", "/financials/ticker/**").hasRole("ADMINISTRATOR")
				.mvcMatchers(HttpMethod.DELETE, "/financials/*/**", "/statistics/*/*/**").hasRole("ADMINISTRATOR")
				.mvcMatchers(HttpMethod.PUT, "/financials/*/*/**").hasRole("ADMINISTRATOR")
				.mvcMatchers(HttpMethod.PUT, "/account/user/{login}*").access("#login == authentication.name")
				.mvcMatchers(HttpMethod.DELETE, "/account/user/{login}*").access("#login == authentication.name or hasRole('ADMINISTRATOR')")
				.mvcMatchers(HttpMethod.PUT, "/account/changePassword/user/{login}*").access("#login == authentication.name")
				.anyRequest().authenticated());
		return httpSecurity.build();
		
	}

}
