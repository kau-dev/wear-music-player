package com.kaudeveloper.wearmusicplayer.service;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import com.kaudeveloper.wearmusicplayer.R;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
public final class MusicRepository {
    ArrayList<com.kaudeveloper.wearmusicplayer.service.Track> data = new ArrayList<>();

    private final int maxIndex = data.size() - 1;
    private int currentItemIndex = 0;

    public void addFilesToArray(String DirectoryName) {
        File directory = new File(DirectoryName);
        File[] files;
        files = directory.listFiles();
        if (files.length == 0) {
            return;
        }
        int i = 0;
        {
            for (i = 0; i < files.length; i++) {
                //Log.d("Files", "FileName:" + files[i].getName());
                if (!files[i].isDirectory() & files != null) {
                    if (files[i].getPath().endsWith(".mp3") || files[i].getPath().endsWith(".ape") || files[i].getPath().endsWith(".flac")) {
//                        MenuItem MyobjTrack = objTrack(files[i].getAbsolutePath(), files[i].getName());
//                        menu_Items.add(MyobjTrack);
                        Track MyobjTrack = objTrack(files[i].getAbsolutePath(), files[i].getName());
                        data.add(MyobjTrack);
                    }
                } else {
                    if (files[i].isDirectory()) {
                        addFilesToArray(files[i].getAbsolutePath());
                    }
                }
            }
        }
    }


    public com.kaudeveloper.wearmusicplayer.service.Track objTrack(String filePath, String NameFile) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filePath);
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        //   String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        //   String bitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String img = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_IMAGE);
        if ("yes".equals(img)) {
            Log.d("my img", "IMAGE ");
        }

        String result = artist + " - " + title;
        if (artist == null & title == null) {
            File ff = new File(NameFile);
            result =   ff.getName().substring(0,ff.getName().length()-4);
        }
        Track myObj = new Track(title, artist, result, R.drawable.index, Uri.fromFile(new File(filePath)), Long.valueOf(duration));
//        Track myObj = new Track(, result, filePath, title, artist, album, bitrate, duration);
        return myObj;
    }

    public com.kaudeveloper.wearmusicplayer.service.Track getNext() {
        if (data.size() == 0) {
            return null;
        }
        if (currentItemIndex == maxIndex)
            currentItemIndex = 0;
        else
            currentItemIndex++;
        return getCurrent();
    }

    public com.kaudeveloper.wearmusicplayer.service.Track getPrevious() {
        if (data.size() == 0) {
            return null;
        }
        if (currentItemIndex == 0)
            currentItemIndex = maxIndex;
        else
            currentItemIndex--;
        return getCurrent();
    }

    public com.kaudeveloper.wearmusicplayer.service.Track getCurrent() {
        try {
            if (data.size() == 0) {
                return null;
            }
            if (currentItemIndex < data.size()) {
                // normal
            } else {
                currentItemIndex = 0;

            }
            if (currentItemIndex < 0) {
                currentItemIndex = data.size() - 1;
            }
            return data.get(currentItemIndex);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public int getCurrentId()
    {
        return currentItemIndex;
    }
    public com.kaudeveloper.wearmusicplayer.service.Track getBymenuPosition(int menuPosition) {
        try {
            if (data.size() == 0) {
                return null;
            }
            if (menuPosition < data.size()) {
                currentItemIndex=menuPosition;
            } else {
                return null;
            }
            return data.get(currentItemIndex);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public ArrayList<com.kaudeveloper.wearmusicplayer.service.Track> getArraylist ()
    {
        return data;
    }
}
