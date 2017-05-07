import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

/**
 * Created by Alex on 19.04.2017.
 */
public class Main {

    public static void learn(){
        NeuronNetwork network = NeuronNetwork.getInstance();
        network.createNetwork();
        //network.loadNetwork();
        network.learning();
        network.saveNetwork();
        network.shutdown();
    }

    public static void runClientThread(Connector connector){
        ClientThread thread = new ClientThread();
        thread.setConnection(connector);
        thread.test();
    }

    public static void main(String args[]){

        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose mode. 1 - learning, 2 - waiting for client");
        String temp = scanner.next();
        if (temp.equalsIgnoreCase("1")) learn();
        else {
            Connector connector = new Connector();
            while (true){
                connector.waitClient();
                System.out.println("New client");
                while (connector.haveClient){
                    try {
                        String line = connector.receiveCommand();
                        if (line==null) break;
                        System.out.println(line);
                        switch (line.toUpperCase()){
                            case "TEST" : runClientThread(connector); break;
                            case "QUIT"  : connector.closeConnection(); break;
                            default: break;
                        }
                    } catch (SocketException e){
                        System.out.println("Connection reset");
                    } catch (IOException e) {
                        //e.printStackTrace();
                        break;
                    }
                }
                System.out.println("Client exit. Wait new Client?");
                temp = scanner.next();
                if (!temp.equalsIgnoreCase("Y")) break;
            }

        }

    }
}
