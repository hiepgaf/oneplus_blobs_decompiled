package android.hardware.camera2.params;

import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.utils.HashCodeHelpers;
import android.hardware.camera2.utils.SurfaceUtils;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.renderscript.Allocation;
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public final class StreamConfigurationMap
{
  private static final long DURATION_20FPS_NS = 50000000L;
  private static final int DURATION_MIN_FRAME = 0;
  private static final int DURATION_STALL = 1;
  private static final int HAL_DATASPACE_DEPTH = 4096;
  private static final int HAL_DATASPACE_RANGE_SHIFT = 27;
  private static final int HAL_DATASPACE_STANDARD_SHIFT = 16;
  private static final int HAL_DATASPACE_TRANSFER_SHIFT = 22;
  private static final int HAL_DATASPACE_UNKNOWN = 0;
  private static final int HAL_DATASPACE_V0_JFIF = 146931712;
  private static final int HAL_PIXEL_FORMAT_BLOB = 33;
  private static final int HAL_PIXEL_FORMAT_IMPLEMENTATION_DEFINED = 34;
  private static final int HAL_PIXEL_FORMAT_RAW10 = 37;
  private static final int HAL_PIXEL_FORMAT_RAW12 = 38;
  private static final int HAL_PIXEL_FORMAT_RAW16 = 32;
  private static final int HAL_PIXEL_FORMAT_RAW_OPAQUE = 36;
  private static final int HAL_PIXEL_FORMAT_Y16 = 540422489;
  private static final int HAL_PIXEL_FORMAT_YCbCr_420_888 = 35;
  private static final String TAG = "StreamConfigurationMap";
  private final SparseIntArray mAllOutputFormats = new SparseIntArray();
  private final StreamConfiguration[] mConfigurations;
  private final StreamConfiguration[] mDepthConfigurations;
  private final StreamConfigurationDuration[] mDepthMinFrameDurations;
  private final SparseIntArray mDepthOutputFormats = new SparseIntArray();
  private final StreamConfigurationDuration[] mDepthStallDurations;
  private final SparseIntArray mHighResOutputFormats = new SparseIntArray();
  private final HighSpeedVideoConfiguration[] mHighSpeedVideoConfigurations;
  private final HashMap<Range<Integer>, Integer> mHighSpeedVideoFpsRangeMap = new HashMap();
  private final HashMap<Size, Integer> mHighSpeedVideoSizeMap = new HashMap();
  private final SparseIntArray mInputFormats = new SparseIntArray();
  private final ReprocessFormatsMap mInputOutputFormatsMap;
  private final boolean mListHighResolution;
  private final StreamConfigurationDuration[] mMinFrameDurations;
  private final SparseIntArray mOutputFormats = new SparseIntArray();
  private final StreamConfigurationDuration[] mStallDurations;
  
  public StreamConfigurationMap(StreamConfiguration[] paramArrayOfStreamConfiguration1, StreamConfigurationDuration[] paramArrayOfStreamConfigurationDuration1, StreamConfigurationDuration[] paramArrayOfStreamConfigurationDuration2, StreamConfiguration[] paramArrayOfStreamConfiguration2, StreamConfigurationDuration[] paramArrayOfStreamConfigurationDuration3, StreamConfigurationDuration[] paramArrayOfStreamConfigurationDuration4, HighSpeedVideoConfiguration[] paramArrayOfHighSpeedVideoConfiguration, ReprocessFormatsMap paramReprocessFormatsMap, boolean paramBoolean)
  {
    label152:
    label165:
    label177:
    int m;
    if (paramArrayOfStreamConfiguration1 == null)
    {
      Preconditions.checkArrayElementsNotNull(paramArrayOfStreamConfiguration2, "depthConfigurations");
      this.mConfigurations = new StreamConfiguration[0];
      this.mMinFrameDurations = new StreamConfigurationDuration[0];
      this.mStallDurations = new StreamConfigurationDuration[0];
      this.mListHighResolution = paramBoolean;
      if (paramArrayOfStreamConfiguration2 != null) {
        break label394;
      }
      this.mDepthConfigurations = new StreamConfiguration[0];
      this.mDepthMinFrameDurations = new StreamConfigurationDuration[0];
      this.mDepthStallDurations = new StreamConfigurationDuration[0];
      if (paramArrayOfHighSpeedVideoConfiguration != null) {
        break label439;
      }
      this.mHighSpeedVideoConfigurations = new HighSpeedVideoConfiguration[0];
      paramArrayOfStreamConfigurationDuration2 = this.mConfigurations;
      int k = paramArrayOfStreamConfigurationDuration2.length;
      i = 0;
      if (i >= k) {
        break label481;
      }
      paramArrayOfStreamConfigurationDuration1 = paramArrayOfStreamConfigurationDuration2[i];
      m = paramArrayOfStreamConfigurationDuration1.getFormat();
      if (!paramArrayOfStreamConfigurationDuration1.isOutput()) {
        break label473;
      }
      this.mAllOutputFormats.put(m, this.mAllOutputFormats.get(m) + 1);
      long l2 = 0L;
      long l1 = l2;
      if (this.mListHighResolution)
      {
        paramArrayOfStreamConfiguration2 = this.mMinFrameDurations;
        j = 0;
        int n = paramArrayOfStreamConfiguration2.length;
        label250:
        l1 = l2;
        if (j < n)
        {
          paramArrayOfStreamConfigurationDuration3 = paramArrayOfStreamConfiguration2[j];
          if ((paramArrayOfStreamConfigurationDuration3.getFormat() != m) || (paramArrayOfStreamConfigurationDuration3.getWidth() != paramArrayOfStreamConfigurationDuration1.getSize().getWidth()) || (paramArrayOfStreamConfigurationDuration3.getHeight() != paramArrayOfStreamConfigurationDuration1.getSize().getHeight())) {
            break label456;
          }
          l1 = paramArrayOfStreamConfigurationDuration3.getDuration();
        }
      }
      if (l1 > 50000000L) {
        break label465;
      }
      paramArrayOfStreamConfigurationDuration1 = this.mOutputFormats;
    }
    for (;;)
    {
      paramArrayOfStreamConfigurationDuration1.put(m, paramArrayOfStreamConfigurationDuration1.get(m) + 1);
      i += 1;
      break label177;
      this.mConfigurations = ((StreamConfiguration[])Preconditions.checkArrayElementsNotNull(paramArrayOfStreamConfiguration1, "configurations"));
      this.mMinFrameDurations = ((StreamConfigurationDuration[])Preconditions.checkArrayElementsNotNull(paramArrayOfStreamConfigurationDuration1, "minFrameDurations"));
      this.mStallDurations = ((StreamConfigurationDuration[])Preconditions.checkArrayElementsNotNull(paramArrayOfStreamConfigurationDuration2, "stallDurations"));
      break;
      label394:
      this.mDepthConfigurations = ((StreamConfiguration[])Preconditions.checkArrayElementsNotNull(paramArrayOfStreamConfiguration2, "depthConfigurations"));
      this.mDepthMinFrameDurations = ((StreamConfigurationDuration[])Preconditions.checkArrayElementsNotNull(paramArrayOfStreamConfigurationDuration3, "depthMinFrameDurations"));
      this.mDepthStallDurations = ((StreamConfigurationDuration[])Preconditions.checkArrayElementsNotNull(paramArrayOfStreamConfigurationDuration4, "depthStallDurations"));
      break label152;
      label439:
      this.mHighSpeedVideoConfigurations = ((HighSpeedVideoConfiguration[])Preconditions.checkArrayElementsNotNull(paramArrayOfHighSpeedVideoConfiguration, "highSpeedVideoConfigurations"));
      break label165;
      label456:
      j += 1;
      break label250;
      label465:
      paramArrayOfStreamConfigurationDuration1 = this.mHighResOutputFormats;
      continue;
      label473:
      paramArrayOfStreamConfigurationDuration1 = this.mInputFormats;
    }
    label481:
    paramArrayOfStreamConfigurationDuration1 = this.mDepthConfigurations;
    int i = 0;
    int j = paramArrayOfStreamConfigurationDuration1.length;
    if (i < j)
    {
      paramArrayOfStreamConfigurationDuration2 = paramArrayOfStreamConfigurationDuration1[i];
      if (!paramArrayOfStreamConfigurationDuration2.isOutput()) {}
      for (;;)
      {
        i += 1;
        break;
        this.mDepthOutputFormats.put(paramArrayOfStreamConfigurationDuration2.getFormat(), this.mDepthOutputFormats.get(paramArrayOfStreamConfigurationDuration2.getFormat()) + 1);
      }
    }
    if ((paramArrayOfStreamConfiguration1 != null) && (this.mOutputFormats.indexOfKey(34) < 0)) {
      throw new AssertionError("At least one stream configuration for IMPLEMENTATION_DEFINED must exist");
    }
    paramArrayOfStreamConfigurationDuration2 = this.mHighSpeedVideoConfigurations;
    i = 0;
    j = paramArrayOfStreamConfigurationDuration2.length;
    while (i < j)
    {
      paramArrayOfStreamConfiguration1 = paramArrayOfStreamConfigurationDuration2[i];
      paramArrayOfStreamConfigurationDuration3 = paramArrayOfStreamConfiguration1.getSize();
      paramArrayOfStreamConfiguration2 = paramArrayOfStreamConfiguration1.getFpsRange();
      paramArrayOfStreamConfigurationDuration1 = (Integer)this.mHighSpeedVideoSizeMap.get(paramArrayOfStreamConfigurationDuration3);
      paramArrayOfStreamConfiguration1 = paramArrayOfStreamConfigurationDuration1;
      if (paramArrayOfStreamConfigurationDuration1 == null) {
        paramArrayOfStreamConfiguration1 = Integer.valueOf(0);
      }
      this.mHighSpeedVideoSizeMap.put(paramArrayOfStreamConfigurationDuration3, Integer.valueOf(paramArrayOfStreamConfiguration1.intValue() + 1));
      paramArrayOfStreamConfigurationDuration1 = (Integer)this.mHighSpeedVideoFpsRangeMap.get(paramArrayOfStreamConfiguration2);
      paramArrayOfStreamConfiguration1 = paramArrayOfStreamConfigurationDuration1;
      if (paramArrayOfStreamConfigurationDuration1 == null) {
        paramArrayOfStreamConfiguration1 = Integer.valueOf(0);
      }
      this.mHighSpeedVideoFpsRangeMap.put(paramArrayOfStreamConfiguration2, Integer.valueOf(paramArrayOfStreamConfiguration1.intValue() + 1));
      i += 1;
    }
    this.mInputOutputFormatsMap = paramReprocessFormatsMap;
  }
  
  private void appendHighResOutputsString(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("HighResolutionOutputs(");
    int[] arrayOfInt = getOutputFormats();
    int k = arrayOfInt.length;
    int i = 0;
    if (i < k)
    {
      int m = arrayOfInt[i];
      Size[] arrayOfSize = getHighResolutionOutputSizes(m);
      if (arrayOfSize == null) {}
      for (;;)
      {
        i += 1;
        break;
        int j = 0;
        int n = arrayOfSize.length;
        while (j < n)
        {
          Size localSize = arrayOfSize[j];
          long l1 = getOutputMinFrameDuration(m, localSize);
          long l2 = getOutputStallDuration(m, localSize);
          paramStringBuilder.append(String.format("[w:%d, h:%d, format:%s(%d), min_duration:%d, stall:%d], ", new Object[] { Integer.valueOf(localSize.getWidth()), Integer.valueOf(localSize.getHeight()), formatToString(m), Integer.valueOf(m), Long.valueOf(l1), Long.valueOf(l2) }));
          j += 1;
        }
      }
    }
    if (paramStringBuilder.charAt(paramStringBuilder.length() - 1) == ' ') {
      paramStringBuilder.delete(paramStringBuilder.length() - 2, paramStringBuilder.length());
    }
    paramStringBuilder.append(")");
  }
  
  private void appendHighSpeedVideoConfigurationsString(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("HighSpeedVideoConfigurations(");
    Size[] arrayOfSize = getHighSpeedVideoSizes();
    int k = arrayOfSize.length;
    int i = 0;
    while (i < k)
    {
      Size localSize = arrayOfSize[i];
      Range[] arrayOfRange = getHighSpeedVideoFpsRangesFor(localSize);
      int m = arrayOfRange.length;
      int j = 0;
      while (j < m)
      {
        Range localRange = arrayOfRange[j];
        paramStringBuilder.append(String.format("[w:%d, h:%d, min_fps:%d, max_fps:%d], ", new Object[] { Integer.valueOf(localSize.getWidth()), Integer.valueOf(localSize.getHeight()), localRange.getLower(), localRange.getUpper() }));
        j += 1;
      }
      i += 1;
    }
    if (paramStringBuilder.charAt(paramStringBuilder.length() - 1) == ' ') {
      paramStringBuilder.delete(paramStringBuilder.length() - 2, paramStringBuilder.length());
    }
    paramStringBuilder.append(")");
  }
  
  private void appendInputsString(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("Inputs(");
    int[] arrayOfInt = getInputFormats();
    int k = arrayOfInt.length;
    int i = 0;
    while (i < k)
    {
      int m = arrayOfInt[i];
      Size[] arrayOfSize = getInputSizes(m);
      int n = arrayOfSize.length;
      int j = 0;
      while (j < n)
      {
        Size localSize = arrayOfSize[j];
        paramStringBuilder.append(String.format("[w:%d, h:%d, format:%s(%d)], ", new Object[] { Integer.valueOf(localSize.getWidth()), Integer.valueOf(localSize.getHeight()), formatToString(m), Integer.valueOf(m) }));
        j += 1;
      }
      i += 1;
    }
    if (paramStringBuilder.charAt(paramStringBuilder.length() - 1) == ' ') {
      paramStringBuilder.delete(paramStringBuilder.length() - 2, paramStringBuilder.length());
    }
    paramStringBuilder.append(")");
  }
  
  private void appendOutputsString(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("Outputs(");
    int[] arrayOfInt = getOutputFormats();
    int k = arrayOfInt.length;
    int i = 0;
    while (i < k)
    {
      int m = arrayOfInt[i];
      Size[] arrayOfSize = getOutputSizes(m);
      int j = 0;
      int n = arrayOfSize.length;
      while (j < n)
      {
        Size localSize = arrayOfSize[j];
        long l1 = getOutputMinFrameDuration(m, localSize);
        long l2 = getOutputStallDuration(m, localSize);
        paramStringBuilder.append(String.format("[w:%d, h:%d, format:%s(%d), min_duration:%d, stall:%d], ", new Object[] { Integer.valueOf(localSize.getWidth()), Integer.valueOf(localSize.getHeight()), formatToString(m), Integer.valueOf(m), Long.valueOf(l1), Long.valueOf(l2) }));
        j += 1;
      }
      i += 1;
    }
    if (paramStringBuilder.charAt(paramStringBuilder.length() - 1) == ' ') {
      paramStringBuilder.delete(paramStringBuilder.length() - 2, paramStringBuilder.length());
    }
    paramStringBuilder.append(")");
  }
  
  private void appendValidOutputFormatsForInputString(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("ValidOutputFormatsForInput(");
    int[] arrayOfInt1 = getInputFormats();
    int k = arrayOfInt1.length;
    int i = 0;
    while (i < k)
    {
      int j = arrayOfInt1[i];
      paramStringBuilder.append(String.format("[in:%s(%d), out:", new Object[] { formatToString(j), Integer.valueOf(j) }));
      int[] arrayOfInt2 = getValidOutputFormatsForInput(j);
      j = 0;
      while (j < arrayOfInt2.length)
      {
        paramStringBuilder.append(String.format("%s(%d)", new Object[] { formatToString(arrayOfInt2[j]), Integer.valueOf(arrayOfInt2[j]) }));
        if (j < arrayOfInt2.length - 1) {
          paramStringBuilder.append(", ");
        }
        j += 1;
      }
      paramStringBuilder.append("], ");
      i += 1;
    }
    if (paramStringBuilder.charAt(paramStringBuilder.length() - 1) == ' ') {
      paramStringBuilder.delete(paramStringBuilder.length() - 2, paramStringBuilder.length());
    }
    paramStringBuilder.append(")");
  }
  
  private static <T> boolean arrayContains(T[] paramArrayOfT, T paramT)
  {
    if (paramArrayOfT == null) {
      return false;
    }
    int j = paramArrayOfT.length;
    int i = 0;
    while (i < j)
    {
      if (Objects.equals(paramArrayOfT[i], paramT)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  static int checkArgumentFormat(int paramInt)
  {
    if ((ImageFormat.isPublicFormat(paramInt)) || (PixelFormat.isPublicFormat(paramInt))) {
      return paramInt;
    }
    throw new IllegalArgumentException(String.format("format 0x%x was not defined in either ImageFormat or PixelFormat", new Object[] { Integer.valueOf(paramInt) }));
  }
  
  static int checkArgumentFormatInternal(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return checkArgumentFormat(paramInt);
    case 33: 
    case 34: 
    case 36: 
    case 540422489: 
      return paramInt;
    }
    throw new IllegalArgumentException("ImageFormat.JPEG is an unknown internal format");
  }
  
  private int checkArgumentFormatSupported(int paramInt, boolean paramBoolean)
  {
    checkArgumentFormat(paramInt);
    int i = imageFormatToInternal(paramInt);
    int j = imageFormatToDataspace(paramInt);
    if (paramBoolean)
    {
      if (j == 4096)
      {
        if (this.mDepthOutputFormats.indexOfKey(i) >= 0) {
          return paramInt;
        }
      }
      else if (this.mAllOutputFormats.indexOfKey(i) >= 0) {
        return paramInt;
      }
    }
    else if (this.mInputFormats.indexOfKey(i) >= 0) {
      return paramInt;
    }
    throw new IllegalArgumentException(String.format("format %x is not supported by this stream configuration map", new Object[] { Integer.valueOf(paramInt) }));
  }
  
  static int depthFormatToPublic(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unknown DATASPACE_DEPTH format " + paramInt);
    case 33: 
      return 257;
    case 540422489: 
      return 1144402265;
    case 256: 
      throw new IllegalArgumentException("ImageFormat.JPEG is an unknown internal format");
    }
    throw new IllegalArgumentException("IMPLEMENTATION_DEFINED must not leak to public API");
  }
  
  private String formatToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case 842094169: 
      return "YV12";
    case 35: 
      return "YUV_420_888";
    case 17: 
      return "NV21";
    case 16: 
      return "NV16";
    case 4: 
      return "RGB_565";
    case 1: 
      return "RGBA_8888";
    case 2: 
      return "RGBX_8888";
    case 3: 
      return "RGB_888";
    case 256: 
      return "JPEG";
    case 20: 
      return "YUY2";
    case 538982489: 
      return "Y8";
    case 540422489: 
      return "Y16";
    case 32: 
      return "RAW_SENSOR";
    case 36: 
      return "RAW_PRIVATE";
    case 37: 
      return "RAW10";
    case 1144402265: 
      return "DEPTH16";
    case 257: 
      return "DEPTH_POINT_CLOUD";
    }
    return "PRIVATE";
  }
  
  private StreamConfigurationDuration[] getDurations(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      throw new IllegalArgumentException("duration was invalid");
    case 0: 
      if (paramInt2 == 4096) {
        return this.mDepthMinFrameDurations;
      }
      return this.mMinFrameDurations;
    }
    if (paramInt2 == 4096) {
      return this.mDepthStallDurations;
    }
    return this.mStallDurations;
  }
  
  private SparseIntArray getFormatsMap(boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.mAllOutputFormats;
    }
    return this.mInputFormats;
  }
  
  private long getInternalFormatDuration(int paramInt1, int paramInt2, Size paramSize, int paramInt3)
  {
    if (!isSupportedInternalConfiguration(paramInt1, paramInt2, paramSize)) {
      throw new IllegalArgumentException("size was not supported");
    }
    StreamConfigurationDuration[] arrayOfStreamConfigurationDuration = getDurations(paramInt3, paramInt2);
    paramInt2 = 0;
    paramInt3 = arrayOfStreamConfigurationDuration.length;
    while (paramInt2 < paramInt3)
    {
      StreamConfigurationDuration localStreamConfigurationDuration = arrayOfStreamConfigurationDuration[paramInt2];
      if ((localStreamConfigurationDuration.getFormat() == paramInt1) && (localStreamConfigurationDuration.getWidth() == paramSize.getWidth()) && (localStreamConfigurationDuration.getHeight() == paramSize.getHeight())) {
        return localStreamConfigurationDuration.getDuration();
      }
      paramInt2 += 1;
    }
    return 0L;
  }
  
  private Size[] getInternalFormatSizes(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramInt2 == 4096) && (paramBoolean2)) {
      return new Size[0];
    }
    Object localObject1;
    int m;
    if (!paramBoolean1)
    {
      localObject1 = this.mInputFormats;
      m = ((SparseIntArray)localObject1).get(paramInt1);
      if (((paramBoolean1) && (paramInt2 != 4096)) || (m != 0)) {
        break label101;
      }
    }
    label101:
    while ((paramBoolean1) && (paramInt2 != 4096) && (this.mAllOutputFormats.get(paramInt1) == 0))
    {
      throw new IllegalArgumentException("format not available");
      if (paramInt2 == 4096)
      {
        localObject1 = this.mDepthOutputFormats;
        break;
      }
      if (paramBoolean2)
      {
        localObject1 = this.mHighResOutputFormats;
        break;
      }
      localObject1 = this.mOutputFormats;
      break;
    }
    Size[] arrayOfSize = new Size[m];
    StreamConfigurationDuration[] arrayOfStreamConfigurationDuration;
    label156:
    int j;
    int i;
    label167:
    Object localObject2;
    int k;
    label220:
    boolean bool;
    if (paramInt2 == 4096)
    {
      localObject1 = this.mDepthConfigurations;
      if (paramInt2 != 4096) {
        break label332;
      }
      arrayOfStreamConfigurationDuration = this.mDepthMinFrameDurations;
      int n = localObject1.length;
      j = 0;
      i = 0;
      if (j >= n) {
        break label382;
      }
      localObject2 = localObject1[j];
      int i1 = ((StreamConfiguration)localObject2).getFormat();
      if ((i1 != paramInt1) || (((StreamConfiguration)localObject2).isOutput() != paramBoolean1)) {
        break label341;
      }
      if ((!paramBoolean1) || (!this.mListHighResolution)) {
        break label359;
      }
      long l2 = 0L;
      k = 0;
      long l1 = l2;
      if (k < arrayOfStreamConfigurationDuration.length)
      {
        StreamConfigurationDuration localStreamConfigurationDuration = arrayOfStreamConfigurationDuration[k];
        if ((localStreamConfigurationDuration.getFormat() != i1) || (localStreamConfigurationDuration.getWidth() != ((StreamConfiguration)localObject2).getSize().getWidth()) || (localStreamConfigurationDuration.getHeight() != ((StreamConfiguration)localObject2).getSize().getHeight())) {
          break label344;
        }
        l1 = localStreamConfigurationDuration.getDuration();
      }
      if (paramInt2 == 4096) {
        break label359;
      }
      if (l1 <= 50000000L) {
        break label353;
      }
      bool = true;
      label307:
      if (paramBoolean2 == bool) {
        break label359;
      }
    }
    for (;;)
    {
      j += 1;
      break label167;
      localObject1 = this.mConfigurations;
      break;
      label332:
      arrayOfStreamConfigurationDuration = this.mMinFrameDurations;
      break label156;
      label341:
      continue;
      label344:
      k += 1;
      break label220;
      label353:
      bool = false;
      break label307;
      label359:
      k = i + 1;
      arrayOfSize[i] = ((StreamConfiguration)localObject2).getSize();
      i = k;
    }
    label382:
    if (i != m) {
      throw new AssertionError("Too few sizes (expected " + m + ", actual " + i + ")");
    }
    return arrayOfSize;
  }
  
  private int getPublicFormatCount(boolean paramBoolean)
  {
    int j = getFormatsMap(paramBoolean).size();
    int i = j;
    if (paramBoolean) {
      i = j + this.mDepthOutputFormats.size();
    }
    return i;
  }
  
  private Size[] getPublicFormatSizes(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      checkArgumentFormatSupported(paramInt, paramBoolean1);
      return getInternalFormatSizes(imageFormatToInternal(paramInt), imageFormatToDataspace(paramInt), paramBoolean1, paramBoolean2);
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return null;
  }
  
  private int[] getPublicFormats(boolean paramBoolean)
  {
    int[] arrayOfInt = new int[getPublicFormatCount(paramBoolean)];
    int i = 0;
    SparseIntArray localSparseIntArray = getFormatsMap(paramBoolean);
    int j = 0;
    while (j < localSparseIntArray.size())
    {
      arrayOfInt[i] = imageFormatToPublic(localSparseIntArray.keyAt(j));
      j += 1;
      i += 1;
    }
    int k = i;
    if (paramBoolean)
    {
      j = 0;
      for (;;)
      {
        k = i;
        if (j >= this.mDepthOutputFormats.size()) {
          break;
        }
        arrayOfInt[i] = depthFormatToPublic(this.mDepthOutputFormats.keyAt(j));
        j += 1;
        i += 1;
      }
    }
    if (arrayOfInt.length != k) {
      throw new AssertionError("Too few formats " + k + ", expected " + arrayOfInt.length);
    }
    return arrayOfInt;
  }
  
  static int imageFormatToDataspace(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 256: 
      return 146931712;
    }
    return 4096;
  }
  
  static int imageFormatToInternal(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return paramInt;
    case 256: 
    case 257: 
      return 33;
    }
    return 540422489;
  }
  
  public static int[] imageFormatToInternal(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return null;
    }
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      paramArrayOfInt[i] = imageFormatToInternal(paramArrayOfInt[i]);
      i += 1;
    }
    return paramArrayOfInt;
  }
  
  static int imageFormatToPublic(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return paramInt;
    case 33: 
      return 256;
    }
    throw new IllegalArgumentException("ImageFormat.JPEG is an unknown internal format");
  }
  
  static int[] imageFormatToPublic(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return null;
    }
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      paramArrayOfInt[i] = imageFormatToPublic(paramArrayOfInt[i]);
      i += 1;
    }
    return paramArrayOfInt;
  }
  
  public static <T> boolean isOutputSupportedFor(Class<T> paramClass)
  {
    Preconditions.checkNotNull(paramClass, "klass must not be null");
    if (paramClass == ImageReader.class) {
      return true;
    }
    if (paramClass == MediaRecorder.class) {
      return true;
    }
    if (paramClass == MediaCodec.class) {
      return true;
    }
    if (paramClass == Allocation.class) {
      return true;
    }
    if (paramClass == SurfaceHolder.class) {
      return true;
    }
    return paramClass == SurfaceTexture.class;
  }
  
  private boolean isSupportedInternalConfiguration(int paramInt1, int paramInt2, Size paramSize)
  {
    StreamConfiguration[] arrayOfStreamConfiguration;
    if (paramInt2 == 4096)
    {
      arrayOfStreamConfiguration = this.mDepthConfigurations;
      paramInt2 = 0;
    }
    for (;;)
    {
      if (paramInt2 >= arrayOfStreamConfiguration.length) {
        break label65;
      }
      if ((arrayOfStreamConfiguration[paramInt2].getFormat() == paramInt1) && (arrayOfStreamConfiguration[paramInt2].getSize().equals(paramSize)))
      {
        return true;
        arrayOfStreamConfiguration = this.mConfigurations;
        break;
      }
      paramInt2 += 1;
    }
    label65:
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof StreamConfigurationMap))
    {
      paramObject = (StreamConfigurationMap)paramObject;
      boolean bool1 = bool2;
      if (Arrays.equals(this.mConfigurations, ((StreamConfigurationMap)paramObject).mConfigurations))
      {
        bool1 = bool2;
        if (Arrays.equals(this.mMinFrameDurations, ((StreamConfigurationMap)paramObject).mMinFrameDurations))
        {
          bool1 = bool2;
          if (Arrays.equals(this.mStallDurations, ((StreamConfigurationMap)paramObject).mStallDurations))
          {
            bool1 = bool2;
            if (Arrays.equals(this.mDepthConfigurations, ((StreamConfigurationMap)paramObject).mDepthConfigurations)) {
              bool1 = Arrays.equals(this.mHighSpeedVideoConfigurations, ((StreamConfigurationMap)paramObject).mHighSpeedVideoConfigurations);
            }
          }
        }
      }
      return bool1;
    }
    return false;
  }
  
  public Size[] getHighResolutionOutputSizes(int paramInt)
  {
    if (!this.mListHighResolution) {
      return null;
    }
    return getPublicFormatSizes(paramInt, true, true);
  }
  
  public Range<Integer>[] getHighSpeedVideoFpsRanges()
  {
    Set localSet = this.mHighSpeedVideoFpsRangeMap.keySet();
    return (Range[])localSet.toArray(new Range[localSet.size()]);
  }
  
  public Range<Integer>[] getHighSpeedVideoFpsRangesFor(Size paramSize)
  {
    int j = 0;
    Object localObject = (Integer)this.mHighSpeedVideoSizeMap.get(paramSize);
    if ((localObject == null) || (((Integer)localObject).intValue() == 0)) {
      throw new IllegalArgumentException(String.format("Size %s does not support high speed video recording", new Object[] { paramSize }));
    }
    localObject = new Range[((Integer)localObject).intValue()];
    HighSpeedVideoConfiguration[] arrayOfHighSpeedVideoConfiguration = this.mHighSpeedVideoConfigurations;
    int m = arrayOfHighSpeedVideoConfiguration.length;
    int i = 0;
    if (j < m)
    {
      HighSpeedVideoConfiguration localHighSpeedVideoConfiguration = arrayOfHighSpeedVideoConfiguration[j];
      if (!paramSize.equals(localHighSpeedVideoConfiguration.getSize())) {
        break label124;
      }
      int k = i + 1;
      localObject[i] = localHighSpeedVideoConfiguration.getFpsRange();
      i = k;
    }
    label124:
    for (;;)
    {
      j += 1;
      break;
      return (Range<Integer>[])localObject;
    }
  }
  
  public Size[] getHighSpeedVideoSizes()
  {
    Set localSet = this.mHighSpeedVideoSizeMap.keySet();
    return (Size[])localSet.toArray(new Size[localSet.size()]);
  }
  
  public Size[] getHighSpeedVideoSizesFor(Range<Integer> paramRange)
  {
    int j = 0;
    Object localObject = (Integer)this.mHighSpeedVideoFpsRangeMap.get(paramRange);
    if ((localObject == null) || (((Integer)localObject).intValue() == 0)) {
      throw new IllegalArgumentException(String.format("FpsRange %s does not support high speed video recording", new Object[] { paramRange }));
    }
    localObject = new Size[((Integer)localObject).intValue()];
    HighSpeedVideoConfiguration[] arrayOfHighSpeedVideoConfiguration = this.mHighSpeedVideoConfigurations;
    int m = arrayOfHighSpeedVideoConfiguration.length;
    int i = 0;
    if (j < m)
    {
      HighSpeedVideoConfiguration localHighSpeedVideoConfiguration = arrayOfHighSpeedVideoConfiguration[j];
      if (!paramRange.equals(localHighSpeedVideoConfiguration.getFpsRange())) {
        break label124;
      }
      int k = i + 1;
      localObject[i] = localHighSpeedVideoConfiguration.getSize();
      i = k;
    }
    label124:
    for (;;)
    {
      j += 1;
      break;
      return (Size[])localObject;
    }
  }
  
  public final int[] getInputFormats()
  {
    return getPublicFormats(false);
  }
  
  public Size[] getInputSizes(int paramInt)
  {
    return getPublicFormatSizes(paramInt, false, false);
  }
  
  public final int[] getOutputFormats()
  {
    return getPublicFormats(true);
  }
  
  public long getOutputMinFrameDuration(int paramInt, Size paramSize)
  {
    Preconditions.checkNotNull(paramSize, "size must not be null");
    checkArgumentFormatSupported(paramInt, true);
    return getInternalFormatDuration(imageFormatToInternal(paramInt), imageFormatToDataspace(paramInt), paramSize, 0);
  }
  
  public <T> long getOutputMinFrameDuration(Class<T> paramClass, Size paramSize)
  {
    if (!isOutputSupportedFor(paramClass)) {
      throw new IllegalArgumentException("klass was not supported");
    }
    return getInternalFormatDuration(34, 0, paramSize, 0);
  }
  
  public Size[] getOutputSizes(int paramInt)
  {
    return getPublicFormatSizes(paramInt, true, false);
  }
  
  public <T> Size[] getOutputSizes(Class<T> paramClass)
  {
    if (!isOutputSupportedFor(paramClass)) {
      return null;
    }
    return getInternalFormatSizes(34, 0, true, false);
  }
  
  public long getOutputStallDuration(int paramInt, Size paramSize)
  {
    checkArgumentFormatSupported(paramInt, true);
    return getInternalFormatDuration(imageFormatToInternal(paramInt), imageFormatToDataspace(paramInt), paramSize, 1);
  }
  
  public <T> long getOutputStallDuration(Class<T> paramClass, Size paramSize)
  {
    if (!isOutputSupportedFor(paramClass)) {
      throw new IllegalArgumentException("klass was not supported");
    }
    return getInternalFormatDuration(34, 0, paramSize, 1);
  }
  
  public final int[] getValidOutputFormatsForInput(int paramInt)
  {
    if (this.mInputOutputFormatsMap == null) {
      return new int[0];
    }
    return this.mInputOutputFormatsMap.getOutputs(paramInt);
  }
  
  public int hashCode()
  {
    return HashCodeHelpers.hashCodeGeneric(new Object[][] { this.mConfigurations, this.mMinFrameDurations, this.mStallDurations, this.mDepthConfigurations, this.mHighSpeedVideoConfigurations });
  }
  
  public boolean isOutputSupportedFor(int paramInt)
  {
    checkArgumentFormat(paramInt);
    int i = imageFormatToInternal(paramInt);
    if (imageFormatToDataspace(paramInt) == 4096) {
      return this.mDepthOutputFormats.indexOfKey(i) >= 0;
    }
    return getFormatsMap(true).indexOfKey(i) >= 0;
  }
  
  public boolean isOutputSupportedFor(Surface paramSurface)
  {
    Preconditions.checkNotNull(paramSurface, "surface must not be null");
    Size localSize = SurfaceUtils.getSurfaceSize(paramSurface);
    int j = SurfaceUtils.getSurfaceFormat(paramSurface);
    int k = SurfaceUtils.getSurfaceDataspace(paramSurface);
    boolean bool = SurfaceUtils.isFlexibleConsumer(paramSurface);
    int i = j;
    if (j >= 1)
    {
      i = j;
      if (j <= 5) {
        i = 34;
      }
    }
    if (k != 4096)
    {
      paramSurface = this.mConfigurations;
      k = paramSurface.length;
      j = 0;
    }
    for (;;)
    {
      if (j >= k) {
        break label146;
      }
      Object localObject = paramSurface[j];
      if ((((StreamConfiguration)localObject).getFormat() == i) && (((StreamConfiguration)localObject).isOutput()))
      {
        if (((StreamConfiguration)localObject).getSize().equals(localSize))
        {
          return true;
          paramSurface = this.mDepthConfigurations;
          break;
        }
        if ((bool) && (((StreamConfiguration)localObject).getSize().getWidth() <= 1920)) {
          return true;
        }
      }
      j += 1;
    }
    label146:
    return false;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("StreamConfiguration(");
    appendOutputsString(localStringBuilder);
    localStringBuilder.append(", ");
    appendHighResOutputsString(localStringBuilder);
    localStringBuilder.append(", ");
    appendInputsString(localStringBuilder);
    localStringBuilder.append(", ");
    appendValidOutputFormatsForInputString(localStringBuilder);
    localStringBuilder.append(", ");
    appendHighSpeedVideoConfigurationsString(localStringBuilder);
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/StreamConfigurationMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */