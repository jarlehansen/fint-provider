package no.fint.provider.subscriber;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InputSubscriber {

    @Value("${my-test}")
    private String myTest;

    public void receive(Map<String, String> headers, byte[] body) {
        System.out.println("myTest: " + myTest);

        System.out.println("Headers: " + headers.size());
        System.out.println("Body: " + new String(body));
    }

}
