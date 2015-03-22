package com.maelstrom.sbr.json;

import java.util.ArrayList;
import java.util.List;

public class WorldArray {
	private List<World> worlds;
	
	public WorldArray(){
		worlds = new ArrayList<World>();
	}
	
	public List<World> getWorlds(){
		if(worlds == null){
			worlds = new ArrayList<World>();
		}
		return worlds;
	}
	
	public String toString(){
		String temp = "=======WorldArray=======\n+\n";
		for(World w : worlds){
			temp += "|\n";
			temp += w.toString();
		}
		temp += "=======EndArray=======";
		return temp;
	}
	
	public void addWorld(World w){
		if(getWorld(w.getName())!=null){
			getWorld(w.getName()).addBackup(w.getBackups());
		}else{
			worlds.add(w);
		}
	}

	public World getWorld(String name) {
		if(worlds != null)
			for(World w : getWorlds())
				if(w.getName().equals(name))
					return w;
		return null;
	}
}
