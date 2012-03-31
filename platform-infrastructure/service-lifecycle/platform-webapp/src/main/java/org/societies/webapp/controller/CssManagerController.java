package org.societies.webapp.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.societies.webapp.models.CssManagerForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.servicelifecycle.model.Service;

@Controller
public class CssManagerController {

	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private ICSSLocalManager cssLocalManager;

	public ICSSLocalManager getCssLocalManager() {
		return cssLocalManager;
	}

	public void setCssLocalManager(ICSSLocalManager cssLocalManager) {
		this.cssLocalManager = cssLocalManager;
	}

	@RequestMapping(value = "/cssmanager.html", method = RequestMethod.GET)
	public ModelAndView cssManager() {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Welcome to the Css Manager Controller Page");
		CssManagerForm cmForm = new CssManagerForm();
		Map<String, String> methods = new LinkedHashMap<String, String>();
		/*
		 * methods.put("RegisterCss", "Register a Css ");
		 * methods.put("RetrieveCss", "Get Registered Css");
		 */
		methods.put("CreateAd",
				"Create a CssAdvertisementRecord on the CssDirectory");
		methods.put("GetAds",
				"Get All CssAdvertisementRecord from Css Directory");
		methods.put("GetAllServices",
				"Get All Services Running on All Css Advertised in Css Directory");
		model.put("methods", methods);
		model.put("cmForm", cmForm);
		model.put("cssmanagerResult", "Css Management Result :");
		return new ModelAndView("cssmanager", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/cssmanager.html", method = RequestMethod.POST)
	public ModelAndView cssManager(@Valid CssManagerForm cmForm,
			BindingResult result, Map model) {

		model.put("message", "Welcome to the Css Manager Controller Page");

		if (result.hasErrors()) {
			model.put("result", "Css Manager form error");
			return new ModelAndView("cssmanager", model);
		}

		if (getCssLocalManager() == null) {
			model.put("errormsg", "Css ManagerService reference not avaiable");
			return new ModelAndView("error", model);
		}

		String method = cmForm.getMethod();
		String res;

		try {

			if (method.equalsIgnoreCase("RegisterCss")) {

				model.put("methodcalled", "RegisterCss");

				Future<CssInterfaceResult> futRegisteredCssDetails;
				CssInterfaceResult registeredCssDetails;

				// We can only have one css ( at the moment anyway, so make sure
				// there is not one there already
				futRegisteredCssDetails = this.getCssLocalManager()
						.getCssRecord();
				registeredCssDetails = futRegisteredCssDetails.get();

				if (registeredCssDetails.getProfile().getCssIdentity() != null) {
					res = "Css already registered for "
							+ registeredCssDetails.getProfile()
									.getCssIdentity()
							+ ", must unregister existing Css before registering a new one";
				} else {

					// We can only have one css ( at the moment anyway, so make
					// sure there is not one there already

					// Create a CssRecord and send it
					CssRecord cssRec = new CssRecord();
					cssRec.setCssIdentity(cmForm.getCssRecordCssIdentity());
					cssRec.setName(cmForm.getCssRecordName());
					cssRec.setPassword(cmForm.getCssRecordPassword());

					// Blank for now
					cssRec.setDomainServer("");
					cssRec.setCssHostingLocation("");
					cssRec.setEntity(0);
					cssRec.setForeName("");
					cssRec.setForeName("");
					cssRec.setIdentityName("");
					cssRec.setEmailID("");
					cssRec.setImID("");
					cssRec.setSocialURI("");
					cssRec.setSex(0);
					cssRec.setHomeLocation("");
					cssRec.setStatus(0);
					cssRec.setCssRegistration("");
					cssRec.setCssInactivation("");
					cssRec.setCssUpTime(0);
					cssRec.setPresence(0);

					futRegisteredCssDetails = this.getCssLocalManager()
							.registerCSS(cssRec);
					registeredCssDetails = futRegisteredCssDetails.get();

					res = "Rgistered Css: "
							+ registeredCssDetails.getProfile()
									.getCssIdentity() + " ; ";

				}
				model.put("res", res);

			} else if (method.equalsIgnoreCase("RetrieveCss")) {

				model.put("methodcalled", "RetrieveCss");

				Future<CssInterfaceResult> futRegisteredCssDetails;
				CssInterfaceResult registeredCssDetails;
				// Create a CssRecord and send it

				futRegisteredCssDetails = this.getCssLocalManager()
						.getCssRecord();
				registeredCssDetails = futRegisteredCssDetails.get();

				if (registeredCssDetails == null) {
					res = "Unable to retriece Css details";
				} else {
					res = "Got this : "
							+ registeredCssDetails.getProfile()
									.getCssIdentity() + " ; ";
				}
				model.put("res", res);

			} else if (method.equalsIgnoreCase("CreateAd")) {

				model.put("methodcalled", "CreateAd");

				CssAdvertisementRecord newCssAd = new CssAdvertisementRecord();

				newCssAd.setName(cmForm.getCssAdName());
				newCssAd.setId(cmForm.getCssAdId());
				newCssAd.setUri(cmForm.getCssAdUri());

				this.getCssLocalManager().addAdvertisementRecord(newCssAd);

				res = "Added Advertisement Record";
				model.put("res", res);

			} else if (method.equalsIgnoreCase("GetAds")) {

				model.put("methodcalled", "GetAds");

				Future<List<CssAdvertisementRecord>> futCssAdDetails;
				List<CssAdvertisementRecord> cssAdDetails;
				// Create a CssRecord and send it

				futCssAdDetails = this.getCssLocalManager()
						.findAllCssAdvertisementRecords();
				cssAdDetails = futCssAdDetails.get();
				if (cssAdDetails == null) {
					res = "No Advertisement Records Found";
				} else {
					res = "Found Advertisement Records";
					model.put("cssAdDetails", cssAdDetails);
				}
				model.put("res", res);

			} else if (method.equalsIgnoreCase("GetAllServices")) {

				model.put("methodcalled", "GetAllServices");

				Future<List<CssAdvertisementRecord>> futCssAdDetails;
				List<CssAdvertisementRecord> cssAdDetails;
				Future<List<Service>> futCssServiceDetails;
				List<Service> cssServiceDetails;

				futCssAdDetails = this.getCssLocalManager()
						.findAllCssAdvertisementRecords();
				cssAdDetails = futCssAdDetails.get();

				if (cssAdDetails == null) {
					res = "No Advertisement Records Found";
				} else {
					futCssServiceDetails = this.getCssLocalManager()
							.findAllCssServiceDetails(cssAdDetails);
					cssServiceDetails = futCssServiceDetails.get();
					if (cssServiceDetails == null) {
						res = "No Service Records Found";
					} else {
						res = "Found Services : ";
						model.put("cssServiceDetails", cssServiceDetails);
					}
				}
				model.put("res", res);
			} else {
				model.put("methodcalled", "Unknown");
				res = "error unknown metod";
			}
		} catch (Exception ex) {
			res = "Oops!!!! <br/>";
		}
		;

		model.put("cmForm", cmForm);

		return new ModelAndView("cssmanagerresult", model);

	}
}