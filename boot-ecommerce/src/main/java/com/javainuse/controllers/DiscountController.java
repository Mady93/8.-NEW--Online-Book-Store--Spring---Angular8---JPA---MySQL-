package com.javainuse.controllers;

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

import com.javainuse.details.ApiResponse;
import com.javainuse.entities.Discount;
import com.javainuse.exceptions.ResourceNotFoundException;
import com.javainuse.repositories.DiscountRepository;

//paginazione
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/discounts", produces = "application/json")
public class DiscountController {

	private final DiscountRepository discountRepository;

	@Autowired
	public DiscountController(DiscountRepository discountRepository) {
		this.discountRepository = discountRepository;
	}
	
	
	
	
	
	
	@GetMapping(path = "/count")
	public ResponseEntity<Integer> countDiscounts() throws ResourceNotFoundException, IllegalArgumentException {
		Long count = discountRepository.countByNotDeleted();
		if (count == 0) {
			throw new ResourceNotFoundException(
					"Unable to perform the count. No discounts resource was found in the database");
		} else {
			return new ResponseEntity<>(count.intValue(), HttpStatus.OK);
		}
	}
	
	
	
	

	@GetMapping(path = "/get")
	public ResponseEntity<List<Discount>> getDiscounts(@RequestParam("page") int page, @RequestParam("size") int size)
			throws ResourceNotFoundException, IllegalArgumentException {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
		Page<Discount> pagedResult = discountRepository.findByNotDeleted(pageable);
		if (pagedResult.hasContent()) {
			return new ResponseEntity<>(pagedResult.getContent(), HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException(
					"Unable to retrieve the page: " + page + ". No books resource was found in the database");
		}
	}
	
	

	
	
	

	@GetMapping(path = "/{id:\\d+}/one")
	public ResponseEntity<Discount> getDiscountById(@PathVariable("id") Long id)
			throws ResourceNotFoundException, IllegalArgumentException {

		Optional<Discount> op = discountRepository.findById(id);
		Discount d = op.orElseThrow(
				() -> new ResourceNotFoundException("Unable to retrieve the resource. The discount resource with ID: "
						+ id + " was not found in the database"));
		return ResponseEntity.ok().body(d);
	}
	
	
	
	
	
	@PostMapping(path = "/add", consumes = "application/json")
	public ResponseEntity<Object> postDiscount(@RequestBody @Valid Discount d)
			throws MethodArgumentNotValidException, IllegalArgumentException {

		d.setActive(true);
		
		discountRepository.save(d);
		
		String message = "Discount created successfully";
		
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message));
	}
	
	
	
	
	
	
	
	@PutMapping(path = "/update/{id:\\d+}", consumes = "application/json")
	public ResponseEntity<Object> putDiscount(@PathVariable("id") Long id, @RequestBody @Valid Discount d)
			throws ResourceNotFoundException, MethodArgumentNotValidException, IllegalArgumentException {

		Optional<Discount> op = discountRepository.findById(id);
		
		if (op.isPresent()) {
			
			Discount existing = op.get();
			
			existing.setPercentage(d.getPercentage());
			existing.setActive(d.isActive());
			
			discountRepository.save(existing);

			String message = "Discount updated successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
		} else {
			throw new ResourceNotFoundException("Unable to perform the modification. The book resource with ID: "
					+ id + " was not found in the database");
		}
	}
	
	
	
	
	
	
	
	
	@DeleteMapping(path = "/{discountId:\\d+}/delete")
	public ResponseEntity<Object> deleteDiscount(@PathVariable("discountId") Long discountId)
			throws ResourceNotFoundException, IllegalArgumentException {

		Optional<Discount> op = discountRepository.findById(discountId);
		if (op.isPresent()) {

			Discount d = op.get();

			d.setActive(false);
			
			discountRepository.save(d);

			String message = "Discount have been deleted successfully";
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));

		} else {
			throw new ResourceNotFoundException("Unable to perform the deletion. The discount resource with ID: " + discountId
					+ " was not found in the database");
		}
	}
	
	
	
	


	
	@DeleteMapping(path = "/deleteAll")
	public ResponseEntity<ApiResponse> deleteDiscounts() throws ResourceNotFoundException, IllegalArgumentException {

	    List<Discount> list = (List<Discount>) discountRepository.findAll();
	    
	    if (!list.isEmpty()) {
	    	
	    // Imposta isActive a false per tutti gli sconti --> cancellazione
	    	
	        list.forEach(discount -> discount.setActive(false));
	        
	        discountRepository.saveAll(list); 

	        String message = "Discounts have been deleted successfully";
	        
	        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(), message));
	    } else {
	        throw new ResourceNotFoundException("Unable to perform the deletion. No discounts resource was found in the database");
	    }
	}
}
