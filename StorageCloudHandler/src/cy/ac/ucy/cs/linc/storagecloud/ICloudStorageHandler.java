package cy.ac.ucy.cs.linc.storagecloud;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import cy.ac.ucy.cs.linc.storagecloud.dropbox.exceptions.ExceptionHandler;


public interface ICloudStorageHandler {
	
	
	/*
	 * This method initialization the right parameters
	 * 
	 * @ true if success 
	 */
	public boolean cloudStorageHandlerinit (HashMap<String,String> params);


	
	/*
	 * This method update a file from your pc to Cloud Storage.
	 * 
	 * This method use the create folder method to create the proper
	 * Folder on  Cloud Storage and update the file with user name
	 * 
	 * @ param Name - file name
	 * @ param PathFromFile - path where the file is
	 * @ param PathDestFile - path where the Cloud Storage 
	 * @ true if success 
	 * 
	 */
	public boolean addFileToContainer(String filename, String pathSrcFile, String pathDestFile)throws ExceptionHandler;
	
	

	/*
	 * This method update a file from your pc to Cloud Storage.
	 * 
	 * This method use the create folder method to create the proper
	 * Folder on  Cloud Storage 
	 * 
	 * @ param PathFromFile - path where the file is
	 * @ param PathDestFile - path where the Cloud Storage 
	 * @ true if success 
	 * 
	 */
	public boolean addFileToContainer(String pathSrcFile, String pathDestFile)throws ExceptionHandler;

	
	/*
	 * This method create folders on Storage Cloud 
	 * 
	 * This method use the create folder method to create the proper
	 * Folder on  Cloud Storage folder with container Name
	 * 
	 * @ param containerName - container Name
	 * @ param PathFromFile - path where the file is
	 * @ param PathDestFile - path where the Cloud Storage folder is
	 * @ true if success 
	 * 
	 * 
	 * 
	 */
	public boolean createContainer(String containerName, String destPath, HashMap<String,String> params)throws ExceptionHandler;
	
	/*
	 * This method delete a file from your  Cloud Storage .
	 * 
	 * This method use the delete folder or file on Cloud Storage. 
	 * 
	 *  @ param PathToDeleteFile - path where the file is
	 * 	@ true if success 
	 */
	public boolean deleteFileOrDicertoryFromContainer(String pathToDeleteFile)throws ExceptionHandler;
	
	
	/*
	 * This method Shared a Folder or file.
	 * 
	 * This method create a shared link for Folder or file
	 * 
	 * @ param PathFile - path where the folder is
	 * @ param email - collaborator's e-mail
	 * @ true if success 
	 */
	public boolean urlShare(String PathFile)throws ExceptionHandler;
		
	
	/*
	 * This method Show information for file or Folder on Storage cloud 
	 * 
	 * @ param PathFile - path where the file is
	 * @ true if success 
	 */
	public JSONObject FileMetadata(String path) throws ExceptionHandler  ;
	
	
	/*
	 * This method Show History for this file on Storage cloud.
	 * 
	 * @ param PathFile - path where the file is
	 * @ true if success 
	 */
	public ArrayList<JSONObject> HistoryFile(String pathFile)throws ExceptionHandler;
	
	
	/*
	 * This method clone a file or folder from Storage cloud local to pc.
	 * 
	 * @ param PathFile - path where the file is
	 * @ true if success 
	 */
	public boolean CloneFileOrContainer(String pathSrcFile, String pathDestFile) throws ExceptionHandler;
	
	/*
	 * This method return HashMap with AccountInfo
	 * 
	 * 
	 * @ HashMap - with info
	 */
	public  HashMap<String,String> AccountInfo() throws ExceptionHandler;
	
	

	/*
	 * This method add to param Arraylist all folders or files that include in the Project
	 * one step into Project
	 * 
	 * @ param list - put all folders or files
	 * @ param Project - the project path (empty for root)
	 * @ void 
	 */
	public void ListOfFolder(ArrayList<String> list,String Project)throws ExceptionHandler;
	
	public boolean ShareProjectWithEmail(String Project, String email)throws ExceptionHandler;
	
	/*
	 * This method add to param Arraylist all folders or files that include in the Project
	 * 
	 * @ param list - put all folders or files
	 * @ param Project - the project path (empty for root)
	 * @ void 
	 */
	public void ListOfAllFile(ArrayList<String> list,String Project)throws ExceptionHandler;
	
	
	public void RestoreHistoryFile(String path, String idcode)throws ExceptionHandler;
	
	public String StorageUsersName(String id)throws ExceptionHandler;
	
}
