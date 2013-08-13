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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultBundle;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultClassName;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultIIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IAssessment;
import org.societies.webapp.models.PrivacyAssessmentForm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("Session")
@SessionScoped
@ManagedBean
public class PrivacyAssessmentService implements Serializable {

	private static final long serialVersionUID = -2466517020378971890L;

	private static final Logger log = LoggerFactory.getLogger(PrivacyAssessmentService.class);

	@ManagedProperty(value = "#{privacyAssessmentForm}")
	private PrivacyAssessmentForm model;

	private boolean imagesGenerated = false;
	private int imageIndex;
	private Map<Integer, byte[]> images;

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
//			for (double d : data) {
//				log.debug("getData(): {}", d);
//			}
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
		images = new HashMap<Integer, byte[]>();
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

	public List<StreamedContent> getImages() {
		log.debug("getImages()");
		if (!imagesGenerated) {
			generateImages();
			imagesGenerated = true;
		}
		List<StreamedContent> result = new ArrayList<StreamedContent>();
		for (byte[] img : images.values()) {
			//log.debug("getImages(): adding image {}", img);
			InputStream is = new ByteArrayInputStream(img);
			//log.debug("getImages(): image input stream: {}", is);
			DefaultStreamedContent dsc = new DefaultStreamedContent(is);
			result.add(dsc);
			//log.debug("getImages(): added image {}", dsc);
		}
		log.debug("getImages(): returning {} images", images.size());
		return result;
	}

	public StreamedContent getImage() {
		log.debug("getImage(): index = {}", imageIndex);
		if (!imagesGenerated) {
			generateImages();
			imagesGenerated = true;
		}
		InputStream is = new ByteArrayInputStream(images.get(imageIndex));
		return new DefaultStreamedContent(is);
	}

	public int getImageIndex() {
		log.debug("getImageIndex(): {}", imageIndex);
		return imageIndex;
	}

	public void setImageIndex(int imageIndex) {
		log.debug("setImageIndex({})", imageIndex);
		this.imageIndex = imageIndex;
	}

	public void previousImage() {
		log.debug("previousImage(): imageIndex before = {}", imageIndex);
		imageIndex = (imageIndex - 1 + images.size()) % images.size();
		log.debug("previousImage(): imageIndex after = {}", imageIndex);
	}

	public void nextImage() {
		imageIndex = (imageIndex + 1) % images.size();
		log.debug("nextImage(): imageIndex = {}", imageIndex);
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
			PlotData[] data, String[] dataLabels, int index) {

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
			BufferedImage image = chart.createBufferedImage(width, height);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			ChartUtilities.writeBufferedImageAsPNG(outStream, image);
			byte[] ba = outStream.toByteArray();
			images.put(index, ba);
			log.debug("Generated image {}", title);
		} catch(IOException e) {
			log.warn("createBarchart(): ", e);
		}
	}

	public void generateImages() {

		log.debug("generateImages()");

		generateImageReceiverIds();
		generateImageSenderIds();
		generateImageSenderClass();
		generateImageSenderBundle();
		generateImageDataAccessClass();
		generateImageDataAccessBundle();
		generateImageDataAccessIds();
	}

	private void generateImageReceiverIds() {

		String title = "Remote and local identities data has been sent to";
		String xlabel = "Receiver identity";
		String ylabel = "Number of data transmissions";

		Map<IIdentity, Integer> identities;
		identities = assessment.getNumDataTransmissionEventsForAllReceivers(
				model.getStartDate(), model.getEndDate());
		log.debug("Number of identities data has been transmitted to: {}", identities.size());
		PlotData[] plotData = new PlotData[] {mapToArrays(identities)};
		String[] plotDataLabels = new String[] {"data"};		
		createBarchart(title, xlabel, ylabel, plotData, plotDataLabels, 0);
	}

	private void generateImageSenderIds() {

		String title = "Local identities which sent data and their correlation with data access";
		String xlabel = "Sender identity";
		String ylabel = "Correlation of data transmission and data access";

		HashMap<IIdentity, AssessmentResultIIdentity> assResult;
		assResult = assessment.getAssessmentAllIds(model.getStartDate(), model.getEndDate());

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
		createBarchart(title, xlabel, ylabel, plotData, plotDataLabels, 1);
	}

	private void generateImageSenderClass() {

		String title = "Local Java classes which sent data and their correlation with data access";
		String xlabel = "Sender class";
		String ylabel = "Correlation of data transmission and data access";

		HashMap<String, AssessmentResultClassName> assResult;
		assResult = assessment.getAssessmentAllClasses(model.isIncludePlatformBundles(), model.getStartDate(), model.getEndDate());

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
		createBarchart(title, xlabel, ylabel, plotData, plotDataLabels, 2);
	}

	private void generateImageSenderBundle() {

		String title = "Local bundles which sent data and their correlation with data access";
		String xlabel = "Sender bundle";
		String ylabel = "Correlation of data transmission and data access";

		HashMap<String, AssessmentResultBundle> assResult;
		assResult = assessment.getAssessmentAllBundles(model.isIncludePlatformBundles(), model.getStartDate(), model.getEndDate());

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
				"Correlation with data access by the sender bundle",
				"Correlation with data access by any bundle"
		};
		createBarchart(title, xlabel, ylabel, plotData, plotDataLabels, 3);
	}

	private void generateImageDataAccessClass() {

		String title = "Java classes which accessed local data";
		String xlabel = "Class";
		String ylabel = "Number of accesses to local data";

		Map<String, Integer> dataAccessClasses;
		dataAccessClasses = assessment.getNumDataAccessEventsForAllClasses(model.isIncludePlatformBundles(), model.getStartDate(), model.getEndDate());
		log.debug("Number of data access events (by class): {}", dataAccessClasses.size());
		PlotData[] plotData = new PlotData[] {mapToArrays(dataAccessClasses)};
		String[] plotDataLabels = new String[] {"data"};
		createBarchart(title, xlabel, ylabel, plotData, plotDataLabels, 4);
	}

	private void generateImageDataAccessBundle() {

		String title = "Bundles which accessed local data";
		String xlabel = "Bundle";
		String ylabel = "Number of accesses to local data";

		Map<String, Integer> dataAccessBundles;
		dataAccessBundles = assessment.getNumDataAccessEventsForAllBundles(model.isIncludePlatformBundles(), model.getStartDate(), model.getEndDate());
		log.debug("Number of data access events (by bundle): {}", dataAccessBundles.size());
		PlotData[] plotData = new PlotData[] {mapToArrays(dataAccessBundles)};
		String[] plotDataLabels = new String[] {"data"};
		createBarchart(title, xlabel, ylabel, plotData, plotDataLabels, 5);
	}

	private void generateImageDataAccessIds() {

		String title = "Identities which accessed local data";
		String xlabel = "Identity";
		String ylabel = "Number of accesses to local data";

		Map<IIdentity, Integer> identities;
		identities = assessment.getNumDataAccessEventsForAllIdentities(model.getStartDate(), model.getEndDate());
		log.debug("Number of data access events (by identity): {}", identities.size());
		PlotData[] plotData = new PlotData[] {mapToArrays(identities)};
		String[] plotDataLabels = new String[] {"data"};
		createBarchart(title, xlabel, ylabel, plotData, plotDataLabels, 6);
	}
}
