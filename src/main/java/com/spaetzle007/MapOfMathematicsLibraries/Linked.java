package com.spaetzle007.MapOfMathematicsLibraries;
import java.util.ArrayList;

public class Linked {
	private String name;
	private LatexText text;
	private String suplink;
	private ArrayList<String> connected;
	
	/**
	 * Default-Konstruktor
	 */
	public Linked() {
		name = "";
		text = new LatexText("", 0);
		suplink="";
		connected=new ArrayList<String>();
	}

	/**
	 * Konstruktor, der String zu Linked decodiert
	 * zu Entwicklungszwecken
	 */
	public Linked(Linked l) {
		this.name=l.getName();
		this.text = l.getLatexText();
		this.suplink=l.getSupLink();
		this.connected=l.getConnecteds();
	}
	/**
	 * Linked aus Text einlesen
	 */
	public Linked(String input) throws LinkedParseException {
		connected=new ArrayList<String>();
		decodeXML(input);
		
		sortConnected();
	}

	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	
	public LatexText getLatexText() {return text;}
	public void setLatexText(LatexText text) {this.text = text;}
	public String getText() {return text.getText();}
	public String getJLatexMathRepresentation() {return text.getJLatexMathRepresentation();}
	public String getLatexViewRepresentation() {return text.getLatexViewRepresentation();}
	public String getEditModeRepresentation() {return text.getEditModeRepresentation();}
	
	public String getSupLink() {return suplink;}
	public void setSupLink(String str) {suplink=str;}
	
	public ArrayList<String> getConnecteds() {return connected;}
	public void setConnecteds(ArrayList<String> bsp) {this.connected=bsp;}
	public void removeConnected(String connection) {
		for(int i=0; i<connected.size(); i++) {
			//if(connected.get(i).getString().equals(connection)) {
			if(connected.get(i).equals(connection)) {
				connected.remove(i);
			}
		}
	}
	public void removeConnected(int pos) {this.connected.remove(pos);}
	public void setConnected(int i, String str) {connected.set(i, str);}
	public void addConnected(String connection) {
		boolean übernehmen=true;
		
		if(name=="Start" || connection.equals(name) || connected.contains(connection)) {
			übernehmen=false;
		}
			
		if(übernehmen) {
			this.connected.add(connection);
		}
	}
	public boolean hasConnected(String name) {
		boolean ret=false;
		for(int i=0; i<connected.size(); i++) {
			if(connected.get(i).equals(name)) {
				ret=true;
			}
		}
		return ret;
	}
	public void clearConnecteds() {this.connected.clear();}

	
	public String convertToXML() {
		String ret="";
		ret+="\t<Linked>\n";
		ret+="\t\t<title>"+name+"</title>\n";
		ret+="\t\t<text>"+text.getText()+"</text>\n";
		ret+="\t\t<suplink>"+suplink+"</suplink>\n";
		for(int i=0; i<connected.size(); i++) {
			ret+="\t\t<link>"+connected.get(i)+"</link>\n";
		}
		ret+="\t</Linked>\n";
		return ret;
	}
	
	private void decodeXML(String str) throws LinkedParseException {
		int i=0, i0;
		//Titel einlesen
		if(str.substring(0, "<Linked><title>".length()).equals("<Linked><title>")) {
			i+="<Linked><title>".length();
		} else {
			throw new LinkedParseException("Falsches Format");
		}
		i0=i;
		while(!str.substring(i, i+"</title>".length()).equals("</title>")) {
			i++;
		}
		name=str.substring(i0, i);
		i+="</title>".length();
		
		//Text einlesen
		if(str.substring(i, i+"<text>".length()).equals("<text>")) {
			i+="<text>".length();
		} else {
			throw new LinkedParseException("Falsches Format");
		}
		i0=i;
		while(!str.substring(i, i+"</text>".length()).equals("</text>")) {
			i++;
		}
		text=new LatexText(str.substring(i0, i), 0);
		i+="</text>".length();
		
		//Suplink einlesen
		if(str.substring(i, i+"<suplink>".length()).equals("<suplink>")) {
			i+="<suplink>".length();
		} else {
			throw new LinkedParseException("Falsches Format");
		}
		i0=i;
		while(!str.substring(i, i+"</suplink>".length()).equals("</suplink>")) {
			i++;
		}
		suplink=str.substring(i0, i);
		i+="</suplink>".length();
		
		//Connecteds einlesen
		while(i<str.length()-"</Linked>".length()) {
			if(str.substring(i, i+"<link>".length()).equals("<link>")) {
				i+="<link>".length();
			} else {
				throw new LinkedParseException("Falsches Format");
			}
			i0=i;
			while(!str.substring(i, i+"</link>".length()).equals("</link>")) {
				i++;
			}
			connected.add(str.substring(i0, i));
			i+="</link>".length();
		}
		
		//Ausgangsstring testen
		if(str.substring(i, i+"</Linked>".length()).equals("</Linked>")) {
			
		} else {
			throw new LinkedParseException("Falsches Format");
		}
	}
	
	public void sortConnected() {
		ArrayList<String> input=connected;
		ArrayList<String> output=new ArrayList<String>();
		String erster;
		while(!input.isEmpty()) {
			erster=input.get(0);
			for(int i=1; i<input.size();i++) {
				if(input.get(i).compareToIgnoreCase(erster)<=0) {
					erster=input.get(i);
				}
			}
			output.add(erster);
			input.remove(erster);
		}
		connected=output;
	}
	
}
