# 🧭 LLM Gateway Execution Flow

This document describes the complete execution flow of the LLM Gateway. Each stage is governed by persona-specific configurations and is modularly handled by dedicated components.

---

## 🔁 End-to-End Flow

```
User Request
    ↓
Load Persona Properties
    ↓
Fetch Chat History (if enabled)
    ↓
Query Rewrite (if enabled)
    └──> Fetch relevant prompt based on user query
          ↓
      Model Routing Component → LLM Call (for query rewrite)
    ↓
RAG (if enabled in persona config)
    ├── Vector Search
    │   ├── Convert context text into segments
    │   ├── Generate embeddings (LLM Call via Model Router)
    │   └── Query vector DB (e.g., Weaviate) using generated vector + persona filter
    ├── Web Search
    │   ├── Trigger web search with user query
    │   ├── Load web search config (HTML selectors etc.) from persona
    │   └── Parse HTML using config to extract relevant content
    └── Combine results if both are enabled
    ↓
Prepare Gateway Request (Main POJO with all runtime context)
    ↓
Final LLM Call via Model Routing Component
    ├── Fetch model-specific properties
    ├── Select prompt (based on rewritten query and persona phase)
    ├── Load tools (if enabled in persona)
    └── Construct LLM request and make API call
    ↓
Tool Calling Phase (if model requests tools)
    └──> Tool Resolver
          ├── Resolve endpoint from tool name
          ├── Use HTTP connection pooling, retries, auth handlers
          └── Call external MCP server
    ↓
Final Response Returned to User
```

---

## 💡 Notes

* **Persona Properties** drive core behaviors: enabling/disabling chat history, query rewrite, RAG, and tool usage.
* **Model Routing Component** is invoked multiple times for: query rewrite LLM call, vector embedding, and final LLM inference.
* **Gateway Request** is the central runtime object encapsulating everything — query details, prompt, tools, results, etc.
* **Tool Calls** are dynamic and handled only if the LLM requests tool execution. These calls use robust HTTP management (retry logic, pooling, authentication).

---

<h3>🔄 Flow Highlights by Stage</h3>

<div style="overflow-x:auto">
  <table>
    <thead>
      <tr>
        <th style="text-align:left;">Stage</th>
        <th style="text-align:left;">Description</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>Query Rewrite</td>
        <td>Transforms user query contextually via relevant prompt and LLM inference.</td>
      </tr>
      <tr>
        <td>RAG</td>
        <td>Augments context using vector DB, web search, or both.</td>
      </tr>
      <tr>
        <td>Prompt Selection</td>
        <td>Chooses the best prompt for the current persona and phase.</td>
      </tr>
      <tr>
        <td>Tool Execution</td>
        <td>Executes tools if invoked by the model using MCP service integration.</td>
      </tr>
      <tr>
        <td>Model Routing</td>
        <td>Centralized abstraction to route to the correct LLM and config.</td>
      </tr>
    </tbody>
  </table>
</div>


---

This document serves as a foundational overview for developers and contributors working on or integrating with the LLM Gateway.