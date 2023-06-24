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
					.mvcMatchers("/account/register/**", "/financials/ticker/*/*/**", "/financials/tickers/**", "/account/resetPassword/**").permitAll()
					.mvcMatchers("/financials/download/**", "/financials/add/ticker/**", "/financials/update/all").hasRole("administrator")
					.mvcMatchers(HttpMethod.DELETE, "/financials/**").hasRole("administrator")
					.mvcMatchers(HttpMethod.PUT, "/financials/**").hasRole("administrator")
					.mvcMatchers(HttpMethod.DELETE, "/account/changeRole/**").hasRole("administrator")
					.mvcMatchers(HttpMethod.PUT, "/account/changeRole/**").hasRole("administrator")

					.mvcMatchers(HttpMethod.PUT, "/account/user/{login}/***").access("#login == authentication.name")
					.mvcMatchers(HttpMethod.DELETE, "/account/delete/{login}/**").access("#login == authentication.name or hasRole('administrator')")
					.mvcMatchers(HttpMethod.PUT, "/account/changePassword/user/{login}/**").access("#login == authentication.name")
					.anyRequest().authenticated());
		return httpSecurity.build();
	}
}
