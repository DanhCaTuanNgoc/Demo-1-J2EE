package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * Configuration for Spring AI Function Calling
 * Định nghĩa các functions mà AI có thể gọi
 */
@Configuration
public class FunctionConfig {

    /**
     * Function để lấy thời gian hiện tại
     * AI sẽ tự động quyết định khi nào cần gọi function này
     */
    @Bean
    @Description("Get the current date and time in Vietnamese format")
    public Function<TimeRequest, TimeResponse> getCurrentTime() {
        return request -> {
            LocalDateTime now = LocalDateTime.now();
            String formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            System.out.println("⏰ Function Call: getCurrentTime() -> " + formattedTime);
            
            return new TimeResponse(formattedTime, "Asia/Ho_Chi_Minh");
        };
    }

    /**
     * Function để tính toán phép toán cơ bản
     * AI sẽ tự động parse và gọi function này khi user hỏi về tính toán
     */
    @Bean
    @Description("Calculate basic arithmetic operations: addition, subtraction, multiplication, division")
    public Function<CalculatorRequest, CalculatorResponse> calculator() {
        return request -> {
            double result;
            String operation = request.operation().toLowerCase();
            
            System.out.println("🔢 Function Call: calculator(" + request.num1() + " " + operation + " " + request.num2() + ")");
            
            switch (operation) {
                case "add":
                case "cong":
                case "+":
                    result = request.num1() + request.num2();
                    break;
                case "subtract":
                case "tru":
                case "-":
                    result = request.num1() - request.num2();
                    break;
                case "multiply":
                case "nhan":
                case "*":
                    result = request.num1() * request.num2();
                    break;
                case "divide":
                case "chia":
                case "/":
                    if (request.num2() == 0) {
                        return new CalculatorResponse(0, "Error: Division by zero");
                    }
                    result = request.num1() / request.num2();
                    break;
                default:
                    return new CalculatorResponse(0, "Error: Unknown operation");
            }
            
            System.out.println("✅ Calculator result: " + result);
            return new CalculatorResponse(result, "Success");
        };
    }

    /**
     * Request model cho time function
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Request to get current time")
    public record TimeRequest(
        @JsonProperty(required = false)
        @JsonPropertyDescription("Optional timezone (default: Asia/Ho_Chi_Minh)")
        String timezone
    ) {}

    /**
     * Response model cho time function
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TimeResponse(
        @JsonProperty(required = true)
        @JsonPropertyDescription("Current time in format yyyy-MM-dd HH:mm:ss")
        String currentTime,
        
        @JsonProperty(required = true)
        @JsonPropertyDescription("Timezone used")
        String timezone
    ) {}

    /**
     * Request model cho calculator function
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Request to perform a calculation")
    public record CalculatorRequest(
        @JsonProperty(required = true)
        @JsonPropertyDescription("First number")
        double num1,
        
        @JsonProperty(required = true)
        @JsonPropertyDescription("Second number")
        double num2,
        
        @JsonProperty(required = true)
        @JsonPropertyDescription("Operation: add/cong/+, subtract/tru/-, multiply/nhan/*, divide/chia//")
        String operation
    ) {}

    /**
     * Response model cho calculator function
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CalculatorResponse(
        @JsonProperty(required = true)
        @JsonPropertyDescription("Result of the calculation")
        double result,
        
        @JsonProperty(required = true)
        @JsonPropertyDescription("Status message")
        String message
    ) {}
}
