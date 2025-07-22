<%@page import="com.api.hub.gateway.model.JsoupExtractionConfig"%>
<%@page import="com.api.hub.gateway.model.JsoupExtractionConfig.ExtractionRule"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Websearch Config</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-50 text-gray-800 p-4">
  <div class="max-w-4xl mx-auto">
    <h2 class="text-2xl font-bold text-blue-600 mb-6">Websearch Config</h2>

    <div id="configContainer" class="space-y-4"></div>

    <button onclick="addNewConfig()" class="mt-4 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">+ Add Websearch Config</button>
  </div>

  <!-- Hidden form for submission -->
  <form id="websearchForm" name="websearch" action="/web/websearch" method="POST" class="hidden">
    <input type="hidden" name="host" id="formHost" />
    <input type="hidden" name="ruleTypes" id="formRuleTypes" />
    <input type="hidden" name="ruleValues" id="formRuleValues" />
    <input type="hidden" name="ruleAll" id="formRuleAll" />
  </form>

  <script>
    const sampleData = [
      <% 
      String jsCode = "${(data.rules || []).map((rule, ruleIdx) => `\r\n"
  			+ "              <div class=\"flex flex-wrap gap-2 items-center\">\r\n"
  			+ "      <input type=\"text\" placeholder=\"Type\" value=\"${rule.type || ''}\" class=\"rule-type border p-2 rounded w-full md:w-1/4\" />\r\n"
  			+ "      <input type=\"text\" placeholder=\"Value\" value=\"${rule.value || ''}\" class=\"rule-value border p-2 rounded w-full md:w-1/3\" />\r\n"
  			+ "      <label class=\"flex items-center gap-1\">\r\n"
  			+ "        <input type=\"checkbox\" class=\"rule-all\" ${rule.all ? 'checked' : ''} /> All\r\n"
  			+ "      </label>\r\n"
  			+ "    </div>\r\n"
  			+ "  `).join('')}";
      List<JsoupExtractionConfig> configs = (List<JsoupExtractionConfig>) request.getAttribute("list");
      if (configs != null && !configs.isEmpty()) {
        for (int i = 0; i < configs.size(); i++) {
          JsoupExtractionConfig jconfig = configs.get(i);
          out.print("{");
          out.print("host: '" + jconfig.getHost() + "',");
          out.print("rules: [");
          List<ExtractionRule> rules = jconfig.getRules();
          if (rules != null && !rules.isEmpty()) {
            for (int j = 0; j < rules.size(); j++) {
              ExtractionRule rule = rules.get(j);
              out.print("{");
              out.print("type: '" + rule.getType() + "', ");
              out.print("value: '" + rule.getValue() + "', ");
              out.print("all: " + rule.isAll());
              out.print("}");
              if (j < rules.size() - 1) out.print(", ");
            }
          }
          out.print("]");
          out.print("}");
          if (i < configs.size() - 1) out.print(", ");
        }
      }
      %>
    ];

    let configIndex = 0;

    function addNewConfig(data = {}) {
      const container = document.getElementById('configContainer');
      const idx = configIndex++;

      const wrapper = document.createElement('div');
      wrapper.className = 'border rounded shadow bg-white';
	<%
		String idx = "${idx}";
		String dataHost = "${data.host || ''}";
		String header = "${data.host || 'New Host Config'}";
	%>
      wrapper.innerHTML = `
        <div class="p-4 cursor-pointer bg-blue-100 flex justify-between items-center" onclick="toggleConfig('config-<%=idx%>')">
          <div><strong><%=header%></strong></div>
          <span class="text-blue-600">▼</span>
        </div>
        <div id="config-<%=idx%>" class="p-4 hidden space-y-4">
          <input type="text" placeholder="Host" value="<%=dataHost%>" class="host w-full border p-2 rounded" />

          <div>
            <label class="block font-semibold mb-2">Extraction Rules:</label>
            <div id="rules-<%=idx%>" class="space-y-2">
              <%=jsCode%>
            </div>
            <button onclick="addRule(<%=idx%>)" class="text-sm text-blue-600 mt-2">+ Add Rule</button>
          </div>

          <button onclick="saveConfig(this, <%=idx%>)" class="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">Save</button>
        </div>
      `;

      container.appendChild(wrapper);
    }

    function toggleConfig(id) {
      const el = document.getElementById(id);
      if (el) el.classList.toggle('hidden');
    }

    function addRule(idx) {
      const rulesDiv = document.getElementById(`rules-<%=idx%>`);
      const div = document.createElement('div');
      div.className = 'flex flex-wrap gap-2 items-center mt-2';
      div.innerHTML = `
        <input type="text" placeholder="Type" class="rule-type border p-2 rounded w-full md:w-1/4" />
        <input type="text" placeholder="Value" class="rule-value border p-2 rounded w-full md:w-1/3" />
        <label class="flex items-center gap-1">
          <input type="checkbox" class="rule-all" /> All
        </label>
      `;
      rulesDiv.appendChild(div);
    }

    function saveConfig(btn, idx) {
      const container = btn.closest(`#config-<%=idx%>`);
      const host = container.querySelector('.host').value;

      const ruleTypes = [];
      const ruleValues = [];
      const ruleAll = [];

      container.querySelectorAll('.rule-type').forEach(el => ruleTypes.push(el.value));
      container.querySelectorAll('.rule-value').forEach(el => ruleValues.push(el.value));
      container.querySelectorAll('.rule-all').forEach(el => ruleAll.push(el.checked));

      // Fill the hidden form
      document.getElementById('formHost').value = host;
      document.getElementById('formRuleTypes').value = ruleTypes.join(',');
      document.getElementById('formRuleValues').value = ruleValues.join(',');
      document.getElementById('formRuleAll').value = ruleAll.join(',');

      // Submit form
      document.getElementById('websearchForm').submit();
    }

    // Render sampleData from JSP
    sampleData.forEach(cfg => addNewConfig(cfg));
  </script>
</body>
</html>