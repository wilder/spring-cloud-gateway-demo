package com.wilderpereira.edgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class EdgeServiceApplication {


	@Bean
	DiscoveryClientRouteDefinitionLocator discoveryRoutes(DiscoveryClient dc) {
		return new DiscoveryClientRouteDefinitionLocator(dc);
	}


	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				//basic proxy
				.route(r -> r.path("/start")
						.uri("http://start.spring.io:80/")
				)

				//load-balanced proxy
				.route("lb_cutomer_service", r -> r.path("/lb")
                        //used for hitting customer-service/customers instead of customer-service/fb
                        .filters(f -> f.rewritePath("/lb",
                                "/customers")
                        )
						.uri("lb://customer-service") //Eureka aware uri
                )

                //load-balanced custom filter.
                .route("lb_customer_service_paths", c -> c.path("/cust/**")
                        .filters(f -> f.rewritePath("/cust/(?<CID>.*)",
                                "/customers/${CID}")
                        )
                        .uri("lb://customer-service")
                )

                //circuit breaker
                .route("cb", cb -> cb.path("/cb")
                        .filters(f -> {
                            f.hystrix("/cb");
                            f.rewritePath("/cb", "/delay");
                            return f;
                        })
                        .uri("lb://customer-service")

                )

                //websocket
                .route("websocket_route", r -> r.path("/echo")
                        .uri("ws://localhost:9000"))


                .build();
	}

	public static void main(String[] args) {
		SpringApplication.run(EdgeServiceApplication.class, args);
	}
}
