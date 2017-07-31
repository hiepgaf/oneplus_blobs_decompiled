package com.android.providers.settings;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FullBackupDataOutput;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.FileUtils;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.util.BackupUtils.BadVersionException;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.CRC32;

public class SettingsBackupAgent
  extends BackupAgentHelper
{
  private static final byte[] EMPTY_DATA = new byte[0];
  private static final String[] PROJECTION = { "name", "value" };
  private static final int[] STATE_SIZES = { 0, 4, 5, 6, 7, 8, 9 };
  private SettingsHelper mSettingsHelper;
  private WifiManager mWfm;
  private String mWifiConfigFile;
  WifiDisableRunnable mWifiDisable = null;
  WifiRestoreRunnable mWifiRestore = null;
  
  private void copyWifiSupplicantTemplate(BufferedWriter paramBufferedWriter)
  {
    try
    {
      BufferedReader localBufferedReader = new BufferedReader(new FileReader("/system/etc/wifi/wpa_supplicant.conf"));
      char[] arrayOfChar = new char['Ѐ'];
      for (;;)
      {
        int i = localBufferedReader.read(arrayOfChar);
        if (i <= 0) {
          break;
        }
        paramBufferedWriter.write(arrayOfChar, 0, i);
      }
      localBufferedReader.close();
    }
    catch (IOException paramBufferedWriter)
    {
      Log.w("SettingsBackupAgent", "Couldn't copy wpa_supplicant file");
      return;
    }
  }
  
  private int enableWifi(boolean paramBoolean)
  {
    if (this.mWfm == null) {
      this.mWfm = ((WifiManager)getSystemService("wifi"));
    }
    if (this.mWfm != null)
    {
      int i = this.mWfm.getWifiState();
      this.mWfm.setWifiEnabled(paramBoolean);
      return i;
    }
    Log.e("SettingsBackupAgent", "Failed to fetch WifiManager instance");
    return 4;
  }
  
  private byte[] extractRelevantValues(Cursor paramCursor, String[] paramArrayOfString)
  {
    int m = paramArrayOfString.length;
    byte[][] arrayOfByte = new byte[m * 2][];
    if (!paramCursor.moveToFirst())
    {
      Log.e("SettingsBackupAgent", "Couldn't read from the cursor");
      return new byte[0];
    }
    int k = 0;
    int i = 0;
    HashMap localHashMap = new HashMap();
    int j = 0;
    if (j < m)
    {
      String str1 = paramArrayOfString[j];
      Object localObject2 = (String)localHashMap.remove(str1);
      int n = paramCursor.getColumnIndex("name");
      int i1 = paramCursor.getColumnIndex("value");
      Object localObject1 = localObject2;
      label107:
      String str2;
      if (localObject2 == null)
      {
        localObject1 = localObject2;
        if (!paramCursor.isAfterLast())
        {
          str2 = paramCursor.getString(n);
          localObject1 = paramCursor.getString(i1);
          paramCursor.moveToNext();
          if (!str1.equals(str2)) {
            break label184;
          }
        }
      }
      localObject1 = this.mSettingsHelper.onBackupValue(str1, (String)localObject1);
      if (localObject1 == null) {}
      for (;;)
      {
        j += 1;
        break;
        label184:
        localHashMap.put(str2, localObject1);
        break label107;
        localObject2 = str1.getBytes();
        n = localObject2.length;
        arrayOfByte[(i * 2)] = localObject2;
        localObject1 = ((String)localObject1).getBytes();
        k = k + (n + 4) + (localObject1.length + 4);
        arrayOfByte[(i * 2 + 1)] = localObject1;
        i += 1;
      }
    }
    paramCursor = new byte[k];
    k = 0;
    j = 0;
    while (j < i * 2)
    {
      k = writeBytes(paramCursor, writeInt(paramCursor, k, arrayOfByte[j].length), arrayOfByte[j]);
      j += 1;
    }
    return paramCursor;
  }
  
  /* Error */
  private byte[] getFileData(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 6
    //   6: aload 5
    //   8: astore 4
    //   10: new 202	java/io/File
    //   13: dup
    //   14: aload_1
    //   15: invokespecial 203	java/io/File:<init>	(Ljava/lang/String;)V
    //   18: astore 7
    //   20: aload 5
    //   22: astore 4
    //   24: new 205	java/io/FileInputStream
    //   27: dup
    //   28: aload 7
    //   30: invokespecial 208	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   33: astore 5
    //   35: aload 7
    //   37: invokevirtual 212	java/io/File:length	()J
    //   40: l2i
    //   41: newarray <illegal type>
    //   43: astore 4
    //   45: iconst_0
    //   46: istore_2
    //   47: iload_2
    //   48: aload 4
    //   50: arraylength
    //   51: if_icmpge +28 -> 79
    //   54: aload 5
    //   56: aload 4
    //   58: iload_2
    //   59: aload 4
    //   61: arraylength
    //   62: iload_2
    //   63: isub
    //   64: invokevirtual 216	java/io/InputStream:read	([BII)I
    //   67: istore_3
    //   68: iload_3
    //   69: iflt +10 -> 79
    //   72: iload_2
    //   73: iload_3
    //   74: iadd
    //   75: istore_2
    //   76: goto -29 -> 47
    //   79: iload_2
    //   80: aload 4
    //   82: arraylength
    //   83: if_icmpge +50 -> 133
    //   86: ldc 102
    //   88: new 218	java/lang/StringBuilder
    //   91: dup
    //   92: invokespecial 219	java/lang/StringBuilder:<init>	()V
    //   95: ldc -35
    //   97: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   100: aload_1
    //   101: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   104: invokevirtual 229	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   107: invokestatic 110	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   110: pop
    //   111: getstatic 59	com/android/providers/settings/SettingsBackupAgent:EMPTY_DATA	[B
    //   114: astore 4
    //   116: aload 5
    //   118: ifnull +8 -> 126
    //   121: aload 5
    //   123: invokevirtual 230	java/io/InputStream:close	()V
    //   126: aload 4
    //   128: areturn
    //   129: astore_1
    //   130: aload 4
    //   132: areturn
    //   133: aload 5
    //   135: ifnull +8 -> 143
    //   138: aload 5
    //   140: invokevirtual 230	java/io/InputStream:close	()V
    //   143: aload 4
    //   145: areturn
    //   146: astore_1
    //   147: aload 4
    //   149: areturn
    //   150: astore 4
    //   152: aload 6
    //   154: astore 5
    //   156: aload 5
    //   158: astore 4
    //   160: ldc 102
    //   162: new 218	java/lang/StringBuilder
    //   165: dup
    //   166: invokespecial 219	java/lang/StringBuilder:<init>	()V
    //   169: ldc -35
    //   171: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   174: aload_1
    //   175: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   178: invokevirtual 229	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   181: invokestatic 110	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   184: pop
    //   185: aload 5
    //   187: astore 4
    //   189: getstatic 59	com/android/providers/settings/SettingsBackupAgent:EMPTY_DATA	[B
    //   192: astore_1
    //   193: aload 5
    //   195: ifnull +8 -> 203
    //   198: aload 5
    //   200: invokevirtual 230	java/io/InputStream:close	()V
    //   203: aload_1
    //   204: areturn
    //   205: astore 4
    //   207: aload_1
    //   208: areturn
    //   209: astore_1
    //   210: aload 4
    //   212: ifnull +8 -> 220
    //   215: aload 4
    //   217: invokevirtual 230	java/io/InputStream:close	()V
    //   220: aload_1
    //   221: athrow
    //   222: astore 4
    //   224: goto -4 -> 220
    //   227: astore_1
    //   228: aload 5
    //   230: astore 4
    //   232: goto -22 -> 210
    //   235: astore 4
    //   237: goto -81 -> 156
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	240	0	this	SettingsBackupAgent
    //   0	240	1	paramString	String
    //   46	38	2	i	int
    //   67	8	3	j	int
    //   8	140	4	localObject1	Object
    //   150	1	4	localIOException1	IOException
    //   158	30	4	localObject2	Object
    //   205	11	4	localIOException2	IOException
    //   222	1	4	localIOException3	IOException
    //   230	1	4	localObject3	Object
    //   235	1	4	localIOException4	IOException
    //   1	228	5	localObject4	Object
    //   4	149	6	localObject5	Object
    //   18	18	7	localFile	File
    // Exception table:
    //   from	to	target	type
    //   121	126	129	java/io/IOException
    //   138	143	146	java/io/IOException
    //   10	20	150	java/io/IOException
    //   24	35	150	java/io/IOException
    //   198	203	205	java/io/IOException
    //   10	20	209	finally
    //   24	35	209	finally
    //   160	185	209	finally
    //   189	193	209	finally
    //   215	220	222	java/io/IOException
    //   35	45	227	finally
    //   47	68	227	finally
    //   79	116	227	finally
    //   35	45	235	java/io/IOException
    //   47	68	235	java/io/IOException
    //   79	116	235	java/io/IOException
  }
  
  private byte[] getGlobalSettings()
  {
    Cursor localCursor = getContentResolver().query(Settings.Global.CONTENT_URI, PROJECTION, null, null, null);
    try
    {
      byte[] arrayOfByte = extractRelevantValues(localCursor, Settings.Global.SETTINGS_TO_BACKUP);
      return arrayOfByte;
    }
    finally
    {
      localCursor.close();
    }
  }
  
  private byte[] getLockSettings()
  {
    Object localObject = new LockPatternUtils(this);
    boolean bool = ((LockPatternUtils)localObject).isOwnerInfoEnabled(UserHandle.myUserId());
    String str = ((LockPatternUtils)localObject).getOwnerInfo(UserHandle.myUserId());
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    for (;;)
    {
      try
      {
        localDataOutputStream.writeUTF("owner_info_enabled");
        if (!bool) {
          continue;
        }
        localObject = "1";
        localDataOutputStream.writeUTF((String)localObject);
        if (str != null)
        {
          localDataOutputStream.writeUTF("owner_info");
          if (str == null) {
            continue;
          }
          localObject = str;
          localDataOutputStream.writeUTF((String)localObject);
        }
        localDataOutputStream.writeUTF("");
        localDataOutputStream.flush();
      }
      catch (IOException localIOException)
      {
        continue;
      }
      return localByteArrayOutputStream.toByteArray();
      localObject = "0";
      continue;
      localObject = "";
    }
  }
  
  private byte[] getNetworkPolicies()
  {
    int i = 0;
    NetworkPolicy[] arrayOfNetworkPolicy = ((NetworkPolicyManager)getSystemService("netpolicy")).getNetworkPolicies();
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream;
    if ((arrayOfNetworkPolicy != null) && (arrayOfNetworkPolicy.length != 0)) {
      localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    }
    for (;;)
    {
      try
      {
        localDataOutputStream.writeInt(1);
        localDataOutputStream.writeInt(arrayOfNetworkPolicy.length);
        int j = arrayOfNetworkPolicy.length;
        if (i < j)
        {
          Object localObject = arrayOfNetworkPolicy[i];
          if (localObject != null)
          {
            localObject = ((NetworkPolicy)localObject).getBytesForBackup();
            localDataOutputStream.writeByte(1);
            localDataOutputStream.writeInt(localObject.length);
            localDataOutputStream.write((byte[])localObject);
          }
          else
          {
            localDataOutputStream.writeByte(0);
          }
        }
      }
      catch (IOException localIOException)
      {
        Log.e("SettingsBackupAgent", "Failed to convert NetworkPolicies to byte array " + localIOException.getMessage());
        localByteArrayOutputStream.reset();
      }
      return localByteArrayOutputStream.toByteArray();
      i += 1;
    }
  }
  
  private byte[] getSecureSettings()
  {
    Cursor localCursor = getContentResolver().query(Settings.Secure.CONTENT_URI, PROJECTION, null, null, null);
    try
    {
      byte[] arrayOfByte = extractRelevantValues(localCursor, Settings.Secure.SETTINGS_TO_BACKUP);
      return arrayOfByte;
    }
    finally
    {
      localCursor.close();
    }
  }
  
  private byte[] getSoftAPConfiguration()
  {
    Object localObject = (WifiManager)getSystemService("wifi");
    try
    {
      localObject = ((WifiManager)localObject).getWifiApConfiguration().getBytesForBackup();
      return (byte[])localObject;
    }
    catch (IOException localIOException)
    {
      Log.e("SettingsBackupAgent", "Failed to marshal SoftAPConfiguration" + localIOException.getMessage());
    }
    return new byte[0];
  }
  
  private byte[] getSystemSettings()
  {
    Cursor localCursor = getContentResolver().query(Settings.System.CONTENT_URI, PROJECTION, null, null, null);
    try
    {
      byte[] arrayOfByte = extractRelevantValues(localCursor, Settings.System.SETTINGS_TO_BACKUP);
      return arrayOfByte;
    }
    finally
    {
      localCursor.close();
    }
  }
  
  /* Error */
  private byte[] getWifiSupplicant(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aload_3
    //   6: astore_2
    //   7: new 202	java/io/File
    //   10: dup
    //   11: aload_1
    //   12: invokespecial 203	java/io/File:<init>	(Ljava/lang/String;)V
    //   15: astore 7
    //   17: aload_3
    //   18: astore_2
    //   19: aload 7
    //   21: invokevirtual 352	java/io/File:exists	()Z
    //   24: ifne +15 -> 39
    //   27: aload_3
    //   28: astore_2
    //   29: getstatic 59	com/android/providers/settings/SettingsBackupAgent:EMPTY_DATA	[B
    //   32: astore_3
    //   33: aconst_null
    //   34: invokestatic 358	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   37: aload_3
    //   38: areturn
    //   39: aload_3
    //   40: astore_2
    //   41: aload_0
    //   42: ldc 117
    //   44: invokevirtual 121	com/android/providers/settings/SettingsBackupAgent:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   47: checkcast 123	android/net/wifi/WifiManager
    //   50: invokevirtual 362	android/net/wifi/WifiManager:getConfiguredNetworks	()Ljava/util/List;
    //   53: astore 6
    //   55: aload_3
    //   56: astore_2
    //   57: new 12	com/android/providers/settings/SettingsBackupAgent$WifiNetworkSettings
    //   60: dup
    //   61: aload_0
    //   62: invokespecial 365	com/android/providers/settings/SettingsBackupAgent$WifiNetworkSettings:<init>	(Lcom/android/providers/settings/SettingsBackupAgent;)V
    //   65: astore 5
    //   67: aload_3
    //   68: astore_2
    //   69: new 80	java/io/BufferedReader
    //   72: dup
    //   73: new 82	java/io/FileReader
    //   76: dup
    //   77: aload 7
    //   79: invokespecial 366	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   82: invokespecial 90	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   85: astore_3
    //   86: aload 5
    //   88: aload_3
    //   89: aload 6
    //   91: iconst_0
    //   92: invokevirtual 370	com/android/providers/settings/SettingsBackupAgent$WifiNetworkSettings:readNetworks	(Ljava/io/BufferedReader;Ljava/util/List;Z)V
    //   95: aload 5
    //   97: getfield 374	com/android/providers/settings/SettingsBackupAgent$WifiNetworkSettings:mKnownNetworks	Ljava/util/HashSet;
    //   100: invokevirtual 379	java/util/HashSet:size	()I
    //   103: ifle +44 -> 147
    //   106: new 273	java/io/ByteArrayOutputStream
    //   109: dup
    //   110: invokespecial 274	java/io/ByteArrayOutputStream:<init>	()V
    //   113: astore_2
    //   114: new 381	java/io/OutputStreamWriter
    //   117: dup
    //   118: aload_2
    //   119: invokespecial 382	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;)V
    //   122: astore 4
    //   124: aload 5
    //   126: aload 4
    //   128: invokevirtual 385	com/android/providers/settings/SettingsBackupAgent$WifiNetworkSettings:write	(Ljava/io/Writer;)V
    //   131: aload 4
    //   133: invokevirtual 386	java/io/OutputStreamWriter:flush	()V
    //   136: aload_2
    //   137: invokevirtual 296	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   140: astore_2
    //   141: aload_3
    //   142: invokestatic 358	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   145: aload_2
    //   146: areturn
    //   147: getstatic 59	com/android/providers/settings/SettingsBackupAgent:EMPTY_DATA	[B
    //   150: astore_2
    //   151: aload_3
    //   152: invokestatic 358	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   155: aload_2
    //   156: areturn
    //   157: astore_2
    //   158: aload 4
    //   160: astore_3
    //   161: aload_3
    //   162: astore_2
    //   163: ldc 102
    //   165: new 218	java/lang/StringBuilder
    //   168: dup
    //   169: invokespecial 219	java/lang/StringBuilder:<init>	()V
    //   172: ldc -35
    //   174: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   177: aload_1
    //   178: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   181: invokevirtual 229	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   184: invokestatic 110	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   187: pop
    //   188: aload_3
    //   189: astore_2
    //   190: getstatic 59	com/android/providers/settings/SettingsBackupAgent:EMPTY_DATA	[B
    //   193: astore_1
    //   194: aload_3
    //   195: invokestatic 358	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   198: aload_1
    //   199: areturn
    //   200: astore_1
    //   201: aload_2
    //   202: invokestatic 358	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   205: aload_1
    //   206: athrow
    //   207: astore_1
    //   208: aload_3
    //   209: astore_2
    //   210: goto -9 -> 201
    //   213: astore_2
    //   214: goto -53 -> 161
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	217	0	this	SettingsBackupAgent
    //   0	217	1	paramString	String
    //   6	150	2	localObject1	Object
    //   157	1	2	localIOException1	IOException
    //   162	48	2	localObject2	Object
    //   213	1	2	localIOException2	IOException
    //   1	208	3	localObject3	Object
    //   3	156	4	localOutputStreamWriter	java.io.OutputStreamWriter
    //   65	60	5	localWifiNetworkSettings	WifiNetworkSettings
    //   53	37	6	localList	List
    //   15	63	7	localFile	File
    // Exception table:
    //   from	to	target	type
    //   7	17	157	java/io/IOException
    //   19	27	157	java/io/IOException
    //   29	33	157	java/io/IOException
    //   41	55	157	java/io/IOException
    //   57	67	157	java/io/IOException
    //   69	86	157	java/io/IOException
    //   7	17	200	finally
    //   19	27	200	finally
    //   29	33	200	finally
    //   41	55	200	finally
    //   57	67	200	finally
    //   69	86	200	finally
    //   163	188	200	finally
    //   190	194	200	finally
    //   86	141	207	finally
    //   147	151	207	finally
    //   86	141	213	java/io/IOException
    //   147	151	213	java/io/IOException
  }
  
  private int readInt(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0xFF) << 24 | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 3)] & 0xFF) << 0;
  }
  
  private long[] readOldChecksums(ParcelFileDescriptor paramParcelFileDescriptor)
    throws IOException
  {
    long[] arrayOfLong = new long[9];
    paramParcelFileDescriptor = new DataInputStream(new FileInputStream(paramParcelFileDescriptor.getFileDescriptor()));
    for (;;)
    {
      int j;
      try
      {
        j = paramParcelFileDescriptor.readInt();
        i = j;
        if (j <= 6) {
          break label77;
        }
        i = 6;
      }
      catch (EOFException localEOFException)
      {
        int i;
        paramParcelFileDescriptor.close();
        return arrayOfLong;
      }
      if (j < STATE_SIZES[i])
      {
        arrayOfLong[j] = paramParcelFileDescriptor.readLong();
        j += 1;
      }
      else
      {
        label77:
        j = 0;
      }
    }
  }
  
  private void restoreFileData(String paramString, byte[] paramArrayOfByte, int paramInt)
  {
    try
    {
      Object localObject = new File(paramString);
      if (((File)localObject).exists()) {
        ((File)localObject).delete();
      }
      localObject = new BufferedOutputStream(new FileOutputStream(paramString, true));
      ((OutputStream)localObject).write(paramArrayOfByte, 0, paramInt);
      ((OutputStream)localObject).close();
      return;
    }
    catch (IOException paramArrayOfByte)
    {
      Log.w("SettingsBackupAgent", "Couldn't restore " + paramString);
    }
  }
  
  private void restoreLockSettings(BackupDataInput paramBackupDataInput)
  {
    byte[] arrayOfByte = new byte[paramBackupDataInput.getDataSize()];
    try
    {
      paramBackupDataInput.readEntityData(arrayOfByte, 0, arrayOfByte.length);
      restoreLockSettings(arrayOfByte, arrayOfByte.length);
      return;
    }
    catch (IOException paramBackupDataInput)
    {
      Log.e("SettingsBackupAgent", "Couldn't read entity data");
    }
  }
  
  private void restoreLockSettings(byte[] paramArrayOfByte, int paramInt)
  {
    LockPatternUtils localLockPatternUtils = new LockPatternUtils(this);
    paramArrayOfByte = new DataInputStream(new ByteArrayInputStream(paramArrayOfByte, 0, paramInt));
    try
    {
      for (;;)
      {
        String str1 = paramArrayOfByte.readUTF();
        if (str1.length() <= 0) {
          break;
        }
        String str2 = paramArrayOfByte.readUTF();
        if (str1.equals("owner_info_enabled")) {
          localLockPatternUtils.setOwnerInfoEnabled("1".equals(str2), UserHandle.myUserId());
        } else if (str1.equals("owner_info")) {
          localLockPatternUtils.setOwnerInfo(str2, UserHandle.myUserId());
        }
      }
      paramArrayOfByte.close();
      return;
    }
    catch (IOException paramArrayOfByte) {}
  }
  
  private void restoreNetworkPolicies(byte[] paramArrayOfByte)
  {
    NetworkPolicyManager localNetworkPolicyManager = (NetworkPolicyManager)getSystemService("netpolicy");
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length != 0))
    {
      paramArrayOfByte = new DataInputStream(new ByteArrayInputStream(paramArrayOfByte));
      try
      {
        i = paramArrayOfByte.readInt();
        if ((i >= 1) && (i <= 1)) {
          break label94;
        }
        throw new BackupUtils.BadVersionException("Unknown Backup Serialization Version");
      }
      catch (NullPointerException|IOException|BackupUtils.BadVersionException paramArrayOfByte)
      {
        Log.e("SettingsBackupAgent", "Failed to convert byte array to NetworkPolicies " + paramArrayOfByte.getMessage());
      }
    }
    return;
    label94:
    int j = paramArrayOfByte.readInt();
    NetworkPolicy[] arrayOfNetworkPolicy = new NetworkPolicy[j];
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        if (paramArrayOfByte.readByte() != 0)
        {
          int k = paramArrayOfByte.readInt();
          byte[] arrayOfByte = new byte[k];
          paramArrayOfByte.read(arrayOfByte, 0, k);
          arrayOfNetworkPolicy[i] = NetworkPolicy.getNetworkPolicyFromBackup(new DataInputStream(new ByteArrayInputStream(arrayOfByte)));
        }
      }
      else
      {
        localNetworkPolicyManager.setNetworkPolicies(arrayOfNetworkPolicy);
        return;
      }
      i += 1;
    }
  }
  
  private void restoreSettings(BackupDataInput paramBackupDataInput, Uri paramUri, HashSet<String> paramHashSet)
  {
    byte[] arrayOfByte = new byte[paramBackupDataInput.getDataSize()];
    try
    {
      paramBackupDataInput.readEntityData(arrayOfByte, 0, arrayOfByte.length);
      restoreSettings(arrayOfByte, arrayOfByte.length, paramUri, paramHashSet);
      return;
    }
    catch (IOException paramBackupDataInput)
    {
      Log.e("SettingsBackupAgent", "Couldn't read entity data");
    }
  }
  
  private void restoreSettings(byte[] paramArrayOfByte, int paramInt, Uri paramUri, HashSet<String> paramHashSet)
  {
    String[] arrayOfString;
    int j;
    HashMap localHashMap;
    ContentValues localContentValues;
    SettingsHelper localSettingsHelper;
    ContentResolver localContentResolver;
    int k;
    label57:
    String str3;
    String str2;
    String str1;
    int i;
    if (paramUri.equals(Settings.Secure.CONTENT_URI))
    {
      arrayOfString = Settings.Secure.SETTINGS_TO_BACKUP;
      j = 0;
      localHashMap = new HashMap();
      localContentValues = new ContentValues(2);
      localSettingsHelper = this.mSettingsHelper;
      localContentResolver = getContentResolver();
      int m = arrayOfString.length;
      k = 0;
      if (k >= m) {
        return;
      }
      str3 = arrayOfString[k];
      str2 = (String)localHashMap.remove(str3);
      str1 = str2;
      i = j;
      if (str2 != null) {}
    }
    for (;;)
    {
      str1 = str2;
      i = j;
      if (j < paramInt)
      {
        i = readInt(paramArrayOfByte, j);
        j += 4;
        if (i <= 0) {
          break label286;
        }
        localObject = new String(paramArrayOfByte, j, i);
        label146:
        j += i;
        i = readInt(paramArrayOfByte, j);
        j += 4;
        if (i <= 0) {
          break label292;
        }
      }
      label286:
      label292:
      for (str1 = new String(paramArrayOfByte, j, i);; str1 = null)
      {
        i = j + i;
        if (!str3.equals(localObject)) {
          break label298;
        }
        if (str1 != null) {
          break label317;
        }
        k += 1;
        j = i;
        break label57;
        if (paramUri.equals(Settings.System.CONTENT_URI))
        {
          arrayOfString = Settings.System.SETTINGS_TO_BACKUP;
          break;
        }
        if (paramUri.equals(Settings.Global.CONTENT_URI))
        {
          arrayOfString = Settings.Global.SETTINGS_TO_BACKUP;
          break;
        }
        throw new IllegalArgumentException("Unknown URI: " + paramUri);
        localObject = null;
        break label146;
      }
      label298:
      localHashMap.put(localObject, str1);
      j = i;
    }
    label317:
    if ((paramHashSet != null) && (paramHashSet.contains(str3))) {}
    for (Object localObject = Settings.Global.CONTENT_URI;; localObject = paramUri)
    {
      localSettingsHelper.restoreValue(this, localContentResolver, localContentValues, (Uri)localObject, str3, str1);
      break;
    }
  }
  
  private void restoreSoftApConfiguration(byte[] paramArrayOfByte)
  {
    WifiManager localWifiManager = (WifiManager)getSystemService("wifi");
    try
    {
      localWifiManager.setWifiApConfiguration(WifiConfiguration.getWifiConfigFromBackup(new DataInputStream(new ByteArrayInputStream(paramArrayOfByte))));
      return;
    }
    catch (IOException|BackupUtils.BadVersionException paramArrayOfByte)
    {
      Log.e("SettingsBackupAgent", "Failed to unMarshal SoftAPConfiguration " + paramArrayOfByte.getMessage());
    }
  }
  
  private void restoreWifiSupplicant(String paramString, byte[] paramArrayOfByte, int paramInt)
  {
    Object localObject;
    int i;
    try
    {
      WifiNetworkSettings localWifiNetworkSettings = new WifiNetworkSettings();
      localObject = new File("/data/misc/wifi/wpa_supplicant.conf");
      if (((File)localObject).exists())
      {
        BufferedReader localBufferedReader = new BufferedReader(new FileReader("/data/misc/wifi/wpa_supplicant.conf"));
        localWifiNetworkSettings.readNetworks(localBufferedReader, null, true);
        localBufferedReader.close();
        ((File)localObject).delete();
      }
      if (paramInt > 0)
      {
        localObject = new char[paramInt];
        i = 0;
        break label169;
        localWifiNetworkSettings.readNetworks(new BufferedReader(new CharArrayReader((char[])localObject)), null, false);
      }
      else
      {
        paramArrayOfByte = new BufferedWriter(new FileWriter("/data/misc/wifi/wpa_supplicant.conf"));
        copyWifiSupplicantTemplate(paramArrayOfByte);
        localWifiNetworkSettings.write(paramArrayOfByte);
        paramArrayOfByte.close();
        return;
      }
    }
    catch (IOException paramArrayOfByte)
    {
      Log.w("SettingsBackupAgent", "Couldn't restore " + paramString);
      return;
    }
    label169:
    while (i < paramInt)
    {
      localObject[i] = ((char)paramArrayOfByte[i]);
      i += 1;
    }
  }
  
  private int writeBytes(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
  {
    System.arraycopy(paramArrayOfByte2, 0, paramArrayOfByte1, paramInt, paramArrayOfByte2.length);
    return paramArrayOfByte2.length + paramInt;
  }
  
  private long writeIfChanged(long paramLong, String paramString, byte[] paramArrayOfByte, BackupDataOutput paramBackupDataOutput)
  {
    CRC32 localCRC32 = new CRC32();
    localCRC32.update(paramArrayOfByte);
    long l = localCRC32.getValue();
    if (paramLong == l) {
      return paramLong;
    }
    try
    {
      paramBackupDataOutput.writeEntityHeader(paramString, paramArrayOfByte.length);
      paramBackupDataOutput.writeEntityData(paramArrayOfByte, paramArrayOfByte.length);
      return l;
    }
    catch (IOException paramString) {}
    return l;
  }
  
  private int writeInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte[(paramInt1 + 0)] = ((byte)(paramInt2 >> 24 & 0xFF));
    paramArrayOfByte[(paramInt1 + 1)] = ((byte)(paramInt2 >> 16 & 0xFF));
    paramArrayOfByte[(paramInt1 + 2)] = ((byte)(paramInt2 >> 8 & 0xFF));
    paramArrayOfByte[(paramInt1 + 3)] = ((byte)(paramInt2 >> 0 & 0xFF));
    return paramInt1 + 4;
  }
  
  private void writeNewChecksums(long[] paramArrayOfLong, ParcelFileDescriptor paramParcelFileDescriptor)
    throws IOException
  {
    paramParcelFileDescriptor = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(paramParcelFileDescriptor.getFileDescriptor())));
    paramParcelFileDescriptor.writeInt(6);
    int i = 0;
    while (i < 9)
    {
      paramParcelFileDescriptor.writeLong(paramArrayOfLong[i]);
      i += 1;
    }
    paramParcelFileDescriptor.close();
  }
  
  void initWifiRestoreIfNecessary()
  {
    if (this.mWifiRestore == null)
    {
      this.mWifiRestore = new WifiRestoreRunnable();
      this.mWifiDisable = new WifiDisableRunnable(this.mWifiRestore);
    }
  }
  
  boolean networkInWhitelist(Network paramNetwork, List<WifiConfiguration> paramList)
  {
    paramNetwork = paramNetwork.configKey();
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      if (Objects.equals(paramNetwork, ((WifiConfiguration)paramList.get(i)).configKey(true))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void onBackup(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2)
    throws IOException
  {
    byte[] arrayOfByte1 = getSystemSettings();
    byte[] arrayOfByte2 = getSecureSettings();
    byte[] arrayOfByte3 = getGlobalSettings();
    byte[] arrayOfByte4 = getLockSettings();
    byte[] arrayOfByte5 = this.mSettingsHelper.getLocaleData();
    byte[] arrayOfByte6 = getWifiSupplicant("/data/misc/wifi/wpa_supplicant.conf");
    byte[] arrayOfByte7 = getFileData(this.mWifiConfigFile);
    byte[] arrayOfByte8 = getSoftAPConfiguration();
    byte[] arrayOfByte9 = getNetworkPolicies();
    paramParcelFileDescriptor1 = readOldChecksums(paramParcelFileDescriptor1);
    paramParcelFileDescriptor1[0] = writeIfChanged(paramParcelFileDescriptor1[0], "system", arrayOfByte1, paramBackupDataOutput);
    paramParcelFileDescriptor1[1] = writeIfChanged(paramParcelFileDescriptor1[1], "secure", arrayOfByte2, paramBackupDataOutput);
    paramParcelFileDescriptor1[5] = writeIfChanged(paramParcelFileDescriptor1[5], "global", arrayOfByte3, paramBackupDataOutput);
    paramParcelFileDescriptor1[2] = writeIfChanged(paramParcelFileDescriptor1[2], "locale", arrayOfByte5, paramBackupDataOutput);
    paramParcelFileDescriptor1[3] = writeIfChanged(paramParcelFileDescriptor1[3], "￭WIFI", arrayOfByte6, paramBackupDataOutput);
    paramParcelFileDescriptor1[4] = writeIfChanged(paramParcelFileDescriptor1[4], "￭CONFIG_WIFI", arrayOfByte7, paramBackupDataOutput);
    paramParcelFileDescriptor1[6] = writeIfChanged(paramParcelFileDescriptor1[6], "lock_settings", arrayOfByte4, paramBackupDataOutput);
    paramParcelFileDescriptor1[7] = writeIfChanged(paramParcelFileDescriptor1[7], "softap_config", arrayOfByte8, paramBackupDataOutput);
    paramParcelFileDescriptor1[8] = writeIfChanged(paramParcelFileDescriptor1[8], "network_policies", arrayOfByte9, paramBackupDataOutput);
    writeNewChecksums(paramParcelFileDescriptor1, paramParcelFileDescriptor2);
  }
  
  public void onCreate()
  {
    this.mSettingsHelper = new SettingsHelper(this);
    super.onCreate();
    WifiManager localWifiManager = (WifiManager)getSystemService("wifi");
    if (localWifiManager != null) {
      this.mWifiConfigFile = localWifiManager.getConfigFile();
    }
  }
  
  public void onFullBackup(FullBackupDataOutput paramFullBackupDataOutput)
    throws IOException
  {
    byte[] arrayOfByte1 = getSystemSettings();
    byte[] arrayOfByte2 = getSecureSettings();
    byte[] arrayOfByte3 = getGlobalSettings();
    byte[] arrayOfByte4 = getLockSettings();
    byte[] arrayOfByte5 = this.mSettingsHelper.getLocaleData();
    byte[] arrayOfByte6 = getWifiSupplicant("/data/misc/wifi/wpa_supplicant.conf");
    byte[] arrayOfByte7 = getFileData(this.mWifiConfigFile);
    byte[] arrayOfByte8 = getSoftAPConfiguration();
    byte[] arrayOfByte9 = getNetworkPolicies();
    File localFile = new File(getFilesDir().getAbsolutePath(), "flattened-data");
    try
    {
      DataOutputStream localDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(localFile)));
      localDataOutputStream.writeInt(5);
      localDataOutputStream.writeInt(arrayOfByte1.length);
      localDataOutputStream.write(arrayOfByte1);
      localDataOutputStream.writeInt(arrayOfByte2.length);
      localDataOutputStream.write(arrayOfByte2);
      localDataOutputStream.writeInt(arrayOfByte3.length);
      localDataOutputStream.write(arrayOfByte3);
      localDataOutputStream.writeInt(arrayOfByte5.length);
      localDataOutputStream.write(arrayOfByte5);
      localDataOutputStream.writeInt(arrayOfByte6.length);
      localDataOutputStream.write(arrayOfByte6);
      localDataOutputStream.writeInt(arrayOfByte7.length);
      localDataOutputStream.write(arrayOfByte7);
      localDataOutputStream.writeInt(arrayOfByte4.length);
      localDataOutputStream.write(arrayOfByte4);
      localDataOutputStream.writeInt(arrayOfByte8.length);
      localDataOutputStream.write(arrayOfByte8);
      localDataOutputStream.writeInt(arrayOfByte9.length);
      localDataOutputStream.write(arrayOfByte9);
      localDataOutputStream.flush();
      fullBackupFile(localFile, paramFullBackupDataOutput);
      return;
    }
    finally
    {
      localFile.delete();
    }
  }
  
  public void onRestore(BackupDataInput paramBackupDataInput, int paramInt, ParcelFileDescriptor paramParcelFileDescriptor)
    throws IOException
  {
    paramParcelFileDescriptor = new HashSet();
    Settings.System.getMovedToGlobalSettings(paramParcelFileDescriptor);
    Settings.Secure.getMovedToGlobalSettings(paramParcelFileDescriptor);
    while (paramBackupDataInput.readNextHeader())
    {
      Object localObject = paramBackupDataInput.getKey();
      paramInt = paramBackupDataInput.getDataSize();
      if (((String)localObject).equals("system"))
      {
        restoreSettings(paramBackupDataInput, Settings.System.CONTENT_URI, paramParcelFileDescriptor);
        this.mSettingsHelper.applyAudioSettings();
      }
      else if (((String)localObject).equals("secure"))
      {
        restoreSettings(paramBackupDataInput, Settings.Secure.CONTENT_URI, paramParcelFileDescriptor);
      }
      else if (((String)localObject).equals("global"))
      {
        restoreSettings(paramBackupDataInput, Settings.Global.CONTENT_URI, null);
      }
      else if (((String)localObject).equals("￭WIFI"))
      {
        initWifiRestoreIfNecessary();
        this.mWifiRestore.incorporateWifiSupplicant(paramBackupDataInput);
      }
      else if (((String)localObject).equals("locale"))
      {
        localObject = new byte[paramInt];
        paramBackupDataInput.readEntityData((byte[])localObject, 0, paramInt);
        this.mSettingsHelper.setLocaleData((byte[])localObject, paramInt);
      }
      else if (((String)localObject).equals("￭CONFIG_WIFI"))
      {
        initWifiRestoreIfNecessary();
        this.mWifiRestore.incorporateWifiConfigFile(paramBackupDataInput);
      }
      else if (((String)localObject).equals("lock_settings"))
      {
        restoreLockSettings(paramBackupDataInput);
      }
      else if (((String)localObject).equals("softap_config"))
      {
        localObject = new byte[paramInt];
        paramBackupDataInput.readEntityData((byte[])localObject, 0, paramInt);
        restoreSoftApConfiguration((byte[])localObject);
      }
      else if (((String)localObject).equals("network_policies"))
      {
        localObject = new byte[paramInt];
        paramBackupDataInput.readEntityData((byte[])localObject, 0, paramInt);
        restoreNetworkPolicies((byte[])localObject);
      }
      else
      {
        paramBackupDataInput.skipEntityData();
      }
    }
    if (this.mWifiRestore != null)
    {
      long l = Settings.Global.getLong(getContentResolver(), "wifi_bounce_delay_override_ms", 60000L);
      new Handler(getMainLooper()).postDelayed(this.mWifiDisable, l);
    }
  }
  
  public void onRestoreFile(ParcelFileDescriptor paramParcelFileDescriptor, long paramLong1, int paramInt, String paramString1, String paramString2, long paramLong2, long paramLong3)
    throws IOException
  {
    DataInputStream localDataInputStream = new DataInputStream(new FileInputStream(paramParcelFileDescriptor.getFileDescriptor()));
    paramInt = localDataInputStream.readInt();
    if (paramInt <= 5)
    {
      paramString2 = new HashSet();
      Settings.System.getMovedToGlobalSettings(paramString2);
      Settings.Secure.getMovedToGlobalSettings(paramString2);
      int i = localDataInputStream.readInt();
      paramString1 = new byte[i];
      localDataInputStream.readFully(paramString1, 0, i);
      restoreSettings(paramString1, i, Settings.System.CONTENT_URI, paramString2);
      i = localDataInputStream.readInt();
      paramParcelFileDescriptor = paramString1;
      if (i > paramString1.length) {
        paramParcelFileDescriptor = new byte[i];
      }
      localDataInputStream.readFully(paramParcelFileDescriptor, 0, i);
      restoreSettings(paramParcelFileDescriptor, i, Settings.Secure.CONTENT_URI, paramString2);
      paramString1 = paramParcelFileDescriptor;
      if (paramInt >= 2)
      {
        i = localDataInputStream.readInt();
        paramString1 = paramParcelFileDescriptor;
        if (i > paramParcelFileDescriptor.length) {
          paramString1 = new byte[i];
        }
        localDataInputStream.readFully(paramString1, 0, i);
        paramString2.clear();
        restoreSettings(paramString1, i, Settings.Global.CONTENT_URI, paramString2);
      }
      i = localDataInputStream.readInt();
      paramParcelFileDescriptor = paramString1;
      if (i > paramString1.length) {
        paramParcelFileDescriptor = new byte[i];
      }
      localDataInputStream.readFully(paramParcelFileDescriptor, 0, i);
      this.mSettingsHelper.setLocaleData(paramParcelFileDescriptor, i);
      i = localDataInputStream.readInt();
      paramString2 = paramParcelFileDescriptor;
      if (i > paramParcelFileDescriptor.length) {
        paramString2 = new byte[i];
      }
      localDataInputStream.readFully(paramString2, 0, i);
      int j = enableWifi(false);
      restoreWifiSupplicant("/data/misc/wifi/wpa_supplicant.conf", paramString2, i);
      FileUtils.setPermissions("/data/misc/wifi/wpa_supplicant.conf", 432, Process.myUid(), 1010);
      boolean bool;
      if (j != 3)
      {
        if (j != 2) {
          break label549;
        }
        bool = true;
      }
      for (;;)
      {
        enableWifi(bool);
        i = localDataInputStream.readInt();
        paramString1 = paramString2;
        if (i > paramString2.length) {
          paramString1 = new byte[i];
        }
        localDataInputStream.readFully(paramString1, 0, i);
        restoreFileData(this.mWifiConfigFile, paramString1, i);
        paramParcelFileDescriptor = paramString1;
        if (paramInt >= 3)
        {
          i = localDataInputStream.readInt();
          paramString2 = paramString1;
          if (i > paramString1.length) {
            paramString2 = new byte[i];
          }
          paramParcelFileDescriptor = paramString2;
          if (i > 0)
          {
            localDataInputStream.readFully(paramString2, 0, i);
            restoreLockSettings(paramString2, i);
            paramParcelFileDescriptor = paramString2;
          }
        }
        paramString1 = paramParcelFileDescriptor;
        if (paramInt >= 4)
        {
          i = localDataInputStream.readInt();
          paramString2 = paramParcelFileDescriptor;
          if (i > paramParcelFileDescriptor.length) {
            paramString2 = new byte[i];
          }
          paramString1 = paramString2;
          if (i > 0)
          {
            localDataInputStream.readFully(paramString2, 0, i);
            restoreSoftApConfiguration(paramString2);
            paramString1 = paramString2;
          }
        }
        if (paramInt >= 5)
        {
          paramInt = localDataInputStream.readInt();
          paramParcelFileDescriptor = paramString1;
          if (paramInt > paramString1.length) {
            paramParcelFileDescriptor = new byte[paramInt];
          }
          if (paramInt > 0)
          {
            localDataInputStream.readFully(paramParcelFileDescriptor, 0, paramInt);
            restoreNetworkPolicies(paramParcelFileDescriptor);
          }
        }
        return;
        bool = true;
        continue;
        label549:
        bool = false;
      }
    }
    paramParcelFileDescriptor.close();
    throw new IOException("Invalid file schema");
  }
  
  static class Network
  {
    boolean certUsed = false;
    boolean hasWepKey = false;
    boolean isEap = false;
    String key_mgmt = "";
    final ArrayList<String> rawLines = new ArrayList();
    String ssid = "";
    
    public static Network readFromStream(BufferedReader paramBufferedReader)
    {
      localNetwork = new Network();
      try
      {
        while (paramBufferedReader.ready())
        {
          String str = paramBufferedReader.readLine();
          if (str == null) {
            break;
          }
          if (str.startsWith("}")) {
            return localNetwork;
          }
          localNetwork.rememberLine(str);
        }
        return localNetwork;
      }
      catch (IOException paramBufferedReader)
      {
        return null;
      }
    }
    
    public String configKey()
    {
      if (this.ssid == null) {
        return null;
      }
      String str1 = this.ssid.substring(this.ssid.indexOf('=') + 1);
      BitSet localBitSet = new BitSet();
      if (this.key_mgmt == null)
      {
        localBitSet.set(1);
        localBitSet.set(2);
        if (localBitSet.get(1)) {
          return str1 + android.net.wifi.WifiConfiguration.KeyMgmt.strings[1];
        }
      }
      else
      {
        String[] arrayOfString = this.key_mgmt.substring(this.key_mgmt.indexOf('=') + 1).split("\\s+");
        int i = 0;
        label111:
        String str2;
        if (i < arrayOfString.length)
        {
          str2 = arrayOfString[i];
          if (!str2.equals("WPA-PSK")) {
            break label154;
          }
          Log.v("SettingsBackupAgent", "  + setting WPA_PSK bit");
          localBitSet.set(1);
        }
        for (;;)
        {
          i += 1;
          break label111;
          break;
          label154:
          if (str2.equals("WPA-EAP"))
          {
            Log.v("SettingsBackupAgent", "  + setting WPA_EAP bit");
            localBitSet.set(2);
          }
          else if (str2.equals("IEEE8021X"))
          {
            Log.v("SettingsBackupAgent", "  + setting IEEE8021X bit");
            localBitSet.set(3);
          }
        }
      }
      if ((localBitSet.get(2)) || (localBitSet.get(3))) {
        return str1 + android.net.wifi.WifiConfiguration.KeyMgmt.strings[2];
      }
      if (this.hasWepKey) {
        return str1 + "WEP";
      }
      return str1 + android.net.wifi.WifiConfiguration.KeyMgmt.strings[0];
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = false;
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof Network)) {
        return false;
      }
      try
      {
        paramObject = (Network)paramObject;
        if (this.ssid.equals(((Network)paramObject).ssid)) {
          bool = this.key_mgmt.equals(((Network)paramObject).key_mgmt);
        }
        return bool;
      }
      catch (ClassCastException paramObject) {}
      return false;
    }
    
    public int hashCode()
    {
      return (this.ssid.hashCode() + 527) * 31 + this.key_mgmt.hashCode();
    }
    
    void rememberLine(String paramString)
    {
      paramString = paramString.trim();
      if (paramString.isEmpty()) {
        return;
      }
      this.rawLines.add(paramString);
      if (paramString.startsWith("ssid=")) {
        this.ssid = paramString;
      }
      do
      {
        do
        {
          return;
          if (!paramString.startsWith("key_mgmt=")) {
            break;
          }
          this.key_mgmt = paramString;
        } while (!paramString.contains("EAP"));
        this.isEap = true;
        return;
        if (paramString.startsWith("client_cert="))
        {
          this.certUsed = true;
          return;
        }
        if (paramString.startsWith("ca_cert="))
        {
          this.certUsed = true;
          return;
        }
        if (paramString.startsWith("ca_path="))
        {
          this.certUsed = true;
          return;
        }
        if (paramString.startsWith("wep_"))
        {
          this.hasWepKey = true;
          return;
        }
      } while (!paramString.startsWith("eap="));
      this.isEap = true;
    }
    
    public void write(Writer paramWriter)
      throws IOException
    {
      paramWriter.write("\nnetwork={\n");
      Iterator localIterator = this.rawLines.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        paramWriter.write("\t" + str + "\n");
      }
      paramWriter.write("}\n");
    }
  }
  
  class WifiDisableRunnable
    implements Runnable
  {
    final SettingsBackupAgent.WifiRestoreRunnable mNextPhase;
    
    public WifiDisableRunnable(SettingsBackupAgent.WifiRestoreRunnable paramWifiRestoreRunnable)
    {
      this.mNextPhase = paramWifiRestoreRunnable;
    }
    
    public void run()
    {
      ContentResolver localContentResolver = SettingsBackupAgent.this.getContentResolver();
      int i = Settings.Global.getInt(localContentResolver, "wifi_scan_always_enabled", 0);
      int j = SettingsBackupAgent.-wrap0(SettingsBackupAgent.this, false);
      if (i != 0) {
        Settings.Global.putInt(localContentResolver, "wifi_scan_always_enabled", 0);
      }
      this.mNextPhase.setPriorState(j, i);
      new Handler(SettingsBackupAgent.this.getMainLooper()).postDelayed(this.mNextPhase, 2500L);
    }
  }
  
  class WifiNetworkSettings
  {
    final HashSet<SettingsBackupAgent.Network> mKnownNetworks = new HashSet();
    final ArrayList<SettingsBackupAgent.Network> mNetworks = new ArrayList(8);
    
    WifiNetworkSettings() {}
    
    public void readNetworks(BufferedReader paramBufferedReader, List<WifiConfiguration> paramList, boolean paramBoolean)
    {
      try
      {
        while (paramBufferedReader.ready())
        {
          Object localObject = paramBufferedReader.readLine();
          if ((localObject != null) && (((String)localObject).startsWith("network")))
          {
            localObject = SettingsBackupAgent.Network.readFromStream(paramBufferedReader);
            if (((paramList == null) || (SettingsBackupAgent.this.networkInWhitelist((SettingsBackupAgent.Network)localObject, paramList))) && ((!((SettingsBackupAgent.Network)localObject).isEap) || (paramBoolean)) && (!this.mKnownNetworks.contains(localObject)))
            {
              this.mKnownNetworks.add(localObject);
              this.mNetworks.add(localObject);
            }
          }
        }
        return;
      }
      catch (IOException paramBufferedReader) {}
    }
    
    public void write(Writer paramWriter)
      throws IOException
    {
      Iterator localIterator = this.mNetworks.iterator();
      while (localIterator.hasNext())
      {
        SettingsBackupAgent.Network localNetwork = (SettingsBackupAgent.Network)localIterator.next();
        if ((!localNetwork.certUsed) && (!localNetwork.isEap)) {
          localNetwork.write(paramWriter);
        }
      }
    }
  }
  
  class WifiRestoreRunnable
    implements Runnable
  {
    private byte[] restoredSupplicantData;
    private byte[] restoredWifiConfigFile;
    private int retainedWifiState;
    private int scanAlways;
    
    WifiRestoreRunnable() {}
    
    void incorporateWifiConfigFile(BackupDataInput paramBackupDataInput)
    {
      this.restoredWifiConfigFile = new byte[paramBackupDataInput.getDataSize()];
      if (this.restoredWifiConfigFile.length <= 0) {
        return;
      }
      try
      {
        paramBackupDataInput.readEntityData(this.restoredWifiConfigFile, 0, paramBackupDataInput.getDataSize());
        return;
      }
      catch (IOException paramBackupDataInput)
      {
        Log.w("SettingsBackupAgent", "Unable to read config file");
        this.restoredWifiConfigFile = null;
      }
    }
    
    void incorporateWifiSupplicant(BackupDataInput paramBackupDataInput)
    {
      this.restoredSupplicantData = new byte[paramBackupDataInput.getDataSize()];
      if (this.restoredSupplicantData.length <= 0) {
        return;
      }
      try
      {
        paramBackupDataInput.readEntityData(this.restoredSupplicantData, 0, paramBackupDataInput.getDataSize());
        return;
      }
      catch (IOException paramBackupDataInput)
      {
        Log.w("SettingsBackupAgent", "Unable to read supplicant data");
        this.restoredSupplicantData = null;
      }
    }
    
    public void run()
    {
      boolean bool2 = true;
      SettingsBackupAgent localSettingsBackupAgent;
      if ((this.restoredSupplicantData != null) || (this.restoredWifiConfigFile != null))
      {
        if (this.restoredSupplicantData != null)
        {
          SettingsBackupAgent.-wrap2(SettingsBackupAgent.this, "/data/misc/wifi/wpa_supplicant.conf", this.restoredSupplicantData, this.restoredSupplicantData.length);
          FileUtils.setPermissions("/data/misc/wifi/wpa_supplicant.conf", 432, Process.myUid(), 1010);
        }
        if (this.restoredWifiConfigFile != null) {
          SettingsBackupAgent.-wrap1(SettingsBackupAgent.this, SettingsBackupAgent.-get0(SettingsBackupAgent.this), this.restoredWifiConfigFile, this.restoredWifiConfigFile.length);
        }
        if (this.scanAlways != 0) {
          Settings.Global.putInt(SettingsBackupAgent.this.getContentResolver(), "wifi_scan_always_enabled", this.scanAlways);
        }
        localSettingsBackupAgent = SettingsBackupAgent.this;
        bool1 = bool2;
        if (this.retainedWifiState != 3) {
          if (this.retainedWifiState != 2) {
            break label142;
          }
        }
      }
      label142:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        SettingsBackupAgent.-wrap0(localSettingsBackupAgent, bool1);
        return;
      }
    }
    
    void setPriorState(int paramInt1, int paramInt2)
    {
      this.retainedWifiState = paramInt1;
      this.scanAlways = paramInt2;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/providers/settings/SettingsBackupAgent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */