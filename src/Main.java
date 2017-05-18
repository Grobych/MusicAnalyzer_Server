import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alex on 19.04.2017.
 */
public class Main {

    static List<Thread> threadList = new ArrayList<>();



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

    public static Socket waitClient(ServerSocket serverSocket)
    {
        Socket clientSocket;
        try {
            System.out.print("Waiting for a client...");
            clientSocket= serverSocket.accept();

            System.out.println("Client connected");
        } catch (IOException e) {
            System.out.println("Can't accept");
            return null;
        }
        return clientSocket;
    }

    public static void checkThreads()
    {
        for (int i =0; i<threadList.size();i++) {
            Thread tmp = threadList.get(i);
            if (!tmp.isAlive()) threadList.remove(i);
        }
    }

    public static void main(String args[]){

        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose mode. 1 - learning, 2 - waiting for client");
        String temp = scanner.next();
        if (temp.equalsIgnoreCase("1")) learn();
        else {
            try {
                ServerSocket serverSocket = new ServerSocket(19999);
                serverSocket.setSoTimeout(30000);
                while (true){
                    System.out.println("Main loop");
                    if (threadList.size()<=50){
                        Socket client = waitClient(serverSocket);
                        if (client!=null){
                            System.out.println("New client");
                            ClientThread clientThread = new ClientThread();
                            Connector connector = new Connector();
                            connector.setSocket(client);
                            clientThread.setConnection(connector);
                            Thread thread = new Thread(clientThread);
                            thread.start();
                            threadList.add(thread);
                        }
                        checkThreads();
                    } else {
                        System.out.println("Too many threads!!!");
                        try {
                            TimeUnit.MILLISECONDS.sleep(20000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        checkThreads();
                    }
                    if (threadList.size()==0)
                    {
                        System.out.println("No more clients. Wait? Y/N");
                        String answer = scanner.next();
                        if (answer.toUpperCase().equals("Y"))
                        {
                            System.out.println("Waiting...");
                        }
                        else
                        {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
