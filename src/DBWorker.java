import data.ServerSong;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 20.04.2017.
 */
public class DBWorker {
    public static Connection connection;
    public static Statement statmt;
    public static ResultSet resSet;

    public static void connect() throws ClassNotFoundException, SQLException{
        connection = null;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:database.s3db");
        statmt = connection.createStatement();

        System.out.println("База Подключена!");
    }
    public static void closeDB() throws ClassNotFoundException, SQLException{
        if (connection!=null) connection.close();
        if (statmt!=null) statmt.close();
        if (resSet!=null) resSet.close();

        System.out.println("Соединения закрыты");
    }
    public static void showDB() throws ClassNotFoundException, SQLException
    {
        resSet = statmt.executeQuery("SELECT * FROM songs");

        while(resSet.next())
        {
            int id = resSet.getInt("id");
            String  name = resSet.getString("name");
            String  author = resSet.getString("author");
            boolean isAnalyzed = resSet.getBoolean("isAnalyzed");
            double rhythm = resSet.getDouble("rhythm");
            double emotional = resSet.getDouble("emotional");
            System.out.println( "ID = " + id );
            System.out.println( "name = " + name );
            System.out.println( "phone = " + author );
            System.out.println("Analyzed - "+isAnalyzed);
            System.out.println("Rhythm = "+rhythm);
            System.out.println("Emotional = "+emotional);
            System.out.println();
        }
    }

    public static void addSong(ServerSong song) throws SQLException {
        addSong(song.getName(),song.getArtist(),song.isAnalyzed(),song.getRhythm(),song.getEmotional());
    }

    public static void addSong(String name, String author, boolean isAnalyzed, double rhythm, double emotional) throws SQLException {
        if (checkSong(name,author)) return;
        String command = new String("INSERT INTO 'songs' ('name', 'author', 'isAnalyzed', 'rhythm', 'emotional') " +
                "VALUES (\""+name+"\", \""+author+"\", "+((isAnalyzed)? 1 : 0)+", "+rhythm+", "+emotional+"); ");
        //System.out.println(command);
        statmt.execute(command);
    }

    public static boolean checkSong(String name, String author) throws SQLException {
        String command = new String("SELECT * FROM 'songs' WHERE name = \""+name+"\" and author = \""+author+"\";");
        //System.out.println(command);
        resSet = statmt.executeQuery(command);
        if (resSet.next()) return true;
        else return false;
    }

    public static void updateSong(String name, String author, double rhythm, double emotional) throws SQLException {
        if (!checkSong(name,author)){
            addSong(name,author,true,rhythm,emotional);
        } else {
            String command = new String("update 'songs'\n" +
                    "set rhythm="+rhythm+", emotional="+emotional+"\n" +
                    "where name = \""+name+"\" and author = \""+author+"\";");
            //System.out.println(command);
            statmt.execute(command);
        }
    }

    public static void updateSong(ServerSong song) throws SQLException {
        updateSong(song.getName(),song.getArtist(),song.getRhythm(),song.getEmotional());
    }

    public static List<ServerSong> getSimilarSong(double rhythm, double emotional, double k) throws SQLException {
        List<ServerSong> result = new ArrayList<>();
        String command = new String("select * from songs\n" +
                "where abs(rhythm-("+rhythm+"))<"+k+" and abs(emotional-("+emotional+"))<"+k+";");
        resSet = statmt.executeQuery(command);
        while (resSet.next()){
            result.add(new ServerSong(
                    resSet.getString("name"),
                    resSet.getString("author"),
                    resSet.getBoolean("isAnalyzed"),
                    resSet.getDouble("rhythm"),
                    resSet.getDouble("emotional")
            ));
        }
        return result;
    }

}
