package com.javainuse.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.AccessDeniedException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandlerRestController {

	private static String getPath(WebRequest request) {
		String path = request.getDescription(true);
		path = path.substring(4, path.lastIndexOf(";"));
		return path;
	}

	private String[] getStackTraceAsArray(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String traceString = sw.toString();
		return traceString.split("\\R");
	}

	// Eccezione per la/le risorsa/e non trovata/e
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(ResourceNotFoundException.class)
	public ErrorResponse<String> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {

		Integer status = HttpStatus.NOT_FOUND.value();
		String path = getPath(request);

		String[] stackTraceArray = getStackTraceAsArray(ex);
		String errors = HttpStatus.NOT_FOUND.getReasonPhrase();
		String message = ex.getMessage();

		return new ErrorResponse<String>(status, errors, message, stackTraceArray, path);
	}

	// Eccezione per la validazione dei campi
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse<List<String>> validationException(MethodArgumentNotValidException ex, WebRequest request) {

		List<String> errors = new ArrayList<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String errorMessage = error.getDefaultMessage();
			errors.add(errorMessage);
		});

		Integer status = HttpStatus.BAD_REQUEST.value();
		String path = getPath(request);

		String[] stackTraceArray = getStackTraceAsArray(ex);

		String timestamp = ZonedDateTime.now(ZoneId.of("Europe/Rome"))
				.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS z"));

		return new ErrorResponse<List<String>>(timestamp, status, errors, ex.getMessage(), stackTraceArray, path);
	}

	// Viene sollevata quando il tipo di un argomento del metodo non corrisponde al
	// tipo atteso.
	// L'eccezione viene gestita quando si verifica un errore di tipo non valido
	// durante la conversione di un argomento del metodo in un tipo intero.
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ErrorResponse<String> notValidInteger(MethodArgumentTypeMismatchException ex, WebRequest request) {
		String errors = ex.getMessage();
		Integer status = HttpStatus.NOT_ACCEPTABLE.value();
		String path = getPath(request);
		String[] stackTraceArray = getStackTraceAsArray(ex);
		String timestamp = ZonedDateTime.now(ZoneId.of("Europe/Rome"))
				.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS z"));

		return new ErrorResponse<>(timestamp, status, errors, ex.getMessage(), stackTraceArray, path);
	}

	// Mancanza di parametro per il campo age "" - conversione da string a integer =
	// null -- in questo progetto non e' necessaria
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ErrorResponse<String> handleBadRequestException(MissingServletRequestParameterException ex,
			WebRequest request) {
		String path = getPath(request);
		String[] stackTraceArray = getStackTraceAsArray(ex);
		String message = ex.getMessage();
		int status = HttpStatus.NOT_ACCEPTABLE.value();
		String errors = HttpStatus.NOT_ACCEPTABLE.getReasonPhrase();

		return new ErrorResponse<String>(status, errors, message, stackTraceArray, path);
	}


	// Payload di richiesta non valido o illeggibile
	/*@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ErrorResponse<String> handleBadRequestException(HttpMessageNotReadableException ex, WebRequest request) {
	    String path = getPath(request);
	    String[] stackTraceArray = getStackTraceAsArray(ex);
	    String message = ex.getMessage();
	    int status = HttpStatus.BAD_REQUEST.value();
	    String errors = HttpStatus.BAD_REQUEST.getReasonPhrase();

	    return new ErrorResponse<String>(status, errors, message, stackTraceArray, path);
	}*/


	// Payload di richiesta non valido o illeggibile
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ErrorResponse<String> handleBadRequestException(HttpMessageNotReadableException ex, WebRequest request) {
		String path = getPath(request);
		String[] stackTraceArray = getStackTraceAsArray(ex);

		String fullMessage = ex.getMessage();
		String errorMessage = extractErrorMessage(fullMessage);

		int status = HttpStatus.BAD_REQUEST.value();
		String errors = HttpStatus.BAD_REQUEST.getReasonPhrase();

		return new ErrorResponse<>(status, errors, errorMessage, stackTraceArray, path);
	}

	private String extractErrorMessage(String fullMessage) {
		String startTag = "Cannot deserialize value of type `int` from String";
		String endTag = "not a valid `int` value";

		int startIndex = fullMessage.indexOf(startTag);
		int endIndex = fullMessage.indexOf(endTag);

		if (startIndex != -1 && endIndex != -1) {
			return fullMessage.substring(startIndex, endIndex + endTag.length());
		}

		return fullMessage;
	}

	// Viene sollevata quando non viene trovato un gestore (handler) per l'URL
	// richiesto
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(value = { NoHandlerFoundException.class })
	public ErrorResponse<String> handleBadRequestExceptions(Exception ex, WebRequest request) {

		String errors = "";
		Integer status = HttpStatus.NOT_FOUND.value();
		String path = getPath(request);
		String[] stackTraceArray = getStackTraceAsArray(ex);

		if (ex instanceof NoHandlerFoundException) {
			errors += "Invalid URL";
		}

		return new ErrorResponse<String>(status, errors, ex.getMessage(), stackTraceArray, path);
	}

	// gestice name di tipo string per i metodi getByNameByNotActive() -
	// All'inserimento di un numero viene sollevata l'eccezione -- in questo
	// progetto non e' necessaria
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse<String> handleGeneric(IllegalArgumentException ex, WebRequest request) {
		String path = getPath(request);
		String[] stackTraceArray = getStackTraceAsArray(ex);
		String message = ex.getMessage();
		int status = HttpStatus.BAD_REQUEST.value();
		String errors = HttpStatus.BAD_REQUEST.getReasonPhrase();

		return new ErrorResponse<String>(status, errors, message, stackTraceArray, path);
	}

	
	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ErrorResponse<List<String>> handleDuplicate(Exception ex, WebRequest request) {

		List<String> errors = new ArrayList<>();
		String errorMessage = "Email already exist";
		errors.add(errorMessage);

		Integer status = HttpStatus.CONFLICT.value();
		String path = getPath(request);
		String[] stackTraceArray = getStackTraceAsArray(ex);

		return new ErrorResponse<List<String>>(status, errors, ex.getMessage(), stackTraceArray, path);
	}
	

	// Generica- solo se non si verifica nessuna delle eccezioni precedentemente
	// gestite
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse<String> handleGenericException(Exception ex, WebRequest request) {
		String path = getPath(request);
		String[] stackTraceArray = getStackTraceAsArray(ex);
		String message = "Internal server error";
		int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		String errors = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();

		return new ErrorResponse<String>(status, errors, message, stackTraceArray, path);
	}

	// Viene sollevata quando l'ultimo admin tenta di cancellarsi dal sito -- (di
	// regola possono essere 5 admin per un sito)
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	public ErrorResponse<String> handleEx(Exception ex, WebRequest request) {
		String path = getPath(request);
		String[] stackTraceArray = getStackTraceAsArray(ex);
		String message = "You're the only one with type 'Admin'. We apologize, you cannot delete yourself!";
		int status = HttpStatus.NOT_ACCEPTABLE.value();
		String errors = HttpStatus.NOT_ACCEPTABLE.getReasonPhrase();

		return new ErrorResponse<String>(status, errors, message, stackTraceArray, path);
	}

	// Viene sollevata quando un admin tenta di cancellare per la seconda volta
	// tutti gli utenti e nella tabella user restano solo gli user con il type Admin
	// (usato come sicurezza per una cancellazione di massa)
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	public ErrorResponse<String> handleExAdmin(Exception ex, WebRequest request) {
		String path = getPath(request);
		String[] stackTraceArray = getStackTraceAsArray(ex);
		String message = "We apologize, you cannot delete yourself and the users with type = 'Admin'! To maintain the security of inadvertent deletion, please delete through the user ID.";
		int status = HttpStatus.NOT_ACCEPTABLE.value();
		String errors = HttpStatus.NOT_ACCEPTABLE.getReasonPhrase();

		return new ErrorResponse<String>(status, errors, message, stackTraceArray, path);
	}

	// Viene sollevata quando un admin tenta di aggiungere il 6 admin
	@ExceptionHandler(MaxAdminLimitExceededException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	public ErrorResponse<String> handleMaxRoleAdminErrorResponse(Exception ex, WebRequest request) {
		String path = getPath(request);
		String[] stackTraceArray = getStackTraceAsArray(ex);
		String message = ex.getMessage();
		int status = HttpStatus.NOT_ACCEPTABLE.value();
		String errors = HttpStatus.NOT_ACCEPTABLE.getReasonPhrase();

		return new ErrorResponse<String>(status, errors, message, stackTraceArray, path);
	}

	// Viene sollevata quando l'ultimo admin tenta di modificare il suo ruolo da
	// admin a basso livello
	@ExceptionHandler(MinAdminLimitRole.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	public ErrorResponse<String> handleMinRoleAdminErrorResponse(Exception ex, WebRequest request) {
		String path = getPath(request);
		String[] stackTraceArray = getStackTraceAsArray(ex);
		String message = ex.getMessage();
		int status = HttpStatus.NOT_ACCEPTABLE.value();
		String errors = HttpStatus.NOT_ACCEPTABLE.getReasonPhrase();

		return new ErrorResponse<String>(status, errors, message, stackTraceArray, path);
	}


// Viene sollevata quando l'utente tenta di cancellare l'ultimo libro dall'ordine
	@ExceptionHandler(MinCancelledBookLimitException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	public ErrorResponse<String> handleMinCancelledBookErrorResponse(Exception ex, WebRequest request) {
		String path = getPath(request);
		String[] stackTraceArray = getStackTraceAsArray(ex);
		String message = ex.getMessage();
		int status = HttpStatus.NOT_ACCEPTABLE.value();
		String errors = HttpStatus.NOT_ACCEPTABLE.getReasonPhrase();

		return new ErrorResponse<String>(status, errors, message, stackTraceArray, path);
	}


}
