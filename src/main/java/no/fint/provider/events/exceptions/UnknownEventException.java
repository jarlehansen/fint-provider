package no.fint.provider.events.exceptions;

public class UnknownEventException extends RuntimeException {
    public UnknownEventException(String corrId) {
        super(corrId);
    }
}
