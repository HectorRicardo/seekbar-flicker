package com.example.clean;

import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

public class MainActivity extends AppCompatActivity {

  private static final String NOTIFICATION_CHANNEL_ID = "default";
  private static final int NOTIFICATION_ID = 1;

  private static final String PLAY_ACTION = "com.example.clean.play";

  private final PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder();

  private MediaSessionCompat mediaSession;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    createNotificationChannel();

    mediaSession = new MediaSessionCompat(this, "bug-mre");
    mediaSession.setMetadata(new MediaMetadataCompat.Builder()
        .putLong(METADATA_KEY_DURATION, 20000)
        .build());
    mediaSession.setCallback(callback);

    findViewById(R.id.button)
        .setOnClickListener(ignored -> mediaSession.getController().getTransportControls().play());

    registerReceiver(onPlayReceiver, new IntentFilter(PLAY_ACTION));
  }

  private void postNotification() {
    Intent playIntent = new Intent(PLAY_ACTION).setPackage(getPackageName());
    PendingIntent onPlay = PendingIntent.getBroadcast(this, 0, playIntent, 0);

    Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .addAction(R.drawable.ic_launcher_background, "Play", onPlay)
        .setStyle(new MediaStyle().setMediaSession(mediaSession.getSessionToken()))
        .build();

    NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
  }

  private void createNotificationChannel() {
    NotificationManagerCompat.from(this).createNotificationChannel(
        new NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL_ID, IMPORTANCE_LOW)
            .setName("Player notifications")
            .build()
    );
  }

  private final MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {
    @Override
    public void onPlay() {
      mediaSession.setActive(true);
      mediaSession.setPlaybackState(playbackStateBuilder
          .setState(STATE_PLAYING, 0, 1)
          .setActions(ACTION_SEEK_TO)
          .build());
      postNotification();
    }

    @Override
    public void onSeekTo(long pos) {
      mediaSession.setPlaybackState(playbackStateBuilder
          .setState(STATE_PAUSED, 0, 1)
          .setActions(ACTION_SEEK_TO | ACTION_PLAY)
          .build());
      postNotification();
    }
  };

  private final BroadcastReceiver onPlayReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      mediaSession.getController().getTransportControls().play();
    }
  };
}