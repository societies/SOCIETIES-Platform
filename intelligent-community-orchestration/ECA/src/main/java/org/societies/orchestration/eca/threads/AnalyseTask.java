package org.societies.orchestration.eca.threads;

import java.util.TimerTask;

import org.societies.orchestration.eca.ECAManager;
import org.societies.orchestration.eca.api.IECAManager;

public class AnalyseTask extends TimerTask {

	private IECAManager ecaManager;

	public AnalyseTask(IECAManager ecaManager) {
		this.ecaManager = ecaManager;
	}

	@Override
	public void run() {
		this.ecaManager.checkLocalContext();
		this.ecaManager.getRelatedCSS();
		System.out.println("Running!");

	}

}
