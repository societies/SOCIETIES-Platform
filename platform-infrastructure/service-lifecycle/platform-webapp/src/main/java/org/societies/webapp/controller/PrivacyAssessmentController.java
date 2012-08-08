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
import java.awt.List;
import java.awt.Paint;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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
	
	private static final String RESULT = "result";

	// FIXME: The path should not depend on Virgo version, etc.
	private static final String contextPath = "work/org.eclipse.virgo.kernel.deployer_3.0.2.RELEASE/staging/" +
			"global/bundle/societies-webapp/1.0.0.SNAPSHOT/societies-webapp.war/";

	/**
	 * URL parts without prefix and suffix
	 */
	public class PageNames {
		public static final String PRIVACY_ASSESSMENT = "privacy-assessment";
		public static final String PRIVACY_ASSESSMENT_SETTINGS = "privacy-assessment-settings";
		public static final String PRIVACY_ASSESSMENT_CHART = "privacy-assessment-chart";
		public static final String PRIVACY_ASSESSMENT_TABLE = "privacy-assessment-table";
	}
	
	/**
	 * Parameters for presentation of results. These affect the view.
	 */
	public class Presentation {
		
		/**
		 * Types or categories of subjects of privacy assessment to be shown
		 */
		public class SubjectTypes {
			
			// Values
			public static final String RECEIVER_IDS = "Receiver identities";
			public static final String SENDER_IDS = "Sender identities";
			public static final String SENDER_CLASSES = "Sender classes";

			// Keys
			public static final String RECEIVER_IDS_KEY = "receiverIds";
			public static final String SENDER_IDS_KEY = "senderIds";
			public static final String SENDER_CLASSES_KEY = "senderClasses";
		}
		
		/**
		 * Subjects of privacy assessment to be shown
		 */
		public class Subjects {
			
			// Keys and values
			public static final String ALL = "All";
		}
		
		/**
		 * Format of presentation
		 */
		public class Format {

			// Keys and values
			public static final String CHART = "Chart";
			public static final String TABLE = "Table";
		}
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

		LOG.debug(PageNames.PRIVACY_ASSESSMENT + " HTTP GET");

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		PrivacyAssessmentForm assForm = new PrivacyAssessmentForm();
		model.put("assForm", assForm);
		
		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
		Map<String, String> assessmentSubjectTypes = new LinkedHashMap<String, String>();
		assessmentSubjectTypes.put(Presentation.SubjectTypes.RECEIVER_IDS_KEY, Presentation.SubjectTypes.RECEIVER_IDS);
		assessmentSubjectTypes.put(Presentation.SubjectTypes.SENDER_IDS_KEY, Presentation.SubjectTypes.SENDER_IDS);
		assessmentSubjectTypes.put(Presentation.SubjectTypes.SENDER_CLASSES_KEY, Presentation.SubjectTypes.SENDER_CLASSES);
		model.put("assessmentSubjectTypes", assessmentSubjectTypes);

		Map<String, String> presentationFormats = new LinkedHashMap<String, String>();
		presentationFormats.put(Presentation.Format.TABLE, Presentation.Format.TABLE);
		presentationFormats.put(Presentation.Format.CHART, Presentation.Format.CHART);
		model.put("presentationFormats", presentationFormats);

		Map<String, String> assessmentSubjects = new LinkedHashMap<String, String>();
		assessmentSubjects.put(Presentation.Subjects.ALL, Presentation.Subjects.ALL);
		model.put("assessmentSubjects", assessmentSubjects);

		return new ModelAndView(PageNames.PRIVACY_ASSESSMENT, model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/" + PageNames.PRIVACY_ASSESSMENT + ".html", method = RequestMethod.POST)
	public ModelAndView privacyAssessment(@Valid PrivacyAssessmentForm assForm,
			BindingResult result, Map model) {

		LOG.debug(PageNames.PRIVACY_ASSESSMENT + " HTTP POST");

		if (result.hasErrors()) {
			LOG.warn("BindingResult has errors");
			model.put(RESULT, "privacy assessment form error");
			return new ModelAndView(PageNames.PRIVACY_ASSESSMENT, model);
		}

		if (assessment == null) {
			LOG.warn("Privacy Assessment Service reference not avaiable");
			model.put("errormsg", "Privacy Assessment Service reference not avaiable");
			return new ModelAndView("error", model);
		}

		String presentationFormat = assForm.getPresentationFormat();
		String subjectType = assForm.getAssessmentSubjectType();
		LOG.debug("presentationFormat = {}, subjectType = {}", presentationFormat, subjectType);
		Object assValues;
		Collection<PrivacyAssessmentForm> charts = new ArrayList<PrivacyAssessmentForm>();
		
		if (presentationFormat.equalsIgnoreCase(Presentation.Format.CHART)) {
			
			if (subjectType.equalsIgnoreCase(Presentation.SubjectTypes.SENDER_IDS_KEY)) {
				HashMap<IIdentity, AssessmentResultIIdentity> assResult;
				assResult = assessment.getAssessmentAllIds();
				assValues = assResult.values();
			}
			else if (subjectType.equalsIgnoreCase(Presentation.SubjectTypes.RECEIVER_IDS_KEY)) {
				// FIXME
				HashMap<IIdentity, AssessmentResultIIdentity> assResult;
				assResult = assessment.getAssessmentAllIds();
				assValues = assResult.values();
			}
			else if (subjectType.equalsIgnoreCase(Presentation.SubjectTypes.SENDER_CLASSES_KEY)) {
				HashMap<String, AssessmentResultClassName> assResult;
				assResult = assessment.getAssessmentAllClasses();
				assValues = assResult.values();
			}
			else {
				LOG.warn("Unexpected {}: {}", Presentation.SubjectTypes.class.getSimpleName(), subjectType);
				return privacyAssessment();
			}
			//model.put("assessmentResults", assValues);
			
			double[][] data = new double[][] {
					{210, 300, 320, 265, 299},
					{200, 304, 201, 201, 340}
					};

			PrivacyAssessmentForm form1 = new PrivacyAssessmentForm();
			form1.setAssessmentSubject("Subject 1");
			String chart1 = "chart-1.png";
			createBarchart(null, "Class", "Packets per month", data, chart1);
			form1.setChart(chart1);
			charts.add(form1);
			PrivacyAssessmentForm form2 = new PrivacyAssessmentForm();
			form2.setAssessmentSubject("Subject 2");
			String chart2 = "chart-2.png";
			createBarchart(null, "Identity", "Correlation with data access by same identity", data, chart2);
			form2.setChart(chart2);
			charts.add(form2);
			model.put("assessmentResults", charts);

			LOG.debug(PageNames.PRIVACY_ASSESSMENT + " HTTP POST end");
			return new ModelAndView(PageNames.PRIVACY_ASSESSMENT_CHART, model);
		}
		else if (presentationFormat.equalsIgnoreCase(Presentation.Format.TABLE)) {

			HashMap<String, AssessmentResultClassName> assResult;
			assResult = assessment.getAssessmentAllClasses();
			model.put("assessmentResults", assResult.values());

			LOG.debug(PageNames.PRIVACY_ASSESSMENT + " HTTP POST end");
			return new ModelAndView(PageNames.PRIVACY_ASSESSMENT_TABLE, model);
		}
		else {
			LOG.warn("Unexpected {}: {}", Presentation.Format.class.getSimpleName(), presentationFormat);
			return privacyAssessment();
		}
	}

	/**
	 * 
	 * @param title
	 * @param categoryLabel
	 * @param valueLabel
	 * @param data The data to be displayed. Example: <br/>
	 *        new double[][] { <br/>
	 *        {210, 300, 320, 265, 299}, <br/>
	 *        {200, 304, 201, 201, 340} <br/>
	 *        };
	 * @param filename
	 */
	private void createBarchart(String title, String categoryLabel, String valueLabel, double[][] data, String filename) {

		LOG.debug("createBarchart({}, ..., {})", title, filename);
		
		final CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				categoryLabel + " ", "", data);

		JFreeChart chart;
		BarRenderer renderer;
		CategoryPlot plot;

		CategoryAxis categoryAxis;
		ValueAxis valueAxis;
		
		if (categoryLabel == null) {
			categoryAxis = new CategoryAxis();
		}
		else {
			categoryAxis = new CategoryAxis(categoryLabel);
		}
//		categoryAxis.setTickLabelsVisible(false);
//		categoryAxis.setVisible(false);
		if (valueLabel == null) {
			valueAxis = new NumberAxis();
		}
		else {
			valueAxis = new NumberAxis(valueLabel);
		}
		renderer = new BarRenderer();

		plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
		plot.setOrientation(PlotOrientation.HORIZONTAL);
		chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

//		chart.setBackgroundPaint(new Color(255, 255, 255));
//
//		Paint p1 = new GradientPaint(
//				0.0f, 0.0f, new Color(16, 89, 172), 0.0f, 0.0f, new Color(201, 201, 244));
//		renderer.setSeriesPaint(1, p1);
//
//		Paint p2 = new GradientPaint(
//				0.0f, 0.0f, new Color(255, 35, 35), 0.0f, 0.0f, new Color(255, 180, 180));
//		renderer.setSeriesPaint(2, p2);

		plot.setRenderer(renderer);

		try {
			final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
			final File file1 = new File(contextPath + filename);
			ChartUtilities.saveChartAsPNG(file1, chart, 600, 400, info);
		} catch (Exception e) {
			LOG.warn("createBarchart(): ", e);
		}
	}

	@RequestMapping(value = "/" + PageNames.PRIVACY_ASSESSMENT_SETTINGS + ".html", method = RequestMethod.GET)
	public ModelAndView privacyAssessmentSettings() {

		LOG.debug(PageNames.PRIVACY_ASSESSMENT_SETTINGS + " HTTP GET");

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

		LOG.debug(PageNames.PRIVACY_ASSESSMENT_SETTINGS + " HTTP POST");

		if (result.hasErrors()) {
			LOG.warn("BindingResult has errors");
			model.put(RESULT, "privacy assessment form error");
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

		LOG.debug(PageNames.PRIVACY_ASSESSMENT_SETTINGS + " HTTP POST end");
		return privacyAssessment();
	}
}
