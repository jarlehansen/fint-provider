package no.fint.provider.admin

import no.fint.audit.plugin.mongo.MongoAuditEvent
import no.fint.event.model.Event
import no.fint.provider.events.sse.FintSseEmitter
import no.fint.provider.events.sse.FintSseEmitters
import no.fint.provider.events.sse.SseService
import no.fint.provider.eventstate.EventState
import no.fint.provider.eventstate.EventStateService
import no.fint.test.utils.MockMvcSpecification
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import static org.hamcrest.CoreMatchers.notNullValue

class AdminControllerSpec extends MockMvcSpecification {
    private AdminController adminController
    private EventStateService eventStateService
    private SseService sseService
    private MongoTemplate mongoTemplate
    private MockMvc mockMvc

    void setup() {
        eventStateService = Mock(EventStateService)
        sseService = Mock(SseService)
        mongoTemplate = Mock(MongoTemplate)
        adminController = new AdminController(sseService: sseService, eventStateService: eventStateService, mongoTemplate: mongoTemplate)
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build()
    }

    def "List connected SSE clients"() {
        given:
        def emitters = FintSseEmitters.with(5)
        emitters.add(new FintSseEmitter(id: '123'))
        emitters.add(new FintSseEmitter(id: '234'))

        when:
        def response = mockMvc.perform(get('/admin/sse-clients'))

        then:
        1 * sseService.getSseClients() >> ['rogfk.no': emitters]
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath('$[0].orgId').value(equalTo('rogfk.no')))
                .andExpect(jsonPath('$[0].clients[0].id').value(equalTo('123')))
                .andExpect(jsonPath('$[0].clients[0].registered').value(notNullValue()))
                .andExpect(jsonPath('$[0].clients[1].id').value(equalTo('234')))
                .andExpect(jsonPath('$[0].clients[1].registered').value(notNullValue()))
    }

    def "Remove all SSE clients"() {
        given:
        def emitters = FintSseEmitters.with(5)
        emitters.add(new FintSseEmitter(id: '123'))
        emitters.add(new FintSseEmitter(id: '234'))

        when:
        def response = mockMvc.perform(delete('/admin/sse-clients'))

        then:
        1 * sseService.removeAll()
        response.andExpect(status().isOk())
    }

    def "List current event states"() {
        when:
        def response = mockMvc.perform(get('/admin/eventStates'))

        then:
        1 * eventStateService.getEventStates() >> [new EventState(new Event(corrId: '123')), new EventState(new Event(corrId: '234'))]
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
    }

    def "Get audited events log"() {
        when:
        def response = mockMvc.perform(get('/admin/audit/events'))

        then:
        1 * mongoTemplate.findAll(MongoAuditEvent) >> []
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
    }
}
