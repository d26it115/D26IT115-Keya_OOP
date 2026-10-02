abstract class Shape {

    abstract double area();
}

class Circle extends Shape {

    double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }
}

class Rectangle extends Shape {

    double length;
    double width;

    Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    double area() {
        return length * width;
    }
}

class Triangle extends Shape {

    double base;
    double height;

    Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }

    @Override
    double area() {
        return 0.5 * base * height;
    }
}

public class ShapeDemo {

    public static void main(String[] args) {

        // Mix of different shapes
        Shape[] shapes = {
                new Circle(5),
                new Rectangle(4, 6),
                new Triangle(8, 5),
                new Circle(3),
                new Rectangle(10, 2)
        };

        double total = 0;
        double largest = 0;

        // One loop handles every shape through polymorphism
        for (Shape shape : shapes) {

            double currentArea = shape.area();

            System.out.printf("Area: %.2f%n", currentArea);

            total += currentArea;

            if (currentArea > largest) {
                largest = currentArea;
            }

            System.out.printf("Running Total: %.2f%n", total);
            System.out.println();
        }

        System.out.printf("Total Area: %.2f%n", total);
        System.out.printf("Largest Area: %.2f%n", largest);
    }
}