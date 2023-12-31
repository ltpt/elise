package com.poly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.poly.utils.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthConfig {

	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	@Autowired
	UserService userService;


	/* Quản lý dữ liệu người sử dụng */
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService);
	}
	
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		// CSRF, CORS
		http.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.disable());
		// Phân quyền sử dụng
		http.authorizeRequests(auth -> auth
				.antMatchers("/elise/rest/authorities", "/elise/rest/authorities/**",
						"/elise/rest/reports/**")
					.hasRole("MAN")
				.antMatchers("/elise/rest/accounts", "/elise/rest/accounts/**",
						"/elise/rest/collections", "/elise/rest/collections/**",
						"/elise/rest/upload/**")
					.hasAnyRole("MAN", "STA")
				.antMatchers("/elise/account/**",
						"/elise/rest/orders", "/elise/rest/orders/**")
					.authenticated()
				.anyRequest().permitAll() // anonymous
		);

		// Điều khiển lỗi truy cập không đúng role
		http.exceptionHandling(handler -> handler.accessDeniedPage("/elise/access/denied")); // error

		// Đăng nhập
		http.formLogin((form) -> form.loginPage("/elise/login") // uri get login page
				.loginProcessingUrl("/login") // form action, mặc định '/login'
				.defaultSuccessUrl("/elise/login/success", false)
				.failureUrl("/elise/login/error")
				.usernameParameter("username") // mặc định 'username'
				.passwordParameter("password") // mặc định 'password'
		);
		http.rememberMe((remember) -> remember.rememberMeParameter("remember")); // mặc định 'remember-me'

		// Đăng xuất
		http.logout((logout) -> logout.logoutUrl("/elise/account/logoff") // mặc định '/logout'
				.logoutSuccessUrl("/elise/logoff/success"));

		// OAuth2- Đăng nhâp từ mang xã hôi
		http.oauth2Login((oauth) -> oauth
				.loginPage("/elise/login")
				.defaultSuccessUrl("/elise/oauth2/login/success", true)
				.failureUrl("/elise/login/error")
				.authorizationEndpoint().baseUri("/elise/oauth2/authorization")
		);
		return http.build();
	}

}
