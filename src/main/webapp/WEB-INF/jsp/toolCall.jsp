<%@page import="com.api.hub.gateway.model.TollCallData"%>
<%@page import="java.util.Iterator"%> 
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Tool Call Configuration</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-50 text-slate-900 p-4">
  <div class="max-w-3xl mx-auto space-y-4">
    <h1 class="text-2xl font-bold text-blue-600 mb-4">Tool Call Configuration</h1>
    <div id="toolList" class="space-y-4"></div>
    <button onclick="addTool()" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition">+ Add Tool</button>
  </div>

  <!-- Hidden Form for Submitting Tool -->
  <form id="toolSubmitForm" action="/web/toolCall" method="POST" class="hidden" name="toolCall">
    <input type="hidden" name="toolName" id="toolForm_toolName" />
    <input type="hidden" name="toolDescription" id="toolForm_toolDescription" />
    <input type="hidden" name="toolArguments" id="toolForm_toolArguments" />
    <input type="hidden" name="enabled" id="toolForm_enabled" />
    <input type="hidden" name="endPoint" id="toolForm_endPoint" />
    <input type="hidden" name="supportedPersona" id="toolForm_supportedPersona" />
  </form>
  <iframe name="submitFrame" style="display:none;"></iframe>

  <script>
  const tools = [
	  <%
	  String header = "<strong>${tool.toolName || 'New Tool'}</strong> — ${tool.toolDescription || ''}";
	  String jsIndexVar = "${index}";
	    List<TollCallData> tools = (List<TollCallData>) request.getAttribute("list");
	    if (tools != null && !tools.isEmpty()) {
	        for (int i = 0; i < tools.size(); i++) {
	            TollCallData tool = tools.get(i);
	            out.println("{");
	            out.println("  toolName: '" + tool.getToolName() + "',");
	            out.println("  toolDescription: '" + tool.getToolDescription() + "',");
	            out.println("  toolArguments: '" + tool.getToolArguments() + "',");
	            out.println("  enabled: " + tool.isEnabled() + ",");
	            out.println("  endPoint: '" + tool.getEndPoint() + "',");
	            out.print("  supportedPersona: [");

	            List<String> personas = tool.getSupportedPersona();
	            if (personas != null && !personas.isEmpty()) {
	                for (int j = 0; j < personas.size(); j++) {
	                    out.print("'" + personas.get(j) + "'");
	                    if (j < personas.size() - 1) {
	                        out.print(", ");
	                    }
	                }
	            }

	            out.println("]");

	            if (i < tools.size() - 1) {
	                out.println("},");
	            } else {
	                out.println("}");
	            }
	        }
	    } else {
	        out.println("{");
	        out.println("  toolName: '',");
	        out.println("  toolDescription: '',");
	        out.println("  toolArguments: '',");
	        out.println("  enabled: false,");
	        out.println("  endPoint: '',");
	        out.println("  supportedPersona: []");
	        out.println("}");
	    }
	  %>
	  ];

    function decodeBase64Props(encoded) {
      try {
        return atob(encoded);
      } catch (e) {
        return '';
      }
    }

    function encodeBase64Props(decoded) {
      return btoa(decoded);
    }

    function renderTools() {
      const container = document.getElementById('toolList');
      container.innerHTML = '';

      tools.forEach((tool, index) => {
        const decodedArgs = decodeBase64Props(tool.toolArguments);
        const supportedPersonaStr = tool.supportedPersona?.join(', ') || '';

        const div = document.createElement('div');
        div.className = 'border border-slate-300 rounded-md shadow';
		<%
		String ph1 = "${tool.toolName || ''}";
		String ph2 = "${tool.toolDescription || ''}";
		String ph3 = "${decodedArgs}";
		String ph4 = "${tool.enabled ? 'selected' : ''}";
		String ph5 = "${tool.endPoint || ''}";
		String ph6 = "${supportedPersonaStr}";
		%>
        div.innerHTML = `
          <button onclick="toggleExpand(<%=jsIndexVar%>)" class="w-full text-left px-4 py-2 bg-slate-100 hover:bg-slate-200 rounded-t-md">
            <%=header%>
          </button>
          <div class="p-4 hidden">
            <label class="block mb-2 font-medium">Tool Name</label>
            <input type="text" class="w-full mb-3 p-2 border rounded" value="<%=ph1%>" onchange="tools[<%=jsIndexVar%>].toolName = this.value" />

            <label class="block mb-2 font-medium">Tool Description</label>
            <input type="text" class="w-full mb-3 p-2 border rounded" value="<%=ph2%>" onchange="tools[<%=jsIndexVar%>].toolDescription = this.value" />

            <label class="block mb-2 font-medium">Tool Arguments (Decoded)</label>
            <textarea rows="5" class="w-full mb-3 p-2 border rounded" onchange="tools[<%=jsIndexVar%>].toolArguments = encodeBase64Props(this.value)"><%=ph3%></textarea>

            <label class="block mb-2 font-medium">Enabled</label>
            <select class="w-full mb-3 p-2 border rounded" onchange="tools[<%=jsIndexVar%>].enabled = this.value === 'true'">
              <option value="true" <%=ph4%>>True</option>
              <option value="false" <%=ph4%>>False</option>
            </select>

            <label class="block mb-2 font-medium">Endpoint</label>
            <input type="text" class="w-full mb-3 p-2 border rounded" value="<%=ph5%>" onchange="tools[<%=jsIndexVar%>].endPoint = this.value" />

            <label class="block mb-2 font-medium">Supported Personas (comma separated)</label>
            <input type="text" class="w-full mb-3 p-2 border rounded" value="<%=ph6%>" onchange="tools[<%=jsIndexVar%>].supportedPersona = this.value.split(',').map(p => p.trim())" />

            <button onclick="saveTool(<%=jsIndexVar%>)" class="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">Save</button>
          </div>
        `;

        container.appendChild(div);
      });
    }

    function toggleExpand(index) {
      const container = document.getElementById('toolList').children[index];
      const content = container.querySelector('div.p-4');
      content.classList.toggle('hidden');
    }

    function addTool() {
      tools.push({
        toolName: '',
        toolDescription: '',
        toolArguments: '',
        enabled: true,
        endPoint: '',
        supportedPersona: []
      });
      renderTools();
    }

    function saveTool(index) {
      const tool = tools[index];
      document.getElementById("toolForm_toolName").value = tool.toolName;
      document.getElementById("toolForm_toolDescription").value = tool.toolDescription;
      document.getElementById("toolForm_toolArguments").value = tool.toolArguments;
      document.getElementById("toolForm_enabled").value = tool.enabled;
      document.getElementById("toolForm_endPoint").value = tool.endPoint;
      document.getElementById("toolForm_supportedPersona").value = tool.supportedPersona;

      document.getElementById("toolSubmitForm").submit();
    }

    renderTools();
  </script>
</body>
</html>