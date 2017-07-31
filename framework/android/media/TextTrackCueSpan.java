package android.media;

class TextTrackCueSpan
{
  boolean mEnabled;
  String mText;
  long mTimestampMs;
  
  TextTrackCueSpan(String paramString, long paramLong)
  {
    this.mTimestampMs = paramLong;
    this.mText = paramString;
    if (this.mTimestampMs < 0L) {}
    for (boolean bool = true;; bool = false)
    {
      this.mEnabled = bool;
      return;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (!(paramObject instanceof TextTrackCueSpan)) {
      return false;
    }
    if (this.mTimestampMs == ((TextTrackCueSpan)paramObject).mTimestampMs) {
      bool = this.mText.equals(((TextTrackCueSpan)paramObject).mText);
    }
    return bool;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/TextTrackCueSpan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */