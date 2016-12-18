package no.fint.provider.events.sse

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import spock.lang.Specification

import java.util.function.Function

class SseEmittersSpec extends Specification {
    private static final int MAX_SIZE = 2
    private SseEmitters sseEmitters
    private Function<SseEmitter, Void> removeCallback
    private boolean callback = false

    void setup() {
        removeCallback = new Function<SseEmitter, Void>() {
            @Override
            Void apply(SseEmitter emitter) {
                callback = true
                return null
            }
        }
        sseEmitters = new SseEmitters(MAX_SIZE, removeCallback)
    }

    def "Add emitter and size is 1"() {
        when:
        sseEmitters.add(new SseEmitter())
        def size = sseEmitters.size()

        then:
        size == 1
    }

    def "Add more than max size, size is still max size"() {
        given:
        def first = new SseEmitter()
        def last = new SseEmitter()

        when:
        sseEmitters.add(first)
        sseEmitters.add(new SseEmitter())
        sseEmitters.add(new SseEmitter())
        sseEmitters.add(new SseEmitter())
        sseEmitters.add(last)
        def size = sseEmitters.size()

        then:
        size == MAX_SIZE
        !sseEmitters.contains(first)
        sseEmitters.contains(last)
        callback
    }

    def "Remove emitter"() {
        given:
        def emitter = new SseEmitter()

        when:
        sseEmitters.add(emitter)
        sseEmitters.remove(emitter)
        def size = sseEmitters.size()

        then:
        size == 0
    }
}
