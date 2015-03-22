package com.maelstrom.sbr.json;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.world.storage.SaveFormatComparator;

public class Backup {
	
	private String date;
	private String time;
	private Boolean hasDescription = false;
	private String description = "";
	private String metaName;
	private String file;
	
	public Backup(String d){
		date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		file = date.replaceAll("/", "")+time.replaceAll(":", "");
		description = d;
		metaName = "###";
	}
	
	public Backup(String desc, String fil) {
		date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		file = fil;
		description = desc;
		metaName = "###";
	}
	
	public Backup(SaveFormatComparator save){
		date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		file = date.replaceAll("/", "")+time.replaceAll(":", "");
		metaName = save.getDisplayName();
	}

	public String getDate(){
		return date;
	}
	
	public String getName(){
		return metaName;
	}
	
	public String getTime(){
		return time;
	}
	
	public Boolean hasDescription(){
		if(!description.equals("") || !description.equals(null))
			hasDescription = true;
		return hasDescription;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getFileName(){
		return file;
	}

	public String toString(){
		String temp = "| |-+"+file+"\n"
				+ "| | |-Description: "+description+"\n"
				+ "| | |-Time: " + time+"\n"
				+ "| | |-Date: " + date;
		return temp;
	}
}
