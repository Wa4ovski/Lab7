import util.*;


public class Main {
    public static void main(String args[]){

        CollectionManager cm = new CollectionManager();
        CommandProcessor cp = new CommandProcessor(cm);
        System.exit(cp.launch());

    }

}








