package first.backtest.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;

import java.util.Collections;

@Configuration
public class HttpToHttpsRedirectConfig {

    // HTTP 요청을 HTTPS로 리다이렉트하는 설정
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

        // HTTP 포트 추가 (예: 8080)
        tomcat.addAdditionalTomcatConnectors(httpConnector());

        return tomcat;
    }

    private org.apache.catalina.connector.Connector httpConnector() {
        org.apache.catalina.connector.Connector connector =
                new org.apache.catalina.connector.Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);

        connector.setScheme("http");
        connector.setPort(8080); // 원래 사용하던 포트
        connector.setSecure(false);
        connector.setRedirectPort(8443); // HTTPS 포트로 리다이렉트

        return connector;
    }
}
