package cy.ac.ucy.cs.linc.storagecloud.dropbox.exceptions;

import java.io.IOException;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.Files.GetMetadataException;

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
}
