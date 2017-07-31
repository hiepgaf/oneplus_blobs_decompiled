package android.print;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;

public final class PrinterInfo
  implements Parcelable
{
  public static final Parcelable.Creator<PrinterInfo> CREATOR = new Parcelable.Creator()
  {
    public PrinterInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PrinterInfo(paramAnonymousParcel, null);
    }
    
    public PrinterInfo[] newArray(int paramAnonymousInt)
    {
      return new PrinterInfo[paramAnonymousInt];
    }
  };
  public static final int STATUS_BUSY = 2;
  public static final int STATUS_IDLE = 1;
  public static final int STATUS_UNAVAILABLE = 3;
  private final PrinterCapabilitiesInfo mCapabilities;
  private final int mCustomPrinterIconGen;
  private final String mDescription;
  private final boolean mHasCustomPrinterIcon;
  private final int mIconResourceId;
  private final PrinterId mId;
  private final PendingIntent mInfoIntent;
  private final String mName;
  private final int mStatus;
  
  private PrinterInfo(Parcel paramParcel)
  {
    this.mId = checkPrinterId((PrinterId)paramParcel.readParcelable(null));
    this.mName = checkName(paramParcel.readString());
    this.mStatus = checkStatus(paramParcel.readInt());
    this.mDescription = paramParcel.readString();
    this.mCapabilities = ((PrinterCapabilitiesInfo)paramParcel.readParcelable(null));
    this.mIconResourceId = paramParcel.readInt();
    if (paramParcel.readByte() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      this.mHasCustomPrinterIcon = bool;
      this.mCustomPrinterIconGen = paramParcel.readInt();
      this.mInfoIntent = ((PendingIntent)paramParcel.readParcelable(null));
      return;
    }
  }
  
  private PrinterInfo(PrinterId paramPrinterId, String paramString1, int paramInt1, int paramInt2, boolean paramBoolean, String paramString2, PendingIntent paramPendingIntent, PrinterCapabilitiesInfo paramPrinterCapabilitiesInfo, int paramInt3)
  {
    this.mId = paramPrinterId;
    this.mName = paramString1;
    this.mStatus = paramInt1;
    this.mIconResourceId = paramInt2;
    this.mHasCustomPrinterIcon = paramBoolean;
    this.mDescription = paramString2;
    this.mInfoIntent = paramPendingIntent;
    this.mCapabilities = paramPrinterCapabilitiesInfo;
    this.mCustomPrinterIconGen = paramInt3;
  }
  
  private static String checkName(String paramString)
  {
    return (String)Preconditions.checkStringNotEmpty(paramString, "name cannot be empty.");
  }
  
  private static PrinterId checkPrinterId(PrinterId paramPrinterId)
  {
    return (PrinterId)Preconditions.checkNotNull(paramPrinterId, "printerId cannot be null.");
  }
  
  private static int checkStatus(int paramInt)
  {
    if ((paramInt != 1) && (paramInt != 2) && (paramInt != 3)) {
      throw new IllegalArgumentException("status is invalid.");
    }
    return paramInt;
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
    paramObject = (PrinterInfo)paramObject;
    if (!equalsIgnoringStatus((PrinterInfo)paramObject)) {
      return false;
    }
    return this.mStatus == ((PrinterInfo)paramObject).mStatus;
  }
  
  public boolean equalsIgnoringStatus(PrinterInfo paramPrinterInfo)
  {
    if (!this.mId.equals(paramPrinterInfo.mId)) {
      return false;
    }
    if (!this.mName.equals(paramPrinterInfo.mName)) {
      return false;
    }
    if (!TextUtils.equals(this.mDescription, paramPrinterInfo.mDescription)) {
      return false;
    }
    if (this.mCapabilities == null)
    {
      if (paramPrinterInfo.mCapabilities != null) {
        return false;
      }
    }
    else if (!this.mCapabilities.equals(paramPrinterInfo.mCapabilities)) {
      return false;
    }
    if (this.mIconResourceId != paramPrinterInfo.mIconResourceId) {
      return false;
    }
    if (this.mHasCustomPrinterIcon != paramPrinterInfo.mHasCustomPrinterIcon) {
      return false;
    }
    if (this.mCustomPrinterIconGen != paramPrinterInfo.mCustomPrinterIconGen) {
      return false;
    }
    if (this.mInfoIntent == null)
    {
      if (paramPrinterInfo.mInfoIntent != null) {
        return false;
      }
    }
    else if (!this.mInfoIntent.equals(paramPrinterInfo.mInfoIntent)) {
      return false;
    }
    return true;
  }
  
  public PrinterCapabilitiesInfo getCapabilities()
  {
    return this.mCapabilities;
  }
  
  public String getDescription()
  {
    return this.mDescription;
  }
  
  public PrinterId getId()
  {
    return this.mId;
  }
  
  public PendingIntent getInfoIntent()
  {
    return this.mInfoIntent;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int getStatus()
  {
    return this.mStatus;
  }
  
  public int hashCode()
  {
    int m = 0;
    int n = this.mId.hashCode();
    int i1 = this.mName.hashCode();
    int i2 = this.mStatus;
    int i;
    int j;
    label57:
    int i3;
    if (this.mDescription != null)
    {
      i = this.mDescription.hashCode();
      if (this.mCapabilities == null) {
        break label150;
      }
      j = this.mCapabilities.hashCode();
      i3 = this.mIconResourceId;
      if (!this.mHasCustomPrinterIcon) {
        break label155;
      }
    }
    label150:
    label155:
    for (int k = 1;; k = 0)
    {
      int i4 = this.mCustomPrinterIconGen;
      if (this.mInfoIntent != null) {
        m = this.mInfoIntent.hashCode();
      }
      return ((((((((n + 31) * 31 + i1) * 31 + i2) * 31 + i) * 31 + j) * 31 + i3) * 31 + k) * 31 + i4) * 31 + m;
      i = 0;
      break;
      j = 0;
      break label57;
    }
  }
  
  public Drawable loadIcon(Context paramContext)
  {
    Object localObject2 = null;
    PackageManager localPackageManager = paramContext.getPackageManager();
    Object localObject1 = localObject2;
    Object localObject3;
    if (this.mHasCustomPrinterIcon)
    {
      localObject3 = ((PrintManager)paramContext.getSystemService("print")).getCustomPrinterIcon(this.mId);
      localObject1 = localObject2;
      if (localObject3 != null) {
        localObject1 = ((Icon)localObject3).loadDrawable(paramContext);
      }
    }
    localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = localObject1;
    }
    try
    {
      String str = this.mId.getServiceName().getPackageName();
      localObject2 = localObject1;
      localObject3 = localPackageManager.getPackageInfo(str, 0).applicationInfo;
      paramContext = (Context)localObject1;
      localObject2 = localObject1;
      if (this.mIconResourceId != 0)
      {
        localObject2 = localObject1;
        paramContext = localPackageManager.getDrawable(str, this.mIconResourceId, (ApplicationInfo)localObject3);
      }
      localObject2 = paramContext;
      if (paramContext == null)
      {
        localObject2 = paramContext;
        paramContext = ((PackageItemInfo)localObject3).loadIcon(localPackageManager);
        localObject2 = paramContext;
      }
      return (Drawable)localObject2;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return (Drawable)localObject2;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PrinterInfo{");
    localStringBuilder.append("id=").append(this.mId);
    localStringBuilder.append(", name=").append(this.mName);
    localStringBuilder.append(", status=").append(this.mStatus);
    localStringBuilder.append(", description=").append(this.mDescription);
    localStringBuilder.append(", capabilities=").append(this.mCapabilities);
    localStringBuilder.append(", iconResId=").append(this.mIconResourceId);
    localStringBuilder.append(", hasCustomPrinterIcon=").append(this.mHasCustomPrinterIcon);
    localStringBuilder.append(", customPrinterIconGen=").append(this.mCustomPrinterIconGen);
    localStringBuilder.append(", infoIntent=").append(this.mInfoIntent);
    localStringBuilder.append("\"}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mId, paramInt);
    paramParcel.writeString(this.mName);
    paramParcel.writeInt(this.mStatus);
    paramParcel.writeString(this.mDescription);
    paramParcel.writeParcelable(this.mCapabilities, paramInt);
    paramParcel.writeInt(this.mIconResourceId);
    if (this.mHasCustomPrinterIcon) {}
    for (int i = 1;; i = 0)
    {
      paramParcel.writeByte((byte)i);
      paramParcel.writeInt(this.mCustomPrinterIconGen);
      paramParcel.writeParcelable(this.mInfoIntent, paramInt);
      return;
    }
  }
  
  public static final class Builder
  {
    private PrinterCapabilitiesInfo mCapabilities;
    private int mCustomPrinterIconGen;
    private String mDescription;
    private boolean mHasCustomPrinterIcon;
    private int mIconResourceId;
    private PendingIntent mInfoIntent;
    private String mName;
    private PrinterId mPrinterId;
    private int mStatus;
    
    public Builder(PrinterId paramPrinterId, String paramString, int paramInt)
    {
      this.mPrinterId = PrinterInfo.-wrap0(paramPrinterId);
      this.mName = PrinterInfo.-wrap2(paramString);
      this.mStatus = PrinterInfo.-wrap1(paramInt);
    }
    
    public Builder(PrinterInfo paramPrinterInfo)
    {
      this.mPrinterId = PrinterInfo.-get5(paramPrinterInfo);
      this.mName = PrinterInfo.-get7(paramPrinterInfo);
      this.mStatus = PrinterInfo.-get8(paramPrinterInfo);
      this.mIconResourceId = PrinterInfo.-get4(paramPrinterInfo);
      this.mHasCustomPrinterIcon = PrinterInfo.-get3(paramPrinterInfo);
      this.mDescription = PrinterInfo.-get2(paramPrinterInfo);
      this.mInfoIntent = PrinterInfo.-get6(paramPrinterInfo);
      this.mCapabilities = PrinterInfo.-get0(paramPrinterInfo);
      this.mCustomPrinterIconGen = PrinterInfo.-get1(paramPrinterInfo);
    }
    
    public PrinterInfo build()
    {
      return new PrinterInfo(this.mPrinterId, this.mName, this.mStatus, this.mIconResourceId, this.mHasCustomPrinterIcon, this.mDescription, this.mInfoIntent, this.mCapabilities, this.mCustomPrinterIconGen, null);
    }
    
    public Builder incCustomPrinterIconGen()
    {
      this.mCustomPrinterIconGen += 1;
      return this;
    }
    
    public Builder setCapabilities(PrinterCapabilitiesInfo paramPrinterCapabilitiesInfo)
    {
      this.mCapabilities = paramPrinterCapabilitiesInfo;
      return this;
    }
    
    public Builder setDescription(String paramString)
    {
      this.mDescription = paramString;
      return this;
    }
    
    public Builder setHasCustomPrinterIcon(boolean paramBoolean)
    {
      this.mHasCustomPrinterIcon = paramBoolean;
      return this;
    }
    
    public Builder setIconResourceId(int paramInt)
    {
      this.mIconResourceId = Preconditions.checkArgumentNonnegative(paramInt, "iconResourceId can't be negative");
      return this;
    }
    
    public Builder setInfoIntent(PendingIntent paramPendingIntent)
    {
      this.mInfoIntent = paramPendingIntent;
      return this;
    }
    
    public Builder setName(String paramString)
    {
      this.mName = PrinterInfo.-wrap2(paramString);
      return this;
    }
    
    public Builder setStatus(int paramInt)
    {
      this.mStatus = PrinterInfo.-wrap1(paramInt);
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrinterInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */