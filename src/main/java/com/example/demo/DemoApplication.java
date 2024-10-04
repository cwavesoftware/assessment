package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}

@Controller
@RequestMapping("/users") 
class UserController {

	@Autowired private JdbcTemplate jdbcTemplate;

	@GetMapping("/search")
	public ModelAndView search(@RequestParam String query)
	{
		// String sql = "SELECT * FROM users WHERE username LIKE '%" + query + "%'";
		// List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);
		String sql = "SELECT * FROM users WHERE username LIKE ?";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, new Object[]{"%" + query + "%"});

		return new ModelAndView("userList","users",users);
	}

	@PostMapping("/add")
	public String addUser(@RequestParam String username,@RequestParam String email)
	{
		// String sql ="INSERT INTO users(username, email) VALUES ('"+username+"','"+email+"')";
		// jdbcTemplate.update(sql);
		String sql = "INSERT INTO Users (username, email) VALUES (?, ?)";
        jdbcTemplate.update(sql, username, email);

		return "redirect:/users";
	}

	@PostMapping("/update")
	public String updateUser(@RequestParam int id, @RequestParam String username)
	{
		// String sql="UPDATE users SET username = '" + username + "' WHERE id = " + id;
		// jdbcTemplate.update(sql);
		String sql = "UPDATE users SET username = ? WHERE id = ?";
        jdbcTemplate.update(sql, username, id);

		return "redirect:/users";
	}

	@Bean
	public CommandLineRunner run(JdbcTemplate jdbcTemplate) {
		return args -> {
			// Create Users table
			jdbcTemplate.execute("CREATE TABLE users (id SERIAL, username VARCHAR(255), email VARCHAR(255), password VARCHAR(255) DEFAULT '{noop}mypwd', enabled BOOLEAN DEFAULT TRUE)");

			// Insert a user into the Users table
			jdbcTemplate.update("INSERT INTO users(username, email) VALUES (?, ?)", "john_doe", "john.doe@example.com");

			// Query and print all users
			jdbcTemplate.query("SELECT * FROM users", 
				(rs, rowNum) -> 
				"User: " + rs.getString("username") + 
				", Email: " + rs.getString("email")+ 
				", Password: " + rs.getString("password") +
				", Enabled: " + rs.getString("enabled"))
				.forEach(System.out::println);
		};
	}
}