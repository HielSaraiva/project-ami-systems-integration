package org.eletra.energy.network.configs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

@TestConfiguration
public class FtpConfig {

    @Bean
    @Primary
    public DefaultFtpSessionFactory defaultFtpSessionFactory(Environment env) {
        DefaultFtpSessionFactory sf = new DefaultFtpSessionFactory();
        sf.setHost(env.getRequiredProperty("test.ftp.host"));
        sf.setPort(Integer.parseInt(env.getRequiredProperty("test.ftp.port")));
        sf.setUsername(env.getRequiredProperty("test.ftp.user"));
        sf.setPassword(env.getRequiredProperty("test.ftp.pass"));
        sf.setClientMode(Integer.parseInt(env.getRequiredProperty("test.ftp.clientMode")));
        return sf;
    }
}


