package android.media;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

class SRTTrack
  extends WebVttTrack
{
  private static final int KEY_LOCAL_SETTING = 102;
  private static final int KEY_START_TIME = 7;
  private static final int KEY_STRUCT_TEXT = 16;
  private static final int MEDIA_TIMED_TEXT = 99;
  private static final String TAG = "SRTTrack";
  private final Handler mEventHandler;
  
  SRTTrack(WebVttRenderingWidget paramWebVttRenderingWidget, MediaFormat paramMediaFormat)
  {
    super(paramWebVttRenderingWidget, paramMediaFormat);
    this.mEventHandler = null;
  }
  
  SRTTrack(Handler paramHandler, MediaFormat paramMediaFormat)
  {
    super(null, paramMediaFormat);
    this.mEventHandler = paramHandler;
  }
  
  private static long parseMs(String paramString)
  {
    return 60L * Long.parseLong(paramString.split(":")[0].trim()) * 60L * 1000L + 60L * Long.parseLong(paramString.split(":")[1].trim()) * 1000L + 1000L * Long.parseLong(paramString.split(":")[2].split(",")[0].trim()) + Long.parseLong(paramString.split(":")[2].split(",")[1].trim());
  }
  
  protected void onData(SubtitleData paramSubtitleData)
  {
    int j = 0;
    try
    {
      TextTrackCue localTextTrackCue = new TextTrackCue();
      localTextTrackCue.mStartTimeMs = (paramSubtitleData.getStartTimeUs() / 1000L);
      localTextTrackCue.mEndTimeMs = ((paramSubtitleData.getStartTimeUs() + paramSubtitleData.getDurationUs()) / 1000L);
      paramSubtitleData = new String(paramSubtitleData.getData(), "UTF-8").split("\\r?\\n");
      localTextTrackCue.mLines = new TextTrackCueSpan[paramSubtitleData.length][];
      int k = paramSubtitleData.length;
      int i = 0;
      while (j < k)
      {
        TextTrackCueSpan localTextTrackCueSpan = new TextTrackCueSpan(paramSubtitleData[j], -1L);
        localTextTrackCue.mLines[i] = { localTextTrackCueSpan };
        j += 1;
        i += 1;
      }
      addCue(localTextTrackCue);
      return;
    }
    catch (UnsupportedEncodingException paramSubtitleData)
    {
      Log.w("SRTTrack", "subtitle data is not UTF-8 encoded: " + paramSubtitleData);
    }
  }
  
  public void onData(byte[] paramArrayOfByte, boolean paramBoolean, long paramLong)
  {
    try
    {
      paramArrayOfByte = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(paramArrayOfByte), "UTF-8"));
      if (paramArrayOfByte.readLine() != null)
      {
        localObject = paramArrayOfByte.readLine();
        if (localObject == null) {
          return;
        }
        localTextTrackCue = new TextTrackCue();
        localObject = ((String)localObject).split("-->");
        localTextTrackCue.mStartTimeMs = parseMs(localObject[0]);
        localTextTrackCue.mEndTimeMs = parseMs(localObject[1]);
        localObject = new ArrayList();
        for (;;)
        {
          str = paramArrayOfByte.readLine();
          if (str == null) {
            break;
          }
          paramBoolean = str.trim().equals("");
          if (paramBoolean) {
            break label166;
          }
          ((List)localObject).add(str);
        }
      }
    }
    catch (UnsupportedEncodingException paramArrayOfByte)
    {
      for (;;)
      {
        TextTrackCue localTextTrackCue;
        String str;
        Log.w("SRTTrack", "subtitle data is not UTF-8 encoded: " + paramArrayOfByte);
        return;
        paramBoolean = true;
        continue;
        localTextTrackCue.mLines = new TextTrackCueSpan[((List)localObject).size()][];
        localTextTrackCue.mStrings = ((String[])((List)localObject).toArray(new String[0]));
        Object localObject = ((Iterable)localObject).iterator();
        int i = 0;
        while (((Iterator)localObject).hasNext())
        {
          str = (String)((Iterator)localObject).next();
          TextTrackCueSpan localTextTrackCueSpan = new TextTrackCueSpan(str, -1L);
          localTextTrackCue.mStrings[i] = str;
          localTextTrackCue.mLines[i] = { localTextTrackCueSpan };
          i += 1;
        }
        addCue(localTextTrackCue);
      }
    }
    catch (IOException paramArrayOfByte)
    {
      label166:
      Log.e("SRTTrack", paramArrayOfByte.getMessage(), paramArrayOfByte);
    }
  }
  
  public void updateView(Vector<SubtitleTrack.Cue> paramVector)
  {
    if (getRenderingWidget() != null)
    {
      super.updateView(paramVector);
      return;
    }
    if (this.mEventHandler == null) {
      return;
    }
    Iterator localIterator = paramVector.iterator();
    while (localIterator.hasNext())
    {
      Object localObject3 = (SubtitleTrack.Cue)localIterator.next();
      Object localObject2 = (TextTrackCue)localObject3;
      Object localObject1 = Parcel.obtain();
      ((Parcel)localObject1).writeInt(102);
      ((Parcel)localObject1).writeInt(7);
      ((Parcel)localObject1).writeInt((int)((SubtitleTrack.Cue)localObject3).mStartTimeMs);
      ((Parcel)localObject1).writeInt(16);
      localObject3 = new StringBuilder();
      localObject2 = ((TextTrackCue)localObject2).mStrings;
      int j = localObject2.length;
      int i = 0;
      while (i < j)
      {
        ((StringBuilder)localObject3).append(localObject2[i]).append('\n');
        i += 1;
      }
      localObject2 = ((StringBuilder)localObject3).toString().getBytes();
      ((Parcel)localObject1).writeInt(localObject2.length);
      ((Parcel)localObject1).writeByteArray((byte[])localObject2);
      localObject1 = this.mEventHandler.obtainMessage(99, 0, 0, localObject1);
      this.mEventHandler.sendMessage((Message)localObject1);
    }
    paramVector.clear();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/SRTTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */