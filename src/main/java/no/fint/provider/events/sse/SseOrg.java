package no.fint.provider.events.sse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SseOrg {
    private String orgId;
    private List<SseClient> clients;
}
