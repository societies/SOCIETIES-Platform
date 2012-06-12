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
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultClassName;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultIIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IAssessment;
import org.societies.webapp.models.PrivacyAssessmentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class PrivacyAssessmentController {

	private static Logger LOG = LoggerFactory.getLogger(PrivacyAssessmentController.class);

	public class PageNames {
		public static final String PRIVACY_ASSESSMENT = "privacy-assessment";
		public static final String PRIVACY_ASSESSMENT_SETTINGS = "privacy-assessment-settings";
		public static final String PRIVACY_ASSESSMENT_CHART = "privacy-assessment-chart";
		public static final String PRIVACY_ASSESSMENT_TABLE = "privacy-assessment-table";
	}
	
	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private IAssessment assessment;
	
	public IAssessment getAssessment() {
		return assessment;
	}

	public void setAssessment(IAssessment sdService) {
		LOG.debug("setAssessment()");
		this.assessment = sdService;
	}

	@RequestMapping(value = "/" + PageNames.PRIVACY_ASSESSMENT + ".html", method = RequestMethod.GET)
	public ModelAndView privacyAssessment() {

		LOG.debug("privacyassessment HTTP GET");

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		PrivacyAssessmentForm assForm = new PrivacyAssessmentForm();
		model.put("assForm", assForm);
		
		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
		Map<String, String> assessmentSubjectTypes = new LinkedHashMap<String, String>();
		assessmentSubjectTypes.put("receiverId", "Receiver identities");
		assessmentSubjectTypes.put("senderId", "Sender identities");
		assessmentSubjectTypes.put("senderClass", "Sender classes");
		model.put("assessmentSubjectTypes", assessmentSubjectTypes);

		Map<String, String> presentationFormats = new LinkedHashMap<String, String>();
		presentationFormats.put("table", "Table");
		presentationFormats.put("chart", "Chart");
		model.put("presentationFormats", presentationFormats);

		Map<String, String> assessmentSubjects = new LinkedHashMap<String, String>();
		assessmentSubjects.put("all", "All");
		model.put("assessmentSubjects", assessmentSubjects);

		return new ModelAndView(PageNames.PRIVACY_ASSESSMENT, model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/" + PageNames.PRIVACY_ASSESSMENT + ".html", method = RequestMethod.POST)
	public ModelAndView privacyAssessment(@Valid PrivacyAssessmentForm assForm,
			BindingResult result, Map model) {

		LOG.debug("privacyassessment HTTP POST");

		if (result.hasErrors()) {
			LOG.warn("BindingResult has errors");
			model.put("result", "privacy assessment form error");
			return new ModelAndView(PageNames.PRIVACY_ASSESSMENT, model);
		}

		if (assessment == null) {
			LOG.warn("Privacy Assessment Service reference not avaiable");
			model.put("errormsg", "Privacy Assessment Service reference not avaiable");
			return new ModelAndView("error", model);
		}

		String method = assForm.getMethod();
		String res;
		LOG.debug("Method = {}", method);
		
		try {
			
			if (method.equalsIgnoreCase("getAssessmentAllIds")) {
				
				HashMap<IIdentity, AssessmentResultIIdentity> assResult;
				assResult = assessment.getAssessmentAllIds();
				res="Privacy Assessment Result for all identities";
				model.put("services", assResult.values());
			}
			else if (method.equalsIgnoreCase("getAssessmentAllClasses")) {

				HashMap<String, AssessmentResultClassName> assResult;
				assResult = assessment.getAssessmentAllClasses();
				res="Privacy Assessment Result for all classes";
				model.put("services", assResult.values());
			}
			else if (method.equalsIgnoreCase("assessAllNow")) {
				
				assessment.assessAllNow();
				res="Privacy Assessment Result";
			}
			else if (method.equalsIgnoreCase("setAutoPeriod")) {
				
				assessment.setAutoPeriod(assForm.getAutoReassessmentInSecs());
				res="Privacy Assessment Result";
			}
			else {
				res="error unknown metod";
			}
		
			model.put("result", res);
		}
		catch (Exception ex)
		{
			LOG.warn("", ex);
			res = "Oops!!!! <br/>";
		};

		LOG.debug("HTTP POST end");
		return new ModelAndView(PageNames.PRIVACY_ASSESSMENT_TABLE, model);
	}
	
	@RequestMapping(value = "/" + PageNames.PRIVACY_ASSESSMENT_CHART + ".html", method = RequestMethod.GET)
	public ModelAndView barchart() {

		LOG.debug("barchart HTTP GET");
        final double[][] data = new double[][]{
                {210, 300, 320, 265, 299},
                {200, 304, 201, 201, 340}
            };

            final CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
                    "Team ", "", data);

            JFreeChart chart = null;
            BarRenderer renderer = null;
            CategoryPlot plot = null;

            final CategoryAxis categoryAxis = new CategoryAxis("Match");
            final ValueAxis valueAxis = new NumberAxis("Run");
            renderer = new BarRenderer();

            plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
            plot.setOrientation(PlotOrientation.HORIZONTAL);
            chart = new JFreeChart("Srore Bord", JFreeChart.DEFAULT_TITLE_FONT, plot, true);

            chart.setBackgroundPaint(new Color(249, 231, 236));

            Paint p1 = new GradientPaint(
                    0.0f, 0.0f, new Color(16, 89, 172), 0.0f, 0.0f, new Color(201, 201, 244));
            renderer.setSeriesPaint(1, p1);

            Paint p2 = new GradientPaint(
                    0.0f, 0.0f, new Color(255, 35, 35), 0.0f, 0.0f, new Color(255, 180, 180));
            renderer.setSeriesPaint(2, p2);

            plot.setRenderer(renderer);

            try {
                final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
                // FIXME
                String contextPath = "work/org.eclipse.virgo.kernel.deployer_3.0.2.RELEASE/staging/" +
                		"global/bundle/societies-webapp/1.0.0.SNAPSHOT/societies-webapp.war/";
                final File file1 = new File(contextPath + "images/barchart.png");
                ChartUtilities.saveChartAsPNG(file1, chart, 600, 400, info);
            } catch (Exception e) {
                LOG.warn("barchart(): ", e);
            }

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
//		model.put("message", "Please input values and submit");
//		
//		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
//		PrivacyAssessmentForm assForm = new PrivacyAssessmentForm();
//		model.put("assForm", assForm);
//		
//		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
//		Map<String, String> methods = new LinkedHashMap<String, String>();
//		methods.put("assessAllNow", "Perform assessment for all data transmissions");
//		methods.put("getAssessmentAllIds", "Get a-posteriori assessment for all sender identities");
//		methods.put("getAssessmentAllClasses", "Get a-posteriori assessment for all sender classes");
//		methods.put("setAutoPeriod", "Set time period of automatic re-assessment for all senders");
//		model.put("methods", methods);
//		
		return new ModelAndView(PageNames.PRIVACY_ASSESSMENT_CHART, model);
	}

	@RequestMapping(value = "/" + PageNames.PRIVACY_ASSESSMENT_SETTINGS + ".html", method = RequestMethod.GET)
	public ModelAndView privacyAssessmentSettings() {

		LOG.debug("privacyAssessmentSettings HTTP GET");

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		PrivacyAssessmentForm assForm = new PrivacyAssessmentForm();
		assForm.setAssessNow(false);
		int autoReassessmentInSecs = assessment.getAutoPeriod();
		assForm.setAutoReassessment(autoReassessmentInSecs >= 0);
		assForm.setAutoReassessmentInSecs(autoReassessmentInSecs);
		model.put("assForm", assForm);
		
		return new ModelAndView(PageNames.PRIVACY_ASSESSMENT_SETTINGS, model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/" + PageNames.PRIVACY_ASSESSMENT_SETTINGS + ".html", method = RequestMethod.POST)
	public ModelAndView privacyAssessmentSettings(@Valid PrivacyAssessmentForm assForm,
			BindingResult result, Map model) {

		LOG.debug("privacyAssessmentSettings HTTP POST");

		if (result.hasErrors()) {
			LOG.warn("BindingResult has errors");
			model.put("result", "privacy assessment form error");
			return new ModelAndView("error", model);
		}

		if (assessment == null) {
			LOG.warn("Privacy Assessment Service reference not avaiable");
			model.put("errormsg", "Privacy Assessment Service reference not avaiable");
			return new ModelAndView("error", model);
		}

		int autoAssessmentPeriod = assForm.getAutoReassessmentInSecs();
		boolean assessNow = assForm.isAssessNow();
		LOG.debug("autoReassessmentInSecs = {}, assessNow = {}", autoAssessmentPeriod, assessNow);
		
		try {
			if (assessNow) {
				assessment.assessAllNow();
			}
			
			if (!assForm.isAutoReassessment()) {
				autoAssessmentPeriod = -1;
			}
			assessment.setAutoPeriod(autoAssessmentPeriod);
		}
		catch (Exception ex)
		{
			LOG.warn("", ex);
		};

		LOG.debug("privacyAssessmentSettings HTTP POST end");
		return privacyAssessment();
	}
}