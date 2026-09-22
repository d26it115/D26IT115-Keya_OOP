public abstract class Shape_area{
    abstract double area();
}
class Circle extends Shape_area{
    double rad;
    Circle(double rad){
        this.rad=rad;
    }
    public double area(){
        return (3.15*rad*rad);
    }
}
class Rectangle extends Shape_area{
    double len,wid;
    Rectangle(double len, double wid){
        this.len=len;
        this.wid=wid;
    }
    public double area(){
        return (len*wid);
    }
}
class Triangle extends Shape_area{
    double base,height;
    Triangle(double base, double height){
        this.base=base;
        this.height=height;
    }
    public double area(){
        return (0.5*base*height);
    }
}
public class Shape{
    public static void main(String[] args) {
        Shape []shapes={
                Circle(2),
                Triangle(2,3),
                Rectangle(4,5);
        }
        for(int i=0;i< shapes.length;i++){
            double a=shapes[i].area();

            double total=0;
            double largest=0;

            total=total+a;
            if(a>largest)
                largest=a;
        }
        System.out.println("Largest:"+largest);
        System.out.println("Total="+total);
    }
}