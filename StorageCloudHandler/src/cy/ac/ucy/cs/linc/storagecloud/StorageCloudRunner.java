package cy.ac.ucy.cs.linc.storagecloud;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import org.json.JSONObject;

import cy.ac.ucy.cs.linc.storagecloud.dropbox.DropboxHandler;
import cy.ac.ucy.cs.linc.storagecloud.dropbox.exceptions.ExceptionHandler;


public class StorageCloudRunner {

	private static final String DEFAULT_CONFIG_PATH = "conf.properties";

	public static void main(String[] args) throws ExceptionHandler {
		// TODO Auto-generated method stub

		// read config file
		HashMap<String, String> params = parseConfigFile(StorageCloudRunner.DEFAULT_CONFIG_PATH);

		if (params == null) {
			System.exit(1);
		}
		System.out.println("Dropbox Api 2.0.1");
		ICloudStorageHandler handler = new DropboxHandler();
		handler.cloudStorageHandlerinit(params);

		String s1 = "/MyProject";
		String s2 = "C:\\Users\\panos\\workspace\\lol\\src\\package1\\da\\panos.java";
		String s3 = "C:\\Users\\panos\\Desktop\\panos.java";

		// handler.addFileToContainer("test.java", s2, s1);//correct

		// handler.addFileToContainer(s2,s1); //correct

		// handler.createContainer("xxx", s1, params);//correct
		// create s1 path and if "" not null create new folder with name at the
		// end of path

		// handler.deleteFileOrDicertoryFromContainer(s1); //correct

		//System.out.println(handler.FileMetadata(s1).toString()); //correct

		// ArrayList<JSONObject> temp;
		// temp=handler.HistoryFile(s1); //correct
		// for(int i=0;i<temp.size();i++){
		// 		System.out.println(temp.get(i).toString());
		// }
		
		//handler.RestoreHistoryFile(s1, "849ab4182");
		// handler.urlShare(s1);//correct

		// handler.CloneFileOrContainer(s1, s3);//correct

//		 ArrayList<String> l = new ArrayList<String>();
//		 String Project = "/MyProject";
//		 handler.ListOfFolder(l, Project);// correct
//		 for (int i = 0; i < l.size(); i++) {
//		 System.out.println(l.get(i));
//		 }

		// HashMap<String,String> info =handler.AccountInfo();//correct
		//
		// System.out.println("name : "+info.get("name"));
		// System.out.println("e-mail : "+ info.get("e-mail"));
		// System.out.println("Type :" +info.get("Dropbox Type"));
		// System.out.println("allocated :" +info.get("Allocated_GB"));
		// System.out.println("used :" +info.get("used_GB"));
		//
		//handler.ShareProjectWithEmail(s1, "dtrihinas@hotmail.com");//correct

		ArrayList<String> l = new ArrayList<String>();

		handler.ListOfAllFile(l, s1);// correct

		for (int i = 0; i < l.size(); i++) {
			System.out.println(l.get(i));
		}
		
//		String id="dbid:AACSiyEOnJM6tAqqmHBebEc8WmghIkNO6sg";
//		System.out.print(handler.StorageUsersName(id));
		
	}

	private static HashMap<String, String> parseConfigFile(String path) {
		Properties proprt = new Properties();
		HashMap<String, String> params = null;

		try {
			InputStream input = new FileInputStream(path);
			proprt.load(input);

			params = new HashMap<String, String>();
			params.put("dropboxPath", proprt.getProperty("storage.cloud.path"));
			params.put("opersyst", proprt.getProperty("operating.system"));
			params.put("ACCESS_TOKEN", proprt.getProperty("storage.cloud.token"));
			params.put("extra", null);
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.print("Erro Confing file not exist!!!");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.print("Erro Confing file can not readed!!!");
		}
		return params;
	}
}
