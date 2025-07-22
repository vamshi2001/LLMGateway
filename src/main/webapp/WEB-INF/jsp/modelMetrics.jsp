<%@page import="java.util.Iterator"%> 
<%@page import="java.util.List"%>
<%@page import="com.api.hub.gateway.model.ModelMetricDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Model Metrics</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-50 p-4">
  <div class="max-w-4xl mx-auto">
    <h2 class="text-2xl font-bold text-blue-600 mb-6">Model Metrics</h2>

    <div id="metricsList" class="space-y-4"></div>

    <button onclick="addNewMetric()"
            class="mt-6 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
      + Add New Metric
    </button>
  </div>

  <script>
  const sampleMetrics = [
    <% 
    String header = "${metric.modelId || 'New'} - ${metric.currentDateStr || 'Date'} (Tokens)";
      List<ModelMetricDTO> list = (List<ModelMetricDTO>) request.getAttribute("list");
      if (list != null && !list.isEmpty()) {
        for (Iterator<ModelMetricDTO> iter = list.iterator(); iter.hasNext(); ) {
          ModelMetricDTO model = iter.next();
    %>{
    	currentDateStr: '<%= model.getCurrentDateStr() != null ? model.getCurrentDateStr(): "" %>',
      modelId: '<%= model.getModelId() %>',
      currentActiveRequest: <%= model.getCurrentActiveRequest() %>,
      currentInputTokenConsumedPerDay: <%= model.getCurrentInputTokenConsumedPerDay() %>,
      currentInputTokenConsumedPerMonth: <%= model.getCurrentInputTokenConsumedPerMonth() %>,
      currentOutputTokenConsumedPerDay: <%= model.getCurrentOutputTokenConsumedPerDay() %>,
      currentOutputTokenConsumedPerMonth: <%= model.getCurrentOutputTokenConsumedPerMonth() %>,
      requestPerDay: <%= model.getRequestPerDay() %>,
      requestPerMonth: <%= model.getRequestPerMonth() %>,
      totalFailuresToday: <%= model.getTotalFailuresToday() %>,
      failureInterval: <%= model.getFailureInterval() %>
    }<%= iter.hasNext() ? "," : "" %>
    <%  }
      } else {
    %>{
      currentDateStr: new Date().toISOString().split('T')[0],
      modelId: '',
      currentActiveRequest:'',
      currentInputTokenConsumedPerDay: '',
      currentInputTokenConsumedPerMonth: '',
      currentOutputTokenConsumedPerDay: '',
      currentOutputTokenConsumedPerMonth: '',
      requestPerDay: '',
      requestPerMonth: '',
      totalFailuresToday: '',
      failureInterval: ''
    }
    <% } %>
  ];

  const renderMetric = (metric, index) => {
    const form = document.createElement('form');
    form.className = 'bg-white rounded-lg shadow-md p-4';
    form.action = '/web/modelMetrics';
    form.method = 'POST';
    form.name = "ModelMetric";

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
      'currentDateStr', 'modelId',
      'currentActiveRequest',
      'currentInputTokenConsumedPerDay', 'currentInputTokenConsumedPerMonth',
      'currentOutputTokenConsumedPerDay', 'currentOutputTokenConsumedPerMonth',
      'requestPerDay', 'requestPerMonth',
      'totalFailuresToday', 'failureInterval'
    ];

    fields.forEach(field => {
      const label = document.createElement('label');
      label.className = 'block text-sm font-medium';
      label.innerText = field.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());

      const input = document.createElement('input');
      input.className = 'mt-1 w-full p-2 border border-gray-300 rounded';
      input.name = field;
      input.value = metric[field] || '';

      if (field === 'currentDateStr') {
        input.type = 'datetime-local';
      } else if (['failureInterval', 'requestPerDay', 'requestPerMonth', 'totalFailuresToday', 'currentActiveRequest'].includes(field)) {
        input.type = 'number';
        input.step = '1';
      } else {
        input.type = 'number';
      }

      const wrapper = document.createElement('div');
      wrapper.appendChild(label);
      wrapper.appendChild(input);

      details.appendChild(wrapper);
    });

    const saveButton = document.createElement('button');
    saveButton.className = 'mt-4 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700';
    saveButton.innerText = 'Save';
    saveButton.type = 'submit';
    details.appendChild(saveButton);

    summary.onclick = () => {
      details.classList.toggle('hidden');
    };

    form.appendChild(summary);
    form.appendChild(details);

    return form;
  };

  const renderList = () => {
    const list = document.getElementById('metricsList');
    list.innerHTML = '';
    sampleMetrics.forEach((metric, index) => {
      const metricElem = renderMetric(metric, index);
      list.appendChild(metricElem);
    });
  };

  const addNewMetric = () => {
    sampleMetrics.push({});
    renderList();
  };

  renderList();
  </script>
</body>
</html>