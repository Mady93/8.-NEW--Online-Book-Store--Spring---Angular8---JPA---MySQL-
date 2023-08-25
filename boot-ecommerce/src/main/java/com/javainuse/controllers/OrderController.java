package com.javainuse.controllers;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.javainuse.details.ApiResponse;
import com.javainuse.entities.Order;
import com.javainuse.entities.User;
import com.javainuse.exceptions.ResourceNotFoundException;
import com.javainuse.repositories.OrderRepository;
import com.javainuse.repositories.UserRepository;

@RestController
@CrossOrigin(origins = "*")
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/orders", produces = "application/json")
public class OrderController {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;

	@Autowired
	public OrderController(OrderRepository orderRepository, UserRepository userRepository) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
	}

	@GetMapping(path = "/{userId:\\d+}/add")
	public ResponseEntity<String> postOrder(@PathVariable("userId") Long userId)
			throws MethodArgumentNotValidException, IllegalArgumentException, ResourceNotFoundException,
			JsonProcessingException {

		Optional<User> optionalPost = userRepository.findById(userId);
		User user = optionalPost.orElseThrow(() -> new ResourceNotFoundException(
				"Unable to create the order because the user with ID: " + userId + " was not found in the database"));

		Order newOrder = new Order();

		newOrder.setUser(user);
		orderRepository.save(newOrder);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode rootNode = objectMapper.createObjectNode();
		rootNode.put("orderId", newOrder.getId());
		String jsonString = objectMapper.writeValueAsString(rootNode);
		return ResponseEntity.status(HttpStatus.CREATED).body(jsonString);
	}

	@GetMapping(path = "/{userId:\\d+}/count")
	public ResponseEntity<Integer> countOrders(@PathVariable("userId") Long userId)
			throws ResourceNotFoundException, IllegalArgumentException {
		Long count = orderRepository.countOrdersByUserId(userId);
		if (count == 0) {
			throw new ResourceNotFoundException(
					"Unable to perform the count. No orders resource was found in the database for the user with ID: "
							+ userId);
		} else {
			return new ResponseEntity<>(count.intValue(), HttpStatus.OK);
		}
	}

	@GetMapping(path = "/{userId:\\d+}/get")
	public ResponseEntity<List<Order>> getOrders(@PathVariable("userId") Long userId, @RequestParam("page") int page,
			@RequestParam("size") int size)
			throws ResourceNotFoundException, IllegalArgumentException {
		Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
		Page<Order> pagedResult = orderRepository.findOrdersByUserId(userId, pageable);
		if (pagedResult.hasContent()) {
			return new ResponseEntity<>(pagedResult.getContent(), HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException(
					"Unable to retrieve the page: " + page
							+ " because no orders resource was found in the database for the user with ID: " + userId);
		}
	}

	@GetMapping(path = "/{userId:\\d+}/{orderId:\\d+}/one")
	public ResponseEntity<Order> getOrderById(@PathVariable("userId") Long userId,
			@PathVariable("orderId") Long orderId)
			throws ResourceNotFoundException, IllegalArgumentException {

		Optional<User> optional = userRepository.findById(userId);
		User user = optional.orElseThrow(
				() -> new ResourceNotFoundException("User with ID: " + userId + " not found in the database"));

		Optional<Order> op = orderRepository.findById(orderId);
		Order order = op.orElseThrow(
				() -> new ResourceNotFoundException("Order with ID: " + orderId + " not found in the database"));

		// Verifica che l'ordine appartenga all'user specificato
		if (!order.getUser().equals(user)) {
			throw new ResourceNotFoundException(
					"Order with ID: " + orderId + " does not belong to the user with ID: " + userId);
		}

		return ResponseEntity.ok().body(order);
	}

	
/* 
@DeleteMapping(path = "/{userId:\\d+}/deleteAll")
public ResponseEntity<Object> deleteOrdersByUserId(@PathVariable("userId") Long userId)
        throws ResourceNotFoundException, IllegalArgumentException {

    List<Order> orders = orderRepository.findByUserId(userId);
    if (orders.isEmpty()) {
        throw new ResourceNotFoundException("No orders found for user with ID: " + userId);
    }

    for (Order order : orders) {
        orderRepository.delete(order);
    }

    String message = "All orders for user with ID: " + userId + " have been deleted successfully";
    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
}


	@DeleteMapping("/deleteAll")
	public void deleteAlRecordsOnOrder() throws ResourceNotFoundException {

		Long orderCount = orderRepository.countOrders();

		if (orderCount == 0) {
			throw new ResourceNotFoundException("No orders found to delete.");
		} else {
			orderRepository.deleteAll();
		}
		
	}*/



}

	
	
	


