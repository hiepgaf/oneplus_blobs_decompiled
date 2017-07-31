package android.media;

import android.util.Log;
import android.util.Pair;
import android.util.Range;
import android.util.Rational;
import android.util.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class MediaCodecInfo
{
  private static final Range<Integer> BITRATE_RANGE = Range.create(Integer.valueOf(0), Integer.valueOf(500000000));
  private static final int DEFAULT_MAX_SUPPORTED_INSTANCES = 32;
  private static final int ERROR_NONE_SUPPORTED = 4;
  private static final int ERROR_UNRECOGNIZED = 1;
  private static final int ERROR_UNSUPPORTED = 2;
  private static final Range<Integer> FRAME_RATE_RANGE;
  private static final int MAX_SUPPORTED_INSTANCES_LIMIT = 256;
  private static final Range<Integer> POSITIVE_INTEGERS = Range.create(Integer.valueOf(1), Integer.valueOf(Integer.MAX_VALUE));
  private static final Range<Long> POSITIVE_LONGS = Range.create(Long.valueOf(1L), Long.valueOf(Long.MAX_VALUE));
  private static final Range<Rational> POSITIVE_RATIONALS = Range.create(new Rational(1, Integer.MAX_VALUE), new Rational(Integer.MAX_VALUE, 1));
  private static final Range<Integer> SIZE_RANGE = Range.create(Integer.valueOf(1), Integer.valueOf(32768));
  private Map<String, CodecCapabilities> mCaps;
  private boolean mIsEncoder;
  private String mName;
  
  static
  {
    FRAME_RATE_RANGE = Range.create(Integer.valueOf(0), Integer.valueOf(960));
  }
  
  MediaCodecInfo(String paramString, boolean paramBoolean, CodecCapabilities[] paramArrayOfCodecCapabilities)
  {
    this.mName = paramString;
    this.mIsEncoder = paramBoolean;
    this.mCaps = new HashMap();
    int i = 0;
    int j = paramArrayOfCodecCapabilities.length;
    while (i < j)
    {
      paramString = paramArrayOfCodecCapabilities[i];
      this.mCaps.put(paramString.getMimeType(), paramString);
      i += 1;
    }
  }
  
  private static int checkPowerOfTwo(int paramInt, String paramString)
  {
    if ((paramInt - 1 & paramInt) != 0) {
      throw new IllegalArgumentException(paramString);
    }
    return paramInt;
  }
  
  public final CodecCapabilities getCapabilitiesForType(String paramString)
  {
    paramString = (CodecCapabilities)this.mCaps.get(paramString);
    if (paramString == null) {
      throw new IllegalArgumentException("codec does not support type");
    }
    return paramString.dup();
  }
  
  public final String getName()
  {
    return this.mName;
  }
  
  public final String[] getSupportedTypes()
  {
    Object localObject = this.mCaps.keySet();
    localObject = (String[])((Set)localObject).toArray(new String[((Set)localObject).size()]);
    Arrays.sort((Object[])localObject);
    return (String[])localObject;
  }
  
  public final boolean isEncoder()
  {
    return this.mIsEncoder;
  }
  
  public MediaCodecInfo makeRegular()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mCaps.values().iterator();
    while (localIterator.hasNext())
    {
      CodecCapabilities localCodecCapabilities = (CodecCapabilities)localIterator.next();
      if (localCodecCapabilities.isRegular()) {
        localArrayList.add(localCodecCapabilities);
      }
    }
    if (localArrayList.size() == 0) {
      return null;
    }
    if (localArrayList.size() == this.mCaps.size()) {
      return this;
    }
    return new MediaCodecInfo(this.mName, this.mIsEncoder, (CodecCapabilities[])localArrayList.toArray(new CodecCapabilities[localArrayList.size()]));
  }
  
  public static final class AudioCapabilities
  {
    private static final int MAX_INPUT_CHANNEL_COUNT = 30;
    private static final String TAG = "AudioCapabilities";
    private Range<Integer> mBitrateRange;
    private int mMaxInputChannelCount;
    private MediaCodecInfo.CodecCapabilities mParent;
    private Range<Integer>[] mSampleRateRanges;
    private int[] mSampleRates;
    
    private void applyLevelLimits()
    {
      int[] arrayOfInt = null;
      Range localRange2 = null;
      Range localRange1 = null;
      int i = 0;
      Object localObject = this.mParent.getMimeType();
      if (((String)localObject).equalsIgnoreCase("audio/mpeg"))
      {
        arrayOfInt = new int[9];
        int[] tmp34_33 = arrayOfInt;
        tmp34_33[0] = 'ὀ';
        int[] tmp40_34 = tmp34_33;
        tmp40_34[1] = '⬑';
        int[] tmp46_40 = tmp40_34;
        tmp46_40[2] = '⻠';
        int[] tmp52_46 = tmp46_40;
        tmp52_46[3] = '㺀';
        int[] tmp58_52 = tmp52_46;
        tmp58_52[4] = '嘢';
        int[] tmp64_58 = tmp58_52;
        tmp64_58[5] = '巀';
        int[] tmp70_64 = tmp64_58;
        tmp70_64[6] = '紀';
        int[] tmp77_70 = tmp70_64;
        tmp77_70[7] = 44100;
        int[] tmp83_77 = tmp77_70;
        tmp83_77[8] = 48000;
        tmp83_77;
        localRange1 = Range.create(Integer.valueOf(8000), Integer.valueOf(320000));
        i = 2;
        if (arrayOfInt == null) {
          break label646;
        }
        limitSampleRates(arrayOfInt);
      }
      for (;;)
      {
        applyLimits(i, localRange1);
        return;
        if (((String)localObject).equalsIgnoreCase("audio/3gpp"))
        {
          arrayOfInt = new int[1];
          arrayOfInt[0] = 8000;
          localRange1 = Range.create(Integer.valueOf(4750), Integer.valueOf(12200));
          i = 1;
          break;
        }
        if (((String)localObject).equalsIgnoreCase("audio/amr-wb"))
        {
          arrayOfInt = new int[1];
          arrayOfInt[0] = 16000;
          localRange1 = Range.create(Integer.valueOf(6600), Integer.valueOf(23850));
          i = 1;
          break;
        }
        if (((String)localObject).equalsIgnoreCase("audio/mp4a-latm"))
        {
          arrayOfInt = new int[13];
          int[] tmp221_220 = arrayOfInt;
          tmp221_220[0] = 'Ჶ';
          int[] tmp227_221 = tmp221_220;
          tmp227_221[1] = 'ὀ';
          int[] tmp233_227 = tmp227_221;
          tmp233_227[2] = '⬑';
          int[] tmp239_233 = tmp233_227;
          tmp239_233[3] = '⻠';
          int[] tmp245_239 = tmp239_233;
          tmp245_239[4] = '㺀';
          int[] tmp251_245 = tmp245_239;
          tmp251_245[5] = '嘢';
          int[] tmp257_251 = tmp251_245;
          tmp257_251[6] = '巀';
          int[] tmp264_257 = tmp257_251;
          tmp264_257[7] = '紀';
          int[] tmp271_264 = tmp264_257;
          tmp271_264[8] = 44100;
          int[] tmp277_271 = tmp271_264;
          tmp277_271[9] = 48000;
          int[] tmp283_277 = tmp277_271;
          tmp283_277[10] = 64000;
          int[] tmp289_283 = tmp283_277;
          tmp289_283[11] = 88200;
          int[] tmp295_289 = tmp289_283;
          tmp295_289[12] = 96000;
          tmp295_289;
          localRange1 = Range.create(Integer.valueOf(8000), Integer.valueOf(510000));
          i = 48;
          break;
        }
        if (((String)localObject).equalsIgnoreCase("audio/vorbis"))
        {
          localRange1 = Range.create(Integer.valueOf(32000), Integer.valueOf(500000));
          localRange2 = Range.create(Integer.valueOf(8000), Integer.valueOf(192000));
          i = 255;
          break;
        }
        if (((String)localObject).equalsIgnoreCase("audio/opus"))
        {
          localRange1 = Range.create(Integer.valueOf(6000), Integer.valueOf(510000));
          arrayOfInt = new int[] { 8000, 12000, 16000, 24000, 48000 };
          i = 255;
          break;
        }
        if (((String)localObject).equalsIgnoreCase("audio/raw"))
        {
          localRange2 = Range.create(Integer.valueOf(1), Integer.valueOf(96000));
          localRange1 = Range.create(Integer.valueOf(1), Integer.valueOf(10000000));
          i = AudioTrack.CHANNEL_COUNT_MAX;
          break;
        }
        if (((String)localObject).equalsIgnoreCase("audio/flac"))
        {
          localRange2 = Range.create(Integer.valueOf(1), Integer.valueOf(655350));
          i = 255;
          break;
        }
        if ((((String)localObject).equalsIgnoreCase("audio/g711-alaw")) || (((String)localObject).equalsIgnoreCase("audio/g711-mlaw")))
        {
          arrayOfInt = new int[1];
          arrayOfInt[0] = 8000;
          localRange1 = Range.create(Integer.valueOf(64000), Integer.valueOf(64000));
          break;
        }
        if (((String)localObject).equalsIgnoreCase("audio/gsm"))
        {
          arrayOfInt = new int[1];
          arrayOfInt[0] = 8000;
          localRange1 = Range.create(Integer.valueOf(13000), Integer.valueOf(13000));
          i = 1;
          break;
        }
        Log.w("AudioCapabilities", "Unsupported mime " + (String)localObject);
        localObject = this.mParent;
        ((MediaCodecInfo.CodecCapabilities)localObject).mError |= 0x2;
        break;
        label646:
        if (localRange2 != null) {
          limitSampleRates(new Range[] { localRange2 });
        }
      }
    }
    
    private void applyLimits(int paramInt, Range<Integer> paramRange)
    {
      this.mMaxInputChannelCount = ((Integer)Range.create(Integer.valueOf(1), Integer.valueOf(this.mMaxInputChannelCount)).clamp(Integer.valueOf(paramInt))).intValue();
      if (paramRange != null) {
        this.mBitrateRange = this.mBitrateRange.intersect(paramRange);
      }
    }
    
    public static AudioCapabilities create(MediaFormat paramMediaFormat, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
    {
      AudioCapabilities localAudioCapabilities = new AudioCapabilities();
      localAudioCapabilities.init(paramMediaFormat, paramCodecCapabilities);
      return localAudioCapabilities;
    }
    
    private void createDiscreteSampleRates()
    {
      this.mSampleRates = new int[this.mSampleRateRanges.length];
      int i = 0;
      while (i < this.mSampleRateRanges.length)
      {
        this.mSampleRates[i] = ((Integer)this.mSampleRateRanges[i].getLower()).intValue();
        i += 1;
      }
    }
    
    private void initWithPlatformLimits()
    {
      this.mBitrateRange = Range.create(Integer.valueOf(0), Integer.valueOf(Integer.MAX_VALUE));
      this.mMaxInputChannelCount = 30;
      this.mSampleRateRanges = new Range[] { Range.create(Integer.valueOf(8000), Integer.valueOf(96000)) };
      this.mSampleRates = null;
    }
    
    private void limitSampleRates(int[] paramArrayOfInt)
    {
      Arrays.sort(paramArrayOfInt);
      ArrayList localArrayList = new ArrayList();
      int i = 0;
      int j = paramArrayOfInt.length;
      while (i < j)
      {
        int k = paramArrayOfInt[i];
        if (supports(Integer.valueOf(k), null)) {
          localArrayList.add(Range.create(Integer.valueOf(k), Integer.valueOf(k)));
        }
        i += 1;
      }
      this.mSampleRateRanges = ((Range[])localArrayList.toArray(new Range[localArrayList.size()]));
      createDiscreteSampleRates();
    }
    
    private void limitSampleRates(Range<Integer>[] paramArrayOfRange)
    {
      Utils.sortDistinctRanges(paramArrayOfRange);
      this.mSampleRateRanges = Utils.intersectSortedDistinctRanges(this.mSampleRateRanges, paramArrayOfRange);
      paramArrayOfRange = this.mSampleRateRanges;
      int j = paramArrayOfRange.length;
      int i = 0;
      while (i < j)
      {
        Range<Integer> localRange = paramArrayOfRange[i];
        if (!((Integer)localRange.getLower()).equals(localRange.getUpper()))
        {
          this.mSampleRates = null;
          return;
        }
        i += 1;
      }
      createDiscreteSampleRates();
    }
    
    private void parseFromInfo(MediaFormat paramMediaFormat)
    {
      int j = 30;
      Range localRange = MediaCodecInfo.-get2();
      if (paramMediaFormat.containsKey("sample-rate-ranges"))
      {
        localObject = paramMediaFormat.getString("sample-rate-ranges").split(",");
        Range[] arrayOfRange = new Range[localObject.length];
        i = 0;
        while (i < localObject.length)
        {
          arrayOfRange[i] = Utils.parseIntRange(localObject[i], null);
          i += 1;
        }
        limitSampleRates(arrayOfRange);
      }
      int i = j;
      if (paramMediaFormat.containsKey("max-channel-count")) {
        i = Utils.parseIntSafely(paramMediaFormat.getString("max-channel-count"), 30);
      }
      Object localObject = localRange;
      if (paramMediaFormat.containsKey("bitrate-range")) {
        localObject = localRange.intersect(Utils.parseIntRange(paramMediaFormat.getString("bitrate-range"), localRange));
      }
      applyLimits(i, (Range)localObject);
    }
    
    private boolean supports(Integer paramInteger1, Integer paramInteger2)
    {
      if ((paramInteger2 != null) && ((paramInteger2.intValue() < 1) || (paramInteger2.intValue() > this.mMaxInputChannelCount))) {
        return false;
      }
      return (paramInteger1 == null) || (Utils.binarySearchDistinctRanges(this.mSampleRateRanges, paramInteger1) >= 0);
    }
    
    public Range<Integer> getBitrateRange()
    {
      return this.mBitrateRange;
    }
    
    public int getMaxInputChannelCount()
    {
      return this.mMaxInputChannelCount;
    }
    
    public Range<Integer>[] getSupportedSampleRateRanges()
    {
      return (Range[])Arrays.copyOf(this.mSampleRateRanges, this.mSampleRateRanges.length);
    }
    
    public int[] getSupportedSampleRates()
    {
      return Arrays.copyOf(this.mSampleRates, this.mSampleRates.length);
    }
    
    public void init(MediaFormat paramMediaFormat, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
    {
      this.mParent = paramCodecCapabilities;
      initWithPlatformLimits();
      applyLevelLimits();
      parseFromInfo(paramMediaFormat);
    }
    
    public boolean isSampleRateSupported(int paramInt)
    {
      return supports(Integer.valueOf(paramInt), null);
    }
    
    public void setDefaultFormat(MediaFormat paramMediaFormat)
    {
      if (((Integer)this.mBitrateRange.getLower()).equals(this.mBitrateRange.getUpper())) {
        paramMediaFormat.setInteger("bitrate", ((Integer)this.mBitrateRange.getLower()).intValue());
      }
      if (this.mMaxInputChannelCount == 1) {
        paramMediaFormat.setInteger("channel-count", 1);
      }
      if ((this.mSampleRates != null) && (this.mSampleRates.length == 1)) {
        paramMediaFormat.setInteger("sample-rate", this.mSampleRates[0]);
      }
    }
    
    public boolean supportsFormat(MediaFormat paramMediaFormat)
    {
      Map localMap = paramMediaFormat.getMap();
      if (!supports((Integer)localMap.get("sample-rate"), (Integer)localMap.get("channel-count"))) {
        return false;
      }
      return MediaCodecInfo.CodecCapabilities.-wrap0(this.mBitrateRange, paramMediaFormat);
    }
  }
  
  public static final class CodecCapabilities
  {
    public static final int COLOR_Format12bitRGB444 = 3;
    public static final int COLOR_Format16bitARGB1555 = 5;
    public static final int COLOR_Format16bitARGB4444 = 4;
    public static final int COLOR_Format16bitBGR565 = 7;
    public static final int COLOR_Format16bitRGB565 = 6;
    public static final int COLOR_Format18BitBGR666 = 41;
    public static final int COLOR_Format18bitARGB1665 = 9;
    public static final int COLOR_Format18bitRGB666 = 8;
    public static final int COLOR_Format19bitARGB1666 = 10;
    public static final int COLOR_Format24BitABGR6666 = 43;
    public static final int COLOR_Format24BitARGB6666 = 42;
    public static final int COLOR_Format24bitARGB1887 = 13;
    public static final int COLOR_Format24bitBGR888 = 12;
    public static final int COLOR_Format24bitRGB888 = 11;
    public static final int COLOR_Format25bitARGB1888 = 14;
    public static final int COLOR_Format32bitABGR8888 = 2130747392;
    public static final int COLOR_Format32bitARGB8888 = 16;
    public static final int COLOR_Format32bitBGRA8888 = 15;
    public static final int COLOR_Format8bitRGB332 = 2;
    public static final int COLOR_FormatCbYCrY = 27;
    public static final int COLOR_FormatCrYCbY = 28;
    public static final int COLOR_FormatL16 = 36;
    public static final int COLOR_FormatL2 = 33;
    public static final int COLOR_FormatL24 = 37;
    public static final int COLOR_FormatL32 = 38;
    public static final int COLOR_FormatL4 = 34;
    public static final int COLOR_FormatL8 = 35;
    public static final int COLOR_FormatMonochrome = 1;
    public static final int COLOR_FormatRGBAFlexible = 2134288520;
    public static final int COLOR_FormatRGBFlexible = 2134292616;
    public static final int COLOR_FormatRawBayer10bit = 31;
    public static final int COLOR_FormatRawBayer8bit = 30;
    public static final int COLOR_FormatRawBayer8bitcompressed = 32;
    public static final int COLOR_FormatSurface = 2130708361;
    public static final int COLOR_FormatYCbYCr = 25;
    public static final int COLOR_FormatYCrYCb = 26;
    public static final int COLOR_FormatYUV411PackedPlanar = 18;
    public static final int COLOR_FormatYUV411Planar = 17;
    public static final int COLOR_FormatYUV420Flexible = 2135033992;
    public static final int COLOR_FormatYUV420PackedPlanar = 20;
    public static final int COLOR_FormatYUV420PackedSemiPlanar = 39;
    public static final int COLOR_FormatYUV420Planar = 19;
    public static final int COLOR_FormatYUV420SemiPlanar = 21;
    public static final int COLOR_FormatYUV422Flexible = 2135042184;
    public static final int COLOR_FormatYUV422PackedPlanar = 23;
    public static final int COLOR_FormatYUV422PackedSemiPlanar = 40;
    public static final int COLOR_FormatYUV422Planar = 22;
    public static final int COLOR_FormatYUV422SemiPlanar = 24;
    public static final int COLOR_FormatYUV444Flexible = 2135181448;
    public static final int COLOR_FormatYUV444Interleaved = 29;
    public static final int COLOR_QCOM_FormatYUV420SemiPlanar = 2141391872;
    public static final int COLOR_TI_FormatYUV420PackedSemiPlanar = 2130706688;
    public static final String FEATURE_AdaptivePlayback = "adaptive-playback";
    public static final String FEATURE_IntraRefresh = "intra-refresh";
    public static final String FEATURE_SecurePlayback = "secure-playback";
    public static final String FEATURE_TunneledPlayback = "tunneled-playback";
    private static final String TAG = "CodecCapabilities";
    private static final MediaCodecInfo.Feature[] decoderFeatures = { new MediaCodecInfo.Feature("adaptive-playback", 1, true), new MediaCodecInfo.Feature("secure-playback", 2, false), new MediaCodecInfo.Feature("tunneled-playback", 4, false) };
    private static final MediaCodecInfo.Feature[] encoderFeatures = { new MediaCodecInfo.Feature("intra-refresh", 1, false) };
    public int[] colorFormats;
    private MediaCodecInfo.AudioCapabilities mAudioCaps;
    private MediaFormat mCapabilitiesInfo;
    private MediaFormat mDefaultFormat;
    private MediaCodecInfo.EncoderCapabilities mEncoderCaps;
    int mError;
    private int mFlagsRequired;
    private int mFlagsSupported;
    private int mFlagsVerified;
    private int mMaxSupportedInstances;
    private String mMime;
    private MediaCodecInfo.VideoCapabilities mVideoCaps;
    public MediaCodecInfo.CodecProfileLevel[] profileLevels;
    
    public CodecCapabilities() {}
    
    CodecCapabilities(MediaCodecInfo.CodecProfileLevel[] paramArrayOfCodecProfileLevel, int[] paramArrayOfInt, boolean paramBoolean, int paramInt, MediaFormat paramMediaFormat1, MediaFormat paramMediaFormat2)
    {
      Map localMap = paramMediaFormat2.getMap();
      this.colorFormats = paramArrayOfInt;
      this.mFlagsVerified = paramInt;
      this.mDefaultFormat = paramMediaFormat1;
      this.mCapabilitiesInfo = paramMediaFormat2;
      this.mMime = this.mDefaultFormat.getString("mime");
      paramArrayOfInt = paramArrayOfCodecProfileLevel;
      if (paramArrayOfCodecProfileLevel.length == 0)
      {
        paramArrayOfInt = paramArrayOfCodecProfileLevel;
        if (this.mMime.equalsIgnoreCase("video/x-vnd.on2.vp9"))
        {
          paramArrayOfCodecProfileLevel = new MediaCodecInfo.CodecProfileLevel();
          paramArrayOfCodecProfileLevel.profile = 1;
          paramArrayOfCodecProfileLevel.level = MediaCodecInfo.VideoCapabilities.equivalentVP9Level(paramMediaFormat2);
          paramArrayOfInt = new MediaCodecInfo.CodecProfileLevel[1];
          paramArrayOfInt[0] = paramArrayOfCodecProfileLevel;
        }
      }
      this.profileLevels = paramArrayOfInt;
      if (this.mMime.toLowerCase().startsWith("audio/"))
      {
        this.mAudioCaps = MediaCodecInfo.AudioCapabilities.create(paramMediaFormat2, this);
        this.mAudioCaps.setDefaultFormat(this.mDefaultFormat);
        if (paramBoolean)
        {
          this.mEncoderCaps = MediaCodecInfo.EncoderCapabilities.create(paramMediaFormat2, this);
          this.mEncoderCaps.setDefaultFormat(this.mDefaultFormat);
        }
        this.mMaxSupportedInstances = Utils.parseIntSafely(MediaCodecList.getGlobalSettings().get("max-concurrent-instances"), 32);
        paramInt = Utils.parseIntSafely(localMap.get("max-concurrent-instances"), this.mMaxSupportedInstances);
        this.mMaxSupportedInstances = ((Integer)Range.create(Integer.valueOf(1), Integer.valueOf(256)).clamp(Integer.valueOf(paramInt))).intValue();
        paramArrayOfCodecProfileLevel = getValidFeatures();
        paramInt = 0;
        int i = paramArrayOfCodecProfileLevel.length;
        label245:
        if (paramInt >= i) {
          return;
        }
        paramArrayOfInt = paramArrayOfCodecProfileLevel[paramInt];
        paramMediaFormat1 = "feature-" + paramArrayOfInt.mName;
        paramMediaFormat2 = (Integer)localMap.get(paramMediaFormat1);
        if (paramMediaFormat2 != null) {
          break label339;
        }
      }
      for (;;)
      {
        paramInt += 1;
        break label245;
        if (!this.mMime.toLowerCase().startsWith("video/")) {
          break;
        }
        this.mVideoCaps = MediaCodecInfo.VideoCapabilities.create(paramMediaFormat2, this);
        break;
        label339:
        if (paramMediaFormat2.intValue() > 0) {
          this.mFlagsRequired |= paramArrayOfInt.mValue;
        }
        this.mFlagsSupported |= paramArrayOfInt.mValue;
        this.mDefaultFormat.setInteger(paramMediaFormat1, 1);
      }
    }
    
    CodecCapabilities(MediaCodecInfo.CodecProfileLevel[] paramArrayOfCodecProfileLevel, int[] paramArrayOfInt, boolean paramBoolean, int paramInt, Map<String, Object> paramMap1, Map<String, Object> paramMap2)
    {
      this(paramArrayOfCodecProfileLevel, paramArrayOfInt, paramBoolean, paramInt, new MediaFormat(paramMap1), new MediaFormat(paramMap2));
    }
    
    private boolean checkFeature(String paramString, int paramInt)
    {
      boolean bool = false;
      MediaCodecInfo.Feature[] arrayOfFeature = getValidFeatures();
      int j = arrayOfFeature.length;
      int i = 0;
      while (i < j)
      {
        MediaCodecInfo.Feature localFeature = arrayOfFeature[i];
        if (localFeature.mName.equals(paramString))
        {
          if ((localFeature.mValue & paramInt) != 0) {
            bool = true;
          }
          return bool;
        }
        i += 1;
      }
      return false;
    }
    
    public static CodecCapabilities createFromProfileLevel(String paramString, int paramInt1, int paramInt2)
    {
      MediaCodecInfo.CodecProfileLevel localCodecProfileLevel = new MediaCodecInfo.CodecProfileLevel();
      localCodecProfileLevel.profile = paramInt1;
      localCodecProfileLevel.level = paramInt2;
      MediaFormat localMediaFormat = new MediaFormat();
      localMediaFormat.setString("mime", paramString);
      paramString = new MediaFormat();
      paramString = new CodecCapabilities(new MediaCodecInfo.CodecProfileLevel[] { localCodecProfileLevel }, new int[0], true, 0, localMediaFormat, paramString);
      if (paramString.mError != 0) {
        return null;
      }
      return paramString;
    }
    
    private MediaCodecInfo.Feature[] getValidFeatures()
    {
      if (!isEncoder()) {
        return decoderFeatures;
      }
      return encoderFeatures;
    }
    
    private boolean isAudio()
    {
      return this.mAudioCaps != null;
    }
    
    private boolean isEncoder()
    {
      return this.mEncoderCaps != null;
    }
    
    private boolean isVideo()
    {
      return this.mVideoCaps != null;
    }
    
    private static boolean supportsBitrate(Range<Integer> paramRange, MediaFormat paramMediaFormat)
    {
      paramMediaFormat = paramMediaFormat.getMap();
      Integer localInteger1 = (Integer)paramMediaFormat.get("max-bitrate");
      Integer localInteger2 = (Integer)paramMediaFormat.get("bitrate");
      if (localInteger2 == null) {
        paramMediaFormat = localInteger1;
      }
      while ((paramMediaFormat != null) && (paramMediaFormat.intValue() > 0))
      {
        return paramRange.contains(paramMediaFormat);
        paramMediaFormat = localInteger2;
        if (localInteger1 != null) {
          paramMediaFormat = Integer.valueOf(Math.max(localInteger2.intValue(), localInteger1.intValue()));
        }
      }
      return true;
    }
    
    private boolean supportsProfileLevel(int paramInt, Integer paramInteger)
    {
      MediaCodecInfo.CodecProfileLevel[] arrayOfCodecProfileLevel = this.profileLevels;
      int m = arrayOfCodecProfileLevel.length;
      int i = 0;
      if (i < m)
      {
        MediaCodecInfo.CodecProfileLevel localCodecProfileLevel = arrayOfCodecProfileLevel[i];
        if (localCodecProfileLevel.profile != paramInt) {}
        label173:
        label242:
        label248:
        label252:
        for (;;)
        {
          i += 1;
          break;
          if ((paramInteger == null) || (this.mMime.equalsIgnoreCase("audio/mp4a-latm"))) {
            return true;
          }
          if (((!this.mMime.equalsIgnoreCase("video/3gpp")) || (localCodecProfileLevel.level == paramInteger.intValue()) || (localCodecProfileLevel.level != 16) || (paramInteger.intValue() <= 1)) && ((!this.mMime.equalsIgnoreCase("video/mp4v-es")) || (localCodecProfileLevel.level == paramInteger.intValue()) || (localCodecProfileLevel.level != 4) || (paramInteger.intValue() <= 1)))
          {
            int j;
            if (this.mMime.equalsIgnoreCase("video/hevc"))
            {
              if ((localCodecProfileLevel.level & 0x2AAAAAA) == 0) {
                break label242;
              }
              j = 1;
              if ((paramInteger.intValue() & 0x2AAAAAA) == 0) {
                break label248;
              }
            }
            for (int k = 1;; k = 0)
            {
              if ((k != 0) && (j == 0)) {
                break label252;
              }
              if (localCodecProfileLevel.level < paramInteger.intValue()) {
                break;
              }
              if (createFromProfileLevel(this.mMime, paramInt, localCodecProfileLevel.level) == null) {
                break label256;
              }
              if (createFromProfileLevel(this.mMime, paramInt, paramInteger.intValue()) == null) {
                break label254;
              }
              return true;
              j = 0;
              break label173;
            }
          }
        }
        label254:
        return false;
        label256:
        return true;
      }
      return false;
    }
    
    public CodecCapabilities dup()
    {
      return new CodecCapabilities((MediaCodecInfo.CodecProfileLevel[])Arrays.copyOf(this.profileLevels, this.profileLevels.length), Arrays.copyOf(this.colorFormats, this.colorFormats.length), isEncoder(), this.mFlagsVerified, this.mDefaultFormat, this.mCapabilitiesInfo);
    }
    
    public MediaCodecInfo.AudioCapabilities getAudioCapabilities()
    {
      return this.mAudioCaps;
    }
    
    public MediaFormat getCapabilitiesInfoFormat()
    {
      return this.mCapabilitiesInfo;
    }
    
    public MediaFormat getDefaultFormat()
    {
      return this.mDefaultFormat;
    }
    
    public MediaCodecInfo.EncoderCapabilities getEncoderCapabilities()
    {
      return this.mEncoderCaps;
    }
    
    public int getMaxSupportedInstances()
    {
      return this.mMaxSupportedInstances;
    }
    
    public String getMimeType()
    {
      return this.mMime;
    }
    
    public MediaCodecInfo.VideoCapabilities getVideoCapabilities()
    {
      return this.mVideoCaps;
    }
    
    public final boolean isFeatureRequired(String paramString)
    {
      return checkFeature(paramString, this.mFlagsRequired);
    }
    
    public final boolean isFeatureSupported(String paramString)
    {
      return checkFeature(paramString, this.mFlagsSupported);
    }
    
    public final boolean isFormatSupported(MediaFormat paramMediaFormat)
    {
      Object localObject1 = paramMediaFormat.getMap();
      Object localObject2 = (String)((Map)localObject1).get("mime");
      int i;
      int j;
      Integer localInteger;
      if ((localObject2 == null) || (this.mMime.equalsIgnoreCase((String)localObject2)))
      {
        localObject2 = getValidFeatures();
        i = 0;
        j = localObject2.length;
        if (i >= j) {
          break label153;
        }
        localObject3 = localObject2[i];
        localInteger = (Integer)((Map)localObject1).get("feature-" + ((MediaCodecInfo.Feature)localObject3).mName);
        if (localInteger != null) {
          break label110;
        }
      }
      label110:
      while (((localInteger.intValue() != 1) || (isFeatureSupported(((MediaCodecInfo.Feature)localObject3).mName))) && ((localInteger.intValue() != 0) || (!isFeatureRequired(((MediaCodecInfo.Feature)localObject3).mName))))
      {
        i += 1;
        break;
        return false;
      }
      return false;
      label153:
      localObject2 = (Integer)((Map)localObject1).get("profile");
      Object localObject3 = (Integer)((Map)localObject1).get("level");
      if (localObject2 != null)
      {
        if (!supportsProfileLevel(((Integer)localObject2).intValue(), (Integer)localObject3)) {
          return false;
        }
        j = 0;
        localObject3 = this.profileLevels;
        i = 0;
        int m = localObject3.length;
        while (i < m)
        {
          localInteger = localObject3[i];
          int k = j;
          if (localInteger.profile == ((Integer)localObject2).intValue())
          {
            k = j;
            if (localInteger.level > j) {
              k = localInteger.level;
            }
          }
          i += 1;
          j = k;
        }
        localObject2 = createFromProfileLevel(this.mMime, ((Integer)localObject2).intValue(), j);
        localObject1 = new HashMap((Map)localObject1);
        ((Map)localObject1).remove("profile");
        localObject1 = new MediaFormat((Map)localObject1);
        if ((localObject2 != null) && (!((CodecCapabilities)localObject2).isFormatSupported((MediaFormat)localObject1))) {}
      }
      else
      {
        if ((this.mAudioCaps != null) && (!this.mAudioCaps.supportsFormat(paramMediaFormat))) {
          break label397;
        }
        if ((this.mVideoCaps != null) && (!this.mVideoCaps.supportsFormat(paramMediaFormat))) {
          break label399;
        }
        if ((this.mEncoderCaps != null) && (!this.mEncoderCaps.supportsFormat(paramMediaFormat))) {
          break label401;
        }
        return true;
      }
      return false;
      label397:
      return false;
      label399:
      return false;
      label401:
      return false;
    }
    
    public boolean isRegular()
    {
      MediaCodecInfo.Feature[] arrayOfFeature = getValidFeatures();
      int j = arrayOfFeature.length;
      int i = 0;
      while (i < j)
      {
        MediaCodecInfo.Feature localFeature = arrayOfFeature[i];
        if ((!localFeature.mDefault) && (isFeatureRequired(localFeature.mName))) {
          return false;
        }
        i += 1;
      }
      return true;
    }
    
    public String[] validFeatures()
    {
      MediaCodecInfo.Feature[] arrayOfFeature = getValidFeatures();
      String[] arrayOfString = new String[arrayOfFeature.length];
      int i = 0;
      while (i < arrayOfString.length)
      {
        arrayOfString[i] = arrayOfFeature[i].mName;
        i += 1;
      }
      return arrayOfString;
    }
  }
  
  public static final class CodecProfileLevel
  {
    public static final int AACObjectELD = 39;
    public static final int AACObjectERLC = 17;
    public static final int AACObjectHE = 5;
    public static final int AACObjectHE_PS = 29;
    public static final int AACObjectLC = 2;
    public static final int AACObjectLD = 23;
    public static final int AACObjectLTP = 4;
    public static final int AACObjectMain = 1;
    public static final int AACObjectSSR = 3;
    public static final int AACObjectScalable = 6;
    public static final int AVCLevel1 = 1;
    public static final int AVCLevel11 = 4;
    public static final int AVCLevel12 = 8;
    public static final int AVCLevel13 = 16;
    public static final int AVCLevel1b = 2;
    public static final int AVCLevel2 = 32;
    public static final int AVCLevel21 = 64;
    public static final int AVCLevel22 = 128;
    public static final int AVCLevel3 = 256;
    public static final int AVCLevel31 = 512;
    public static final int AVCLevel32 = 1024;
    public static final int AVCLevel4 = 2048;
    public static final int AVCLevel41 = 4096;
    public static final int AVCLevel42 = 8192;
    public static final int AVCLevel5 = 16384;
    public static final int AVCLevel51 = 32768;
    public static final int AVCLevel52 = 65536;
    public static final int AVCProfileBaseline = 1;
    public static final int AVCProfileExtended = 4;
    public static final int AVCProfileHigh = 8;
    public static final int AVCProfileHigh10 = 16;
    public static final int AVCProfileHigh422 = 32;
    public static final int AVCProfileHigh444 = 64;
    public static final int AVCProfileMain = 2;
    public static final int DolbyVisionLevelFhd24 = 4;
    public static final int DolbyVisionLevelFhd30 = 8;
    public static final int DolbyVisionLevelFhd60 = 16;
    public static final int DolbyVisionLevelHd24 = 1;
    public static final int DolbyVisionLevelHd30 = 2;
    public static final int DolbyVisionLevelUhd24 = 32;
    public static final int DolbyVisionLevelUhd30 = 64;
    public static final int DolbyVisionLevelUhd48 = 128;
    public static final int DolbyVisionLevelUhd60 = 256;
    public static final int DolbyVisionProfileDvavPen = 2;
    public static final int DolbyVisionProfileDvavPer = 1;
    public static final int DolbyVisionProfileDvheDen = 8;
    public static final int DolbyVisionProfileDvheDer = 4;
    public static final int DolbyVisionProfileDvheDtb = 128;
    public static final int DolbyVisionProfileDvheDth = 64;
    public static final int DolbyVisionProfileDvheDtr = 16;
    public static final int DolbyVisionProfileDvheStn = 32;
    public static final int H263Level10 = 1;
    public static final int H263Level20 = 2;
    public static final int H263Level30 = 4;
    public static final int H263Level40 = 8;
    public static final int H263Level45 = 16;
    public static final int H263Level50 = 32;
    public static final int H263Level60 = 64;
    public static final int H263Level70 = 128;
    public static final int H263ProfileBackwardCompatible = 4;
    public static final int H263ProfileBaseline = 1;
    public static final int H263ProfileH320Coding = 2;
    public static final int H263ProfileHighCompression = 32;
    public static final int H263ProfileHighLatency = 256;
    public static final int H263ProfileISWV2 = 8;
    public static final int H263ProfileISWV3 = 16;
    public static final int H263ProfileInterlace = 128;
    public static final int H263ProfileInternet = 64;
    public static final int HEVCHighTierLevel1 = 2;
    public static final int HEVCHighTierLevel2 = 8;
    public static final int HEVCHighTierLevel21 = 32;
    public static final int HEVCHighTierLevel3 = 128;
    public static final int HEVCHighTierLevel31 = 512;
    public static final int HEVCHighTierLevel4 = 2048;
    public static final int HEVCHighTierLevel41 = 8192;
    public static final int HEVCHighTierLevel5 = 32768;
    public static final int HEVCHighTierLevel51 = 131072;
    public static final int HEVCHighTierLevel52 = 524288;
    public static final int HEVCHighTierLevel6 = 2097152;
    public static final int HEVCHighTierLevel61 = 8388608;
    public static final int HEVCHighTierLevel62 = 33554432;
    private static final int HEVCHighTierLevels = 44739242;
    public static final int HEVCMainTierLevel1 = 1;
    public static final int HEVCMainTierLevel2 = 4;
    public static final int HEVCMainTierLevel21 = 16;
    public static final int HEVCMainTierLevel3 = 64;
    public static final int HEVCMainTierLevel31 = 256;
    public static final int HEVCMainTierLevel4 = 1024;
    public static final int HEVCMainTierLevel41 = 4096;
    public static final int HEVCMainTierLevel5 = 16384;
    public static final int HEVCMainTierLevel51 = 65536;
    public static final int HEVCMainTierLevel52 = 262144;
    public static final int HEVCMainTierLevel6 = 1048576;
    public static final int HEVCMainTierLevel61 = 4194304;
    public static final int HEVCMainTierLevel62 = 16777216;
    public static final int HEVCProfileMain = 1;
    public static final int HEVCProfileMain10 = 2;
    public static final int HEVCProfileMain10HDR10 = 4096;
    public static final int MPEG2LevelH14 = 2;
    public static final int MPEG2LevelHL = 3;
    public static final int MPEG2LevelHP = 4;
    public static final int MPEG2LevelLL = 0;
    public static final int MPEG2LevelML = 1;
    public static final int MPEG2Profile422 = 2;
    public static final int MPEG2ProfileHigh = 5;
    public static final int MPEG2ProfileMain = 1;
    public static final int MPEG2ProfileSNR = 3;
    public static final int MPEG2ProfileSimple = 0;
    public static final int MPEG2ProfileSpatial = 4;
    public static final int MPEG4Level0 = 1;
    public static final int MPEG4Level0b = 2;
    public static final int MPEG4Level1 = 4;
    public static final int MPEG4Level2 = 8;
    public static final int MPEG4Level3 = 16;
    public static final int MPEG4Level3b = 24;
    public static final int MPEG4Level4 = 32;
    public static final int MPEG4Level4a = 64;
    public static final int MPEG4Level5 = 128;
    public static final int MPEG4Level6 = 256;
    public static final int MPEG4ProfileAdvancedCoding = 4096;
    public static final int MPEG4ProfileAdvancedCore = 8192;
    public static final int MPEG4ProfileAdvancedRealTime = 1024;
    public static final int MPEG4ProfileAdvancedScalable = 16384;
    public static final int MPEG4ProfileAdvancedSimple = 32768;
    public static final int MPEG4ProfileBasicAnimated = 256;
    public static final int MPEG4ProfileCore = 4;
    public static final int MPEG4ProfileCoreScalable = 2048;
    public static final int MPEG4ProfileHybrid = 512;
    public static final int MPEG4ProfileMain = 8;
    public static final int MPEG4ProfileNbit = 16;
    public static final int MPEG4ProfileScalableTexture = 32;
    public static final int MPEG4ProfileSimple = 1;
    public static final int MPEG4ProfileSimpleFBA = 128;
    public static final int MPEG4ProfileSimpleFace = 64;
    public static final int MPEG4ProfileSimpleScalable = 2;
    public static final int VP8Level_Version0 = 1;
    public static final int VP8Level_Version1 = 2;
    public static final int VP8Level_Version2 = 4;
    public static final int VP8Level_Version3 = 8;
    public static final int VP8ProfileMain = 1;
    public static final int VP9Level1 = 1;
    public static final int VP9Level11 = 2;
    public static final int VP9Level2 = 4;
    public static final int VP9Level21 = 8;
    public static final int VP9Level3 = 16;
    public static final int VP9Level31 = 32;
    public static final int VP9Level4 = 64;
    public static final int VP9Level41 = 128;
    public static final int VP9Level5 = 256;
    public static final int VP9Level51 = 512;
    public static final int VP9Level52 = 1024;
    public static final int VP9Level6 = 2048;
    public static final int VP9Level61 = 4096;
    public static final int VP9Level62 = 8192;
    public static final int VP9Profile0 = 1;
    public static final int VP9Profile1 = 2;
    public static final int VP9Profile2 = 4;
    public static final int VP9Profile2HDR = 4096;
    public static final int VP9Profile3 = 8;
    public static final int VP9Profile3HDR = 8192;
    public int level;
    public int profile;
  }
  
  public static final class EncoderCapabilities
  {
    public static final int BITRATE_MODE_CBR = 2;
    public static final int BITRATE_MODE_CQ = 0;
    public static final int BITRATE_MODE_VBR = 1;
    private static final MediaCodecInfo.Feature[] bitrates = { new MediaCodecInfo.Feature("VBR", 1, true), new MediaCodecInfo.Feature("CBR", 2, false), new MediaCodecInfo.Feature("CQ", 0, false) };
    private int mBitControl;
    private Range<Integer> mComplexityRange;
    private Integer mDefaultComplexity;
    private Integer mDefaultQuality;
    private MediaCodecInfo.CodecCapabilities mParent;
    private Range<Integer> mQualityRange;
    private String mQualityScale;
    
    private void applyLevelLimits()
    {
      String str = this.mParent.getMimeType();
      if (str.equalsIgnoreCase("audio/flac"))
      {
        this.mComplexityRange = Range.create(Integer.valueOf(0), Integer.valueOf(8));
        this.mBitControl = 1;
      }
      while ((!str.equalsIgnoreCase("audio/3gpp")) && (!str.equalsIgnoreCase("audio/amr-wb")) && (!str.equalsIgnoreCase("audio/g711-alaw")) && (!str.equalsIgnoreCase("audio/g711-mlaw")) && (!str.equalsIgnoreCase("audio/gsm"))) {
        return;
      }
      this.mBitControl = 4;
    }
    
    public static EncoderCapabilities create(MediaFormat paramMediaFormat, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
    {
      EncoderCapabilities localEncoderCapabilities = new EncoderCapabilities();
      localEncoderCapabilities.init(paramMediaFormat, paramCodecCapabilities);
      return localEncoderCapabilities;
    }
    
    private static int parseBitrateMode(String paramString)
    {
      MediaCodecInfo.Feature[] arrayOfFeature = bitrates;
      int j = arrayOfFeature.length;
      int i = 0;
      while (i < j)
      {
        MediaCodecInfo.Feature localFeature = arrayOfFeature[i];
        if (localFeature.mName.equalsIgnoreCase(paramString)) {
          return localFeature.mValue;
        }
        i += 1;
      }
      return 0;
    }
    
    private void parseFromInfo(MediaFormat paramMediaFormat)
    {
      Map localMap = paramMediaFormat.getMap();
      if (paramMediaFormat.containsKey("complexity-range")) {
        this.mComplexityRange = Utils.parseIntRange(paramMediaFormat.getString("complexity-range"), this.mComplexityRange);
      }
      if (paramMediaFormat.containsKey("quality-range")) {
        this.mQualityRange = Utils.parseIntRange(paramMediaFormat.getString("quality-range"), this.mQualityRange);
      }
      if (paramMediaFormat.containsKey("feature-bitrate-control"))
      {
        paramMediaFormat = paramMediaFormat.getString("feature-bitrate-control").split(",");
        int i = 0;
        int j = paramMediaFormat.length;
        while (i < j)
        {
          String str = paramMediaFormat[i];
          this.mBitControl |= parseBitrateMode(str);
          i += 1;
        }
      }
      try
      {
        this.mDefaultComplexity = Integer.valueOf(Integer.parseInt((String)localMap.get("complexity-default")));
        try
        {
          this.mDefaultQuality = Integer.valueOf(Integer.parseInt((String)localMap.get("quality-default")));
          this.mQualityScale = ((String)localMap.get("quality-scale"));
          return;
        }
        catch (NumberFormatException paramMediaFormat)
        {
          for (;;) {}
        }
      }
      catch (NumberFormatException paramMediaFormat)
      {
        for (;;) {}
      }
    }
    
    private boolean supports(Integer paramInteger1, Integer paramInteger2, Integer paramInteger3)
    {
      boolean bool2 = true;
      boolean bool1 = bool2;
      if (1 != 0)
      {
        bool1 = bool2;
        if (paramInteger1 != null) {
          bool1 = this.mComplexityRange.contains(paramInteger1);
        }
      }
      bool2 = bool1;
      if (bool1)
      {
        bool2 = bool1;
        if (paramInteger2 != null) {
          bool2 = this.mQualityRange.contains(paramInteger2);
        }
      }
      bool1 = bool2;
      int i;
      int j;
      if (bool2)
      {
        bool1 = bool2;
        if (paramInteger3 != null)
        {
          paramInteger2 = this.mParent.profileLevels;
          i = 0;
          j = paramInteger2.length;
        }
      }
      for (;;)
      {
        paramInteger1 = paramInteger3;
        if (i < j)
        {
          if (paramInteger2[i].profile == paramInteger3.intValue()) {
            paramInteger1 = null;
          }
        }
        else
        {
          if (paramInteger1 != null) {
            break;
          }
          bool1 = true;
          return bool1;
        }
        i += 1;
      }
      return false;
    }
    
    public Range<Integer> getComplexityRange()
    {
      return this.mComplexityRange;
    }
    
    public Range<Integer> getQualityRange()
    {
      return this.mQualityRange;
    }
    
    public void init(MediaFormat paramMediaFormat, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
    {
      this.mParent = paramCodecCapabilities;
      this.mComplexityRange = Range.create(Integer.valueOf(0), Integer.valueOf(0));
      this.mQualityRange = Range.create(Integer.valueOf(0), Integer.valueOf(0));
      this.mBitControl = 2;
      applyLevelLimits();
      parseFromInfo(paramMediaFormat);
    }
    
    public boolean isBitrateModeSupported(int paramInt)
    {
      MediaCodecInfo.Feature[] arrayOfFeature = bitrates;
      int j = arrayOfFeature.length;
      int i = 0;
      while (i < j)
      {
        if (paramInt == arrayOfFeature[i].mValue) {
          return (this.mBitControl & 1 << paramInt) != 0;
        }
        i += 1;
      }
      return false;
    }
    
    public void setDefaultFormat(MediaFormat paramMediaFormat)
    {
      if ((!((Integer)this.mQualityRange.getUpper()).equals(this.mQualityRange.getLower())) && (this.mDefaultQuality != null)) {
        paramMediaFormat.setInteger("quality", this.mDefaultQuality.intValue());
      }
      if ((!((Integer)this.mComplexityRange.getUpper()).equals(this.mComplexityRange.getLower())) && (this.mDefaultComplexity != null)) {
        paramMediaFormat.setInteger("complexity", this.mDefaultComplexity.intValue());
      }
      MediaCodecInfo.Feature[] arrayOfFeature = bitrates;
      int j = arrayOfFeature.length;
      int i = 0;
      for (;;)
      {
        if (i < j)
        {
          MediaCodecInfo.Feature localFeature = arrayOfFeature[i];
          if ((this.mBitControl & 1 << localFeature.mValue) != 0) {
            paramMediaFormat.setInteger("bitrate-mode", localFeature.mValue);
          }
        }
        else
        {
          return;
        }
        i += 1;
      }
    }
    
    public boolean supportsFormat(MediaFormat paramMediaFormat)
    {
      Map localMap = paramMediaFormat.getMap();
      Object localObject2 = this.mParent.getMimeType();
      paramMediaFormat = (Integer)localMap.get("bitrate-mode");
      Object localObject1;
      Integer localInteger;
      if ((paramMediaFormat == null) || (isBitrateModeSupported(paramMediaFormat.intValue())))
      {
        localObject1 = (Integer)localMap.get("complexity");
        paramMediaFormat = (MediaFormat)localObject1;
        if ("audio/flac".equalsIgnoreCase((String)localObject2))
        {
          localInteger = (Integer)localMap.get("flac-compression-level");
          if (localObject1 != null) {
            break label154;
          }
          paramMediaFormat = localInteger;
        }
        localInteger = (Integer)localMap.get("profile");
        localObject1 = localInteger;
        if ("audio/mp4a-latm".equalsIgnoreCase((String)localObject2))
        {
          localObject2 = (Integer)localMap.get("aac-profile");
          if (localInteger != null) {
            break label180;
          }
          localObject1 = localObject2;
        }
      }
      label154:
      label180:
      do
      {
        do
        {
          return supports(paramMediaFormat, (Integer)localMap.get("quality"), (Integer)localObject1);
          return false;
          paramMediaFormat = (MediaFormat)localObject1;
          if (localInteger == null) {
            break;
          }
          paramMediaFormat = (MediaFormat)localObject1;
          if (((Integer)localObject1).equals(localInteger)) {
            break;
          }
          throw new IllegalArgumentException("conflicting values for complexity and flac-compression-level");
          localObject1 = localInteger;
        } while (localObject2 == null);
        localObject1 = localInteger;
      } while (((Integer)localObject2).equals(localInteger));
      throw new IllegalArgumentException("conflicting values for profile and aac-profile");
    }
  }
  
  private static class Feature
  {
    public boolean mDefault;
    public String mName;
    public int mValue;
    
    public Feature(String paramString, int paramInt, boolean paramBoolean)
    {
      this.mName = paramString;
      this.mValue = paramInt;
      this.mDefault = paramBoolean;
    }
  }
  
  public static final class VideoCapabilities
  {
    private static final String TAG = "VideoCapabilities";
    private boolean mAllowMbOverride;
    private Range<Rational> mAspectRatioRange;
    private Range<Integer> mBitrateRange;
    private Range<Rational> mBlockAspectRatioRange;
    private Range<Integer> mBlockCountRange;
    private int mBlockHeight;
    private int mBlockWidth;
    private Range<Long> mBlocksPerSecondRange;
    private Range<Integer> mFrameRateRange;
    private int mHeightAlignment;
    private Range<Integer> mHeightRange;
    private Range<Integer> mHorizontalBlockRange;
    private Map<Size, Range<Long>> mMeasuredFrameRates;
    private MediaCodecInfo.CodecCapabilities mParent;
    private int mSmallerDimensionUpperLimit;
    private Range<Integer> mVerticalBlockRange;
    private int mWidthAlignment;
    private Range<Integer> mWidthRange;
    
    private void applyAlignment(int paramInt1, int paramInt2)
    {
      MediaCodecInfo.-wrap0(paramInt1, "widthAlignment must be a power of two");
      MediaCodecInfo.-wrap0(paramInt2, "heightAlignment must be a power of two");
      if ((paramInt1 > this.mBlockWidth) || (paramInt2 > this.mBlockHeight)) {
        applyBlockLimits(Math.max(paramInt1, this.mBlockWidth), Math.max(paramInt2, this.mBlockHeight), MediaCodecInfo.-get2(), MediaCodecInfo.-get3(), MediaCodecInfo.-get4());
      }
      this.mWidthAlignment = Math.max(paramInt1, this.mWidthAlignment);
      this.mHeightAlignment = Math.max(paramInt2, this.mHeightAlignment);
      this.mWidthRange = Utils.alignRange(this.mWidthRange, this.mWidthAlignment);
      this.mHeightRange = Utils.alignRange(this.mHeightRange, this.mHeightAlignment);
    }
    
    private void applyBlockLimits(int paramInt1, int paramInt2, Range<Integer> paramRange, Range<Long> paramRange1, Range<Rational> paramRange2)
    {
      MediaCodecInfo.-wrap0(paramInt1, "blockWidth must be a power of two");
      MediaCodecInfo.-wrap0(paramInt2, "blockHeight must be a power of two");
      int i = Math.max(paramInt1, this.mBlockWidth);
      int j = Math.max(paramInt2, this.mBlockHeight);
      int k = i * j / this.mBlockWidth / this.mBlockHeight;
      if (k != 1)
      {
        this.mBlockCountRange = Utils.factorRange(this.mBlockCountRange, k);
        this.mBlocksPerSecondRange = Utils.factorRange(this.mBlocksPerSecondRange, k);
        this.mBlockAspectRatioRange = Utils.scaleRange(this.mBlockAspectRatioRange, j / this.mBlockHeight, i / this.mBlockWidth);
        this.mHorizontalBlockRange = Utils.factorRange(this.mHorizontalBlockRange, i / this.mBlockWidth);
        this.mVerticalBlockRange = Utils.factorRange(this.mVerticalBlockRange, j / this.mBlockHeight);
      }
      k = i * j / paramInt1 / paramInt2;
      Object localObject3 = paramRange;
      Object localObject2 = paramRange1;
      Object localObject1 = paramRange2;
      if (k != 1)
      {
        localObject3 = Utils.factorRange(paramRange, k);
        localObject2 = Utils.factorRange(paramRange1, k);
        localObject1 = Utils.scaleRange(paramRange2, j / paramInt2, i / paramInt1);
      }
      this.mBlockCountRange = this.mBlockCountRange.intersect((Range)localObject3);
      this.mBlocksPerSecondRange = this.mBlocksPerSecondRange.intersect((Range)localObject2);
      this.mBlockAspectRatioRange = this.mBlockAspectRatioRange.intersect((Range)localObject1);
      this.mBlockWidth = i;
      this.mBlockHeight = j;
    }
    
    private void applyLevelLimits()
    {
      int i = 4;
      Object localObject1 = this.mParent.profileLevels;
      String str = this.mParent.getMimeType();
      int i5;
      long l1;
      int i1;
      int i4;
      int i6;
      int i8;
      int n;
      Object localObject2;
      int m;
      int k;
      int j;
      int i2;
      int i7;
      label285:
      int i3;
      if (str.equalsIgnoreCase("video/avc"))
      {
        i5 = 99;
        l1 = 1485L;
        i1 = 64000;
        i4 = 396;
        i6 = 0;
        i8 = localObject1.length;
        n = i;
        if (i6 < i8)
        {
          localObject2 = localObject1[i6];
          m = 0;
          k = 0;
          i = 0;
          j = 0;
          i2 = 1;
          i7 = 1;
          switch (((MediaCodecInfo.CodecProfileLevel)localObject2).level)
          {
          default: 
            Log.w("VideoCapabilities", "Unrecognized level " + ((MediaCodecInfo.CodecProfileLevel)localObject2).level + " for " + str);
            n |= 0x1;
            i3 = n;
            switch (((MediaCodecInfo.CodecProfileLevel)localObject2).profile)
            {
            default: 
              Log.w("VideoCapabilities", "Unrecognized profile " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + " for " + str);
              n |= 0x1;
              i *= 1000;
              i3 = i7;
            }
            break;
          }
          for (;;)
          {
            i2 = n;
            if (i3 != 0) {
              i2 = n & 0xFFFFFFFB;
            }
            l1 = Math.max(m, l1);
            i5 = Math.max(k, i5);
            i1 = Math.max(i, i1);
            i4 = Math.max(i4, j);
            i6 += 1;
            n = i2;
            break;
            m = 1485;
            k = 99;
            i = 64;
            j = 396;
            break label285;
            m = 1485;
            k = 99;
            i = 128;
            j = 396;
            break label285;
            m = 3000;
            k = 396;
            i = 192;
            j = 900;
            break label285;
            m = 6000;
            k = 396;
            i = 384;
            j = 2376;
            break label285;
            m = 11880;
            k = 396;
            i = 768;
            j = 2376;
            break label285;
            m = 11880;
            k = 396;
            i = 2000;
            j = 2376;
            break label285;
            m = 19800;
            k = 792;
            i = 4000;
            j = 4752;
            break label285;
            m = 20250;
            k = 1620;
            i = 4000;
            j = 8100;
            break label285;
            m = 40500;
            k = 1620;
            i = 10000;
            j = 8100;
            break label285;
            m = 108000;
            k = 3600;
            i = 14000;
            j = 18000;
            break label285;
            m = 216000;
            k = 5120;
            i = 20000;
            j = 20480;
            break label285;
            m = 245760;
            k = 8192;
            i = 20000;
            j = 32768;
            break label285;
            m = 245760;
            k = 8192;
            i = 50000;
            j = 32768;
            break label285;
            m = 522240;
            k = 8704;
            i = 50000;
            j = 34816;
            break label285;
            m = 589824;
            k = 22080;
            i = 135000;
            j = 110400;
            break label285;
            m = 983040;
            k = 36864;
            i = 240000;
            j = 184320;
            break label285;
            m = 2073600;
            k = 36864;
            i = 240000;
            j = 184320;
            break label285;
            i *= 1250;
            i3 = i7;
            continue;
            i *= 3000;
            i3 = i7;
            continue;
            Log.w("VideoCapabilities", "Unsupported profile " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + " for " + str);
            i3 = n | 0x2;
            i2 = 0;
            i *= 1000;
            n = i3;
            i3 = i2;
          }
        }
        i = (int)Math.sqrt(i5 * 8);
        applyMacroBlockLimits(i, i, i5, l1, 16, 16, 1, 1);
        j = i1;
        i = n;
      }
      for (;;)
      {
        this.mBitrateRange = Range.create(Integer.valueOf(1), Integer.valueOf(j));
        localObject1 = this.mParent;
        ((MediaCodecInfo.CodecCapabilities)localObject1).mError |= i;
        return;
        int i9;
        int i11;
        int i10;
        if (str.equalsIgnoreCase("video/mpeg2"))
        {
          i7 = 11;
          i6 = 9;
          i5 = 15;
          i9 = 99;
          l1 = 1485L;
          i3 = 64000;
          i8 = 0;
          i11 = localObject1.length;
          i2 = i;
          if (i8 < i11)
          {
            localObject2 = localObject1[i8];
            n = 0;
            k = 0;
            i = 0;
            j = 0;
            i1 = 0;
            m = 0;
            i10 = 1;
            switch (((MediaCodecInfo.CodecProfileLevel)localObject2).profile)
            {
            default: 
              Log.w("VideoCapabilities", "Unrecognized profile " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + " for " + str);
              i2 |= 0x1;
            }
            for (;;)
            {
              i4 = i2;
              if (i10 != 0) {
                i4 = i2 & 0xFFFFFFFB;
              }
              l1 = Math.max(n, l1);
              i9 = Math.max(k, i9);
              i3 = Math.max(i * 1000, i3);
              i7 = Math.max(i1, i7);
              i6 = Math.max(m, i6);
              i5 = Math.max(j, i5);
              i8 += 1;
              i2 = i4;
              break;
              switch (((MediaCodecInfo.CodecProfileLevel)localObject2).level)
              {
              default: 
                Log.w("VideoCapabilities", "Unrecognized profile/level " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + "/" + ((MediaCodecInfo.CodecProfileLevel)localObject2).level + " for " + str);
                i2 |= 0x1;
                break;
              case 1: 
                j = 30;
                i1 = 45;
                m = 36;
                n = 40500;
                k = 1620;
                i = 15000;
                continue;
                switch (((MediaCodecInfo.CodecProfileLevel)localObject2).level)
                {
                default: 
                  Log.w("VideoCapabilities", "Unrecognized profile/level " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + "/" + ((MediaCodecInfo.CodecProfileLevel)localObject2).level + " for " + str);
                  i2 |= 0x1;
                  break;
                case 0: 
                  j = 30;
                  i1 = 22;
                  m = 18;
                  n = 11880;
                  k = 396;
                  i = 4000;
                  break;
                case 1: 
                  j = 30;
                  i1 = 45;
                  m = 36;
                  n = 40500;
                  k = 1620;
                  i = 15000;
                  break;
                case 2: 
                  j = 60;
                  i1 = 90;
                  m = 68;
                  n = 183600;
                  k = 6120;
                  i = 60000;
                  break;
                case 3: 
                  j = 60;
                  i1 = 120;
                  m = 68;
                  n = 244800;
                  k = 8160;
                  i = 80000;
                  break;
                case 4: 
                  j = 60;
                  i1 = 120;
                  m = 68;
                  n = 489600;
                  k = 8160;
                  i = 80000;
                  continue;
                  Log.i("VideoCapabilities", "Unsupported profile " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + " for " + str);
                  i2 |= 0x2;
                  i10 = 0;
                }
                break;
              }
            }
          }
          applyMacroBlockLimits(i7, i6, i9, l1, 16, 16, 1, 1);
          this.mFrameRateRange = this.mFrameRateRange.intersect(Integer.valueOf(12), Integer.valueOf(i5));
          i = i2;
          j = i3;
        }
        else
        {
          int i12;
          if (str.equalsIgnoreCase("video/mp4v-es"))
          {
            i9 = 11;
            i8 = 9;
            i6 = 15;
            i10 = 99;
            l1 = 1485L;
            i3 = 64000;
            i7 = 0;
            i12 = localObject1.length;
            i2 = i;
            if (i7 < i12)
            {
              localObject2 = localObject1[i7];
              n = 0;
              k = 0;
              i = 0;
              j = 0;
              i1 = 0;
              m = 0;
              i5 = 0;
              i11 = 1;
              switch (((MediaCodecInfo.CodecProfileLevel)localObject2).profile)
              {
              default: 
                Log.w("VideoCapabilities", "Unrecognized profile " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + " for " + str);
                i2 |= 0x1;
                i4 = i2;
                if (i11 != 0) {
                  i4 = i2 & 0xFFFFFFFB;
                }
                l1 = Math.max(n, l1);
                i10 = Math.max(k, i10);
                i3 = Math.max(i * 1000, i3);
                if (i5 != 0)
                {
                  k = Math.max(i1, i9);
                  m = Math.max(m, i8);
                  i = Math.max(j, i6);
                  j = m;
                }
                break;
              }
              for (;;)
              {
                i7 += 1;
                i9 = k;
                i8 = j;
                i2 = i4;
                i6 = i;
                break;
                switch (((MediaCodecInfo.CodecProfileLevel)localObject2).level)
                {
                default: 
                  Log.w("VideoCapabilities", "Unrecognized profile/level " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + "/" + ((MediaCodecInfo.CodecProfileLevel)localObject2).level + " for " + str);
                  i2 |= 0x1;
                  break;
                case 1: 
                  i5 = 1;
                  j = 15;
                  i1 = 11;
                  m = 9;
                  n = 1485;
                  k = 99;
                  i = 64;
                  break;
                case 4: 
                  j = 30;
                  i1 = 11;
                  m = 9;
                  n = 1485;
                  k = 99;
                  i = 64;
                  break;
                case 2: 
                  i5 = 1;
                  j = 15;
                  i1 = 11;
                  m = 9;
                  n = 1485;
                  k = 99;
                  i = 128;
                  break;
                case 8: 
                  j = 30;
                  i1 = 22;
                  m = 18;
                  n = 5940;
                  k = 396;
                  i = 128;
                  break;
                case 16: 
                  j = 30;
                  i1 = 22;
                  m = 18;
                  n = 11880;
                  k = 396;
                  i = 384;
                  break;
                case 64: 
                  j = 30;
                  i1 = 40;
                  m = 30;
                  n = 36000;
                  k = 1200;
                  i = 4000;
                  break;
                case 128: 
                  j = 30;
                  i1 = 45;
                  m = 36;
                  n = 40500;
                  k = 1620;
                  i = 8000;
                  break;
                case 256: 
                  j = 30;
                  i1 = 80;
                  m = 45;
                  n = 108000;
                  k = 3600;
                  i = 12000;
                  break;
                  switch (((MediaCodecInfo.CodecProfileLevel)localObject2).level)
                  {
                  default: 
                    Log.w("VideoCapabilities", "Unrecognized profile/level " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + "/" + ((MediaCodecInfo.CodecProfileLevel)localObject2).level + " for " + str);
                    i2 |= 0x1;
                    break;
                  case 1: 
                  case 4: 
                    j = 30;
                    i1 = 11;
                    m = 9;
                    n = 2970;
                    k = 99;
                    i = 128;
                    break;
                  case 8: 
                    j = 30;
                    i1 = 22;
                    m = 18;
                    n = 5940;
                    k = 396;
                    i = 384;
                    break;
                  case 16: 
                    j = 30;
                    i1 = 22;
                    m = 18;
                    n = 11880;
                    k = 396;
                    i = 768;
                    break;
                  case 24: 
                    j = 30;
                    i1 = 22;
                    m = 18;
                    n = 11880;
                    k = 396;
                    i = 1500;
                    break;
                  case 32: 
                    j = 30;
                    i1 = 44;
                    m = 36;
                    n = 23760;
                    k = 792;
                    i = 3000;
                    break;
                  case 128: 
                    j = 30;
                    i1 = 45;
                    m = 36;
                    n = 48600;
                    k = 1620;
                    i = 8000;
                    break;
                    Log.i("VideoCapabilities", "Unsupported profile " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + " for " + str);
                    i2 |= 0x2;
                    i11 = 0;
                    break;
                    i = (int)Math.sqrt(k * 2);
                    k = Math.max(i, i9);
                    i = Math.max(i, i8);
                    m = Math.max(Math.max(j, 60), i6);
                    j = i;
                    i = m;
                  }
                  break;
                }
              }
            }
            applyMacroBlockLimits(i9, i8, i10, l1, 16, 16, 1, 1);
            this.mFrameRateRange = this.mFrameRateRange.intersect(Integer.valueOf(12), Integer.valueOf(i6));
            i = i2;
            j = i3;
          }
          else if (str.equalsIgnoreCase("video/3gpp"))
          {
            i12 = 11;
            i11 = 9;
            i10 = 15;
            i7 = 11;
            i5 = 9;
            i4 = 16;
            int i14 = 99;
            l1 = 1485L;
            i6 = 64000;
            int i13 = 0;
            int i15 = localObject1.length;
            i1 = i;
            if (i13 < i15)
            {
              localObject2 = localObject1[i13];
              m = 0;
              i = 0;
              j = 0;
              n = 0;
              k = 0;
              i2 = i7;
              i8 = i5;
              i3 = 0;
              switch (((MediaCodecInfo.CodecProfileLevel)localObject2).level)
              {
              default: 
                Log.w("VideoCapabilities", "Unrecognized profile/level " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + "/" + ((MediaCodecInfo.CodecProfileLevel)localObject2).level + " for " + str);
                i9 = i1 | 0x1;
                i1 = i8;
                label3134:
                i8 = i9;
                switch (((MediaCodecInfo.CodecProfileLevel)localObject2).profile)
                {
                default: 
                  Log.w("VideoCapabilities", "Unrecognized profile " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + " for " + str);
                  i8 = i9 | 0x1;
                }
                if (i3 != 0)
                {
                  i2 = 11;
                  i1 = 9;
                }
                break;
              }
              for (;;)
              {
                i3 = i8 & 0xFFFFFFFB;
                l1 = Math.max(m, l1);
                i14 = Math.max(n * k, i14);
                i6 = Math.max(64000 * i, i6);
                i12 = Math.max(n, i12);
                i11 = Math.max(k, i11);
                i10 = Math.max(j, i10);
                i7 = Math.min(i2, i7);
                i5 = Math.min(i1, i5);
                i13 += 1;
                i1 = i3;
                break;
                i3 = 1;
                j = 15;
                n = 11;
                k = 9;
                i = 1;
                m = 99 * 15;
                i9 = i1;
                i1 = i8;
                break label3134;
                i3 = 1;
                j = 30;
                n = 22;
                k = 18;
                i = 2;
                m = 'ƌ' * 15;
                i9 = i1;
                i1 = i8;
                break label3134;
                i3 = 1;
                j = 30;
                n = 22;
                k = 18;
                i = 6;
                m = 'ƌ' * 30;
                i9 = i1;
                i1 = i8;
                break label3134;
                i3 = 1;
                j = 30;
                n = 22;
                k = 18;
                i = 32;
                m = 'ƌ' * 30;
                i9 = i1;
                i1 = i8;
                break label3134;
                if (((MediaCodecInfo.CodecProfileLevel)localObject2).profile != 1)
                {
                  if (((MediaCodecInfo.CodecProfileLevel)localObject2).profile != 4) {
                    break label3598;
                  }
                  i3 = 1;
                }
                for (;;)
                {
                  if (i3 == 0)
                  {
                    i2 = 1;
                    i8 = 1;
                    i4 = 4;
                  }
                  j = 15;
                  n = 11;
                  k = 9;
                  i = 2;
                  m = 99 * 15;
                  i9 = i1;
                  i1 = i8;
                  break;
                  i3 = 1;
                  continue;
                  label3598:
                  i3 = 0;
                }
                i2 = 1;
                i8 = 1;
                i4 = 4;
                j = 60;
                n = 22;
                k = 18;
                i = 64;
                m = 'ƌ' * 50;
                i9 = i1;
                i1 = i8;
                break label3134;
                i2 = 1;
                i8 = 1;
                i4 = 4;
                j = 60;
                n = 45;
                k = 18;
                i = 128;
                m = '̪' * 50;
                i9 = i1;
                i1 = i8;
                break label3134;
                i2 = 1;
                i8 = 1;
                i4 = 4;
                j = 60;
                n = 45;
                k = 36;
                i = 256;
                m = 'ٔ' * 50;
                i9 = i1;
                i1 = i8;
                break label3134;
                this.mAllowMbOverride = true;
              }
            }
            if (!this.mAllowMbOverride) {
              this.mBlockAspectRatioRange = Range.create(new Rational(11, 9), new Rational(11, 9));
            }
            applyMacroBlockLimits(i7, i5, i12, i11, i14, l1, 16, 16, i4, i4);
            this.mFrameRateRange = Range.create(Integer.valueOf(1), Integer.valueOf(i10));
            i = i1;
            j = i6;
          }
          else if (str.equalsIgnoreCase("video/x-vnd.on2.vp8"))
          {
            m = 100000000;
            k = 0;
            n = localObject1.length;
            while (k < n)
            {
              localObject2 = localObject1[k];
              j = i;
              switch (((MediaCodecInfo.CodecProfileLevel)localObject2).level)
              {
              case 3: 
              case 5: 
              case 6: 
              case 7: 
              default: 
                Log.w("VideoCapabilities", "Unrecognized level " + ((MediaCodecInfo.CodecProfileLevel)localObject2).level + " for " + str);
                j = i | 0x1;
              }
              i = j;
              switch (((MediaCodecInfo.CodecProfileLevel)localObject2).profile)
              {
              default: 
                Log.w("VideoCapabilities", "Unrecognized profile " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + " for " + str);
                i = j | 0x1;
              }
              i &= 0xFFFFFFFB;
              k += 1;
            }
            applyMacroBlockLimits(32767, 32767, Integer.MAX_VALUE, 2147483647L, 16, 16, 1, 1);
            j = m;
          }
          else if (str.equalsIgnoreCase("video/x-vnd.on2.vp9"))
          {
            long l2 = 829440L;
            i2 = 36864;
            n = 200000;
            i1 = 512;
            i3 = 0;
            i5 = localObject1.length;
            m = i;
            if (i3 < i5)
            {
              localObject2 = localObject1[i3];
              l1 = 0L;
              k = 0;
              i = 0;
              j = 0;
              switch (((MediaCodecInfo.CodecProfileLevel)localObject2).level)
              {
              default: 
                Log.w("VideoCapabilities", "Unrecognized level " + ((MediaCodecInfo.CodecProfileLevel)localObject2).level + " for " + str);
                m |= 0x1;
              }
              for (;;)
              {
                i4 = m;
                switch (((MediaCodecInfo.CodecProfileLevel)localObject2).profile)
                {
                default: 
                  Log.w("VideoCapabilities", "Unrecognized profile " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + " for " + str);
                  i4 = m | 0x1;
                }
                m = i4 & 0xFFFFFFFB;
                l2 = Math.max(l1, l2);
                i2 = Math.max(k, i2);
                n = Math.max(i * 1000, n);
                i1 = Math.max(j, i1);
                i3 += 1;
                break;
                l1 = 829440L;
                k = 36864;
                i = 200;
                j = 512;
                continue;
                l1 = 2764800L;
                k = 73728;
                i = 800;
                j = 768;
                continue;
                l1 = 4608000L;
                k = 122880;
                i = 1800;
                j = 960;
                continue;
                l1 = 9216000L;
                k = 245760;
                i = 3600;
                j = 1344;
                continue;
                l1 = 20736000L;
                k = 552960;
                i = 7200;
                j = 2048;
                continue;
                l1 = 36864000L;
                k = 983040;
                i = 12000;
                j = 2752;
                continue;
                l1 = 83558400L;
                k = 2228224;
                i = 18000;
                j = 4160;
                continue;
                l1 = 160432128L;
                k = 2228224;
                i = 30000;
                j = 4160;
                continue;
                l1 = 311951360L;
                k = 8912896;
                i = 60000;
                j = 8384;
                continue;
                l1 = 588251136L;
                k = 8912896;
                i = 120000;
                j = 8384;
                continue;
                l1 = 1176502272L;
                k = 8912896;
                i = 180000;
                j = 8384;
                continue;
                l1 = 1176502272L;
                k = 35651584;
                i = 180000;
                j = 16832;
                continue;
                l1 = 2353004544L;
                k = 35651584;
                i = 240000;
                j = 16832;
                continue;
                l1 = 4706009088L;
                k = 35651584;
                i = 480000;
                j = 16832;
              }
            }
            i = Utils.divUp(i1, 8);
            applyMacroBlockLimits(i, i, Utils.divUp(i2, 64), Utils.divUp(l2, 64L), 8, 8, 1, 1);
            i = m;
            j = n;
          }
          else if (str.equalsIgnoreCase("video/hevc"))
          {
            n = 576;
            l1 = '⇀';
            m = 128000;
            i1 = 0;
            i3 = localObject1.length;
            k = i;
            if (i1 < i3)
            {
              localObject2 = localObject1[i1];
              double d = 0.0D;
              j = 0;
              i = 0;
              switch (((MediaCodecInfo.CodecProfileLevel)localObject2).level)
              {
              default: 
                Log.w("VideoCapabilities", "Unrecognized level " + ((MediaCodecInfo.CodecProfileLevel)localObject2).level + " for " + str);
                k |= 0x1;
              }
              for (;;)
              {
                i2 = k;
                switch (((MediaCodecInfo.CodecProfileLevel)localObject2).profile)
                {
                default: 
                  Log.w("VideoCapabilities", "Unrecognized profile " + ((MediaCodecInfo.CodecProfileLevel)localObject2).profile + " for " + str);
                  i2 = k | 0x1;
                }
                j >>= 6;
                k = i2 & 0xFFFFFFFB;
                l1 = Math.max((int)(j * d), l1);
                n = Math.max(j, n);
                m = Math.max(i * 1000, m);
                i1 += 1;
                break;
                d = 15.0D;
                j = 36864;
                i = 128;
                continue;
                d = 30.0D;
                j = 122880;
                i = 1500;
                continue;
                d = 30.0D;
                j = 245760;
                i = 3000;
                continue;
                d = 30.0D;
                j = 552960;
                i = 6000;
                continue;
                d = 33.75D;
                j = 983040;
                i = 10000;
                continue;
                d = 30.0D;
                j = 2228224;
                i = 12000;
                continue;
                d = 30.0D;
                j = 2228224;
                i = 30000;
                continue;
                d = 60.0D;
                j = 2228224;
                i = 20000;
                continue;
                d = 60.0D;
                j = 2228224;
                i = 50000;
                continue;
                d = 30.0D;
                j = 8912896;
                i = 25000;
                continue;
                d = 30.0D;
                j = 8912896;
                i = 100000;
                continue;
                d = 60.0D;
                j = 8912896;
                i = 40000;
                continue;
                d = 60.0D;
                j = 8912896;
                i = 160000;
                continue;
                d = 120.0D;
                j = 8912896;
                i = 60000;
                continue;
                d = 120.0D;
                j = 8912896;
                i = 240000;
                continue;
                d = 30.0D;
                j = 35651584;
                i = 60000;
                continue;
                d = 30.0D;
                j = 35651584;
                i = 240000;
                continue;
                d = 60.0D;
                j = 35651584;
                i = 120000;
                continue;
                d = 60.0D;
                j = 35651584;
                i = 480000;
                continue;
                d = 120.0D;
                j = 35651584;
                i = 240000;
                continue;
                d = 120.0D;
                j = 35651584;
                i = 800000;
              }
            }
            i = (int)Math.sqrt(n * 8);
            applyMacroBlockLimits(i, i, n, l1, 8, 8, 1, 1);
            i = k;
            j = m;
          }
          else
          {
            Log.w("VideoCapabilities", "Unsupported mime " + str);
            j = 64000;
            i = 6;
          }
        }
      }
    }
    
    private void applyMacroBlockLimits(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, long paramLong, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
    {
      applyAlignment(paramInt8, paramInt9);
      applyBlockLimits(paramInt6, paramInt7, Range.create(Integer.valueOf(1), Integer.valueOf(paramInt5)), Range.create(Long.valueOf(1L), Long.valueOf(paramLong)), Range.create(new Rational(1, paramInt4), new Rational(paramInt3, 1)));
      this.mHorizontalBlockRange = this.mHorizontalBlockRange.intersect(Integer.valueOf(Utils.divUp(paramInt1, this.mBlockWidth / paramInt6)), Integer.valueOf(paramInt3 / (this.mBlockWidth / paramInt6)));
      this.mVerticalBlockRange = this.mVerticalBlockRange.intersect(Integer.valueOf(Utils.divUp(paramInt2, this.mBlockHeight / paramInt7)), Integer.valueOf(paramInt4 / (this.mBlockHeight / paramInt7)));
    }
    
    private void applyMacroBlockLimits(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
    {
      applyMacroBlockLimits(1, 1, paramInt1, paramInt2, paramInt3, paramLong, paramInt4, paramInt5, paramInt6, paramInt7);
    }
    
    public static VideoCapabilities create(MediaFormat paramMediaFormat, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
    {
      VideoCapabilities localVideoCapabilities = new VideoCapabilities();
      localVideoCapabilities.init(paramMediaFormat, paramCodecCapabilities);
      return localVideoCapabilities;
    }
    
    public static int equivalentVP9Level(MediaFormat paramMediaFormat)
    {
      paramMediaFormat = paramMediaFormat.getMap();
      Object localObject = Utils.parseSize(paramMediaFormat.get("block-size"), new Size(8, 8));
      int j = ((Size)localObject).getWidth() * ((Size)localObject).getHeight();
      localObject = Utils.parseIntRange(paramMediaFormat.get("block-count-range"), null);
      int i;
      long l;
      if (localObject == null)
      {
        i = 0;
        localObject = Utils.parseLongRange(paramMediaFormat.get("blocks-per-second-range"), null);
        if (localObject != null) {
          break label176;
        }
        l = 0L;
        label87:
        localObject = parseWidthHeightRanges(paramMediaFormat.get("size-range"));
        if (localObject != null) {
          break label195;
        }
        j = 0;
        label108:
        paramMediaFormat = Utils.parseIntRange(paramMediaFormat.get("bitrate-range"), null);
        if (paramMediaFormat != null) {
          break label236;
        }
      }
      label176:
      label195:
      label236:
      for (int k = 0;; k = Utils.divUp(((Integer)paramMediaFormat.getUpper()).intValue(), 1000))
      {
        if ((l > 829440L) || (i > 36864) || (k > 200) || (j > 512)) {
          break label256;
        }
        return 1;
        i = j * ((Integer)((Range)localObject).getUpper()).intValue();
        break;
        l = j * ((Long)((Range)localObject).getUpper()).longValue();
        break label87;
        j = Math.max(((Integer)((Range)((Pair)localObject).first).getUpper()).intValue(), ((Integer)((Range)((Pair)localObject).second).getUpper()).intValue());
        break label108;
      }
      label256:
      if ((l <= 2764800L) && (i <= 73728) && (k <= 800) && (j <= 768)) {
        return 2;
      }
      if ((l <= 4608000L) && (i <= 122880) && (k <= 1800) && (j <= 960)) {
        return 4;
      }
      if ((l <= 9216000L) && (i <= 245760) && (k <= 3600) && (j <= 1344)) {
        return 8;
      }
      if ((l <= 20736000L) && (i <= 552960) && (k <= 7200) && (j <= 2048)) {
        return 16;
      }
      if ((l <= 36864000L) && (i <= 983040) && (k <= 12000) && (j <= 2752)) {
        return 32;
      }
      if ((l <= 83558400L) && (i <= 2228224) && (k <= 18000) && (j <= 4160)) {
        return 64;
      }
      if ((l <= 160432128L) && (i <= 2228224) && (k <= 30000) && (j <= 4160)) {
        return 128;
      }
      if ((l <= 311951360L) && (i <= 8912896) && (k <= 60000) && (j <= 8384)) {
        return 256;
      }
      if ((l <= 588251136L) && (i <= 8912896) && (k <= 120000) && (j <= 8384)) {
        return 512;
      }
      if ((l <= 1176502272L) && (i <= 8912896) && (k <= 180000) && (j <= 8384)) {
        return 1024;
      }
      if ((l <= 1176502272L) && (i <= 35651584) && (k <= 180000) && (j <= 16832)) {
        return 2048;
      }
      if ((l <= 2353004544L) && (i <= 35651584) && (k <= 240000) && (j <= 16832)) {
        return 4096;
      }
      if ((l <= 4706009088L) && (i <= 35651584) && (k <= 480000) && (j <= 16832)) {
        return 8192;
      }
      return 8192;
    }
    
    private Range<Double> estimateFrameRatesFor(int paramInt1, int paramInt2)
    {
      Object localObject = findClosestSize(paramInt1, paramInt2);
      Range localRange = (Range)this.mMeasuredFrameRates.get(localObject);
      localObject = Double.valueOf(getBlockCount(((Size)localObject).getWidth(), ((Size)localObject).getHeight()) / Math.max(getBlockCount(paramInt1, paramInt2), 1));
      return Range.create(Double.valueOf(((Long)localRange.getLower()).longValue() * ((Double)localObject).doubleValue()), Double.valueOf(((Long)localRange.getUpper()).longValue() * ((Double)localObject).doubleValue()));
    }
    
    private Size findClosestSize(int paramInt1, int paramInt2)
    {
      int i = getBlockCount(paramInt1, paramInt2);
      Object localObject = null;
      paramInt1 = Integer.MAX_VALUE;
      Iterator localIterator = this.mMeasuredFrameRates.keySet().iterator();
      while (localIterator.hasNext())
      {
        Size localSize = (Size)localIterator.next();
        paramInt2 = Math.abs(i - getBlockCount(localSize.getWidth(), localSize.getHeight()));
        if (paramInt2 < paramInt1)
        {
          paramInt1 = paramInt2;
          localObject = localSize;
        }
      }
      return (Size)localObject;
    }
    
    private int getBlockCount(int paramInt1, int paramInt2)
    {
      return Utils.divUp(paramInt1, this.mBlockWidth) * Utils.divUp(paramInt2, this.mBlockHeight);
    }
    
    private Map<Size, Range<Long>> getMeasuredFrameRates(Map<String, Object> paramMap)
    {
      HashMap localHashMap = new HashMap();
      Iterator localIterator = paramMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        Object localObject1 = (String)localIterator.next();
        if (((String)localObject1).startsWith("measured-frame-rate-"))
        {
          ((String)localObject1).substring("measured-frame-rate-".length());
          Object localObject2 = ((String)localObject1).split("-");
          if (localObject2.length == 5)
          {
            localObject2 = Utils.parseSize(localObject2[3], null);
            if ((localObject2 != null) && (((Size)localObject2).getWidth() * ((Size)localObject2).getHeight() > 0))
            {
              localObject1 = Utils.parseLongRange(paramMap.get(localObject1), null);
              if ((localObject1 != null) && (((Long)((Range)localObject1).getLower()).longValue() >= 0L) && (((Long)((Range)localObject1).getUpper()).longValue() >= 0L)) {
                localHashMap.put(localObject2, localObject1);
              }
            }
          }
        }
      }
      return localHashMap;
    }
    
    private void initWithPlatformLimits()
    {
      this.mBitrateRange = MediaCodecInfo.-get0();
      this.mWidthRange = MediaCodecInfo.-get5();
      this.mHeightRange = MediaCodecInfo.-get5();
      this.mFrameRateRange = MediaCodecInfo.-get1();
      this.mHorizontalBlockRange = MediaCodecInfo.-get5();
      this.mVerticalBlockRange = MediaCodecInfo.-get5();
      this.mBlockCountRange = MediaCodecInfo.-get2();
      this.mBlocksPerSecondRange = MediaCodecInfo.-get3();
      this.mBlockAspectRatioRange = MediaCodecInfo.-get4();
      this.mAspectRatioRange = MediaCodecInfo.-get4();
      this.mWidthAlignment = 2;
      this.mHeightAlignment = 2;
      this.mBlockWidth = 2;
      this.mBlockHeight = 2;
      this.mSmallerDimensionUpperLimit = ((Integer)MediaCodecInfo.-get5().getUpper()).intValue();
    }
    
    private void parseFromInfo(MediaFormat paramMediaFormat)
    {
      Object localObject4 = paramMediaFormat.getMap();
      Object localObject3 = new Size(this.mBlockWidth, this.mBlockHeight);
      Object localObject2 = new Size(this.mWidthAlignment, this.mHeightAlignment);
      Object localObject1 = null;
      paramMediaFormat = null;
      Size localSize1 = Utils.parseSize(((Map)localObject4).get("block-size"), (Size)localObject3);
      Size localSize2 = Utils.parseSize(((Map)localObject4).get("alignment"), (Size)localObject2);
      Range localRange2 = Utils.parseIntRange(((Map)localObject4).get("block-count-range"), null);
      Range localRange3 = Utils.parseLongRange(((Map)localObject4).get("blocks-per-second-range"), null);
      this.mMeasuredFrameRates = getMeasuredFrameRates((Map)localObject4);
      localObject2 = parseWidthHeightRanges(((Map)localObject4).get("size-range"));
      if (localObject2 != null)
      {
        localObject1 = (Range)((Pair)localObject2).first;
        paramMediaFormat = (Range)((Pair)localObject2).second;
      }
      localObject3 = paramMediaFormat;
      localObject2 = localObject1;
      if (((Map)localObject4).containsKey("feature-can-swap-width-height"))
      {
        if (localObject1 == null) {
          break label627;
        }
        this.mSmallerDimensionUpperLimit = Math.min(((Integer)((Range)localObject1).getUpper()).intValue(), ((Integer)paramMediaFormat.getUpper()).intValue());
        localObject3 = ((Range)localObject1).extend(paramMediaFormat);
        localObject2 = localObject3;
      }
      for (;;)
      {
        Range localRange4 = Utils.parseRationalRange(((Map)localObject4).get("block-aspect-ratio-range"), null);
        Range localRange5 = Utils.parseRationalRange(((Map)localObject4).get("pixel-aspect-ratio-range"), null);
        localObject1 = Utils.parseIntRange(((Map)localObject4).get("frame-rate-range"), null);
        paramMediaFormat = (MediaFormat)localObject1;
        if (localObject1 != null) {}
        try
        {
          paramMediaFormat = ((Range)localObject1).intersect(MediaCodecInfo.-get1());
          localObject4 = Utils.parseIntRange(((Map)localObject4).get("bitrate-range"), null);
          localObject1 = localObject4;
          if (localObject4 == null) {}
        }
        catch (IllegalArgumentException paramMediaFormat)
        {
          try
          {
            localObject1 = ((Range)localObject4).intersect(MediaCodecInfo.-get0());
            MediaCodecInfo.-wrap0(localSize1.getWidth(), "block-size width must be power of two");
            MediaCodecInfo.-wrap0(localSize1.getHeight(), "block-size height must be power of two");
            MediaCodecInfo.-wrap0(localSize2.getWidth(), "alignment width must be power of two");
            MediaCodecInfo.-wrap0(localSize2.getHeight(), "alignment height must be power of two");
            applyMacroBlockLimits(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, localSize1.getWidth(), localSize1.getHeight(), localSize2.getWidth(), localSize2.getHeight());
            if (((this.mParent.mError & 0x2) != 0) || (this.mAllowMbOverride))
            {
              if (localObject2 != null) {
                this.mWidthRange = MediaCodecInfo.-get5().intersect((Range)localObject2);
              }
              if (localObject3 != null) {
                this.mHeightRange = MediaCodecInfo.-get5().intersect((Range)localObject3);
              }
              if (localRange2 != null) {
                this.mBlockCountRange = MediaCodecInfo.-get2().intersect(Utils.factorRange(localRange2, this.mBlockWidth * this.mBlockHeight / localSize1.getWidth() / localSize1.getHeight()));
              }
              if (localRange3 != null) {
                this.mBlocksPerSecondRange = MediaCodecInfo.-get3().intersect(Utils.factorRange(localRange3, this.mBlockWidth * this.mBlockHeight / localSize1.getWidth() / localSize1.getHeight()));
              }
              if (localRange5 != null) {
                this.mBlockAspectRatioRange = MediaCodecInfo.-get4().intersect(Utils.scaleRange(localRange5, this.mBlockHeight / localSize1.getHeight(), this.mBlockWidth / localSize1.getWidth()));
              }
              if (localRange4 != null) {
                this.mAspectRatioRange = MediaCodecInfo.-get4().intersect(localRange4);
              }
              if (paramMediaFormat != null) {
                this.mFrameRateRange = MediaCodecInfo.-get1().intersect(paramMediaFormat);
              }
              if (localObject1 != null)
              {
                if ((this.mParent.mError & 0x2) != 0) {
                  this.mBitrateRange = MediaCodecInfo.-get0().intersect((Range)localObject1);
                }
              }
              else
              {
                updateLimits();
                return;
                label627:
                Log.w("VideoCapabilities", "feature can-swap-width-height is best used with size-range");
                this.mSmallerDimensionUpperLimit = Math.min(((Integer)this.mWidthRange.getUpper()).intValue(), ((Integer)this.mHeightRange.getUpper()).intValue());
                localObject2 = this.mWidthRange.extend(this.mHeightRange);
                this.mHeightRange = ((Range)localObject2);
                this.mWidthRange = ((Range)localObject2);
                localObject3 = paramMediaFormat;
                localObject2 = localObject1;
                continue;
                paramMediaFormat = paramMediaFormat;
                Log.w("VideoCapabilities", "frame rate range (" + localObject1 + ") is out of limits: " + MediaCodecInfo.-get1());
                paramMediaFormat = null;
              }
            }
          }
          catch (IllegalArgumentException localIllegalArgumentException)
          {
            for (;;)
            {
              Log.w("VideoCapabilities", "bitrate range (" + localObject4 + ") is out of limits: " + MediaCodecInfo.-get0());
              Range localRange1 = null;
              continue;
              this.mBitrateRange = this.mBitrateRange.intersect(localRange1);
              continue;
              if (localObject2 != null) {
                this.mWidthRange = this.mWidthRange.intersect((Range)localObject2);
              }
              if (localObject3 != null) {
                this.mHeightRange = this.mHeightRange.intersect((Range)localObject3);
              }
              if (localRange2 != null) {
                this.mBlockCountRange = this.mBlockCountRange.intersect(Utils.factorRange(localRange2, this.mBlockWidth * this.mBlockHeight / localSize1.getWidth() / localSize1.getHeight()));
              }
              if (localRange3 != null) {
                this.mBlocksPerSecondRange = this.mBlocksPerSecondRange.intersect(Utils.factorRange(localRange3, this.mBlockWidth * this.mBlockHeight / localSize1.getWidth() / localSize1.getHeight()));
              }
              if (localRange5 != null) {
                this.mBlockAspectRatioRange = this.mBlockAspectRatioRange.intersect(Utils.scaleRange(localRange5, this.mBlockHeight / localSize1.getHeight(), this.mBlockWidth / localSize1.getWidth()));
              }
              if (localRange4 != null) {
                this.mAspectRatioRange = this.mAspectRatioRange.intersect(localRange4);
              }
              if (paramMediaFormat != null) {
                this.mFrameRateRange = this.mFrameRateRange.intersect(paramMediaFormat);
              }
              if (localRange1 != null) {
                this.mBitrateRange = this.mBitrateRange.intersect(localRange1);
              }
            }
          }
        }
      }
    }
    
    private static Pair<Range<Integer>, Range<Integer>> parseWidthHeightRanges(Object paramObject)
    {
      Pair localPair = Utils.parseSizeRange(paramObject);
      if (localPair != null) {
        try
        {
          localPair = Pair.create(Range.create(Integer.valueOf(((Size)localPair.first).getWidth()), Integer.valueOf(((Size)localPair.second).getWidth())), Range.create(Integer.valueOf(((Size)localPair.first).getHeight()), Integer.valueOf(((Size)localPair.second).getHeight())));
          return localPair;
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          Log.w("VideoCapabilities", "could not parse size range '" + paramObject + "'");
        }
      }
      return null;
    }
    
    private boolean supports(Integer paramInteger1, Integer paramInteger2, Number paramNumber)
    {
      boolean bool2 = true;
      boolean bool1 = bool2;
      if (1 != 0)
      {
        bool1 = bool2;
        if (paramInteger1 != null)
        {
          if (!this.mWidthRange.contains(paramInteger1)) {
            break label319;
          }
          if (paramInteger1.intValue() % this.mWidthAlignment != 0) {
            break label313;
          }
          bool1 = true;
        }
      }
      bool2 = bool1;
      if (bool1)
      {
        bool2 = bool1;
        if (paramInteger2 != null)
        {
          if (!this.mHeightRange.contains(paramInteger2)) {
            break label331;
          }
          if (paramInteger2.intValue() % this.mHeightAlignment != 0) {
            break label325;
          }
          bool2 = true;
        }
      }
      label88:
      bool1 = bool2;
      if (bool2)
      {
        bool1 = bool2;
        if (paramNumber != null) {
          bool1 = this.mFrameRateRange.contains(Utils.intRangeFor(paramNumber.doubleValue()));
        }
      }
      bool2 = bool1;
      int i;
      label167:
      int m;
      if (bool1)
      {
        bool2 = bool1;
        if (paramInteger2 != null)
        {
          bool2 = bool1;
          if (paramInteger1 != null)
          {
            if (Math.min(paramInteger2.intValue(), paramInteger1.intValue()) > this.mSmallerDimensionUpperLimit) {
              break label337;
            }
            i = 1;
            int j = Utils.divUp(paramInteger1.intValue(), this.mBlockWidth);
            int k = Utils.divUp(paramInteger2.intValue(), this.mBlockHeight);
            m = j * k;
            if ((i == 0) || (!this.mBlockCountRange.contains(Integer.valueOf(m))) || (!this.mBlockAspectRatioRange.contains(new Rational(j, k)))) {
              break label343;
            }
          }
        }
      }
      label313:
      label319:
      label325:
      label331:
      label337:
      label343:
      for (bool1 = this.mAspectRatioRange.contains(new Rational(paramInteger1.intValue(), paramInteger2.intValue()));; bool1 = false)
      {
        bool2 = bool1;
        if (bool1)
        {
          bool2 = bool1;
          if (paramNumber != null)
          {
            double d1 = m;
            double d2 = paramNumber.doubleValue();
            bool2 = this.mBlocksPerSecondRange.contains(Utils.longRangeFor(d1 * d2));
          }
        }
        return bool2;
        bool1 = false;
        break;
        bool1 = false;
        break;
        bool2 = false;
        break label88;
        bool2 = false;
        break label88;
        i = 0;
        break label167;
      }
    }
    
    private void updateLimits()
    {
      this.mHorizontalBlockRange = this.mHorizontalBlockRange.intersect(Utils.factorRange(this.mWidthRange, this.mBlockWidth));
      this.mHorizontalBlockRange = this.mHorizontalBlockRange.intersect(Range.create(Integer.valueOf(((Integer)this.mBlockCountRange.getLower()).intValue() / ((Integer)this.mVerticalBlockRange.getUpper()).intValue()), Integer.valueOf(((Integer)this.mBlockCountRange.getUpper()).intValue() / ((Integer)this.mVerticalBlockRange.getLower()).intValue())));
      this.mVerticalBlockRange = this.mVerticalBlockRange.intersect(Utils.factorRange(this.mHeightRange, this.mBlockHeight));
      this.mVerticalBlockRange = this.mVerticalBlockRange.intersect(Range.create(Integer.valueOf(((Integer)this.mBlockCountRange.getLower()).intValue() / ((Integer)this.mHorizontalBlockRange.getUpper()).intValue()), Integer.valueOf(((Integer)this.mBlockCountRange.getUpper()).intValue() / ((Integer)this.mHorizontalBlockRange.getLower()).intValue())));
      Range localRange = this.mBlockCountRange;
      int i = ((Integer)this.mHorizontalBlockRange.getLower()).intValue();
      int j = ((Integer)this.mVerticalBlockRange.getLower()).intValue();
      int k = ((Integer)this.mHorizontalBlockRange.getUpper()).intValue();
      this.mBlockCountRange = localRange.intersect(Range.create(Integer.valueOf(j * i), Integer.valueOf(((Integer)this.mVerticalBlockRange.getUpper()).intValue() * k)));
      this.mBlockAspectRatioRange = this.mBlockAspectRatioRange.intersect(new Rational(((Integer)this.mHorizontalBlockRange.getLower()).intValue(), ((Integer)this.mVerticalBlockRange.getUpper()).intValue()), new Rational(((Integer)this.mHorizontalBlockRange.getUpper()).intValue(), ((Integer)this.mVerticalBlockRange.getLower()).intValue()));
      this.mWidthRange = this.mWidthRange.intersect(Integer.valueOf((((Integer)this.mHorizontalBlockRange.getLower()).intValue() - 1) * this.mBlockWidth + this.mWidthAlignment), Integer.valueOf(((Integer)this.mHorizontalBlockRange.getUpper()).intValue() * this.mBlockWidth));
      this.mHeightRange = this.mHeightRange.intersect(Integer.valueOf((((Integer)this.mVerticalBlockRange.getLower()).intValue() - 1) * this.mBlockHeight + this.mHeightAlignment), Integer.valueOf(((Integer)this.mVerticalBlockRange.getUpper()).intValue() * this.mBlockHeight));
      this.mAspectRatioRange = this.mAspectRatioRange.intersect(new Rational(((Integer)this.mWidthRange.getLower()).intValue(), ((Integer)this.mHeightRange.getUpper()).intValue()), new Rational(((Integer)this.mWidthRange.getUpper()).intValue(), ((Integer)this.mHeightRange.getLower()).intValue()));
      this.mSmallerDimensionUpperLimit = Math.min(this.mSmallerDimensionUpperLimit, Math.min(((Integer)this.mWidthRange.getUpper()).intValue(), ((Integer)this.mHeightRange.getUpper()).intValue()));
      this.mBlocksPerSecondRange = this.mBlocksPerSecondRange.intersect(Long.valueOf(((Integer)this.mBlockCountRange.getLower()).intValue() * ((Integer)this.mFrameRateRange.getLower()).intValue()), Long.valueOf(((Integer)this.mBlockCountRange.getUpper()).intValue() * ((Integer)this.mFrameRateRange.getUpper()).intValue()));
      this.mFrameRateRange = this.mFrameRateRange.intersect(Integer.valueOf((int)(((Long)this.mBlocksPerSecondRange.getLower()).longValue() / ((Integer)this.mBlockCountRange.getUpper()).intValue())), Integer.valueOf((int)(((Long)this.mBlocksPerSecondRange.getUpper()).longValue() / ((Integer)this.mBlockCountRange.getLower()).intValue())));
    }
    
    public boolean areSizeAndRateSupported(int paramInt1, int paramInt2, double paramDouble)
    {
      return supports(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Double.valueOf(paramDouble));
    }
    
    public Range<Double> getAchievableFrameRatesFor(int paramInt1, int paramInt2)
    {
      if (!supports(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), null)) {
        throw new IllegalArgumentException("unsupported size");
      }
      if ((this.mMeasuredFrameRates == null) || (this.mMeasuredFrameRates.size() <= 0))
      {
        Log.w("VideoCapabilities", "Codec did not publish any measurement data.");
        return null;
      }
      return estimateFrameRatesFor(paramInt1, paramInt2);
    }
    
    public Range<Rational> getAspectRatioRange(boolean paramBoolean)
    {
      if (paramBoolean) {
        return this.mBlockAspectRatioRange;
      }
      return this.mAspectRatioRange;
    }
    
    public Range<Integer> getBitrateRange()
    {
      return this.mBitrateRange;
    }
    
    public Range<Integer> getBlockCountRange()
    {
      return this.mBlockCountRange;
    }
    
    public Size getBlockSize()
    {
      return new Size(this.mBlockWidth, this.mBlockHeight);
    }
    
    public Range<Long> getBlocksPerSecondRange()
    {
      return this.mBlocksPerSecondRange;
    }
    
    public int getHeightAlignment()
    {
      return this.mHeightAlignment;
    }
    
    public int getSmallerDimensionUpperLimit()
    {
      return this.mSmallerDimensionUpperLimit;
    }
    
    public Range<Integer> getSupportedFrameRates()
    {
      return this.mFrameRateRange;
    }
    
    public Range<Double> getSupportedFrameRatesFor(int paramInt1, int paramInt2)
    {
      Range localRange = this.mHeightRange;
      if (!supports(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), null)) {
        throw new IllegalArgumentException("unsupported size");
      }
      paramInt1 = Utils.divUp(paramInt1, this.mBlockWidth) * Utils.divUp(paramInt2, this.mBlockHeight);
      return Range.create(Double.valueOf(Math.max(((Long)this.mBlocksPerSecondRange.getLower()).longValue() / paramInt1, ((Integer)this.mFrameRateRange.getLower()).intValue())), Double.valueOf(Math.min(((Long)this.mBlocksPerSecondRange.getUpper()).longValue() / paramInt1, ((Integer)this.mFrameRateRange.getUpper()).intValue())));
    }
    
    public Range<Integer> getSupportedHeights()
    {
      return this.mHeightRange;
    }
    
    public Range<Integer> getSupportedHeightsFor(int paramInt)
    {
      try
      {
        Range localRange1 = this.mHeightRange;
        if ((!this.mWidthRange.contains(Integer.valueOf(paramInt))) || (paramInt % this.mWidthAlignment != 0)) {
          throw new IllegalArgumentException("unsupported width");
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        Log.v("VideoCapabilities", "could not get supported heights for " + paramInt);
        throw new IllegalArgumentException("unsupported width");
      }
      int j = Utils.divUp(paramInt, this.mBlockWidth);
      int i = Math.max(Utils.divUp(((Integer)this.mBlockCountRange.getLower()).intValue(), j), (int)Math.ceil(j / ((Rational)this.mBlockAspectRatioRange.getUpper()).doubleValue()));
      j = Math.min(((Integer)this.mBlockCountRange.getUpper()).intValue() / j, (int)(j / ((Rational)this.mBlockAspectRatioRange.getLower()).doubleValue()));
      Range localRange2 = localIllegalArgumentException.intersect(Integer.valueOf((i - 1) * this.mBlockHeight + this.mHeightAlignment), Integer.valueOf(this.mBlockHeight * j));
      Object localObject = localRange2;
      if (paramInt > this.mSmallerDimensionUpperLimit) {
        localObject = localRange2.intersect(Integer.valueOf(1), Integer.valueOf(this.mSmallerDimensionUpperLimit));
      }
      localObject = ((Range)localObject).intersect(Integer.valueOf((int)Math.ceil(paramInt / ((Rational)this.mAspectRatioRange.getUpper()).doubleValue())), Integer.valueOf((int)(paramInt / ((Rational)this.mAspectRatioRange.getLower()).doubleValue())));
      return (Range<Integer>)localObject;
    }
    
    public Range<Integer> getSupportedWidths()
    {
      return this.mWidthRange;
    }
    
    public Range<Integer> getSupportedWidthsFor(int paramInt)
    {
      try
      {
        Range localRange1 = this.mWidthRange;
        if ((!this.mHeightRange.contains(Integer.valueOf(paramInt))) || (paramInt % this.mHeightAlignment != 0)) {
          throw new IllegalArgumentException("unsupported height");
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        Log.v("VideoCapabilities", "could not get supported widths for " + paramInt);
        throw new IllegalArgumentException("unsupported height");
      }
      int j = Utils.divUp(paramInt, this.mBlockHeight);
      int i = Math.max(Utils.divUp(((Integer)this.mBlockCountRange.getLower()).intValue(), j), (int)Math.ceil(((Rational)this.mBlockAspectRatioRange.getLower()).doubleValue() * j));
      j = Math.min(((Integer)this.mBlockCountRange.getUpper()).intValue() / j, (int)(((Rational)this.mBlockAspectRatioRange.getUpper()).doubleValue() * j));
      Range localRange2 = localIllegalArgumentException.intersect(Integer.valueOf((i - 1) * this.mBlockWidth + this.mWidthAlignment), Integer.valueOf(this.mBlockWidth * j));
      Object localObject = localRange2;
      if (paramInt > this.mSmallerDimensionUpperLimit) {
        localObject = localRange2.intersect(Integer.valueOf(1), Integer.valueOf(this.mSmallerDimensionUpperLimit));
      }
      localObject = ((Range)localObject).intersect(Integer.valueOf((int)Math.ceil(((Rational)this.mAspectRatioRange.getLower()).doubleValue() * paramInt)), Integer.valueOf((int)(((Rational)this.mAspectRatioRange.getUpper()).doubleValue() * paramInt)));
      return (Range<Integer>)localObject;
    }
    
    public int getWidthAlignment()
    {
      return this.mWidthAlignment;
    }
    
    public void init(MediaFormat paramMediaFormat, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
    {
      this.mParent = paramCodecCapabilities;
      initWithPlatformLimits();
      applyLevelLimits();
      parseFromInfo(paramMediaFormat);
      updateLimits();
    }
    
    public boolean isSizeSupported(int paramInt1, int paramInt2)
    {
      return supports(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), null);
    }
    
    public boolean supportsFormat(MediaFormat paramMediaFormat)
    {
      Map localMap = paramMediaFormat.getMap();
      if (!supports((Integer)localMap.get("width"), (Integer)localMap.get("height"), (Number)localMap.get("frame-rate"))) {
        return false;
      }
      return MediaCodecInfo.CodecCapabilities.-wrap0(this.mBitrateRange, paramMediaFormat);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaCodecInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */