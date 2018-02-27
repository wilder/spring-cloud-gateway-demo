package com.wilderpereira.customerservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class CustomerServiceApplication {

	@Bean
	ApplicationRunner init(CustomerRepository cr) {
		return args ->
				cr.deleteAll()
					.thenMany(Flux.just("A","B","C").map(l -> new Customer(null, l))
					.flatMap(cr::save)).thenMany(cr.findAll())
					.subscribe( s -> System.out.println(s));

	}

	@Bean
    RouterFunction<?> routes(CustomerRepository cr) {
	    return route(GET("/customers"), r -> ServerResponse.ok().body(cr.findAll(), Customer.class))
                .andRoute(GET("/customers/{id}"), r -> ServerResponse.ok().body(cr.findById(r.pathVariable("id")), Customer.class))
                .andRoute(GET("/delay"), r -> ServerResponse.ok().body(Flux.just("Hello, World!").delayElements(Duration.ofSeconds(1)), String.class));
    }

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}
}


interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

}

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {

	@Id
	private String id;
	private String name;

	public Customer(String id, String name) {
	    this.id = id;
	    this.name = name;
	}

    public Customer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return id+" "+name;
    }
}