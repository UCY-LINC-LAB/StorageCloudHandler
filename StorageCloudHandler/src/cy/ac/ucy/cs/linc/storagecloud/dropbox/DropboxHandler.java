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
import java.util.LinkedList;
import org.json.JSONObject;

import com.dropbox.core.*;
import com.dropbox.core.json.JsonReadException;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.v1.*;
import com.dropbox.core.v2.*;
import com.dropbox.core.v2.files.CreateFolderErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.ListRevisionsError;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.AccessLevel;
import com.dropbox.core.v2.sharing.AddFolderMemberErrorException;
import com.dropbox.core.v2.sharing.AddMember;
import com.dropbox.core.v2.sharing.CreateSharedLinkErrorException;
import com.dropbox.core.v2.sharing.MemberSelector;
import com.dropbox.core.v2.sharing.ShareFolderErrorException;
import com.dropbox.core.v2.sharing.ShareFolderLaunch;
import com.dropbox.core.v2.sharing.SharedFolderMetadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.users.BasicAccount;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.v2.users.SpaceUsage;

import cy.ac.ucy.cs.linc.storagecloud.ICloudStorageHandler;
import cy.ac.ucy.cs.linc.storagecloud.dropbox.exceptions.ExceptionHandler;

public class DropboxHandler implements ICloudStorageHandler {

	private String Dropboxpath;

	private String ACCESS_TOKEN;
	private DbxRequestConfig config;
	private DbxClientV2 client;

	boolean ready = false;
	static char PathSeperetor;

	private String connentionError = "DropboxHandler>> CANNOT CONNECT to Dropbox service right now... please check your connections";
	private String loaclFileError = "DropboxHandler>> CANNOT FIND path file locally... please check your local path";
	private String dropboxFolderError = "DropboxHandler>> CANNOT Creat folder at Dropbox path...please check your Dropbox path";
	private String dropboxFileError = "DropboxHandler>> CANNOT FIND path file at Dropbox... please check your Dropbox path";
	private String shareURlError = "DropboxHandler>> CANNOT CREATE URL link for this Dropbox path file... please check your Dropbox path";
	private String shareError = "DropboxHandler>> CANNOT CREATE URL link for this Dropbox path file... please check your Dropbox path folder only";
	private String dropboxListError = "DropboxHandler>> CANNOT FIND list of files at this Dropbox path ... please check your Dropbox path";
	private String addMemberForShare = "DropboxHandler>> CANNOT add member for share Project Folder ... please check email";

	@Override
	public boolean cloudStorageHandlerinit(HashMap<String, String> params) {
		boolean response = true;

		Dropboxpath = params.get("dropboxPath");
		String opersyst = params.get("opersyst");
		ACCESS_TOKEN = params.get("ACCESS_TOKEN");
		if (opersyst.equals("Windows")) {
			PathSeperetor = '\\';
		}

		System.out.println(ACCESS_TOKEN);

		config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
		client = new DbxClientV2(config, ACCESS_TOKEN);
		return response;
	}

	private static String dropboxPath(String path) {
		if (path.contains("/")) {
			return path;
		}
		String newpath = path.replace("\\", "/");

		int k = 0;
		String word = null;

		for (int i = 0; i < path.length(); i++) {
			k++;
			if (path.charAt(i) != PathSeperetor) {
				word = word + path.charAt(i);
			} else {

				if (word.contentEquals("Dropbox")) {
					break;
				}
				word = "";
			}
		}

		newpath = newpath.substring(k - 1, path.length());

		return newpath;
	}

	@Override
	public boolean addFileToContainer(String filename, String pathSrcFile, String pathDestFile)
			throws ExceptionHandler {

		// at this point we use Client for Version 1 because we want to update
		// the
		// newest version of file

		DbxClientV1 tempclient = new DbxClientV1(config, ACCESS_TOKEN);

		File inputFile = new File(pathSrcFile);

		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			throw new ExceptionHandler(loaclFileError, e);
		}

		try {
			DbxEntry.File uploadedFile = null;
			try {
				uploadedFile = tempclient.uploadFile(pathDestFile + "/" + filename, DbxWriteMode.force(),
						inputFile.length(), inputStream);
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError, e);
			} catch (IOException e) {
				throw new ExceptionHandler(loaclFileError, e);
			}

		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new ExceptionHandler(loaclFileError, e);
			}
		}

		return false;
	}

	@Override
	public boolean addFileToContainer(String pathSrcFile, String pathDestFile) throws ExceptionHandler {
		// TODO Auto-generated method stub

		DbxClientV1 tempclient = new DbxClientV1(config, ACCESS_TOKEN);

		File inputFile = new File(pathSrcFile);

		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			throw new ExceptionHandler(loaclFileError, e);
		}

		try {
			DbxEntry.File uploadedFile = null;

			try {
				uploadedFile = tempclient.uploadFile(pathDestFile, DbxWriteMode.force(), inputFile.length(),
						inputStream);
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError, e);
			} catch (IOException e) {
				throw new ExceptionHandler(loaclFileError, e);
			}

		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new ExceptionHandler(loaclFileError, e);
			}
		}
		return false;
	}

	@Override
	public boolean createContainer(String containerName, String destPath, HashMap<String, String> params)
			throws ExceptionHandler {
		// TODO Auto-generated method stub

		FolderMetadata fl = null;
		if (containerName == "") {
			try {
				fl = client.files().createFolder(destPath);
			} catch (CreateFolderErrorException e) {
				throw new ExceptionHandler(dropboxFolderError, e);
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError, e);
			}
		} else {
			try {
				fl = client.files().createFolder(destPath + "/" + containerName);
			} catch (CreateFolderErrorException e) {
				throw new ExceptionHandler(dropboxFolderError, e);
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError, e);
			}
		}

		return false;
	}

	@Override
	public boolean deleteFileOrDicertoryFromContainer(String pathToDeleteFile) throws ExceptionHandler {

		Metadata entr = null;
		try {
			entr = client.files().delete(pathToDeleteFile);
		} catch (GetMetadataErrorException e) {
			throw new ExceptionHandler(dropboxFileError, e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError, e);
		}
		return false;
	}

	@Override
	public JSONObject FileMetadata(String path) throws ExceptionHandler {

		Metadata entr = null;
		try {
			entr = client.files().getMetadata(path);
		} catch (GetMetadataErrorException e) {
			throw new ExceptionHandler(dropboxFileError, e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError, e);
		}

		String temp = entr.toString();

		final JSONObject json = new JSONObject(temp);

		// System.out.println(json.toString());
		if (json != null) {
			return json;
		}

		return null;

	}

	@Override
	public boolean urlShare(String pathFile) throws ExceptionHandler {
		// TODO Auto-generated method stub

		SharedLinkMetadata file = null;
		try {
			file = client.sharing().createSharedLinkWithSettings(pathFile);
		} catch (CreateSharedLinkErrorException e) {
			throw new ExceptionHandler(shareURlError, e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError, e);
		}

		System.out.println(file.getUrl());
		return false;
	}

	@Override
	public ArrayList<JSONObject> HistoryFile(String pathFile) throws ExceptionHandler {
		// TODO Auto-generated method stub
		ArrayList<JSONObject> History=new ArrayList<JSONObject>();
		Metadata entr = null;
		try {
			entr = client.files().getMetadata(pathFile);
		} catch (GetMetadataErrorException e) {
			throw new ExceptionHandler(dropboxFileError, e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError, e);
		}

		String temp = entr.toString();
		

		final JSONObject json = new JSONObject(temp);

		String type =  json.getString(".tag");

		// check if path is from file or folder
		if (type.equals("file")) {
			// if is file has history

			// java.util.List<FileMetadata> md
			// =client.files().listRevisions(pathFile).getEntries();
			ArrayList<FileMetadata> test = null;
			try {
				// test = client.files().listRevisions(pathFile).entries;
				test = (ArrayList<FileMetadata>) client.files().listRevisions(pathFile).getEntries();
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError, e);
			}

			for (Metadata metadata : test) {
				String temp1 = metadata.toStringMultiline();
				temp1 = temp1.replaceAll("FileMetadata.", "");
				final JSONObject obj = new JSONObject(temp1);
				History.add(obj);
			}

		} else if (type.equals("folder")) {
			// if is folder has no history
			System.out.print(type + "so NO History ");
		} else {
			System.out.print("Anone Type...");
		}

		return History;
	}

	@Override
	public boolean CloneFileOrContainer(String pathSrcFile, String pathDestFile) throws ExceptionHandler {

		// System.out.println ("ta 2 nea paths : ");
		// System.out.println(pathSrcFile +"!!!!"+ pathDestFile);
		Metadata entr = null;

		try {
			entr = client.files().getMetadata(pathSrcFile);
		} catch (GetMetadataErrorException e) {
			throw new ExceptionHandler(dropboxFileError, e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError, e);
		}

		String temp = entr.toString();
		temp = temp.replaceAll("FileMetadata.", "");
		temp = temp.replaceAll("FolderMetadata.", "");

		final JSONObject json = new JSONObject(temp);

		String type = (String) json.get(".tag");

		if (type.equals("file")) {

			int lastFoledr = pathDestFile.lastIndexOf(PathSeperetor);

			String to = pathDestFile.substring(0, lastFoledr);

			File file = new File(to);

			if (!file.exists()) {
				file.mkdirs();
			}

			file = new File(pathDestFile);
			OutputStream fop;
			try {
				fop = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				throw new ExceptionHandler(loaclFileError, e);
			}

			DbxDownloader<FileMetadata> meta;
			try {
				// FileMetadata metadata =
				// client.files().downloadBuilder(pathSrcFile).run(fop);
				meta = client.files().download(pathSrcFile);
				meta.download(fop);
			} catch (DbxException | IOException e) {
				throw new ExceptionHandler(connentionError, e);
			}
			System.out.println("graftike ena arxeio  :" + file);

		} else if (type.equals("folder")) {

			ArrayList<Metadata> entries = null;
			try {
				entries = (ArrayList<Metadata>) client.files().listFolder(pathSrcFile).getEntries();
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError, e);
			}
			for (Metadata metadata1 : entries) {

				String temp1 = entr.toString();
				temp1 = temp.replaceAll("FileMetadata.", "");
				temp1 = temp.replaceAll("FolderMetadata.", "");

				final JSONObject json1 = new JSONObject(temp1);
				// System.out.println ("json : ");
				String type1 = (String) json.get(".tag");
				// System.out.println(json1);
				if (type1.equals("folder")) {

					String test = pathDestFile + "\\" + metadata1.getName();
					String s = metadata1.getPathLower();
					CloneFileOrContainer(s, test);
				} else if (type1.equals("file")) {
					System.out.print("lol");
				}

			}
		}

		return false;
	}

	@Override
	public HashMap<String, String> AccountInfo() throws ExceptionHandler {
		HashMap<String, String> info = new HashMap<String, String>();
		FullAccount account = null;
		try {
			account = client.users().getCurrentAccount();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			throw new ExceptionHandler(connentionError, e);
		}
		info.put("name", account.getName().getDisplayName());
		info.put("e-mail", account.getEmail());
		info.put("Dropbox Type", account.getAccountType().toString());

		SpaceUsage account2;
		try {
			account2 = client.users().getSpaceUsage();
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError, e);
		}

		double allocated = account2.getAllocation().getIndividualValue().getAllocated();
		double allocated_GB = allocated / 1073741824;
		info.put("Allocated_GB", String.valueOf(allocated_GB));

		double used = account2.getUsed();
		double used_GB = used / 1073741824;
		info.put("used_GB", String.valueOf(used_GB));

		return info;
	}

	@Override
	public void ListOfFolder(ArrayList<String> list, String Project) throws ExceptionHandler {
		// TODO Auto-generated method stub

		ListFolderResult result;
		try {
			result = client.files().listFolder(Project);
		} catch (ListFolderErrorException e) {
			throw new ExceptionHandler(dropboxListError, e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError, e);
		}

		for (Metadata metadata : result.getEntries()) {
			String tempPath = metadata.getPathLower();
			Metadata entr;
			try {
				entr = client.files().getMetadata(tempPath);

			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError, e);
			}
			String temp = entr.toString();

			final JSONObject json2 = new JSONObject(temp);

			String type = json2.getString(".tag");

			if (type.equals("folder")) {
				list.add(metadata.getName());
			}
		}

	}

	@Override
	public boolean ShareProjectWithEmail(String Project, String email) throws ExceptionHandler {

		Metadata md = null;
		try {
			md = client.files().getMetadata(Project);
		} catch (GetMetadataErrorException e) {
			throw new ExceptionHandler(dropboxFileError, e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError, e);
		}
		final JSONObject json = new JSONObject(md.toString());

		String SharedFolderId = null;

		if (json.has("shared_folder_id")) {
			SharedFolderId = json.getString("shared_folder_id");
		} else {
			ShareFolderLaunch l = null;
			try {
				l = client.sharing().shareFolder(Project);
			} catch (ShareFolderErrorException e) {
				throw new ExceptionHandler(shareError, e);
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError, e);
			}
			SharedFolderMetadata md2 = l.getCompleteValue();

			SharedFolderId = md2.getSharedFolderId();
		}

		ArrayList<AddMember> list = new ArrayList<AddMember>();
		AddMember a1 = new AddMember(MemberSelector.email(email), AccessLevel.EDITOR);

		list.add(a1);

		try {
			client.sharing().addFolderMember(SharedFolderId, list);
		} catch (AddFolderMemberErrorException e) {
			throw new ExceptionHandler(addMemberForShare, e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError, e);
		}

		return false;
	}

	@Override
	public void ListOfAllFile(ArrayList<String> list, String Path) throws ExceptionHandler {
		// TODO Auto-generated method stub
		Metadata entr = null;
		try {
			entr = client.files().getMetadata(Path);
		} catch (GetMetadataErrorException e) {
			throw new ExceptionHandler(dropboxFileError, e);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError, e);
		}

		String temp = entr.toString();

		final JSONObject json = new JSONObject(temp);
		String type = json.getString(".tag");
		if (type.equals("file")) {

			String pathfile = json.getString("path_display");
			list.add(pathfile);

		} else if (type.equals("folder")) {
			ListFolderResult result;
			try {
				result = client.files().listFolder(Path);
			} catch (ListFolderErrorException e) {
				throw new ExceptionHandler(dropboxListError, e);
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError, e);
			}

			for (Metadata metadata : result.getEntries()) {
				String tempPath = metadata.getPathLower();
				try {
					entr = client.files().getMetadata(tempPath);

				} catch (DbxException e) {
					throw new ExceptionHandler(connentionError, e);
				}
				temp = entr.toString();

				final JSONObject json2 = new JSONObject(temp);

				type = json2.getString(".tag");

				if (type.equals("file")) {
					String pathfile = (String) json2.get("path_display");
					list.add(pathfile);
				} else if (type.equals("folder")) {

					String folderfile = (String) json2.get("path_display");
					ListOfAllFile(list, folderfile);
				}
			}
		}

	}

	@Override
	public void RestoreHistoryFile(String path, String idcode) throws ExceptionHandler {
		FileMetadata metadata;
		 try {
			 metadata = client.files().restore(path, idcode);
		} catch (DbxException e) {
			throw new ExceptionHandler(connentionError, e);
		}
		
	}

	@Override
	public String StorageUsersName(String id) throws ExceptionHandler {
		
		if(!id.equals("")){
			BasicAccount account2;
			try {
				account2 =client.users().getAccount(id);
			} catch (DbxException e) {
				throw new ExceptionHandler(connentionError, e);
			}
			return account2.getName().getDisplayName();
		}else{
			FullAccount account = null;
			try {
				account = client.users().getCurrentAccount();
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				throw new ExceptionHandler(connentionError, e);
			}
			return account.getName().getDisplayName();
		}
	}

}
