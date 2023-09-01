package com.javainuse;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.javainuse.entities.Book;
import com.javainuse.entities.Email;
import com.javainuse.entities.OrderBook;
import com.javainuse.entities.OrderBook.OrderBooksId;
import com.javainuse.entities.Order;
import com.javainuse.entities.User;
import com.javainuse.repositories.BookRepository;
import com.javainuse.repositories.EmailRepository;
import com.javainuse.repositories.OrderBookRepository;
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
	EmailRepository emailRepository;

	@Autowired
	private OrderBookRepository orderBookRepository;

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
		byte[] bookImage1 = loadBookImage("C:/Users/mady/Desktop/books/1.jpg"); // Carica l'immagine da un percorso
																				// specifico
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
		Book book11 = new Book("Piccolo manuale di Arduino. Il cuore della robotica fai da te", "Matteo Tettamanzi",
				12.26, bookImage11);

		// Salva i libri nel repository

		Book b1 = bookRepository.save(book1);
		Book b2 = bookRepository.save(book2);
		Book b3 = bookRepository.save(book3);
		Book b4 = bookRepository.save(book4);
		Book b5 = bookRepository.save(book5);
		Book b6 = bookRepository.save(book6);
		Book b7 = bookRepository.save(book7);
		Book b8 = bookRepository.save(book8);
		Book b9 = bookRepository.save(book9);
		Book b10 = bookRepository.save(book10);
		Book b11 = bookRepository.save(book11);

		User user1 = new User("Pippo Baudo", "Admin", "pippo12_2023@libero.it", "11111111");
		user1.setActive(true);

		User user2 = new User("Erminio Ottone", "User", "eccomerceorder@libero.it", "11111110");
		user2.setActive(true);

		User user3 = new User("Silvia Lolli", "Seller", "silvia_23_29@libero.it", "11111100");
		user3.setActive(true);

		User user4 = new User("Monalisa Silvestri", "User", "monalisa_2023@libero.it", "11111000");
		user4.setActive(true);

		User user5 = new User("Ugo Fantozzi", "User", "ugofantozzi_2023@libero.it", "11110000");
		user5.setActive(true);

		User user6 = new User("Salvatore Brutto", "User", "salvatore23_88@libero.it", "11100000");
		user6.setActive(true);

		User user7 = new User("Squallor", "Order", "squallor_2023@libero.it", "11000000");
		user7.setActive(true);
		
		User user8 = new User("Testone", "Order", "testone_2023@libero.it", "10000000");
		user8.setActive(true);

		userRepository.save(user1);
		userRepository.save(user2);
		userRepository.save(user3);
		userRepository.save(user4);
		userRepository.save(user5);

		userRepository.save(user6);
		userRepository.save(user7);
		userRepository.save(user8);

		// Create Orders
		Order order1 = new Order(user1, "Working");
		order1.setActive(true);
		order1.setUser(user1);

		Order order2 = new Order(user1, "Send");
		order2.setActive(true);
		order2.setUser(user1);

		Order order3 = new Order(user1, "Send");
		order3.setActive(true);
		order3.setUser(user1);

		Order order4 = new Order(user1, "Working");
		order4.setActive(true);
		order4.setUser(user1);

		Order order5 = new Order(user2, "Send");
		order5.setActive(true);
		order5.setUser(user2);

		Order order6 = new Order(user2, "Working");
		order6.setActive(true);
		order6.setUser(user2);

		Order order7 = new Order(user2, "Working");
		order7.setActive(true);
		order7.setUser(user2);

		Order order8 = new Order(user3, "Send");
		order8.setActive(true);
		order8.setUser(user3);

		Order order9 = new Order(user3, "Send");
		order9.setActive(true);
		order9.setUser(user3);

		Order order10 = new Order(user4, "Working");
		order10.setActive(true);
		order10.setUser(user4);

		Order order11 = new Order(user4, "Working");
		order11.setActive(true);
		order11.setUser(user4);

		Order order12 = new Order(user4, "Send");
		order12.setActive(true);
		order12.setUser(user4);

		Order order13 = new Order(user5, "Send");
		order13.setActive(true);
		order13.setUser(user5);

		Order order14 = new Order(user5, "Send");
		order14.setActive(true);
		order14.setUser(user5);

		Order order15 = new Order(user5, "Working");
		order15.setActive(true);
		order15.setUser(user5);

		// Orders related to User entity
		Order o1 = orderRepository.save(order1);
		Order o2 = orderRepository.save(order2);
		Order o3 = orderRepository.save(order3);
		Order o4 = orderRepository.save(order4);
		Order o5 = orderRepository.save(order5);
		Order o6 = orderRepository.save(order6);
		Order o7 = orderRepository.save(order7);
		Order o8 = orderRepository.save(order8);
		Order o9 = orderRepository.save(order9);
		Order o10 = orderRepository.save(order10);
		Order o11 = orderRepository.save(order11);
		Order o12 = orderRepository.save(order12);
		Order o13 = orderRepository.save(order13);
		Order o14 = orderRepository.save(order14);
		Order o15 = orderRepository.save(order15);

		Email email1 = new Email("bruttus", "ugly", "prrrrrrr", "kebab pizza patatine sushi sashimi fettuccine alfredo", order2);
		email1.setActive(true);
		emailRepository.save(email1);

		OrderBook.OrderBooksId orderBooksId1 = new OrderBooksId(o1.getId(), b1.getId());
		OrderBook orderBook1 = new OrderBook(orderBooksId1, 3, b1.getPrice());
		orderBook1.setOrder(o1);
		orderBook1.setBook(b1);
		orderBook1.setActive(true);

		OrderBook.OrderBooksId orderBooksId2 = new OrderBooksId(o1.getId(), b2.getId());
		OrderBook orderBook2 = new OrderBook(orderBooksId2, 2, b2.getPrice());
		orderBook2.setOrder(o1);
		orderBook2.setBook(b2);
		orderBook2.setActive(true);

		OrderBook.OrderBooksId orderBooksId3 = new OrderBooksId(o2.getId(), b3.getId());
		OrderBook orderBook3 = new OrderBook(orderBooksId3, 1, b3.getPrice());
		orderBook3.setOrder(o2);
		orderBook3.setBook(b3);
		orderBook3.setActive(true);

		OrderBook.OrderBooksId orderBooksId4 = new OrderBooksId(o2.getId(), b4.getId());
		OrderBook orderBook4 = new OrderBook(orderBooksId4, 3, b4.getPrice());
		orderBook4.setOrder(o2);
		orderBook4.setBook(b4);
		orderBook4.setActive(true);

		OrderBook.OrderBooksId orderBooksId5 = new OrderBooksId(o3.getId(), b5.getId());
		OrderBook orderBook5 = new OrderBook(orderBooksId5, 9, b5.getPrice());
		orderBook5.setOrder(o3);
		orderBook5.setBook(b5);
		orderBook5.setActive(true);

		OrderBook.OrderBooksId orderBooksId6 = new OrderBooksId(o4.getId(), b6.getId());
		OrderBook orderBook6 = new OrderBook(orderBooksId6, 3, b6.getPrice());
		orderBook6.setOrder(o4);
		orderBook6.setBook(b6);
		orderBook6.setActive(true);

		OrderBook.OrderBooksId orderBooksId7 = new OrderBooksId(o5.getId(), b7.getId());
		OrderBook orderBook7 = new OrderBook(orderBooksId7, 5, b7.getPrice());
		orderBook7.setOrder(o5);
		orderBook7.setBook(b7);
		orderBook7.setActive(true);

		OrderBook.OrderBooksId orderBooksId8 = new OrderBooksId(o6.getId(), b8.getId());
		OrderBook orderBook8 = new OrderBook(orderBooksId8, 45, b8.getPrice());
		orderBook8.setOrder(o6);
		orderBook8.setBook(b8);
		orderBook8.setActive(true);

		OrderBook.OrderBooksId orderBooksId9 = new OrderBooksId(o7.getId(), b9.getId());
		OrderBook orderBook9 = new OrderBook(orderBooksId9, 7, b9.getPrice());
		orderBook9.setOrder(o7);
		orderBook9.setBook(b9);
		orderBook9.setActive(true);

		OrderBook.OrderBooksId orderBooksId10 = new OrderBooksId(o8.getId(), b10.getId());
		OrderBook orderBook10 = new OrderBook(orderBooksId10, 1, b10.getPrice());
		orderBook10.setOrder(o8);
		orderBook10.setBook(b10);
		orderBook10.setActive(true);

		OrderBook.OrderBooksId orderBooksId11 = new OrderBooksId(o9.getId(), b11.getId());
		OrderBook orderBook11 = new OrderBook(orderBooksId11, 2, b11.getPrice());
		orderBook11.setOrder(o9);
		orderBook11.setBook(b11);
		orderBook11.setActive(true);

		OrderBook.OrderBooksId OrderBooksId12 = new OrderBooksId(o10.getId(), b1.getId());
		OrderBook orderBook12 = new OrderBook(OrderBooksId12, 4, b1.getPrice());
		orderBook12.setOrder(o10);
		orderBook12.setBook(b1);
		orderBook12.setActive(true);

		OrderBook.OrderBooksId orderBooksId13 = new OrderBooksId(o11.getId(), b2.getId());
		OrderBook orderBook13 = new OrderBook(orderBooksId13, 5, b2.getPrice());
		orderBook13.setOrder(o11);
		orderBook13.setBook(b2);
		orderBook13.setActive(true);

		OrderBook.OrderBooksId orderBooksId14 = new OrderBooksId(o12.getId(), b3.getId());
		OrderBook orderBook14 = new OrderBook(orderBooksId14, 5, b3.getPrice());
		orderBook14.setOrder(o12);
		orderBook14.setBook(b3);
		orderBook14.setActive(true);

		OrderBook.OrderBooksId orderBooksId15 = new OrderBooksId(o13.getId(), b4.getId());
		OrderBook orderBook15 = new OrderBook(orderBooksId15, 7, b4.getPrice());
		orderBook15.setOrder(o13);
		orderBook15.setBook(b4);
		orderBook15.setActive(true);

		OrderBook.OrderBooksId orderBooksId16 = new OrderBooksId(o14.getId(), b5.getId());
		OrderBook orderBook16 = new OrderBook(orderBooksId16, 9, b5.getPrice());
		orderBook16.setOrder(o14);
		orderBook16.setBook(b5);
		orderBook16.setActive(true);

		OrderBook.OrderBooksId orderBooksId17 = new OrderBooksId(o15.getId(), b6.getId());
		OrderBook orderBook17 = new OrderBook(orderBooksId17, 10, b6.getPrice());
		orderBook17.setOrder(o15);
		orderBook17.setBook(b6);
		orderBook17.setActive(true);

		orderBookRepository.save(orderBook1);
		orderBookRepository.save(orderBook2);
		orderBookRepository.save(orderBook3);
		orderBookRepository.save(orderBook4);
		orderBookRepository.save(orderBook5);
		orderBookRepository.save(orderBook6);
		orderBookRepository.save(orderBook7);
		orderBookRepository.save(orderBook8);
		orderBookRepository.save(orderBook9);
		orderBookRepository.save(orderBook10);
		orderBookRepository.save(orderBook11);
		orderBookRepository.save(orderBook12);
		orderBookRepository.save(orderBook13);
		orderBookRepository.save(orderBook14);
		orderBookRepository.save(orderBook15);
		orderBookRepository.save(orderBook16);
		orderBookRepository.save(orderBook17);

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

		// List<Book> bookLst = bookRepository.findAll();
		// Iterable<Book> bookLst = bookRepository.findAll();

		List<User> userLst = userRepository.findAll();
		List<Order> orderLst = orderRepository.findAll();

		Iterable<OrderBook> orderBookLst = orderBookRepository.findAll();

		System.out.println("");
		System.out.println("=================== Book List: ==================");
		// bookLst.forEach(System.out::println);

		System.out.println("");
		System.out.println("=================== Order List: ==================");
		orderLst.forEach(System.out::println);

		System.out.println("");
		System.out.println("=================== User List: ==================");
		userLst.forEach(User -> System.out.println(User.toString()));

		System.out.println("");
		System.out.println("=================== orderBook List: ==================");
		orderBookLst.forEach(System.out::println);
	}

}
