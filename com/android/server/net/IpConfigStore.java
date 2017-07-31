package com.android.server.net;

import android.net.IpConfiguration;
import android.net.IpConfiguration.IpAssignment;
import android.net.IpConfiguration.ProxySettings;
import android.net.LinkAddress;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;

public class IpConfigStore
{
  private static final boolean DBG = false;
  protected static final String DNS_KEY = "dns";
  protected static final String EOS = "eos";
  protected static final String EXCLUSION_LIST_KEY = "exclusionList";
  protected static final String GATEWAY_KEY = "gateway";
  protected static final String ID_KEY = "id";
  protected static final int IPCONFIG_FILE_VERSION = 2;
  protected static final String IP_ASSIGNMENT_KEY = "ipAssignment";
  protected static final String LINK_ADDRESS_KEY = "linkAddress";
  protected static final String PROXY_HOST_KEY = "proxyHost";
  protected static final String PROXY_PAC_FILE = "proxyPac";
  protected static final String PROXY_PORT_KEY = "proxyPort";
  protected static final String PROXY_SETTINGS_KEY = "proxySettings";
  private static final String TAG = "IpConfigStore";
  protected final DelayedDiskWrite mWriter;
  
  public IpConfigStore()
  {
    this(new DelayedDiskWrite());
  }
  
  public IpConfigStore(DelayedDiskWrite paramDelayedDiskWrite)
  {
    this.mWriter = paramDelayedDiskWrite;
  }
  
  private boolean writeConfig(DataOutputStream paramDataOutputStream, int paramInt, IpConfiguration paramIpConfiguration)
    throws IOException
  {
    boolean bool4 = false;
    boolean bool1 = false;
    boolean bool2 = bool1;
    boolean bool3 = bool4;
    try
    {
      switch (-getandroid-net-IpConfiguration$IpAssignmentSwitchesValues()[paramIpConfiguration.ipAssignment.ordinal()])
      {
      case 3: 
        bool3 = bool4;
        loge("Ignore invalid ip assignment while writing");
        bool2 = bool1;
        bool1 = bool2;
        bool3 = bool2;
        switch (-getandroid-net-IpConfiguration$ProxySettingsSwitchesValues()[paramIpConfiguration.proxySettings.ordinal()])
        {
        case 4: 
          bool3 = bool2;
          loge("Ignore invalid proxy settings while writing");
          bool1 = bool2;
          bool3 = bool1;
          if (bool1)
          {
            bool3 = bool1;
            paramDataOutputStream.writeUTF("id");
            bool3 = bool1;
            paramDataOutputStream.writeInt(paramInt);
            bool3 = bool1;
          }
          paramDataOutputStream.writeUTF("eos");
          return bool3;
        }
      case 2: 
        bool3 = bool4;
        paramDataOutputStream.writeUTF("ipAssignment");
        bool3 = bool4;
        paramDataOutputStream.writeUTF(paramIpConfiguration.ipAssignment.toString());
        bool3 = bool4;
        Object localObject1 = paramIpConfiguration.staticIpConfiguration;
        if (localObject1 != null)
        {
          bool3 = bool4;
          if (((StaticIpConfiguration)localObject1).ipAddress != null)
          {
            bool3 = bool4;
            localObject2 = ((StaticIpConfiguration)localObject1).ipAddress;
            bool3 = bool4;
            paramDataOutputStream.writeUTF("linkAddress");
            bool3 = bool4;
            paramDataOutputStream.writeUTF(((LinkAddress)localObject2).getAddress().getHostAddress());
            bool3 = bool4;
            paramDataOutputStream.writeInt(((LinkAddress)localObject2).getPrefixLength());
          }
          bool3 = bool4;
          if (((StaticIpConfiguration)localObject1).gateway != null)
          {
            bool3 = bool4;
            paramDataOutputStream.writeUTF("gateway");
            bool3 = bool4;
            paramDataOutputStream.writeInt(0);
            bool3 = bool4;
            paramDataOutputStream.writeInt(1);
            bool3 = bool4;
            paramDataOutputStream.writeUTF(((StaticIpConfiguration)localObject1).gateway.getHostAddress());
          }
          bool3 = bool4;
          localObject1 = ((StaticIpConfiguration)localObject1).dnsServers.iterator();
          for (;;)
          {
            bool3 = bool4;
            if (!((Iterator)localObject1).hasNext()) {
              break;
            }
            bool3 = bool4;
            localObject2 = (InetAddress)((Iterator)localObject1).next();
            bool3 = bool4;
            paramDataOutputStream.writeUTF("dns");
            bool3 = bool4;
            paramDataOutputStream.writeUTF(((InetAddress)localObject2).getHostAddress());
          }
        }
        break;
      }
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;)
      {
        loge("Failure in writing " + paramIpConfiguration + localNullPointerException);
        continue;
        bool2 = true;
        continue;
        bool3 = bool4;
        paramDataOutputStream.writeUTF("ipAssignment");
        bool3 = bool4;
        paramDataOutputStream.writeUTF(paramIpConfiguration.ipAssignment.toString());
        bool2 = true;
        continue;
        bool3 = bool2;
        ProxyInfo localProxyInfo = paramIpConfiguration.httpProxy;
        bool3 = bool2;
        Object localObject2 = localProxyInfo.getExclusionListAsString();
        bool3 = bool2;
        paramDataOutputStream.writeUTF("proxySettings");
        bool3 = bool2;
        paramDataOutputStream.writeUTF(paramIpConfiguration.proxySettings.toString());
        bool3 = bool2;
        paramDataOutputStream.writeUTF("proxyHost");
        bool3 = bool2;
        paramDataOutputStream.writeUTF(localProxyInfo.getHost());
        bool3 = bool2;
        paramDataOutputStream.writeUTF("proxyPort");
        bool3 = bool2;
        paramDataOutputStream.writeInt(localProxyInfo.getPort());
        if (localObject2 != null)
        {
          bool3 = bool2;
          paramDataOutputStream.writeUTF("exclusionList");
          bool3 = bool2;
          paramDataOutputStream.writeUTF((String)localObject2);
          break label695;
          bool3 = bool2;
          localProxyInfo = paramIpConfiguration.httpProxy;
          bool3 = bool2;
          paramDataOutputStream.writeUTF("proxySettings");
          bool3 = bool2;
          paramDataOutputStream.writeUTF(paramIpConfiguration.proxySettings.toString());
          bool3 = bool2;
          paramDataOutputStream.writeUTF("proxyPac");
          bool3 = bool2;
          paramDataOutputStream.writeUTF(localProxyInfo.getPacFileUrl().toString());
          bool1 = true;
          continue;
          bool3 = bool2;
          paramDataOutputStream.writeUTF("proxySettings");
          bool3 = bool2;
          paramDataOutputStream.writeUTF(paramIpConfiguration.proxySettings.toString());
          bool1 = true;
          continue;
          continue;
          continue;
        }
        label695:
        bool1 = true;
      }
    }
  }
  
  protected void log(String paramString)
  {
    Log.d("IpConfigStore", paramString);
  }
  
  protected void loge(String paramString)
  {
    Log.e("IpConfigStore", paramString);
  }
  
  /* Error */
  public SparseArray<IpConfiguration> readIpAndProxyConfigurations(String paramString)
  {
    // Byte code:
    //   0: new 258	android/util/SparseArray
    //   3: dup
    //   4: invokespecial 259	android/util/SparseArray:<init>	()V
    //   7: astore 15
    //   9: aconst_null
    //   10: astore 9
    //   12: aconst_null
    //   13: astore 7
    //   15: aconst_null
    //   16: astore 8
    //   18: new 261	java/io/DataInputStream
    //   21: dup
    //   22: new 263	java/io/BufferedInputStream
    //   25: dup
    //   26: new 265	java/io/FileInputStream
    //   29: dup
    //   30: aload_1
    //   31: invokespecial 267	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   34: invokespecial 270	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   37: invokespecial 271	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   40: astore_1
    //   41: aload_1
    //   42: invokevirtual 274	java/io/DataInputStream:readInt	()I
    //   45: istore 5
    //   47: iload 5
    //   49: iconst_2
    //   50: if_icmpeq +57 -> 107
    //   53: iload 5
    //   55: iconst_1
    //   56: if_icmpeq +51 -> 107
    //   59: aload_0
    //   60: ldc_w 276
    //   63: invokevirtual 135	com/android/server/net/IpConfigStore:loge	(Ljava/lang/String;)V
    //   66: aload_1
    //   67: ifnull +7 -> 74
    //   70: aload_1
    //   71: invokevirtual 279	java/io/DataInputStream:close	()V
    //   74: aconst_null
    //   75: areturn
    //   76: astore_1
    //   77: aconst_null
    //   78: areturn
    //   79: new 220	android/net/ProxyInfo
    //   82: dup
    //   83: aload 10
    //   85: iload_3
    //   86: aload 13
    //   88: invokespecial 282	android/net/ProxyInfo:<init>	(Ljava/lang/String;ILjava/lang/String;)V
    //   91: astore 8
    //   93: aload 7
    //   95: aload 11
    //   97: putfield 139	android/net/IpConfiguration:proxySettings	Landroid/net/IpConfiguration$ProxySettings;
    //   100: aload 7
    //   102: aload 8
    //   104: putfield 218	android/net/IpConfiguration:httpProxy	Landroid/net/ProxyInfo;
    //   107: iconst_m1
    //   108: istore_2
    //   109: getstatic 70	android/net/IpConfiguration$IpAssignment:DHCP	Landroid/net/IpConfiguration$IpAssignment;
    //   112: astore 8
    //   114: getstatic 93	android/net/IpConfiguration$ProxySettings:NONE	Landroid/net/IpConfiguration$ProxySettings;
    //   117: astore 11
    //   119: new 160	android/net/StaticIpConfiguration
    //   122: dup
    //   123: invokespecial 283	android/net/StaticIpConfiguration:<init>	()V
    //   126: astore 16
    //   128: aconst_null
    //   129: astore 10
    //   131: aconst_null
    //   132: astore 9
    //   134: iconst_m1
    //   135: istore_3
    //   136: aconst_null
    //   137: astore 7
    //   139: aload 8
    //   141: astore 12
    //   143: aload 7
    //   145: astore 13
    //   147: aload_1
    //   148: invokevirtual 286	java/io/DataInputStream:readUTF	()Ljava/lang/String;
    //   151: astore 7
    //   153: aload 7
    //   155: ldc 28
    //   157: invokevirtual 292	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   160: ifeq +23 -> 183
    //   163: aload_1
    //   164: invokevirtual 274	java/io/DataInputStream:readInt	()I
    //   167: istore 4
    //   169: aload 13
    //   171: astore 7
    //   173: iload 4
    //   175: istore_2
    //   176: aload 12
    //   178: astore 8
    //   180: goto -41 -> 139
    //   183: aload 7
    //   185: ldc 34
    //   187: invokevirtual 292	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   190: ifeq +19 -> 209
    //   193: aload_1
    //   194: invokevirtual 286	java/io/DataInputStream:readUTF	()Ljava/lang/String;
    //   197: invokestatic 296	android/net/IpConfiguration$IpAssignment:valueOf	(Ljava/lang/String;)Landroid/net/IpConfiguration$IpAssignment;
    //   200: astore 8
    //   202: aload 13
    //   204: astore 7
    //   206: goto -67 -> 139
    //   209: aload 7
    //   211: ldc 37
    //   213: invokevirtual 292	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   216: ifeq +192 -> 408
    //   219: new 166	android/net/LinkAddress
    //   222: dup
    //   223: aload_1
    //   224: invokevirtual 286	java/io/DataInputStream:readUTF	()Ljava/lang/String;
    //   227: invokestatic 302	android/net/NetworkUtils:numericToInetAddress	(Ljava/lang/String;)Ljava/net/InetAddress;
    //   230: aload_1
    //   231: invokevirtual 274	java/io/DataInputStream:readInt	()I
    //   234: invokespecial 305	android/net/LinkAddress:<init>	(Ljava/net/InetAddress;I)V
    //   237: astore 7
    //   239: aload 7
    //   241: invokevirtual 170	android/net/LinkAddress:getAddress	()Ljava/net/InetAddress;
    //   244: instanceof 307
    //   247: ifeq +80 -> 327
    //   250: aload 16
    //   252: getfield 164	android/net/StaticIpConfiguration:ipAddress	Landroid/net/LinkAddress;
    //   255: ifnonnull +72 -> 327
    //   258: aload 16
    //   260: aload 7
    //   262: putfield 164	android/net/StaticIpConfiguration:ipAddress	Landroid/net/LinkAddress;
    //   265: aload 13
    //   267: astore 7
    //   269: aload 12
    //   271: astore 8
    //   273: goto -134 -> 139
    //   276: astore 7
    //   278: aload_0
    //   279: new 203	java/lang/StringBuilder
    //   282: dup
    //   283: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   286: ldc_w 309
    //   289: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   292: aload 7
    //   294: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   297: invokevirtual 214	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   300: invokevirtual 135	com/android/server/net/IpConfigStore:loge	(Ljava/lang/String;)V
    //   303: aload 13
    //   305: astore 7
    //   307: aload 12
    //   309: astore 8
    //   311: goto -172 -> 139
    //   314: astore 7
    //   316: aload_1
    //   317: ifnull +7 -> 324
    //   320: aload_1
    //   321: invokevirtual 279	java/io/DataInputStream:close	()V
    //   324: aload 15
    //   326: areturn
    //   327: aload_0
    //   328: new 203	java/lang/StringBuilder
    //   331: dup
    //   332: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   335: ldc_w 311
    //   338: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   341: aload 7
    //   343: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   346: invokevirtual 214	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   349: invokevirtual 135	com/android/server/net/IpConfigStore:loge	(Ljava/lang/String;)V
    //   352: aload 13
    //   354: astore 7
    //   356: aload 12
    //   358: astore 8
    //   360: goto -221 -> 139
    //   363: astore 8
    //   365: aload_1
    //   366: astore 7
    //   368: aload_0
    //   369: new 203	java/lang/StringBuilder
    //   372: dup
    //   373: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   376: ldc_w 313
    //   379: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   382: aload 8
    //   384: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   387: invokevirtual 214	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   390: invokevirtual 135	com/android/server/net/IpConfigStore:loge	(Ljava/lang/String;)V
    //   393: aload_1
    //   394: ifnull -70 -> 324
    //   397: aload_1
    //   398: invokevirtual 279	java/io/DataInputStream:close	()V
    //   401: aload 15
    //   403: areturn
    //   404: astore_1
    //   405: aload 15
    //   407: areturn
    //   408: aload 7
    //   410: ldc 25
    //   412: invokevirtual 292	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   415: ifeq +237 -> 652
    //   418: aconst_null
    //   419: astore 7
    //   421: aconst_null
    //   422: astore 8
    //   424: iload 5
    //   426: iconst_1
    //   427: if_icmpne +97 -> 524
    //   430: aload_1
    //   431: invokevirtual 286	java/io/DataInputStream:readUTF	()Ljava/lang/String;
    //   434: invokestatic 302	android/net/NetworkUtils:numericToInetAddress	(Ljava/lang/String;)Ljava/net/InetAddress;
    //   437: astore 7
    //   439: aload 16
    //   441: getfield 181	android/net/StaticIpConfiguration:gateway	Ljava/net/InetAddress;
    //   444: ifnonnull +41 -> 485
    //   447: aload 16
    //   449: aload 7
    //   451: putfield 181	android/net/StaticIpConfiguration:gateway	Ljava/net/InetAddress;
    //   454: aload 13
    //   456: astore 7
    //   458: aload 12
    //   460: astore 8
    //   462: goto -323 -> 139
    //   465: astore 8
    //   467: aload_1
    //   468: astore 7
    //   470: aload 8
    //   472: astore_1
    //   473: aload 7
    //   475: ifnull +8 -> 483
    //   478: aload 7
    //   480: invokevirtual 279	java/io/DataInputStream:close	()V
    //   483: aload_1
    //   484: athrow
    //   485: aload_0
    //   486: new 203	java/lang/StringBuilder
    //   489: dup
    //   490: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   493: ldc_w 315
    //   496: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   499: aload 7
    //   501: invokevirtual 175	java/net/InetAddress:getHostAddress	()Ljava/lang/String;
    //   504: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   507: invokevirtual 214	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   510: invokevirtual 135	com/android/server/net/IpConfigStore:loge	(Ljava/lang/String;)V
    //   513: aload 13
    //   515: astore 7
    //   517: aload 12
    //   519: astore 8
    //   521: goto -382 -> 139
    //   524: aload_1
    //   525: invokevirtual 274	java/io/DataInputStream:readInt	()I
    //   528: iconst_1
    //   529: if_icmpne +23 -> 552
    //   532: new 166	android/net/LinkAddress
    //   535: dup
    //   536: aload_1
    //   537: invokevirtual 286	java/io/DataInputStream:readUTF	()Ljava/lang/String;
    //   540: invokestatic 302	android/net/NetworkUtils:numericToInetAddress	(Ljava/lang/String;)Ljava/net/InetAddress;
    //   543: aload_1
    //   544: invokevirtual 274	java/io/DataInputStream:readInt	()I
    //   547: invokespecial 305	android/net/LinkAddress:<init>	(Ljava/net/InetAddress;I)V
    //   550: astore 7
    //   552: aload_1
    //   553: invokevirtual 274	java/io/DataInputStream:readInt	()I
    //   556: iconst_1
    //   557: if_icmpne +12 -> 569
    //   560: aload_1
    //   561: invokevirtual 286	java/io/DataInputStream:readUTF	()Ljava/lang/String;
    //   564: invokestatic 302	android/net/NetworkUtils:numericToInetAddress	(Ljava/lang/String;)Ljava/net/InetAddress;
    //   567: astore 8
    //   569: new 317	android/net/RouteInfo
    //   572: dup
    //   573: aload 7
    //   575: aload 8
    //   577: invokespecial 320	android/net/RouteInfo:<init>	(Landroid/net/LinkAddress;Ljava/net/InetAddress;)V
    //   580: astore 7
    //   582: aload 7
    //   584: invokevirtual 323	android/net/RouteInfo:isIPv4Default	()Z
    //   587: ifeq +29 -> 616
    //   590: aload 16
    //   592: getfield 181	android/net/StaticIpConfiguration:gateway	Ljava/net/InetAddress;
    //   595: ifnonnull +21 -> 616
    //   598: aload 16
    //   600: aload 8
    //   602: putfield 181	android/net/StaticIpConfiguration:gateway	Ljava/net/InetAddress;
    //   605: aload 13
    //   607: astore 7
    //   609: aload 12
    //   611: astore 8
    //   613: goto -474 -> 139
    //   616: aload_0
    //   617: new 203	java/lang/StringBuilder
    //   620: dup
    //   621: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   624: ldc_w 325
    //   627: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   630: aload 7
    //   632: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   635: invokevirtual 214	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   638: invokevirtual 135	com/android/server/net/IpConfigStore:loge	(Ljava/lang/String;)V
    //   641: aload 13
    //   643: astore 7
    //   645: aload 12
    //   647: astore 8
    //   649: goto -510 -> 139
    //   652: aload 7
    //   654: ldc 16
    //   656: invokevirtual 292	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   659: ifeq +30 -> 689
    //   662: aload 16
    //   664: getfield 185	android/net/StaticIpConfiguration:dnsServers	Ljava/util/ArrayList;
    //   667: aload_1
    //   668: invokevirtual 286	java/io/DataInputStream:readUTF	()Ljava/lang/String;
    //   671: invokestatic 302	android/net/NetworkUtils:numericToInetAddress	(Ljava/lang/String;)Ljava/net/InetAddress;
    //   674: invokevirtual 330	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   677: pop
    //   678: aload 13
    //   680: astore 7
    //   682: aload 12
    //   684: astore 8
    //   686: goto -547 -> 139
    //   689: aload 7
    //   691: ldc 49
    //   693: invokevirtual 292	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   696: ifeq +27 -> 723
    //   699: aload_1
    //   700: invokevirtual 286	java/io/DataInputStream:readUTF	()Ljava/lang/String;
    //   703: invokestatic 333	android/net/IpConfiguration$ProxySettings:valueOf	(Ljava/lang/String;)Landroid/net/IpConfiguration$ProxySettings;
    //   706: astore 14
    //   708: aload 13
    //   710: astore 7
    //   712: aload 12
    //   714: astore 8
    //   716: aload 14
    //   718: astore 11
    //   720: goto -581 -> 139
    //   723: aload 7
    //   725: ldc 40
    //   727: invokevirtual 292	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   730: ifeq +24 -> 754
    //   733: aload_1
    //   734: invokevirtual 286	java/io/DataInputStream:readUTF	()Ljava/lang/String;
    //   737: astore 14
    //   739: aload 13
    //   741: astore 7
    //   743: aload 12
    //   745: astore 8
    //   747: aload 14
    //   749: astore 10
    //   751: goto -612 -> 139
    //   754: aload 7
    //   756: ldc 46
    //   758: invokevirtual 292	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   761: ifeq +23 -> 784
    //   764: aload_1
    //   765: invokevirtual 274	java/io/DataInputStream:readInt	()I
    //   768: istore 4
    //   770: aload 13
    //   772: astore 7
    //   774: aload 12
    //   776: astore 8
    //   778: iload 4
    //   780: istore_3
    //   781: goto -642 -> 139
    //   784: aload 7
    //   786: ldc 43
    //   788: invokevirtual 292	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   791: ifeq +24 -> 815
    //   794: aload_1
    //   795: invokevirtual 286	java/io/DataInputStream:readUTF	()Ljava/lang/String;
    //   798: astore 14
    //   800: aload 13
    //   802: astore 7
    //   804: aload 12
    //   806: astore 8
    //   808: aload 14
    //   810: astore 9
    //   812: goto -673 -> 139
    //   815: aload 7
    //   817: ldc 22
    //   819: invokevirtual 292	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   822: ifeq +16 -> 838
    //   825: aload_1
    //   826: invokevirtual 286	java/io/DataInputStream:readUTF	()Ljava/lang/String;
    //   829: astore 7
    //   831: aload 12
    //   833: astore 8
    //   835: goto -696 -> 139
    //   838: aload 7
    //   840: ldc 19
    //   842: invokevirtual 292	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   845: istore 6
    //   847: iload 6
    //   849: ifeq +133 -> 982
    //   852: iload_2
    //   853: iconst_m1
    //   854: if_icmpeq -747 -> 107
    //   857: new 127	android/net/IpConfiguration
    //   860: dup
    //   861: invokespecial 334	android/net/IpConfiguration:<init>	()V
    //   864: astore 7
    //   866: aload 15
    //   868: iload_2
    //   869: aload 7
    //   871: invokevirtual 338	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   874: invokestatic 125	com/android/server/net/IpConfigStore:-getandroid-net-IpConfiguration$IpAssignmentSwitchesValues	()[I
    //   877: aload 12
    //   879: invokevirtual 74	android/net/IpConfiguration$IpAssignment:ordinal	()I
    //   882: iaload
    //   883: tableswitch	default:+270->1153, 1:+158->1041, 2:+141->1024, 3:+168->1051
    //   908: aload_0
    //   909: ldc_w 340
    //   912: invokevirtual 135	com/android/server/net/IpConfigStore:loge	(Ljava/lang/String;)V
    //   915: aload 7
    //   917: getstatic 80	android/net/IpConfiguration$IpAssignment:UNASSIGNED	Landroid/net/IpConfiguration$IpAssignment;
    //   920: putfield 129	android/net/IpConfiguration:ipAssignment	Landroid/net/IpConfiguration$IpAssignment;
    //   923: invokestatic 137	com/android/server/net/IpConfigStore:-getandroid-net-IpConfiguration$ProxySettingsSwitchesValues	()[I
    //   926: aload 11
    //   928: invokevirtual 94	android/net/IpConfiguration$ProxySettings:ordinal	()I
    //   931: iaload
    //   932: tableswitch	default:+224->1156, 1:+165->1097, 2:+137->1069, 3:+-853->79, 4:+175->1107
    //   964: aload_0
    //   965: ldc_w 342
    //   968: invokevirtual 135	com/android/server/net/IpConfigStore:loge	(Ljava/lang/String;)V
    //   971: aload 7
    //   973: getstatic 101	android/net/IpConfiguration$ProxySettings:UNASSIGNED	Landroid/net/IpConfiguration$ProxySettings;
    //   976: putfield 139	android/net/IpConfiguration:proxySettings	Landroid/net/IpConfiguration$ProxySettings;
    //   979: goto -872 -> 107
    //   982: aload_0
    //   983: new 203	java/lang/StringBuilder
    //   986: dup
    //   987: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   990: ldc_w 344
    //   993: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   996: aload 7
    //   998: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1001: ldc_w 346
    //   1004: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1007: invokevirtual 214	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1010: invokevirtual 135	com/android/server/net/IpConfigStore:loge	(Ljava/lang/String;)V
    //   1013: aload 13
    //   1015: astore 7
    //   1017: aload 12
    //   1019: astore 8
    //   1021: goto -882 -> 139
    //   1024: aload 7
    //   1026: aload 16
    //   1028: putfield 158	android/net/IpConfiguration:staticIpConfiguration	Landroid/net/StaticIpConfiguration;
    //   1031: aload 7
    //   1033: aload 12
    //   1035: putfield 129	android/net/IpConfiguration:ipAssignment	Landroid/net/IpConfiguration$IpAssignment;
    //   1038: goto -115 -> 923
    //   1041: aload 7
    //   1043: aload 12
    //   1045: putfield 129	android/net/IpConfiguration:ipAssignment	Landroid/net/IpConfiguration$IpAssignment;
    //   1048: goto -125 -> 923
    //   1051: aload_0
    //   1052: ldc_w 348
    //   1055: invokevirtual 135	com/android/server/net/IpConfigStore:loge	(Ljava/lang/String;)V
    //   1058: aload 7
    //   1060: getstatic 70	android/net/IpConfiguration$IpAssignment:DHCP	Landroid/net/IpConfiguration$IpAssignment;
    //   1063: putfield 129	android/net/IpConfiguration:ipAssignment	Landroid/net/IpConfiguration$IpAssignment;
    //   1066: goto -143 -> 923
    //   1069: new 220	android/net/ProxyInfo
    //   1072: dup
    //   1073: aload 9
    //   1075: invokespecial 349	android/net/ProxyInfo:<init>	(Ljava/lang/String;)V
    //   1078: astore 8
    //   1080: aload 7
    //   1082: aload 11
    //   1084: putfield 139	android/net/IpConfiguration:proxySettings	Landroid/net/IpConfiguration$ProxySettings;
    //   1087: aload 7
    //   1089: aload 8
    //   1091: putfield 218	android/net/IpConfiguration:httpProxy	Landroid/net/ProxyInfo;
    //   1094: goto -987 -> 107
    //   1097: aload 7
    //   1099: aload 11
    //   1101: putfield 139	android/net/IpConfiguration:proxySettings	Landroid/net/IpConfiguration$ProxySettings;
    //   1104: goto -997 -> 107
    //   1107: aload_0
    //   1108: ldc_w 351
    //   1111: invokevirtual 135	com/android/server/net/IpConfigStore:loge	(Ljava/lang/String;)V
    //   1114: aload 7
    //   1116: getstatic 93	android/net/IpConfiguration$ProxySettings:NONE	Landroid/net/IpConfiguration$ProxySettings;
    //   1119: putfield 139	android/net/IpConfiguration:proxySettings	Landroid/net/IpConfiguration$ProxySettings;
    //   1122: goto -1015 -> 107
    //   1125: astore_1
    //   1126: aload 15
    //   1128: areturn
    //   1129: astore 7
    //   1131: goto -648 -> 483
    //   1134: astore_1
    //   1135: goto -662 -> 473
    //   1138: astore_1
    //   1139: aload 8
    //   1141: astore_1
    //   1142: goto -826 -> 316
    //   1145: astore 8
    //   1147: aload 9
    //   1149: astore_1
    //   1150: goto -785 -> 365
    //   1153: goto -245 -> 908
    //   1156: goto -192 -> 964
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1159	0	this	IpConfigStore
    //   0	1159	1	paramString	String
    //   108	761	2	i	int
    //   85	696	3	j	int
    //   167	612	4	k	int
    //   45	383	5	m	int
    //   845	3	6	bool	boolean
    //   13	255	7	localObject1	Object
    //   276	17	7	localIllegalArgumentException	IllegalArgumentException
    //   305	1	7	localObject2	Object
    //   314	28	7	localEOFException	java.io.EOFException
    //   354	761	7	localObject3	Object
    //   1129	1	7	localException	Exception
    //   16	343	8	localObject4	Object
    //   363	20	8	localIOException1	IOException
    //   422	39	8	localObject5	Object
    //   465	6	8	localObject6	Object
    //   519	621	8	localObject7	Object
    //   1145	1	8	localIOException2	IOException
    //   10	1138	9	localObject8	Object
    //   83	667	10	localObject9	Object
    //   95	1005	11	localObject10	Object
    //   141	903	12	localObject11	Object
    //   86	928	13	localObject12	Object
    //   706	103	14	localObject13	Object
    //   7	1120	15	localSparseArray	SparseArray
    //   126	901	16	localStaticIpConfiguration	StaticIpConfiguration
    // Exception table:
    //   from	to	target	type
    //   70	74	76	java/lang/Exception
    //   153	169	276	java/lang/IllegalArgumentException
    //   183	202	276	java/lang/IllegalArgumentException
    //   209	265	276	java/lang/IllegalArgumentException
    //   327	352	276	java/lang/IllegalArgumentException
    //   408	418	276	java/lang/IllegalArgumentException
    //   430	454	276	java/lang/IllegalArgumentException
    //   485	513	276	java/lang/IllegalArgumentException
    //   524	552	276	java/lang/IllegalArgumentException
    //   552	569	276	java/lang/IllegalArgumentException
    //   569	605	276	java/lang/IllegalArgumentException
    //   616	641	276	java/lang/IllegalArgumentException
    //   652	678	276	java/lang/IllegalArgumentException
    //   689	708	276	java/lang/IllegalArgumentException
    //   723	739	276	java/lang/IllegalArgumentException
    //   754	770	276	java/lang/IllegalArgumentException
    //   784	800	276	java/lang/IllegalArgumentException
    //   815	831	276	java/lang/IllegalArgumentException
    //   838	847	276	java/lang/IllegalArgumentException
    //   982	1013	276	java/lang/IllegalArgumentException
    //   41	47	314	java/io/EOFException
    //   59	66	314	java/io/EOFException
    //   79	107	314	java/io/EOFException
    //   109	128	314	java/io/EOFException
    //   147	153	314	java/io/EOFException
    //   153	169	314	java/io/EOFException
    //   183	202	314	java/io/EOFException
    //   209	265	314	java/io/EOFException
    //   278	303	314	java/io/EOFException
    //   327	352	314	java/io/EOFException
    //   408	418	314	java/io/EOFException
    //   430	454	314	java/io/EOFException
    //   485	513	314	java/io/EOFException
    //   524	552	314	java/io/EOFException
    //   552	569	314	java/io/EOFException
    //   569	605	314	java/io/EOFException
    //   616	641	314	java/io/EOFException
    //   652	678	314	java/io/EOFException
    //   689	708	314	java/io/EOFException
    //   723	739	314	java/io/EOFException
    //   754	770	314	java/io/EOFException
    //   784	800	314	java/io/EOFException
    //   815	831	314	java/io/EOFException
    //   838	847	314	java/io/EOFException
    //   857	908	314	java/io/EOFException
    //   908	923	314	java/io/EOFException
    //   923	964	314	java/io/EOFException
    //   964	979	314	java/io/EOFException
    //   982	1013	314	java/io/EOFException
    //   1024	1038	314	java/io/EOFException
    //   1041	1048	314	java/io/EOFException
    //   1051	1066	314	java/io/EOFException
    //   1069	1094	314	java/io/EOFException
    //   1097	1104	314	java/io/EOFException
    //   1107	1122	314	java/io/EOFException
    //   41	47	363	java/io/IOException
    //   59	66	363	java/io/IOException
    //   79	107	363	java/io/IOException
    //   109	128	363	java/io/IOException
    //   147	153	363	java/io/IOException
    //   153	169	363	java/io/IOException
    //   183	202	363	java/io/IOException
    //   209	265	363	java/io/IOException
    //   278	303	363	java/io/IOException
    //   327	352	363	java/io/IOException
    //   408	418	363	java/io/IOException
    //   430	454	363	java/io/IOException
    //   485	513	363	java/io/IOException
    //   524	552	363	java/io/IOException
    //   552	569	363	java/io/IOException
    //   569	605	363	java/io/IOException
    //   616	641	363	java/io/IOException
    //   652	678	363	java/io/IOException
    //   689	708	363	java/io/IOException
    //   723	739	363	java/io/IOException
    //   754	770	363	java/io/IOException
    //   784	800	363	java/io/IOException
    //   815	831	363	java/io/IOException
    //   838	847	363	java/io/IOException
    //   857	908	363	java/io/IOException
    //   908	923	363	java/io/IOException
    //   923	964	363	java/io/IOException
    //   964	979	363	java/io/IOException
    //   982	1013	363	java/io/IOException
    //   1024	1038	363	java/io/IOException
    //   1041	1048	363	java/io/IOException
    //   1051	1066	363	java/io/IOException
    //   1069	1094	363	java/io/IOException
    //   1097	1104	363	java/io/IOException
    //   1107	1122	363	java/io/IOException
    //   397	401	404	java/lang/Exception
    //   41	47	465	finally
    //   59	66	465	finally
    //   79	107	465	finally
    //   109	128	465	finally
    //   147	153	465	finally
    //   153	169	465	finally
    //   183	202	465	finally
    //   209	265	465	finally
    //   278	303	465	finally
    //   327	352	465	finally
    //   408	418	465	finally
    //   430	454	465	finally
    //   485	513	465	finally
    //   524	552	465	finally
    //   552	569	465	finally
    //   569	605	465	finally
    //   616	641	465	finally
    //   652	678	465	finally
    //   689	708	465	finally
    //   723	739	465	finally
    //   754	770	465	finally
    //   784	800	465	finally
    //   815	831	465	finally
    //   838	847	465	finally
    //   857	908	465	finally
    //   908	923	465	finally
    //   923	964	465	finally
    //   964	979	465	finally
    //   982	1013	465	finally
    //   1024	1038	465	finally
    //   1041	1048	465	finally
    //   1051	1066	465	finally
    //   1069	1094	465	finally
    //   1097	1104	465	finally
    //   1107	1122	465	finally
    //   320	324	1125	java/lang/Exception
    //   478	483	1129	java/lang/Exception
    //   18	41	1134	finally
    //   368	393	1134	finally
    //   18	41	1138	java/io/EOFException
    //   18	41	1145	java/io/IOException
  }
  
  public void writeIpAndProxyConfigurations(String paramString, final SparseArray<IpConfiguration> paramSparseArray)
  {
    this.mWriter.write(paramString, new DelayedDiskWrite.Writer()
    {
      public void onWriteCalled(DataOutputStream paramAnonymousDataOutputStream)
        throws IOException
      {
        paramAnonymousDataOutputStream.writeInt(2);
        int i = 0;
        while (i < paramSparseArray.size())
        {
          IpConfigStore.-wrap0(IpConfigStore.this, paramAnonymousDataOutputStream, paramSparseArray.keyAt(i), (IpConfiguration)paramSparseArray.valueAt(i));
          i += 1;
        }
      }
    });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/net/IpConfigStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */