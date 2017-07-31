package android.os;

import android.content.IntentSender;
import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;

public abstract interface IUserManager
  extends IInterface
{
  public abstract boolean canAddMoreManagedProfiles(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean canHaveRestrictedProfile(int paramInt)
    throws RemoteException;
  
  public abstract void clearSeedAccountData()
    throws RemoteException;
  
  public abstract UserInfo createProfileForUser(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract UserInfo createRestrictedProfile(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract UserInfo createUser(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract Bundle getApplicationRestrictions(String paramString)
    throws RemoteException;
  
  public abstract Bundle getApplicationRestrictionsForUser(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getCredentialOwnerProfile(int paramInt)
    throws RemoteException;
  
  public abstract Bundle getDefaultGuestRestrictions()
    throws RemoteException;
  
  public abstract UserInfo getPrimaryUser()
    throws RemoteException;
  
  public abstract int[] getProfileIds(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract UserInfo getProfileParent(int paramInt)
    throws RemoteException;
  
  public abstract List<UserInfo> getProfiles(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract String getSeedAccountName()
    throws RemoteException;
  
  public abstract PersistableBundle getSeedAccountOptions()
    throws RemoteException;
  
  public abstract String getSeedAccountType()
    throws RemoteException;
  
  public abstract String getUserAccount(int paramInt)
    throws RemoteException;
  
  public abstract long getUserCreationTime(int paramInt)
    throws RemoteException;
  
  public abstract int getUserHandle(int paramInt)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor getUserIcon(int paramInt)
    throws RemoteException;
  
  public abstract UserInfo getUserInfo(int paramInt)
    throws RemoteException;
  
  public abstract int getUserRestrictionSource(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract Bundle getUserRestrictions(int paramInt)
    throws RemoteException;
  
  public abstract int getUserSerialNumber(int paramInt)
    throws RemoteException;
  
  public abstract List<UserInfo> getUsers(boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean hasBaseUserRestriction(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean hasUserRestriction(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean isDemoUser(int paramInt)
    throws RemoteException;
  
  public abstract boolean isManagedProfile(int paramInt)
    throws RemoteException;
  
  public abstract boolean isQuietModeEnabled(int paramInt)
    throws RemoteException;
  
  public abstract boolean isRestricted()
    throws RemoteException;
  
  public abstract boolean isSameProfileGroup(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean markGuestForDeletion(int paramInt)
    throws RemoteException;
  
  public abstract boolean removeUser(int paramInt)
    throws RemoteException;
  
  public abstract void setApplicationRestrictions(String paramString, Bundle paramBundle, int paramInt)
    throws RemoteException;
  
  public abstract void setDefaultGuestRestrictions(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void setQuietModeEnabled(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setSeedAccountData(int paramInt, String paramString1, String paramString2, PersistableBundle paramPersistableBundle, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setUserAccount(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void setUserEnabled(int paramInt)
    throws RemoteException;
  
  public abstract void setUserIcon(int paramInt, Bitmap paramBitmap)
    throws RemoteException;
  
  public abstract void setUserName(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void setUserRestriction(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract boolean someUserHasSeedAccount(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract boolean trySetQuietModeDisabled(int paramInt, IntentSender paramIntentSender)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IUserManager
  {
    private static final String DESCRIPTOR = "android.os.IUserManager";
    static final int TRANSACTION_canAddMoreManagedProfiles = 14;
    static final int TRANSACTION_canHaveRestrictedProfile = 22;
    static final int TRANSACTION_clearSeedAccountData = 43;
    static final int TRANSACTION_createProfileForUser = 3;
    static final int TRANSACTION_createRestrictedProfile = 4;
    static final int TRANSACTION_createUser = 2;
    static final int TRANSACTION_getApplicationRestrictions = 31;
    static final int TRANSACTION_getApplicationRestrictionsForUser = 32;
    static final int TRANSACTION_getCredentialOwnerProfile = 1;
    static final int TRANSACTION_getDefaultGuestRestrictions = 34;
    static final int TRANSACTION_getPrimaryUser = 10;
    static final int TRANSACTION_getProfileIds = 13;
    static final int TRANSACTION_getProfileParent = 15;
    static final int TRANSACTION_getProfiles = 12;
    static final int TRANSACTION_getSeedAccountName = 40;
    static final int TRANSACTION_getSeedAccountOptions = 42;
    static final int TRANSACTION_getSeedAccountType = 41;
    static final int TRANSACTION_getUserAccount = 18;
    static final int TRANSACTION_getUserCreationTime = 20;
    static final int TRANSACTION_getUserHandle = 24;
    static final int TRANSACTION_getUserIcon = 9;
    static final int TRANSACTION_getUserInfo = 17;
    static final int TRANSACTION_getUserRestrictionSource = 25;
    static final int TRANSACTION_getUserRestrictions = 26;
    static final int TRANSACTION_getUserSerialNumber = 23;
    static final int TRANSACTION_getUsers = 11;
    static final int TRANSACTION_hasBaseUserRestriction = 27;
    static final int TRANSACTION_hasUserRestriction = 28;
    static final int TRANSACTION_isDemoUser = 46;
    static final int TRANSACTION_isManagedProfile = 45;
    static final int TRANSACTION_isQuietModeEnabled = 37;
    static final int TRANSACTION_isRestricted = 21;
    static final int TRANSACTION_isSameProfileGroup = 16;
    static final int TRANSACTION_markGuestForDeletion = 35;
    static final int TRANSACTION_removeUser = 6;
    static final int TRANSACTION_setApplicationRestrictions = 30;
    static final int TRANSACTION_setDefaultGuestRestrictions = 33;
    static final int TRANSACTION_setQuietModeEnabled = 36;
    static final int TRANSACTION_setSeedAccountData = 39;
    static final int TRANSACTION_setUserAccount = 19;
    static final int TRANSACTION_setUserEnabled = 5;
    static final int TRANSACTION_setUserIcon = 8;
    static final int TRANSACTION_setUserName = 7;
    static final int TRANSACTION_setUserRestriction = 29;
    static final int TRANSACTION_someUserHasSeedAccount = 44;
    static final int TRANSACTION_trySetQuietModeDisabled = 38;
    
    public Stub()
    {
      attachInterface(this, "android.os.IUserManager");
    }
    
    public static IUserManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.IUserManager");
      if ((localIInterface != null) && ((localIInterface instanceof IUserManager))) {
        return (IUserManager)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      label981:
      Object localObject;
      String str1;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.os.IUserManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramInt1 = getCredentialOwnerProfile(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = createUser(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 3: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = createProfileForUser(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 4: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = createRestrictedProfile(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 5: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        setUserEnabled(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        bool = removeUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        setUserName(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bitmap)Bitmap.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setUserIcon(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = getUserIcon(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 10: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = getPrimaryUser();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 11: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          paramParcel1 = getUsers(bool);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedList(paramParcel1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          paramParcel1 = getProfiles(paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedList(paramParcel1);
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          paramParcel1 = getProfileIds(paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeIntArray(paramParcel1);
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = canAddMoreManagedProfiles(paramInt1, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label981;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 15: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = getProfileParent(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 16: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        bool = isSameProfileGroup(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 17: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = getUserInfo(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 18: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = getUserAccount(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        setUserAccount(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        long l = getUserCreationTime(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        bool = isRestricted();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 22: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        bool = canHaveRestrictedProfile(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 23: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramInt1 = getUserSerialNumber(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 24: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramInt1 = getUserHandle(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 25: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramInt1 = getUserRestrictionSource(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 26: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = getUserRestrictions(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 27: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        bool = hasBaseUserRestriction(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 28: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        bool = hasUserRestriction(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 29: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setUserRestriction((String)localObject, bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 30: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        str1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          setApplicationRestrictions(str1, (Bundle)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 31: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = getApplicationRestrictions(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 32: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = getApplicationRestrictionsForUser(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 33: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setDefaultGuestRestrictions(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 34: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = getDefaultGuestRestrictions();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 35: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        bool = markGuestForDeletion(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 36: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setQuietModeEnabled(paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 37: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        bool = isQuietModeEnabled(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 38: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (IntentSender)IntentSender.CREATOR.createFromParcel(paramParcel1);
          bool = trySetQuietModeDisabled(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1938;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 39: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramInt1 = paramParcel1.readInt();
        str1 = paramParcel1.readString();
        String str2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          localObject = (PersistableBundle)PersistableBundle.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2022;
          }
        }
        for (bool = true;; bool = false)
        {
          setSeedAccountData(paramInt1, str1, str2, (PersistableBundle)localObject, bool);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 40: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = getSeedAccountName();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 41: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = getSeedAccountType();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 42: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        paramParcel1 = getSeedAccountOptions();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 43: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        clearSeedAccountData();
        paramParcel2.writeNoException();
        return true;
      case 44: 
        paramParcel1.enforceInterface("android.os.IUserManager");
        bool = someUserHasSeedAccount(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 45: 
        label1938:
        label2022:
        paramParcel1.enforceInterface("android.os.IUserManager");
        bool = isManagedProfile(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.os.IUserManager");
      boolean bool = isDemoUser(paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IUserManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      /* Error */
      public boolean canAddMoreManagedProfiles(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_3
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 34
        //   16: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload 4
        //   21: iload_1
        //   22: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   25: iload_3
        //   26: istore_1
        //   27: iload_2
        //   28: ifeq +5 -> 33
        //   31: iconst_1
        //   32: istore_1
        //   33: aload 4
        //   35: iload_1
        //   36: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   39: aload_0
        //   40: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   43: bipush 14
        //   45: aload 4
        //   47: aload 5
        //   49: iconst_0
        //   50: invokeinterface 48 5 0
        //   55: pop
        //   56: aload 5
        //   58: invokevirtual 51	android/os/Parcel:readException	()V
        //   61: aload 5
        //   63: invokevirtual 55	android/os/Parcel:readInt	()I
        //   66: istore_1
        //   67: iload_1
        //   68: ifeq +17 -> 85
        //   71: iconst_1
        //   72: istore_2
        //   73: aload 5
        //   75: invokevirtual 58	android/os/Parcel:recycle	()V
        //   78: aload 4
        //   80: invokevirtual 58	android/os/Parcel:recycle	()V
        //   83: iload_2
        //   84: ireturn
        //   85: iconst_0
        //   86: istore_2
        //   87: goto -14 -> 73
        //   90: astore 6
        //   92: aload 5
        //   94: invokevirtual 58	android/os/Parcel:recycle	()V
        //   97: aload 4
        //   99: invokevirtual 58	android/os/Parcel:recycle	()V
        //   102: aload 6
        //   104: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	105	0	this	Proxy
        //   0	105	1	paramInt	int
        //   0	105	2	paramBoolean	boolean
        //   1	25	3	i	int
        //   5	93	4	localParcel1	Parcel
        //   10	83	5	localParcel2	Parcel
        //   90	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   12	25	90	finally
        //   33	67	90	finally
      }
      
      /* Error */
      public boolean canHaveRestrictedProfile(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 22
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 58	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 58	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 58	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 58	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      public void clearSeedAccountData()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          this.mRemote.transact(43, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public UserInfo createProfileForUser(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   29: aload 4
        //   31: iload_3
        //   32: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_3
        //   40: aload 4
        //   42: aload 5
        //   44: iconst_0
        //   45: invokeinterface 48 5 0
        //   50: pop
        //   51: aload 5
        //   53: invokevirtual 51	android/os/Parcel:readException	()V
        //   56: aload 5
        //   58: invokevirtual 55	android/os/Parcel:readInt	()I
        //   61: ifeq +29 -> 90
        //   64: getstatic 73	android/content/pm/UserInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   67: aload 5
        //   69: invokeinterface 79 2 0
        //   74: checkcast 69	android/content/pm/UserInfo
        //   77: astore_1
        //   78: aload 5
        //   80: invokevirtual 58	android/os/Parcel:recycle	()V
        //   83: aload 4
        //   85: invokevirtual 58	android/os/Parcel:recycle	()V
        //   88: aload_1
        //   89: areturn
        //   90: aconst_null
        //   91: astore_1
        //   92: goto -14 -> 78
        //   95: astore_1
        //   96: aload 5
        //   98: invokevirtual 58	android/os/Parcel:recycle	()V
        //   101: aload 4
        //   103: invokevirtual 58	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramString	String
        //   0	108	2	paramInt1	int
        //   0	108	3	paramInt2	int
        //   3	99	4	localParcel1	Parcel
        //   8	89	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	78	95	finally
      }
      
      /* Error */
      public UserInfo createRestrictedProfile(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_4
        //   30: aload_3
        //   31: aload 4
        //   33: iconst_0
        //   34: invokeinterface 48 5 0
        //   39: pop
        //   40: aload 4
        //   42: invokevirtual 51	android/os/Parcel:readException	()V
        //   45: aload 4
        //   47: invokevirtual 55	android/os/Parcel:readInt	()I
        //   50: ifeq +28 -> 78
        //   53: getstatic 73	android/content/pm/UserInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   56: aload 4
        //   58: invokeinterface 79 2 0
        //   63: checkcast 69	android/content/pm/UserInfo
        //   66: astore_1
        //   67: aload 4
        //   69: invokevirtual 58	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 58	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: areturn
        //   78: aconst_null
        //   79: astore_1
        //   80: goto -13 -> 67
        //   83: astore_1
        //   84: aload 4
        //   86: invokevirtual 58	android/os/Parcel:recycle	()V
        //   89: aload_3
        //   90: invokevirtual 58	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramString	String
        //   0	95	2	paramInt	int
        //   3	87	3	localParcel1	Parcel
        //   7	78	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	67	83	finally
      }
      
      /* Error */
      public UserInfo createUser(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_2
        //   30: aload_3
        //   31: aload 4
        //   33: iconst_0
        //   34: invokeinterface 48 5 0
        //   39: pop
        //   40: aload 4
        //   42: invokevirtual 51	android/os/Parcel:readException	()V
        //   45: aload 4
        //   47: invokevirtual 55	android/os/Parcel:readInt	()I
        //   50: ifeq +28 -> 78
        //   53: getstatic 73	android/content/pm/UserInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   56: aload 4
        //   58: invokeinterface 79 2 0
        //   63: checkcast 69	android/content/pm/UserInfo
        //   66: astore_1
        //   67: aload 4
        //   69: invokevirtual 58	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 58	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: areturn
        //   78: aconst_null
        //   79: astore_1
        //   80: goto -13 -> 67
        //   83: astore_1
        //   84: aload 4
        //   86: invokevirtual 58	android/os/Parcel:recycle	()V
        //   89: aload_3
        //   90: invokevirtual 58	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramString	String
        //   0	95	2	paramInt	int
        //   3	87	3	localParcel1	Parcel
        //   7	78	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	67	83	finally
      }
      
      /* Error */
      public Bundle getApplicationRestrictions(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: bipush 31
        //   25: aload_2
        //   26: aload_3
        //   27: iconst_0
        //   28: invokeinterface 48 5 0
        //   33: pop
        //   34: aload_3
        //   35: invokevirtual 51	android/os/Parcel:readException	()V
        //   38: aload_3
        //   39: invokevirtual 55	android/os/Parcel:readInt	()I
        //   42: ifeq +26 -> 68
        //   45: getstatic 87	android/os/Bundle:CREATOR	Landroid/os/Parcelable$Creator;
        //   48: aload_3
        //   49: invokeinterface 79 2 0
        //   54: checkcast 86	android/os/Bundle
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 58	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: invokevirtual 58	android/os/Parcel:recycle	()V
        //   66: aload_1
        //   67: areturn
        //   68: aconst_null
        //   69: astore_1
        //   70: goto -12 -> 58
        //   73: astore_1
        //   74: aload_3
        //   75: invokevirtual 58	android/os/Parcel:recycle	()V
        //   78: aload_2
        //   79: invokevirtual 58	android/os/Parcel:recycle	()V
        //   82: aload_1
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	Proxy
        //   0	84	1	paramString	String
        //   3	76	2	localParcel1	Parcel
        //   7	68	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	58	73	finally
      }
      
      /* Error */
      public Bundle getApplicationRestrictionsForUser(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 32
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_0
        //   35: invokeinterface 48 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 51	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 55	android/os/Parcel:readInt	()I
        //   51: ifeq +28 -> 79
        //   54: getstatic 87	android/os/Bundle:CREATOR	Landroid/os/Parcelable$Creator;
        //   57: aload 4
        //   59: invokeinterface 79 2 0
        //   64: checkcast 86	android/os/Bundle
        //   67: astore_1
        //   68: aload 4
        //   70: invokevirtual 58	android/os/Parcel:recycle	()V
        //   73: aload_3
        //   74: invokevirtual 58	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: areturn
        //   79: aconst_null
        //   80: astore_1
        //   81: goto -13 -> 68
        //   84: astore_1
        //   85: aload 4
        //   87: invokevirtual 58	android/os/Parcel:recycle	()V
        //   90: aload_3
        //   91: invokevirtual 58	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString	String
        //   0	96	2	paramInt	int
        //   3	88	3	localParcel1	Parcel
        //   7	79	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	68	84	finally
      }
      
      public int getCredentialOwnerProfile(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public Bundle getDefaultGuestRestrictions()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 34
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 48 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 51	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 55	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 87	android/os/Bundle:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 79 2 0
        //   49: checkcast 86	android/os/Bundle
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 58	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 58	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 58	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 58	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localBundle	Bundle
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.os.IUserManager";
      }
      
      /* Error */
      public UserInfo getPrimaryUser()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 10
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 48 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 51	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 55	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 73	android/content/pm/UserInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 79 2 0
        //   49: checkcast 69	android/content/pm/UserInfo
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 58	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 58	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 58	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 58	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localUserInfo	UserInfo
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public int[] getProfileIds(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public UserInfo getProfileParent(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 15
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 73	android/content/pm/UserInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 79 2 0
        //   59: checkcast 69	android/content/pm/UserInfo
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 58	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 58	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 58	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 58	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localUserInfo	UserInfo
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public List<UserInfo> getProfiles(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(UserInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getSeedAccountName()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          this.mRemote.transact(40, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public PersistableBundle getSeedAccountOptions()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 42
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 48 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 51	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 55	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 122	android/os/PersistableBundle:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 79 2 0
        //   49: checkcast 121	android/os/PersistableBundle
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 58	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 58	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 58	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 58	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localPersistableBundle	PersistableBundle
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public String getSeedAccountType()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          this.mRemote.transact(41, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getUserAccount(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public long getUserCreationTime(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(20, localParcel1, localParcel2, 0);
          localParcel2.readException();
          long l = localParcel2.readLong();
          return l;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getUserHandle(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(24, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ParcelFileDescriptor getUserIcon(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 9
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 137	android/os/ParcelFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 79 2 0
        //   59: checkcast 136	android/os/ParcelFileDescriptor
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 58	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 58	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 58	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 58	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localParcelFileDescriptor	ParcelFileDescriptor
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      /* Error */
      public UserInfo getUserInfo(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 17
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 73	android/content/pm/UserInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 79 2 0
        //   59: checkcast 69	android/content/pm/UserInfo
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 58	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 58	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 58	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 58	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localUserInfo	UserInfo
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public int getUserRestrictionSource(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(25, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public Bundle getUserRestrictions(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 26
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 87	android/os/Bundle:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 79 2 0
        //   59: checkcast 86	android/os/Bundle
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 58	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 58	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 58	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 58	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localBundle	Bundle
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public int getUserSerialNumber(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(23, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<UserInfo> getUsers(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(UserInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean hasBaseUserRestriction(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 27
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 48 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 51	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 55	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 58	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 58	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 58	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 58	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean hasUserRestriction(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 28
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 48 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 51	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 55	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 58	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 58	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 58	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 58	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean isDemoUser(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 46
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 58	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 58	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 58	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 58	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      /* Error */
      public boolean isManagedProfile(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 45
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 58	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 58	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 58	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 58	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      /* Error */
      public boolean isQuietModeEnabled(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 37
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 58	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 58	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 58	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 58	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      /* Error */
      public boolean isRestricted()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 21
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 55	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 58	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 58	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 58	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 58	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isSameProfileGroup(int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 16
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 48 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 51	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 55	android/os/Parcel:readInt	()I
        //   56: istore_1
        //   57: iload_1
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 58	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 58	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore 6
        //   82: aload 5
        //   84: invokevirtual 58	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: invokevirtual 58	android/os/Parcel:recycle	()V
        //   92: aload 6
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramInt1	int
        //   0	95	2	paramInt2	int
        //   62	15	3	bool	boolean
        //   3	85	4	localParcel1	Parcel
        //   8	75	5	localParcel2	Parcel
        //   80	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean markGuestForDeletion(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 35
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 58	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 58	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 58	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 58	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      /* Error */
      public boolean removeUser(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 6
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 58	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 58	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 58	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 58	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      /* Error */
      public void setApplicationRestrictions(String paramString, Bundle paramBundle, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_2
        //   24: ifnull +55 -> 79
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 164	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 30
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 48 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 51	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 58	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 58	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   85: goto -45 -> 40
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 58	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 58	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramString	String
        //   0	101	2	paramBundle	Bundle
        //   0	101	3	paramInt	int
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	88	finally
        //   27	40	88	finally
        //   40	68	88	finally
        //   79	85	88	finally
      }
      
      /* Error */
      public void setDefaultGuestRestrictions(Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 164	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 33
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 48 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 51	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 58	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 58	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 58	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 58	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramBundle	Bundle
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void setQuietModeEnabled(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(36, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setSeedAccountData(int paramInt, String paramString1, String paramString2, PersistableBundle paramPersistableBundle, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.os.IUserManager");
            localParcel1.writeInt(paramInt);
            localParcel1.writeString(paramString1);
            localParcel1.writeString(paramString2);
            if (paramPersistableBundle != null)
            {
              localParcel1.writeInt(1);
              paramPersistableBundle.writeToParcel(localParcel1, 0);
              break label126;
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(39, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label126:
          do
          {
            paramInt = 0;
            break;
          } while (!paramBoolean);
          paramInt = i;
        }
      }
      
      public void setUserAccount(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setUserEnabled(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void setUserIcon(int paramInt, Bitmap paramBitmap)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +45 -> 66
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 179	android/graphics/Bitmap:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 8
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 48 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 51	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 58	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 58	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   71: goto -36 -> 35
        //   74: astore_2
        //   75: aload 4
        //   77: invokevirtual 58	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 58	android/os/Parcel:recycle	()V
        //   84: aload_2
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramInt	int
        //   0	86	2	paramBitmap	Bitmap
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	35	74	finally
        //   35	56	74	finally
        //   66	71	74	finally
      }
      
      public void setUserName(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setUserRestriction(String paramString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.IUserManager");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(29, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean someUserHasSeedAccount(String paramString1, String paramString2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload_0
        //   30: getfield 19	android/os/IUserManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 44
        //   35: aload 5
        //   37: aload 6
        //   39: iconst_0
        //   40: invokeinterface 48 5 0
        //   45: pop
        //   46: aload 6
        //   48: invokevirtual 51	android/os/Parcel:readException	()V
        //   51: aload 6
        //   53: invokevirtual 55	android/os/Parcel:readInt	()I
        //   56: istore_3
        //   57: iload_3
        //   58: ifeq +19 -> 77
        //   61: iconst_1
        //   62: istore 4
        //   64: aload 6
        //   66: invokevirtual 58	android/os/Parcel:recycle	()V
        //   69: aload 5
        //   71: invokevirtual 58	android/os/Parcel:recycle	()V
        //   74: iload 4
        //   76: ireturn
        //   77: iconst_0
        //   78: istore 4
        //   80: goto -16 -> 64
        //   83: astore_1
        //   84: aload 6
        //   86: invokevirtual 58	android/os/Parcel:recycle	()V
        //   89: aload 5
        //   91: invokevirtual 58	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString1	String
        //   0	96	2	paramString2	String
        //   56	2	3	i	int
        //   62	17	4	bool	boolean
        //   3	87	5	localParcel1	Parcel
        //   8	77	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	83	finally
      }
      
      public boolean trySetQuietModeDisabled(int paramInt, IntentSender paramIntentSender)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.os.IUserManager");
            localParcel1.writeInt(paramInt);
            if (paramIntentSender != null)
            {
              localParcel1.writeInt(1);
              paramIntentSender.writeToParcel(localParcel1, 0);
              this.mRemote.transact(38, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/IUserManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */