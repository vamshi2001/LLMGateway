<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.List,com.api.hub.gateway.model.Model"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Model Metadata</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-50 p-4">
  <div class="max-w-4xl mx-auto">
    <h2 class="text-2xl font-bold text-blue-600 mb-6">Model Metadata</h2>

    <div id="modelList" class="space-y-4"></div>

    <button onclick="addNewModel()"
            class="mt-6 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
      + Add New Model
    </button>

  </div>

  <script>
  const sampleData = [
  <%
  String header = "${model.modelId || 'New Model'} - ${model.provider || 'Provider'} - ${model.modelName || 'Model'} (${model.type || 'Type'})";
  	List<Model> list = (List<Model>) request.getAttribute("list");
  if(list != null && !list.isEmpty()){
	  Iterator<Model> iter = list.iterator();
	  while(iter.hasNext()){
		  Model model = iter.next();
		  out.println("{");
		  out.println(" modelId: '" + model.getModelId()+"',");
		  out.println(" provider: '" + model.getProvider()+"',");
		  out.println(" modelName: '" + model.getModelName()+"',");
		  out.println(" rank: '" + model.getRank()+"',");
		  out.println(" type: '" + model.getType()+"',");
		  out.println(" maxTokenDay: '" + model.getMaxTokenDay()+"',");
		  out.println(" maxTokenMonth: '" + model.getMaxTokenMonth()+"',");
		  out.println(" maxRequestDay: '" + model.getMaxRequestDay()+"',");
		  out.println(" maxRequestMonth: '" + model.getMaxRequestMonth()+"',");
		  out.println(" enable: '" + model.isEnable()+"',");
		  out.println(" topics: [");
		  int count = model.getTopics().size();
		  for ( String persona : model.getTopics()){
			  count--;
			  if(count <= 0){
				  out.print("'" + persona + "'");
			  }else{
				  out.print("'" + persona + "'" + ",");
			  }
		  }
		  out.println(" ]");
		  
		  if(iter.hasNext()){
			  out.println("},");
		  }else{
			  out.println("}");
		  }
		  
	  }
  }else{
	  out.println("{");
	  out.println(" modelId: '',");
	  out.println(" provider: '',");
	  out.println(" modelName: '',");
	  out.println(" rank: '',");
	  out.println(" maxTokenDay: '',");
	  out.println(" maxTokenMonth: '',");
	  out.println(" maxRequestDay: '',");
	  out.println(" maxRequestMonth: '',");
	  out.println(" enable: '',");
	  out.println(" topics: []");
	  out.println("}");
  }
  %>  
      
    ];

    const renderModel = (model, index) => {
      const form = document.createElement('form');
      form.className = 'bg-white rounded-lg shadow-md p-4';
      form.action = '/web/modelMetaData';
      form.method = 'POST';
      form.name = "Model";

      const summary = document.createElement('div');
      summary.className = 'cursor-pointer flex justify-between items-center';
      summary.innerHTML = `
        <div>
          <h3 class="font-semibold text-lg"> <%=header%> </h3>
        </div>
        <span class="text-blue-500">▼</span>
      `;

      const details = document.createElement('div');
      details.className = 'grid grid-cols-1 md:grid-cols-2 gap-4 mt-4 hidden';

      const fields = [
        'modelId', 'provider', 'modelName', 'type', 'rank',
        'maxTokenDay', 'maxTokenMonth', 'maxRequestDay', 'maxRequestMonth',
        'enable', 'topicsStr'
      ];

      fields.forEach(field => {
        const label = document.createElement('label');
        label.className = 'block text-sm font-medium';
        label.innerText = field.replace(/([A-Z])/g, ' $1').toLowerCase();

        let input;
        if (field === 'topicsStr') {
          input = document.createElement('textarea');
          input.value = Array.isArray(model.topics) ? model.topics.join(',') : '';
        } else {
          input = document.createElement('input');
          if (field === 'enable') {
            input.type = 'checkbox';
            input.checked = model[field];
            input.value = 'true';
          } else if (field === 'rank') {
            input.type = 'number';
            input.step = '0.1';
          } else if (['maxTokenDay', 'maxTokenMonth', 'maxRequestDay', 'maxRequestMonth'].includes(field)) {
            input.type = 'number';
          } else {
            input.type = 'text';
          }

          if (field !== 'enable') {
            input.value = model[field] || '';
          }
        }

        input.name = field;
        input.className = 'mt-1 w-full p-2 border border-gray-300 rounded';

        const wrapper = document.createElement('div');
        wrapper.appendChild(label);
        wrapper.appendChild(input);
        details.appendChild(wrapper);
      });

      const saveButton = document.createElement('button');
      saveButton.className = 'mt-4 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 col-span-full';
      saveButton.type = 'submit';
      saveButton.innerText = 'Save';
      details.appendChild(saveButton);
      //saveButton.onclick = () => form.submit();

      summary.onclick = () => {
        details.classList.toggle('hidden');
      };

      form.appendChild(summary);
      form.appendChild(details);

      return form;
    };

    const renderList = () => {
      const list = document.getElementById('modelList');
      list.innerHTML = '';
      sampleData.forEach((model, index) => {
        const modelElem = renderModel(model, index);
        list.appendChild(modelElem);
      });
    };

    const addNewModel = () => {
      sampleData.push({});
      renderList();
    };

    renderList();
  </script>
</body>
</html>