package engine;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;

public class SoundPlayer {

    public static Music start;
    public static Music during;
    public static Music end;

    public static Music currentlyPlaying = null;

    /**
     * Initialize SoundPlayer with various songs
     * @param start the song played at the menu
     * @param during the song played during the game
     * @param end the song played when one side wins
     */
    public static void init(String start, String during, String end){
        TinySound.init();
        SoundPlayer.start = TinySound.loadMusic(start);
        SoundPlayer.during = TinySound.loadMusic(during);
        SoundPlayer.end = TinySound.loadMusic(end);
    }

    /**
     * plays the toPlay file
     * @param toPlay - the file to play, it should be a variable from SoundPlayer
     * @implNote If we play an already playing sound, the behavior isn't defined(yet), so try to only play at the start
     */
    public static void play(Music toPlay){
        currentlyPlaying = toPlay;
        // currentlyPlaying.play(true);
    }

    /**
     * stops playing the current file
     */
    public static void stop(){
        currentlyPlaying.stop();
    }

}
