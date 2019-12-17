package Player;

import java.util.ArrayDeque;
import java.util.Deque;


public class Player {
    private Deque<Song> reproductionQueue;
    private int progress = 0;
    private int maxProgress = 0;

    public Player() {
        reproductionQueue = new ArrayDeque<Song>();
    }

    public Song getCurrentSong() {
        return this.reproductionQueue.element();
    }

    public void removeFirstElement() {
        this.reproductionQueue.removeFirst();
    }

    public void putInHead(Song song) {
        this.reproductionQueue.addFirst(song);
    }

    public void putInTail(Song song) {
        this.reproductionQueue.addLast(song);
    }

    public int getSizeReproductionQueue() {
        return this.reproductionQueue.size();
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void incrementProgress() {
        this.progress++;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public Boolean timeOut() {
        return (progress == maxProgress);
    }

    public void resetProgresses() {
        progress = 0;
        maxProgress = 0;
        if(reproductionQueue.size()>0){
            maxProgress = reproductionQueue.element().seconds;
        }
    }

    public Boolean next() {
        Boolean flag = false;

        if (reproductionQueue.size() > 0 && this.progress > 0) {
            this.removeFirstElement();
            this.resetProgresses();
            flag = true;
        }
        return flag;
    }

    public void jump(int position){
        if(reproductionQueue.size()>=position){
            for(int i = 0; i < position; i++){
                this.removeFirstElement();
            }
            this.resetProgresses();
        }
    }

    public void clearReproductionQueue(){
        reproductionQueue.clear();
    }

    public byte[] reproductionQueueToString(){
        return this.reproductionQueue.toString().getBytes();
    }
}