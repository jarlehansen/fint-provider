package no.fint.provider.events.response

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.event.model.Event
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ResponseControllerSpec extends Specification {
    private ResponseController responseController
    private ResponseService responseService
    private MockMvc mockMvc

    void setup() {
        responseService = Mock(ResponseService)
        responseController = new ResponseController(responseService: responseService)
        mockMvc = MockMvcBuilders.standaloneSetup(responseController).build()
    }

    def "Return status OK if response is handled"() {
        given:
        def jsonBody = new ObjectMapper().writeValueAsString(new Event())

        when:
        def response = mockMvc.perform(post('/response')
                .header('x-org-id', 'rogfk.no')
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))

        then:
        1 * responseService.handleAdapterResponse(_ as Event) >> true
        response.andExpect(status().isOk())
    }

    def "Return status GONE if response is already handled"() {
        given:
        def jsonBody = new ObjectMapper().writeValueAsString(new Event())

        when:
        def response = mockMvc.perform(post('/response')
                .header('x-org-id', 'rogfk.no')
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))

        then:
        1 * responseService.handleAdapterResponse(_ as Event) >> false
        response.andExpect(status().isGone())

    }
}
