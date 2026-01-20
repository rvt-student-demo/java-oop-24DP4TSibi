package rvt;

import java.util.Scanner;

public class MainToDoList {
    private static ToDoList todo;

    public static void main(String[] args) {
        ToDoList toDoList = new ToDoList();
        Scanner scanner = new Scanner(System.in);
        UserInteface ui = new UserInteface(toDoList, scanner);
        ui.start(); 
    }
    
}
