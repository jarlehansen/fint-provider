package no.fint.provider.admin

import no.fint.audit.plugin.mongo.MongoAuditEvent
import no.fint.provider.events.sse.FintSseEmitter
import no.fint.provider.events.sse.FintSseEmitters
import no.fint.provider.events.sse.SseService
import no.fint.provider.eventstate.EventState
import no.fint.provider.eventstate.EventStateRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.equalTo
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminControllerSpec extends Specification {
    private AdminController adminController
    private EventStateRepository eventStateRepository
    private SseService sseService
    private MongoTemplate mongoTemplate
    private MockMvc mockMvc

    void setup() {
        eventStateRepository = Mock(EventStateRepository)
        sseService = Mock(SseService)
        mongoTemplate = Mock(MongoTemplate)
        adminController = new AdminController(eventStateRepository: eventStateRepository, sseService: sseService, mongoTemplate: mongoTemplate)
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build()
    }

    def "List connected SSE clients"() {
        given:
        def emitters = FintSseEmitters.with(5)
        emitters.add(new FintSseEmitter(id: '123'))
        emitters.add(new FintSseEmitter(id: '234'))

        when:
        def response = mockMvc.perform(get('/provider/admin/sse-clients'))

        then:
        1 * sseService.getSseClients() >> ['rogfk.no': emitters]
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath('$[0].orgId').value(equalTo('rogfk.no')))
                .andExpect(jsonPath('$[0].connectedIds[0]').value(equalTo('123')))
                .andExpect(jsonPath('$[0].connectedIds[1]').value(equalTo('234')))
    }

    def "List current event states"() {
        when:
        def response = mockMvc.perform(get('/provider/admin/eventStates'))

        then:
        1 * eventStateRepository.getMap() >> ['rogfk.no': new EventState()]
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
    }

    def "Get audited events log"() {
        when:
        def response = mockMvc.perform(get('/provider/admin/audit/events'))

        then:
        1 * mongoTemplate.findAll(MongoAuditEvent) >> []
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
    }
}
