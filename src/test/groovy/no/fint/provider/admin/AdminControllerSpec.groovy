package no.fint.provider.admin

import no.fint.audit.plugin.mongo.MongoAuditEvent
import no.fint.provider.events.sse.SseEmitters
import no.fint.provider.events.sse.SseService
import no.fint.provider.eventstate.EventState
import no.fint.provider.eventstate.EventStateRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
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
        when:
        def response = mockMvc.perform(get('/provider/admin/sse-clients'))

        then:
        1 * sseService.getSseClients() >> ['rogfk.no': SseEmitters.with(5)]
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
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
