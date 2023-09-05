package com.javainuse.controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.javainuse.entities.Book;
import com.javainuse.entities.Email;
import com.javainuse.entities.Order;
import com.javainuse.entities.User;
import com.javainuse.exceptions.ResourceNotFoundException;
import com.javainuse.repositories.EmailRepository;
import com.javainuse.repositories.OrderRepository;
import com.javainuse.repositories.UserRepository;
import com.javainuse.services.EmailService;
import com.javainuse.services.OrderBookService;
import com.javainuse.services.OrderService;

@RestController
@CrossOrigin(origins = "*")
// @CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/orders", produces = "application/json")
public class OrderController {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final OrderService orderService;
	private final OrderBookService orderBookService;

	// faccio l'autowire del servizio mail
	private final EmailService emailService;
	private final EmailRepository emailRepository;

	@Autowired
	public OrderController(OrderRepository orderRepository, UserRepository userRepository, OrderService orderService,
			EmailService emailService, OrderBookService orderBookService, EmailRepository emailRepository) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.orderService = orderService;
		this.emailService = emailService;
		this.emailRepository = emailRepository;
		this.orderBookService = orderBookService;
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
		newOrder.setState("Working");
		newOrder.setActive(true);
		orderRepository.save(newOrder);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode rootNode = objectMapper.createObjectNode();
		rootNode.put("orderId", newOrder.getId());
		String jsonString = objectMapper.writeValueAsString(rootNode);
		return ResponseEntity.status(HttpStatus.CREATED).body(jsonString);
	}

/*
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
 */

 	@GetMapping(path = "/{userId:\\d+}/count")
	public ResponseEntity<Integer> countOrders(@PathVariable("userId") Long userId)
			throws ResourceNotFoundException, IllegalArgumentException {
		Long count = orderRepository.countNotDeletedAndByUserId(userId);
		if (count == 0) {
			throw new ResourceNotFoundException(
					"Unable to perform the count. No orders resource was found in the database for the user with ID: "
							+ userId);
		} else {
			return new ResponseEntity<>(count.intValue(), HttpStatus.OK);
		}
	}

	/*
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
	 */

	@GetMapping(path = "/{userId:\\d+}/get")
	public ResponseEntity<List<Order>> getOrders(@PathVariable("userId") Long userId, @RequestParam("page") int page,
			@RequestParam("size") int size)
			throws ResourceNotFoundException, IllegalArgumentException {
		Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
		Page<Order> pagedResult = orderRepository.findByNotDeletedAndByUserId(userId, pageable);
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


	
	 @DeleteMapping(path = "/{orderId:\\d+}/delete")
	public ResponseEntity<Object> deleteOrder(@RequestParam("reason") String reason,
											  @PathVariable("orderId") Long orderId,
											  @RequestParam("vuid") Long vuid)
			throws ResourceNotFoundException, IllegalArgumentException {

		Optional<Order> orderOptional = orderRepository.findById(orderId);
		Order order = orderOptional.orElseThrow(
				() -> new ResourceNotFoundException("Order with ID: " + orderId + " not found in the database"));


		
		Optional<User> userOptional = userRepository.findById(vuid);
		User user = userOptional.orElseThrow(
				() -> new ResourceNotFoundException("User with ID: " + vuid + " not found in the database"));



		// Prima di eliminare l'ordine, elimina i record correlati nella tabella
		// "order_books" che fanno riferimento all'ordine
		//order.setActive(false);
		order.setState("Cancelled");
		order.setReason(reason);
		order.setCancelledDate(new Date());
		orderRepository.save(order);

		String successMessage = "Order deleted successfully";
		Map<String, Object> response = new HashMap<>();
		response.put("order", order);
		response.put("message", successMessage);


		
		Email email = new Email();
		email.setActive(true);
		email.setSubject("Order: #"+orderId);
		email.setBody("Order: #"+orderId+" has been cancelled");
		email.setOrder(order);
		email.setFrom(user);
		email.setSendedAt(new Date());

			for (User usr : userRepository.getUsersByRole("Order")) {

				if (user.getId() != usr.getId()) {
				/*
				 //ricreo l'istanza per evitare di sovrascrivere le mail
				Email nem = new Email();
				nem.setActive(email.isActive());
				nem.setSubject(email.getSubject());
				nem.setBody(email.getBody());
				nem.setOrder(email.getOrder());
				nem.setSendedAt(email.getSendedAt());
				nem.setFrom(email.getFrom());
				nem.setTo(usr);
				//nem.setTo(order.getUser());
				emailRepository.save(nem);
				 */
				
				 Email nem = new Email();
				nem.setActive(email.isActive());
				nem.setSubject(email.getSubject());
				nem.setBody(email.getBody());
				nem.setOrder(email.getOrder());
				nem.setSendedAt(email.getSendedAt());
				nem.setFrom(email.getFrom());
				nem.setTo(usr);
				//nem.setTo(order.getUser());
				emailRepository.save(nem);



				CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				String obj_ = "Order #" + order.getId();
				String msg_ = "Dear Order, the order"+order.getId()+" has been cancelled!";
				//emailService.sendEmail(user.getEmail(), obj_, msg_);
				emailService.sendEmail(usr.getEmail(), obj_, msg_);
			});
		}
	}

		return ResponseEntity.ok().body(response);
	}
	 






	@PutMapping(path = "/update", consumes = "application/json")
	public ResponseEntity<Object> updateOrder(@RequestBody /*@Valid*/ Order orderInfo,
											  @RequestParam("vuid") long vuid,
											  @RequestParam("action") String action)
											throws ResourceNotFoundException, MethodArgumentNotValidException, IllegalArgumentException {

		
		Optional<User> userOptional = userRepository.findById(vuid);
		User user = userOptional.orElseThrow(() -> new ResourceNotFoundException("User with ID: " + vuid + " not found in the database"));

		Optional<Order> orderOptional = orderRepository.findById(orderInfo.getId());
		Order order = orderOptional.orElseThrow(() -> new ResourceNotFoundException("Order with ID: " + orderInfo.getId() + " not found in the database"));
		System.out.println("Received OrderInfo ID: " + orderInfo.getId());
		System.out.println("Have Order ID: " + order.getId());

		String successMessage = "Nothing done";
		String obj = "";
		String msg = "";

		if (action.equals("state") && user.getType().equals("Order")){
			order.setState(orderInfo.getState());
			successMessage = "Order: " + order.getId() + " has been updated successfully to state: " + orderInfo.getState();


			obj = "Order #" + order.getId();
			msg = "Dear customer, your order has been changed to: " + orderInfo.getState();

			//invio la mail vera in modo asincrono
			
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				String obj_ = "Order #" + order.getId();
				//String msg_ = "Dear customer, your order has been changed to: " + orderInfo.getState();
				String msg_ = "Dear user, your order #"+order.getId()+" state has changed in: "+order.getState();
				emailService.sendEmail(order.getUser().getEmail(), obj_, msg_);
			});
			

			/*Email email = new Email();

			email.setActive(true);
			email.setFrom(user);
			email.setTo(order.getUser());
			email.setOrder(order);
			email.setSubject("Order status");
			email.setBody("Dear user, your order #"+order.getId()+" state has changed in: "+order.getState());
			
			emailRepository.save(email);*/
			
			


		}else if (action.equals("edit")){

			if (order.getEdit() && !order.getEditBy().equals(user.getName()))
			{

				Date now = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(order.getEditDate());
				calendar.add(Calendar.MINUTE, 20);
				if (!now.after(calendar.getTime()))
				{
					return ResponseEntity.status(423).body("The order is currently edited by another user");
				}
				
			}

			order.setEdit(orderInfo.getEdit(), user.getName(), ""+user.getId());
			successMessage = "Order: " + order.getId() + " has been updated successfully to edit: " + orderInfo.getEdit();

			obj = "Order #" + order.getId();

		
		 if(user.getType().equals("Order")) {
			if (order.getEdit()) msg = "Dear customer, your order is currently edited by "+user.getName()+" (Role: "+user.getType()+")";
				else msg = "Dear customer, your order is now free";
		} else {
			if (order.getEdit()) msg = "Dear order, the order is currently edited by "+user.getName()+" (Role: "+user.getType()+")";
				else msg = "Dear order, the order is now free";
		}
		 

			/*if (order.getEdit()) msg = "Dear customer, your order is currently edited by "+user.getName()+" (Role: "+user.getType()+")";
				else msg = "Dear customer, your order is now free";*/

		}else{
			return ResponseEntity.status(403).body("");
		}
		orderRepository.save(order);




		//invio una notifica interna
		Email email = new Email();
		email.setFrom(user);
		email.setSubject(obj);
		email.setBody(msg);
		email.setOrder(order);
		email.setActive(true);
		email.setSendedAt(new Date());
		


		
		
		if (user.getType().equals("Order"))
		{
			email.setTo(order.getUser());
			emailRepository.save(email);
		} else {
		
			for (User usr : userRepository.getUsersByRole("Order")) {

				if (user.getId() != usr.getId()) {
				//ricreo l'istanza per evitare di sovrascrivere le mail
				Email nem = new Email();
				nem.setActive(email.isActive());
				nem.setSubject(email.getSubject());
				nem.setBody(email.getBody());
				nem.setOrder(email.getOrder());
				nem.setSendedAt(email.getSendedAt());
				nem.setFrom(email.getFrom());
				nem.setTo(usr);

				emailRepository.save(nem);
			}
		}

		}

		/*Map<String, Object> response = new HashMap<>();
		response.put("order", order);
		response.put("message", successMessage);*/
		
		return ResponseEntity.status(200).body(order);						
		//return ResponseEntity.status(200).body(response);	
	}
	

	


	@Deprecated
	@PutMapping(path = "/update/{orderId:\\d+}/{state}", consumes = "application/json")
	public ResponseEntity<Object> updateState(@PathVariable("orderId") Long orderId,
			@PathVariable("state") String state, @RequestParam("vuid") long vuid)
			throws ResourceNotFoundException, MethodArgumentNotValidException, IllegalArgumentException {


		Optional<User> userOptional = userRepository.findById(vuid);
		User user = userOptional.orElseThrow(
				() -> new ResourceNotFoundException("User with ID: " + vuid + " not found in the database"));

		Optional<Order> op = orderRepository.findById(orderId);
		Order order = op.orElseThrow(
				() -> new ResourceNotFoundException("Order with ID: " + orderId + " not found in the database"));

		order.setState(state);
		orderRepository.save(order);




		String obj = "Order #" + order.getId();
		String msg = "Dear customer, your order has been changed to: " + state;


		//invio la mail vera
		String userEmail = order.getUser().getEmail();
		emailService.sendEmail(userEmail, obj, msg);


		//invio una notifica interna
		Email email = new Email();
		email.setFrom(user);
		email.setSubject(obj);
		email.setBody(msg);
		email.setOrder(order);

		if (user.getType() == "Order")
		{
			email.setTo(order.getUser());
			emailRepository.save(email);
		} else {
			//invio la mail a tutti gli utenti order
			for (User usr : userRepository.getUsersByRole("Order")) {

				if (user.getId() != usr.getId()) {

				//ricreo l'istanza per evitare di sovrascrivere le mail
				Email nem = new Email();
				nem.setActive(email.isActive());
				nem.setSubject(email.getSubject());
				nem.setBody(email.getBody());
				nem.setOrder(email.getOrder());
				nem.setSendedAt(email.getSendedAt());
				nem.setFrom(email.getFrom());
				nem.setTo(usr);

				emailRepository.save(nem);
			}

		}

	}

		String successMessage = "Order: " + order.getId() + " has been updated successfully to state: " + state;
		Map<String, Object> response = new HashMap<>();
		response.put("order", order);
		response.put("message", successMessage);
		return ResponseEntity.status(200).body(response);
	}
	





	@GetMapping(path = "/count/all")
	public ResponseEntity<Integer> countAllOrders()
			throws ResourceNotFoundException, IllegalArgumentException {
		Long count = orderRepository.countTotalOrdersInWorkingState();
		if (count == 0) {
			throw new ResourceNotFoundException(
					"Unable to perform the count. No orders resource was found in the database!");
		} else {
			return new ResponseEntity<>(count.intValue(), HttpStatus.OK);
		}
	}

	@GetMapping(path = "/inbox/all")
	public ResponseEntity<List<Order>> getOrders(@RequestParam("page") int page,
			@RequestParam("size") int size)
			throws ResourceNotFoundException, IllegalArgumentException {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
		Page<Order> pagedResult = orderRepository.getOrdersInWorkingStateWithDetails(pageable);
		if (pagedResult.hasContent()) {
			return new ResponseEntity<>(pagedResult.getContent(), HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException(
					"Unable to retrieve the page: " + page
							+ " because no orders resource was found in the database");
		}
	}


	@Deprecated
	@PutMapping(path = "/update/edit")
	public ResponseEntity<Order> udpateOrders(@RequestBody @Valid Order order_, @RequestParam("vuid") long vuid)
			throws ResourceNotFoundException, IllegalArgumentException {
		

		long oid = order_.getId();

		if (order_.getUser().getId() != vuid){
			return ResponseEntity.status(403).body(null);
		}

		Optional<User> userOptional = userRepository.findById(vuid);
		User user = userOptional.orElseThrow(
				() -> new ResourceNotFoundException("User with ID: " + vuid + " not found in the database"));

		Optional<Order> orderOptional = orderRepository.findById(oid);
		Order order = orderOptional.orElseThrow(
				() -> new ResourceNotFoundException("Order with ID: " + oid + " not found in the database"));


		order.setEdit(order_.getEdit(), user.getName(), "");
		orderRepository.save(order);





		String obj = "Order #" + order.getId();
		String msg = "Dear customer, your order is currently edited by "+user.getName()+" (Role: "+user.getType()+")";


		//invio una notifica interna
		Email email = new Email();
		email.setActive(true);
		email.setFrom(user);
		email.setSubject(obj);
		email.setBody(msg);
		email.setOrder(order);
		email.setSendedAt(new Date());
		
		if (user.getType() == "Order")
		{
			email.setTo(order.getUser());
			emailRepository.save(email);
		} else {
			
			for (User usr : userRepository.getUsersByRole("Order")) {

				if (user.getId() != usr.getId()) {
				//ricreo l'istanza per evitare di sovrascrivere le mail
				Email nem = new Email();
				nem.setActive(email.isActive());
				nem.setSubject(email.getSubject());
				nem.setBody(email.getBody());
				nem.setOrder(email.getOrder());
				nem.setSendedAt(email.getSendedAt());
				nem.setFrom(email.getFrom());
				nem.setTo(usr);

				emailRepository.save(nem);
			}
		}
	}

		return ResponseEntity.status(200).body(order);

	}





// inboxCancelled
@GetMapping(path = "/count/allCanceled")
	public ResponseEntity<Integer> countAllOrdersCancelled()
			throws ResourceNotFoundException, IllegalArgumentException {
		Long count = orderRepository.countTotalOrdersInCancelledState();
		if (count == 0) {
			throw new ResourceNotFoundException(
					"Unable to perform the count. No orders resource was found in the database!");
		} else {
			return new ResponseEntity<>(count.intValue(), HttpStatus.OK);
		}
	}

// inboxCancelled
	@GetMapping(path = "/inbox/allCanceled")
	public ResponseEntity<List<Order>> getOrdersCancelled(@RequestParam("page") int page,
			@RequestParam("size") int size)
			throws ResourceNotFoundException, IllegalArgumentException {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
		Page<Order> pagedResult = orderRepository.getOrdersInCancelledStatWithDetails(pageable);
		if (pagedResult.hasContent()) {
			return new ResponseEntity<>(pagedResult.getContent(), HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException(
					"Unable to retrieve the page: " + page
							+ " because no orders resource was found in the database");
		}
	}


}
