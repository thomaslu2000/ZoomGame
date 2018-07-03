package corksproductions.SpeederBall;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    static ArrayList<MediaPlayer> mediaArray = new ArrayList<>();
    static MediaPlayer music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size); //This stuff gets the size of the screen, so size.x is the screen's width, size.y is height

        music=MediaPlayer.create(this, R.raw.music);

        music.setVolume(0.6f,0.6f);

        mediaArray.add(MediaPlayer.create(this, R.raw.hit));
        mediaArray.add(MediaPlayer.create(this,R.raw.powerup));
        mediaArray.add(MediaPlayer.create(this,R.raw.win));
        mediaArray.add(MediaPlayer.create(this,R.raw.lose));
        for(MediaPlayer a : mediaArray){
            a.setVolume(0.2f,0.2f);
        }

        gameView = new GameView(this, size.x, size.y); //makes the gameView

        setContentView(gameView);//Lets us see the view
    }

    @Override
    protected void onPause() { //pausing
        super.onPause();
        gameView.pause();
        if (music.isPlaying()) {
            music.stop();
            try {
                music.prepare();
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onResume() { //resuming
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (music.isPlaying()) {
            music.stop();
            try {
                music.prepare();
            } catch (Exception e) {
            }
        }
    }

    static String[] placeStr= {"First","Second","Third","Fourth","Fifth","Sixth","Seventh","Eighth","Ninth","Tenth"};
    public static String wordPlace(int a) {
        try{
            return placeStr[a-1];
        } catch (IndexOutOfBoundsException e){
            return "";
        }
    }
}
