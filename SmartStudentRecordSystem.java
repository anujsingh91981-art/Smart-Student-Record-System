import java.io.*;
import java.util.*;

public class SmartStudentRecordSystem {

    static class Student {
        String id;
        String name;
        int marks;
        Student next;
        Student(String id, String name, int marks) {
            this.id = id; this.name = name; this.marks = marks;
        }
        public String toString() { return id + " | " + name + " | " + marks; }
    }

    static class BSTNode {
        Student data;
        BSTNode left, right;
        BSTNode(Student s) { data = s; }
    }

    static final String FILE_NAME = "student_records.txt";
    static Student head = null;           
    static BSTNode root = null;           
    static int count = 0;
    static int nextIdNumber = 101;        
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadRecords();
        while (true) {
            System.out.println("\n===== SMART STUDENT RECORD SYSTEM =====");
            System.out.println("1. Add student");
            System.out.println("2. Display all students");
            System.out.println("3. Search by ID");
            System.out.println("4. Search by name (BST)");
            System.out.println("5. Delete student");
            System.out.println("6. Rank list (sort by marks)");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");
            int choice = readInt();
            switch (choice) {
                case 1: addStudent(); break;
                case 2: displayAll(); break;
                case 3: searchById(); break;
                case 4: searchByName(); break;
                case 5: deleteStudent(); break;
                case 6: rankList(); break;
                case 7: saveRecords(); System.out.println("Records saved. Goodbye!"); return;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static String readLine() {
        return sc.hasNextLine() ? sc.nextLine().trim() : null;
    }

    static int readInt() {
        String line = readLine();
        if (line == null) { saveRecords(); System.exit(0); }
        try { return Integer.parseInt(line); }
        catch (NumberFormatException e) { return Integer.MIN_VALUE; }
    }

    static void addStudent() {
        System.out.print("Enter name: ");
        String name = readLine();
        if (name == null || name.isEmpty()) { System.out.println("Name cannot be empty."); return; }
        System.out.print("Enter marks (0-100): ");
        int marks = readInt();
        if (marks < 0 || marks > 100) {
            System.out.println("Invalid marks. Marks must be between 0 and 100.");
            return;
        }
        String id = "S" + nextIdNumber;
        if (findById(id) != null) {                      
            System.out.println("Duplicate ID " + id + " rejected.");
            return;
        }
        nextIdNumber++;
        Student s = new Student(id, name, marks);
        insertList(s);
        root = insertBST(root, s);
        count++;
        System.out.println("Student added. ID: " + s);
    }

    static void insertList(Student s) {                   
        if (head == null) { head = s; return; }
        Student t = head;
        while (t.next != null) t = t.next;
        t.next = s;
    }

    static BSTNode insertBST(BSTNode node, Student s) {
        if (node == null) return new BSTNode(s);
        if (s.name.compareToIgnoreCase(node.data.name) < 0)
            node.left = insertBST(node.left, s);
        else
            node.right = insertBST(node.right, s);
        return node;
    }

    static Student searchBST(BSTNode node, String name) {
        if (node == null) return null;
        int c = name.compareToIgnoreCase(node.data.name);
        if (c == 0) return node.data;
        return c < 0 ? searchBST(node.left, name) : searchBST(node.right, name);
    }

    static BSTNode deleteBST(BSTNode node, Student s) {
        if (node == null) return null;
        int c = s.name.compareToIgnoreCase(node.data.name);
        if (c < 0) node.left = deleteBST(node.left, s);
        else if (c > 0) node.right = deleteBST(node.right, s);
        else if (node.data != s) node.right = deleteBST(node.right, s);   
        else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            BSTNode min = node.right;                                     
            while (min.left != null) min = min.left;
            node.data = min.data;
            node.right = deleteBST(node.right, min.data);
        }
        return node;
    }

    static void displayAll() {
        if (head == null) { System.out.println("No student records."); return; }
        System.out.println("ID   | Name | Marks");
        for (Student t = head; t != null; t = t.next) System.out.println(t);
    }

    static Student findById(String id) {
        for (Student t = head; t != null; t = t.next)
            if (t.id.equalsIgnoreCase(id)) return t;
        return null;
    }

    static void searchById() {
        System.out.print("Enter student ID: ");
        String id = readLine();
        Student s = (id == null) ? null : findById(id);
        System.out.println(s == null ? "Student " + id + " not found." : s.toString());
    }

    static void searchByName() {
        System.out.print("Enter student name: ");
        String name = readLine();
        Student s = (name == null) ? null : searchBST(root, name);
        System.out.println(s == null ? "Student " + name + " not found." : s.toString());
    }

    static void deleteStudent() {
        System.out.print("Enter student ID to delete: ");
        String id = readLine();
        Student s = (id == null) ? null : findById(id);
        if (s == null) { System.out.println("Student " + id + " not found."); return; }
        if (head == s) head = s.next;
        else {
            Student t = head;
            while (t.next != s) t = t.next;
            t.next = s.next;
        }
        root = deleteBST(root, s);
        count--;
        System.out.println("Student deleted. ID: " + s.id);
    }

    static void rankList() {
        if (count == 0) { System.out.println("No student records."); return; }
        Student[] arr = new Student[count];
        int i = 0;
        for (Student t = head; t != null; t = t.next) arr[i++] = t;
        mergeSort(arr, 0, arr.length - 1);
        for (i = 0; i < arr.length; i++) System.out.println((i + 1) + ". " + arr[i]);
    }

    static void mergeSort(Student[] a, int lo, int hi) {
        if (lo >= hi) return;
        int mid = (lo + hi) / 2;
        mergeSort(a, lo, mid);
        mergeSort(a, mid + 1, hi);
        merge(a, lo, mid, hi);
    }

    static void merge(Student[] a, int lo, int mid, int hi) {
        Student[] tmp = new Student[hi - lo + 1];
        int i = lo, j = mid + 1, k = 0;
        while (i <= mid && j <= hi)
            tmp[k++] = (a[i].marks >= a[j].marks) ? a[i++] : a[j++];   
        while (i <= mid) tmp[k++] = a[i++];
        while (j <= hi) tmp[k++] = a[j++];
        for (k = 0; k < tmp.length; k++) a[lo + k] = tmp[k];
    }

   
    static void loadRecords() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length != 3) continue;
                try {
                    int marks = Integer.parseInt(p[2].trim());
                    if (marks < 0 || marks > 100 || findById(p[0].trim()) != null) continue;
                    Student s = new Student(p[0].trim(), p[1].trim(), marks);
                    insertList(s);
                    root = insertBST(root, s); 
                    count++;
                    int num = Integer.parseInt(s.id.substring(1));
                    if (num >= nextIdNumber) nextIdNumber = num + 1;
                } catch (NumberFormatException e) { /* skip bad line */ }
            }
            System.out.println(count + " record(s) loaded from " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Could not read file: " + e.getMessage());
        }
    }

    static void saveRecords() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Student t = head; t != null; t = t.next)
                pw.println(t.id + "," + t.name + "," + t.marks);
        } catch (IOException e) {
            System.out.println("Could not save file: " + e.getMessage());
        }
    }
}
