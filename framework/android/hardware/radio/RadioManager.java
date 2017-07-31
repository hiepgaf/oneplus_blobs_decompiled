package android.hardware.radio;

import android.content.Context;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public class RadioManager
{
  public static final int BAND_AM = 0;
  public static final int BAND_AM_HD = 3;
  public static final int BAND_FM = 1;
  public static final int BAND_FM_HD = 2;
  public static final int CLASS_AM_FM = 0;
  public static final int CLASS_DT = 2;
  public static final int CLASS_SAT = 1;
  public static final int REGION_ITU_1 = 0;
  public static final int REGION_ITU_2 = 1;
  public static final int REGION_JAPAN = 3;
  public static final int REGION_KOREA = 4;
  public static final int REGION_OIRT = 2;
  public static final int STATUS_BAD_VALUE = -22;
  public static final int STATUS_DEAD_OBJECT = -32;
  public static final int STATUS_ERROR = Integer.MIN_VALUE;
  public static final int STATUS_INVALID_OPERATION = -38;
  public static final int STATUS_NO_INIT = -19;
  public static final int STATUS_OK = 0;
  public static final int STATUS_PERMISSION_DENIED = -1;
  public static final int STATUS_TIMED_OUT = -110;
  private final Context mContext;
  
  public RadioManager(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public native int listModules(List<ModuleProperties> paramList);
  
  public RadioTuner openTuner(int paramInt, BandConfig paramBandConfig, boolean paramBoolean, RadioTuner.Callback paramCallback, Handler paramHandler)
  {
    if (paramCallback == null) {
      return null;
    }
    paramCallback = new RadioModule(paramInt, paramBandConfig, paramBoolean, paramCallback, paramHandler);
    paramBandConfig = paramCallback;
    if (paramCallback != null)
    {
      paramBandConfig = paramCallback;
      if (!paramCallback.initCheck()) {
        paramBandConfig = null;
      }
    }
    return paramBandConfig;
  }
  
  public static class AmBandConfig
    extends RadioManager.BandConfig
  {
    public static final Parcelable.Creator<AmBandConfig> CREATOR = new Parcelable.Creator()
    {
      public RadioManager.AmBandConfig createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RadioManager.AmBandConfig(paramAnonymousParcel, null);
      }
      
      public RadioManager.AmBandConfig[] newArray(int paramAnonymousInt)
      {
        return new RadioManager.AmBandConfig[paramAnonymousInt];
      }
    };
    private final boolean mStereo;
    
    AmBandConfig(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
    {
      super(paramInt2, paramInt3, paramInt4, paramInt5);
      this.mStereo = paramBoolean;
    }
    
    AmBandConfig(RadioManager.AmBandDescriptor paramAmBandDescriptor)
    {
      super();
      this.mStereo = paramAmBandDescriptor.isStereoSupported();
    }
    
    private AmBandConfig(Parcel paramParcel)
    {
      super(null);
      if (paramParcel.readByte() == 1) {}
      for (;;)
      {
        this.mStereo = bool;
        return;
        bool = false;
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
      if (!super.equals(paramObject)) {
        return false;
      }
      if (!(paramObject instanceof AmBandConfig)) {
        return false;
      }
      paramObject = (AmBandConfig)paramObject;
      return this.mStereo == ((AmBandConfig)paramObject).getStereo();
    }
    
    public boolean getStereo()
    {
      return this.mStereo;
    }
    
    public int hashCode()
    {
      int j = super.hashCode();
      if (this.mStereo) {}
      for (int i = 1;; i = 0) {
        return j * 31 + i;
      }
    }
    
    public String toString()
    {
      return "AmBandConfig [" + super.toString() + ", mStereo=" + this.mStereo + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      if (this.mStereo) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeByte((byte)paramInt);
        return;
      }
    }
    
    public static class Builder
    {
      private final RadioManager.BandDescriptor mDescriptor;
      private boolean mStereo;
      
      public Builder(RadioManager.AmBandConfig paramAmBandConfig)
      {
        this.mDescriptor = new RadioManager.BandDescriptor(paramAmBandConfig.getRegion(), paramAmBandConfig.getType(), paramAmBandConfig.getLowerLimit(), paramAmBandConfig.getUpperLimit(), paramAmBandConfig.getSpacing());
        this.mStereo = paramAmBandConfig.getStereo();
      }
      
      public Builder(RadioManager.AmBandDescriptor paramAmBandDescriptor)
      {
        this.mDescriptor = new RadioManager.BandDescriptor(paramAmBandDescriptor.getRegion(), paramAmBandDescriptor.getType(), paramAmBandDescriptor.getLowerLimit(), paramAmBandDescriptor.getUpperLimit(), paramAmBandDescriptor.getSpacing());
        this.mStereo = paramAmBandDescriptor.isStereoSupported();
      }
      
      public RadioManager.AmBandConfig build()
      {
        return new RadioManager.AmBandConfig(this.mDescriptor.getRegion(), this.mDescriptor.getType(), this.mDescriptor.getLowerLimit(), this.mDescriptor.getUpperLimit(), this.mDescriptor.getSpacing(), this.mStereo);
      }
      
      public Builder setStereo(boolean paramBoolean)
      {
        this.mStereo = paramBoolean;
        return this;
      }
    }
  }
  
  public static class AmBandDescriptor
    extends RadioManager.BandDescriptor
  {
    public static final Parcelable.Creator<AmBandDescriptor> CREATOR = new Parcelable.Creator()
    {
      public RadioManager.AmBandDescriptor createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RadioManager.AmBandDescriptor(paramAnonymousParcel, null);
      }
      
      public RadioManager.AmBandDescriptor[] newArray(int paramAnonymousInt)
      {
        return new RadioManager.AmBandDescriptor[paramAnonymousInt];
      }
    };
    private final boolean mStereo;
    
    AmBandDescriptor(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
    {
      super(paramInt2, paramInt3, paramInt4, paramInt5);
      this.mStereo = paramBoolean;
    }
    
    private AmBandDescriptor(Parcel paramParcel)
    {
      super(null);
      if (paramParcel.readByte() == 1) {}
      for (;;)
      {
        this.mStereo = bool;
        return;
        bool = false;
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
      if (!super.equals(paramObject)) {
        return false;
      }
      if (!(paramObject instanceof AmBandDescriptor)) {
        return false;
      }
      paramObject = (AmBandDescriptor)paramObject;
      return this.mStereo == ((AmBandDescriptor)paramObject).isStereoSupported();
    }
    
    public int hashCode()
    {
      int j = super.hashCode();
      if (this.mStereo) {}
      for (int i = 1;; i = 0) {
        return j * 31 + i;
      }
    }
    
    public boolean isStereoSupported()
    {
      return this.mStereo;
    }
    
    public String toString()
    {
      return "AmBandDescriptor [ " + super.toString() + " mStereo=" + this.mStereo + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      if (this.mStereo) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeByte((byte)paramInt);
        return;
      }
    }
  }
  
  public static class BandConfig
    implements Parcelable
  {
    public static final Parcelable.Creator<BandConfig> CREATOR = new Parcelable.Creator()
    {
      public RadioManager.BandConfig createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RadioManager.BandConfig(paramAnonymousParcel, null);
      }
      
      public RadioManager.BandConfig[] newArray(int paramAnonymousInt)
      {
        return new RadioManager.BandConfig[paramAnonymousInt];
      }
    };
    final RadioManager.BandDescriptor mDescriptor;
    
    BandConfig(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      this.mDescriptor = new RadioManager.BandDescriptor(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    BandConfig(RadioManager.BandDescriptor paramBandDescriptor)
    {
      this.mDescriptor = paramBandDescriptor;
    }
    
    private BandConfig(Parcel paramParcel)
    {
      this.mDescriptor = new RadioManager.BandDescriptor(paramParcel, null);
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
      if (!(paramObject instanceof BandConfig)) {
        return false;
      }
      paramObject = (BandConfig)paramObject;
      return this.mDescriptor == ((BandConfig)paramObject).getDescriptor();
    }
    
    RadioManager.BandDescriptor getDescriptor()
    {
      return this.mDescriptor;
    }
    
    public int getLowerLimit()
    {
      return this.mDescriptor.getLowerLimit();
    }
    
    public int getRegion()
    {
      return this.mDescriptor.getRegion();
    }
    
    public int getSpacing()
    {
      return this.mDescriptor.getSpacing();
    }
    
    public int getType()
    {
      return this.mDescriptor.getType();
    }
    
    public int getUpperLimit()
    {
      return this.mDescriptor.getUpperLimit();
    }
    
    public int hashCode()
    {
      return this.mDescriptor.hashCode() + 31;
    }
    
    public String toString()
    {
      return "BandConfig [ " + this.mDescriptor.toString() + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      this.mDescriptor.writeToParcel(paramParcel, paramInt);
    }
  }
  
  public static class BandDescriptor
    implements Parcelable
  {
    public static final Parcelable.Creator<BandDescriptor> CREATOR = new Parcelable.Creator()
    {
      public RadioManager.BandDescriptor createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RadioManager.BandDescriptor(paramAnonymousParcel, null);
      }
      
      public RadioManager.BandDescriptor[] newArray(int paramAnonymousInt)
      {
        return new RadioManager.BandDescriptor[paramAnonymousInt];
      }
    };
    private final int mLowerLimit;
    private final int mRegion;
    private final int mSpacing;
    private final int mType;
    private final int mUpperLimit;
    
    BandDescriptor(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      this.mRegion = paramInt1;
      this.mType = paramInt2;
      this.mLowerLimit = paramInt3;
      this.mUpperLimit = paramInt4;
      this.mSpacing = paramInt5;
    }
    
    private BandDescriptor(Parcel paramParcel)
    {
      this.mRegion = paramParcel.readInt();
      this.mType = paramParcel.readInt();
      this.mLowerLimit = paramParcel.readInt();
      this.mUpperLimit = paramParcel.readInt();
      this.mSpacing = paramParcel.readInt();
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
      if (!(paramObject instanceof BandDescriptor)) {
        return false;
      }
      paramObject = (BandDescriptor)paramObject;
      if (this.mRegion != ((BandDescriptor)paramObject).getRegion()) {
        return false;
      }
      if (this.mType != ((BandDescriptor)paramObject).getType()) {
        return false;
      }
      if (this.mLowerLimit != ((BandDescriptor)paramObject).getLowerLimit()) {
        return false;
      }
      if (this.mUpperLimit != ((BandDescriptor)paramObject).getUpperLimit()) {
        return false;
      }
      return this.mSpacing == ((BandDescriptor)paramObject).getSpacing();
    }
    
    public int getLowerLimit()
    {
      return this.mLowerLimit;
    }
    
    public int getRegion()
    {
      return this.mRegion;
    }
    
    public int getSpacing()
    {
      return this.mSpacing;
    }
    
    public int getType()
    {
      return this.mType;
    }
    
    public int getUpperLimit()
    {
      return this.mUpperLimit;
    }
    
    public int hashCode()
    {
      return ((((this.mRegion + 31) * 31 + this.mType) * 31 + this.mLowerLimit) * 31 + this.mUpperLimit) * 31 + this.mSpacing;
    }
    
    public String toString()
    {
      return "BandDescriptor [mRegion=" + this.mRegion + ", mType=" + this.mType + ", mLowerLimit=" + this.mLowerLimit + ", mUpperLimit=" + this.mUpperLimit + ", mSpacing=" + this.mSpacing + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mRegion);
      paramParcel.writeInt(this.mType);
      paramParcel.writeInt(this.mLowerLimit);
      paramParcel.writeInt(this.mUpperLimit);
      paramParcel.writeInt(this.mSpacing);
    }
  }
  
  public static class FmBandConfig
    extends RadioManager.BandConfig
  {
    public static final Parcelable.Creator<FmBandConfig> CREATOR = new Parcelable.Creator()
    {
      public RadioManager.FmBandConfig createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RadioManager.FmBandConfig(paramAnonymousParcel, null);
      }
      
      public RadioManager.FmBandConfig[] newArray(int paramAnonymousInt)
      {
        return new RadioManager.FmBandConfig[paramAnonymousInt];
      }
    };
    private final boolean mAf;
    private final boolean mEa;
    private final boolean mRds;
    private final boolean mStereo;
    private final boolean mTa;
    
    FmBandConfig(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
    {
      super(paramInt2, paramInt3, paramInt4, paramInt5);
      this.mStereo = paramBoolean1;
      this.mRds = paramBoolean2;
      this.mTa = paramBoolean3;
      this.mAf = paramBoolean4;
      this.mEa = paramBoolean5;
    }
    
    FmBandConfig(RadioManager.FmBandDescriptor paramFmBandDescriptor)
    {
      super();
      this.mStereo = paramFmBandDescriptor.isStereoSupported();
      this.mRds = paramFmBandDescriptor.isRdsSupported();
      this.mTa = paramFmBandDescriptor.isTaSupported();
      this.mAf = paramFmBandDescriptor.isAfSupported();
      this.mEa = paramFmBandDescriptor.isEaSupported();
    }
    
    private FmBandConfig(Parcel paramParcel)
    {
      super(null);
      if (paramParcel.readByte() == 1)
      {
        bool1 = true;
        this.mStereo = bool1;
        if (paramParcel.readByte() != 1) {
          break label89;
        }
        bool1 = true;
        label33:
        this.mRds = bool1;
        if (paramParcel.readByte() != 1) {
          break label94;
        }
        bool1 = true;
        label48:
        this.mTa = bool1;
        if (paramParcel.readByte() != 1) {
          break label99;
        }
        bool1 = true;
        label63:
        this.mAf = bool1;
        if (paramParcel.readByte() != 1) {
          break label104;
        }
      }
      label89:
      label94:
      label99:
      label104:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        this.mEa = bool1;
        return;
        bool1 = false;
        break;
        bool1 = false;
        break label33;
        bool1 = false;
        break label48;
        bool1 = false;
        break label63;
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
      if (!super.equals(paramObject)) {
        return false;
      }
      if (!(paramObject instanceof FmBandConfig)) {
        return false;
      }
      paramObject = (FmBandConfig)paramObject;
      if (this.mStereo != ((FmBandConfig)paramObject).mStereo) {
        return false;
      }
      if (this.mRds != ((FmBandConfig)paramObject).mRds) {
        return false;
      }
      if (this.mTa != ((FmBandConfig)paramObject).mTa) {
        return false;
      }
      if (this.mAf != ((FmBandConfig)paramObject).mAf) {
        return false;
      }
      return this.mEa == ((FmBandConfig)paramObject).mEa;
    }
    
    public boolean getAf()
    {
      return this.mAf;
    }
    
    public boolean getEa()
    {
      return this.mEa;
    }
    
    public boolean getRds()
    {
      return this.mRds;
    }
    
    public boolean getStereo()
    {
      return this.mStereo;
    }
    
    public boolean getTa()
    {
      return this.mTa;
    }
    
    public int hashCode()
    {
      int n = 1;
      int i1 = super.hashCode();
      int i;
      int j;
      label27:
      int k;
      label36:
      int m;
      if (this.mStereo)
      {
        i = 1;
        if (!this.mRds) {
          break label88;
        }
        j = 1;
        if (!this.mTa) {
          break label93;
        }
        k = 1;
        if (!this.mAf) {
          break label98;
        }
        m = 1;
        label46:
        if (!this.mEa) {
          break label104;
        }
      }
      for (;;)
      {
        return ((((i1 * 31 + i) * 31 + j) * 31 + k) * 31 + m) * 31 + n;
        i = 0;
        break;
        label88:
        j = 0;
        break label27;
        label93:
        k = 0;
        break label36;
        label98:
        m = 0;
        break label46;
        label104:
        n = 0;
      }
    }
    
    public String toString()
    {
      return "FmBandConfig [" + super.toString() + ", mStereo=" + this.mStereo + ", mRds=" + this.mRds + ", mTa=" + this.mTa + ", mAf=" + this.mAf + ", mEa =" + this.mEa + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 1;
      super.writeToParcel(paramParcel, paramInt);
      if (this.mStereo)
      {
        paramInt = 1;
        paramParcel.writeByte((byte)paramInt);
        if (!this.mRds) {
          break label89;
        }
        paramInt = 1;
        label32:
        paramParcel.writeByte((byte)paramInt);
        if (!this.mTa) {
          break label94;
        }
        paramInt = 1;
        label47:
        paramParcel.writeByte((byte)paramInt);
        if (!this.mAf) {
          break label99;
        }
        paramInt = 1;
        label62:
        paramParcel.writeByte((byte)paramInt);
        if (!this.mEa) {
          break label104;
        }
      }
      label89:
      label94:
      label99:
      label104:
      for (paramInt = i;; paramInt = 0)
      {
        paramParcel.writeByte((byte)paramInt);
        return;
        paramInt = 0;
        break;
        paramInt = 0;
        break label32;
        paramInt = 0;
        break label47;
        paramInt = 0;
        break label62;
      }
    }
    
    public static class Builder
    {
      private boolean mAf;
      private final RadioManager.BandDescriptor mDescriptor;
      private boolean mEa;
      private boolean mRds;
      private boolean mStereo;
      private boolean mTa;
      
      public Builder(RadioManager.FmBandConfig paramFmBandConfig)
      {
        this.mDescriptor = new RadioManager.BandDescriptor(paramFmBandConfig.getRegion(), paramFmBandConfig.getType(), paramFmBandConfig.getLowerLimit(), paramFmBandConfig.getUpperLimit(), paramFmBandConfig.getSpacing());
        this.mStereo = paramFmBandConfig.getStereo();
        this.mRds = paramFmBandConfig.getRds();
        this.mTa = paramFmBandConfig.getTa();
        this.mAf = paramFmBandConfig.getAf();
        this.mEa = paramFmBandConfig.getEa();
      }
      
      public Builder(RadioManager.FmBandDescriptor paramFmBandDescriptor)
      {
        this.mDescriptor = new RadioManager.BandDescriptor(paramFmBandDescriptor.getRegion(), paramFmBandDescriptor.getType(), paramFmBandDescriptor.getLowerLimit(), paramFmBandDescriptor.getUpperLimit(), paramFmBandDescriptor.getSpacing());
        this.mStereo = paramFmBandDescriptor.isStereoSupported();
        this.mRds = paramFmBandDescriptor.isRdsSupported();
        this.mTa = paramFmBandDescriptor.isTaSupported();
        this.mAf = paramFmBandDescriptor.isAfSupported();
        this.mEa = paramFmBandDescriptor.isEaSupported();
      }
      
      public RadioManager.FmBandConfig build()
      {
        return new RadioManager.FmBandConfig(this.mDescriptor.getRegion(), this.mDescriptor.getType(), this.mDescriptor.getLowerLimit(), this.mDescriptor.getUpperLimit(), this.mDescriptor.getSpacing(), this.mStereo, this.mRds, this.mTa, this.mAf, this.mEa);
      }
      
      public Builder setAf(boolean paramBoolean)
      {
        this.mAf = paramBoolean;
        return this;
      }
      
      public Builder setEa(boolean paramBoolean)
      {
        this.mEa = paramBoolean;
        return this;
      }
      
      public Builder setRds(boolean paramBoolean)
      {
        this.mRds = paramBoolean;
        return this;
      }
      
      public Builder setStereo(boolean paramBoolean)
      {
        this.mStereo = paramBoolean;
        return this;
      }
      
      public Builder setTa(boolean paramBoolean)
      {
        this.mTa = paramBoolean;
        return this;
      }
    }
  }
  
  public static class FmBandDescriptor
    extends RadioManager.BandDescriptor
  {
    public static final Parcelable.Creator<FmBandDescriptor> CREATOR = new Parcelable.Creator()
    {
      public RadioManager.FmBandDescriptor createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RadioManager.FmBandDescriptor(paramAnonymousParcel, null);
      }
      
      public RadioManager.FmBandDescriptor[] newArray(int paramAnonymousInt)
      {
        return new RadioManager.FmBandDescriptor[paramAnonymousInt];
      }
    };
    private final boolean mAf;
    private final boolean mEa;
    private final boolean mRds;
    private final boolean mStereo;
    private final boolean mTa;
    
    FmBandDescriptor(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
    {
      super(paramInt2, paramInt3, paramInt4, paramInt5);
      this.mStereo = paramBoolean1;
      this.mRds = paramBoolean2;
      this.mTa = paramBoolean3;
      this.mAf = paramBoolean4;
      this.mEa = paramBoolean5;
    }
    
    private FmBandDescriptor(Parcel paramParcel)
    {
      super(null);
      if (paramParcel.readByte() == 1)
      {
        bool1 = true;
        this.mStereo = bool1;
        if (paramParcel.readByte() != 1) {
          break label89;
        }
        bool1 = true;
        label33:
        this.mRds = bool1;
        if (paramParcel.readByte() != 1) {
          break label94;
        }
        bool1 = true;
        label48:
        this.mTa = bool1;
        if (paramParcel.readByte() != 1) {
          break label99;
        }
        bool1 = true;
        label63:
        this.mAf = bool1;
        if (paramParcel.readByte() != 1) {
          break label104;
        }
      }
      label89:
      label94:
      label99:
      label104:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        this.mEa = bool1;
        return;
        bool1 = false;
        break;
        bool1 = false;
        break label33;
        bool1 = false;
        break label48;
        bool1 = false;
        break label63;
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
      if (!super.equals(paramObject)) {
        return false;
      }
      if (!(paramObject instanceof FmBandDescriptor)) {
        return false;
      }
      paramObject = (FmBandDescriptor)paramObject;
      if (this.mStereo != ((FmBandDescriptor)paramObject).isStereoSupported()) {
        return false;
      }
      if (this.mRds != ((FmBandDescriptor)paramObject).isRdsSupported()) {
        return false;
      }
      if (this.mTa != ((FmBandDescriptor)paramObject).isTaSupported()) {
        return false;
      }
      if (this.mAf != ((FmBandDescriptor)paramObject).isAfSupported()) {
        return false;
      }
      return this.mEa == ((FmBandDescriptor)paramObject).isEaSupported();
    }
    
    public int hashCode()
    {
      int n = 1;
      int i1 = super.hashCode();
      int i;
      int j;
      label27:
      int k;
      label36:
      int m;
      if (this.mStereo)
      {
        i = 1;
        if (!this.mRds) {
          break label88;
        }
        j = 1;
        if (!this.mTa) {
          break label93;
        }
        k = 1;
        if (!this.mAf) {
          break label98;
        }
        m = 1;
        label46:
        if (!this.mEa) {
          break label104;
        }
      }
      for (;;)
      {
        return ((((i1 * 31 + i) * 31 + j) * 31 + k) * 31 + m) * 31 + n;
        i = 0;
        break;
        label88:
        j = 0;
        break label27;
        label93:
        k = 0;
        break label36;
        label98:
        m = 0;
        break label46;
        label104:
        n = 0;
      }
    }
    
    public boolean isAfSupported()
    {
      return this.mAf;
    }
    
    public boolean isEaSupported()
    {
      return this.mEa;
    }
    
    public boolean isRdsSupported()
    {
      return this.mRds;
    }
    
    public boolean isStereoSupported()
    {
      return this.mStereo;
    }
    
    public boolean isTaSupported()
    {
      return this.mTa;
    }
    
    public String toString()
    {
      return "FmBandDescriptor [ " + super.toString() + " mStereo=" + this.mStereo + ", mRds=" + this.mRds + ", mTa=" + this.mTa + ", mAf=" + this.mAf + ", mEa =" + this.mEa + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 1;
      super.writeToParcel(paramParcel, paramInt);
      if (this.mStereo)
      {
        paramInt = 1;
        paramParcel.writeByte((byte)paramInt);
        if (!this.mRds) {
          break label89;
        }
        paramInt = 1;
        label32:
        paramParcel.writeByte((byte)paramInt);
        if (!this.mTa) {
          break label94;
        }
        paramInt = 1;
        label47:
        paramParcel.writeByte((byte)paramInt);
        if (!this.mAf) {
          break label99;
        }
        paramInt = 1;
        label62:
        paramParcel.writeByte((byte)paramInt);
        if (!this.mEa) {
          break label104;
        }
      }
      label89:
      label94:
      label99:
      label104:
      for (paramInt = i;; paramInt = 0)
      {
        paramParcel.writeByte((byte)paramInt);
        return;
        paramInt = 0;
        break;
        paramInt = 0;
        break label32;
        paramInt = 0;
        break label47;
        paramInt = 0;
        break label62;
      }
    }
  }
  
  public static class ModuleProperties
    implements Parcelable
  {
    public static final Parcelable.Creator<ModuleProperties> CREATOR = new Parcelable.Creator()
    {
      public RadioManager.ModuleProperties createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RadioManager.ModuleProperties(paramAnonymousParcel, null);
      }
      
      public RadioManager.ModuleProperties[] newArray(int paramAnonymousInt)
      {
        return new RadioManager.ModuleProperties[paramAnonymousInt];
      }
    };
    private final RadioManager.BandDescriptor[] mBands;
    private final int mClassId;
    private final int mId;
    private final String mImplementor;
    private final boolean mIsCaptureSupported;
    private final int mNumAudioSources;
    private final int mNumTuners;
    private final String mProduct;
    private final String mSerial;
    private final String mVersion;
    
    ModuleProperties(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt3, int paramInt4, boolean paramBoolean, RadioManager.BandDescriptor[] paramArrayOfBandDescriptor)
    {
      this.mId = paramInt1;
      this.mClassId = paramInt2;
      this.mImplementor = paramString1;
      this.mProduct = paramString2;
      this.mVersion = paramString3;
      this.mSerial = paramString4;
      this.mNumTuners = paramInt3;
      this.mNumAudioSources = paramInt4;
      this.mIsCaptureSupported = paramBoolean;
      this.mBands = paramArrayOfBandDescriptor;
    }
    
    private ModuleProperties(Parcel paramParcel)
    {
      this.mId = paramParcel.readInt();
      this.mClassId = paramParcel.readInt();
      this.mImplementor = paramParcel.readString();
      this.mProduct = paramParcel.readString();
      this.mVersion = paramParcel.readString();
      this.mSerial = paramParcel.readString();
      this.mNumTuners = paramParcel.readInt();
      this.mNumAudioSources = paramParcel.readInt();
      if (paramParcel.readInt() == 1) {}
      for (;;)
      {
        this.mIsCaptureSupported = bool;
        paramParcel = paramParcel.readParcelableArray(RadioManager.BandDescriptor.class.getClassLoader());
        this.mBands = new RadioManager.BandDescriptor[paramParcel.length];
        int i = 0;
        while (i < paramParcel.length)
        {
          this.mBands[i] = ((RadioManager.BandDescriptor)paramParcel[i]);
          i += 1;
        }
        bool = false;
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
      if (!(paramObject instanceof ModuleProperties)) {
        return false;
      }
      paramObject = (ModuleProperties)paramObject;
      if (this.mId != ((ModuleProperties)paramObject).getId()) {
        return false;
      }
      if (this.mClassId != ((ModuleProperties)paramObject).getClassId()) {
        return false;
      }
      if (this.mImplementor == null)
      {
        if (((ModuleProperties)paramObject).getImplementor() != null) {
          return false;
        }
      }
      else if (!this.mImplementor.equals(((ModuleProperties)paramObject).getImplementor())) {
        return false;
      }
      if (this.mProduct == null)
      {
        if (((ModuleProperties)paramObject).getProduct() != null) {
          return false;
        }
      }
      else if (!this.mProduct.equals(((ModuleProperties)paramObject).getProduct())) {
        return false;
      }
      if (this.mVersion == null)
      {
        if (((ModuleProperties)paramObject).getVersion() != null) {
          return false;
        }
      }
      else if (!this.mVersion.equals(((ModuleProperties)paramObject).getVersion())) {
        return false;
      }
      if (this.mSerial == null)
      {
        if (((ModuleProperties)paramObject).getSerial() != null) {
          return false;
        }
      }
      else if (!this.mSerial.equals(((ModuleProperties)paramObject).getSerial())) {
        return false;
      }
      if (this.mNumTuners != ((ModuleProperties)paramObject).getNumTuners()) {
        return false;
      }
      if (this.mNumAudioSources != ((ModuleProperties)paramObject).getNumAudioSources()) {
        return false;
      }
      if (this.mIsCaptureSupported != ((ModuleProperties)paramObject).isCaptureSupported()) {
        return false;
      }
      return Arrays.equals(this.mBands, ((ModuleProperties)paramObject).getBands());
    }
    
    public RadioManager.BandDescriptor[] getBands()
    {
      return this.mBands;
    }
    
    public int getClassId()
    {
      return this.mClassId;
    }
    
    public int getId()
    {
      return this.mId;
    }
    
    public String getImplementor()
    {
      return this.mImplementor;
    }
    
    public int getNumAudioSources()
    {
      return this.mNumAudioSources;
    }
    
    public int getNumTuners()
    {
      return this.mNumTuners;
    }
    
    public String getProduct()
    {
      return this.mProduct;
    }
    
    public String getSerial()
    {
      return this.mSerial;
    }
    
    public String getVersion()
    {
      return this.mVersion;
    }
    
    public int hashCode()
    {
      int n = 0;
      int i1 = this.mId;
      int i2 = this.mClassId;
      int i;
      int j;
      label33:
      int k;
      if (this.mImplementor == null)
      {
        i = 0;
        if (this.mProduct != null) {
          break label147;
        }
        j = 0;
        if (this.mVersion != null) {
          break label158;
        }
        k = 0;
        label42:
        if (this.mSerial != null) {
          break label169;
        }
      }
      label147:
      label158:
      label169:
      for (int m = 0;; m = this.mSerial.hashCode())
      {
        int i3 = this.mNumTuners;
        int i4 = this.mNumAudioSources;
        if (this.mIsCaptureSupported) {
          n = 1;
        }
        return (((((((((i1 + 31) * 31 + i2) * 31 + i) * 31 + j) * 31 + k) * 31 + m) * 31 + i3) * 31 + i4) * 31 + n) * 31 + Arrays.hashCode(this.mBands);
        i = this.mImplementor.hashCode();
        break;
        j = this.mProduct.hashCode();
        break label33;
        k = this.mVersion.hashCode();
        break label42;
      }
    }
    
    public boolean isCaptureSupported()
    {
      return this.mIsCaptureSupported;
    }
    
    public String toString()
    {
      return "ModuleProperties [mId=" + this.mId + ", mClassId=" + this.mClassId + ", mImplementor=" + this.mImplementor + ", mProduct=" + this.mProduct + ", mVersion=" + this.mVersion + ", mSerial=" + this.mSerial + ", mNumTuners=" + this.mNumTuners + ", mNumAudioSources=" + this.mNumAudioSources + ", mIsCaptureSupported=" + this.mIsCaptureSupported + ", mBands=" + Arrays.toString(this.mBands) + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mId);
      paramParcel.writeInt(this.mClassId);
      paramParcel.writeString(this.mImplementor);
      paramParcel.writeString(this.mProduct);
      paramParcel.writeString(this.mVersion);
      paramParcel.writeString(this.mSerial);
      paramParcel.writeInt(this.mNumTuners);
      paramParcel.writeInt(this.mNumAudioSources);
      if (this.mIsCaptureSupported) {}
      for (int i = 1;; i = 0)
      {
        paramParcel.writeInt(i);
        paramParcel.writeParcelableArray(this.mBands, paramInt);
        return;
      }
    }
  }
  
  public static class ProgramInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<ProgramInfo> CREATOR = new Parcelable.Creator()
    {
      public RadioManager.ProgramInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RadioManager.ProgramInfo(paramAnonymousParcel, null);
      }
      
      public RadioManager.ProgramInfo[] newArray(int paramAnonymousInt)
      {
        return new RadioManager.ProgramInfo[paramAnonymousInt];
      }
    };
    private final int mChannel;
    private final boolean mDigital;
    private final RadioMetadata mMetadata;
    private final int mSignalStrength;
    private final boolean mStereo;
    private final int mSubChannel;
    private final boolean mTuned;
    
    ProgramInfo(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt3, RadioMetadata paramRadioMetadata)
    {
      this.mChannel = paramInt1;
      this.mSubChannel = paramInt2;
      this.mTuned = paramBoolean1;
      this.mStereo = paramBoolean2;
      this.mDigital = paramBoolean3;
      this.mSignalStrength = paramInt3;
      this.mMetadata = paramRadioMetadata;
    }
    
    private ProgramInfo(Parcel paramParcel)
    {
      this.mChannel = paramParcel.readInt();
      this.mSubChannel = paramParcel.readInt();
      if (paramParcel.readByte() == 1)
      {
        bool1 = true;
        this.mTuned = bool1;
        if (paramParcel.readByte() != 1) {
          break label107;
        }
      }
      label107:
      for (boolean bool1 = true;; bool1 = false)
      {
        this.mStereo = bool1;
        bool1 = bool2;
        if (paramParcel.readByte() == 1) {
          bool1 = true;
        }
        this.mDigital = bool1;
        this.mSignalStrength = paramParcel.readInt();
        if (paramParcel.readByte() != 1) {
          break label112;
        }
        this.mMetadata = ((RadioMetadata)RadioMetadata.CREATOR.createFromParcel(paramParcel));
        return;
        bool1 = false;
        break;
      }
      label112:
      this.mMetadata = null;
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
      if (!(paramObject instanceof ProgramInfo)) {
        return false;
      }
      paramObject = (ProgramInfo)paramObject;
      if (this.mChannel != ((ProgramInfo)paramObject).getChannel()) {
        return false;
      }
      if (this.mSubChannel != ((ProgramInfo)paramObject).getSubChannel()) {
        return false;
      }
      if (this.mTuned != ((ProgramInfo)paramObject).isTuned()) {
        return false;
      }
      if (this.mStereo != ((ProgramInfo)paramObject).isStereo()) {
        return false;
      }
      if (this.mDigital != ((ProgramInfo)paramObject).isDigital()) {
        return false;
      }
      if (this.mSignalStrength != ((ProgramInfo)paramObject).getSignalStrength()) {
        return false;
      }
      if (this.mMetadata == null)
      {
        if (((ProgramInfo)paramObject).getMetadata() != null) {
          return false;
        }
      }
      else if (!this.mMetadata.equals(((ProgramInfo)paramObject).getMetadata())) {
        return false;
      }
      return true;
    }
    
    public int getChannel()
    {
      return this.mChannel;
    }
    
    public RadioMetadata getMetadata()
    {
      return this.mMetadata;
    }
    
    public int getSignalStrength()
    {
      return this.mSignalStrength;
    }
    
    public int getSubChannel()
    {
      return this.mSubChannel;
    }
    
    public int hashCode()
    {
      int k = 1;
      int m = 0;
      int n = this.mChannel;
      int i1 = this.mSubChannel;
      int i;
      int j;
      label35:
      label42:
      int i2;
      if (this.mTuned)
      {
        i = 1;
        if (!this.mStereo) {
          break label99;
        }
        j = 1;
        if (!this.mDigital) {
          break label104;
        }
        i2 = this.mSignalStrength;
        if (this.mMetadata != null) {
          break label109;
        }
      }
      for (;;)
      {
        return ((((((n + 31) * 31 + i1) * 31 + i) * 31 + j) * 31 + k) * 31 + i2) * 31 + m;
        i = 0;
        break;
        label99:
        j = 0;
        break label35;
        label104:
        k = 0;
        break label42;
        label109:
        m = this.mMetadata.hashCode();
      }
    }
    
    public boolean isDigital()
    {
      return this.mDigital;
    }
    
    public boolean isStereo()
    {
      return this.mStereo;
    }
    
    public boolean isTuned()
    {
      return this.mTuned;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("ProgramInfo [mChannel=").append(this.mChannel).append(", mSubChannel=").append(this.mSubChannel).append(", mTuned=").append(this.mTuned).append(", mStereo=").append(this.mStereo).append(", mDigital=").append(this.mDigital).append(", mSignalStrength=").append(this.mSignalStrength);
      if (this.mMetadata == null) {}
      for (String str = "";; str = ", mMetadata=" + this.mMetadata.toString()) {
        return str + "]";
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mChannel);
      paramParcel.writeInt(this.mSubChannel);
      if (this.mTuned)
      {
        i = 1;
        paramParcel.writeByte((byte)i);
        if (!this.mStereo) {
          break label87;
        }
        i = 1;
        label40:
        paramParcel.writeByte((byte)i);
        if (!this.mDigital) {
          break label92;
        }
      }
      label87:
      label92:
      for (int i = 1;; i = 0)
      {
        paramParcel.writeByte((byte)i);
        paramParcel.writeInt(this.mSignalStrength);
        if (this.mMetadata != null) {
          break label97;
        }
        paramParcel.writeByte((byte)0);
        return;
        i = 0;
        break;
        i = 0;
        break label40;
      }
      label97:
      paramParcel.writeByte((byte)1);
      this.mMetadata.writeToParcel(paramParcel, paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/radio/RadioManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */