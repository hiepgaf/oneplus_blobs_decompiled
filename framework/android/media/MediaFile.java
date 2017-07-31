package android.media;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MediaFile
{
  public static final int FILE_TYPE_3GPA = 211;
  public static final int FILE_TYPE_3GPP = 23;
  public static final int FILE_TYPE_3GPP2 = 24;
  public static final int FILE_TYPE_AAC = 8;
  public static final int FILE_TYPE_AC3 = 212;
  public static final int FILE_TYPE_AIFF = 216;
  public static final int FILE_TYPE_AMR = 4;
  public static final int FILE_TYPE_APE = 217;
  public static final int FILE_TYPE_ARW = 304;
  public static final int FILE_TYPE_ASF = 26;
  public static final int FILE_TYPE_AVI = 29;
  public static final int FILE_TYPE_AWB = 5;
  public static final int FILE_TYPE_BMP = 34;
  public static final int FILE_TYPE_CR2 = 301;
  public static final int FILE_TYPE_DASH = 45;
  public static final int FILE_TYPE_DIVX = 201;
  public static final int FILE_TYPE_DNG = 300;
  public static final int FILE_TYPE_DSD = 218;
  public static final int FILE_TYPE_DTS = 210;
  public static final int FILE_TYPE_EC3 = 215;
  public static final int FILE_TYPE_FL = 51;
  public static final int FILE_TYPE_FLAC = 10;
  public static final int FILE_TYPE_FLV = 202;
  public static final int FILE_TYPE_GIF = 32;
  public static final int FILE_TYPE_HTML = 101;
  public static final int FILE_TYPE_HTTPLIVE = 44;
  public static final int FILE_TYPE_IMY = 13;
  public static final int FILE_TYPE_JPEG = 31;
  public static final int FILE_TYPE_M3U = 41;
  public static final int FILE_TYPE_M4A = 2;
  public static final int FILE_TYPE_M4V = 22;
  public static final int FILE_TYPE_MID = 11;
  public static final int FILE_TYPE_MKA = 9;
  public static final int FILE_TYPE_MKV = 27;
  public static final int FILE_TYPE_MP2PS = 200;
  public static final int FILE_TYPE_MP2TS = 28;
  public static final int FILE_TYPE_MP3 = 1;
  public static final int FILE_TYPE_MP4 = 21;
  public static final int FILE_TYPE_MS_EXCEL = 105;
  public static final int FILE_TYPE_MS_POWERPOINT = 106;
  public static final int FILE_TYPE_MS_WORD = 104;
  public static final int FILE_TYPE_NEF = 302;
  public static final int FILE_TYPE_NRW = 303;
  public static final int FILE_TYPE_OGG = 7;
  public static final int FILE_TYPE_ORF = 306;
  public static final int FILE_TYPE_PCM = 214;
  public static final int FILE_TYPE_PDF = 102;
  public static final int FILE_TYPE_PEF = 308;
  public static final int FILE_TYPE_PLS = 42;
  public static final int FILE_TYPE_PNG = 33;
  public static final int FILE_TYPE_QCP = 213;
  public static final int FILE_TYPE_QT = 203;
  public static final int FILE_TYPE_RAF = 307;
  public static final int FILE_TYPE_RW2 = 305;
  public static final int FILE_TYPE_SD = 52;
  public static final int FILE_TYPE_SMF = 12;
  public static final int FILE_TYPE_SRW = 309;
  public static final int FILE_TYPE_TEXT = 100;
  public static final int FILE_TYPE_WAV = 3;
  public static final int FILE_TYPE_WBMP = 35;
  public static final int FILE_TYPE_WEBM = 30;
  public static final int FILE_TYPE_WEBP = 36;
  public static final int FILE_TYPE_WMA = 6;
  public static final int FILE_TYPE_WMV = 25;
  public static final int FILE_TYPE_WPL = 43;
  public static final int FILE_TYPE_XML = 103;
  public static final int FILE_TYPE_ZIP = 107;
  private static final int FIRST_AUDIO_FILE_TYPE = 1;
  private static final int FIRST_AUDIO_FILE_TYPE_EXT = 210;
  private static final int FIRST_DRM_FILE_TYPE = 51;
  private static final int FIRST_IMAGE_FILE_TYPE = 31;
  private static final int FIRST_MIDI_FILE_TYPE = 11;
  private static final int FIRST_PLAYLIST_FILE_TYPE = 41;
  private static final int FIRST_RAW_IMAGE_FILE_TYPE = 300;
  private static final int FIRST_VIDEO_FILE_TYPE = 21;
  private static final int FIRST_VIDEO_FILE_TYPE2 = 200;
  private static final int LAST_AUDIO_FILE_TYPE = 10;
  private static final int LAST_AUDIO_FILE_TYPE_EXT = 218;
  private static final int LAST_DRM_FILE_TYPE = 52;
  private static final int LAST_IMAGE_FILE_TYPE = 36;
  private static final int LAST_MIDI_FILE_TYPE = 13;
  private static final int LAST_PLAYLIST_FILE_TYPE = 45;
  private static final int LAST_RAW_IMAGE_FILE_TYPE = 309;
  private static final int LAST_VIDEO_FILE_TYPE = 30;
  private static final int LAST_VIDEO_FILE_TYPE2 = 203;
  private static final HashMap<String, MediaFileType> sFileTypeMap = new HashMap();
  private static final HashMap<String, Integer> sFileTypeToFormatMap;
  private static final HashMap<Integer, String> sFormatToMimeTypeMap;
  private static final HashMap<String, Integer> sMimeTypeMap = new HashMap();
  private static final HashMap<String, Integer> sMimeTypeToFormatMap;
  
  static
  {
    sFileTypeToFormatMap = new HashMap();
    sMimeTypeToFormatMap = new HashMap();
    sFormatToMimeTypeMap = new HashMap();
    addFileType("MP3", 1, "audio/mpeg", 12297);
    addFileType("MP3", 1, "audio/mp3", 12297);
    addFileType("MPGA", 1, "audio/mpeg", 12297);
    addFileType("M4A", 2, "audio/mp4", 12299);
    addFileType("M4A", 2, "audio/mp4a-latm", 12299);
    addFileType("WAV", 3, "audio/x-wav", 12296);
    addFileType("AMR", 4, "audio/amr");
    addFileType("AWB", 5, "audio/amr-wb");
    if (isWMAEnabled()) {
      addFileType("WMA", 6, "audio/x-ms-wma", 47361);
    }
    addFileType("OGG", 7, "audio/ogg", 47362);
    addFileType("OGG", 7, "application/ogg", 47362);
    addFileType("OGA", 7, "application/ogg", 47362);
    addFileType("AAC", 8, "audio/aac", 47363);
    addFileType("AAC", 8, "audio/aac-adts", 47363);
    addFileType("MKA", 9, "audio/x-matroska");
    addFileType("MID", 11, "audio/midi");
    addFileType("MIDI", 11, "audio/midi");
    addFileType("XMF", 11, "audio/midi");
    addFileType("RTTTL", 11, "audio/midi");
    addFileType("SMF", 12, "audio/sp-midi");
    addFileType("IMY", 13, "audio/imelody");
    addFileType("RTX", 11, "audio/midi");
    addFileType("OTA", 11, "audio/midi");
    addFileType("MXMF", 11, "audio/midi");
    addFileType("MPEG", 21, "video/mpeg", 12299);
    addFileType("MPG", 21, "video/mpeg", 12299);
    addFileType("MP4", 21, "video/mp4", 12299);
    addFileType("M4V", 22, "video/mp4", 12299);
    addFileType("MOV", 203, "video/quicktime", 12299);
    addFileType("MOV", 203, "video/mp4");
    addFileType("3GP", 23, "video/3gpp", 47492);
    addFileType("3GPP", 23, "video/3gpp", 47492);
    addFileType("3G2", 24, "video/3gpp2", 47492);
    addFileType("3GPP2", 24, "video/3gpp2", 47492);
    addFileType("MKV", 27, "video/x-matroska");
    addFileType("WEBM", 30, "video/webm");
    addFileType("TS", 28, "video/mp2ts");
    addFileType("AVI", 29, "video/avi");
    if (isWMVEnabled())
    {
      addFileType("WMV", 25, "video/x-ms-wmv", 47489);
      addFileType("ASF", 26, "video/x-ms-asf");
    }
    addFileType("JPG", 31, "image/jpeg", 14337);
    addFileType("JPEG", 31, "image/jpeg", 14337);
    addFileType("GIF", 32, "image/gif", 14343);
    addFileType("PNG", 33, "image/png", 14347);
    addFileType("BMP", 34, "image/x-ms-bmp", 14340);
    addFileType("WBMP", 35, "image/vnd.wap.wbmp", 14336);
    addFileType("WEBP", 36, "image/webp", 14336);
    addFileType("DNG", 300, "image/x-adobe-dng", 14353);
    addFileType("CR2", 301, "image/x-canon-cr2", 14349);
    addFileType("NEF", 302, "image/x-nikon-nef", 14338);
    addFileType("NRW", 303, "image/x-nikon-nrw", 14349);
    addFileType("ARW", 304, "image/x-sony-arw", 14349);
    addFileType("RW2", 305, "image/x-panasonic-rw2", 14349);
    addFileType("ORF", 306, "image/x-olympus-orf", 14349);
    addFileType("RAF", 307, "image/x-fuji-raf", 14336);
    addFileType("PEF", 308, "image/x-pentax-pef", 14349);
    addFileType("SRW", 309, "image/x-samsung-srw", 14349);
    addFileType("M3U", 41, "audio/x-mpegurl", 47633);
    addFileType("M3U", 41, "application/x-mpegurl", 47633);
    addFileType("PLS", 42, "audio/x-scpls", 47636);
    addFileType("WPL", 43, "application/vnd.ms-wpl", 47632);
    addFileType("M3U8", 44, "application/vnd.apple.mpegurl");
    addFileType("M3U8", 44, "audio/mpegurl");
    addFileType("M3U8", 44, "audio/x-mpegurl");
    addFileType("FL", 51, "application/x-android-drm-fl");
    addFileType("DCF", 52, "application/vnd.oma.drm.content");
    addFileType("TXT", 100, "text/plain", 12292);
    addFileType("HTM", 101, "text/html", 12293);
    addFileType("HTML", 101, "text/html", 12293);
    addFileType("PDF", 102, "application/pdf");
    addFileType("DOC", 104, "application/msword", 47747);
    addFileType("XLS", 105, "application/vnd.ms-excel", 47749);
    addFileType("PPT", 106, "application/mspowerpoint", 47750);
    addFileType("FLAC", 10, "audio/flac", 47366);
    addFileType("ZIP", 107, "application/zip");
    addFileType("MPG", 200, "video/mp2p");
    addFileType("MPEG", 200, "video/mp2p");
    addFileType("DIVX", 201, "video/divx");
    addFileType("FLV", 202, "video/flv");
    addFileType("MPD", 45, "application/dash+xml");
    addFileType("QCP", 213, "audio/qcelp");
    addFileType("AC3", 212, "audio/ac3");
    addFileType("EC3", 215, "audio/eac3");
    addFileType("AIF", 216, "audio/x-aiff");
    addFileType("AIFF", 216, "audio/x-aiff");
    addFileType("APE", 217, "audio/x-ape");
    addFileType("DSF", 218, "audio/x-dsf");
    addFileType("DFF", 218, "audio/x-dff");
    addFileType("DSD", 218, "audio/dsd");
  }
  
  static void addFileType(String paramString1, int paramInt, String paramString2)
  {
    sFileTypeMap.put(paramString1, new MediaFileType(paramInt, paramString2));
    sMimeTypeMap.put(paramString2, Integer.valueOf(paramInt));
  }
  
  static void addFileType(String paramString1, int paramInt1, String paramString2, int paramInt2)
  {
    addFileType(paramString1, paramInt1, paramString2);
    sFileTypeToFormatMap.put(paramString1, Integer.valueOf(paramInt2));
    sMimeTypeToFormatMap.put(paramString2, Integer.valueOf(paramInt2));
    sFormatToMimeTypeMap.put(Integer.valueOf(paramInt2), paramString2);
  }
  
  public static String getFileTitle(String paramString)
  {
    int i = paramString.lastIndexOf('/');
    String str = paramString;
    if (i >= 0)
    {
      i += 1;
      str = paramString;
      if (i < paramString.length()) {
        str = paramString.substring(i);
      }
    }
    i = str.lastIndexOf('.');
    paramString = str;
    if (i > 0) {
      paramString = str.substring(0, i);
    }
    return paramString;
  }
  
  public static MediaFileType getFileType(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    if (i < 0) {
      return null;
    }
    return (MediaFileType)sFileTypeMap.get(paramString.substring(i + 1).toUpperCase(Locale.ROOT));
  }
  
  public static int getFileTypeForMimeType(String paramString)
  {
    paramString = (Integer)sMimeTypeMap.get(paramString);
    if (paramString == null) {
      return 0;
    }
    return paramString.intValue();
  }
  
  public static int getFormatCode(String paramString1, String paramString2)
  {
    if (paramString2 != null)
    {
      paramString2 = (Integer)sMimeTypeToFormatMap.get(paramString2);
      if (paramString2 != null) {
        return paramString2.intValue();
      }
    }
    int i = paramString1.lastIndexOf('.');
    if (i > 0)
    {
      paramString1 = paramString1.substring(i + 1).toUpperCase(Locale.ROOT);
      paramString1 = (Integer)sFileTypeToFormatMap.get(paramString1);
      if (paramString1 != null) {
        return paramString1.intValue();
      }
    }
    return 12288;
  }
  
  public static String getMimeTypeForFile(String paramString)
  {
    paramString = getFileType(paramString);
    if (paramString == null) {
      return null;
    }
    return paramString.mimeType;
  }
  
  public static String getMimeTypeForFormatCode(int paramInt)
  {
    return (String)sFormatToMimeTypeMap.get(Integer.valueOf(paramInt));
  }
  
  public static boolean isAudioFileType(int paramInt)
  {
    if ((paramInt >= 1) && (paramInt <= 10)) {}
    do
    {
      do
      {
        return true;
      } while ((paramInt >= 11) && (paramInt <= 13));
      if (paramInt < 210) {
        break;
      }
    } while (paramInt <= 218);
    return false;
    return false;
  }
  
  public static boolean isDrmFileType(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 51)
    {
      bool1 = bool2;
      if (paramInt <= 52) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean isExtAudioFileType(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 210)
    {
      bool1 = bool2;
      if (paramInt <= 218) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean isImageFileType(int paramInt)
  {
    if ((paramInt >= 31) && (paramInt <= 36)) {}
    do
    {
      return true;
      if (paramInt < 300) {
        break;
      }
    } while (paramInt <= 309);
    return false;
    return false;
  }
  
  public static boolean isLegacyAudioFileType(int paramInt)
  {
    if ((paramInt >= 1) && (paramInt <= 10)) {}
    do
    {
      return true;
      if (paramInt < 11) {
        break;
      }
    } while (paramInt <= 13);
    return false;
    return false;
  }
  
  public static boolean isMimeTypeMedia(String paramString)
  {
    int i = getFileTypeForMimeType(paramString);
    if ((!isAudioFileType(i)) && (!isVideoFileType(i)) && (!isImageFileType(i))) {
      return isPlayListFileType(i);
    }
    return true;
  }
  
  public static boolean isPlayListFileType(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 41)
    {
      bool1 = bool2;
      if (paramInt <= 45) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean isRawImageFileType(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 300)
    {
      bool1 = bool2;
      if (paramInt <= 309) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean isVideoFileType(int paramInt)
  {
    if ((paramInt >= 21) && (paramInt <= 30)) {}
    do
    {
      return true;
      if (paramInt < 200) {
        break;
      }
    } while (paramInt <= 203);
    return false;
    return false;
  }
  
  private static boolean isWMAEnabled()
  {
    List localList = DecoderCapabilities.getAudioDecoders();
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      if ((DecoderCapabilities.AudioDecoder)localList.get(i) == DecoderCapabilities.AudioDecoder.AUDIO_DECODER_WMA) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private static boolean isWMVEnabled()
  {
    List localList = DecoderCapabilities.getVideoDecoders();
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      if ((DecoderCapabilities.VideoDecoder)localList.get(i) == DecoderCapabilities.VideoDecoder.VIDEO_DECODER_WMV) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static class MediaFileType
  {
    public final int fileType;
    public final String mimeType;
    
    MediaFileType(int paramInt, String paramString)
    {
      this.fileType = paramInt;
      this.mimeType = paramString;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */