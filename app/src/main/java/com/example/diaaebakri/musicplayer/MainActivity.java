    package com.example.diaaebakri.musicplayer;

    import android.content.SharedPreferences;
    import android.database.Cursor;
    import android.media.MediaPlayer;
    import android.provider.MediaStore;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.TextView;
    import java.io.IOException;


    public class MainActivity extends AppCompatActivity{

            MediaPlayer mp;
            Cursor cursor;
            TextView songTitle;
            TextView songArtist;
            TextView songAlbum;
            Button playback;
            TextView position;
            SharedPreferences sp;

            static String title;
            static String artist;
            static String album;
            static String path;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                sp = getSharedPreferences("musicPlayer", MODE_PRIVATE);
                songTitle = findViewById(R.id.songTitle);
                songArtist = findViewById(R.id.songArtist);
                songAlbum = findViewById(R.id.songAlbum);
                playback = findViewById(R.id.pbButton);

                mp = new MediaPlayer();

                int lastPlayingPos = sp.getInt("lastPlayingPosition", 0);
                String lastTitle = sp.getString("mediaTitle", "");
                String lastArtist = sp.getString("mediaArtist", "");
                String lastAlbum = sp.getString("mediaAlbum", "");
                String lastPath = sp.getString("mediaPath", "");
                Boolean lastPlayed = sp.getBoolean("lastPlayed", false);
                if(!lastPlayed){
                    getNewSong();
                }else{
                    try {
                        songTitle.setText(lastTitle);
                        songArtist.setText(lastArtist);
                        songAlbum.setText(lastAlbum);
                        mp.setDataSource(lastPath);
                        mp.prepare();
                        mp.seekTo(lastPlayingPos);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            protected  void onPause(){
                super.onPause();
                updateLastPlayingPosition();
            }

            @Override
            protected void onStop(){
                super.onStop();
            }

            public void requestNewSong(View view) {
                getNewSong();
            }

            public void getNewSong(){
                cursor = getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null, null, null,
                        " RANDOM() LIMIT 1");

                while(cursor.moveToNext()) {
                    title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    sp.edit().putString("mediaTitle", title).apply();
                    artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    sp.edit().putString("mediaArtist", artist).apply();
                    album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    sp.edit().putString("mediaAlbum", album).apply();
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    sp.edit().putString("mediaPath", path).apply();

                    songTitle.setText(title);
                    songArtist.setText(artist);
                    songAlbum.setText(album);

                    play(path);
                }

            }

            public void play(String path){
                try{
                    if(mp.isPlaying()) mp.pause();
                    mp.reset();
                    mp.setDataSource(path);
                    mp.prepare();
                }catch(IOException e){
                    // idk
                }
            }

            public void onPlayback(View view) {
                if (!mp.isPlaying()) {
                    mp.start();
                } else {
                    mp.pause();
                }
            }

            private void updateLastPlayingPosition(){
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sp.edit().putInt("lastPlayingPosition", mp.getCurrentPosition()).apply();
                    }
                });
            }
        }
