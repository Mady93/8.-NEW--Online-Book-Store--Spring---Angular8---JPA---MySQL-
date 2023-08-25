package com.javainuse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
}
