    package org.eletra.energy.network.configs;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

    @Configuration
    public class FtpConfig {

        @Bean
        public DefaultFtpSessionFactory ftpSessionFactory() {
            DefaultFtpSessionFactory sf = new DefaultFtpSessionFactory();
            sf.setHost("localhost");
            sf.setPort(21);
            sf.setUsername("ftp_server");
            sf.setPassword("ftp_server");
            sf.setClientMode(2);
            return sf;
        }
    }
