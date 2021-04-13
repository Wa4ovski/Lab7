import java.io.File;
import util.*;


public class Main {
    public static void main(String args[]){
//        Workers workers = new Workers();
//        //workers.setWorkers(new Worker(12);
//        Worker worker1 = new Worker( );
//        worker1.setCoordinates(new Coordinates(1.0,new Float(5.0)));
//        worker1.setSalary(12432);
//        worker1.setId(new Long(123));
//        worker1.setName("fcd");
//        Worker worker2 = new Worker();
//        worker2.setCoordinates(new Coordinates(12.0,new Float(3.0)));
//        worker2.setSalary(12432);
//        worker2.setId(new Long(4321));
//        worker2.setName("ded");
//        LinkedHashSet<Worker> w = new LinkedHashSet<>();
//        w.add(worker1);
//        w.add(worker2);
//        workers.setWorkers(w);
//        System.out.println(worker1.toString());
        //LocalDateTime dateTime = new LocalDateTime(12.09.12);
//        File file = new File("");
//        String path = "" + file.getAbsolutePath() + "//src//xmlStorage.xml";
//        System.out.println(path);
//        Date t = new Date();
//        System.out.println(t);
//        String s = ""+t.getYear()+"-"+t.getMonth()+"-"+t.getDate();
//        System.out.println(s);
        CollectionManager cm = new CollectionManager();
        CommandProcessor cp = new CommandProcessor(cm);
        System.exit(cp.launch());

//        Long X = 1234565l;
//        System.out.printf("dfe %d", X);
//        Worker w = new Worker(Updater.updateName(),
//                Updater.updateCoordinates(),
//                Updater.updateSalary(),
//                Updater.updateStartDate(),
//                Updater.updateEndDate(),
//                Updater.updateStatus(),
//                Updater.updatePerson());
//        System.out.println(w.toString());
//



    }

}








