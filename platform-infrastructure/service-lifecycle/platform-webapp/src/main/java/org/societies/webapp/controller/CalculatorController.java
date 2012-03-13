package org.societies.webapp.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;

import org.societies.service.api.IMathService;
import org.societies.webapp.models.CalculatorForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CalculatorController {

	@Autowired
	private IMathService calService;
	
	public IMathService getCalService() {
		return calService;
	}

	public void setCalService(IMathService calService) {
		this.calService = calService;
	}

	@RequestMapping(value = "/calculator.html", method = RequestMethod.GET)
	public ModelAndView calcultor() {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		CalculatorForm calForm = new CalculatorForm();
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put("ADD", "add number");
		methods.put("MULTIPLY", "Mutiply");
		methods.put("SUBTRACT", "Subtract");
		model.put("methods", methods);
		model.put("calForm", calForm);
		model.put("calculatorResult", "Calculator Result :");
		return new ModelAndView("calculator", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/calculator.html", method = RequestMethod.POST)
	public ModelAndView calcultor(@Valid CalculatorForm calForm,
			BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "calculator form error");
			return new ModelAndView("calculator", model);
		}

		if (getCalService() == null) {
			model.put("errormsg", "Calculator Service referece not avaiable");
			return new ModelAndView("error", model);
		}

		int numA = calForm.getNumberA();
		int numB = calForm.getNumberB();
		String method = calForm.getMethod();
		String res;
		int calresult;
		if (method.equalsIgnoreCase("ADD")) {
			calresult=this.getCalService().add(numA, numB);
			res="Calculator Result :"+numA+" "+method+" "+numB+" = "+calresult;
		} else if(method.equalsIgnoreCase("MULTIPLY")) {
			calresult=this.getCalService().multiply(numA, numB); 
			res="Calculator Result :"+numA+" "+method+" "+numB+" = "+calresult;
		}else if(method.equalsIgnoreCase("SUBTRACT")){
			calresult=this.getCalService().subtract(numA, numB); 
			res="Calculator Result :"+numA+" "+method+" "+numB+" = "+calresult;
		}else{
			res="error unknown metod";
		}
		
		model.put("calculatorResult", res);		
		return new ModelAndView("calculatorresult", model);

	}
}