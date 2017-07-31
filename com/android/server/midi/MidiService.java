package com.android.server.midi;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.media.midi.IBluetoothMidiService;
import android.media.midi.IBluetoothMidiService.Stub;
import android.media.midi.IMidiDeviceListener;
import android.media.midi.IMidiDeviceOpenCallback;
import android.media.midi.IMidiDeviceServer;
import android.media.midi.IMidiDeviceServer.Stub;
import android.media.midi.IMidiManager.Stub;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiDeviceStatus;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.SystemService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MidiService
  extends IMidiManager.Stub
{
  private static final MidiDeviceInfo[] EMPTY_DEVICE_INFO_ARRAY = new MidiDeviceInfo[0];
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  private static final String TAG = "MidiService";
  private final HashMap<BluetoothDevice, Device> mBluetoothDevices = new HashMap();
  private int mBluetoothServiceUid;
  private final HashMap<IBinder, Client> mClients = new HashMap();
  private final Context mContext;
  private final HashMap<MidiDeviceInfo, Device> mDevicesByInfo = new HashMap();
  private final HashMap<IBinder, Device> mDevicesByServer = new HashMap();
  private int mNextDeviceId = 1;
  private final PackageManager mPackageManager;
  private final PackageMonitor mPackageMonitor = new PackageMonitor()
  {
    public void onPackageAdded(String paramAnonymousString, int paramAnonymousInt)
    {
      MidiService.-wrap0(MidiService.this, paramAnonymousString);
    }
    
    public void onPackageModified(String paramAnonymousString)
    {
      MidiService.-wrap3(MidiService.this, paramAnonymousString);
      MidiService.-wrap0(MidiService.this, paramAnonymousString);
    }
    
    public void onPackageRemoved(String paramAnonymousString, int paramAnonymousInt)
    {
      MidiService.-wrap3(MidiService.this, paramAnonymousString);
    }
  };
  
  public MidiService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mPackageManager = paramContext.getPackageManager();
    this.mBluetoothServiceUid = -1;
  }
  
  private MidiDeviceInfo addDeviceLocked(int paramInt1, int paramInt2, int paramInt3, String[] arg4, String[] paramArrayOfString2, Bundle paramBundle, IMidiDeviceServer paramIMidiDeviceServer, ServiceInfo paramServiceInfo, boolean paramBoolean, int paramInt4)
  {
    int i = this.mNextDeviceId;
    this.mNextDeviceId = (i + 1);
    MidiDeviceInfo localMidiDeviceInfo = new MidiDeviceInfo(paramInt1, i, paramInt2, paramInt3, ???, paramArrayOfString2, paramBundle, paramBoolean);
    if (paramIMidiDeviceServer != null) {}
    try
    {
      paramIMidiDeviceServer.setDeviceInfo(localMidiDeviceInfo);
      ??? = null;
      paramArrayOfString2 = null;
      if (paramInt1 == 3)
      {
        paramBundle = (BluetoothDevice)paramBundle.getParcelable("bluetooth_device");
        Device localDevice = (Device)this.mBluetoothDevices.get(paramBundle);
        ??? = localDevice;
        paramArrayOfString2 = paramBundle;
        if (localDevice != null)
        {
          localDevice.setDeviceInfo(localMidiDeviceInfo);
          paramArrayOfString2 = paramBundle;
          ??? = localDevice;
        }
      }
      paramBundle = ???;
      if (??? == null) {
        paramBundle = new Device(paramIMidiDeviceServer, localMidiDeviceInfo, paramServiceInfo, paramInt4);
      }
      this.mDevicesByInfo.put(localMidiDeviceInfo, paramBundle);
      if (paramArrayOfString2 != null) {
        this.mBluetoothDevices.put(paramArrayOfString2, paramBundle);
      }
      synchronized (this.mClients)
      {
        paramArrayOfString2 = this.mClients.values().iterator();
        if (paramArrayOfString2.hasNext()) {
          ((Client)paramArrayOfString2.next()).deviceAdded(paramBundle);
        }
      }
    }
    catch (RemoteException ???)
    {
      Log.e("MidiService", "RemoteException in setDeviceInfo()");
      return null;
    }
    return localMidiDeviceInfo;
  }
  
  /* Error */
  private void addPackageDeviceServer(ServiceInfo paramServiceInfo)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 12
    //   3: aconst_null
    //   4: astore 11
    //   6: aload_1
    //   7: aload_0
    //   8: getfield 120	com/android/server/midi/MidiService:mPackageManager	Landroid/content/pm/PackageManager;
    //   11: ldc -62
    //   13: invokevirtual 200	android/content/pm/ServiceInfo:loadXmlMetaData	(Landroid/content/pm/PackageManager;Ljava/lang/String;)Landroid/content/res/XmlResourceParser;
    //   16: astore 10
    //   18: aload 10
    //   20: ifnonnull +16 -> 36
    //   23: aload 10
    //   25: ifnull +10 -> 35
    //   28: aload 10
    //   30: invokeinterface 205 1 0
    //   35: return
    //   36: aload 10
    //   38: astore 11
    //   40: aload 10
    //   42: astore 12
    //   44: ldc -49
    //   46: aload_1
    //   47: getfield 210	android/content/pm/ServiceInfo:permission	Ljava/lang/String;
    //   50: invokevirtual 214	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   53: ifne +62 -> 115
    //   56: aload 10
    //   58: astore 11
    //   60: aload 10
    //   62: astore 12
    //   64: ldc 28
    //   66: new 216	java/lang/StringBuilder
    //   69: dup
    //   70: invokespecial 217	java/lang/StringBuilder:<init>	()V
    //   73: ldc -37
    //   75: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   78: aload_1
    //   79: getfield 226	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
    //   82: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   85: ldc -28
    //   87: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   90: ldc -49
    //   92: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   98: invokestatic 235	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   101: pop
    //   102: aload 10
    //   104: ifnull +10 -> 114
    //   107: aload 10
    //   109: invokeinterface 205 1 0
    //   114: return
    //   115: aconst_null
    //   116: astore 13
    //   118: iconst_0
    //   119: istore_3
    //   120: iconst_0
    //   121: istore_2
    //   122: iconst_0
    //   123: istore 9
    //   125: aload 10
    //   127: astore 11
    //   129: aload 10
    //   131: astore 12
    //   133: new 237	java/util/ArrayList
    //   136: dup
    //   137: invokespecial 238	java/util/ArrayList:<init>	()V
    //   140: astore 16
    //   142: aload 10
    //   144: astore 11
    //   146: aload 10
    //   148: astore 12
    //   150: new 237	java/util/ArrayList
    //   153: dup
    //   154: invokespecial 238	java/util/ArrayList:<init>	()V
    //   157: astore 17
    //   159: aload 10
    //   161: astore 11
    //   163: aload 10
    //   165: astore 12
    //   167: aload 10
    //   169: invokeinterface 241 1 0
    //   174: istore 4
    //   176: iload 4
    //   178: iconst_1
    //   179: if_icmpne +16 -> 195
    //   182: aload 10
    //   184: ifnull +10 -> 194
    //   187: aload 10
    //   189: invokeinterface 205 1 0
    //   194: return
    //   195: iload 4
    //   197: iconst_2
    //   198: if_icmpne +684 -> 882
    //   201: aload 10
    //   203: astore 11
    //   205: aload 10
    //   207: astore 12
    //   209: aload 10
    //   211: invokeinterface 244 1 0
    //   216: astore 14
    //   218: aload 10
    //   220: astore 11
    //   222: aload 10
    //   224: astore 12
    //   226: ldc -10
    //   228: aload 14
    //   230: invokevirtual 214	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   233: ifeq +293 -> 526
    //   236: aload 13
    //   238: ifnull +91 -> 329
    //   241: aload 10
    //   243: astore 11
    //   245: aload 10
    //   247: astore 12
    //   249: ldc 28
    //   251: new 216	java/lang/StringBuilder
    //   254: dup
    //   255: invokespecial 217	java/lang/StringBuilder:<init>	()V
    //   258: ldc -8
    //   260: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   263: aload_1
    //   264: getfield 226	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
    //   267: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   270: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   273: invokestatic 235	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   276: pop
    //   277: goto -118 -> 159
    //   280: astore 10
    //   282: aload 11
    //   284: astore 12
    //   286: ldc 28
    //   288: new 216	java/lang/StringBuilder
    //   291: dup
    //   292: invokespecial 217	java/lang/StringBuilder:<init>	()V
    //   295: ldc -6
    //   297: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   300: aload_1
    //   301: invokevirtual 251	android/content/pm/ServiceInfo:toString	()Ljava/lang/String;
    //   304: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   307: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   310: aload 10
    //   312: invokestatic 254	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   315: pop
    //   316: aload 11
    //   318: ifnull -124 -> 194
    //   321: aload 11
    //   323: invokeinterface 205 1 0
    //   328: return
    //   329: aload 10
    //   331: astore 11
    //   333: aload 10
    //   335: astore 12
    //   337: new 137	android/os/Bundle
    //   340: dup
    //   341: invokespecial 255	android/os/Bundle:<init>	()V
    //   344: astore 14
    //   346: aload 10
    //   348: astore 11
    //   350: aload 10
    //   352: astore 12
    //   354: aload 14
    //   356: ldc_w 257
    //   359: aload_1
    //   360: invokevirtual 261	android/os/Bundle:putParcelable	(Ljava/lang/String;Landroid/os/Parcelable;)V
    //   363: iconst_0
    //   364: istore 5
    //   366: iconst_0
    //   367: istore 6
    //   369: iconst_0
    //   370: istore 8
    //   372: aload 10
    //   374: astore 11
    //   376: aload 10
    //   378: astore 12
    //   380: aload 10
    //   382: invokeinterface 264 1 0
    //   387: istore 7
    //   389: iconst_0
    //   390: istore 4
    //   392: iload 5
    //   394: istore_3
    //   395: iload 6
    //   397: istore_2
    //   398: aload 14
    //   400: astore 13
    //   402: iload 8
    //   404: istore 9
    //   406: iload 4
    //   408: iload 7
    //   410: if_icmpge -251 -> 159
    //   413: aload 10
    //   415: astore 11
    //   417: aload 10
    //   419: astore 12
    //   421: aload 10
    //   423: iload 4
    //   425: invokeinterface 268 2 0
    //   430: astore 13
    //   432: aload 10
    //   434: astore 11
    //   436: aload 10
    //   438: astore 12
    //   440: aload 10
    //   442: iload 4
    //   444: invokeinterface 271 2 0
    //   449: astore 15
    //   451: aload 10
    //   453: astore 11
    //   455: aload 10
    //   457: astore 12
    //   459: ldc_w 273
    //   462: aload 13
    //   464: invokevirtual 214	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   467: ifeq +24 -> 491
    //   470: aload 10
    //   472: astore 11
    //   474: aload 10
    //   476: astore 12
    //   478: ldc_w 275
    //   481: aload 15
    //   483: invokevirtual 214	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   486: istore 8
    //   488: goto +673 -> 1161
    //   491: aload 10
    //   493: astore 11
    //   495: aload 10
    //   497: astore 12
    //   499: aload 14
    //   501: aload 13
    //   503: aload 15
    //   505: invokevirtual 279	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   508: goto +653 -> 1161
    //   511: astore_1
    //   512: aload 12
    //   514: ifnull +10 -> 524
    //   517: aload 12
    //   519: invokeinterface 205 1 0
    //   524: aload_1
    //   525: athrow
    //   526: aload 10
    //   528: astore 11
    //   530: aload 10
    //   532: astore 12
    //   534: ldc_w 281
    //   537: aload 14
    //   539: invokevirtual 214	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   542: ifeq +162 -> 704
    //   545: aload 13
    //   547: ifnonnull +43 -> 590
    //   550: aload 10
    //   552: astore 11
    //   554: aload 10
    //   556: astore 12
    //   558: ldc 28
    //   560: new 216	java/lang/StringBuilder
    //   563: dup
    //   564: invokespecial 217	java/lang/StringBuilder:<init>	()V
    //   567: ldc_w 283
    //   570: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   573: aload_1
    //   574: getfield 226	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
    //   577: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   580: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   583: invokestatic 235	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   586: pop
    //   587: goto -428 -> 159
    //   590: iload_3
    //   591: iconst_1
    //   592: iadd
    //   593: istore 4
    //   595: aconst_null
    //   596: astore 15
    //   598: aload 10
    //   600: astore 11
    //   602: aload 10
    //   604: astore 12
    //   606: aload 10
    //   608: invokeinterface 264 1 0
    //   613: istore 5
    //   615: iconst_0
    //   616: istore_3
    //   617: aload 15
    //   619: astore 14
    //   621: iload_3
    //   622: iload 5
    //   624: if_icmpge +58 -> 682
    //   627: aload 10
    //   629: astore 11
    //   631: aload 10
    //   633: astore 12
    //   635: aload 10
    //   637: iload_3
    //   638: invokeinterface 268 2 0
    //   643: astore 18
    //   645: aload 10
    //   647: astore 11
    //   649: aload 10
    //   651: astore 12
    //   653: aload 10
    //   655: iload_3
    //   656: invokeinterface 271 2 0
    //   661: astore 14
    //   663: aload 10
    //   665: astore 11
    //   667: aload 10
    //   669: astore 12
    //   671: ldc_w 285
    //   674: aload 18
    //   676: invokevirtual 214	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   679: ifeq +491 -> 1170
    //   682: aload 10
    //   684: astore 11
    //   686: aload 10
    //   688: astore 12
    //   690: aload 16
    //   692: aload 14
    //   694: invokevirtual 288	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   697: pop
    //   698: iload 4
    //   700: istore_3
    //   701: goto -542 -> 159
    //   704: aload 10
    //   706: astore 11
    //   708: aload 10
    //   710: astore 12
    //   712: ldc_w 290
    //   715: aload 14
    //   717: invokevirtual 214	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   720: ifeq -561 -> 159
    //   723: aload 13
    //   725: ifnonnull +43 -> 768
    //   728: aload 10
    //   730: astore 11
    //   732: aload 10
    //   734: astore 12
    //   736: ldc 28
    //   738: new 216	java/lang/StringBuilder
    //   741: dup
    //   742: invokespecial 217	java/lang/StringBuilder:<init>	()V
    //   745: ldc_w 292
    //   748: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   751: aload_1
    //   752: getfield 226	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
    //   755: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   758: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   761: invokestatic 235	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   764: pop
    //   765: goto -606 -> 159
    //   768: iload_2
    //   769: iconst_1
    //   770: iadd
    //   771: istore 4
    //   773: aconst_null
    //   774: astore 15
    //   776: aload 10
    //   778: astore 11
    //   780: aload 10
    //   782: astore 12
    //   784: aload 10
    //   786: invokeinterface 264 1 0
    //   791: istore 5
    //   793: iconst_0
    //   794: istore_2
    //   795: aload 15
    //   797: astore 14
    //   799: iload_2
    //   800: iload 5
    //   802: if_icmpge +58 -> 860
    //   805: aload 10
    //   807: astore 11
    //   809: aload 10
    //   811: astore 12
    //   813: aload 10
    //   815: iload_2
    //   816: invokeinterface 268 2 0
    //   821: astore 18
    //   823: aload 10
    //   825: astore 11
    //   827: aload 10
    //   829: astore 12
    //   831: aload 10
    //   833: iload_2
    //   834: invokeinterface 271 2 0
    //   839: astore 14
    //   841: aload 10
    //   843: astore 11
    //   845: aload 10
    //   847: astore 12
    //   849: ldc_w 285
    //   852: aload 18
    //   854: invokevirtual 214	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   857: ifeq +320 -> 1177
    //   860: aload 10
    //   862: astore 11
    //   864: aload 10
    //   866: astore 12
    //   868: aload 17
    //   870: aload 14
    //   872: invokevirtual 288	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   875: pop
    //   876: iload 4
    //   878: istore_2
    //   879: goto -720 -> 159
    //   882: iload 4
    //   884: iconst_3
    //   885: if_icmpne -726 -> 159
    //   888: aload 10
    //   890: astore 11
    //   892: aload 10
    //   894: astore 12
    //   896: ldc -10
    //   898: aload 10
    //   900: invokeinterface 244 1 0
    //   905: invokevirtual 214	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   908: ifeq -749 -> 159
    //   911: aload 13
    //   913: ifnull -754 -> 159
    //   916: iload_3
    //   917: ifne +47 -> 964
    //   920: iload_2
    //   921: ifne +43 -> 964
    //   924: aload 10
    //   926: astore 11
    //   928: aload 10
    //   930: astore 12
    //   932: ldc 28
    //   934: new 216	java/lang/StringBuilder
    //   937: dup
    //   938: invokespecial 217	java/lang/StringBuilder:<init>	()V
    //   941: ldc_w 294
    //   944: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   947: aload_1
    //   948: getfield 226	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
    //   951: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   954: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   957: invokestatic 235	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   960: pop
    //   961: goto -802 -> 159
    //   964: aload 10
    //   966: astore 11
    //   968: aload 10
    //   970: astore 12
    //   972: aload_0
    //   973: getfield 120	com/android/server/midi/MidiService:mPackageManager	Landroid/content/pm/PackageManager;
    //   976: aload_1
    //   977: getfield 226	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
    //   980: iconst_0
    //   981: invokevirtual 300	android/content/pm/PackageManager:getApplicationInfo	(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;
    //   984: getfield 305	android/content/pm/ApplicationInfo:uid	I
    //   987: istore 4
    //   989: aload 10
    //   991: astore 11
    //   993: aload 10
    //   995: astore 12
    //   997: aload_0
    //   998: getfield 65	com/android/server/midi/MidiService:mDevicesByInfo	Ljava/util/HashMap;
    //   1001: astore 14
    //   1003: aload 10
    //   1005: astore 11
    //   1007: aload 10
    //   1009: astore 12
    //   1011: aload 14
    //   1013: monitorenter
    //   1014: aload_0
    //   1015: iconst_2
    //   1016: iload_3
    //   1017: iload_2
    //   1018: aload 16
    //   1020: getstatic 99	com/android/server/midi/MidiService:EMPTY_STRING_ARRAY	[Ljava/lang/String;
    //   1023: invokevirtual 309	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
    //   1026: checkcast 310	[Ljava/lang/String;
    //   1029: aload 17
    //   1031: getstatic 99	com/android/server/midi/MidiService:EMPTY_STRING_ARRAY	[Ljava/lang/String;
    //   1034: invokevirtual 309	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
    //   1037: checkcast 310	[Ljava/lang/String;
    //   1040: aload 13
    //   1042: aconst_null
    //   1043: aload_1
    //   1044: iload 9
    //   1046: iload 4
    //   1048: invokespecial 312	com/android/server/midi/MidiService:addDeviceLocked	(III[Ljava/lang/String;[Ljava/lang/String;Landroid/os/Bundle;Landroid/media/midi/IMidiDeviceServer;Landroid/content/pm/ServiceInfo;ZI)Landroid/media/midi/MidiDeviceInfo;
    //   1051: pop
    //   1052: aload 10
    //   1054: astore 11
    //   1056: aload 10
    //   1058: astore 12
    //   1060: aload 14
    //   1062: monitorexit
    //   1063: aconst_null
    //   1064: astore 13
    //   1066: aload 10
    //   1068: astore 11
    //   1070: aload 10
    //   1072: astore 12
    //   1074: aload 16
    //   1076: invokevirtual 315	java/util/ArrayList:clear	()V
    //   1079: aload 10
    //   1081: astore 11
    //   1083: aload 10
    //   1085: astore 12
    //   1087: aload 17
    //   1089: invokevirtual 315	java/util/ArrayList:clear	()V
    //   1092: goto -933 -> 159
    //   1095: astore 11
    //   1097: aload 10
    //   1099: astore 11
    //   1101: aload 10
    //   1103: astore 12
    //   1105: ldc 28
    //   1107: new 216	java/lang/StringBuilder
    //   1110: dup
    //   1111: invokespecial 217	java/lang/StringBuilder:<init>	()V
    //   1114: ldc_w 317
    //   1117: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1120: aload_1
    //   1121: getfield 226	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
    //   1124: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1127: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1130: invokestatic 186	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1133: pop
    //   1134: goto -975 -> 159
    //   1137: astore 13
    //   1139: aload 10
    //   1141: astore 11
    //   1143: aload 10
    //   1145: astore 12
    //   1147: aload 14
    //   1149: monitorexit
    //   1150: aload 10
    //   1152: astore 11
    //   1154: aload 10
    //   1156: astore 12
    //   1158: aload 13
    //   1160: athrow
    //   1161: iload 4
    //   1163: iconst_1
    //   1164: iadd
    //   1165: istore 4
    //   1167: goto -775 -> 392
    //   1170: iload_3
    //   1171: iconst_1
    //   1172: iadd
    //   1173: istore_3
    //   1174: goto -557 -> 617
    //   1177: iload_2
    //   1178: iconst_1
    //   1179: iadd
    //   1180: istore_2
    //   1181: goto -386 -> 795
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1184	0	this	MidiService
    //   0	1184	1	paramServiceInfo	ServiceInfo
    //   121	1060	2	i	int
    //   119	1055	3	j	int
    //   174	992	4	k	int
    //   364	439	5	m	int
    //   367	29	6	n	int
    //   387	24	7	i1	int
    //   370	117	8	bool1	boolean
    //   123	922	9	bool2	boolean
    //   16	230	10	localXmlResourceParser	android.content.res.XmlResourceParser
    //   280	875	10	localException	Exception
    //   4	1078	11	localObject1	Object
    //   1095	1	11	localNameNotFoundException	PackageManager.NameNotFoundException
    //   1099	54	11	localObject2	Object
    //   1	1156	12	localObject3	Object
    //   116	949	13	localObject4	Object
    //   1137	22	13	localObject5	Object
    //   216	932	14	localObject6	Object
    //   449	347	15	str1	String
    //   140	935	16	localArrayList1	ArrayList
    //   157	931	17	localArrayList2	ArrayList
    //   643	210	18	str2	String
    // Exception table:
    //   from	to	target	type
    //   6	18	280	java/lang/Exception
    //   44	56	280	java/lang/Exception
    //   64	102	280	java/lang/Exception
    //   133	142	280	java/lang/Exception
    //   150	159	280	java/lang/Exception
    //   167	176	280	java/lang/Exception
    //   209	218	280	java/lang/Exception
    //   226	236	280	java/lang/Exception
    //   249	277	280	java/lang/Exception
    //   337	346	280	java/lang/Exception
    //   354	363	280	java/lang/Exception
    //   380	389	280	java/lang/Exception
    //   421	432	280	java/lang/Exception
    //   440	451	280	java/lang/Exception
    //   459	470	280	java/lang/Exception
    //   478	488	280	java/lang/Exception
    //   499	508	280	java/lang/Exception
    //   534	545	280	java/lang/Exception
    //   558	587	280	java/lang/Exception
    //   606	615	280	java/lang/Exception
    //   635	645	280	java/lang/Exception
    //   653	663	280	java/lang/Exception
    //   671	682	280	java/lang/Exception
    //   690	698	280	java/lang/Exception
    //   712	723	280	java/lang/Exception
    //   736	765	280	java/lang/Exception
    //   784	793	280	java/lang/Exception
    //   813	823	280	java/lang/Exception
    //   831	841	280	java/lang/Exception
    //   849	860	280	java/lang/Exception
    //   868	876	280	java/lang/Exception
    //   896	911	280	java/lang/Exception
    //   932	961	280	java/lang/Exception
    //   972	989	280	java/lang/Exception
    //   997	1003	280	java/lang/Exception
    //   1011	1014	280	java/lang/Exception
    //   1060	1063	280	java/lang/Exception
    //   1074	1079	280	java/lang/Exception
    //   1087	1092	280	java/lang/Exception
    //   1105	1134	280	java/lang/Exception
    //   1147	1150	280	java/lang/Exception
    //   1158	1161	280	java/lang/Exception
    //   6	18	511	finally
    //   44	56	511	finally
    //   64	102	511	finally
    //   133	142	511	finally
    //   150	159	511	finally
    //   167	176	511	finally
    //   209	218	511	finally
    //   226	236	511	finally
    //   249	277	511	finally
    //   286	316	511	finally
    //   337	346	511	finally
    //   354	363	511	finally
    //   380	389	511	finally
    //   421	432	511	finally
    //   440	451	511	finally
    //   459	470	511	finally
    //   478	488	511	finally
    //   499	508	511	finally
    //   534	545	511	finally
    //   558	587	511	finally
    //   606	615	511	finally
    //   635	645	511	finally
    //   653	663	511	finally
    //   671	682	511	finally
    //   690	698	511	finally
    //   712	723	511	finally
    //   736	765	511	finally
    //   784	793	511	finally
    //   813	823	511	finally
    //   831	841	511	finally
    //   849	860	511	finally
    //   868	876	511	finally
    //   896	911	511	finally
    //   932	961	511	finally
    //   972	989	511	finally
    //   997	1003	511	finally
    //   1011	1014	511	finally
    //   1060	1063	511	finally
    //   1074	1079	511	finally
    //   1087	1092	511	finally
    //   1105	1134	511	finally
    //   1147	1150	511	finally
    //   1158	1161	511	finally
    //   972	989	1095	android/content/pm/PackageManager$NameNotFoundException
    //   1014	1052	1137	finally
  }
  
  private void addPackageDeviceServers(String paramString)
  {
    try
    {
      PackageInfo localPackageInfo = this.mPackageManager.getPackageInfo(paramString, 132);
      paramString = localPackageInfo.services;
      if (paramString == null) {
        return;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.e("MidiService", "handlePackageUpdate could not find package " + paramString, localNameNotFoundException);
      return;
    }
    int i = 0;
    while (i < paramString.length)
    {
      addPackageDeviceServer(paramString[i]);
      i += 1;
    }
  }
  
  private Client getClient(IBinder paramIBinder)
  {
    synchronized (this.mClients)
    {
      Client localClient2 = (Client)this.mClients.get(paramIBinder);
      Client localClient1 = localClient2;
      if (localClient2 == null) {
        localClient1 = new Client(paramIBinder);
      }
      try
      {
        paramIBinder.linkToDeath(localClient1, 0);
        this.mClients.put(paramIBinder, localClient1);
        return localClient1;
      }
      catch (RemoteException paramIBinder)
      {
        return null;
      }
    }
  }
  
  private void notifyDeviceStatusChanged(Device paramDevice, MidiDeviceStatus paramMidiDeviceStatus)
  {
    synchronized (this.mClients)
    {
      Iterator localIterator = this.mClients.values().iterator();
      if (localIterator.hasNext()) {
        ((Client)localIterator.next()).deviceStatusChanged(paramDevice, paramMidiDeviceStatus);
      }
    }
  }
  
  private void onUnlockUser()
  {
    this.mPackageMonitor.register(this.mContext, null, true);
    Object localObject1 = new Intent("android.media.midi.MidiDeviceService");
    localObject1 = this.mPackageManager.queryIntentServices((Intent)localObject1, 128);
    if (localObject1 != null)
    {
      int j = ((List)localObject1).size();
      int i = 0;
      while (i < j)
      {
        ServiceInfo localServiceInfo = ((ResolveInfo)((List)localObject1).get(i)).serviceInfo;
        if (localServiceInfo != null) {
          addPackageDeviceServer(localServiceInfo);
        }
        i += 1;
      }
    }
    try
    {
      localObject1 = this.mPackageManager.getPackageInfo("com.android.bluetoothmidiservice", 0);
      if ((localObject1 != null) && (((PackageInfo)localObject1).applicationInfo != null))
      {
        this.mBluetoothServiceUid = ((PackageInfo)localObject1).applicationInfo.uid;
        return;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        Object localObject2 = null;
      }
      this.mBluetoothServiceUid = -1;
    }
  }
  
  private void removeDeviceLocked(Device paramDevice)
  {
    ??? = paramDevice.getDeviceServer();
    if (??? != null) {
      this.mDevicesByServer.remove(((IMidiDeviceServer)???).asBinder());
    }
    this.mDevicesByInfo.remove(paramDevice.getDeviceInfo());
    synchronized (this.mClients)
    {
      Iterator localIterator = this.mClients.values().iterator();
      if (localIterator.hasNext()) {
        ((Client)localIterator.next()).deviceRemoved(paramDevice);
      }
    }
  }
  
  private void removePackageDeviceServers(String paramString)
  {
    synchronized (this.mDevicesByInfo)
    {
      Iterator localIterator = this.mDevicesByInfo.values().iterator();
      while (localIterator.hasNext())
      {
        Device localDevice = (Device)localIterator.next();
        if (paramString.equals(localDevice.getPackageName()))
        {
          localIterator.remove();
          removeDeviceLocked(localDevice);
        }
      }
    }
  }
  
  private void updateStickyDeviceStatus(int paramInt, IMidiDeviceListener paramIMidiDeviceListener)
  {
    synchronized (this.mDevicesByInfo)
    {
      Iterator localIterator = this.mDevicesByInfo.values().iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          Object localObject = (Device)localIterator.next();
          boolean bool = ((Device)localObject).isUidAllowed(paramInt);
          if (!bool) {
            continue;
          }
          try
          {
            localObject = ((Device)localObject).getDeviceStatus();
            if (localObject != null) {
              paramIMidiDeviceListener.onDeviceStatusChanged((MidiDeviceStatus)localObject);
            }
          }
          catch (RemoteException localRemoteException)
          {
            Log.e("MidiService", "remote exception", localRemoteException);
          }
        }
      }
    }
  }
  
  public void closeDevice(IBinder paramIBinder1, IBinder paramIBinder2)
  {
    paramIBinder1 = getClient(paramIBinder1);
    if (paramIBinder1 == null) {
      return;
    }
    paramIBinder1.removeDeviceConnection(paramIBinder2);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter arg2, String[] paramArrayOfString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "MidiService");
    paramFileDescriptor = new IndentingPrintWriter(???, "  ");
    paramFileDescriptor.println("MIDI Manager State:");
    paramFileDescriptor.increaseIndent();
    paramFileDescriptor.println("Devices:");
    paramFileDescriptor.increaseIndent();
    synchronized (this.mDevicesByInfo)
    {
      paramArrayOfString = this.mDevicesByInfo.values().iterator();
      if (paramArrayOfString.hasNext()) {
        paramFileDescriptor.println(((Device)paramArrayOfString.next()).toString());
      }
    }
    paramFileDescriptor.decreaseIndent();
    paramFileDescriptor.println("Clients:");
    paramFileDescriptor.increaseIndent();
    synchronized (this.mClients)
    {
      paramArrayOfString = this.mClients.values().iterator();
      if (paramArrayOfString.hasNext()) {
        paramFileDescriptor.println(((Client)paramArrayOfString.next()).toString());
      }
    }
    paramFileDescriptor.decreaseIndent();
  }
  
  public MidiDeviceStatus getDeviceStatus(MidiDeviceInfo paramMidiDeviceInfo)
  {
    Device localDevice = (Device)this.mDevicesByInfo.get(paramMidiDeviceInfo);
    if (localDevice == null) {
      throw new IllegalArgumentException("no such device for " + paramMidiDeviceInfo);
    }
    return localDevice.getDeviceStatus();
  }
  
  public MidiDeviceInfo[] getDevices()
  {
    ArrayList localArrayList = new ArrayList();
    int i = Binder.getCallingUid();
    synchronized (this.mDevicesByInfo)
    {
      Iterator localIterator = this.mDevicesByInfo.values().iterator();
      while (localIterator.hasNext())
      {
        Device localDevice = (Device)localIterator.next();
        if (localDevice.isUidAllowed(i)) {
          localArrayList.add(localDevice.getDeviceInfo());
        }
      }
    }
    return (MidiDeviceInfo[])((ArrayList)localObject).toArray(EMPTY_DEVICE_INFO_ARRAY);
  }
  
  public MidiDeviceInfo getServiceDeviceInfo(String paramString1, String paramString2)
  {
    synchronized (this.mDevicesByInfo)
    {
      Iterator localIterator = this.mDevicesByInfo.values().iterator();
      while (localIterator.hasNext())
      {
        Device localDevice = (Device)localIterator.next();
        ServiceInfo localServiceInfo = localDevice.getServiceInfo();
        if ((localServiceInfo != null) && (paramString1.equals(localServiceInfo.packageName)) && (paramString2.equals(localServiceInfo.name)))
        {
          paramString1 = localDevice.getDeviceInfo();
          return paramString1;
        }
      }
      return null;
    }
  }
  
  public void openBluetoothDevice(IBinder paramIBinder, BluetoothDevice paramBluetoothDevice, IMidiDeviceOpenCallback paramIMidiDeviceOpenCallback)
  {
    Client localClient = getClient(paramIBinder);
    if (localClient == null) {
      return;
    }
    synchronized (this.mDevicesByInfo)
    {
      Device localDevice = (Device)this.mBluetoothDevices.get(paramBluetoothDevice);
      paramIBinder = localDevice;
      if (localDevice == null)
      {
        paramIBinder = new Device(paramBluetoothDevice);
        this.mBluetoothDevices.put(paramBluetoothDevice, paramIBinder);
      }
      l = Binder.clearCallingIdentity();
    }
  }
  
  public void openDevice(IBinder arg1, MidiDeviceInfo paramMidiDeviceInfo, IMidiDeviceOpenCallback paramIMidiDeviceOpenCallback)
  {
    Client localClient = getClient(???);
    if (localClient == null) {
      return;
    }
    Device localDevice;
    synchronized (this.mDevicesByInfo)
    {
      localDevice = (Device)this.mDevicesByInfo.get(paramMidiDeviceInfo);
      if (localDevice == null) {
        throw new IllegalArgumentException("device does not exist: " + paramMidiDeviceInfo);
      }
    }
    if (!localDevice.isUidAllowed(Binder.getCallingUid())) {
      throw new SecurityException("Attempt to open private device with wrong UID");
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      localClient.addDeviceConnection(localDevice, paramIMidiDeviceOpenCallback);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public MidiDeviceInfo registerDeviceServer(IMidiDeviceServer paramIMidiDeviceServer, int paramInt1, int paramInt2, String[] paramArrayOfString1, String[] paramArrayOfString2, Bundle paramBundle, int paramInt3)
  {
    int i = Binder.getCallingUid();
    if ((paramInt3 == 1) && (i != 1000)) {
      throw new SecurityException("only system can create USB devices");
    }
    if ((paramInt3 == 3) && (i != this.mBluetoothServiceUid)) {
      throw new SecurityException("only MidiBluetoothService can create Bluetooth devices");
    }
    synchronized (this.mDevicesByInfo)
    {
      paramIMidiDeviceServer = addDeviceLocked(paramInt3, paramInt1, paramInt2, paramArrayOfString1, paramArrayOfString2, paramBundle, paramIMidiDeviceServer, null, false, i);
      return paramIMidiDeviceServer;
    }
  }
  
  public void registerListener(IBinder paramIBinder, IMidiDeviceListener paramIMidiDeviceListener)
  {
    paramIBinder = getClient(paramIBinder);
    if (paramIBinder == null) {
      return;
    }
    paramIBinder.addListener(paramIMidiDeviceListener);
    updateStickyDeviceStatus(Client.-get0(paramIBinder), paramIMidiDeviceListener);
  }
  
  public void setDeviceStatus(IMidiDeviceServer paramIMidiDeviceServer, MidiDeviceStatus paramMidiDeviceStatus)
  {
    paramIMidiDeviceServer = (Device)this.mDevicesByServer.get(paramIMidiDeviceServer.asBinder());
    if (paramIMidiDeviceServer != null)
    {
      if (Binder.getCallingUid() != paramIMidiDeviceServer.getUid()) {
        throw new SecurityException("setDeviceStatus() caller UID " + Binder.getCallingUid() + " does not match device's UID " + paramIMidiDeviceServer.getUid());
      }
      paramIMidiDeviceServer.setDeviceStatus(paramMidiDeviceStatus);
      notifyDeviceStatusChanged(paramIMidiDeviceServer, paramMidiDeviceStatus);
    }
  }
  
  public void unregisterDeviceServer(IMidiDeviceServer paramIMidiDeviceServer)
  {
    synchronized (this.mDevicesByInfo)
    {
      paramIMidiDeviceServer = (Device)this.mDevicesByServer.get(paramIMidiDeviceServer.asBinder());
      if (paramIMidiDeviceServer != null) {
        paramIMidiDeviceServer.closeLocked();
      }
      return;
    }
  }
  
  public void unregisterListener(IBinder paramIBinder, IMidiDeviceListener paramIMidiDeviceListener)
  {
    paramIBinder = getClient(paramIBinder);
    if (paramIBinder == null) {
      return;
    }
    paramIBinder.removeListener(paramIMidiDeviceListener);
  }
  
  private final class Client
    implements IBinder.DeathRecipient
  {
    private final HashMap<IBinder, MidiService.DeviceConnection> mDeviceConnections = new HashMap();
    private final HashMap<IBinder, IMidiDeviceListener> mListeners = new HashMap();
    private final int mPid;
    private final IBinder mToken;
    private final int mUid;
    
    public Client(IBinder paramIBinder)
    {
      this.mToken = paramIBinder;
      this.mUid = Binder.getCallingUid();
      this.mPid = Binder.getCallingPid();
    }
    
    private void close()
    {
      synchronized (MidiService.-get2(MidiService.this))
      {
        MidiService.-get2(MidiService.this).remove(this.mToken);
        this.mToken.unlinkToDeath(this, 0);
        ??? = this.mDeviceConnections.values().iterator();
        if (((Iterator)???).hasNext())
        {
          MidiService.DeviceConnection localDeviceConnection = (MidiService.DeviceConnection)((Iterator)???).next();
          localDeviceConnection.getDevice().removeDeviceConnection(localDeviceConnection);
        }
      }
    }
    
    public void addDeviceConnection(MidiService.Device paramDevice, IMidiDeviceOpenCallback paramIMidiDeviceOpenCallback)
    {
      paramIMidiDeviceOpenCallback = new MidiService.DeviceConnection(MidiService.this, paramDevice, this, paramIMidiDeviceOpenCallback);
      this.mDeviceConnections.put(paramIMidiDeviceOpenCallback.getToken(), paramIMidiDeviceOpenCallback);
      paramDevice.addDeviceConnection(paramIMidiDeviceOpenCallback);
    }
    
    public void addListener(IMidiDeviceListener paramIMidiDeviceListener)
    {
      this.mListeners.put(paramIMidiDeviceListener.asBinder(), paramIMidiDeviceListener);
    }
    
    public void binderDied()
    {
      Log.d("MidiService", "Client died: " + this);
      close();
    }
    
    public void deviceAdded(MidiService.Device paramDevice)
    {
      if (!paramDevice.isUidAllowed(this.mUid)) {
        return;
      }
      paramDevice = paramDevice.getDeviceInfo();
      try
      {
        Iterator localIterator = this.mListeners.values().iterator();
        while (localIterator.hasNext()) {
          ((IMidiDeviceListener)localIterator.next()).onDeviceAdded(paramDevice);
        }
        return;
      }
      catch (RemoteException paramDevice)
      {
        Log.e("MidiService", "remote exception", paramDevice);
      }
    }
    
    public void deviceRemoved(MidiService.Device paramDevice)
    {
      if (!paramDevice.isUidAllowed(this.mUid)) {
        return;
      }
      paramDevice = paramDevice.getDeviceInfo();
      try
      {
        Iterator localIterator = this.mListeners.values().iterator();
        while (localIterator.hasNext()) {
          ((IMidiDeviceListener)localIterator.next()).onDeviceRemoved(paramDevice);
        }
        return;
      }
      catch (RemoteException paramDevice)
      {
        Log.e("MidiService", "remote exception", paramDevice);
      }
    }
    
    public void deviceStatusChanged(MidiService.Device paramDevice, MidiDeviceStatus paramMidiDeviceStatus)
    {
      if (!paramDevice.isUidAllowed(this.mUid)) {
        return;
      }
      try
      {
        paramDevice = this.mListeners.values().iterator();
        while (paramDevice.hasNext()) {
          ((IMidiDeviceListener)paramDevice.next()).onDeviceStatusChanged(paramMidiDeviceStatus);
        }
        return;
      }
      catch (RemoteException paramDevice)
      {
        Log.e("MidiService", "remote exception", paramDevice);
      }
    }
    
    public int getUid()
    {
      return this.mUid;
    }
    
    public void removeDeviceConnection(IBinder paramIBinder)
    {
      paramIBinder = (MidiService.DeviceConnection)this.mDeviceConnections.remove(paramIBinder);
      if (paramIBinder != null) {
        paramIBinder.getDevice().removeDeviceConnection(paramIBinder);
      }
      if ((this.mListeners.size() == 0) && (this.mDeviceConnections.size() == 0)) {
        close();
      }
    }
    
    public void removeDeviceConnection(MidiService.DeviceConnection paramDeviceConnection)
    {
      this.mDeviceConnections.remove(paramDeviceConnection.getToken());
      if ((this.mListeners.size() == 0) && (this.mDeviceConnections.size() == 0)) {
        close();
      }
    }
    
    public void removeListener(IMidiDeviceListener paramIMidiDeviceListener)
    {
      this.mListeners.remove(paramIMidiDeviceListener.asBinder());
      if ((this.mListeners.size() == 0) && (this.mDeviceConnections.size() == 0)) {
        close();
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("Client: UID: ");
      localStringBuilder.append(this.mUid);
      localStringBuilder.append(" PID: ");
      localStringBuilder.append(this.mPid);
      localStringBuilder.append(" listener count: ");
      localStringBuilder.append(this.mListeners.size());
      localStringBuilder.append(" Device Connections:");
      Iterator localIterator = this.mDeviceConnections.values().iterator();
      while (localIterator.hasNext())
      {
        MidiService.DeviceConnection localDeviceConnection = (MidiService.DeviceConnection)localIterator.next();
        localStringBuilder.append(" <device ");
        localStringBuilder.append(localDeviceConnection.getDevice().getDeviceInfo().getId());
        localStringBuilder.append(">");
      }
      return localStringBuilder.toString();
    }
  }
  
  private final class Device
    implements IBinder.DeathRecipient
  {
    private final BluetoothDevice mBluetoothDevice;
    private final ArrayList<MidiService.DeviceConnection> mDeviceConnections = new ArrayList();
    private MidiDeviceInfo mDeviceInfo;
    private MidiDeviceStatus mDeviceStatus;
    private IMidiDeviceServer mServer;
    private ServiceConnection mServiceConnection;
    private final ServiceInfo mServiceInfo;
    private final int mUid;
    
    public Device(BluetoothDevice paramBluetoothDevice)
    {
      this.mBluetoothDevice = paramBluetoothDevice;
      this.mServiceInfo = null;
      this.mUid = MidiService.-get1(MidiService.this);
    }
    
    public Device(IMidiDeviceServer paramIMidiDeviceServer, MidiDeviceInfo paramMidiDeviceInfo, ServiceInfo paramServiceInfo, int paramInt)
    {
      this.mDeviceInfo = paramMidiDeviceInfo;
      this.mServiceInfo = paramServiceInfo;
      this.mUid = paramInt;
      this.mBluetoothDevice = ((BluetoothDevice)paramMidiDeviceInfo.getProperties().getParcelable("bluetooth_device"));
      setDeviceServer(paramIMidiDeviceServer);
    }
    
    private void setDeviceServer(IMidiDeviceServer paramIMidiDeviceServer)
    {
      Object localObject;
      if (paramIMidiDeviceServer != null)
      {
        if (this.mServer != null)
        {
          Log.e("MidiService", "mServer already set in setDeviceServer");
          return;
        }
        localObject = paramIMidiDeviceServer.asBinder();
      }
      for (;;)
      {
        try
        {
          if (this.mDeviceInfo == null) {
            this.mDeviceInfo = paramIMidiDeviceServer.getDeviceInfo();
          }
          ((IBinder)localObject).linkToDeath(this, 0);
          this.mServer = paramIMidiDeviceServer;
          MidiService.-get5(MidiService.this).put(localObject, this);
          if (this.mDeviceConnections == null) {
            break;
          }
          localObject = this.mDeviceConnections.iterator();
          if (!((Iterator)localObject).hasNext()) {
            break;
          }
          ((MidiService.DeviceConnection)((Iterator)localObject).next()).notifyClient(paramIMidiDeviceServer);
          continue;
          if (this.mServer == null) {
            continue;
          }
        }
        catch (RemoteException paramIMidiDeviceServer)
        {
          this.mServer = null;
          return;
        }
        paramIMidiDeviceServer = this.mServer;
        this.mServer = null;
        localObject = paramIMidiDeviceServer.asBinder();
        MidiService.-get5(MidiService.this).remove(localObject);
        try
        {
          paramIMidiDeviceServer.closeDevice();
          ((IBinder)localObject).unlinkToDeath(this, 0);
        }
        catch (RemoteException localRemoteException) {}
      }
    }
    
    public void addDeviceConnection(MidiService.DeviceConnection paramDeviceConnection)
    {
      for (;;)
      {
        synchronized (this.mDeviceConnections)
        {
          if (this.mServer != null)
          {
            this.mDeviceConnections.add(paramDeviceConnection);
            paramDeviceConnection.notifyClient(this.mServer);
            return;
          }
          if ((this.mServiceConnection != null) || ((this.mServiceInfo == null) && (this.mBluetoothDevice == null))) {
            break label210;
          }
          this.mDeviceConnections.add(paramDeviceConnection);
          this.mServiceConnection = new ServiceConnection()
          {
            public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
            {
              paramAnonymousComponentName = null;
              if (MidiService.Device.-get0(MidiService.Device.this) != null) {
                paramAnonymousIBinder = IBluetoothMidiService.Stub.asInterface(paramAnonymousIBinder);
              }
              for (;;)
              {
                try
                {
                  paramAnonymousIBinder = IMidiDeviceServer.Stub.asInterface(paramAnonymousIBinder.addBluetoothDevice(MidiService.Device.-get0(MidiService.Device.this)));
                  paramAnonymousComponentName = paramAnonymousIBinder;
                }
                catch (RemoteException paramAnonymousIBinder)
                {
                  Log.e("MidiService", "Could not call addBluetoothDevice()", paramAnonymousIBinder);
                  continue;
                }
                MidiService.Device.-wrap0(MidiService.Device.this, paramAnonymousComponentName);
                return;
                paramAnonymousComponentName = IMidiDeviceServer.Stub.asInterface(paramAnonymousIBinder);
              }
            }
            
            public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
            {
              MidiService.Device.-wrap0(MidiService.Device.this, null);
              MidiService.Device.-set0(MidiService.Device.this, null);
            }
          };
          if (this.mBluetoothDevice != null)
          {
            paramDeviceConnection = new Intent("android.media.midi.BluetoothMidiService");
            paramDeviceConnection.setComponent(new ComponentName("com.android.bluetoothmidiservice", "com.android.bluetoothmidiservice.BluetoothMidiService"));
            if (MidiService.-get3(MidiService.this).bindService(paramDeviceConnection, this.mServiceConnection, 1)) {
              continue;
            }
            Log.e("MidiService", "Unable to bind service: " + paramDeviceConnection);
            setDeviceServer(null);
            this.mServiceConnection = null;
          }
        }
        paramDeviceConnection = new Intent("android.media.midi.MidiDeviceService");
        paramDeviceConnection.setComponent(new ComponentName(this.mServiceInfo.packageName, this.mServiceInfo.name));
        continue;
        label210:
        Log.e("MidiService", "No way to connect to device in addDeviceConnection");
        paramDeviceConnection.notifyClient(null);
      }
    }
    
    public void binderDied()
    {
      Log.d("MidiService", "Device died: " + this);
      synchronized (MidiService.-get4(MidiService.this))
      {
        closeLocked();
        return;
      }
    }
    
    public void closeLocked()
    {
      synchronized (this.mDeviceConnections)
      {
        Iterator localIterator = this.mDeviceConnections.iterator();
        if (localIterator.hasNext())
        {
          MidiService.DeviceConnection localDeviceConnection = (MidiService.DeviceConnection)localIterator.next();
          localDeviceConnection.getClient().removeDeviceConnection(localDeviceConnection);
        }
      }
      this.mDeviceConnections.clear();
      setDeviceServer(null);
      if (this.mServiceInfo == null) {
        MidiService.-wrap2(MidiService.this, this);
      }
      for (;;)
      {
        if (this.mBluetoothDevice != null) {
          MidiService.-get0(MidiService.this).remove(this.mBluetoothDevice);
        }
        return;
        this.mDeviceStatus = new MidiDeviceStatus(this.mDeviceInfo);
      }
    }
    
    public MidiDeviceInfo getDeviceInfo()
    {
      return this.mDeviceInfo;
    }
    
    public IMidiDeviceServer getDeviceServer()
    {
      return this.mServer;
    }
    
    public MidiDeviceStatus getDeviceStatus()
    {
      return this.mDeviceStatus;
    }
    
    public String getPackageName()
    {
      if (this.mServiceInfo == null) {
        return null;
      }
      return this.mServiceInfo.packageName;
    }
    
    public ServiceInfo getServiceInfo()
    {
      return this.mServiceInfo;
    }
    
    public int getUid()
    {
      return this.mUid;
    }
    
    public boolean isUidAllowed(int paramInt)
    {
      return (!this.mDeviceInfo.isPrivate()) || (this.mUid == paramInt);
    }
    
    public void removeDeviceConnection(MidiService.DeviceConnection arg1)
    {
      for (;;)
      {
        synchronized (this.mDeviceConnections)
        {
          this.mDeviceConnections.remove(???);
          if ((this.mDeviceConnections.size() == 0) && (this.mServiceConnection != null))
          {
            MidiService.-get3(MidiService.this).unbindService(this.mServiceConnection);
            this.mServiceConnection = null;
            if (this.mBluetoothDevice == null) {}
          }
          else
          {
            synchronized (MidiService.-get4(MidiService.this))
            {
              closeLocked();
              return;
            }
          }
        }
        setDeviceServer(null);
      }
    }
    
    public void setDeviceInfo(MidiDeviceInfo paramMidiDeviceInfo)
    {
      this.mDeviceInfo = paramMidiDeviceInfo;
    }
    
    public void setDeviceStatus(MidiDeviceStatus paramMidiDeviceStatus)
    {
      this.mDeviceStatus = paramMidiDeviceStatus;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("Device Info: ");
      localStringBuilder.append(this.mDeviceInfo);
      localStringBuilder.append(" Status: ");
      localStringBuilder.append(this.mDeviceStatus);
      localStringBuilder.append(" UID: ");
      localStringBuilder.append(this.mUid);
      localStringBuilder.append(" DeviceConnection count: ");
      localStringBuilder.append(this.mDeviceConnections.size());
      localStringBuilder.append(" mServiceConnection: ");
      localStringBuilder.append(this.mServiceConnection);
      return localStringBuilder.toString();
    }
  }
  
  private final class DeviceConnection
  {
    private IMidiDeviceOpenCallback mCallback;
    private final MidiService.Client mClient;
    private final MidiService.Device mDevice;
    private final IBinder mToken = new Binder();
    
    public DeviceConnection(MidiService.Device paramDevice, MidiService.Client paramClient, IMidiDeviceOpenCallback paramIMidiDeviceOpenCallback)
    {
      this.mDevice = paramDevice;
      this.mClient = paramClient;
      this.mCallback = paramIMidiDeviceOpenCallback;
    }
    
    public MidiService.Client getClient()
    {
      return this.mClient;
    }
    
    public MidiService.Device getDevice()
    {
      return this.mDevice;
    }
    
    public IBinder getToken()
    {
      return this.mToken;
    }
    
    public void notifyClient(IMidiDeviceServer paramIMidiDeviceServer)
    {
      if (this.mCallback != null) {}
      for (;;)
      {
        try
        {
          IMidiDeviceOpenCallback localIMidiDeviceOpenCallback = this.mCallback;
          if (paramIMidiDeviceServer != null) {
            continue;
          }
          localIBinder = null;
          localIMidiDeviceOpenCallback.onDeviceOpened(paramIMidiDeviceServer, localIBinder);
        }
        catch (RemoteException paramIMidiDeviceServer)
        {
          IBinder localIBinder;
          continue;
        }
        this.mCallback = null;
        return;
        localIBinder = this.mToken;
      }
    }
    
    public String toString()
    {
      return "DeviceConnection Device ID: " + this.mDevice.getDeviceInfo().getId();
    }
  }
  
  public static class Lifecycle
    extends SystemService
  {
    private MidiService mMidiService;
    
    public Lifecycle(Context paramContext)
    {
      super();
    }
    
    public void onStart()
    {
      this.mMidiService = new MidiService(getContext());
      publishBinderService("midi", this.mMidiService);
    }
    
    public void onUnlockUser(int paramInt)
    {
      if (paramInt == 0) {
        MidiService.-wrap1(this.mMidiService);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/midi/MidiService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */