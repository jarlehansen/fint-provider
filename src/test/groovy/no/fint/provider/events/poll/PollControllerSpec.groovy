package no.fint.provider.events.poll

import no.fint.event.model.Event
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PollControllerSpec extends Specification {
    private PollController pollController
    private PollService pollService
    private MockMvc mockMvc

    void setup() {
        pollService = Mock(PollService)
        pollController = new PollController(pollService: pollService)
        mockMvc = MockMvcBuilders.standaloneSetup(pollController).build()
    }

    def "Return content if event is available"() {
        when:
        def response = mockMvc.perform(get('/provider/poll').header('x-org-id', 'rogfk.no'))

        then:
        1 * pollService.readEvent('rogfk.no') >> Optional.of(new Event())
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
    }

    def "Return no content if event is not available"() {
        when:
        def response = mockMvc.perform(get('/provider/poll').header('x-org-id', 'rogfk.no'))

        then:
        1 * pollService.readEvent('rogfk.no') >> Optional.empty()
        response.andExpect(status().isNoContent())
    }
}
