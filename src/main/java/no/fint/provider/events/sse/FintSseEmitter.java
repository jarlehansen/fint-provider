package no.fint.provider.events.sse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class FintSseEmitter extends SseEmitter {
    private String id;
    private String client;
    private String registered;
    private final AtomicInteger eventCounter = new AtomicInteger();

    public FintSseEmitter() {
        setRegisteredDate();
    }

    public FintSseEmitter(String id, String client, long timeout) {
        super(timeout);
        this.id = id;
        this.client = client;
        setRegisteredDate();
    }

    private void setRegisteredDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        registered = formatter.format(new Date());
    }

	@Override
	public void send(SseEventBuilder builder) throws IOException {
		eventCounter.incrementAndGet();
		super.send(builder);
	}
    
    
}
