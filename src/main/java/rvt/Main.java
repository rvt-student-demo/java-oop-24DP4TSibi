package rvt;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		System.out.println("=== Warehouse price test ===");
		Warehouse warehouse = new Warehouse();
		warehouse.addProduct("milk", 3, 10);
		warehouse.addProduct("coffee", 5, 7);

		System.out.println("prices:");
		System.out.println("milk: " + warehouse.price("milk"));
		System.out.println("coffee: " + warehouse.price("coffee"));
		System.out.println("sugar: " + warehouse.price("sugar"));

		System.out.println("\n=== Stock & take test ===");
		Warehouse wh2 = new Warehouse();
		wh2.addProduct("coffee", 5, 1);
		System.out.println("stock:");
		System.out.println("coffee:  " + wh2.stock("coffee"));
		System.out.println("sugar: " + wh2.stock("sugar"));

		System.out.println("taking coffee " + wh2.take("coffee"));
		System.out.println("taking coffee " + wh2.take("coffee"));
		System.out.println("taking sugar " + wh2.take("sugar"));

		System.out.println("stock:");
		System.out.println("coffee:  " + wh2.stock("coffee"));
		System.out.println("sugar: " + wh2.stock("sugar"));

		System.out.println("\n=== ShoppingCart test ===");
		ShoppingCart cart = new ShoppingCart();
		cart.add("milk", 3);
		cart.add("buttermilk", 2);
		cart.add("cheese", 5);
		System.out.println("cart price: " + cart.price());
		cart.add("computer", 899);
		System.out.println("cart price: " + cart.price());

		System.out.println("\ncart contents:");
		cart.print();

		System.out.println("\n=== Interactive Store (type products, enter empty line to finish) ===");
		Warehouse storeWh = new Warehouse();
		storeWh.addProduct("coffee", 5, 10);
		storeWh.addProduct("milk", 3, 20);
		storeWh.addProduct("cream", 2, 55);
		storeWh.addProduct("bread", 7, 8);

		Scanner scanner = new Scanner(System.in);
		Store store = new Store(storeWh, scanner);
		store.shop("John");

	}
}