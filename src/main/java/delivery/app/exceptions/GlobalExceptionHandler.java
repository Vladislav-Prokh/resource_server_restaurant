package delivery.app.exceptions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final MessageSource messageSource;

	public GlobalExceptionHandler(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(),
				System.currentTimeMillis());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> handleInsertionException(DataIntegrityViolationException ex, Locale locale) {
		Map<String, Object> errorDetails = new HashMap<>();
		String errorMessage = ex.getMessage();
		if (errorMessage.contains("unique constraint")) {
			errorDetails.put("error", messageSource.getMessage("error.uniqueConstraint", null, locale));
		} else if (errorMessage.contains("violates foreign key constraint")) {
			errorDetails.put("error", messageSource.getMessage("error.foreignKeyViolation", null, locale));
		}
		else if (errorMessage.contains("value too long")) {
			errorDetails.put("error", messageSource.getMessage("error.value.too.long", null, locale));
		}
		else {
			errorDetails.put("error", messageSource.getMessage("error.general", null, locale));
		}
		errorDetails.put("status", "BAD_REQUEST");
		return ResponseEntity.badRequest().body(errorDetails);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"An unexpected error occurred: " + ex.getMessage(), System.currentTimeMillis());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
				"An unexpected error occurred: " + ex.getMessage(), System.currentTimeMillis());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DeleteRecordException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Map<String, Object>> handleDeleteRecordException(DeleteRecordException ex) {
	    Map<String, Object> errorDetails = new HashMap<>();
	    errorDetails.put("status", 500);
	    errorDetails.put("message", ex.getMessage());
	    errorDetails.put("timestamp", System.currentTimeMillis());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
	}
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Map<String, Object>> handleValidationFieldException(DeleteRecordException ex, Locale locale) {
		Map<String, Object> errorDetails = new HashMap<>();
		String message = messageSource.getMessage("error.inputfield", null, locale);
		errorDetails.put("status", 500);
		errorDetails.put("message", message+ex.getMessage());
		errorDetails.put("timestamp", System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex, Locale locale) {
		Map<String, Object> errorDetails = new HashMap<>();
		Optional<FieldError> firstError = ex.getBindingResult().getFieldErrors().stream().findFirst();
		firstError.ifPresent(error -> {
			errorDetails.put("status", 500);
			errorDetails.put("message", messageSource.getMessage("invalid.params." + error.getField(), null, locale));
			errorDetails.put("timestamp", System.currentTimeMillis());
		});
		return ResponseEntity.badRequest().body(errorDetails);
	}

	@ExceptionHandler(RequestLimitException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<?> handleTooManyRequests(RequestLimitException ex) {
		Map<String, Object> errorDetails = new HashMap<>();
		errorDetails.put("status", 429);
		errorDetails.put("message", ex.getMessage());
		errorDetails.put("timestamp", System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorDetails);
	}


	@Getter
	@Setter
	@AllArgsConstructor
	public static class ErrorResponse {
		private int status;
		private String message;
		private long timestamp;
	}
}
