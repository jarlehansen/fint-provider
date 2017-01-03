package no.fint.provider.events.sse;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

public class FintSseEmitters implements Iterable<FintSseEmitter> {

    private final int maxSize;
    private final Deque<FintSseEmitter> emitters;
    private Function<FintSseEmitter, Void> removeCallback;

    public FintSseEmitters(int maxSize, Function<FintSseEmitter, Void> removeCallback) {
        this.maxSize = maxSize;
        emitters = new ArrayDeque<>(maxSize);
        this.removeCallback = removeCallback;
    }

    public void add(FintSseEmitter emitter) {
        if (emitters.size() == maxSize) {
            if (removeCallback != null) {
                removeCallback.apply(emitter);
            }
            emitters.remove();
        }

        emitters.add(emitter);
    }

    public void remove(FintSseEmitter emitter) {
        emitters.remove(emitter);
    }

    public Optional<FintSseEmitter> get(String id) {
        return emitters.stream().filter(registered -> registered.getId().equals(id)).findFirst();
    }

    public int size() {
        return emitters.size();
    }

    @Override
    public Iterator<FintSseEmitter> iterator() {
        return new ArrayDeque<>(emitters).iterator();
    }

    public static FintSseEmitters with(int maxSize) {
        return new FintSseEmitters(maxSize, null);
    }

    public static FintSseEmitters with(int maxSize, Function<FintSseEmitter, Void> removeCallback) {
        return new FintSseEmitters(maxSize, removeCallback);
    }
}
