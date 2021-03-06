package android.net.wifi.p2p;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.LruCache;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class WifiP2pGroupList
  implements Parcelable
{
  public static final Parcelable.Creator<WifiP2pGroupList> CREATOR = new Parcelable.Creator()
  {
    public WifiP2pGroupList createFromParcel(Parcel paramAnonymousParcel)
    {
      WifiP2pGroupList localWifiP2pGroupList = new WifiP2pGroupList();
      int j = paramAnonymousParcel.readInt();
      int i = 0;
      while (i < j)
      {
        localWifiP2pGroupList.add((WifiP2pGroup)paramAnonymousParcel.readParcelable(null));
        i += 1;
      }
      return localWifiP2pGroupList;
    }
    
    public WifiP2pGroupList[] newArray(int paramAnonymousInt)
    {
      return new WifiP2pGroupList[paramAnonymousInt];
    }
  };
  private static final int CREDENTIAL_MAX_NUM = 32;
  private boolean isClearCalled = false;
  private final LruCache<Integer, WifiP2pGroup> mGroups;
  private final GroupDeleteListener mListener;
  
  public WifiP2pGroupList()
  {
    this(null, null);
  }
  
  public WifiP2pGroupList(WifiP2pGroupList paramWifiP2pGroupList, GroupDeleteListener paramGroupDeleteListener)
  {
    this.mListener = paramGroupDeleteListener;
    this.mGroups = new LruCache(32)
    {
      protected void entryRemoved(boolean paramAnonymousBoolean, Integer paramAnonymousInteger, WifiP2pGroup paramAnonymousWifiP2pGroup1, WifiP2pGroup paramAnonymousWifiP2pGroup2)
      {
        if ((WifiP2pGroupList.-get1(WifiP2pGroupList.this) == null) || (WifiP2pGroupList.-get0(WifiP2pGroupList.this))) {
          return;
        }
        WifiP2pGroupList.-get1(WifiP2pGroupList.this).onDeleteGroup(paramAnonymousWifiP2pGroup1.getNetworkId());
      }
    };
    if (paramWifiP2pGroupList != null)
    {
      paramWifiP2pGroupList = paramWifiP2pGroupList.mGroups.snapshot().entrySet().iterator();
      while (paramWifiP2pGroupList.hasNext())
      {
        paramGroupDeleteListener = (Map.Entry)paramWifiP2pGroupList.next();
        this.mGroups.put((Integer)paramGroupDeleteListener.getKey(), (WifiP2pGroup)paramGroupDeleteListener.getValue());
      }
    }
  }
  
  public void add(WifiP2pGroup paramWifiP2pGroup)
  {
    this.mGroups.put(Integer.valueOf(paramWifiP2pGroup.getNetworkId()), paramWifiP2pGroup);
  }
  
  public boolean clear()
  {
    if (this.mGroups.size() == 0) {
      return false;
    }
    this.isClearCalled = true;
    this.mGroups.evictAll();
    this.isClearCalled = false;
    return true;
  }
  
  public boolean contains(int paramInt)
  {
    Iterator localIterator = this.mGroups.snapshot().values().iterator();
    while (localIterator.hasNext()) {
      if (paramInt == ((WifiP2pGroup)localIterator.next()).getNetworkId()) {
        return true;
      }
    }
    return false;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Collection<WifiP2pGroup> getGroupList()
  {
    return this.mGroups.snapshot().values();
  }
  
  public int getNetworkId(String paramString)
  {
    if (paramString == null) {
      return -1;
    }
    Iterator localIterator = this.mGroups.snapshot().values().iterator();
    while (localIterator.hasNext())
    {
      WifiP2pGroup localWifiP2pGroup = (WifiP2pGroup)localIterator.next();
      if (paramString.equalsIgnoreCase(localWifiP2pGroup.getOwner().deviceAddress))
      {
        this.mGroups.get(Integer.valueOf(localWifiP2pGroup.getNetworkId()));
        return localWifiP2pGroup.getNetworkId();
      }
    }
    return -1;
  }
  
  public int getNetworkId(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      return -1;
    }
    Iterator localIterator = this.mGroups.snapshot().values().iterator();
    while (localIterator.hasNext())
    {
      WifiP2pGroup localWifiP2pGroup = (WifiP2pGroup)localIterator.next();
      if ((paramString1.equalsIgnoreCase(localWifiP2pGroup.getOwner().deviceAddress)) && (paramString2.equals(localWifiP2pGroup.getNetworkName())))
      {
        this.mGroups.get(Integer.valueOf(localWifiP2pGroup.getNetworkId()));
        return localWifiP2pGroup.getNetworkId();
      }
    }
    return -1;
  }
  
  public String getOwnerAddr(int paramInt)
  {
    WifiP2pGroup localWifiP2pGroup = (WifiP2pGroup)this.mGroups.get(Integer.valueOf(paramInt));
    if (localWifiP2pGroup != null) {
      return localWifiP2pGroup.getOwner().deviceAddress;
    }
    return null;
  }
  
  public void remove(int paramInt)
  {
    this.mGroups.remove(Integer.valueOf(paramInt));
  }
  
  void remove(String paramString)
  {
    remove(getNetworkId(paramString));
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Iterator localIterator = this.mGroups.snapshot().values().iterator();
    while (localIterator.hasNext()) {
      localStringBuffer.append((WifiP2pGroup)localIterator.next()).append("\n");
    }
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    Object localObject = this.mGroups.snapshot().values();
    paramParcel.writeInt(((Collection)localObject).size());
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      paramParcel.writeParcelable((WifiP2pGroup)((Iterator)localObject).next(), paramInt);
    }
  }
  
  public static abstract interface GroupDeleteListener
  {
    public abstract void onDeleteGroup(int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/WifiP2pGroupList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */