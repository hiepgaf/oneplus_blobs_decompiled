package android.content.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.os.ParcelFileDescriptor;
import java.util.List;

public abstract class ShortcutServiceInternal
{
  public abstract void addListener(ShortcutChangeListener paramShortcutChangeListener);
  
  public abstract Intent[] createShortcutIntents(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2);
  
  public abstract ParcelFileDescriptor getShortcutIconFd(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2);
  
  public abstract int getShortcutIconResId(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2);
  
  public abstract List<ShortcutInfo> getShortcuts(int paramInt1, String paramString1, long paramLong, String paramString2, List<String> paramList, ComponentName paramComponentName, int paramInt2, int paramInt3);
  
  public abstract boolean hasShortcutHostPermission(int paramInt, String paramString);
  
  public abstract boolean isPinnedByCaller(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2);
  
  public abstract void pinShortcuts(int paramInt1, String paramString1, String paramString2, List<String> paramList, int paramInt2);
  
  public static abstract interface ShortcutChangeListener
  {
    public abstract void onShortcutChanged(String paramString, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/ShortcutServiceInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */