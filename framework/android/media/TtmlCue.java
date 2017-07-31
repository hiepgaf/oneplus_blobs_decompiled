package android.media;

class TtmlCue
  extends SubtitleTrack.Cue
{
  public String mText;
  public String mTtmlFragment;
  
  public TtmlCue(long paramLong1, long paramLong2, String paramString1, String paramString2)
  {
    this.mStartTimeMs = paramLong1;
    this.mEndTimeMs = paramLong2;
    this.mText = paramString1;
    this.mTtmlFragment = paramString2;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/TtmlCue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */