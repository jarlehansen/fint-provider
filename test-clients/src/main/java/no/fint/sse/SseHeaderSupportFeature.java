package no.fint.sse;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class SseHeaderSupportFeature implements Feature {
    private final SseHeaderSupportFilter filter;

    public SseHeaderSupportFeature(SseHeaderProvider provider) {
        this.filter = new SseHeaderSupportFilter(provider);
    }

    @Override
    public boolean configure(FeatureContext context) {
        context.register(filter);
        return true;
    }
}