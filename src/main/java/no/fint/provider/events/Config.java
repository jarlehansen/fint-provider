package no.fint.provider.events;

import com.hazelcast.config.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class Config {

    @Value("${fint.hazelcast.members}")
    private String members;

    @Bean
    public com.hazelcast.config.Config hazelcastConfig() {
        com.hazelcast.config.Config cfg = new ClasspathXmlConfig("fint-hazelcast.xml");
        return cfg.setNetworkConfig(new NetworkConfig().setJoin(new JoinConfig().setTcpIpConfig(new TcpIpConfig().setMembers(Arrays.asList(members.split(","))).setEnabled(true)).setMulticastConfig(new MulticastConfig().setEnabled(false))));
    }

}
