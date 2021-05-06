package no.fint.provider.exceptions;

public class UnknownEventException extends RuntimeException {
    public UnknownEventException(String corrId) {
        super(corrId);
    }
}
