package no.fint.provider.events.poll

import no.fint.event.model.Event
import no.fint.event.model.HeaderConstants
import no.fint.test.utils.MockMvcSpecification
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

class PollControllerSpec extends MockMvcSpecification {
    private PollController pollController
    private PollService pollService
    private MockMvc mockMvc

    void setup() {
        pollService = Mock(PollService)
        pollController = new PollController(pollService: pollService)
        mockMvc = standaloneSetup(pollController)
    }

    def "Return content if event is available"() {
        when:
        def response = mockMvc.perform(get('/poll').header(HeaderConstants.ORG_ID, 'rogfk.no'))

        then:
        1 * pollService.readEvent('rogfk.no') >> Optional.of(new Event())
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
    }

    def "Return no content if event is not available"() {
        when:
        def response = mockMvc.perform(get('/poll').header(HeaderConstants.ORG_ID, 'rogfk.no'))

        then:
        1 * pollService.readEvent('rogfk.no') >> Optional.empty()
        response.andExpect(status().isNoContent())
    }
}
