package com.javainuse.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.text.Style;
import javax.validation.Valid;

import org.hibernate.mapping.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javainuse.details.ApiResponse;
import com.javainuse.entities.Book;
import com.javainuse.entities.JoinTable;
import com.javainuse.entities.Order;
import com.javainuse.entities.User;
import com.javainuse.exceptions.ResourceNotFoundException;
import com.javainuse.repositories.BookRepository;
import com.javainuse.repositories.JoinTableRepository;
import com.javainuse.repositories.OrderRepository;
import com.javainuse.repositories.UserRepository;








@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/service", produces = "application/json")
public class ServiceController {



    
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final JoinTableRepository joinTableRepository;




    @Autowired
    public ServiceController(ObjectMapper objectMapper, OrderRepository orderRepository, UserRepository userRepository,BookRepository bookRepository, JoinTableRepository joinTableRepository) {
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.joinTableRepository = joinTableRepository;
    }
	





    @PostMapping(path = "/addOrder", consumes = "application/json")
    public ResponseEntity<Object> postJoinTable(@RequestBody String orderRequest) throws JsonMappingException, JsonProcessingException, MethodArgumentNotValidException, IllegalArgumentException, ResourceNotFoundException {

        Map<String, Object> data = this.objectMapper.readValue(orderRequest, Map.class);


        Long uid = ((Number) data.get("userId")).longValue();

        Optional<User> optionalUser = userRepository.findById(uid);
	    User user = optionalUser.orElseThrow(() -> new ResourceNotFoundException("Unable to create the order because the user with ID: " + uid + " was not found in the database"));

        Order newOrder = new Order();
	    newOrder.setUser(user); 
	    orderRepository.save(newOrder);

        List<Map<String, Object>> books = (List<Map<String, Object>>) data.get("books");
        for (Map<String, Object> x : books)
        {
            Long bid = ((Number) x.get("id")).longValue();
            Integer q = ((Number) x.get("q")).intValue();
            System.out.println(bid+" - "+q);


            //Book book = bookRepository.findById(bid);
            Optional<Book> optionalBook = bookRepository.findById(bid);
	        Book book = optionalBook.orElseThrow(() -> new ResourceNotFoundException("Unable to find book with ID: "+bid));

            JoinTable.JoinTableId joinTableId = new JoinTable.JoinTableId(newOrder.getId(), bid);

            JoinTable jt = new JoinTable(joinTableId, q, book.getPrice());
            jt.setBook(book);
            jt.setOrder(newOrder);
            joinTableRepository.save(jt);

        }




	    String message = "The order for the user with ID: " + uid+ " was created successfully";
	    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message));
	}





}



/*
    query per la lettura degli ordini effettuati
    SELECT b.name, jt.quantity, jt.price FROM books b, orders o, join_table jt WHERE o.id=16 AND o.id=jt.order_id AND jt.book_id=b.id
 */