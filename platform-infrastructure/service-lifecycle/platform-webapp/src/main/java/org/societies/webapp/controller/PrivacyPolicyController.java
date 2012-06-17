package org.societies.webapp.controller;
/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultClassName;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultIIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IAssessment;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants;
import org.societies.comm.xmpp.xc.impl.CommManagerHelper;
import org.societies.webapp.models.PrivacyPolicyForm;


@Controller
public class PrivacyPolicyController {

	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyController.class);

	/**
	 * OSGI service get auto injected
	 */
	@Autowired(required=false)
	private IPrivacyPolicyManager privacyPolicyManager;
	@Autowired(required=false)
	private ICommManager commMngrRef;

	@RequestMapping(value = "/privacy-policies.html", method = RequestMethod.GET)
	public ModelAndView indexAction() {
		LOG.debug("privacy policy index HTTP GET");

		Map<String, Object> model = new HashMap<String, Object>();
		return new ModelAndView("privacy/privacy-policy/index", model);
	}

	@RequestMapping(value = "/privacy-policy.html", method = RequestMethod.GET)
	public ModelAndView updateAction() {
		LOG.debug("privacy policy update HTTP GET");

		Map<String, Object> model = new HashMap<String, Object>();
		PrivacyPolicyForm privacyPolicyFrom = new PrivacyPolicyForm();
		privacyPolicyFrom.createEmptyPrivacyPolicyFrom();
		model.put("privacyPolicy", privacyPolicyFrom);
		model.put("ActionList", ActionConstants.values());
		model.put("ConditionList", ConditionConstants.values());
		model.put("ResourceTypeList", CtxAttributeTypes.class.getDeclaredFields());
		return new ModelAndView("privacy/privacy-policy/update", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/privacy-policy.html", method = RequestMethod.POST)
	public ModelAndView updateAction(@Valid PrivacyPolicyForm privacyPolicyFrom, BindingResult result, Map model) {
		LOG.debug("privacy policy update HTTP POST");

		// -- Verification
		if (result.hasErrors()) {
			LOG.warn("BindingResult has errors");
			model.put("errormsg", "privacy policy form error<br />"+result.toString()+"<br />"+result.getFieldErrors().get(0).getObjectName());
			return new ModelAndView("error", model);
		}
		LOG.info(privacyPolicyFrom.toString());

		// -- Storage
		RequestPolicy privacyPolicy;
		StringBuffer resultMsg = new StringBuffer();
		if (isDepencyInjectionDone()) {
			try {
				privacyPolicy = privacyPolicyFrom.toRequestPolicy(commMngrRef.getIdManager());
				privacyPolicyManager.updatePrivacyPolicy(privacyPolicy);
				resultMsg.append("Privacy policy successfully saved.");
			} catch (InvalidFormatException e) {
				resultMsg.append("Error during privacy policy saving: "+e.getLocalizedMessage());
				LOG.error("Error during privacy policy saving", e);
			} catch (PrivacyException e) {
				resultMsg.append("Error during privacy policy saving: "+e.getLocalizedMessage());
				LOG.error("Error during privacy policy saving", e);
			}
		}
		else {
			resultMsg.append("Error with dependency injection");
			LOG.error("Error with dependency injection");
		}

		// -- Display the privacy policy
		model.put("privacyPolicy", privacyPolicyFrom);
		model.put("ActionList", ActionConstants.values());
		model.put("ConditionList", ConditionConstants.values());
		model.put("ResourceTypeList", CtxAttributeTypes.class.getDeclaredFields());
		model.put("ResultMsg", CtxAttributeTypes.class.getDeclaredFields());
		return new ModelAndView("privacy/privacy-policy/update", model);
	}
	//
	//	@SuppressWarnings("unchecked")
	//	@RequestMapping(value = "/privacy-assessment-settings.html", method = RequestMethod.POST)
	//	public ModelAndView privacyAssessmentSettings(@Valid PrivacyAssessmentForm assForm,
	//			BindingResult result, Map model) {
	//
	//		LOG.debug("privacyAssessmentSettings HTTP POST");
	//
	//		if (result.hasErrors()) {
	//			LOG.warn("BindingResult has errors");
	//			model.put("result", "privacy assessment form error");
	//			return new ModelAndView("error", model);
	//		}
	//
	//		if (assessment == null) {
	//			LOG.warn("Privacy Assessment Service reference not avaiable");
	//			model.put("errormsg", "Privacy Assessment Service reference not avaiable");
	//			return new ModelAndView("error", model);
	//		}
	//
	//		int autoAssessmentPeriod = assForm.getAutoReassessmentInSecs();
	//		boolean assessNow = assForm.isAssessNow();
	//		LOG.debug("autoReassessmentInSecs = {}, assessNow = {}", autoAssessmentPeriod, assessNow);
	//
	//		try {
	//			if (assessNow) {
	//				assessment.assessAllNow();
	//			}
	//
	//			if (!assForm.isAutoReassessment()) {
	//				autoAssessmentPeriod = -1;
	//			}
	//			assessment.setAutoPeriod(autoAssessmentPeriod);
	//		}
	//		catch (Exception ex)
	//		{
	//			LOG.warn("", ex);
	//		};
	//
	//		LOG.debug("privacyAssessmentSettings HTTP POST end");
	//		return privacyAssessment();
	//	}

	private boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	private boolean isDepencyInjectionDone(int level) {
		if (null == commMngrRef) {
			LOG.info("[Dependency Injection] Missing ICommManager");
			return false;
		}
		if (null == commMngrRef.getIdManager()) {
			LOG.info("[Dependency Injection] Missing IIdentityManager");
			return false;
		}
		return true;
	}

	// -- Dependency Injection
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
		LOG.info("[DepencyInjection] IPrivacyPolicyManager injected");
	}
	public void setCommMngrRef(ICommManager commMngrRef) {
		this.commMngrRef = commMngrRef;
		LOG.info("[DepencyInjection] ICommManager injected");
	}
}