package delivery.app.exceptions;

import java.io.Serial;

public class InvalidRoleException extends Exception {

	@Serial
	private static final long serialVersionUID = 1L;
	
	public InvalidRoleException(String message) {
		super(message);
	}
	

}
