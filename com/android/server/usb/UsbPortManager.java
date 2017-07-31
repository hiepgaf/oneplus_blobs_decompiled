package com.android.server.usb;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbPort;
import android.hardware.usb.UsbPortStatus;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UEventObserver;
import android.os.UEventObserver.UEvent;
import android.os.UserHandle;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.FgThread;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import libcore.io.IoUtils;

public class UsbPortManager
{
  private static final int COMBO_SINK_DEVICE = UsbPort.combineRolesAsBit(2, 2);
  private static final int COMBO_SINK_HOST;
  private static final int COMBO_SOURCE_DEVICE;
  private static final int COMBO_SOURCE_HOST = UsbPort.combineRolesAsBit(1, 1);
  private static final int MSG_UPDATE_PORTS = 1;
  private static final String PORT_DATA_ROLE_DEVICE = "device";
  private static final String PORT_DATA_ROLE_HOST = "host";
  private static final String PORT_MODE_DFP = "dfp";
  private static final String PORT_MODE_UFP = "ufp";
  private static final String PORT_POWER_ROLE_SINK = "sink";
  private static final String PORT_POWER_ROLE_SOURCE = "source";
  private static final String SYSFS_CLASS = "/sys/class/dual_role_usb";
  private static final String SYSFS_PORT_DATA_ROLE = "data_role";
  private static final String SYSFS_PORT_MODE = "mode";
  private static final String SYSFS_PORT_POWER_ROLE = "power_role";
  private static final String SYSFS_PORT_SUPPORTED_MODES = "supported_modes";
  private static final String TAG = "UsbPortManager";
  private static final String UEVENT_FILTER = "SUBSYSTEM=dual_role_usb";
  private static final String USB_TYPEC_PROP_PREFIX = "sys.usb.typec.";
  private static final String USB_TYPEC_STATE = "sys.usb.typec.state";
  private final Context mContext;
  private final Handler mHandler = new Handler(FgThread.get().getLooper())
  {
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      default: 
        return;
      }
      synchronized (UsbPortManager.-get1(UsbPortManager.this))
      {
        UsbPortManager.-wrap1(UsbPortManager.this, null);
        return;
      }
    }
  };
  private final boolean mHaveKernelSupport;
  private final Object mLock = new Object();
  private final ArrayMap<String, PortInfo> mPorts = new ArrayMap();
  private final ArrayMap<String, SimulatedPortInfo> mSimulatedPorts = new ArrayMap();
  private final UEventObserver mUEventObserver = new UEventObserver()
  {
    public void onUEvent(UEventObserver.UEvent paramAnonymousUEvent)
    {
      UsbPortManager.-wrap0(UsbPortManager.this);
    }
  };
  
  static
  {
    COMBO_SOURCE_DEVICE = UsbPort.combineRolesAsBit(1, 2);
    COMBO_SINK_HOST = UsbPort.combineRolesAsBit(2, 1);
  }
  
  public UsbPortManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHaveKernelSupport = new File("/sys/class/dual_role_usb").exists();
  }
  
  private void addOrUpdatePortLocked(String paramString, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, int paramInt4, boolean paramBoolean3, IndentingPrintWriter paramIndentingPrintWriter)
  {
    int i = paramInt2;
    if (paramInt1 != 3)
    {
      boolean bool = false;
      i = paramInt2;
      paramBoolean1 = bool;
      if (paramInt2 != 0)
      {
        i = paramInt2;
        paramBoolean1 = bool;
        if (paramInt2 != paramInt1)
        {
          logAndPrint(5, paramIndentingPrintWriter, "Ignoring inconsistent current mode from USB port driver: supportedModes=" + UsbPort.modeToString(paramInt1) + ", currentMode=" + UsbPort.modeToString(paramInt2));
          i = 0;
          paramBoolean1 = bool;
        }
      }
    }
    int j = UsbPort.combineRolesAsBit(paramInt3, paramInt4);
    paramInt2 = j;
    if (i != 0)
    {
      paramInt2 = j;
      if (paramInt3 != 0)
      {
        paramInt2 = j;
        if (paramInt4 != 0)
        {
          if ((!paramBoolean2) || (!paramBoolean3)) {
            break label203;
          }
          paramInt2 = j | COMBO_SOURCE_HOST | COMBO_SOURCE_DEVICE | COMBO_SINK_HOST | COMBO_SINK_DEVICE;
        }
      }
    }
    PortInfo localPortInfo;
    for (;;)
    {
      localPortInfo = (PortInfo)this.mPorts.get(paramString);
      if (localPortInfo != null) {
        break;
      }
      paramIndentingPrintWriter = new PortInfo(paramString, paramInt1);
      paramIndentingPrintWriter.setStatus(i, paramBoolean1, paramInt3, paramBoolean2, paramInt4, paramBoolean3, paramInt2);
      this.mPorts.put(paramString, paramIndentingPrintWriter);
      return;
      label203:
      if (paramBoolean2)
      {
        paramInt2 = j | UsbPort.combineRolesAsBit(1, paramInt4) | UsbPort.combineRolesAsBit(2, paramInt4);
      }
      else if (paramBoolean3)
      {
        paramInt2 = j | UsbPort.combineRolesAsBit(paramInt3, 1) | UsbPort.combineRolesAsBit(paramInt3, 2);
      }
      else
      {
        paramInt2 = j;
        if (paramBoolean1) {
          paramInt2 = j | COMBO_SOURCE_HOST | COMBO_SINK_DEVICE;
        }
      }
    }
    if (paramInt1 != localPortInfo.mUsbPort.getSupportedModes()) {
      logAndPrint(5, paramIndentingPrintWriter, "Ignoring inconsistent list of supported modes from USB port driver (should be immutable): previous=" + UsbPort.modeToString(localPortInfo.mUsbPort.getSupportedModes()) + ", current=" + UsbPort.modeToString(paramInt1));
    }
    if (localPortInfo.setStatus(i, paramBoolean1, paramInt3, paramBoolean2, paramInt4, paramBoolean3, paramInt2))
    {
      localPortInfo.mDisposition = 1;
      return;
    }
    localPortInfo.mDisposition = 2;
  }
  
  private static boolean canChangeDataRole(File paramFile)
  {
    return fileIsRootWritable(new File(paramFile, "data_role").getPath());
  }
  
  private static boolean canChangeMode(File paramFile)
  {
    return fileIsRootWritable(new File(paramFile, "mode").getPath());
  }
  
  private static boolean canChangePowerRole(File paramFile)
  {
    return fileIsRootWritable(new File(paramFile, "power_role").getPath());
  }
  
  private static boolean fileIsRootWritable(String paramString)
  {
    boolean bool = false;
    try
    {
      int i = Os.stat(paramString).st_mode;
      int j = OsConstants.S_IWUSR;
      if ((i & j) != 0) {
        bool = true;
      }
      return bool;
    }
    catch (ErrnoException paramString) {}
    return false;
  }
  
  private void handlePortAddedLocked(PortInfo paramPortInfo, IndentingPrintWriter paramIndentingPrintWriter)
  {
    logAndPrint(4, paramIndentingPrintWriter, "USB port added: " + paramPortInfo);
    sendPortChangedBroadcastLocked(paramPortInfo);
  }
  
  private void handlePortChangedLocked(PortInfo paramPortInfo, IndentingPrintWriter paramIndentingPrintWriter)
  {
    logAndPrint(4, paramIndentingPrintWriter, "USB port changed: " + paramPortInfo);
    sendPortChangedBroadcastLocked(paramPortInfo);
  }
  
  private void handlePortRemovedLocked(PortInfo paramPortInfo, IndentingPrintWriter paramIndentingPrintWriter)
  {
    logAndPrint(4, paramIndentingPrintWriter, "USB port removed: " + paramPortInfo);
    sendPortChangedBroadcastLocked(paramPortInfo);
  }
  
  private static void logAndPrint(int paramInt, IndentingPrintWriter paramIndentingPrintWriter, String paramString)
  {
    Slog.println(paramInt, "UsbPortManager", paramString);
    if (paramIndentingPrintWriter != null) {
      paramIndentingPrintWriter.println(paramString);
    }
  }
  
  private static String propertyFromFilename(String paramString)
  {
    return "sys.usb.typec." + paramString;
  }
  
  private static int readCurrentDataRole(File paramFile)
  {
    paramFile = readFile(paramFile, "data_role");
    if (paramFile != null)
    {
      if (paramFile.equals("host")) {
        return 1;
      }
      if (paramFile.equals("device")) {
        return 2;
      }
    }
    return 0;
  }
  
  private static int readCurrentMode(File paramFile)
  {
    paramFile = readFile(paramFile, "mode");
    if (paramFile != null)
    {
      if (paramFile.equals("dfp")) {
        return 1;
      }
      if (paramFile.equals("ufp")) {
        return 2;
      }
    }
    return 0;
  }
  
  private static int readCurrentPowerRole(File paramFile)
  {
    paramFile = readFile(paramFile, "power_role");
    if (paramFile != null)
    {
      if (paramFile.equals("source")) {
        return 1;
      }
      if (paramFile.equals("sink")) {
        return 2;
      }
    }
    return 0;
  }
  
  private static String readFile(File paramFile, String paramString)
  {
    paramFile = new File(paramFile, paramString);
    try
    {
      paramFile = IoUtils.readFileAsString(paramFile.getAbsolutePath()).trim();
      return paramFile;
    }
    catch (IOException paramFile) {}
    return null;
  }
  
  private static int readSupportedModes(File paramFile)
  {
    int i = 0;
    int j = 0;
    paramFile = readFile(paramFile, "supported_modes");
    if (paramFile != null)
    {
      if (paramFile.contains("dfp")) {
        j = 1;
      }
      i = j;
      if (paramFile.contains("ufp")) {
        i = j | 0x2;
      }
    }
    return i;
  }
  
  private void scheduleUpdatePorts()
  {
    if (!this.mHandler.hasMessages(1)) {
      this.mHandler.sendEmptyMessage(1);
    }
  }
  
  private void sendPortChangedBroadcastLocked(PortInfo paramPortInfo)
  {
    final Intent localIntent = new Intent("android.hardware.usb.action.USB_PORT_CHANGED");
    localIntent.addFlags(268435456);
    localIntent.putExtra("port", paramPortInfo.mUsbPort);
    localIntent.putExtra("portStatus", paramPortInfo.mUsbPortStatus);
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        UsbPortManager.-get0(UsbPortManager.this).sendBroadcastAsUser(localIntent, UserHandle.ALL);
      }
    });
  }
  
  private void updatePortsLocked(IndentingPrintWriter paramIndentingPrintWriter)
  {
    for (int i = this.mPorts.size();; i = j)
    {
      j = i - 1;
      if (i <= 0) {
        break;
      }
      ((PortInfo)this.mPorts.valueAt(j)).mDisposition = 3;
    }
    Object localObject;
    if (!this.mSimulatedPorts.isEmpty())
    {
      j = this.mSimulatedPorts.size();
      i = 0;
      while (i < j)
      {
        localObject = (SimulatedPortInfo)this.mSimulatedPorts.valueAt(i);
        addOrUpdatePortLocked(((SimulatedPortInfo)localObject).mPortId, ((SimulatedPortInfo)localObject).mSupportedModes, ((SimulatedPortInfo)localObject).mCurrentMode, ((SimulatedPortInfo)localObject).mCanChangeMode, ((SimulatedPortInfo)localObject).mCurrentPowerRole, ((SimulatedPortInfo)localObject).mCanChangePowerRole, ((SimulatedPortInfo)localObject).mCurrentDataRole, ((SimulatedPortInfo)localObject).mCanChangeDataRole, paramIndentingPrintWriter);
        i += 1;
      }
    }
    if (this.mHaveKernelSupport)
    {
      localObject = new File("/sys/class/dual_role_usb").listFiles();
      if (localObject != null)
      {
        j = localObject.length;
        i = 0;
        if (i < j)
        {
          File localFile = localObject[i];
          if (!localFile.isDirectory()) {}
          for (;;)
          {
            i += 1;
            break;
            addOrUpdatePortLocked(localFile.getName(), readSupportedModes(localFile), readCurrentMode(localFile), canChangeMode(localFile), readCurrentPowerRole(localFile), canChangePowerRole(localFile), readCurrentDataRole(localFile), canChangeDataRole(localFile), paramIndentingPrintWriter);
          }
        }
      }
    }
    i = this.mPorts.size();
    int j = i - 1;
    if (i > 0)
    {
      localObject = (PortInfo)this.mPorts.valueAt(j);
      switch (((PortInfo)localObject).mDisposition)
      {
      }
      for (;;)
      {
        i = j;
        break;
        handlePortAddedLocked((PortInfo)localObject, paramIndentingPrintWriter);
        ((PortInfo)localObject).mDisposition = 2;
        continue;
        handlePortChangedLocked((PortInfo)localObject, paramIndentingPrintWriter);
        ((PortInfo)localObject).mDisposition = 2;
        continue;
        this.mPorts.removeAt(j);
        ((PortInfo)localObject).mUsbPortStatus = null;
        handlePortRemovedLocked((PortInfo)localObject, paramIndentingPrintWriter);
      }
    }
  }
  
  private static boolean waitForState(String paramString1, String paramString2)
  {
    String str = null;
    int i = 0;
    while (i < 100)
    {
      str = SystemProperties.get(paramString1);
      if (paramString2.equals(str)) {
        return true;
      }
      SystemClock.sleep(50L);
      i += 1;
    }
    Slog.e("UsbPortManager", "waitForState(" + paramString2 + ") for " + paramString1 + " FAILED: got " + str);
    return false;
  }
  
  private static boolean writeFile(File paramFile, String paramString1, String paramString2)
  {
    SystemProperties.set(propertyFromFilename(paramString1), paramString2);
    return waitForState("sys.usb.typec.state", paramString2);
  }
  
  public void addSimulatedPort(String paramString, int paramInt, IndentingPrintWriter paramIndentingPrintWriter)
  {
    synchronized (this.mLock)
    {
      if (this.mSimulatedPorts.containsKey(paramString))
      {
        paramIndentingPrintWriter.println("Port with same name already exists.  Please remove it first.");
        return;
      }
      paramIndentingPrintWriter.println("Adding simulated port: portId=" + paramString + ", supportedModes=" + UsbPort.modeToString(paramInt));
      this.mSimulatedPorts.put(paramString, new SimulatedPortInfo(paramString, paramInt));
      updatePortsLocked(paramIndentingPrintWriter);
      return;
    }
  }
  
  public void connectSimulatedPort(String paramString, int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, int paramInt3, boolean paramBoolean3, IndentingPrintWriter paramIndentingPrintWriter)
  {
    synchronized (this.mLock)
    {
      SimulatedPortInfo localSimulatedPortInfo = (SimulatedPortInfo)this.mSimulatedPorts.get(paramString);
      if (localSimulatedPortInfo == null)
      {
        paramIndentingPrintWriter.println("Cannot connect simulated port which does not exist.");
        return;
      }
      if ((paramInt1 == 0) || (paramInt2 == 0)) {}
      while (paramInt3 == 0)
      {
        paramIndentingPrintWriter.println("Cannot connect simulated port in null mode, power role, or data role.");
        return;
      }
      if ((localSimulatedPortInfo.mSupportedModes & paramInt1) == 0)
      {
        paramIndentingPrintWriter.println("Simulated port does not support mode: " + UsbPort.modeToString(paramInt1));
        return;
      }
      paramIndentingPrintWriter.println("Connecting simulated port: portId=" + paramString + ", mode=" + UsbPort.modeToString(paramInt1) + ", canChangeMode=" + paramBoolean1 + ", powerRole=" + UsbPort.powerRoleToString(paramInt2) + ", canChangePowerRole=" + paramBoolean2 + ", dataRole=" + UsbPort.dataRoleToString(paramInt3) + ", canChangeDataRole=" + paramBoolean3);
      localSimulatedPortInfo.mCurrentMode = paramInt1;
      localSimulatedPortInfo.mCanChangeMode = paramBoolean1;
      localSimulatedPortInfo.mCurrentPowerRole = paramInt2;
      localSimulatedPortInfo.mCanChangePowerRole = paramBoolean2;
      localSimulatedPortInfo.mCurrentDataRole = paramInt3;
      localSimulatedPortInfo.mCanChangeDataRole = paramBoolean3;
      updatePortsLocked(paramIndentingPrintWriter);
      return;
    }
  }
  
  public void disconnectSimulatedPort(String paramString, IndentingPrintWriter paramIndentingPrintWriter)
  {
    synchronized (this.mLock)
    {
      SimulatedPortInfo localSimulatedPortInfo = (SimulatedPortInfo)this.mSimulatedPorts.get(paramString);
      if (localSimulatedPortInfo == null)
      {
        paramIndentingPrintWriter.println("Cannot disconnect simulated port which does not exist.");
        return;
      }
      paramIndentingPrintWriter.println("Disconnecting simulated port: portId=" + paramString);
      localSimulatedPortInfo.mCurrentMode = 0;
      localSimulatedPortInfo.mCanChangeMode = false;
      localSimulatedPortInfo.mCurrentPowerRole = 0;
      localSimulatedPortInfo.mCanChangePowerRole = false;
      localSimulatedPortInfo.mCurrentDataRole = 0;
      localSimulatedPortInfo.mCanChangeDataRole = false;
      updatePortsLocked(paramIndentingPrintWriter);
      return;
    }
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    synchronized (this.mLock)
    {
      paramIndentingPrintWriter.print("USB Port State:");
      if (!this.mSimulatedPorts.isEmpty()) {
        paramIndentingPrintWriter.print(" (simulation active; end with 'dumpsys usb reset')");
      }
      paramIndentingPrintWriter.println();
      if (this.mPorts.isEmpty()) {
        paramIndentingPrintWriter.println("  <no ports>");
      }
      Iterator localIterator;
      do
      {
        return;
        localIterator = this.mPorts.values().iterator();
      } while (!localIterator.hasNext());
      PortInfo localPortInfo = (PortInfo)localIterator.next();
      paramIndentingPrintWriter.println("  " + localPortInfo.mUsbPort.getId() + ": " + localPortInfo);
    }
  }
  
  public UsbPortStatus getPortStatus(String paramString)
  {
    Object localObject1 = null;
    synchronized (this.mLock)
    {
      PortInfo localPortInfo = (PortInfo)this.mPorts.get(paramString);
      paramString = (String)localObject1;
      if (localPortInfo != null) {
        paramString = localPortInfo.mUsbPortStatus;
      }
      return paramString;
    }
  }
  
  public UsbPort[] getPorts()
  {
    synchronized (this.mLock)
    {
      int j = this.mPorts.size();
      UsbPort[] arrayOfUsbPort = new UsbPort[j];
      int i = 0;
      while (i < j)
      {
        arrayOfUsbPort[i] = ((PortInfo)this.mPorts.valueAt(i)).mUsbPort;
        i += 1;
      }
      return arrayOfUsbPort;
    }
  }
  
  public void removeSimulatedPort(String paramString, IndentingPrintWriter paramIndentingPrintWriter)
  {
    synchronized (this.mLock)
    {
      int i = this.mSimulatedPorts.indexOfKey(paramString);
      if (i < 0)
      {
        paramIndentingPrintWriter.println("Cannot remove simulated port which does not exist.");
        return;
      }
      paramIndentingPrintWriter.println("Disconnecting simulated port: portId=" + paramString);
      this.mSimulatedPorts.removeAt(i);
      updatePortsLocked(paramIndentingPrintWriter);
      return;
    }
  }
  
  public void resetSimulation(IndentingPrintWriter paramIndentingPrintWriter)
  {
    synchronized (this.mLock)
    {
      paramIndentingPrintWriter.println("Removing all simulated ports and ending simulation.");
      if (!this.mSimulatedPorts.isEmpty())
      {
        this.mSimulatedPorts.clear();
        updatePortsLocked(paramIndentingPrintWriter);
      }
      return;
    }
  }
  
  public void setPortRoles(String paramString, int paramInt1, int paramInt2, IndentingPrintWriter paramIndentingPrintWriter)
  {
    for (;;)
    {
      Object localObject1;
      int k;
      int m;
      boolean bool1;
      int i;
      synchronized (this.mLock)
      {
        localObject1 = (PortInfo)this.mPorts.get(paramString);
        if (localObject1 == null)
        {
          if (paramIndentingPrintWriter != null) {
            paramIndentingPrintWriter.println("No such USB port: " + paramString);
          }
          return;
        }
        if (!((PortInfo)localObject1).mUsbPortStatus.isRoleCombinationSupported(paramInt1, paramInt2))
        {
          logAndPrint(6, paramIndentingPrintWriter, "Attempted to set USB port into unsupported role combination: portId=" + paramString + ", newPowerRole=" + UsbPort.powerRoleToString(paramInt1) + ", newDataRole=" + UsbPort.dataRoleToString(paramInt2));
          return;
        }
        k = ((PortInfo)localObject1).mUsbPortStatus.getCurrentDataRole();
        m = ((PortInfo)localObject1).mUsbPortStatus.getCurrentPowerRole();
        if ((k == paramInt2) && (m == paramInt1))
        {
          if (paramIndentingPrintWriter != null) {
            paramIndentingPrintWriter.println("No change.");
          }
          return;
        }
        bool1 = ((PortInfo)localObject1).mCanChangeMode;
        boolean bool2 = ((PortInfo)localObject1).mCanChangePowerRole;
        boolean bool3 = ((PortInfo)localObject1).mCanChangeDataRole;
        int j = ((PortInfo)localObject1).mUsbPortStatus.getCurrentMode();
        if ((!bool2) && (m != paramInt1))
        {
          break label747;
          logAndPrint(4, paramIndentingPrintWriter, "Setting USB port mode and role: portId=" + paramString + ", currentMode=" + UsbPort.modeToString(j) + ", currentPowerRole=" + UsbPort.powerRoleToString(m) + ", currentDataRole=" + UsbPort.dataRoleToString(k) + ", newMode=" + UsbPort.modeToString(i) + ", newPowerRole=" + UsbPort.powerRoleToString(paramInt1) + ", newDataRole=" + UsbPort.dataRoleToString(paramInt2));
          localObject1 = (SimulatedPortInfo)this.mSimulatedPorts.get(paramString);
          if (localObject1 != null)
          {
            ((SimulatedPortInfo)localObject1).mCurrentMode = i;
            ((SimulatedPortInfo)localObject1).mCurrentPowerRole = paramInt1;
            ((SimulatedPortInfo)localObject1).mCurrentDataRole = paramInt2;
            updatePortsLocked(paramIndentingPrintWriter);
          }
        }
        else
        {
          if ((!bool3) && (k != paramInt2)) {
            break label747;
          }
          i = j;
          continue;
          if ((bool1) && (paramInt1 == 2) && (paramInt2 == 2))
          {
            i = 2;
            continue;
          }
          logAndPrint(6, paramIndentingPrintWriter, "Found mismatch in supported USB role combinations while attempting to change role: " + localObject1 + ", newPowerRole=" + UsbPort.powerRoleToString(paramInt1) + ", newDataRole=" + UsbPort.dataRoleToString(paramInt2));
          return;
        }
        if (!this.mHaveKernelSupport) {
          continue;
        }
        File localFile = new File("/sys/class/dual_role_usb", paramString);
        if (!localFile.exists())
        {
          logAndPrint(6, paramIndentingPrintWriter, "USB port not found: portId=" + paramString);
          return;
        }
        if (j == i) {
          break label768;
        }
        if (i == 1)
        {
          localObject1 = "dfp";
          if (!writeFile(localFile, "mode", (String)localObject1)) {
            logAndPrint(6, paramIndentingPrintWriter, "Failed to set the USB port mode: portId=" + paramString + ", newMode=" + UsbPort.modeToString(i));
          }
        }
        else
        {
          localObject1 = "ufp";
          continue;
        }
        continue;
        if (writeFile(localFile, "power_role", (String)localObject1)) {
          break label786;
        }
        logAndPrint(6, paramIndentingPrintWriter, "Failed to set the USB port power role: portId=" + paramString + ", newPowerRole=" + UsbPort.powerRoleToString(paramInt1));
        return;
        localObject1 = "sink";
        continue;
        if (writeFile(localFile, "data_role", (String)localObject1)) {
          continue;
        }
        logAndPrint(6, paramIndentingPrintWriter, "Failed to set the USB port data role: portId=" + paramString + ", newDataRole=" + UsbPort.dataRoleToString(paramInt2));
        return;
        localObject1 = "device";
      }
      label747:
      if ((bool1) && (paramInt1 == 1) && (paramInt2 == 1))
      {
        i = 1;
        continue;
        label768:
        if (m != paramInt1)
        {
          if (paramInt1 == 1) {
            localObject1 = "source";
          }
        }
        else {
          label786:
          if (k != paramInt2) {
            if (paramInt2 == 1) {
              localObject1 = "host";
            }
          }
        }
      }
    }
  }
  
  public void systemReady()
  {
    this.mUEventObserver.startObserving("SUBSYSTEM=dual_role_usb");
    scheduleUpdatePorts();
  }
  
  private static final class PortInfo
  {
    public static final int DISPOSITION_ADDED = 0;
    public static final int DISPOSITION_CHANGED = 1;
    public static final int DISPOSITION_READY = 2;
    public static final int DISPOSITION_REMOVED = 3;
    public boolean mCanChangeDataRole;
    public boolean mCanChangeMode;
    public boolean mCanChangePowerRole;
    public int mDisposition;
    public final UsbPort mUsbPort;
    public UsbPortStatus mUsbPortStatus;
    
    public PortInfo(String paramString, int paramInt)
    {
      this.mUsbPort = new UsbPort(paramString, paramInt);
    }
    
    public boolean setStatus(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, int paramInt3, boolean paramBoolean3, int paramInt4)
    {
      this.mCanChangeMode = paramBoolean1;
      this.mCanChangePowerRole = paramBoolean2;
      this.mCanChangeDataRole = paramBoolean3;
      if ((this.mUsbPortStatus == null) || (this.mUsbPortStatus.getCurrentMode() != paramInt1)) {}
      while ((this.mUsbPortStatus.getCurrentPowerRole() != paramInt2) || (this.mUsbPortStatus.getCurrentDataRole() != paramInt3) || (this.mUsbPortStatus.getSupportedRoleCombinations() != paramInt4))
      {
        this.mUsbPortStatus = new UsbPortStatus(paramInt1, paramInt2, paramInt3, paramInt4);
        return true;
      }
      return false;
    }
    
    public String toString()
    {
      return "port=" + this.mUsbPort + ", status=" + this.mUsbPortStatus + ", canChangeMode=" + this.mCanChangeMode + ", canChangePowerRole=" + this.mCanChangePowerRole + ", canChangeDataRole=" + this.mCanChangeDataRole;
    }
  }
  
  private static final class SimulatedPortInfo
  {
    public boolean mCanChangeDataRole;
    public boolean mCanChangeMode;
    public boolean mCanChangePowerRole;
    public int mCurrentDataRole;
    public int mCurrentMode;
    public int mCurrentPowerRole;
    public final String mPortId;
    public final int mSupportedModes;
    
    public SimulatedPortInfo(String paramString, int paramInt)
    {
      this.mPortId = paramString;
      this.mSupportedModes = paramInt;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usb/UsbPortManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */