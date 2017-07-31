package android.net.wifi;

import android.net.IpConfiguration;
import android.net.IpConfiguration.IpAssignment;
import android.net.IpConfiguration.ProxySettings;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.BackupUtils;
import android.util.BackupUtils.BadVersionException;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;

public class WifiConfiguration
  implements Parcelable
{
  public static final int AP_BAND_2GHZ = 0;
  public static final int AP_BAND_5GHZ = 1;
  private static final int BACKUP_VERSION = 2;
  public static final Parcelable.Creator<WifiConfiguration> CREATOR = new Parcelable.Creator()
  {
    public WifiConfiguration createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool2 = true;
      WifiConfiguration localWifiConfiguration = new WifiConfiguration();
      localWifiConfiguration.networkId = paramAnonymousParcel.readInt();
      localWifiConfiguration.status = paramAnonymousParcel.readInt();
      WifiConfiguration.-get0(localWifiConfiguration).readFromParcel(paramAnonymousParcel);
      localWifiConfiguration.SSID = paramAnonymousParcel.readString();
      localWifiConfiguration.BSSID = paramAnonymousParcel.readString();
      localWifiConfiguration.apBand = paramAnonymousParcel.readInt();
      localWifiConfiguration.apChannel = paramAnonymousParcel.readInt();
      localWifiConfiguration.FQDN = paramAnonymousParcel.readString();
      localWifiConfiguration.providerFriendlyName = paramAnonymousParcel.readString();
      int j = paramAnonymousParcel.readInt();
      localWifiConfiguration.roamingConsortiumIds = new long[j];
      int i = 0;
      while (i < j)
      {
        localWifiConfiguration.roamingConsortiumIds[i] = paramAnonymousParcel.readLong();
        i += 1;
      }
      localWifiConfiguration.preSharedKey = paramAnonymousParcel.readString();
      localWifiConfiguration.wapiASCert = paramAnonymousParcel.readString();
      localWifiConfiguration.wapiUserCert = paramAnonymousParcel.readString();
      localWifiConfiguration.wapiPsk = paramAnonymousParcel.readString();
      localWifiConfiguration.wapiPskType = paramAnonymousParcel.readInt();
      i = 0;
      while (i < localWifiConfiguration.wepKeys.length)
      {
        localWifiConfiguration.wepKeys[i] = paramAnonymousParcel.readString();
        i += 1;
      }
      localWifiConfiguration.wepTxKeyIndex = paramAnonymousParcel.readInt();
      localWifiConfiguration.priority = paramAnonymousParcel.readInt();
      if (paramAnonymousParcel.readInt() != 0)
      {
        bool1 = true;
        localWifiConfiguration.hiddenSSID = bool1;
        if (paramAnonymousParcel.readInt() == 0) {
          break label711;
        }
        bool1 = true;
        label251:
        localWifiConfiguration.requirePMF = bool1;
        localWifiConfiguration.updateIdentifier = paramAnonymousParcel.readString();
        localWifiConfiguration.allowedKeyManagement = WifiConfiguration.-wrap0(paramAnonymousParcel);
        localWifiConfiguration.allowedProtocols = WifiConfiguration.-wrap0(paramAnonymousParcel);
        localWifiConfiguration.allowedAuthAlgorithms = WifiConfiguration.-wrap0(paramAnonymousParcel);
        localWifiConfiguration.allowedPairwiseCiphers = WifiConfiguration.-wrap0(paramAnonymousParcel);
        localWifiConfiguration.allowedGroupCiphers = WifiConfiguration.-wrap0(paramAnonymousParcel);
        localWifiConfiguration.enterpriseConfig = ((WifiEnterpriseConfig)paramAnonymousParcel.readParcelable(null));
        WifiConfiguration.-set0(localWifiConfiguration, (IpConfiguration)paramAnonymousParcel.readParcelable(null));
        localWifiConfiguration.dhcpServer = paramAnonymousParcel.readString();
        localWifiConfiguration.defaultGwMacAddress = paramAnonymousParcel.readString();
        if (paramAnonymousParcel.readInt() == 0) {
          break label717;
        }
        bool1 = true;
        label367:
        localWifiConfiguration.selfAdded = bool1;
        if (paramAnonymousParcel.readInt() == 0) {
          break label723;
        }
        bool1 = true;
        label384:
        localWifiConfiguration.didSelfAdd = bool1;
        if (paramAnonymousParcel.readInt() == 0) {
          break label729;
        }
        bool1 = true;
        label401:
        localWifiConfiguration.validatedInternetAccess = bool1;
        if (paramAnonymousParcel.readInt() == 0) {
          break label735;
        }
        bool1 = true;
        label418:
        localWifiConfiguration.ephemeral = bool1;
        if (paramAnonymousParcel.readInt() == 0) {
          break label741;
        }
        bool1 = true;
        label435:
        localWifiConfiguration.meteredHint = bool1;
        if (paramAnonymousParcel.readInt() == 0) {
          break label747;
        }
        bool1 = true;
        label452:
        localWifiConfiguration.useExternalScores = bool1;
        localWifiConfiguration.creatorUid = paramAnonymousParcel.readInt();
        localWifiConfiguration.lastConnectUid = paramAnonymousParcel.readInt();
        localWifiConfiguration.lastUpdateUid = paramAnonymousParcel.readInt();
        localWifiConfiguration.creatorName = paramAnonymousParcel.readString();
        localWifiConfiguration.lastUpdateName = paramAnonymousParcel.readString();
        localWifiConfiguration.lastConnectionFailure = paramAnonymousParcel.readLong();
        localWifiConfiguration.lastRoamingFailure = paramAnonymousParcel.readLong();
        localWifiConfiguration.lastRoamingFailureReason = paramAnonymousParcel.readInt();
        localWifiConfiguration.roamingFailureBlackListTimeMilli = paramAnonymousParcel.readLong();
        localWifiConfiguration.numScorerOverride = paramAnonymousParcel.readInt();
        localWifiConfiguration.numScorerOverrideAndSwitchedNetwork = paramAnonymousParcel.readInt();
        localWifiConfiguration.numAssociation = paramAnonymousParcel.readInt();
        localWifiConfiguration.numUserTriggeredWifiDisableLowRSSI = paramAnonymousParcel.readInt();
        localWifiConfiguration.numUserTriggeredWifiDisableBadRSSI = paramAnonymousParcel.readInt();
        localWifiConfiguration.numUserTriggeredWifiDisableNotHighRSSI = paramAnonymousParcel.readInt();
        localWifiConfiguration.numTicksAtLowRSSI = paramAnonymousParcel.readInt();
        localWifiConfiguration.numTicksAtBadRSSI = paramAnonymousParcel.readInt();
        localWifiConfiguration.numTicksAtNotHighRSSI = paramAnonymousParcel.readInt();
        localWifiConfiguration.numUserTriggeredJoinAttempts = paramAnonymousParcel.readInt();
        localWifiConfiguration.userApproved = paramAnonymousParcel.readInt();
        localWifiConfiguration.numNoInternetAccessReports = paramAnonymousParcel.readInt();
        if (paramAnonymousParcel.readInt() == 0) {
          break label753;
        }
        bool1 = true;
        label658:
        localWifiConfiguration.noInternetAccessExpected = bool1;
        if (paramAnonymousParcel.readInt() == 0) {
          break label759;
        }
      }
      label711:
      label717:
      label723:
      label729:
      label735:
      label741:
      label747:
      label753:
      label759:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        localWifiConfiguration.shared = bool1;
        WifiConfiguration.-set1(localWifiConfiguration, paramAnonymousParcel.readString());
        localWifiConfiguration.SIMNum = paramAnonymousParcel.readInt();
        return localWifiConfiguration;
        bool1 = false;
        break;
        bool1 = false;
        break label251;
        bool1 = false;
        break label367;
        bool1 = false;
        break label384;
        bool1 = false;
        break label401;
        bool1 = false;
        break label418;
        bool1 = false;
        break label435;
        bool1 = false;
        break label452;
        bool1 = false;
        break label658;
      }
    }
    
    public WifiConfiguration[] newArray(int paramAnonymousInt)
    {
      return new WifiConfiguration[paramAnonymousInt];
    }
  };
  public static final int HOME_NETWORK_RSSI_BOOST = 5;
  public static final int INVALID_NETWORK_ID = -1;
  public static int INVALID_RSSI = 0;
  public static int ROAMING_FAILURE_AUTH_FAILURE = 0;
  public static int ROAMING_FAILURE_IP_CONFIG = 0;
  public static final String SIMNumVarName = "sim_num";
  private static final String TAG = "WifiConfiguration";
  public static final int UNKNOWN_UID = -1;
  public static final int USER_APPROVED = 1;
  public static final int USER_BANNED = 2;
  public static final int USER_PENDING = 3;
  public static final int USER_UNSPECIFIED = 0;
  public static final String bssidVarName = "bssid";
  public static final String hiddenSSIDVarName = "scan_ssid";
  public static final String pmfVarName = "ieee80211w";
  public static final String priorityVarName = "priority";
  public static final String pskVarName = "psk";
  public static final String ssidVarName = "ssid";
  public static final String updateIdentiferVarName = "update_identifier";
  public static final String wapiAsCertFileVarName = "as_cert_file";
  public static final String wapiPskTypeVarName = "wapi_key_type";
  public static final String wapiPskVarName = "wapi_psk";
  public static final String wapiUserCertFileVarName = "user_cert_file";
  public static final String[] wepKeyVarNames = { "wep_key0", "wep_key1", "wep_key2", "wep_key3" };
  public static final String wepTxKeyIdxVarName = "wep_tx_keyidx";
  public String BSSID;
  public String FQDN;
  public int SIMNum;
  public String SSID;
  public BitSet allowedAuthAlgorithms;
  public BitSet allowedGroupCiphers;
  public BitSet allowedKeyManagement;
  public BitSet allowedPairwiseCiphers;
  public BitSet allowedProtocols;
  public int apBand = 0;
  public int apChannel = 0;
  public String creationTime;
  public String creatorName;
  public int creatorUid;
  public String defaultGwMacAddress;
  public String dhcpServer;
  public boolean didSelfAdd;
  public int dtimInterval = 0;
  public WifiEnterpriseConfig enterpriseConfig;
  public boolean ephemeral;
  public boolean hiddenSSID;
  public int lastConnectUid;
  public long lastConnected;
  public long lastConnectionFailure;
  public long lastDisconnected;
  public String lastFailure;
  public long lastRoamingFailure;
  public int lastRoamingFailureReason;
  public String lastUpdateName;
  public int lastUpdateUid;
  public HashMap<String, Integer> linkedConfigurations;
  String mCachedConfigKey;
  private IpConfiguration mIpConfiguration;
  private final NetworkSelectionStatus mNetworkSelectionStatus = new NetworkSelectionStatus(null);
  private String mPasspointManagementObjectTree;
  public boolean meteredHint;
  public int networkId;
  public boolean noInternetAccessExpected;
  public int numAssociation;
  public int numNoInternetAccessReports;
  public int numScorerOverride;
  public int numScorerOverrideAndSwitchedNetwork;
  public int numTicksAtBadRSSI;
  public int numTicksAtLowRSSI;
  public int numTicksAtNotHighRSSI;
  public int numUserTriggeredJoinAttempts;
  public int numUserTriggeredWifiDisableBadRSSI;
  public int numUserTriggeredWifiDisableLowRSSI;
  public int numUserTriggeredWifiDisableNotHighRSSI;
  public String peerWifiConfiguration;
  public String preSharedKey;
  public int priority;
  public String providerFriendlyName;
  public boolean requirePMF;
  public long[] roamingConsortiumIds;
  public long roamingFailureBlackListTimeMilli = 1000L;
  public boolean selfAdded;
  public boolean shared;
  public int status;
  public String updateIdentifier;
  public String updateTime;
  public boolean useExternalScores;
  public int userApproved = 0;
  public boolean validatedInternetAccess;
  public Visibility visibility;
  public String wapiASCert;
  public String wapiPsk;
  public int wapiPskType;
  public String wapiUserCert;
  public String[] wepKeys;
  public int wepTxKeyIndex;
  
  static
  {
    INVALID_RSSI = -127;
    ROAMING_FAILURE_IP_CONFIG = 1;
    ROAMING_FAILURE_AUTH_FAILURE = 2;
  }
  
  public WifiConfiguration()
  {
    this.networkId = -1;
    this.SSID = null;
    this.BSSID = null;
    this.FQDN = null;
    this.roamingConsortiumIds = new long[0];
    this.priority = 0;
    this.hiddenSSID = false;
    this.allowedKeyManagement = new BitSet();
    this.allowedProtocols = new BitSet();
    this.allowedAuthAlgorithms = new BitSet();
    this.allowedPairwiseCiphers = new BitSet();
    this.allowedGroupCiphers = new BitSet();
    this.wepKeys = new String[4];
    int i = 0;
    while (i < this.wepKeys.length)
    {
      this.wepKeys[i] = null;
      i += 1;
    }
    this.enterpriseConfig = new WifiEnterpriseConfig();
    this.selfAdded = false;
    this.didSelfAdd = false;
    this.ephemeral = false;
    this.meteredHint = false;
    this.useExternalScores = false;
    this.validatedInternetAccess = false;
    this.mIpConfiguration = new IpConfiguration();
    this.lastUpdateUid = -1;
    this.creatorUid = -1;
    this.shared = true;
    this.dtimInterval = 0;
    this.SIMNum = 0;
    this.wapiPskType = 0;
  }
  
  public WifiConfiguration(WifiConfiguration paramWifiConfiguration)
  {
    if (paramWifiConfiguration != null)
    {
      this.networkId = paramWifiConfiguration.networkId;
      this.status = paramWifiConfiguration.status;
      this.SSID = paramWifiConfiguration.SSID;
      this.BSSID = paramWifiConfiguration.BSSID;
      this.FQDN = paramWifiConfiguration.FQDN;
      this.roamingConsortiumIds = ((long[])paramWifiConfiguration.roamingConsortiumIds.clone());
      this.providerFriendlyName = paramWifiConfiguration.providerFriendlyName;
      this.preSharedKey = paramWifiConfiguration.preSharedKey;
      this.mNetworkSelectionStatus.copy(paramWifiConfiguration.getNetworkSelectionStatus());
      this.apBand = paramWifiConfiguration.apBand;
      this.apChannel = paramWifiConfiguration.apChannel;
      this.wepKeys = new String[4];
      int i = 0;
      while (i < this.wepKeys.length)
      {
        this.wepKeys[i] = paramWifiConfiguration.wepKeys[i];
        i += 1;
      }
      this.wepTxKeyIndex = paramWifiConfiguration.wepTxKeyIndex;
      this.priority = paramWifiConfiguration.priority;
      this.hiddenSSID = paramWifiConfiguration.hiddenSSID;
      this.allowedKeyManagement = ((BitSet)paramWifiConfiguration.allowedKeyManagement.clone());
      this.allowedProtocols = ((BitSet)paramWifiConfiguration.allowedProtocols.clone());
      this.allowedAuthAlgorithms = ((BitSet)paramWifiConfiguration.allowedAuthAlgorithms.clone());
      this.allowedPairwiseCiphers = ((BitSet)paramWifiConfiguration.allowedPairwiseCiphers.clone());
      this.allowedGroupCiphers = ((BitSet)paramWifiConfiguration.allowedGroupCiphers.clone());
      this.enterpriseConfig = new WifiEnterpriseConfig(paramWifiConfiguration.enterpriseConfig);
      this.defaultGwMacAddress = paramWifiConfiguration.defaultGwMacAddress;
      this.mIpConfiguration = new IpConfiguration(paramWifiConfiguration.mIpConfiguration);
      if ((paramWifiConfiguration.linkedConfigurations != null) && (paramWifiConfiguration.linkedConfigurations.size() > 0))
      {
        this.linkedConfigurations = new HashMap();
        this.linkedConfigurations.putAll(paramWifiConfiguration.linkedConfigurations);
      }
      this.mCachedConfigKey = null;
      this.selfAdded = paramWifiConfiguration.selfAdded;
      this.validatedInternetAccess = paramWifiConfiguration.validatedInternetAccess;
      this.ephemeral = paramWifiConfiguration.ephemeral;
      this.meteredHint = paramWifiConfiguration.meteredHint;
      this.useExternalScores = paramWifiConfiguration.useExternalScores;
      if (paramWifiConfiguration.visibility != null) {
        this.visibility = new Visibility(paramWifiConfiguration.visibility);
      }
      this.lastFailure = paramWifiConfiguration.lastFailure;
      this.didSelfAdd = paramWifiConfiguration.didSelfAdd;
      this.lastConnectUid = paramWifiConfiguration.lastConnectUid;
      this.lastUpdateUid = paramWifiConfiguration.lastUpdateUid;
      this.creatorUid = paramWifiConfiguration.creatorUid;
      this.creatorName = paramWifiConfiguration.creatorName;
      this.lastUpdateName = paramWifiConfiguration.lastUpdateName;
      this.peerWifiConfiguration = paramWifiConfiguration.peerWifiConfiguration;
      this.lastConnected = paramWifiConfiguration.lastConnected;
      this.lastDisconnected = paramWifiConfiguration.lastDisconnected;
      this.lastConnectionFailure = paramWifiConfiguration.lastConnectionFailure;
      this.lastRoamingFailure = paramWifiConfiguration.lastRoamingFailure;
      this.lastRoamingFailureReason = paramWifiConfiguration.lastRoamingFailureReason;
      this.roamingFailureBlackListTimeMilli = paramWifiConfiguration.roamingFailureBlackListTimeMilli;
      this.numScorerOverride = paramWifiConfiguration.numScorerOverride;
      this.numScorerOverrideAndSwitchedNetwork = paramWifiConfiguration.numScorerOverrideAndSwitchedNetwork;
      this.numAssociation = paramWifiConfiguration.numAssociation;
      this.numUserTriggeredWifiDisableLowRSSI = paramWifiConfiguration.numUserTriggeredWifiDisableLowRSSI;
      this.numUserTriggeredWifiDisableBadRSSI = paramWifiConfiguration.numUserTriggeredWifiDisableBadRSSI;
      this.numUserTriggeredWifiDisableNotHighRSSI = paramWifiConfiguration.numUserTriggeredWifiDisableNotHighRSSI;
      this.numTicksAtLowRSSI = paramWifiConfiguration.numTicksAtLowRSSI;
      this.numTicksAtBadRSSI = paramWifiConfiguration.numTicksAtBadRSSI;
      this.numTicksAtNotHighRSSI = paramWifiConfiguration.numTicksAtNotHighRSSI;
      this.numUserTriggeredJoinAttempts = paramWifiConfiguration.numUserTriggeredJoinAttempts;
      this.userApproved = paramWifiConfiguration.userApproved;
      this.numNoInternetAccessReports = paramWifiConfiguration.numNoInternetAccessReports;
      this.noInternetAccessExpected = paramWifiConfiguration.noInternetAccessExpected;
      this.creationTime = paramWifiConfiguration.creationTime;
      this.updateTime = paramWifiConfiguration.updateTime;
      this.shared = paramWifiConfiguration.shared;
      this.SIMNum = paramWifiConfiguration.SIMNum;
      this.wapiPskType = paramWifiConfiguration.wapiPskType;
    }
  }
  
  public static WifiConfiguration getWifiConfigFromBackup(DataInputStream paramDataInputStream)
    throws IOException, BackupUtils.BadVersionException
  {
    WifiConfiguration localWifiConfiguration = new WifiConfiguration();
    int i = paramDataInputStream.readInt();
    if ((i < 1) || (i > 2)) {
      throw new BackupUtils.BadVersionException("Unknown Backup Serialization Version");
    }
    if (i == 1) {
      return null;
    }
    localWifiConfiguration.SSID = BackupUtils.readString(paramDataInputStream);
    localWifiConfiguration.apBand = paramDataInputStream.readInt();
    localWifiConfiguration.apChannel = paramDataInputStream.readInt();
    localWifiConfiguration.preSharedKey = BackupUtils.readString(paramDataInputStream);
    localWifiConfiguration.allowedKeyManagement.set(paramDataInputStream.readInt());
    return localWifiConfiguration;
  }
  
  private static BitSet readBitSet(Parcel paramParcel)
  {
    int j = paramParcel.readInt();
    BitSet localBitSet = new BitSet();
    int i = 0;
    while (i < j)
    {
      localBitSet.set(paramParcel.readInt());
      i += 1;
    }
    return localBitSet;
  }
  
  private String trimStringForKeyId(String paramString)
  {
    return paramString.replace("\"", "").replace(" ", "");
  }
  
  public static String userApprovedAsString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "INVALID";
    case 1: 
      return "USER_APPROVED";
    case 2: 
      return "USER_BANNED";
    }
    return "USER_UNSPECIFIED";
  }
  
  private static void writeBitSet(Parcel paramParcel, BitSet paramBitSet)
  {
    int i = -1;
    paramParcel.writeInt(paramBitSet.cardinality());
    for (;;)
    {
      i = paramBitSet.nextSetBit(i + 1);
      if (i == -1) {
        break;
      }
      paramParcel.writeInt(i);
    }
  }
  
  public String configKey()
  {
    return configKey(false);
  }
  
  public String configKey(boolean paramBoolean)
  {
    Object localObject1;
    if ((paramBoolean) && (this.mCachedConfigKey != null)) {
      localObject1 = this.mCachedConfigKey;
    }
    Object localObject2;
    do
    {
      return (String)localObject1;
      if (this.providerFriendlyName == null) {
        break;
      }
      localObject2 = this.FQDN + KeyMgmt.strings[2];
      localObject1 = localObject2;
    } while (this.shared);
    return (String)localObject2 + "-" + Integer.toString(UserHandle.getUserId(this.creatorUid));
    if (this.allowedKeyManagement.get(1)) {
      localObject1 = this.SSID + KeyMgmt.strings[1];
    }
    for (;;)
    {
      localObject2 = localObject1;
      if (!this.shared) {
        localObject2 = (String)localObject1 + "-" + Integer.toString(UserHandle.getUserId(this.creatorUid));
      }
      this.mCachedConfigKey = ((String)localObject2);
      return (String)localObject2;
      if ((this.allowedKeyManagement.get(2)) || (this.allowedKeyManagement.get(3))) {
        localObject1 = this.SSID + KeyMgmt.strings[2];
      } else if (this.wepKeys[0] != null) {
        localObject1 = this.SSID + "WEP";
      } else if (this.allowedKeyManagement.get(6)) {
        localObject1 = this.SSID + KeyMgmt.strings[6];
      } else if (this.allowedKeyManagement.get(7)) {
        localObject1 = this.SSID + KeyMgmt.strings[7];
      } else {
        localObject1 = this.SSID + KeyMgmt.strings[0];
      }
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getAuthType()
  {
    if (this.allowedKeyManagement.cardinality() > 1)
    {
      Log.e("WifiConfiguration", "WAPI: debug: getAuthType(): allowedKeyManagement.cardinality() " + this.allowedKeyManagement.cardinality());
      if ((this.allowedKeyManagement.get(6)) && (this.allowedKeyManagement.get(7)))
      {
        if (this.allowedKeyManagement.cardinality() != 4) {
          throw new IllegalStateException("More than one auth type set");
        }
      }
      else {
        throw new IllegalStateException("More than one auth type set");
      }
    }
    if (this.allowedKeyManagement.get(1)) {
      return 1;
    }
    if (this.allowedKeyManagement.get(4)) {
      return 4;
    }
    if (this.allowedKeyManagement.get(2)) {
      return 2;
    }
    if (this.allowedKeyManagement.get(3)) {
      return 3;
    }
    if (this.allowedKeyManagement.get(6)) {
      return 6;
    }
    if (this.allowedKeyManagement.get(7)) {
      return 7;
    }
    return 0;
  }
  
  public byte[] getBytesForBackup()
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    localDataOutputStream.writeInt(2);
    BackupUtils.writeString(localDataOutputStream, this.SSID);
    localDataOutputStream.writeInt(this.apBand);
    localDataOutputStream.writeInt(this.apChannel);
    BackupUtils.writeString(localDataOutputStream, this.preSharedKey);
    localDataOutputStream.writeInt(getAuthType());
    return localByteArrayOutputStream.toByteArray();
  }
  
  public ProxyInfo getHttpProxy()
  {
    return this.mIpConfiguration.httpProxy;
  }
  
  public IpConfiguration.IpAssignment getIpAssignment()
  {
    return this.mIpConfiguration.ipAssignment;
  }
  
  public IpConfiguration getIpConfiguration()
  {
    return this.mIpConfiguration;
  }
  
  public String getKeyIdForCredentials(WifiConfiguration paramWifiConfiguration)
  {
    Object localObject3 = null;
    Object localObject1 = null;
    try
    {
      if (TextUtils.isEmpty(this.SSID)) {
        this.SSID = paramWifiConfiguration.SSID;
      }
      if (this.allowedKeyManagement.cardinality() == 0) {
        this.allowedKeyManagement = paramWifiConfiguration.allowedKeyManagement;
      }
      if (this.allowedKeyManagement.get(2)) {
        localObject1 = KeyMgmt.strings[2];
      }
      if (this.allowedKeyManagement.get(5)) {
        localObject1 = KeyMgmt.strings[5];
      }
      localObject2 = localObject1;
      if (this.allowedKeyManagement.get(3)) {
        localObject2 = (String)localObject1 + KeyMgmt.strings[3];
      }
      if (TextUtils.isEmpty((CharSequence)localObject2)) {
        throw new IllegalStateException("Not an EAP network");
      }
    }
    catch (NullPointerException paramWifiConfiguration)
    {
      throw new IllegalStateException("Invalid config details");
    }
    Object localObject2 = new StringBuilder().append(trimStringForKeyId(this.SSID)).append("_").append((String)localObject2).append("_");
    WifiEnterpriseConfig localWifiEnterpriseConfig = this.enterpriseConfig;
    localObject1 = localObject3;
    if (paramWifiConfiguration != null) {
      localObject1 = paramWifiConfiguration.enterpriseConfig;
    }
    paramWifiConfiguration = trimStringForKeyId(localWifiEnterpriseConfig.getKeyId((WifiEnterpriseConfig)localObject1));
    return paramWifiConfiguration;
  }
  
  public String getMoTree()
  {
    return this.mPasspointManagementObjectTree;
  }
  
  public NetworkSelectionStatus getNetworkSelectionStatus()
  {
    return this.mNetworkSelectionStatus;
  }
  
  public String getPrintableSsid()
  {
    if (this.SSID == null) {
      return "";
    }
    int i = this.SSID.length();
    if ((i > 2) && (this.SSID.charAt(0) == '"') && (this.SSID.charAt(i - 1) == '"')) {
      return this.SSID.substring(1, i - 1);
    }
    if ((i > 3) && (this.SSID.charAt(0) == 'P') && (this.SSID.charAt(1) == '"') && (this.SSID.charAt(i - 1) == '"')) {
      return WifiSsid.createFromAsciiEncoded(this.SSID.substring(2, i - 1)).toString();
    }
    return this.SSID;
  }
  
  public IpConfiguration.ProxySettings getProxySettings()
  {
    return this.mIpConfiguration.proxySettings;
  }
  
  public StaticIpConfiguration getStaticIpConfiguration()
  {
    return this.mIpConfiguration.getStaticIpConfiguration();
  }
  
  public boolean hasNoInternetAccess()
  {
    return (this.numNoInternetAccessReports > 0) && (!this.validatedInternetAccess);
  }
  
  public boolean isEnterprise()
  {
    if (!this.allowedKeyManagement.get(2)) {
      return this.allowedKeyManagement.get(3);
    }
    return true;
  }
  
  public boolean isLinked(WifiConfiguration paramWifiConfiguration)
  {
    return (paramWifiConfiguration != null) && (paramWifiConfiguration.linkedConfigurations != null) && (this.linkedConfigurations != null) && (paramWifiConfiguration.linkedConfigurations.get(configKey()) != null) && (this.linkedConfigurations.get(paramWifiConfiguration.configKey()) != null);
  }
  
  public boolean isPasspoint()
  {
    if ((TextUtils.isEmpty(this.FQDN)) || (TextUtils.isEmpty(this.providerFriendlyName))) {}
    while ((this.enterpriseConfig == null) || (this.enterpriseConfig.getEapMethod() == -1)) {
      return false;
    }
    return true;
  }
  
  public void setHttpProxy(ProxyInfo paramProxyInfo)
  {
    this.mIpConfiguration.httpProxy = paramProxyInfo;
  }
  
  public void setIpAssignment(IpConfiguration.IpAssignment paramIpAssignment)
  {
    this.mIpConfiguration.ipAssignment = paramIpAssignment;
  }
  
  public void setIpConfiguration(IpConfiguration paramIpConfiguration)
  {
    this.mIpConfiguration = paramIpConfiguration;
  }
  
  public void setPasspointManagementObjectTree(String paramString)
  {
    this.mPasspointManagementObjectTree = paramString;
  }
  
  public void setProxy(IpConfiguration.ProxySettings paramProxySettings, ProxyInfo paramProxyInfo)
  {
    this.mIpConfiguration.proxySettings = paramProxySettings;
    this.mIpConfiguration.httpProxy = paramProxyInfo;
  }
  
  public void setProxySettings(IpConfiguration.ProxySettings paramProxySettings)
  {
    this.mIpConfiguration.proxySettings = paramProxySettings;
  }
  
  public void setStaticIpConfiguration(StaticIpConfiguration paramStaticIpConfiguration)
  {
    this.mIpConfiguration.setStaticIpConfiguration(paramStaticIpConfiguration);
  }
  
  public void setVisibility(Visibility paramVisibility)
  {
    this.visibility = paramVisibility;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.status == 0) {
      localStringBuilder.append("* ");
    }
    for (;;)
    {
      localStringBuilder.append("ID: ").append(this.networkId).append(" SSID: ").append(this.SSID).append(" PROVIDER-NAME: ").append(this.providerFriendlyName).append(" BSSID: ").append(this.BSSID).append(" FQDN: ").append(this.FQDN).append(" PRIO: ").append(this.priority).append(" HIDDEN: ").append(this.hiddenSSID).append('\n');
      localStringBuilder.append(" NetworkSelectionStatus ").append(this.mNetworkSelectionStatus.getNetworkStatusString()).append("\n");
      if (this.mNetworkSelectionStatus.getNetworkSelectionDisableReason() <= 0) {
        break;
      }
      localStringBuilder.append(" mNetworkSelectionDisableReason ").append(this.mNetworkSelectionStatus.getNetworkDisableReasonString()).append("\n");
      i = 0;
      while (i < 10)
      {
        if (this.mNetworkSelectionStatus.getDisableReasonCounter(i) != 0) {
          localStringBuilder.append(NetworkSelectionStatus.getNetworkDisableReasonString(i)).append(" counter:").append(this.mNetworkSelectionStatus.getDisableReasonCounter(i)).append("\n");
        }
        i += 1;
      }
      if (this.status == 1) {
        localStringBuilder.append("- DSBLE ");
      }
    }
    if (this.mNetworkSelectionStatus.getConnectChoice() != null)
    {
      localStringBuilder.append(" connect choice: ").append(this.mNetworkSelectionStatus.getConnectChoice());
      localStringBuilder.append(" connect choice set time: ").append(this.mNetworkSelectionStatus.getConnectChoiceTimestamp());
    }
    localStringBuilder.append(" hasEverConnected: ").append(this.mNetworkSelectionStatus.getHasEverConnected()).append("\n");
    if (this.numAssociation > 0) {
      localStringBuilder.append(" numAssociation ").append(this.numAssociation).append("\n");
    }
    if (this.numNoInternetAccessReports > 0)
    {
      localStringBuilder.append(" numNoInternetAccessReports ");
      localStringBuilder.append(this.numNoInternetAccessReports).append("\n");
    }
    if (this.updateTime != null) {
      localStringBuilder.append("update ").append(this.updateTime).append("\n");
    }
    if (this.creationTime != null) {
      localStringBuilder.append("creation").append(this.creationTime).append("\n");
    }
    if (this.didSelfAdd) {
      localStringBuilder.append(" didSelfAdd");
    }
    if (this.selfAdded) {
      localStringBuilder.append(" selfAdded");
    }
    if (this.validatedInternetAccess) {
      localStringBuilder.append(" validatedInternetAccess");
    }
    if (this.ephemeral) {
      localStringBuilder.append(" ephemeral");
    }
    if (this.meteredHint) {
      localStringBuilder.append(" meteredHint");
    }
    if (this.useExternalScores) {
      localStringBuilder.append(" useExternalScores");
    }
    if ((this.didSelfAdd) || (this.selfAdded) || (this.validatedInternetAccess) || (this.ephemeral) || (this.meteredHint) || (this.useExternalScores)) {
      localStringBuilder.append("\n");
    }
    localStringBuilder.append(" KeyMgmt:");
    int i = 0;
    if (i < this.allowedKeyManagement.size())
    {
      if (this.allowedKeyManagement.get(i))
      {
        localStringBuilder.append(" ");
        if (i >= KeyMgmt.strings.length) {
          break label670;
        }
        localStringBuilder.append(KeyMgmt.strings[i]);
      }
      for (;;)
      {
        i += 1;
        break;
        label670:
        localStringBuilder.append("??");
      }
    }
    localStringBuilder.append(" Protocols:");
    i = 0;
    if (i < this.allowedProtocols.size())
    {
      if (this.allowedProtocols.get(i))
      {
        localStringBuilder.append(" ");
        if (i >= Protocol.strings.length) {
          break label750;
        }
        localStringBuilder.append(Protocol.strings[i]);
      }
      for (;;)
      {
        i += 1;
        break;
        label750:
        localStringBuilder.append("??");
      }
    }
    localStringBuilder.append('\n');
    localStringBuilder.append(" AuthAlgorithms:");
    i = 0;
    if (i < this.allowedAuthAlgorithms.size())
    {
      if (this.allowedAuthAlgorithms.get(i))
      {
        localStringBuilder.append(" ");
        if (i >= AuthAlgorithm.strings.length) {
          break label838;
        }
        localStringBuilder.append(AuthAlgorithm.strings[i]);
      }
      for (;;)
      {
        i += 1;
        break;
        label838:
        localStringBuilder.append("??");
      }
    }
    localStringBuilder.append('\n');
    localStringBuilder.append(" PairwiseCiphers:");
    i = 0;
    if (i < this.allowedPairwiseCiphers.size())
    {
      if (this.allowedPairwiseCiphers.get(i))
      {
        localStringBuilder.append(" ");
        if (i >= PairwiseCipher.strings.length) {
          break label926;
        }
        localStringBuilder.append(PairwiseCipher.strings[i]);
      }
      for (;;)
      {
        i += 1;
        break;
        label926:
        localStringBuilder.append("??");
      }
    }
    localStringBuilder.append('\n');
    localStringBuilder.append(" GroupCiphers:");
    i = 0;
    if (i < this.allowedGroupCiphers.size())
    {
      if (this.allowedGroupCiphers.get(i))
      {
        localStringBuilder.append(" ");
        if (i >= GroupCipher.strings.length) {
          break label1014;
        }
        localStringBuilder.append(GroupCipher.strings[i]);
      }
      for (;;)
      {
        i += 1;
        break;
        label1014:
        localStringBuilder.append("??");
      }
    }
    localStringBuilder.append('\n').append(" PSK: ");
    if (this.preSharedKey != null) {
      localStringBuilder.append('*');
    }
    localStringBuilder.append('\n').append(" sim_num ");
    if (this.SIMNum > 0) {
      localStringBuilder.append('*');
    }
    localStringBuilder.append("\nEnterprise config:\n");
    localStringBuilder.append(this.enterpriseConfig);
    localStringBuilder.append("IP config:\n");
    localStringBuilder.append(this.mIpConfiguration.toString());
    if (this.mNetworkSelectionStatus.getNetworkSelectionBSSID() != null) {
      localStringBuilder.append(" networkSelectionBSSID=").append(this.mNetworkSelectionStatus.getNetworkSelectionBSSID());
    }
    long l1 = System.currentTimeMillis();
    long l2;
    if (this.mNetworkSelectionStatus.getDisableTime() != -1L)
    {
      localStringBuilder.append('\n');
      l2 = l1 - this.mNetworkSelectionStatus.getDisableTime();
      if (l2 <= 0L) {
        localStringBuilder.append(" blackListed since <incorrect>");
      }
    }
    else
    {
      if (this.creatorUid != 0) {
        localStringBuilder.append(" cuid=").append(this.creatorUid);
      }
      if (this.creatorName != null) {
        localStringBuilder.append(" cname=").append(this.creatorName);
      }
      if (this.lastUpdateUid != 0) {
        localStringBuilder.append(" luid=").append(this.lastUpdateUid);
      }
      if (this.lastUpdateName != null) {
        localStringBuilder.append(" lname=").append(this.lastUpdateName);
      }
      localStringBuilder.append(" lcuid=").append(this.lastConnectUid);
      localStringBuilder.append(" userApproved=").append(userApprovedAsString(this.userApproved));
      localStringBuilder.append(" noInternetAccessExpected=").append(this.noInternetAccessExpected);
      localStringBuilder.append(" ");
      if (this.lastConnected != 0L)
      {
        localStringBuilder.append('\n');
        l2 = l1 - this.lastConnected;
        if (l2 > 0L) {
          break label1605;
        }
        localStringBuilder.append("lastConnected since <incorrect>");
      }
      label1400:
      if (this.lastConnectionFailure != 0L)
      {
        localStringBuilder.append('\n');
        l2 = l1 - this.lastConnectionFailure;
        if (l2 > 0L) {
          break label1635;
        }
        localStringBuilder.append("lastConnectionFailure since <incorrect> ");
      }
      label1441:
      if (this.lastRoamingFailure != 0L)
      {
        localStringBuilder.append('\n');
        l1 -= this.lastRoamingFailure;
        if (l1 > 0L) {
          break label1668;
        }
        localStringBuilder.append("lastRoamingFailure since <incorrect> ");
      }
    }
    for (;;)
    {
      localStringBuilder.append("roamingFailureBlackListTimeMilli: ").append(Long.toString(this.roamingFailureBlackListTimeMilli));
      localStringBuilder.append('\n');
      if (this.linkedConfigurations == null) {
        break label1700;
      }
      Iterator localIterator = this.linkedConfigurations.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        localStringBuilder.append(" linked: ").append(str);
        localStringBuilder.append('\n');
      }
      localStringBuilder.append(" blackListed: ").append(Long.toString(l2 / 1000L)).append("sec ");
      break;
      label1605:
      localStringBuilder.append("lastConnected: ").append(Long.toString(l2 / 1000L)).append("sec ");
      break label1400;
      label1635:
      localStringBuilder.append("lastConnectionFailure: ").append(Long.toString(l2 / 1000L));
      localStringBuilder.append("sec ");
      break label1441;
      label1668:
      localStringBuilder.append("lastRoamingFailure: ").append(Long.toString(l1 / 1000L));
      localStringBuilder.append("sec ");
    }
    label1700:
    localStringBuilder.append("triggeredLow: ").append(this.numUserTriggeredWifiDisableLowRSSI);
    localStringBuilder.append(" triggeredBad: ").append(this.numUserTriggeredWifiDisableBadRSSI);
    localStringBuilder.append(" triggeredNotHigh: ").append(this.numUserTriggeredWifiDisableNotHighRSSI);
    localStringBuilder.append('\n');
    localStringBuilder.append("ticksLow: ").append(this.numTicksAtLowRSSI);
    localStringBuilder.append(" ticksBad: ").append(this.numTicksAtBadRSSI);
    localStringBuilder.append(" ticksNotHigh: ").append(this.numTicksAtNotHighRSSI);
    localStringBuilder.append('\n');
    localStringBuilder.append("triggeredJoin: ").append(this.numUserTriggeredJoinAttempts);
    localStringBuilder.append('\n');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    paramParcel.writeInt(this.networkId);
    paramParcel.writeInt(this.status);
    this.mNetworkSelectionStatus.writeToParcel(paramParcel);
    paramParcel.writeString(this.SSID);
    paramParcel.writeString(this.BSSID);
    paramParcel.writeInt(this.apBand);
    paramParcel.writeInt(this.apChannel);
    paramParcel.writeString(this.FQDN);
    paramParcel.writeString(this.providerFriendlyName);
    paramParcel.writeInt(this.roamingConsortiumIds.length);
    Object localObject = this.roamingConsortiumIds;
    int k = localObject.length;
    int i = 0;
    while (i < k)
    {
      paramParcel.writeLong(localObject[i]);
      i += 1;
    }
    paramParcel.writeString(this.preSharedKey);
    paramParcel.writeString(this.wapiASCert);
    paramParcel.writeString(this.wapiUserCert);
    paramParcel.writeString(this.wapiPsk);
    paramParcel.writeInt(this.wapiPskType);
    localObject = this.wepKeys;
    k = localObject.length;
    i = 0;
    while (i < k)
    {
      paramParcel.writeString(localObject[i]);
      i += 1;
    }
    paramParcel.writeInt(this.wepTxKeyIndex);
    paramParcel.writeInt(this.priority);
    if (this.hiddenSSID)
    {
      i = 1;
      paramParcel.writeInt(i);
      if (!this.requirePMF) {
        break label621;
      }
      i = 1;
      label231:
      paramParcel.writeInt(i);
      paramParcel.writeString(this.updateIdentifier);
      writeBitSet(paramParcel, this.allowedKeyManagement);
      writeBitSet(paramParcel, this.allowedProtocols);
      writeBitSet(paramParcel, this.allowedAuthAlgorithms);
      writeBitSet(paramParcel, this.allowedPairwiseCiphers);
      writeBitSet(paramParcel, this.allowedGroupCiphers);
      paramParcel.writeParcelable(this.enterpriseConfig, paramInt);
      paramParcel.writeParcelable(this.mIpConfiguration, paramInt);
      paramParcel.writeString(this.dhcpServer);
      paramParcel.writeString(this.defaultGwMacAddress);
      if (!this.selfAdded) {
        break label626;
      }
      paramInt = 1;
      label327:
      paramParcel.writeInt(paramInt);
      if (!this.didSelfAdd) {
        break label631;
      }
      paramInt = 1;
      label341:
      paramParcel.writeInt(paramInt);
      if (!this.validatedInternetAccess) {
        break label636;
      }
      paramInt = 1;
      label355:
      paramParcel.writeInt(paramInt);
      if (!this.ephemeral) {
        break label641;
      }
      paramInt = 1;
      label369:
      paramParcel.writeInt(paramInt);
      if (!this.meteredHint) {
        break label646;
      }
      paramInt = 1;
      label383:
      paramParcel.writeInt(paramInt);
      if (!this.useExternalScores) {
        break label651;
      }
      paramInt = 1;
      label397:
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.creatorUid);
      paramParcel.writeInt(this.lastConnectUid);
      paramParcel.writeInt(this.lastUpdateUid);
      paramParcel.writeString(this.creatorName);
      paramParcel.writeString(this.lastUpdateName);
      paramParcel.writeLong(this.lastConnectionFailure);
      paramParcel.writeLong(this.lastRoamingFailure);
      paramParcel.writeInt(this.lastRoamingFailureReason);
      paramParcel.writeLong(this.roamingFailureBlackListTimeMilli);
      paramParcel.writeInt(this.numScorerOverride);
      paramParcel.writeInt(this.numScorerOverrideAndSwitchedNetwork);
      paramParcel.writeInt(this.numAssociation);
      paramParcel.writeInt(this.numUserTriggeredWifiDisableLowRSSI);
      paramParcel.writeInt(this.numUserTriggeredWifiDisableBadRSSI);
      paramParcel.writeInt(this.numUserTriggeredWifiDisableNotHighRSSI);
      paramParcel.writeInt(this.numTicksAtLowRSSI);
      paramParcel.writeInt(this.numTicksAtBadRSSI);
      paramParcel.writeInt(this.numTicksAtNotHighRSSI);
      paramParcel.writeInt(this.numUserTriggeredJoinAttempts);
      paramParcel.writeInt(this.userApproved);
      paramParcel.writeInt(this.numNoInternetAccessReports);
      if (!this.noInternetAccessExpected) {
        break label656;
      }
      paramInt = 1;
      label579:
      paramParcel.writeInt(paramInt);
      if (!this.shared) {
        break label661;
      }
    }
    label621:
    label626:
    label631:
    label636:
    label641:
    label646:
    label651:
    label656:
    label661:
    for (paramInt = j;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeString(this.mPasspointManagementObjectTree);
      paramParcel.writeInt(this.SIMNum);
      return;
      i = 0;
      break;
      i = 0;
      break label231;
      paramInt = 0;
      break label327;
      paramInt = 0;
      break label341;
      paramInt = 0;
      break label355;
      paramInt = 0;
      break label369;
      paramInt = 0;
      break label383;
      paramInt = 0;
      break label397;
      paramInt = 0;
      break label579;
    }
  }
  
  public static class AuthAlgorithm
  {
    public static final int LEAP = 2;
    public static final int OPEN = 0;
    public static final int SHARED = 1;
    public static final String[] strings = { "OPEN", "SHARED", "LEAP" };
    public static final String varName = "auth_alg";
  }
  
  public static class GroupCipher
  {
    public static final int CCMP = 3;
    public static final int GTK_NOT_USED = 4;
    public static final int TKIP = 2;
    public static final int WEP104 = 1;
    public static final int WEP40 = 0;
    public static final String[] strings = { "WEP40", "WEP104", "TKIP", "CCMP", "GTK_NOT_USED", "SMS4" };
    public static final String varName = "group";
  }
  
  public static class KeyMgmt
  {
    public static final int IEEE8021X = 3;
    public static final int NONE = 0;
    public static final int OSEN = 5;
    public static final int WAPI_CERT = 7;
    public static final int WAPI_PSK = 6;
    public static final int WPA2_PSK = 4;
    public static final int WPA_EAP = 2;
    public static final int WPA_PSK = 1;
    public static final String[] strings = { "NONE", "WPA_PSK", "WPA_EAP", "IEEE8021X", "WPA2_PSK", "OSEN", "WAPI_PSK", "WAPI_CERT" };
    public static final String varName = "key_mgmt";
  }
  
  public static class NetworkSelectionStatus
  {
    private static final int CONNECT_CHOICE_EXISTS = 1;
    private static final int CONNECT_CHOICE_NOT_EXISTS = -1;
    public static final int DISABLED_ASSOCIATION_REJECTION = 2;
    public static final int DISABLED_AUTHENTICATION_FAILURE = 3;
    public static final int DISABLED_AUTHENTICATION_NO_CREDENTIALS = 7;
    public static final int DISABLED_BAD_LINK = 1;
    public static final int DISABLED_BY_WIFI_MANAGER = 9;
    public static final int DISABLED_DHCP_FAILURE = 4;
    public static final int DISABLED_DNS_FAILURE = 5;
    public static final int DISABLED_NO_INTERNET = 8;
    public static final int DISABLED_TLS_VERSION_MISMATCH = 6;
    public static final long INVALID_NETWORK_SELECTION_DISABLE_TIMESTAMP = -1L;
    public static final int NETWORK_SELECTION_DISABLED_MAX = 10;
    public static final int NETWORK_SELECTION_ENABLE = 0;
    public static final int NETWORK_SELECTION_ENABLED = 0;
    public static final int NETWORK_SELECTION_PERMANENTLY_DISABLED = 2;
    public static final int NETWORK_SELECTION_STATUS_MAX = 3;
    public static final int NETWORK_SELECTION_TEMPORARY_DISABLED = 1;
    private static final String[] QUALITY_NETWORK_SELECTION_DISABLE_REASON = { "NETWORK_SELECTION_ENABLE", "NETWORK_SELECTION_DISABLED_BAD_LINK", "NETWORK_SELECTION_DISABLED_ASSOCIATION_REJECTION ", "NETWORK_SELECTION_DISABLED_AUTHENTICATION_FAILURE", "NETWORK_SELECTION_DISABLED_DHCP_FAILURE", "NETWORK_SELECTION_DISABLED_DNS_FAILURE", "NETWORK_SELECTION_DISABLED_TLS_VERSION", "NETWORK_SELECTION_DISABLED_AUTHENTICATION_NO_CREDENTIALS", "NETWORK_SELECTION_DISABLED_NO_INTERNET", "NETWORK_SELECTION_DISABLED_BY_WIFI_MANAGER" };
    private static final String[] QUALITY_NETWORK_SELECTION_STATUS = { "NETWORK_SELECTION_ENABLED", "NETWORK_SELECTION_TEMPORARY_DISABLED", "NETWORK_SELECTION_PERMANENTLY_DISABLED" };
    private ScanResult mCandidate;
    private int mCandidateScore;
    private String mConnectChoice;
    private long mConnectChoiceTimestamp = -1L;
    private boolean mHasEverConnected = false;
    private int[] mNetworkSeclectionDisableCounter = new int[10];
    private String mNetworkSelectionBSSID;
    private int mNetworkSelectionDisableReason;
    private boolean mSeenInLastQualifiedNetworkSelection;
    private int mStatus;
    private long mTemporarilyDisabledTimestamp = -1L;
    
    public static String getNetworkDisableReasonString(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < 10)) {
        return QUALITY_NETWORK_SELECTION_DISABLE_REASON[paramInt];
      }
      return null;
    }
    
    public void clearDisableReasonCounter()
    {
      Arrays.fill(this.mNetworkSeclectionDisableCounter, 0);
    }
    
    public void clearDisableReasonCounter(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < 10))
      {
        this.mNetworkSeclectionDisableCounter[paramInt] = 0;
        return;
      }
      throw new IllegalArgumentException("Illegal reason value: " + paramInt);
    }
    
    public void copy(NetworkSelectionStatus paramNetworkSelectionStatus)
    {
      this.mStatus = paramNetworkSelectionStatus.mStatus;
      this.mNetworkSelectionDisableReason = paramNetworkSelectionStatus.mNetworkSelectionDisableReason;
      int i = 0;
      while (i < 10)
      {
        this.mNetworkSeclectionDisableCounter[i] = paramNetworkSelectionStatus.mNetworkSeclectionDisableCounter[i];
        i += 1;
      }
      this.mTemporarilyDisabledTimestamp = paramNetworkSelectionStatus.mTemporarilyDisabledTimestamp;
      this.mNetworkSelectionBSSID = paramNetworkSelectionStatus.mNetworkSelectionBSSID;
      setConnectChoice(paramNetworkSelectionStatus.getConnectChoice());
      setConnectChoiceTimestamp(paramNetworkSelectionStatus.getConnectChoiceTimestamp());
      setHasEverConnected(paramNetworkSelectionStatus.getHasEverConnected());
    }
    
    public ScanResult getCandidate()
    {
      return this.mCandidate;
    }
    
    public int getCandidateScore()
    {
      return this.mCandidateScore;
    }
    
    public String getConnectChoice()
    {
      return this.mConnectChoice;
    }
    
    public long getConnectChoiceTimestamp()
    {
      return this.mConnectChoiceTimestamp;
    }
    
    public int getDisableReasonCounter(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < 10)) {
        return this.mNetworkSeclectionDisableCounter[paramInt];
      }
      throw new IllegalArgumentException("Illegal reason value: " + paramInt);
    }
    
    public long getDisableTime()
    {
      return this.mTemporarilyDisabledTimestamp;
    }
    
    public boolean getHasEverConnected()
    {
      return this.mHasEverConnected;
    }
    
    public String getNetworkDisableReasonString()
    {
      return QUALITY_NETWORK_SELECTION_DISABLE_REASON[this.mNetworkSelectionDisableReason];
    }
    
    public String getNetworkSelectionBSSID()
    {
      return this.mNetworkSelectionBSSID;
    }
    
    public int getNetworkSelectionDisableReason()
    {
      return this.mNetworkSelectionDisableReason;
    }
    
    public int getNetworkSelectionStatus()
    {
      return this.mStatus;
    }
    
    public String getNetworkStatusString()
    {
      return QUALITY_NETWORK_SELECTION_STATUS[this.mStatus];
    }
    
    public boolean getSeenInLastQualifiedNetworkSelection()
    {
      return this.mSeenInLastQualifiedNetworkSelection;
    }
    
    public void incrementDisableReasonCounter(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < 10))
      {
        int[] arrayOfInt = this.mNetworkSeclectionDisableCounter;
        arrayOfInt[paramInt] += 1;
        return;
      }
      throw new IllegalArgumentException("Illegal reason value: " + paramInt);
    }
    
    public boolean isDisabledByReason(int paramInt)
    {
      return this.mNetworkSelectionDisableReason == paramInt;
    }
    
    public boolean isNetworkEnabled()
    {
      boolean bool = false;
      if (this.mStatus == 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isNetworkPermanentlyDisabled()
    {
      return this.mStatus == 2;
    }
    
    public boolean isNetworkTemporaryDisabled()
    {
      return this.mStatus == 1;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      boolean bool = true;
      setNetworkSelectionStatus(paramParcel.readInt());
      setNetworkSelectionDisableReason(paramParcel.readInt());
      int i = 0;
      while (i < 10)
      {
        setDisableReasonCounter(i, paramParcel.readInt());
        i += 1;
      }
      setDisableTime(paramParcel.readLong());
      setNetworkSelectionBSSID(paramParcel.readString());
      if (paramParcel.readInt() == 1)
      {
        setConnectChoice(paramParcel.readString());
        setConnectChoiceTimestamp(paramParcel.readLong());
        if (paramParcel.readInt() == 0) {
          break label110;
        }
      }
      for (;;)
      {
        setHasEverConnected(bool);
        return;
        setConnectChoice(null);
        setConnectChoiceTimestamp(-1L);
        break;
        label110:
        bool = false;
      }
    }
    
    public void setCandidate(ScanResult paramScanResult)
    {
      this.mCandidate = paramScanResult;
    }
    
    public void setCandidateScore(int paramInt)
    {
      this.mCandidateScore = paramInt;
    }
    
    public void setConnectChoice(String paramString)
    {
      this.mConnectChoice = paramString;
    }
    
    public void setConnectChoiceTimestamp(long paramLong)
    {
      this.mConnectChoiceTimestamp = paramLong;
    }
    
    public void setDisableReasonCounter(int paramInt1, int paramInt2)
    {
      if ((paramInt1 >= 0) && (paramInt1 < 10))
      {
        this.mNetworkSeclectionDisableCounter[paramInt1] = paramInt2;
        return;
      }
      throw new IllegalArgumentException("Illegal reason value: " + paramInt1);
    }
    
    public void setDisableTime(long paramLong)
    {
      this.mTemporarilyDisabledTimestamp = paramLong;
    }
    
    public void setHasEverConnected(boolean paramBoolean)
    {
      this.mHasEverConnected = paramBoolean;
    }
    
    public void setNetworkSelectionBSSID(String paramString)
    {
      this.mNetworkSelectionBSSID = paramString;
    }
    
    public void setNetworkSelectionDisableReason(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < 10))
      {
        this.mNetworkSelectionDisableReason = paramInt;
        return;
      }
      throw new IllegalArgumentException("Illegal reason value: " + paramInt);
    }
    
    public void setNetworkSelectionStatus(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < 3)) {
        this.mStatus = paramInt;
      }
    }
    
    public void setSeenInLastQualifiedNetworkSelection(boolean paramBoolean)
    {
      this.mSeenInLastQualifiedNetworkSelection = paramBoolean;
    }
    
    public void writeToParcel(Parcel paramParcel)
    {
      int j = 1;
      paramParcel.writeInt(getNetworkSelectionStatus());
      paramParcel.writeInt(getNetworkSelectionDisableReason());
      int i = 0;
      while (i < 10)
      {
        paramParcel.writeInt(getDisableReasonCounter(i));
        i += 1;
      }
      paramParcel.writeLong(getDisableTime());
      paramParcel.writeString(getNetworkSelectionBSSID());
      if (getConnectChoice() != null)
      {
        paramParcel.writeInt(1);
        paramParcel.writeString(getConnectChoice());
        paramParcel.writeLong(getConnectChoiceTimestamp());
        if (!getHasEverConnected()) {
          break label109;
        }
      }
      label109:
      for (i = j;; i = 0)
      {
        paramParcel.writeInt(i);
        return;
        paramParcel.writeInt(-1);
        break;
      }
    }
  }
  
  public static class PairwiseCipher
  {
    public static final int CCMP = 2;
    public static final int NONE = 0;
    public static final int TKIP = 1;
    public static final String[] strings = { "NONE", "TKIP", "CCMP", "SMS4" };
    public static final String varName = "pairwise";
  }
  
  public static class Protocol
  {
    public static final int OSEN = 2;
    public static final int RSN = 1;
    public static final int WAPI = 3;
    public static final int WPA = 0;
    public static final String[] strings = { "WPA", "RSN", "OSEN", "WAPI" };
    public static final String varName = "proto";
  }
  
  public static class Status
  {
    public static final int CURRENT = 0;
    public static final int DISABLED = 1;
    public static final int ENABLED = 2;
    public static final String[] strings = { "current", "disabled", "enabled" };
  }
  
  public static final class Visibility
  {
    public String BSSID24;
    public String BSSID5;
    public long age24;
    public long age5;
    public int bandPreferenceBoost;
    public int currentNetworkBoost;
    public int lastChoiceBoost;
    public String lastChoiceConfig;
    public int num24;
    public int num5;
    public int rssi24;
    public int rssi5;
    public int score;
    
    public Visibility()
    {
      this.rssi5 = WifiConfiguration.INVALID_RSSI;
      this.rssi24 = WifiConfiguration.INVALID_RSSI;
    }
    
    public Visibility(Visibility paramVisibility)
    {
      this.rssi5 = paramVisibility.rssi5;
      this.rssi24 = paramVisibility.rssi24;
      this.age24 = paramVisibility.age24;
      this.age5 = paramVisibility.age5;
      this.num24 = paramVisibility.num24;
      this.num5 = paramVisibility.num5;
      this.BSSID5 = paramVisibility.BSSID5;
      this.BSSID24 = paramVisibility.BSSID24;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      if (this.rssi24 > WifiConfiguration.INVALID_RSSI)
      {
        localStringBuilder.append(Integer.toString(this.rssi24));
        localStringBuilder.append(",");
        localStringBuilder.append(Integer.toString(this.num24));
        if (this.BSSID24 != null) {
          localStringBuilder.append(",").append(this.BSSID24);
        }
      }
      localStringBuilder.append("; ");
      if (this.rssi5 > WifiConfiguration.INVALID_RSSI)
      {
        localStringBuilder.append(Integer.toString(this.rssi5));
        localStringBuilder.append(",");
        localStringBuilder.append(Integer.toString(this.num5));
        if (this.BSSID5 != null) {
          localStringBuilder.append(",").append(this.BSSID5);
        }
      }
      if (this.score != 0)
      {
        localStringBuilder.append("; ").append(this.score);
        localStringBuilder.append(", ").append(this.currentNetworkBoost);
        localStringBuilder.append(", ").append(this.bandPreferenceBoost);
        if (this.lastChoiceConfig != null)
        {
          localStringBuilder.append(", ").append(this.lastChoiceBoost);
          localStringBuilder.append(", ").append(this.lastChoiceConfig);
        }
      }
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */