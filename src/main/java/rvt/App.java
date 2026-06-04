package rvt;

import java.util.List;
import java.util.Scanner;

public class App {
    private final DatabaseConnection database;
    private final Scanner scanner;

    public App() {
        this.database = new DatabaseConnection();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new App().run();
    }

    private void run() {
        while (true) {
            printMenu();
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> addCategory();
                case "2" -> addProduct();
                case "3" -> showCategories();
                case "4" -> showProducts();
                case "5" -> searchProductsByCategory();
                case "6" -> deleteProduct();
                case "7" -> updateProduct();
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Unknown option. Please choose a number from 0 to 7.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Product and Category Manager ===");
        System.out.println("1) Add category");
        System.out.println("2) Add product");
        System.out.println("3) Show all categories");
        System.out.println("4) Show all products");
        System.out.println("5) Search products by category id or name");
        System.out.println("6) Delete product");
        System.out.println("7) Update product");
        System.out.println("0) Exit");
        System.out.print("Choose option: ");
    }

    private void addCategory() {
        System.out.print("Enter category name: ");
        String name = scanner.nextLine().trim();
        if (name.isBlank()) {
            System.out.println("Category name must not be empty.");
            return;
        }
        try {
            int categoryId = database.addCategory(name);
            System.out.printf("Category added with id %d.%n", categoryId);
        } catch (RuntimeException e) {
            System.out.println("Failed to add category: " + e.getMessage());
        }
    }

    private void addProduct() {
        List<Category> categories = database.findAllCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories found. Add a category first.");
            return;
        }
        showCategories();
        System.out.print("Enter product name: ");
        String name = scanner.nextLine().trim();
        if (name.isBlank()) {
            System.out.println("Product name must not be empty.");
            return;
        }

        double price = readDouble("Enter product price: ");
        if (price <= 0) {
            System.out.println("Price must be greater than 0.");
            return;
        }

        int categoryId = readInt("Enter category id: ");
        if (!database.hasCategory(categoryId)) {
            System.out.println("Category id not found.");
            return;
        }

        try {
            int productId = database.addProduct(name, price, categoryId);
            System.out.printf("Product added with id %d.%n", productId);
        } catch (RuntimeException e) {
            System.out.println("Failed to add product: " + e.getMessage());
        }
    }

    private void showCategories() {
        List<Category> categories = database.findAllCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            return;
        }
        System.out.println("\nCategories:");
        System.out.printf("%-5s | %-20s%n", "ID", "Name");
        System.out.println("-----+----------------------");
        for (Category category : categories) {
            System.out.printf("%-5d | %-20s%n", category.getId(), category.getName());
        }
    }

    private void showProducts() {
        List<Product> products = database.findAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        System.out.println("\nProducts:");
        System.out.printf("%-5s | %-20s | %-8s | %-20s%n", "ID", "Name", "Price", "Category");
        System.out.println("-----+----------------------+----------+----------------------");
        for (Product product : products) {
            System.out.printf("%-5d | %-20s | %-8.2f | %-20s%n",
                    product.getId(), product.getName(), product.getPrice(), product.getCategoryName());
        }
    }

    private void searchProductsByCategory() {
        System.out.print("Enter category id or category name: ");
        String input = scanner.nextLine().trim();
        if (input.isBlank()) {
            System.out.println("Search text must not be empty.");
            return;
        }

        List<Product> products;
        if (isInteger(input)) {
            products = database.findProductsByCategoryId(Integer.parseInt(input));
        } else {
            products = database.findProductsByCategoryName(input);
        }

        if (products.isEmpty()) {
            System.out.println("No products found for the requested category.");
            return;
        }
        System.out.println("\nSearch results:");
        System.out.printf("%-5s | %-20s | %-8s | %-20s%n", "ID", "Name", "Price", "Category");
        System.out.println("-----+----------------------+----------+----------------------");
        for (Product product : products) {
            System.out.printf("%-5d | %-20s | %-8.2f | %-20s%n",
                    product.getId(), product.getName(), product.getPrice(), product.getCategoryName());
        }
    }

    private void deleteProduct() {
        int productId = readInt("Enter product id to delete: ");
        if (productId <= 0) {
            System.out.println("Product id must be positive.");
            return;
        }

        try {
            boolean deleted = database.deleteProduct(productId);
            if (deleted) {
                System.out.println("Product deleted.");
            } else {
                System.out.println("No product found with that id.");
            }
        } catch (RuntimeException e) {
            System.out.println("Failed to delete product: " + e.getMessage());
        }
    }

    private void updateProduct() {
        int productId = readInt("Enter product id to update: ");
        if (productId <= 0) {
            System.out.println("Product id must be positive.");
            return;
        }

        System.out.print("Enter new product name: ");
        String name = scanner.nextLine().trim();
        if (name.isBlank()) {
            System.out.println("Product name must not be empty.");
            return;
        }

        double price = readDouble("Enter new product price: ");
        if (price <= 0) {
            System.out.println("Price must be greater than 0.");
            return;
        }

        showCategories();
        int categoryId = readInt("Enter new category id: ");
        if (!database.hasCategory(categoryId)) {
            System.out.println("Category id not found.");
            return;
        }

        try {
            boolean updated = database.updateProduct(productId, name, price, categoryId);
            if (updated) {
                System.out.println("Product updated.");
            } else {
                System.out.println("No product found with that id.");
            }
        } catch (RuntimeException e) {
            System.out.println("Failed to update product: " + e.getMessage());
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isBlank()) {
                return -1;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isBlank()) {
                return -1;
            }
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private boolean isInteger(String value) {
        return value.matches("\\d+");
    }
}
