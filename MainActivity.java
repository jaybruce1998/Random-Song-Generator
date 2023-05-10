package jay.randomsong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    YouTubePlayer player;
    YouTubePlayerView youTubePlayerView;
    Button generateButton, showButton;
    TextView textView;
    String name;
    private String urlContent(String URL)
    {
        InputStreamReader i=null;
        String r="";
        try
        {
            URL url=new URL(URL);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setConnectTimeout(5000);
            huc.setRequestMethod("GET");
            huc.connect();
            i=new InputStreamReader(huc.getInputStream());
            BufferedReader b=new BufferedReader(i);
            for(String l=b.readLine(); l!=null; l=b.readLine())
                r+=l;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                i.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return r;
    }
    public void generateContent(View v)
    {
        textView.setText("Loading...");
        youTubePlayerView.setVisibility(View.GONE);
        generateButton.setVisibility(View.GONE);
        showButton.setVisibility(View.GONE);
        new Thread(){
            public void run() {
                name=urlContent("https://en.wikipedia.org/wiki/Special:Random").split("title>")[1]
                        .split(" - Wikipedia")[0];
                String video=urlContent("https://www.google.com/search?q="+name+"+youtube+song&tbm=vid")
                        .split("watch\\?v=")[1].split("\"")[0];
                player.pause();
                player.loadVideo(video, 0);
                player.play();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        generateButton.setVisibility(View.VISIBLE);
                        showButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        }.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        generateButton=findViewById(R.id.generateNewVideo);
        showButton=findViewById(R.id.showVideo);
        textView=findViewById(R.id.text);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                player=youTubePlayer;
                generateContent(null);
            }
        });
    }
    public void show(View v)
    {
        showButton.setVisibility(View.INVISIBLE);
        youTubePlayerView.setVisibility(View.VISIBLE);
        textView.setText("Search: "+name);
    }
}