package android.net;

import android.util.Log;
import java.net.InetAddress;
import java.util.Arrays;

public class SntpClient
{
  private static final boolean DBG = true;
  private static final int NTP_LEAP_NOSYNC = 3;
  private static final int NTP_MODE_BROADCAST = 5;
  private static final int NTP_MODE_CLIENT = 3;
  private static final int NTP_MODE_SERVER = 4;
  private static final int NTP_PACKET_SIZE = 48;
  private static final int NTP_PORT = 123;
  private static final int NTP_STRATUM_DEATH = 0;
  private static final int NTP_STRATUM_MAX = 15;
  private static final int NTP_VERSION = 3;
  private static final long OFFSET_1900_TO_1970 = 2208988800L;
  private static final int ORIGINATE_TIME_OFFSET = 24;
  private static final int RECEIVE_TIME_OFFSET = 32;
  private static final int REFERENCE_TIME_OFFSET = 16;
  private static final String TAG = "SntpClient";
  private static final int TRANSMIT_TIME_OFFSET = 40;
  private long mNtpTime;
  private long mNtpTimeReference;
  private long mRoundTripTime;
  
  private static void checkValidServerReply(byte paramByte1, byte paramByte2, int paramInt, long paramLong)
    throws SntpClient.InvalidServerReplyException
  {
    if (paramByte1 == 3) {
      throw new InvalidServerReplyException("unsynchronized server");
    }
    if ((paramByte2 != 4) && (paramByte2 != 5)) {
      throw new InvalidServerReplyException("untrusted mode: " + paramByte2);
    }
    if ((paramInt == 0) || (paramInt > 15)) {
      throw new InvalidServerReplyException("untrusted stratum: " + paramInt);
    }
    if (paramLong == 0L) {
      throw new InvalidServerReplyException("zero transmitTime");
    }
  }
  
  private long read32(byte[] paramArrayOfByte, int paramInt)
  {
    int m = paramArrayOfByte[paramInt];
    int i = paramArrayOfByte[(paramInt + 1)];
    int j = paramArrayOfByte[(paramInt + 2)];
    int k = paramArrayOfByte[(paramInt + 3)];
    if ((m & 0x80) == 128)
    {
      paramInt = (m & 0x7F) + 128;
      if ((i & 0x80) != 128) {
        break label142;
      }
      i = (i & 0x7F) + 128;
      label67:
      if ((j & 0x80) != 128) {
        break label145;
      }
      j = (j & 0x7F) + 128;
      label90:
      if ((k & 0x80) != 128) {
        break label148;
      }
      k = (k & 0x7F) + 128;
    }
    label142:
    label145:
    label148:
    for (;;)
    {
      return (paramInt << 24) + (i << 16) + (j << 8) + k;
      paramInt = m;
      break;
      break label67;
      break label90;
    }
  }
  
  private long readTimeStamp(byte[] paramArrayOfByte, int paramInt)
  {
    long l1 = read32(paramArrayOfByte, paramInt);
    long l2 = read32(paramArrayOfByte, paramInt + 4);
    if ((l1 == 0L) && (l2 == 0L)) {
      return 0L;
    }
    return (l1 - 2208988800L) * 1000L + l2 * 1000L / 4294967296L;
  }
  
  private void writeTimeStamp(byte[] paramArrayOfByte, int paramInt, long paramLong)
  {
    if (paramLong == 0L)
    {
      Arrays.fill(paramArrayOfByte, paramInt, paramInt + 8, (byte)0);
      return;
    }
    long l1 = paramLong / 1000L;
    long l2 = l1 + 2208988800L;
    int i = paramInt + 1;
    paramArrayOfByte[paramInt] = ((byte)(int)(l2 >> 24));
    paramInt = i + 1;
    paramArrayOfByte[i] = ((byte)(int)(l2 >> 16));
    i = paramInt + 1;
    paramArrayOfByte[paramInt] = ((byte)(int)(l2 >> 8));
    paramInt = i + 1;
    paramArrayOfByte[i] = ((byte)(int)(l2 >> 0));
    paramLong = 4294967296L * (paramLong - 1000L * l1) / 1000L;
    i = paramInt + 1;
    paramArrayOfByte[paramInt] = ((byte)(int)(paramLong >> 24));
    paramInt = i + 1;
    paramArrayOfByte[i] = ((byte)(int)(paramLong >> 16));
    i = paramInt + 1;
    paramArrayOfByte[paramInt] = ((byte)(int)(paramLong >> 8));
    paramArrayOfByte[i] = ((byte)(int)(Math.random() * 255.0D));
  }
  
  public long getNtpTime()
  {
    return this.mNtpTime;
  }
  
  public long getNtpTimeReference()
  {
    return this.mNtpTimeReference;
  }
  
  public long getRoundTripTime()
  {
    return this.mRoundTripTime;
  }
  
  public boolean requestTime(String paramString, int paramInt)
  {
    try
    {
      paramString = InetAddress.getByName(paramString);
      return requestTime(paramString, 123, paramInt);
    }
    catch (Exception paramString)
    {
      Log.d("SntpClient", "request time failed: " + paramString);
    }
    return false;
  }
  
  /* Error */
  public boolean requestTime(InetAddress paramInetAddress, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 18
    //   3: aconst_null
    //   4: astore 20
    //   6: new 140	java/net/DatagramSocket
    //   9: dup
    //   10: invokespecial 141	java/net/DatagramSocket:<init>	()V
    //   13: astore 19
    //   15: aload 19
    //   17: iload_3
    //   18: invokevirtual 145	java/net/DatagramSocket:setSoTimeout	(I)V
    //   21: bipush 48
    //   23: newarray <illegal type>
    //   25: astore 18
    //   27: new 147	java/net/DatagramPacket
    //   30: dup
    //   31: aload 18
    //   33: aload 18
    //   35: arraylength
    //   36: aload_1
    //   37: iload_2
    //   38: invokespecial 150	java/net/DatagramPacket:<init>	([BILjava/net/InetAddress;I)V
    //   41: astore_1
    //   42: aload 18
    //   44: iconst_0
    //   45: bipush 27
    //   47: bastore
    //   48: invokestatic 155	java/lang/System:currentTimeMillis	()J
    //   51: lstore 8
    //   53: invokestatic 160	android/os/SystemClock:elapsedRealtime	()J
    //   56: lstore 10
    //   58: aload_0
    //   59: aload 18
    //   61: bipush 40
    //   63: lload 8
    //   65: invokespecial 162	android/net/SntpClient:writeTimeStamp	([BIJ)V
    //   68: aload 19
    //   70: aload_1
    //   71: invokevirtual 166	java/net/DatagramSocket:send	(Ljava/net/DatagramPacket;)V
    //   74: aload 19
    //   76: new 147	java/net/DatagramPacket
    //   79: dup
    //   80: aload 18
    //   82: aload 18
    //   84: arraylength
    //   85: invokespecial 169	java/net/DatagramPacket:<init>	([BI)V
    //   88: invokevirtual 172	java/net/DatagramSocket:receive	(Ljava/net/DatagramPacket;)V
    //   91: invokestatic 160	android/os/SystemClock:elapsedRealtime	()J
    //   94: lstore 6
    //   96: lload 8
    //   98: lload 6
    //   100: lload 10
    //   102: lsub
    //   103: ladd
    //   104: lstore 8
    //   106: aload 18
    //   108: iconst_0
    //   109: baload
    //   110: bipush 6
    //   112: ishr
    //   113: iconst_3
    //   114: iand
    //   115: i2b
    //   116: istore 4
    //   118: aload 18
    //   120: iconst_0
    //   121: baload
    //   122: bipush 7
    //   124: iand
    //   125: i2b
    //   126: istore 5
    //   128: aload 18
    //   130: iconst_1
    //   131: baload
    //   132: istore_2
    //   133: aload_0
    //   134: aload 18
    //   136: bipush 24
    //   138: invokespecial 174	android/net/SntpClient:readTimeStamp	([BI)J
    //   141: lstore 12
    //   143: aload_0
    //   144: aload 18
    //   146: bipush 32
    //   148: invokespecial 174	android/net/SntpClient:readTimeStamp	([BI)J
    //   151: lstore 14
    //   153: aload_0
    //   154: aload 18
    //   156: bipush 40
    //   158: invokespecial 174	android/net/SntpClient:readTimeStamp	([BI)J
    //   161: lstore 16
    //   163: iload 4
    //   165: iload 5
    //   167: iload_2
    //   168: sipush 255
    //   171: iand
    //   172: lload 16
    //   174: invokestatic 176	android/net/SntpClient:checkValidServerReply	(BBIJ)V
    //   177: lload 6
    //   179: lload 10
    //   181: lsub
    //   182: lload 16
    //   184: lload 14
    //   186: lsub
    //   187: lsub
    //   188: lstore 10
    //   190: lload 14
    //   192: lload 12
    //   194: lsub
    //   195: lload 16
    //   197: lload 8
    //   199: lsub
    //   200: ladd
    //   201: ldc2_w 177
    //   204: ldiv
    //   205: lstore 12
    //   207: ldc 41
    //   209: new 60	java/lang/StringBuilder
    //   212: dup
    //   213: invokespecial 61	java/lang/StringBuilder:<init>	()V
    //   216: ldc -76
    //   218: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   221: lload 10
    //   223: invokevirtual 183	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   226: ldc -71
    //   228: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   231: ldc -69
    //   233: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   236: lload 12
    //   238: invokevirtual 183	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   241: ldc -67
    //   243: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   246: invokevirtual 74	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   249: invokestatic 138	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   252: pop
    //   253: aload_0
    //   254: lload 8
    //   256: lload 12
    //   258: ladd
    //   259: putfield 108	android/net/SntpClient:mNtpTime	J
    //   262: aload_0
    //   263: lload 6
    //   265: putfield 111	android/net/SntpClient:mNtpTimeReference	J
    //   268: aload_0
    //   269: lload 10
    //   271: putfield 114	android/net/SntpClient:mRoundTripTime	J
    //   274: aload 19
    //   276: ifnull +8 -> 284
    //   279: aload 19
    //   281: invokevirtual 192	java/net/DatagramSocket:close	()V
    //   284: iconst_1
    //   285: ireturn
    //   286: astore 19
    //   288: aload 20
    //   290: astore_1
    //   291: aload_1
    //   292: astore 18
    //   294: ldc 41
    //   296: new 60	java/lang/StringBuilder
    //   299: dup
    //   300: invokespecial 61	java/lang/StringBuilder:<init>	()V
    //   303: ldc -127
    //   305: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   308: aload 19
    //   310: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   313: invokevirtual 74	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   316: invokestatic 138	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   319: pop
    //   320: aload_1
    //   321: ifnull +7 -> 328
    //   324: aload_1
    //   325: invokevirtual 192	java/net/DatagramSocket:close	()V
    //   328: iconst_0
    //   329: ireturn
    //   330: astore_1
    //   331: aload 18
    //   333: ifnull +8 -> 341
    //   336: aload 18
    //   338: invokevirtual 192	java/net/DatagramSocket:close	()V
    //   341: aload_1
    //   342: athrow
    //   343: astore_1
    //   344: aload 19
    //   346: astore 18
    //   348: goto -17 -> 331
    //   351: astore 18
    //   353: aload 19
    //   355: astore_1
    //   356: aload 18
    //   358: astore 19
    //   360: goto -69 -> 291
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	363	0	this	SntpClient
    //   0	363	1	paramInetAddress	InetAddress
    //   0	363	2	paramInt1	int
    //   0	363	3	paramInt2	int
    //   116	48	4	b1	byte
    //   126	40	5	b2	byte
    //   94	170	6	l1	long
    //   51	204	8	l2	long
    //   56	214	10	l3	long
    //   141	116	12	l4	long
    //   151	40	14	l5	long
    //   161	35	16	l6	long
    //   1	346	18	localObject1	Object
    //   351	6	18	localException1	Exception
    //   13	267	19	localDatagramSocket	java.net.DatagramSocket
    //   286	68	19	localException2	Exception
    //   358	1	19	localException3	Exception
    //   4	285	20	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   6	15	286	java/lang/Exception
    //   6	15	330	finally
    //   294	320	330	finally
    //   15	42	343	finally
    //   48	96	343	finally
    //   133	177	343	finally
    //   190	274	343	finally
    //   15	42	351	java/lang/Exception
    //   48	96	351	java/lang/Exception
    //   133	177	351	java/lang/Exception
    //   190	274	351	java/lang/Exception
  }
  
  private static class InvalidServerReplyException
    extends Exception
  {
    public InvalidServerReplyException(String paramString)
    {
      super();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/SntpClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */