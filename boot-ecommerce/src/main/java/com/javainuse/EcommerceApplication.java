package com.javainuse;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.javainuse.entities.Book;
import com.javainuse.entities.JoinTable;
import com.javainuse.entities.JoinTable.JoinTableId;
import com.javainuse.entities.Order;
import com.javainuse.entities.User;
import com.javainuse.repositories.BookRepository;
import com.javainuse.repositories.JoinTableRepository;
import com.javainuse.repositories.OrderRepository;
import com.javainuse.repositories.UserRepository;

//Book
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@EnableJpaAuditing
public class EcommerceApplication implements CommandLineRunner {

	
		@Autowired
		BookRepository bookRepository;
	
		@Autowired
		UserRepository userRepository;
		
		@Autowired
		OrderRepository orderRepository;

		@Autowired
	    private JoinTableRepository joinTableRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		clearData();
		saveData();
		showData();
	}


    @Transactional
	private void clearData() {
    	orderRepository.deleteAll();
		userRepository.deleteAll();
		
	}


    @Transactional
	private void saveData() {
    	saveDataAllEntity();
	}


    // Save objects Book and User objects that include Orders
	private void saveDataAllEntity() {

		
		 // Creazione di oggetti Book
	    byte[] bookImage1 = loadBookImage("C:/Users/mady/Desktop/books/1.jpg"); // Carica l'immagine da un percorso specifico
	    Book book1 = new Book("La vita intima", "Niccolò Ammaniti", 18.05, bookImage1);
	    
	    byte[] bookImage2 = loadBookImage("C:/Users/mady/Desktop/books/2.jpg");
	    Book book2 = new Book("Il cognome delle donne", "Aurora Tamigio", 18.05, bookImage2);
	    
	    byte[] bookImage3 = loadBookImage("C:/Users/mady/Desktop/books/3.jpg");
	    Book book3 = new Book("Death note. Vol. 3", "Takeshi Obata", 4.66, bookImage3);
	    
	    byte[] bookImage4 = loadBookImage("C:/Users/mady/Desktop/books/4.jpg");
	    Book book4 = new Book("Naruto. Il mito. Vol. 11", "Masashi Kishimoto", 4.66, bookImage4);
	    
	    byte[] bookImage5 = loadBookImage("C:/Users/mady/Desktop/books/5.jpg");
	    Book book5 = new Book("Monster deluxe. Vol. 3", "Naoki Urasawa", 13.20, bookImage5);
	    
	    byte[] bookImage6 = loadBookImage("C:/Users/mady/Desktop/books/6.jpg");
	    Book book6 = new Book("The Unbroken. Magic of the Lost. Vol. 1", "C. L. Clark", 22.80, bookImage6);
	    
	    byte[] bookImage7 = loadBookImage("C:/Users/mady/Desktop/books/7.jpg");
	    Book book7 = new Book("Inuyasha. Wide edition. Vol. 6", "Rumiko Takahashi", 9.45, bookImage7);
	    
	    byte[] bookImage8 = loadBookImage("C:/Users/mady/Desktop/books/8.jpg");
	    Book book8 = new Book("1984", "George Orwell", 10.45, bookImage8);
	    
	    byte[] bookImage9 = loadBookImage("C:/Users/mady/Desktop/books/9.jpg");
	    Book book9 = new Book("Storie della fantascienza. Vol. 1: 1939-1943", "Isaac Asimov", 33.25, bookImage9);
	    
	    byte[] bookImage10 = loadBookImage("C:/Users/mady/Desktop/books/10.jpg");
	    Book book10 = new Book("Prison school. Vol. 2", "Akira Hiramoto", 4.66, bookImage10);
	    
	    byte[] bookImage11 = loadBookImage("C:/Users/mady/Desktop/books/11.jpg");
	    Book book11 = new Book("Piccolo manuale di Arduino. Il cuore della robotica fai da te", "Matteo Tettamanzi", 12.26, bookImage11);

	    // Salva i libri nel repository
	   
	    Book b1 = bookRepository.save(book1);
	   	Book b2 = bookRepository.save(book2);
	    Book b3 = bookRepository.save(book3);
	    Book b4 = bookRepository.save(book4);
	    Book b5 = bookRepository.save(book5);
	    bookRepository.save(book6);
	    bookRepository.save(book7);
	    bookRepository.save(book8);
	    bookRepository.save(book9);
	    bookRepository.save(book10);
	    bookRepository.save(book11);
		
		
		
		
        User user1 = new User("Pippo Baudo", "pippo@gmail.com", "11111111", "Admin");
        User user2 = new User("Erminio Ottone", "erminio@gmail.com", "11111110", "User");
        User user3 = new User("Silvia Lolli", "silvia@gmail.com", "11111100", "User");
        User user4 = new User("Monalisa Silvestri", "monalisa@gmail.com", "11111000", "User");
        User user5 = new User("Ugo Fantozzi", "ugo@gmail.com", "11110000", "User");


		
      
         userRepository.save(user1);
         userRepository.save(user2);
         userRepository.save(user3);
         userRepository.save(user4);
         userRepository.save(user5);


       
         
		// Create Orders
		Order order1 = new Order(user1);
		order1.setUser(user1);

		Order order2 = new Order(user1);
		order2.setUser(user1);
		
		Order order3 = new Order(user1);
		order3.setUser(user1);
		
		Order order4 = new Order(user1);
		order4.setUser(user1);
		
		Order order5 = new Order(user2);
		order5.setUser(user2);
		
		Order order6 = new Order(user2);
		order6.setUser(user2);
		
		Order order7 = new Order(user2);
		order7.setUser(user2);
		
		
		Order order8 = new Order(user3);
		order8.setUser(user3);
		
		
		Order order9 = new Order(user3);
		order9.setUser(user3);
		
		
		Order order10 = new Order(user4);
		order10.setUser(user4);
		
		
		Order order11 = new Order(user4);
		order11.setUser(user4);
		
		Order order12 = new Order(user4);
		order12.setUser(user4);
		
		Order order13 = new Order(user5);
		order13.setUser(user5);
		
		Order order14 = new Order(user5);
		order14.setUser(user5);
		
		Order order15 = new Order(user5);
		order15.setUser(user5);


		// Orders related to User entity
		Order o1 = orderRepository.save(order1);
		Order o2 = orderRepository.save(order2);
		Order o3 = orderRepository.save(order3);
		orderRepository.save(order4);
		orderRepository.save(order5);
		orderRepository.save(order6);
		orderRepository.save(order7);
		orderRepository.save(order8);
		orderRepository.save(order9);
		orderRepository.save(order10);
		orderRepository.save(order11);
		orderRepository.save(order12);
		orderRepository.save(order13);
		orderRepository.save(order14);
		orderRepository.save(order15);
		
		
		JoinTable.JoinTableId joinTable1Id1 = new JoinTableId(o1.getId(), b1.getId());
		JoinTable joinTable1 = new JoinTable(joinTable1Id1, 3, 18.05);
		joinTable1.setOrder(o1); 
		joinTable1.setBook(b1); 

		JoinTable.JoinTableId joinTable1Id2 = new JoinTableId(o1.getId(), b2.getId());
		JoinTable joinTable2 = new JoinTable(joinTable1Id2, 2, 18.05);
		joinTable2.setOrder(o1); 
		joinTable2.setBook(b2); 
		
		JoinTable.JoinTableId joinTable1Id3 = new JoinTableId(o2.getId(), b3.getId());
		JoinTable joinTable3 = new JoinTable(joinTable1Id3, 1, 4.66);
		joinTable3.setOrder(o2); 
		joinTable3.setBook(b3); 

		JoinTable.JoinTableId joinTable1Id4 = new JoinTableId(o2.getId(), b4.getId());
		JoinTable joinTable4 = new JoinTable(joinTable1Id4, 3, 4.66);
		joinTable4.setOrder(o2); 
		joinTable4.setBook(b4); 

		JoinTable.JoinTableId joinTable1Id5 = new JoinTableId(o3.getId(), b5.getId());
		JoinTable joinTable5 = new JoinTable(joinTable1Id5, 5, 13.20);
		joinTable5.setOrder(o3); 
		joinTable5.setBook(b5); 

		joinTableRepository.save(joinTable1);
		joinTableRepository.save(joinTable2);
	    joinTableRepository.save(joinTable3);
	    joinTableRepository.save(joinTable4);
	    joinTableRepository.save(joinTable5);
	
    }
	

	// image []byte
	private byte[] loadBookImage(String imagePath) {
	    try {
	        Path path = Paths.get(imagePath);
	        return Files.readAllBytes(path);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return new byte[0]; // Restituisce un array vuoto in caso di errore
	    }
	}


	private void showData() {
		// get All data
		
		//List<Book> bookLst = bookRepository.findAll();
		Iterable<Book> bookLst = bookRepository.findAll();
		
		List<User> userLst = userRepository.findAll();
		List<Order> orderLst = orderRepository.findAll();
		
		Iterable<JoinTable> joinTableLst = joinTableRepository.findAll();
		
		System.out.println("");
		System.out.println("=================== Book List: ==================");
		//bookLst.forEach(System.out::println);
		

		System.out.println("");
		System.out.println("=================== Order List: ==================");
		orderLst.forEach(System.out::println);

		System.out.println("");
		System.out.println("=================== User List: ==================");
		userLst.forEach(User -> System.out.println(User.toString()));
		
		
		System.out.println("");
		System.out.println("=================== JoinTable List: ==================");
		joinTableLst.forEach(System.out::println);
	}
		

}
