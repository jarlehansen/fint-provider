package no.fint.provider.events.swagger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.SwaggerDefinition;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/springfox")
public class ApiDocsController {

    @Value("${fint.provider.swagger-https:true}")
    private String swaggerUrlHttps;

    private RestTemplate restTemplate = new RestTemplate();
    private HttpHeaders headers;

    @Getter
    private String externalDocsDescription;
    @Getter
    private String externalDocsUrl;

    public ApiDocsController() {
        headers = new HttpHeaders();
        headers.put(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, Lists.newArrayList("*"));
        headers.put(HttpHeaders.CONTENT_TYPE, Lists.newArrayList(MediaType.APPLICATION_JSON_VALUE));

        Reflections reflections = new Reflections("no.fint.provider.events");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(SwaggerDefinition.class);
        if (classes.size() == 1) {
            Class<?> clazz = classes.iterator().next();
            SwaggerDefinition annotation = clazz.getAnnotation(SwaggerDefinition.class);
            ExternalDocs externalDocs = annotation.externalDocs();
            externalDocsDescription = externalDocs.value();
            externalDocsUrl = externalDocs.url();
        }
    }

    @GetMapping("/api-docs")
    public ResponseEntity getApiDocs(HttpServletRequest request) throws IOException {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromServletMapping(request).path("/v2/api-docs");
        if (Boolean.valueOf(swaggerUrlHttps)) {
            builder.scheme("https");
        }

        UriComponents uri = builder.build();
        ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, new HttpEntity<>(null), String.class);

        String json = response.getBody();
        json = addExternalDocs(json);
        return new ResponseEntity<>(json, headers, response.getStatusCode());
    }

    String addExternalDocs(String json) {
        if (!StringUtils.isEmpty(externalDocsDescription) && !StringUtils.isEmpty(externalDocsUrl)) {
            json = JsonPath.parse(json).put("$", "externalDocs", ImmutableMap.of("description", externalDocsDescription, "url", externalDocsUrl)).jsonString();
        }
        return json;
    }

}
