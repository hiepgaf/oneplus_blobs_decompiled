package android.nfc.cardemulation;

import android.app.Activity;
import android.app.ActivityThread;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.nfc.INfcFCardEmulation;
import android.nfc.NfcAdapter;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import java.util.HashMap;
import java.util.List;

public final class NfcFCardEmulation
{
  static final String TAG = "NfcFCardEmulation";
  static HashMap<Context, NfcFCardEmulation> sCardEmus = new HashMap();
  static boolean sIsInitialized = false;
  static INfcFCardEmulation sService;
  final Context mContext;
  
  private NfcFCardEmulation(Context paramContext, INfcFCardEmulation paramINfcFCardEmulation)
  {
    this.mContext = paramContext.getApplicationContext();
    sService = paramINfcFCardEmulation;
  }
  
  public static NfcFCardEmulation getInstance(NfcAdapter paramNfcAdapter)
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
      Log.e("NfcFCardEmulation", "NfcAdapter context is null.");
      throw new UnsupportedOperationException();
    }
    if (!sIsInitialized)
    {
      localObject = ActivityThread.getPackageManager();
      if (localObject == null)
      {
        Log.e("NfcFCardEmulation", "Cannot get PackageManager");
        throw new UnsupportedOperationException();
      }
      try
      {
        if (!((IPackageManager)localObject).hasSystemFeature("android.hardware.nfc.hcef", 0))
        {
          Log.e("NfcFCardEmulation", "This device does not support NFC-F card emulation");
          throw new UnsupportedOperationException();
        }
      }
      catch (RemoteException paramNfcAdapter)
      {
        Log.e("NfcFCardEmulation", "PackageManager query failed.");
        throw new UnsupportedOperationException();
      }
      sIsInitialized = true;
    }
    NfcFCardEmulation localNfcFCardEmulation = (NfcFCardEmulation)sCardEmus.get(localContext);
    Object localObject = localNfcFCardEmulation;
    if (localNfcFCardEmulation == null)
    {
      paramNfcAdapter = paramNfcAdapter.getNfcFCardEmulationService();
      if (paramNfcAdapter == null)
      {
        Log.e("NfcFCardEmulation", "This device does not implement the INfcFCardEmulation interface.");
        throw new UnsupportedOperationException();
      }
      localObject = new NfcFCardEmulation(localContext, paramNfcAdapter);
      sCardEmus.put(localContext, localObject);
    }
    return (NfcFCardEmulation)localObject;
  }
  
  public static boolean isValidNfcid2(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    if (paramString.length() != 16)
    {
      Log.e("NfcFCardEmulation", "NFCID2 " + paramString + " is not a valid NFCID2.");
      return false;
    }
    if (!paramString.toUpperCase().startsWith("02FE"))
    {
      Log.e("NfcFCardEmulation", "NFCID2 " + paramString + " is not a valid NFCID2.");
      return false;
    }
    try
    {
      Long.parseLong(paramString, 16);
      return true;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.e("NfcFCardEmulation", "NFCID2 " + paramString + " is not a valid NFCID2.");
    }
    return false;
  }
  
  public static boolean isValidSystemCode(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    if (paramString.length() != 4)
    {
      Log.e("NfcFCardEmulation", "System Code " + paramString + " is not a valid System Code.");
      return false;
    }
    if ((!paramString.startsWith("4")) || (paramString.toUpperCase().endsWith("FF")))
    {
      Log.e("NfcFCardEmulation", "System Code " + paramString + " is not a valid System Code.");
      return false;
    }
    try
    {
      Integer.valueOf(paramString, 16);
      return true;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.e("NfcFCardEmulation", "System Code " + paramString + " is not a valid System Code.");
    }
    return false;
  }
  
  public boolean disableService(Activity paramActivity)
    throws RuntimeException
  {
    if (paramActivity == null) {
      throw new NullPointerException("activity is null");
    }
    if (!paramActivity.isResumed()) {
      throw new IllegalArgumentException("Activity must be resumed.");
    }
    try
    {
      bool = sService.disableNfcFForegroundService();
      return bool;
    }
    catch (RemoteException paramActivity)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("NfcFCardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.disableNfcFForegroundService();
        return bool;
      }
      catch (RemoteException paramActivity)
      {
        Log.e("NfcFCardEmulation", "Failed to reach CardEmulationService.");
        paramActivity.rethrowAsRuntimeException();
      }
    }
    return false;
  }
  
  public boolean enableService(Activity paramActivity, ComponentName paramComponentName)
    throws RuntimeException
  {
    if ((paramActivity == null) || (paramComponentName == null)) {
      throw new NullPointerException("activity or service is null");
    }
    if (!paramActivity.isResumed()) {
      throw new IllegalArgumentException("Activity must be resumed.");
    }
    try
    {
      bool = sService.enableNfcFForegroundService(paramComponentName);
      return bool;
    }
    catch (RemoteException paramActivity)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("NfcFCardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.enableNfcFForegroundService(paramComponentName);
        return bool;
      }
      catch (RemoteException paramActivity)
      {
        Log.e("NfcFCardEmulation", "Failed to reach CardEmulationService.");
        paramActivity.rethrowAsRuntimeException();
      }
    }
    return false;
  }
  
  public int getMaxNumOfRegisterableSystemCodes()
  {
    try
    {
      i = sService.getMaxNumOfRegisterableSystemCodes();
      return i;
    }
    catch (RemoteException localRemoteException1)
    {
      int i;
      recoverService();
      if (sService == null)
      {
        Log.e("NfcFCardEmulation", "Failed to recover CardEmulationService.");
        return -1;
      }
      try
      {
        i = sService.getMaxNumOfRegisterableSystemCodes();
        return i;
      }
      catch (RemoteException localRemoteException2)
      {
        Log.e("NfcFCardEmulation", "Failed to reach CardEmulationService.");
      }
    }
    return -1;
  }
  
  public List<NfcFServiceInfo> getNfcFServices()
  {
    try
    {
      List localList1 = sService.getNfcFServices(UserHandle.myUserId());
      return localList1;
    }
    catch (RemoteException localRemoteException1)
    {
      recoverService();
      if (sService == null)
      {
        Log.e("NfcFCardEmulation", "Failed to recover CardEmulationService.");
        return null;
      }
      try
      {
        List localList2 = sService.getNfcFServices(UserHandle.myUserId());
        return localList2;
      }
      catch (RemoteException localRemoteException2)
      {
        Log.e("NfcFCardEmulation", "Failed to reach CardEmulationService.");
      }
    }
    return null;
  }
  
  public String getNfcid2ForService(ComponentName paramComponentName)
    throws RuntimeException
  {
    if (paramComponentName == null) {
      throw new NullPointerException("service is null");
    }
    try
    {
      String str = sService.getNfcid2ForService(UserHandle.myUserId(), paramComponentName);
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      recoverService();
      if (sService == null)
      {
        Log.e("NfcFCardEmulation", "Failed to recover CardEmulationService.");
        return null;
      }
      try
      {
        paramComponentName = sService.getNfcid2ForService(UserHandle.myUserId(), paramComponentName);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("NfcFCardEmulation", "Failed to reach CardEmulationService.");
        paramComponentName.rethrowAsRuntimeException();
      }
    }
    return null;
  }
  
  public String getSystemCodeForService(ComponentName paramComponentName)
    throws RuntimeException
  {
    if (paramComponentName == null) {
      throw new NullPointerException("service is null");
    }
    try
    {
      String str = sService.getSystemCodeForService(UserHandle.myUserId(), paramComponentName);
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      recoverService();
      if (sService == null)
      {
        Log.e("NfcFCardEmulation", "Failed to recover CardEmulationService.");
        return null;
      }
      try
      {
        paramComponentName = sService.getSystemCodeForService(UserHandle.myUserId(), paramComponentName);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("NfcFCardEmulation", "Failed to reach CardEmulationService.");
        paramComponentName.rethrowAsRuntimeException();
      }
    }
    return null;
  }
  
  void recoverService()
  {
    sService = NfcAdapter.getDefaultAdapter(this.mContext).getNfcFCardEmulationService();
  }
  
  public boolean registerSystemCodeForService(ComponentName paramComponentName, String paramString)
    throws RuntimeException
  {
    if ((paramComponentName == null) || (paramString == null)) {
      throw new NullPointerException("service or systemCode is null");
    }
    try
    {
      bool = sService.registerSystemCodeForService(UserHandle.myUserId(), paramComponentName, paramString);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("NfcFCardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.registerSystemCodeForService(UserHandle.myUserId(), paramComponentName, paramString);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("NfcFCardEmulation", "Failed to reach CardEmulationService.");
        paramComponentName.rethrowAsRuntimeException();
      }
    }
    return false;
  }
  
  public boolean setNfcid2ForService(ComponentName paramComponentName, String paramString)
    throws RuntimeException
  {
    if ((paramComponentName == null) || (paramString == null)) {
      throw new NullPointerException("service or nfcid2 is null");
    }
    try
    {
      bool = sService.setNfcid2ForService(UserHandle.myUserId(), paramComponentName, paramString);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("NfcFCardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.setNfcid2ForService(UserHandle.myUserId(), paramComponentName, paramString);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("NfcFCardEmulation", "Failed to reach CardEmulationService.");
        paramComponentName.rethrowAsRuntimeException();
      }
    }
    return false;
  }
  
  public boolean unregisterSystemCodeForService(ComponentName paramComponentName)
    throws RuntimeException
  {
    if (paramComponentName == null) {
      throw new NullPointerException("service is null");
    }
    try
    {
      bool = sService.removeSystemCodeForService(UserHandle.myUserId(), paramComponentName);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      boolean bool;
      recoverService();
      if (sService == null)
      {
        Log.e("NfcFCardEmulation", "Failed to recover CardEmulationService.");
        return false;
      }
      try
      {
        bool = sService.removeSystemCodeForService(UserHandle.myUserId(), paramComponentName);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        Log.e("NfcFCardEmulation", "Failed to reach CardEmulationService.");
        paramComponentName.rethrowAsRuntimeException();
      }
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/cardemulation/NfcFCardEmulation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */