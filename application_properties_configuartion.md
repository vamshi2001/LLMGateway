# ⚙️ LLM Gateway Configuration - `application.properties`

This document explains each section of the `application.properties` file used in the LLM Gateway project. All key-value properties are organized by functionality, and tables are rendered in HTML for better compatibility across Markdown renderers.

---

## ✅ Basic Spring Boot Config

<div style="overflow-x:auto">
<table>
  <tr><th>Property</th><th>Description</th></tr>
  <tr><td>spring.application.name</td><td>Name of the application for Spring context.</td></tr>
  <tr><td>server.port</td><td>Port on which the server runs.</td></tr>
  <tr><td>spring.servlet.multipart.max-file-size</td><td>Max file upload size.</td></tr>
  <tr><td>spring.servlet.multipart.max-request-size</td><td>Max request size for multipart requests.</td></tr>
  <tr><td>spring.autoconfigure.exclude</td><td>Disables auto configuration of JPA and DataSource as custom DB setup is used.</td></tr>
  <tr><td>spring.main.allow-circular-references</td><td>Allows circular dependency injection if needed.</td></tr>
</table>
</div>

---

## 🎨 MVC / JSP View Config

<div style="overflow-x:auto">
<table>
  <tr><th>Property</th><th>Description</th></tr>
  <tr><td>spring.mvc.view.prefix</td><td>Prefix path for resolving JSP files.</td></tr>
  <tr><td>spring.mvc.view.suffix</td><td>Suffix used for JSP views.</td></tr>
</table>
</div>

---

## 💬 General App Settings

<div style="overflow-x:auto">
<table>
  <tr><th>Property</th><th>Description</th></tr>
  <tr><td>nChats</td><td>Number of concurrent chats allowed.</td></tr>
</table>
</div>

---

## 🧠 OpenAI & LangChain4j LLM Configuration

<div style="overflow-x:auto">
<table>
  <tr><th>Property</th><th>Description</th></tr>
  <tr><td>langchain4j.openai.chat-model.api-key</td><td>OpenAI API key.</td></tr>
  <tr><td>langchain4j.openai.chat-model.base-url</td><td>OpenAI service base URL.</td></tr>
  <tr><td>langchain4j.openai.chat-model.timeout</td><td>Request timeout for OpenAI chat model.</td></tr>
  <tr><td>langchain4j.openai.chat-model.log-requests</td><td>Enable request logging.</td></tr>
  <tr><td>langchain4j.openai.chat-model.log-responses</td><td>Enable response logging.</td></tr>
  <tr><td>langchain4j.ollama.embedding-model.base-url</td><td>Ollama service base URL for embedding generation.</td></tr>
  <tr><td>langchain4j.ollama.embedding-model.modelName</td><td>Model used for embedding (e.g., nomic-embed-text).</td></tr>
  <tr><td>ollama.embedding-model.maxSegmentSizeInChars</td><td>Chunk size limit when splitting input for embeddings.</td></tr>
  <tr><td>ollama.embedding-model.maxOverlapSizeInChars</td><td>Overlap allowed between segments.</td></tr>
</table>
</div>

---

## 🗃️ Cache Configuration

<div style="overflow-x:auto">
<table>
  <tr><th>Property</th><th>Description</th></tr>
  <tr><td>cache.enabled</td><td>Enables cache globally.</td></tr>
  <tr><td>cache.default.refresher.enabled</td><td>Allows auto-refresh for cache entries.</td></tr>
  <tr><td>cache.syncOnChange</td><td>Sync cache on external changes.</td></tr>
  <tr><td>cache.llm.*</td><td>Refresh settings for various caches (metrics, metadata, tool call, etc.).</td></tr>
</table>
</div>

---

## 🗄️ Database Configuration

### MySQL (Relational)

<div style="overflow-x:auto">
<table>
  <tr><th>Property</th><th>Description</th></tr>
  <tr><td>sql.db.enable</td><td>Enable SQL DB integration.</td></tr>
  <tr><td>sql.db.hibernate.*</td><td>Hibernate-specific settings for dialect, DDL strategy, logging.</td></tr>
  <tr><td>db.url, db.username, db.password</td><td>MySQL connection credentials.</td></tr>
  <tr><td>db.initial-size, db.max-active</td><td>Connection pool settings.</td></tr>
</table>
</div>

### MongoDB (NoSQL)

<div style="overflow-x:auto">
<table>
  <tr><th>Property</th><th>Description</th></tr>
  <tr><td>mongoDB.url</td><td>MongoDB cluster connection string.</td></tr>
  <tr><td>mongoDB.DBName</td><td>MongoDB database name.</td></tr>
  <tr><td>mongoDB.maxConnections</td><td>Max concurrent Mongo connections.</td></tr>
  <tr><td>mongoDB.connectionTimeOut, readTimeOut</td><td>Timeouts for connection and read operations.</td></tr>
</table>
</div>

---

## 🧭 Weaviate Vector DB Config

<div style="overflow-x:auto">
<table>
  <tr><th>Property</th><th>Description</th></tr>
  <tr><td>weaviate.api-key</td><td>API Key for Weaviate.</td></tr>
  <tr><td>weaviate.host, port, scheme</td><td>Connection details for Weaviate service.</td></tr>
  <tr><td>weaviate.object-class</td><td>Class in Weaviate to perform operations on.</td></tr>
  <tr><td>weaviate.text-field</td><td>Text property for embedding search.</td></tr>
  <tr><td>weaviate.metadata-field</td><td>Custom metadata property used for filtering.</td></tr>
  <tr><td>weaviate.minScore, maxResults</td><td>Filters for minimum similarity and top-k results.</td></tr>
</table>
</div>

---

## 🌐 Google Custom Search Engine (CSE)

<div style="overflow-x:auto">
<table>
  <tr><th>Property</th><th>Description</th></tr>
  <tr><td>google.search.api-key</td><td>Google Search API Key.</td></tr>
  <tr><td>google.search.csi</td><td>Custom Search Engine ID.</td></tr>
  <tr><td>google.search.timeout</td><td>Search API timeout.</td></tr>
  <tr><td>google.search.request.language</td><td>Preferred language of search results.</td></tr>
  <tr><td>google.search.request.safe-search</td><td>Enables SafeSearch filter on results.</td></tr>
</table>
</div>

---

## 🛠️ Tool Call Service Configuration

<div style="overflow-x:auto">
<table>
  <tr><th>Property</th><th>Description</th></tr>
  <tr><td>toolcall.service.enable</td><td>Enable or disable external tool invocation.</td></tr>
  <tr><td>toolcall.service.httpHandler</td><td>HTTP handler class for tool calls (e.g., Spring RestTemplate).</td></tr>
  <tr><td>toolcall.service.hostReslover</td><td>Resolves host for tool call (can be round robin, etc.).</td></tr>
  <tr><td>toolcall.service.autheticationHandler</td><td>Authentication handler for secured APIs.</td></tr>
  <tr><td>http.rt.toolcall.*</td><td>Timeouts, max connections, retry configurations for tool API requests.</td></tr>
</table>
</div>

---

## 🔀 Feature Toggles (`#switch`)

The **`#switch` section** allows granular control of integrations with third-party components. By toggling these flags, developers can disable default behavior and inject custom implementations.

<div style="overflow-x:auto">
<table>
  <tr><th>Component</th><th>Switch</th><th>Description</th></tr>
  <tr><td>Chat History (Mongo)</td><td>mongoDB.chathistory.enable</td><td>Enables loading/saving user chat history from MongoDB.</td></tr>
  <tr><td>Model Metadata (SQL)</td><td>sql.model.metadata.enable</td><td>Reads model config metadata from relational DB.</td></tr>
  <tr><td>Model Props (Mongo)</td><td>mongoDB.model.props.enable</td><td>Loads model-specific configurations (tokens, strategies).</td></tr>
  <tr><td>Prompt Versions</td><td>mongoDB.prompt.enabled</td><td>Enable prompt versioning from MongoDB collection.</td></tr>
  <tr><td>Tool Metadata</td><td>mongoDB.toolCall.metadata.enable</td><td>Enables fetching tool schema/config from Mongo.</td></tr>
  <tr><td>Web Search Config</td><td>mongoDB.websearch.props.enable</td><td>Custom parsing logic configuration from MongoDB.</td></tr>
  <tr><td>Persona Properties</td><td>sql.persona.props.enable</td><td>Load persona-level feature flags from MySQL.</td></tr>
  <tr><td>Google Search</td><td>google.searchengine.enabled</td><td>Enables integration with Google CSE.</td></tr>
  <tr><td>Weaviate Vector Search</td><td>weaviateDB.sysinfo.enable</td><td>Enables semantic search using Weaviate.</td></tr>
</table>
</div>

> **🧩 Use Case:** If you want to skip default DB integrations (e.g., Mongo or SQL), just disable the corresponding switch and plug in your own custom loader/service.

---

Let me know if you'd like this exported into a file (`README.md` or `docs/config.md`) or want each section broken into its own page for a larger documentation portal.
