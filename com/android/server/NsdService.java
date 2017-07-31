package com.android.server;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.nsd.DnsSdTxtRecord;
import android.net.nsd.INsdManager.Stub;
import android.net.nsd.NsdServiceInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.util.Base64;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

public class NsdService
  extends INsdManager.Stub
{
  private static final int BASE = 393216;
  private static final int CMD_TO_STRING_COUNT = 19;
  private static final boolean DBG = false;
  private static final String MDNS_TAG = "mDnsConnector";
  private static final String TAG = "NsdService";
  private static String[] sCmdToString = new String[19];
  private int INVALID_ID = 0;
  private HashMap<Messenger, ClientInfo> mClients = new HashMap();
  private ContentResolver mContentResolver;
  private Context mContext;
  private SparseArray<ClientInfo> mIdToClientInfoMap = new SparseArray();
  private NativeDaemonConnector mNativeConnector;
  private final CountDownLatch mNativeDaemonConnected = new CountDownLatch(1);
  private NsdStateMachine mNsdStateMachine;
  private AsyncChannel mReplyChannel = new AsyncChannel();
  private int mUniqueId = 1;
  
  static
  {
    sCmdToString[1] = "DISCOVER";
    sCmdToString[6] = "STOP-DISCOVER";
    sCmdToString[9] = "REGISTER";
    sCmdToString[12] = "UNREGISTER";
    sCmdToString[18] = "RESOLVE";
  }
  
  private NsdService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mContentResolver = paramContext.getContentResolver();
    this.mNativeConnector = new NativeDaemonConnector(new NativeCallbackReceiver(), "mdns", 10, "mDnsConnector", 25, null);
    this.mNsdStateMachine = new NsdStateMachine("NsdService");
    this.mNsdStateMachine.start();
    new Thread(this.mNativeConnector, "mDnsConnector").start();
  }
  
  private static String cmdToString(int paramInt)
  {
    paramInt -= 393216;
    if ((paramInt >= 0) && (paramInt < sCmdToString.length)) {
      return sCmdToString[paramInt];
    }
    return null;
  }
  
  public static NsdService create(Context paramContext)
    throws InterruptedException
  {
    paramContext = new NsdService(paramContext);
    paramContext.mNativeDaemonConnected.await();
    return paramContext;
  }
  
  private boolean discoverServices(int paramInt, String paramString)
  {
    try
    {
      this.mNativeConnector.execute("mdnssd", new Object[] { "discover", Integer.valueOf(paramInt), paramString });
      return true;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      Slog.e("NsdService", "Failed to discoverServices " + paramString);
    }
    return false;
  }
  
  private boolean getAddrInfo(int paramInt, String paramString)
  {
    try
    {
      this.mNativeConnector.execute("mdnssd", new Object[] { "getaddrinfo", Integer.valueOf(paramInt), paramString });
      return true;
    }
    catch (NativeDaemonConnectorException paramString)
    {
      Slog.e("NsdService", "Failed to getAddrInfo " + paramString);
    }
    return false;
  }
  
  private int getUniqueId()
  {
    int i = this.mUniqueId + 1;
    this.mUniqueId = i;
    if (i == this.INVALID_ID)
    {
      i = this.mUniqueId + 1;
      this.mUniqueId = i;
      return i;
    }
    return this.mUniqueId;
  }
  
  private boolean isNsdEnabled()
  {
    return Settings.Global.getInt(this.mContentResolver, "nsd_on", 1) == 1;
  }
  
  private Message obtainMessage(Message paramMessage)
  {
    Message localMessage = Message.obtain();
    localMessage.arg2 = paramMessage.arg2;
    return localMessage;
  }
  
  private boolean registerService(int paramInt, NsdServiceInfo paramNsdServiceInfo)
  {
    try
    {
      paramNsdServiceInfo = new NativeDaemonConnector.Command("mdnssd", new Object[] { "register", Integer.valueOf(paramInt), paramNsdServiceInfo.getServiceName(), paramNsdServiceInfo.getServiceType(), Integer.valueOf(paramNsdServiceInfo.getPort()), Base64.encodeToString(paramNsdServiceInfo.getTxtRecord(), 0).replace("\n", "") });
      this.mNativeConnector.execute(paramNsdServiceInfo);
      return true;
    }
    catch (NativeDaemonConnectorException paramNsdServiceInfo)
    {
      Slog.e("NsdService", "Failed to execute registerService " + paramNsdServiceInfo);
    }
    return false;
  }
  
  private void replyToMessage(Message paramMessage, int paramInt)
  {
    if (paramMessage.replyTo == null) {
      return;
    }
    Message localMessage = obtainMessage(paramMessage);
    localMessage.what = paramInt;
    this.mReplyChannel.replyToMessage(paramMessage, localMessage);
  }
  
  private void replyToMessage(Message paramMessage, int paramInt1, int paramInt2)
  {
    if (paramMessage.replyTo == null) {
      return;
    }
    Message localMessage = obtainMessage(paramMessage);
    localMessage.what = paramInt1;
    localMessage.arg1 = paramInt2;
    this.mReplyChannel.replyToMessage(paramMessage, localMessage);
  }
  
  private void replyToMessage(Message paramMessage, int paramInt, Object paramObject)
  {
    if (paramMessage.replyTo == null) {
      return;
    }
    Message localMessage = obtainMessage(paramMessage);
    localMessage.what = paramInt;
    localMessage.obj = paramObject;
    this.mReplyChannel.replyToMessage(paramMessage, localMessage);
  }
  
  private boolean resolveService(int paramInt, NsdServiceInfo paramNsdServiceInfo)
  {
    try
    {
      this.mNativeConnector.execute("mdnssd", new Object[] { "resolve", Integer.valueOf(paramInt), paramNsdServiceInfo.getServiceName(), paramNsdServiceInfo.getServiceType(), "local." });
      return true;
    }
    catch (NativeDaemonConnectorException paramNsdServiceInfo)
    {
      Slog.e("NsdService", "Failed to resolveService " + paramNsdServiceInfo);
    }
    return false;
  }
  
  private void sendNsdStateChangeBroadcast(boolean paramBoolean)
  {
    Intent localIntent = new Intent("android.net.nsd.STATE_CHANGED");
    localIntent.addFlags(67108864);
    if (paramBoolean) {
      localIntent.putExtra("nsd_state", 2);
    }
    for (;;)
    {
      this.mContext.sendStickyBroadcastAsUser(localIntent, UserHandle.ALL);
      return;
      localIntent.putExtra("nsd_state", 1);
    }
  }
  
  private boolean startMDnsDaemon()
  {
    try
    {
      this.mNativeConnector.execute("mdnssd", new Object[] { "start-service" });
      return true;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      Slog.e("NsdService", "Failed to start daemon" + localNativeDaemonConnectorException);
    }
    return false;
  }
  
  private boolean stopGetAddrInfo(int paramInt)
  {
    try
    {
      this.mNativeConnector.execute("mdnssd", new Object[] { "stop-getaddrinfo", Integer.valueOf(paramInt) });
      return true;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      Slog.e("NsdService", "Failed to stopGetAddrInfo " + localNativeDaemonConnectorException);
    }
    return false;
  }
  
  private boolean stopMDnsDaemon()
  {
    try
    {
      this.mNativeConnector.execute("mdnssd", new Object[] { "stop-service" });
      return true;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      Slog.e("NsdService", "Failed to start daemon" + localNativeDaemonConnectorException);
    }
    return false;
  }
  
  private boolean stopResolveService(int paramInt)
  {
    try
    {
      this.mNativeConnector.execute("mdnssd", new Object[] { "stop-resolve", Integer.valueOf(paramInt) });
      return true;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      Slog.e("NsdService", "Failed to stop resolve " + localNativeDaemonConnectorException);
    }
    return false;
  }
  
  private boolean stopServiceDiscovery(int paramInt)
  {
    try
    {
      this.mNativeConnector.execute("mdnssd", new Object[] { "stop-discover", Integer.valueOf(paramInt) });
      return true;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      Slog.e("NsdService", "Failed to stopServiceDiscovery " + localNativeDaemonConnectorException);
    }
    return false;
  }
  
  private String unescape(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
    int i;
    for (int j = 0;; j = i + 1)
    {
      char c2;
      if (j < paramString.length())
      {
        c2 = paramString.charAt(j);
        c1 = c2;
        i = j;
        if (c2 != '\\') {
          break label204;
        }
        j += 1;
        if (j < paramString.length()) {
          break label91;
        }
        Slog.e("NsdService", "Unexpected end of escape sequence in: " + paramString);
      }
      for (;;)
      {
        return localStringBuilder.toString();
        label91:
        c2 = paramString.charAt(j);
        c1 = c2;
        i = j;
        if (c2 == '.') {
          break label204;
        }
        c1 = c2;
        i = j;
        if (c2 == '\\') {
          break label204;
        }
        if (j + 2 < paramString.length()) {
          break;
        }
        Slog.e("NsdService", "Unexpected end of escape sequence in: " + paramString);
      }
      char c1 = (char)((c2 - '0') * 100 + (paramString.charAt(j + 1) - '0') * 10 + (paramString.charAt(j + 2) - '0'));
      i = j + 2;
      label204:
      localStringBuilder.append(c1);
    }
  }
  
  private boolean unregisterService(int paramInt)
  {
    try
    {
      this.mNativeConnector.execute("mdnssd", new Object[] { "stop-register", Integer.valueOf(paramInt) });
      return true;
    }
    catch (NativeDaemonConnectorException localNativeDaemonConnectorException)
    {
      Slog.e("NsdService", "Failed to execute unregisterService " + localNativeDaemonConnectorException);
    }
    return false;
  }
  
  private boolean updateService(int paramInt, DnsSdTxtRecord paramDnsSdTxtRecord)
  {
    if (paramDnsSdTxtRecord == null) {
      return false;
    }
    try
    {
      this.mNativeConnector.execute("mdnssd", new Object[] { "update", Integer.valueOf(paramInt), Integer.valueOf(paramDnsSdTxtRecord.size()), paramDnsSdTxtRecord.getRawData() });
      return true;
    }
    catch (NativeDaemonConnectorException paramDnsSdTxtRecord)
    {
      Slog.e("NsdService", "Failed to updateServices " + paramDnsSdTxtRecord);
    }
    return false;
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump ServiceDiscoverService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    Iterator localIterator = this.mClients.values().iterator();
    while (localIterator.hasNext())
    {
      ClientInfo localClientInfo = (ClientInfo)localIterator.next();
      paramPrintWriter.println("Client Info");
      paramPrintWriter.println(localClientInfo);
    }
    this.mNsdStateMachine.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  public Messenger getMessenger()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.INTERNET", "NsdService");
    return new Messenger(this.mNsdStateMachine.getHandler());
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NsdService");
    ContentResolver localContentResolver = this.mContentResolver;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "nsd_on", i);
      if (!paramBoolean) {
        break;
      }
      this.mNsdStateMachine.sendMessage(393240);
      return;
    }
    this.mNsdStateMachine.sendMessage(393241);
  }
  
  private class ClientInfo
  {
    private static final int MAX_LIMIT = 10;
    private final AsyncChannel mChannel;
    private SparseArray<Integer> mClientIds = new SparseArray();
    private SparseArray<Integer> mClientRequests = new SparseArray();
    private final Messenger mMessenger;
    private NsdServiceInfo mResolvedService;
    
    private ClientInfo(AsyncChannel paramAsyncChannel, Messenger paramMessenger)
    {
      this.mChannel = paramAsyncChannel;
      this.mMessenger = paramMessenger;
    }
    
    private void expungeAllRequests()
    {
      int i = 0;
      if (i < this.mClientIds.size())
      {
        int j = this.mClientIds.keyAt(i);
        int k = ((Integer)this.mClientIds.valueAt(i)).intValue();
        NsdService.-get2(NsdService.this).remove(k);
        switch (((Integer)this.mClientRequests.get(j)).intValue())
        {
        }
        for (;;)
        {
          i += 1;
          break;
          NsdService.-wrap9(NsdService.this, k);
          continue;
          NsdService.-wrap8(NsdService.this, k);
          continue;
          NsdService.-wrap10(NsdService.this, k);
        }
      }
      this.mClientIds.clear();
      this.mClientRequests.clear();
    }
    
    private int getClientId(int paramInt)
    {
      int i = 0;
      int j = this.mClientIds.size();
      while (i < j)
      {
        if (paramInt == ((Integer)this.mClientIds.valueAt(i)).intValue()) {
          return this.mClientIds.keyAt(i);
        }
        i += 1;
      }
      return -1;
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append("mChannel ").append(this.mChannel).append("\n");
      localStringBuffer.append("mMessenger ").append(this.mMessenger).append("\n");
      localStringBuffer.append("mResolvedService ").append(this.mResolvedService).append("\n");
      int i = 0;
      while (i < this.mClientIds.size())
      {
        int j = this.mClientIds.keyAt(i);
        localStringBuffer.append("clientId ").append(j).append(" mDnsId ").append(this.mClientIds.valueAt(i)).append(" type ").append(this.mClientRequests.get(j)).append("\n");
        i += 1;
      }
      return localStringBuffer.toString();
    }
  }
  
  class NativeCallbackReceiver
    implements INativeDaemonConnectorCallbacks
  {
    NativeCallbackReceiver() {}
    
    public boolean onCheckHoldWakeLock(int paramInt)
    {
      return false;
    }
    
    public void onDaemonConnected()
    {
      NsdService.-get3(NsdService.this).countDown();
    }
    
    public boolean onEvent(int paramInt, String paramString, String[] paramArrayOfString)
    {
      paramString = new NsdService.NativeEvent(NsdService.this, paramInt, paramString, paramArrayOfString);
      NsdService.-get4(NsdService.this).sendMessage(393242, paramString);
      return true;
    }
  }
  
  private class NativeEvent
  {
    final int code;
    final String[] cooked;
    final String raw;
    
    NativeEvent(int paramInt, String paramString, String[] paramArrayOfString)
    {
      this.code = paramInt;
      this.raw = paramString;
      this.cooked = paramArrayOfString;
    }
  }
  
  class NativeResponseCode
  {
    public static final int SERVICE_DISCOVERY_FAILED = 602;
    public static final int SERVICE_FOUND = 603;
    public static final int SERVICE_GET_ADDR_FAILED = 611;
    public static final int SERVICE_GET_ADDR_SUCCESS = 612;
    public static final int SERVICE_LOST = 604;
    public static final int SERVICE_REGISTERED = 606;
    public static final int SERVICE_REGISTRATION_FAILED = 605;
    public static final int SERVICE_RESOLUTION_FAILED = 607;
    public static final int SERVICE_RESOLVED = 608;
    public static final int SERVICE_UPDATED = 609;
    public static final int SERVICE_UPDATE_FAILED = 610;
    
    NativeResponseCode() {}
  }
  
  private class NsdStateMachine
    extends StateMachine
  {
    private final DefaultState mDefaultState = new DefaultState();
    private final DisabledState mDisabledState = new DisabledState();
    private final EnabledState mEnabledState = new EnabledState();
    
    NsdStateMachine(String paramString)
    {
      super();
      addState(this.mDefaultState);
      addState(this.mDisabledState, this.mDefaultState);
      addState(this.mEnabledState, this.mDefaultState);
      if (NsdService.-wrap2(NsdService.this)) {
        setInitialState(this.mEnabledState);
      }
      for (;;)
      {
        setLogRecSize(25);
        registerForNsdSetting();
        return;
        setInitialState(this.mDisabledState);
      }
    }
    
    private void registerForNsdSetting()
    {
      ContentObserver local1 = new ContentObserver(getHandler())
      {
        public void onChange(boolean paramAnonymousBoolean)
        {
          if (NsdService.-wrap2(NsdService.this))
          {
            NsdService.-get4(NsdService.this).sendMessage(393240);
            return;
          }
          NsdService.-get4(NsdService.this).sendMessage(393241);
        }
      };
      NsdService.-get1(NsdService.this).getContentResolver().registerContentObserver(Settings.Global.getUriFor("nsd_on"), false, local1);
    }
    
    protected String getWhatToString(int paramInt)
    {
      return NsdService.-wrap12(paramInt);
    }
    
    class DefaultState
      extends State
    {
      DefaultState() {}
      
      public boolean processMessage(Message paramMessage)
      {
        Object localObject;
        switch (paramMessage.what)
        {
        default: 
          Slog.e("NsdService", "Unhandled " + paramMessage);
          return false;
        case 69632: 
          if (paramMessage.arg1 == 0)
          {
            localObject = (AsyncChannel)paramMessage.obj;
            ((AsyncChannel)localObject).sendMessage(69634);
            localObject = new NsdService.ClientInfo(NsdService.this, (AsyncChannel)localObject, paramMessage.replyTo, null);
            NsdService.-get0(NsdService.this).put(paramMessage.replyTo, localObject);
          }
          break;
        }
        for (;;)
        {
          return true;
          Slog.e("NsdService", "Client connection failure, error=" + paramMessage.arg1);
          continue;
          switch (paramMessage.arg1)
          {
          }
          for (;;)
          {
            localObject = (NsdService.ClientInfo)NsdService.-get0(NsdService.this).get(paramMessage.replyTo);
            if (localObject != null)
            {
              NsdService.ClientInfo.-wrap1((NsdService.ClientInfo)localObject);
              NsdService.-get0(NsdService.this).remove(paramMessage.replyTo);
            }
            if (NsdService.-get0(NsdService.this).size() != 0) {
              break;
            }
            NsdService.-wrap7(NsdService.this);
            break;
            Slog.e("NsdService", "Send failed, client connection lost");
          }
          new AsyncChannel().connect(NsdService.-get1(NsdService.this), NsdService.NsdStateMachine.this.getHandler(), paramMessage.replyTo);
          continue;
          NsdService.-wrap15(NsdService.this, paramMessage, 393219, 0);
          continue;
          NsdService.-wrap15(NsdService.this, paramMessage, 393223, 0);
          continue;
          NsdService.-wrap15(NsdService.this, paramMessage, 393226, 0);
          continue;
          NsdService.-wrap15(NsdService.this, paramMessage, 393229, 0);
          continue;
          NsdService.-wrap15(NsdService.this, paramMessage, 393235, 0);
        }
      }
    }
    
    class DisabledState
      extends State
    {
      DisabledState() {}
      
      public void enter()
      {
        NsdService.-wrap17(NsdService.this, false);
      }
      
      public boolean processMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default: 
          return false;
        }
        NsdService.NsdStateMachine.-wrap0(NsdService.NsdStateMachine.this, NsdService.NsdStateMachine.-get1(NsdService.NsdStateMachine.this));
        return true;
      }
    }
    
    class EnabledState
      extends State
    {
      EnabledState() {}
      
      private boolean handleNativeEvent(int paramInt, String paramString, String[] paramArrayOfString)
      {
        boolean bool = true;
        int k = Integer.parseInt(paramArrayOfString[1]);
        NsdService.ClientInfo localClientInfo = (NsdService.ClientInfo)NsdService.-get2(NsdService.this).get(k);
        if (localClientInfo == null)
        {
          Slog.e("NsdService", "Unique id with no client mapping: " + k);
          return false;
        }
        int j = NsdService.ClientInfo.-wrap0(localClientInfo, k);
        if (j < 0)
        {
          Slog.d("NsdService", "Notification for a listener that is no longer active: " + k);
          return false;
        }
        switch (paramInt)
        {
        default: 
          bool = false;
        case 609: 
        case 610: 
          return bool;
        case 603: 
          paramString = new NsdServiceInfo(paramArrayOfString[2], paramArrayOfString[3]);
          NsdService.ClientInfo.-get0(localClientInfo).sendMessage(393220, 0, j, paramString);
          return true;
        case 604: 
          paramString = new NsdServiceInfo(paramArrayOfString[2], paramArrayOfString[3]);
          NsdService.ClientInfo.-get0(localClientInfo).sendMessage(393221, 0, j, paramString);
          return true;
        case 602: 
          NsdService.ClientInfo.-get0(localClientInfo).sendMessage(393219, 0, j);
          return true;
        case 606: 
          paramString = new NsdServiceInfo(paramArrayOfString[2], null);
          NsdService.ClientInfo.-get0(localClientInfo).sendMessage(393227, k, j, paramString);
          return true;
        case 605: 
          NsdService.ClientInfo.-get0(localClientInfo).sendMessage(393226, 0, j);
          return true;
        case 608: 
          int i;
          for (paramInt = 0; (paramInt < paramArrayOfString[2].length()) && (paramArrayOfString[2].charAt(paramInt) != '.'); paramInt = i + 1)
          {
            i = paramInt;
            if (paramArrayOfString[2].charAt(paramInt) == '\\') {
              i = paramInt + 1;
            }
          }
          if (paramInt >= paramArrayOfString[2].length())
          {
            Slog.e("NsdService", "Invalid service found " + paramString);
            return true;
          }
          String str = paramArrayOfString[2].substring(0, paramInt);
          paramString = paramArrayOfString[2].substring(paramInt).replace(".local.", "");
          str = NsdService.-wrap13(NsdService.this, str);
          NsdService.ClientInfo.-get3(localClientInfo).setServiceName(str);
          NsdService.ClientInfo.-get3(localClientInfo).setServiceType(paramString);
          NsdService.ClientInfo.-get3(localClientInfo).setPort(Integer.parseInt(paramArrayOfString[4]));
          NsdService.ClientInfo.-get3(localClientInfo).setTxtRecords(paramArrayOfString[6]);
          NsdService.-wrap8(NsdService.this, k);
          removeRequestMap(j, k, localClientInfo);
          paramInt = NsdService.-wrap11(NsdService.this);
          if (NsdService.-wrap1(NsdService.this, paramInt, paramArrayOfString[3]))
          {
            storeRequestMap(j, paramInt, localClientInfo, 393234);
            return true;
          }
          NsdService.ClientInfo.-get0(localClientInfo).sendMessage(393235, 0, j);
          NsdService.ClientInfo.-set0(localClientInfo, null);
          return true;
        case 607: 
          NsdService.-wrap8(NsdService.this, k);
          removeRequestMap(j, k, localClientInfo);
          NsdService.ClientInfo.-set0(localClientInfo, null);
          NsdService.ClientInfo.-get0(localClientInfo).sendMessage(393235, 0, j);
          return true;
        case 611: 
          NsdService.-wrap6(NsdService.this, k);
          removeRequestMap(j, k, localClientInfo);
          NsdService.ClientInfo.-set0(localClientInfo, null);
          NsdService.ClientInfo.-get0(localClientInfo).sendMessage(393235, 0, j);
          return true;
        }
        try
        {
          NsdService.ClientInfo.-get3(localClientInfo).setHost(InetAddress.getByName(paramArrayOfString[4]));
          NsdService.ClientInfo.-get0(localClientInfo).sendMessage(393236, 0, j, NsdService.ClientInfo.-get3(localClientInfo));
          NsdService.-wrap6(NsdService.this, k);
          removeRequestMap(j, k, localClientInfo);
          NsdService.ClientInfo.-set0(localClientInfo, null);
          return true;
        }
        catch (UnknownHostException paramString)
        {
          for (;;)
          {
            NsdService.ClientInfo.-get0(localClientInfo).sendMessage(393235, 0, j);
          }
        }
      }
      
      private void removeRequestMap(int paramInt1, int paramInt2, NsdService.ClientInfo paramClientInfo)
      {
        NsdService.ClientInfo.-get1(paramClientInfo).remove(paramInt1);
        NsdService.ClientInfo.-get2(paramClientInfo).remove(paramInt1);
        NsdService.-get2(NsdService.this).remove(paramInt2);
      }
      
      private boolean requestLimitReached(NsdService.ClientInfo paramClientInfo)
      {
        return NsdService.ClientInfo.-get1(paramClientInfo).size() >= 10;
      }
      
      private void storeRequestMap(int paramInt1, int paramInt2, NsdService.ClientInfo paramClientInfo, int paramInt3)
      {
        NsdService.ClientInfo.-get1(paramClientInfo).put(paramInt1, Integer.valueOf(paramInt2));
        NsdService.ClientInfo.-get2(paramClientInfo).put(paramInt1, Integer.valueOf(paramInt3));
        NsdService.-get2(NsdService.this).put(paramInt2, paramClientInfo);
      }
      
      public void enter()
      {
        NsdService.-wrap17(NsdService.this, true);
        if (NsdService.-get0(NsdService.this).size() > 0) {
          NsdService.-wrap5(NsdService.this);
        }
      }
      
      public void exit()
      {
        if (NsdService.-get0(NsdService.this).size() > 0) {
          NsdService.-wrap7(NsdService.this);
        }
      }
      
      public boolean processMessage(Message paramMessage)
      {
        boolean bool = true;
        switch (paramMessage.what)
        {
        default: 
          bool = false;
        }
        do
        {
          return bool;
          if ((paramMessage.arg1 == 0) && (NsdService.-get0(NsdService.this).size() == 0)) {
            NsdService.-wrap5(NsdService.this);
          }
          return false;
          return false;
          NsdService.NsdStateMachine.-wrap0(NsdService.NsdStateMachine.this, NsdService.NsdStateMachine.-get0(NsdService.NsdStateMachine.this));
          return true;
          Object localObject = (NsdServiceInfo)paramMessage.obj;
          NsdService.ClientInfo localClientInfo2 = (NsdService.ClientInfo)NsdService.-get0(NsdService.this).get(paramMessage.replyTo);
          if (requestLimitReached(localClientInfo2))
          {
            NsdService.-wrap15(NsdService.this, paramMessage, 393219, 4);
            return true;
          }
          int i = NsdService.-wrap11(NsdService.this);
          if (NsdService.-wrap0(NsdService.this, i, ((NsdServiceInfo)localObject).getServiceType()))
          {
            storeRequestMap(paramMessage.arg2, i, localClientInfo2, paramMessage.what);
            NsdService.-wrap16(NsdService.this, paramMessage, 393218, localObject);
            return true;
          }
          NsdService.-wrap9(NsdService.this, i);
          NsdService.-wrap15(NsdService.this, paramMessage, 393219, 0);
          return true;
          localObject = (NsdService.ClientInfo)NsdService.-get0(NsdService.this).get(paramMessage.replyTo);
          try
          {
            i = ((Integer)NsdService.ClientInfo.-get1((NsdService.ClientInfo)localObject).get(paramMessage.arg2)).intValue();
            removeRequestMap(paramMessage.arg2, i, (NsdService.ClientInfo)localObject);
            if (NsdService.-wrap9(NsdService.this, i))
            {
              NsdService.-wrap14(NsdService.this, paramMessage, 393224);
              return true;
            }
          }
          catch (NullPointerException localNullPointerException1)
          {
            NsdService.-wrap15(NsdService.this, paramMessage, 393223, 0);
            return true;
          }
          NsdService.-wrap15(NsdService.this, paramMessage, 393223, 0);
          return true;
          NsdService.ClientInfo localClientInfo1 = (NsdService.ClientInfo)NsdService.-get0(NsdService.this).get(paramMessage.replyTo);
          if (requestLimitReached(localClientInfo1))
          {
            NsdService.-wrap15(NsdService.this, paramMessage, 393226, 4);
            return true;
          }
          i = NsdService.-wrap11(NsdService.this);
          if (NsdService.-wrap3(NsdService.this, i, (NsdServiceInfo)paramMessage.obj))
          {
            storeRequestMap(paramMessage.arg2, i, localClientInfo1, paramMessage.what);
            return true;
          }
          NsdService.-wrap10(NsdService.this, i);
          NsdService.-wrap15(NsdService.this, paramMessage, 393226, 0);
          return true;
          localClientInfo1 = (NsdService.ClientInfo)NsdService.-get0(NsdService.this).get(paramMessage.replyTo);
          try
          {
            i = ((Integer)NsdService.ClientInfo.-get1(localClientInfo1).get(paramMessage.arg2)).intValue();
            removeRequestMap(paramMessage.arg2, i, localClientInfo1);
            if (NsdService.-wrap10(NsdService.this, i))
            {
              NsdService.-wrap14(NsdService.this, paramMessage, 393230);
              return true;
            }
          }
          catch (NullPointerException localNullPointerException2)
          {
            NsdService.-wrap15(NsdService.this, paramMessage, 393229, 0);
            return true;
          }
          NsdService.-wrap15(NsdService.this, paramMessage, 393229, 0);
          return true;
          NsdServiceInfo localNsdServiceInfo = (NsdServiceInfo)paramMessage.obj;
          localClientInfo2 = (NsdService.ClientInfo)NsdService.-get0(NsdService.this).get(paramMessage.replyTo);
          if (NsdService.ClientInfo.-get3(localClientInfo2) != null)
          {
            NsdService.-wrap15(NsdService.this, paramMessage, 393235, 3);
            return true;
          }
          i = NsdService.-wrap11(NsdService.this);
          if (NsdService.-wrap4(NsdService.this, i, localNsdServiceInfo))
          {
            NsdService.ClientInfo.-set0(localClientInfo2, new NsdServiceInfo());
            storeRequestMap(paramMessage.arg2, i, localClientInfo2, paramMessage.what);
            return true;
          }
          NsdService.-wrap15(NsdService.this, paramMessage, 393235, 0);
          return true;
          paramMessage = (NsdService.NativeEvent)paramMessage.obj;
        } while (handleNativeEvent(paramMessage.code, paramMessage.raw, paramMessage.cooked));
        return false;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/NsdService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */