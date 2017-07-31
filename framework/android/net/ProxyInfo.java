package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.net.InetSocketAddress;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.util.List;
import java.util.Locale;

public class ProxyInfo
  implements Parcelable
{
  public static final Parcelable.Creator<ProxyInfo> CREATOR = new Parcelable.Creator()
  {
    public ProxyInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      String str = null;
      int i = 0;
      if (paramAnonymousParcel.readByte() != 0) {
        return new ProxyInfo((Uri)Uri.CREATOR.createFromParcel(paramAnonymousParcel), paramAnonymousParcel.readInt());
      }
      if (paramAnonymousParcel.readByte() != 0)
      {
        str = paramAnonymousParcel.readString();
        i = paramAnonymousParcel.readInt();
      }
      return new ProxyInfo(str, i, paramAnonymousParcel.readString(), paramAnonymousParcel.readStringArray(), null);
    }
    
    public ProxyInfo[] newArray(int paramAnonymousInt)
    {
      return new ProxyInfo[paramAnonymousInt];
    }
  };
  public static final String LOCAL_EXCL_LIST = "";
  public static final String LOCAL_HOST = "localhost";
  public static final int LOCAL_PORT = -1;
  private String mExclusionList;
  private String mHost;
  private Uri mPacFileUrl;
  private String[] mParsedExclusionList;
  private int mPort;
  
  public ProxyInfo(ProxyInfo paramProxyInfo)
  {
    if (paramProxyInfo != null)
    {
      this.mHost = paramProxyInfo.getHost();
      this.mPort = paramProxyInfo.getPort();
      this.mPacFileUrl = paramProxyInfo.mPacFileUrl;
      this.mExclusionList = paramProxyInfo.getExclusionListAsString();
      this.mParsedExclusionList = paramProxyInfo.mParsedExclusionList;
      return;
    }
    this.mPacFileUrl = Uri.EMPTY;
  }
  
  public ProxyInfo(Uri paramUri)
  {
    this.mHost = "localhost";
    this.mPort = -1;
    setExclusionList("");
    if (paramUri == null) {
      throw new NullPointerException();
    }
    this.mPacFileUrl = paramUri;
  }
  
  public ProxyInfo(Uri paramUri, int paramInt)
  {
    this.mHost = "localhost";
    this.mPort = paramInt;
    setExclusionList("");
    if (paramUri == null) {
      throw new NullPointerException();
    }
    this.mPacFileUrl = paramUri;
  }
  
  public ProxyInfo(String paramString)
  {
    this.mHost = "localhost";
    this.mPort = -1;
    setExclusionList("");
    this.mPacFileUrl = Uri.parse(paramString);
  }
  
  public ProxyInfo(String paramString1, int paramInt, String paramString2)
  {
    this.mHost = paramString1;
    this.mPort = paramInt;
    setExclusionList(paramString2);
    this.mPacFileUrl = Uri.EMPTY;
  }
  
  private ProxyInfo(String paramString1, int paramInt, String paramString2, String[] paramArrayOfString)
  {
    this.mHost = paramString1;
    this.mPort = paramInt;
    this.mExclusionList = paramString2;
    this.mParsedExclusionList = paramArrayOfString;
    this.mPacFileUrl = Uri.EMPTY;
  }
  
  public static ProxyInfo buildDirectProxy(String paramString, int paramInt)
  {
    return new ProxyInfo(paramString, paramInt, null);
  }
  
  public static ProxyInfo buildDirectProxy(String paramString, int paramInt, List<String> paramList)
  {
    paramList = (String[])paramList.toArray(new String[paramList.size()]);
    return new ProxyInfo(paramString, paramInt, TextUtils.join(",", paramList), paramList);
  }
  
  public static ProxyInfo buildPacProxy(Uri paramUri)
  {
    return new ProxyInfo(paramUri);
  }
  
  private void setExclusionList(String paramString)
  {
    this.mExclusionList = paramString;
    if (this.mExclusionList == null)
    {
      this.mParsedExclusionList = new String[0];
      return;
    }
    this.mParsedExclusionList = paramString.toLowerCase(Locale.ROOT).split(",");
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ProxyInfo)) {
      return false;
    }
    paramObject = (ProxyInfo)paramObject;
    if (!Uri.EMPTY.equals(this.mPacFileUrl)) {
      return (this.mPacFileUrl.equals(((ProxyInfo)paramObject).getPacFileUrl())) && (this.mPort == ((ProxyInfo)paramObject).mPort);
    }
    if (!Uri.EMPTY.equals(((ProxyInfo)paramObject).mPacFileUrl)) {
      return false;
    }
    if ((this.mExclusionList == null) || (this.mExclusionList.equals(((ProxyInfo)paramObject).getExclusionListAsString())))
    {
      if ((this.mHost != null) && (((ProxyInfo)paramObject).getHost() != null) && (!this.mHost.equals(((ProxyInfo)paramObject).getHost()))) {
        return false;
      }
    }
    else {
      return false;
    }
    if ((this.mHost != null) && (((ProxyInfo)paramObject).mHost == null)) {
      return false;
    }
    if ((this.mHost == null) && (((ProxyInfo)paramObject).mHost != null)) {
      return false;
    }
    return this.mPort == ((ProxyInfo)paramObject).mPort;
  }
  
  public String[] getExclusionList()
  {
    return this.mParsedExclusionList;
  }
  
  public String getExclusionListAsString()
  {
    return this.mExclusionList;
  }
  
  public String getHost()
  {
    return this.mHost;
  }
  
  public Uri getPacFileUrl()
  {
    return this.mPacFileUrl;
  }
  
  public int getPort()
  {
    return this.mPort;
  }
  
  public InetSocketAddress getSocketAddress()
  {
    try
    {
      InetSocketAddress localInetSocketAddress = new InetSocketAddress(this.mHost, this.mPort);
      return localInetSocketAddress;
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return null;
  }
  
  public int hashCode()
  {
    int j = 0;
    int i;
    if (this.mHost == null)
    {
      i = 0;
      if (this.mExclusionList != null) {
        break label38;
      }
    }
    for (;;)
    {
      return i + j + this.mPort;
      i = this.mHost.hashCode();
      break;
      label38:
      j = this.mExclusionList.hashCode();
    }
  }
  
  public boolean isValid()
  {
    if (!Uri.EMPTY.equals(this.mPacFileUrl)) {
      return true;
    }
    String str1;
    String str2;
    if (this.mHost == null)
    {
      str1 = "";
      if (this.mPort != 0) {
        break label64;
      }
      str2 = "";
      label35:
      if (this.mExclusionList != null) {
        break label75;
      }
    }
    label64:
    label75:
    for (String str3 = "";; str3 = this.mExclusionList)
    {
      if (Proxy.validate(str1, str2, str3) != 0) {
        break label83;
      }
      return true;
      str1 = this.mHost;
      break;
      str2 = Integer.toString(this.mPort);
      break label35;
    }
    label83:
    return false;
  }
  
  public java.net.Proxy makeProxy()
  {
    java.net.Proxy localProxy = java.net.Proxy.NO_PROXY;
    Object localObject = localProxy;
    if (this.mHost != null) {}
    try
    {
      localObject = new InetSocketAddress(this.mHost, this.mPort);
      localObject = new java.net.Proxy(Proxy.Type.HTTP, (SocketAddress)localObject);
      return (java.net.Proxy)localObject;
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return localProxy;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (!Uri.EMPTY.equals(this.mPacFileUrl))
    {
      localStringBuilder.append("PAC Script: ");
      localStringBuilder.append(this.mPacFileUrl);
    }
    if (this.mHost != null)
    {
      localStringBuilder.append("[");
      localStringBuilder.append(this.mHost);
      localStringBuilder.append("] ");
      localStringBuilder.append(Integer.toString(this.mPort));
      if (this.mExclusionList != null) {
        localStringBuilder.append(" xl=").append(this.mExclusionList);
      }
    }
    for (;;)
    {
      return localStringBuilder.toString();
      localStringBuilder.append("[ProxyProperties.mHost == null]");
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (!Uri.EMPTY.equals(this.mPacFileUrl))
    {
      paramParcel.writeByte((byte)1);
      this.mPacFileUrl.writeToParcel(paramParcel, 0);
      paramParcel.writeInt(this.mPort);
      return;
    }
    paramParcel.writeByte((byte)0);
    if (this.mHost != null)
    {
      paramParcel.writeByte((byte)1);
      paramParcel.writeString(this.mHost);
      paramParcel.writeInt(this.mPort);
    }
    for (;;)
    {
      paramParcel.writeString(this.mExclusionList);
      paramParcel.writeStringArray(this.mParsedExclusionList);
      return;
      paramParcel.writeByte((byte)0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/ProxyInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */