package com.javainuse.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javainuse.details.ApiResponse;
import com.javainuse.entities.Discount;
import com.javainuse.entities.Email;
import com.javainuse.exceptions.ResourceNotFoundException;
import com.javainuse.repositories.EmailRepository;
import com.javainuse.services.EmailService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/emails", produces = "application/json")
public class EmailController {

	private final EmailRepository emailRepository;
	private final EmailService emailService;

	@Autowired
	public EmailController(EmailRepository emailRepository, EmailService emailService) {
		this.emailRepository = emailRepository;
		this.emailService = emailService;
	}

	@GetMapping("/{userId:\\d+}/list")
	public ResponseEntity<List<Email>> getEmails(@PathVariable("userId") Long userId)
			throws ResourceNotFoundException, IllegalArgumentException {

		List<Email> userList = (List<Email>) emailRepository.getEmailByUserId(userId);
		return Optional.of(userList)
				.filter(list -> !list.isEmpty())
				.map(list -> new ResponseEntity<>(list, HttpStatus.OK))
				.orElseThrow(() -> new ResourceNotFoundException(
						"Unable to retrieve the list. No emails resource was found in the database for the user with ID: "
								+ userId));
	}


	/*@DeleteMapping(path = "/{emailId:\\d+}/{userlId:\\d+}/delete")
	public ResponseEntity<Object> deleteEmail(@PathVariable("emailId") Long emailId, @PathVariable("userId") Long userId)
			throws ResourceNotFoundException, IllegalArgumentException {

		Optional<Email> opem = emailRepository.findByEmailIdAndUserId(emailId,userId);
		if (opem.isPresent()) {

			Email e = opem.get();

			//d.setActive(false);
			emailService.updateEmailtatus(emailId,userId,false);
			emailRepository.save(e);

			String message = "Email have been deleted successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));

		} else {
			throw new ResourceNotFoundException("Unable to perform the deletion. The email resource with ID: " + emailId
					+ " was not found in the database for the user with ID: "+userId);
		}
	}
	
	
	
	


	
	@DeleteMapping(path = "/deleteAll/{userlId:\\d+}")
	public ResponseEntity<ApiResponse> deleteEmails(@PathVariable("userId") Long userId) throws ResourceNotFoundException, IllegalArgumentException {

	    List<Email> list = (List<Email>) emailRepository.getEmailByUserId(userId);
	    
	    if (!list.isEmpty()) {
	    	
	    // Imposta isActive a false per tutti gli sconti --> cancellazione

			emailService.updateAllEmailsStatus(false);

	        String message = "Emails have been deleted successfully";
	        
	        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
	    } else {
	        throw new ResourceNotFoundException("Unable to perform the deletion. No emails resource was found in the database");
	    }
	}*/
}
