package com.android.server;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Slog;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.SpellCheckerSubtype;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.inputmethod.InputMethodUtils;
import com.android.internal.textservice.ISpellCheckerService;
import com.android.internal.textservice.ISpellCheckerService.Stub;
import com.android.internal.textservice.ISpellCheckerSessionListener;
import com.android.internal.textservice.ITextServicesManager.Stub;
import com.android.internal.textservice.ITextServicesSessionListener;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import org.xmlpull.v1.XmlPullParserException;

public class TextServicesManagerService
  extends ITextServicesManager.Stub
{
  private static final boolean DBG = false;
  private static final String TAG = TextServicesManagerService.class.getSimpleName();
  private final Context mContext;
  private final TextServicesMonitor mMonitor;
  private final TextServicesSettings mSettings;
  private final HashMap<String, SpellCheckerBindGroup> mSpellCheckerBindGroups = new HashMap();
  private final ArrayList<SpellCheckerInfo> mSpellCheckerList = new ArrayList();
  private final HashMap<String, SpellCheckerInfo> mSpellCheckerMap = new HashMap();
  private boolean mSystemReady = false;
  private final UserManager mUserManager;
  
  public TextServicesManagerService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mUserManager = ((UserManager)this.mContext.getSystemService(UserManager.class));
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.USER_ADDED");
    localIntentFilter.addAction("android.intent.action.USER_REMOVED");
    this.mContext.registerReceiver(new TextServicesBroadcastReceiver(), localIntentFilter);
    int i = 0;
    try
    {
      int j = ActivityManagerNative.getDefault().getCurrentUser().id;
      i = j;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w(TAG, "Couldn't get current user ID; guessing it's 0", localRemoteException);
        continue;
        boolean bool = true;
      }
    }
    this.mMonitor = new TextServicesMonitor(null);
    this.mMonitor.register(paramContext, null, true);
    if ((this.mSystemReady) && (this.mUserManager.isUserUnlockingOrUnlocked(i)))
    {
      bool = false;
      this.mSettings = new TextServicesSettings(paramContext.getContentResolver(), i, bool);
      resetInternalState(i);
      return;
    }
  }
  
  private boolean bindCurrentSpellCheckerService(Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt)
  {
    if ((paramIntent == null) || (paramServiceConnection == null))
    {
      Slog.e(TAG, "--- bind failed: service = " + paramIntent + ", conn = " + paramServiceConnection);
      return false;
    }
    return this.mContext.bindServiceAsUser(paramIntent, paramServiceConnection, paramInt, new UserHandle(this.mSettings.getCurrentUserId()));
  }
  
  private static void buildSpellCheckerMapLocked(Context paramContext, ArrayList<SpellCheckerInfo> paramArrayList, HashMap<String, SpellCheckerInfo> paramHashMap, TextServicesSettings paramTextServicesSettings)
  {
    paramArrayList.clear();
    paramHashMap.clear();
    paramTextServicesSettings = paramContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.service.textservice.SpellCheckerService"), 128, paramTextServicesSettings.getCurrentUserId());
    int j = paramTextServicesSettings.size();
    int i = 0;
    if (i < j)
    {
      Object localObject = (ResolveInfo)paramTextServicesSettings.get(i);
      ServiceInfo localServiceInfo = ((ResolveInfo)localObject).serviceInfo;
      ComponentName localComponentName = new ComponentName(localServiceInfo.packageName, localServiceInfo.name);
      if (!"android.permission.BIND_TEXT_SERVICE".equals(localServiceInfo.permission)) {
        Slog.w(TAG, "Skipping text service " + localComponentName + ": it does not require the permission " + "android.permission.BIND_TEXT_SERVICE");
      }
      for (;;)
      {
        i += 1;
        break;
        try
        {
          localObject = new SpellCheckerInfo(paramContext, (ResolveInfo)localObject);
          if (((SpellCheckerInfo)localObject).getSubtypeCount() <= 0) {
            Slog.w(TAG, "Skipping text service " + localComponentName + ": it does not contain subtypes.");
          }
        }
        catch (XmlPullParserException localXmlPullParserException)
        {
          Slog.w(TAG, "Unable to load the spell checker " + localComponentName, localXmlPullParserException);
          continue;
          paramArrayList.add(localXmlPullParserException);
          paramHashMap.put(localXmlPullParserException.getId(), localXmlPullParserException);
        }
        catch (IOException localIOException)
        {
          Slog.w(TAG, "Unable to load the spell checker " + localComponentName, localIOException);
        }
      }
    }
  }
  
  private boolean calledFromValidUser()
  {
    int i = Binder.getCallingUid();
    int j = UserHandle.getUserId(i);
    if ((i == 1000) || (j == this.mSettings.getCurrentUserId())) {
      return true;
    }
    this.mSettings.isCurrentProfile(j);
    if (this.mSettings.isCurrentProfile(j))
    {
      SpellCheckerInfo localSpellCheckerInfo = getCurrentSpellCheckerWithoutVerification();
      if (localSpellCheckerInfo != null)
      {
        if ((localSpellCheckerInfo.getServiceInfo().applicationInfo.flags & 0x1) != 0) {}
        for (i = 1; i != 0; i = 0) {
          return true;
        }
      }
    }
    return false;
  }
  
  private SpellCheckerInfo findAvailSpellCheckerLocked(String paramString)
  {
    int m = this.mSpellCheckerList.size();
    if (m == 0)
    {
      Slog.w(TAG, "no available spell checker services found");
      return null;
    }
    Object localObject;
    if (paramString != null)
    {
      i = 0;
      while (i < m)
      {
        localObject = (SpellCheckerInfo)this.mSpellCheckerList.get(i);
        if (paramString.equals(((SpellCheckerInfo)localObject).getPackageName())) {
          return (SpellCheckerInfo)localObject;
        }
        i += 1;
      }
    }
    paramString = InputMethodUtils.getSuitableLocalesForSpellChecker(this.mContext.getResources().getConfiguration().locale);
    int n = paramString.size();
    int i = 0;
    while (i < n)
    {
      localObject = (Locale)paramString.get(i);
      int j = 0;
      while (j < m)
      {
        SpellCheckerInfo localSpellCheckerInfo = (SpellCheckerInfo)this.mSpellCheckerList.get(j);
        int i1 = localSpellCheckerInfo.getSubtypeCount();
        int k = 0;
        while (k < i1)
        {
          if (((Locale)localObject).equals(InputMethodUtils.constructLocaleFromString(localSpellCheckerInfo.getSubtypeAt(k).getLocale()))) {
            return localSpellCheckerInfo;
          }
          k += 1;
        }
        j += 1;
      }
      i += 1;
    }
    if (m > 1) {
      Slog.w(TAG, "more than one spell checker service found, picking first");
    }
    return (SpellCheckerInfo)this.mSpellCheckerList.get(0);
  }
  
  private SpellCheckerInfo getCurrentSpellCheckerWithoutVerification()
  {
    synchronized (this.mSpellCheckerMap)
    {
      Object localObject1 = this.mSettings.getSelectedSpellChecker();
      boolean bool = TextUtils.isEmpty((CharSequence)localObject1);
      if (bool) {
        return null;
      }
      localObject1 = (SpellCheckerInfo)this.mSpellCheckerMap.get(localObject1);
      return (SpellCheckerInfo)localObject1;
    }
  }
  
  private static String getStackTrace()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      throw new RuntimeException();
    }
    catch (RuntimeException localRuntimeException)
    {
      StackTraceElement[] arrayOfStackTraceElement = localRuntimeException.getStackTrace();
      int i = 1;
      while (i < arrayOfStackTraceElement.length)
      {
        localStringBuilder.append(arrayOfStackTraceElement[i].toString()).append("\n");
        i += 1;
      }
    }
    return localStringBuilder.toString();
  }
  
  private boolean isSpellCheckerEnabledLocked()
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      boolean bool = this.mSettings.isSpellCheckerEnabled();
      return bool;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void resetInternalState(int paramInt)
  {
    if ((this.mSystemReady) && (this.mUserManager.isUserUnlockingOrUnlocked(paramInt))) {}
    for (boolean bool = false;; bool = true)
    {
      this.mSettings.switchCurrentUser(paramInt, bool);
      updateCurrentProfileIds();
      unbindServiceLocked();
      buildSpellCheckerMapLocked(this.mContext, this.mSpellCheckerList, this.mSpellCheckerMap, this.mSettings);
      if (getCurrentSpellChecker(null) == null)
      {
        SpellCheckerInfo localSpellCheckerInfo = findAvailSpellCheckerLocked(null);
        if (localSpellCheckerInfo != null) {
          setCurrentSpellCheckerLocked(localSpellCheckerInfo.getId());
        }
      }
      return;
    }
  }
  
  private void setCurrentSpellCheckerLocked(String paramString)
  {
    if ((!TextUtils.isEmpty(paramString)) && (this.mSpellCheckerMap.containsKey(paramString)))
    {
      SpellCheckerInfo localSpellCheckerInfo = getCurrentSpellChecker(null);
      if ((localSpellCheckerInfo == null) || (!localSpellCheckerInfo.getId().equals(paramString))) {}
    }
    else
    {
      return;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mSettings.putSelectedSpellChecker(paramString);
      setCurrentSpellCheckerSubtypeLocked(0);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  /* Error */
  private void setCurrentSpellCheckerSubtypeLocked(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: invokevirtual 441	com/android/server/TextServicesManagerService:getCurrentSpellChecker	(Ljava/lang/String;)Landroid/view/textservice/SpellCheckerInfo;
    //   5: astore 7
    //   7: iconst_0
    //   8: istore 4
    //   10: iconst_0
    //   11: istore_2
    //   12: iload 4
    //   14: istore_3
    //   15: aload 7
    //   17: ifnull +30 -> 47
    //   20: iload 4
    //   22: istore_3
    //   23: iload_2
    //   24: aload 7
    //   26: invokevirtual 298	android/view/textservice/SpellCheckerInfo:getSubtypeCount	()I
    //   29: if_icmpge +18 -> 47
    //   32: aload 7
    //   34: iload_2
    //   35: invokevirtual 380	android/view/textservice/SpellCheckerInfo:getSubtypeAt	(I)Landroid/view/textservice/SpellCheckerSubtype;
    //   38: invokevirtual 453	android/view/textservice/SpellCheckerSubtype:hashCode	()I
    //   41: iload_1
    //   42: if_icmpne +24 -> 66
    //   45: iload_1
    //   46: istore_3
    //   47: invokestatic 421	android/os/Binder:clearCallingIdentity	()J
    //   50: lstore 5
    //   52: aload_0
    //   53: getfield 60	com/android/server/TextServicesManagerService:mSettings	Lcom/android/server/TextServicesManagerService$TextServicesSettings;
    //   56: iload_3
    //   57: invokevirtual 456	com/android/server/TextServicesManagerService$TextServicesSettings:putSelectedSpellCheckerSubtype	(I)V
    //   60: lload 5
    //   62: invokestatic 428	android/os/Binder:restoreCallingIdentity	(J)V
    //   65: return
    //   66: iload_2
    //   67: iconst_1
    //   68: iadd
    //   69: istore_2
    //   70: goto -58 -> 12
    //   73: astore 7
    //   75: lload 5
    //   77: invokestatic 428	android/os/Binder:restoreCallingIdentity	(J)V
    //   80: aload 7
    //   82: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	83	0	this	TextServicesManagerService
    //   0	83	1	paramInt	int
    //   11	59	2	i	int
    //   14	43	3	j	int
    //   8	13	4	k	int
    //   50	26	5	l	long
    //   5	28	7	localSpellCheckerInfo	SpellCheckerInfo
    //   73	8	7	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   52	60	73	finally
  }
  
  private void setSpellCheckerEnabledLocked(boolean paramBoolean)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mSettings.setSpellCheckerEnabled(paramBoolean);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void startSpellCheckerServiceInnerLocked(SpellCheckerInfo paramSpellCheckerInfo, String paramString, ITextServicesSessionListener paramITextServicesSessionListener, ISpellCheckerSessionListener paramISpellCheckerSessionListener, int paramInt, Bundle paramBundle)
  {
    String str = paramSpellCheckerInfo.getId();
    InternalServiceConnection localInternalServiceConnection = new InternalServiceConnection(str, paramString, paramBundle);
    Intent localIntent = new Intent("android.service.textservice.SpellCheckerService");
    localIntent.setComponent(paramSpellCheckerInfo.getComponent());
    if (!bindCurrentSpellCheckerService(localIntent, localInternalServiceConnection, 33554433))
    {
      Slog.e(TAG, "Failed to get a spell checker service.");
      return;
    }
    paramSpellCheckerInfo = new SpellCheckerBindGroup(localInternalServiceConnection, paramITextServicesSessionListener, paramString, paramISpellCheckerSessionListener, paramInt, paramBundle);
    this.mSpellCheckerBindGroups.put(str, paramSpellCheckerInfo);
  }
  
  private void unbindServiceLocked()
  {
    Iterator localIterator = this.mSpellCheckerBindGroups.values().iterator();
    while (localIterator.hasNext()) {
      ((SpellCheckerBindGroup)localIterator.next()).removeAll();
    }
    this.mSpellCheckerBindGroups.clear();
  }
  
  protected void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump TextServicesManagerService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    synchronized (this.mSpellCheckerMap)
    {
      paramPrintWriter.println("Current Text Services Manager state:");
      paramPrintWriter.println("  Spell Checkers:");
      int i = 0;
      paramArrayOfString = this.mSpellCheckerMap.values().iterator();
      Object localObject1;
      while (paramArrayOfString.hasNext())
      {
        localObject1 = (SpellCheckerInfo)paramArrayOfString.next();
        paramPrintWriter.println("  Spell Checker #" + i);
        ((SpellCheckerInfo)localObject1).dump(paramPrintWriter, "    ");
        i += 1;
      }
      paramPrintWriter.println("");
      paramPrintWriter.println("  Spell Checker Bind Groups:");
      paramArrayOfString = this.mSpellCheckerBindGroups.entrySet().iterator();
      while (paramArrayOfString.hasNext())
      {
        Object localObject2 = (Map.Entry)paramArrayOfString.next();
        localObject1 = (SpellCheckerBindGroup)((Map.Entry)localObject2).getValue();
        paramPrintWriter.println("    " + (String)((Map.Entry)localObject2).getKey() + " " + localObject1 + ":");
        paramPrintWriter.println("      mInternalConnection=" + SpellCheckerBindGroup.-get0((SpellCheckerBindGroup)localObject1));
        paramPrintWriter.println("      mSpellChecker=" + ((SpellCheckerBindGroup)localObject1).mSpellChecker);
        paramPrintWriter.println("      mBound=" + ((SpellCheckerBindGroup)localObject1).mBound + " mConnected=" + ((SpellCheckerBindGroup)localObject1).mConnected);
        int j = SpellCheckerBindGroup.-get1((SpellCheckerBindGroup)localObject1).size();
        i = 0;
        while (i < j)
        {
          localObject2 = (InternalDeathRecipient)SpellCheckerBindGroup.-get1((SpellCheckerBindGroup)localObject1).get(i);
          paramPrintWriter.println("      Listener #" + i + ":");
          paramPrintWriter.println("        mTsListener=" + ((InternalDeathRecipient)localObject2).mTsListener);
          paramPrintWriter.println("        mScListener=" + ((InternalDeathRecipient)localObject2).mScListener);
          paramPrintWriter.println("        mGroup=" + InternalDeathRecipient.-get0((InternalDeathRecipient)localObject2));
          paramPrintWriter.println("        mScLocale=" + ((InternalDeathRecipient)localObject2).mScLocale + " mUid=" + ((InternalDeathRecipient)localObject2).mUid);
          i += 1;
        }
      }
      paramPrintWriter.println("");
      paramPrintWriter.println("  mSettings:");
      this.mSettings.dumpLocked(paramPrintWriter, "    ");
      return;
    }
  }
  
  public void finishSpellCheckerService(ISpellCheckerSessionListener paramISpellCheckerSessionListener)
  {
    if (!calledFromValidUser()) {
      return;
    }
    ArrayList localArrayList;
    synchronized (this.mSpellCheckerMap)
    {
      localArrayList = new ArrayList();
      Iterator localIterator = this.mSpellCheckerBindGroups.values().iterator();
      while (localIterator.hasNext())
      {
        SpellCheckerBindGroup localSpellCheckerBindGroup = (SpellCheckerBindGroup)localIterator.next();
        if (localSpellCheckerBindGroup != null) {
          localArrayList.add(localSpellCheckerBindGroup);
        }
      }
    }
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      ((SpellCheckerBindGroup)localArrayList.get(i)).removeListener(paramISpellCheckerSessionListener);
      i += 1;
    }
  }
  
  public SpellCheckerInfo getCurrentSpellChecker(String paramString)
  {
    if (!calledFromValidUser()) {
      return null;
    }
    return getCurrentSpellCheckerWithoutVerification();
  }
  
  public SpellCheckerSubtype getCurrentSpellCheckerSubtype(String arg1, boolean paramBoolean)
  {
    if (!calledFromValidUser()) {
      return null;
    }
    int j;
    SpellCheckerInfo localSpellCheckerInfo;
    Object localObject4;
    synchronized (this.mSpellCheckerMap)
    {
      j = this.mSettings.getSelectedSpellCheckerSubtype(0);
      localSpellCheckerInfo = getCurrentSpellChecker(null);
      localObject4 = this.mContext.getResources().getConfiguration().locale;
      if ((localSpellCheckerInfo == null) || (localSpellCheckerInfo.getSubtypeCount() == 0)) {
        return null;
      }
    }
    Object localObject2;
    int i;
    String str;
    if ((j != 0) || (paramBoolean))
    {
      ??? = null;
      localObject3 = null;
      if (j == 0)
      {
        ??? = (InputMethodManager)this.mContext.getSystemService(InputMethodManager.class);
        localObject2 = localObject3;
        if (??? != null)
        {
          ??? = ???.getCurrentInputMethodSubtype();
          localObject2 = localObject3;
          if (??? != null)
          {
            ??? = ???.getLocale();
            localObject2 = localObject3;
            if (!TextUtils.isEmpty(???)) {
              localObject2 = ???;
            }
          }
        }
        ??? = (String)localObject2;
        if (localObject2 == null) {
          ??? = ((Locale)localObject4).toString();
        }
      }
      localObject2 = null;
      i = 0;
      if (i >= localSpellCheckerInfo.getSubtypeCount()) {
        break label286;
      }
      localObject4 = localSpellCheckerInfo.getSubtypeAt(i);
      if (j != 0) {
        break label269;
      }
      str = ((SpellCheckerSubtype)localObject4).getLocale();
      if (???.equals(str)) {
        return (SpellCheckerSubtype)localObject4;
      }
    }
    else
    {
      return null;
    }
    Object localObject3 = localObject2;
    if (localObject2 == null)
    {
      localObject3 = localObject2;
      if (???.length() >= 2)
      {
        localObject3 = localObject2;
        if (str.length() >= 2)
        {
          localObject3 = localObject2;
          if (???.startsWith(str)) {
            localObject3 = localObject4;
          }
        }
      }
    }
    label269:
    do
    {
      i += 1;
      localObject2 = localObject3;
      break;
      localObject3 = localObject2;
    } while (((SpellCheckerSubtype)localObject4).hashCode() != j);
    return (SpellCheckerSubtype)localObject4;
    label286:
    return (SpellCheckerSubtype)localObject2;
  }
  
  public SpellCheckerInfo[] getEnabledSpellCheckers()
  {
    if (!calledFromValidUser()) {
      return null;
    }
    return (SpellCheckerInfo[])this.mSpellCheckerList.toArray(new SpellCheckerInfo[this.mSpellCheckerList.size()]);
  }
  
  public void getSpellCheckerService(String paramString1, String paramString2, ITextServicesSessionListener paramITextServicesSessionListener, ISpellCheckerSessionListener paramISpellCheckerSessionListener, Bundle paramBundle)
  {
    if (!calledFromValidUser()) {
      return;
    }
    if (!this.mSystemReady) {
      return;
    }
    if ((TextUtils.isEmpty(paramString1)) || (paramITextServicesSessionListener == null)) {}
    while (paramISpellCheckerSessionListener == null)
    {
      Slog.e(TAG, "getSpellCheckerService: Invalid input.");
      return;
    }
    synchronized (this.mSpellCheckerMap)
    {
      boolean bool = this.mSpellCheckerMap.containsKey(paramString1);
      if (!bool) {
        return;
      }
      SpellCheckerInfo localSpellCheckerInfo = (SpellCheckerInfo)this.mSpellCheckerMap.get(paramString1);
      int j = Binder.getCallingUid();
      SpellCheckerBindGroup localSpellCheckerBindGroup;
      int i;
      if (this.mSpellCheckerBindGroups.containsKey(paramString1))
      {
        localSpellCheckerBindGroup = (SpellCheckerBindGroup)this.mSpellCheckerBindGroups.get(paramString1);
        if (localSpellCheckerBindGroup != null)
        {
          paramString1 = ((SpellCheckerBindGroup)this.mSpellCheckerBindGroups.get(paramString1)).addListener(paramITextServicesSessionListener, paramString2, paramISpellCheckerSessionListener, j, paramBundle);
          if (paramString1 == null) {
            return;
          }
          if (localSpellCheckerBindGroup.mSpellChecker != null) {
            break label214;
          }
          i = 1;
          if ((i & localSpellCheckerBindGroup.mConnected) == 0) {
            break label220;
          }
          Slog.e(TAG, "The state of the spell checker bind group is illegal.");
          localSpellCheckerBindGroup.removeAll();
        }
      }
      for (;;)
      {
        l = Binder.clearCallingIdentity();
        try
        {
          startSpellCheckerServiceInnerLocked(localSpellCheckerInfo, paramString2, paramITextServicesSessionListener, paramISpellCheckerSessionListener, j, paramBundle);
          Binder.restoreCallingIdentity(l);
          return;
        }
        finally
        {
          label214:
          ISpellCheckerService localISpellCheckerService;
          Binder.restoreCallingIdentity(l);
        }
        i = 0;
        break;
        label220:
        localISpellCheckerService = localSpellCheckerBindGroup.mSpellChecker;
        if (localISpellCheckerService == null) {
          continue;
        }
        try
        {
          paramString1 = localSpellCheckerBindGroup.mSpellChecker.getISpellCheckerSession(paramString1.mScLocale, paramString1.mScListener, paramBundle);
          if (paramString1 != null)
          {
            paramITextServicesSessionListener.onServiceConnected(paramString1);
            return;
          }
          localSpellCheckerBindGroup.removeAll();
        }
        catch (RemoteException paramString1)
        {
          Slog.e(TAG, "Exception in getting spell checker session: " + paramString1);
          localSpellCheckerBindGroup.removeAll();
        }
      }
    }
  }
  
  public boolean isSpellCheckerEnabled()
  {
    if (!calledFromValidUser()) {
      return false;
    }
    synchronized (this.mSpellCheckerMap)
    {
      boolean bool = isSpellCheckerEnabledLocked();
      return bool;
    }
  }
  
  void onSwitchUser(int paramInt)
  {
    synchronized (this.mSpellCheckerMap)
    {
      resetInternalState(paramInt);
      return;
    }
  }
  
  void onUnlockUser(int paramInt)
  {
    synchronized (this.mSpellCheckerMap)
    {
      int i = this.mSettings.getCurrentUserId();
      if (paramInt != i) {
        return;
      }
      resetInternalState(i);
      return;
    }
  }
  
  public void setCurrentSpellChecker(String arg1, String paramString2)
  {
    if (!calledFromValidUser()) {
      return;
    }
    synchronized (this.mSpellCheckerMap)
    {
      if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
        throw new SecurityException("Requires permission android.permission.WRITE_SECURE_SETTINGS");
      }
    }
    setCurrentSpellCheckerLocked(paramString2);
  }
  
  public void setCurrentSpellCheckerSubtype(String arg1, int paramInt)
  {
    if (!calledFromValidUser()) {
      return;
    }
    synchronized (this.mSpellCheckerMap)
    {
      if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
        throw new SecurityException("Requires permission android.permission.WRITE_SECURE_SETTINGS");
      }
    }
    setCurrentSpellCheckerSubtypeLocked(paramInt);
  }
  
  public void setSpellCheckerEnabled(boolean paramBoolean)
  {
    if (!calledFromValidUser()) {
      return;
    }
    synchronized (this.mSpellCheckerMap)
    {
      if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
        throw new SecurityException("Requires permission android.permission.WRITE_SECURE_SETTINGS");
      }
    }
    setSpellCheckerEnabledLocked(paramBoolean);
  }
  
  void systemRunning()
  {
    synchronized (this.mSpellCheckerMap)
    {
      if (!this.mSystemReady)
      {
        this.mSystemReady = true;
        resetInternalState(this.mSettings.getCurrentUserId());
      }
      return;
    }
  }
  
  void updateCurrentProfileIds()
  {
    this.mSettings.setCurrentProfileIds(this.mUserManager.getProfileIdsWithDisabled(this.mSettings.getCurrentUserId()));
  }
  
  private class InternalDeathRecipient
    implements IBinder.DeathRecipient
  {
    public final Bundle mBundle;
    private final TextServicesManagerService.SpellCheckerBindGroup mGroup;
    public final ISpellCheckerSessionListener mScListener;
    public final String mScLocale;
    public final ITextServicesSessionListener mTsListener;
    public final int mUid;
    
    public InternalDeathRecipient(TextServicesManagerService.SpellCheckerBindGroup paramSpellCheckerBindGroup, ITextServicesSessionListener paramITextServicesSessionListener, String paramString, ISpellCheckerSessionListener paramISpellCheckerSessionListener, int paramInt, Bundle paramBundle)
    {
      this.mTsListener = paramITextServicesSessionListener;
      this.mScListener = paramISpellCheckerSessionListener;
      this.mScLocale = paramString;
      this.mGroup = paramSpellCheckerBindGroup;
      this.mUid = paramInt;
      this.mBundle = paramBundle;
    }
    
    public void binderDied()
    {
      this.mGroup.removeListener(this.mScListener);
    }
    
    public boolean hasSpellCheckerListener(ISpellCheckerSessionListener paramISpellCheckerSessionListener)
    {
      return paramISpellCheckerSessionListener.asBinder().equals(this.mScListener.asBinder());
    }
  }
  
  private class InternalServiceConnection
    implements ServiceConnection
  {
    private final Bundle mBundle;
    private final String mLocale;
    private final String mSciId;
    
    public InternalServiceConnection(String paramString1, String paramString2, Bundle paramBundle)
    {
      this.mSciId = paramString1;
      this.mLocale = paramString2;
      this.mBundle = paramBundle;
    }
    
    private void onServiceConnectedInnerLocked(ComponentName paramComponentName, IBinder paramIBinder)
    {
      paramComponentName = ISpellCheckerService.Stub.asInterface(paramIBinder);
      paramIBinder = (TextServicesManagerService.SpellCheckerBindGroup)TextServicesManagerService.-get3(TextServicesManagerService.this).get(this.mSciId);
      if ((paramIBinder != null) && (this == TextServicesManagerService.SpellCheckerBindGroup.-get0(paramIBinder))) {
        paramIBinder.onServiceConnected(paramComponentName);
      }
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      synchronized (TextServicesManagerService.-get5(TextServicesManagerService.this))
      {
        onServiceConnectedInnerLocked(paramComponentName, paramIBinder);
        return;
      }
    }
    
    public void onServiceDisconnected(ComponentName arg1)
    {
      synchronized (TextServicesManagerService.-get5(TextServicesManagerService.this))
      {
        TextServicesManagerService.SpellCheckerBindGroup localSpellCheckerBindGroup = (TextServicesManagerService.SpellCheckerBindGroup)TextServicesManagerService.-get3(TextServicesManagerService.this).get(this.mSciId);
        if ((localSpellCheckerBindGroup != null) && (this == TextServicesManagerService.SpellCheckerBindGroup.-get0(localSpellCheckerBindGroup))) {
          TextServicesManagerService.-get3(TextServicesManagerService.this).remove(this.mSciId);
        }
        return;
      }
    }
  }
  
  public static final class Lifecycle
    extends SystemService
  {
    private TextServicesManagerService mService;
    
    public Lifecycle(Context paramContext)
    {
      super();
      this.mService = new TextServicesManagerService(paramContext);
    }
    
    public void onBootPhase(int paramInt)
    {
      if (paramInt == 550) {
        this.mService.systemRunning();
      }
    }
    
    public void onStart()
    {
      publishBinderService("textservices", this.mService);
    }
    
    public void onSwitchUser(int paramInt)
    {
      this.mService.onSwitchUser(paramInt);
    }
    
    public void onUnlockUser(int paramInt)
    {
      this.mService.onUnlockUser(paramInt);
    }
  }
  
  private class SpellCheckerBindGroup
  {
    private final String TAG = SpellCheckerBindGroup.class.getSimpleName();
    public boolean mBound;
    public boolean mConnected;
    private final TextServicesManagerService.InternalServiceConnection mInternalConnection;
    private final CopyOnWriteArrayList<TextServicesManagerService.InternalDeathRecipient> mListeners = new CopyOnWriteArrayList();
    public ISpellCheckerService mSpellChecker;
    
    public SpellCheckerBindGroup(TextServicesManagerService.InternalServiceConnection paramInternalServiceConnection, ITextServicesSessionListener paramITextServicesSessionListener, String paramString, ISpellCheckerSessionListener paramISpellCheckerSessionListener, int paramInt, Bundle paramBundle)
    {
      this.mInternalConnection = paramInternalServiceConnection;
      this.mBound = true;
      this.mConnected = false;
      addListener(paramITextServicesSessionListener, paramString, paramISpellCheckerSessionListener, paramInt, paramBundle);
    }
    
    private void cleanLocked()
    {
      if ((this.mBound) && (this.mListeners.isEmpty()))
      {
        this.mBound = false;
        String str = TextServicesManagerService.InternalServiceConnection.-get0(this.mInternalConnection);
        if ((SpellCheckerBindGroup)TextServicesManagerService.-get3(TextServicesManagerService.this).get(str) == this) {
          TextServicesManagerService.-get3(TextServicesManagerService.this).remove(str);
        }
        TextServicesManagerService.-get1(TextServicesManagerService.this).unbindService(this.mInternalConnection);
      }
    }
    
    /* Error */
    public TextServicesManagerService.InternalDeathRecipient addListener(ITextServicesSessionListener paramITextServicesSessionListener, String paramString, ISpellCheckerSessionListener paramISpellCheckerSessionListener, int paramInt, Bundle paramBundle)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 34	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:this$0	Lcom/android/server/TextServicesManagerService;
      //   4: invokestatic 93	com/android/server/TextServicesManagerService:-get5	(Lcom/android/server/TextServicesManagerService;)Ljava/util/HashMap;
      //   7: astore 9
      //   9: aload 9
      //   11: monitorenter
      //   12: aload_0
      //   13: getfield 30	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:mListeners	Ljava/util/concurrent/CopyOnWriteArrayList;
      //   16: invokevirtual 97	java/util/concurrent/CopyOnWriteArrayList:size	()I
      //   19: istore 7
      //   21: iconst_0
      //   22: istore 6
      //   24: iload 6
      //   26: iload 7
      //   28: if_icmpge +40 -> 68
      //   31: aload_0
      //   32: getfield 30	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:mListeners	Ljava/util/concurrent/CopyOnWriteArrayList;
      //   35: iload 6
      //   37: invokevirtual 100	java/util/concurrent/CopyOnWriteArrayList:get	(I)Ljava/lang/Object;
      //   40: checkcast 102	com/android/server/TextServicesManagerService$InternalDeathRecipient
      //   43: aload_3
      //   44: invokevirtual 106	com/android/server/TextServicesManagerService$InternalDeathRecipient:hasSpellCheckerListener	(Lcom/android/internal/textservice/ISpellCheckerSessionListener;)Z
      //   47: istore 8
      //   49: iload 8
      //   51: ifeq +8 -> 59
      //   54: aload 9
      //   56: monitorexit
      //   57: aconst_null
      //   58: areturn
      //   59: iload 6
      //   61: iconst_1
      //   62: iadd
      //   63: istore 6
      //   65: goto -41 -> 24
      //   68: new 102	com/android/server/TextServicesManagerService$InternalDeathRecipient
      //   71: dup
      //   72: aload_0
      //   73: getfield 34	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:this$0	Lcom/android/server/TextServicesManagerService;
      //   76: aload_0
      //   77: aload_1
      //   78: aload_2
      //   79: aload_3
      //   80: iload 4
      //   82: aload 5
      //   84: invokespecial 109	com/android/server/TextServicesManagerService$InternalDeathRecipient:<init>	(Lcom/android/server/TextServicesManagerService;Lcom/android/server/TextServicesManagerService$SpellCheckerBindGroup;Lcom/android/internal/textservice/ITextServicesSessionListener;Ljava/lang/String;Lcom/android/internal/textservice/ISpellCheckerSessionListener;ILandroid/os/Bundle;)V
      //   87: astore_1
      //   88: aload_3
      //   89: invokeinterface 115 1 0
      //   94: aload_1
      //   95: iconst_0
      //   96: invokeinterface 121 3 0
      //   101: aload_0
      //   102: getfield 30	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:mListeners	Ljava/util/concurrent/CopyOnWriteArrayList;
      //   105: aload_1
      //   106: invokevirtual 125	java/util/concurrent/CopyOnWriteArrayList:add	(Ljava/lang/Object;)Z
      //   109: pop
      //   110: aload_0
      //   111: invokespecial 127	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:cleanLocked	()V
      //   114: aload 9
      //   116: monitorexit
      //   117: aload_1
      //   118: areturn
      //   119: astore_1
      //   120: aload 9
      //   122: monitorexit
      //   123: aload_1
      //   124: athrow
      //   125: astore_1
      //   126: goto -6 -> 120
      //   129: astore_1
      //   130: aconst_null
      //   131: astore_1
      //   132: goto -22 -> 110
      //   135: astore_2
      //   136: goto -26 -> 110
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	139	0	this	SpellCheckerBindGroup
      //   0	139	1	paramITextServicesSessionListener	ITextServicesSessionListener
      //   0	139	2	paramString	String
      //   0	139	3	paramISpellCheckerSessionListener	ISpellCheckerSessionListener
      //   0	139	4	paramInt	int
      //   0	139	5	paramBundle	Bundle
      //   22	42	6	i	int
      //   19	10	7	j	int
      //   47	3	8	bool	boolean
      //   7	114	9	localHashMap	HashMap
      // Exception table:
      //   from	to	target	type
      //   12	21	119	finally
      //   31	49	119	finally
      //   68	88	119	finally
      //   88	110	125	finally
      //   110	114	125	finally
      //   12	21	129	android/os/RemoteException
      //   31	49	129	android/os/RemoteException
      //   68	88	129	android/os/RemoteException
      //   88	110	135	android/os/RemoteException
    }
    
    /* Error */
    public void onServiceConnected(ISpellCheckerService paramISpellCheckerService)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 30	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:mListeners	Ljava/util/concurrent/CopyOnWriteArrayList;
      //   4: invokeinterface 135 1 0
      //   9: astore_3
      //   10: aload_3
      //   11: invokeinterface 140 1 0
      //   16: ifeq +98 -> 114
      //   19: aload_3
      //   20: invokeinterface 144 1 0
      //   25: checkcast 102	com/android/server/TextServicesManagerService$InternalDeathRecipient
      //   28: astore 4
      //   30: aload_1
      //   31: aload 4
      //   33: getfield 147	com/android/server/TextServicesManagerService$InternalDeathRecipient:mScLocale	Ljava/lang/String;
      //   36: aload 4
      //   38: getfield 151	com/android/server/TextServicesManagerService$InternalDeathRecipient:mScListener	Lcom/android/internal/textservice/ISpellCheckerSessionListener;
      //   41: aload 4
      //   43: getfield 155	com/android/server/TextServicesManagerService$InternalDeathRecipient:mBundle	Landroid/os/Bundle;
      //   46: invokeinterface 161 4 0
      //   51: astore 5
      //   53: aload_0
      //   54: getfield 34	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:this$0	Lcom/android/server/TextServicesManagerService;
      //   57: invokestatic 93	com/android/server/TextServicesManagerService:-get5	(Lcom/android/server/TextServicesManagerService;)Ljava/util/HashMap;
      //   60: astore_2
      //   61: aload_2
      //   62: monitorenter
      //   63: aload_0
      //   64: getfield 30	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:mListeners	Ljava/util/concurrent/CopyOnWriteArrayList;
      //   67: aload 4
      //   69: invokevirtual 164	java/util/concurrent/CopyOnWriteArrayList:contains	(Ljava/lang/Object;)Z
      //   72: ifeq +15 -> 87
      //   75: aload 4
      //   77: getfield 168	com/android/server/TextServicesManagerService$InternalDeathRecipient:mTsListener	Lcom/android/internal/textservice/ITextServicesSessionListener;
      //   80: aload 5
      //   82: invokeinterface 173 2 0
      //   87: aload_2
      //   88: monitorexit
      //   89: goto -79 -> 10
      //   92: astore_1
      //   93: aload_0
      //   94: getfield 45	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:TAG	Ljava/lang/String;
      //   97: ldc -81
      //   99: aload_1
      //   100: invokestatic 181	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   103: pop
      //   104: aload_0
      //   105: invokevirtual 184	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:removeAll	()V
      //   108: return
      //   109: astore_1
      //   110: aload_2
      //   111: monitorexit
      //   112: aload_1
      //   113: athrow
      //   114: aload_0
      //   115: getfield 34	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:this$0	Lcom/android/server/TextServicesManagerService;
      //   118: invokestatic 93	com/android/server/TextServicesManagerService:-get5	(Lcom/android/server/TextServicesManagerService;)Ljava/util/HashMap;
      //   121: astore_2
      //   122: aload_2
      //   123: monitorenter
      //   124: aload_0
      //   125: aload_1
      //   126: putfield 186	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:mSpellChecker	Lcom/android/internal/textservice/ISpellCheckerService;
      //   129: aload_0
      //   130: iconst_1
      //   131: putfield 52	com/android/server/TextServicesManagerService$SpellCheckerBindGroup:mConnected	Z
      //   134: aload_2
      //   135: monitorexit
      //   136: return
      //   137: astore_1
      //   138: aload_2
      //   139: monitorexit
      //   140: aload_1
      //   141: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	142	0	this	SpellCheckerBindGroup
      //   0	142	1	paramISpellCheckerService	ISpellCheckerService
      //   9	11	3	localIterator	Iterator
      //   28	48	4	localInternalDeathRecipient	TextServicesManagerService.InternalDeathRecipient
      //   51	30	5	localISpellCheckerSession	com.android.internal.textservice.ISpellCheckerSession
      // Exception table:
      //   from	to	target	type
      //   30	63	92	android/os/RemoteException
      //   87	89	92	android/os/RemoteException
      //   110	114	92	android/os/RemoteException
      //   63	87	109	finally
      //   124	134	137	finally
    }
    
    public void removeAll()
    {
      Slog.e(this.TAG, "Remove the spell checker bind unexpectedly.");
      synchronized (TextServicesManagerService.-get5(TextServicesManagerService.this))
      {
        int j = this.mListeners.size();
        int i = 0;
        while (i < j)
        {
          TextServicesManagerService.InternalDeathRecipient localInternalDeathRecipient = (TextServicesManagerService.InternalDeathRecipient)this.mListeners.get(i);
          localInternalDeathRecipient.mScListener.asBinder().unlinkToDeath(localInternalDeathRecipient, 0);
          i += 1;
        }
        this.mListeners.clear();
        cleanLocked();
        return;
      }
    }
    
    public void removeListener(ISpellCheckerSessionListener paramISpellCheckerSessionListener)
    {
      for (;;)
      {
        int i;
        synchronized (TextServicesManagerService.-get5(TextServicesManagerService.this))
        {
          int j = this.mListeners.size();
          ArrayList localArrayList = new ArrayList();
          i = 0;
          if (i < j)
          {
            TextServicesManagerService.InternalDeathRecipient localInternalDeathRecipient = (TextServicesManagerService.InternalDeathRecipient)this.mListeners.get(i);
            if (localInternalDeathRecipient.hasSpellCheckerListener(paramISpellCheckerSessionListener)) {
              localArrayList.add(localInternalDeathRecipient);
            }
          }
          else
          {
            j = localArrayList.size();
            i = 0;
            if (i < j)
            {
              paramISpellCheckerSessionListener = (TextServicesManagerService.InternalDeathRecipient)localArrayList.get(i);
              paramISpellCheckerSessionListener.mScListener.asBinder().unlinkToDeath(paramISpellCheckerSessionListener, 0);
              this.mListeners.remove(paramISpellCheckerSessionListener);
              i += 1;
              continue;
            }
            cleanLocked();
            return;
          }
        }
        i += 1;
      }
    }
  }
  
  class TextServicesBroadcastReceiver
    extends BroadcastReceiver
  {
    TextServicesBroadcastReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      if (("android.intent.action.USER_ADDED".equals(paramContext)) || ("android.intent.action.USER_REMOVED".equals(paramContext)))
      {
        TextServicesManagerService.this.updateCurrentProfileIds();
        return;
      }
      Slog.w(TextServicesManagerService.-get0(), "Unexpected intent " + paramIntent);
    }
  }
  
  private class TextServicesMonitor
    extends PackageMonitor
  {
    private TextServicesMonitor() {}
    
    private boolean isChangingPackagesOfCurrentUser()
    {
      return getChangingUserId() == TextServicesManagerService.-get2(TextServicesManagerService.this).getCurrentUserId();
    }
    
    public void onSomePackagesChanged()
    {
      if (!isChangingPackagesOfCurrentUser()) {
        return;
      }
      synchronized (TextServicesManagerService.-get5(TextServicesManagerService.this))
      {
        TextServicesManagerService.-wrap1(TextServicesManagerService.-get1(TextServicesManagerService.this), TextServicesManagerService.-get4(TextServicesManagerService.this), TextServicesManagerService.-get5(TextServicesManagerService.this), TextServicesManagerService.-get2(TextServicesManagerService.this));
        Object localObject1 = TextServicesManagerService.this.getCurrentSpellChecker(null);
        if (localObject1 == null) {
          return;
        }
        localObject1 = ((SpellCheckerInfo)localObject1).getPackageName();
        int i = isPackageDisappearing((String)localObject1);
        if ((i == 3) || (i == 2))
        {
          localObject1 = TextServicesManagerService.-wrap0(TextServicesManagerService.this, (String)localObject1);
          if (localObject1 != null) {
            TextServicesManagerService.-wrap2(TextServicesManagerService.this, ((SpellCheckerInfo)localObject1).getId());
          }
        }
        boolean bool;
        do
        {
          return;
          bool = isPackageModified((String)localObject1);
        } while (!bool);
      }
    }
  }
  
  private static class TextServicesSettings
  {
    private boolean mCopyOnWrite = false;
    private final HashMap<String, String> mCopyOnWriteDataStore = new HashMap();
    @GuardedBy("mLock")
    private int[] mCurrentProfileIds = new int[0];
    private int mCurrentUserId;
    private Object mLock = new Object();
    private final ContentResolver mResolver;
    
    public TextServicesSettings(ContentResolver paramContentResolver, int paramInt, boolean paramBoolean)
    {
      this.mResolver = paramContentResolver;
      switchCurrentUser(paramInt, paramBoolean);
    }
    
    private boolean getBoolean(String paramString, boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = 1; getInt(paramString, i) == 1; i = 0) {
        return true;
      }
      return false;
    }
    
    private int getInt(String paramString, int paramInt)
    {
      if ((this.mCopyOnWrite) && (this.mCopyOnWriteDataStore.containsKey(paramString)))
      {
        paramString = (String)this.mCopyOnWriteDataStore.get(paramString);
        if (paramString != null) {
          return Integer.parseInt(paramString);
        }
        return 0;
      }
      return Settings.Secure.getIntForUser(this.mResolver, paramString, paramInt, this.mCurrentUserId);
    }
    
    private String getString(String paramString1, String paramString2)
    {
      if ((this.mCopyOnWrite) && (this.mCopyOnWriteDataStore.containsKey(paramString1))) {}
      for (paramString1 = (String)this.mCopyOnWriteDataStore.get(paramString1); paramString1 != null; paramString1 = Settings.Secure.getStringForUser(this.mResolver, paramString1, this.mCurrentUserId)) {
        return paramString1;
      }
      return paramString2;
    }
    
    private void putBoolean(String paramString, boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        putInt(paramString, i);
        return;
      }
    }
    
    private void putInt(String paramString, int paramInt)
    {
      if (this.mCopyOnWrite)
      {
        this.mCopyOnWriteDataStore.put(paramString, String.valueOf(paramInt));
        return;
      }
      Settings.Secure.putIntForUser(this.mResolver, paramString, paramInt, this.mCurrentUserId);
    }
    
    private void putString(String paramString1, String paramString2)
    {
      if (this.mCopyOnWrite)
      {
        this.mCopyOnWriteDataStore.put(paramString1, paramString2);
        return;
      }
      Settings.Secure.putStringForUser(this.mResolver, paramString1, paramString2, this.mCurrentUserId);
    }
    
    public void dumpLocked(PrintWriter paramPrintWriter, String paramString)
    {
      paramPrintWriter.println(paramString + "mCurrentUserId=" + this.mCurrentUserId);
      paramPrintWriter.println(paramString + "mCurrentProfileIds=" + Arrays.toString(this.mCurrentProfileIds));
      paramPrintWriter.println(paramString + "mCopyOnWrite=" + this.mCopyOnWrite);
    }
    
    public int getCurrentUserId()
    {
      return this.mCurrentUserId;
    }
    
    public String getSelectedSpellChecker()
    {
      return getString("selected_spell_checker", "");
    }
    
    public int getSelectedSpellCheckerSubtype(int paramInt)
    {
      return getInt("selected_spell_checker_subtype", paramInt);
    }
    
    public boolean isCurrentProfile(int paramInt)
    {
      synchronized (this.mLock)
      {
        int i = this.mCurrentUserId;
        if (paramInt == i) {
          return true;
        }
        i = 0;
        while (i < this.mCurrentProfileIds.length)
        {
          int j = this.mCurrentProfileIds[i];
          if (paramInt == j) {
            return true;
          }
          i += 1;
        }
        return false;
      }
    }
    
    public boolean isSpellCheckerEnabled()
    {
      return getBoolean("spell_checker_enabled", true);
    }
    
    public void putSelectedSpellChecker(String paramString)
    {
      if (TextUtils.isEmpty(paramString))
      {
        putString("selected_spell_checker", null);
        return;
      }
      putString("selected_spell_checker", paramString);
    }
    
    public void putSelectedSpellCheckerSubtype(int paramInt)
    {
      putInt("selected_spell_checker_subtype", paramInt);
    }
    
    public void setCurrentProfileIds(int[] paramArrayOfInt)
    {
      synchronized (this.mLock)
      {
        this.mCurrentProfileIds = paramArrayOfInt;
        return;
      }
    }
    
    public void setSpellCheckerEnabled(boolean paramBoolean)
    {
      putBoolean("spell_checker_enabled", paramBoolean);
    }
    
    public void switchCurrentUser(int paramInt, boolean paramBoolean)
    {
      if ((this.mCurrentUserId != paramInt) || (this.mCopyOnWrite != paramBoolean)) {
        this.mCopyOnWriteDataStore.clear();
      }
      this.mCurrentUserId = paramInt;
      this.mCopyOnWrite = paramBoolean;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/TextServicesManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */