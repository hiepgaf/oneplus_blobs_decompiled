package android.nfc.cardemulation;

import android.app.Activity;
import android.app.ActivityThread;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.nfc.INfcCardEmulation;
import android.nfc.NfcAdapter;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import java.util.HashMap;
import java.util.List;

public final class CardEmulation
{
  public static final String ACTION_CHANGE_DEFAULT = "android.nfc.cardemulation.action.ACTION_CHANGE_DEFAULT";
  public static final String CATEGORY_OTHER = "other";
  public static final String CATEGORY_PAYMENT = "payment";
  public static final String EXTRA_CATEGORY = "category";
  public static final String EXTRA_SERVICE_COMPONENT = "component";
  public static final int SELECTION_MODE_ALWAYS_ASK = 1;
  public static final int SELECTION_MODE_ASK_IF_CONFLICT = 2;
  public static final int SELECTION_MODE_PREFER_DEFAULT = 0;
  static final String TAG = "CardEmulation";
  static HashMap<Context, CardEmulation> sCardEmus = new HashMap();
  static boolean sIsInitialized = false;
  static INfcCardEmulation sService;
  final Context mContext;
  
  private CardEmulation(Context paramContext, INfcCardEmulation paramINfcCardEmulation)
  {
    this.mContext = paramContext.getApplicationContext();
    sService = paramINfcCardEmulation;
  }
  
  public static CardEmulation getInstance(NfcAdapter paramNfcAdapter)
  {
    if (paramNfcAdapter == null) {
      try
      {
        throw new NullPointerException("NfcAdapter is null");
      }
      finally {}
    }
    Context localContext = paramNfcAdapter.getContext();
    if (localContext == null)
    {
      Log.e("CardEmulation", "NfcAdapter context is null.");
      throw new UnsupportedOperationException();
    }
    if (!sIsInitialized)
    {
      localObject = ActivityThread.getPackageManager();
      if (localObject == null)
      {
        Log.e("CardEmulation", "Cannot get PackageManager");
        throw new UnsupportedOperationException();
      }
      try
      {
        if (!((IPackageManager)localObject).hasSystemFeature("android.hardware.nfc.hce", 0))
        {
          Log.e("CardEmulation", "This device does not support card emulation");
          throw new UnsupportedOperationException();
        }
      }
      catch (RemoteException paramNfcAdapter)
      {
        Log.e("CardEmulation", "PackageManager query failed.");
        throw new UnsupportedOperationException();
      }
      sIsInitialized = true;
    }
    CardEmulation localCardEmulation = (CardEmulation)sCardEmus.get(localContext);
    Object localObject = localCardEmulation;
    if (localCardEmulation == null)
    {
      paramNfcAdapter = paramNfcAdapter.getCardEmulationService();
      if (paramNfcAdapter == null)
      {
        Log.e("CardEmulation", "This device does not implement the INfcCardEmulation interface.");
        throw new UnsupportedOperationException();
      }
      localObject = new CardEmulation(localContext, paramNfcAdapter);
      sCardEmus.put(localContext, localObject);
    }
    return (CardEmulation)localObject;
  }
  
  public static boolean isValidAid(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    if ((paramString.endsWith("*")) && (paramString.length() % 2 == 0))
    {
      Log.e("CardEmulation", "AID " + paramString + " is not a valid AID.");
      return false;
    }
    if ((!paramString.endsWith("*")) && (paramString.length() % 2 != 0))
    {
      Log.e("CardEmulation", "AID " + paramString + " is not a valid AID.");
      return false;
    }
    if (!paramString.matches("[0-9A-Fa-f]{10,32}\\*?"))
    {
      Log.e("CardEmulation", "AID " + paramString + " is not a valid AID.");
      return false;
    }
    return true;
  }
  
  public boolean categoryAllowsForegroundPreference(String paramString)
  {
    if ("payment".equals(paramString)) {}
    try
    {
      int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "nfc_payment_foreground");
      return i != 0;
    }
    catch (Settings.SettingNotFoundException paramString) {}
    return true;
    return false;
  }
  
  public List<String> getAidsForService(ComponentName paramComponentName, String paramString)
  {
    Object localObject = null;
    List localList = null;
    try
    {
      AidGroup localAidGroup = sService.getAidGroupForService(UserHandle.myUserId(), paramComponentName, paramString);
      if (localAidGroup != null) {
        localList = localAidGroup.getAids();
      }
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      recoverService();
      if (sService == null)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
        return null;
      }
      try
      {
        paramString = sService.getAidGroupForService(UserHandle.myUserId(), paramComponentName, paramString);
        paramComponentName = (ComponentName)localObject;
        if (paramString != null) {
          paramComponentName = paramString.getAids();
        }
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
      }
    }
    return null;
  }
  
  public int getSelectionModeForCategory(String paramString)
  {
    if ("payment".equals(paramString))
    {
      if (Settings.Secure.getString(this.mContext.getContentResolver(), "nfc_payment_default_component") != null) {
        return 0;
      }
      return 1;
    }
    return 2;
  }
  
  public List<ApduServiceInfo> getServices(String paramString)
  {
    try
    {
      List localList = sService.getServices(UserHandle.myUserId(), paramString);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      recoverService();
      if (sService == null)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
        return null;
      }
      try
      {
        paramString = sService.getServices(UserHandle.myUserId(), paramString);
        return paramString;
      }
      catch (RemoteException paramString)
      {
        Log.e("CardEmulation", "Failed to reach CardEmulationService.");
      }
    }
    return null;
  }
  
  public boolean isDefaultServiceForAid(ComponentName paramComponentName, String paramString)
  {
    try
    {
      bool = sService.isDefaultServiceForAid(UserHandle.myUserId(), paramComponentName, paramString);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.isDefaultServiceForAid(UserHandle.myUserId(), paramComponentName, paramString);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("CardEmulation", "Failed to reach CardEmulationService.");
      }
    }
    return false;
  }
  
  public boolean isDefaultServiceForCategory(ComponentName paramComponentName, String paramString)
  {
    try
    {
      bool = sService.isDefaultServiceForCategory(UserHandle.myUserId(), paramComponentName, paramString);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.isDefaultServiceForCategory(UserHandle.myUserId(), paramComponentName, paramString);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
      }
    }
    return false;
  }
  
  void recoverService()
  {
    sService = NfcAdapter.getDefaultAdapter(this.mContext).getCardEmulationService();
  }
  
  public boolean registerAidsForService(ComponentName paramComponentName, String paramString, List<String> paramList)
  {
    paramString = new AidGroup(paramList, paramString);
    try
    {
      bool = sService.registerAidGroupForService(UserHandle.myUserId(), paramComponentName, paramString);
      return bool;
    }
    catch (RemoteException paramList)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.registerAidGroupForService(UserHandle.myUserId(), paramComponentName, paramString);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("CardEmulation", "Failed to reach CardEmulationService.");
      }
    }
    return false;
  }
  
  public boolean removeAidsForService(ComponentName paramComponentName, String paramString)
  {
    try
    {
      bool = sService.removeAidGroupForService(UserHandle.myUserId(), paramComponentName, paramString);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.removeAidGroupForService(UserHandle.myUserId(), paramComponentName, paramString);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("CardEmulation", "Failed to reach CardEmulationService.");
      }
    }
    return false;
  }
  
  public boolean setDefaultForNextTap(ComponentName paramComponentName)
  {
    try
    {
      bool = sService.setDefaultForNextTap(UserHandle.myUserId(), paramComponentName);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.setDefaultForNextTap(UserHandle.myUserId(), paramComponentName);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("CardEmulation", "Failed to reach CardEmulationService.");
      }
    }
    return false;
  }
  
  public boolean setDefaultServiceForCategory(ComponentName paramComponentName, String paramString)
  {
    try
    {
      bool = sService.setDefaultServiceForCategory(UserHandle.myUserId(), paramComponentName, paramString);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.setDefaultServiceForCategory(UserHandle.myUserId(), paramComponentName, paramString);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("CardEmulation", "Failed to reach CardEmulationService.");
      }
    }
    return false;
  }
  
  public boolean setPreferredService(Activity paramActivity, ComponentName paramComponentName)
  {
    if ((paramActivity == null) || (paramComponentName == null)) {
      throw new NullPointerException("activity or service or category is null");
    }
    if (!paramActivity.isResumed()) {
      throw new IllegalArgumentException("Activity must be resumed.");
    }
    try
    {
      bool = sService.setPreferredService(paramComponentName);
      return bool;
    }
    catch (RemoteException paramActivity)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.setPreferredService(paramComponentName);
        return bool;
      }
      catch (RemoteException paramActivity)
      {
        Log.e("CardEmulation", "Failed to reach CardEmulationService.");
      }
    }
    return false;
  }
  
  public boolean supportsAidPrefixRegistration()
  {
    try
    {
      bool = sService.supportsAidPrefixRegistration();
      return bool;
    }
    catch (RemoteException localRemoteException1)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.supportsAidPrefixRegistration();
        return bool;
      }
      catch (RemoteException localRemoteException2)
      {
        Log.e("CardEmulation", "Failed to reach CardEmulationService.");
      }
    }
    return false;
  }
  
  public boolean unsetPreferredService(Activity paramActivity)
  {
    if (paramActivity == null) {
      throw new NullPointerException("activity is null");
    }
    if (!paramActivity.isResumed()) {
      throw new IllegalArgumentException("Activity must be resumed.");
    }
    try
    {
      bool = sService.unsetPreferredService();
      return bool;
    }
    catch (RemoteException paramActivity)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("CardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.unsetPreferredService();
        return bool;
      }
      catch (RemoteException paramActivity)
      {
        Log.e("CardEmulation", "Failed to reach CardEmulationService.");
      }
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/cardemulation/CardEmulation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */