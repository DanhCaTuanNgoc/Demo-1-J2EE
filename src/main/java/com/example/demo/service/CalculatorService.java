package com.example.demo.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CalculatorService {

    /**
     * ✅ Spring AI Function Bean - AI có thể tự động gọi function này để tính toán
     */
    @Bean
    @Description("Perform basic math operations: add, subtract, multiply, divide")
    public Function<Request, Response> calculator() {
        return request -> {
            try {
                double result = switch (request.operation().toLowerCase().trim()) {
                    case "add", "+" -> request.a() + request.b();
                    case "subtract", "-" -> request.a() - request.b();
                    case "multiply", "*", "x" -> request.a() * request.b();
                    case "divide", "/" -> {
                        if (request.b() == 0) {
                            throw new ArithmeticException("Cannot divide by zero");
                        }
                        yield request.a() / request.b();
                    }
                    default -> throw new IllegalArgumentException("Invalid operation: " + request.operation());
                };
                
                System.out.println("🧮 Function called: calculator()");
                System.out.println("   Input: " + request.a() + " " + request.operation() + " " + request.b());
                System.out.println("   Result: " + result);
                
                return new Response(result, "Success");
                
            } catch (Exception e) {
                System.err.println("❌ Calculator error: " + e.getMessage());
                return new Response(0.0, "Error: " + e.getMessage());
            }
        };
    }

    public record Request(
        double a,
        double b,
        String operation
    ) {}
    
    public record Response(
        double result,
        String message
    ) {}
}
