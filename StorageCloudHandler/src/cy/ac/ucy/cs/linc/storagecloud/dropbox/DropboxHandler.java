package cy.ac.ucy.cs.linc.storagecloud.dropbox;

import java.awt.List;
import java.io.File;
import com.dropbox.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONObject;

import com.dropbox.core.*;
import com.dropbox.core.json.JsonReadException;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.v1.*;
import com.dropbox.core.v2.*;
import com.dropbox.core.v2.Files.CreateFolderException;
import com.dropbox.core.v2.Files.FileMetadata;
import com.dropbox.core.v2.Files.FolderMetadata;
import com.dropbox.core.v2.Files.GetMetadataException;
import com.dropbox.core.v2.Files.ListFolderException;
import com.dropbox.core.v2.Files.ListRevisionsException;
import com.dropbox.core.v2.Files.SearchBuilder;
import com.dropbox.core.v2.Files.UploadBuilder;
import com.dropbox.core.v2.Files.UploadException;
import com.dropbox.core.v2.Files.WriteMode;
import com.dropbox.core.v2.Sharing.CreateSharedLinkException;
import com.dropbox.core.v2.Sharing.PathLinkMetadata;

import cy.ac.ucy.cs.linc.storagecloud.ICloudStorageHandler;
import cy.ac.ucy.cs.linc.storagecloud.dropbox.exceptions.ExceptionHandler;


public class DropboxHandler implements ICloudStorageHandler  {

	private String Dropboxpath;
	
	private String ACCESS_TOKEN;
	private DbxRequestConfig config;
	private  DbxClientV2 client ;
	
	
	boolean ready=false;
	static char PathSeperetor;
	
	private String connentionError ="DropboxHandler>> CANNOT CONNECT to Dropbox service right now... please check your connections";
	private String loaclFileError ="DropboxHandler>> CANNOT FIND path file locally... please check your local path";
	private String dropboxFolderError ="DropboxHandler>> CANNOT Creat folder at Dropbox path...please check your Dropbox path";
	private String dropboxFileError ="DropboxHandler>> CANNOT FIND path file at Dropbox... please check your Dropbox path";
	private String shareURlError ="DropboxHandler>> CANNOT CREATE URL link for this Dropbox path file... please check your Dropbox path";
	private String dropboxListError ="DropboxHandler>> CANNOT FIND list of files at this Dropbox path ... please check your Dropbox path";
	@Override
	public boolean cloudStorageHandlerinit(HashMap<String, String> params) {
		boolean response = true;
		
		Dropboxpath = params.get("dropboxPath");
		String opersyst = params.get("opersyst");
		ACCESS_TOKEN = params.get("ACCESS_TOKEN");
		if(opersyst.equals("Windows")) {
			PathSeperetor='\\';
		}
		
		System.out.println(ACCESS_TOKEN);
		
		config= new DbxRequestConfig("dropbox/java-tutorial", "en_US");
		client = new DbxClientV2(config, ACCESS_TOKEN);
		return response;		
	}
	

	private static String dropboxPath(String path){
		if(path.contains("/")){
			return path;
		}
		String newpath= path.replace("\\","/");
		
			int k=0;
			String word = null;
			
			for(int i=0; i<path.length(); i++){
				k++;
				if(path.charAt(i)!=PathSeperetor){
					word=word+path.charAt(i);
				}
				else{
					
					if(word.contentEquals("Dropbox")){
						break;
					}
					word="";
				}
			}
			
			newpath=newpath.substring(k-1, path.length());
			
		return newpath;
	}
	
	@Override
	public boolean addFileToContainer(String filename, String pathSrcFile, String pathDestFile) throws ExceptionHandler {
	
		// at this point we use Client for Version 1 because we want to update the
		//newest version of file
		pathDestFile=dropboxPath(pathDestFile);
		System.out.println(pathDestFile);
		
		DbxClientV1 tempclient = new DbxClientV1(config, ACCESS_TOKEN);
		
		File inputFile = new File(pathSrcFile);
		
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			throw new ExceptionHandler(loaclFileError,e);
		}
		
		try {
			DbxEntry.File uploadedFile = null;
			try {
				uploadedFile = tempclient.uploadFile(pathDestFile+"/"+filename,
					    DbxWriteMode.force(), inputFile.length(), inputStream);
				} catch (DbxException e) {
					throw new ExceptionHandler(connentionError,e);
				} catch (IOException e) {
					throw new ExceptionHandler(loaclFileError,e);
				}
    
		} finally {
		    try {
				inputStream.close();
			} catch (IOException e) {
				throw new ExceptionHandler(loaclFileError,e);
			}
		}
		
		return false;
	}

	@Override
	public boolean addFileToContainer(String pathSrcFile, String pathDestFile) throws ExceptionHandler {
		// TODO Auto-generated method stub
		pathDestFile=dropboxPath(pathDestFile);
		
		DbxClientV1 tempclient = new DbxClientV1(config, ACCESS_TOKEN);
		
		File inputFile = new File(pathSrcFile);
		
		
			
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			throw new ExceptionHandler(loaclFileError,e);
		}
		
		try {
		    DbxEntry.File uploadedFile = null;
			
				try {
					uploadedFile = tempclient.uploadFile(pathDestFile,
					    DbxWriteMode.force(), inputFile.length(), inputStream);
				} catch (DbxException e) {
					throw new ExceptionHandler(connentionError,e);
				} catch (IOException e) {
					throw new ExceptionHandler(loaclFileError,e);
				}
		
		    
		} finally {
		    try {
				inputStream.close();
			} catch (IOException e) {
				throw new ExceptionHandler(loaclFileError,e);
			}
		}
		return false;
	}

	@Override
	public boolean createContainer(String containerName, String destPath, HashMap<String, String> params) throws ExceptionHandler {
		// TODO Auto-generated method stub
		destPath=dropboxPath(destPath);
		
		System.out.println(destPath);
		FolderMetadata fl=null;
		if(containerName==""){
			try {
				 fl = client.files.createFolder(destPath);
			} catch (CreateFolderException e) {
				throw new ExceptionHandler(dropboxFolderError,e);
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError,e);
			} 
		}
		else{
			try {
				fl = client.files.createFolder("/"+containerName+destPath);
			} catch (CreateFolderException e) {
				throw new ExceptionHandler(dropboxFolderError,e);
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError,e);
			} 
		}
		
		return false;
	}

	@Override
	public boolean deleteFileOrDicertoryFromContainer(String pathToDeleteFile) throws ExceptionHandler {
		pathToDeleteFile=dropboxPath(pathToDeleteFile);
		
		Files.Metadata entr = null;
		try {
			entr= client.files.delete(pathToDeleteFile);
		} catch (GetMetadataException e) {
			throw new ExceptionHandler(dropboxFileError,e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError,e);
		}
		return false;
	}

	@Override
	public String fileMetadata(String path) throws ExceptionHandler  {
		path=dropboxPath(path);

		Files.Metadata entr = null;
		try {
			entr= client.files.getMetadata(path);
		} catch (GetMetadataException e) {
			throw new ExceptionHandler(dropboxFileError,e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError,e);
		}

		String temp =entr.toString();
		temp=temp.replaceAll("FileMetadata.", "");
		temp=temp.replaceAll("FolderMetadata.", "");
		
		final JSONObject json = new JSONObject(temp);
		
		System.out.print(json);
		if (json !=null){
			return json.toString();
		}
		
		return null;
		
	}

	@Override
	public boolean urlShare(String pathFile) throws ExceptionHandler {
		// TODO Auto-generated method stub
		pathFile=dropboxPath(pathFile);
		
		PathLinkMetadata file=null;
		try {
			file = client.sharing.createSharedLink(pathFile);
		} catch (CreateSharedLinkException e) {
			throw new ExceptionHandler(shareURlError,e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError,e);
		}
		
	    System.out.println(file.url);
		return false;
	}

	@Override
	public boolean HistoryFile(String pathFile) throws ExceptionHandler {
		// TODO Auto-generated method stub
		
		pathFile=dropboxPath(pathFile);
		
		Files.Metadata entr = null;
		try {
			entr= client.files.getMetadata(pathFile);
		} catch (GetMetadataException e) {
			throw new ExceptionHandler(dropboxFileError,e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError,e);
		}
		
		
		String temp =entr.toString();
		temp=temp.replaceAll("FileMetadata.", "");
		temp=temp.replaceAll("FolderMetadata.", "");
		
		final JSONObject json = new JSONObject(temp);
	
		String type =(String) json.get(".tag");
		
		//check if path is from file or folder 
		if(type.equals("file")){
			// if is file has history 
			ArrayList<FileMetadata> test=null;
			try {
				test = client.files.listRevisions(pathFile).entries;
			} catch (ListRevisionsException e) {
				throw new ExceptionHandler(dropboxListError,e);
				
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError,e);
			}  
		       
	        for (Files.Metadata metadata : test) {
	        	String temp1=metadata.toStringMultiline();
	        	temp1=temp1.replaceAll("FileMetadata.", "");
	        	final JSONObject obj = new JSONObject(temp1);
	            System.out.println(obj);
	        } 
			
		}
		else if (type.equals("folder")){
			// if is folder has no history 
			System.out.print(type+"so NO History ");
		}
		else{
			System.out.print("Anone Type...");
		}
		
		return false;
	}

	@Override
	public boolean CloneFileOrContainer(String pathSrcFile, String pathDestFile) throws ExceptionHandler {
		pathSrcFile=dropboxPath(pathSrcFile);
		System.out.println ("ta 2 nea paths : ");
		System.out.println(pathSrcFile +"!!!!"+ pathDestFile);
		Files.Metadata entr = null;

		try {
			entr= client.files.getMetadata(pathSrcFile);
		} catch (GetMetadataException e) {
			throw new ExceptionHandler(dropboxFileError,e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError,e);
		}
		
		
		
		String temp =entr.toString();
		temp=temp.replaceAll("FileMetadata.", "");
		temp=temp.replaceAll("FolderMetadata.", "");
		
		final JSONObject json = new JSONObject(temp);
	
		String type =(String) json.get(".tag");
		
		
		if(type.equals("file")){
			
			int lastFoledr=pathDestFile.lastIndexOf(PathSeperetor);
					
			String to =pathDestFile.substring(0,lastFoledr );
			
			File file=new File(to);
			
			if(!file.exists()){
				file.mkdirs();
			}
			
			file= new File (pathDestFile); 
			OutputStream fop;
			try {
				fop = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				throw new ExceptionHandler(loaclFileError,e);
			}
		
			
	      
	       try {
			FileMetadata metadata = client.files.downloadBuilder(pathSrcFile).run(fop);
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError,e);
			} catch (IOException e) {
				throw new ExceptionHandler(loaclFileError,e);
			}
	       	System.out.println("graftike ena arxeio  :" +file);
	       	
		}else if(type.equals("folder")){
		
			ArrayList<Files.Metadata> entries=null;
			try {
				entries = client.files.listFolder(pathSrcFile).entries;
			} catch (ListFolderException e) {
				throw new ExceptionHandler(dropboxListError,e);
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError,e);
			}
	        for (Files.Metadata metadata1 : entries) {
	        	
	        	String temp1 =entr.toString();
	    		temp1=temp.replaceAll("FileMetadata.", "");
	    		temp1=temp.replaceAll("FolderMetadata.", "");
	    		
	    		final JSONObject json1 = new JSONObject(temp1);
	    		System.out.println ("json : ");
	    		String type1 =(String) json.get(".tag");
	    		System.out.println(json1);
	    		if( type1.equals("folder")){
		    		
		    		String test =pathDestFile +"\\" +metadata1.name;
		    		String s =metadata1.pathLower;
		    		CloneFileOrContainer (s,test);
	    		}
	    		else if (type1.equals("file")){
	    			System.out.print("lol");
	    		}
      
	        }
		}
		
		return false;
	}

}
