package rvt;

import java.util.Scanner;

public class UserInteface {
    private ToDoList list;
    private Scanner scanner;

    public UserInteface(ToDoList list, Scanner scanner) {
        this.list = list;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("Command (add, list, remove, exit): ");
            String command = scanner.nextLine();

            if (command.equals("add")) {
                System.out.println("Task to add: ");
                String task = scanner.nextLine();
                list.add(task);
            } else if (command.equals("list")) {
                list.print();
            } else if (command.equals("remove")) {
                System.out.println("Enter task number to remove: ");
                int number = Integer.parseInt(scanner.nextLine());
                list.remove(number);
            } else if (command.equals("exit")) {
                break;
            } else {
                System.out.println("Unknown command");
            }
        }
    }
    
}
