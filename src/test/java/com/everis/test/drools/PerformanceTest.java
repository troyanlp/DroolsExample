package com.everis.test.drools;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import com.everis.drools.entity.Customer;
import com.everis.drools.entity.Order;
import com.everis.drools.entity.Product;

public class PerformanceTest {

	@Test(timeout = 3000)
	public void performanceTest() {
		codeMode();
		drlMode();
		decisionTableMode();
	}

	public static void codeMode() {
		List<Order> orderList = getInitData(false);
		long start = System.currentTimeMillis();
		for (Order order : orderList) {
			// Initial Rule
			order.calculateTotalPrice();
			// Silver and Gold Rules
			if (order.getCustomer().getStatus() == Customer.SILVER_CUSTOMER) {
				order.setTotalPrice(order.getTotalPrice() * (1 - (5 / 100d)));
			} else if (order.getCustomer().getStatus() == Customer.GOLD_CUSTOMER) {
				order.setTotalPrice(order.getTotalPrice() * (1 - (10 / 100d)));
			}
			// Number of Products Rule
			if (order.numProducts >= 10) {
				if (order.isComparedToDate("01/07/2018", true) && order.isComparedToDate("01/10/2018", false)) {
					order.setTotalPrice(order.getTotalPrice() * (1 - (15 / 100d)));
				}
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Code Mode took " + (end - start) + " MilliSeconds");
		// showResults(orderList);

	}

	public static void drlMode() {
		// KieServices is the factory for all KIE services
		KieServices ks = KieServices.Factory.get();

		// From the kie services, a container is created from the classpath
		KieContainer kc = ks.getKieClasspathContainer();

		List<Order> orderList = null;
		try {
			// From the container, a session is created based on
			// its definition and configuration in the META-INF/kmodule.xml file
			KieSession ksession = kc.newKieSession("point-rulesKS");

			orderList = getInitData(false);

			long start = System.currentTimeMillis();
			for (int i = 0; i < orderList.size(); i++) {
				Order o = orderList.get(i);
				ksession.insert(o);
			}
			ksession.fireAllRules();
			long end = System.currentTimeMillis();
			System.out.println("Drl Mode took " + (end - start) + " MilliSeconds");

		} catch (Exception e) {
			e.printStackTrace();
		}
		// showResults(orderList);
	}

	public static void decisionTableMode() {
		StatelessKnowledgeSession session;
		try {
			KieServices ks = KieServices.Factory.get();

			// Load Excel file
			KnowledgeBase knowledgeBase = createKnowledgeBaseFromSpreadsheet();
			session = knowledgeBase.newStatelessKnowledgeSession();

			// From the kie services, a container is created from the classpath
			KieContainer kc = ks.getKieClasspathContainer();

			// InputStream in =
			// getClass().getResourceAsStream("/rules/spreadsheets/rules.xls");
			// System.out.println(getDRL(in));

			try {
				// From the container, a session is created based on
				// its definition and configuration in the META-INF/kmodule.xml file
				KieSession ksession = kc.newKieSession("point-rulesKS");

				List<Order> orderList = getInitData(true);

				long start = System.currentTimeMillis();
				for (int i = 0; i < orderList.size(); i++) {
					Order o = orderList.get(i);
					session.execute(o);
				}
				long end = System.currentTimeMillis();
				System.out.println("Decision Table Mode took " + (end - start) + " MilliSeconds");
				// showResults(orderList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<Order> getInitData(boolean decisionTable) {
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

	private static KnowledgeBase createKnowledgeBaseFromSpreadsheet() throws Exception {

		DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();

		dtconf.setInputType(DecisionTableInputType.XLS);

		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

		knowledgeBuilder.add(ResourceFactory.newClassPathResource("rules/spreadsheets/rules.xls"), ResourceType.DTABLE,
				dtconf);

		if (knowledgeBuilder.hasErrors()) {
			throw new RuntimeException(knowledgeBuilder.getErrors().toString());
		}

		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());

		return knowledgeBase;

	}

	private static void showResults(List<Order> orders) {
		for (Order order : orders) {
			System.out.println("Cliente " + order.getCustomer().getName() + " productos: " + order.getProducts().size()
					+ " Precio total: " + order.getTotalPrice());
		}
	}

}
