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
@RequestMapping(path = "/api", produces = "application/json")
public class JoinTableController {

	
private final JoinTableRepository joinTableRepository;
private final BookRepository bookRepository;
private final OrderRepository orderRepository;
	
	@Autowired
	public JoinTableController(JoinTableRepository joinTableRepository, BookRepository bookRepository, OrderRepository orderRepository) {
		this.joinTableRepository = joinTableRepository;
		this.bookRepository = bookRepository;
		this.orderRepository = orderRepository;
	}
	
	
	@PostMapping(path = "/joinTables/create", consumes = "application/json")
	    public ResponseEntity<Object> postJoinTable(@RequestBody @Valid JoinTable newJoinTable)
	        throws MethodArgumentNotValidException, IllegalArgumentException, ResourceNotFoundException {

		Long bid = newJoinTable.getBook().getId();
		Long oid = newJoinTable.getOrder().getId();

	    Optional<Book> optionalBook = bookRepository.findById(bid);
	    Book book = optionalBook.orElseThrow(() -> new ResourceNotFoundException("Unable to create the joinTable because the book with ID: " + bid + " was not found in the database"));

	    Optional<Order> optionalOrder = orderRepository.findById(oid);
	    Order order = optionalOrder.orElseThrow(() -> new ResourceNotFoundException("Unable to create the joinTable because the order with ID: " + oid + " was not found in the database"));

	    JoinTable.JoinTableId joinTableId = new JoinTable.JoinTableId(oid, bid);
	    
	    newJoinTable.setId(joinTableId);
	    newJoinTable.setBook(book);
	    newJoinTable.setOrder(order);
		newJoinTable.setPrice(book.getPrice());
	    joinTableRepository.save(newJoinTable); 

	    String message = "The joinTable for the book with ID: " + bid+ " and order with ID "+ oid + " was created successfully";
	    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message));
	}
	




	 @GetMapping(path = "/joinTables/{bookId:\\d+}/{orderId:\\d+}/count")
	 public ResponseEntity<Long> countJoinTables(@PathVariable("bookId") Long bookId, @PathVariable("orderId") Long orderId)
	         throws ResourceNotFoundException {

	     JoinTable.JoinTableId joinTableId = new JoinTable.JoinTableId(orderId, bookId);

	     Optional<JoinTable> optionalJoinTable = joinTableRepository.findById(joinTableId);
	     if (!optionalJoinTable.isPresent()) {
	         throw new ResourceNotFoundException(
	                 "No joinTables resource was found in the database for the book with ID: " + bookId + " and order with ID: " + orderId);
	     }

	     Long count = joinTableRepository.countJoinTablesByCompositeId(joinTableId);

	     return new ResponseEntity<>(count, HttpStatus.OK);
	 }






	
	 @GetMapping(path = "/joinTables/{bookId:\\d+}/{orderId:\\d+}/all")
	 public ResponseEntity<List<JoinTable>> getJoinTables(@PathVariable("bookId") Long bookId, @PathVariable("orderId") Long orderId, @RequestParam("page") int page, @RequestParam("size") int size)
	         throws ResourceNotFoundException, IllegalArgumentException {
	     JoinTable.JoinTableId joinTableId = new JoinTable.JoinTableId(orderId, bookId);
	     
	     Pageable pageable = PageRequest.of(page, size, Sort.by("addedAt").descending());
	     Page<JoinTable> pagedResult = joinTableRepository.findJoinTablesByCompositeId(joinTableId, pageable);
	     
	     if (pagedResult.hasContent()) {
	         return new ResponseEntity<>(pagedResult.getContent(), HttpStatus.OK);
	     } else {
	         throw new ResourceNotFoundException(
	                 "Unable to retrieve the page: " + page + " because no joinTables resource was found in the database for the book with ID: "+ bookId + " and order with ID: " + orderId);
	     }
	 }


	 

		@GetMapping(path = "/joinTables/{bookId:\\d+}/{orderId:\\d+}/one")
		public ResponseEntity<JoinTable> getJoinTableById(@PathVariable("bookId") Long bookId, @PathVariable("orderId") Long orderId)
		        throws ResourceNotFoundException {

		    JoinTableId joinTableId = new JoinTableId(orderId, bookId);
		    Optional<JoinTable> optionalJoinTable = joinTableRepository.findById(joinTableId);

		    JoinTable joinTable = optionalJoinTable.orElseThrow(() ->
		            new ResourceNotFoundException("JoinTable entry with Book ID: " + bookId + " and Order ID: " + orderId + " was not found in the database"));

		    return ResponseEntity.ok().body(joinTable);
		}


		


		@PutMapping(path = "/joinTables/{bookId:\\d+}/{orderId:\\d+}/put", consumes = "application/json")
		public ResponseEntity<Object> putJoinTable(@PathVariable("bookId") Long bookId, @PathVariable("orderId") Long orderId, @RequestBody @Valid JoinTable updatedJoinTable)
				throws ResourceNotFoundException, MethodArgumentNotValidException, IllegalArgumentException {

			JoinTableId joinTableId = new JoinTableId(orderId, bookId);
		    Optional<JoinTable> optionalJoinTable = joinTableRepository.findById(joinTableId);
	                if (optionalJoinTable.isPresent()) {
	                    JoinTable existingJoinTable = optionalJoinTable.get();
	            
	                    if (!joinTableId.equals(existingJoinTable.getId())) {
	                        throw new IllegalArgumentException("The joinTable with ID: " + joinTableId + " was not found in the database");
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
		
		
	
		

		@DeleteMapping(path = "/joinTables/{bookId:\\d+}/{orderId:\\d+}/delete/one")
		public ResponseEntity<Object> deleteJoinTable(@PathVariable("bookId") Long bookId, @PathVariable("orderId") Long orderId)
		        throws ResourceNotFoundException, IllegalArgumentException {

		    JoinTableId joinTableId = new JoinTableId(orderId, bookId);
		    Optional<JoinTable> optionalJoinTable = joinTableRepository.findById(joinTableId);
		    if (optionalJoinTable.isPresent()) {
		        JoinTable existingJoinTable = optionalJoinTable.get();

		        if (!joinTableId.equals(existingJoinTable.getId())) {
		            throw new IllegalArgumentException("The joinTable with ID: " + joinTableId + " was not found in the database");
		        }

		        joinTableRepository.delete(existingJoinTable); 

		        String message = "JoinTable with ID: " + joinTableId + " deleted successfully";
		        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
		    } else {
		        throw new ResourceNotFoundException("Unable to delete the joinTable. The joinTable resource with ID: "
		                + joinTableId + " was not found in the database");
		    }
		}


		
		
		
	    
		@DeleteMapping(path = "/joinTables/{bookId:\\d+}/{orderId:\\d+}/delete/all")
		public ResponseEntity<ApiResponse> deleteJoinTables(@PathVariable("bookId") Long bookId, @PathVariable("orderId") Long orderId)
		        throws ResourceNotFoundException, IllegalArgumentException {

		    JoinTable.JoinTableId joinTableId = new JoinTable.JoinTableId(orderId, bookId);
		    Optional<JoinTable> optionalJoinTable = joinTableRepository.findById(joinTableId);

		    if (optionalJoinTable.isPresent()) {
		        List<JoinTable> joinTablesToDelete = joinTableRepository.findJoinTablesListByCompositeId(joinTableId);

		        // Verifica che le joinTables abbiano l'ID composto corrispondente
		        List<JoinTable> validJoinTablesToDelete = joinTablesToDelete.stream()
		                .filter(joinTable -> joinTable.getId().equals(joinTableId))
		                .collect(Collectors.toList());

		        if (!validJoinTablesToDelete.isEmpty()) {
		            joinTableRepository.deleteAll(validJoinTablesToDelete);

		            String message = "All JoinTables with book ID: " + bookId + " and order ID: " + orderId + " have been deleted successfully";
		            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
		        } else {
		            throw new ResourceNotFoundException("No valid joinTables associated with book ID: " + bookId + " and order ID: " + orderId + " were found in the database");
		        }
		    } else {
		        throw new ResourceNotFoundException("No joinTables with book ID: " + bookId + " and order ID: " + orderId + " were found in the database");
		    }
		}
		
		
		
		
	/*	@DeleteMapping(path = "/joinTables/{bookId:\\d+}/{orderId:\\d+}/delete/allBooks")
		public ResponseEntity<ApiResponse> deleteJoinTables(@PathVariable("bookId") Long bookId, @PathVariable("orderId") Long orderId)
		        throws ResourceNotFoundException, IllegalArgumentException {

		    // Creare l'ID composto per l'ordine specificato
		    JoinTable.JoinTableId joinTableId = new JoinTable.JoinTableId(orderId, null);  // Cambiato da "new JoinTable.JoinTableId(orderId, bookId);"

		    // Trovare tutte le joinTables associate all'ordine specificato
		    List<JoinTable> joinTablesToDelete = joinTableRepository.findJoinTablesListByCompositeId(joinTableId);

		    if (!joinTablesToDelete.isEmpty()) {
		        joinTableRepository.deleteAll(joinTablesToDelete);

		        String message = "All JoinTables with order ID: " + orderId + " have been deleted successfully";
		        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
		    } else {
		        throw new ResourceNotFoundException("No joinTables associated with order ID: " + orderId + " were found in the database");
		    }
		}




		@DeleteMapping(path = "/joinTables/{bookId:\\d+}/delete/allOrders")
		public ResponseEntity<ApiResponse> deleteAllOrdersForBook(@PathVariable("bookId") Long bookId)
		        throws ResourceNotFoundException, IllegalArgumentException {

		    // Trovare tutti gli ordini associati al libro specificato
		    List<JoinTable> ordersToDelete = joinTableRepository.findJoinTablesListByCompositeId(new JoinTable.JoinTableId(null, bookId));

		    if (!ordersToDelete.isEmpty()) {
		        joinTableRepository.deleteAll(ordersToDelete);

		        String message = "All orders for book ID: " + bookId + " have been deleted successfully";
		        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
		    } else {
		        throw new ResourceNotFoundException("No orders associated with book ID: " + bookId + " were found in the database");
		    }
		}*/

	
}
