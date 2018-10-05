package com.spaetzle007.MapOfMathematicsLibraries;

public class LinkedString {
	private String name;
	private byte type;	//0: normal
	
	public LinkedString(String str, byte typ) {
		name=str;
		type=typ;
	}
	
	
	public String getName() {return name;}
	public void setName(String str) {name=str;}
	public byte getType() {return type;}
	public void setType(byte t) {type=t;}
	public boolean equals(LinkedString other) {
		if(name.equals(other.getName()) && type==other.getType()) {
			return true;
		} else {
			return false;
		}
	}
}
