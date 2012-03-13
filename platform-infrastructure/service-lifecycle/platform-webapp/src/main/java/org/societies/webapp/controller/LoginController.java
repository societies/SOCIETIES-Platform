package org.societies.webapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.societies.webapp.models.LoginForm;
import org.societies.webapp.service.UserAuthentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;
import javax.validation.Valid;

@Controller
public class LoginController {
	/**
	 * Displays login Page
	 * @return
	 */
	public  LoginController() {
				
	}
	
	@RequestMapping(value="/login.html",method = RequestMethod.GET)
	public ModelAndView login() {
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please login to your Societies account");
		LoginForm loginForm = new LoginForm();
		model.put("loginForm", loginForm);		
		return new ModelAndView("login", model) ;
	}
		
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/login.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid LoginForm loginForm, BindingResult result,
    Map model) {

			if (result.hasErrors()) {
				model.put("result", "Login form error");
				return new ModelAndView("login", model);
			}			
			String userName = loginForm.getUserName();
			String password = loginForm.getPassword();
			UserAuthentication userAuth=new UserAuthentication();
			model.put("name", userName);
			boolean isAuthenticated=userAuth.authenticate(userName,password);
			if(isAuthenticated){
				model.put("result", "Login Successfull");
				return new ModelAndView("loginsuccess", model);	
			}else{					
				model.put("result", "Login UnSuccessfull");
				return new ModelAndView("login", model);
			}			
	}	
}