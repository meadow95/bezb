package com.cgen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cgen.certificate.CertificateGenerator;
import com.cgen.repository.KSRepository;


@Configuration
public class Config {

	@Bean
	public CertificateGenerator certificateGenerator() {
		return new CertificateGenerator();
	}
	
	@Bean
	public KSRepository keyStoreRepository() {
		return new KSRepository();
	}

}
