package com.android.server.am;

import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import com.google.android.collect.Sets;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Iterator;

final class UriPermission
{
  private static final long INVALID_TIME = Long.MIN_VALUE;
  public static final int STRENGTH_GLOBAL = 2;
  public static final int STRENGTH_NONE = 0;
  public static final int STRENGTH_OWNED = 1;
  public static final int STRENGTH_PERSISTABLE = 3;
  private static final String TAG = "UriPermission";
  int globalModeFlags = 0;
  private ArraySet<UriPermissionOwner> mReadOwners;
  private ArraySet<UriPermissionOwner> mWriteOwners;
  int modeFlags = 0;
  int ownedModeFlags = 0;
  int persistableModeFlags = 0;
  long persistedCreateTime = Long.MIN_VALUE;
  int persistedModeFlags = 0;
  final String sourcePkg;
  private String stringName;
  final String targetPkg;
  final int targetUid;
  final int targetUserId;
  final ActivityManagerService.GrantUri uri;
  
  UriPermission(String paramString1, String paramString2, int paramInt, ActivityManagerService.GrantUri paramGrantUri)
  {
    this.targetUserId = UserHandle.getUserId(paramInt);
    this.sourcePkg = paramString1;
    this.targetPkg = paramString2;
    this.targetUid = paramInt;
    this.uri = paramGrantUri;
  }
  
  private void addReadOwner(UriPermissionOwner paramUriPermissionOwner)
  {
    if (this.mReadOwners == null)
    {
      this.mReadOwners = Sets.newArraySet();
      this.ownedModeFlags |= 0x1;
      updateModeFlags();
    }
    if (this.mReadOwners.add(paramUriPermissionOwner)) {
      paramUriPermissionOwner.addReadPermission(this);
    }
  }
  
  private void addWriteOwner(UriPermissionOwner paramUriPermissionOwner)
  {
    if (this.mWriteOwners == null)
    {
      this.mWriteOwners = Sets.newArraySet();
      this.ownedModeFlags |= 0x2;
      updateModeFlags();
    }
    if (this.mWriteOwners.add(paramUriPermissionOwner)) {
      paramUriPermissionOwner.addWritePermission(this);
    }
  }
  
  private void updateModeFlags()
  {
    int i = this.modeFlags;
    this.modeFlags = (this.ownedModeFlags | this.globalModeFlags | this.persistableModeFlags | this.persistedModeFlags);
    if ((Log.isLoggable("UriPermission", 2)) && (this.modeFlags != i)) {
      Slog.d("UriPermission", "Permission for " + this.targetPkg + " to " + this.uri + " is changing from 0x" + Integer.toHexString(i) + " to 0x" + Integer.toHexString(this.modeFlags), new Throwable());
    }
  }
  
  public android.content.UriPermission buildPersistedPublicApiObject()
  {
    return new android.content.UriPermission(this.uri.uri, this.persistedModeFlags, this.persistedCreateTime);
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("targetUserId=" + this.targetUserId);
    paramPrintWriter.print(" sourcePkg=" + this.sourcePkg);
    paramPrintWriter.println(" targetPkg=" + this.targetPkg);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mode=0x" + Integer.toHexString(this.modeFlags));
    paramPrintWriter.print(" owned=0x" + Integer.toHexString(this.ownedModeFlags));
    paramPrintWriter.print(" global=0x" + Integer.toHexString(this.globalModeFlags));
    paramPrintWriter.print(" persistable=0x" + Integer.toHexString(this.persistableModeFlags));
    paramPrintWriter.print(" persisted=0x" + Integer.toHexString(this.persistedModeFlags));
    if (this.persistedCreateTime != Long.MIN_VALUE) {
      paramPrintWriter.print(" persistedCreate=" + this.persistedCreateTime);
    }
    paramPrintWriter.println();
    Iterator localIterator;
    UriPermissionOwner localUriPermissionOwner;
    if (this.mReadOwners != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("readOwners:");
      localIterator = this.mReadOwners.iterator();
      while (localIterator.hasNext())
      {
        localUriPermissionOwner = (UriPermissionOwner)localIterator.next();
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("  * " + localUriPermissionOwner);
      }
    }
    if (this.mWriteOwners != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("writeOwners:");
      localIterator = this.mReadOwners.iterator();
      while (localIterator.hasNext())
      {
        localUriPermissionOwner = (UriPermissionOwner)localIterator.next();
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("  * " + localUriPermissionOwner);
      }
    }
  }
  
  public int getStrength(int paramInt)
  {
    paramInt &= 0x3;
    if ((this.persistableModeFlags & paramInt) == paramInt) {
      return 3;
    }
    if ((this.globalModeFlags & paramInt) == paramInt) {
      return 2;
    }
    if ((this.ownedModeFlags & paramInt) == paramInt) {
      return 1;
    }
    return 0;
  }
  
  void grantModes(int paramInt, UriPermissionOwner paramUriPermissionOwner)
  {
    int i = 0;
    if ((paramInt & 0x40) != 0) {
      i = 1;
    }
    paramInt &= 0x3;
    if (i != 0) {
      this.persistableModeFlags |= paramInt;
    }
    if (paramUriPermissionOwner == null) {
      this.globalModeFlags |= paramInt;
    }
    for (;;)
    {
      updateModeFlags();
      return;
      if ((paramInt & 0x1) != 0) {
        addReadOwner(paramUriPermissionOwner);
      }
      if ((paramInt & 0x2) != 0) {
        addWriteOwner(paramUriPermissionOwner);
      }
    }
  }
  
  void initPersistedModes(int paramInt, long paramLong)
  {
    paramInt &= 0x3;
    this.persistableModeFlags = paramInt;
    this.persistedModeFlags = paramInt;
    this.persistedCreateTime = paramLong;
    updateModeFlags();
  }
  
  boolean releasePersistableModes(int paramInt)
  {
    boolean bool = false;
    paramInt &= 0x3;
    int i = this.persistedModeFlags;
    this.persistableModeFlags &= paramInt;
    this.persistedModeFlags &= paramInt;
    if (this.persistedModeFlags == 0) {
      this.persistedCreateTime = Long.MIN_VALUE;
    }
    updateModeFlags();
    if (this.persistedModeFlags != i) {
      bool = true;
    }
    return bool;
  }
  
  void removeReadOwner(UriPermissionOwner paramUriPermissionOwner)
  {
    if (!this.mReadOwners.remove(paramUriPermissionOwner)) {
      Slog.wtf("UriPermission", "Unknown read owner " + paramUriPermissionOwner + " in " + this);
    }
    if (this.mReadOwners.size() == 0)
    {
      this.mReadOwners = null;
      this.ownedModeFlags &= 0xFFFFFFFE;
      updateModeFlags();
    }
  }
  
  void removeWriteOwner(UriPermissionOwner paramUriPermissionOwner)
  {
    if (!this.mWriteOwners.remove(paramUriPermissionOwner)) {
      Slog.wtf("UriPermission", "Unknown write owner " + paramUriPermissionOwner + " in " + this);
    }
    if (this.mWriteOwners.size() == 0)
    {
      this.mWriteOwners = null;
      this.ownedModeFlags &= 0xFFFFFFFD;
      updateModeFlags();
    }
  }
  
  boolean revokeModes(int paramInt, boolean paramBoolean)
  {
    boolean bool = false;
    if ((paramInt & 0x40) != 0) {}
    int j;
    Iterator localIterator;
    for (int i = 1;; i = 0)
    {
      paramInt &= 0x3;
      j = this.persistedModeFlags;
      if ((paramInt & 0x1) == 0) {
        break label135;
      }
      if (i != 0)
      {
        this.persistableModeFlags &= 0xFFFFFFFE;
        this.persistedModeFlags &= 0xFFFFFFFE;
      }
      this.globalModeFlags &= 0xFFFFFFFE;
      if ((this.mReadOwners == null) || (!paramBoolean)) {
        break label135;
      }
      this.ownedModeFlags &= 0xFFFFFFFE;
      localIterator = this.mReadOwners.iterator();
      while (localIterator.hasNext()) {
        ((UriPermissionOwner)localIterator.next()).removeReadPermission(this);
      }
    }
    this.mReadOwners = null;
    label135:
    if ((paramInt & 0x2) != 0)
    {
      if (i != 0)
      {
        this.persistableModeFlags &= 0xFFFFFFFD;
        this.persistedModeFlags &= 0xFFFFFFFD;
      }
      this.globalModeFlags &= 0xFFFFFFFD;
      if ((this.mWriteOwners != null) && (paramBoolean))
      {
        this.ownedModeFlags &= 0xFFFFFFFD;
        localIterator = this.mWriteOwners.iterator();
        while (localIterator.hasNext()) {
          ((UriPermissionOwner)localIterator.next()).removeWritePermission(this);
        }
        this.mWriteOwners = null;
      }
    }
    if (this.persistedModeFlags == 0) {
      this.persistedCreateTime = Long.MIN_VALUE;
    }
    updateModeFlags();
    paramBoolean = bool;
    if (this.persistedModeFlags != j) {
      paramBoolean = true;
    }
    return paramBoolean;
  }
  
  public Snapshot snapshot()
  {
    return new Snapshot(this, null);
  }
  
  boolean takePersistableModes(int paramInt)
  {
    boolean bool = false;
    paramInt &= 0x3;
    if ((this.persistableModeFlags & paramInt) != paramInt)
    {
      Slog.w("UriPermission", "Requested flags 0x" + Integer.toHexString(paramInt) + ", but only 0x" + Integer.toHexString(this.persistableModeFlags) + " are allowed");
      return false;
    }
    int i = this.persistedModeFlags;
    this.persistedModeFlags |= this.persistableModeFlags & paramInt;
    if (this.persistedModeFlags != 0) {
      this.persistedCreateTime = System.currentTimeMillis();
    }
    updateModeFlags();
    if (this.persistedModeFlags != i) {
      bool = true;
    }
    return bool;
  }
  
  public String toString()
  {
    if (this.stringName != null) {
      return this.stringName;
    }
    Object localObject = new StringBuilder(128);
    ((StringBuilder)localObject).append("UriPermission{");
    ((StringBuilder)localObject).append(Integer.toHexString(System.identityHashCode(this)));
    ((StringBuilder)localObject).append(' ');
    ((StringBuilder)localObject).append(this.uri);
    ((StringBuilder)localObject).append('}');
    localObject = ((StringBuilder)localObject).toString();
    this.stringName = ((String)localObject);
    return (String)localObject;
  }
  
  public static class PersistedTimeComparator
    implements Comparator<UriPermission>
  {
    public int compare(UriPermission paramUriPermission1, UriPermission paramUriPermission2)
    {
      return Long.compare(paramUriPermission1.persistedCreateTime, paramUriPermission2.persistedCreateTime);
    }
  }
  
  public static class Snapshot
  {
    final long persistedCreateTime;
    final int persistedModeFlags;
    final String sourcePkg;
    final String targetPkg;
    final int targetUserId;
    final ActivityManagerService.GrantUri uri;
    
    private Snapshot(UriPermission paramUriPermission)
    {
      this.targetUserId = paramUriPermission.targetUserId;
      this.sourcePkg = paramUriPermission.sourcePkg;
      this.targetPkg = paramUriPermission.targetPkg;
      this.uri = paramUriPermission.uri;
      this.persistedModeFlags = paramUriPermission.persistedModeFlags;
      this.persistedCreateTime = paramUriPermission.persistedCreateTime;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/UriPermission.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */