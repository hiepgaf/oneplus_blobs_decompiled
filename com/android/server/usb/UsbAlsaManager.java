package com.android.server.usb;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.media.IAudioService;
import android.media.IAudioService.Stub;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.util.Slog;
import com.android.internal.alsa.AlsaCardsParser;
import com.android.internal.alsa.AlsaCardsParser.AlsaCardRecord;
import com.android.internal.alsa.AlsaDevicesParser;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.audio.AudioService;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import libcore.io.IoUtils;

public final class UsbAlsaManager
{
  private static final String ALSA_DIRECTORY = "/dev/snd/";
  private static final boolean DEBUG = false;
  private static final String TAG = UsbAlsaManager.class.getSimpleName();
  private UsbAudioDevice mAccessoryAudioDevice = null;
  private final HashMap<String, AlsaDevice> mAlsaDevices = new HashMap();
  private final FileObserver mAlsaObserver = new FileObserver("/dev/snd/", 768)
  {
    public void onEvent(int paramAnonymousInt, String paramAnonymousString)
    {
      switch (paramAnonymousInt)
      {
      default: 
        return;
      case 256: 
        UsbAlsaManager.-wrap0(UsbAlsaManager.this, paramAnonymousString);
        return;
      }
      UsbAlsaManager.-wrap1(UsbAlsaManager.this, paramAnonymousString);
    }
  };
  private final HashMap<UsbDevice, UsbAudioDevice> mAudioDevices = new HashMap();
  private IAudioService mAudioService;
  private final AlsaCardsParser mCardsParser = new AlsaCardsParser();
  private final Context mContext;
  private final AlsaDevicesParser mDevicesParser = new AlsaDevicesParser();
  private final boolean mHasMidiFeature;
  private final HashMap<UsbDevice, UsbMidiDevice> mMidiDevices = new HashMap();
  private UsbMidiDevice mPeripheralMidiDevice = null;
  
  UsbAlsaManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHasMidiFeature = paramContext.getPackageManager().hasSystemFeature("android.software.midi");
    this.mCardsParser.scan();
  }
  
  /* Error */
  private void alsaFileAdded(String paramString)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_1
    //   3: ldc 117
    //   5: invokevirtual 122	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   8: ifeq +181 -> 189
    //   11: aload_1
    //   12: ldc 124
    //   14: invokevirtual 127	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   17: ifeq +158 -> 175
    //   20: iconst_1
    //   21: istore_2
    //   22: iload_2
    //   23: ifeq +151 -> 174
    //   26: aload_1
    //   27: bipush 67
    //   29: invokevirtual 131	java/lang/String:indexOf	(I)I
    //   32: istore 6
    //   34: aload_1
    //   35: bipush 68
    //   37: invokevirtual 131	java/lang/String:indexOf	(I)I
    //   40: istore 5
    //   42: aload_1
    //   43: invokevirtual 135	java/lang/String:length	()I
    //   46: istore 4
    //   48: iload_2
    //   49: iconst_1
    //   50: if_icmpeq +190 -> 240
    //   53: iload 4
    //   55: istore_3
    //   56: iload_2
    //   57: iconst_2
    //   58: if_icmpne +6 -> 64
    //   61: goto +179 -> 240
    //   64: aload_1
    //   65: iload 6
    //   67: iconst_1
    //   68: iadd
    //   69: iload 5
    //   71: invokevirtual 139	java/lang/String:substring	(II)Ljava/lang/String;
    //   74: invokestatic 145	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   77: istore 4
    //   79: aload_1
    //   80: iload 5
    //   82: iconst_1
    //   83: iadd
    //   84: iload_3
    //   85: invokevirtual 139	java/lang/String:substring	(II)Ljava/lang/String;
    //   88: invokestatic 145	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   91: istore_3
    //   92: aload_0
    //   93: getfield 83	com/android/server/usb/UsbAlsaManager:mAlsaDevices	Ljava/util/HashMap;
    //   96: astore 7
    //   98: aload 7
    //   100: monitorenter
    //   101: aload_0
    //   102: getfield 83	com/android/server/usb/UsbAlsaManager:mAlsaDevices	Ljava/util/HashMap;
    //   105: aload_1
    //   106: invokevirtual 149	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   109: ifnonnull +62 -> 171
    //   112: new 8	com/android/server/usb/UsbAlsaManager$AlsaDevice
    //   115: dup
    //   116: aload_0
    //   117: iload_2
    //   118: iload 4
    //   120: iload_3
    //   121: invokespecial 152	com/android/server/usb/UsbAlsaManager$AlsaDevice:<init>	(Lcom/android/server/usb/UsbAlsaManager;III)V
    //   124: astore 8
    //   126: getstatic 60	com/android/server/usb/UsbAlsaManager:TAG	Ljava/lang/String;
    //   129: new 154	java/lang/StringBuilder
    //   132: dup
    //   133: invokespecial 155	java/lang/StringBuilder:<init>	()V
    //   136: ldc -99
    //   138: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: aload 8
    //   143: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   146: invokevirtual 167	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   149: invokestatic 173	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   152: pop
    //   153: aload_0
    //   154: getfield 83	com/android/server/usb/UsbAlsaManager:mAlsaDevices	Ljava/util/HashMap;
    //   157: aload_1
    //   158: aload 8
    //   160: invokevirtual 177	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   163: pop
    //   164: aload_0
    //   165: getfield 83	com/android/server/usb/UsbAlsaManager:mAlsaDevices	Ljava/util/HashMap;
    //   168: invokevirtual 180	java/util/HashMap:notifyAll	()V
    //   171: aload 7
    //   173: monitorexit
    //   174: return
    //   175: aload_1
    //   176: ldc -74
    //   178: invokevirtual 127	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   181: ifeq -159 -> 22
    //   184: iconst_2
    //   185: istore_2
    //   186: goto -164 -> 22
    //   189: aload_1
    //   190: ldc -72
    //   192: invokevirtual 122	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   195: ifeq -173 -> 22
    //   198: iconst_3
    //   199: istore_2
    //   200: goto -178 -> 22
    //   203: astore 7
    //   205: getstatic 60	com/android/server/usb/UsbAlsaManager:TAG	Ljava/lang/String;
    //   208: new 154	java/lang/StringBuilder
    //   211: dup
    //   212: invokespecial 155	java/lang/StringBuilder:<init>	()V
    //   215: ldc -70
    //   217: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   220: aload_1
    //   221: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   224: invokevirtual 167	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   227: aload 7
    //   229: invokestatic 190	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   232: pop
    //   233: return
    //   234: astore_1
    //   235: aload 7
    //   237: monitorexit
    //   238: aload_1
    //   239: athrow
    //   240: iload 4
    //   242: iconst_1
    //   243: isub
    //   244: istore_3
    //   245: goto -181 -> 64
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	248	0	this	UsbAlsaManager
    //   0	248	1	paramString	String
    //   1	199	2	i	int
    //   55	190	3	j	int
    //   46	198	4	k	int
    //   40	44	5	m	int
    //   32	37	6	n	int
    //   203	33	7	localException	Exception
    //   124	35	8	localAlsaDevice	AlsaDevice
    // Exception table:
    //   from	to	target	type
    //   26	48	203	java/lang/Exception
    //   64	92	203	java/lang/Exception
    //   101	171	234	finally
  }
  
  private void alsaFileRemoved(String paramString)
  {
    synchronized (this.mAlsaDevices)
    {
      paramString = (AlsaDevice)this.mAlsaDevices.remove(paramString);
      if (paramString != null) {
        Slog.d(TAG, "ALSA device removed: " + paramString);
      }
      return;
    }
  }
  
  private void notifyDeviceState(UsbAudioDevice paramUsbAudioDevice, boolean paramBoolean)
  {
    if (this.mAudioService == null)
    {
      Slog.e(TAG, "no AudioService");
      return;
    }
    if (Settings.Secure.getInt(this.mContext.getContentResolver(), "usb_audio_automatic_routing_disabled", 0) != 0) {
      return;
    }
    if (paramBoolean) {}
    int j;
    int k;
    for (int i = 1;; i = 0)
    {
      j = paramUsbAudioDevice.mCard;
      k = paramUsbAudioDevice.mDevice;
      if ((j >= 0) && (k >= 0)) {
        break;
      }
      Slog.e(TAG, "Invalid alsa card or device alsaCard: " + j + " alsaDevice: " + k);
      return;
    }
    String str = AudioService.makeAlsaAddressString(j, k);
    try
    {
      if (paramUsbAudioDevice.mHasPlayback)
      {
        if (paramUsbAudioDevice == this.mAccessoryAudioDevice)
        {
          j = 8192;
          this.mAudioService.setWiredDeviceConnectionState(j, i, str, paramUsbAudioDevice.mDeviceName, TAG);
        }
      }
      else if (paramUsbAudioDevice.mHasCapture) {
        if (paramUsbAudioDevice != this.mAccessoryAudioDevice) {
          break label204;
        }
      }
      label204:
      for (j = -2147481600;; j = -2147479552)
      {
        this.mAudioService.setWiredDeviceConnectionState(j, i, str, paramUsbAudioDevice.mDeviceName, TAG);
        return;
        j = 16384;
        break;
      }
      return;
    }
    catch (RemoteException paramUsbAudioDevice)
    {
      Slog.e(TAG, "RemoteException in setWiredDeviceConnectionState");
    }
  }
  
  private AlsaDevice waitForAlsaDevice(int paramInt1, int paramInt2, int paramInt3)
  {
    AlsaDevice localAlsaDevice = new AlsaDevice(paramInt3, paramInt1, paramInt2);
    synchronized (this.mAlsaDevices)
    {
      long l1 = SystemClock.elapsedRealtime() + 2500L;
      for (;;)
      {
        boolean bool = this.mAlsaDevices.values().contains(localAlsaDevice);
        if (bool) {
          return localAlsaDevice;
        }
        long l2 = SystemClock.elapsedRealtime();
        l2 = l1 - l2;
        if (l2 > 0L) {}
        try
        {
          this.mAlsaDevices.wait(l2);
          l2 = SystemClock.elapsedRealtime();
          if (l1 > l2) {
            continue;
          }
          Slog.e(TAG, "waitForAlsaDevice failed for " + localAlsaDevice);
          return null;
        }
        catch (InterruptedException localInterruptedException)
        {
          for (;;)
          {
            Slog.d(TAG, "usb: InterruptedException while waiting for ALSA file.");
          }
        }
      }
    }
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    paramIndentingPrintWriter.println("USB Audio Devices:");
    Iterator localIterator = this.mAudioDevices.keySet().iterator();
    UsbDevice localUsbDevice;
    while (localIterator.hasNext())
    {
      localUsbDevice = (UsbDevice)localIterator.next();
      paramIndentingPrintWriter.println("  " + localUsbDevice.getDeviceName() + ": " + this.mAudioDevices.get(localUsbDevice));
    }
    paramIndentingPrintWriter.println("USB MIDI Devices:");
    localIterator = this.mMidiDevices.keySet().iterator();
    while (localIterator.hasNext())
    {
      localUsbDevice = (UsbDevice)localIterator.next();
      paramIndentingPrintWriter.println("  " + localUsbDevice.getDeviceName() + ": " + this.mMidiDevices.get(localUsbDevice));
    }
  }
  
  public ArrayList<UsbAudioDevice> getConnectedDevices()
  {
    ArrayList localArrayList = new ArrayList(this.mAudioDevices.size());
    Iterator localIterator = this.mAudioDevices.entrySet().iterator();
    while (localIterator.hasNext()) {
      localArrayList.add((UsbAudioDevice)((Map.Entry)localIterator.next()).getValue());
    }
    return localArrayList;
  }
  
  public void logDevices(String paramString) {}
  
  public void logDevicesList(String paramString) {}
  
  UsbAudioDevice selectAudioCard(int paramInt)
  {
    if (!this.mCardsParser.isCardUsb(paramInt)) {
      return null;
    }
    this.mDevicesParser.scan();
    int j = this.mDevicesParser.getDefaultDeviceNum(paramInt);
    boolean bool1 = this.mDevicesParser.hasPlaybackDevices(paramInt);
    boolean bool2 = this.mDevicesParser.hasCaptureDevices(paramInt);
    if (this.mCardsParser.isCardUsb(paramInt)) {}
    for (int i = 2; (bool1) && (waitForAlsaDevice(paramInt, j, 1) == null); i = 1) {
      return null;
    }
    if ((bool2) && (waitForAlsaDevice(paramInt, j, 2) == null)) {
      return null;
    }
    UsbAudioDevice localUsbAudioDevice = new UsbAudioDevice(paramInt, j, bool1, bool2, i | 0x80000000);
    AlsaCardsParser.AlsaCardRecord localAlsaCardRecord = this.mCardsParser.getCardRecordFor(paramInt);
    localUsbAudioDevice.mDeviceName = localAlsaCardRecord.mCardName;
    localUsbAudioDevice.mDeviceDescription = localAlsaCardRecord.mCardDescription;
    notifyDeviceState(localUsbAudioDevice, true);
    return localUsbAudioDevice;
  }
  
  UsbAudioDevice selectDefaultDevice()
  {
    return selectAudioCard(this.mCardsParser.getDefaultCard());
  }
  
  void setAccessoryAudioState(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (paramBoolean)
    {
      this.mAccessoryAudioDevice = new UsbAudioDevice(paramInt1, paramInt2, true, false, 2);
      notifyDeviceState(this.mAccessoryAudioDevice, true);
    }
    while (this.mAccessoryAudioDevice == null) {
      return;
    }
    notifyDeviceState(this.mAccessoryAudioDevice, false);
    this.mAccessoryAudioDevice = null;
  }
  
  void setPeripheralMidiState(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (!this.mHasMidiFeature) {
      return;
    }
    if ((paramBoolean) && (this.mPeripheralMidiDevice == null))
    {
      localBundle = new Bundle();
      localResources = this.mContext.getResources();
      localBundle.putString("name", localResources.getString(17040872));
      localBundle.putString("manufacturer", localResources.getString(17040873));
      localBundle.putString("product", localResources.getString(17040874));
      localBundle.putInt("alsa_card", paramInt1);
      localBundle.putInt("alsa_device", paramInt2);
      this.mPeripheralMidiDevice = UsbMidiDevice.create(this.mContext, localBundle, paramInt1, paramInt2);
    }
    while ((paramBoolean) || (this.mPeripheralMidiDevice == null))
    {
      Bundle localBundle;
      Resources localResources;
      return;
    }
    IoUtils.closeQuietly(this.mPeripheralMidiDevice);
    this.mPeripheralMidiDevice = null;
  }
  
  public void systemReady()
  {
    this.mAudioService = IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
    this.mAlsaObserver.startWatching();
    File[] arrayOfFile = new File("/dev/snd/").listFiles();
    if (arrayOfFile != null)
    {
      int i = 0;
      while (i < arrayOfFile.length)
      {
        alsaFileAdded(arrayOfFile[i].getName());
        i += 1;
      }
    }
  }
  
  void usbDeviceAdded(UsbDevice paramUsbDevice)
  {
    int j = 0;
    int k = paramUsbDevice.getInterfaceCount();
    int i = 0;
    while ((j == 0) && (i < k))
    {
      if (paramUsbDevice.getInterface(i).getInterfaceClass() == 1) {
        j = 1;
      }
      i += 1;
    }
    if (j == 0) {
      return;
    }
    i = this.mCardsParser.getDefaultUsbCard();
    Object localObject;
    AlsaDevice localAlsaDevice;
    Bundle localBundle;
    String str1;
    String str2;
    String str3;
    if (this.mCardsParser.isCardUsb(i))
    {
      localObject = selectAudioCard(i);
      if (localObject != null)
      {
        this.mAudioDevices.put(paramUsbDevice, localObject);
        Slog.i(TAG, "USB Audio Device Added: " + localObject);
      }
      if ((this.mDevicesParser.hasMIDIDevices(i)) && (this.mHasMidiFeature))
      {
        localAlsaDevice = waitForAlsaDevice(i, this.mDevicesParser.getDefaultDeviceNum(i), 3);
        if (localAlsaDevice != null)
        {
          localBundle = new Bundle();
          str1 = paramUsbDevice.getManufacturerName();
          str2 = paramUsbDevice.getProductName();
          str3 = paramUsbDevice.getVersion();
          if ((str1 != null) && (!str1.isEmpty())) {
            break label324;
          }
          localObject = str2;
        }
      }
    }
    for (;;)
    {
      localBundle.putString("name", (String)localObject);
      localBundle.putString("manufacturer", str1);
      localBundle.putString("product", str2);
      localBundle.putString("version", str3);
      localBundle.putString("serial_number", paramUsbDevice.getSerialNumber());
      localBundle.putInt("alsa_card", localAlsaDevice.mCard);
      localBundle.putInt("alsa_device", localAlsaDevice.mDevice);
      localBundle.putParcelable("usb_device", paramUsbDevice);
      localObject = UsbMidiDevice.create(this.mContext, localBundle, localAlsaDevice.mCard, localAlsaDevice.mDevice);
      if (localObject != null) {
        this.mMidiDevices.put(paramUsbDevice, localObject);
      }
      return;
      label324:
      if ((str2 == null) || (str2.isEmpty())) {
        localObject = str1;
      } else {
        localObject = str1 + " " + str2;
      }
    }
  }
  
  void usbDeviceRemoved(UsbDevice paramUsbDevice)
  {
    UsbAudioDevice localUsbAudioDevice = (UsbAudioDevice)this.mAudioDevices.remove(paramUsbDevice);
    Slog.i(TAG, "USB Audio Device Removed: " + localUsbAudioDevice);
    if ((localUsbAudioDevice != null) && ((localUsbAudioDevice.mHasPlayback) || (localUsbAudioDevice.mHasCapture)))
    {
      notifyDeviceState(localUsbAudioDevice, false);
      selectDefaultDevice();
    }
    paramUsbDevice = (UsbMidiDevice)this.mMidiDevices.remove(paramUsbDevice);
    if (paramUsbDevice != null) {
      IoUtils.closeQuietly(paramUsbDevice);
    }
  }
  
  private final class AlsaDevice
  {
    public static final int TYPE_CAPTURE = 2;
    public static final int TYPE_MIDI = 3;
    public static final int TYPE_PLAYBACK = 1;
    public static final int TYPE_UNKNOWN = 0;
    public int mCard;
    public int mDevice;
    public int mType;
    
    public AlsaDevice(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mType = paramInt1;
      this.mCard = paramInt2;
      this.mDevice = paramInt3;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!(paramObject instanceof AlsaDevice)) {
        return false;
      }
      paramObject = (AlsaDevice)paramObject;
      boolean bool1 = bool2;
      if (this.mType == ((AlsaDevice)paramObject).mType)
      {
        bool1 = bool2;
        if (this.mCard == ((AlsaDevice)paramObject).mCard)
        {
          bool1 = bool2;
          if (this.mDevice == ((AlsaDevice)paramObject).mDevice) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("AlsaDevice: [card: ").append(this.mCard);
      localStringBuilder.append(", device: ").append(this.mDevice);
      localStringBuilder.append(", type: ").append(this.mType);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usb/UsbAlsaManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */