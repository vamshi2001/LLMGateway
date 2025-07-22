<%@page import="org.bson.Document"%>
<%@page import="com.mongodb.client.FindIterable"%>
<%@page import="java.util.Iterator"%> 
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Model Properties</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-50 p-4 text-slate-800">
  <div class="max-w-3xl mx-auto">
    <h1 class="text-2xl font-bold text-blue-600 mb-6 text-center">Model Properties</h1>
    <div id="modelPropsContainer" class="space-y-4"></div>

    <button onclick="addModelProps()" class="mt-6 w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition">+ Add New Model</button>
  </div>

  <!-- Form to submit model data -->
  <form id="modelSubmitForm"
        class="hidden"
        action="/web/modelProperties"
        method="POST"
        name="modelProperties">
    <input type="hidden" name="modelId" id="hiddenModelId" />
    <input type="hidden" name="modelProps" id="hiddenModelProps" />
  </form>

  <script>
  
  const sampleData = [
	  <%
	  String header = "<strong>Model ID:</strong> ${entry.modelId || '(new)'} | <strong>Props Preview:</strong> ${entry.modelProps ? decodeProps(entry.modelProps).split(\"\\n\")[0] : ''}";
	  String placeHolder1 = "${decodeProps(entry.modelProps)}";
	  String placeHolder2Index = "${index}";
	  String placeHolder3 = "${entry.modelId}";
	  String placeHolder4 = "${entry.modelProps}";
	  FindIterable<Document> list = (FindIterable<Document>) request.getAttribute("list");
	  if(list != null){
		  Iterator<Document> iter = list.iterator();
		  boolean hasMinOneRecord = false;
		  while(iter.hasNext()){
			  hasMinOneRecord = true;
			  Document doc = iter.next();
			  String filename = doc.getString("modelId");
		      String base64EncodedProps = doc.getString("modelProps");
			  out.println("{");
			  out.println(" modelId: '" + filename+"',");
			  out.println(" modelProps: '" + base64EncodedProps+"'");
			  
			  if(iter.hasNext()){
				  out.println("},");
			  }else{
				  out.println("}");
			  }
			  
		  }
		  if(!hasMinOneRecord){
			  out.println("{");
			  out.println(" modelId: '',");
			  out.println(" modelProps: ''");
			  out.println("}");
		  }
	  }else{
		  out.println("{");
		  out.println(" modelId: '',");
		  out.println(" modelProps: ''");
		  out.println("}");
	  }
	  %>  
	      
	    ];

    function decodeProps(base64) {
      try {
        return atob(base64);
      } catch (e) {
        return "";
      }
    }

    function encodeProps(str) {
      return btoa(str);
    }

    function addModelProps(entry = { modelId: '', modelProps: '' }) {
      const container = document.getElementById('modelPropsContainer');
      const index = container.children.length;

      const wrapper = document.createElement('div');
      wrapper.className = 'border border-slate-300 rounded-md bg-white shadow';

      const header = document.createElement('button');
      header.className = 'w-full px-4 py-3 text-left bg-slate-100 hover:bg-slate-200 rounded-t-md';
      header.innerHTML = `<%=header%>`;
      header.onclick = () => {
        content.classList.toggle('hidden');
      };

      const content = document.createElement('div');
      content.className = 'p-4 space-y-3 hidden';
      content.innerHTML = `
        <label class="block">
          <span class="text-sm font-medium">Model ID</span>
          <input type="text" class="w-full border rounded px-3 py-2" value="<%=placeHolder3%>" id="modelId-<%=placeHolder2Index%>" />
        </label>

        <label class="block">
          <span class="text-sm font-medium">Properties (key=val per line)</span>
          <textarea rows="5" class="w-full border rounded px-3 py-2" id="modelPropsDecoded-<%=placeHolder2Index%>"><%=placeHolder1%></textarea>
        </label>

        <input type="hidden" id="modelPropsEncoded-<%=placeHolder2Index%>" value="<%=placeHolder4%>" />

        <button type="button" onclick="saveModelProps(<%=placeHolder2Index%>)" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">Save</button>
      `;

      wrapper.appendChild(header);
      wrapper.appendChild(content);
      container.appendChild(wrapper);
    }

    function saveModelProps(index) {
      const modelId = document.getElementById(`modelId-<%=placeHolder2Index%>`).value;
      const propsDecoded = document.getElementById(`modelPropsDecoded-<%=placeHolder2Index%>`).value;
      const encoded = encodeProps(propsDecoded);

      // Set form values
      document.getElementById('hiddenModelId').value = modelId;
      document.getElementById('hiddenModelProps').value = encoded;

      // Submit the form
      document.getElementById('modelSubmitForm').submit();
    }

    // Load sample
    sampleData.forEach(d => addModelProps(d));
  </script>
</body>
</html>