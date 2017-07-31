package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public final class NetworkCapabilities
  implements Parcelable
{
  public static final Parcelable.Creator<NetworkCapabilities> CREATOR = new Parcelable.Creator()
  {
    public NetworkCapabilities createFromParcel(Parcel paramAnonymousParcel)
    {
      NetworkCapabilities localNetworkCapabilities = new NetworkCapabilities();
      NetworkCapabilities.-set2(localNetworkCapabilities, paramAnonymousParcel.readLong());
      NetworkCapabilities.-set5(localNetworkCapabilities, paramAnonymousParcel.readLong());
      NetworkCapabilities.-set1(localNetworkCapabilities, paramAnonymousParcel.readInt());
      NetworkCapabilities.-set0(localNetworkCapabilities, paramAnonymousParcel.readInt());
      NetworkCapabilities.-set3(localNetworkCapabilities, paramAnonymousParcel.readString());
      NetworkCapabilities.-set4(localNetworkCapabilities, paramAnonymousParcel.readInt());
      return localNetworkCapabilities;
    }
    
    public NetworkCapabilities[] newArray(int paramAnonymousInt)
    {
      return new NetworkCapabilities[paramAnonymousInt];
    }
  };
  private static final long DEFAULT_CAPABILITIES = 57344L;
  public static final String MATCH_ALL_REQUESTS_NETWORK_SPECIFIER = "*";
  private static final int MAX_NET_CAPABILITY = 18;
  private static final int MAX_TRANSPORT = 4;
  private static final int MIN_NET_CAPABILITY = 0;
  private static final int MIN_TRANSPORT = 0;
  private static final long MUTABLE_CAPABILITIES = 475136L;
  public static final int NET_CAPABILITY_CAPTIVE_PORTAL = 17;
  public static final int NET_CAPABILITY_CBS = 5;
  public static final int NET_CAPABILITY_DUN = 2;
  public static final int NET_CAPABILITY_EIMS = 10;
  public static final int NET_CAPABILITY_FOREGROUND = 18;
  public static final int NET_CAPABILITY_FOTA = 3;
  public static final int NET_CAPABILITY_IA = 7;
  public static final int NET_CAPABILITY_IMS = 4;
  public static final int NET_CAPABILITY_INTERNET = 12;
  public static final int NET_CAPABILITY_MMS = 0;
  public static final int NET_CAPABILITY_NOT_METERED = 11;
  public static final int NET_CAPABILITY_NOT_RESTRICTED = 13;
  public static final int NET_CAPABILITY_NOT_VPN = 15;
  public static final int NET_CAPABILITY_RCS = 8;
  public static final int NET_CAPABILITY_SUPL = 1;
  public static final int NET_CAPABILITY_TRUSTED = 14;
  public static final int NET_CAPABILITY_VALIDATED = 16;
  public static final int NET_CAPABILITY_WIFI_P2P = 6;
  public static final int NET_CAPABILITY_XCAP = 9;
  private static final long NON_REQUESTABLE_CAPABILITIES = 458752L;
  private static final long RESTRICTED_CAPABILITIES = 1980L;
  public static final int SIGNAL_STRENGTH_UNSPECIFIED = Integer.MIN_VALUE;
  public static final int TRANSPORT_BLUETOOTH = 2;
  public static final int TRANSPORT_CELLULAR = 0;
  public static final int TRANSPORT_ETHERNET = 3;
  public static final int TRANSPORT_VPN = 4;
  public static final int TRANSPORT_WIFI = 1;
  private int mLinkDownBandwidthKbps;
  private int mLinkUpBandwidthKbps;
  private long mNetworkCapabilities;
  private String mNetworkSpecifier;
  private int mSignalStrength;
  private long mTransportTypes;
  
  public NetworkCapabilities()
  {
    clearAll();
    this.mNetworkCapabilities = 57344L;
  }
  
  public NetworkCapabilities(NetworkCapabilities paramNetworkCapabilities)
  {
    if (paramNetworkCapabilities != null)
    {
      this.mNetworkCapabilities = paramNetworkCapabilities.mNetworkCapabilities;
      this.mTransportTypes = paramNetworkCapabilities.mTransportTypes;
      this.mLinkUpBandwidthKbps = paramNetworkCapabilities.mLinkUpBandwidthKbps;
      this.mLinkDownBandwidthKbps = paramNetworkCapabilities.mLinkDownBandwidthKbps;
      this.mNetworkSpecifier = paramNetworkCapabilities.mNetworkSpecifier;
      this.mSignalStrength = paramNetworkCapabilities.mSignalStrength;
    }
  }
  
  private void combineLinkBandwidths(NetworkCapabilities paramNetworkCapabilities)
  {
    this.mLinkUpBandwidthKbps = Math.max(this.mLinkUpBandwidthKbps, paramNetworkCapabilities.mLinkUpBandwidthKbps);
    this.mLinkDownBandwidthKbps = Math.max(this.mLinkDownBandwidthKbps, paramNetworkCapabilities.mLinkDownBandwidthKbps);
  }
  
  private void combineNetCapabilities(NetworkCapabilities paramNetworkCapabilities)
  {
    this.mNetworkCapabilities |= paramNetworkCapabilities.mNetworkCapabilities;
  }
  
  private void combineSignalStrength(NetworkCapabilities paramNetworkCapabilities)
  {
    this.mSignalStrength = Math.max(this.mSignalStrength, paramNetworkCapabilities.mSignalStrength);
  }
  
  private void combineSpecifiers(NetworkCapabilities paramNetworkCapabilities)
  {
    paramNetworkCapabilities = paramNetworkCapabilities.getNetworkSpecifier();
    if (TextUtils.isEmpty(paramNetworkCapabilities)) {
      return;
    }
    if (!TextUtils.isEmpty(this.mNetworkSpecifier)) {
      throw new IllegalStateException("Can't combine two networkSpecifiers");
    }
    setNetworkSpecifier(paramNetworkCapabilities);
  }
  
  private void combineTransportTypes(NetworkCapabilities paramNetworkCapabilities)
  {
    this.mTransportTypes |= paramNetworkCapabilities.mTransportTypes;
  }
  
  private int[] enumerateBits(long paramLong)
  {
    int[] arrayOfInt = new int[Long.bitCount(paramLong)];
    int j = 0;
    int i = 0;
    if (paramLong > 0L)
    {
      if ((paramLong & 1L) != 1L) {
        break label57;
      }
      int k = i + 1;
      arrayOfInt[i] = j;
      i = k;
    }
    label57:
    for (;;)
    {
      paramLong >>= 1;
      j += 1;
      break;
      return arrayOfInt;
    }
  }
  
  private boolean equalsLinkBandwidths(NetworkCapabilities paramNetworkCapabilities)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mLinkUpBandwidthKbps == paramNetworkCapabilities.mLinkUpBandwidthKbps)
    {
      bool1 = bool2;
      if (this.mLinkDownBandwidthKbps == paramNetworkCapabilities.mLinkDownBandwidthKbps) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean equalsNetCapabilitiesImmutable(NetworkCapabilities paramNetworkCapabilities)
  {
    return (this.mNetworkCapabilities & 0xFFFFFFFFFFF8BFFF) == (paramNetworkCapabilities.mNetworkCapabilities & 0xFFFFFFFFFFF8BFFF);
  }
  
  private boolean equalsNetCapabilitiesRequestable(NetworkCapabilities paramNetworkCapabilities)
  {
    return (this.mNetworkCapabilities & 0xFFFFFFFFFFF8FFFF) == (paramNetworkCapabilities.mNetworkCapabilities & 0xFFFFFFFFFFF8FFFF);
  }
  
  private boolean equalsSignalStrength(NetworkCapabilities paramNetworkCapabilities)
  {
    return this.mSignalStrength == paramNetworkCapabilities.mSignalStrength;
  }
  
  private boolean equalsSpecifier(NetworkCapabilities paramNetworkCapabilities)
  {
    if (TextUtils.isEmpty(this.mNetworkSpecifier)) {
      return TextUtils.isEmpty(paramNetworkCapabilities.mNetworkSpecifier);
    }
    return this.mNetworkSpecifier.equals(paramNetworkCapabilities.mNetworkSpecifier);
  }
  
  private boolean satisfiedByLinkBandwidths(NetworkCapabilities paramNetworkCapabilities)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mLinkUpBandwidthKbps <= paramNetworkCapabilities.mLinkUpBandwidthKbps)
    {
      bool1 = bool2;
      if (this.mLinkDownBandwidthKbps <= paramNetworkCapabilities.mLinkDownBandwidthKbps) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean satisfiedByNetCapabilities(NetworkCapabilities paramNetworkCapabilities, boolean paramBoolean)
  {
    long l2 = this.mNetworkCapabilities;
    long l1 = l2;
    if (paramBoolean) {
      l1 = l2 & 0xFFFFFFFFFFF8BFFF;
    }
    return (paramNetworkCapabilities.mNetworkCapabilities & l1) == l1;
  }
  
  private boolean satisfiedByNetworkCapabilities(NetworkCapabilities paramNetworkCapabilities, boolean paramBoolean)
  {
    if ((paramNetworkCapabilities != null) && (satisfiedByNetCapabilities(paramNetworkCapabilities, paramBoolean)) && (satisfiedByTransportTypes(paramNetworkCapabilities)) && ((paramBoolean) || (satisfiedByLinkBandwidths(paramNetworkCapabilities))) && (satisfiedBySpecifier(paramNetworkCapabilities)))
    {
      if (!paramBoolean) {
        return satisfiedBySignalStrength(paramNetworkCapabilities);
      }
      return true;
    }
    return false;
  }
  
  private boolean satisfiedBySignalStrength(NetworkCapabilities paramNetworkCapabilities)
  {
    return this.mSignalStrength <= paramNetworkCapabilities.mSignalStrength;
  }
  
  private boolean satisfiedBySpecifier(NetworkCapabilities paramNetworkCapabilities)
  {
    if ((!TextUtils.isEmpty(this.mNetworkSpecifier)) && ((TextUtils.isEmpty(this.mNetworkSpecifier)) || (!this.mNetworkSpecifier.equals(paramNetworkCapabilities.mNetworkSpecifier)))) {
      return "*".equals(paramNetworkCapabilities.mNetworkSpecifier);
    }
    return true;
  }
  
  private boolean satisfiedByTransportTypes(NetworkCapabilities paramNetworkCapabilities)
  {
    return (this.mTransportTypes == 0L) || ((this.mTransportTypes & paramNetworkCapabilities.mTransportTypes) != 0L);
  }
  
  public static String transportNamesOf(int[] paramArrayOfInt)
  {
    Object localObject2 = "";
    int i = 0;
    if (i < paramArrayOfInt.length)
    {
      Object localObject1;
      switch (paramArrayOfInt[i])
      {
      default: 
        localObject1 = localObject2;
      }
      for (;;)
      {
        int j = i + 1;
        i = j;
        localObject2 = localObject1;
        if (j >= paramArrayOfInt.length) {
          break;
        }
        localObject2 = (String)localObject1 + "|";
        i = j;
        break;
        localObject1 = (String)localObject2 + "CELLULAR";
        continue;
        localObject1 = (String)localObject2 + "WIFI";
        continue;
        localObject1 = (String)localObject2 + "BLUETOOTH";
        continue;
        localObject1 = (String)localObject2 + "ETHERNET";
        continue;
        localObject1 = (String)localObject2 + "VPN";
      }
    }
    return (String)localObject2;
  }
  
  public NetworkCapabilities addCapability(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 18)) {
      throw new IllegalArgumentException("NetworkCapability out of range");
    }
    this.mNetworkCapabilities |= 1 << paramInt;
    return this;
  }
  
  public NetworkCapabilities addTransportType(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 4)) {
      throw new IllegalArgumentException("TransportType out of range");
    }
    this.mTransportTypes |= 1 << paramInt;
    setNetworkSpecifier(this.mNetworkSpecifier);
    return this;
  }
  
  public void clearAll()
  {
    this.mTransportTypes = 0L;
    this.mNetworkCapabilities = 0L;
    this.mLinkDownBandwidthKbps = 0;
    this.mLinkUpBandwidthKbps = 0;
    this.mNetworkSpecifier = null;
    this.mSignalStrength = Integer.MIN_VALUE;
  }
  
  public void combineCapabilities(NetworkCapabilities paramNetworkCapabilities)
  {
    combineNetCapabilities(paramNetworkCapabilities);
    combineTransportTypes(paramNetworkCapabilities);
    combineLinkBandwidths(paramNetworkCapabilities);
    combineSpecifiers(paramNetworkCapabilities);
    combineSignalStrength(paramNetworkCapabilities);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String describeFirstNonRequestableCapability()
  {
    if (hasCapability(16)) {
      return "NET_CAPABILITY_VALIDATED";
    }
    if (hasCapability(17)) {
      return "NET_CAPABILITY_CAPTIVE_PORTAL";
    }
    if (hasCapability(18)) {
      return "NET_CAPABILITY_FOREGROUND";
    }
    if ((this.mNetworkCapabilities & 0x70000) != 0L) {
      return "unknown non-requestable capabilities " + Long.toHexString(this.mNetworkCapabilities);
    }
    if ((this.mLinkUpBandwidthKbps != 0) || (this.mLinkDownBandwidthKbps != 0)) {
      return "link bandwidth";
    }
    if (hasSignalStrength()) {
      return "signalStrength";
    }
    return null;
  }
  
  public boolean equalImmutableCapabilities(NetworkCapabilities paramNetworkCapabilities)
  {
    boolean bool2 = false;
    if (paramNetworkCapabilities == null) {
      return false;
    }
    boolean bool1 = bool2;
    if (equalsNetCapabilitiesImmutable(paramNetworkCapabilities))
    {
      bool1 = bool2;
      if (equalsTransportTypes(paramNetworkCapabilities)) {
        bool1 = equalsSpecifier(paramNetworkCapabilities);
      }
    }
    return bool1;
  }
  
  public boolean equalRequestableCapabilities(NetworkCapabilities paramNetworkCapabilities)
  {
    boolean bool2 = false;
    if (paramNetworkCapabilities == null) {
      return false;
    }
    boolean bool1 = bool2;
    if (equalsNetCapabilitiesRequestable(paramNetworkCapabilities))
    {
      bool1 = bool2;
      if (equalsTransportTypes(paramNetworkCapabilities)) {
        bool1 = equalsSpecifier(paramNetworkCapabilities);
      }
    }
    return bool1;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if ((paramObject == null) || (!(paramObject instanceof NetworkCapabilities))) {
      return false;
    }
    paramObject = (NetworkCapabilities)paramObject;
    boolean bool1 = bool2;
    if (equalsNetCapabilities((NetworkCapabilities)paramObject))
    {
      bool1 = bool2;
      if (equalsTransportTypes((NetworkCapabilities)paramObject))
      {
        bool1 = bool2;
        if (equalsLinkBandwidths((NetworkCapabilities)paramObject))
        {
          bool1 = bool2;
          if (equalsSignalStrength((NetworkCapabilities)paramObject)) {
            bool1 = equalsSpecifier((NetworkCapabilities)paramObject);
          }
        }
      }
    }
    return bool1;
  }
  
  public boolean equalsNetCapabilities(NetworkCapabilities paramNetworkCapabilities)
  {
    return paramNetworkCapabilities.mNetworkCapabilities == this.mNetworkCapabilities;
  }
  
  public boolean equalsTransportTypes(NetworkCapabilities paramNetworkCapabilities)
  {
    return paramNetworkCapabilities.mTransportTypes == this.mTransportTypes;
  }
  
  public int[] getCapabilities()
  {
    return enumerateBits(this.mNetworkCapabilities);
  }
  
  public int getLinkDownstreamBandwidthKbps()
  {
    return this.mLinkDownBandwidthKbps;
  }
  
  public int getLinkUpstreamBandwidthKbps()
  {
    return this.mLinkUpBandwidthKbps;
  }
  
  public String getNetworkSpecifier()
  {
    return this.mNetworkSpecifier;
  }
  
  public int getSignalStrength()
  {
    return this.mSignalStrength;
  }
  
  public int[] getTransportTypes()
  {
    return enumerateBits(this.mTransportTypes);
  }
  
  public boolean hasCapability(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 18)) {
      return false;
    }
    return (this.mNetworkCapabilities & 1 << paramInt) != 0L;
  }
  
  public boolean hasSignalStrength()
  {
    return this.mSignalStrength > Integer.MIN_VALUE;
  }
  
  public boolean hasTransport(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 4)) {
      return false;
    }
    return (this.mTransportTypes & 1 << paramInt) != 0L;
  }
  
  public int hashCode()
  {
    int j = (int)(this.mNetworkCapabilities & 0xFFFFFFFFFFFFFFFF);
    int k = (int)(this.mNetworkCapabilities >> 32);
    int m = (int)(this.mTransportTypes & 0xFFFFFFFFFFFFFFFF);
    int n = (int)(this.mTransportTypes >> 32);
    int i1 = this.mLinkUpBandwidthKbps;
    int i2 = this.mLinkDownBandwidthKbps;
    if (TextUtils.isEmpty(this.mNetworkSpecifier)) {}
    for (int i = 0;; i = this.mNetworkSpecifier.hashCode() * 17) {
      return i + (i2 * 13 + (j + k * 3 + m * 5 + n * 7 + i1 * 11)) + this.mSignalStrength * 19;
    }
  }
  
  public void maybeMarkCapabilitiesRestricted()
  {
    if (((this.mNetworkCapabilities & 0xFFFFFFFFFFFF1843) == 0L) && ((this.mNetworkCapabilities & 0x7BC) != 0L)) {
      removeCapability(13);
    }
  }
  
  public NetworkCapabilities removeCapability(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 18)) {
      throw new IllegalArgumentException("NetworkCapability out of range");
    }
    this.mNetworkCapabilities &= 1 << paramInt;
    return this;
  }
  
  public NetworkCapabilities removeTransportType(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 4)) {
      throw new IllegalArgumentException("TransportType out of range");
    }
    this.mTransportTypes &= 1 << paramInt;
    setNetworkSpecifier(this.mNetworkSpecifier);
    return this;
  }
  
  public boolean satisfiedByImmutableNetworkCapabilities(NetworkCapabilities paramNetworkCapabilities)
  {
    return satisfiedByNetworkCapabilities(paramNetworkCapabilities, true);
  }
  
  public boolean satisfiedByNetworkCapabilities(NetworkCapabilities paramNetworkCapabilities)
  {
    return satisfiedByNetworkCapabilities(paramNetworkCapabilities, false);
  }
  
  public void setLinkDownstreamBandwidthKbps(int paramInt)
  {
    this.mLinkDownBandwidthKbps = paramInt;
  }
  
  public void setLinkUpstreamBandwidthKbps(int paramInt)
  {
    this.mLinkUpBandwidthKbps = paramInt;
  }
  
  public NetworkCapabilities setNetworkSpecifier(String paramString)
  {
    if ((!TextUtils.isEmpty(paramString)) && (Long.bitCount(this.mTransportTypes) != 1)) {
      throw new IllegalStateException("Must have a single transport specified to use setNetworkSpecifier");
    }
    this.mNetworkSpecifier = paramString;
    return this;
  }
  
  public void setSignalStrength(int paramInt)
  {
    this.mSignalStrength = paramInt;
  }
  
  public String toString()
  {
    Object localObject1 = getTransportTypes();
    String str1;
    Object localObject3;
    Object localObject2;
    label52:
    int i;
    if (localObject1.length > 0)
    {
      str1 = " Transports: " + transportNamesOf((int[])localObject1);
      localObject3 = getCapabilities();
      if (localObject3.length <= 0) {
        break label209;
      }
      localObject2 = " Capabilities: ";
      i = 0;
      label54:
      if (i >= localObject3.length) {
        break label691;
      }
      switch (localObject3[i])
      {
      default: 
        localObject1 = localObject2;
      }
    }
    for (;;)
    {
      int j = i + 1;
      localObject2 = localObject1;
      i = j;
      if (j >= localObject3.length) {
        break label54;
      }
      localObject2 = (String)localObject1 + "&";
      i = j;
      break label54;
      str1 = "";
      break;
      label209:
      localObject2 = "";
      break label52;
      localObject1 = (String)localObject2 + "MMS";
      continue;
      localObject1 = (String)localObject2 + "SUPL";
      continue;
      localObject1 = (String)localObject2 + "DUN";
      continue;
      localObject1 = (String)localObject2 + "FOTA";
      continue;
      localObject1 = (String)localObject2 + "IMS";
      continue;
      localObject1 = (String)localObject2 + "CBS";
      continue;
      localObject1 = (String)localObject2 + "WIFI_P2P";
      continue;
      localObject1 = (String)localObject2 + "IA";
      continue;
      localObject1 = (String)localObject2 + "RCS";
      continue;
      localObject1 = (String)localObject2 + "XCAP";
      continue;
      localObject1 = (String)localObject2 + "EIMS";
      continue;
      localObject1 = (String)localObject2 + "NOT_METERED";
      continue;
      localObject1 = (String)localObject2 + "INTERNET";
      continue;
      localObject1 = (String)localObject2 + "NOT_RESTRICTED";
      continue;
      localObject1 = (String)localObject2 + "TRUSTED";
      continue;
      localObject1 = (String)localObject2 + "NOT_VPN";
      continue;
      localObject1 = (String)localObject2 + "VALIDATED";
      continue;
      localObject1 = (String)localObject2 + "CAPTIVE_PORTAL";
      continue;
      localObject1 = (String)localObject2 + "FOREGROUND";
    }
    label691:
    label766:
    String str2;
    if (this.mLinkUpBandwidthKbps > 0)
    {
      localObject1 = " LinkUpBandwidth>=" + this.mLinkUpBandwidthKbps + "Kbps";
      if (this.mLinkDownBandwidthKbps <= 0) {
        break label867;
      }
      localObject3 = " LinkDnBandwidth>=" + this.mLinkDownBandwidthKbps + "Kbps";
      if (this.mNetworkSpecifier != null) {
        break label874;
      }
      str2 = "";
      label777:
      if (!hasSignalStrength()) {
        break label908;
      }
    }
    label867:
    label874:
    label908:
    for (String str3 = " SignalStrength: " + this.mSignalStrength;; str3 = "")
    {
      return "[" + str1 + (String)localObject2 + (String)localObject1 + (String)localObject3 + str2 + str3 + "]";
      localObject1 = "";
      break;
      localObject3 = "";
      break label766;
      str2 = " Specifier: <" + this.mNetworkSpecifier + ">";
      break label777;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.mNetworkCapabilities);
    paramParcel.writeLong(this.mTransportTypes);
    paramParcel.writeInt(this.mLinkUpBandwidthKbps);
    paramParcel.writeInt(this.mLinkDownBandwidthKbps);
    paramParcel.writeString(this.mNetworkSpecifier);
    paramParcel.writeInt(this.mSignalStrength);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkCapabilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */