package com.javainuse.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javainuse.details.ApiResponse;
import com.javainuse.entities.Book;
import com.javainuse.entities.JoinTable;
import com.javainuse.entities.JoinTable.JoinTableId;
import com.javainuse.entities.Order;
import com.javainuse.exceptions.ResourceNotFoundException;
import com.javainuse.repositories.BookRepository;
import com.javainuse.repositories.JoinTableRepository;
import com.javainuse.repositories.OrderRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/joinTables", produces = "application/json")
public class JoinTableController {

	private final JoinTableRepository joinTableRepository;
	private final BookRepository bookRepository;
	private final OrderRepository orderRepository;

	@Autowired
	public JoinTableController(JoinTableRepository joinTableRepository, BookRepository bookRepository,
			OrderRepository orderRepository) {
		this.joinTableRepository = joinTableRepository;
		this.bookRepository = bookRepository;
		this.orderRepository = orderRepository;
	}

	@PostMapping(path = "/add", consumes = "application/json")
	public ResponseEntity<Object> postJoinTable(@RequestBody @Valid JoinTable newJoinTable)
			throws MethodArgumentNotValidException, IllegalArgumentException, ResourceNotFoundException {

		Long bid = newJoinTable.getBook().getId();
		Long oid = newJoinTable.getOrder().getId();

		Optional<Book> optionalBook = bookRepository.findById(bid);
		Book book = optionalBook.orElseThrow(() -> new ResourceNotFoundException(
				"Unable to create the joinTable because the book with ID: " + bid + " was not found in the database"));

		Optional<Order> optionalOrder = orderRepository.findById(oid);
		Order order = optionalOrder.orElseThrow(() -> new ResourceNotFoundException(
				"Unable to create the joinTable because the order with ID: " + oid + " was not found in the database"));

		JoinTable.JoinTableId joinTableId = new JoinTable.JoinTableId(oid, bid);

		newJoinTable.setId(joinTableId);
		newJoinTable.setBook(book);
		newJoinTable.setOrder(order);
		newJoinTable.setPrice(book.getPrice());
		joinTableRepository.save(newJoinTable);

		String message = "The joinTable for the book with ID: " + bid + " and order with ID " + oid
				+ " was created successfully";
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message));
	}


	@GetMapping(path = "/{orderId:\\d+}/get")
	public ResponseEntity<List<JoinTable>> getJoinTables(@PathVariable("orderId") Long orderId)
			throws ResourceNotFoundException, IllegalArgumentException {

		List<JoinTable> ls = joinTableRepository.getJoinTablesByOrderId(orderId);

		return new ResponseEntity<>(ls, HttpStatus.OK);

	}

	@PutMapping(path = "/update/{bookId:\\d+}/{orderId:\\d+}", consumes = "application/json")
	public ResponseEntity<Object> putJoinTable(@PathVariable("bookId") Long bookId,
			@PathVariable("orderId") Long orderId, @RequestBody @Valid JoinTable updatedJoinTable)
			throws ResourceNotFoundException, MethodArgumentNotValidException, IllegalArgumentException {

		JoinTableId joinTableId = new JoinTableId(orderId, bookId);
		Optional<JoinTable> optionalJoinTable = joinTableRepository.findById(joinTableId);
		if (optionalJoinTable.isPresent()) {
			JoinTable existingJoinTable = optionalJoinTable.get();

			if (!joinTableId.equals(existingJoinTable.getId())) {
				throw new IllegalArgumentException(
						"The joinTable with ID: " + joinTableId + " was not found in the database");
			}

			existingJoinTable.setQuantity(updatedJoinTable.getQuantity());
			existingJoinTable.setPrice(updatedJoinTable.getPrice());

			joinTableRepository.save(existingJoinTable);

			String message = "JoinTable with ID: " + joinTableId + " was updated successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
		} else {
			throw new ResourceNotFoundException("Unable to perform the modification. The joinTable resource with ID: "
					+ joinTableId + " was not found in the database");
		}
	}

	



	@Transactional
	@DeleteMapping(path = "/{bookId:\\d+}/delete")
	public ResponseEntity<Object> deleteJoinTable(@PathVariable("bookId") Long bookId)
			throws ResourceNotFoundException, IllegalArgumentException {

			joinTableRepository.deleteJoinTablesById_BookId(bookId);

			String message = "All JoinTables with bookId: "+bookId+" have been deleted successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));

	}

	@Transactional
	@DeleteMapping(path = "/deleteAll")
	public ResponseEntity<ApiResponse> deleteAllRecordsOnJoinTable() {
    	joinTableRepository.deleteAllRecords();

    	String message = "All JoinTables have been deleted successfully";
    	return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
}


}
	


