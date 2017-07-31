package com.android.server.accounts;

import android.accounts.AuthenticatorDescription;
import android.content.pm.RegisteredServicesCache.ServiceInfo;
import android.content.pm.RegisteredServicesCacheListener;
import android.os.Handler;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collection;

public abstract interface IAccountAuthenticatorCache
{
  public abstract void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString, int paramInt);
  
  public abstract Collection<RegisteredServicesCache.ServiceInfo<AuthenticatorDescription>> getAllServices(int paramInt);
  
  public abstract RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> getServiceInfo(AuthenticatorDescription paramAuthenticatorDescription, int paramInt);
  
  public abstract void invalidateCache(int paramInt);
  
  public abstract void setListener(RegisteredServicesCacheListener<AuthenticatorDescription> paramRegisteredServicesCacheListener, Handler paramHandler);
  
  public abstract void updateServices(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accounts/IAccountAuthenticatorCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */