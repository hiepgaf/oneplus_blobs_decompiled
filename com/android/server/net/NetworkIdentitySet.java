package com.android.server.net;

import android.net.NetworkIdentity;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class NetworkIdentitySet
  extends HashSet<NetworkIdentity>
  implements Comparable<NetworkIdentitySet>
{
  private static final int VERSION_ADD_METERED = 4;
  private static final int VERSION_ADD_NETWORK_ID = 3;
  private static final int VERSION_ADD_ROAMING = 2;
  private static final int VERSION_INIT = 1;
  
  public NetworkIdentitySet() {}
  
  public NetworkIdentitySet(DataInputStream paramDataInputStream)
    throws IOException
  {
    int j = paramDataInputStream.readInt();
    int k = paramDataInputStream.readInt();
    int i = 0;
    if (i < k)
    {
      if (j <= 1) {
        paramDataInputStream.readInt();
      }
      int m = paramDataInputStream.readInt();
      int n = paramDataInputStream.readInt();
      String str2 = readOptionalString(paramDataInputStream);
      String str1;
      label62:
      boolean bool2;
      label73:
      boolean bool1;
      if (j >= 3)
      {
        str1 = readOptionalString(paramDataInputStream);
        if (j < 2) {
          break label121;
        }
        bool2 = paramDataInputStream.readBoolean();
        if (j < 4) {
          break label127;
        }
        bool1 = paramDataInputStream.readBoolean();
      }
      for (;;)
      {
        add(new NetworkIdentity(m, n, str2, str1, bool2, bool1));
        i += 1;
        break;
        str1 = null;
        break label62;
        label121:
        bool2 = false;
        break label73;
        label127:
        if (m == 0) {
          bool1 = true;
        } else {
          bool1 = false;
        }
      }
    }
  }
  
  private static String readOptionalString(DataInputStream paramDataInputStream)
    throws IOException
  {
    if (paramDataInputStream.readByte() != 0) {
      return paramDataInputStream.readUTF();
    }
    return null;
  }
  
  private static void writeOptionalString(DataOutputStream paramDataOutputStream, String paramString)
    throws IOException
  {
    if (paramString != null)
    {
      paramDataOutputStream.writeByte(1);
      paramDataOutputStream.writeUTF(paramString);
      return;
    }
    paramDataOutputStream.writeByte(0);
  }
  
  public int compareTo(NetworkIdentitySet paramNetworkIdentitySet)
  {
    if (isEmpty()) {
      return -1;
    }
    if (paramNetworkIdentitySet.isEmpty()) {
      return 1;
    }
    return ((NetworkIdentity)iterator().next()).compareTo((NetworkIdentity)paramNetworkIdentitySet.iterator().next());
  }
  
  public boolean isAnyMemberRoaming()
  {
    if (isEmpty()) {
      return false;
    }
    Iterator localIterator = iterator();
    while (localIterator.hasNext()) {
      if (((NetworkIdentity)localIterator.next()).getRoaming()) {
        return true;
      }
    }
    return false;
  }
  
  public void writeToStream(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeInt(4);
    paramDataOutputStream.writeInt(size());
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      NetworkIdentity localNetworkIdentity = (NetworkIdentity)localIterator.next();
      paramDataOutputStream.writeInt(localNetworkIdentity.getType());
      paramDataOutputStream.writeInt(localNetworkIdentity.getSubType());
      writeOptionalString(paramDataOutputStream, localNetworkIdentity.getSubscriberId());
      writeOptionalString(paramDataOutputStream, localNetworkIdentity.getNetworkId());
      paramDataOutputStream.writeBoolean(localNetworkIdentity.getRoaming());
      paramDataOutputStream.writeBoolean(localNetworkIdentity.getMetered());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/net/NetworkIdentitySet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */