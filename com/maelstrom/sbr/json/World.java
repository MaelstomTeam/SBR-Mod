package com.maelstrom.sbr.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.minecraft.world.storage.SaveFormatComparator;

public class World {
	private String name;
	private String description;
	private List<Backup> backups;
	
	public World(String nam, String des){
		name = nam;
		description = des;
		backups = new ArrayList<Backup>();
	}

	public World(SaveFormatComparator save) {
		description = "backups of " + save.getDisplayName();
		name = save.getFileName();
	}

	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	public List<Backup> getBackups(){
		if(backups == null)
			backups = new ArrayList<Backup>();
		return backups;
	}
	
	public String toString(){
		String temp = "|-+World: "+name+"\n| |-Description: "+description+"\n";
		if(backups != null)
			for(Backup b : backups)
				temp += "| |\n" + b.toString() + "\n";
		return temp;
	}
	
	public void addBackup(Backup b){
		backups.add(b);
	}
	
	public void addBackup(List<Backup> b){
		backups.addAll(b);
	}
	
	public void createBackup(){
		
	}
}