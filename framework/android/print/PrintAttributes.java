package android.print;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.Map;

public final class PrintAttributes
  implements Parcelable
{
  public static final int COLOR_MODE_COLOR = 2;
  public static final int COLOR_MODE_MONOCHROME = 1;
  public static final Parcelable.Creator<PrintAttributes> CREATOR = new Parcelable.Creator()
  {
    public PrintAttributes createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PrintAttributes(paramAnonymousParcel, null);
    }
    
    public PrintAttributes[] newArray(int paramAnonymousInt)
    {
      return new PrintAttributes[paramAnonymousInt];
    }
  };
  public static final int DUPLEX_MODE_LONG_EDGE = 2;
  public static final int DUPLEX_MODE_NONE = 1;
  public static final int DUPLEX_MODE_SHORT_EDGE = 4;
  private static final int VALID_COLOR_MODES = 3;
  private static final int VALID_DUPLEX_MODES = 7;
  private int mColorMode;
  private int mDuplexMode;
  private MediaSize mMediaSize;
  private Margins mMinMargins;
  private Resolution mResolution;
  
  PrintAttributes() {}
  
  private PrintAttributes(Parcel paramParcel)
  {
    if (paramParcel.readInt() == 1)
    {
      localObject1 = MediaSize.createFromParcel(paramParcel);
      this.mMediaSize = ((MediaSize)localObject1);
      if (paramParcel.readInt() != 1) {
        break label112;
      }
    }
    label112:
    for (Object localObject1 = Resolution.createFromParcel(paramParcel);; localObject1 = null)
    {
      this.mResolution = ((Resolution)localObject1);
      localObject1 = localObject2;
      if (paramParcel.readInt() == 1) {
        localObject1 = Margins.createFromParcel(paramParcel);
      }
      this.mMinMargins = ((Margins)localObject1);
      this.mColorMode = paramParcel.readInt();
      if (this.mColorMode != 0) {
        enforceValidColorMode(this.mColorMode);
      }
      this.mDuplexMode = paramParcel.readInt();
      if (this.mDuplexMode != 0) {
        enforceValidDuplexMode(this.mDuplexMode);
      }
      return;
      localObject1 = null;
      break;
    }
  }
  
  static String colorModeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "COLOR_MODE_UNKNOWN";
    case 1: 
      return "COLOR_MODE_MONOCHROME";
    }
    return "COLOR_MODE_COLOR";
  }
  
  static String duplexModeToString(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
    default: 
      return "DUPLEX_MODE_UNKNOWN";
    case 1: 
      return "DUPLEX_MODE_NONE";
    case 2: 
      return "DUPLEX_MODE_LONG_EDGE";
    }
    return "DUPLEX_MODE_SHORT_EDGE";
  }
  
  static void enforceValidColorMode(int paramInt)
  {
    if (((paramInt & 0x3) == 0) || (Integer.bitCount(paramInt) != 1)) {
      throw new IllegalArgumentException("invalid color mode: " + paramInt);
    }
  }
  
  static void enforceValidDuplexMode(int paramInt)
  {
    if (((paramInt & 0x7) == 0) || (Integer.bitCount(paramInt) != 1)) {
      throw new IllegalArgumentException("invalid duplex mode: " + paramInt);
    }
  }
  
  public PrintAttributes asLandscape()
  {
    if (!isPortrait()) {
      return this;
    }
    PrintAttributes localPrintAttributes = new PrintAttributes();
    localPrintAttributes.setMediaSize(getMediaSize().asLandscape());
    Resolution localResolution = getResolution();
    localPrintAttributes.setResolution(new Resolution(localResolution.getId(), localResolution.getLabel(), localResolution.getVerticalDpi(), localResolution.getHorizontalDpi()));
    localPrintAttributes.setMinMargins(getMinMargins());
    localPrintAttributes.setColorMode(getColorMode());
    localPrintAttributes.setDuplexMode(getDuplexMode());
    return localPrintAttributes;
  }
  
  public PrintAttributes asPortrait()
  {
    if (isPortrait()) {
      return this;
    }
    PrintAttributes localPrintAttributes = new PrintAttributes();
    localPrintAttributes.setMediaSize(getMediaSize().asPortrait());
    Resolution localResolution = getResolution();
    localPrintAttributes.setResolution(new Resolution(localResolution.getId(), localResolution.getLabel(), localResolution.getVerticalDpi(), localResolution.getHorizontalDpi()));
    localPrintAttributes.setMinMargins(getMinMargins());
    localPrintAttributes.setColorMode(getColorMode());
    localPrintAttributes.setDuplexMode(getDuplexMode());
    return localPrintAttributes;
  }
  
  public void clear()
  {
    this.mMediaSize = null;
    this.mResolution = null;
    this.mMinMargins = null;
    this.mColorMode = 0;
    this.mDuplexMode = 0;
  }
  
  public void copyFrom(PrintAttributes paramPrintAttributes)
  {
    this.mMediaSize = paramPrintAttributes.mMediaSize;
    this.mResolution = paramPrintAttributes.mResolution;
    this.mMinMargins = paramPrintAttributes.mMinMargins;
    this.mColorMode = paramPrintAttributes.mColorMode;
    this.mDuplexMode = paramPrintAttributes.mDuplexMode;
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
    paramObject = (PrintAttributes)paramObject;
    if (this.mColorMode != ((PrintAttributes)paramObject).mColorMode) {
      return false;
    }
    if (this.mDuplexMode != ((PrintAttributes)paramObject).mDuplexMode) {
      return false;
    }
    if (this.mMinMargins == null)
    {
      if (((PrintAttributes)paramObject).mMinMargins != null) {
        return false;
      }
    }
    else if (!this.mMinMargins.equals(((PrintAttributes)paramObject).mMinMargins)) {
      return false;
    }
    if (this.mMediaSize == null)
    {
      if (((PrintAttributes)paramObject).mMediaSize != null) {
        return false;
      }
    }
    else if (!this.mMediaSize.equals(((PrintAttributes)paramObject).mMediaSize)) {
      return false;
    }
    if (this.mResolution == null)
    {
      if (((PrintAttributes)paramObject).mResolution != null) {
        return false;
      }
    }
    else if (!this.mResolution.equals(((PrintAttributes)paramObject).mResolution)) {
      return false;
    }
    return true;
  }
  
  public int getColorMode()
  {
    return this.mColorMode;
  }
  
  public int getDuplexMode()
  {
    return this.mDuplexMode;
  }
  
  public MediaSize getMediaSize()
  {
    return this.mMediaSize;
  }
  
  public Margins getMinMargins()
  {
    return this.mMinMargins;
  }
  
  public Resolution getResolution()
  {
    return this.mResolution;
  }
  
  public int hashCode()
  {
    int k = 0;
    int m = this.mColorMode;
    int n = this.mDuplexMode;
    int i;
    int j;
    if (this.mMinMargins == null)
    {
      i = 0;
      if (this.mMediaSize != null) {
        break label77;
      }
      j = 0;
      label32:
      if (this.mResolution != null) {
        break label88;
      }
    }
    for (;;)
    {
      return ((((m + 31) * 31 + n) * 31 + i) * 31 + j) * 31 + k;
      i = this.mMinMargins.hashCode();
      break;
      label77:
      j = this.mMediaSize.hashCode();
      break label32;
      label88:
      k = this.mResolution.hashCode();
    }
  }
  
  public boolean isPortrait()
  {
    return this.mMediaSize.isPortrait();
  }
  
  public void setColorMode(int paramInt)
  {
    enforceValidColorMode(paramInt);
    this.mColorMode = paramInt;
  }
  
  public void setDuplexMode(int paramInt)
  {
    enforceValidDuplexMode(paramInt);
    this.mDuplexMode = paramInt;
  }
  
  public void setMediaSize(MediaSize paramMediaSize)
  {
    this.mMediaSize = paramMediaSize;
  }
  
  public void setMinMargins(Margins paramMargins)
  {
    this.mMinMargins = paramMargins;
  }
  
  public void setResolution(Resolution paramResolution)
  {
    this.mResolution = paramResolution;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("PrintAttributes{");
    localStringBuilder1.append("mediaSize: ").append(this.mMediaSize);
    String str;
    if (this.mMediaSize != null)
    {
      StringBuilder localStringBuilder2 = localStringBuilder1.append(", orientation: ");
      if (this.mMediaSize.isPortrait())
      {
        str = "portrait";
        localStringBuilder2.append(str);
      }
    }
    for (;;)
    {
      localStringBuilder1.append(", resolution: ").append(this.mResolution);
      localStringBuilder1.append(", minMargins: ").append(this.mMinMargins);
      localStringBuilder1.append(", colorMode: ").append(colorModeToString(this.mColorMode));
      localStringBuilder1.append(", duplexMode: ").append(duplexModeToString(this.mDuplexMode));
      localStringBuilder1.append("}");
      return localStringBuilder1.toString();
      str = "landscape";
      break;
      localStringBuilder1.append(", orientation: ").append("null");
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mMediaSize != null)
    {
      paramParcel.writeInt(1);
      this.mMediaSize.writeToParcel(paramParcel);
      if (this.mResolution == null) {
        break label85;
      }
      paramParcel.writeInt(1);
      this.mResolution.writeToParcel(paramParcel);
      label40:
      if (this.mMinMargins == null) {
        break label93;
      }
      paramParcel.writeInt(1);
      this.mMinMargins.writeToParcel(paramParcel);
    }
    for (;;)
    {
      paramParcel.writeInt(this.mColorMode);
      paramParcel.writeInt(this.mDuplexMode);
      return;
      paramParcel.writeInt(0);
      break;
      label85:
      paramParcel.writeInt(0);
      break label40;
      label93:
      paramParcel.writeInt(0);
    }
  }
  
  public static final class Builder
  {
    private final PrintAttributes mAttributes = new PrintAttributes();
    
    public PrintAttributes build()
    {
      return this.mAttributes;
    }
    
    public Builder setColorMode(int paramInt)
    {
      this.mAttributes.setColorMode(paramInt);
      return this;
    }
    
    public Builder setDuplexMode(int paramInt)
    {
      this.mAttributes.setDuplexMode(paramInt);
      return this;
    }
    
    public Builder setMediaSize(PrintAttributes.MediaSize paramMediaSize)
    {
      this.mAttributes.setMediaSize(paramMediaSize);
      return this;
    }
    
    public Builder setMinMargins(PrintAttributes.Margins paramMargins)
    {
      this.mAttributes.setMinMargins(paramMargins);
      return this;
    }
    
    public Builder setResolution(PrintAttributes.Resolution paramResolution)
    {
      this.mAttributes.setResolution(paramResolution);
      return this;
    }
  }
  
  public static final class Margins
  {
    public static final Margins NO_MARGINS = new Margins(0, 0, 0, 0);
    private final int mBottomMils;
    private final int mLeftMils;
    private final int mRightMils;
    private final int mTopMils;
    
    public Margins(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.mTopMils = paramInt2;
      this.mLeftMils = paramInt1;
      this.mRightMils = paramInt3;
      this.mBottomMils = paramInt4;
    }
    
    static Margins createFromParcel(Parcel paramParcel)
    {
      return new Margins(paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt());
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
      paramObject = (Margins)paramObject;
      if (this.mBottomMils != ((Margins)paramObject).mBottomMils) {
        return false;
      }
      if (this.mLeftMils != ((Margins)paramObject).mLeftMils) {
        return false;
      }
      if (this.mRightMils != ((Margins)paramObject).mRightMils) {
        return false;
      }
      return this.mTopMils == ((Margins)paramObject).mTopMils;
    }
    
    public int getBottomMils()
    {
      return this.mBottomMils;
    }
    
    public int getLeftMils()
    {
      return this.mLeftMils;
    }
    
    public int getRightMils()
    {
      return this.mRightMils;
    }
    
    public int getTopMils()
    {
      return this.mTopMils;
    }
    
    public int hashCode()
    {
      return (((this.mBottomMils + 31) * 31 + this.mLeftMils) * 31 + this.mRightMils) * 31 + this.mTopMils;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Margins{");
      localStringBuilder.append("leftMils: ").append(this.mLeftMils);
      localStringBuilder.append(", topMils: ").append(this.mTopMils);
      localStringBuilder.append(", rightMils: ").append(this.mRightMils);
      localStringBuilder.append(", bottomMils: ").append(this.mBottomMils);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeInt(this.mLeftMils);
      paramParcel.writeInt(this.mTopMils);
      paramParcel.writeInt(this.mRightMils);
      paramParcel.writeInt(this.mBottomMils);
    }
  }
  
  public static final class MediaSize
  {
    public static final MediaSize ISO_A0;
    public static final MediaSize ISO_A1;
    public static final MediaSize ISO_A10;
    public static final MediaSize ISO_A2;
    public static final MediaSize ISO_A3;
    public static final MediaSize ISO_A4;
    public static final MediaSize ISO_A5;
    public static final MediaSize ISO_A6;
    public static final MediaSize ISO_A7;
    public static final MediaSize ISO_A8;
    public static final MediaSize ISO_A9;
    public static final MediaSize ISO_B0;
    public static final MediaSize ISO_B1;
    public static final MediaSize ISO_B10;
    public static final MediaSize ISO_B2;
    public static final MediaSize ISO_B3;
    public static final MediaSize ISO_B4;
    public static final MediaSize ISO_B5;
    public static final MediaSize ISO_B6;
    public static final MediaSize ISO_B7;
    public static final MediaSize ISO_B8;
    public static final MediaSize ISO_B9;
    public static final MediaSize ISO_C0;
    public static final MediaSize ISO_C1;
    public static final MediaSize ISO_C10;
    public static final MediaSize ISO_C2;
    public static final MediaSize ISO_C3;
    public static final MediaSize ISO_C4;
    public static final MediaSize ISO_C5;
    public static final MediaSize ISO_C6;
    public static final MediaSize ISO_C7;
    public static final MediaSize ISO_C8;
    public static final MediaSize ISO_C9;
    public static final MediaSize JIS_B0;
    public static final MediaSize JIS_B1;
    public static final MediaSize JIS_B10;
    public static final MediaSize JIS_B2;
    public static final MediaSize JIS_B3;
    public static final MediaSize JIS_B4;
    public static final MediaSize JIS_B5;
    public static final MediaSize JIS_B6;
    public static final MediaSize JIS_B7;
    public static final MediaSize JIS_B8;
    public static final MediaSize JIS_B9;
    public static final MediaSize JIS_EXEC;
    public static final MediaSize JPN_CHOU2;
    public static final MediaSize JPN_CHOU3;
    public static final MediaSize JPN_CHOU4;
    public static final MediaSize JPN_HAGAKI;
    public static final MediaSize JPN_KAHU = new MediaSize("JPN_KAHU", "android", 17040796, 9449, 12681);
    public static final MediaSize JPN_KAKU2 = new MediaSize("JPN_KAKU2", "android", 17040797, 9449, 13071);
    public static final MediaSize JPN_OUFUKU;
    public static final MediaSize JPN_YOU4 = new MediaSize("JPN_YOU4", "android", 17040798, 4134, 9252);
    private static final String LOG_TAG = "MediaSize";
    public static final MediaSize NA_FOOLSCAP;
    public static final MediaSize NA_GOVT_LETTER;
    public static final MediaSize NA_INDEX_3X5;
    public static final MediaSize NA_INDEX_4X6;
    public static final MediaSize NA_INDEX_5X8;
    public static final MediaSize NA_JUNIOR_LEGAL;
    public static final MediaSize NA_LEDGER;
    public static final MediaSize NA_LEGAL;
    public static final MediaSize NA_LETTER;
    public static final MediaSize NA_MONARCH;
    public static final MediaSize NA_QUARTO;
    public static final MediaSize NA_TABLOID;
    public static final MediaSize OM_DAI_PA_KAI;
    public static final MediaSize OM_JUURO_KU_KAI;
    public static final MediaSize OM_PA_KAI;
    public static final MediaSize PRC_1;
    public static final MediaSize PRC_10;
    public static final MediaSize PRC_16K;
    public static final MediaSize PRC_2;
    public static final MediaSize PRC_3;
    public static final MediaSize PRC_4;
    public static final MediaSize PRC_5;
    public static final MediaSize PRC_6;
    public static final MediaSize PRC_7;
    public static final MediaSize PRC_8;
    public static final MediaSize PRC_9;
    public static final MediaSize ROC_16K;
    public static final MediaSize ROC_8K;
    public static final MediaSize UNKNOWN_LANDSCAPE;
    public static final MediaSize UNKNOWN_PORTRAIT;
    private static final Map<String, MediaSize> sIdToMediaSizeMap = new ArrayMap();
    private final int mHeightMils;
    private final String mId;
    public final String mLabel;
    public final int mLabelResId;
    public final String mPackageName;
    private final int mWidthMils;
    
    static
    {
      UNKNOWN_PORTRAIT = new MediaSize("UNKNOWN_PORTRAIT", "android", 17040799, 1, Integer.MAX_VALUE);
      UNKNOWN_LANDSCAPE = new MediaSize("UNKNOWN_LANDSCAPE", "android", 17040800, Integer.MAX_VALUE, 1);
      ISO_A0 = new MediaSize("ISO_A0", "android", 17040718, 33110, 46810);
      ISO_A1 = new MediaSize("ISO_A1", "android", 17040719, 23390, 33110);
      ISO_A2 = new MediaSize("ISO_A2", "android", 17040720, 16540, 23390);
      ISO_A3 = new MediaSize("ISO_A3", "android", 17040721, 11690, 16540);
      ISO_A4 = new MediaSize("ISO_A4", "android", 17040722, 8270, 11690);
      ISO_A5 = new MediaSize("ISO_A5", "android", 17040723, 5830, 8270);
      ISO_A6 = new MediaSize("ISO_A6", "android", 17040724, 4130, 5830);
      ISO_A7 = new MediaSize("ISO_A7", "android", 17040725, 2910, 4130);
      ISO_A8 = new MediaSize("ISO_A8", "android", 17040726, 2050, 2910);
      ISO_A9 = new MediaSize("ISO_A9", "android", 17040727, 1460, 2050);
      ISO_A10 = new MediaSize("ISO_A10", "android", 17040728, 1020, 1460);
      ISO_B0 = new MediaSize("ISO_B0", "android", 17040729, 39370, 55670);
      ISO_B1 = new MediaSize("ISO_B1", "android", 17040730, 27830, 39370);
      ISO_B2 = new MediaSize("ISO_B2", "android", 17040731, 19690, 27830);
      ISO_B3 = new MediaSize("ISO_B3", "android", 17040732, 13900, 19690);
      ISO_B4 = new MediaSize("ISO_B4", "android", 17040733, 9840, 13900);
      ISO_B5 = new MediaSize("ISO_B5", "android", 17040734, 6930, 9840);
      ISO_B6 = new MediaSize("ISO_B6", "android", 17040735, 4920, 6930);
      ISO_B7 = new MediaSize("ISO_B7", "android", 17040736, 3460, 4920);
      ISO_B8 = new MediaSize("ISO_B8", "android", 17040737, 2440, 3460);
      ISO_B9 = new MediaSize("ISO_B9", "android", 17040738, 1730, 2440);
      ISO_B10 = new MediaSize("ISO_B10", "android", 17040739, 1220, 1730);
      ISO_C0 = new MediaSize("ISO_C0", "android", 17040740, 36100, 51060);
      ISO_C1 = new MediaSize("ISO_C1", "android", 17040741, 25510, 36100);
      ISO_C2 = new MediaSize("ISO_C2", "android", 17040742, 18030, 25510);
      ISO_C3 = new MediaSize("ISO_C3", "android", 17040743, 12760, 18030);
      ISO_C4 = new MediaSize("ISO_C4", "android", 17040744, 9020, 12760);
      ISO_C5 = new MediaSize("ISO_C5", "android", 17040745, 6380, 9020);
      ISO_C6 = new MediaSize("ISO_C6", "android", 17040746, 4490, 6380);
      ISO_C7 = new MediaSize("ISO_C7", "android", 17040747, 3190, 4490);
      ISO_C8 = new MediaSize("ISO_C8", "android", 17040748, 2240, 3190);
      ISO_C9 = new MediaSize("ISO_C9", "android", 17040749, 1570, 2240);
      ISO_C10 = new MediaSize("ISO_C10", "android", 17040750, 1100, 1570);
      NA_LETTER = new MediaSize("NA_LETTER", "android", 17040751, 8500, 11000);
      NA_GOVT_LETTER = new MediaSize("NA_GOVT_LETTER", "android", 17040752, 8000, 10500);
      NA_LEGAL = new MediaSize("NA_LEGAL", "android", 17040753, 8500, 14000);
      NA_JUNIOR_LEGAL = new MediaSize("NA_JUNIOR_LEGAL", "android", 17040754, 8000, 5000);
      NA_LEDGER = new MediaSize("NA_LEDGER", "android", 17040755, 17000, 11000);
      NA_TABLOID = new MediaSize("NA_TABLOID", "android", 17040756, 11000, 17000);
      NA_INDEX_3X5 = new MediaSize("NA_INDEX_3X5", "android", 17040757, 3000, 5000);
      NA_INDEX_4X6 = new MediaSize("NA_INDEX_4X6", "android", 17040758, 4000, 6000);
      NA_INDEX_5X8 = new MediaSize("NA_INDEX_5X8", "android", 17040759, 5000, 8000);
      NA_MONARCH = new MediaSize("NA_MONARCH", "android", 17040760, 7250, 10500);
      NA_QUARTO = new MediaSize("NA_QUARTO", "android", 17040761, 8000, 10000);
      NA_FOOLSCAP = new MediaSize("NA_FOOLSCAP", "android", 17040762, 8000, 13000);
      ROC_8K = new MediaSize("ROC_8K", "android", 17040763, 10629, 15354);
      ROC_16K = new MediaSize("ROC_16K", "android", 17040764, 7677, 10629);
      PRC_1 = new MediaSize("PRC_1", "android", 17040765, 4015, 6496);
      PRC_2 = new MediaSize("PRC_2", "android", 17040766, 4015, 6929);
      PRC_3 = new MediaSize("PRC_3", "android", 17040767, 4921, 6929);
      PRC_4 = new MediaSize("PRC_4", "android", 17040768, 4330, 8189);
      PRC_5 = new MediaSize("PRC_5", "android", 17040769, 4330, 8661);
      PRC_6 = new MediaSize("PRC_6", "android", 17040770, 4724, 12599);
      PRC_7 = new MediaSize("PRC_7", "android", 17040771, 6299, 9055);
      PRC_8 = new MediaSize("PRC_8", "android", 17040772, 4724, 12165);
      PRC_9 = new MediaSize("PRC_9", "android", 17040773, 9016, 12756);
      PRC_10 = new MediaSize("PRC_10", "android", 17040774, 12756, 18032);
      PRC_16K = new MediaSize("PRC_16K", "android", 17040775, 5749, 8465);
      OM_PA_KAI = new MediaSize("OM_PA_KAI", "android", 17040776, 10512, 15315);
      OM_DAI_PA_KAI = new MediaSize("OM_DAI_PA_KAI", "android", 17040777, 10827, 15551);
      OM_JUURO_KU_KAI = new MediaSize("OM_JUURO_KU_KAI", "android", 17040778, 7796, 10827);
      JIS_B10 = new MediaSize("JIS_B10", "android", 17040779, 1259, 1772);
      JIS_B9 = new MediaSize("JIS_B9", "android", 17040780, 1772, 2520);
      JIS_B8 = new MediaSize("JIS_B8", "android", 17040781, 2520, 3583);
      JIS_B7 = new MediaSize("JIS_B7", "android", 17040782, 3583, 5049);
      JIS_B6 = new MediaSize("JIS_B6", "android", 17040783, 5049, 7165);
      JIS_B5 = new MediaSize("JIS_B5", "android", 17040784, 7165, 10118);
      JIS_B4 = new MediaSize("JIS_B4", "android", 17040785, 10118, 14331);
      JIS_B3 = new MediaSize("JIS_B3", "android", 17040786, 14331, 20276);
      JIS_B2 = new MediaSize("JIS_B2", "android", 17040787, 20276, 28661);
      JIS_B1 = new MediaSize("JIS_B1", "android", 17040788, 28661, 40551);
      JIS_B0 = new MediaSize("JIS_B0", "android", 17040789, 40551, 57323);
      JIS_EXEC = new MediaSize("JIS_EXEC", "android", 17040790, 8504, 12992);
      JPN_CHOU4 = new MediaSize("JPN_CHOU4", "android", 17040791, 3543, 8071);
      JPN_CHOU3 = new MediaSize("JPN_CHOU3", "android", 17040792, 4724, 9252);
      JPN_CHOU2 = new MediaSize("JPN_CHOU2", "android", 17040793, 4374, 5748);
      JPN_HAGAKI = new MediaSize("JPN_HAGAKI", "android", 17040794, 3937, 5827);
      JPN_OUFUKU = new MediaSize("JPN_OUFUKU", "android", 17040795, 5827, 7874);
    }
    
    public MediaSize(String paramString1, String paramString2, int paramInt1, int paramInt2)
    {
      this(paramString1, paramString2, null, paramInt1, paramInt2, 0);
    }
    
    public MediaSize(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
    {
      this(paramString1, null, paramString2, paramInt2, paramInt3, paramInt1);
      sIdToMediaSizeMap.put(this.mId, this);
    }
    
    public MediaSize(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3)
    {
      this.mPackageName = paramString3;
      this.mId = ((String)Preconditions.checkStringNotEmpty(paramString1, "id cannot be empty."));
      this.mLabelResId = paramInt3;
      this.mWidthMils = Preconditions.checkArgumentPositive(paramInt1, "widthMils cannot be less than or equal to zero.");
      this.mHeightMils = Preconditions.checkArgumentPositive(paramInt2, "heightMils cannot be less than or equal to zero.");
      this.mLabel = paramString2;
      if (TextUtils.isEmpty(paramString2))
      {
        paramInt1 = 0;
        if ((TextUtils.isEmpty(paramString3)) || (paramInt3 == 0)) {
          break label108;
        }
        paramInt2 = 1;
        label86:
        if (paramInt1 == paramInt2) {
          break label114;
        }
      }
      for (;;)
      {
        Preconditions.checkArgument(bool, "label cannot be empty.");
        return;
        paramInt1 = 1;
        break;
        label108:
        paramInt2 = 0;
        break label86;
        label114:
        bool = false;
      }
    }
    
    static MediaSize createFromParcel(Parcel paramParcel)
    {
      return new MediaSize(paramParcel.readString(), paramParcel.readString(), paramParcel.readString(), paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt());
    }
    
    public static ArraySet<MediaSize> getAllPredefinedSizes()
    {
      ArraySet localArraySet = new ArraySet(sIdToMediaSizeMap.values());
      localArraySet.remove(UNKNOWN_PORTRAIT);
      localArraySet.remove(UNKNOWN_LANDSCAPE);
      return localArraySet;
    }
    
    public static MediaSize getStandardMediaSizeById(String paramString)
    {
      return (MediaSize)sIdToMediaSizeMap.get(paramString);
    }
    
    public MediaSize asLandscape()
    {
      if (!isPortrait()) {
        return this;
      }
      return new MediaSize(this.mId, this.mLabel, this.mPackageName, Math.max(this.mWidthMils, this.mHeightMils), Math.min(this.mWidthMils, this.mHeightMils), this.mLabelResId);
    }
    
    public MediaSize asPortrait()
    {
      if (isPortrait()) {
        return this;
      }
      return new MediaSize(this.mId, this.mLabel, this.mPackageName, Math.min(this.mWidthMils, this.mHeightMils), Math.max(this.mWidthMils, this.mHeightMils), this.mLabelResId);
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
      paramObject = (MediaSize)paramObject;
      if (this.mWidthMils != ((MediaSize)paramObject).mWidthMils) {
        return false;
      }
      return this.mHeightMils == ((MediaSize)paramObject).mHeightMils;
    }
    
    public int getHeightMils()
    {
      return this.mHeightMils;
    }
    
    public String getId()
    {
      return this.mId;
    }
    
    public String getLabel(PackageManager paramPackageManager)
    {
      if ((!TextUtils.isEmpty(this.mPackageName)) && (this.mLabelResId > 0)) {
        try
        {
          paramPackageManager = paramPackageManager.getResourcesForApplication(this.mPackageName).getString(this.mLabelResId);
          return paramPackageManager;
        }
        catch (Resources.NotFoundException|PackageManager.NameNotFoundException paramPackageManager)
        {
          Log.w("MediaSize", "Could not load resouce" + this.mLabelResId + " from package " + this.mPackageName);
        }
      }
      return this.mLabel;
    }
    
    public int getWidthMils()
    {
      return this.mWidthMils;
    }
    
    public int hashCode()
    {
      return (this.mWidthMils + 31) * 31 + this.mHeightMils;
    }
    
    public boolean isPortrait()
    {
      return this.mHeightMils >= this.mWidthMils;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("MediaSize{");
      localStringBuilder.append("id: ").append(this.mId);
      localStringBuilder.append(", label: ").append(this.mLabel);
      localStringBuilder.append(", packageName: ").append(this.mPackageName);
      localStringBuilder.append(", heightMils: ").append(this.mHeightMils);
      localStringBuilder.append(", widthMils: ").append(this.mWidthMils);
      localStringBuilder.append(", labelResId: ").append(this.mLabelResId);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeString(this.mId);
      paramParcel.writeString(this.mLabel);
      paramParcel.writeString(this.mPackageName);
      paramParcel.writeInt(this.mWidthMils);
      paramParcel.writeInt(this.mHeightMils);
      paramParcel.writeInt(this.mLabelResId);
    }
  }
  
  public static final class Resolution
  {
    private final int mHorizontalDpi;
    private final String mId;
    private final String mLabel;
    private final int mVerticalDpi;
    
    public Resolution(String paramString1, String paramString2, int paramInt1, int paramInt2)
    {
      if (TextUtils.isEmpty(paramString1)) {
        throw new IllegalArgumentException("id cannot be empty.");
      }
      if (TextUtils.isEmpty(paramString2)) {
        throw new IllegalArgumentException("label cannot be empty.");
      }
      if (paramInt1 <= 0) {
        throw new IllegalArgumentException("horizontalDpi cannot be less than or equal to zero.");
      }
      if (paramInt2 <= 0) {
        throw new IllegalArgumentException("verticalDpi cannot be less than or equal to zero.");
      }
      this.mId = paramString1;
      this.mLabel = paramString2;
      this.mHorizontalDpi = paramInt1;
      this.mVerticalDpi = paramInt2;
    }
    
    static Resolution createFromParcel(Parcel paramParcel)
    {
      return new Resolution(paramParcel.readString(), paramParcel.readString(), paramParcel.readInt(), paramParcel.readInt());
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
      paramObject = (Resolution)paramObject;
      if (this.mHorizontalDpi != ((Resolution)paramObject).mHorizontalDpi) {
        return false;
      }
      return this.mVerticalDpi == ((Resolution)paramObject).mVerticalDpi;
    }
    
    public int getHorizontalDpi()
    {
      return this.mHorizontalDpi;
    }
    
    public String getId()
    {
      return this.mId;
    }
    
    public String getLabel()
    {
      return this.mLabel;
    }
    
    public int getVerticalDpi()
    {
      return this.mVerticalDpi;
    }
    
    public int hashCode()
    {
      return (this.mHorizontalDpi + 31) * 31 + this.mVerticalDpi;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Resolution{");
      localStringBuilder.append("id: ").append(this.mId);
      localStringBuilder.append(", label: ").append(this.mLabel);
      localStringBuilder.append(", horizontalDpi: ").append(this.mHorizontalDpi);
      localStringBuilder.append(", verticalDpi: ").append(this.mVerticalDpi);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeString(this.mId);
      paramParcel.writeString(this.mLabel);
      paramParcel.writeInt(this.mHorizontalDpi);
      paramParcel.writeInt(this.mVerticalDpi);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrintAttributes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */