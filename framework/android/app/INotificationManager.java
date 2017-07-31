package android.app;

import android.content.ComponentName;
import android.content.pm.ParceledListSlice;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.service.notification.Adjustment;
import android.service.notification.Condition;
import android.service.notification.IConditionProvider;
import android.service.notification.IConditionProvider.Stub;
import android.service.notification.INotificationListener;
import android.service.notification.INotificationListener.Stub;
import android.service.notification.StatusBarNotification;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.ZenRule;
import java.util.ArrayList;
import java.util.List;

public abstract interface INotificationManager
  extends IInterface
{
  public abstract String addAutomaticZenRule(AutomaticZenRule paramAutomaticZenRule)
    throws RemoteException;
  
  public abstract void applyAdjustmentFromRankerService(INotificationListener paramINotificationListener, Adjustment paramAdjustment)
    throws RemoteException;
  
  public abstract void applyAdjustmentsFromRankerService(INotificationListener paramINotificationListener, List<Adjustment> paramList)
    throws RemoteException;
  
  public abstract void applyRestore(byte[] paramArrayOfByte, int paramInt)
    throws RemoteException;
  
  public abstract boolean areNotificationsEnabled(String paramString)
    throws RemoteException;
  
  public abstract boolean areNotificationsEnabledForPackage(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void cancelAllNotifications(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void cancelNotificationFromListener(INotificationListener paramINotificationListener, String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void cancelNotificationWithTag(String paramString1, String paramString2, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void cancelNotificationsFromListener(INotificationListener paramINotificationListener, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void cancelToast(String paramString, ITransientNotification paramITransientNotification)
    throws RemoteException;
  
  public abstract void enqueueNotificationWithTag(String paramString1, String paramString2, String paramString3, int paramInt1, Notification paramNotification, int[] paramArrayOfInt, int paramInt2)
    throws RemoteException;
  
  public abstract void enqueueToast(String paramString, ITransientNotification paramITransientNotification, int paramInt)
    throws RemoteException;
  
  public abstract StatusBarNotification[] getActiveNotifications(String paramString)
    throws RemoteException;
  
  public abstract ParceledListSlice getActiveNotificationsFromListener(INotificationListener paramINotificationListener, String[] paramArrayOfString, int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice getAppActiveNotifications(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract AutomaticZenRule getAutomaticZenRule(String paramString)
    throws RemoteException;
  
  public abstract byte[] getBackupPayload(int paramInt)
    throws RemoteException;
  
  public abstract ComponentName getEffectsSuppressor()
    throws RemoteException;
  
  public abstract int getHintsFromListener(INotificationListener paramINotificationListener)
    throws RemoteException;
  
  public abstract StatusBarNotification[] getHistoricalNotifications(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getImportance(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getInterruptionFilterFromListener(INotificationListener paramINotificationListener)
    throws RemoteException;
  
  public abstract NotificationManager.Policy getNotificationPolicy(String paramString)
    throws RemoteException;
  
  public abstract int getOPLevel(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getOnePlusPackagePriority(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getPackageImportance(String paramString)
    throws RemoteException;
  
  public abstract String[] getPackagesRequestingNotificationPolicyAccess()
    throws RemoteException;
  
  public abstract int getPriority(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getRuleInstanceCount(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract int getVisibilityOverride(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getZenMode()
    throws RemoteException;
  
  public abstract ZenModeConfig getZenModeConfig()
    throws RemoteException;
  
  public abstract List<ZenModeConfig.ZenRule> getZenRules()
    throws RemoteException;
  
  public abstract boolean isNotificationLedEnabled(String paramString)
    throws RemoteException;
  
  public abstract boolean isNotificationPolicyAccessGranted(String paramString)
    throws RemoteException;
  
  public abstract boolean isNotificationPolicyAccessGrantedForPackage(String paramString)
    throws RemoteException;
  
  public abstract boolean isSystemConditionProviderEnabled(String paramString)
    throws RemoteException;
  
  public abstract boolean matchesCallFilter(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void notifyConditions(String paramString, IConditionProvider paramIConditionProvider, Condition[] paramArrayOfCondition)
    throws RemoteException;
  
  public abstract void registerListener(INotificationListener paramINotificationListener, ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract boolean removeAutomaticZenRule(String paramString)
    throws RemoteException;
  
  public abstract boolean removeAutomaticZenRules(String paramString)
    throws RemoteException;
  
  public abstract void requestBindListener(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract void requestHintsFromListener(INotificationListener paramINotificationListener, int paramInt)
    throws RemoteException;
  
  public abstract void requestInterruptionFilterFromListener(INotificationListener paramINotificationListener, int paramInt)
    throws RemoteException;
  
  public abstract void requestUnbindListener(INotificationListener paramINotificationListener)
    throws RemoteException;
  
  public abstract void setImportance(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setInterruptionFilter(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setNotificationLedStatus(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setNotificationPolicy(String paramString, NotificationManager.Policy paramPolicy)
    throws RemoteException;
  
  public abstract void setNotificationPolicyAccessGranted(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setNotificationsEnabledForPackage(String paramString, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setNotificationsShownFromListener(INotificationListener paramINotificationListener, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void setOPLevel(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setOnNotificationPostedTrimFromListener(INotificationListener paramINotificationListener, int paramInt)
    throws RemoteException;
  
  public abstract void setOnePlusVibrateInSilentMode(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPriority(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setVisibilityOverride(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setZenMode(int paramInt, Uri paramUri, String paramString)
    throws RemoteException;
  
  public abstract void unregisterListener(INotificationListener paramINotificationListener, int paramInt)
    throws RemoteException;
  
  public abstract boolean updateAutomaticZenRule(String paramString, AutomaticZenRule paramAutomaticZenRule)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INotificationManager
  {
    private static final String DESCRIPTOR = "android.app.INotificationManager";
    static final int TRANSACTION_addAutomaticZenRule = 49;
    static final int TRANSACTION_applyAdjustmentFromRankerService = 32;
    static final int TRANSACTION_applyAdjustmentsFromRankerService = 33;
    static final int TRANSACTION_applyRestore = 55;
    static final int TRANSACTION_areNotificationsEnabled = 8;
    static final int TRANSACTION_areNotificationsEnabledForPackage = 7;
    static final int TRANSACTION_cancelAllNotifications = 1;
    static final int TRANSACTION_cancelNotificationFromListener = 20;
    static final int TRANSACTION_cancelNotificationWithTag = 5;
    static final int TRANSACTION_cancelNotificationsFromListener = 21;
    static final int TRANSACTION_cancelToast = 3;
    static final int TRANSACTION_enqueueNotificationWithTag = 4;
    static final int TRANSACTION_enqueueToast = 2;
    static final int TRANSACTION_getActiveNotifications = 16;
    static final int TRANSACTION_getActiveNotificationsFromListener = 25;
    static final int TRANSACTION_getAppActiveNotifications = 56;
    static final int TRANSACTION_getAutomaticZenRule = 47;
    static final int TRANSACTION_getBackupPayload = 54;
    static final int TRANSACTION_getEffectsSuppressor = 34;
    static final int TRANSACTION_getHintsFromListener = 27;
    static final int TRANSACTION_getHistoricalNotifications = 17;
    static final int TRANSACTION_getImportance = 14;
    static final int TRANSACTION_getInterruptionFilterFromListener = 29;
    static final int TRANSACTION_getNotificationPolicy = 42;
    static final int TRANSACTION_getOPLevel = 60;
    static final int TRANSACTION_getOnePlusPackagePriority = 61;
    static final int TRANSACTION_getPackageImportance = 15;
    static final int TRANSACTION_getPackagesRequestingNotificationPolicyAccess = 44;
    static final int TRANSACTION_getPriority = 12;
    static final int TRANSACTION_getRuleInstanceCount = 53;
    static final int TRANSACTION_getVisibilityOverride = 10;
    static final int TRANSACTION_getZenMode = 37;
    static final int TRANSACTION_getZenModeConfig = 38;
    static final int TRANSACTION_getZenRules = 48;
    static final int TRANSACTION_isNotificationLedEnabled = 58;
    static final int TRANSACTION_isNotificationPolicyAccessGranted = 41;
    static final int TRANSACTION_isNotificationPolicyAccessGrantedForPackage = 45;
    static final int TRANSACTION_isSystemConditionProviderEnabled = 36;
    static final int TRANSACTION_matchesCallFilter = 35;
    static final int TRANSACTION_notifyConditions = 40;
    static final int TRANSACTION_registerListener = 18;
    static final int TRANSACTION_removeAutomaticZenRule = 51;
    static final int TRANSACTION_removeAutomaticZenRules = 52;
    static final int TRANSACTION_requestBindListener = 22;
    static final int TRANSACTION_requestHintsFromListener = 26;
    static final int TRANSACTION_requestInterruptionFilterFromListener = 28;
    static final int TRANSACTION_requestUnbindListener = 23;
    static final int TRANSACTION_setImportance = 13;
    static final int TRANSACTION_setInterruptionFilter = 31;
    static final int TRANSACTION_setNotificationLedStatus = 57;
    static final int TRANSACTION_setNotificationPolicy = 43;
    static final int TRANSACTION_setNotificationPolicyAccessGranted = 46;
    static final int TRANSACTION_setNotificationsEnabledForPackage = 6;
    static final int TRANSACTION_setNotificationsShownFromListener = 24;
    static final int TRANSACTION_setOPLevel = 59;
    static final int TRANSACTION_setOnNotificationPostedTrimFromListener = 30;
    static final int TRANSACTION_setOnePlusVibrateInSilentMode = 62;
    static final int TRANSACTION_setPriority = 11;
    static final int TRANSACTION_setVisibilityOverride = 9;
    static final int TRANSACTION_setZenMode = 39;
    static final int TRANSACTION_unregisterListener = 19;
    static final int TRANSACTION_updateAutomaticZenRule = 50;
    
    public Stub()
    {
      attachInterface(this, "android.app.INotificationManager");
    }
    
    public static INotificationManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.INotificationManager");
      if ((localIInterface != null) && ((localIInterface instanceof INotificationManager))) {
        return (INotificationManager)localIInterface;
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
      Object localObject2;
      Object localObject1;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.INotificationManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        cancelAllNotifications(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        enqueueToast(paramParcel1.readString(), ITransientNotification.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        cancelToast(paramParcel1.readString(), ITransientNotification.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        localObject2 = paramParcel1.readString();
        String str1 = paramParcel1.readString();
        String str2 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Notification)Notification.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          int[] arrayOfInt = paramParcel1.createIntArray();
          enqueueNotificationWithTag((String)localObject2, str1, str2, paramInt1, (Notification)localObject1, arrayOfInt, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeIntArray(arrayOfInt);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        cancelNotificationWithTag(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        localObject1 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setNotificationsEnabledForPackage((String)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        bool = areNotificationsEnabledForPackage(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        bool = areNotificationsEnabled(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        setVisibilityOverride(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramInt1 = getVisibilityOverride(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        setPriority(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramInt1 = getPriority(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        setImportance(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramInt1 = getImportance(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramInt1 = getPackageImportance(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramParcel1 = getActiveNotifications(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramParcel1 = getHistoricalNotifications(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        localObject2 = INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          registerListener((INotificationListener)localObject2, (ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 19: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        unregisterListener(INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        cancelNotificationFromListener(INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        cancelNotificationsFromListener(INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.createStringArray());
        paramParcel2.writeNoException();
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          requestBindListener(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 23: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        requestUnbindListener(INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 24: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        setNotificationsShownFromListener(INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.createStringArray());
        paramParcel2.writeNoException();
        return true;
      case 25: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramParcel1 = getActiveNotificationsFromListener(INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.createStringArray(), paramParcel1.readInt());
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
      case 26: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        requestHintsFromListener(INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 27: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramInt1 = getHintsFromListener(INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 28: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        requestInterruptionFilterFromListener(INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 29: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramInt1 = getInterruptionFilterFromListener(INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 30: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        setOnNotificationPostedTrimFromListener(INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 31: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        setInterruptionFilter(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 32: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        localObject1 = INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Adjustment)Adjustment.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          applyAdjustmentFromRankerService((INotificationListener)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 33: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        applyAdjustmentsFromRankerService(INotificationListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.createTypedArrayList(Adjustment.CREATOR));
        paramParcel2.writeNoException();
        return true;
      case 34: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramParcel1 = getEffectsSuppressor();
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
        paramParcel1.enforceInterface("android.app.INotificationManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          bool = matchesCallFilter(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1767;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 36: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        bool = isSystemConditionProviderEnabled(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 37: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramInt1 = getZenMode();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 38: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramParcel1 = getZenModeConfig();
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
      case 39: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          setZenMode(paramInt1, paramParcel2, paramParcel1.readString());
          return true;
        }
      case 40: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        notifyConditions(paramParcel1.readString(), IConditionProvider.Stub.asInterface(paramParcel1.readStrongBinder()), (Condition[])paramParcel1.createTypedArray(Condition.CREATOR));
        return true;
      case 41: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        bool = isNotificationPolicyAccessGranted(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 42: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramParcel1 = getNotificationPolicy(paramParcel1.readString());
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
        paramParcel1.enforceInterface("android.app.INotificationManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NotificationManager.Policy)NotificationManager.Policy.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setNotificationPolicy((String)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 44: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramParcel1 = getPackagesRequestingNotificationPolicyAccess();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 45: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        bool = isNotificationPolicyAccessGrantedForPackage(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 46: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setNotificationPolicyAccessGranted((String)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 47: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramParcel1 = getAutomaticZenRule(paramParcel1.readString());
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
      case 48: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramParcel1 = getZenRules();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 49: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (AutomaticZenRule)AutomaticZenRule.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = addAutomaticZenRule(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 50: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (AutomaticZenRule)AutomaticZenRule.CREATOR.createFromParcel(paramParcel1);
          bool = updateAutomaticZenRule((String)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label2368;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 51: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        bool = removeAutomaticZenRule(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 52: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        bool = removeAutomaticZenRules(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 53: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getRuleInstanceCount(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 54: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramParcel1 = getBackupPayload(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      case 55: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        applyRestore(paramParcel1.createByteArray(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 56: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramParcel1 = getAppActiveNotifications(paramParcel1.readString(), paramParcel1.readInt());
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
      case 57: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setNotificationLedStatus((String)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 58: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        bool = isNotificationLedEnabled(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 59: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        setOPLevel(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 60: 
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramInt1 = getOPLevel(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 61: 
        label1767:
        label2368:
        paramParcel1.enforceInterface("android.app.INotificationManager");
        paramInt1 = getOnePlusPackagePriority(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.app.INotificationManager");
      if (paramParcel1.readInt() != 0) {}
      for (boolean bool = true;; bool = false)
      {
        setOnePlusVibrateInSilentMode(bool);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements INotificationManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public String addAutomaticZenRule(AutomaticZenRule paramAutomaticZenRule)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +48 -> 63
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/app/AutomaticZenRule:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 49
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 52 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 55	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 59	android/os/Parcel:readString	()Ljava/lang/String;
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 62	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 62	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aload_2
        //   64: iconst_0
        //   65: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   68: goto -39 -> 29
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 62	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramAutomaticZenRule	AutomaticZenRule
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	29	71	finally
        //   29	53	71	finally
        //   63	68	71	finally
      }
      
      /* Error */
      public void applyAdjustmentFromRankerService(INotificationListener paramINotificationListener, Adjustment paramAdjustment)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 32
        //   16: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload_1
        //   20: ifnull +10 -> 30
        //   23: aload_1
        //   24: invokeinterface 71 1 0
        //   29: astore_3
        //   30: aload 4
        //   32: aload_3
        //   33: invokevirtual 74	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   36: aload_2
        //   37: ifnull +49 -> 86
        //   40: aload 4
        //   42: iconst_1
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_2
        //   47: aload 4
        //   49: iconst_0
        //   50: invokevirtual 77	android/service/notification/Adjustment:writeToParcel	(Landroid/os/Parcel;I)V
        //   53: aload_0
        //   54: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   57: bipush 32
        //   59: aload 4
        //   61: aload 5
        //   63: iconst_0
        //   64: invokeinterface 52 5 0
        //   69: pop
        //   70: aload 5
        //   72: invokevirtual 55	android/os/Parcel:readException	()V
        //   75: aload 5
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 62	android/os/Parcel:recycle	()V
        //   85: return
        //   86: aload 4
        //   88: iconst_0
        //   89: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   92: goto -39 -> 53
        //   95: astore_1
        //   96: aload 5
        //   98: invokevirtual 62	android/os/Parcel:recycle	()V
        //   101: aload 4
        //   103: invokevirtual 62	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramINotificationListener	INotificationListener
        //   0	108	2	paramAdjustment	Adjustment
        //   1	32	3	localIBinder	IBinder
        //   5	97	4	localParcel1	Parcel
        //   10	87	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	19	95	finally
        //   23	30	95	finally
        //   30	36	95	finally
        //   40	53	95	finally
        //   53	75	95	finally
        //   86	92	95	finally
      }
      
      public void applyAdjustmentsFromRankerService(INotificationListener paramINotificationListener, List<Adjustment> paramList)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          if (paramINotificationListener != null) {
            localIBinder = paramINotificationListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeTypedList(paramList);
          this.mRemote.transact(33, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void applyRestore(byte[] paramArrayOfByte, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeByteArray(paramArrayOfByte);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(55, localParcel1, localParcel2, 0);
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
      public boolean areNotificationsEnabled(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 8
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 52 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 55	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 100	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 62	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 62	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean areNotificationsEnabledForPackage(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 7
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 52 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 55	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 100	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 62	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 62	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 62	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 62	android/os/Parcel:recycle	()V
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void cancelAllNotifications(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void cancelNotificationFromListener(INotificationListener paramINotificationListener, String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          if (paramINotificationListener != null) {
            localIBinder = paramINotificationListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(20, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void cancelNotificationWithTag(String paramString1, String paramString2, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
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
      
      public void cancelNotificationsFromListener(INotificationListener paramINotificationListener, String[] paramArrayOfString)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          if (paramINotificationListener != null) {
            localIBinder = paramINotificationListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeStringArray(paramArrayOfString);
          this.mRemote.transact(21, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void cancelToast(String paramString, ITransientNotification paramITransientNotification)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          paramString = (String)localObject;
          if (paramITransientNotification != null) {
            paramString = paramITransientNotification.asBinder();
          }
          localParcel1.writeStrongBinder(paramString);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
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
      public void enqueueNotificationWithTag(String paramString1, String paramString2, String paramString3, int paramInt1, Notification paramNotification, int[] paramArrayOfInt, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 8
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 9
        //   10: aload 8
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 8
        //   19: aload_1
        //   20: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 8
        //   25: aload_2
        //   26: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload 8
        //   31: aload_3
        //   32: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload 8
        //   37: iload 4
        //   39: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   42: aload 5
        //   44: ifnull +70 -> 114
        //   47: aload 8
        //   49: iconst_1
        //   50: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   53: aload 5
        //   55: aload 8
        //   57: iconst_0
        //   58: invokevirtual 124	android/app/Notification:writeToParcel	(Landroid/os/Parcel;I)V
        //   61: aload 8
        //   63: aload 6
        //   65: invokevirtual 128	android/os/Parcel:writeIntArray	([I)V
        //   68: aload 8
        //   70: iload 7
        //   72: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   75: aload_0
        //   76: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   79: iconst_4
        //   80: aload 8
        //   82: aload 9
        //   84: iconst_0
        //   85: invokeinterface 52 5 0
        //   90: pop
        //   91: aload 9
        //   93: invokevirtual 55	android/os/Parcel:readException	()V
        //   96: aload 9
        //   98: aload 6
        //   100: invokevirtual 131	android/os/Parcel:readIntArray	([I)V
        //   103: aload 9
        //   105: invokevirtual 62	android/os/Parcel:recycle	()V
        //   108: aload 8
        //   110: invokevirtual 62	android/os/Parcel:recycle	()V
        //   113: return
        //   114: aload 8
        //   116: iconst_0
        //   117: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   120: goto -59 -> 61
        //   123: astore_1
        //   124: aload 9
        //   126: invokevirtual 62	android/os/Parcel:recycle	()V
        //   129: aload 8
        //   131: invokevirtual 62	android/os/Parcel:recycle	()V
        //   134: aload_1
        //   135: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	136	0	this	Proxy
        //   0	136	1	paramString1	String
        //   0	136	2	paramString2	String
        //   0	136	3	paramString3	String
        //   0	136	4	paramInt1	int
        //   0	136	5	paramNotification	Notification
        //   0	136	6	paramArrayOfInt	int[]
        //   0	136	7	paramInt2	int
        //   3	127	8	localParcel1	Parcel
        //   8	117	9	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	42	123	finally
        //   47	61	123	finally
        //   61	103	123	finally
        //   114	120	123	finally
      }
      
      public void enqueueToast(String paramString, ITransientNotification paramITransientNotification, int paramInt)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          paramString = (String)localObject;
          if (paramITransientNotification != null) {
            paramString = paramITransientNotification.asBinder();
          }
          localParcel1.writeStrongBinder(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public StatusBarNotification[] getActiveNotifications(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = (StatusBarNotification[])localParcel2.createTypedArray(StatusBarNotification.CREATOR);
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ParceledListSlice getActiveNotificationsFromListener(INotificationListener paramINotificationListener, String[] paramArrayOfString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 71 1 0
        //   30: astore 4
        //   32: aload 5
        //   34: aload 4
        //   36: invokevirtual 74	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload 5
        //   41: aload_2
        //   42: invokevirtual 114	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   45: aload 5
        //   47: iload_3
        //   48: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   51: aload_0
        //   52: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   55: bipush 25
        //   57: aload 5
        //   59: aload 6
        //   61: iconst_0
        //   62: invokeinterface 52 5 0
        //   67: pop
        //   68: aload 6
        //   70: invokevirtual 55	android/os/Parcel:readException	()V
        //   73: aload 6
        //   75: invokevirtual 100	android/os/Parcel:readInt	()I
        //   78: ifeq +29 -> 107
        //   81: getstatic 154	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   84: aload 6
        //   86: invokeinterface 160 2 0
        //   91: checkcast 151	android/content/pm/ParceledListSlice
        //   94: astore_1
        //   95: aload 6
        //   97: invokevirtual 62	android/os/Parcel:recycle	()V
        //   100: aload 5
        //   102: invokevirtual 62	android/os/Parcel:recycle	()V
        //   105: aload_1
        //   106: areturn
        //   107: aconst_null
        //   108: astore_1
        //   109: goto -14 -> 95
        //   112: astore_1
        //   113: aload 6
        //   115: invokevirtual 62	android/os/Parcel:recycle	()V
        //   118: aload 5
        //   120: invokevirtual 62	android/os/Parcel:recycle	()V
        //   123: aload_1
        //   124: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	125	0	this	Proxy
        //   0	125	1	paramINotificationListener	INotificationListener
        //   0	125	2	paramArrayOfString	String[]
        //   0	125	3	paramInt	int
        //   1	34	4	localIBinder	IBinder
        //   6	113	5	localParcel1	Parcel
        //   11	103	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	112	finally
        //   24	32	112	finally
        //   32	95	112	finally
      }
      
      /* Error */
      public ParceledListSlice getAppActiveNotifications(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 56
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_0
        //   35: invokeinterface 52 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 100	android/os/Parcel:readInt	()I
        //   51: ifeq +28 -> 79
        //   54: getstatic 154	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   57: aload 4
        //   59: invokeinterface 160 2 0
        //   64: checkcast 151	android/content/pm/ParceledListSlice
        //   67: astore_1
        //   68: aload 4
        //   70: invokevirtual 62	android/os/Parcel:recycle	()V
        //   73: aload_3
        //   74: invokevirtual 62	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: areturn
        //   79: aconst_null
        //   80: astore_1
        //   81: goto -13 -> 68
        //   84: astore_1
        //   85: aload 4
        //   87: invokevirtual 62	android/os/Parcel:recycle	()V
        //   90: aload_3
        //   91: invokevirtual 62	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public AutomaticZenRule getAutomaticZenRule(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: bipush 47
        //   25: aload_2
        //   26: aload_3
        //   27: iconst_0
        //   28: invokeinterface 52 5 0
        //   33: pop
        //   34: aload_3
        //   35: invokevirtual 55	android/os/Parcel:readException	()V
        //   38: aload_3
        //   39: invokevirtual 100	android/os/Parcel:readInt	()I
        //   42: ifeq +26 -> 68
        //   45: getstatic 165	android/app/AutomaticZenRule:CREATOR	Landroid/os/Parcelable$Creator;
        //   48: aload_3
        //   49: invokeinterface 168 2 0
        //   54: checkcast 42	android/app/AutomaticZenRule
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: invokevirtual 62	android/os/Parcel:recycle	()V
        //   66: aload_1
        //   67: areturn
        //   68: aconst_null
        //   69: astore_1
        //   70: goto -12 -> 58
        //   73: astore_1
        //   74: aload_3
        //   75: invokevirtual 62	android/os/Parcel:recycle	()V
        //   78: aload_2
        //   79: invokevirtual 62	android/os/Parcel:recycle	()V
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
      
      public byte[] getBackupPayload(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(54, localParcel1, localParcel2, 0);
          localParcel2.readException();
          byte[] arrayOfByte = localParcel2.createByteArray();
          return arrayOfByte;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ComponentName getEffectsSuppressor()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 34
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 52 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 55	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 100	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 179	android/content/ComponentName:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 168 2 0
        //   49: checkcast 178	android/content/ComponentName
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 62	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 62	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 62	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 62	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localComponentName	ComponentName
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public int getHintsFromListener(INotificationListener paramINotificationListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          if (paramINotificationListener != null) {
            localIBinder = paramINotificationListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(27, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public StatusBarNotification[] getHistoricalNotifications(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = (StatusBarNotification[])localParcel2.createTypedArray(StatusBarNotification.CREATOR);
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getImportance(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
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
      
      public String getInterfaceDescriptor()
      {
        return "android.app.INotificationManager";
      }
      
      public int getInterruptionFilterFromListener(INotificationListener paramINotificationListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          if (paramINotificationListener != null) {
            localIBinder = paramINotificationListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(29, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public NotificationManager.Policy getNotificationPolicy(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: bipush 42
        //   25: aload_2
        //   26: aload_3
        //   27: iconst_0
        //   28: invokeinterface 52 5 0
        //   33: pop
        //   34: aload_3
        //   35: invokevirtual 55	android/os/Parcel:readException	()V
        //   38: aload_3
        //   39: invokevirtual 100	android/os/Parcel:readInt	()I
        //   42: ifeq +26 -> 68
        //   45: getstatic 192	android/app/NotificationManager$Policy:CREATOR	Landroid/os/Parcelable$Creator;
        //   48: aload_3
        //   49: invokeinterface 168 2 0
        //   54: checkcast 191	android/app/NotificationManager$Policy
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: invokevirtual 62	android/os/Parcel:recycle	()V
        //   66: aload_1
        //   67: areturn
        //   68: aconst_null
        //   69: astore_1
        //   70: goto -12 -> 58
        //   73: astore_1
        //   74: aload_3
        //   75: invokevirtual 62	android/os/Parcel:recycle	()V
        //   78: aload_2
        //   79: invokevirtual 62	android/os/Parcel:recycle	()V
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
      
      public int getOPLevel(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(60, localParcel1, localParcel2, 0);
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
      
      public int getOnePlusPackagePriority(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(61, localParcel1, localParcel2, 0);
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
      
      public int getPackageImportance(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String[] getPackagesRequestingNotificationPolicyAccess()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          this.mRemote.transact(44, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String[] arrayOfString = localParcel2.createStringArray();
          return arrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getPriority(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
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
      public int getRuleInstanceCount(ComponentName paramComponentName)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +52 -> 68
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 205	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 53
        //   36: aload_3
        //   37: aload 4
        //   39: iconst_0
        //   40: invokeinterface 52 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 55	android/os/Parcel:readException	()V
        //   51: aload 4
        //   53: invokevirtual 100	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: aload 4
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 62	android/os/Parcel:recycle	()V
        //   66: iload_2
        //   67: ireturn
        //   68: aload_3
        //   69: iconst_0
        //   70: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   73: goto -43 -> 30
        //   76: astore_1
        //   77: aload 4
        //   79: invokevirtual 62	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 62	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramComponentName	ComponentName
        //   56	11	2	i	int
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	76	finally
        //   19	30	76	finally
        //   30	57	76	finally
        //   68	73	76	finally
      }
      
      public int getVisibilityOverride(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
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
      
      public int getZenMode()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          this.mRemote.transact(37, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ZenModeConfig getZenModeConfig()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 38
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 52 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 55	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 100	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 212	android/service/notification/ZenModeConfig:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 168 2 0
        //   49: checkcast 211	android/service/notification/ZenModeConfig
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 62	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 62	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 62	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 62	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localZenModeConfig	ZenModeConfig
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public List<ZenModeConfig.ZenRule> getZenRules()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          this.mRemote.transact(48, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(ZenModeConfig.ZenRule.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean isNotificationLedEnabled(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 58
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 52 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 55	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 100	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 62	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 62	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean isNotificationPolicyAccessGranted(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 41
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 52 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 55	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 100	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 62	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 62	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean isNotificationPolicyAccessGrantedForPackage(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 45
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 52 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 55	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 100	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 62	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 62	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean isSystemConditionProviderEnabled(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 36
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 52 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 55	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 100	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 62	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 62	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      public boolean matchesCallFilter(Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.INotificationManager");
            if (paramBundle != null)
            {
              localParcel1.writeInt(1);
              paramBundle.writeToParcel(localParcel1, 0);
              this.mRemote.transact(35, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
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
      
      public void notifyConditions(String paramString, IConditionProvider paramIConditionProvider, Condition[] paramArrayOfCondition)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.INotificationManager");
          localParcel.writeString(paramString);
          paramString = (String)localObject;
          if (paramIConditionProvider != null) {
            paramString = paramIConditionProvider.asBinder();
          }
          localParcel.writeStrongBinder(paramString);
          localParcel.writeTypedArray(paramArrayOfCondition, 0);
          this.mRemote.transact(40, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void registerListener(INotificationListener paramINotificationListener, ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 71 1 0
        //   30: astore 4
        //   32: aload 5
        //   34: aload 4
        //   36: invokevirtual 74	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_2
        //   40: ifnull +55 -> 95
        //   43: aload 5
        //   45: iconst_1
        //   46: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   49: aload_2
        //   50: aload 5
        //   52: iconst_0
        //   53: invokevirtual 205	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   56: aload 5
        //   58: iload_3
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: aload_0
        //   63: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   66: bipush 18
        //   68: aload 5
        //   70: aload 6
        //   72: iconst_0
        //   73: invokeinterface 52 5 0
        //   78: pop
        //   79: aload 6
        //   81: invokevirtual 55	android/os/Parcel:readException	()V
        //   84: aload 6
        //   86: invokevirtual 62	android/os/Parcel:recycle	()V
        //   89: aload 5
        //   91: invokevirtual 62	android/os/Parcel:recycle	()V
        //   94: return
        //   95: aload 5
        //   97: iconst_0
        //   98: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   101: goto -45 -> 56
        //   104: astore_1
        //   105: aload 6
        //   107: invokevirtual 62	android/os/Parcel:recycle	()V
        //   110: aload 5
        //   112: invokevirtual 62	android/os/Parcel:recycle	()V
        //   115: aload_1
        //   116: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	117	0	this	Proxy
        //   0	117	1	paramINotificationListener	INotificationListener
        //   0	117	2	paramComponentName	ComponentName
        //   0	117	3	paramInt	int
        //   1	34	4	localIBinder	IBinder
        //   6	105	5	localParcel1	Parcel
        //   11	95	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	104	finally
        //   24	32	104	finally
        //   32	39	104	finally
        //   43	56	104	finally
        //   56	84	104	finally
        //   95	101	104	finally
      }
      
      /* Error */
      public boolean removeAutomaticZenRule(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 51
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 52 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 55	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 100	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 62	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 62	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean removeAutomaticZenRules(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 52
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 52 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 55	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 100	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 62	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 62	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public void requestBindListener(ComponentName paramComponentName)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 205	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 22
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 52 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 55	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 62	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 62	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 62	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 62	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramComponentName	ComponentName
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void requestHintsFromListener(INotificationListener paramINotificationListener, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          if (paramINotificationListener != null) {
            localIBinder = paramINotificationListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(26, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void requestInterruptionFilterFromListener(INotificationListener paramINotificationListener, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          if (paramINotificationListener != null) {
            localIBinder = paramINotificationListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(28, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void requestUnbindListener(INotificationListener paramINotificationListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          if (paramINotificationListener != null) {
            localIBinder = paramINotificationListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(23, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setImportance(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setInterruptionFilter(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setNotificationLedStatus(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(57, localParcel1, localParcel2, 0);
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
      public void setNotificationPolicy(String paramString, NotificationManager.Policy paramPolicy)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_2
        //   21: ifnull +45 -> 66
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 259	android/app/NotificationManager$Policy:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 43
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 52 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 55	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 62	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 62	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -36 -> 35
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 62	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramString	String
        //   0	86	2	paramPolicy	NotificationManager.Policy
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	35	74	finally
        //   35	56	74	finally
        //   66	71	74	finally
      }
      
      public void setNotificationPolicyAccessGranted(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(46, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setNotificationsEnabledForPackage(String paramString, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setNotificationsShownFromListener(INotificationListener paramINotificationListener, String[] paramArrayOfString)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          if (paramINotificationListener != null) {
            localIBinder = paramINotificationListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeStringArray(paramArrayOfString);
          this.mRemote.transact(24, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setOPLevel(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(59, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setOnNotificationPostedTrimFromListener(INotificationListener paramINotificationListener, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          if (paramINotificationListener != null) {
            localIBinder = paramINotificationListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(30, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setOnePlusVibrateInSilentMode(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(62, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setPriority(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setVisibilityOverride(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
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
      public void setZenMode(int paramInt, Uri paramUri, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: aload 4
        //   7: ldc 32
        //   9: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 4
        //   14: iload_1
        //   15: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   18: aload_2
        //   19: ifnull +44 -> 63
        //   22: aload 4
        //   24: iconst_1
        //   25: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   28: aload_2
        //   29: aload 4
        //   31: iconst_0
        //   32: invokevirtual 274	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload 4
        //   37: aload_3
        //   38: invokevirtual 96	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   41: aload_0
        //   42: getfield 19	android/app/INotificationManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   45: bipush 39
        //   47: aload 4
        //   49: aconst_null
        //   50: iconst_1
        //   51: invokeinterface 52 5 0
        //   56: pop
        //   57: aload 4
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
        //   62: return
        //   63: aload 4
        //   65: iconst_0
        //   66: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   69: goto -34 -> 35
        //   72: astore_2
        //   73: aload 4
        //   75: invokevirtual 62	android/os/Parcel:recycle	()V
        //   78: aload_2
        //   79: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	80	0	this	Proxy
        //   0	80	1	paramInt	int
        //   0	80	2	paramUri	Uri
        //   0	80	3	paramString	String
        //   3	71	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	18	72	finally
        //   22	35	72	finally
        //   35	57	72	finally
        //   63	69	72	finally
      }
      
      public void unregisterListener(INotificationListener paramINotificationListener, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.INotificationManager");
          if (paramINotificationListener != null) {
            localIBinder = paramINotificationListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
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
      
      public boolean updateAutomaticZenRule(String paramString, AutomaticZenRule paramAutomaticZenRule)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.INotificationManager");
            localParcel1.writeString(paramString);
            if (paramAutomaticZenRule != null)
            {
              localParcel1.writeInt(1);
              paramAutomaticZenRule.writeToParcel(localParcel1, 0);
              this.mRemote.transact(50, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/INotificationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */