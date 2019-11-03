package Protocol;


public class Song {
    public int id;
    public String nameSong;
    public String author;
    public int seconds;

    public Song(int id, String nameSong, String author, int seconds){
        this.id = id;
        this.nameSong = nameSong;
        this.author = author;
        this.seconds = seconds;
    }

    @Override
    public String toString(){
        return "id:"+id+","+nameSong+"-"+author+"-"+seconds+"[s]";
    }
}