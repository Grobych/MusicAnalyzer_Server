import data.Params;
import data.ServerSong;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 02.05.2017.
 */
public class ClientThread {
    private Connector connection;

    //Map<ServerSong, Params> songParamsMap = new HashMap<>();
    List<ServerSong> songList = new ArrayList<>();
    List<Params> paramsList = new ArrayList<>();

    public void setConnection(Connector connection){
        this.connection = connection;
    }

    public double calculatePreference(String preference){
        double result = 0;
        switch (preference){
            case "rhythm":{
                for (ServerSong song : songList) {
                    result+=song.getRhythm();
                }
                break;
            }
            case "emotional":{
                for (ServerSong song : songList) {
                    result+=song.getEmotional();
                }
                break;
            }
            default: {
                System.out.println("Incorrect param!");
                return 0;
            }
        }
        result/=songList.size();
        return result;
    }

    private void loadSongFromClient(){
        String name = connection.receive();
        String author = connection.receive();
        System.out.println(name +" "+author);

        double RMS[] = connection.receiveArray();
        double MFCC[] = connection.receiveArray();

        double averageRMS = connection.receiveDouble();
        double averageDeltaRMS = connection.receiveDouble();
        double maxDeltaRMS = connection.receiveDouble();

        System.out.println(averageRMS);
        System.out.println(averageDeltaRMS);
        System.out.println(maxDeltaRMS);

        ServerSong song = new ServerSong(name,author,false);
        Params params = new Params(RMS,MFCC,averageRMS,averageDeltaRMS,maxDeltaRMS);
        songList.add(song);
        paramsList.add(params);
    }

    private void loadSongs(){
        while (true){
            String command = connection.receiveCommand();
            switch (command){
                case "NEXT" : loadSongFromClient(); break;
                case "END"  : {
                    System.out.println("END");
                    return;
                }
            }
        }
    }

    public void sendResultsToClient(double prefRhythm, double prefEmotional, List<ServerSong> list){
        connection.send("SUCCESS");
        connection.send(String.valueOf(prefRhythm));
        connection.send(String.valueOf(prefEmotional));
        for (ServerSong song : list) {
            connection.send("NEXT");
            connection.send(song.getName());
            connection.send(song.getArtist());
            connection.sendDouble(song.getEmotional());
            connection.sendDouble(song.getRhythm());
        }
        connection.send("END");
    }

    public void test(){
        if (connection.receiveCommand().compareTo("LOADSONGS")==0){
            loadSongs();
        } else System.out.println("SMTH wrong");
        System.out.println("DONE");
        System.out.println(songList.size());
        System.out.println(paramsList.size());


        NeuronNetwork network = NeuronNetwork.getInstance();
        network.loadNetwork();
        for (int i = 0; i < paramsList.size(); i++) {
            Params params = paramsList.get(i);
            double input[] = network.normalizeInput(params.getRMS(),params.getMFCC(),params.getAverageRMS(),params.getAverageDeltaRMS(),params.getMaxDeltaRMS());
            double result[] = network.calculate(input);
            songList.get(i).setEmotional(result[0]);
            songList.get(i).setRhythm(result[1]);
            songList.get(i).setAnalyzed(true);
        }

        double prefRhythm = calculatePreference("rhythm");
        double prefEmotional = calculatePreference("emotional");

        System.out.println("Preferred rhythm: "+prefRhythm+" Preferred emotional: "+prefEmotional);
        sendResultsToClient(prefRhythm,prefEmotional,songList);

        try {
            DBWorker.connect();
            List<ServerSong> list = DBWorker.getSimilarSong(prefRhythm, prefEmotional, 0.1);
            for (ServerSong song : songList) {
                list.remove(song);
            }
            if (list.size()==0) {
                System.out.println("No similar");
                connection.send("NO SIMILAR");
            } else {
                connection.send("SIMILAR");
                list.forEach(System.out::print);
                for (ServerSong song : list) {
                    connection.send(new String(song.getArtist()+" "+song.getName()));
                }
                connection.send("END");
            }
            for (ServerSong song : songList) {
                DBWorker.updateSong(song);
            }
            DBWorker.closeDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
