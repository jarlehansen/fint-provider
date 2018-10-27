package no.fint.provider.events.testmode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnabledIfTestMode
@RestController
@RequestMapping("/endpoints")
public class ActiveAssetsController {

    @GetMapping
    public String[] getActiveEndpoints() {
        return new String[]{TestModeConstants.ORGID};
    }
}
