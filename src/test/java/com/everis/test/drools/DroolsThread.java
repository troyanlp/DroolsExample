package com.everis.test.drools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.everis.drools.entity.Order;

public class DroolsThread extends Thread {

	public String name;
	private static final Logger LOGGER = Logger.getLogger(DroolsThread.class.getName());

	public DroolsThread(String name) {
		this.name = name;
	}

	public void run() {
		LOGGER.log(java.util.logging.Level.INFO, this.name);

		int count = 0;
		StringBuilder builder = new StringBuilder();
		List<Order> orderList = new ArrayList<Order>();
		// StatefulThreads state = new StatefulThreads();
		while (count < 5) {
			// System.out.println(name);
			orderList = StatefulThreads.getInitData(false);

			long startFull = System.currentTimeMillis();
			orderList.forEach(k -> StatefulThreads.session.insert(k));
			for (int i = 0; i < orderList.size(); i++) {
				long start = System.currentTimeMillis();
				Order o = orderList.get(i);
				StatefulThreads.session.insert(o);
				StatefulThreads.session.fireAllRules();
				long end = System.currentTimeMillis();
				builder.append(name + ": -Drl Mode took " + (end - start) + " MilliSeconds \n");
				// System.out.println(name + ": -Drl Mode took " + (end - start) + "
				// MilliSeconds");
			}

			long endFull = System.currentTimeMillis();
			builder.append(name + " Drl Mode took " + (endFull - startFull) + " MilliSeconds IN TOTAL! \n");
			// System.out.println(name + " Drl Mode took " + (endFull - startFull) + "
			// MilliSeconds IN TOTAL!");
			count++;
		}

		LOGGER.log(java.util.logging.Level.INFO, builder.toString());

		StatefulThreads.showResults(orderList);

	}
}
