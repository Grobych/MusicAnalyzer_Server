import com.google.gson.Gson;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Alex on 21.04.2017.
 */
public class Connector {
    ServerSocket serverSocket;
    Socket clientSocket;
    boolean haveClient = false;
    InetAddress IPServer, IPClient;
    final int port = 19999;
    Gson gson;
    BufferedReader in;
    PrintWriter out;
    ObjectInputStream objectIn;
    ObjectOutputStream objectOut;

    public Connector(){
        try {
            serverSocket = new ServerSocket(port);
            IPServer = InetAddress.getLocalHost();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waitClient()
    {
        try {
            System.out.print("Waiting for a client...");
            clientSocket= serverSocket.accept();
            IPClient =  clientSocket.getInetAddress();

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(),true);
            objectIn = new ObjectInputStream(clientSocket.getInputStream());
            objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            gson = new Gson();
            haveClient=true;
            System.out.println("Client connected");
        } catch (IOException e) {
            System.out.println("Can't accept");
            System.exit(-1);
        }
    }

    public void closeConnection() throws IOException {
        haveClient = false;
        out.close();
        in.close();
        clientSocket.close();
    }

    public void send(String line){
        out.println(line);
        out.flush();
    }
    public String receive(){
        String result = null;
        try {
            result = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

//    public boolean sendCommand(String command){
//        send(command);
//        String answer = receive();
//        if (answer.compareTo("GOT_"+command)==0) return true;
//        else return false;
//    }
    public String receiveCommand(){
        String result = null;
        try {
            result = in.readLine();
            send("GOT_"+result);
        } catch (IOException e) {
            return result;
        } finally {
            return result;
        }
    }

    public void sendArray(double array[]){

        String line = gson.toJson(array);
        out.println(line);
        out.flush();
    }
    public double[] receiveArray(){
        try {
            String line = in.readLine();
            double result[] = gson.fromJson(line,double[].class);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public void sendDouble(double value){
        String line = gson.toJson(value);
        out.println(line);
        out.flush();
    }

    public double receiveDouble(){
        try {
            String line = in.readLine();
            return gson.fromJson(line,double.class);
        } catch (IOException e) {
            e.printStackTrace();
            return Double.parseDouble(null);
        }
    }

}
