package no.fint.provider.events.admin

import no.fint.audit.plugin.mongo.MongoAuditEvent
import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.test.utils.MockMvcSpecification
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.web.servlet.MockMvc

class AdminControllerSpec extends MockMvcSpecification {
    private AdminController controller
    private MongoTemplate mongoTemplate
    private FintEvents fintEvents
    private MockMvc mockMvc

    void setup() {
        mongoTemplate = Mock(MongoTemplate)
        fintEvents = Mock(FintEvents)
        controller = new AdminController(mongoTemplate: mongoTemplate, fintEvents: fintEvents)
        mockMvc = standaloneSetup(controller)
    }

    def "GET all audit events"() {
        when:
        def response = mockMvc.perform(get('/admin/audit/events'))

        then:
        1 * mongoTemplate.findAll(MongoAuditEvent) >> [new MongoAuditEvent(new Event(orgId: 'rogfk.no'), true)]
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$[0].orgId').value(equalTo('rogfk.no')))
    }

    def "DELETE temporary queues"() {
        when:
        def response = mockMvc.perform(delete('/admin/tempQueues'))

        then:
        1 * fintEvents.deleteTempQueues() >> true
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$').value(equalTo(true)))
    }
}
