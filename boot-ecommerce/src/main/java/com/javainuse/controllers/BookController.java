package com.javainuse.controllers;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

import com.javainuse.details.ApiResponse;
import com.javainuse.entities.Book;
import com.javainuse.exceptions.ResourceNotFoundException;
import com.javainuse.repositories.BookRepository;

//paginazione
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
// @CrossOrigin(origins = "http://localhost:4200") // Problemi con i cors
@CrossOrigin(origins = "*")
// @RequestMapping(path = "books") // Non produce jsnon ed il punto di innesco di base non mi piace
@RequestMapping(path = "/books", produces = "application/json")
public class BookController {
	
	// gestione separata dell'array di byte
	private byte[] imageBytes;

	// non è final e la dependency injection non è fatta nel costruttore,di conseguenza non va bene per i multi threading (concorrenza)
	/* @Autowired
	private BookRepository bookRepository; */
	
	
	private final BookRepository bookRepository;
	
	@Autowired
	public BookController(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}
	

		
		@GetMapping(path = "/count")
		public ResponseEntity<Integer> countBooks() throws ResourceNotFoundException, IllegalArgumentException {
			Long count = bookRepository.count();
			if (count == 0) {
				throw new ResourceNotFoundException(
						"Unable to perform the count. No books resource was found in the database");
			} else {
				return new ResponseEntity<>(count.intValue(), HttpStatus.OK);
			}
		}	
		
	
		@GetMapping(path = "/get")
		public ResponseEntity<List<Book>> getBooks(@RequestParam("page") int page, @RequestParam("size") int size)
				throws ResourceNotFoundException, IllegalArgumentException {
			Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
			Page<Book> pagedResult = bookRepository.findAll(pageable);
			if (pagedResult.hasContent()) {
				return new ResponseEntity<>(pagedResult.getContent(), HttpStatus.OK);
			} else {
				throw new ResourceNotFoundException(
						"Unable to retrieve the page: " + page + ". No books resource was found in the database");
			}
		}
		
		
		// Non consuma json, non ha le eccezioni gestite e l'url adeguata
	/* @PostMapping("/upload")
	public void uploadImage(@RequestParam("imageFile") MultipartFile file) throws IOException {
		this.bytes = file.getBytes();
	}*/
		
		
		
		@PostMapping(path = "/upload", consumes = "multipart/form-data")
		public ResponseEntity<Object> uploadImage(@RequestParam("imageFile") MultipartFile file)
		        throws MethodArgumentNotValidException, IllegalArgumentException, IOException {
		    this.imageBytes = file.getBytes();
		    String message = "Image uploaded successfully";
		    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
		}

		
		
		
	// Non consuma json, non ha le eccezioni gestite e l'url adeguata
	 /* @PostMapping("/add")
	public void createBook(@RequestBody Book book) throws IOException {
		book.setPicByte(this.bytes);
		bookRepository.save(book);
		this.bytes = null;
	}*/
	 
	 @PostMapping(path = "/add", consumes = "application/json")
		public ResponseEntity<Object> postBook(@RequestBody @Valid Book book)
				throws MethodArgumentNotValidException, IllegalArgumentException {
		 
		 
		 	if (this.imageBytes == null) throw new IllegalArgumentException("You have to load an image before this call");
		 
		 
		 	book.setPicByte(this.imageBytes);
			bookRepository.save(book);
			this.imageBytes = null;
			String message = "Book created successfully";
			return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message));
		}
	
	 
	 
	// Aggiunto perche mancante
		@GetMapping(path = "/{id:\\d+}/one")
		public ResponseEntity<Book> getBookById(@PathVariable("id") Long id)
				throws ResourceNotFoundException, IllegalArgumentException {

			Optional<Book> optional = bookRepository.findById(id);
			Book book = optional.orElseThrow(
					() -> new ResourceNotFoundException("Unable to retrieve the resource. The book resource with ID: "
							+ id + " was not found in the database"));
			return ResponseEntity.ok().body(book);
		}
		
	
		// Non consuma json, non ha le eccezioni gestite e l'url adeguata
		/*@PutMapping("/update")
		public void updateBook(@RequestBody Book book) {
			bookRepository.save(book);
		}*/
		
		
		@PutMapping(path = "/update/{id:\\d+}", consumes = "application/json")
		public ResponseEntity<Object> putBook(@PathVariable("id") Long id, @RequestBody @Valid Book book)
				throws ResourceNotFoundException, MethodArgumentNotValidException, IllegalArgumentException {

			Optional<Book> optional = bookRepository.findById(id);
			if (optional.isPresent()) {
				Book existingBook = optional.get();
				existingBook.setName(book.getName());
				existingBook.setAuthor(book.getAuthor());
				existingBook.setPrice(book.getPrice());
				//existingBook.setPicByte(book.getPicByte());
				
				if (this.imageBytes != null) {
					existingBook.setPicByte(this.imageBytes);
				}
				

				bookRepository.save(existingBook);

				String message = "Book updated successfully";
				return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
			} else {
				throw new ResourceNotFoundException("Unable to perform the modification. The book resource with ID: "
						+ id + " was not found in the database");
			}
		}
	 
	 
	 
	
	/*
	 // getOne() deprecato 
	@DeleteMapping(path = { "/{id}" })
	public Book deleteBook(@PathVariable("id") long id) {
		Book book = bookRepository.getOne(id);
		bookRepository.deleteById(id);
		return book;
	}*/
	
	
		@DeleteMapping(path = "/{id:\\d+}/delete")
		public ResponseEntity<Object> deleteBook(@PathVariable("id") Long id)
				throws ResourceNotFoundException, IllegalArgumentException {

			Optional<Book> optional = bookRepository.findById(id);
			if (optional.isPresent()) {

				bookRepository.deleteById(id);

				String message = "Book deleted successfully";
				return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));

			} else {
				throw new ResourceNotFoundException("Unable to perform the deletion. The book resource with ID: " + id
						+ " was not found in the database");
			}
		}

		// Aggiunto perche mancante
		@DeleteMapping(path = "/deleteAll")
		public ResponseEntity<ApiResponse> deleteBooks() throws ResourceNotFoundException, IllegalArgumentException {

			List<Book> list = (List<Book>) bookRepository.findAll();
			if (!list.isEmpty()) {
				bookRepository.deleteAll(list);

				String message = "Books deleted successfully";
				return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));

			} else {
				throw new ResourceNotFoundException(
						"Unable to perform the deletion. No books resource was found in the database");
			}
		}

	
	
}
