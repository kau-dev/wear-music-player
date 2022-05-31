package com.kaudeveloper.wearmusicplayer.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.icu.text.DecimalFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kaudeveloper.wearmusicplayer.R;
import com.kaudeveloper.wearmusicplayer.service.PlayerService;



public class MainActivity extends WearableActivity {

    private PlayerService.PlayerServiceBinder playerServiceBinder;
    private MediaControllerCompat mediaController;
    private MediaControllerCompat.Callback callback;
    private ServiceConnection serviceConnection;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 9;
    CircularSeekBar circularSeekBar1;
    boolean changeCirle = false;
    int progressPos = 0;
    int prevmenupos=-1;
    BottomSheetBehavior behavior;
    WearableRecyclerView recyclerView;
    PlayerService myService;
    Boolean bound = false;
    AudioManager audioManager;
    int postDelayed = 450;
    final private Handler threadHandler = new Handler();
    UpdateSeekBarThread updateSeekBarThread;
    DecimalFormat formatter;
    TextView timeTrack;
    TextView bitRateTrack;
    TextView album;
    TextView NameOfTrack;
    MediaMetadataCompat metadataComp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setHideable(true);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //Write your Logic here
                        //  Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        //Write your Logic here
                        //  Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        //Write your Logic here
                        recyclerView.setClickable(false);
                        recyclerView.setVisibility(View.INVISIBLE);
                        circularSeekBar1.setVisibility(View.VISIBLE);
                        NameOfTrack.setPadding(0, 75, 0, 0);
                        // Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //Write your Logic here
                        recyclerView.setClickable(true);
                        recyclerView.setVisibility(View.VISIBLE);
                        circularSeekBar1.setVisibility(View.INVISIBLE);
                        NameOfTrack.setPadding(0, 4, 0, 0);
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        //Write your Logic here
                        recyclerView.setClickable(true);
                        recyclerView.setVisibility(View.VISIBLE);
                        circularSeekBar1.setVisibility(View.INVISIBLE);
                        NameOfTrack.setPadding(0, 4, 0, 0);
                        //  Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //Write your Logic here
                //Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
            }
        });
        ImageButton playButton = (ImageButton) findViewById(R.id.play);
        ImageButton skipToNextButton = (ImageButton) findViewById(R.id.skip_to_next);
        ImageButton skipToPreviousButton = (ImageButton) findViewById(R.id.skip_to_previous);
        ImageButton imageButton_vol_showUI = (ImageButton) findViewById(R.id.imageButton_vol_showUI);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        circularSeekBar1 = findViewById(R.id.circularSeekBar1);
        com.kaudeveloper.wearmusicplayer.ui.CircularSeekBar.OnCircularSeekBarChangeListener seekB = new com.kaudeveloper.wearmusicplayer.ui.CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                progressPos = progress;
                if (changeCirle) {
                    Long duration = mediaController.getMetadata().getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
                    mediaController.getTransportControls().seekTo((int) (progressPos * ((double) (duration / 100))));
                    playButton.setImageResource(R.drawable.ic_pause);
                    changeCirle = false;
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                changeCirle = true;
            }
        };
        recyclerView = findViewById(R.id.main_menu_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEdgeItemsCenteringEnabled(true);
        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        circularSeekBar1.setOnSeekBarChangeListener(seekB);

        formatter = new DecimalFormat();
        formatter.setMinimumIntegerDigits(2);
        NameOfTrack = (TextView) findViewById(R.id.NameOfTrack);
        findViewById(R.id.NameOfTrack).setSelected(true);
        album = (TextView) findViewById(R.id.textalbum);
        timeTrack = (TextView) findViewById(R.id.texttimeTrack);
        bitRateTrack = (TextView) findViewById(R.id.bitRateTrack);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
            }
        }
        callback = new MediaControllerCompat.Callback() {
            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                if (state == null) {
                    return;
                }
                boolean playing = state.getState() == PlaybackStateCompat.STATE_PLAYING;
                refreshlabels();
            }
        };

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                playerServiceBinder = (PlayerService.PlayerServiceBinder) service;
                myService = ((PlayerService.PlayerServiceBinder) service).getService();

                try {
                    mediaController = new MediaControllerCompat(MainActivity.this, playerServiceBinder.getMediaSessionToken());
                    mediaController.registerCallback(callback);
                    callback.onPlaybackStateChanged(mediaController.getPlaybackState());
                    bound = true;


                } catch (RemoteException e) {
                    mediaController = null;
                    bound = false;
                }
                recyclerView.setAdapter(new MainMenuAdapter(getApplicationContext(), myService.musicRepository.getArraylist(), new MainMenuAdapter.AdapterCallback() {
                    @Override
                    public void onItemClicked(final Integer menuPosition) {
                        if (mediaController != null) {
                            if (mediaController.getPlaybackState() == null | myService.getMenuPosition() != menuPosition) {
                                progressPos = 0;
                                myService.prepareToPlayBYmenuPosition(menuPosition);
                                mediaController.getTransportControls().play();
                                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                playButton.setImageResource(R.drawable.ic_pause);
                            } else {
                                if (mediaController.getPlaybackState().getState() != PlaybackStateCompat.STATE_PLAYING) {

                                    mediaController.getTransportControls().play();
                                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    playButton.setImageResource(R.drawable.ic_pause);
                                } else {
                                    mediaController.getTransportControls().pause();
                                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                    playButton.setImageResource(R.drawable.ic_play);
                                }
                            }

                            refreshlabels();
                        }
                    }
                }));

                updateSeekBarThread = new UpdateSeekBarThread();
                threadHandler.postDelayed(updateSeekBarThread, postDelayed);
                refreshlabels();
                if(mediaController!=null){

                if (mediaController.getMetadata()!=null) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    int pos_=myService.musicRepository.getCurrentId();
                    recyclerView.smoothScrollToPosition(pos_+1);
                    View view_ =recyclerView.getChildAt(pos_);
                    if(view_!=null) {
                        TextView menuItem_ = view_.findViewById(R.id.menu_item);
                        Typeface defTypeFace_ = menuItem_.getTypeface();
                        //menuItem_.setTextColor(getColor(R.color.selectedText));
                        menuItem_.setTypeface(Typeface.create(defTypeFace_, Typeface.BOLD));
                    }
                }}
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                playerServiceBinder = null;
                if (mediaController != null) {
                    mediaController.unregisterCallback(callback);
                    mediaController = null;
                    refreshlabels();
                }
            }
        };

        bindService(new Intent(this, PlayerService.class), serviceConnection, BIND_AUTO_CREATE);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                    playButton.setImageResource(R.drawable.ic_play);
                    mediaController.getTransportControls().pause();
                } else {
                    playButton.setImageResource(R.drawable.ic_pause);
                    mediaController.getTransportControls().play();
                }
                postDelayed=450;
                updateSeekBarThread = new UpdateSeekBarThread();
                threadHandler.postDelayed(updateSeekBarThread, postDelayed);
                refreshlabels();

            }
        });


        skipToNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaController != null)
                    mediaController.getTransportControls().skipToNext();
                postDelayed=450;
                updateSeekBarThread = new UpdateSeekBarThread();
                threadHandler.postDelayed(updateSeekBarThread, postDelayed);
                refreshlabels();
            }
        });

        skipToPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaController != null)
                    mediaController.getTransportControls().skipToPrevious();
                postDelayed=450;
                updateSeekBarThread = new UpdateSeekBarThread();
                threadHandler.postDelayed(updateSeekBarThread, postDelayed);
                refreshlabels();
            }
        });


        imageButton_vol_showUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
            }
        });
        if (mediaController != null) {
            if (mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            } else {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(updateSeekBarThread==null) {
            updateSeekBarThread = new UpdateSeekBarThread();
        }
        threadHandler.postDelayed(updateSeekBarThread, postDelayed);
        if (!bound) {
            try {
                bindService(new Intent(this, PlayerService.class), serviceConnection, BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        postDelayed=450;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaController != null) {
          PlaybackStateCompat playbaState_= mediaController.getPlaybackState();
          if(playbaState_!=null) {
              if (mediaController.getPlaybackState().getState() != PlaybackStateCompat.STATE_PLAYING) {
                  stopService(new Intent(MainActivity.this, PlayerService.class));
              }
          }
            mediaController.unregisterCallback(callback);
            mediaController = null;
        }
        playerServiceBinder = null;
        postDelayed=45000;
        unbindService(serviceConnection);
        threadHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        postDelayed=45000;
    }
    class UpdateSeekBarThread implements Runnable {
        public void run() {
            Integer temp = 0;
            Long duration = (long) 0;
            Long currentPosition = (long) 0;
            if (mediaController == null) {
                Log.d("not stop", "error ... runnable not stop!!!!!!!");
            }
            if(postDelayed<1000) {
                threadHandler.postDelayed(this, postDelayed);
            }
            if (!changeCirle) {
                //    temp = Integer.valueOf( / ());
                if (mediaController == null) {

                    return;
                }
                MediaMetadataCompat metadataComp = mediaController.getMetadata();
                if (metadataComp != null) {
                    duration = metadataComp.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
                    currentPosition = myService.getCurrentPosition();
                    if (currentPosition == 0 & (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)) {
                        temp = progressPos;
                    } else {
                        temp = (int) (currentPosition / (duration / 100));
                    }
                    circularSeekBar1.setProgress(temp);
                }

                timeTrack.setText(millisecondsToString(currentPosition) + " / " + millisecondsToString(duration));
            }
        }
    }

    private void refreshlabels() {
        String Text_Textview="";
        if (mediaController != null) metadataComp = mediaController.getMetadata();
        if (metadataComp != null) {
            String artist = metadataComp.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
            String title = metadataComp.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
            Text_Textview=artist + " - " + title;
            if(artist==null & title==null)
            {
                Text_Textview=    myService.musicRepository.getCurrent().getName();
            }
        }else{
            Text_Textview="";
        }
        NameOfTrack.setText(Text_Textview );
    }

    private String millisecondsToString(long milliseconds) {
        long seconds_full = milliseconds / 1000;
        long minutes = milliseconds / 60000;
        long seconds = seconds_full - (minutes * 60);

        return formatter.format(minutes) + ":" + formatter.format(seconds);
    }
}
