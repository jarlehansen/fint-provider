package no.fint.sse;

import java.util.Map;

@FunctionalInterface
public interface SseHeaderProvider {
    Map<String, String> getHeaders();
}