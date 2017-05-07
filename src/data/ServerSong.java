package data;


/**
 * Created by Alex on 12.01.2017.
 */
public class ServerSong {

    private String name;
    private String artist;

    private boolean isAnalyzed;

    private double rhythm;
    private double emotional;

    public ServerSong(String name, String author, boolean isAnalyzed, double rhythm, double emotional) {
        setName(name);
        setArtist(author);
        setEmotional(emotional);
        setRhythm(rhythm);
        setAnalyzed(isAnalyzed);
    }
    public ServerSong(String name, String artist, boolean isAnalyzed){
        setName(name);
        setArtist(artist);
        setAnalyzed(isAnalyzed);
    }
    public boolean isAnalyzed() {
        return isAnalyzed;
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

    public void setAnalyzed(boolean analyzed) {
        isAnalyzed = analyzed;
    }
}
