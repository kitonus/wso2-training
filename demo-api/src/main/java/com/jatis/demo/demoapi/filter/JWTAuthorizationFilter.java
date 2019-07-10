package com.jatis.demo.demoapi.filter;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import com.jatis.demo.demoapi.constants.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter{

    private static final Logger log = LoggerFactory.getLogger(JWTAuthorizationFilter.class);
    
    private String tokenHeader;
    private UserDetailsService userDtlService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, String tokenHeader, 
    		UserDetailsService userDtlService) {
        super(authenticationManager);
        this.tokenHeader = tokenHeader;
        this.userDtlService = userDtlService;
    }
    
    private PublicKey pubKey;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, String tokenHeader, 
    		UserDetailsService userDtlService, PublicKey pubKey) {
        super(authenticationManager);
        this.pubKey = pubKey;
        this.tokenHeader = tokenHeader;
        this.userDtlService = userDtlService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        String header = request.getHeader(tokenHeader);
        
        if (StringUtils.isEmpty(header)) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
    

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        if (log.isDebugEnabled()) {
        	log.debug("******Token header "+tokenHeader+": "+token);
        }

        if (!StringUtils.isEmpty(token)) {
            try {
            	if (pubKey != null) {
            		
            		Jws<Claims> parsedToken = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(token);
            		
	                String username = parsedToken
		                    .getBody().get("http://wso2.org/claims/enduser", String.class);
	                username = username.substring(0, username.indexOf('@'));
	                

	                if (log.isDebugEnabled()) {
	                	log.debug("******username="+username);
	                }
	                UserDetails detls = userDtlService.loadUserByUsername(username);
	
	                if (!StringUtils.isEmpty(username)) {
	                    return new UsernamePasswordAuthenticationToken(username, null, detls.getAuthorities());
	                }
            	} else {
	                byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();
	
	                Jws<Claims> parsedToken = Jwts.parser()
	                    .setSigningKey(signingKey)
	                    .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX+" ", ""));
	
	                String username = parsedToken
	                    .getBody()
	                    .getSubject();
	
	                List<SimpleGrantedAuthority> authorities = ((List<?>) parsedToken.getBody()
	                    .get("rol")).stream()
	                    .map(authority -> new SimpleGrantedAuthority((String) authority))
	                    .collect(Collectors.toList());
	
	                if (!StringUtils.isEmpty(username)) {
	                    return new UsernamePasswordAuthenticationToken(username, null, authorities);
	                }
            	}
            } catch (ExpiredJwtException exception) {
                log.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
            } catch (UnsupportedJwtException exception) {
                log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
            } catch (MalformedJwtException exception) {
                log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
            } catch (SignatureException exception) {
                log.warn("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
            } catch (IllegalArgumentException exception) {
                log.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
            }
        }

        return null;
    }

}
