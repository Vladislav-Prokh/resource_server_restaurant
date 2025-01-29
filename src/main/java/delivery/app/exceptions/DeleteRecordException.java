package delivery.app.exceptions;

import java.io.Serial;

public class DeleteRecordException extends RuntimeException{
	@Serial
    private static final long serialVersionUID = 1L;
	public DeleteRecordException(String message) {
        super(message);
    }
}
