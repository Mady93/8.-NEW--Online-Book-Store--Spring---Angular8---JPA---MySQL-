package com.javainuse.controllers;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.catalina.connector.Response;
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
import com.javainuse.exceptions.ResourceNotFoundException;
import com.javainuse.repositories.UserRepository;

//paginazione
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

// JWT
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")  // Problemi con i cors
@CrossOrigin(origins = "*")
//@RequestMapping(path = "users") // Non produce jsnon ed il punto di innesco di base non mi piace
@RequestMapping(path = "/users", produces = "application/json")
public class UserController {

	// non è final e la dependency injection non è fatta nel costruttore,di conseguenza non va bene per i multi threading (concorrenza)
	/*@Autowired
	private UserRepository userRepository;*/
	
	
	private final UserRepository userRepository;
	private String pubKey;


	@Autowired
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}


	@PostMapping(path = "/setPubKey", consumes = "text/plain")
	public ResponseEntity<Object> setPubKey(@RequestBody String key){

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


	/* funzione che calcola quanti secondi di validita' ha il token master utilizzato per il refresh token */
	long getExpInSeconds(String token) throws Exception{



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
            //return !expirationDate.before(now);

			long diff = expirationDate.getTime() - now.getTime();
			diff = diff / 1000;

			return diff;

        } catch (JwtException | IllegalArgumentException e) {
            // Il token è scaduto o non è valido
            //return false;
			return 0;
        }

	}


	@PostMapping(path = "/saveToken", consumes = "application/json")
	public ResponseEntity<Object> saveToken(@RequestBody String data) throws JsonMappingException, JsonProcessingException{


		Map<String, Object> req = new ObjectMapper().readValue(data, Map.class);
		Long uid = Long.parseLong(req.get("uid").toString());
		String token = req.get("token").toString();

		Optional<User> optional = userRepository.findById(uid);
		User user = optional.orElseThrow(
				() -> new ResourceNotFoundException("Unable to retrieve the resource. The user resource with ID: "
						+ uid + " was not found in the database"));

		user.setToken(token);
		userRepository.save(user);
		//userRepository.save(user);

		return ResponseEntity.status(200).body("");
	}


	
	@PostMapping(path = "/login", consumes = "application/json")
    public ResponseEntity<Object> login(@RequestBody User user) throws MethodArgumentNotValidException, IllegalArgumentException, ResourceNotFoundException {
        HttpStatus retStatus = HttpStatus.OK;
        User x = this.userRepository.findUserByEmail(user.getEmail());

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        System.out.println(user.toString());
        System.out.println(x != null ? x.toString() : "User not found");

        if (x == null || !x.getPassword().equals(user.getPassword())) {
            json.put("msg", "Authentication failed");
            retStatus = HttpStatus.FORBIDDEN;
        } else {
            json.put("msg", "Login succeeded")
                .put("id", String.valueOf(x.getId()))
                .put("role", x.getType().toString());
        }

        return ResponseEntity.status(retStatus).body(json);
    }




	@PostMapping(path = "/register", consumes = "application/json")
    public ResponseEntity<Object> register(@RequestBody @Valid User user) throws MethodArgumentNotValidException, IllegalArgumentException, ResourceNotFoundException {
        HttpStatus retStatus = HttpStatus.CREATED;
        

		userRepository.save(user);


        return ResponseEntity.status(retStatus).body("");
    }
	

	
	@GetMapping(path = "/{uid:\\d+}/getTokenTime")
	public ResponseEntity<Object> refreshToken(@PathVariable Long uid) throws Exception {
        
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
	
	
	// Non consuma json, non ha le eccezioni gestite e l'url adeguata
	/*@PostMapping("/add")
	public void createUser(@RequestBody User user) {
		userRepository.save(user);
	}*/
	
	
	// metodo per admin - crea l'utente
	@PostMapping(path = "/add", consumes = "application/json")
	public ResponseEntity<Object> postUserAdmin(@RequestBody @Valid User user)
			throws MethodArgumentNotValidException, IllegalArgumentException, DataIntegrityViolationException {

		userRepository.save(user);
		String message = "User created successfully";
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message));
	}
	
	

	// metodo per user - crea se stesso
	@PostMapping(path = "/addUserItSelf", consumes = "application/json")
	public ResponseEntity<Object> postUserItSelf(@RequestBody @Valid User user)
			throws MethodArgumentNotValidException, IllegalArgumentException, DataIntegrityViolationException{

				user.setType("User");

		userRepository.save(user);
		String message = "User created successfully";
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message));
	}


	// metodo per admin - l'unico che puo modificare il ruolo
	@PostMapping(path = "/customers/setRole", consumes = "application/json")
	public ResponseEntity<Object> setRole(@RequestBody JsonNode data)
			throws MethodArgumentNotValidException, IllegalArgumentException {

		String code = data.get("code").asText();
		String email = data.get("email").asText();

		User u = this.userRepository.findUserByType(code);

		if (u == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
		User user = this.userRepository.findUserByEmail(email);
		if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		user.setType(u.getType());

		userRepository.save(user);

		return ResponseEntity.status(HttpStatus.OK).body("");
	}



	
	
	// Aggiunto perche mancante
	@GetMapping(path = "/{id:\\d+}/one")
	public ResponseEntity<User> getUserById(@PathVariable("id") Long id)
			throws ResourceNotFoundException, IllegalArgumentException {

		Optional<User> optional = userRepository.findById(id);
		User user = optional.orElseThrow(
				() -> new ResourceNotFoundException("Unable to retrieve the resource. The user resource with ID: "
						+ id + " was not found in the database"));
		return ResponseEntity.ok().body(user);
	}
	
	
	
	// Aggiunto perche mancante
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
	
	
	@DeleteMapping(path = "/{id:\\d+}/delete")
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
	@DeleteMapping(path = "/deleteAll")
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