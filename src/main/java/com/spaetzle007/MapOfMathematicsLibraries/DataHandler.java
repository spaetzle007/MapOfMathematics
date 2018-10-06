package com.spaetzle007.MapOfMathematicsLibraries;
//Alle Klassen in Android nutzbar
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.NetworkIOException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;

public class DataHandler {
	//private final String path = "/media/jonas/OS/Users/Jonas Spinner/Documents/Studium/MapOfMathematics/MOM.dat";
	private final String sicherung1 = "/media/jonas/OS/Users/Jonas Spinner/Documents/Studium/MapOfMathematics/Sicherung/MOMSicherung";
	private final String sicherung2 = ".xml";
	private final String database ="MOM.xml";
	private  String databaseOnSystem;
	private final String übergangslösung="übergang.xml";
	
	private String dateCode;
	public static final int lengthDateCode=11; 	
	//Date Code ist in lokaler Datei zusätzliche Info, wann diese gedownloadet wurde(am Ende der Datei)
	//Format: <YYY.MM.DD>
	
	private final String ACCESS_TOKEN="ixYDUgcLTYAAAAAAAAAACj80qA2esg408YeIfIT2VjIu9bEq6EXIAYLX8hXkwcla";
	private DbxClientV2 client;
	
	/**
	 * Konstruktor
	 * Initialisiert DbxClientV2
	 * Initialisiert dateCode
	 */
	public DataHandler() throws AccessException {
		//Gewünschter Speicherort bestimmen
		databaseOnSystem=cutLast(DataHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath())+File.separator+database;
		System.out.println(databaseOnSystem);
		
		if(hasText()) {
			String text=getText();
			dateCode=text.substring(text.length()-lengthDateCode, text.length());
		} else {
			dateCode=getDateCode();
		}
		
		DbxRequestConfig config= new DbxRequestConfig("dropbox/TestDbxMOM");
		client = new DbxClientV2(config, ACCESS_TOKEN);
	}

	/**
	 * Text auslesen
	 * Routine-Download ausführen, Text einlesen, dateCode entfernen
	 */
	public String getMOMText() throws AccessException {
		try {
			downloadText();	//routine drin->wegmachen
			//routine();
		} catch(FileNotFoundException f) {
			f.printStackTrace();
			throw new AccessException("MOM.xml kann nicht gefunden werden");
		} catch(DbxException f) {
			f.printStackTrace();
			throw new AccessException("Kein Internetzugang");
		} catch(IOException f) {
			f.printStackTrace();
			throw new AccessException("Problem bei Zugriff auf Datei");
		}
		
		String text=getText();
		text=text.substring(0, text.length()-lengthDateCode);
		return text;
	}
	/**
	 * Text einlesen
	 * Text speichern und dann hochladen
	 */
	public void uploadMOMtext(String text) throws DbxException, IOException, DeleteErrorException {
		String input=text;
		try {
			BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(übergangslösung), StandardCharsets.UTF_8));
			out.write(input);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		@SuppressWarnings("deprecation")
		Metadata meta = client.files().delete("/"+database);
		
		InputStream in = new FileInputStream(übergangslösung);
        FileMetadata metadata = client.files().uploadBuilder("/"+database).uploadAndFinish(in);
       
		Files.delete(Paths.get(übergangslösung));
		
		saveText(input+dateCode);
	}
	/**
	 * Falls Sicherungspfad existiert, sicherungskopie erstellen
	 */
	public void sicherungskopie() {
		File bsp=new File(sicherung1+"0"+sicherung2);
		if(!bsp.exists()) {
			return;
		}
		int i=0; 
		while(true) {
			bsp=new File(sicherung1+i+sicherung2);
			if(!bsp.exists()) {break;}
			i++;
		}
		
		String input=getText();
		input=input.substring(0, input.length()-lengthDateCode);
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream((sicherung1+i+sicherung2)), StandardCharsets.UTF_8));
			out.write(input);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Routine: 
	 * Erstdownload prüfen, dateCode prüfen und evtl Folgedownload
	 */
	private void routine() throws NetworkIOException, FileNotFoundException, DbxException, IOException {
		if(!hasText()) {
			downloadText();
			return;
		}
		//DateCode auslesen
		String text=getText();
		String dateCode=text.substring(text.length()-lengthDateCode, text.length());
		int y=Integer.parseInt(dateCode.substring(1, 4));
		int m=Integer.parseInt(dateCode.substring(5, 7));
		int d=Integer.parseInt(dateCode.substring(8, 10));
		
		//Ist Routine-Download nötig?
		boolean isNeed=false;
		Date now=new Date();
		if(y<now.getYear() || m<now.getMonth() || d<now.getDay()) {
			isNeed=true;
			sicherungskopie();
		}
		if(isNeed) {
			downloadText();
		}
	}
	/**
	 * Creates a File with the given String
	 * Datecode muss vorher schon implementiert worden sein
	 */
	private void saveText(String code) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(databaseOnSystem), StandardCharsets.UTF_8));
			out.write(code);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Downloads the File, add dateCode 
	 */
	private void downloadText() throws NetworkIOException, FileNotFoundException, DbxException, IOException {
		
		DbxDownloader<FileMetadata> downloader = client.files().download("/"+database);
		
		new File(databaseOnSystem).createNewFile();
		FileOutputStream out = new FileOutputStream(databaseOnSystem);
        downloader.download(out);
        out.close();
		
		String text=getText();
		//add DateCode
		dateCode=getDateCode();
		
		saveText(text+dateCode);
	}

	/**
	 * Reads the File
	 */
	private String getText() {
		String code="";
		try {
			BufferedReader read=new BufferedReader(new InputStreamReader(new FileInputStream(databaseOnSystem), StandardCharsets.UTF_8));
			//BufferedReader read=new BufferedReader(new FileReader(database));
			//BufferedReader read=new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(database)));
			String subcode;
			while((subcode = read.readLine())!=null) {
				code+="\n"+subcode;
			}
			
			read.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
        return code;
	}
	/**
	 * Checks, whether a local file exists
	 */
	private boolean hasText() {
		boolean exists=false;
		File f = new File(databaseOnSystem);
		if(f.exists()) {
			exists=true;
		}
		return exists;
	}
	private String getDateCode() {
		Date now = new Date();
		String futDC="<"+now.getYear()+".";
		if(now.getMonth()<10) {futDC+="0";}
		futDC+=now.getMonth()+".";
		if(now.getDay()<10) {futDC+="0";}
		futDC+=now.getDay()+">";
		return futDC;
	}
	private String cutLast(String str) {
		String ret=str;

		while(ret.length()>0) {
			ret=ret.substring(0, ret.length()-1);
			if(str.charAt(ret.length()-1)==File.separatorChar) {
				break;
			}
		}
		return ret;
	}
}
