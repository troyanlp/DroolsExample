package com.everis.test.drools;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.everis.drools.entity.Customer;
import com.everis.drools.entity.Order;
import com.everis.drools.entity.Product;

public class StatefulThreads {
	public static KieSession session;
	List<DroolsThread> th = new ArrayList<DroolsThread>();

	public void setUp() {
		// KieServices is the factory for all KIE services
		KieServices ks = KieServices.Factory.get();

		// From the kie services, a container is created from the classpath
		KieContainer kc = ks.getKieClasspathContainer();

		session = kc.newKieSession("point-rulesKS");

	}

	public void loop() {
		th.add(new DroolsThread("Hilo 1"));
		th.add(new DroolsThread("Hilo 2"));
		th.add(new DroolsThread("Hilo 3"));
		th.add(new DroolsThread("Hilo 4"));
		th.add(new DroolsThread("Hilo 5"));

		th.forEach(k -> k.start());

		// List<Order> orderList = StatefulThreads.getInitData(false);
		//
		// long startFull = System.currentTimeMillis();
		// orderList.forEach(k -> StatefulThreads.session.insert(k));
		// for (int i = 0; i < orderList.size(); i++) {
		// long start = System.currentTimeMillis();
		// Order o = orderList.get(i);
		// StatefulThreads.session.insert(o);
		// StatefulThreads.session.fireAllRules();
		// long end = System.currentTimeMillis();
		// System.out.println("MANUAL: -Drl Mode took " + (end - start) +
		// "MilliSeconds");
		// }
		// long endFull = System.currentTimeMillis();
		// System.out.println("MANUAL Drl Mode took " + (endFull - startFull) +
		// "MilliSeconds IN TOTAL!");

	}

	public static List<Order> getInitData(boolean decisionTable) {
		Customer customerA = new Customer(Customer.DEFAULT_CUSTOMER, "Customer A");
		Customer customerB = new Customer(Customer.SILVER_CUSTOMER, "Customer B");
		Customer customerC = new Customer(Customer.GOLD_CUSTOMER, "Customer C");
		Customer customerD = new Customer(Customer.DEFAULT_CUSTOMER, "Customer D");
		Customer customerE = new Customer(Customer.SILVER_CUSTOMER, "Customer E");

		Product productA = new Product(1, "Product 1", 350);
		Product productB = new Product(2, "Product 2", 60);

		List<Order> orderList = new ArrayList<Order>();
		{
			Order order = new Order(customerA, decisionTable);
			order.addProduct(productA);
			order.addProduct(productB);
			orderList.add(order);
		}
		{
			Order order = new Order(customerB, decisionTable);
			order.addProduct(productA);
			order.addProduct(productB);
			orderList.add(order);
		}
		{
			Order order = new Order(customerC, decisionTable);
			order.addProduct(productA);
			order.addProduct(productB);
			orderList.add(order);
		}
		{
			Order order = new Order(customerD, decisionTable);
			order.addProduct(productA);
			order.addProduct(productB);
			order.addProduct(new Product(3, "Product 3", 60));
			order.addProduct(new Product(4, "Product 4", 60));
			order.addProduct(new Product(5, "Product 5", 60));
			order.addProduct(new Product(6, "Product 6", 15));
			order.addProduct(new Product(7, "Product 7", 5));
			order.addProduct(new Product(8, "Product 8", 35));
			order.addProduct(new Product(9, "Product 9", 10));
			order.addProduct(new Product(10, "Product 10", 40));
			orderList.add(order);
		}
		{
			Order order = new Order(customerE, decisionTable);
			order.addProduct(productA);
			order.addProduct(productB);
			order.addProduct(new Product(3, "Product 3", 60));
			order.addProduct(new Product(4, "Product 4", 60));
			order.addProduct(new Product(5, "Product 5", 60));
			order.addProduct(new Product(6, "Product 6", 15));
			order.addProduct(new Product(7, "Product 7", 5));
			order.addProduct(new Product(8, "Product 8", 35));
			order.addProduct(new Product(9, "Product 9", 10));
			order.addProduct(new Product(10, "Product 10", 40));
			orderList.add(order);
		}
		return orderList;
	}

	public static void showResults(List<Order> orders) {
		for (Order order : orders) {
			System.out.println("Cliente " + order.getCustomer().getName() + " productos: " + order.getProducts().size()
					+ " Precio total: " + order.getTotalPrice());
		}
	}

	public KieSession getSession() {
		return session;
	}

	public static void main(String[] args) {
		StatefulThreads stateful = new StatefulThreads();
		stateful.setUp();
		stateful.loop();
	}
}
