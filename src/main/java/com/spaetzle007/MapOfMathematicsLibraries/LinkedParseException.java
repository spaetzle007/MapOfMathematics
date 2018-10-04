package com.spaetzle007.MapOfMathematicsLibraries;

public class LinkedParseException extends Exception {
	private String fehler;
	public LinkedParseException(String fehler)  {
		this.fehler=fehler;
	}
	public LinkedParseException() {
		this.fehler="Unbekannter Fehler";
	}
	public String getMessage() {
		return fehler;
	}
}
