package android.media;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

class WebVttTrack
  extends SubtitleTrack
  implements WebVttCueListener
{
  private static final String TAG = "WebVttTrack";
  private Long mCurrentRunID;
  private final UnstyledTextExtractor mExtractor = new UnstyledTextExtractor();
  private final WebVttParser mParser = new WebVttParser(this);
  private final Map<String, TextTrackRegion> mRegions = new HashMap();
  private final WebVttRenderingWidget mRenderingWidget;
  private final Vector<Long> mTimestamps = new Vector();
  private final Tokenizer mTokenizer = new Tokenizer(this.mExtractor);
  
  WebVttTrack(WebVttRenderingWidget paramWebVttRenderingWidget, MediaFormat paramMediaFormat)
  {
    super(paramMediaFormat);
    this.mRenderingWidget = paramWebVttRenderingWidget;
  }
  
  public WebVttRenderingWidget getRenderingWidget()
  {
    return this.mRenderingWidget;
  }
  
  public void onCueParsed(TextTrackCue paramTextTrackCue)
  {
    for (;;)
    {
      int i;
      int j;
      synchronized (this.mParser)
      {
        if (paramTextTrackCue.mRegionId.length() != 0) {
          paramTextTrackCue.mRegion = ((TextTrackRegion)this.mRegions.get(paramTextTrackCue.mRegionId));
        }
        if (this.DEBUG) {
          Log.v("WebVttTrack", "adding cue " + paramTextTrackCue);
        }
        this.mTokenizer.reset();
        Object localObject1 = paramTextTrackCue.mStrings;
        i = 0;
        j = localObject1.length;
        if (i < j)
        {
          str = localObject1[i];
          this.mTokenizer.tokenize(str);
          i += 1;
          continue;
        }
        paramTextTrackCue.mLines = this.mExtractor.getText();
        if (this.DEBUG) {
          Log.v("WebVttTrack", paramTextTrackCue.appendLinesToBuilder(paramTextTrackCue.appendStringsToBuilder(new StringBuilder()).append(" simplified to: ")).toString());
        }
        localObject1 = paramTextTrackCue.mLines;
        int k = localObject1.length;
        i = 0;
        if (i >= k) {
          break label287;
        }
        String str = localObject1[i];
        j = 0;
        int m = str.length;
        if (j < m)
        {
          Object localObject2 = str[j];
          if ((((TextTrackCueSpan)localObject2).mTimestampMs <= paramTextTrackCue.mStartTimeMs) || (((TextTrackCueSpan)localObject2).mTimestampMs >= paramTextTrackCue.mEndTimeMs) || (this.mTimestamps.contains(Long.valueOf(((TextTrackCueSpan)localObject2).mTimestampMs)))) {
            break label386;
          }
          this.mTimestamps.add(Long.valueOf(((TextTrackCueSpan)localObject2).mTimestampMs));
        }
      }
      i += 1;
      continue;
      label287:
      if (this.mTimestamps.size() > 0)
      {
        paramTextTrackCue.mInnerTimesMs = new long[this.mTimestamps.size()];
        i = 0;
        while (i < this.mTimestamps.size())
        {
          paramTextTrackCue.mInnerTimesMs[i] = ((Long)this.mTimestamps.get(i)).longValue();
          i += 1;
        }
        this.mTimestamps.clear();
      }
      for (;;)
      {
        paramTextTrackCue.mRunID = this.mCurrentRunID.longValue();
        addCue(paramTextTrackCue);
        return;
        paramTextTrackCue.mInnerTimesMs = null;
      }
      label386:
      j += 1;
    }
  }
  
  public void onData(byte[] arg1, boolean paramBoolean, long paramLong)
  {
    try
    {
      String str1 = new String(???, "UTF-8");
      synchronized (this.mParser)
      {
        if ((this.mCurrentRunID != null) && (paramLong != this.mCurrentRunID.longValue())) {
          throw new IllegalStateException("Run #" + this.mCurrentRunID + " in progress.  Cannot process run #" + paramLong);
        }
      }
      this.mCurrentRunID = Long.valueOf(paramLong);
    }
    catch (UnsupportedEncodingException ???)
    {
      Log.w("WebVttTrack", "subtitle data is not UTF-8 encoded: " + ???);
      return;
    }
    this.mParser.parse(str2);
    if (paramBoolean)
    {
      finishedRun(paramLong);
      this.mParser.eos();
      this.mRegions.clear();
      this.mCurrentRunID = null;
    }
  }
  
  public void onRegionParsed(TextTrackRegion paramTextTrackRegion)
  {
    synchronized (this.mParser)
    {
      this.mRegions.put(paramTextTrackRegion.mId, paramTextTrackRegion);
      return;
    }
  }
  
  public void updateView(Vector<SubtitleTrack.Cue> paramVector)
  {
    if (!this.mVisible) {
      return;
    }
    if ((this.DEBUG) && (this.mTimeProvider != null)) {}
    try
    {
      Log.d("WebVttTrack", "at " + this.mTimeProvider.getCurrentTimeUs(false, true) / 1000L + " ms the active cues are:");
      if (this.mRenderingWidget != null) {
        this.mRenderingWidget.setActiveCues(paramVector);
      }
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      for (;;)
      {
        Log.d("WebVttTrack", "at (illegal state) the active cues are:");
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/WebVttTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */