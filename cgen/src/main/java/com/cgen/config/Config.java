package com.cgen.config;



import javax.xml.parsers.SAXParserFactory;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
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
	
	
	@Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
               
            }
        };
        tomcat.addAdditionalTomcatConnectors(getHttpConnector());
        return tomcat;
    }

    private Connector getHttpConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8082);
        connector.setSecure(false);
        connector.setRedirectPort(8089);
        return connector;
    }
    
	@Bean
    public SAXParserFactory saxDTD() {  		//disable DTD to prevent DTD

		 SAXParserFactory saxfac = SAXParserFactory.newInstance();
		  saxfac.setValidating(false);
		  try {
		    saxfac.setFeature("http://xml.org/sax/features/validation", false);
		    saxfac.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		    saxfac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		    saxfac.setFeature("http://xml.org/sax/features/external-general-entities", false);
		    saxfac.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		  }
		  catch (Exception e1) {
		    e1.printStackTrace();
		  }
		  
		  return saxfac;
		
    }
	

}
