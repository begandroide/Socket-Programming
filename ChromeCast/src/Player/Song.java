package Player;


public class Song {
    public int clientId;
    public String nameSong;
    public String author;
    public int seconds;

    public Song(String nameSong, String author, int seconds, int clientId){
        this.clientId = clientId;
        this.nameSong = nameSong;
        this.author = author;
        this.seconds = seconds;
    }

    @Override
    public String toString(){
        return "{Client:"+clientId+","+nameSong+"-"+author+"-"+seconds+"[s]}";
    }
}