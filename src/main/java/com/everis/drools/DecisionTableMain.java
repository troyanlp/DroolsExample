package com.everis.drools;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
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

public class DecisionTableMain {
	private static StatelessKnowledgeSession session;

	public static void main(String[] args) {
		// KieServices is the factory for all KIE services
		DecisionTableMain test = new DecisionTableMain();
		try {
			test.readExcel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readExcel() throws Exception {
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
			execute(kc);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private String getDRL(InputStream stream) {
		SpreadsheetCompiler comp = new SpreadsheetCompiler();
		String drl = comp.compile(false, stream, InputType.XLS);
		return drl;
	}

	public static void execute(KieContainer kc) throws Exception {
		// From the container, a session is created based on
		// its definition and configuration in the META-INF/kmodule.xml file
		KieSession ksession = kc.newKieSession("point-rulesKS");

		List<Order> orderList = getInitData();

		for (int i = 0; i < orderList.size(); i++) {
			Order o = orderList.get(i);
			session.execute(o);
		}

		showResults(orderList);

		ksession.dispose();

	}

	private static List<Order> getInitData() throws Exception {
		Customer customerA = new Customer(Customer.DEFAULT_CUSTOMER, "Customer A");
		Customer customerB = new Customer(Customer.SILVER_CUSTOMER, "Customer B");
		Customer customerC = new Customer(Customer.GOLD_CUSTOMER, "Customer C");
		Customer customerD = new Customer(Customer.DEFAULT_CUSTOMER, "Customer D");
		Customer customerE = new Customer(Customer.SILVER_CUSTOMER, "Customer E");

		Product productA = new Product(1, "Product 1", 350);
		Product productB = new Product(2, "Product 2", 60);

		List<Order> orderList = new ArrayList<Order>();
		{
			Order order = new Order(customerA, true);
			order.addProduct(productA);
			order.addProduct(productB);
			orderList.add(order);
		}
		{
			Order order = new Order(customerB, true);
			order.addProduct(productA);
			order.addProduct(productB);
			orderList.add(order);
		}
		{
			Order order = new Order(customerC, true);
			order.addProduct(productA);
			order.addProduct(productB);
			orderList.add(order);
		}
		{
			Order order = new Order(customerD, true);
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
			Order order = new Order(customerE, true);
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

	private static void showResults(List<Order> orders) {
		for (Order order : orders) {
			System.out.println("Cliente " + order.getCustomer().getName() + " productos: " + order.getProducts().size()
					+ " Precio total: " + order.getTotalPrice());
		}
	}

}
