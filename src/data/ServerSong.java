package data;


/**
 * Created by Alex on 12.01.2017.
 */
public class ServerSong {

    private String name;
    private String artist;

    private double rhythm;
    private double emotional;

    public ServerSong(String name, String author, double rhythm, double emotional) {
        setName(name);
        setArtist(author);
        setEmotional(emotional);
        setRhythm(rhythm);
    }
    public ServerSong(String name, String artist){
        setName(name);
        setArtist(artist);
    }

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    public double getRhythm() {
        return rhythm;
    }

    public double getEmotional() {
        return emotional;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setEmotional(double emotional) {
        if (Math.abs(emotional)>1) {
            System.out.println("Incorrect param! "+emotional);
        }
        else {
            this.emotional = emotional;
        }
    }

    public void setRhythm(double rhythm) {
        if (Math.abs(rhythm)>1) {
            System.out.println("Incorrect param! "+rhythm);
        }
        else {
            this.rhythm = rhythm;
        }
    }

}
