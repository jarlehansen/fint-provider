package no.fint.provider.sse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping(value = "/provider/sse")
@RestController
public class SseController {

    @Autowired
    private SseService sseService;

    @RequestMapping(method = RequestMethod.GET)
    public SseEmitter subscribe(@RequestHeader("x-org-id") String orgId) {
        return sseService.subscribe(orgId);
    }

}
