<%@page import="com.api.hub.gateway.model.PersonaProperties"%>
<%@page import="java.util.Iterator"%> 
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Persona Properties</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-50 p-4">
  <div class="max-w-4xl mx-auto">
    <h2 class="text-2xl font-bold text-blue-600 mb-6">Persona Properties</h2>

    <div id="personaList" class="space-y-4"></div>

    <button onclick="addNewPersona()"
            class="mt-6 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
      + Add New Persona
    </button>
  </div>

  <script>
  const samplePersonas = [
	  <%
	  String header = "${persona.persona || 'New Persona'}";
	  	List<PersonaProperties> list = (List<PersonaProperties>) request.getAttribute("list");
	  if(list != null && !list.isEmpty()){
		  Iterator<PersonaProperties> iter = list.iterator();
		  while(iter.hasNext()){
			  PersonaProperties model = iter.next();
			  out.println("{");
			  out.println(" persona: '" + model.getPersona()+"',");
			  out.println(" chatHistoryEnabled: '" + model.isChatHistoryEnabled()+"',");
			  out.println(" queryRewriteEnabled: '" + model.isQueryRewriteEnabled()+"',");
			  out.println(" ragEnabled: '" + model.isRagEnabled()+"',");
			  out.println(" ragSource: '" + model.getRagSource()+"',");
			  out.println(" maxFallBackModels: '" + model.getMaxFallBackModels()+"',");
			  out.println(" toolCallEnabled: '" + model.isToolCallEnabled()+"',");
			  out.println(" toolChoice: '" + model.getToolChoice()+"',");
			  
			  if(iter.hasNext()){
				  out.println("},");
			  }else{
				  out.println("}");
			  }
			  
		  }
	  }else{
		  out.println("{");
		  out.println(" persona: '',");
		  out.println(" chatHistoryEnabled: '',");
		  out.println(" queryRewriteEnabled: '',");
		  out.println(" ragEnabled: '',");
		  out.println(" ragSource: '',");
		  out.println(" maxFallBackModels: 0,");
		  out.println(" toolCallEnabled: '',");
		  out.println(" toolChoice: ''");
		  out.println("}");
	  }
	  %>  
	      
	    ];
    const renderPersona = (persona, index) => {
		const form = document.createElement('form');
	    form.className = 'bg-white rounded-lg shadow-md p-4';
	    form.action = '/web/personaProperties';
	    form.method = 'POST';
	    form.name = "personaProperties";
	    
      const container = document.createElement('div');
      container.className = 'bg-white rounded-lg shadow-md p-4';

      const summary = document.createElement('div');
      summary.className = 'cursor-pointer flex justify-between items-center';
      summary.innerHTML = `
        <div>
          <h3 class="font-semibold text-lg"><%=header%></h3>
        </div>
        <span class="text-blue-500">▼</span>
      `;

      const details = document.createElement('div');
      details.className = 'grid grid-cols-1 md:grid-cols-2 gap-4 mt-4 hidden';

      const fields = [
        'persona',
        'chatHistoryEnabled',
        'queryRewriteEnabled',
        'ragEnabled',
        'toolCallEnabled',
        'ragSource',
        'maxFallBackModels',
        'toolChoice'
      ];

      fields.forEach(field => {
        const label = document.createElement('label');
        label.className = 'block text-sm font-medium';
        label.innerText = field.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());

        let input;

        if ([
          'chatHistoryEnabled',
          'queryRewriteEnabled',
          'ragEnabled',
          'toolCallEnabled'
        ].includes(field)) {
          input = document.createElement('select');
          input.innerHTML = `
            <option value="true">True</option>
            <option value="false">False</option>
          `;
          input.value = persona[field] ? 'true' : 'false';
        } else {
          input = document.createElement('input');
          input.type = field === 'maxFallBackModels' ? 'number' : 'text';
          input.value = persona[field] || 0;
        }

        input.name = field;
        input.className = 'mt-1 w-full p-2 border border-gray-300 rounded';

        const wrapper = document.createElement('div');
        wrapper.appendChild(label);
        wrapper.appendChild(input);
        details.appendChild(wrapper);
      });

      const saveButton = document.createElement('button');
      saveButton.className = 'mt-4 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700';
      saveButton.innerText = 'Save';
      //saveButton.onclick = () => savePersona(index, details);
      details.appendChild(saveButton);

      summary.onclick = () => {
        details.classList.toggle('hidden');
      };

      form.appendChild(summary);
      form.appendChild(details);

      return form;
    };

    const renderList = () => {
      const list = document.getElementById('personaList');
      list.innerHTML = '';
      samplePersonas.forEach((persona, index) => {
        const personaElem = renderPersona(persona, index);
        list.appendChild(personaElem);
      });
    };

    const addNewPersona = () => {
      samplePersonas.push({});
      renderList();
    };

    const savePersona = (index, details) => {
      const inputs = details.querySelectorAll('input, select');
      inputs.forEach(input => {
        const name = input.name;
        samplePersonas[index][name] =
          input.tagName.toLowerCase() === 'select' ? input.value === 'true' : input.value;
      });
      alert('Persona saved!');
      renderList();
    };

    renderList();
  </script>
</body>
</html>