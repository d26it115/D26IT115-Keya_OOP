public class Thermostat{
    private String location;
    private int temperature;
    private static final int MIN=16;
    private static final int MAX=30;
    private static int activeCount=0;

    Thermostat(String location,int startTemp){
      this.location=location;
      if(startTemp>=MIN && startTemp<=MAX){
        this.temperature=startTemp;
      }
      else{
       this.temperature=22;
      }
      activeCount++;
    }

    Thermostat(String location){
        this(location,22);
    }

    public void raise(){
        if(this.temperature<MAX){
            this.temperature++;
        }
        else{
            System.out.println("Temp already at MAX(30)");
        }
    }

    public void lower(){
        if(this.temperature>MIN){
            this.temperature--;
        }
        else{
            System.out.println("Already at minimum (16).");
        }
    }

    public int getTemperature(){
        return this.temperature;
    }

    public static int getActiveCount(){
        return activeCount;
    }

    public static void main(String args[]){
        Thermostat t1=new Thermostat("bedroom",25);
        Thermostat t2=new Thermostat("living room");

        System.out.println("Thermostat 1:"+t1.location);
        System.out.println("Starting temperature: "+t1.getTemperature());
                    
        for(int i=0;i<10;i++){
            t1.raise();
            System.out.println("Temperature:"+ t1.getTemperature());
        }

        for(int i=0;i<20;i++){
            t1.lower();
            System.out.println("Temperature:"+ t1.getTemperature());
        }
         System.out.println("Active Count:" +Thermostat.getActiveCount());
    }
}