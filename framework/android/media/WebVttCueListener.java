package android.media;

abstract interface WebVttCueListener
{
  public abstract void onCueParsed(TextTrackCue paramTextTrackCue);
  
  public abstract void onRegionParsed(TextTrackRegion paramTextTrackRegion);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/WebVttCueListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */