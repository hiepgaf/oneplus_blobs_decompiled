package android.media;

import java.util.ArrayList;
import java.util.List;

public class EncoderCapabilities
{
  private static final String TAG = "EncoderCapabilities";
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  public static List<AudioEncoderCap> getAudioEncoders()
  {
    int j = native_get_num_audio_encoders();
    if (j == 0) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < j)
    {
      localArrayList.add(native_get_audio_encoder_cap(i));
      i += 1;
    }
    return localArrayList;
  }
  
  public static int[] getOutputFileFormats()
  {
    int j = native_get_num_file_formats();
    if (j == 0) {
      return null;
    }
    int[] arrayOfInt = new int[j];
    int i = 0;
    while (i < j)
    {
      arrayOfInt[i] = native_get_file_format(i);
      i += 1;
    }
    return arrayOfInt;
  }
  
  public static List<VideoEncoderCap> getVideoEncoders()
  {
    int j = native_get_num_video_encoders();
    if (j == 0) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < j)
    {
      localArrayList.add(native_get_video_encoder_cap(i));
      i += 1;
    }
    return localArrayList;
  }
  
  private static final native AudioEncoderCap native_get_audio_encoder_cap(int paramInt);
  
  private static final native int native_get_file_format(int paramInt);
  
  private static final native int native_get_num_audio_encoders();
  
  private static final native int native_get_num_file_formats();
  
  private static final native int native_get_num_video_encoders();
  
  private static final native VideoEncoderCap native_get_video_encoder_cap(int paramInt);
  
  private static final native void native_init();
  
  public static class AudioEncoderCap
  {
    public final int mCodec;
    public final int mMaxBitRate;
    public final int mMaxChannels;
    public final int mMaxSampleRate;
    public final int mMinBitRate;
    public final int mMinChannels;
    public final int mMinSampleRate;
    
    private AudioEncoderCap(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
    {
      this.mCodec = paramInt1;
      this.mMinBitRate = paramInt2;
      this.mMaxBitRate = paramInt3;
      this.mMinSampleRate = paramInt4;
      this.mMaxSampleRate = paramInt5;
      this.mMinChannels = paramInt6;
      this.mMaxChannels = paramInt7;
    }
  }
  
  public static class VideoEncoderCap
  {
    public final int mCodec;
    public final int mMaxBitRate;
    public final int mMaxFrameHeight;
    public final int mMaxFrameRate;
    public final int mMaxFrameWidth;
    public final int mMinBitRate;
    public final int mMinFrameHeight;
    public final int mMinFrameRate;
    public final int mMinFrameWidth;
    
    private VideoEncoderCap(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
    {
      this.mCodec = paramInt1;
      this.mMinBitRate = paramInt2;
      this.mMaxBitRate = paramInt3;
      this.mMinFrameRate = paramInt4;
      this.mMaxFrameRate = paramInt5;
      this.mMinFrameWidth = paramInt6;
      this.mMaxFrameWidth = paramInt7;
      this.mMinFrameHeight = paramInt8;
      this.mMaxFrameHeight = paramInt9;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/EncoderCapabilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */