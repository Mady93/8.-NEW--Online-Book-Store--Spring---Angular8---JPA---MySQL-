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
import org.springframework.web.bind.annotation.RequestBody;
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

		// String message = "The order for the user with ID: " + userId+ " was created
		// successfully";
		return ResponseEntity.status(HttpStatus.CREATED).body(jsonString);
	}

	/*
	 * @GetMapping(path = "/{userId:\\d+}/list")
	 * public ResponseEntity<List<Order>> getOrders(@PathVariable("userId") Long
	 * userId)
	 * throws ResourceNotFoundException, IllegalArgumentException {
	 * 
	 * List<Order> list = orderRepository.findOrdersByIdUser(userId);
	 * 
	 * if (!list.isEmpty()) {
	 * return ResponseEntity.ok(list);
	 * } else {
	 * throw new
	 * ResourceNotFoundException("Unable to retrieve the list. No orders resource was found in the database for the user with ID: "
	 * + userId);
	 * }
	 * }
	 */

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
	 * @PutMapping(path = "/{userId:\\d+}/{orderId:\\d+}/put", consumes =
	 * "application/json")
	 * public ResponseEntity<Object> putOrder(@PathVariable("userId") Long
	 * userId, @PathVariable("orderId") Long orderId, @RequestBody @Valid Order
	 * updatedOrder)
	 * throws ResourceNotFoundException, MethodArgumentNotValidException,
	 * IllegalArgumentException {
	 * 
	 * Optional<Order> optional = orderRepository.findById(orderId);
	 * if (optional.isPresent()) {
	 * Order existingOrder = optional.get();
	 * 
	 * // Verifica che l'ordine appartenga all'user specificato
	 * if (!existingOrder.getUser().getId().equals(userId)) {
	 * throw new IllegalArgumentException("The order with ID: " + orderId +
	 * " does not belong to the user with ID: " + userId);
	 * }
	 * 
	 * existingOrder.setUser(updatedOrder.getUser());
	 * 
	 * orderRepository.save(existingOrder);
	 * 
	 * String message = "Order with ID: " +orderId+
	 * " was updated successfully for the user with ID: " + userId;
	 * return ResponseEntity.status(HttpStatus.OK).body(new
	 * ApiResponse(HttpStatus.OK.value(), message));
	 * } else {
	 * throw new
	 * ResourceNotFoundException("Unable to perform the modification. The order resource with ID: "
	 * + orderId + " was not found in the database for the user with ID: " +
	 * userId);
	 * }
	 * }
	 */

	@DeleteMapping(path = "/{userId:\\d+}/{orderId:\\d+}/delete/one")
	public ResponseEntity<Object> deleteOrder(@PathVariable("userId") Long userId,
			@PathVariable("orderId") Long orderId)
			throws ResourceNotFoundException, IllegalArgumentException {

		Optional<Order> optional = orderRepository.findById(orderId);
		Order order = optional.orElseThrow(
				() -> new ResourceNotFoundException("Order with ID: " + orderId + " was not found in the database"));

		// Verifica che l'ID dell'user associato all'ordine sia uguale a userId
		if (!order.getUser().getId().equals(userId)) {
			throw new IllegalArgumentException(
					"The order with ID: " + orderId + " is not associated with the user with ID: " + userId);
		}

		orderRepository.deleteById(orderId);

		String message = "Order deleted successfully";
		return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
	}

	@DeleteMapping(path = "/{userId:\\d+}/delete/all")
	public ResponseEntity<ApiResponse> deleteOrders(@PathVariable("userId") Long userId)
			throws ResourceNotFoundException, IllegalArgumentException {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			List<Order> list = orderRepository.findOrdersByUser(user);

			if (!list.isEmpty()) {
				orderRepository.deleteAll(list);

				String message = "All Orders associated with user ID: " + userId + " have been deleted successfully";
				return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
			} else {
				throw new ResourceNotFoundException(
						"No orders associated with user ID: " + userId + " were found in the database");
			}
		} else {
			throw new ResourceNotFoundException("User with ID: " + userId + " was not found in the database");
		}
	}

}
