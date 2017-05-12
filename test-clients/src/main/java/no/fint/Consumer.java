package no.fint;

import no.fint.events.FintEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumer")
public class Consumer {

    @Autowired
    private FintEvents fintEvents;

    @GetMapping("/healthCheck")
    public String healthCheck() {
        return "";
    }
}
