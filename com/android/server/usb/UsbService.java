package com.android.server.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.IUsbManager.Stub;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbPort;
import android.hardware.usb.UsbPortStatus;
import android.os.Binder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import com.android.server.SystemService;
import java.io.File;

public class UsbService
  extends IUsbManager.Stub
{
  private static final String TAG = "UsbService";
  private final UsbAlsaManager mAlsaManager;
  private final Context mContext;
  private UsbDeviceManager mDeviceManager;
  private UsbHostManager mHostManager;
  private final Object mLock = new Object();
  private UsbPortManager mPortManager;
  private BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", -1);
      ??? = paramAnonymousIntent.getAction();
      if ("android.intent.action.USER_SWITCHED".equals(???)) {
        UsbService.-wrap0(UsbService.this, i);
      }
      do
      {
        return;
        if ("android.intent.action.USER_STOPPED".equals(???)) {
          synchronized (UsbService.-get1(UsbService.this))
          {
            UsbService.-get2(UsbService.this).remove(i);
            return;
          }
        }
      } while ((!"android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(???)) || (UsbService.-get0(UsbService.this) == null));
      UsbService.-get0(UsbService.this).updateUserRestrictions();
    }
  };
  @GuardedBy("mLock")
  private final SparseArray<UsbSettingsManager> mSettingsByUser = new SparseArray();
  
  public UsbService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAlsaManager = new UsbAlsaManager(paramContext);
    if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.usb.host")) {
      this.mHostManager = new UsbHostManager(paramContext, this.mAlsaManager);
    }
    if (new File("/sys/class/android_usb").exists()) {
      this.mDeviceManager = new UsbDeviceManager(paramContext, this.mAlsaManager);
    }
    if ((this.mHostManager != null) || (this.mDeviceManager != null)) {
      this.mPortManager = new UsbPortManager(paramContext);
    }
    setCurrentUser(0);
    paramContext = new IntentFilter();
    paramContext.setPriority(1000);
    paramContext.addAction("android.intent.action.USER_SWITCHED");
    paramContext.addAction("android.intent.action.USER_STOPPED");
    paramContext.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
    this.mContext.registerReceiver(this.mReceiver, paramContext, null, null);
  }
  
  private UsbSettingsManager getSettingsForUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      UsbSettingsManager localUsbSettingsManager2 = (UsbSettingsManager)this.mSettingsByUser.get(paramInt);
      UsbSettingsManager localUsbSettingsManager1 = localUsbSettingsManager2;
      if (localUsbSettingsManager2 == null)
      {
        localUsbSettingsManager1 = new UsbSettingsManager(this.mContext, new UserHandle(paramInt));
        this.mSettingsByUser.put(paramInt, localUsbSettingsManager1);
      }
      return localUsbSettingsManager1;
    }
  }
  
  private static boolean isSupportedCurrentFunction(String paramString)
  {
    if (paramString == null) {
      return true;
    }
    if (paramString.equals("none")) {}
    while ((paramString.equals("audio_source")) || (paramString.equals("midi")) || (paramString.equals("mtp")) || (paramString.equals("ptp")) || (paramString.equals("rndis")) || (paramString.equals("charging"))) {
      return true;
    }
    return false;
  }
  
  private static final String removeLastChar(String paramString)
  {
    return paramString.substring(0, paramString.length() - 1);
  }
  
  private void setCurrentUser(int paramInt)
  {
    UsbSettingsManager localUsbSettingsManager = getSettingsForUser(paramInt);
    if (this.mHostManager != null) {
      this.mHostManager.setCurrentSettings(localUsbSettingsManager);
    }
    if (this.mDeviceManager != null) {
      this.mDeviceManager.setCurrentUser(paramInt, localUsbSettingsManager);
    }
  }
  
  public void allowUsbDebugging(boolean paramBoolean, String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    this.mDeviceManager.allowUsbDebugging(paramBoolean, paramString);
  }
  
  public void bootCompleted()
  {
    if (this.mDeviceManager != null) {
      this.mDeviceManager.bootCompleted();
    }
  }
  
  public void clearDefaults(String paramString, int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    getSettingsForUser(paramInt).clearDefaults(paramString);
  }
  
  public void clearUsbDebuggingKeys()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    this.mDeviceManager.clearUsbDebuggingKeys();
  }
  
  public void denyUsbDebugging()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    this.mDeviceManager.denyUsbDebugging();
  }
  
  /* Error */
  public void dump(java.io.FileDescriptor paramFileDescriptor, java.io.PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 69	com/android/server/usb/UsbService:mContext	Landroid/content/Context;
    //   4: ldc -36
    //   6: ldc 13
    //   8: invokevirtual 201	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   11: new 222	com/android/internal/util/IndentingPrintWriter
    //   14: dup
    //   15: aload_2
    //   16: ldc -32
    //   18: invokespecial 227	com/android/internal/util/IndentingPrintWriter:<init>	(Ljava/io/Writer;Ljava/lang/String;)V
    //   21: astore_2
    //   22: invokestatic 233	android/os/Binder:clearCallingIdentity	()J
    //   25: lstore 7
    //   27: aload_3
    //   28: ifnull +8 -> 36
    //   31: aload_3
    //   32: arraylength
    //   33: ifne +165 -> 198
    //   36: aload_2
    //   37: ldc -21
    //   39: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   42: aload_2
    //   43: invokevirtual 241	com/android/internal/util/IndentingPrintWriter:increaseIndent	()V
    //   46: aload_0
    //   47: getfield 36	com/android/server/usb/UsbService:mDeviceManager	Lcom/android/server/usb/UsbDeviceManager;
    //   50: ifnull +11 -> 61
    //   53: aload_0
    //   54: getfield 36	com/android/server/usb/UsbService:mDeviceManager	Lcom/android/server/usb/UsbDeviceManager;
    //   57: aload_2
    //   58: invokevirtual 244	com/android/server/usb/UsbDeviceManager:dump	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   61: aload_0
    //   62: getfield 96	com/android/server/usb/UsbService:mHostManager	Lcom/android/server/usb/UsbHostManager;
    //   65: ifnull +11 -> 76
    //   68: aload_0
    //   69: getfield 96	com/android/server/usb/UsbService:mHostManager	Lcom/android/server/usb/UsbHostManager;
    //   72: aload_2
    //   73: invokevirtual 245	com/android/server/usb/UsbHostManager:dump	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   76: aload_0
    //   77: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   80: ifnull +11 -> 91
    //   83: aload_0
    //   84: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   87: aload_2
    //   88: invokevirtual 246	com/android/server/usb/UsbPortManager:dump	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   91: aload_0
    //   92: getfield 75	com/android/server/usb/UsbService:mAlsaManager	Lcom/android/server/usb/UsbAlsaManager;
    //   95: aload_2
    //   96: invokevirtual 247	com/android/server/usb/UsbAlsaManager:dump	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   99: aload_0
    //   100: getfield 41	com/android/server/usb/UsbService:mLock	Ljava/lang/Object;
    //   103: astore_1
    //   104: aload_1
    //   105: monitorenter
    //   106: iconst_0
    //   107: istore 4
    //   109: iload 4
    //   111: aload_0
    //   112: getfield 45	com/android/server/usb/UsbService:mSettingsByUser	Landroid/util/SparseArray;
    //   115: invokevirtual 250	android/util/SparseArray:size	()I
    //   118: if_icmpge +190 -> 308
    //   121: aload_0
    //   122: getfield 45	com/android/server/usb/UsbService:mSettingsByUser	Landroid/util/SparseArray;
    //   125: iload 4
    //   127: invokevirtual 254	android/util/SparseArray:keyAt	(I)I
    //   130: istore 5
    //   132: aload_0
    //   133: getfield 45	com/android/server/usb/UsbService:mSettingsByUser	Landroid/util/SparseArray;
    //   136: iload 4
    //   138: invokevirtual 257	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
    //   141: checkcast 142	com/android/server/usb/UsbSettingsManager
    //   144: astore_3
    //   145: aload_2
    //   146: new 259	java/lang/StringBuilder
    //   149: dup
    //   150: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   153: ldc_w 262
    //   156: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: iload 5
    //   161: invokevirtual 269	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   164: ldc_w 271
    //   167: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   173: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   176: aload_2
    //   177: invokevirtual 241	com/android/internal/util/IndentingPrintWriter:increaseIndent	()V
    //   180: aload_3
    //   181: aload_2
    //   182: invokevirtual 276	com/android/server/usb/UsbSettingsManager:dump	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   185: aload_2
    //   186: invokevirtual 279	com/android/internal/util/IndentingPrintWriter:decreaseIndent	()V
    //   189: iload 4
    //   191: iconst_1
    //   192: iadd
    //   193: istore 4
    //   195: goto -86 -> 109
    //   198: ldc_w 281
    //   201: aload_3
    //   202: iconst_0
    //   203: aaload
    //   204: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   207: ifne -171 -> 36
    //   210: aload_3
    //   211: arraylength
    //   212: iconst_4
    //   213: if_icmpne +245 -> 458
    //   216: ldc_w 283
    //   219: aload_3
    //   220: iconst_0
    //   221: aaload
    //   222: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   225: ifeq +233 -> 458
    //   228: aload_3
    //   229: iconst_1
    //   230: aaload
    //   231: astore_1
    //   232: aload_3
    //   233: iconst_2
    //   234: aaload
    //   235: astore 12
    //   237: aload 12
    //   239: ldc_w 285
    //   242: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   245: ifeq +81 -> 326
    //   248: iconst_1
    //   249: istore 4
    //   251: aload_3
    //   252: iconst_3
    //   253: aaload
    //   254: astore 12
    //   256: aload 12
    //   258: ldc_w 287
    //   261: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   264: ifeq +128 -> 392
    //   267: iconst_1
    //   268: istore 5
    //   270: aload_0
    //   271: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   274: ifnull +28 -> 302
    //   277: aload_0
    //   278: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   281: aload_1
    //   282: iload 4
    //   284: iload 5
    //   286: aload_2
    //   287: invokevirtual 291	com/android/server/usb/UsbPortManager:setPortRoles	(Ljava/lang/String;IILcom/android/internal/util/IndentingPrintWriter;)V
    //   290: aload_2
    //   291: invokevirtual 293	com/android/internal/util/IndentingPrintWriter:println	()V
    //   294: aload_0
    //   295: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   298: aload_2
    //   299: invokevirtual 246	com/android/server/usb/UsbPortManager:dump	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   302: lload 7
    //   304: invokestatic 297	android/os/Binder:restoreCallingIdentity	(J)V
    //   307: return
    //   308: aload_1
    //   309: monitorexit
    //   310: goto -8 -> 302
    //   313: astore_1
    //   314: lload 7
    //   316: invokestatic 297	android/os/Binder:restoreCallingIdentity	(J)V
    //   319: aload_1
    //   320: athrow
    //   321: astore_2
    //   322: aload_1
    //   323: monitorexit
    //   324: aload_2
    //   325: athrow
    //   326: aload 12
    //   328: ldc_w 299
    //   331: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   334: ifeq +9 -> 343
    //   337: iconst_2
    //   338: istore 4
    //   340: goto -89 -> 251
    //   343: aload 12
    //   345: ldc_w 301
    //   348: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   351: ifeq +9 -> 360
    //   354: iconst_0
    //   355: istore 4
    //   357: goto -106 -> 251
    //   360: aload_2
    //   361: new 259	java/lang/StringBuilder
    //   364: dup
    //   365: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   368: ldc_w 303
    //   371: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   374: aload_3
    //   375: iconst_2
    //   376: aaload
    //   377: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   380: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   383: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   386: lload 7
    //   388: invokestatic 297	android/os/Binder:restoreCallingIdentity	(J)V
    //   391: return
    //   392: aload 12
    //   394: ldc_w 305
    //   397: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   400: ifeq +9 -> 409
    //   403: iconst_2
    //   404: istore 5
    //   406: goto -136 -> 270
    //   409: aload 12
    //   411: ldc_w 307
    //   414: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   417: ifeq +9 -> 426
    //   420: iconst_0
    //   421: istore 5
    //   423: goto -153 -> 270
    //   426: aload_2
    //   427: new 259	java/lang/StringBuilder
    //   430: dup
    //   431: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   434: ldc_w 309
    //   437: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   440: aload_3
    //   441: iconst_3
    //   442: aaload
    //   443: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   446: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   449: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   452: lload 7
    //   454: invokestatic 297	android/os/Binder:restoreCallingIdentity	(J)V
    //   457: return
    //   458: aload_3
    //   459: arraylength
    //   460: iconst_3
    //   461: if_icmpne +153 -> 614
    //   464: ldc_w 311
    //   467: aload_3
    //   468: iconst_0
    //   469: aaload
    //   470: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   473: ifeq +141 -> 614
    //   476: aload_3
    //   477: iconst_1
    //   478: aaload
    //   479: astore_1
    //   480: aload_3
    //   481: iconst_2
    //   482: aaload
    //   483: astore 12
    //   485: aload 12
    //   487: ldc_w 313
    //   490: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   493: ifeq +39 -> 532
    //   496: iconst_2
    //   497: istore 4
    //   499: aload_0
    //   500: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   503: ifnull -201 -> 302
    //   506: aload_0
    //   507: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   510: aload_1
    //   511: iload 4
    //   513: aload_2
    //   514: invokevirtual 317	com/android/server/usb/UsbPortManager:addSimulatedPort	(Ljava/lang/String;ILcom/android/internal/util/IndentingPrintWriter;)V
    //   517: aload_2
    //   518: invokevirtual 293	com/android/internal/util/IndentingPrintWriter:println	()V
    //   521: aload_0
    //   522: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   525: aload_2
    //   526: invokevirtual 246	com/android/server/usb/UsbPortManager:dump	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   529: goto -227 -> 302
    //   532: aload 12
    //   534: ldc_w 319
    //   537: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   540: ifeq +9 -> 549
    //   543: iconst_1
    //   544: istore 4
    //   546: goto -47 -> 499
    //   549: aload 12
    //   551: ldc_w 321
    //   554: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   557: ifeq +9 -> 566
    //   560: iconst_3
    //   561: istore 4
    //   563: goto -64 -> 499
    //   566: aload 12
    //   568: ldc -100
    //   570: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   573: ifeq +9 -> 582
    //   576: iconst_0
    //   577: istore 4
    //   579: goto -80 -> 499
    //   582: aload_2
    //   583: new 259	java/lang/StringBuilder
    //   586: dup
    //   587: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   590: ldc_w 323
    //   593: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   596: aload_3
    //   597: iconst_2
    //   598: aaload
    //   599: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   602: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   605: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   608: lload 7
    //   610: invokestatic 297	android/os/Binder:restoreCallingIdentity	(J)V
    //   613: return
    //   614: aload_3
    //   615: arraylength
    //   616: iconst_5
    //   617: if_icmpne +330 -> 947
    //   620: ldc_w 325
    //   623: aload_3
    //   624: iconst_0
    //   625: aaload
    //   626: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   629: ifeq +318 -> 947
    //   632: aload_3
    //   633: iconst_1
    //   634: aaload
    //   635: astore 12
    //   637: aload_3
    //   638: iconst_2
    //   639: aaload
    //   640: ldc_w 327
    //   643: invokevirtual 330	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   646: istore 9
    //   648: iload 9
    //   650: ifeq +720 -> 1370
    //   653: aload_3
    //   654: iconst_2
    //   655: aaload
    //   656: invokestatic 332	com/android/server/usb/UsbService:removeLastChar	(Ljava/lang/String;)Ljava/lang/String;
    //   659: astore_1
    //   660: aload_1
    //   661: ldc_w 313
    //   664: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   667: ifeq +122 -> 789
    //   670: iconst_2
    //   671: istore 4
    //   673: aload_3
    //   674: iconst_3
    //   675: aaload
    //   676: ldc_w 327
    //   679: invokevirtual 330	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   682: istore 10
    //   684: iload 10
    //   686: ifeq +151 -> 837
    //   689: aload_3
    //   690: iconst_3
    //   691: aaload
    //   692: invokestatic 332	com/android/server/usb/UsbService:removeLastChar	(Ljava/lang/String;)Ljava/lang/String;
    //   695: astore_1
    //   696: aload_1
    //   697: ldc_w 285
    //   700: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   703: ifeq +141 -> 844
    //   706: iconst_1
    //   707: istore 5
    //   709: aload_3
    //   710: iconst_4
    //   711: aaload
    //   712: ldc_w 327
    //   715: invokevirtual 330	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   718: istore 11
    //   720: iload 11
    //   722: ifeq +170 -> 892
    //   725: aload_3
    //   726: iconst_4
    //   727: aaload
    //   728: invokestatic 332	com/android/server/usb/UsbService:removeLastChar	(Ljava/lang/String;)Ljava/lang/String;
    //   731: astore_1
    //   732: aload_1
    //   733: ldc_w 287
    //   736: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   739: ifeq +160 -> 899
    //   742: iconst_1
    //   743: istore 6
    //   745: aload_0
    //   746: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   749: ifnull -447 -> 302
    //   752: aload_0
    //   753: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   756: aload 12
    //   758: iload 4
    //   760: iload 9
    //   762: iload 5
    //   764: iload 10
    //   766: iload 6
    //   768: iload 11
    //   770: aload_2
    //   771: invokevirtual 336	com/android/server/usb/UsbPortManager:connectSimulatedPort	(Ljava/lang/String;IZIZIZLcom/android/internal/util/IndentingPrintWriter;)V
    //   774: aload_2
    //   775: invokevirtual 293	com/android/internal/util/IndentingPrintWriter:println	()V
    //   778: aload_0
    //   779: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   782: aload_2
    //   783: invokevirtual 246	com/android/server/usb/UsbPortManager:dump	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   786: goto -484 -> 302
    //   789: aload_1
    //   790: ldc_w 319
    //   793: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   796: ifeq +9 -> 805
    //   799: iconst_1
    //   800: istore 4
    //   802: goto -129 -> 673
    //   805: aload_2
    //   806: new 259	java/lang/StringBuilder
    //   809: dup
    //   810: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   813: ldc_w 323
    //   816: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   819: aload_3
    //   820: iconst_2
    //   821: aaload
    //   822: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   825: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   828: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   831: lload 7
    //   833: invokestatic 297	android/os/Binder:restoreCallingIdentity	(J)V
    //   836: return
    //   837: aload_3
    //   838: iconst_3
    //   839: aaload
    //   840: astore_1
    //   841: goto -145 -> 696
    //   844: aload_1
    //   845: ldc_w 299
    //   848: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   851: ifeq +9 -> 860
    //   854: iconst_2
    //   855: istore 5
    //   857: goto -148 -> 709
    //   860: aload_2
    //   861: new 259	java/lang/StringBuilder
    //   864: dup
    //   865: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   868: ldc_w 303
    //   871: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   874: aload_3
    //   875: iconst_3
    //   876: aaload
    //   877: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   880: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   883: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   886: lload 7
    //   888: invokestatic 297	android/os/Binder:restoreCallingIdentity	(J)V
    //   891: return
    //   892: aload_3
    //   893: iconst_4
    //   894: aaload
    //   895: astore_1
    //   896: goto -164 -> 732
    //   899: aload_1
    //   900: ldc_w 305
    //   903: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   906: ifeq +9 -> 915
    //   909: iconst_2
    //   910: istore 6
    //   912: goto -167 -> 745
    //   915: aload_2
    //   916: new 259	java/lang/StringBuilder
    //   919: dup
    //   920: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   923: ldc_w 309
    //   926: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   929: aload_3
    //   930: iconst_4
    //   931: aaload
    //   932: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   935: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   938: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   941: lload 7
    //   943: invokestatic 297	android/os/Binder:restoreCallingIdentity	(J)V
    //   946: return
    //   947: aload_3
    //   948: arraylength
    //   949: iconst_2
    //   950: if_icmpne +50 -> 1000
    //   953: ldc_w 338
    //   956: aload_3
    //   957: iconst_0
    //   958: aaload
    //   959: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   962: ifeq +38 -> 1000
    //   965: aload_3
    //   966: iconst_1
    //   967: aaload
    //   968: astore_1
    //   969: aload_0
    //   970: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   973: ifnull -671 -> 302
    //   976: aload_0
    //   977: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   980: aload_1
    //   981: aload_2
    //   982: invokevirtual 342	com/android/server/usb/UsbPortManager:disconnectSimulatedPort	(Ljava/lang/String;Lcom/android/internal/util/IndentingPrintWriter;)V
    //   985: aload_2
    //   986: invokevirtual 293	com/android/internal/util/IndentingPrintWriter:println	()V
    //   989: aload_0
    //   990: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   993: aload_2
    //   994: invokevirtual 246	com/android/server/usb/UsbPortManager:dump	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   997: goto -695 -> 302
    //   1000: aload_3
    //   1001: arraylength
    //   1002: iconst_2
    //   1003: if_icmpne +50 -> 1053
    //   1006: ldc_w 344
    //   1009: aload_3
    //   1010: iconst_0
    //   1011: aaload
    //   1012: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1015: ifeq +38 -> 1053
    //   1018: aload_3
    //   1019: iconst_1
    //   1020: aaload
    //   1021: astore_1
    //   1022: aload_0
    //   1023: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   1026: ifnull -724 -> 302
    //   1029: aload_0
    //   1030: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   1033: aload_1
    //   1034: aload_2
    //   1035: invokevirtual 347	com/android/server/usb/UsbPortManager:removeSimulatedPort	(Ljava/lang/String;Lcom/android/internal/util/IndentingPrintWriter;)V
    //   1038: aload_2
    //   1039: invokevirtual 293	com/android/internal/util/IndentingPrintWriter:println	()V
    //   1042: aload_0
    //   1043: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   1046: aload_2
    //   1047: invokevirtual 246	com/android/server/usb/UsbPortManager:dump	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   1050: goto -748 -> 302
    //   1053: aload_3
    //   1054: arraylength
    //   1055: iconst_1
    //   1056: if_icmpne +45 -> 1101
    //   1059: ldc_w 349
    //   1062: aload_3
    //   1063: iconst_0
    //   1064: aaload
    //   1065: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1068: ifeq +33 -> 1101
    //   1071: aload_0
    //   1072: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   1075: ifnull -773 -> 302
    //   1078: aload_0
    //   1079: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   1082: aload_2
    //   1083: invokevirtual 352	com/android/server/usb/UsbPortManager:resetSimulation	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   1086: aload_2
    //   1087: invokevirtual 293	com/android/internal/util/IndentingPrintWriter:println	()V
    //   1090: aload_0
    //   1091: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   1094: aload_2
    //   1095: invokevirtual 246	com/android/server/usb/UsbPortManager:dump	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   1098: goto -796 -> 302
    //   1101: aload_3
    //   1102: arraylength
    //   1103: iconst_1
    //   1104: if_icmpne +33 -> 1137
    //   1107: ldc_w 354
    //   1110: aload_3
    //   1111: iconst_0
    //   1112: aaload
    //   1113: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1116: ifeq +21 -> 1137
    //   1119: aload_0
    //   1120: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   1123: ifnull -821 -> 302
    //   1126: aload_0
    //   1127: getfield 115	com/android/server/usb/UsbService:mPortManager	Lcom/android/server/usb/UsbPortManager;
    //   1130: aload_2
    //   1131: invokevirtual 246	com/android/server/usb/UsbPortManager:dump	(Lcom/android/internal/util/IndentingPrintWriter;)V
    //   1134: goto -832 -> 302
    //   1137: aload_2
    //   1138: ldc_w 356
    //   1141: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1144: aload_2
    //   1145: ldc_w 358
    //   1148: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1151: aload_2
    //   1152: ldc_w 360
    //   1155: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1158: aload_2
    //   1159: ldc_w 362
    //   1162: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1165: aload_2
    //   1166: ldc_w 364
    //   1169: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1172: aload_2
    //   1173: ldc_w 366
    //   1176: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1179: aload_2
    //   1180: ldc_w 368
    //   1183: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1186: aload_2
    //   1187: ldc_w 370
    //   1190: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1193: aload_2
    //   1194: ldc_w 372
    //   1197: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1200: aload_2
    //   1201: invokevirtual 293	com/android/internal/util/IndentingPrintWriter:println	()V
    //   1204: aload_2
    //   1205: ldc_w 374
    //   1208: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1211: aload_2
    //   1212: ldc_w 376
    //   1215: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1218: aload_2
    //   1219: invokevirtual 293	com/android/internal/util/IndentingPrintWriter:println	()V
    //   1222: aload_2
    //   1223: ldc_w 378
    //   1226: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1229: aload_2
    //   1230: ldc_w 380
    //   1233: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1236: aload_2
    //   1237: ldc_w 382
    //   1240: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1243: aload_2
    //   1244: ldc_w 384
    //   1247: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1250: aload_2
    //   1251: ldc_w 386
    //   1254: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1257: aload_2
    //   1258: ldc_w 388
    //   1261: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1264: aload_2
    //   1265: ldc_w 390
    //   1268: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1271: aload_2
    //   1272: invokevirtual 293	com/android/internal/util/IndentingPrintWriter:println	()V
    //   1275: aload_2
    //   1276: ldc_w 392
    //   1279: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1282: aload_2
    //   1283: ldc_w 380
    //   1286: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1289: aload_2
    //   1290: ldc_w 394
    //   1293: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1296: aload_2
    //   1297: ldc_w 390
    //   1300: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1303: aload_2
    //   1304: invokevirtual 293	com/android/internal/util/IndentingPrintWriter:println	()V
    //   1307: aload_2
    //   1308: ldc_w 396
    //   1311: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1314: aload_2
    //   1315: ldc_w 380
    //   1318: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1321: aload_2
    //   1322: ldc_w 398
    //   1325: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1328: aload_2
    //   1329: ldc_w 390
    //   1332: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1335: aload_2
    //   1336: invokevirtual 293	com/android/internal/util/IndentingPrintWriter:println	()V
    //   1339: aload_2
    //   1340: ldc_w 400
    //   1343: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1346: aload_2
    //   1347: ldc_w 402
    //   1350: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1353: aload_2
    //   1354: ldc_w 404
    //   1357: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1360: aload_2
    //   1361: ldc_w 390
    //   1364: invokevirtual 238	com/android/internal/util/IndentingPrintWriter:println	(Ljava/lang/String;)V
    //   1367: goto -1065 -> 302
    //   1370: aload_3
    //   1371: iconst_2
    //   1372: aaload
    //   1373: astore_1
    //   1374: goto -714 -> 660
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1377	0	this	UsbService
    //   0	1377	1	paramFileDescriptor	java.io.FileDescriptor
    //   0	1377	2	paramPrintWriter	java.io.PrintWriter
    //   0	1377	3	paramArrayOfString	String[]
    //   107	694	4	i	int
    //   130	726	5	j	int
    //   743	168	6	k	int
    //   25	917	7	l	long
    //   646	115	9	bool1	boolean
    //   682	83	10	bool2	boolean
    //   718	51	11	bool3	boolean
    //   235	522	12	str	String
    // Exception table:
    //   from	to	target	type
    //   31	36	313	finally
    //   36	61	313	finally
    //   61	76	313	finally
    //   76	91	313	finally
    //   91	106	313	finally
    //   198	228	313	finally
    //   237	248	313	finally
    //   256	267	313	finally
    //   270	302	313	finally
    //   308	310	313	finally
    //   322	326	313	finally
    //   326	337	313	finally
    //   343	354	313	finally
    //   360	386	313	finally
    //   392	403	313	finally
    //   409	420	313	finally
    //   426	452	313	finally
    //   458	476	313	finally
    //   485	496	313	finally
    //   499	529	313	finally
    //   532	543	313	finally
    //   549	560	313	finally
    //   566	576	313	finally
    //   582	608	313	finally
    //   614	632	313	finally
    //   637	648	313	finally
    //   653	660	313	finally
    //   660	670	313	finally
    //   673	684	313	finally
    //   689	696	313	finally
    //   696	706	313	finally
    //   709	720	313	finally
    //   725	732	313	finally
    //   732	742	313	finally
    //   745	786	313	finally
    //   789	799	313	finally
    //   805	831	313	finally
    //   844	854	313	finally
    //   860	886	313	finally
    //   899	909	313	finally
    //   915	941	313	finally
    //   947	965	313	finally
    //   969	997	313	finally
    //   1000	1018	313	finally
    //   1022	1050	313	finally
    //   1053	1098	313	finally
    //   1101	1134	313	finally
    //   1137	1367	313	finally
    //   109	189	321	finally
  }
  
  public UsbAccessory getCurrentAccessory()
  {
    if (this.mDeviceManager != null) {
      return this.mDeviceManager.getCurrentAccessory();
    }
    return null;
  }
  
  public void getDeviceList(Bundle paramBundle)
  {
    if (this.mHostManager != null) {
      this.mHostManager.getDeviceList(paramBundle);
    }
  }
  
  public UsbPortStatus getPortStatus(String paramString)
  {
    UsbPortStatus localUsbPortStatus = null;
    Preconditions.checkNotNull(paramString, "portId must not be null");
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    long l = Binder.clearCallingIdentity();
    try
    {
      if (this.mPortManager != null) {
        localUsbPortStatus = this.mPortManager.getPortStatus(paramString);
      }
      return localUsbPortStatus;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public UsbPort[] getPorts()
  {
    UsbPort[] arrayOfUsbPort = null;
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    long l = Binder.clearCallingIdentity();
    try
    {
      if (this.mPortManager != null) {
        arrayOfUsbPort = this.mPortManager.getPorts();
      }
      return arrayOfUsbPort;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void grantAccessoryPermission(UsbAccessory paramUsbAccessory, int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    getSettingsForUser(UserHandle.getUserId(paramInt)).grantAccessoryPermission(paramUsbAccessory, paramInt);
  }
  
  public void grantDevicePermission(UsbDevice paramUsbDevice, int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    getSettingsForUser(UserHandle.getUserId(paramInt)).grantDevicePermission(paramUsbDevice, paramInt);
  }
  
  public boolean hasAccessoryPermission(UsbAccessory paramUsbAccessory)
  {
    return getSettingsForUser(UserHandle.getCallingUserId()).hasPermission(paramUsbAccessory);
  }
  
  public boolean hasDefaults(String paramString, int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    return getSettingsForUser(paramInt).hasDefaults(paramString);
  }
  
  public boolean hasDevicePermission(UsbDevice paramUsbDevice)
  {
    return getSettingsForUser(UserHandle.getCallingUserId()).hasPermission(paramUsbDevice);
  }
  
  public boolean isFunctionEnabled(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    if (this.mDeviceManager != null) {
      return this.mDeviceManager.isFunctionEnabled(paramString);
    }
    return false;
  }
  
  public boolean isUsbDataUnlocked()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    return this.mDeviceManager.isUsbDataUnlocked();
  }
  
  public ParcelFileDescriptor openAccessory(UsbAccessory paramUsbAccessory)
  {
    if (this.mDeviceManager != null) {
      return this.mDeviceManager.openAccessory(paramUsbAccessory);
    }
    return null;
  }
  
  public ParcelFileDescriptor openDevice(String paramString)
  {
    if (this.mHostManager != null) {
      return this.mHostManager.openDevice(paramString);
    }
    return null;
  }
  
  public void requestAccessoryPermission(UsbAccessory paramUsbAccessory, String paramString, PendingIntent paramPendingIntent)
  {
    getSettingsForUser(UserHandle.getCallingUserId()).requestPermission(paramUsbAccessory, paramString, paramPendingIntent);
  }
  
  public void requestDevicePermission(UsbDevice paramUsbDevice, String paramString, PendingIntent paramPendingIntent)
  {
    getSettingsForUser(UserHandle.getCallingUserId()).requestPermission(paramUsbDevice, paramString, paramPendingIntent);
  }
  
  public void setAccessoryPackage(UsbAccessory paramUsbAccessory, String paramString, int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    getSettingsForUser(paramInt).setAccessoryPackage(paramUsbAccessory, paramString);
  }
  
  public void setCurrentFunction(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    String str = paramString;
    if (!isSupportedCurrentFunction(paramString))
    {
      Slog.w("UsbService", "Caller of setCurrentFunction() requested unsupported USB function: " + paramString);
      str = "none";
    }
    if (this.mDeviceManager != null)
    {
      this.mDeviceManager.setCurrentFunctions(str);
      return;
    }
    throw new IllegalStateException("USB device mode not supported");
  }
  
  public void setDevicePackage(UsbDevice paramUsbDevice, String paramString, int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    getSettingsForUser(paramInt).setDevicePackage(paramUsbDevice, paramString);
  }
  
  public void setPortRoles(String paramString, int paramInt1, int paramInt2)
  {
    Preconditions.checkNotNull(paramString, "portId must not be null");
    UsbPort.checkRoles(paramInt1, paramInt2);
    if ((paramInt1 == 2) && (paramInt2 == 1))
    {
      Slog.w("UsbService", "while power is sink when data rols is host\n");
      return;
    }
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    long l = Binder.clearCallingIdentity();
    try
    {
      if (this.mPortManager != null) {
        this.mPortManager.setPortRoles(paramString, paramInt1, paramInt2, null);
      }
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void setUsbDataUnlocked(boolean paramBoolean)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
    this.mDeviceManager.setUsbDataUnlocked(paramBoolean);
  }
  
  public void systemReady()
  {
    this.mAlsaManager.systemReady();
    if (this.mDeviceManager != null) {
      this.mDeviceManager.systemReady();
    }
    if (this.mHostManager != null) {
      this.mHostManager.systemReady();
    }
    if (this.mPortManager != null) {
      this.mPortManager.systemReady();
    }
  }
  
  public static class Lifecycle
    extends SystemService
  {
    private UsbService mUsbService;
    
    public Lifecycle(Context paramContext)
    {
      super();
    }
    
    public void onBootPhase(int paramInt)
    {
      if (paramInt == 550) {
        this.mUsbService.systemReady();
      }
      while (paramInt != 1000) {
        return;
      }
      this.mUsbService.bootCompleted();
    }
    
    public void onStart()
    {
      this.mUsbService = new UsbService(getContext());
      publishBinderService("usb", this.mUsbService);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usb/UsbService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */