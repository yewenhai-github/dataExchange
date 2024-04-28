package com.easy.sequence;

public class GenerateSequenceException extends Exception {
	private static final long serialVersionUID = 1L;

	public GenerateSequenceException() {
		super();
	}

	public GenerateSequenceException(String message) {
		super(message);
	}

	public GenerateSequenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenerateSequenceException(Throwable cause) {
		super(cause);
	}
}
