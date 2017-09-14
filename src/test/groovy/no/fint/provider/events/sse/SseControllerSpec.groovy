package no.fint.provider.events.sse

import no.fint.event.model.HeaderConstants
import no.fint.events.FintEvents
import no.fint.provider.events.subscriber.DownstreamSubscriber
import no.fint.test.utils.MockMvcSpecification
import org.springframework.test.web.servlet.MockMvc

class SseControllerSpec extends MockMvcSpecification {
    private SseController controller
    private SseService sseService
    private FintEvents fintEvents
    private MockMvc mockMvc

    void setup() {
        sseService = Mock(SseService)
        fintEvents = Mock(FintEvents)
        controller = new SseController(sseService: sseService, fintEvents: fintEvents)
        mockMvc = standaloneSetup(controller)
    }

    def "Subscribe to sse client"() {
        when:
        def response = mockMvc.perform(get('/sse/123').header(HeaderConstants.ORG_ID, 'rogfk.no'))

        then:
        1 * sseService.subscribe('123', 'rogfk.no')
        1 * fintEvents.registerDownstreamListener(DownstreamSubscriber, 'rogfk.no')
        response.andExpect(status().isOk())
    }

    def "Get registered sse clients"() {
        given:
        def emitter = new FintSseEmitter('123', 1000)
        def emitters = new FintSseEmitters(1, null)
        emitters.add(emitter)

        when:
        def response = mockMvc.perform(get('/sse/clients'))

        then:
        1 * sseService.getSseClients() >> ['rogfk.no': emitters]
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$[0].orgId').value(equalTo('rogfk.no')))
                .andExpect(jsonPath('$[0].clients[0].id').value(equalTo('123')))
    }

    def "Delete registered sse clients"() {
        when:
        def response = mockMvc.perform(delete('/sse/clients'))

        then:
        1 * sseService.removeAll()
        response.andExpect(status().isOk())
    }

    def "Auth init"() {
        when:
        def response = mockMvc.perform(get('/sse/auth-init'))

        then:
        response.andExpect(status().isOk())
    }
}
