package android.media;

import java.util.Arrays;

class TextTrackCue
  extends SubtitleTrack.Cue
{
  static final int ALIGNMENT_END = 202;
  static final int ALIGNMENT_LEFT = 203;
  static final int ALIGNMENT_MIDDLE = 200;
  static final int ALIGNMENT_RIGHT = 204;
  static final int ALIGNMENT_START = 201;
  private static final String TAG = "TTCue";
  static final int WRITING_DIRECTION_HORIZONTAL = 100;
  static final int WRITING_DIRECTION_VERTICAL_LR = 102;
  static final int WRITING_DIRECTION_VERTICAL_RL = 101;
  int mAlignment = 200;
  boolean mAutoLinePosition;
  String mId = "";
  Integer mLinePosition = null;
  TextTrackCueSpan[][] mLines = null;
  boolean mPauseOnExit = false;
  TextTrackRegion mRegion = null;
  String mRegionId = "";
  int mSize = 100;
  boolean mSnapToLines = true;
  String[] mStrings;
  int mTextPosition = 50;
  int mWritingDirection = 100;
  
  public StringBuilder appendLinesToBuilder(StringBuilder paramStringBuilder)
  {
    if (this.mLines == null)
    {
      paramStringBuilder.append("null");
      return paramStringBuilder;
    }
    paramStringBuilder.append("[");
    int j = 1;
    TextTrackCueSpan[][] arrayOfTextTrackCueSpan = this.mLines;
    int m = arrayOfTextTrackCueSpan.length;
    int i = 0;
    if (i < m)
    {
      TextTrackCueSpan[] arrayOfTextTrackCueSpan1 = arrayOfTextTrackCueSpan[i];
      if (j == 0) {
        paramStringBuilder.append(", ");
      }
      if (arrayOfTextTrackCueSpan1 == null) {
        paramStringBuilder.append("null");
      }
      for (;;)
      {
        j = 0;
        i += 1;
        break;
        paramStringBuilder.append("\"");
        int k = 1;
        long l1 = -1L;
        j = 0;
        int n = arrayOfTextTrackCueSpan1.length;
        while (j < n)
        {
          TextTrackCueSpan localTextTrackCueSpan = arrayOfTextTrackCueSpan1[j];
          if (k == 0) {
            paramStringBuilder.append(" ");
          }
          long l2 = l1;
          if (localTextTrackCueSpan.mTimestampMs != l1)
          {
            paramStringBuilder.append("<").append(WebVttParser.timeToString(localTextTrackCueSpan.mTimestampMs)).append(">");
            l2 = localTextTrackCueSpan.mTimestampMs;
          }
          paramStringBuilder.append(localTextTrackCueSpan.mText);
          k = 0;
          j += 1;
          l1 = l2;
        }
        paramStringBuilder.append("\"");
      }
    }
    paramStringBuilder.append("]");
    return paramStringBuilder;
  }
  
  public StringBuilder appendStringsToBuilder(StringBuilder paramStringBuilder)
  {
    if (this.mStrings == null)
    {
      paramStringBuilder.append("null");
      return paramStringBuilder;
    }
    paramStringBuilder.append("[");
    int j = 1;
    String[] arrayOfString = this.mStrings;
    int i = 0;
    int k = arrayOfString.length;
    if (i < k)
    {
      String str = arrayOfString[i];
      if (j == 0) {
        paramStringBuilder.append(", ");
      }
      if (str == null) {
        paramStringBuilder.append("null");
      }
      for (;;)
      {
        j = 0;
        i += 1;
        break;
        paramStringBuilder.append("\"");
        paramStringBuilder.append(str);
        paramStringBuilder.append("\"");
      }
    }
    paramStringBuilder.append("]");
    return paramStringBuilder;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof TextTrackCue)) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    for (;;)
    {
      boolean bool1;
      int i;
      try
      {
        if ((this.mId.equals(((TextTrackCue)paramObject).mId)) && (this.mPauseOnExit == ((TextTrackCue)paramObject).mPauseOnExit) && (this.mWritingDirection == ((TextTrackCue)paramObject).mWritingDirection) && (this.mRegionId.equals(((TextTrackCue)paramObject).mRegionId)) && (this.mSnapToLines == ((TextTrackCue)paramObject).mSnapToLines) && (this.mAutoLinePosition == ((TextTrackCue)paramObject).mAutoLinePosition) && ((this.mAutoLinePosition) || ((this.mLinePosition != null) && (this.mLinePosition.equals(((TextTrackCue)paramObject).mLinePosition))) || ((this.mLinePosition == null) && (((TextTrackCue)paramObject).mLinePosition == null))) && (this.mTextPosition == ((TextTrackCue)paramObject).mTextPosition) && (this.mSize == ((TextTrackCue)paramObject).mSize) && (this.mAlignment == ((TextTrackCue)paramObject).mAlignment))
        {
          if (this.mLines.length == ((TextTrackCue)paramObject).mLines.length)
          {
            bool1 = true;
            break label236;
            if (i >= this.mLines.length) {
              continue;
            }
            boolean bool2 = Arrays.equals(this.mLines[i], paramObject.mLines[i]);
            if (!bool2) {
              return false;
            }
          }
          else
          {
            bool1 = false;
            break label236;
          }
        }
        else
        {
          bool1 = false;
          break label236;
        }
        i += 1;
        continue;
        return bool1;
      }
      catch (IncompatibleClassChangeError paramObject)
      {
        return false;
      }
      label236:
      if (bool1) {
        i = 0;
      }
    }
  }
  
  public int hashCode()
  {
    return toString().hashCode();
  }
  
  public void onTime(long paramLong)
  {
    TextTrackCueSpan[][] arrayOfTextTrackCueSpan = this.mLines;
    int k = arrayOfTextTrackCueSpan.length;
    int i = 0;
    while (i < k)
    {
      TextTrackCueSpan[] arrayOfTextTrackCueSpan1 = arrayOfTextTrackCueSpan[i];
      int m = arrayOfTextTrackCueSpan1.length;
      int j = 0;
      if (j < m)
      {
        TextTrackCueSpan localTextTrackCueSpan = arrayOfTextTrackCueSpan1[j];
        if (paramLong >= localTextTrackCueSpan.mTimestampMs) {}
        for (boolean bool = true;; bool = false)
        {
          localTextTrackCueSpan.mEnabled = bool;
          j += 1;
          break;
        }
      }
      i += 1;
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    StringBuilder localStringBuilder2 = localStringBuilder1.append(WebVttParser.timeToString(this.mStartTimeMs)).append(" --> ").append(WebVttParser.timeToString(this.mEndTimeMs)).append(" {id:\"").append(this.mId).append("\", pauseOnExit:").append(this.mPauseOnExit).append(", direction:");
    Object localObject;
    if (this.mWritingDirection == 100)
    {
      localObject = "horizontal";
      localStringBuilder2 = localStringBuilder2.append((String)localObject).append(", regionId:\"").append(this.mRegionId).append("\", snapToLines:").append(this.mSnapToLines).append(", linePosition:");
      if (!this.mAutoLinePosition) {
        break label232;
      }
      localObject = "auto";
      label121:
      localStringBuilder2 = localStringBuilder2.append(localObject).append(", textPosition:").append(this.mTextPosition).append(", size:").append(this.mSize).append(", alignment:");
      if (this.mAlignment != 202) {
        break label240;
      }
      localObject = "end";
    }
    for (;;)
    {
      localStringBuilder2.append((String)localObject).append(", text:");
      appendStringsToBuilder(localStringBuilder1).append("}");
      return localStringBuilder1.toString();
      if (this.mWritingDirection == 102)
      {
        localObject = "vertical_lr";
        break;
      }
      if (this.mWritingDirection == 101)
      {
        localObject = "vertical_rl";
        break;
      }
      localObject = "INVALID";
      break;
      label232:
      localObject = this.mLinePosition;
      break label121;
      label240:
      if (this.mAlignment == 203) {
        localObject = "left";
      } else if (this.mAlignment == 200) {
        localObject = "middle";
      } else if (this.mAlignment == 204) {
        localObject = "right";
      } else if (this.mAlignment == 201) {
        localObject = "start";
      } else {
        localObject = "INVALID";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/TextTrackCue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */