package com.javainuse.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javainuse.details.ApiResponse;
import com.javainuse.entities.User;
import com.javainuse.exceptions.ResourceNotFoundException;
import com.javainuse.repositories.UserRepository;

//paginazione
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")  // Problemi con i cors
@CrossOrigin(origins = "*")
//@RequestMapping(path = "users") // Non produce jsnon ed il punto di innesco di base non mi piace
@RequestMapping(path = "/api", produces = "application/json")
public class UserController {

	// non è final e la dependency injection non è fatta nel costruttore,di conseguenza non va bene per i multi threading (concorrenza)
	/*@Autowired
	private UserRepository userRepository;*/
	
	
	private final UserRepository userRepository;;

	@Autowired
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	
	
	@GetMapping(path = "/users/count")
	public ResponseEntity<Integer> countUsers() throws ResourceNotFoundException, IllegalArgumentException {
		Long count = userRepository.count();
		if (count == 0) {
			throw new ResourceNotFoundException(
					"Unable to perform the count. No users resource was found in the database");
		} else {
			return new ResponseEntity<>(count.intValue(), HttpStatus.OK);
		}
	}

	@GetMapping(path = "/users/all")
	public ResponseEntity<List<User>> getUsers(@RequestParam("page") int page, @RequestParam("size") int size)
			throws ResourceNotFoundException, IllegalArgumentException {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		Page<User> pagedResult = userRepository.findAll(pageable);
		if (pagedResult.hasContent()) {
			return new ResponseEntity<>(pagedResult.getContent(), HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException(
					"Unable to retrieve the page: " + page + ". No users resource was found in the database");
		}
	}
	
	
	// Non consuma json, non ha le eccezioni gestite e l'url adeguata
	/*@PostMapping("/add")
	public void createUser(@RequestBody User user) {
		userRepository.save(user);
	}*/
	
	
	@PostMapping(path = "/users/create", consumes = "application/json")
	public ResponseEntity<Object> postUser(@RequestBody @Valid User user)
			throws MethodArgumentNotValidException, IllegalArgumentException {
		userRepository.save(user);
		String message = "User created successfully";
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message));
	}
	
	
	
	
	// Aggiunto perche mancante
	@GetMapping(path = "/users/{id:\\d+}/one")
	public ResponseEntity<User> getUserById(@PathVariable("id") Long id)
			throws ResourceNotFoundException, IllegalArgumentException {

		Optional<User> optional = userRepository.findById(id);
		User user = optional.orElseThrow(
				() -> new ResourceNotFoundException("Unable to retrieve the resource. The user resource with ID: "
						+ id + " was not found in the database"));
		return ResponseEntity.ok().body(user);
	}
	
	
	
	// Aggiunto perche mancante
	@PutMapping(path = "/users/{id:\\d+}/put", consumes = "application/json")
	public ResponseEntity<Object> putUser(@PathVariable("id") Long id, @RequestBody @Valid User user)
	        throws ResourceNotFoundException, MethodArgumentNotValidException, IllegalArgumentException {

	    Optional<User> optionalUser = userRepository.findById(id);
	    if (optionalUser.isPresent()) {
	        User existingUser = optionalUser.get();

	        // Verifica se l'utente sta cercando di mantenere la stessa email
	        if (existingUser.getId().equals(user.getId()) && existingUser.getEmail().equals(user.getEmail())) {
	            existingUser.setName(user.getName());
	            existingUser.setPassword(user.getPassword());

	            userRepository.save(existingUser);

	            String message = "User updated successfully";
	            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
	        } else {
	            // Verifica se l'utente sta cercando di modificare l'email
	            if (!existingUser.getEmail().equals(user.getEmail())) {
	                // Verifica se l'email è già in uso da un altro utente
	                if (userRepository.existsByEmail(user.getEmail())) {
	                    return ResponseEntity.status(HttpStatus.CONFLICT)
	                            .body(new ApiResponse(HttpStatus.CONFLICT.value(), "Email already exists"));
	                }
	            }

	            // Aggiorna gli altri campi e salva l'utente
	            existingUser.setName(user.getName());
	            existingUser.setEmail(user.getEmail());
	            existingUser.setPassword(user.getPassword());

	            userRepository.save(existingUser);

	            String message = "User updated successfully";
	            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
	        }
	    } else {
	        throw new ResourceNotFoundException("Unable to perform the modification. The user resource with ID: "
	                + id + " was not found in the database");
	    }
	}


	
	
	
	
	
	
	
	
	/*
	// getOne() deprecato , non ha le eccezioni gestite e l'url adeguata
	@DeleteMapping(path = { "/{id}" })
	public User deleteUser(@PathVariable("id") long id) {
		User user = userRepository.getOne(id);
		userRepository.deleteById(id);
		return user;
	}*/
	
	
	@DeleteMapping(path = "/users/{id:\\d+}/delete/one")
	public ResponseEntity<Object> deleteUser(@PathVariable("id") Long id)
			throws ResourceNotFoundException, IllegalArgumentException {

		Optional<User> optionalUser = userRepository.findById(id);
		if (optionalUser.isPresent()) {
			userRepository.deleteById(id);

			String message = "User deleted successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));

		} else {
			throw new ResourceNotFoundException("Unable to perform the deletion. The user resource with ID: " + id
					+ " was not found in the database");
		}
	}

	// Aggiunto perche mancante
	@DeleteMapping(path = "/users/delete/all")
	public ResponseEntity<ApiResponse> deleteUsers() throws ResourceNotFoundException, IllegalArgumentException {

		List<User> userList = (List<User>) userRepository.findAll();
		if (!userList.isEmpty()) {
			userRepository.deleteAll(userList);

			String message = "Users deleted successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));

		} else {
			throw new ResourceNotFoundException(
					"Unable to perform the deletion. No users resource was found in the database");
		}
	}


}
