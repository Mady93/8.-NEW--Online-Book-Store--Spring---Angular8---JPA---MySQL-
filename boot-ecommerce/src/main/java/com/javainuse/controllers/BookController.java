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
import com.javainuse.services.BookService;

//paginazione
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/books", produces = "application/json")
public class BookController {

	// gestione separata dell'array di byte
	private byte[] imageBytes;

	private final BookRepository bookRepository;
	private final BookService bookService;

	@Autowired
	public BookController(BookRepository bookRepository, BookService bookService) {
		this.bookRepository = bookRepository;
		this.bookService = bookService;
	}

	@GetMapping(path = "/count")
	public ResponseEntity<Integer> countBooks() throws ResourceNotFoundException, IllegalArgumentException {
		Long count = bookRepository.countNotDeleted();
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
		Page<Book> pagedResult = bookRepository.findByNotDeleted(pageable);
		if (pagedResult.hasContent()) {
			return new ResponseEntity<>(pagedResult.getContent(), HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException(
					"Unable to retrieve the page: " + page + ". No books resource was found in the database");
		}
	}

	@PostMapping(path = "/upload", consumes = "multipart/form-data")
	public ResponseEntity<Object> uploadImage(@RequestParam("imageFile") MultipartFile file)
			throws MethodArgumentNotValidException, IllegalArgumentException, IOException {
		this.imageBytes = file.getBytes();
		String message = "Image uploaded successfully";
		return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
	}

	@PostMapping(path = "/add", consumes = "application/json")
	public ResponseEntity<Object> postBook(@RequestBody @Valid Book book)
			throws MethodArgumentNotValidException, IllegalArgumentException {

		if (this.imageBytes == null)
			throw new IllegalArgumentException("You have to load an image before this call");

		book.setPicByte(this.imageBytes);
		bookRepository.save(book);
		this.imageBytes = null;
		String message = "Book created successfully";
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message));
	}

	@GetMapping(path = "/{id:\\d+}/one")
	public ResponseEntity<Book> getBookById(@PathVariable("id") Long id)
			throws ResourceNotFoundException, IllegalArgumentException {

		Optional<Book> optional = bookRepository.findById(id);
		Book book = optional.orElseThrow(
				() -> new ResourceNotFoundException("Unable to retrieve the resource. The book resource with ID: "
						+ id + " was not found in the database"));
		return ResponseEntity.ok().body(book);
	}

	@PutMapping(path = "/update/{id:\\d+}", consumes = "application/json")
	public ResponseEntity<Object> putBook(@PathVariable("id") Long id, @RequestBody @Valid Book book)
			throws ResourceNotFoundException, MethodArgumentNotValidException, IllegalArgumentException {

		Optional<Book> optional = bookRepository.findById(id);
		if (optional.isPresent()) {
			Book existingBook = optional.get();
			existingBook.setName(book.getName());
			existingBook.setAuthor(book.getAuthor());
			existingBook.setPrice(book.getPrice());

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

	@PutMapping(path = "/update/{id:\\d+}/price", consumes = "application/json")
	public ResponseEntity<Object> updatePrice(@PathVariable("id") Long id, @RequestBody @Valid Book book)
			throws ResourceNotFoundException, MethodArgumentNotValidException, IllegalArgumentException {

		Optional<Book> optional = bookRepository.findById(id);
		if (optional.isPresent()) {
			Book existingBook = optional.get();
			existingBook.setPrice(book.getPrice());

			bookRepository.save(existingBook);

			String message = "Book updated successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));

		} else {

			throw new ResourceNotFoundException("Unable to perform the modification. The book resource with ID: "
					+ id + " was not found in the database");
		}
	}

	@DeleteMapping(path = "/{bookId:\\d+}/delete")
	public ResponseEntity<Object> deleteBook(@PathVariable("bookId") Long bookId)
			throws ResourceNotFoundException, IllegalArgumentException {

		Optional<Book> optional = bookRepository.findById(bookId);
		if (optional.isPresent()) {

			Book book = optional.get();

			book.setDeleted(true);
			bookRepository.save(book);

			String message = "Book and associated data have been deleted successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));

		} else {
			throw new ResourceNotFoundException("Unable to perform the deletion. The book resource with ID: " + bookId
					+ " was not found in the database");
		}
	}

	@DeleteMapping(path = "/deleteAll")
	public ResponseEntity<ApiResponse> deleteBooks() throws ResourceNotFoundException, IllegalArgumentException {

		List<Book> list = (List<Book>) bookRepository.findAll();
		if (!list.isEmpty()) {

			bookService.deleteAllBooks();

			String message = "Books and their associated data have been deleted successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));

		} else {
			throw new ResourceNotFoundException(
					"Unable to perform the deletion. No books resource was found in the database");
		}
	}

	@GetMapping(path = "/{name}/find")
	public ResponseEntity<List<Book>> findBook(@PathVariable("name") String name)
			throws ResourceNotFoundException, IllegalArgumentException {

		System.out.println(name);
		List<Book> res = bookRepository.findByNameContaining(name);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

}
