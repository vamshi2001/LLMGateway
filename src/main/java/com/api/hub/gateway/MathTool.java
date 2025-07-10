package com.api.hub.gateway;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

public class MathTool {

	@Tool(name = "addition", value = "adds 2 numbers")
    public int add(@P(required = true,value = "input Value to sum 2 numbers") int a, int b) {
        return a + b;
    }

    @Tool
    public int multiply(int a, int b) {
        return a * b;
    }
}
