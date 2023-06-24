package telran.java2022.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityService {
	
	@Bean
	public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.httpBasic();
		httpSecurity.csrf().disable();
		httpSecurity.cors();
		httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		httpSecurity.authorizeRequests(authorize -> authorize
					.mvcMatchers("/financials/ticker/*/*/**", "/financials/tickers/**", "/financials/lastPrices/**", "/account/register/**", "/account/resetPassword/**").permitAll()
					.mvcMatchers("/financials/download/**", "/financials/add/ticker/**", "/financials/update/all/**", "/account/changeRole/**").hasRole("ADMINISTRATOR")
					.mvcMatchers(HttpMethod.DELETE, "/financials/**").hasRole("ADMINISTRATOR")
					.mvcMatchers(HttpMethod.PUT, "/financials/**").hasRole("ADMINISTRATOR")
					.mvcMatchers(HttpMethod.PUT, "/account/user/{login}/***").access("#login == authentication.name")
					.mvcMatchers(HttpMethod.DELETE, "/account/delete/{login}/**").access("#login == authentication.name or hasRole('ADMINISTRATOR')")
					.mvcMatchers(HttpMethod.PUT, "/account/changePassword/user/{login}/**").access("#login == authentication.name")
					.anyRequest().authenticated());
		return httpSecurity.build();
	}
}
