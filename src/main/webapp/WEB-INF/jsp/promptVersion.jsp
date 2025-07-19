<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.api.hub.gateway.model.PromptVersion"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Prompt Versions</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50 text-gray-800 p-4">
  <div class="max-w-4xl mx-auto">
    <h2 class="text-2xl font-bold text-indigo-600 mb-6">Prompt Version Configurations</h2>

    <div id="promptContainer" class="space-y-4"></div>

    <button onclick="addPromptVersion()" class="mt-4 bg-indigo-600 text-white px-4 py-2 rounded hover:bg-indigo-700">+ Add Prompt Version</button>
  </div>
<%
	String ph1 = "${idx}";
	String header = "<div><strong>${data.persona || 'New Prompt'} - ${data.phase || ''}</strong></div>";
	String ph2 = "${data.persona || ''}";
	String ph3 = "${data.phase || ''}";
	String ph4 = "${data.prompt || ''}";
%>
  <script>
    let promptIndex = 0;

    function addPromptVersion(data = {}) {
    	const container = document.getElementById('promptContainer');
      const idx = promptIndex++;
      const wrapper = document.createElement('form');
      
      wrapper.className = 'border rounded shadow bg-white';
      wrapper.method = 'POST';
      wrapper.action = '/web/promptVersion';
      wrapper.name = 'promptVersion';

      wrapper.innerHTML = `
        <div class="p-4 cursor-pointer bg-indigo-100 flex justify-between items-center" onclick="togglePrompt('prompt-<%=ph1%>')">
          <%=header%>
          <span class="text-indigo-600">▼</span>
        </div>
        <div id="prompt-<%=ph1%>" class="p-4 hidden space-y-4">
          <input type="text" name="persona" placeholder="Persona" value="<%=ph2%>" class="w-full border p-2 rounded" required />
          <input type="text" name="phase" placeholder="Phase" value="<%=ph3%>" class="w-full border p-2 rounded" required />
          <textarea name="prompt" placeholder="Prompt" class="w-full border p-2 rounded h-32" required><%=ph4%></textarea>
          <button type="submit" class="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">Save</button>
        </div>
      `;

      container.appendChild(wrapper);
    }

    function togglePrompt(id) {
      const el = document.getElementById(id);
      if (el) el.classList.toggle('hidden');
    }

    // === sampleData from server-side ===
    const sampleData = [
      <% 
        List<PromptVersion> promptVersions = (List<PromptVersion>) request.getAttribute("list");
        if (promptVersions != null) {
        	Iterator<PromptVersion>  iter = promptVersions.iterator();
        	while(iter.hasNext()){
        		PromptVersion pv = iter.next();
        	
            String persona = pv.getPersona() != null ? pv.getPersona().replace("\"", "\\\"") : "";
            String phase = pv.getPhase() != null ? pv.getPhase().replace("\"", "\\\"") : "";
            String prompt = pv.getPrompt() != null ? pv.getPrompt().replace("\"", "\\\"").replace("\n", "\\n") : "";
            %>
		    
        { persona: "<%= persona %>", phase: "<%= phase %>", prompt: "<%= prompt %>" }
		     <%if(iter.hasNext()){%>
		    	  ,
		  	 <%}
    	 	}
		}else{%>
        	{ persona: '', phase: '', prompt: '' }
      <%}%>
      
    ];

    // Render all from sampleData on load
    window.onload = function () {
      sampleData.forEach(data => addPromptVersion(data));
    };
  </script>
</body>
</html>