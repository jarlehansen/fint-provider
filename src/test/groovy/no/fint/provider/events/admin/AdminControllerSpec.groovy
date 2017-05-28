package no.fint.provider.events.admin

import no.fint.audit.plugin.mongo.MongoAuditEvent
import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.provider.events.Constants
import no.fint.test.utils.MockMvcSpecification
import org.redisson.api.RBlockingQueue
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

    def "DELETE the content of all queues"() {
        given:
        def queue = Mock(RBlockingQueue)

        when:
        def response = mockMvc.perform(delete('/admin/clear/all'))

        then:
        1 * fintEvents.getQueues() >> ['my-queue']
        1 * fintEvents.getQueue('my-queue') >> queue
        1 * queue.clear()
        response.andExpect(status().isOk())
    }

    def "DELETE content of downstream queue for orgId"() {
        given:
        def queue = Mock(RBlockingQueue)

        when:
        def response = mockMvc.perform(delete('/admin/clear/downstream').header(Constants.HEADER_ORGID, 'rogfk.no'))

        then:
        1 * fintEvents.getDownstream('rogfk.no') >> queue
        1 * queue.clear()
        response.andExpect(status().isOk())
    }

    def "DELETE content of upstream queue for orgId"() {
        given:
        def queue = Mock(RBlockingQueue)

        when:
        def response = mockMvc.perform(delete('/admin/clear/upstream').header(Constants.HEADER_ORGID, 'rogfk.no'))

        then:
        1 * fintEvents.getUpstream('rogfk.no') >> queue
        1 * queue.clear()
        response.andExpect(status().isOk())
    }
}
