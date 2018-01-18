package no.fint.provider.events;

import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
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
        return new com.hazelcast.config.Config().setNetworkConfig(new NetworkConfig().setJoin(new JoinConfig().setTcpIpConfig(new TcpIpConfig().setMembers(Arrays.asList(members.split(","))).setEnabled(true)).setMulticastConfig(new MulticastConfig().setEnabled(false))));
    }

}
