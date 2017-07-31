package android.net;

import android.net.wifi.WifiInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.telephony.TelephonyManager;
import android.util.BackupUtils;
import android.util.BackupUtils.BadVersionException;
import com.android.internal.util.ArrayUtils;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class NetworkTemplate
  implements Parcelable
{
  private static final int BACKUP_VERSION = 1;
  public static final Parcelable.Creator<NetworkTemplate> CREATOR = new Parcelable.Creator()
  {
    public NetworkTemplate createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NetworkTemplate(paramAnonymousParcel, null);
    }
    
    public NetworkTemplate[] newArray(int paramAnonymousInt)
    {
      return new NetworkTemplate[paramAnonymousInt];
    }
  };
  public static final int MATCH_BLUETOOTH = 8;
  public static final int MATCH_ETHERNET = 5;
  @Deprecated
  public static final int MATCH_MOBILE_3G_LOWER = 2;
  @Deprecated
  public static final int MATCH_MOBILE_4G = 3;
  public static final int MATCH_MOBILE_ALL = 1;
  public static final int MATCH_MOBILE_WILDCARD = 6;
  public static final int MATCH_PROXY = 9;
  public static final int MATCH_WIFI = 4;
  public static final int MATCH_WIFI_WILDCARD = 7;
  private static boolean sForceAllNetworkTypes = false;
  private final int mMatchRule;
  private final String[] mMatchSubscriberIds;
  private final String mNetworkId;
  private final String mSubscriberId;
  
  public NetworkTemplate(int paramInt, String paramString1, String paramString2)
  {
    this(paramInt, paramString1, new String[] { paramString1 }, paramString2);
  }
  
  public NetworkTemplate(int paramInt, String paramString1, String[] paramArrayOfString, String paramString2)
  {
    this.mMatchRule = paramInt;
    this.mSubscriberId = paramString1;
    this.mMatchSubscriberIds = paramArrayOfString;
    this.mNetworkId = paramString2;
  }
  
  private NetworkTemplate(Parcel paramParcel)
  {
    this.mMatchRule = paramParcel.readInt();
    this.mSubscriberId = paramParcel.readString();
    this.mMatchSubscriberIds = paramParcel.createStringArray();
    this.mNetworkId = paramParcel.readString();
  }
  
  public static NetworkTemplate buildTemplateBluetooth()
  {
    return new NetworkTemplate(8, null, null);
  }
  
  public static NetworkTemplate buildTemplateEthernet()
  {
    return new NetworkTemplate(5, null, null);
  }
  
  @Deprecated
  public static NetworkTemplate buildTemplateMobile3gLower(String paramString)
  {
    return new NetworkTemplate(2, paramString, null);
  }
  
  @Deprecated
  public static NetworkTemplate buildTemplateMobile4g(String paramString)
  {
    return new NetworkTemplate(3, paramString, null);
  }
  
  public static NetworkTemplate buildTemplateMobileAll(String paramString)
  {
    return new NetworkTemplate(1, paramString, null);
  }
  
  public static NetworkTemplate buildTemplateMobileWildcard()
  {
    return new NetworkTemplate(6, null, null);
  }
  
  public static NetworkTemplate buildTemplateProxy()
  {
    return new NetworkTemplate(9, null, null);
  }
  
  @Deprecated
  public static NetworkTemplate buildTemplateWifi()
  {
    return buildTemplateWifiWildcard();
  }
  
  public static NetworkTemplate buildTemplateWifi(String paramString)
  {
    return new NetworkTemplate(4, null, paramString);
  }
  
  public static NetworkTemplate buildTemplateWifiWildcard()
  {
    return new NetworkTemplate(7, null, null);
  }
  
  private static void ensureSubtypeAvailable()
  {
    throw new IllegalArgumentException("Unable to enforce 3G_LOWER template on combined data.");
  }
  
  public static void forceAllNetworkTypes()
  {
    sForceAllNetworkTypes = true;
  }
  
  private static String getMatchRuleName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case 2: 
      return "MOBILE_3G_LOWER";
    case 3: 
      return "MOBILE_4G";
    case 1: 
      return "MOBILE_ALL";
    case 4: 
      return "WIFI";
    case 5: 
      return "ETHERNET";
    case 6: 
      return "MOBILE_WILDCARD";
    case 7: 
      return "WIFI_WILDCARD";
    case 8: 
      return "BLUETOOTH";
    }
    return "PROXY";
  }
  
  public static NetworkTemplate getNetworkTemplateFromBackup(DataInputStream paramDataInputStream)
    throws IOException, BackupUtils.BadVersionException
  {
    int i = paramDataInputStream.readInt();
    if ((i < 1) || (i > 1)) {
      throw new BackupUtils.BadVersionException("Unknown Backup Serialization Version");
    }
    return new NetworkTemplate(paramDataInputStream.readInt(), BackupUtils.readString(paramDataInputStream), BackupUtils.readString(paramDataInputStream));
  }
  
  private boolean matchesBluetooth(NetworkIdentity paramNetworkIdentity)
  {
    return paramNetworkIdentity.mType == 7;
  }
  
  private boolean matchesEthernet(NetworkIdentity paramNetworkIdentity)
  {
    return paramNetworkIdentity.mType == 9;
  }
  
  private boolean matchesMobile(NetworkIdentity paramNetworkIdentity)
  {
    if (paramNetworkIdentity.mType == 6) {
      return true;
    }
    if (((!sForceAllNetworkTypes) && ((paramNetworkIdentity.mType != 0) || (!paramNetworkIdentity.mMetered))) || (ArrayUtils.isEmpty(this.mMatchSubscriberIds))) {
      return false;
    }
    return ArrayUtils.contains(this.mMatchSubscriberIds, paramNetworkIdentity.mSubscriberId);
  }
  
  @Deprecated
  private boolean matchesMobile3gLower(NetworkIdentity paramNetworkIdentity)
  {
    
    if (paramNetworkIdentity.mType == 6) {
      return false;
    }
    if (matchesMobile(paramNetworkIdentity)) {}
    switch (TelephonyManager.getNetworkClass(paramNetworkIdentity.mSubType))
    {
    default: 
      return false;
    }
    return true;
  }
  
  @Deprecated
  private boolean matchesMobile4g(NetworkIdentity paramNetworkIdentity)
  {
    
    if (paramNetworkIdentity.mType == 6) {
      return true;
    }
    if (matchesMobile(paramNetworkIdentity)) {}
    switch (TelephonyManager.getNetworkClass(paramNetworkIdentity.mSubType))
    {
    default: 
      return false;
    }
    return true;
  }
  
  private boolean matchesMobileWildcard(NetworkIdentity paramNetworkIdentity)
  {
    boolean bool = true;
    if (paramNetworkIdentity.mType == 6) {
      return true;
    }
    if (!sForceAllNetworkTypes)
    {
      if (paramNetworkIdentity.mType == 0) {
        bool = paramNetworkIdentity.mMetered;
      }
    }
    else {
      return bool;
    }
    return false;
  }
  
  private boolean matchesProxy(NetworkIdentity paramNetworkIdentity)
  {
    return paramNetworkIdentity.mType == 16;
  }
  
  private boolean matchesWifi(NetworkIdentity paramNetworkIdentity)
  {
    switch (paramNetworkIdentity.mType)
    {
    default: 
      return false;
    }
    return Objects.equals(WifiInfo.removeDoubleQuotes(this.mNetworkId), WifiInfo.removeDoubleQuotes(paramNetworkIdentity.mNetworkId));
  }
  
  private boolean matchesWifiWildcard(NetworkIdentity paramNetworkIdentity)
  {
    switch (paramNetworkIdentity.mType)
    {
    default: 
      return false;
    }
    return true;
  }
  
  public static NetworkTemplate normalize(NetworkTemplate paramNetworkTemplate, String[] paramArrayOfString)
  {
    if ((paramNetworkTemplate.isMatchRuleMobile()) && (ArrayUtils.contains(paramArrayOfString, paramNetworkTemplate.mSubscriberId))) {
      return new NetworkTemplate(paramNetworkTemplate.mMatchRule, paramArrayOfString[0], paramArrayOfString, paramNetworkTemplate.mNetworkId);
    }
    return paramNetworkTemplate;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if ((paramObject instanceof NetworkTemplate))
    {
      paramObject = (NetworkTemplate)paramObject;
      boolean bool1 = bool2;
      if (this.mMatchRule == ((NetworkTemplate)paramObject).mMatchRule)
      {
        bool1 = bool2;
        if (Objects.equals(this.mSubscriberId, ((NetworkTemplate)paramObject).mSubscriberId)) {
          bool1 = Objects.equals(this.mNetworkId, ((NetworkTemplate)paramObject).mNetworkId);
        }
      }
      return bool1;
    }
    return false;
  }
  
  public byte[] getBytesForBackup()
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    localDataOutputStream.writeInt(1);
    localDataOutputStream.writeInt(this.mMatchRule);
    BackupUtils.writeString(localDataOutputStream, this.mSubscriberId);
    BackupUtils.writeString(localDataOutputStream, this.mNetworkId);
    return localByteArrayOutputStream.toByteArray();
  }
  
  public int getMatchRule()
  {
    return this.mMatchRule;
  }
  
  public String getNetworkId()
  {
    return this.mNetworkId;
  }
  
  public String getSubscriberId()
  {
    return this.mSubscriberId;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(this.mMatchRule), this.mSubscriberId, this.mNetworkId });
  }
  
  public boolean isMatchRuleMobile()
  {
    switch (this.mMatchRule)
    {
    case 4: 
    case 5: 
    default: 
      return false;
    }
    return true;
  }
  
  public boolean isPersistable()
  {
    switch (this.mMatchRule)
    {
    default: 
      return true;
    }
    return false;
  }
  
  public boolean matches(NetworkIdentity paramNetworkIdentity)
  {
    switch (this.mMatchRule)
    {
    default: 
      throw new IllegalArgumentException("unknown network template");
    case 1: 
      return matchesMobile(paramNetworkIdentity);
    case 2: 
      return matchesMobile3gLower(paramNetworkIdentity);
    case 3: 
      return matchesMobile4g(paramNetworkIdentity);
    case 4: 
      return matchesWifi(paramNetworkIdentity);
    case 5: 
      return matchesEthernet(paramNetworkIdentity);
    case 6: 
      return matchesMobileWildcard(paramNetworkIdentity);
    case 7: 
      return matchesWifiWildcard(paramNetworkIdentity);
    case 8: 
      return matchesBluetooth(paramNetworkIdentity);
    }
    return matchesProxy(paramNetworkIdentity);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("NetworkTemplate: ");
    localStringBuilder.append("matchRule=").append(getMatchRuleName(this.mMatchRule));
    if (this.mSubscriberId != null) {
      localStringBuilder.append(", subscriberId=").append(NetworkIdentity.scrubSubscriberId(this.mSubscriberId));
    }
    if (this.mMatchSubscriberIds != null) {
      localStringBuilder.append(", matchSubscriberIds=").append(Arrays.toString(NetworkIdentity.scrubSubscriberId(this.mMatchSubscriberIds)));
    }
    if (this.mNetworkId != null) {
      localStringBuilder.append(", networkId=").append(this.mNetworkId);
    }
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mMatchRule);
    paramParcel.writeString(this.mSubscriberId);
    paramParcel.writeStringArray(this.mMatchSubscriberIds);
    paramParcel.writeString(this.mNetworkId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkTemplate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */