package android.media;

import java.util.Vector;

class UnstyledTextExtractor
  implements Tokenizer.OnTokenListener
{
  Vector<TextTrackCueSpan> mCurrentLine = new Vector();
  long mLastTimestamp;
  StringBuilder mLine = new StringBuilder();
  Vector<TextTrackCueSpan[]> mLines = new Vector();
  
  UnstyledTextExtractor()
  {
    init();
  }
  
  private void init()
  {
    this.mLine.delete(0, this.mLine.length());
    this.mLines.clear();
    this.mCurrentLine.clear();
    this.mLastTimestamp = -1L;
  }
  
  public TextTrackCueSpan[][] getText()
  {
    if ((this.mLine.length() > 0) || (this.mCurrentLine.size() > 0)) {
      onLineEnd();
    }
    TextTrackCueSpan[][] arrayOfTextTrackCueSpan = new TextTrackCueSpan[this.mLines.size()][];
    this.mLines.toArray(arrayOfTextTrackCueSpan);
    init();
    return arrayOfTextTrackCueSpan;
  }
  
  public void onData(String paramString)
  {
    this.mLine.append(paramString);
  }
  
  public void onEnd(String paramString) {}
  
  public void onLineEnd()
  {
    if (this.mLine.length() > 0)
    {
      this.mCurrentLine.add(new TextTrackCueSpan(this.mLine.toString(), this.mLastTimestamp));
      this.mLine.delete(0, this.mLine.length());
    }
    TextTrackCueSpan[] arrayOfTextTrackCueSpan = new TextTrackCueSpan[this.mCurrentLine.size()];
    this.mCurrentLine.toArray(arrayOfTextTrackCueSpan);
    this.mCurrentLine.clear();
    this.mLines.add(arrayOfTextTrackCueSpan);
  }
  
  public void onStart(String paramString1, String[] paramArrayOfString, String paramString2) {}
  
  public void onTimeStamp(long paramLong)
  {
    if ((this.mLine.length() > 0) && (paramLong != this.mLastTimestamp))
    {
      this.mCurrentLine.add(new TextTrackCueSpan(this.mLine.toString(), this.mLastTimestamp));
      this.mLine.delete(0, this.mLine.length());
    }
    this.mLastTimestamp = paramLong;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/UnstyledTextExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */