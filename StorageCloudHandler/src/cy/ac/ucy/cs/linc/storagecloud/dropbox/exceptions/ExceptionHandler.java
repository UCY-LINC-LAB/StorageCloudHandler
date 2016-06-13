package cy.ac.ucy.cs.linc.storagecloud.dropbox.exceptions;

import java.io.IOException;

import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.dropbox.core.v2.sharing.CreateSharedLinkError;



public class ExceptionHandler extends Exception{

	public enum EXCEPTION_TYPE { GetMetadataError, NoConnection};

	private Throwable extype;
	private String msg;
	
	public ExceptionHandler(String message, Throwable cause) {
		super(message);
		extype= cause;
		
		
	}
	
	public ExceptionHandler(String message) {
        super(message);
    }

	public ExceptionHandler(String message, GetMetadataErrorException e) {
		// TODO Auto-generated constructor stub
		super(message);
	}

	public ExceptionHandler(String message, CreateSharedLinkError e) {
		// TODO Auto-generated constructor stub
		super(message);
	}
}
