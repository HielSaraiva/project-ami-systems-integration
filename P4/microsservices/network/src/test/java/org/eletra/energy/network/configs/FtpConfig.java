package org.eletra.energy.network.configs;

import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@TestConfiguration
public class FtpConfig {

    @Value("${ftp.port:0}")
    private int ftpPort;

    @Bean
    public FtpServer ftpServer() throws Exception {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();

        listenerFactory.setPort(ftpPort);

        DataConnectionConfigurationFactory dataConnFactory = new DataConnectionConfigurationFactory();
        dataConnFactory.setPassivePorts("50100-50200");
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

    @Bean
    public DefaultFtpSessionFactory ftpSessionFactory(FtpServer ftpServer) {
        int actualPort = ((DefaultFtpServer) ftpServer).getListener("default").getPort();
        DefaultFtpSessionFactory sf = new DefaultFtpSessionFactory();
        sf.setHost("localhost");
        sf.setPort(actualPort);
        sf.setUsername("ftp_server");
        sf.setPassword("ftp_server");
        sf.setClientMode(2);
        return sf;
    }
}
