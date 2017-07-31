package android.os;

import java.io.PrintStream;

public class Broadcaster
{
  private Registration mReg;
  
  public void broadcast(Message paramMessage)
  {
    try
    {
      Object localObject1 = this.mReg;
      if (localObject1 == null) {
        return;
      }
      int i = paramMessage.what;
      Registration localRegistration = this.mReg;
      Object localObject2;
      for (localObject1 = localRegistration;; localObject1 = localObject2)
      {
        if (((Registration)localObject1).senderWhat >= i) {}
        do
        {
          if (((Registration)localObject1).senderWhat != i) {
            break;
          }
          localObject2 = ((Registration)localObject1).targets;
          localObject1 = ((Registration)localObject1).targetWhats;
          int j = localObject2.length;
          i = 0;
          while (i < j)
          {
            localRegistration = localObject2[i];
            Message localMessage = Message.obtain();
            localMessage.copyFrom(paramMessage);
            localMessage.what = localObject1[i];
            localRegistration.sendMessage(localMessage);
            i += 1;
          }
          localObject2 = ((Registration)localObject1).next;
          localObject1 = localObject2;
        } while (localObject2 == localRegistration);
      }
      return;
    }
    finally {}
  }
  
  /* Error */
  public void cancelRequest(int paramInt1, Handler paramHandler, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 18	android/os/Broadcaster:mReg	Landroid/os/Broadcaster$Registration;
    //   6: astore 7
    //   8: aload 7
    //   10: astore 5
    //   12: aload 7
    //   14: ifnonnull +28 -> 42
    //   17: aload_0
    //   18: monitorexit
    //   19: return
    //   20: aload 5
    //   22: getfield 51	android/os/Broadcaster$Registration:next	Landroid/os/Broadcaster$Registration;
    //   25: astore 6
    //   27: aload 6
    //   29: astore 5
    //   31: aload 6
    //   33: aload 7
    //   35: if_acmpeq +16 -> 51
    //   38: aload 6
    //   40: astore 5
    //   42: aload 5
    //   44: getfield 27	android/os/Broadcaster$Registration:senderWhat	I
    //   47: iload_1
    //   48: if_icmplt -28 -> 20
    //   51: aload 5
    //   53: getfield 27	android/os/Broadcaster$Registration:senderWhat	I
    //   56: iload_1
    //   57: if_icmpne +140 -> 197
    //   60: aload 5
    //   62: getfield 31	android/os/Broadcaster$Registration:targets	[Landroid/os/Handler;
    //   65: astore 6
    //   67: aload 5
    //   69: getfield 35	android/os/Broadcaster$Registration:targetWhats	[I
    //   72: astore 7
    //   74: aload 6
    //   76: arraylength
    //   77: istore 4
    //   79: iconst_0
    //   80: istore_1
    //   81: iload_1
    //   82: iload 4
    //   84: if_icmpge +113 -> 197
    //   87: aload 6
    //   89: iload_1
    //   90: aaload
    //   91: aload_2
    //   92: if_acmpne +108 -> 200
    //   95: aload 7
    //   97: iload_1
    //   98: iaload
    //   99: iload_3
    //   100: if_icmpne +100 -> 200
    //   103: aload 5
    //   105: iload 4
    //   107: iconst_1
    //   108: isub
    //   109: anewarray 44	android/os/Handler
    //   112: putfield 31	android/os/Broadcaster$Registration:targets	[Landroid/os/Handler;
    //   115: aload 5
    //   117: iload 4
    //   119: iconst_1
    //   120: isub
    //   121: newarray <illegal type>
    //   123: putfield 35	android/os/Broadcaster$Registration:targetWhats	[I
    //   126: iload_1
    //   127: ifle +29 -> 156
    //   130: aload 6
    //   132: iconst_0
    //   133: aload 5
    //   135: getfield 31	android/os/Broadcaster$Registration:targets	[Landroid/os/Handler;
    //   138: iconst_0
    //   139: iload_1
    //   140: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   143: aload 7
    //   145: iconst_0
    //   146: aload 5
    //   148: getfield 35	android/os/Broadcaster$Registration:targetWhats	[I
    //   151: iconst_0
    //   152: iload_1
    //   153: invokestatic 62	java/lang/System:arraycopy	([II[III)V
    //   156: iload 4
    //   158: iload_1
    //   159: isub
    //   160: iconst_1
    //   161: isub
    //   162: istore_3
    //   163: iload_3
    //   164: ifeq +33 -> 197
    //   167: aload 6
    //   169: iload_1
    //   170: iconst_1
    //   171: iadd
    //   172: aload 5
    //   174: getfield 31	android/os/Broadcaster$Registration:targets	[Landroid/os/Handler;
    //   177: iload_1
    //   178: iload_3
    //   179: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   182: aload 7
    //   184: iload_1
    //   185: iconst_1
    //   186: iadd
    //   187: aload 5
    //   189: getfield 35	android/os/Broadcaster$Registration:targetWhats	[I
    //   192: iload_1
    //   193: iload_3
    //   194: invokestatic 62	java/lang/System:arraycopy	([II[III)V
    //   197: aload_0
    //   198: monitorexit
    //   199: return
    //   200: iload_1
    //   201: iconst_1
    //   202: iadd
    //   203: istore_1
    //   204: goto -123 -> 81
    //   207: astore_2
    //   208: aload_0
    //   209: monitorexit
    //   210: aload_2
    //   211: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	212	0	this	Broadcaster
    //   0	212	1	paramInt1	int
    //   0	212	2	paramHandler	Handler
    //   0	212	3	paramInt2	int
    //   77	83	4	i	int
    //   10	178	5	localObject1	Object
    //   25	143	6	localObject2	Object
    //   6	177	7	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   2	8	207	finally
    //   20	27	207	finally
    //   42	51	207	finally
    //   51	79	207	finally
    //   103	126	207	finally
    //   130	156	207	finally
    //   167	197	207	finally
  }
  
  public void dumpRegistrations()
  {
    try
    {
      Registration localRegistration1 = this.mReg;
      System.out.println("Broadcaster " + this + " {");
      if (localRegistration1 != null)
      {
        Object localObject1 = localRegistration1;
        Registration localRegistration2;
        do
        {
          System.out.println("    senderWhat=" + ((Registration)localObject1).senderWhat);
          int j = ((Registration)localObject1).targets.length;
          int i = 0;
          while (i < j)
          {
            System.out.println("        [" + localObject1.targetWhats[i] + "] " + localObject1.targets[i]);
            i += 1;
          }
          localRegistration2 = ((Registration)localObject1).next;
          localObject1 = localRegistration2;
        } while (localRegistration2 != localRegistration1);
      }
      System.out.println("}");
      return;
    }
    finally {}
  }
  
  /* Error */
  public void request(int paramInt1, Handler paramHandler, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 18	android/os/Broadcaster:mReg	Landroid/os/Broadcaster$Registration;
    //   6: ifnonnull +76 -> 82
    //   9: new 6	android/os/Broadcaster$Registration
    //   12: dup
    //   13: aload_0
    //   14: aconst_null
    //   15: invokespecial 106	android/os/Broadcaster$Registration:<init>	(Landroid/os/Broadcaster;Landroid/os/Broadcaster$Registration;)V
    //   18: astore 6
    //   20: aload 6
    //   22: iload_1
    //   23: putfield 27	android/os/Broadcaster$Registration:senderWhat	I
    //   26: aload 6
    //   28: iconst_1
    //   29: anewarray 44	android/os/Handler
    //   32: putfield 31	android/os/Broadcaster$Registration:targets	[Landroid/os/Handler;
    //   35: aload 6
    //   37: iconst_1
    //   38: newarray <illegal type>
    //   40: putfield 35	android/os/Broadcaster$Registration:targetWhats	[I
    //   43: aload 6
    //   45: getfield 31	android/os/Broadcaster$Registration:targets	[Landroid/os/Handler;
    //   48: iconst_0
    //   49: aload_2
    //   50: aastore
    //   51: aload 6
    //   53: getfield 35	android/os/Broadcaster$Registration:targetWhats	[I
    //   56: iconst_0
    //   57: iload_3
    //   58: iastore
    //   59: aload_0
    //   60: aload 6
    //   62: putfield 18	android/os/Broadcaster:mReg	Landroid/os/Broadcaster$Registration;
    //   65: aload 6
    //   67: aload 6
    //   69: putfield 51	android/os/Broadcaster$Registration:next	Landroid/os/Broadcaster$Registration;
    //   72: aload 6
    //   74: aload 6
    //   76: putfield 109	android/os/Broadcaster$Registration:prev	Landroid/os/Broadcaster$Registration;
    //   79: aload_0
    //   80: monitorexit
    //   81: return
    //   82: aload_0
    //   83: getfield 18	android/os/Broadcaster:mReg	Landroid/os/Broadcaster$Registration;
    //   86: astore 8
    //   88: aload 8
    //   90: astore 6
    //   92: aload 6
    //   94: getfield 27	android/os/Broadcaster$Registration:senderWhat	I
    //   97: iload_1
    //   98: if_icmplt +135 -> 233
    //   101: aload 6
    //   103: getfield 27	android/os/Broadcaster$Registration:senderWhat	I
    //   106: iload_1
    //   107: if_icmpeq +151 -> 258
    //   110: new 6	android/os/Broadcaster$Registration
    //   113: dup
    //   114: aload_0
    //   115: aconst_null
    //   116: invokespecial 106	android/os/Broadcaster$Registration:<init>	(Landroid/os/Broadcaster;Landroid/os/Broadcaster$Registration;)V
    //   119: astore 7
    //   121: aload 7
    //   123: iload_1
    //   124: putfield 27	android/os/Broadcaster$Registration:senderWhat	I
    //   127: aload 7
    //   129: iconst_1
    //   130: anewarray 44	android/os/Handler
    //   133: putfield 31	android/os/Broadcaster$Registration:targets	[Landroid/os/Handler;
    //   136: aload 7
    //   138: iconst_1
    //   139: newarray <illegal type>
    //   141: putfield 35	android/os/Broadcaster$Registration:targetWhats	[I
    //   144: aload 7
    //   146: aload 6
    //   148: putfield 51	android/os/Broadcaster$Registration:next	Landroid/os/Broadcaster$Registration;
    //   151: aload 7
    //   153: aload 6
    //   155: getfield 109	android/os/Broadcaster$Registration:prev	Landroid/os/Broadcaster$Registration;
    //   158: putfield 109	android/os/Broadcaster$Registration:prev	Landroid/os/Broadcaster$Registration;
    //   161: aload 6
    //   163: getfield 109	android/os/Broadcaster$Registration:prev	Landroid/os/Broadcaster$Registration;
    //   166: aload 7
    //   168: putfield 51	android/os/Broadcaster$Registration:next	Landroid/os/Broadcaster$Registration;
    //   171: aload 6
    //   173: aload 7
    //   175: putfield 109	android/os/Broadcaster$Registration:prev	Landroid/os/Broadcaster$Registration;
    //   178: aload 6
    //   180: aload_0
    //   181: getfield 18	android/os/Broadcaster:mReg	Landroid/os/Broadcaster$Registration;
    //   184: if_acmpne +195 -> 379
    //   187: aload 6
    //   189: getfield 27	android/os/Broadcaster$Registration:senderWhat	I
    //   192: aload 7
    //   194: getfield 27	android/os/Broadcaster$Registration:senderWhat	I
    //   197: if_icmple +182 -> 379
    //   200: aload_0
    //   201: aload 7
    //   203: putfield 18	android/os/Broadcaster:mReg	Landroid/os/Broadcaster$Registration;
    //   206: goto +173 -> 379
    //   209: aload 6
    //   211: getfield 31	android/os/Broadcaster$Registration:targets	[Landroid/os/Handler;
    //   214: iload_1
    //   215: aload_2
    //   216: aastore
    //   217: aload 6
    //   219: getfield 35	android/os/Broadcaster$Registration:targetWhats	[I
    //   222: iload_1
    //   223: iload_3
    //   224: iastore
    //   225: goto -146 -> 79
    //   228: astore_2
    //   229: aload_0
    //   230: monitorexit
    //   231: aload_2
    //   232: athrow
    //   233: aload 6
    //   235: getfield 51	android/os/Broadcaster$Registration:next	Landroid/os/Broadcaster$Registration;
    //   238: astore 7
    //   240: aload 7
    //   242: astore 6
    //   244: aload 7
    //   246: aload 8
    //   248: if_acmpeq -147 -> 101
    //   251: aload 7
    //   253: astore 6
    //   255: goto -163 -> 92
    //   258: aload 6
    //   260: getfield 31	android/os/Broadcaster$Registration:targets	[Landroid/os/Handler;
    //   263: arraylength
    //   264: istore 4
    //   266: aload 6
    //   268: getfield 31	android/os/Broadcaster$Registration:targets	[Landroid/os/Handler;
    //   271: astore 7
    //   273: aload 6
    //   275: getfield 35	android/os/Broadcaster$Registration:targetWhats	[I
    //   278: astore 8
    //   280: iconst_0
    //   281: istore_1
    //   282: iload_1
    //   283: iload 4
    //   285: if_icmpge +33 -> 318
    //   288: aload 7
    //   290: iload_1
    //   291: aaload
    //   292: aload_2
    //   293: if_acmpne +18 -> 311
    //   296: aload 8
    //   298: iload_1
    //   299: iaload
    //   300: istore 5
    //   302: iload 5
    //   304: iload_3
    //   305: if_icmpne +6 -> 311
    //   308: aload_0
    //   309: monitorexit
    //   310: return
    //   311: iload_1
    //   312: iconst_1
    //   313: iadd
    //   314: istore_1
    //   315: goto -33 -> 282
    //   318: aload 6
    //   320: iload 4
    //   322: iconst_1
    //   323: iadd
    //   324: anewarray 44	android/os/Handler
    //   327: putfield 31	android/os/Broadcaster$Registration:targets	[Landroid/os/Handler;
    //   330: aload 7
    //   332: iconst_0
    //   333: aload 6
    //   335: getfield 31	android/os/Broadcaster$Registration:targets	[Landroid/os/Handler;
    //   338: iconst_0
    //   339: iload 4
    //   341: invokestatic 59	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   344: aload 6
    //   346: iload 4
    //   348: iconst_1
    //   349: iadd
    //   350: newarray <illegal type>
    //   352: putfield 35	android/os/Broadcaster$Registration:targetWhats	[I
    //   355: aload 8
    //   357: iconst_0
    //   358: aload 6
    //   360: getfield 35	android/os/Broadcaster$Registration:targetWhats	[I
    //   363: iconst_0
    //   364: iload 4
    //   366: invokestatic 62	java/lang/System:arraycopy	([II[III)V
    //   369: iload 4
    //   371: istore_1
    //   372: goto -163 -> 209
    //   375: astore_2
    //   376: goto -147 -> 229
    //   379: aload 7
    //   381: astore 6
    //   383: iconst_0
    //   384: istore_1
    //   385: goto -176 -> 209
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	388	0	this	Broadcaster
    //   0	388	1	paramInt1	int
    //   0	388	2	paramHandler	Handler
    //   0	388	3	paramInt2	int
    //   264	106	4	i	int
    //   300	6	5	j	int
    //   18	364	6	localObject1	Object
    //   119	261	7	localObject2	Object
    //   86	270	8	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   2	20	228	finally
    //   82	88	228	finally
    //   92	101	228	finally
    //   101	206	228	finally
    //   209	225	228	finally
    //   233	240	228	finally
    //   258	280	228	finally
    //   318	369	228	finally
    //   20	79	375	finally
  }
  
  private class Registration
  {
    Registration next;
    Registration prev;
    int senderWhat;
    int[] targetWhats;
    Handler[] targets;
    
    private Registration() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Broadcaster.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */