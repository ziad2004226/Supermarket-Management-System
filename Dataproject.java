/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataproject;

import java.util.Scanner;
import java.util.Stack;
import java.util.LinkedList;
import java.util.Queue;
import java.io.*;
import java.util.ArrayList;

public class Dataproject {
    public static void main(String[] args) {
        SupermarketManagementSystem system = new SupermarketManagementSystem();
        system.start();
    }
}

class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String description;
    private String category;
    private String priority;

    public Item(int id, String name, String description, String category, String priority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.priority = priority;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getPriority() { return priority; }
    
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setPriority(String priority) { this.priority = priority; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Category: " + category + 
               ", Priority: " + priority + "\nDescription: " + description;
    }
}

class ItemLinkedList {
    private Node head;

    class Node {
        Item data;
        Node next;

        Node(Item data) {
            this.data = data;
            this.next = null;
        }
        
        public Item getData() {
            return data;
        }
        
        public Node getNext() {
            return next;
        }
    }

    public Node getHead() {
        return head;
    }

    public void addItem(Item item) {
        Node newNode = new Node(item);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    public boolean deleteItem(int itemId) {
        if (head == null) return false;

        if (head.data.getId() == itemId) {
            head = head.next;
            return true;
        }

        Node current = head;
        while (current.next != null) {
            if (current.next.data.getId() == itemId) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void displayItems() {
        Node current = head;
        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
    }
    
    public Item getItem(int itemId) {
        Node current = head;
        while (current != null) {
            if (current.data.getId() == itemId) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }
}

class ItemBST {
    private Node root;

    private class Node {
        Item item;
        Node left, right;

        Node(Item item) {
            this.item = item;
            left = right = null;
        }
    }

    public void insert(Item item) {
        root = insertRec(root, item);
    }

    private Node insertRec(Node root, Item item) {
        if (root == null) {
            root = new Node(item);
            return root;
        }

        if (item.getId() < root.item.getId()) {
            root.left = insertRec(root.left, item);
        } else if (item.getId() > root.item.getId()) {
            root.right = insertRec(root.right, item);
        }

        return root;
    }

    public Item search(int itemId) {
        return searchRec(root, itemId);
    }

    private Item searchRec(Node root, int itemId) {
        if (root == null || root.item.getId() == itemId) {
            return root == null ? null : root.item;
        }

        if (itemId < root.item.getId()) {
            return searchRec(root.left, itemId);
        }

        return searchRec(root.right, itemId);
    }
    
    public boolean delete(int itemId) {
        if (root == null) return false;
        root = deleteRec(root, itemId);
        return true;
    }
    
    private Node deleteRec(Node root, int itemId) {
        if (root == null) return root;

        if (itemId < root.item.getId()) {
            root.left = deleteRec(root.left, itemId);
        } else if (itemId > root.item.getId()) {
            root.right = deleteRec(root.right, itemId);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            
            root.item = minValue(root.right);
            root.right = deleteRec(root.right, root.item.getId());
        }
        return root;
    }
    
    private Item minValue(Node root) {
        Item minv = root.item;
        while (root.left != null) {
            minv = root.left.item;
            root = root.left;
        }
        return minv;
    }
}

class ItemPriorityQueue {
    private Queue<Item> urgentQueue;
    private Queue<Item> normalQueue;

    public ItemPriorityQueue() {
        urgentQueue = new LinkedList<>();
        normalQueue = new LinkedList<>();
    }

    public void enqueue(Item item) {
        if (item.getPriority().equals("urgent")) {
            urgentQueue.add(item);
        } else {
            normalQueue.add(item);
        }
    }

    public Item dequeue() {
        if (!urgentQueue.isEmpty()) {
            return urgentQueue.poll();
        } else if (!normalQueue.isEmpty()) {
            return normalQueue.poll();
        }
        return null;
    }
    
    public boolean remove(Item item) {
        if (item.getPriority().equals("urgent")) {
            return urgentQueue.remove(item);
        } else {
            return normalQueue.remove(item);
        }
    }
}

class ItemUndoStack {
    private Stack<Item> stack;

    public ItemUndoStack() {
        stack = new Stack<>();
    }

    public void push(Item item) {
        stack.push(item);
    }

    public Item pop() {
        return stack.isEmpty() ? null : stack.pop();
    }
}

class SupermarketManagementSystem {
    private ItemLinkedList itemList;
    private ItemBST itemBST;
    private ItemPriorityQueue priorityQueue;
    private ItemUndoStack undoStack;
    private Scanner scanner;
    private static final String DATA_FILE = "supermarket_data.ser";

    public SupermarketManagementSystem() {
        itemList = new ItemLinkedList();
        itemBST = new ItemBST();
        priorityQueue = new ItemPriorityQueue();
        undoStack = new ItemUndoStack();
        scanner = new Scanner(System.in);
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            ArrayList<Item> items = new ArrayList<>();
            // Convert linked list to array list for saving
            ItemLinkedList.Node current = itemList.getHead();
            while (current != null) {
                items.add(current.getData());
                current = current.getNext();
            }
            oos.writeObject(items);
            System.out.println("Data saved successfully to " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            // Clear existing data structures
            itemList = new ItemLinkedList();
            itemBST = new ItemBST();
            priorityQueue = new ItemPriorityQueue();
            undoStack = new ItemUndoStack();

            @SuppressWarnings("unchecked")
            ArrayList<Item> items = (ArrayList<Item>) ois.readObject();
            
            // Populate all data structures
            for (Item item : items) {
                itemList.addItem(item);
                itemBST.insert(item);
                priorityQueue.enqueue(item);
            }
            System.out.println("Data loaded successfully from " + DATA_FILE);
        } catch (FileNotFoundException e) {
            System.out.println("No saved data found.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    public void start() {
        System.out.println("=== Supermarket Management System ===");
        System.out.println("Developed by [Your Name]");
        System.out.println("Using Data Structures: LinkedList, BST, PriorityQueue, Stack\n");
        
        // Load data when starting
        loadFromFile();
        
        while (true) {
            System.out.println("\nMain Menu");
            System.out.println("1. Add Item");
            System.out.println("2. View All Items");
            System.out.println("3. Update Item");
            System.out.println("4. Delete Item");
            System.out.println("5. Undo Delete");
            System.out.println("6. Process Next Item by Priority");
            System.out.println("7. Search Item by ID");
            System.out.println("8. Save Data to File");
            System.out.println("9. Load Data from File");
            System.out.println("10. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addItem();
                    break;
                case 2:
                    viewAllItems();
                    break;
                case 3:
                    updateItem();
                    break;
                case 4:
                    deleteItem();
                    break;
                case 5:
                    undoDelete();
                    break;
                case 6:
                    processNextItem();
                    break;
                case 7:
                    searchItem();
                    break;
                case 8:
                    saveToFile();
                    break;
                case 9:
                    loadFromFile();
                    break;
                case 10:
                    System.out.println("Saving data before exit...");
                    saveToFile();
                    System.out.println("Exiting system...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void addItem() {
        System.out.println("\nAdd New Item");
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        if (itemBST.search(id) != null) {
            System.out.println("Item with this ID already exists!");
            return;
        }
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Description: ");
        String description = scanner.nextLine();
        
        System.out.print("Enter Category: ");
        String category = scanner.nextLine();
        
        String priority;
        while (true) {
            System.out.print("Enter Priority (urgent/normal): ");
            priority = scanner.nextLine().toLowerCase();
            if (priority.equals("urgent") || priority.equals("normal")) {
                break;
            }
            System.out.println("Invalid priority. Please enter 'urgent' or 'normal'.");
        }

        Item newItem = new Item(id, name, description, category, priority);
        itemList.addItem(newItem);
        itemBST.insert(newItem);
        priorityQueue.enqueue(newItem);
        
        System.out.println("Item added successfully!");
    }

    private void viewAllItems() {
        System.out.println("\nAll Items in the System:");
        itemList.displayItems();
    }

    private void updateItem() {
        System.out.print("\nEnter Item ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        Item item = itemBST.search(id);
        if (item == null) {
            System.out.println("Item not found!");
            return;
        }
        
        System.out.println("Current Item Details:");
        System.out.println(item);
        
        System.out.print("Enter new Name (leave blank to keep current): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) item.setName(name);
        
        System.out.print("Enter new Description (leave blank to keep current): ");
        String description = scanner.nextLine();
        if (!description.isEmpty()) item.setDescription(description);
        
        System.out.print("Enter new Category (leave blank to keep current): ");
        String category = scanner.nextLine();
        if (!category.isEmpty()) item.setCategory(category);
        
        String priority;
        while (true) {
            System.out.print("Enter new Priority (urgent/normal, leave blank to keep current): ");
            priority = scanner.nextLine().toLowerCase();
            if (priority.isEmpty()) break;
            if (priority.equals("urgent") || priority.equals("normal")) {
                item.setPriority(priority);
                break;
            }
            System.out.println("Invalid priority. Please enter 'urgent' or 'normal'.");
        }
        
        System.out.println("Item updated successfully!");
    }

    private void deleteItem() {
        System.out.print("\nEnter Item ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        Item item = itemBST.search(id);
        if (item == null) {
            System.out.println("Item not found!");
            return;
        }
        
        undoStack.push(item);
        itemList.deleteItem(id);
        itemBST.delete(id);
        priorityQueue.remove(item);
        System.out.println("Item deleted successfully!");
    }

    private void undoDelete() {
        Item item = undoStack.pop();
        if (item == null) {
            System.out.println("Nothing to undo!");
            return;
        }
        
        itemList.addItem(item);
        itemBST.insert(item);
        priorityQueue.enqueue(item);
        System.out.println("Undo successful! Item restored:");
        System.out.println(item);
    }

    private void processNextItem() {
        Item item = priorityQueue.dequeue();
        if (item == null) {
            System.out.println("No items in queue!");
            return;
        }
        
        // Remove from other data structures for consistency
        itemList.deleteItem(item.getId());
        itemBST.delete(item.getId());
        
        System.out.println("\nProcessing next item by priority:");
        System.out.println(item);
    }

    private void searchItem() {
        System.out.print("\nEnter Item ID to search: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        Item item = itemBST.search(id);
        if (item == null) {
            System.out.println("Item not found!");
        } else {
            System.out.println("Item found:");
            System.out.println(item);
        }
    }
}