package android.os;

import android.content.pm.UserInfo;
import android.graphics.Bitmap;

public abstract class UserManagerInternal
{
  public abstract void addUserRestrictionsListener(UserRestrictionsListener paramUserRestrictionsListener);
  
  public abstract UserInfo createUserEvenWhenDisallowed(String paramString, int paramInt);
  
  public abstract Bundle getBaseUserRestrictions(int paramInt);
  
  public abstract boolean getUserRestriction(int paramInt, String paramString);
  
  public abstract boolean isUserRunning(int paramInt);
  
  public abstract boolean isUserUnlockingOrUnlocked(int paramInt);
  
  public abstract void onEphemeralUserStop(int paramInt);
  
  public abstract void removeAllUsers();
  
  public abstract void removeUserRestrictionsListener(UserRestrictionsListener paramUserRestrictionsListener);
  
  public abstract void removeUserState(int paramInt);
  
  public abstract void setBaseUserRestrictionsByDpmsForMigration(int paramInt, Bundle paramBundle);
  
  public abstract void setDeviceManaged(boolean paramBoolean);
  
  public abstract void setDevicePolicyUserRestrictions(int paramInt, Bundle paramBundle1, Bundle paramBundle2);
  
  public abstract void setForceEphemeralUsers(boolean paramBoolean);
  
  public abstract void setUserIcon(int paramInt, Bitmap paramBitmap);
  
  public abstract void setUserManaged(int paramInt, boolean paramBoolean);
  
  public abstract void setUserState(int paramInt1, int paramInt2);
  
  public static abstract interface UserRestrictionsListener
  {
    public abstract void onUserRestrictionsChanged(int paramInt, Bundle paramBundle1, Bundle paramBundle2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/UserManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */