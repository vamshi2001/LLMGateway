package com.api.hub.gateway.controller;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.dao.ModelDao;
import com.api.hub.gateway.dao.ModelMetricDao;
import com.api.hub.gateway.dao.ModelPropsDao;
import com.api.hub.gateway.dao.PersonaDao;
import com.api.hub.gateway.dao.PromptVersionDao;
import com.api.hub.gateway.dao.ToolCallDao;
import com.api.hub.gateway.dao.WebSearchConfigDao;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.JsoupExtractionConfig;
import com.api.hub.gateway.model.JsoupExtractionConfig.ExtractionRule;
import com.api.hub.gateway.model.Model;
import com.api.hub.gateway.model.ModelMetric;
import com.api.hub.gateway.model.ModelMetricDTO;
import com.api.hub.gateway.model.PersonaProperties;
import com.api.hub.gateway.model.PromptVersion;
import com.api.hub.gateway.model.TollCallData;
import com.mongodb.client.FindIterable;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/web")
@Slf4j
public class WebController {
	
	@Autowired
	ModelDao dao;
	
	@Autowired
	ModelMetricDao modelMetic;
	
	@Autowired
	PersonaDao personaDao;
	
	@Autowired
	ModelPropsDao modelPropsDao;
	
	@Autowired
	ToolCallDao toolCallDao;
	
	@Autowired
	WebSearchConfigDao webSearchConfigDao;
	
	@Autowired
	PromptVersionDao promptVersionDao;
	
	@GetMapping(value = "/modelMetaData")
	public ModelAndView modelMetaData() {
		ModelAndView mv = new ModelAndView();
		
		List<Model> list = dao.get();
		if(list != null && list.size() > 0) {
			mv.addObject("list", list);
		}
		mv.setViewName("modelMetaData");
		return mv;
	}

	@PostMapping(value = "/modelMetaData", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ModelAndView modelMetaData(@ModelAttribute("Model") Model req, HttpServletRequest request) {
		
		req.setTopicsFromString(req.getTopicsStr());
		dao.save(req);
		
		return modelMetaData();
	}
	
	@GetMapping(value = "/modelMetrics")
	public ModelAndView modelMetrics() {
		ModelAndView mv = new ModelAndView();
		
		List<ModelMetric> list = modelMetic.get();
		if(list != null && list.size() > 0) {
			List<ModelMetricDTO> dtoList = new ArrayList<ModelMetricDTO>();
			list.forEach(e -> dtoList.add(ModelMetricDTO.toDTO(e)));
			mv.addObject("list", dtoList);
		}
		mv.setViewName("modelMetrics");
		return mv;
	}

	@PostMapping(value = "/modelMetrics", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ModelAndView modelMetrics(@ModelAttribute("Model") ModelMetricDTO req, HttpServletRequest request) {
		
		modelMetic.save(ModelMetricDTO.toModelMetricO(req));
		
		return modelMetrics();
	}
	
	@GetMapping(value = "/personaProperties")
	public ModelAndView personaProperties() {
		ModelAndView mv = new ModelAndView();
		
		List<PersonaProperties> list = personaDao.get();
		if(list != null && list.size() > 0) {
			mv.addObject("list", list);
		}
		mv.setViewName("personaProperties");
		return mv;
	}

	@PostMapping(value = "/personaProperties", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ModelAndView personaProperties(@ModelAttribute("personaProperties") PersonaProperties req, HttpServletRequest request) {
		
		personaDao.save(req);
		return personaProperties();
	}
	
	@GetMapping(value = "/modelProperties")
	public ModelAndView modelProperties() {
		ModelAndView mv = new ModelAndView();
		
		FindIterable<Document> list = modelPropsDao.get();
		if(list != null) {
			mv.addObject("list", list);
		}
		mv.setViewName("modelProperties");
		return mv;
	}

	@PostMapping(value = "/modelProperties", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ModelAndView modelProperties(@RequestParam String modelId, @RequestParam String modelProps) {
		try {
			modelPropsDao.save(modelId, modelProps);
		} catch (ApiHubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return modelProperties();
	}
	
	@GetMapping(value = "/toolCall")
	public ModelAndView toolCall() {
		ModelAndView mv = new ModelAndView();
		
		List<TollCallData> list = toolCallDao.get();
		if(list != null&& list.size() > 0) {
			mv.addObject("list", list);
		}
		mv.setViewName("toolCall");
		return mv;
	}

	@PostMapping(value = "/toolCall", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ModelAndView toolCall(@ModelAttribute("toolCall") TollCallData req, HttpServletRequest request) {
		
		toolCallDao.save(req);
		return toolCall();
	}
	
	@GetMapping(value = "/websearch")
	public ModelAndView websearch() {
		ModelAndView mv = new ModelAndView();
		
		List<JsoupExtractionConfig> list = null;
		try {
			list = webSearchConfigDao.get();
		} catch (ApiHubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list != null&& list.size() > 0) {
			mv.addObject("list", list);
		}
		mv.setViewName("websearch");
		return mv;
	}

	@PostMapping(value = "/websearch", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ModelAndView websearch(
		    @RequestParam String host,
		    @RequestParam List<String> ruleTypes,
		    @RequestParam List<String> ruleValues,
		    @RequestParam List<Boolean> ruleAll
		) {
		    // Build JsoupExtractionConfig and process it
		    List<ExtractionRule> rules = new ArrayList<>();
		    for (int i = 0; i < ruleTypes.size(); i++) {
		        ExtractionRule rule = new ExtractionRule();
		        rule.setType(ruleTypes.get(i));
		        rule.setValue(ruleValues.get(i));
		        rule.setAll(ruleAll.get(i));
		        rules.add(rule);
		    }

		    JsoupExtractionConfig config = new JsoupExtractionConfig();
		    config.setHost(host);
		    config.setRules(rules);

		    // Save config, log, etc.
		    
		    webSearchConfigDao.save(config);
		    return websearch();
		}
	
	@GetMapping(value = "/promptVersion")
	public ModelAndView promptVersion() {
		ModelAndView mv = new ModelAndView();
		
		List<PromptVersion> list = promptVersionDao.get();
		if(list != null&& list.size() > 0) {
			mv.addObject("list", list);
		}
		mv.setViewName("promptVersion");
		return mv;
	}

	@PostMapping(value = "/promptVersion", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ModelAndView promptVersion(@ModelAttribute("promptVersion") PromptVersion req, HttpServletRequest request) {
		promptVersionDao.save(req);
		return promptVersion();
	}
}
