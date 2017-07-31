package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GpsMeasurement
  implements Parcelable
{
  private static final short ADR_ALL = 7;
  public static final short ADR_STATE_CYCLE_SLIP = 4;
  public static final short ADR_STATE_RESET = 2;
  public static final short ADR_STATE_UNKNOWN = 0;
  public static final short ADR_STATE_VALID = 1;
  public static final Parcelable.Creator<GpsMeasurement> CREATOR = new Parcelable.Creator()
  {
    public GpsMeasurement createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool = false;
      GpsMeasurement localGpsMeasurement = new GpsMeasurement();
      GpsMeasurement.-set17(localGpsMeasurement, paramAnonymousParcel.readInt());
      GpsMeasurement.-set20(localGpsMeasurement, paramAnonymousParcel.readByte());
      GpsMeasurement.-set30(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set28(localGpsMeasurement, (short)paramAnonymousParcel.readInt());
      GpsMeasurement.-set25(localGpsMeasurement, paramAnonymousParcel.readLong());
      GpsMeasurement.-set26(localGpsMeasurement, paramAnonymousParcel.readLong());
      GpsMeasurement.-set10(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set22(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set23(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set1(localGpsMeasurement, (short)paramAnonymousParcel.readInt());
      GpsMeasurement.-set0(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set2(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set21(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set24(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set11(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set12(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set7(localGpsMeasurement, paramAnonymousParcel.readFloat());
      GpsMeasurement.-set6(localGpsMeasurement, paramAnonymousParcel.readLong());
      GpsMeasurement.-set8(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set9(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set18(localGpsMeasurement, paramAnonymousParcel.readByte());
      GpsMeasurement.-set5(localGpsMeasurement, paramAnonymousParcel.readInt());
      GpsMeasurement.-set29(localGpsMeasurement, (short)paramAnonymousParcel.readInt());
      GpsMeasurement.-set13(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set14(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set19(localGpsMeasurement, paramAnonymousParcel.readByte());
      GpsMeasurement.-set27(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set15(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set16(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set3(localGpsMeasurement, paramAnonymousParcel.readDouble());
      GpsMeasurement.-set4(localGpsMeasurement, paramAnonymousParcel.readDouble());
      if (paramAnonymousParcel.readInt() != 0) {
        bool = true;
      }
      GpsMeasurement.-set31(localGpsMeasurement, bool);
      return localGpsMeasurement;
    }
    
    public GpsMeasurement[] newArray(int paramAnonymousInt)
    {
      return new GpsMeasurement[paramAnonymousInt];
    }
  };
  private static final int GPS_MEASUREMENT_HAS_UNCORRECTED_PSEUDORANGE_RATE = 262144;
  private static final int HAS_AZIMUTH = 8;
  private static final int HAS_AZIMUTH_UNCERTAINTY = 16;
  private static final int HAS_BIT_NUMBER = 8192;
  private static final int HAS_CARRIER_CYCLES = 1024;
  private static final int HAS_CARRIER_FREQUENCY = 512;
  private static final int HAS_CARRIER_PHASE = 2048;
  private static final int HAS_CARRIER_PHASE_UNCERTAINTY = 4096;
  private static final int HAS_CODE_PHASE = 128;
  private static final int HAS_CODE_PHASE_UNCERTAINTY = 256;
  private static final int HAS_DOPPLER_SHIFT = 32768;
  private static final int HAS_DOPPLER_SHIFT_UNCERTAINTY = 65536;
  private static final int HAS_ELEVATION = 2;
  private static final int HAS_ELEVATION_UNCERTAINTY = 4;
  private static final int HAS_NO_FLAGS = 0;
  private static final int HAS_PSEUDORANGE = 32;
  private static final int HAS_PSEUDORANGE_UNCERTAINTY = 64;
  private static final int HAS_SNR = 1;
  private static final int HAS_TIME_FROM_LAST_BIT = 16384;
  private static final int HAS_USED_IN_FIX = 131072;
  public static final byte LOSS_OF_LOCK_CYCLE_SLIP = 2;
  public static final byte LOSS_OF_LOCK_OK = 1;
  public static final byte LOSS_OF_LOCK_UNKNOWN = 0;
  public static final byte MULTIPATH_INDICATOR_DETECTED = 1;
  public static final byte MULTIPATH_INDICATOR_NOT_USED = 2;
  public static final byte MULTIPATH_INDICATOR_UNKNOWN = 0;
  private static final short STATE_ALL = 31;
  public static final short STATE_BIT_SYNC = 2;
  public static final short STATE_CODE_LOCK = 1;
  public static final short STATE_MSEC_AMBIGUOUS = 16;
  public static final short STATE_SUBFRAME_SYNC = 4;
  public static final short STATE_TOW_DECODED = 8;
  public static final short STATE_UNKNOWN = 0;
  private double mAccumulatedDeltaRangeInMeters;
  private short mAccumulatedDeltaRangeState;
  private double mAccumulatedDeltaRangeUncertaintyInMeters;
  private double mAzimuthInDeg;
  private double mAzimuthUncertaintyInDeg;
  private int mBitNumber;
  private long mCarrierCycles;
  private float mCarrierFrequencyInHz;
  private double mCarrierPhase;
  private double mCarrierPhaseUncertainty;
  private double mCn0InDbHz;
  private double mCodePhaseInChips;
  private double mCodePhaseUncertaintyInChips;
  private double mDopplerShiftInHz;
  private double mDopplerShiftUncertaintyInHz;
  private double mElevationInDeg;
  private double mElevationUncertaintyInDeg;
  private int mFlags;
  private byte mLossOfLock;
  private byte mMultipathIndicator;
  private byte mPrn;
  private double mPseudorangeInMeters;
  private double mPseudorangeRateInMetersPerSec;
  private double mPseudorangeRateUncertaintyInMetersPerSec;
  private double mPseudorangeUncertaintyInMeters;
  private long mReceivedGpsTowInNs;
  private long mReceivedGpsTowUncertaintyInNs;
  private double mSnrInDb;
  private short mState;
  private short mTimeFromLastBitInMs;
  private double mTimeOffsetInNs;
  private boolean mUsedInFix;
  
  GpsMeasurement()
  {
    initialize();
  }
  
  private String getAccumulatedDeltaRangeStateString()
  {
    if (this.mAccumulatedDeltaRangeState == 0) {
      return "Unknown";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if ((this.mAccumulatedDeltaRangeState & 0x1) == 1) {
      localStringBuilder.append("Valid|");
    }
    if ((this.mAccumulatedDeltaRangeState & 0x2) == 2) {
      localStringBuilder.append("Reset|");
    }
    if ((this.mAccumulatedDeltaRangeState & 0x4) == 4) {
      localStringBuilder.append("CycleSlip|");
    }
    int i = this.mAccumulatedDeltaRangeState & 0xFFFFFFF8;
    if (i > 0)
    {
      localStringBuilder.append("Other(");
      localStringBuilder.append(Integer.toBinaryString(i));
      localStringBuilder.append(")|");
    }
    localStringBuilder.deleteCharAt(localStringBuilder.length() - 1);
    return localStringBuilder.toString();
  }
  
  private String getLossOfLockString()
  {
    switch (this.mLossOfLock)
    {
    default: 
      return "<Invalid:" + this.mLossOfLock + ">";
    case 0: 
      return "Unknown";
    case 1: 
      return "Ok";
    }
    return "CycleSlip";
  }
  
  private String getMultipathIndicatorString()
  {
    switch (this.mMultipathIndicator)
    {
    default: 
      return "<Invalid:" + this.mMultipathIndicator + ">";
    case 0: 
      return "Unknown";
    case 1: 
      return "Detected";
    }
    return "NotUsed";
  }
  
  private String getStateString()
  {
    if (this.mState == 0) {
      return "Unknown";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if ((this.mState & 0x1) == 1) {
      localStringBuilder.append("CodeLock|");
    }
    if ((this.mState & 0x2) == 2) {
      localStringBuilder.append("BitSync|");
    }
    if ((this.mState & 0x4) == 4) {
      localStringBuilder.append("SubframeSync|");
    }
    if ((this.mState & 0x8) == 8) {
      localStringBuilder.append("TowDecoded|");
    }
    if ((this.mState & 0x10) == 16) {
      localStringBuilder.append("MsecAmbiguous");
    }
    int i = this.mState & 0xFFFFFFE0;
    if (i > 0)
    {
      localStringBuilder.append("Other(");
      localStringBuilder.append(Integer.toBinaryString(i));
      localStringBuilder.append(")|");
    }
    localStringBuilder.deleteCharAt(localStringBuilder.length() - 1);
    return localStringBuilder.toString();
  }
  
  private void initialize()
  {
    this.mFlags = 0;
    setPrn((byte)Byte.MIN_VALUE);
    setTimeOffsetInNs(-9.223372036854776E18D);
    setState((short)0);
    setReceivedGpsTowInNs(Long.MIN_VALUE);
    setReceivedGpsTowUncertaintyInNs(Long.MAX_VALUE);
    setCn0InDbHz(Double.MIN_VALUE);
    setPseudorangeRateInMetersPerSec(Double.MIN_VALUE);
    setPseudorangeRateUncertaintyInMetersPerSec(Double.MIN_VALUE);
    setAccumulatedDeltaRangeState((short)0);
    setAccumulatedDeltaRangeInMeters(Double.MIN_VALUE);
    setAccumulatedDeltaRangeUncertaintyInMeters(Double.MIN_VALUE);
    resetPseudorangeInMeters();
    resetPseudorangeUncertaintyInMeters();
    resetCodePhaseInChips();
    resetCodePhaseUncertaintyInChips();
    resetCarrierFrequencyInHz();
    resetCarrierCycles();
    resetCarrierPhase();
    resetCarrierPhaseUncertainty();
    setLossOfLock((byte)0);
    resetBitNumber();
    resetTimeFromLastBitInMs();
    resetDopplerShiftInHz();
    resetDopplerShiftUncertaintyInHz();
    setMultipathIndicator((byte)0);
    resetSnrInDb();
    resetElevationInDeg();
    resetElevationUncertaintyInDeg();
    resetAzimuthInDeg();
    resetAzimuthUncertaintyInDeg();
    setUsedInFix(false);
  }
  
  private boolean isFlagSet(int paramInt)
  {
    return (this.mFlags & paramInt) == paramInt;
  }
  
  private void resetFlag(int paramInt)
  {
    this.mFlags &= paramInt;
  }
  
  private void setFlag(int paramInt)
  {
    this.mFlags |= paramInt;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public double getAccumulatedDeltaRangeInMeters()
  {
    return this.mAccumulatedDeltaRangeInMeters;
  }
  
  public short getAccumulatedDeltaRangeState()
  {
    return this.mAccumulatedDeltaRangeState;
  }
  
  public double getAccumulatedDeltaRangeUncertaintyInMeters()
  {
    return this.mAccumulatedDeltaRangeUncertaintyInMeters;
  }
  
  public double getAzimuthInDeg()
  {
    return this.mAzimuthInDeg;
  }
  
  public double getAzimuthUncertaintyInDeg()
  {
    return this.mAzimuthUncertaintyInDeg;
  }
  
  public int getBitNumber()
  {
    return this.mBitNumber;
  }
  
  public long getCarrierCycles()
  {
    return this.mCarrierCycles;
  }
  
  public float getCarrierFrequencyInHz()
  {
    return this.mCarrierFrequencyInHz;
  }
  
  public double getCarrierPhase()
  {
    return this.mCarrierPhase;
  }
  
  public double getCarrierPhaseUncertainty()
  {
    return this.mCarrierPhaseUncertainty;
  }
  
  public double getCn0InDbHz()
  {
    return this.mCn0InDbHz;
  }
  
  public double getCodePhaseInChips()
  {
    return this.mCodePhaseInChips;
  }
  
  public double getCodePhaseUncertaintyInChips()
  {
    return this.mCodePhaseUncertaintyInChips;
  }
  
  public double getDopplerShiftInHz()
  {
    return this.mDopplerShiftInHz;
  }
  
  public double getDopplerShiftUncertaintyInHz()
  {
    return this.mDopplerShiftUncertaintyInHz;
  }
  
  public double getElevationInDeg()
  {
    return this.mElevationInDeg;
  }
  
  public double getElevationUncertaintyInDeg()
  {
    return this.mElevationUncertaintyInDeg;
  }
  
  public byte getLossOfLock()
  {
    return this.mLossOfLock;
  }
  
  public byte getMultipathIndicator()
  {
    return this.mMultipathIndicator;
  }
  
  public byte getPrn()
  {
    return this.mPrn;
  }
  
  public double getPseudorangeInMeters()
  {
    return this.mPseudorangeInMeters;
  }
  
  public double getPseudorangeRateInMetersPerSec()
  {
    return this.mPseudorangeRateInMetersPerSec;
  }
  
  public double getPseudorangeRateUncertaintyInMetersPerSec()
  {
    return this.mPseudorangeRateUncertaintyInMetersPerSec;
  }
  
  public double getPseudorangeUncertaintyInMeters()
  {
    return this.mPseudorangeUncertaintyInMeters;
  }
  
  public long getReceivedGpsTowInNs()
  {
    return this.mReceivedGpsTowInNs;
  }
  
  public long getReceivedGpsTowUncertaintyInNs()
  {
    return this.mReceivedGpsTowUncertaintyInNs;
  }
  
  public double getSnrInDb()
  {
    return this.mSnrInDb;
  }
  
  public short getState()
  {
    return this.mState;
  }
  
  public short getTimeFromLastBitInMs()
  {
    return this.mTimeFromLastBitInMs;
  }
  
  public double getTimeOffsetInNs()
  {
    return this.mTimeOffsetInNs;
  }
  
  public boolean hasAzimuthInDeg()
  {
    return isFlagSet(8);
  }
  
  public boolean hasAzimuthUncertaintyInDeg()
  {
    return isFlagSet(16);
  }
  
  public boolean hasBitNumber()
  {
    return isFlagSet(8192);
  }
  
  public boolean hasCarrierCycles()
  {
    return isFlagSet(1024);
  }
  
  public boolean hasCarrierFrequencyInHz()
  {
    return isFlagSet(512);
  }
  
  public boolean hasCarrierPhase()
  {
    return isFlagSet(2048);
  }
  
  public boolean hasCarrierPhaseUncertainty()
  {
    return isFlagSet(4096);
  }
  
  public boolean hasCodePhaseInChips()
  {
    return isFlagSet(128);
  }
  
  public boolean hasCodePhaseUncertaintyInChips()
  {
    return isFlagSet(256);
  }
  
  public boolean hasDopplerShiftInHz()
  {
    return isFlagSet(32768);
  }
  
  public boolean hasDopplerShiftUncertaintyInHz()
  {
    return isFlagSet(65536);
  }
  
  public boolean hasElevationInDeg()
  {
    return isFlagSet(2);
  }
  
  public boolean hasElevationUncertaintyInDeg()
  {
    return isFlagSet(4);
  }
  
  public boolean hasPseudorangeInMeters()
  {
    return isFlagSet(32);
  }
  
  public boolean hasPseudorangeUncertaintyInMeters()
  {
    return isFlagSet(64);
  }
  
  public boolean hasSnrInDb()
  {
    return isFlagSet(1);
  }
  
  public boolean hasTimeFromLastBitInMs()
  {
    return isFlagSet(16384);
  }
  
  public boolean isPseudorangeRateCorrected()
  {
    return !isFlagSet(262144);
  }
  
  public boolean isUsedInFix()
  {
    return this.mUsedInFix;
  }
  
  public void reset()
  {
    initialize();
  }
  
  public void resetAzimuthInDeg()
  {
    resetFlag(8);
    this.mAzimuthInDeg = NaN.0D;
  }
  
  public void resetAzimuthUncertaintyInDeg()
  {
    resetFlag(16);
    this.mAzimuthUncertaintyInDeg = NaN.0D;
  }
  
  public void resetBitNumber()
  {
    resetFlag(8192);
    this.mBitNumber = Integer.MIN_VALUE;
  }
  
  public void resetCarrierCycles()
  {
    resetFlag(1024);
    this.mCarrierCycles = Long.MIN_VALUE;
  }
  
  public void resetCarrierFrequencyInHz()
  {
    resetFlag(512);
    this.mCarrierFrequencyInHz = NaN.0F;
  }
  
  public void resetCarrierPhase()
  {
    resetFlag(2048);
    this.mCarrierPhase = NaN.0D;
  }
  
  public void resetCarrierPhaseUncertainty()
  {
    resetFlag(4096);
    this.mCarrierPhaseUncertainty = NaN.0D;
  }
  
  public void resetCodePhaseInChips()
  {
    resetFlag(128);
    this.mCodePhaseInChips = NaN.0D;
  }
  
  public void resetCodePhaseUncertaintyInChips()
  {
    resetFlag(256);
    this.mCodePhaseUncertaintyInChips = NaN.0D;
  }
  
  public void resetDopplerShiftInHz()
  {
    resetFlag(32768);
    this.mDopplerShiftInHz = NaN.0D;
  }
  
  public void resetDopplerShiftUncertaintyInHz()
  {
    resetFlag(65536);
    this.mDopplerShiftUncertaintyInHz = NaN.0D;
  }
  
  public void resetElevationInDeg()
  {
    resetFlag(2);
    this.mElevationInDeg = NaN.0D;
  }
  
  public void resetElevationUncertaintyInDeg()
  {
    resetFlag(4);
    this.mElevationUncertaintyInDeg = NaN.0D;
  }
  
  public void resetPseudorangeInMeters()
  {
    resetFlag(32);
    this.mPseudorangeInMeters = NaN.0D;
  }
  
  public void resetPseudorangeUncertaintyInMeters()
  {
    resetFlag(64);
    this.mPseudorangeUncertaintyInMeters = NaN.0D;
  }
  
  public void resetSnrInDb()
  {
    resetFlag(1);
    this.mSnrInDb = NaN.0D;
  }
  
  public void resetTimeFromLastBitInMs()
  {
    resetFlag(16384);
    this.mTimeFromLastBitInMs = Short.MIN_VALUE;
  }
  
  public void set(GpsMeasurement paramGpsMeasurement)
  {
    this.mFlags = paramGpsMeasurement.mFlags;
    this.mPrn = paramGpsMeasurement.mPrn;
    this.mTimeOffsetInNs = paramGpsMeasurement.mTimeOffsetInNs;
    this.mState = paramGpsMeasurement.mState;
    this.mReceivedGpsTowInNs = paramGpsMeasurement.mReceivedGpsTowInNs;
    this.mReceivedGpsTowUncertaintyInNs = paramGpsMeasurement.mReceivedGpsTowUncertaintyInNs;
    this.mCn0InDbHz = paramGpsMeasurement.mCn0InDbHz;
    this.mPseudorangeRateInMetersPerSec = paramGpsMeasurement.mPseudorangeRateInMetersPerSec;
    this.mPseudorangeRateUncertaintyInMetersPerSec = paramGpsMeasurement.mPseudorangeRateUncertaintyInMetersPerSec;
    this.mAccumulatedDeltaRangeState = paramGpsMeasurement.mAccumulatedDeltaRangeState;
    this.mAccumulatedDeltaRangeInMeters = paramGpsMeasurement.mAccumulatedDeltaRangeInMeters;
    this.mAccumulatedDeltaRangeUncertaintyInMeters = paramGpsMeasurement.mAccumulatedDeltaRangeUncertaintyInMeters;
    this.mPseudorangeInMeters = paramGpsMeasurement.mPseudorangeInMeters;
    this.mPseudorangeUncertaintyInMeters = paramGpsMeasurement.mPseudorangeUncertaintyInMeters;
    this.mCodePhaseInChips = paramGpsMeasurement.mCodePhaseInChips;
    this.mCodePhaseUncertaintyInChips = paramGpsMeasurement.mCodePhaseUncertaintyInChips;
    this.mCarrierFrequencyInHz = paramGpsMeasurement.mCarrierFrequencyInHz;
    this.mCarrierCycles = paramGpsMeasurement.mCarrierCycles;
    this.mCarrierPhase = paramGpsMeasurement.mCarrierPhase;
    this.mCarrierPhaseUncertainty = paramGpsMeasurement.mCarrierPhaseUncertainty;
    this.mLossOfLock = paramGpsMeasurement.mLossOfLock;
    this.mBitNumber = paramGpsMeasurement.mBitNumber;
    this.mTimeFromLastBitInMs = paramGpsMeasurement.mTimeFromLastBitInMs;
    this.mDopplerShiftInHz = paramGpsMeasurement.mDopplerShiftInHz;
    this.mDopplerShiftUncertaintyInHz = paramGpsMeasurement.mDopplerShiftUncertaintyInHz;
    this.mMultipathIndicator = paramGpsMeasurement.mMultipathIndicator;
    this.mSnrInDb = paramGpsMeasurement.mSnrInDb;
    this.mElevationInDeg = paramGpsMeasurement.mElevationInDeg;
    this.mElevationUncertaintyInDeg = paramGpsMeasurement.mElevationUncertaintyInDeg;
    this.mAzimuthInDeg = paramGpsMeasurement.mAzimuthInDeg;
    this.mAzimuthUncertaintyInDeg = paramGpsMeasurement.mAzimuthUncertaintyInDeg;
    this.mUsedInFix = paramGpsMeasurement.mUsedInFix;
  }
  
  public void setAccumulatedDeltaRangeInMeters(double paramDouble)
  {
    this.mAccumulatedDeltaRangeInMeters = paramDouble;
  }
  
  public void setAccumulatedDeltaRangeState(short paramShort)
  {
    this.mAccumulatedDeltaRangeState = paramShort;
  }
  
  public void setAccumulatedDeltaRangeUncertaintyInMeters(double paramDouble)
  {
    this.mAccumulatedDeltaRangeUncertaintyInMeters = paramDouble;
  }
  
  public void setAzimuthInDeg(double paramDouble)
  {
    setFlag(8);
    this.mAzimuthInDeg = paramDouble;
  }
  
  public void setAzimuthUncertaintyInDeg(double paramDouble)
  {
    setFlag(16);
    this.mAzimuthUncertaintyInDeg = paramDouble;
  }
  
  public void setBitNumber(int paramInt)
  {
    setFlag(8192);
    this.mBitNumber = paramInt;
  }
  
  public void setCarrierCycles(long paramLong)
  {
    setFlag(1024);
    this.mCarrierCycles = paramLong;
  }
  
  public void setCarrierFrequencyInHz(float paramFloat)
  {
    setFlag(512);
    this.mCarrierFrequencyInHz = paramFloat;
  }
  
  public void setCarrierPhase(double paramDouble)
  {
    setFlag(2048);
    this.mCarrierPhase = paramDouble;
  }
  
  public void setCarrierPhaseUncertainty(double paramDouble)
  {
    setFlag(4096);
    this.mCarrierPhaseUncertainty = paramDouble;
  }
  
  public void setCn0InDbHz(double paramDouble)
  {
    this.mCn0InDbHz = paramDouble;
  }
  
  public void setCodePhaseInChips(double paramDouble)
  {
    setFlag(128);
    this.mCodePhaseInChips = paramDouble;
  }
  
  public void setCodePhaseUncertaintyInChips(double paramDouble)
  {
    setFlag(256);
    this.mCodePhaseUncertaintyInChips = paramDouble;
  }
  
  public void setDopplerShiftInHz(double paramDouble)
  {
    setFlag(32768);
    this.mDopplerShiftInHz = paramDouble;
  }
  
  public void setDopplerShiftUncertaintyInHz(double paramDouble)
  {
    setFlag(65536);
    this.mDopplerShiftUncertaintyInHz = paramDouble;
  }
  
  public void setElevationInDeg(double paramDouble)
  {
    setFlag(2);
    this.mElevationInDeg = paramDouble;
  }
  
  public void setElevationUncertaintyInDeg(double paramDouble)
  {
    setFlag(4);
    this.mElevationUncertaintyInDeg = paramDouble;
  }
  
  public void setLossOfLock(byte paramByte)
  {
    this.mLossOfLock = paramByte;
  }
  
  public void setMultipathIndicator(byte paramByte)
  {
    this.mMultipathIndicator = paramByte;
  }
  
  public void setPrn(byte paramByte)
  {
    this.mPrn = paramByte;
  }
  
  public void setPseudorangeInMeters(double paramDouble)
  {
    setFlag(32);
    this.mPseudorangeInMeters = paramDouble;
  }
  
  public void setPseudorangeRateInMetersPerSec(double paramDouble)
  {
    this.mPseudorangeRateInMetersPerSec = paramDouble;
  }
  
  public void setPseudorangeRateUncertaintyInMetersPerSec(double paramDouble)
  {
    this.mPseudorangeRateUncertaintyInMetersPerSec = paramDouble;
  }
  
  public void setPseudorangeUncertaintyInMeters(double paramDouble)
  {
    setFlag(64);
    this.mPseudorangeUncertaintyInMeters = paramDouble;
  }
  
  public void setReceivedGpsTowInNs(long paramLong)
  {
    this.mReceivedGpsTowInNs = paramLong;
  }
  
  public void setReceivedGpsTowUncertaintyInNs(long paramLong)
  {
    this.mReceivedGpsTowUncertaintyInNs = paramLong;
  }
  
  public void setSnrInDb(double paramDouble)
  {
    setFlag(1);
    this.mSnrInDb = paramDouble;
  }
  
  public void setState(short paramShort)
  {
    this.mState = paramShort;
  }
  
  public void setTimeFromLastBitInMs(short paramShort)
  {
    setFlag(16384);
    this.mTimeFromLastBitInMs = paramShort;
  }
  
  public void setTimeOffsetInNs(double paramDouble)
  {
    this.mTimeOffsetInNs = paramDouble;
  }
  
  public void setUsedInFix(boolean paramBoolean)
  {
    this.mUsedInFix = paramBoolean;
  }
  
  public String toString()
  {
    Object localObject3 = null;
    StringBuilder localStringBuilder = new StringBuilder("GpsMeasurement:\n");
    localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "Prn", Byte.valueOf(this.mPrn) }));
    localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "TimeOffsetInNs", Double.valueOf(this.mTimeOffsetInNs) }));
    localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "State", getStateString() }));
    localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "ReceivedGpsTowInNs", Long.valueOf(this.mReceivedGpsTowInNs), "ReceivedGpsTowUncertaintyInNs", Long.valueOf(this.mReceivedGpsTowUncertaintyInNs) }));
    localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "Cn0InDbHz", Double.valueOf(this.mCn0InDbHz) }));
    localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "PseudorangeRateInMetersPerSec", Double.valueOf(this.mPseudorangeRateInMetersPerSec), "PseudorangeRateUncertaintyInMetersPerSec", Double.valueOf(this.mPseudorangeRateUncertaintyInMetersPerSec) }));
    localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "PseudorangeRateIsCorrected", Boolean.valueOf(isPseudorangeRateCorrected()) }));
    localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "AccumulatedDeltaRangeState", getAccumulatedDeltaRangeStateString() }));
    localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "AccumulatedDeltaRangeInMeters", Double.valueOf(this.mAccumulatedDeltaRangeInMeters), "AccumulatedDeltaRangeUncertaintyInMeters", Double.valueOf(this.mAccumulatedDeltaRangeUncertaintyInMeters) }));
    Object localObject2;
    if (hasPseudorangeInMeters())
    {
      localObject1 = Double.valueOf(this.mPseudorangeInMeters);
      if (!hasPseudorangeUncertaintyInMeters()) {
        break label1048;
      }
      localObject2 = Double.valueOf(this.mPseudorangeUncertaintyInMeters);
      label374:
      localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "PseudorangeInMeters", localObject1, "PseudorangeUncertaintyInMeters", localObject2 }));
      if (!hasCodePhaseInChips()) {
        break label1053;
      }
      localObject1 = Double.valueOf(this.mCodePhaseInChips);
      label425:
      if (!hasCodePhaseUncertaintyInChips()) {
        break label1058;
      }
      localObject2 = Double.valueOf(this.mCodePhaseUncertaintyInChips);
      label440:
      localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "CodePhaseInChips", localObject1, "CodePhaseUncertaintyInChips", localObject2 }));
      if (!hasCarrierFrequencyInHz()) {
        break label1063;
      }
      localObject1 = Float.valueOf(this.mCarrierFrequencyInHz);
      label491:
      localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "CarrierFrequencyInHz", localObject1 }));
      if (!hasCarrierCycles()) {
        break label1068;
      }
      localObject1 = Long.valueOf(this.mCarrierCycles);
      label532:
      localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "CarrierCycles", localObject1 }));
      if (!hasCarrierPhase()) {
        break label1073;
      }
      localObject1 = Double.valueOf(this.mCarrierPhase);
      label573:
      if (!hasCarrierPhaseUncertainty()) {
        break label1078;
      }
      localObject2 = Double.valueOf(this.mCarrierPhaseUncertainty);
      label588:
      localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "CarrierPhase", localObject1, "CarrierPhaseUncertainty", localObject2 }));
      localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "LossOfLock", getLossOfLockString() }));
      if (!hasBitNumber()) {
        break label1083;
      }
      localObject1 = Integer.valueOf(this.mBitNumber);
      label668:
      localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "BitNumber", localObject1 }));
      if (!hasTimeFromLastBitInMs()) {
        break label1088;
      }
      localObject1 = Short.valueOf(this.mTimeFromLastBitInMs);
      label709:
      localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "TimeFromLastBitInMs", localObject1 }));
      if (!hasDopplerShiftInHz()) {
        break label1093;
      }
      localObject1 = Double.valueOf(this.mDopplerShiftInHz);
      label750:
      if (!hasDopplerShiftUncertaintyInHz()) {
        break label1098;
      }
      localObject2 = Double.valueOf(this.mDopplerShiftUncertaintyInHz);
      label765:
      localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "DopplerShiftInHz", localObject1, "DopplerShiftUncertaintyInHz", localObject2 }));
      localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "MultipathIndicator", getMultipathIndicatorString() }));
      if (!hasSnrInDb()) {
        break label1103;
      }
      localObject1 = Double.valueOf(this.mSnrInDb);
      label845:
      localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "SnrInDb", localObject1 }));
      if (!hasElevationInDeg()) {
        break label1108;
      }
      localObject1 = Double.valueOf(this.mElevationInDeg);
      label886:
      if (!hasElevationUncertaintyInDeg()) {
        break label1113;
      }
      localObject2 = Double.valueOf(this.mElevationUncertaintyInDeg);
      label901:
      localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "ElevationInDeg", localObject1, "ElevationUncertaintyInDeg", localObject2 }));
      if (!hasAzimuthInDeg()) {
        break label1118;
      }
    }
    label1048:
    label1053:
    label1058:
    label1063:
    label1068:
    label1073:
    label1078:
    label1083:
    label1088:
    label1093:
    label1098:
    label1103:
    label1108:
    label1113:
    label1118:
    for (Object localObject1 = Double.valueOf(this.mAzimuthInDeg);; localObject1 = null)
    {
      localObject2 = localObject3;
      if (hasAzimuthUncertaintyInDeg()) {
        localObject2 = Double.valueOf(this.mAzimuthUncertaintyInDeg);
      }
      localStringBuilder.append(String.format("   %-29s = %-25s   %-40s = %s\n", new Object[] { "AzimuthInDeg", localObject1, "AzimuthUncertaintyInDeg", localObject2 }));
      localStringBuilder.append(String.format("   %-29s = %s\n", new Object[] { "UsedInFix", Boolean.valueOf(this.mUsedInFix) }));
      return localStringBuilder.toString();
      localObject1 = null;
      break;
      localObject2 = null;
      break label374;
      localObject1 = null;
      break label425;
      localObject2 = null;
      break label440;
      localObject1 = null;
      break label491;
      localObject1 = null;
      break label532;
      localObject1 = null;
      break label573;
      localObject2 = null;
      break label588;
      localObject1 = null;
      break label668;
      localObject1 = null;
      break label709;
      localObject1 = null;
      break label750;
      localObject2 = null;
      break label765;
      localObject1 = null;
      break label845;
      localObject1 = null;
      break label886;
      localObject2 = null;
      break label901;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mFlags);
    paramParcel.writeByte(this.mPrn);
    paramParcel.writeDouble(this.mTimeOffsetInNs);
    paramParcel.writeInt(this.mState);
    paramParcel.writeLong(this.mReceivedGpsTowInNs);
    paramParcel.writeLong(this.mReceivedGpsTowUncertaintyInNs);
    paramParcel.writeDouble(this.mCn0InDbHz);
    paramParcel.writeDouble(this.mPseudorangeRateInMetersPerSec);
    paramParcel.writeDouble(this.mPseudorangeRateUncertaintyInMetersPerSec);
    paramParcel.writeInt(this.mAccumulatedDeltaRangeState);
    paramParcel.writeDouble(this.mAccumulatedDeltaRangeInMeters);
    paramParcel.writeDouble(this.mAccumulatedDeltaRangeUncertaintyInMeters);
    paramParcel.writeDouble(this.mPseudorangeInMeters);
    paramParcel.writeDouble(this.mPseudorangeUncertaintyInMeters);
    paramParcel.writeDouble(this.mCodePhaseInChips);
    paramParcel.writeDouble(this.mCodePhaseUncertaintyInChips);
    paramParcel.writeFloat(this.mCarrierFrequencyInHz);
    paramParcel.writeLong(this.mCarrierCycles);
    paramParcel.writeDouble(this.mCarrierPhase);
    paramParcel.writeDouble(this.mCarrierPhaseUncertainty);
    paramParcel.writeByte(this.mLossOfLock);
    paramParcel.writeInt(this.mBitNumber);
    paramParcel.writeInt(this.mTimeFromLastBitInMs);
    paramParcel.writeDouble(this.mDopplerShiftInHz);
    paramParcel.writeDouble(this.mDopplerShiftUncertaintyInHz);
    paramParcel.writeByte(this.mMultipathIndicator);
    paramParcel.writeDouble(this.mSnrInDb);
    paramParcel.writeDouble(this.mElevationInDeg);
    paramParcel.writeDouble(this.mElevationUncertaintyInDeg);
    paramParcel.writeDouble(this.mAzimuthInDeg);
    paramParcel.writeDouble(this.mAzimuthUncertaintyInDeg);
    if (this.mUsedInFix) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GpsMeasurement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */