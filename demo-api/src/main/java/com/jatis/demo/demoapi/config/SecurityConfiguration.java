package com.jatis.demo.demoapi.config;

import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.jatis.demo.demoapi.filter.JWTAuthenticationFilter;
import com.jatis.demo.demoapi.filter.JWTAuthorizationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	private final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

	private PublicKey pubKey;
	private String tokenHeader;
	
	public SecurityConfiguration(@Value("${jwt.signature.cert.der:N/A}") String jwtSignatureCertDer,
			@Value("${jwt.token.header:Authorization}") String tokenHeader) {
		this.tokenHeader = tokenHeader;
		logger.info("******Using "+tokenHeader+" token header");
		if ("N/A".equalsIgnoreCase(jwtSignatureCertDer)) {
			logger.info("******NOT using public key for JWT signature verification******");
			return;
		}
		try (InputStream is = ResourceUtils.getURL(jwtSignatureCertDer).openStream()){
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			
			Certificate cert = cf.generateCertificate(is);
			
			pubKey = cert.getPublicKey();
			logger.info("******USING public key ("+jwtSignatureCertDer+")for JWT signature verification******");
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Failed loading certificate DER file", e);
		}
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
            .httpBasic().disable()
            .formLogin().disable()
            .authorizeRequests()
            .antMatchers("/swagger-ui.html/**").permitAll()
            .antMatchers("/swagger-resources/**").permitAll()
            .antMatchers("/csrf").permitAll()
            .antMatchers("/").permitAll()
            .antMatchers("/v2/api-docs").permitAll()           
            .antMatchers("/webjars/**").permitAll()
            .antMatchers("/api/public").permitAll()
            .antMatchers("/api/authenticate").permitAll()
            .anyRequest().authenticated()
            .and()
            .csrf().disable()
            .addFilter(new JWTAuthenticationFilter(authenticationManager(), tokenHeader))
            .addFilter(new JWTAuthorizationFilter(authenticationManager(), tokenHeader, this.userDetailsService, this.pubKey))
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
    
    private PasswordEncoder passwordEncoder;
    
    
    private UserDetailsService userDetailsService;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("user")
            .password(passwordEncoder().encode("password"))
            .authorities("ROLE_USER")
            .and()
            .withUser("admin")
            .password(passwordEncoder().encode("passwordadmin"))
            .authorities("ROLE_USER", "ROLE_ADMIN");
        
        this.userDetailsService = auth.getDefaultUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
    	if (this.passwordEncoder == null) {
    		this.passwordEncoder = new BCryptPasswordEncoder();
    	}
        return this.passwordEncoder;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());

        return source;
    }
}
