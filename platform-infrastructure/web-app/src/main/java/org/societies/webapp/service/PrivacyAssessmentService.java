package org.societies.webapp.service;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

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
import org.societies.webapp.models.PrivacyAssessmentForm.ImageFileNames;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("Session")
@SessionScoped
@ManagedBean
public class PrivacyAssessmentService implements Serializable {

	private static final long serialVersionUID = -2466517020378971890L;

	// FIXME: The path should not depend on Virgo version, etc.
	private static final String contextPath = "work/org.eclipse.virgo.kernel.deployer_3.0.2.RELEASE/staging/" +
			"global/bundle/societies-webapp/0.6.0/societies-webapp.war/";
	
	private static final Logger log = LoggerFactory.getLogger(PrivacyAssessmentService.class);
	
	@ManagedProperty(value = "#{privacyAssessmentForm}")
	private PrivacyAssessmentForm model;

	private List<String> images;  
	private boolean imagesGenerated = false;
	
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
					log.debug("obj2str: obj[{}] = {}", k, obj[k]);
					labelsStr[k] = (String) obj[k];
				}
				return labelsStr;
			}
			else if (IIdentity.class.isAssignableFrom(clazz)) {
				String[] labelsStr = new String[obj.length];
				for (int k = 0; k < obj.length; k++) {
					log.debug("obj2str: obj[{}] = {}", k, obj[k]);
					labelsStr[k] = obj[k] == null ? null : ((IIdentity) obj[k]).getJid();
				}
				return labelsStr;
			}
			else {
				log.warn("Unsupported class: {}", clazz.getName());
				return new String[0];
			}
		}

		public double[] getData() {
			for (double d : data) {
				log.debug("getData(): {}", d);
			}
			return data;
		}

		public String[] getLabels() {
			for (String s : labels) {
				log.debug("getLabels(): {}", s);
			}
			return labels;
		}
	}
	
	public PrivacyAssessmentService() {
		
		log.info("constructor");
		
		images = new ArrayList<String>();  
		images.add(ImageFileNames.RECEIVER_IDS);
		images.add(ImageFileNames.SENDER_IDS);
		images.add(ImageFileNames.SENDER_CLASSES);
		images.add("http://a1.s6img.com/cdn/0008/p/1623955_9772764_lz.jpg");
		images.add(ImageFileNames.DATA_ACCESS_IDS);
		images.add(ImageFileNames.DATA_ACCESS_CLASSES);
	}
	
	/**
	 * OSGI service get auto injected
	 */
	@ManagedProperty(value = "#{privacyAssessment}")
	private IAssessment assessment;
	
	public IAssessment getAssessment() {
		return assessment;
	}

	public void setAssessment(IAssessment assessment) {
		log.debug("setAssessment()");
		this.assessment = assessment;
	}
	
	/**
	 * @return the model
	 */
	public PrivacyAssessmentForm getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(PrivacyAssessmentForm model) {
		log.debug("Model set");
		this.model = model;
	}

    public List<String> getImages() {
    	
    	if (!imagesGenerated) {
    		generateImages();
    		imagesGenerated = true;
    	}
        return images;
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
			log.warn("createBarchart(): ", e);
		}
	}
	
	public void generateImages() {

		log.debug("generateImages()");
		
		generateImageReceiverIds();
		generateImageSenderIds();
		generateImageSenderClass();
		generateImageDataAccessClass();
		generateImageDataAccessIds();
	}
	
	private void generateImageReceiverIds() {
		
		String xlabel = "Receiver identity";
		String ylabel = "Number of data transmissions";

		Map<IIdentity, Integer> identities;
		identities = assessment.getNumDataTransmissionEventsForAllReceivers(
				new Date(0), new Date());
		log.debug("Number of identities data has been transmitted to: {}", identities.size());
		PlotData[] plotData = new PlotData[] {mapToArrays(identities)};
		String[] plotDataLabels = new String[] {"data"};		
		createBarchart(null, xlabel, ylabel, plotData, plotDataLabels, PrivacyAssessmentForm.ImageFileNames.RECEIVER_IDS);
	}
	
	private void generateImageSenderIds() {

		String xlabel = "Sender identity";
		String ylabel = "Correlation of data transmission and data access";

		HashMap<IIdentity, AssessmentResultIIdentity> assResult;
		assResult = assessment.getAssessmentAllIds();

		int size = assResult.size();
		IIdentity[] labels = new IIdentity[size];
		double[][] data = new double[2][size];
		Iterator<IIdentity> iterator = assResult.keySet().iterator();
		
		log.debug("privacyAssessment(): size = {}", size);
		
		for (int k = 0; k < size; k++) {
			labels[k] = iterator.next();
			data[0][k] = assResult.get(labels[k]).getCorrWithDataAccessBySender();
			data[1][k] = assResult.get(labels[k]).getCorrWithDataAccessByAll();
			
			log.debug("privacyAssessment(): label[{}] = {}", k, labels[k]);
			log.debug("privacyAssessment(): data[0][{}] = {}", k, data[0][k]);
			log.debug("privacyAssessment(): data[1][{}] = {}", k, data[1][k]);
		}
		
		PlotData[] plotData = new PlotData[] {
				new PlotData(data[0], labels),
				new PlotData(data[1], labels)
				};
		String[] plotDataLabels = new String[] {
				"Correlation with data access by the sender identity",
				"Correlation with data access by any identity"
				};
		createBarchart(null, xlabel, ylabel, plotData, plotDataLabels, PrivacyAssessmentForm.ImageFileNames.SENDER_IDS);
	}
	
	private void generateImageSenderClass() {
		
		String xlabel = "Sender class";
		String ylabel = "Correlation of data transmission and data access";

		HashMap<String, AssessmentResultClassName> assResult;
		assResult = assessment.getAssessmentAllClasses();

		int size = assResult.size();
		String[] labels = new String[size];
		double[][] data = new double[2][size];
		Iterator<String> iterator = assResult.keySet().iterator();
		
		log.debug("privacyAssessment(): size = {}", size);

		for (int k = 0; k < size; k++) {
			labels[k] = iterator.next();
			data[0][k] = assResult.get(labels[k]).getCorrWithDataAccessBySender();
			data[1][k] = assResult.get(labels[k]).getCorrWithDataAccessByAll();
			
			log.debug("privacyAssessment(): label[{}] = {}", k, labels[k]);
			log.debug("privacyAssessment(): data[0][{}] = {}", k, data[0][k]);
			log.debug("privacyAssessment(): data[1][{}] = {}", k, data[1][k]);
		}
		
		PlotData[] plotData = new PlotData[] {
				new PlotData(data[0], labels),
				new PlotData(data[1], labels)
				};
		String[] plotDataLabels = new String[] {
				"Correlation with data access by the sender class",
				"Correlation with data access by any class"
				};
		createBarchart(null, xlabel, ylabel, plotData, plotDataLabels, PrivacyAssessmentForm.ImageFileNames.SENDER_CLASSES);
	}
	
	private void generateImageDataAccessClass() {
		
		String xlabel = "Class";
		String ylabel = "Number of accesses to local data";

		Map<String, Integer> dataAccessClasses;
		dataAccessClasses = assessment.getNumDataAccessEventsForAllClasses(new Date(0), new Date());
		log.debug("Number of data access events (by class): {}", dataAccessClasses.size());
		PlotData[] plotData = new PlotData[] {mapToArrays(dataAccessClasses)};
		String[] plotDataLabels = new String[] {"data"};
		createBarchart(null, xlabel, ylabel, plotData, plotDataLabels, PrivacyAssessmentForm.ImageFileNames.DATA_ACCESS_CLASSES);
	}
	
	private void generateImageDataAccessIds() {
		
		String xlabel = "Identity";
		String ylabel = "Number of accesses to local data";

		Map<IIdentity, Integer> identities;
		identities = assessment.getNumDataAccessEventsForAllIdentities(new Date(0), new Date());
		log.debug("Number of data access events (by identity): {}", identities.size());
		PlotData[] plotData = new PlotData[] {mapToArrays(identities)};
		String[] plotDataLabels = new String[] {"data"};
		createBarchart(null, xlabel, ylabel, plotData, plotDataLabels, PrivacyAssessmentForm.ImageFileNames.DATA_ACCESS_IDS);
	}
}
