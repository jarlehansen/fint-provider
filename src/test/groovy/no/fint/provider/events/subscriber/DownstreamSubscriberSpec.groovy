package no.fint.provider.events.subscriber

import no.fint.audit.FintAuditService
import no.fint.provider.events.sse.SseService
import no.fint.provider.eventstate.EventStateService
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class DownstreamSubscriberSpec extends Specification {
    private DownstreamSubscriber downstreamSubscriber
    private SseService sseService
    private EventStateService eventStateService
    private FintAuditService fintAuditService

    void setup() {
        sseService = Mock(SseService)
        eventStateService = Mock(EventStateService)
        fintAuditService = Mock(FintAuditService)

        downstreamSubscriber = new DownstreamSubscriber(sseService: sseService, eventStateService: eventStateService, fintAuditService: fintAuditService)
    }

}
