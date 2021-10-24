# Seekbar Flickering bug

In this repository, I present a bug that happens on **Android 11**. When using Media Style
notifications, the seekbar in the notification sometimes flickers. This repo is a minimal
reproducible example of this bug.

1. Our starting point is commit [155b381](https://github.com/HectorRicardo/seekbar-flicker/commit/155b381e771b6e2f8b0c1d445b82efc15b9d4e9d).
I just simply created a project from scratch in Android Studio.
2. Next, I quickly created a media session and configured its notification in commit [8665821](https://github.com/HectorRicardo/seekbar-flicker/commit/86658210271e76c0e199f966ce904ff6088af24f).

To reproduce:

1. Run the app.
2. Click the "Hello World" button. It should post a Media Style notification in the media
notifications carousel.
3. (With the session playing) Drag the seekbar to whichever position. The seekbar should return to
the beginning.
4. Now, click on the colored squared icon in the notification (which will trigger a "Play" command).
You can see that the seekbar flickers and instantly moves to the previous position. It then
instantly returns to the beginning and playback starts.
