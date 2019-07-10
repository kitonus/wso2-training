package com.jatis.demo.demoapi;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.junit.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class TestJWTWithRSA {

	@Test
	public void test() throws Exception {
//		String publicKeyString = 
//				"MIIDSTCCAjGgAwIBAgIEAoLQ/TANBgkqhkiG9w0BAQsFADBVMQswCQYDVQQGEwJV" + 
//				"UzELMAkGA1UECBMCQ0ExFjAUBgNVBAcTDU1vdW50YWluIFZpZXcxDTALBgNVBAoT" + 
//				"BFdTTzIxEjAQBgNVBAMTCWxvY2FsaG9zdDAeFw0xNzA3MTkwNjUyNTFaFw0yNzA3" + 
//				"MTcwNjUyNTFaMFUxCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJDQTEWMBQGA1UEBxMN" + 
//				"TW91bnRhaW4gVmlldzENMAsGA1UEChMEV1NPMjESMBAGA1UEAxMJbG9jYWxob3N0" + 
//				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAluZFdW1ynitztkWLC6xK" + 
//				"egbRWxky+5P0p4ShYEOkHs30QI2VCuR6Qo4Bz5rTgLBrky03W1GAVrZxuvKRGj9V" + 
//				"9+PmjdGtau4CTXu9pLLcqnruaczoSdvBYA3lS9a7zgFU0+s6kMl2EhB+rk7gXluE" + 
//				"ep7lIOenzfl2f6IoTKa2fVgVd3YKiSGsyL4tztS70vmmX121qm0sTJdKWP4HxXyq" + 
//				"K9neolXI9fYyHOYILVNZ69z/73OOVhkh/mvTmWZLM7GM6sApmyLX6OXUp8z0pkY+" + 
//				"vT/9+zRxxQs7GurC4/C1nK3rI/0ySUgGEafO1atNjYmlFN+M3tZX6nEcA6g94Iav" + 
//				"yQIDAQABoyEwHzAdBgNVHQ4EFgQUtS8kIYxQ8UVvVrZSdgyide9OHxUwDQYJKoZI" + 
//				"hvcNAQELBQADggEBABfk5mqsVUrpFCYTZZhOxTRRpGXqoW1G05bOxHxs42Paxw8r" + 
//				"AJ06Pty9jqM1CgRPpqvZa2lPQBQqZrHkdDE06q4NG0DqMH8NT+tNkXBe9YTre3EJ" + 
//				"CSfsvswtLVDZ7GDvTHKojJjQvdVCzRj6XH5Truwefb4BJz9APtnlyJIvjHk1hdoz" + 
//				"qyOniVZd0QOxLAbcdt946chNdQvCm6aUOputp8Xogr0KBnEy3U8es2cAfNZaEkPU" + 
//				"8Va5bU6Xjny8zGQnXCXxPKp7sMpgO93nPBt/liX1qfyXM7xEotWoxmm6HZx8oWQ8" + 
//				"U5aiXjZ5RKDWCCq4ZuXl6wVsUz1iE61suO5yWi8=";
		String jwsEncodedPayload = 
				"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Ik5UQXhabU14TkRNeVpEZzNNVFUxWkdNME16RXpPREpoWldJNE5ETmxaRFUxT0dGa05qRmlNUSJ9.eyJhdWQiOiJodHRwOlwvXC9vcmcud3NvMi5hcGltZ3RcL2dhdGV3YXkiLCJzdWIiOiJhZG1pbiIsImFwcGxpY2F0aW9uIjp7ImlkIjoxLCJuYW1lIjoiRGVmYXVsdEFwcGxpY2F0aW9uIiwidGllciI6IlVubGltaXRlZCIsIm93bmVyIjoiYWRtaW4ifSwic2NvcGUiOiJhbV9hcHBsaWNhdGlvbl9zY29wZSBkZWZhdWx0IiwiaXNzIjoiaHR0cHM6XC9cL2xvY2FsaG9zdDo5NDQzXC9vYXV0aDJcL3Rva2VuIiwia2V5dHlwZSI6IlBST0RVQ1RJT04iLCJzdWJzY3JpYmVkQVBJcyI6W3sibmFtZSI6IlBpenphU2hhY2tBUEkiLCJjb250ZXh0IjoiXC9waXp6YXNoYWNrXC8xLjAuMCIsInZlcnNpb24iOiIxLjAuMCIsInB1Ymxpc2hlciI6ImFkbWluIiwic3Vic2NyaXB0aW9uVGllciI6IlVubGltaXRlZCIsInN1YnNjcmliZXJUZW5hbnREb21haW4iOiJjYXJib24uc3VwZXIifSx7Im5hbWUiOiJwaG9uZXZlcmlmeSIsImNvbnRleHQiOiJcL3Bob25ldmVyaWZ5XC8yLjAuMCIsInZlcnNpb24iOiIyLjAuMCIsInB1Ymxpc2hlciI6ImFkbWluIiwic3Vic2NyaXB0aW9uVGllciI6IlVubGltaXRlZCIsInN1YnNjcmliZXJUZW5hbnREb21haW4iOiJjYXJib24uc3VwZXIifSx7Im5hbWUiOiJwaG9uZXZlcmlmeSIsImNvbnRleHQiOiJcL3Bob25ldmVyaWZ5XC8zLjAuMCIsInZlcnNpb24iOiIzLjAuMCIsInB1Ymxpc2hlciI6ImFkbWluIiwic3Vic2NyaXB0aW9uVGllciI6IlVubGltaXRlZCIsInN1YnNjcmliZXJUZW5hbnREb21haW4iOiJjYXJib24uc3VwZXIifSx7Im5hbWUiOiJqYXRpcy1kZW1vLWFwaSIsImNvbnRleHQiOiJcL2phdGlzLWRlbW8tYXBpXC8yLjAiLCJ2ZXJzaW9uIjoiMi4wIiwicHVibGlzaGVyIjoiYWRtaW4iLCJzdWJzY3JpcHRpb25UaWVyIjoiVW5saW1pdGVkIiwic3Vic2NyaWJlclRlbmFudERvbWFpbiI6ImNhcmJvbi5zdXBlciJ9XSwiY29uc3VtZXJLZXkiOiJyS2YxMVJwbWtzUVlHQlh0ZFI5RVlSZEVob1VhIiwiZXhwIjoxNTU1MzIxOTE3LCJpYXQiOjE1NTUzMTgzMTc0MjMsImp0aSI6IjgyYzZkNWI5LWJjZTAtNDkzMi1hZTcwLWNiYjRiZmU5YzRmYSJ9.MWZi3ludcGtWfbCT130yaWLQiseuj0YkgKuX6HNeCSEeUzC37AMKPkZDB1QrZo9vchq4orUJkR03ToeQxp-9AOKOcaXdIpRWG0YNr5Gr6AiN6KZh55J46WZXg3zApkVSpMbAfYGiNQvPR-wsdJZoQhAqlYDP6Y47ZagyA2pSTfaO-JGB472aZIxLpEDlde_zNpLivUotlq_SX2j8Vh3dg4pSJ1ppY1iBuDnQODBfWOUjcGe5h4FP1KcAkR9rXJ_swbvjXJseugeQlQtROpE8ch4tkZ4MsWieh4qfsjDPLtW-SmxF9z7xjCSXPv4aMJxBEttvMezIoP9W6OxzTvyYtA==";
//
//		
//		KeyFactory kf = KeyFactory.getInstance("RSA");
//		byte[] byteKey = Base64.getDecoder().decode(publicKeyString);
//		X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
//		PublicKey pub = kf.generatePublic(X509publicKey);
//
//		Jws<Claims> token = Jwts.parser().setSigningKey(pub).parseClaimsJws(jwsEncodedPayload);
//		System.out.println(token.getBody());
		FileInputStream fis = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			fis = new FileInputStream("/home/hwicaksono/wso2.crt");
			
			Certificate cert = cf.generateCertificate(fis);
			
			PublicKey pubKey = cert.getPublicKey();
			
			Jws<Claims> token = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(jwsEncodedPayload);
			
			System.out.println(token.getBody());
		} finally {
			fis.close();
		}
		
	}
}
