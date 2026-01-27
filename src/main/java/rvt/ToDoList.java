package rvt;
import java.io.File;
import java.util.ArrayList;

public class ToDoList {
   private ArrayList<String> tasks;
   private final String filePath = "todo.csv";
    public ToDoList() {
         this.tasks = new ArrayList<>();
            loadFromFile();
    }

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            return; 
        }

        try (java.util.Scanner fileScanner = new java.util.Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                this.tasks.add(line);
            }
        } catch (Exception e) {
            System.out.println("Error loading tasks from file: " + e.getMessage());
        }
        
        
    }
    public void add(String task) {
        this.tasks.add(task);
        updateFile();
    }

    public void print(){
        for (String job: tasks){
            System.out.println(this.tasks.indexOf(job)+ 1 + ":" + job);    
        }
    }

    public void remove(int number){
        this.tasks.remove(number - 1);
        updateFile();
    }

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public String getTask(int id){
        if (id < 1 || id > tasks.size()) {
            return null; 
        }
        return tasks.get(id);
    }

    private boolean updateFile() {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(filePath)) {
            for (String task : tasks) {
                writer.println(task);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkEventString(String value) {
        if (value == null) {
            return false;
        }
        if (value.length() < 3) {
            return false;
        }
        return value.matches("^[\\p{L}\\d ]+$");
    }

      
}