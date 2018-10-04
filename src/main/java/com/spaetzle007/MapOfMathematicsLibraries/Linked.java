package com.spaetzle007.MapOfMathematicsLibraries;
import java.util.ArrayList;

public class Linked {
	private String name;
	private LatexText text;
	private String suplink;
	private ArrayList<LinkedString> links;
	
	/**
	 * Default-Konstruktor
	 */
	public Linked() {
		name = "";
		text = new LatexText("", 0);
		suplink="";
		links=new ArrayList<LinkedString>();
	}

	/**
	 * Konstruktor, der String zu Linked decodiert
	 * zu Entwicklungszwecken
	 */
	public Linked(Linked l) {
		this.name=l.getName();
		this.text = l.getLatexText();
		this.suplink=l.getSupLink();
		this.links=l.getLinks();
	}
	/**
	 * Linked aus Text einlesen
	 */
	public Linked(String input) throws LinkedParseException {
		links=new ArrayList<LinkedString>();
		decodeXML(input);
		
		sortLinks();
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
	
	public ArrayList<LinkedString> getLinks() {return links;}
	public void setLinks(ArrayList<LinkedString> bsp) {this.links=bsp;}
	public void removeLink(String connection) {
		for(int i=0; i<links.size(); i++) {
			//if(links.get(i).getString().equals(connection)) {
			if(links.get(i).equals(connection)) {
				links.remove(i);
			}
		}
	}
	public void removeLink(int pos) {this.links.remove(pos);}
	public void setLink(int i, LinkedString str) {links.set(i, str);}
	public void addLink(LinkedString connection) {
		boolean übernehmen=true;
		
		if(name=="Start" || connection.getName().equals(name) || links.contains(connection)) {
			übernehmen=false;
		}
			
		if(übernehmen) {
			this.links.add(connection);
		}
	}
	public boolean hasLink(LinkedString name) {
		boolean ret=false;
		for(int i=0; i<links.size(); i++) {
			if(links.get(i).getName().equals(name)) {
				ret=true;
			}
		}
		return ret;
	}
	public void clearLinks() {this.links.clear();}

	
	public String convertToXML() {
		String ret="";
		ret+="\t<Linked>\n";
		ret+="\t\t<title>"+name+"</title>\n";
		ret+="\t\t<text>"+text.getText()+"</text>\n";
		ret+="\t\t<suplink>"+suplink+"</suplink>\n";
		for(int i=0; i<links.size(); i++) {
			ret+="\t\t<link>"+links.get(i).getName()+";"+links.get(i).getType()+"</link>\n";
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
			throw new LinkedParseException("Falsches Format:\nTitle-String falsch");
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
			throw new LinkedParseException("Falsches Format:\nText-String falsch bei Linked "+name);
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
			throw new LinkedParseException("Falsches Format:\nSupstring falsch bei Linked "+name);
		}
		i0=i;
		while(!str.substring(i, i+"</suplink>".length()).equals("</suplink>")) {
			i++;
		}
		suplink=str.substring(i0, i);
		i+="</suplink>".length();
		
		//Linkss einlesen
		while(i<str.length()-"</Linked>".length()) {
			if(str.substring(i, i+"<link>".length()).equals("<link>")) {
				i+="<link>".length();
			} else {
				throw new LinkedParseException("Falsches Format:\nLink falsch bei Linked "+name);
			}
			i0=i;
			int i1=0;
			while(!str.substring(i, i+"</link>".length()).equals("</link>")) {
				i++;
				if(str.charAt(i)==';') {i1=i;}
			}
			if(i1==0) {throw new LinkedParseException("Falsches Format:\nLink-Ausgangsstring falsch bei Linked "+name);}
			
			
			links.add(new LinkedString(str.substring(i0, i1), Byte.parseByte(str.substring(i1+1, i))));
			i+="</link>".length();
		}
		
		//Ausgangsstring testen
		if(str.substring(i, i+"</Linked>".length()).equals("</Linked>")) {
			
		} else {
			throw new LinkedParseException("Falsches Format:\nLinked-Ausgangsstring falsch bei Linked "+name);
		}
	}
	
	public void sortLinks() {
		ArrayList<LinkedString> input=links;
		ArrayList<LinkedString> output=new ArrayList<LinkedString>();
		LinkedString erster;
		while(!input.isEmpty()) {
			erster=input.get(0);
			for(int i=1; i<input.size();i++) {
				if(input.get(i).getName().compareToIgnoreCase(erster.getName())<=0 && input.get(i).getType()<=erster.getType()) {
					erster=input.get(i);
				}
			}
			output.add(erster);
			input.remove(erster);
		}
		links=output;
	}
	
}
