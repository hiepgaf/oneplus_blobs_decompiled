package android.print;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;

public final class PrinterCapabilitiesInfo
  implements Parcelable
{
  public static final Parcelable.Creator<PrinterCapabilitiesInfo> CREATOR = new Parcelable.Creator()
  {
    public PrinterCapabilitiesInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PrinterCapabilitiesInfo(paramAnonymousParcel, null);
    }
    
    public PrinterCapabilitiesInfo[] newArray(int paramAnonymousInt)
    {
      return new PrinterCapabilitiesInfo[paramAnonymousInt];
    }
  };
  private static final PrintAttributes.Margins DEFAULT_MARGINS = new PrintAttributes.Margins(0, 0, 0, 0);
  public static final int DEFAULT_UNDEFINED = -1;
  private static final int PROPERTY_COLOR_MODE = 2;
  private static final int PROPERTY_COUNT = 4;
  private static final int PROPERTY_DUPLEX_MODE = 3;
  private static final int PROPERTY_MEDIA_SIZE = 0;
  private static final int PROPERTY_RESOLUTION = 1;
  private int mColorModes;
  private final int[] mDefaults = new int[4];
  private int mDuplexModes;
  private List<PrintAttributes.MediaSize> mMediaSizes;
  private PrintAttributes.Margins mMinMargins = DEFAULT_MARGINS;
  private List<PrintAttributes.Resolution> mResolutions;
  
  public PrinterCapabilitiesInfo()
  {
    Arrays.fill(this.mDefaults, -1);
  }
  
  private PrinterCapabilitiesInfo(Parcel paramParcel)
  {
    this.mMinMargins = ((PrintAttributes.Margins)Preconditions.checkNotNull(readMargins(paramParcel)));
    readMediaSizes(paramParcel);
    readResolutions(paramParcel);
    this.mColorModes = paramParcel.readInt();
    enforceValidMask(this.mColorModes, new -void__init__android_os_Parcel_parcel_LambdaImpl0());
    this.mDuplexModes = paramParcel.readInt();
    enforceValidMask(this.mDuplexModes, new -void__init__android_os_Parcel_parcel_LambdaImpl1());
    readDefaults(paramParcel);
    if (this.mMediaSizes.size() > this.mDefaults[0])
    {
      bool1 = true;
      Preconditions.checkArgument(bool1);
      if (this.mResolutions.size() <= this.mDefaults[1]) {
        break label148;
      }
    }
    label148:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      Preconditions.checkArgument(bool1);
      return;
      bool1 = false;
      break;
    }
  }
  
  public PrinterCapabilitiesInfo(PrinterCapabilitiesInfo paramPrinterCapabilitiesInfo)
  {
    copyFrom(paramPrinterCapabilitiesInfo);
  }
  
  private String colorModesToString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('[');
    int i = this.mColorModes;
    while (i != 0)
    {
      int j = 1 << Integer.numberOfTrailingZeros(i);
      i &= j;
      if (localStringBuilder.length() > 1) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append(PrintAttributes.colorModeToString(j));
    }
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  private String duplexModesToString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('[');
    int i = this.mDuplexModes;
    while (i != 0)
    {
      int j = 1 << Integer.numberOfTrailingZeros(i);
      i &= j;
      if (localStringBuilder.length() > 1) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append(PrintAttributes.duplexModeToString(j));
    }
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  private static void enforceValidMask(int paramInt, IntConsumer paramIntConsumer)
  {
    while (paramInt > 0)
    {
      int i = 1 << Integer.numberOfTrailingZeros(paramInt);
      paramInt &= i;
      paramIntConsumer.accept(i);
    }
  }
  
  private void readDefaults(Parcel paramParcel)
  {
    int j = paramParcel.readInt();
    int i = 0;
    while (i < j)
    {
      this.mDefaults[i] = paramParcel.readInt();
      i += 1;
    }
  }
  
  private PrintAttributes.Margins readMargins(Parcel paramParcel)
  {
    if (paramParcel.readInt() == 1) {
      return PrintAttributes.Margins.createFromParcel(paramParcel);
    }
    return null;
  }
  
  private void readMediaSizes(Parcel paramParcel)
  {
    int j = paramParcel.readInt();
    if ((j > 0) && (this.mMediaSizes == null)) {
      this.mMediaSizes = new ArrayList();
    }
    int i = 0;
    while (i < j)
    {
      this.mMediaSizes.add(PrintAttributes.MediaSize.createFromParcel(paramParcel));
      i += 1;
    }
  }
  
  private void readResolutions(Parcel paramParcel)
  {
    int j = paramParcel.readInt();
    if ((j > 0) && (this.mResolutions == null)) {
      this.mResolutions = new ArrayList();
    }
    int i = 0;
    while (i < j)
    {
      this.mResolutions.add(PrintAttributes.Resolution.createFromParcel(paramParcel));
      i += 1;
    }
  }
  
  private void writeDefaults(Parcel paramParcel)
  {
    int j = this.mDefaults.length;
    paramParcel.writeInt(j);
    int i = 0;
    while (i < j)
    {
      paramParcel.writeInt(this.mDefaults[i]);
      i += 1;
    }
  }
  
  private void writeMargins(PrintAttributes.Margins paramMargins, Parcel paramParcel)
  {
    if (paramMargins == null)
    {
      paramParcel.writeInt(0);
      return;
    }
    paramParcel.writeInt(1);
    paramMargins.writeToParcel(paramParcel);
  }
  
  private void writeMediaSizes(Parcel paramParcel)
  {
    if (this.mMediaSizes == null)
    {
      paramParcel.writeInt(0);
      return;
    }
    int j = this.mMediaSizes.size();
    paramParcel.writeInt(j);
    int i = 0;
    while (i < j)
    {
      ((PrintAttributes.MediaSize)this.mMediaSizes.get(i)).writeToParcel(paramParcel);
      i += 1;
    }
  }
  
  private void writeResolutions(Parcel paramParcel)
  {
    if (this.mResolutions == null)
    {
      paramParcel.writeInt(0);
      return;
    }
    int j = this.mResolutions.size();
    paramParcel.writeInt(j);
    int i = 0;
    while (i < j)
    {
      ((PrintAttributes.Resolution)this.mResolutions.get(i)).writeToParcel(paramParcel);
      i += 1;
    }
  }
  
  public void copyFrom(PrinterCapabilitiesInfo paramPrinterCapabilitiesInfo)
  {
    if (this == paramPrinterCapabilitiesInfo) {
      return;
    }
    this.mMinMargins = paramPrinterCapabilitiesInfo.mMinMargins;
    if (paramPrinterCapabilitiesInfo.mMediaSizes != null) {
      if (this.mMediaSizes != null)
      {
        this.mMediaSizes.clear();
        this.mMediaSizes.addAll(paramPrinterCapabilitiesInfo.mMediaSizes);
        if (paramPrinterCapabilitiesInfo.mResolutions == null) {
          break label180;
        }
        if (this.mResolutions == null) {
          break label162;
        }
        this.mResolutions.clear();
        this.mResolutions.addAll(paramPrinterCapabilitiesInfo.mResolutions);
      }
    }
    for (;;)
    {
      this.mColorModes = paramPrinterCapabilitiesInfo.mColorModes;
      this.mDuplexModes = paramPrinterCapabilitiesInfo.mDuplexModes;
      int j = paramPrinterCapabilitiesInfo.mDefaults.length;
      int i = 0;
      while (i < j)
      {
        this.mDefaults[i] = paramPrinterCapabilitiesInfo.mDefaults[i];
        i += 1;
      }
      this.mMediaSizes = new ArrayList(paramPrinterCapabilitiesInfo.mMediaSizes);
      break;
      this.mMediaSizes = null;
      break;
      label162:
      this.mResolutions = new ArrayList(paramPrinterCapabilitiesInfo.mResolutions);
      continue;
      label180:
      this.mResolutions = null;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (PrinterCapabilitiesInfo)paramObject;
    if (this.mMinMargins == null)
    {
      if (((PrinterCapabilitiesInfo)paramObject).mMinMargins != null) {
        return false;
      }
    }
    else if (!this.mMinMargins.equals(((PrinterCapabilitiesInfo)paramObject).mMinMargins)) {
      return false;
    }
    if (this.mMediaSizes == null)
    {
      if (((PrinterCapabilitiesInfo)paramObject).mMediaSizes != null) {
        return false;
      }
    }
    else if (!this.mMediaSizes.equals(((PrinterCapabilitiesInfo)paramObject).mMediaSizes)) {
      return false;
    }
    if (this.mResolutions == null)
    {
      if (((PrinterCapabilitiesInfo)paramObject).mResolutions != null) {
        return false;
      }
    }
    else if (!this.mResolutions.equals(((PrinterCapabilitiesInfo)paramObject).mResolutions)) {
      return false;
    }
    if (this.mColorModes != ((PrinterCapabilitiesInfo)paramObject).mColorModes) {
      return false;
    }
    if (this.mDuplexModes != ((PrinterCapabilitiesInfo)paramObject).mDuplexModes) {
      return false;
    }
    return Arrays.equals(this.mDefaults, ((PrinterCapabilitiesInfo)paramObject).mDefaults);
  }
  
  public int getColorModes()
  {
    return this.mColorModes;
  }
  
  public PrintAttributes getDefaults()
  {
    PrintAttributes.Builder localBuilder = new PrintAttributes.Builder();
    localBuilder.setMinMargins(this.mMinMargins);
    int i = this.mDefaults[0];
    if (i >= 0) {
      localBuilder.setMediaSize((PrintAttributes.MediaSize)this.mMediaSizes.get(i));
    }
    i = this.mDefaults[1];
    if (i >= 0) {
      localBuilder.setResolution((PrintAttributes.Resolution)this.mResolutions.get(i));
    }
    i = this.mDefaults[2];
    if (i > 0) {
      localBuilder.setColorMode(i);
    }
    i = this.mDefaults[3];
    if (i > 0) {
      localBuilder.setDuplexMode(i);
    }
    return localBuilder.build();
  }
  
  public int getDuplexModes()
  {
    return this.mDuplexModes;
  }
  
  public List<PrintAttributes.MediaSize> getMediaSizes()
  {
    return Collections.unmodifiableList(this.mMediaSizes);
  }
  
  public PrintAttributes.Margins getMinMargins()
  {
    return this.mMinMargins;
  }
  
  public List<PrintAttributes.Resolution> getResolutions()
  {
    return Collections.unmodifiableList(this.mResolutions);
  }
  
  public int hashCode()
  {
    int k = 0;
    int i;
    int j;
    if (this.mMinMargins == null)
    {
      i = 0;
      if (this.mMediaSizes != null) {
        break label80;
      }
      j = 0;
      label20:
      if (this.mResolutions != null) {
        break label93;
      }
    }
    for (;;)
    {
      return (((((i + 31) * 31 + j) * 31 + k) * 31 + this.mColorModes) * 31 + this.mDuplexModes) * 31 + Arrays.hashCode(this.mDefaults);
      i = this.mMinMargins.hashCode();
      break;
      label80:
      j = this.mMediaSizes.hashCode();
      break label20;
      label93:
      k = this.mResolutions.hashCode();
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PrinterInfo{");
    localStringBuilder.append("minMargins=").append(this.mMinMargins);
    localStringBuilder.append(", mediaSizes=").append(this.mMediaSizes);
    localStringBuilder.append(", resolutions=").append(this.mResolutions);
    localStringBuilder.append(", colorModes=").append(colorModesToString());
    localStringBuilder.append(", duplexModes=").append(duplexModesToString());
    localStringBuilder.append("\"}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    writeMargins(this.mMinMargins, paramParcel);
    writeMediaSizes(paramParcel);
    writeResolutions(paramParcel);
    paramParcel.writeInt(this.mColorModes);
    paramParcel.writeInt(this.mDuplexModes);
    writeDefaults(paramParcel);
  }
  
  public static final class Builder
  {
    private final PrinterCapabilitiesInfo mPrototype;
    
    public Builder(PrinterId paramPrinterId)
    {
      if (paramPrinterId == null) {
        throw new IllegalArgumentException("printerId cannot be null.");
      }
      this.mPrototype = new PrinterCapabilitiesInfo();
    }
    
    private void throwIfDefaultAlreadySpecified(int paramInt)
    {
      if (PrinterCapabilitiesInfo.-get1(this.mPrototype)[paramInt] != -1) {
        throw new IllegalArgumentException("Default already specified.");
      }
    }
    
    public Builder addMediaSize(PrintAttributes.MediaSize paramMediaSize, boolean paramBoolean)
    {
      if (PrinterCapabilitiesInfo.-get3(this.mPrototype) == null) {
        PrinterCapabilitiesInfo.-set2(this.mPrototype, new ArrayList());
      }
      int i = PrinterCapabilitiesInfo.-get3(this.mPrototype).size();
      PrinterCapabilitiesInfo.-get3(this.mPrototype).add(paramMediaSize);
      if (paramBoolean)
      {
        throwIfDefaultAlreadySpecified(0);
        PrinterCapabilitiesInfo.-get1(this.mPrototype)[0] = i;
      }
      return this;
    }
    
    public Builder addResolution(PrintAttributes.Resolution paramResolution, boolean paramBoolean)
    {
      if (PrinterCapabilitiesInfo.-get5(this.mPrototype) == null) {
        PrinterCapabilitiesInfo.-set4(this.mPrototype, new ArrayList());
      }
      int i = PrinterCapabilitiesInfo.-get5(this.mPrototype).size();
      PrinterCapabilitiesInfo.-get5(this.mPrototype).add(paramResolution);
      if (paramBoolean)
      {
        throwIfDefaultAlreadySpecified(1);
        PrinterCapabilitiesInfo.-get1(this.mPrototype)[1] = i;
      }
      return this;
    }
    
    public PrinterCapabilitiesInfo build()
    {
      if ((PrinterCapabilitiesInfo.-get3(this.mPrototype) == null) || (PrinterCapabilitiesInfo.-get3(this.mPrototype).isEmpty())) {
        throw new IllegalStateException("No media size specified.");
      }
      if (PrinterCapabilitiesInfo.-get1(this.mPrototype)[0] == -1) {
        throw new IllegalStateException("No default media size specified.");
      }
      if ((PrinterCapabilitiesInfo.-get5(this.mPrototype) == null) || (PrinterCapabilitiesInfo.-get5(this.mPrototype).isEmpty())) {
        throw new IllegalStateException("No resolution specified.");
      }
      if (PrinterCapabilitiesInfo.-get1(this.mPrototype)[1] == -1) {
        throw new IllegalStateException("No default resolution specified.");
      }
      if (PrinterCapabilitiesInfo.-get0(this.mPrototype) == 0) {
        throw new IllegalStateException("No color mode specified.");
      }
      if (PrinterCapabilitiesInfo.-get1(this.mPrototype)[2] == -1) {
        throw new IllegalStateException("No default color mode specified.");
      }
      if (PrinterCapabilitiesInfo.-get2(this.mPrototype) == 0) {
        setDuplexModes(1, 1);
      }
      if (PrinterCapabilitiesInfo.-get4(this.mPrototype) == null) {
        throw new IllegalArgumentException("margins cannot be null");
      }
      return this.mPrototype;
    }
    
    public Builder setColorModes(int paramInt1, int paramInt2)
    {
      PrinterCapabilitiesInfo.-wrap0(paramInt1, new -android_print_PrinterCapabilitiesInfo.Builder_setColorModes_int_colorModes_int_defaultColorMode_LambdaImpl0());
      PrintAttributes.enforceValidColorMode(paramInt2);
      PrinterCapabilitiesInfo.-set0(this.mPrototype, paramInt1);
      PrinterCapabilitiesInfo.-get1(this.mPrototype)[2] = paramInt2;
      return this;
    }
    
    public Builder setDuplexModes(int paramInt1, int paramInt2)
    {
      PrinterCapabilitiesInfo.-wrap0(paramInt1, new -android_print_PrinterCapabilitiesInfo.Builder_setDuplexModes_int_duplexModes_int_defaultDuplexMode_LambdaImpl0());
      PrintAttributes.enforceValidDuplexMode(paramInt2);
      PrinterCapabilitiesInfo.-set1(this.mPrototype, paramInt1);
      PrinterCapabilitiesInfo.-get1(this.mPrototype)[3] = paramInt2;
      return this;
    }
    
    public Builder setMinMargins(PrintAttributes.Margins paramMargins)
    {
      if (paramMargins == null) {
        throw new IllegalArgumentException("margins cannot be null");
      }
      PrinterCapabilitiesInfo.-set3(this.mPrototype, paramMargins);
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrinterCapabilitiesInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */