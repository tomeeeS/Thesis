package tmit.bme.telkicar.service;

public class NoCapacityException extends RuntimeException {
	public NoCapacityException(String message) {
		super(message);
	}

	public NoCapacityException() {
	}
}
