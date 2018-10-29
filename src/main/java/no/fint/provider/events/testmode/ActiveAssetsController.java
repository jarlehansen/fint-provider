package no.fint.provider.events.testmode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@EnabledIfTestMode
@RestController
@RequestMapping("/endpoints")
public class ActiveAssetsController {

    @GetMapping
    public String[] getActiveEndpoints() {
        log.info("Returning test endpoint {}", TestModeConstants.ORGID);
        return new String[]{TestModeConstants.ORGID};
    }
}
