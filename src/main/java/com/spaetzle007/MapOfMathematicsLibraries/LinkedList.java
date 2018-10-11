package com.spaetzle007.MapOfMathematicsLibraries;
import java.util.ArrayList;

import java.io.IOException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.DeleteErrorException;

public class LinkedList {
	private ArrayList<Linked> list;
	private DataHandler droppi;

	/**
	 * Konstruktor, der Datei einliest und LinkedList dementsprechend erstellt
	 */
	public LinkedList(String database) throws AccessException, LinkedParseException {
		droppi=new DataHandler(database);
		String code = droppi.getMOMText();
		
		list = new ArrayList<Linked>();
		decodeXML(code);
		
		sort();
	}
	
	/**
	 * Speichert LinkedList in Datei
	 */
	public void saveList() throws  AccessException {
		droppi.uploadMOMtext(convertToXML());
	}
	public void sicherungskopie() throws AccessException  {
		droppi.sicherungskopie();
	}
	public Linked getOriginLinked() {
		Linked originLinked = new Linked();
		for (Linked Linked : list) {
			if (Linked.getSupLink().equals(Linked.getName())){
				originLinked = Linked;
			}
		}
		return originLinked;
	}
	public Linked get(int i) {return list.get(i);}
	public void add(Linked link) {list.add(link);}
	public void set(int pos, Linked link) {list.set(pos, link);}
	public int size() {return list.size();}
	public void removeLinked(String name) {
		//Position bestimmen
		int pos=0;
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getName().equals(name)) {
				pos=i;
				break;
			}
		}
		//Linked löschen
		list.remove(pos);
		//Verknüpfungen zum Eintrag löschen
		for(int i=0; i<list.size(); i++) {
			for(int j=0; j<list.get(i).getLinks().size(); i++) {
				if(list.get(i).getLinks().get(j).getName().equals(name)) {
					list.get(i).removeOnlyThisLink(list.get(i).getLinks().get(j).getName());
				}
			}
		}
	}
	public int search(String name) {
		int pos=-1;
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getName().equals(name)) {
				pos=i;
			}
		}
		return pos;
	}
	
	public void removeLink(Linked actual, LinkedString link) {
		//Link löschen
		list.get(search(actual.getName())).removeOnlyThisLink(link.getName());
		
		if(link.getType()==(byte)0) {
			//Bijektion des Links auch löschen
			list.get(search(link.getName())).removeOnlyThisLink(actual.getName());
		}
	}
	
	public void removeLink(Linked actual, int pos) {
		removeLink(list.get(search(actual.getName())), list.get(search(actual.getName())).getLinks().get(pos));
	}
	
	public void addLink(Linked actual, LinkedString connection) {
		//Bedingung dafür, dass Link hinzugefügt wird
		if(actual.getName()=="Mathematik" || connection.getName().equals(actual.getName()) || list.get(search(actual.getName())).getLinks().contains(connection)) {
			return;
		}
		//Link hinzufügen
		list.get(search(actual.getName())).addOnlyThisLink(connection);
		//Eventuell weitere Links hinzufügen
		if(connection.getType()==(byte)0) {
			list.get(search(connection.getName())).addOnlyThisLink(new LinkedString(actual.getName(), (byte)0));
		}
	}
	
	public void setLinkedsName(Linked actual, String newname) {	
		String oldname=actual.getName();
		list.get(search(actual.getName())).setOnlyThisName(newname);
		
		//Namen in allen Links ändern
		for(int i=0; i<list.size(); i++) {
			for(int j=0; j<list.get(i).getLinks().size(); j++) {
				if(list.get(i).getLinks().get(j).getName().equals(oldname)) {
					byte type=list.get(i).getLinks().get(j).getType();
					list.get(i).removeOnlyThisLink(list.get(i).getLinks().get(j).getName());
					list.get(i).addOnlyThisLink(new LinkedString(newname, type));
				}
			}
		}
	}
	
	
	public String convertToXML() {
		String ret="";
		ret+="<LinkedList>\n";
		for(int i=0; i<list.size(); i++) {
			ret+=list.get(i).convertToXML();
		}
		ret+="</LinkedList>";
		return ret;
	}
	
	/**
	 * Keine '\n's angezeigt, da in dem von gedit enthaltenen code keine Leerzeilen enthalten sind
	 * Lösche alle Tabs - Diese sind nur zur Übersicht im xml-Format
	 */
	private void decodeXML(String input) throws LinkedParseException {
		String str=input.replace("\t", "").replace("\n", "");
		
		int i=0;
		//Eingangsstring testen
		if(str.substring(0, "<LinkedList>".length()).equals("<LinkedList>")) {
			i+="<LinkedList>".length();
		} else {
			throw new LinkedParseException("Falsches Format:\nAnfangsstring LinkedList falsch");
		}
		
		while(i<str.length()-"</LinkedList>".length()) {
			int i0=i;
			if(str.substring(i, i+"<Linked>".length()).equals("<Linked>")) {
				i+="<Linked>".length();
			} else {
				throw new LinkedParseException("Falsches Format:\nAnfangsstring Linked falsch\nPosition: "+i);
			}
			
			while(!str.substring(i, i+"</Linked>".length()).equals("</Linked>")) {
				i++;
			}
			i+="</Linked>".length();
			
			list.add(new Linked(str.substring(i0, i)));
			
			//Leere Einträge direkt löschen
			if(list.get(list.size()-1).getName().equals("")) {
				list.remove(list.get(list.size()-1));
			}
		}
		//Ausgangsstring testen
		if(!str.substring(i, i+"</LinkedList>".length()).equals("</LinkedList>")) {
			throw new LinkedParseException("Falsches Format:\nEndstring LinkedList falsch");
		}
	}
	
	private void sort() {
		ArrayList<Linked> output=new ArrayList<Linked>();
		Linked erster;
		ArrayList<Linked> speicher=list;
		while(!speicher.isEmpty()) {
			erster=speicher.get(0);
			for(int j=1; j<speicher.size(); j++) {
				if(speicher.get(j).getName().compareToIgnoreCase(erster.getName())>0) {
					continue;
				} else {
					erster=speicher.get(j);
					continue;
				}
			}
			output.add(erster);
			speicher.remove(erster);
		}
		list=output;
	}
	public ArrayList<String> getSubLinks(Linked actual) {
		ArrayList<String> ret=new ArrayList<String>();
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getSupLink().equals(actual.getName()) && !list.get(i).getName().equals("Mathematik")) {
				ret.add(list.get(i).getName());
			} 
		}
		return ret;
	}
	public ArrayList<String> getEqualLinks(Linked actual) {
		ArrayList<String> ret=new ArrayList<String>();
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getSupLink().equals(actual.getSupLink()) && !list.get(i).getName().equals("Mathematik") && !list.get(i).getName().equals(actual.getName())) {
				ret.add(list.get(i).getName());
			}
		}
		return ret;
	}
}
