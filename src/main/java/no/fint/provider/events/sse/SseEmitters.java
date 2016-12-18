package no.fint.provider.events.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.Function;

public class SseEmitters implements Iterable<SseEmitter> {

    private final int maxSize;
    private final Deque<SseEmitter> emitters;
    private Function<SseEmitter, Void> removeCallback;

    public SseEmitters(int maxSize, Function<SseEmitter, Void> removeCallback) {
        this.maxSize = maxSize;
        emitters = new ArrayDeque<>(maxSize);
        this.removeCallback = removeCallback;
    }

    public void add(SseEmitter emitter) {
        if (emitters.size() == maxSize) {
            if (removeCallback != null) {
                removeCallback.apply(emitter);
            }
            emitters.remove();
        }

        emitters.add(emitter);
    }

    public void remove(SseEmitter emitter) {
        emitters.remove(emitter);
    }

    public boolean contains(SseEmitter emitter) {
        return emitters.contains(emitter);
    }

    public int size() {
        return emitters.size();
    }

    @Override
    public Iterator<SseEmitter> iterator() {
        return emitters.iterator();
    }

    public static SseEmitters with(int maxSize) {
        return new SseEmitters(maxSize, null);
    }

    public static SseEmitters with(int maxSize, Function<SseEmitter, Void> removeCallback) {
        return new SseEmitters(maxSize, removeCallback);
    }
}
