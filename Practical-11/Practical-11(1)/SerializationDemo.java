import java.io.*;

class Student implements Serializable{
    int rollno;
    String name;
    transient String password;

    Student(int rollno, String name, String password){
        this.rollno=rollno;
        this.name=name;
        this.password=password;
    }
    void display(){
        System.out.println("Rollno: "+rollno);
        System.out.println("Name: "+name);
        System.out.println("Password: "+password);
        System.out.println();
    }
}

public class SerializationDemo{
    public static void main(String[] args) {
        Student[] students={
                new Student(101, "hardi", "abc123"),
                new Student(102, "kinal", "xyz456"),
                new Student(103, "dhyanam", "pqr789")
        };

        try{
            ObjectOutputStream out=
                    new ObjectOutputStream(new FileOutputStream("students.dat"));

            out.writeObject(students);
            out.close();

            System.out.println("Objects saved successfully\n");
        }
        catch(IOException e){
            System.out.println("Error"+e);
        }

        try{
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream("students.dat"));

            Student[] loadedStudents= (Student[]) in.readObject();
            in.close();

            System.out.println("Objects loaded successfully\n");

            for(Student s: loadedStudents){
                s.display();
            }
        }
        catch(IOException | ClassNotFoundException e){
            System.out.println("Error"+e);
        }
    }
}