package com.javainuse.controllers;

import java.nio.file.AccessDeniedException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.javainuse.details.ApiResponse;
import com.javainuse.entities.User;
import com.javainuse.exceptions.MaxAdminLimitExceededException;
import com.javainuse.exceptions.MinAdminLimitRole;
import com.javainuse.exceptions.ResourceNotFoundException;
import com.javainuse.repositories.UserRepository;
import com.javainuse.services.UserService;

//paginazione
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

// JWT
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/users", produces = "application/json")
public class UserController {

	private final UserRepository userRepository;
	private final UserService userService;

	private String pubKey;

	@Autowired
	public UserController(UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}

	@PostMapping(path = "/setPubKey", consumes = "text/plain")
	public ResponseEntity<Object> setPubKey(@RequestBody String key) {

		this.pubKey = key;

		return ResponseEntity.status(HttpStatus.OK).body("");

	}

	private static PublicKey getPublicKeyFromString(String keyString) throws Exception {
		keyString = keyString
				.replace("-----BEGIN PUBLIC KEY-----", "")
				.replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s", ""); // Rimuove eventuali spazi

		byte[] publicKeyBytes = Base64.getDecoder().decode(keyString);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(keySpec);
	}

	// funzione che calcola quanti secondi di validita' ha il token master
	// utilizzato per il refresh token
	long getExpInSeconds(String token) throws Exception {

		try {

			PublicKey pubKeyFromatted = getPublicKeyFromString(this.pubKey);

			Jws<Claims> jws = Jwts.parserBuilder()
					.setSigningKey(pubKeyFromatted)
					.build()
					.parseClaimsJws(token);
			Claims claims = jws.getBody();

			// Esempio: verifica che il token non sia scaduto
			Date expirationDate = claims.getExpiration();
			Date now = new Date();
			// return !expirationDate.before(now);

			long diff = expirationDate.getTime() - now.getTime();
			diff = diff / 1000;

			return diff;

		} catch (/*JwtException | IllegalArgumentException e*/Exception e) {
			// Il token è scaduto o non è valido
			return 0;
		}

	}

	@PostMapping(path = "/saveToken", consumes = "application/json")
	public ResponseEntity<Object> saveToken(@RequestBody String data)
			throws JsonMappingException, JsonProcessingException {

		Map<String, Object> req = new ObjectMapper().readValue(data, Map.class);
		Long uid = Long.parseLong(req.get("uid").toString());
		String token = req.get("token").toString();

		Optional<User> optional = userRepository.findById(uid);
		User user = optional.orElseThrow(
				() -> new ResourceNotFoundException("Unable to retrieve the resource. The user resource with ID: "
						+ uid + " was not found in the database"));

		user.setToken(token);
		userRepository.save(user);

		return ResponseEntity.status(200).body("");
	}

	@PostMapping(path = "/register", consumes = "application/json")
	public ResponseEntity<Object> register(@RequestBody @Valid User user)
			throws MethodArgumentNotValidException, IllegalArgumentException, ResourceNotFoundException {

		user.setType("User"); // Imposta il ruolo di default come 'User'
		userRepository.save(user);

		String message = "User registered successfully";
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message));
	}

	@PostMapping(path = "/login", consumes = "application/json")
	public ResponseEntity<Object> login(@RequestBody User user)
			throws MethodArgumentNotValidException, IllegalArgumentException, ResourceNotFoundException {
		User existingUser = userRepository.findUserByEmail(user.getEmail());

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode json = objectMapper.createObjectNode();

		if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
			json.put("message", "Authentication failed");

			throw new ResourceNotFoundException("The user resource was not found in the database");

		} else {
			json.put("message", "Login succeeded")
					.put("id", String.valueOf(existingUser.getId()))
					.put("role", existingUser.getType().toString());
			return ResponseEntity.status(HttpStatus.OK).body(json.toString());
		}
	}

	@GetMapping(path = "/{uid:\\d+}/getTokenTime")
	public ResponseEntity<Object> refreshToken(@PathVariable("uid") Long uid) throws Exception {

		Optional<User> optional = userRepository.findById(uid);
		User user = optional.orElseThrow(
				() -> new ResourceNotFoundException("Unable to retrieve the resource. The user resource with ID: "
						+ uid + " was not found in the database"));

		String token = user.getToken();

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode json = objectMapper.createObjectNode();
		json.put("time", getExpInSeconds(token));

		return ResponseEntity.status(HttpStatus.OK).body(json);
	}

	/*
	 @GetMapping(path = "/count")
	public ResponseEntity<Integer> countUsers() throws ResourceNotFoundException, IllegalArgumentException {
		Long count = userRepository.count();
		if (count == 0) {
			throw new ResourceNotFoundException(
					"Unable to perform the count. No users resource was found in the database");
		} else {
			return new ResponseEntity<>(count.intValue(), HttpStatus.OK);
		}
	}
	 */

	 @GetMapping(path = "/count")
	public ResponseEntity<Integer> countUsers() throws ResourceNotFoundException, IllegalArgumentException {
		Long count = userRepository.countByNotDeleted();
		if (count == 0) {
			throw new ResourceNotFoundException(
					"Unable to perform the count. No users resource was found in the database");
		} else {
			return new ResponseEntity<>(count.intValue(), HttpStatus.OK);
		}
	}

	/*
	 @GetMapping(path = "/get")
	public ResponseEntity<List<User>> getUsers(@RequestParam("page") int page, @RequestParam("size") int size)
			throws ResourceNotFoundException, IllegalArgumentException {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
		Page<User> pagedResult = userRepository.findAll(pageable);
		if (pagedResult.hasContent()) {
			return new ResponseEntity<>(pagedResult.getContent(), HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException(
					"Unable to retrieve the page: " + page + ". No users resource was found in the database");
		}
	}
	 */

	 @GetMapping(path = "/get")
	public ResponseEntity<List<User>> getUsers(@RequestParam("page") int page, @RequestParam("size") int size)
			throws ResourceNotFoundException, IllegalArgumentException {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
		Page<User> pagedResult = userRepository.findByNotDeleted(pageable);
		if (pagedResult.hasContent()) {
			return new ResponseEntity<>(pagedResult.getContent(), HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException(
					"Unable to retrieve the page: " + page + ". No users resource was found in the database");
		}
	}

	@PostMapping(path = "/add", consumes = "application/json")
	public ResponseEntity<Object> postUserAdmin(@RequestBody @Valid User user)
			throws MethodArgumentNotValidException, IllegalArgumentException, DataIntegrityViolationException {
		user.setActive(true);
		userRepository.save(user);
		String message = "User created successfully";
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message));
	}

	// Vincoli : massimo 5 admin e minimo 1 admin
	@PostMapping(path = "/setRole", consumes = "application/json")
	public ResponseEntity<Object> setRole(@RequestBody JsonNode data)
			throws MethodArgumentNotValidException, IllegalArgumentException, MaxAdminLimitExceededException,
			MinAdminLimitRole {

		Long uid = data.get("uid").asLong();
		Optional<User> optional = userRepository.findById(uid);
		User user = optional.orElseThrow(
				() -> new ResourceNotFoundException("Unable to retrieve the resource. The user resource with ID: " + uid
						+ " was not found in the database"));

		String role = data.get("role").asText();

		if ("Admin".equalsIgnoreCase(role)) {
			// Conta il numero di utenti con ruolo "admin"
			long adminCount = userRepository.countByType("Admin");

			if (adminCount >= 5) {
				throw new MaxAdminLimitExceededException("Maximum administrators limit reached!");
			}

		} else if ("User".equalsIgnoreCase(role) || "Seller".equalsIgnoreCase(role) || "Order".equalsIgnoreCase(role) || "Marketing".equalsIgnoreCase(role)) {
			// Verifica se l'utente è l'ultimo admin e sta cercando di cambiare il ruolo a
			// "User" o "Seller"
			long adminCount = userRepository.countByType("Admin");
			if (adminCount == 1 && user.getType().equalsIgnoreCase("admin")) {
				throw new MinAdminLimitRole("You cannot change the role of the last admin!");
			}
		}

		user.setType(role);
		userRepository.save(user);

		Map<String, String> responseMap = new HashMap<>();
		responseMap.put("res", "Role successfully modified!");
		return ResponseEntity.status(HttpStatus.OK).body(responseMap);
	}

	@GetMapping(path = "/{id:\\d+}/one")
	public ResponseEntity<User> getUserById(@PathVariable("id") Long id)
			throws ResourceNotFoundException, IllegalArgumentException {

		Optional<User> optional = userRepository.findById(id);
		User user = optional.orElseThrow(
				() -> new ResourceNotFoundException("Unable to retrieve the resource. The user resource with ID: "
						+ id + " was not found in the database"));
		return ResponseEntity.ok().body(user);
	}

	@PutMapping(path = "/update/{id:\\d+}", consumes = "application/json")
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

	// Entity Manager => UserService => delete user by id EXCEPT the last 'Admin'
	@DeleteMapping(path = "/{userId:\\d+}/delete")
	public ResponseEntity<Object> deleteUser(@PathVariable("userId") Long userId)
			throws ResourceNotFoundException, IllegalArgumentException, AccessDeniedException {

		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isPresent()) {
			userService.deleteUserAndRelatedData(userId);

			String message = "User and associated data have been deleted successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));

		} else {
			throw new ResourceNotFoundException("Unable to perform the deletion. The user resource with ID: " + userId
					+ " was not found in the database");
		}
	}

	// Entity Manager => UserService => DELETE ALL USERS EXCEPT ALL 'Admin'
	@DeleteMapping(path = "/deleteAll")
	public ResponseEntity<ApiResponse> deleteUsers() throws IllegalArgumentException, RuntimeException {

		userService.deleteAllExceptAdmin();
		String message = "Users and their associated data have been deleted successfully";
		return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
	}

}
