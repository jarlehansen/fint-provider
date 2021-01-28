package no.fint.provider.sse;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SseOrg {
    private String path;
    private String orgId;
    private List<SseClient> clients;
}
