🔧 LLM Gateway UI Configuration
The <code>LLMConfig.html</code> file provides a central UI to configure model behavior, persona pipelines, tool calls, and prompt logic. Each section interacts with either SQL or MongoDB to drive your LLM Gateway logic.

📌 1. Model Metadata <span style="background-color:#DBEAFE; color:#1E40AF; font-size:12px; font-weight:600; padding:2px 6px; border-radius:6px;">SQL DB</span>
Defines core properties and limits for each registered model.

<ul> <li><code>modelId</code>: Unique identifier</li> <li><code>provider</code>: e.g. OpenAI, Ollama</li> <li><code>modelName</code>, <code>type</code>, <code>rank</code>: Technical specs</li> <li><code>maxTokenDay</code> / <code>maxTokenMonth</code>, <code>maxRequestDay</code> / <code>maxRequestMonth</code></li> <li><code>enable</code>: Model status</li> <li><code>topicsStr</code>: Comma-separated personas this model supports</li> </ul>
📊 2. Model Metrics <span style="background-color:#DBEAFE; color:#1E40AF; font-size:12px; font-weight:600; padding:2px 6px; border-radius:6px;">SQL DB</span>
Real-time usage and limits tracking for each model.

<ul> <li><code>currentInputTokenConsumedPerDay</code>, <code>currentOutputTokenConsumedPerDay</code></li> <li><code>currentInputTokenConsumedPerMonth</code>, <code>currentOutputTokenConsumedPerMonth</code></li> <li><code>currentActiveRequest</code>, <code>totalFailuresToday</code></li> <li>Enables fallback, usage enforcement</li> </ul>
👤 3. Persona Properties <span style="background-color:#DBEAFE; color:#1E40AF; font-size:12px; font-weight:600; padding:2px 6px; border-radius:6px;">SQL DB</span>
Controls per-persona pipeline options like RAG, tools, history, etc.

<ul> <li><code>chatHistoryEnabled</code>, <code>queryRewriteEnabled</code>, <code>ragEnabled</code></li> <li><code>toolCallEnabled</code>, <code>ragSource</code>, <code>toolChoice</code></li> <li>Enables selective pipeline flow logic per persona</li> </ul>
⚙️ 4. Model Properties <span style="background-color:#DCFCE7; color:#166534; font-size:12px; font-weight:600; padding:2px 6px; border-radius:6px;">MongoDB</span>
Base64 encoded config to inject headers, auth, params during model API call.

<ul> <li><code>modelId</code>, <code>props</code> (Base64 config)</li> </ul>
🌐 5. Web Search Rules <span style="background-color:#DCFCE7; color:#166534; font-size:12px; font-weight:600; padding:2px 6px; border-radius:6px;">MongoDB</span>
HTML scraping configuration per domain using JSoup rules.

<ul> <li><code>host</code>: Target domain</li> <li><code>rules</code>: List of extraction rules (id, class, tag, selector)</li> <li><code>all</code>: Whether to extract all matches or just the first</li> </ul>
🔧 6. Tool Call Metadata <span style="background-color:#DCFCE7; color:#166534; font-size:12px; font-weight:600; padding:2px 6px; border-radius:6px;">MongoDB</span>
Defines MCP tools, argument schemas and persona restrictions.

<ul> <li><code>toolName</code>, <code>toolDescription</code>, <code>endPoint</code></li> <li><code>toolArguments</code>, <code>supportedPersona</code>, <code>toolSpecification</code></li> </ul>
🧾 7. Prompt Versions <span style="background-color:#DCFCE7; color:#166534; font-size:12px; font-weight:600; padding:2px 6px; border-radius:6px;">MongoDB</span>
Different prompts mapped to persona and pipeline phase.

<ul> <li><code>persona</code>, <code>phase</code>, <code>prompt</code></li> <li>Used in query rewriting, RAG prep, and LLM calls</li> </ul>
🪛 #switch: Plug & Play Config
The <code>#switch</code> mechanism allows toggling off default implementations for DB, websearch, etc., and injecting your own handlers. Useful for extending or disabling features without breaking the gateway.

<ul> <li>All classes that interact with external systems are behind a switch</li> <li>Examples: disable SQL and inject Redis, bypass websearch, etc.</li> <li>Handled using properties or <code>@Conditional</code> style flags in Spring Boot</li> </ul>