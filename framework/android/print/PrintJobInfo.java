package android.print;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.Preconditions;
import java.util.Arrays;

public final class PrintJobInfo
  implements Parcelable
{
  public static final Parcelable.Creator<PrintJobInfo> CREATOR = new Parcelable.Creator()
  {
    public PrintJobInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PrintJobInfo(paramAnonymousParcel, null);
    }
    
    public PrintJobInfo[] newArray(int paramAnonymousInt)
    {
      return new PrintJobInfo[paramAnonymousInt];
    }
  };
  public static final int STATE_ANY = -1;
  public static final int STATE_ANY_ACTIVE = -3;
  public static final int STATE_ANY_SCHEDULED = -4;
  public static final int STATE_ANY_VISIBLE_TO_CLIENTS = -2;
  public static final int STATE_BLOCKED = 4;
  public static final int STATE_CANCELED = 7;
  public static final int STATE_COMPLETED = 5;
  public static final int STATE_CREATED = 1;
  public static final int STATE_FAILED = 6;
  public static final int STATE_QUEUED = 2;
  public static final int STATE_STARTED = 3;
  private Bundle mAdvancedOptions;
  private int mAppId;
  private PrintAttributes mAttributes;
  private boolean mCanceling;
  private int mCopies;
  private long mCreationTime;
  private PrintDocumentInfo mDocumentInfo;
  private PrintJobId mId;
  private String mLabel;
  private PageRange[] mPageRanges;
  private PrinterId mPrinterId;
  private String mPrinterName;
  private float mProgress;
  private int mState;
  private CharSequence mStatus;
  private int mStatusRes;
  private CharSequence mStatusResAppPackageName;
  private String mTag;
  
  public PrintJobInfo()
  {
    this.mProgress = -1.0F;
  }
  
  private PrintJobInfo(Parcel paramParcel)
  {
    this.mId = ((PrintJobId)paramParcel.readParcelable(null));
    this.mLabel = paramParcel.readString();
    this.mPrinterId = ((PrinterId)paramParcel.readParcelable(null));
    this.mPrinterName = paramParcel.readString();
    this.mState = paramParcel.readInt();
    this.mAppId = paramParcel.readInt();
    this.mTag = paramParcel.readString();
    this.mCreationTime = paramParcel.readLong();
    this.mCopies = paramParcel.readInt();
    Parcelable[] arrayOfParcelable = paramParcel.readParcelableArray(null);
    if (arrayOfParcelable != null)
    {
      this.mPageRanges = new PageRange[arrayOfParcelable.length];
      int i = 0;
      while (i < arrayOfParcelable.length)
      {
        this.mPageRanges[i] = ((PageRange)arrayOfParcelable[i]);
        i += 1;
      }
    }
    this.mAttributes = ((PrintAttributes)paramParcel.readParcelable(null));
    this.mDocumentInfo = ((PrintDocumentInfo)paramParcel.readParcelable(null));
    this.mProgress = paramParcel.readFloat();
    this.mStatus = paramParcel.readCharSequence();
    this.mStatusRes = paramParcel.readInt();
    this.mStatusResAppPackageName = paramParcel.readCharSequence();
    if (paramParcel.readInt() == 1)
    {
      bool1 = true;
      this.mCanceling = bool1;
      this.mAdvancedOptions = paramParcel.readBundle();
      if (this.mAdvancedOptions != null) {
        if (!this.mAdvancedOptions.containsKey(null)) {
          break label248;
        }
      }
    }
    label248:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      Preconditions.checkArgument(bool1);
      return;
      bool1 = false;
      break;
    }
  }
  
  public PrintJobInfo(PrintJobInfo paramPrintJobInfo)
  {
    this.mId = paramPrintJobInfo.mId;
    this.mLabel = paramPrintJobInfo.mLabel;
    this.mPrinterId = paramPrintJobInfo.mPrinterId;
    this.mPrinterName = paramPrintJobInfo.mPrinterName;
    this.mState = paramPrintJobInfo.mState;
    this.mAppId = paramPrintJobInfo.mAppId;
    this.mTag = paramPrintJobInfo.mTag;
    this.mCreationTime = paramPrintJobInfo.mCreationTime;
    this.mCopies = paramPrintJobInfo.mCopies;
    this.mPageRanges = paramPrintJobInfo.mPageRanges;
    this.mAttributes = paramPrintJobInfo.mAttributes;
    this.mDocumentInfo = paramPrintJobInfo.mDocumentInfo;
    this.mProgress = paramPrintJobInfo.mProgress;
    this.mStatus = paramPrintJobInfo.mStatus;
    this.mStatusRes = paramPrintJobInfo.mStatusRes;
    this.mStatusResAppPackageName = paramPrintJobInfo.mStatusResAppPackageName;
    this.mCanceling = paramPrintJobInfo.mCanceling;
    this.mAdvancedOptions = paramPrintJobInfo.mAdvancedOptions;
  }
  
  public static String stateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "STATE_UNKNOWN";
    case 1: 
      return "STATE_CREATED";
    case 2: 
      return "STATE_QUEUED";
    case 3: 
      return "STATE_STARTED";
    case 4: 
      return "STATE_BLOCKED";
    case 6: 
      return "STATE_FAILED";
    case 5: 
      return "STATE_COMPLETED";
    }
    return "STATE_CANCELED";
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getAdvancedIntOption(String paramString)
  {
    if (this.mAdvancedOptions != null) {
      return this.mAdvancedOptions.getInt(paramString);
    }
    return 0;
  }
  
  public Bundle getAdvancedOptions()
  {
    return this.mAdvancedOptions;
  }
  
  public String getAdvancedStringOption(String paramString)
  {
    if (this.mAdvancedOptions != null) {
      return this.mAdvancedOptions.getString(paramString);
    }
    return null;
  }
  
  public int getAppId()
  {
    return this.mAppId;
  }
  
  public PrintAttributes getAttributes()
  {
    return this.mAttributes;
  }
  
  public int getCopies()
  {
    return this.mCopies;
  }
  
  public long getCreationTime()
  {
    return this.mCreationTime;
  }
  
  public PrintDocumentInfo getDocumentInfo()
  {
    return this.mDocumentInfo;
  }
  
  public PrintJobId getId()
  {
    return this.mId;
  }
  
  public String getLabel()
  {
    return this.mLabel;
  }
  
  public PageRange[] getPages()
  {
    return this.mPageRanges;
  }
  
  public PrinterId getPrinterId()
  {
    return this.mPrinterId;
  }
  
  public String getPrinterName()
  {
    return this.mPrinterName;
  }
  
  public float getProgress()
  {
    return this.mProgress;
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  public CharSequence getStatus(PackageManager paramPackageManager)
  {
    if (this.mStatusRes == 0) {
      return this.mStatus;
    }
    try
    {
      paramPackageManager = paramPackageManager.getResourcesForApplication(this.mStatusResAppPackageName.toString()).getString(this.mStatusRes);
      return paramPackageManager;
    }
    catch (PackageManager.NameNotFoundException|Resources.NotFoundException paramPackageManager) {}
    return null;
  }
  
  public String getTag()
  {
    return this.mTag;
  }
  
  public boolean hasAdvancedOption(String paramString)
  {
    if (this.mAdvancedOptions != null) {
      return this.mAdvancedOptions.containsKey(paramString);
    }
    return false;
  }
  
  public boolean isCancelling()
  {
    return this.mCanceling;
  }
  
  public void setAdvancedOptions(Bundle paramBundle)
  {
    this.mAdvancedOptions = paramBundle;
  }
  
  public void setAppId(int paramInt)
  {
    this.mAppId = paramInt;
  }
  
  public void setAttributes(PrintAttributes paramPrintAttributes)
  {
    this.mAttributes = paramPrintAttributes;
  }
  
  public void setCancelling(boolean paramBoolean)
  {
    this.mCanceling = paramBoolean;
  }
  
  public void setCopies(int paramInt)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("Copies must be more than one.");
    }
    this.mCopies = paramInt;
  }
  
  public void setCreationTime(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("creationTime must be non-negative.");
    }
    this.mCreationTime = paramLong;
  }
  
  public void setDocumentInfo(PrintDocumentInfo paramPrintDocumentInfo)
  {
    this.mDocumentInfo = paramPrintDocumentInfo;
  }
  
  public void setId(PrintJobId paramPrintJobId)
  {
    this.mId = paramPrintJobId;
  }
  
  public void setLabel(String paramString)
  {
    this.mLabel = paramString;
  }
  
  public void setPages(PageRange[] paramArrayOfPageRange)
  {
    this.mPageRanges = paramArrayOfPageRange;
  }
  
  public void setPrinterId(PrinterId paramPrinterId)
  {
    this.mPrinterId = paramPrinterId;
  }
  
  public void setPrinterName(String paramString)
  {
    this.mPrinterName = paramString;
  }
  
  public void setProgress(float paramFloat)
  {
    Preconditions.checkArgumentInRange(paramFloat, 0.0F, 1.0F, "progress");
    this.mProgress = paramFloat;
  }
  
  public void setState(int paramInt)
  {
    this.mState = paramInt;
  }
  
  public void setStatus(int paramInt, CharSequence paramCharSequence)
  {
    this.mStatus = null;
    this.mStatusRes = paramInt;
    this.mStatusResAppPackageName = paramCharSequence;
  }
  
  public void setStatus(CharSequence paramCharSequence)
  {
    this.mStatusRes = 0;
    this.mStatusResAppPackageName = null;
    this.mStatus = paramCharSequence;
  }
  
  public void setTag(String paramString)
  {
    this.mTag = paramString;
  }
  
  public String toString()
  {
    Object localObject2 = null;
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("PrintJobInfo{");
    localStringBuilder1.append("label: ").append(this.mLabel);
    localStringBuilder1.append(", id: ").append(this.mId);
    localStringBuilder1.append(", state: ").append(stateToString(this.mState));
    localStringBuilder1.append(", printer: ").append(this.mPrinterId);
    localStringBuilder1.append(", tag: ").append(this.mTag);
    localStringBuilder1.append(", creationTime: ").append(this.mCreationTime);
    localStringBuilder1.append(", copies: ").append(this.mCopies);
    StringBuilder localStringBuilder2 = localStringBuilder1.append(", attributes: ");
    label192:
    label240:
    boolean bool;
    if (this.mAttributes != null)
    {
      localObject1 = this.mAttributes.toString();
      localStringBuilder2.append((String)localObject1);
      localStringBuilder2 = localStringBuilder1.append(", documentInfo: ");
      if (this.mDocumentInfo == null) {
        break label393;
      }
      localObject1 = this.mDocumentInfo.toString();
      localStringBuilder2.append((String)localObject1);
      localStringBuilder1.append(", cancelling: ").append(this.mCanceling);
      localStringBuilder2 = localStringBuilder1.append(", pages: ");
      if (this.mPageRanges == null) {
        break label398;
      }
      localObject1 = Arrays.toString(this.mPageRanges);
      localStringBuilder2.append((String)localObject1);
      localObject1 = localStringBuilder1.append(", hasAdvancedOptions: ");
      if (this.mAdvancedOptions == null) {
        break label403;
      }
      bool = true;
      label265:
      ((StringBuilder)localObject1).append(bool);
      localStringBuilder1.append(", progress: ").append(this.mProgress);
      localStringBuilder2 = localStringBuilder1.append(", status: ");
      if (this.mStatus == null) {
        break label408;
      }
    }
    label393:
    label398:
    label403:
    label408:
    for (Object localObject1 = this.mStatus.toString();; localObject1 = null)
    {
      localStringBuilder2.append((String)localObject1);
      localStringBuilder1.append(", statusRes: ").append(this.mStatusRes);
      localStringBuilder2 = localStringBuilder1.append(", statusResAppPackageName: ");
      localObject1 = localObject2;
      if (this.mStatusResAppPackageName != null) {
        localObject1 = this.mStatusResAppPackageName.toString();
      }
      localStringBuilder2.append((String)localObject1);
      localStringBuilder1.append("}");
      return localStringBuilder1.toString();
      localObject1 = null;
      break;
      localObject1 = null;
      break label192;
      localObject1 = null;
      break label240;
      bool = false;
      break label265;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 0;
    paramParcel.writeParcelable(this.mId, paramInt);
    paramParcel.writeString(this.mLabel);
    paramParcel.writeParcelable(this.mPrinterId, paramInt);
    paramParcel.writeString(this.mPrinterName);
    paramParcel.writeInt(this.mState);
    paramParcel.writeInt(this.mAppId);
    paramParcel.writeString(this.mTag);
    paramParcel.writeLong(this.mCreationTime);
    paramParcel.writeInt(this.mCopies);
    paramParcel.writeParcelableArray(this.mPageRanges, paramInt);
    paramParcel.writeParcelable(this.mAttributes, paramInt);
    paramParcel.writeParcelable(this.mDocumentInfo, 0);
    paramParcel.writeFloat(this.mProgress);
    paramParcel.writeCharSequence(this.mStatus);
    paramParcel.writeInt(this.mStatusRes);
    paramParcel.writeCharSequence(this.mStatusResAppPackageName);
    paramInt = i;
    if (this.mCanceling) {
      paramInt = 1;
    }
    paramParcel.writeInt(paramInt);
    paramParcel.writeBundle(this.mAdvancedOptions);
  }
  
  public static final class Builder
  {
    private final PrintJobInfo mPrototype;
    
    public Builder(PrintJobInfo paramPrintJobInfo)
    {
      if (paramPrintJobInfo != null) {}
      for (paramPrintJobInfo = new PrintJobInfo(paramPrintJobInfo);; paramPrintJobInfo = new PrintJobInfo())
      {
        this.mPrototype = paramPrintJobInfo;
        return;
      }
    }
    
    public PrintJobInfo build()
    {
      return this.mPrototype;
    }
    
    public void putAdvancedOption(String paramString, int paramInt)
    {
      if (PrintJobInfo.-get0(this.mPrototype) == null) {
        PrintJobInfo.-set0(this.mPrototype, new Bundle());
      }
      PrintJobInfo.-get0(this.mPrototype).putInt(paramString, paramInt);
    }
    
    public void putAdvancedOption(String paramString1, String paramString2)
    {
      Preconditions.checkNotNull(paramString1, "key cannot be null");
      if (PrintJobInfo.-get0(this.mPrototype) == null) {
        PrintJobInfo.-set0(this.mPrototype, new Bundle());
      }
      PrintJobInfo.-get0(this.mPrototype).putString(paramString1, paramString2);
    }
    
    public void setAttributes(PrintAttributes paramPrintAttributes)
    {
      PrintJobInfo.-set1(this.mPrototype, paramPrintAttributes);
    }
    
    public void setCopies(int paramInt)
    {
      PrintJobInfo.-set2(this.mPrototype, paramInt);
    }
    
    public void setPages(PageRange[] paramArrayOfPageRange)
    {
      PrintJobInfo.-set3(this.mPrototype, paramArrayOfPageRange);
    }
    
    public void setProgress(float paramFloat)
    {
      Preconditions.checkArgumentInRange(paramFloat, 0.0F, 1.0F, "progress");
      PrintJobInfo.-set4(this.mPrototype, paramFloat);
    }
    
    public void setStatus(CharSequence paramCharSequence)
    {
      PrintJobInfo.-set5(this.mPrototype, paramCharSequence);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrintJobInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */