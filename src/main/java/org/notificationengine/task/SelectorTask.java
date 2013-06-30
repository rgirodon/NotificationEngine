package org.notificationengine.task;

import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.notificationengine.selector.ISelector;

public class SelectorTask extends TimerTask {

	private static Logger LOGGER = Logger.getLogger(SelectorTask.class);
	
	private ISelector selector;
	
	public SelectorTask(ISelector selector) {
		
		LOGGER.info("Instantiating Selector Task : " + selector.getClass().getName());
		
		this.selector = selector;
		
		LOGGER.info("Selector Task instantiated : " + selector.getClass().getName());
	}
	
	@Override
	public void run() {
		
		LOGGER.info("Launching Selector Task : " + selector.getClass().getName());
		
		this.selector.process();
		
		LOGGER.info("Selector Task has finished : " + selector.getClass().getName());
	}

}
