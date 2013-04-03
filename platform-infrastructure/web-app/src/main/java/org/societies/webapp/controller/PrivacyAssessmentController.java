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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.validation.Valid;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultClassName;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultIIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IAssessment;
import org.societies.webapp.models.PrivacyAssessmentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@ManagedBean(name = "privacyAssessment")
@SessionScoped
public class PrivacyAssessmentController {

	private static Logger LOG = LoggerFactory.getLogger(PrivacyAssessmentController.class);
	
	private static final String RESULT = "result";

	// FIXME: The path should not depend on Virgo version, etc.
	private static final String contextPath = "work/org.eclipse.virgo.kernel.deployer_3.0.2.RELEASE/staging/" +
			"global/bundle/societies-webapp/0.4.1/societies-webapp.war/";

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
			public static final String DATA_ACCESS_IDS = "Data access by identities";
			public static final String DATA_ACCESS_CLASSES = "Data access by classes";

			// Keys
			public static final String RECEIVER_IDS_KEY = "receiverIds";
			public static final String SENDER_IDS_KEY = "senderIds";
			public static final String SENDER_CLASSES_KEY = "senderClasses";
			public static final String DATA_ACCESS_IDS_KEY = "dataAccessIds";
			public static final String DATA_ACCESS_CLASSES_KEY = "dataAccessClasses";
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
	
	public class PlotData {
		
		private double[] data;
		private String[] labels;
		
		public PlotData(double[] data, Object[] labels) {
			this.data = data;
			this.labels = obj2str(labels);
		}
		
		private String[] obj2str(Object[] obj) {
			
			Class<? extends Object> clazz;
			
			if (obj.length == 0) {
				return new String[0];
			}
			clazz = obj[0].getClass();
			
			if (String.class.isAssignableFrom(clazz)) {
				//return (String[]) obj;
				String[] labelsStr = new String[obj.length];
				for (int k = 0; k < obj.length; k++) {
					LOG.debug("obj2str: obj[{}] = {}", k, obj[k]);
					labelsStr[k] = (String) obj[k];
				}
				return labelsStr;
			}
			else if (IIdentity.class.isAssignableFrom(clazz)) {
				String[] labelsStr = new String[obj.length];
				for (int k = 0; k < obj.length; k++) {
					LOG.debug("obj2str: obj[{}] = {}", k, obj[k]);
					labelsStr[k] = obj[k] == null ? null : ((IIdentity) obj[k]).getJid();
				}
				return labelsStr;
			}
			else {
				LOG.warn("Unsupported class: {}", clazz.getName());
				return new String[0];
			}
		}

		public double[] getData() {
			for (double d : data) {
				LOG.debug("getData(): {}", d);
			}
			return data;
		}

		public String[] getLabels() {
			for (String s : labels) {
				LOG.debug("getLabels(): {}", s);
			}
			return labels;
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
		assessmentSubjectTypes.put(Presentation.SubjectTypes.DATA_ACCESS_IDS_KEY, Presentation.SubjectTypes.DATA_ACCESS_IDS);
		assessmentSubjectTypes.put(Presentation.SubjectTypes.DATA_ACCESS_CLASSES_KEY, Presentation.SubjectTypes.DATA_ACCESS_CLASSES);
		model.put("assessmentSubjectTypes", assessmentSubjectTypes);

		Map<String, String> presentationFormats = new LinkedHashMap<String, String>();
		presentationFormats.put(Presentation.Format.CHART, Presentation.Format.CHART);
		presentationFormats.put(Presentation.Format.TABLE, Presentation.Format.TABLE);
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
		Collection<PrivacyAssessmentForm> charts = new ArrayList<PrivacyAssessmentForm>();
		
		if (presentationFormat.equalsIgnoreCase(Presentation.Format.CHART)) {
			
			String chartFileName = "chart-1.png";
			
			String title;
			String xlabel;
			String ylabel;
			PlotData[] plotData;
			String[] plotDataLabels;
			
			if (subjectType.equalsIgnoreCase(Presentation.SubjectTypes.RECEIVER_IDS_KEY)) {
				
				title = Presentation.SubjectTypes.RECEIVER_IDS;
				xlabel = "Receiver identity";
				ylabel = "Number of data transmissions";
				
				Map<IIdentity, Integer> identities;
				identities = assessment.getNumDataTransmissionEventsForAllReceivers(
						new Date(0), new Date());
				LOG.debug("Number of identities data has been transmitted to: {}", identities.size());
				plotData = new PlotData[] {mapToArrays(identities)};
				plotDataLabels = new String[] {"data"};
			}
			else if (subjectType.equalsIgnoreCase(Presentation.SubjectTypes.SENDER_IDS_KEY)) {
				
				title = Presentation.SubjectTypes.SENDER_IDS;
				xlabel = "Sender identity";
				ylabel = "Correlation of data transmission and data access";

				HashMap<IIdentity, AssessmentResultIIdentity> assResult;
				assResult = assessment.getAssessmentAllIds();

				int size = assResult.size();
				IIdentity[] labels = new IIdentity[size];
				double[][] data = new double[2][size];
				Iterator<IIdentity> iterator = assResult.keySet().iterator();
				
				LOG.debug("privacyAssessment(): size = {}", size);
				
				for (int k = 0; k < size; k++) {
					labels[k] = iterator.next();
					data[0][k] = assResult.get(labels[k]).getCorrWithDataAccessBySender();
					data[1][k] = assResult.get(labels[k]).getCorrWithDataAccessByAll();
					
					LOG.debug("privacyAssessment(): label[{}] = {}", k, labels[k]);
					LOG.debug("privacyAssessment(): data[0][{}] = {}", k, data[0][k]);
					LOG.debug("privacyAssessment(): data[1][{}] = {}", k, data[1][k]);
				}
				
				plotData = new PlotData[] {
						new PlotData(data[0], labels),
						new PlotData(data[1], labels)
						};
				plotDataLabels = new String[] {
						"Correlation with data access by the sender identity",
						"Correlation with data access by any identity"
						};
			}
			else if (subjectType.equalsIgnoreCase(Presentation.SubjectTypes.SENDER_CLASSES_KEY)) {
				
				title = Presentation.SubjectTypes.SENDER_CLASSES;
				xlabel = "Sender class";
				ylabel = "Correlation of data transmission and data access";

				HashMap<String, AssessmentResultClassName> assResult;
				assResult = assessment.getAssessmentAllClasses();

				int size = assResult.size();
				String[] labels = new String[size];
				double[][] data = new double[2][size];
				Iterator<String> iterator = assResult.keySet().iterator();
				
				LOG.debug("privacyAssessment(): size = {}", size);

				for (int k = 0; k < size; k++) {
					labels[k] = iterator.next();
					data[0][k] = assResult.get(labels[k]).getCorrWithDataAccessBySender();
					data[1][k] = assResult.get(labels[k]).getCorrWithDataAccessByAll();
					
					LOG.debug("privacyAssessment(): label[{}] = {}", k, labels[k]);
					LOG.debug("privacyAssessment(): data[0][{}] = {}", k, data[0][k]);
					LOG.debug("privacyAssessment(): data[1][{}] = {}", k, data[1][k]);
				}
				
				plotData = new PlotData[] {
						new PlotData(data[0], labels),
						new PlotData(data[1], labels)
						};
				plotDataLabels = new String[] {
						"Correlation with data access by the sender class",
						"Correlation with data access by any class"
						};
			}
			else if (subjectType.equalsIgnoreCase(Presentation.SubjectTypes.DATA_ACCESS_CLASSES_KEY)) {
				
				title = Presentation.SubjectTypes.DATA_ACCESS_CLASSES;
				xlabel = "Class";
				ylabel = "Number of accesses to local data";

				Map<String, Integer> dataAccessClasses;
				dataAccessClasses = assessment.getNumDataAccessEventsForAllClasses(new Date(0), new Date());
				LOG.debug("Number of data access events (by class): {}", dataAccessClasses.size());
				plotData = new PlotData[] {mapToArrays(dataAccessClasses)};
				plotDataLabels = new String[] {"data"};
			}
			else if (subjectType.equalsIgnoreCase(Presentation.SubjectTypes.DATA_ACCESS_IDS_KEY)) {
				
				title = Presentation.SubjectTypes.DATA_ACCESS_IDS;
				xlabel = "Identity";
				ylabel = "Number of accesses to local data";

				Map<IIdentity, Integer> identities;
				identities = assessment.getNumDataAccessEventsForAllIdentities(new Date(0), new Date());
				LOG.debug("Number of data access events (by identity): {}", identities.size());
				plotData = new PlotData[] {mapToArrays(identities)};
				plotDataLabels = new String[] {"data"};
			}
			else {
				LOG.warn("Unexpected {}: {}", Presentation.SubjectTypes.class.getSimpleName(), subjectType);
				return privacyAssessment();
			}
			
			PrivacyAssessmentForm form1 = new PrivacyAssessmentForm();
			form1.setAssessmentSubject(title);
//			createBarchart(null, xlabel, ylabel, dataLabels, data, chartFileName);
			createBarchart(null, xlabel, ylabel, plotData, plotDataLabels, chartFileName);
			form1.setChart(chartFileName);
			charts.add(form1);
//			PrivacyAssessmentForm form2 = new PrivacyAssessmentForm();
//			form2.setAssessmentSubject(title + " 2");
//			form2.setChart(chartFileName);
//			charts.add(form2);
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
	 * @param map Map<String, Integer> or Map<IIdentity, Integer>
	 * @return
	 */
	private PlotData mapToArrays(Map map) {
		
		Object[] labels = new Object[map.keySet().size()];
		double[] data = new double[map.keySet().size()];
		int k;
		
		k = 0;
		for (Object key : map.keySet()) {
			labels[k] = key;
			data[k] = (Integer) map.get(key);
			++k;
		}
		
		return new PlotData(data, labels);
	}

//	private void createBarchart(String title, String categoryLabel, String valueLabel,
//			PlotData data, String filename) {
//		
//		createBarchart(title, categoryLabel, valueLabel, new PlotData[] {data},
//				new String[] {"data"}, filename);
//	}
	
	private void createBarchart(String title, String categoryLabel, String valueLabel,
			PlotData[] data, String[] dataLabels, String filename) {
		
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		int maxDataLength = 0;
		
		for (int i = 0; i < dataLabels.length; i++) {
			for (int j = 0; j < data[i].getData().length; j++) {
				dataSet.addValue(data[i].getData()[j], dataLabels[i], data[i].getLabels()[j]);
			}
			if (maxDataLength < data[i].getData().length) {
				maxDataLength = data[i].getData().length;
			}
		}

		JFreeChart chart = ChartFactory.createBarChart(
				title,
				categoryLabel,
				valueLabel,
				dataSet,
				PlotOrientation.VERTICAL,
				dataLabels.length > 1,  // display legend
				false,
				false
				);

		CategoryPlot plot = (CategoryPlot)chart.getPlot();
		CategoryAxis xAxis = (CategoryAxis)plot.getDomainAxis();
		xAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
		xAxis.setMaximumCategoryLabelLines(6);

		//chart.setBackgroundPaint(ChartColor.WHITE);
		int width = 50 + maxDataLength * 170;
		if (maxDataLength < 3) {
			width += 170;
		}
		int height = 400;
		try {
			File file = new File(contextPath + filename);
			ChartUtilities.saveChartAsPNG(file, chart, width, height);
		} catch(IOException e) {
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
