package com.javainuse.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
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

	/*@GetMapping(path = "/{bookId:\\d+}/{orderId:\\d+}/count")
	public ResponseEntity<Long> countJoinTables(@PathVariable("bookId") Long bookId,
			@PathVariable("orderId") Long orderId)
			throws ResourceNotFoundException {

		JoinTable.JoinTableId joinTableId = new JoinTable.JoinTableId(orderId, bookId);

		Optional<JoinTable> optionalJoinTable = joinTableRepository.findById(joinTableId);
		if (!optionalJoinTable.isPresent()) {
			throw new ResourceNotFoundException(
					"No joinTables resource was found in the database for the book with ID: " + bookId
							+ " and order with ID: " + orderId);
		}

		Long count = joinTableRepository.countJoinTablesByCompositeId(joinTableId);

		return new ResponseEntity<>(count, HttpStatus.OK);
	}*/

	@GetMapping(path = "/{orderId:\\d+}/get")
	public ResponseEntity<List<JoinTable>> getJoinTables(@PathVariable("orderId") Long orderId)
			throws ResourceNotFoundException, IllegalArgumentException {

		List<JoinTable> ls = joinTableRepository.getJoinTablesByOrderId(orderId);

		return new ResponseEntity<>(ls, HttpStatus.OK);

	}

	/*
	@GetMapping(path = "/{bookId:\\d+}/{orderId:\\d+}/get")
	public ResponseEntity<List<JoinTable>> getJoinTables(@PathVariable("bookId")
	Long bookId,

	@PathVariable("orderId") Long orderId, @RequestParam("page") int
	page, @RequestParam("size") int size)
	throws ResourceNotFoundException, IllegalArgumentException {
	JoinTable.JoinTableId joinTableId = new JoinTable.JoinTableId(orderId,
	bookId);

	Pageable pageable = PageRequest.of(page, size,
	Sort.by("addedAt").ascending());
	Page<JoinTable> pagedResult =
	joinTableRepository.findJoinTablesByCompositeId(joinTableId, pageable);

	if (pagedResult.hasContent()) {
	return new ResponseEntity<>(pagedResult.getContent(), HttpStatus.OK);
	} else {
	throw new ResourceNotFoundException(
	"Unable to retrieve the page: " + page
	+
	" because no joinTables resource was found in the database for the book with ID: "
	+ bookId + " and order with ID: " + orderId);
	}
	}

	@GetMapping(path = "/{bookId:\\d+}/{orderId:\\d+}/one")
	public ResponseEntity<JoinTable> getJoinTableById(@PathVariable("bookId")
	Long bookId,

	@PathVariable("orderId") Long orderId)
	throws ResourceNotFoundException {

	JoinTableId joinTableId = new JoinTableId(orderId, bookId);
	Optional<JoinTable> optionalJoinTable =
	joinTableRepository.findById(joinTableId);

	JoinTable joinTable = optionalJoinTable
	.orElseThrow(() -> new
	ResourceNotFoundException("JoinTable entry with Book ID: " + bookId
	+ " and Order ID: " + orderId + " was not found in the database"));

	return ResponseEntity.ok().body(joinTable);
	}
	 */

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

	/*
	@DeleteMapping(path = "/{bookId:\\d+}/{orderId:\\d+}/delete")
	public ResponseEntity<Object> deleteJoinTable(@PathVariable("bookId") Long bookId,
			@PathVariable("orderId") Long orderId)
			throws ResourceNotFoundException, IllegalArgumentException {

		JoinTableId joinTableId = new JoinTableId(orderId, bookId);
		Optional<JoinTable> optionalJoinTable = joinTableRepository.findById(joinTableId);
		if (optionalJoinTable.isPresent()) {
			JoinTable existingJoinTable = optionalJoinTable.get();

			if (!joinTableId.equals(existingJoinTable.getId())) {
				throw new IllegalArgumentException(
						"The joinTable with ID: " + joinTableId + " was not found in the database");
			}

			joinTableRepository.delete(existingJoinTable);

			String message = "JoinTable with ID: " + joinTableId + " deleted successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
		} else {
			throw new ResourceNotFoundException("Unable to delete the joinTable. The joinTable resource with ID: "
					+ joinTableId + " was not found in the database");
		}
	}

	@DeleteMapping(path = "/deleteAll")
	public ResponseEntity<ApiResponse> deleteAllRecordsOnJoinTable() {
		joinTableRepository.deleteAll();

		String message = "All JoinTables have been deleted successfully";
		return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
	}
	*/





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
	


