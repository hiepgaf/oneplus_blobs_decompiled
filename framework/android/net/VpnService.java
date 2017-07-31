package android.net;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.system.OsConstants;
import com.android.internal.net.VpnConfig;
import java.io.FileDescriptor;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class VpnService
  extends Service
{
  public static final String SERVICE_INTERFACE = "android.net.VpnService";
  
  private static void check(InetAddress paramInetAddress, int paramInt)
  {
    if (paramInetAddress.isLoopbackAddress()) {
      throw new IllegalArgumentException("Bad address");
    }
    if ((paramInetAddress instanceof Inet4Address))
    {
      if ((paramInt < 0) || (paramInt > 32)) {
        throw new IllegalArgumentException("Bad prefixLength");
      }
    }
    else if ((paramInetAddress instanceof Inet6Address))
    {
      if ((paramInt < 0) || (paramInt > 128)) {
        throw new IllegalArgumentException("Bad prefixLength");
      }
    }
    else {
      throw new IllegalArgumentException("Unsupported family");
    }
  }
  
  private static IConnectivityManager getService()
  {
    return IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
  }
  
  public static Intent prepare(Context paramContext)
  {
    try
    {
      boolean bool = getService().prepareVpn(paramContext.getPackageName(), null, UserHandle.myUserId());
      if (bool) {
        return null;
      }
    }
    catch (RemoteException paramContext) {}
    return VpnConfig.getIntentForConfirmation();
  }
  
  public static void prepareAndAuthorize(Context paramContext)
  {
    IConnectivityManager localIConnectivityManager = getService();
    paramContext = paramContext.getPackageName();
    try
    {
      int i = UserHandle.myUserId();
      if (!localIConnectivityManager.prepareVpn(paramContext, null, i)) {
        localIConnectivityManager.prepareVpn(null, paramContext, i);
      }
      localIConnectivityManager.setVpnPackageAuthorization(paramContext, i, true);
      return;
    }
    catch (RemoteException paramContext) {}
  }
  
  public boolean addAddress(InetAddress paramInetAddress, int paramInt)
  {
    check(paramInetAddress, paramInt);
    try
    {
      boolean bool = getService().addVpnAddress(paramInetAddress.getHostAddress(), paramInt);
      return bool;
    }
    catch (RemoteException paramInetAddress)
    {
      throw new IllegalStateException(paramInetAddress);
    }
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    if ((paramIntent != null) && ("android.net.VpnService".equals(paramIntent.getAction()))) {
      return new Callback(null);
    }
    return null;
  }
  
  public void onRevoke()
  {
    stopSelf();
  }
  
  public boolean protect(int paramInt)
  {
    return NetworkUtils.protectFromVpn(paramInt);
  }
  
  public boolean protect(DatagramSocket paramDatagramSocket)
  {
    return protect(paramDatagramSocket.getFileDescriptor$().getInt$());
  }
  
  public boolean protect(Socket paramSocket)
  {
    return protect(paramSocket.getFileDescriptor$().getInt$());
  }
  
  public boolean removeAddress(InetAddress paramInetAddress, int paramInt)
  {
    check(paramInetAddress, paramInt);
    try
    {
      boolean bool = getService().removeVpnAddress(paramInetAddress.getHostAddress(), paramInt);
      return bool;
    }
    catch (RemoteException paramInetAddress)
    {
      throw new IllegalStateException(paramInetAddress);
    }
  }
  
  public boolean setUnderlyingNetworks(Network[] paramArrayOfNetwork)
  {
    try
    {
      boolean bool = getService().setUnderlyingNetworksForVpn(paramArrayOfNetwork);
      return bool;
    }
    catch (RemoteException paramArrayOfNetwork)
    {
      throw new IllegalStateException(paramArrayOfNetwork);
    }
  }
  
  public class Builder
  {
    private final List<LinkAddress> mAddresses = new ArrayList();
    private final VpnConfig mConfig = new VpnConfig();
    private final List<RouteInfo> mRoutes = new ArrayList();
    
    public Builder()
    {
      this.mConfig.user = VpnService.this.getClass().getName();
    }
    
    private void verifyApp(String paramString)
      throws PackageManager.NameNotFoundException
    {
      IPackageManager localIPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
      try
      {
        localIPackageManager.getApplicationInfo(paramString, 0, UserHandle.getCallingUserId());
        return;
      }
      catch (RemoteException paramString)
      {
        throw new IllegalStateException(paramString);
      }
    }
    
    public Builder addAddress(String paramString, int paramInt)
    {
      return addAddress(InetAddress.parseNumericAddress(paramString), paramInt);
    }
    
    public Builder addAddress(InetAddress paramInetAddress, int paramInt)
    {
      VpnService.-wrap1(paramInetAddress, paramInt);
      if (paramInetAddress.isAnyLocalAddress()) {
        throw new IllegalArgumentException("Bad address");
      }
      this.mAddresses.add(new LinkAddress(paramInetAddress, paramInt));
      this.mConfig.updateAllowedFamilies(paramInetAddress);
      return this;
    }
    
    public Builder addAllowedApplication(String paramString)
      throws PackageManager.NameNotFoundException
    {
      if (this.mConfig.disallowedApplications != null) {
        throw new UnsupportedOperationException("addDisallowedApplication already called");
      }
      verifyApp(paramString);
      if (this.mConfig.allowedApplications == null) {
        this.mConfig.allowedApplications = new ArrayList();
      }
      this.mConfig.allowedApplications.add(paramString);
      return this;
    }
    
    public Builder addDisallowedApplication(String paramString)
      throws PackageManager.NameNotFoundException
    {
      if (this.mConfig.allowedApplications != null) {
        throw new UnsupportedOperationException("addAllowedApplication already called");
      }
      verifyApp(paramString);
      if (this.mConfig.disallowedApplications == null) {
        this.mConfig.disallowedApplications = new ArrayList();
      }
      this.mConfig.disallowedApplications.add(paramString);
      return this;
    }
    
    public Builder addDnsServer(String paramString)
    {
      return addDnsServer(InetAddress.parseNumericAddress(paramString));
    }
    
    public Builder addDnsServer(InetAddress paramInetAddress)
    {
      if ((paramInetAddress.isLoopbackAddress()) || (paramInetAddress.isAnyLocalAddress())) {
        throw new IllegalArgumentException("Bad address");
      }
      if (this.mConfig.dnsServers == null) {
        this.mConfig.dnsServers = new ArrayList();
      }
      this.mConfig.dnsServers.add(paramInetAddress.getHostAddress());
      return this;
    }
    
    public Builder addRoute(String paramString, int paramInt)
    {
      return addRoute(InetAddress.parseNumericAddress(paramString), paramInt);
    }
    
    public Builder addRoute(InetAddress paramInetAddress, int paramInt)
    {
      VpnService.-wrap1(paramInetAddress, paramInt);
      int i = paramInt / 8;
      byte[] arrayOfByte = paramInetAddress.getAddress();
      if (i < arrayOfByte.length)
      {
        arrayOfByte[i] = ((byte)(arrayOfByte[i] << paramInt % 8));
        while (i < arrayOfByte.length)
        {
          if (arrayOfByte[i] != 0) {
            throw new IllegalArgumentException("Bad address");
          }
          i += 1;
        }
      }
      this.mRoutes.add(new RouteInfo(new IpPrefix(paramInetAddress, paramInt), null));
      this.mConfig.updateAllowedFamilies(paramInetAddress);
      return this;
    }
    
    public Builder addSearchDomain(String paramString)
    {
      if (this.mConfig.searchDomains == null) {
        this.mConfig.searchDomains = new ArrayList();
      }
      this.mConfig.searchDomains.add(paramString);
      return this;
    }
    
    public Builder allowBypass()
    {
      this.mConfig.allowBypass = true;
      return this;
    }
    
    public Builder allowFamily(int paramInt)
    {
      if (paramInt == OsConstants.AF_INET)
      {
        this.mConfig.allowIPv4 = true;
        return this;
      }
      if (paramInt == OsConstants.AF_INET6)
      {
        this.mConfig.allowIPv6 = true;
        return this;
      }
      throw new IllegalArgumentException(paramInt + " is neither " + OsConstants.AF_INET + " nor " + OsConstants.AF_INET6);
    }
    
    public ParcelFileDescriptor establish()
    {
      this.mConfig.addresses = this.mAddresses;
      this.mConfig.routes = this.mRoutes;
      try
      {
        ParcelFileDescriptor localParcelFileDescriptor = VpnService.-wrap0().establishVpn(this.mConfig);
        return localParcelFileDescriptor;
      }
      catch (RemoteException localRemoteException)
      {
        throw new IllegalStateException(localRemoteException);
      }
    }
    
    public Builder setBlocking(boolean paramBoolean)
    {
      this.mConfig.blocking = paramBoolean;
      return this;
    }
    
    public Builder setConfigureIntent(PendingIntent paramPendingIntent)
    {
      this.mConfig.configureIntent = paramPendingIntent;
      return this;
    }
    
    public Builder setMtu(int paramInt)
    {
      if (paramInt <= 0) {
        throw new IllegalArgumentException("Bad mtu");
      }
      this.mConfig.mtu = paramInt;
      return this;
    }
    
    public Builder setSession(String paramString)
    {
      this.mConfig.session = paramString;
      return this;
    }
    
    public Builder setUnderlyingNetworks(Network[] paramArrayOfNetwork)
    {
      Network[] arrayOfNetwork = null;
      VpnConfig localVpnConfig = this.mConfig;
      if (paramArrayOfNetwork != null) {
        arrayOfNetwork = (Network[])paramArrayOfNetwork.clone();
      }
      localVpnConfig.underlyingNetworks = arrayOfNetwork;
      return this;
    }
  }
  
  private class Callback
    extends Binder
  {
    private Callback() {}
    
    protected boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    {
      if (paramInt1 == 16777215)
      {
        VpnService.this.onRevoke();
        return true;
      }
      return false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/VpnService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */