package android.media;

import java.util.ArrayList;
import java.util.List;

public class DecoderCapabilities
{
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  public static List<AudioDecoder> getAudioDecoders()
  {
    ArrayList localArrayList = new ArrayList();
    int j = native_get_num_audio_decoders();
    int i = 0;
    while (i < j)
    {
      localArrayList.add(AudioDecoder.values()[native_get_audio_decoder_type(i)]);
      i += 1;
    }
    return localArrayList;
  }
  
  public static List<VideoDecoder> getVideoDecoders()
  {
    ArrayList localArrayList = new ArrayList();
    int j = native_get_num_video_decoders();
    int i = 0;
    while (i < j)
    {
      localArrayList.add(VideoDecoder.values()[native_get_video_decoder_type(i)]);
      i += 1;
    }
    return localArrayList;
  }
  
  private static final native int native_get_audio_decoder_type(int paramInt);
  
  private static final native int native_get_num_audio_decoders();
  
  private static final native int native_get_num_video_decoders();
  
  private static final native int native_get_video_decoder_type(int paramInt);
  
  private static final native void native_init();
  
  public static enum AudioDecoder
  {
    AUDIO_DECODER_WMA;
  }
  
  public static enum VideoDecoder
  {
    VIDEO_DECODER_WMV;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/DecoderCapabilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */