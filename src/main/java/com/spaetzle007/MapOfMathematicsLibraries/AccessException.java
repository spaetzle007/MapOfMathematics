package com.spaetzle007.MapOfMathematicsLibraries;
public class AccessException extends Exception {
	String fehler;
	public AccessException() {
		fehler="Unbekannter Fehler";
	}
	public AccessException(String fehler) {
		this.fehler=fehler;
	}
	public String getMessage() {return fehler;}
}
