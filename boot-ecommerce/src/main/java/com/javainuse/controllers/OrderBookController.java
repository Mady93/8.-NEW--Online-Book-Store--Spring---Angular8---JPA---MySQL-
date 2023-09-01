package com.javainuse.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javainuse.details.ApiResponse;
import com.javainuse.entities.Book;
import com.javainuse.entities.OrderBook;
import com.javainuse.entities.OrderBook.OrderBooksId;
import com.javainuse.entities.Order;
import com.javainuse.exceptions.ResourceNotFoundException;
import com.javainuse.repositories.BookRepository;
import com.javainuse.repositories.OrderBookRepository;
import com.javainuse.repositories.OrderRepository;
import com.javainuse.services.OrderBookService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/order_book", produces = "application/json")
public class OrderBookController {

	private final OrderBookRepository orderBookRepository;
	private final OrderBookService orderBookService;
	private final BookRepository bookRepository;
	private final OrderRepository orderRepository;

	@Autowired
	public OrderBookController(OrderBookRepository orderBookRepository, OrderBookService orderBookService,
			BookRepository bookRepository, OrderRepository orderRepository) {
		this.orderBookRepository = orderBookRepository;
		this.orderBookService = orderBookService;
		this.bookRepository = bookRepository;
		this.orderRepository = orderRepository;
	}

	@PostMapping(path = "/add", consumes = "application/json")
	public ResponseEntity<Object> postOrderBook(@RequestBody @Valid OrderBook newOrderBook)
			throws MethodArgumentNotValidException, IllegalArgumentException, ResourceNotFoundException {

		Long bookId = newOrderBook.getBook().getId();
		Long orderId = newOrderBook.getOrder().getId();

		Optional<Book> optionalBook = bookRepository.findById(bookId);
		Book book = optionalBook.orElseThrow(() -> new ResourceNotFoundException(
				"Unable to create the order intersection row because the book with ID: " + bookId
						+ " was not found in the database"));

		Optional<Order> optionalOrder = orderRepository.findById(orderId);
		Order order = optionalOrder.orElseThrow(() -> new ResourceNotFoundException(
				"Unable to create the order intersection row because the order with ID: " + orderId
						+ " was not found in the database"));

		OrderBook.OrderBooksId OrderBooksId = new OrderBook.OrderBooksId(orderId, bookId);

		newOrderBook.setId(OrderBooksId);
		newOrderBook.setBook(book);
		newOrderBook.setOrder(order);
		newOrderBook.setPrice(book.getPrice());
		newOrderBook.setActive(true);
		orderBookRepository.save(newOrderBook);

		String message = "The order intersection row for the book with ID: " + bookId + " and order with ID " + orderId
				+ " was created successfully";
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message));
	}

	@GetMapping(path = "/{orderId:\\d+}/get")
	public ResponseEntity<List<OrderBook>> getOrderBooks(@PathVariable("orderId") Long orderId)
			throws ResourceNotFoundException, IllegalArgumentException {

		List<OrderBook> ls = orderBookRepository.getOrderBooksByOrderId(orderId);

		return new ResponseEntity<>(ls, HttpStatus.OK);

	}

	@PutMapping(path = "/update/{bookId:\\d+}/{orderId:\\d+}", consumes = "application/json")
	public ResponseEntity<Object> putOrderBook(@PathVariable("bookId") Long bookId,
			@PathVariable("orderId") Long orderId, @RequestBody @Valid OrderBook updatedOrderBook)
			throws ResourceNotFoundException, MethodArgumentNotValidException, IllegalArgumentException {

		OrderBooksId OrderBooksId = new OrderBooksId(orderId, bookId);
		Optional<OrderBook> optionalOrderBook = orderBookRepository.findById(OrderBooksId);
		if (optionalOrderBook.isPresent()) {
			OrderBook existingOrderBook = optionalOrderBook.get();

			if (!OrderBooksId.equals(existingOrderBook.getId())) {
				throw new IllegalArgumentException(
						"The order intersection row with ID: " + OrderBooksId + " was not found in the database");
			}

			Integer q = updatedOrderBook.getQuantity();
			String message;

			if (q > 0) {
				existingOrderBook.setQuantity(updatedOrderBook.getQuantity());
				orderBookRepository.save(existingOrderBook);
				message = "The order intersection row with ID: " + OrderBooksId + " was updated successfully";
			} else {
				orderBookRepository.delete(existingOrderBook);
				message = "The book in current order has been cancelled";
			}

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
		} else {
			throw new ResourceNotFoundException(
					"Unable to perform the modification. The order intersection row resource with ID: "
							+ OrderBooksId + " was not found in the database");
		}
	}

		// Query native commentate

	/* @Transactional
	@DeleteMapping(path = "/{bookId:\\d+}/delete")
	public ResponseEntity<Object> deleteOrderBook(@PathVariable("bookId") Long bookId)
			throws ResourceNotFoundException, IllegalArgumentException {

			orderBookRepository.deleteOrderBooksById_BookId(bookId);

			String message = "All orders intersection rows with bookId: "+bookId+" have been deleted successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
	}

	@Transactional
	@DeleteMapping(path = "/deleteAll")
	public ResponseEntity<ApiResponse> deleteOrderBooks() {
		orderBookRepository.deleteAllRecords();

    	String message = "All orders intersection rows have been deleted successfully";
    	return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
	}*/

}
