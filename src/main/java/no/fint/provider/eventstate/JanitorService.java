package no.fint.provider.eventstate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RefreshScope
@Service
public class JanitorService {

    @Value("${fint.provider.eventstate.ttl:120000}")
    private long eventStateTimeToLive;

    @Autowired
    private EventStateService eventStateService;

    @Scheduled(fixedDelayString = "${fint.provider.eventstate.run-interval:10000}")
    public void run() {
        Map<String, EventState> eventStateMap = eventStateService.getMap();
        eventStateMap.forEach((s, eventState) -> {
            if ((System.currentTimeMillis() - eventState.getTimestamp()) > eventStateTimeToLive) {
                log.info("Clearing event {}", eventState.getEvent().getCorrId());
                eventStateService.clear(eventState.getEvent());
            }
        });
    }
}
