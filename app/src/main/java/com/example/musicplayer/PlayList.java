package com.example.musicplayer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.Adapter.PlaylistAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlayList extends AppCompatActivity {
    //ListView listView;
    RecyclerView recyclerView;
    ArrayList<File> mysongs;
    String items[];

/*   public void getpermission(ListView listView){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).
                withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getView(listView);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken)
                    {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public void getView(ListView listView){
        mysongs=new ArrayList<File>(fetchsong(Environment.getExternalStorageDirectory()));
        items=new String [mysongs.size()];
        for(int i=0; i<mysongs.size();i++){
            items[i]=mysongs.get(i).getName().replace(".mp3","").replace(".wav","");
        }
        OwaisAddapter addapter=new OwaisAddapter(this,0,items);
        listView.setAdapter(addapter);
    }

 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_list);
        Dexter.withContext(PlayList.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).
                withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        mysongs = new ArrayList<File>(fetchsong(Environment.getExternalStorageDirectory()));
                        items = new String[mysongs.size()];
                        for (int i = 0; i < mysongs.size(); i++) {
                            items[i] = mysongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
                        }
                        /*
                        listView = findViewById(R.id.listView);
                        OwaisAddapter addapter = new OwaisAddapter(PlayList.this, 0, items);
                        listView.setAdapter(addapter);

                         */
                        recyclerView=findViewById(R.id.recyclerView);
                        LinearLayoutManager layoutManager=new LinearLayoutManager(PlayList.this);
                        recyclerView.setLayoutManager(layoutManager);
                        //recyclerView.addItemDecoration(new DividerItemDecoration(
                          //     PlayList.this,DividerItemDecoration.VERTICAL));
                        PlaylistAdapter adapter=new PlaylistAdapter(items,PlayList.this);
                        recyclerView.setAdapter(adapter);


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken)
                    {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    public ArrayList<File> fetchsong(File file) {
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
/*
    public class OwaisAddapter extends ArrayAdapter<String> {
        private String[] arr;
        private Context context;


        public OwaisAddapter(@NonNull Context context, int resource, @NonNull String[] arr) {
            super(context, resource, arr);
            this.context = context;
            this.arr = arr;
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return arr[position];
        }

        @SuppressLint("ViewHolder")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_list,
                    parent, false);
            TextView t = convertView.findViewById(R.id.textView);
            t.setText(getItem(position));
            t.setSelected(true);
            mysongs=new ArrayList<File>(fetchsong(Environment.getExternalStorageDirectory()));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PlayList.this, MainActivity.class);
                    intent.putExtra("position", position);
                    startActivity(intent);

                }
            });
            return convertView;
        }

    }


 */

}
