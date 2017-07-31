package android.net;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Slog;
import java.util.Objects;

public class NetworkIdentity
  implements Comparable<NetworkIdentity>
{
  @Deprecated
  public static final boolean COMBINE_SUBTYPE_ENABLED = true;
  public static final int SUBTYPE_COMBINED = -1;
  private static final String TAG = "NetworkIdentity";
  final boolean mMetered;
  final String mNetworkId;
  final boolean mRoaming;
  final int mSubType;
  final String mSubscriberId;
  final int mType;
  
  public NetworkIdentity(int paramInt1, int paramInt2, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mType = paramInt1;
    this.mSubType = -1;
    this.mSubscriberId = paramString1;
    this.mNetworkId = paramString2;
    this.mRoaming = paramBoolean1;
    this.mMetered = paramBoolean2;
  }
  
  public static NetworkIdentity buildNetworkIdentity(Context paramContext, NetworkState paramNetworkState)
  {
    int i = paramNetworkState.networkInfo.getType();
    int j = paramNetworkState.networkInfo.getSubtype();
    Object localObject2 = null;
    String str = null;
    boolean bool3 = false;
    boolean bool4 = false;
    Object localObject1;
    boolean bool2;
    boolean bool1;
    if (ConnectivityManager.isNetworkTypeMobile(i))
    {
      if ((paramNetworkState.subscriberId == null) && (paramNetworkState.networkInfo.getState() != NetworkInfo.State.DISCONNECTED) && (paramNetworkState.networkInfo.getState() != NetworkInfo.State.UNKNOWN)) {
        Slog.w("NetworkIdentity", "Active mobile network without subscriber! ni = " + paramNetworkState.networkInfo);
      }
      localObject1 = paramNetworkState.subscriberId;
      bool2 = paramNetworkState.networkInfo.isRoaming();
      if (paramNetworkState.networkCapabilities.hasCapability(11))
      {
        if (!paramContext.getResources().getBoolean(17957067)) {
          break label169;
        }
        bool1 = paramNetworkState.networkCapabilities.hasCapability(4);
      }
    }
    for (;;)
    {
      return new NetworkIdentity(i, j, (String)localObject1, str, bool2, bool1);
      bool1 = true;
      continue;
      label169:
      bool1 = false;
      continue;
      localObject1 = localObject2;
      bool2 = bool3;
      bool1 = bool4;
      if (i == 1) {
        if (paramNetworkState.networkId != null)
        {
          str = paramNetworkState.networkId;
          localObject1 = localObject2;
          bool2 = bool3;
          bool1 = bool4;
        }
        else
        {
          paramContext = ((WifiManager)paramContext.getSystemService("wifi")).getConnectionInfo();
          if (paramContext != null)
          {
            str = paramContext.getSSID();
            localObject1 = localObject2;
            bool2 = bool3;
            bool1 = bool4;
          }
          else
          {
            str = null;
            localObject1 = localObject2;
            bool2 = bool3;
            bool1 = bool4;
          }
        }
      }
    }
  }
  
  public static String scrubSubscriberId(String paramString)
  {
    if ("eng".equals(Build.TYPE)) {
      return paramString;
    }
    if (paramString != null) {
      return paramString.substring(0, Math.min(6, paramString.length())) + "...";
    }
    return "null";
  }
  
  public static String[] scrubSubscriberId(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {
      return null;
    }
    String[] arrayOfString = new String[paramArrayOfString.length];
    int i = 0;
    while (i < arrayOfString.length)
    {
      arrayOfString[i] = scrubSubscriberId(paramArrayOfString[i]);
      i += 1;
    }
    return arrayOfString;
  }
  
  public int compareTo(NetworkIdentity paramNetworkIdentity)
  {
    int j = Integer.compare(this.mType, paramNetworkIdentity.mType);
    int i = j;
    if (j == 0) {
      i = Integer.compare(this.mSubType, paramNetworkIdentity.mSubType);
    }
    j = i;
    if (i == 0)
    {
      j = i;
      if (this.mSubscriberId != null)
      {
        j = i;
        if (paramNetworkIdentity.mSubscriberId != null) {
          j = this.mSubscriberId.compareTo(paramNetworkIdentity.mSubscriberId);
        }
      }
    }
    i = j;
    if (j == 0)
    {
      i = j;
      if (this.mNetworkId != null)
      {
        i = j;
        if (paramNetworkIdentity.mNetworkId != null) {
          i = this.mNetworkId.compareTo(paramNetworkIdentity.mNetworkId);
        }
      }
    }
    j = i;
    if (i == 0) {
      j = Boolean.compare(this.mRoaming, paramNetworkIdentity.mRoaming);
    }
    i = j;
    if (j == 0) {
      i = Boolean.compare(this.mMetered, paramNetworkIdentity.mMetered);
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if ((paramObject instanceof NetworkIdentity))
    {
      paramObject = (NetworkIdentity)paramObject;
      boolean bool1 = bool2;
      if (this.mType == ((NetworkIdentity)paramObject).mType)
      {
        bool1 = bool2;
        if (this.mSubType == ((NetworkIdentity)paramObject).mSubType)
        {
          bool1 = bool2;
          if (this.mRoaming == ((NetworkIdentity)paramObject).mRoaming)
          {
            bool1 = bool2;
            if (Objects.equals(this.mSubscriberId, ((NetworkIdentity)paramObject).mSubscriberId))
            {
              bool1 = bool2;
              if (Objects.equals(this.mNetworkId, ((NetworkIdentity)paramObject).mNetworkId))
              {
                bool1 = bool2;
                if (this.mMetered == ((NetworkIdentity)paramObject).mMetered) {
                  bool1 = true;
                }
              }
            }
          }
        }
      }
      return bool1;
    }
    return false;
  }
  
  public boolean getMetered()
  {
    return this.mMetered;
  }
  
  public String getNetworkId()
  {
    return this.mNetworkId;
  }
  
  public boolean getRoaming()
  {
    return this.mRoaming;
  }
  
  public int getSubType()
  {
    return this.mSubType;
  }
  
  public String getSubscriberId()
  {
    return this.mSubscriberId;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(this.mType), Integer.valueOf(this.mSubType), this.mSubscriberId, this.mNetworkId, Boolean.valueOf(this.mRoaming), Boolean.valueOf(this.mMetered) });
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("{");
    localStringBuilder.append("type=").append(ConnectivityManager.getNetworkTypeName(this.mType));
    localStringBuilder.append(", subType=");
    localStringBuilder.append("COMBINED");
    if (this.mSubscriberId != null) {
      localStringBuilder.append(", subscriberId=").append(scrubSubscriberId(this.mSubscriberId));
    }
    if (this.mNetworkId != null) {
      localStringBuilder.append(", networkId=").append(this.mNetworkId);
    }
    if (this.mRoaming) {
      localStringBuilder.append(", ROAMING");
    }
    localStringBuilder.append(", metered=").append(this.mMetered);
    return "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkIdentity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */