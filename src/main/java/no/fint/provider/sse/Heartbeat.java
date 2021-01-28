package no.fint.provider.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty("fint.provider.sse.heartbeat.enabled")
public class Heartbeat {
    @Autowired
    private SseService sseService;

    @Scheduled(initialDelay = 15000, fixedRateString = "${fint.provider.sse.heartbeat.interval:15000}")
    public void sendHeartbeat() {
        log.debug("\uD83D\uDC93");
        sseService.sendHeartbeat();
    }
}
