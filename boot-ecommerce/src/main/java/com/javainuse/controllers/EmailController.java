package com.javainuse.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javainuse.entities.Email;
import com.javainuse.exceptions.ResourceNotFoundException;
import com.javainuse.repositories.EmailRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/emails", produces = "application/json")
public class EmailController {

	private final EmailRepository emailRepository;

	@Autowired
	public EmailController(EmailRepository emailRepository) {
		this.emailRepository = emailRepository;
	}
	
	@GetMapping("/{userId:\\d+}/list")
	public ResponseEntity<List<Email>> getEmails(@PathVariable("userId") Long userId) throws ResourceNotFoundException, IllegalArgumentException {

		List<Email> userList = (List<Email>) emailRepository.getEmailByUserId(userId);
		return Optional.of(userList)
				.filter(list -> !list.isEmpty())
				.map(list -> new ResponseEntity<>(list, HttpStatus.OK))
				.orElseThrow(() -> new ResourceNotFoundException(
						"Unable to retrieve the list. No emails resource was found in the database for the user with ID: "+userId));
	}
}
