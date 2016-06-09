package cy.ac.ucy.cs.linc.storagecloud;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import cy.ac.ucy.cs.linc.storagecloud.dropbox.DropboxHandler;
import cy.ac.ucy.cs.linc.storagecloud.dropbox.exceptions.ExceptionHandler;


public class StorageCloudRunner {

	private static final String DEFAULT_CONFIG_PATH = "conf.properties";
	
	public static void main(String[] args) throws ExceptionHandler  {
		// TODO Auto-generated method stub

		//read config file
		HashMap<String,String> params = parseConfigFile(StorageCloudRunner.DEFAULT_CONFIG_PATH);
		
		if (params == null){
			System.exit(1);
		}
		
		ICloudStorageHandler handler = new DropboxHandler();
		handler.cloudStorageHandlerinit(params);
		
		String s1="C:\\Users\\panos\\Dropbox\\lol\\xsa.txt\\test.java";
		String s2="C:\\Users\\panos\\Dropbox\\epl435_Team_8.zip";
		
		String s3="C:\\Users\\panos\\workspace\\lol\\src\\package1\\da\\panos.java";
		
		String s4="C:\\Users\\panos\\Dropbox\\lol";
		String s5="C:\\Users\\panos\\Desktop\\New folder";
		String s6="C:\\Users\\panos\\Dropbox";
		String s7="C:\\Users\\panos\\Desktop\\lol\\test.java";
		//handler.addFileToContainer("test.java", s3, s1);
		//
		
		//handler.addFileToContainer(s3,s4);
		
		//handler.createContainer("xxx", s4, params);
		
	
		
		//handler.deleteFileOrDicertoryFromContainer(s4);
		
		//handler.fileMetadata(s1);
		//
		//handler.HistoryFile(s1);
		
		handler.urlShare(s2);
		//handler.CloneFileOrContainer(s1, s7);
	
	}

	private static HashMap<String,String> parseConfigFile(String path) {
		Properties proprt= new Properties();
		HashMap<String,String> params = null;
		
		try {
			InputStream input = new FileInputStream(path);
			proprt.load(input);
			
			params = new HashMap<String,String>();
			params.put("dropboxPath", proprt.getProperty("storage.cloud.path"));
			params.put("opersyst", proprt.getProperty("operating.system"));
			params.put("ACCESS_TOKEN", proprt.getProperty("storage.cloud.token"));
			params.put("extra", null);
			input.close();			
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.print("Erro Confing file not exist!!!");
		} 
		catch (IOException e) {
			e.printStackTrace();
			System.out.print("Erro Confing file can not readed!!!");
		}
		return params;
	}
}
