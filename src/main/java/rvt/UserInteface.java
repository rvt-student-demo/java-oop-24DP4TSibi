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
            System.out.println("Command: ");
            String command = scanner.nextLine();

            if (command.equals("add")) {
                System.out.println("To add: ");
                String task = scanner.nextLine();
                list.add(task);
            } else if (command.equals("list")) {
                list.print();
            } else if (command.equals("remove")) {
                System.out.println("Which one is removed? ");
                int number = Integer.parseInt(scanner.nextLine());
                list.remove(number);
            } else if (command.equals("stop")) {
                break;
            } else {
                System.out.println("Unknown command");
            }
        }
    }
    
}
