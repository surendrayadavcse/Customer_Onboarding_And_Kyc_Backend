package com.kyc.onboarding.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ServiceAlreadyExistsException.class)
    public ResponseEntity<?> handleServiceAlreadyExists(ServiceAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<?> handleServiceNotFound(ServiceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

 
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DocumentStorageException.class)
    public ResponseEntity<?> handleDocumentStorage(DocumentStorageException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(OcrVerificationException.class)
    public ResponseEntity<?> handleOcrVerification(OcrVerificationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
    }
    @ExceptionHandler(InvalidDocumentFormatException.class)
    public ResponseEntity<String> handleInvalidDocFormat(InvalidDocumentFormatException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(AadhaarAlreadyVerifiedException.class)
    public ResponseEntity<String> handleAadharVerified(AadhaarAlreadyVerifiedException ex) {
    	  return ResponseEntity.badRequest().body(ex.getMessage());
    }
    
    @ExceptionHandler(PanAlreadyVerifiedException.class)
    public ResponseEntity<String> handlePanVerified(PanAlreadyVerifiedException ex) {
    	return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(KycDocumentNotFoundException.class)
    public ResponseEntity<String> handleKycDocumentNotFound(KycDocumentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<String> handleEncryptionException(EncryptionException ex) {
        return new ResponseEntity<>("Encryption/Decryption error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
   
}
