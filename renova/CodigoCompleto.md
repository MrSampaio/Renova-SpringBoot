### Arquivo: \⁠ ./src/test/java/com/renova/renova/RenovaApplicationTests.java\ ⁠
⁠ java
package com.renova.renova;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RenovaApplicationTests {

	@Test
	void contextLoads() {
	}

}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/security/SecurityConfig.java\ ⁠
⁠ java
package com.renova.renova.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register-costumer").permitAll()
                        .requestMatchers(HttpMethod.POST, "/catalog").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/catalog").hasRole("EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // muda depois para o front deployado
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/security/SecurityFilter.java\ ⁠
⁠ java
package com.renova.renova.security;

import com.renova.renova.features.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.w3c.dom.html.HTMLTableCaptionElement;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.security.Security;

@Component
public class SecurityFilter extends OncePerRequestFilter{

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null){
            var email = tokenService.validateToken(token);
            UserDetails user = userRepository.findByEmail(email);

            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    // traduz o cookie de volta para jwt
    private String recoverToken(HttpServletRequest httpServletRequest){
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/security/TokenService.java\ ⁠
⁠ java
package com.renova.renova.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.renova.renova.features.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException error){
            throw new RuntimeException("Error when trying to generate token", error);
        }
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException error){
            throw new RuntimeException("Error when trying to validate token", error);
        }

    }

    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/products/ProductController.java\ ⁠
⁠ java
package com.renova.renova.features.products;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/catalog")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ProductController {

    @Autowired
    private ProductService productService;

    // POST /catalog
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        Product savedProduct = productService.registerProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping
    public ResponseEntity<List<Product>> listProducts(@RequestParam(value = "nome", required = false) String name) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(productService.findProductsByName(name));
        }
        return ResponseEntity.ok(productService.listProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.findProductById(id));
    }
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/products/Product.java\ ⁠
⁠ java
package com.renova.renova.features.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String productId;

    @NotBlank(message = "O nome do produto é obrigatório.")
    private String productName;

    @NotBlank(message = "A descrição do produto é obrigatória.")
    private String description;

    @JdbcTypeCode(SqlTypes.DECIMAL)
    @Positive(message = "Insira um preço válido maior que zero.")
    private double price;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O status do produto é obrigatório.")
    private ProductStatus status;

} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/products/ProductRepository.java\ ⁠
⁠ java
package com.renova.renova.features.products;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByProductNameContainingIgnoreCase(String name);
}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/products/ProductService.java\ ⁠
⁠ java
package com.renova.renova.features.products;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public Product registerProduct(Product product) {
        return repository.save(product);
    }

    public List<Product> listProducts() {
        return repository.findAll();
    }

    public List<Product> findProductsByName(String name) {
        return repository.findByProductNameContainingIgnoreCase(name);
    }

    public Product findProductById(String id) {
        // se não encontrar, lança exceção
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto com o ID " + id + "não encontrado."));
    }
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/products/ProductStatus.java\ ⁠
⁠ java
package com.renova.renova.features.products;

public enum ProductStatus {
    AVAILABLE, RESERVED, SOLD
}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/auth/dto/CostumerRegistrationRequest.java\ ⁠
⁠ java
package com.renova.renova.features.auth.dto;

import com.renova.renova.features.user.UserRole;

public record CostumerRegistrationRequest(String name, String email, String password, String phone, UserRole role) {
}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/auth/dto/AuthResponse.java\ ⁠
⁠ java
package com.renova.renova.features.auth.dto;

public record AuthResponse(String token) {
}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/auth/dto/LoginRequest.java\ ⁠
⁠ java
package com.renova.renova.features.auth.dto;

public record LoginRequest(String email, String password) {
}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/auth/AuthController.java\ ⁠
⁠ java
package com.renova.renova.features.auth;

import com.renova.renova.features.auth.dto.AuthResponse;
import com.renova.renova.features.auth.dto.LoginRequest;
import com.renova.renova.features.auth.dto.CostumerRegistrationRequest;
import com.renova.renova.features.user.User;
import com.renova.renova.features.user.UserRepository;
import com.renova.renova.features.user.UserRole;
import com.renova.renova.security.TokenService;
import jakarta.validation.Valid;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

@RestController
@RequestMapping("auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    UserRepository repository;

    @Autowired
    TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequest data, HttpServletResponse response) {
        var emailPassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authManager.authenticate(emailPassword);

        var token = tokenService.generateToken((User) Objects.requireNonNull(auth.getPrincipal()));

        this.addJwtCookie(response, token);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register-costumer")
    public ResponseEntity register(@RequestBody @Valid CostumerRegistrationRequest data, HttpServletResponse response) {
        if (this.repository.findByEmail(data.email()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.email(), encryptedPassword, data.name(), data.phone(), UserRole.CUSTOMER);

        this.repository.save(newUser);

        // gera o token diretamente para o usuário recém-criado
        var token = tokenService.generateToken(newUser);

        // anexa o cookie HTTP-Only para logar automaticamente
        this.addJwtCookie(response, token);

        return ResponseEntity.ok().build();
    }

    private void addJwtCookie(HttpServletResponse response, String token) {
        // criação do cookie HTTP-Only
        Cookie cookie = new Cookie("jwt_token", token);
        cookie.setHttpOnly(true); // oculta do JavaScript
        cookie.setSecure(false); // MUDAR PARA TRUE QUANDO FOR PRA PRODUÇÃO
        cookie.setPath("/"); // libera para todas as rotas da API
        cookie.setMaxAge(7200); // duraçåo de duas horas

        response.addCookie(cookie);
    }
}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/auth/AuthService.java\ ⁠
⁠ java
package com.renova.renova.features.auth;

import com.renova.renova.features.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username);
    }


}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/booking/dto/BookingRequest.java\ ⁠
⁠ java
package com.renova.renova.features.booking.dto;

import com.renova.renova.features.booking.Booking;
import com.renova.renova.features.products.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingRequest(

        @NotNull(message = "Os dados da reserva são obrigatórios.")
        @Valid // adiciona @NotNull e @Positive
        Booking booking,

        @NotEmpty(message = "O carrinho não pode estar vazio.")
        @Valid // valida cada produto na lista
        List<Product> basket

) {} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/booking/BookingService.java\ ⁠
⁠ java
package com.renova.renova.features.booking;

import com.renova.renova.features.booking.dto.BookingRequest;
import com.renova.renova.features.products.Product;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    // garante que tudo salva ou tudo cancela na hora
    @Transactional
    public boolean finalizeBooking(BookingRequest request) {
        Booking booking = request.booking();
        List<Product> cart = request.basket();

        // define os dados de criação
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.AWAITING_PAYMENT);

        // JPA vai pegar esses produtos e fazer os INSERTS na booking_items
        booking.setProducts(cart);

        bookingRepository.save(booking);
        return true;
    }

    public List<Booking> listAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> listBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> findBookingsByCustomerName(String customerName) {
        return bookingRepository.findByCustomerNameContaining(customerName);
    }

    public Booking findBookingById(String bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }

    public List<Product> listProductsByBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada."));
        return booking.getProducts();
    }

    public void updateBookingStatus(String bookingId, BookingStatus newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada."));

        booking.setStatus(newStatus);
        bookingRepository.save(booking);
    }
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/booking/Booking.java\ ⁠
⁠ java
package com.renova.renova.features.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.renova.renova.features.products.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String bookingId;

    @NotBlank(message = "O ID do usuário é obrigatório.")
    @Column(name = "user_id")
    private String userId;

    @JdbcTypeCode(SqlTypes.DECIMAL)
    @Positive(message = "O valor total da reserva deve ser maior que zero.")
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O método de pagamento é obrigatório.")
    private PaymentMethod paymentMethod;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime bookingDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Transient
    private String customerName;

    @ManyToMany
    @JoinTable(
            name = "booking_items", // Nome da sua tabela de junção no banco
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/booking/PaymentMethod.java\ ⁠
⁠ java
package com.renova.renova.features.booking;

public enum PaymentMethod {
    DEBIT, CASH, PIX
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/booking/BookingStatus.java\ ⁠
⁠ java
package com.renova.renova.features.booking;

public enum BookingStatus {
    AWAITING_PAYMENT, COMPLETED, CANCELLED
}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/booking/BookingController.java\ ⁠
⁠ java
package com.renova.renova.features.booking;

import com.renova.renova.features.booking.dto.BookingRequest;
import com.renova.renova.features.products.Product;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Boolean> createBooking(@RequestBody @Valid BookingRequest request) {
        return ResponseEntity.ok(bookingService.finalizeBooking(request));
    }

    @GetMapping
    public ResponseEntity<List<Booking>> listBookings(@RequestParam(name = "customerName", required = false) String customerName) {
        if (customerName != null && !customerName.isBlank()) {
            return ResponseEntity.ok(bookingService.findBookingsByCustomerName(customerName));
        }
        return ResponseEntity.ok(bookingService.listAllBookings());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> listUserBookings(@PathVariable String userId) {
        return ResponseEntity.ok(bookingService.listBookingsByUser(userId));
    }

    @GetMapping("/{bookingId}/products")
    public ResponseEntity<List<Product>> listProductsByBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.listProductsByBooking(bookingId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> findBookingById(@PathVariable String bookingId) {
        Booking booking = bookingService.findBookingById(bookingId);
        return booking != null ? ResponseEntity.ok(booking) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<Void> updateBookingStatus(
            @PathVariable String bookingId,
            @RequestParam("newStatus") BookingStatus newStatus) {

        bookingService.updateBookingStatus(bookingId, newStatus);
        return ResponseEntity.ok().build();
    }
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/booking/BookingRepository.java\ ⁠
⁠ java
package com.renova.renova.features.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {

    // lista todas as reservas de um usuário específico
    List<Booking> findByUserId(String userId);

    @Query(value = "SELECT b.*, u.name as customer_name FROM bookings b JOIN users u ON b.user_id = u.user_id WHERE u.name LIKE %:customerName%", nativeQuery = true)
    List<Booking> findByCustomerNameContaining(@Param("customerName") String customerName);
}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/user/dto/UserUpdateDTO.java\ ⁠
⁠ java
package com.renova.renova.features.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @NotBlank(message = "O nome não pode estar vazio.")
        String name,

        @Size(min = 11, max = 11, message = "O telefone deve ter 11 dígitos.")
        String phone
) {} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/user/UserRole.java\ ⁠
⁠ java
package com.renova.renova.features.user;

public enum UserRole {
    EMPLOYEE("EMPLOYEE"),
    CUSTOMER("CUSTOMER");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static UserRole fromRole(String role) {
        for (UserRole userRole : values()) {
            if (userRole.role.equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Unknown user role: " + role);
    }
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/user/UserService.java\ ⁠
⁠ java
package com.renova.renova.features.user;

import com.renova.renova.features.user.dto.UserUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public User getProfile(String email) {
        return repository.findByEmail(email);
    }

    public User updateProfile(String email, UserUpdateDTO data) {
        User user = repository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found.");

        user.setName(data.name());
        user.setPhone(data.phone());

        return repository.save(user);
    }
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/user/User.java\ ⁠
⁠ java
package com.renova.renova.features.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @Email(message = "Formato de e-mail inválido.")
    @NotBlank(message = "O e-mail é obrigatório.")
    @Column(unique = true)
    private String email;

    private String password;

    @Size(min = 11, max = 11, message = "O telefone deve ter exatamente 11 dígitos.")
    @Column(columnDefinition = "CHAR(11)")
    private String phone;

    @Convert(converter = UserRoleConverter.class)
    private UserRole userRole;

    public User(String email, String password, String name, String phone, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.userRole = userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.userRole == UserRole.EMPLOYEE) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/user/UserRepository.java\ ⁠
⁠ java
package com.renova.renova.features.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User findByEmail(String email);

}
 ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/user/UserRoleConverter.java\ ⁠
⁠ java
package com.renova.renova.features.user;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole userRole) {
        return userRole == null ? null : userRole.getRole();
    }

    @Override
    public UserRole convertToEntityAttribute(String value) {
        return value == null ? null : UserRole.fromRole(value);
    }
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/features/user/UserController.java\ ⁠
⁠ java
package com.renova.renova.features.user;

import com.renova.renova.features.user.dto.UserUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    // retorna os dados do usuario logado
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal User loggedUser) {
        // O loggedUser já contém as informações descodificadas do token!
        return ResponseEntity.ok(userService.getProfile(loggedUser.getEmail()));
    }

    // atualiza os dados do usuario logado
    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(
            @AuthenticationPrincipal User loggedUser,
            @RequestBody @Valid UserUpdateDTO data) {

        User updatedUser = userService.updateProfile(loggedUser.getEmail(), data);
        return ResponseEntity.ok(updatedUser);
    }
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/exceptions/GlobalExceptionHandler.java\ ⁠
⁠ java
package com.renova.renova.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // pega os erros das anotações @NotBlank, @Positive, etc
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            // retorna { "productName": "O campo 'nome do produto' é obrigatório!" }
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
} ⁠

---

### Arquivo: \⁠ ./src/main/java/com/renova/renova/RenovaApplication.java\ ⁠
⁠ java
package com.renova.renova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RenovaApplication {

	public static void main(String[] args) {
		SpringApplication.run(RenovaApplication.class, args);
	}

}
 ⁠

---

