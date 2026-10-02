abstract class Employee {

    String name;
    int id;

    Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    abstract double monthlySalary();
}

class FullTime extends Employee {

    double fixedSalary;

    FullTime(String name, int id, double fixedSalary) {
        super(name, id);
        this.fixedSalary = fixedSalary;
    }

    @Override
    double monthlySalary() {
        return fixedSalary;
    }
}

class PartTime extends Employee {

    int hours;
    double rate;

    PartTime(String name, int id, int hours, double rate) {
        super(name, id);
        this.hours = hours;
        this.rate = rate;
    }

    @Override
    double monthlySalary() {
        return hours * rate;
    }
}

class Intern extends Employee {

    double stipend;

    Intern(String name, int id, double stipend) {
        super(name, id);
        this.stipend = stipend;
    }

    @Override
    double monthlySalary() {
        return stipend;
    }
}

public class Payroll {

    public static void main(String[] args) {

        Employee[] employees = {
                new FullTime("Keya", 101, 50000),
                new PartTime("Hardi", 102, 80, 300),
                new Intern("Dhyanam", 103, 15000),
                new FullTime("Yanna", 104, 60000),
                new PartTime("Janvi", 105, 60, 400)
        };

        double total = 0;

        for (Employee employee : employees) {

            double salary = employee.monthlySalary();

            System.out.printf(
                    "Name: %s, ID: %d, Salary: %.2f",
                    employee.name,
                    employee.id,
                    salary
            );

            // Add note only for interns
            if (employee instanceof Intern) {
                System.out.print(" (Intern - Stipend)");
            }

            System.out.println();

            total += salary;
        }

        System.out.printf("%nTotal Payroll: %.2f%n", total);
    }
}