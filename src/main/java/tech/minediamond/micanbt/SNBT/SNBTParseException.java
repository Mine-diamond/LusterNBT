package tech.minediamond.micanbt.SNBT;

public class SNBTParseException extends RuntimeException {
    public SNBTParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SNBTParseException(String message) {
        super(message);
    }
}
