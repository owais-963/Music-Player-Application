 package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.media.MediaPlayer.create;

public class MainActivity extends AppCompatActivity{

    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        //updateSeek.interrupt();
        //timer.interrupt();

    }

    ImageView play_list,pause,next,previous,frwrd,
            backwrd,shuffel,loop,share;
    MediaPlayer mediaPlayer;
    String[] items;
    ArrayList<File> songs;
    String songName;
    TextView textView , s_t_duration,shareApp,shareSong;
    int position;
    SeekBar seekBar;
   /// Thread updateSeek,timer;
    Handler handler;
    int clk_check;
    Handler mSeekbarUpdateHandler;
    CardView shareView;
    //Runnable mUpdateSeekbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pause = findViewById(R.id.pause);
        play_list = findViewById(R.id.play_list);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        //textView = findViewById(R.id.textView2);
        Intent intent = getIntent();
        Uri song = intent.getData();
        if (song == null) {
            position = intent.getIntExtra("position", -1);
            play_from_list(position);
        } else if (song != null) {
            try {
                play_from_uri(song);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this, "Song "+song.toString(), Toast.LENGTH_SHORT).show();
        }

        pause=findViewById(R.id.pause);
        play_list=findViewById(R.id.play_list);
        play_list.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                pause.setImageResource(R.drawable.play_arrow);
            }
            Intent intent1=new Intent(MainActivity.this,PlayList.class);
            startActivity(intent1);

        });


    }

    public void play_from_list(int position){
        songs= new ArrayList<>(fetchsong(Environment.getExternalStorageDirectory()));

        String song=songs.get(position).toString();

        clk_check=0;

        songName=songs.get(position).getName()
                .replace(".mp3","").replace(".wav","");

        textView=findViewById(R.id.songName);

        textView.setText(songName);

        textView.setSelected(true);

        Uri uri=Uri.parse(song);

        mediaPlayer= create(this,uri);


        mediaPlayer.start();

        seekbar(mediaPlayer);

        play_pause(mediaPlayer);

        next_previous(position,mediaPlayer);

        s_t_duration = findViewById(R.id.timeDuration);

        handler=new Handler();

        int delay=1000;


        handler.postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                try {

                    s_t_duration.setText(convertFormat(mediaPlayer.getDuration())
                            + " / " +
                            convertFormat(mediaPlayer.getCurrentPosition()));
                    handler.postDelayed(this, delay);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, delay);



        mSeekbarUpdateHandler = new Handler();
        seekBar=findViewById(R.id.seekBar);

        mSeekbarUpdateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this,1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },1000);

        frwrd_bkwrd(mediaPlayer);

        pause=findViewById(R.id.pause);

        shuffel();

        loop();

        oncompletation(mediaPlayer,position);

        //createNotificationChannel(songName);

        share(uri);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void play_from_uri(Uri uri) throws IOException {
        songs= new ArrayList<>(fetchsong(Environment.getExternalStorageDirectory()));

        items=new String[songs.size()];

        for (int i=0;i<songs.size();i++){
            items[i]=songs.get(i).getName().
                    replace(".mp3","").replace(".wav","");
        }

        songName=getFileName(uri);

        songName=songName.replace(".mp3","").
                replace(".wav","");

        ArrayList<String> array= new ArrayList<>();

        Collections.addAll(array, items);
        position=array.indexOf(songName);


        textView=findViewById(R.id.songName);

        textView.setText(songName);

        textView.setSelected(true);

        MediaPlayer mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setDataSource(MainActivity.this,uri);

        mediaPlayer.prepare();

        mediaPlayer.start();

        seekbar(mediaPlayer);

        play_pause(mediaPlayer);

        next_previous(position,mediaPlayer);

        s_t_duration=findViewById(R.id.timeDuration);

        handler = new Handler();
        final int delay = 1000;
        try {
            handler.postDelayed(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    try {
                        s_t_duration.setText(convertFormat(mediaPlayer.getDuration())
                                + " / " +
                                convertFormat(mediaPlayer.getCurrentPosition()));
                        handler.postDelayed(this, delay);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, delay);
        }catch (Exception e){
            e.printStackTrace();
        }

        mSeekbarUpdateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this,1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },1000);

        //set_duration(mediaPlayer);

        frwrd_bkwrd(mediaPlayer);

        shuffel();

        loop();

        oncompletation(mediaPlayer,position);

        share(uri);


    }

    public void seekbar(MediaPlayer mediaPlayer){
        seekBar=findViewById(R.id.seekBar);
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }
                    //set_duration(mediaPlayer1);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    mediaPlayer.start();
                    //set_duration(mediaPlayer1);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });



        /*
        updateSeek = new Thread() {
            @Override
            public void run() {
                int currentposition=0;
                try {
                    while (currentposition < mediaPlayer.getDuration()) {
                        currentposition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentposition);
                        //set_duration(mediaPlayer1);
                        //noinspection BusyWait
                        sleep(800);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        try {
            updateSeek.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        */
       // Log.v("progress", "seekbar: "+seekBar.getProgress()/1000);
        try {
            seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.lifht_corol), PorterDuff.Mode.MULTIPLY);
            seekBar.getThumb().setColorFilter(getResources().getColor(R.color.lifht_corol), PorterDuff.Mode.SRC_IN);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void oncompletation(MediaPlayer mediaPlayer,int position) {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
               if(clk_check==1){
                   try {
                       int i=playRandom();
                       while (i==position){
                           i=playRandom();
                       }
                       play_from_list(i);
                       clk_check=1;
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
                }else if(clk_check==2) {
                   try {
                       mediaPlayer.seekTo(0);
                       mediaPlayer.start();
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                }else {
                   try {
                       songs = new ArrayList<>(fetchsong(Environment.getExternalStorageDirectory()));
                       if (position < songs.size() - 1) {
                               try {
                                   play_from_list(position + 1);
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                       } else {
                           pause.setImageResource(R.drawable.play_arrow);
                           pause.setOnClickListener(v -> {
                               try {
                                   play_from_list(0);
                                   pause.setImageResource(R.drawable.baseline_pause);
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                           });
                       }
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                    }
                }
        });
    }

    public void play_pause(MediaPlayer mediaPlayer) {
        pause = findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    pause.setImageResource(R.drawable.play_arrow);
                    mediaPlayer.pause();
                } else{
                    pause.setImageResource(R.drawable.baseline_pause);
                    mediaPlayer.start();
                }
            }
        });
    }

    public void next_previous(int position,MediaPlayer mediaPlayer){
        songs= new ArrayList<>(fetchsong(Environment.getExternalStorageDirectory()));
        next=findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    if (position != songs.size() - 1 && position != -1) {
                        if (clk_check==1) {
                            int i = position + 1;
                            play_from_list(i);
                            clk_check=1;
                        }else {
                            int i=position+1;
                            play_from_list(i);
                        }

                    } else if (position == songs.size() - 1) {
                        if (clk_check==1) {
                            play_from_list(0);
                            clk_check = 1;
                        }else {
                            play_from_list(0);
                        }
                    } else {
                        if (clk_check==1) {
                            play_from_list(songs.size() - 1);
                            clk_check=1;
                        }else {
                            play_from_list(songs.size() - 1);
                        }
                    }
                }
                else {
                    if (clk_check==1) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        pause.setImageResource(R.drawable.baseline_pause);
                        if (position != songs.size() - 1 && position != -1) {
                            int i = position + 1;
                            play_from_list(i);
                            clk_check=1;
                        } else if (position == songs.size() - 1) {
                            play_from_list(0);
                            clk_check=1;
                        } else {
                            play_from_list(songs.size() - 1);
                            clk_check=1;
                        }
                    }else {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        pause.setImageResource(R.drawable.baseline_pause);
                        if (position != songs.size() - 1 && position != -1) {
                            int i = position + 1;
                            play_from_list(i);
                        } else if (position == songs.size() - 1) {
                            play_from_list(0);
                        } else {
                            play_from_list(songs.size() - 1);
                        }
                    }
                }
            }

        }

        );
        previous=findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clk_check==1) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        if (position != 0) {
                            int i = position - 1;
                            play_from_list(i);
                        } else if (position == 0) {
                            play_from_list(songs.size() - 1);
                        } else {
                            play_from_list(0);
                        }
                    } else {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        pause.setImageResource(R.drawable.baseline_pause);
                        if (position != 0) {
                            int i = position - 1;
                            play_from_list(i);
                        } else if (position == 0) {
                            play_from_list(songs.size() - 1);
                        } else {
                            play_from_list(0);
                        }
                    }
                    clk_check=1;
                }else {
                    if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    if (position != 0) {
                        int i = position - 1;
                        play_from_list(i);
                    } else if (position == 0) {
                        play_from_list(songs.size() - 1);
                    } else {
                        play_from_list(0);
                    }
                }else {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        pause.setImageResource(R.drawable.baseline_pause);
                        if (position != 0) {
                            int i = position - 1;
                            play_from_list(i);
                        } else if (position == 0) {
                            play_from_list(songs.size() - 1);
                        } else {
                            play_from_list(0);
                        }
                    }
                }
            }
        });
    }

    public void frwrd_bkwrd(MediaPlayer mediaPlayer){
        frwrd=findViewById(R.id.frwrd);
        backwrd=findViewById(R.id.bkwrd);
        frwrd.setOnClickListener(v -> {
            int duration=mediaPlayer.getDuration();
            int currentposition=mediaPlayer.getCurrentPosition();
            if (currentposition!=duration){
                currentposition=currentposition+10000;
                mediaPlayer.seekTo(currentposition);
            }

        });
        backwrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentposition=mediaPlayer.getCurrentPosition();
                if (currentposition>10000){
                    currentposition=currentposition-10000;
                    mediaPlayer.seekTo(currentposition);
                }
                else{
                    currentposition=0;
                    mediaPlayer.seekTo(currentposition);
                }

            }
        });
    }

    public void shuffel(){
        shuffel=findViewById(R.id.shuffel);
        loop=findViewById(R.id.loop);
        shuffel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clk_check!=1){
                    shuffel.getDrawable().setColorFilter(getResources()
                    .getColor(R.color.textColor),PorterDuff.Mode.MULTIPLY);
                    clk_check=1;
                    loop.getDrawable().setColorFilter(getResources()
                            .getColor(R.color.lifht_corol),PorterDuff.Mode.MULTIPLY);

                    Toast.makeText(MainActivity.this, "Shuffle On", Toast.LENGTH_SHORT).show();

                }else{
                    shuffel.getDrawable().setColorFilter(getResources()
                            .getColor(R.color.lifht_corol),PorterDuff.Mode.MULTIPLY);
                    clk_check=0;
                    Toast.makeText(MainActivity.this, "Shuffle off"+clk_check, Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    public void loop(){
        loop=findViewById(R.id.loop);
        shuffel=findViewById(R.id.shuffel);
        loop.getDrawable().setColorFilter(getResources()
                .getColor(R.color.lifht_corol),PorterDuff.Mode.MULTIPLY);
        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clk_check!=2){
                    loop.getDrawable().setColorFilter(getResources()
                            .getColor(R.color.textColor),PorterDuff.Mode.MULTIPLY);
                    clk_check=2;
                    shuffel.getDrawable().setColorFilter(getResources()
                            .getColor(R.color.lifht_corol),PorterDuff.Mode.MULTIPLY);
                    Toast.makeText(MainActivity.this, "Loop On", Toast.LENGTH_SHORT).show();
                }else{
                    loop.getDrawable().setColorFilter(getResources()
                            .getColor(R.color.lifht_corol),PorterDuff.Mode.MULTIPLY);
                    clk_check=0;
                    Toast.makeText(MainActivity.this, "Loop Off", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration)-
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public int playRandom(){
        songs=new ArrayList<File>(fetchsong(Environment.getExternalStorageDirectory()));
        int i=songs.size();
        Random random=new Random();
        position=random.nextInt(i);
        return position;
    }

    public ArrayList<File> fetchsong(File file){
        ArrayList arrayList=new ArrayList();
        File[] songs=file.listFiles();
        if(songs!=null){
            for (File myfiles:songs){
                if (!myfiles.isHidden() && myfiles.isDirectory()){
                    arrayList.addAll(fetchsong(myfiles));
                }
                else {
                    if(myfiles.getName().endsWith(".mp3")|| myfiles.getName().endsWith(".wav")){
                        arrayList.add(myfiles);
                    }
                }
            }
        }
        return arrayList;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
/*
    public void notification(String songName){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this)
                .setSmallIcon(R.drawable.musicnode)
                .setContentTitle("Now Playing")
                .setContentText(songName)
                .setAutoCancel(false);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel(String songName) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        int notifyID=1;
        String chanelID="01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.textview);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(chanelID, name, importance);
            channel.setDescription(description);

            Notification builder = new NotificationCompat.Builder(MainActivity.this,chanelID)
                    .setSmallIcon(R.drawable.multimedia)
                    .setContentTitle("Now Playing")
                    .setContentText(songName)
                    .setAutoCancel(false)
                    .setChannelId(chanelID)
                    .build();
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);

// Issue the notification.
            mNotificationManager.notify(notifyID , builder);

        }else {
            NotificationCompat.Builder builder;
            builder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.multimedia)
            .setContentTitle("Now Playin")
            .setContentText(songName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        }

    }

    public void seek(SeekBar seekBar,MediaPlayer mediaPlayer){
        Handler mSeekbarUpdateHandler = new Handler();
        Runnable mUpdateSeekbar = new Runnable() {
            @Override
            public void run() {
                int currentPosition=0;
                while (currentPosition!=mediaPlayer.getDuration()){
                 try {
                     currentPosition=mediaPlayer.getCurrentPosition();
                     seekBar.setProgress(currentPosition);
                     mSeekbarUpdateHandler.postDelayed(this, 1000);

                 }catch (Exception e){
                     e.printStackTrace();
                 }
                }
            }
        };
        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 1000);
        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);

    }


 */

    public void share(Uri uri){
        String url="https://drive.google.com/drive/folders/15uTK1PTNpFtLPckrsZXCwTnBbICfrWcn?usp=sharing";
        share=findViewById(R.id.share);
        shareView=findViewById(R.id.shareView);
        shareApp=findViewById(R.id.shareApp);
        shareSong=findViewById(R.id.shareSong);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareView.getVisibility()==View.VISIBLE){
                    shareView.setVisibility(View.GONE);
                }else {
                    shareView.setVisibility(View.VISIBLE);
                }
            }
        });
        shareSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendintent=new Intent();
                sendintent.setAction(Intent.ACTION_SEND);
                sendintent.putExtra(Intent.EXTRA_STREAM,uri);
                sendintent.setType("audio/*");

                Intent shareIntent=Intent.createChooser(sendintent,null);
                startActivity(shareIntent);
                shareView.setVisibility(View.GONE);
            }
        });
        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareView.setVisibility(View.GONE);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(i, "Share URL"));
            }
        });
    }



}

