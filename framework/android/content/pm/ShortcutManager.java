package android.content.pm;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import java.util.List;

public class ShortcutManager
{
  private static final String TAG = "ShortcutManager";
  private final Context mContext;
  private final IShortcutService mService;
  
  public ShortcutManager(Context paramContext)
  {
    this(paramContext, IShortcutService.Stub.asInterface(ServiceManager.getService("shortcut")));
  }
  
  public ShortcutManager(Context paramContext, IShortcutService paramIShortcutService)
  {
    this.mContext = paramContext;
    this.mService = paramIShortcutService;
  }
  
  public boolean addDynamicShortcuts(List<ShortcutInfo> paramList)
  {
    try
    {
      boolean bool = this.mService.addDynamicShortcuts(this.mContext.getPackageName(), new ParceledListSlice(paramList), injectMyUserId());
      return bool;
    }
    catch (RemoteException paramList)
    {
      throw paramList.rethrowFromSystemServer();
    }
  }
  
  public void disableShortcuts(List<String> paramList)
  {
    try
    {
      this.mService.disableShortcuts(this.mContext.getPackageName(), paramList, null, 0, injectMyUserId());
      return;
    }
    catch (RemoteException paramList)
    {
      throw paramList.rethrowFromSystemServer();
    }
  }
  
  public void disableShortcuts(List<String> paramList, int paramInt)
  {
    try
    {
      this.mService.disableShortcuts(this.mContext.getPackageName(), paramList, null, paramInt, injectMyUserId());
      return;
    }
    catch (RemoteException paramList)
    {
      throw paramList.rethrowFromSystemServer();
    }
  }
  
  public void disableShortcuts(List<String> paramList, CharSequence paramCharSequence)
  {
    try
    {
      this.mService.disableShortcuts(this.mContext.getPackageName(), paramList, paramCharSequence, 0, injectMyUserId());
      return;
    }
    catch (RemoteException paramList)
    {
      throw paramList.rethrowFromSystemServer();
    }
  }
  
  public void disableShortcuts(List<String> paramList, String paramString)
  {
    disableShortcuts(paramList, paramString);
  }
  
  public void enableShortcuts(List<String> paramList)
  {
    try
    {
      this.mService.enableShortcuts(this.mContext.getPackageName(), paramList, injectMyUserId());
      return;
    }
    catch (RemoteException paramList)
    {
      throw paramList.rethrowFromSystemServer();
    }
  }
  
  public List<ShortcutInfo> getDynamicShortcuts()
  {
    try
    {
      List localList = this.mService.getDynamicShortcuts(this.mContext.getPackageName(), injectMyUserId()).getList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getIconMaxHeight()
  {
    try
    {
      int i = this.mService.getIconMaxDimensions(this.mContext.getPackageName(), injectMyUserId());
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getIconMaxWidth()
  {
    try
    {
      int i = this.mService.getIconMaxDimensions(this.mContext.getPackageName(), injectMyUserId());
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<ShortcutInfo> getManifestShortcuts()
  {
    try
    {
      List localList = this.mService.getManifestShortcuts(this.mContext.getPackageName(), injectMyUserId()).getList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getMaxShortcutCountForActivity()
  {
    return getMaxShortcutCountPerActivity();
  }
  
  public int getMaxShortcutCountPerActivity()
  {
    try
    {
      int i = this.mService.getMaxShortcutCountPerActivity(this.mContext.getPackageName(), injectMyUserId());
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<ShortcutInfo> getPinnedShortcuts()
  {
    try
    {
      List localList = this.mService.getPinnedShortcuts(this.mContext.getPackageName(), injectMyUserId()).getList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public long getRateLimitResetTime()
  {
    try
    {
      long l = this.mService.getRateLimitResetTime(this.mContext.getPackageName(), injectMyUserId());
      return l;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getRemainingCallCount()
  {
    try
    {
      int i = this.mService.getRemainingCallCount(this.mContext.getPackageName(), injectMyUserId());
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  protected int injectMyUserId()
  {
    return UserHandle.myUserId();
  }
  
  public boolean isRateLimitingActive()
  {
    boolean bool = false;
    try
    {
      int i = this.mService.getRemainingCallCount(this.mContext.getPackageName(), injectMyUserId());
      if (i == 0) {
        bool = true;
      }
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void onApplicationActive(String paramString, int paramInt)
  {
    try
    {
      this.mService.onApplicationActive(paramString, paramInt);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void removeAllDynamicShortcuts()
  {
    try
    {
      this.mService.removeAllDynamicShortcuts(this.mContext.getPackageName(), injectMyUserId());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void removeDynamicShortcuts(List<String> paramList)
  {
    try
    {
      this.mService.removeDynamicShortcuts(this.mContext.getPackageName(), paramList, injectMyUserId());
      return;
    }
    catch (RemoteException paramList)
    {
      throw paramList.rethrowFromSystemServer();
    }
  }
  
  public void reportShortcutUsed(String paramString)
  {
    try
    {
      this.mService.reportShortcutUsed(this.mContext.getPackageName(), paramString, injectMyUserId());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean setDynamicShortcuts(List<ShortcutInfo> paramList)
  {
    try
    {
      boolean bool = this.mService.setDynamicShortcuts(this.mContext.getPackageName(), new ParceledListSlice(paramList), injectMyUserId());
      return bool;
    }
    catch (RemoteException paramList)
    {
      throw paramList.rethrowFromSystemServer();
    }
  }
  
  public boolean updateShortcuts(List<ShortcutInfo> paramList)
  {
    try
    {
      boolean bool = this.mService.updateShortcuts(this.mContext.getPackageName(), new ParceledListSlice(paramList), injectMyUserId());
      return bool;
    }
    catch (RemoteException paramList)
    {
      throw paramList.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/ShortcutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */