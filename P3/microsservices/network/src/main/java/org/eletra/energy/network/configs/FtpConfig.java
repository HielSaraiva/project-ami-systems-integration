    package org.eletra.energy.network.configs;

    import org.apache.ftpserver.DataConnectionConfigurationFactory;
    import org.apache.ftpserver.FtpServer;
    import org.apache.ftpserver.FtpServerFactory;
    import org.apache.ftpserver.ftplet.Authority;
    import org.apache.ftpserver.ftplet.FtpException;
    import org.apache.ftpserver.listener.ListenerFactory;
    import org.apache.ftpserver.usermanager.impl.BaseUser;
    import org.apache.ftpserver.usermanager.impl.WritePermission;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.ArrayList;
    import java.util.List;

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

        @Bean
        public FtpServer ftpServer() throws Exception {
            FtpServerFactory serverFactory = new FtpServerFactory();

            ListenerFactory listenerFactory = new ListenerFactory();
            listenerFactory.setPort(21);

            DataConnectionConfigurationFactory dataConnFactory = new DataConnectionConfigurationFactory();
            dataConnFactory.setPassivePorts("50000-50100");
            listenerFactory.setDataConnectionConfiguration(dataConnFactory.createDataConnectionConfiguration());

            serverFactory.addListener("default", listenerFactory.createListener());

            BaseUser user = new BaseUser();
            user.setName("ftp_server");
            user.setPassword("ftp_server");

            Path home = Paths.get(System.getProperty("user.home"), "ftp");
            Files.createDirectories(home);
            user.setHomeDirectory(home.toAbsolutePath().toString());

            List<Authority> authorities = new ArrayList<>();
            authorities.add(new WritePermission());
            user.setAuthorities(authorities);

            serverFactory.getUserManager().save(user);

            FtpServer server = serverFactory.createServer();
            server.start();
            return server;
        }
    }
