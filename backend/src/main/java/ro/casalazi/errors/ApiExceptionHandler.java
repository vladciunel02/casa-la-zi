package ro.casalazi.errors;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ro.casalazi.errors.firme.FirmaNegasitaException;

import ro.casalazi.errors.firme.CuiDuplicatException;
import ro.casalazi.errors.firme.CuiInvalidException;
import ro.casalazi.errors.firme.DateFirmaInvalideException;
import ro.casalazi.errors.anaf.AnafFirmaNegasitaException;
import ro.casalazi.errors.anaf.AnafIndisponibilException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.UUID;

/** traduce exceptiile aplicatiei in raspunsuri http problemdetail. */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FirmaNegasitaException.class)
    public ProblemDetail firmaNegasita(FirmaNegasitaException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DateFirmaInvalideException.class)
    public ProblemDetail dateInvalide(DateFirmaInvalideException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setProperty("erori", exception.getErori());
        return problem;
    }

    @ExceptionHandler(CuiInvalidException.class)
    public ProblemDetail cuiInvalid(CuiInvalidException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CuiDuplicatException.class)
    public ProblemDetail cuiDuplicat(CuiDuplicatException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(AnafFirmaNegasitaException.class)
    public ProblemDetail anafNegasita(AnafFirmaNegasitaException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(AnafIndisponibilException.class)
    public ProblemDetail anafIndisponibil(AnafIndisponibilException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Corpul cererii trebuie să fie un JSON valid, cu tipurile de câmp corecte.");
        return handleExceptionInternal(exception, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String detail = UUID.class.equals(exception.getRequiredType())
                ? "Id-ul firmei nu e valid."
                : "Un parametru al cererii nu este valid.";
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        return handleExceptionInternal(exception, problem, headers, HttpStatus.BAD_REQUEST, request);
    }
}
