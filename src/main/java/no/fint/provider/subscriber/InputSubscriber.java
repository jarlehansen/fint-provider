package no.fint.provider.subscriber;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InputSubscriber {

    public void receive(Map<String, String> headers, byte[] body) {
        System.out.println("Headers: " + headers.size());
        System.out.println("Body: " + new String(body));
    }

}
