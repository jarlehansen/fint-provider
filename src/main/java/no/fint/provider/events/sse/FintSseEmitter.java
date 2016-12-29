package no.fint.provider.events.sse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class FintSseEmitter extends SseEmitter {
    private String id;
    private String registered;

    public FintSseEmitter() {
        setRegisteredDate();
    }

    public FintSseEmitter(String id, long timeout) {
        super(timeout);
        this.id = id;
        setRegisteredDate();
    }

    private void setRegisteredDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        registered = formatter.format(new Date());
    }
}
