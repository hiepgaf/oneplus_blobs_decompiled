package com.android.server.am;

import android.os.Binder;
import android.os.IBinder;
import android.util.ArraySet;
import com.google.android.collect.Sets;
import java.io.PrintWriter;
import java.util.Iterator;

final class UriPermissionOwner
{
  Binder externalToken;
  private ArraySet<UriPermission> mReadPerms;
  private ArraySet<UriPermission> mWritePerms;
  final Object owner;
  final ActivityManagerService service;
  
  UriPermissionOwner(ActivityManagerService paramActivityManagerService, Object paramObject)
  {
    this.service = paramActivityManagerService;
    this.owner = paramObject;
  }
  
  static UriPermissionOwner fromExternalToken(IBinder paramIBinder)
  {
    if ((paramIBinder instanceof ExternalToken)) {
      return ((ExternalToken)paramIBinder).getOwner();
    }
    return null;
  }
  
  public void addReadPermission(UriPermission paramUriPermission)
  {
    if (this.mReadPerms == null) {
      this.mReadPerms = Sets.newArraySet();
    }
    this.mReadPerms.add(paramUriPermission);
  }
  
  public void addWritePermission(UriPermission paramUriPermission)
  {
    if (this.mWritePerms == null) {
      this.mWritePerms = Sets.newArraySet();
    }
    this.mWritePerms.add(paramUriPermission);
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    if (this.mReadPerms != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("readUriPermissions=");
      paramPrintWriter.println(this.mReadPerms);
    }
    if (this.mWritePerms != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("writeUriPermissions=");
      paramPrintWriter.println(this.mWritePerms);
    }
  }
  
  Binder getExternalTokenLocked()
  {
    if (this.externalToken == null) {
      this.externalToken = new ExternalToken();
    }
    return this.externalToken;
  }
  
  public void removeReadPermission(UriPermission paramUriPermission)
  {
    this.mReadPerms.remove(paramUriPermission);
    if (this.mReadPerms.isEmpty()) {
      this.mReadPerms = null;
    }
  }
  
  void removeUriPermissionLocked(ActivityManagerService.GrantUri paramGrantUri, int paramInt)
  {
    Iterator localIterator;
    UriPermission localUriPermission;
    if (((paramInt & 0x1) != 0) && (this.mReadPerms != null))
    {
      localIterator = this.mReadPerms.iterator();
      while (localIterator.hasNext())
      {
        localUriPermission = (UriPermission)localIterator.next();
        if ((paramGrantUri == null) || (paramGrantUri.equals(localUriPermission.uri)))
        {
          localUriPermission.removeReadOwner(this);
          this.service.removeUriPermissionIfNeededLocked(localUriPermission);
          localIterator.remove();
        }
      }
      if (this.mReadPerms.isEmpty()) {
        this.mReadPerms = null;
      }
    }
    if (((paramInt & 0x2) != 0) && (this.mWritePerms != null))
    {
      localIterator = this.mWritePerms.iterator();
      while (localIterator.hasNext())
      {
        localUriPermission = (UriPermission)localIterator.next();
        if ((paramGrantUri == null) || (paramGrantUri.equals(localUriPermission.uri)))
        {
          localUriPermission.removeWriteOwner(this);
          this.service.removeUriPermissionIfNeededLocked(localUriPermission);
          localIterator.remove();
        }
      }
      if (this.mWritePerms.isEmpty()) {
        this.mWritePerms = null;
      }
    }
  }
  
  void removeUriPermissionsLocked()
  {
    removeUriPermissionsLocked(3);
  }
  
  void removeUriPermissionsLocked(int paramInt)
  {
    removeUriPermissionLocked(null, paramInt);
  }
  
  public void removeWritePermission(UriPermission paramUriPermission)
  {
    this.mWritePerms.remove(paramUriPermission);
    if (this.mWritePerms.isEmpty()) {
      this.mWritePerms = null;
    }
  }
  
  public String toString()
  {
    return this.owner.toString();
  }
  
  class ExternalToken
    extends Binder
  {
    ExternalToken() {}
    
    UriPermissionOwner getOwner()
    {
      return UriPermissionOwner.this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/UriPermissionOwner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */