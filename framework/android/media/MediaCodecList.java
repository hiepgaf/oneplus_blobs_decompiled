package android.media;

import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public final class MediaCodecList
{
  public static final int ALL_CODECS = 1;
  public static final int REGULAR_CODECS = 0;
  private static final String TAG = "MediaCodecList";
  private static MediaCodecInfo[] sAllCodecInfos;
  private static Map<String, Object> sGlobalSettings;
  private static Object sInitLock = new Object();
  private static MediaCodecInfo[] sRegularCodecInfos;
  private MediaCodecInfo[] mCodecInfos;
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  private MediaCodecList()
  {
    this(0);
  }
  
  public MediaCodecList(int paramInt)
  {
    initCodecList();
    if (paramInt == 0)
    {
      this.mCodecInfos = sRegularCodecInfos;
      return;
    }
    this.mCodecInfos = sAllCodecInfos;
  }
  
  static final native int findCodecByName(String paramString);
  
  private String findCodecForFormat(boolean paramBoolean, MediaFormat paramMediaFormat)
  {
    String str = paramMediaFormat.getString("mime");
    MediaCodecInfo[] arrayOfMediaCodecInfo = this.mCodecInfos;
    int i = 0;
    int j = arrayOfMediaCodecInfo.length;
    Object localObject;
    if (i < j)
    {
      localObject = arrayOfMediaCodecInfo[i];
      if (((MediaCodecInfo)localObject).isEncoder() == paramBoolean) {}
    }
    for (;;)
    {
      i += 1;
      break;
      try
      {
        MediaCodecInfo.CodecCapabilities localCodecCapabilities = ((MediaCodecInfo)localObject).getCapabilitiesForType(str);
        if ((localCodecCapabilities == null) || (!localCodecCapabilities.isFormatSupported(paramMediaFormat))) {
          continue;
        }
        localObject = ((MediaCodecInfo)localObject).getName();
        return (String)localObject;
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      return null;
    }
  }
  
  static final native MediaCodecInfo.CodecCapabilities getCodecCapabilities(int paramInt, String paramString);
  
  public static final int getCodecCount()
  {
    initCodecList();
    return sRegularCodecInfos.length;
  }
  
  public static final MediaCodecInfo getCodecInfoAt(int paramInt)
  {
    
    if ((paramInt < 0) || (paramInt > sRegularCodecInfos.length)) {
      throw new IllegalArgumentException();
    }
    return sRegularCodecInfos[paramInt];
  }
  
  static final native String getCodecName(int paramInt);
  
  static final Map<String, Object> getGlobalSettings()
  {
    synchronized (sInitLock)
    {
      if (sGlobalSettings == null) {
        sGlobalSettings = native_getGlobalSettings();
      }
      return sGlobalSettings;
    }
  }
  
  public static MediaCodecInfo getInfoFor(String paramString)
  {
    initCodecList();
    return sAllCodecInfos[findCodecByName(paramString)];
  }
  
  private static MediaCodecInfo getNewCodecInfoAt(int paramInt)
  {
    String[] arrayOfString = getSupportedTypes(paramInt);
    MediaCodecInfo.CodecCapabilities[] arrayOfCodecCapabilities = new MediaCodecInfo.CodecCapabilities[arrayOfString.length];
    int j = 0;
    int k = arrayOfString.length;
    int i = 0;
    while (j < k)
    {
      arrayOfCodecCapabilities[i] = getCodecCapabilities(paramInt, arrayOfString[j]);
      j += 1;
      i += 1;
    }
    return new MediaCodecInfo(getCodecName(paramInt), isEncoder(paramInt), arrayOfCodecCapabilities);
  }
  
  static final native String[] getSupportedTypes(int paramInt);
  
  private static final void initCodecList()
  {
    ArrayList localArrayList2;
    synchronized (sInitLock)
    {
      if (sRegularCodecInfos != null) {
        break label136;
      }
      int j = native_getCodecCount();
      ArrayList localArrayList1 = new ArrayList();
      localArrayList2 = new ArrayList();
      int i = 0;
      if (i < j)
      {
        try
        {
          MediaCodecInfo localMediaCodecInfo = getNewCodecInfoAt(i);
          localArrayList2.add(localMediaCodecInfo);
          localMediaCodecInfo = localMediaCodecInfo.makeRegular();
          if (localMediaCodecInfo != null) {
            localArrayList1.add(localMediaCodecInfo);
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Log.e("MediaCodecList", "Could not get codec capabilities", localException);
          }
        }
        i += 1;
      }
    }
    sRegularCodecInfos = (MediaCodecInfo[])((ArrayList)localObject2).toArray(new MediaCodecInfo[((ArrayList)localObject2).size()]);
    sAllCodecInfos = (MediaCodecInfo[])localArrayList2.toArray(new MediaCodecInfo[localArrayList2.size()]);
    label136:
  }
  
  static final native boolean isEncoder(int paramInt);
  
  private static final native int native_getCodecCount();
  
  static final native Map<String, Object> native_getGlobalSettings();
  
  private static final native void native_init();
  
  public final String findDecoderForFormat(MediaFormat paramMediaFormat)
  {
    return findCodecForFormat(false, paramMediaFormat);
  }
  
  public final String findEncoderForFormat(MediaFormat paramMediaFormat)
  {
    return findCodecForFormat(true, paramMediaFormat);
  }
  
  public final MediaCodecInfo[] getCodecInfos()
  {
    return (MediaCodecInfo[])Arrays.copyOf(this.mCodecInfos, this.mCodecInfos.length);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaCodecList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */