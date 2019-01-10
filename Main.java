import java.text.ParseException;
import java.util.Scanner;

/**
 * Created by samansadeghyan on 11/15/16.
 */
public class Main {


    public static void main(String[] args) throws ParseException{

        System.out.println("choose position :\n");
        System.out.println("1. server");
        System.out.println("2. client");
        System.out.println("3. exit");

        Scanner scan = new Scanner(System.in);
        int pos = scan.nextInt();

        if(pos == 1){
            Server server = new Server();
            server.run();
        }else if(pos == 2){
            System.out.println("please enter server IP address :");
            String serverIp = scan.next();
//            String serverIp = "192.168.1.105";
            Client client = new Client(serverIp);
            client.run();
        }else if (pos == 3){
            System.out.println("exited");
            System.exit(0);
        }else {
            System.out.println("wrong input!");
        }

    }

}
