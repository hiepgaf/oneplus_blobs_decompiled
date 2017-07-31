package android.os;

import android.system.OsConstants;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

class CommonTimeUtils
{
  public static final int ERROR = -1;
  public static final int ERROR_BAD_VALUE = -4;
  public static final int ERROR_DEAD_OBJECT = -7;
  public static final int SUCCESS = 0;
  private String mInterfaceDesc;
  private IBinder mRemote;
  
  public CommonTimeUtils(IBinder paramIBinder, String paramString)
  {
    this.mRemote = paramIBinder;
    this.mInterfaceDesc = paramString;
  }
  
  /* Error */
  public int transactGetInt(int paramInt1, int paramInt2)
    throws RemoteException
  {
    // Byte code:
    //   0: invokestatic 37	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   3: astore_3
    //   4: invokestatic 37	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   7: astore 4
    //   9: aload_3
    //   10: aload_0
    //   11: getfield 26	android/os/CommonTimeUtils:mInterfaceDesc	Ljava/lang/String;
    //   14: invokevirtual 41	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   17: aload_0
    //   18: getfield 24	android/os/CommonTimeUtils:mRemote	Landroid/os/IBinder;
    //   21: iload_1
    //   22: aload_3
    //   23: aload 4
    //   25: iconst_0
    //   26: invokeinterface 47 5 0
    //   31: pop
    //   32: aload 4
    //   34: invokevirtual 51	android/os/Parcel:readInt	()I
    //   37: ifne +20 -> 57
    //   40: aload 4
    //   42: invokevirtual 51	android/os/Parcel:readInt	()I
    //   45: istore_1
    //   46: aload 4
    //   48: invokevirtual 54	android/os/Parcel:recycle	()V
    //   51: aload_3
    //   52: invokevirtual 54	android/os/Parcel:recycle	()V
    //   55: iload_1
    //   56: ireturn
    //   57: iload_2
    //   58: istore_1
    //   59: goto -13 -> 46
    //   62: astore 5
    //   64: aload 4
    //   66: invokevirtual 54	android/os/Parcel:recycle	()V
    //   69: aload_3
    //   70: invokevirtual 54	android/os/Parcel:recycle	()V
    //   73: aload 5
    //   75: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	76	0	this	CommonTimeUtils
    //   0	76	1	paramInt1	int
    //   0	76	2	paramInt2	int
    //   3	67	3	localParcel1	Parcel
    //   7	58	4	localParcel2	Parcel
    //   62	12	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	46	62	finally
  }
  
  /* Error */
  public long transactGetLong(int paramInt, long paramLong)
    throws RemoteException
  {
    // Byte code:
    //   0: invokestatic 37	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   3: astore 4
    //   5: invokestatic 37	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   8: astore 5
    //   10: aload 4
    //   12: aload_0
    //   13: getfield 26	android/os/CommonTimeUtils:mInterfaceDesc	Ljava/lang/String;
    //   16: invokevirtual 41	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   19: aload_0
    //   20: getfield 24	android/os/CommonTimeUtils:mRemote	Landroid/os/IBinder;
    //   23: iload_1
    //   24: aload 4
    //   26: aload 5
    //   28: iconst_0
    //   29: invokeinterface 47 5 0
    //   34: pop
    //   35: aload 5
    //   37: invokevirtual 51	android/os/Parcel:readInt	()I
    //   40: ifne +21 -> 61
    //   43: aload 5
    //   45: invokevirtual 61	android/os/Parcel:readLong	()J
    //   48: lstore_2
    //   49: aload 5
    //   51: invokevirtual 54	android/os/Parcel:recycle	()V
    //   54: aload 4
    //   56: invokevirtual 54	android/os/Parcel:recycle	()V
    //   59: lload_2
    //   60: lreturn
    //   61: goto -12 -> 49
    //   64: astore 6
    //   66: aload 5
    //   68: invokevirtual 54	android/os/Parcel:recycle	()V
    //   71: aload 4
    //   73: invokevirtual 54	android/os/Parcel:recycle	()V
    //   76: aload 6
    //   78: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	79	0	this	CommonTimeUtils
    //   0	79	1	paramInt	int
    //   0	79	2	paramLong	long
    //   3	69	4	localParcel1	Parcel
    //   8	59	5	localParcel2	Parcel
    //   64	13	6	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	49	64	finally
  }
  
  /* Error */
  public InetSocketAddress transactGetSockaddr(int paramInt)
    throws RemoteException
  {
    // Byte code:
    //   0: invokestatic 37	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   3: astore 9
    //   5: invokestatic 37	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   8: astore 10
    //   10: aconst_null
    //   11: astore 8
    //   13: aload 9
    //   15: aload_0
    //   16: getfield 26	android/os/CommonTimeUtils:mInterfaceDesc	Ljava/lang/String;
    //   19: invokevirtual 41	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   22: aload_0
    //   23: getfield 24	android/os/CommonTimeUtils:mRemote	Landroid/os/IBinder;
    //   26: iload_1
    //   27: aload 9
    //   29: aload 10
    //   31: iconst_0
    //   32: invokeinterface 47 5 0
    //   37: pop
    //   38: aload 8
    //   40: astore 7
    //   42: aload 10
    //   44: invokevirtual 51	android/os/Parcel:readInt	()I
    //   47: ifne +121 -> 168
    //   50: iconst_0
    //   51: istore_1
    //   52: aconst_null
    //   53: astore 6
    //   55: aload 10
    //   57: invokevirtual 51	android/os/Parcel:readInt	()I
    //   60: istore_2
    //   61: getstatic 68	android/system/OsConstants:AF_INET	I
    //   64: iload_2
    //   65: if_icmpne +116 -> 181
    //   68: aload 10
    //   70: invokevirtual 51	android/os/Parcel:readInt	()I
    //   73: istore_2
    //   74: aload 10
    //   76: invokevirtual 51	android/os/Parcel:readInt	()I
    //   79: istore_1
    //   80: getstatic 74	java/util/Locale:US	Ljava/util/Locale;
    //   83: ldc 76
    //   85: iconst_4
    //   86: anewarray 4	java/lang/Object
    //   89: dup
    //   90: iconst_0
    //   91: iload_2
    //   92: bipush 24
    //   94: ishr
    //   95: sipush 255
    //   98: iand
    //   99: invokestatic 82	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   102: aastore
    //   103: dup
    //   104: iconst_1
    //   105: iload_2
    //   106: bipush 16
    //   108: ishr
    //   109: sipush 255
    //   112: iand
    //   113: invokestatic 82	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   116: aastore
    //   117: dup
    //   118: iconst_2
    //   119: iload_2
    //   120: bipush 8
    //   122: ishr
    //   123: sipush 255
    //   126: iand
    //   127: invokestatic 82	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   130: aastore
    //   131: dup
    //   132: iconst_3
    //   133: iload_2
    //   134: sipush 255
    //   137: iand
    //   138: invokestatic 82	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   141: aastore
    //   142: invokestatic 88	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   145: astore 6
    //   147: aload 8
    //   149: astore 7
    //   151: aload 6
    //   153: ifnull +15 -> 168
    //   156: new 90	java/net/InetSocketAddress
    //   159: dup
    //   160: aload 6
    //   162: iload_1
    //   163: invokespecial 93	java/net/InetSocketAddress:<init>	(Ljava/lang/String;I)V
    //   166: astore 7
    //   168: aload 10
    //   170: invokevirtual 54	android/os/Parcel:recycle	()V
    //   173: aload 9
    //   175: invokevirtual 54	android/os/Parcel:recycle	()V
    //   178: aload 7
    //   180: areturn
    //   181: getstatic 96	android/system/OsConstants:AF_INET6	I
    //   184: iload_2
    //   185: if_icmpne -38 -> 147
    //   188: aload 10
    //   190: invokevirtual 51	android/os/Parcel:readInt	()I
    //   193: istore_2
    //   194: aload 10
    //   196: invokevirtual 51	android/os/Parcel:readInt	()I
    //   199: istore_3
    //   200: aload 10
    //   202: invokevirtual 51	android/os/Parcel:readInt	()I
    //   205: istore 4
    //   207: aload 10
    //   209: invokevirtual 51	android/os/Parcel:readInt	()I
    //   212: istore 5
    //   214: aload 10
    //   216: invokevirtual 51	android/os/Parcel:readInt	()I
    //   219: istore_1
    //   220: aload 10
    //   222: invokevirtual 51	android/os/Parcel:readInt	()I
    //   225: pop
    //   226: aload 10
    //   228: invokevirtual 51	android/os/Parcel:readInt	()I
    //   231: pop
    //   232: getstatic 74	java/util/Locale:US	Ljava/util/Locale;
    //   235: ldc 98
    //   237: bipush 8
    //   239: anewarray 4	java/lang/Object
    //   242: dup
    //   243: iconst_0
    //   244: iload_2
    //   245: bipush 16
    //   247: ishr
    //   248: ldc 99
    //   250: iand
    //   251: invokestatic 82	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   254: aastore
    //   255: dup
    //   256: iconst_1
    //   257: ldc 99
    //   259: iload_2
    //   260: iand
    //   261: invokestatic 82	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   264: aastore
    //   265: dup
    //   266: iconst_2
    //   267: iload_3
    //   268: bipush 16
    //   270: ishr
    //   271: ldc 99
    //   273: iand
    //   274: invokestatic 82	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   277: aastore
    //   278: dup
    //   279: iconst_3
    //   280: ldc 99
    //   282: iload_3
    //   283: iand
    //   284: invokestatic 82	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   287: aastore
    //   288: dup
    //   289: iconst_4
    //   290: iload 4
    //   292: bipush 16
    //   294: ishr
    //   295: ldc 99
    //   297: iand
    //   298: invokestatic 82	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   301: aastore
    //   302: dup
    //   303: iconst_5
    //   304: ldc 99
    //   306: iload 4
    //   308: iand
    //   309: invokestatic 82	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   312: aastore
    //   313: dup
    //   314: bipush 6
    //   316: iload 5
    //   318: bipush 16
    //   320: ishr
    //   321: ldc 99
    //   323: iand
    //   324: invokestatic 82	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   327: aastore
    //   328: dup
    //   329: bipush 7
    //   331: ldc 99
    //   333: iload 5
    //   335: iand
    //   336: invokestatic 82	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   339: aastore
    //   340: invokestatic 88	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   343: astore 6
    //   345: goto -198 -> 147
    //   348: astore 6
    //   350: aload 10
    //   352: invokevirtual 54	android/os/Parcel:recycle	()V
    //   355: aload 9
    //   357: invokevirtual 54	android/os/Parcel:recycle	()V
    //   360: aload 6
    //   362: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	363	0	this	CommonTimeUtils
    //   0	363	1	paramInt	int
    //   60	201	2	i	int
    //   199	85	3	j	int
    //   205	104	4	k	int
    //   212	124	5	m	int
    //   53	291	6	str	String
    //   348	13	6	localObject1	Object
    //   40	139	7	localObject2	Object
    //   11	137	8	localObject3	Object
    //   3	353	9	localParcel1	Parcel
    //   8	343	10	localParcel2	Parcel
    // Exception table:
    //   from	to	target	type
    //   13	38	348	finally
    //   42	50	348	finally
    //   55	147	348	finally
    //   156	168	348	finally
    //   181	345	348	finally
  }
  
  /* Error */
  public String transactGetString(int paramInt, String paramString)
    throws RemoteException
  {
    // Byte code:
    //   0: invokestatic 37	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   3: astore_3
    //   4: invokestatic 37	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   7: astore 4
    //   9: aload_3
    //   10: aload_0
    //   11: getfield 26	android/os/CommonTimeUtils:mInterfaceDesc	Ljava/lang/String;
    //   14: invokevirtual 41	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   17: aload_0
    //   18: getfield 24	android/os/CommonTimeUtils:mRemote	Landroid/os/IBinder;
    //   21: iload_1
    //   22: aload_3
    //   23: aload 4
    //   25: iconst_0
    //   26: invokeinterface 47 5 0
    //   31: pop
    //   32: aload 4
    //   34: invokevirtual 51	android/os/Parcel:readInt	()I
    //   37: ifne +20 -> 57
    //   40: aload 4
    //   42: invokevirtual 105	android/os/Parcel:readString	()Ljava/lang/String;
    //   45: astore_2
    //   46: aload 4
    //   48: invokevirtual 54	android/os/Parcel:recycle	()V
    //   51: aload_3
    //   52: invokevirtual 54	android/os/Parcel:recycle	()V
    //   55: aload_2
    //   56: areturn
    //   57: goto -11 -> 46
    //   60: astore_2
    //   61: aload 4
    //   63: invokevirtual 54	android/os/Parcel:recycle	()V
    //   66: aload_3
    //   67: invokevirtual 54	android/os/Parcel:recycle	()V
    //   70: aload_2
    //   71: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	CommonTimeUtils
    //   0	72	1	paramInt	int
    //   0	72	2	paramString	String
    //   3	64	3	localParcel1	Parcel
    //   7	55	4	localParcel2	Parcel
    // Exception table:
    //   from	to	target	type
    //   9	46	60	finally
  }
  
  public int transactSetInt(int paramInt1, int paramInt2)
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken(this.mInterfaceDesc);
      localParcel1.writeInt(paramInt2);
      this.mRemote.transact(paramInt1, localParcel1, localParcel2, 0);
      paramInt1 = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt1;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException = localRemoteException;
      localParcel2.recycle();
      localParcel1.recycle();
      return -7;
    }
    finally
    {
      localObject = finally;
      localParcel2.recycle();
      localParcel1.recycle();
      throw ((Throwable)localObject);
    }
  }
  
  public int transactSetLong(int paramInt, long paramLong)
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken(this.mInterfaceDesc);
      localParcel1.writeLong(paramLong);
      this.mRemote.transact(paramInt, localParcel1, localParcel2, 0);
      paramInt = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException = localRemoteException;
      localParcel2.recycle();
      localParcel1.recycle();
      return -7;
    }
    finally
    {
      localObject = finally;
      localParcel2.recycle();
      localParcel1.recycle();
      throw ((Throwable)localObject);
    }
  }
  
  public int transactSetSockaddr(int paramInt, InetSocketAddress paramInetSocketAddress)
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    for (;;)
    {
      try
      {
        localParcel1.writeInterfaceToken(this.mInterfaceDesc);
        if (paramInetSocketAddress != null) {
          continue;
        }
        localParcel1.writeInt(0);
      }
      catch (RemoteException paramInetSocketAddress)
      {
        InetAddress localInetAddress;
        byte[] arrayOfByte;
        int j;
        int k;
        int m;
        int n;
        return -7;
        if (!(localInetAddress instanceof Inet6Address)) {
          break label325;
        }
        localParcel1.writeInt(OsConstants.AF_INET6);
        int i = 0;
        if (i >= 4) {
          continue;
        }
        localParcel1.writeInt((arrayOfByte[(i * 4 + 0)] & 0xFF) << 24 | (arrayOfByte[(i * 4 + 1)] & 0xFF) << 16 | (arrayOfByte[(i * 4 + 2)] & 0xFF) << 8 | arrayOfByte[(i * 4 + 3)] & 0xFF);
        i += 1;
        continue;
        localParcel1.writeInt(j);
        localParcel1.writeInt(0);
        localParcel1.writeInt(localInetAddress.getScopeId());
        continue;
      }
      finally
      {
        localParcel2.recycle();
        localParcel1.recycle();
      }
      this.mRemote.transact(paramInt, localParcel1, localParcel2, 0);
      paramInt = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt;
      localParcel1.writeInt(1);
      localInetAddress = paramInetSocketAddress.getAddress();
      arrayOfByte = localInetAddress.getAddress();
      j = paramInetSocketAddress.getPort();
      if (!(localInetAddress instanceof Inet4Address)) {
        continue;
      }
      i = arrayOfByte[0];
      k = arrayOfByte[1];
      m = arrayOfByte[2];
      n = arrayOfByte[3];
      localParcel1.writeInt(OsConstants.AF_INET);
      localParcel1.writeInt((i & 0xFF) << 24 | (k & 0xFF) << 16 | (m & 0xFF) << 8 | n & 0xFF);
      localParcel1.writeInt(j);
    }
    label325:
    localParcel2.recycle();
    localParcel1.recycle();
    return -4;
  }
  
  public int transactSetString(int paramInt, String paramString)
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken(this.mInterfaceDesc);
      localParcel1.writeString(paramString);
      this.mRemote.transact(paramInt, localParcel1, localParcel2, 0);
      paramInt = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return paramInt;
    }
    catch (RemoteException paramString)
    {
      paramString = paramString;
      localParcel2.recycle();
      localParcel1.recycle();
      return -7;
    }
    finally
    {
      paramString = finally;
      localParcel2.recycle();
      localParcel1.recycle();
      throw paramString;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/CommonTimeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */