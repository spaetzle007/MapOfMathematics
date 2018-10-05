package com.spaetzle007.MapOfMathematicsLibraries;

/**
 * Umwandler für verschiedene Textdarstellungen
 * Zustand 0 - Normaler Text(Speicherung im Zutand 0) (so abgespeichert in MOM.xml)
 * Zeilenwechsel mit "\\"
 * Zustand 1 - JLatexMath (Desktop)
 * Zeilenwechsel mit "}\\\text{"  
 * Zustand 2 - LatexView (Android)
 * Zeilenwechsel mit "\\", Formeln einrahmen mit "\\("..."\\)"
 * Zustand 3 - EditMode (für MOMEditMode) (Desktop)
 * Zeilenwechsel mit "\n"
 * 
 * Zu berücksichtigen: 
 * -Bei Umwandlung von "\\" in "\n" können "\\" nicht einfach ersetzt werden:
 *  Problem bei "\begin{cases}a\\b\\c\end{cases}"
 */
public class LatexText {
	String text;	//Text liegt normal vor: Leerzeichen mit \\
	
	public LatexText() {
		text="\\textbf{}";
	}
	/**
	 * Übergebener integer type ist Typ des eingegebenen Formats: 
	 * 0=aus Datei ausgelesen
	 * 3=Aus MOMEdit.EditMode ausgelesen
	 */
	public LatexText(String str, int type) {
		if(type==0) {			//Einlesen aus Datei
			text=str;
		} else if(type==3) {	//Einlesen aus EditMode
			text=str.replace("\n", "\\\\");
		} 
	}
	
	public String getStandardRepresentation() {return text;}
	/**
	 * JLatexMath-Darstellung des Texts (für Verwendung in Desktop-App)
	 */
	public String getJLatexMathRepresentation() {
		String ret="\\text{"+text+"}";
		ret=replaceExceptOfBeginEnd(ret, "}\\\\\\text{");
		
		
		return ret;
	}
	/**
	 * Darstellung des Texts für Bearbeitung in MOMEdit.EditMode
	 */
	public String getEditModeRepresentation() {
		String ret=replaceExceptOfBeginEnd(text, "\n");
		return ret;
	}
	
	/**
	 * LaTeXView-Darstellung des Texts (für Verwendung in Android-App)
	 */
	public String getLatexViewRepresentation() {
		//Ersetze in text $...$ durch \\(...\\)
		String bau=text;
		boolean drin=false;
		int i=0;
		System.out.println("Anfang: "+bau);
		do {
			if(bau.charAt(i)=='$') {
				if(drin) {
					bau=bau.substring(0, i)+"\\)"+bau.substring(i+1, bau.length());
				} else {
					bau=bau.substring(0, i)+"\\("+bau.substring(i+1, bau.length());
				}
				i++;
				drin=!drin;
			}
			i++;
		} while(i<bau.length());
		//In array-Umgebung verpacken
		
		System.out.println("Erste Ersetzung: "+bau);
		String ret="\\text{"+replaceExceptOfBeginEnd(bau, "}\\\\\\text{")+"}";
		System.out.println("Fertig: "+ret);
		return ret;
	}
	
	/**
	 * Ersetze global "\\\\" durch str, außer im "\begin{"..."\end{"-Bereich
	 */
	private String replaceExceptOfBeginEnd(String input, String str) {
		boolean easy=true;
		String ret=input;
		int i=0;
		try {
			do {
				//Zwischen Modi umschalten
				if(ret.substring(i, i+"\\begin{".length()).equals("\\begin{")) {
					easy=false;
					i+="\\begin{".length();
				} else if(ret.substring(i, i+"\\end{".length()).equals("\\end{")) {
					easy=true;
					i+="\\end{".length();
				}
				//Gemäß aktuellem Modi umschalten
				if(ret.substring(i, i+"\\\\".length()).equals("\\\\") && easy) {
					ret=ret.substring(0, i)+str+ret.substring(i+"\\\\".length(), ret.length());
					i+=str.length();
				}
				i++;
			} while(i<ret.length());
		} catch(StringIndexOutOfBoundsException e) {}
		
		return ret;
	}
}
