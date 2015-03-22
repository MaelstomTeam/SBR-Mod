package com.maelstrom.sbr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.minecraft.client.Minecraft;
public class Zipper {
	
	public static void zipFolder(File srcFolder, File destZipFile) throws Exception{
		File f = new File(destZipFile.getParent());
		if(!f.exists())
			f.mkdirs();
		FileOutputStream fileWriter = new FileOutputStream(destZipFile);
		ZipOutputStream zip = new ZipOutputStream(fileWriter);
		
		addFolderToZip("", srcFolder.getPath(), zip);
		
		zip.flush();
		zip.close();
	}
	
	@SuppressWarnings("resource")
	static void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception{
		File folder = new File(srcFile);
		if(folder.isDirectory())
			addFolderToZip(path, srcFile, zip);
		else{
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path+"/"+folder.getName()));
			while((len = in.read(buf))>0){
				zip.write(buf, 0, len);
			}
		}
	}
	
	static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);
		for(String fileName : folder.list()){
			if(path.equals(""))
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			else
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
		}
	}
	
	
	
	
	public static void unZip(File in){
		File out = new File(Minecraft.getMinecraft().mcDataDir, "/saves/");
		byte[] buffer = new byte[1024];
		try{
			if(!out.exists()){
				out.mkdirs();
			}
			ZipInputStream zis = new ZipInputStream(new FileInputStream(in));
			ZipEntry ze = zis.getNextEntry();
			
			while(ze!=null){
				
				String fileName = ze.getName();
				File newFile = new File(out.getAbsoluteFile() + File.separator + fileName);
				new File(newFile.getParent()).mkdir();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while((len = zis.read(buffer)) > 0){
					fos.write(buffer, 0, len);
				}
				fos.close();
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
}