package com.example.Login.Services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtEncodingException;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import com.example.Login.Entity.User;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

//https://www.baeldung.com/java-secret-key-to-string
//https://connect2id.com/products/nimbus-jose-jwt/examples/jwk-generation#oct

@Service
public class TokenService {
	
	final Logger LOGGER = LogManager.getLogger(getClass());
	
	private String salt = "1234";
	
	public TokenService() {}
	
	//generate a secret key with user's uuid and salt
	private SecretKey getKeyFromPassword(String uuid) throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(
				uuid.toCharArray(),
				salt.getBytes(),
				65536,
				256);
		return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
	}

	public String generateToken(Authentication authentication) throws JwtEncodingException, NoSuchAlgorithmException, InvalidKeySpecException {
		Instant now  = Instant.now();
		String scope = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));
		
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("self")
				.issuedAt(now)
				.expiresAt(now.plus(24, ChronoUnit.HOURS)) //expires in 1 day
				.subject(authentication.getName())
				.claim("scope", scope)
				.build();

		User user = (User) authentication.getPrincipal();
		JWK jwk = new OctetSequenceKey.Builder(getKeyFromPassword(user.getUuid().toString())).build(); //generate token with uuid
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		JwtEncoder encoder = new NimbusJwtEncoder(jwks);
		
		return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

}
