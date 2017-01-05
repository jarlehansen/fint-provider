package no.fint.provider.events.subscriber

import no.fint.audit.FintAuditService
import no.fint.event.model.Event
import no.fint.event.model.Status
import no.fint.provider.events.sse.SseService
import no.fint.provider.eventstate.EventState
import no.fint.provider.eventstate.EventStateService
import no.fint.provider.exceptions.EventNotApprovedByProviderException
import spock.lang.Specification

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

    def "Receive initial event and consume message from queue"() {
        given:
        def event = new Event()

        when:
        downstreamSubscriber.receive(null, event)

        then:
        1 * eventStateService.exists(event) >> false
        1 * eventStateService.add(null, _ as Event)
        1 * fintAuditService.audit(_ as Event, _ as Boolean)
        1 * sseService.send(_ as Event)
        1 * eventStateService.exists(_ as Event, _ as Status) >> false
    }

    def "Receive initial event and throw exception when event is not approved by provider"() {
        given:
        def event = new Event()

        when:
        downstreamSubscriber.receive(null, event)

        then:
        1 * eventStateService.exists(event) >> false
        1 * eventStateService.add(null, _ as Event)
        1 * fintAuditService.audit(_ as Event, _ as Boolean)
        1 * sseService.send(_ as Event)
        1 * eventStateService.exists(_ as Event, _ as Status) >> true
        thrown(EventNotApprovedByProviderException)
    }

    def "Receive existing event and consume message from queue"() {
        given:
        def event = new Event()

        when:
        downstreamSubscriber.receive(null, event)

        then:
        1 * eventStateService.exists(event) >> true
        1 * eventStateService.getEventState(event) >> Optional.of(new EventState(event: event))
        1 * eventStateService.exists(_ as Event, _ as Status) >> false
    }

    def "Receive existing event and throw exception when event is not approved by provider"() {
        given:
        def event = new Event()

        when:
        downstreamSubscriber.receive(null, event)

        then:
        1 * eventStateService.exists(event) >> true
        1 * eventStateService.getEventState(event) >> Optional.of(new EventState(event: event))
        1 * eventStateService.exists(_ as Event, _ as Status) >> true
        1 * sseService.send(_ as Event)
        thrown(EventNotApprovedByProviderException)
    }
}
