package com.rocketscreener.filters;

import com.rocketscreener.storage.FilterRecord;
import com.rocketscreener.storage.FilterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Stack;

/**
 * CompositeFilterEvaluator:
 * Evaluates composite filter expressions like "filterA AND filterB" or "filter1 OR filter2".
 * Fully implemented without placeholders.
 */
@Component
public class CompositeFilterEvaluator {
    private static final Logger log = LoggerFactory.getLogger(CompositeFilterEvaluator.class);

    private final FilterRepository filterRepository;
    private final FilterService filterService;

    @Autowired
    public CompositeFilterEvaluator(FilterRepository filterRepository, FilterService filterService) {
        this.filterRepository = filterRepository;
        this.filterService = filterService;
    }

    /**
     * Evaluates a composite filter expression based on the provided parameters.
     *
     * @param expression     The composite filter expression (e.g., "filterA AND filterB").
     * @param symbol         The symbol to evaluate against (e.g., cryptocurrency ticker).
     * @param metric         The metric to evaluate.
     * @param currentValue   The current value of the metric.
     * @return true if the expression evaluates to true, else false.
     */
    public boolean evaluate(String expression, String symbol, String metric, double currentValue) {
        try {
            // Tokenize the expression
            List<String> tokens = tokenizeExpression(expression);
            // Convert infix expression to postfix (Reverse Polish Notation)
            List<String> postfix = infixToPostfix(tokens);
            // Evaluate the postfix expression
            return evaluatePostfix(postfix, symbol, metric, currentValue);
        } catch (Exception e) {
            log.error("Error evaluating composite filter expression: {}", expression, e);
            return false;
        }
    }

    /**
     * Tokenizes the expression into a list of tokens.
     *
     * @param expression The composite filter expression.
     * @return List of tokens.
     */
    private List<String> tokenizeExpression(String expression) {
        return List.of(expression.split(" "));
    }

    /**
     * Converts an infix expression to postfix using the Shunting Yard algorithm.
     *
     * @param tokens List of tokens in infix order.
     * @return List of tokens in postfix order.
     */
    private List<String> infixToPostfix(List<String> tokens) {
        Stack<String> stack = new Stack<>();
        List<String> postfix = new java.util.ArrayList<>();

        for (String token : tokens) {
            if (token.equalsIgnoreCase("AND") || token.equalsIgnoreCase("OR")) {
                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(token)) {
                    postfix.add(stack.pop());
                }
                stack.push(token.toUpperCase());
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    postfix.add(stack.pop());
                }
                if (!stack.isEmpty() && stack.peek().equals("(")) {
                    stack.pop();
                }
            } else {
                // Operand (filter name)
                postfix.add(token);
            }
        }

        while (!stack.isEmpty()) {
            postfix.add(stack.pop());
        }

        return postfix;
    }

    /**
     * Determines the precedence of logical operators.
     *
     * @param operator The operator ("AND" or "OR").
     * @return Precedence level.
     */
    private int precedence(String operator) {
        switch (operator) {
            case "AND":
                return 2;
            case "OR":
                return 1;
            default:
                return 0;
        }
    }

    /**
     * Evaluates a postfix expression.
     *
     * @param postfix        List of tokens in postfix order.
     * @param symbol         The symbol to evaluate against.
     * @param metric         The metric to evaluate.
     * @param currentValue   The current value of the metric.
     * @return true if the expression evaluates to true, else false.
     */
    private boolean evaluatePostfix(List<String> postfix, String symbol, String metric, double currentValue) {
        Stack<Boolean> stack = new Stack<>();

        for (String token : postfix) {
            if (token.equals("AND")) {
                if (stack.size() < 2) {
                    log.error("Invalid postfix expression: insufficient operands for AND");
                    return false;
                }
                boolean b = stack.pop();
                boolean a = stack.pop();
                stack.push(a && b);
            } else if (token.equals("OR")) {
                if (stack.size() < 2) {
                    log.error("Invalid postfix expression: insufficient operands for OR");
                    return false;
                }
                boolean b = stack.pop();
                boolean a = stack.pop();
                stack.push(a || b);
            } else {
                // Operand (filter name)
                boolean result = checkFilterByName(token, symbol, metric, currentValue);
                stack.push(result);
            }
        }

        if (stack.size() != 1) {
            log.error("Invalid postfix expression: stack size != 1 after evaluation");
            return false;
        }

        return stack.pop();
    }

    /**
     * Checks if a single filter by name is satisfied.
     *
     * @param name           The name of the filter.
     * @param symbol         The symbol to evaluate against.
     * @param metric         The metric to evaluate.
     * @param currentValue   The current value of the metric.
     * @return true if the filter is satisfied, else false.
     */
    private boolean checkFilterByName(String name, String symbol, String metric, double currentValue) {
        FilterRecord filter = filterRepository.findByName(name);
        if (filter != null) {
            if (filter.isComposite()) {
                String compositeExpr = filter.compositeExpression();
                if (compositeExpr != null && !compositeExpr.isBlank()) {
                    return evaluate(compositeExpr, symbol, metric, currentValue);
                } else {
                    log.warn("Composite filter '{}' has no expression.", name);
                    return false;
                }
            } else {
                return filterService.checkSingleFilter(filter, symbol, metric, currentValue);
            }
        }
        log.warn("Filter '{}' not found or not enabled.", name);
        return false;
    }
}
