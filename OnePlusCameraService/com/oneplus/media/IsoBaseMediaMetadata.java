package com.oneplus.media;

import com.oneplus.base.BasicBaseObject;

final class IsoBaseMediaMetadata
  extends BasicBaseObject
  implements VideoMetadata
{
  private boolean m_ContinueReading;
  private IsoBaseMediaMovieHeader m_MovieHeader;
  private IsoBaseMediaTrackHeader m_VideoTrackHeader;
  
  /* Error */
  public IsoBaseMediaMetadata(java.io.InputStream paramInputStream)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 9
    //   6: aload_0
    //   7: invokespecial 19	com/oneplus/base/BasicBaseObject:<init>	()V
    //   10: aload_0
    //   11: iconst_1
    //   12: putfield 21	com/oneplus/media/IsoBaseMediaMetadata:m_ContinueReading	Z
    //   15: aconst_null
    //   16: astore 4
    //   18: aconst_null
    //   19: astore_3
    //   20: new 23	com/oneplus/media/IsoBaseMediaReader
    //   23: dup
    //   24: aload_1
    //   25: invokespecial 25	com/oneplus/media/IsoBaseMediaReader:<init>	(Ljava/io/InputStream;)V
    //   28: astore 5
    //   30: aload 5
    //   32: invokevirtual 29	com/oneplus/media/IsoBaseMediaReader:read	()Z
    //   35: ifeq +363 -> 398
    //   38: aload 5
    //   40: invokevirtual 33	com/oneplus/media/IsoBaseMediaReader:currentBoxType	()I
    //   43: istore_2
    //   44: iload_2
    //   45: ldc 34
    //   47: if_icmpne -17 -> 30
    //   50: aconst_null
    //   51: astore_3
    //   52: aconst_null
    //   53: astore_1
    //   54: aload 5
    //   56: invokevirtual 38	com/oneplus/media/IsoBaseMediaReader:getBoxDataReader	()Lcom/oneplus/media/IsoBaseMediaReader;
    //   59: astore 6
    //   61: aload 6
    //   63: astore_1
    //   64: aload 6
    //   66: astore_3
    //   67: aload_0
    //   68: getfield 21	com/oneplus/media/IsoBaseMediaMetadata:m_ContinueReading	Z
    //   71: ifeq +287 -> 358
    //   74: aload 6
    //   76: astore_1
    //   77: aload 6
    //   79: astore_3
    //   80: aload 6
    //   82: invokevirtual 29	com/oneplus/media/IsoBaseMediaReader:read	()Z
    //   85: ifeq +273 -> 358
    //   88: aload 6
    //   90: astore_1
    //   91: aload 6
    //   93: astore_3
    //   94: aload 6
    //   96: invokevirtual 33	com/oneplus/media/IsoBaseMediaReader:currentBoxType	()I
    //   99: lookupswitch	default:+394->493, 1836476516:+25->124, 1953653099:+108->207
    //   124: aload 6
    //   126: astore_1
    //   127: aload 6
    //   129: astore_3
    //   130: aload_0
    //   131: aload 6
    //   133: invokevirtual 42	com/oneplus/media/IsoBaseMediaReader:getBoxData	()[B
    //   136: invokespecial 46	com/oneplus/media/IsoBaseMediaMetadata:readMvhdBox	([B)V
    //   139: goto -78 -> 61
    //   142: astore_3
    //   143: aload_3
    //   144: athrow
    //   145: astore 4
    //   147: aload_3
    //   148: astore 6
    //   150: aload_1
    //   151: ifnull +10 -> 161
    //   154: aload_1
    //   155: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   158: aload_3
    //   159: astore 6
    //   161: aload 6
    //   163: ifnull +232 -> 395
    //   166: aload 6
    //   168: athrow
    //   169: astore_3
    //   170: aload 5
    //   172: astore_1
    //   173: aload_3
    //   174: athrow
    //   175: astore 5
    //   177: aload_3
    //   178: astore 4
    //   180: aload 5
    //   182: astore_3
    //   183: aload 4
    //   185: astore 5
    //   187: aload_1
    //   188: ifnull +11 -> 199
    //   191: aload_1
    //   192: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   195: aload 4
    //   197: astore 5
    //   199: aload 5
    //   201: ifnull +258 -> 459
    //   204: aload 5
    //   206: athrow
    //   207: aconst_null
    //   208: astore_3
    //   209: aconst_null
    //   210: astore_1
    //   211: aload 6
    //   213: invokevirtual 38	com/oneplus/media/IsoBaseMediaReader:getBoxDataReader	()Lcom/oneplus/media/IsoBaseMediaReader;
    //   216: astore 4
    //   218: aload 4
    //   220: astore_1
    //   221: aload 4
    //   223: astore_3
    //   224: aload_0
    //   225: aload 4
    //   227: invokespecial 53	com/oneplus/media/IsoBaseMediaMetadata:readTrakBox	(Lcom/oneplus/media/IsoBaseMediaReader;)V
    //   230: aload 4
    //   232: ifnull +11 -> 243
    //   235: aload 6
    //   237: astore_3
    //   238: aload 4
    //   240: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   243: aconst_null
    //   244: astore 4
    //   246: aload 4
    //   248: ifnull -187 -> 61
    //   251: aload 6
    //   253: astore_1
    //   254: aload 6
    //   256: astore_3
    //   257: aload 4
    //   259: athrow
    //   260: astore 4
    //   262: aconst_null
    //   263: astore 6
    //   265: aload_3
    //   266: astore_1
    //   267: aload 6
    //   269: astore_3
    //   270: goto -123 -> 147
    //   273: astore 4
    //   275: goto -29 -> 246
    //   278: astore 4
    //   280: aload 4
    //   282: athrow
    //   283: astore 7
    //   285: aload 4
    //   287: astore 8
    //   289: aload_1
    //   290: ifnull +14 -> 304
    //   293: aload 6
    //   295: astore_3
    //   296: aload_1
    //   297: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   300: aload 4
    //   302: astore 8
    //   304: aload 8
    //   306: ifnull +43 -> 349
    //   309: aload 6
    //   311: astore_1
    //   312: aload 6
    //   314: astore_3
    //   315: aload 8
    //   317: athrow
    //   318: aload 4
    //   320: astore 8
    //   322: aload 4
    //   324: aload 10
    //   326: if_acmpeq -22 -> 304
    //   329: aload 6
    //   331: astore_1
    //   332: aload 6
    //   334: astore_3
    //   335: aload 4
    //   337: aload 10
    //   339: invokevirtual 57	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   342: aload 4
    //   344: astore 8
    //   346: goto -42 -> 304
    //   349: aload 6
    //   351: astore_1
    //   352: aload 6
    //   354: astore_3
    //   355: aload 7
    //   357: athrow
    //   358: aload 6
    //   360: ifnull +8 -> 368
    //   363: aload 6
    //   365: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   368: aconst_null
    //   369: astore_1
    //   370: aload_1
    //   371: ifnull +27 -> 398
    //   374: aload_1
    //   375: athrow
    //   376: aload_3
    //   377: astore 6
    //   379: aload_3
    //   380: aload_1
    //   381: if_acmpeq -220 -> 161
    //   384: aload_3
    //   385: aload_1
    //   386: invokevirtual 57	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   389: aload_3
    //   390: astore 6
    //   392: goto -231 -> 161
    //   395: aload 4
    //   397: athrow
    //   398: aload 7
    //   400: astore_1
    //   401: aload 5
    //   403: ifnull +11 -> 414
    //   406: aload 5
    //   408: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   411: aload 7
    //   413: astore_1
    //   414: aload_1
    //   415: ifnull +46 -> 461
    //   418: aload_1
    //   419: athrow
    //   420: astore_1
    //   421: goto -7 -> 414
    //   424: astore_1
    //   425: aload 4
    //   427: ifnonnull +9 -> 436
    //   430: aload_1
    //   431: astore 5
    //   433: goto -234 -> 199
    //   436: aload 4
    //   438: astore 5
    //   440: aload 4
    //   442: aload_1
    //   443: if_acmpeq -244 -> 199
    //   446: aload 4
    //   448: aload_1
    //   449: invokevirtual 57	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   452: aload 4
    //   454: astore 5
    //   456: goto -257 -> 199
    //   459: aload_3
    //   460: athrow
    //   461: return
    //   462: astore_3
    //   463: aload 4
    //   465: astore_1
    //   466: aload 9
    //   468: astore 4
    //   470: goto -287 -> 183
    //   473: astore 4
    //   475: aload_3
    //   476: astore_1
    //   477: aload 4
    //   479: astore_3
    //   480: goto -307 -> 173
    //   483: astore 7
    //   485: aconst_null
    //   486: astore 4
    //   488: aload_3
    //   489: astore_1
    //   490: goto -205 -> 285
    //   493: goto -432 -> 61
    //   496: astore 10
    //   498: aload 4
    //   500: ifnonnull -182 -> 318
    //   503: aload 10
    //   505: astore 8
    //   507: goto -203 -> 304
    //   510: astore_3
    //   511: aload 5
    //   513: astore_1
    //   514: aload 9
    //   516: astore 4
    //   518: goto -335 -> 183
    //   521: astore_1
    //   522: goto -152 -> 370
    //   525: astore_1
    //   526: aload_3
    //   527: ifnonnull -151 -> 376
    //   530: aload_1
    //   531: astore 6
    //   533: goto -372 -> 161
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	536	0	this	IsoBaseMediaMetadata
    //   0	536	1	paramInputStream	java.io.InputStream
    //   43	5	2	i	int
    //   19	111	3	localObject1	Object
    //   142	17	3	localThrowable1	Throwable
    //   169	9	3	localThrowable2	Throwable
    //   182	278	3	localObject2	Object
    //   462	14	3	localObject3	Object
    //   479	10	3	localThrowable3	Throwable
    //   510	17	3	localObject4	Object
    //   16	1	4	localObject5	Object
    //   145	1	4	localObject6	Object
    //   178	80	4	localObject7	Object
    //   260	1	4	localObject8	Object
    //   273	1	4	localThrowable4	Throwable
    //   278	186	4	localThrowable5	Throwable
    //   468	1	4	localObject9	Object
    //   473	5	4	localThrowable6	Throwable
    //   486	31	4	localObject10	Object
    //   28	143	5	localIsoBaseMediaReader	IsoBaseMediaReader
    //   175	6	5	localObject11	Object
    //   185	327	5	localObject12	Object
    //   59	473	6	localObject13	Object
    //   1	1	7	localObject14	Object
    //   283	129	7	localObject15	Object
    //   483	1	7	localObject16	Object
    //   287	219	8	localObject17	Object
    //   4	511	9	localObject18	Object
    //   324	14	10	localThrowable7	Throwable
    //   496	8	10	localThrowable8	Throwable
    // Exception table:
    //   from	to	target	type
    //   54	61	142	java/lang/Throwable
    //   67	74	142	java/lang/Throwable
    //   80	88	142	java/lang/Throwable
    //   94	124	142	java/lang/Throwable
    //   130	139	142	java/lang/Throwable
    //   257	260	142	java/lang/Throwable
    //   315	318	142	java/lang/Throwable
    //   335	342	142	java/lang/Throwable
    //   355	358	142	java/lang/Throwable
    //   143	145	145	finally
    //   30	44	169	java/lang/Throwable
    //   166	169	169	java/lang/Throwable
    //   374	376	169	java/lang/Throwable
    //   384	389	169	java/lang/Throwable
    //   395	398	169	java/lang/Throwable
    //   173	175	175	finally
    //   54	61	260	finally
    //   67	74	260	finally
    //   80	88	260	finally
    //   94	124	260	finally
    //   130	139	260	finally
    //   238	243	260	finally
    //   257	260	260	finally
    //   296	300	260	finally
    //   315	318	260	finally
    //   335	342	260	finally
    //   355	358	260	finally
    //   238	243	273	java/lang/Throwable
    //   211	218	278	java/lang/Throwable
    //   224	230	278	java/lang/Throwable
    //   280	283	283	finally
    //   406	411	420	java/lang/Throwable
    //   191	195	424	java/lang/Throwable
    //   20	30	462	finally
    //   20	30	473	java/lang/Throwable
    //   211	218	483	finally
    //   224	230	483	finally
    //   296	300	496	java/lang/Throwable
    //   30	44	510	finally
    //   154	158	510	finally
    //   166	169	510	finally
    //   363	368	510	finally
    //   374	376	510	finally
    //   384	389	510	finally
    //   395	398	510	finally
    //   363	368	521	java/lang/Throwable
    //   154	158	525	java/lang/Throwable
  }
  
  /* Error */
  private void readMdiaBox(IsoBaseMediaReader paramIsoBaseMediaReader)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 5
    //   6: aload_1
    //   7: invokevirtual 29	com/oneplus/media/IsoBaseMediaReader:read	()Z
    //   10: ifeq +112 -> 122
    //   13: aload_1
    //   14: invokevirtual 33	com/oneplus/media/IsoBaseMediaReader:currentBoxType	()I
    //   17: ldc 60
    //   19: if_icmpne -13 -> 6
    //   22: aconst_null
    //   23: astore_3
    //   24: aconst_null
    //   25: astore_2
    //   26: aload_1
    //   27: invokevirtual 38	com/oneplus/media/IsoBaseMediaReader:getBoxDataReader	()Lcom/oneplus/media/IsoBaseMediaReader;
    //   30: astore_1
    //   31: aload_1
    //   32: astore_2
    //   33: aload_1
    //   34: astore_3
    //   35: aload_0
    //   36: aload_1
    //   37: invokespecial 63	com/oneplus/media/IsoBaseMediaMetadata:readMinfBox	(Lcom/oneplus/media/IsoBaseMediaReader;)V
    //   40: aload 5
    //   42: astore_2
    //   43: aload_1
    //   44: ifnull +10 -> 54
    //   47: aload_1
    //   48: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   51: aload 5
    //   53: astore_2
    //   54: aload_2
    //   55: ifnull +67 -> 122
    //   58: aload_2
    //   59: athrow
    //   60: astore_2
    //   61: goto -7 -> 54
    //   64: astore_1
    //   65: aload_1
    //   66: athrow
    //   67: astore_3
    //   68: aload_1
    //   69: astore 4
    //   71: aload_2
    //   72: ifnull +10 -> 82
    //   75: aload_2
    //   76: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   79: aload_1
    //   80: astore 4
    //   82: aload 4
    //   84: ifnull +36 -> 120
    //   87: aload 4
    //   89: athrow
    //   90: astore_2
    //   91: aload_1
    //   92: ifnonnull +9 -> 101
    //   95: aload_2
    //   96: astore 4
    //   98: goto -16 -> 82
    //   101: aload_1
    //   102: astore 4
    //   104: aload_1
    //   105: aload_2
    //   106: if_acmpeq -24 -> 82
    //   109: aload_1
    //   110: aload_2
    //   111: invokevirtual 57	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   114: aload_1
    //   115: astore 4
    //   117: goto -35 -> 82
    //   120: aload_3
    //   121: athrow
    //   122: return
    //   123: astore_1
    //   124: aload_3
    //   125: astore_2
    //   126: aload_1
    //   127: astore_3
    //   128: aload 4
    //   130: astore_1
    //   131: goto -63 -> 68
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	134	0	this	IsoBaseMediaMetadata
    //   0	134	1	paramIsoBaseMediaReader	IsoBaseMediaReader
    //   25	34	2	localObject1	Object
    //   60	16	2	localThrowable1	Throwable
    //   90	21	2	localThrowable2	Throwable
    //   125	1	2	localObject2	Object
    //   23	12	3	localIsoBaseMediaReader1	IsoBaseMediaReader
    //   67	58	3	localObject3	Object
    //   127	1	3	localIsoBaseMediaReader2	IsoBaseMediaReader
    //   1	128	4	localObject4	Object
    //   4	48	5	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   47	51	60	java/lang/Throwable
    //   26	31	64	java/lang/Throwable
    //   35	40	64	java/lang/Throwable
    //   65	67	67	finally
    //   75	79	90	java/lang/Throwable
    //   26	31	123	finally
    //   35	40	123	finally
  }
  
  /* Error */
  private void readMinfBox(IsoBaseMediaReader paramIsoBaseMediaReader)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: iconst_0
    //   4: istore_2
    //   5: aload_1
    //   6: invokevirtual 29	com/oneplus/media/IsoBaseMediaReader:read	()Z
    //   9: ifeq +153 -> 162
    //   12: aload_1
    //   13: invokevirtual 33	com/oneplus/media/IsoBaseMediaReader:currentBoxType	()I
    //   16: lookupswitch	default:+28->44, 1937007212:+31->47, 1986881636:+77->93
    //   44: goto -39 -> 5
    //   47: iload_2
    //   48: ifeq -43 -> 5
    //   51: aconst_null
    //   52: astore 4
    //   54: aconst_null
    //   55: astore_3
    //   56: aload_1
    //   57: invokevirtual 38	com/oneplus/media/IsoBaseMediaReader:getBoxDataReader	()Lcom/oneplus/media/IsoBaseMediaReader;
    //   60: astore 5
    //   62: aload 5
    //   64: astore_3
    //   65: aload 5
    //   67: astore 4
    //   69: aload_0
    //   70: aload 5
    //   72: invokespecial 66	com/oneplus/media/IsoBaseMediaMetadata:readStblBox	(Lcom/oneplus/media/IsoBaseMediaReader;)V
    //   75: aload 5
    //   77: ifnull +8 -> 85
    //   80: aload 5
    //   82: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   85: aconst_null
    //   86: astore_3
    //   87: aload_3
    //   88: ifnull -83 -> 5
    //   91: aload_3
    //   92: athrow
    //   93: iconst_1
    //   94: istore_2
    //   95: goto -90 -> 5
    //   98: astore_3
    //   99: goto -12 -> 87
    //   102: astore_1
    //   103: aload_1
    //   104: athrow
    //   105: astore 4
    //   107: aload_1
    //   108: astore 5
    //   110: aload_3
    //   111: ifnull +10 -> 121
    //   114: aload_3
    //   115: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   118: aload_1
    //   119: astore 5
    //   121: aload 5
    //   123: ifnull +36 -> 159
    //   126: aload 5
    //   128: athrow
    //   129: astore_3
    //   130: aload_1
    //   131: ifnonnull +9 -> 140
    //   134: aload_3
    //   135: astore 5
    //   137: goto -16 -> 121
    //   140: aload_1
    //   141: astore 5
    //   143: aload_1
    //   144: aload_3
    //   145: if_acmpeq -24 -> 121
    //   148: aload_1
    //   149: aload_3
    //   150: invokevirtual 57	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   153: aload_1
    //   154: astore 5
    //   156: goto -35 -> 121
    //   159: aload 4
    //   161: athrow
    //   162: iload_2
    //   163: ifeq +9 -> 172
    //   166: aload_0
    //   167: iconst_0
    //   168: putfield 21	com/oneplus/media/IsoBaseMediaMetadata:m_ContinueReading	Z
    //   171: return
    //   172: aload_0
    //   173: aconst_null
    //   174: putfield 68	com/oneplus/media/IsoBaseMediaMetadata:m_VideoTrackHeader	Lcom/oneplus/media/IsoBaseMediaTrackHeader;
    //   177: return
    //   178: astore_1
    //   179: aload 4
    //   181: astore_3
    //   182: aload_1
    //   183: astore 4
    //   185: aload 6
    //   187: astore_1
    //   188: goto -81 -> 107
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	191	0	this	IsoBaseMediaMetadata
    //   0	191	1	paramIsoBaseMediaReader	IsoBaseMediaReader
    //   4	159	2	i	int
    //   55	37	3	localObject1	Object
    //   98	17	3	localThrowable1	Throwable
    //   129	21	3	localThrowable2	Throwable
    //   181	1	3	localObject2	Object
    //   52	16	4	localObject3	Object
    //   105	75	4	localObject4	Object
    //   183	1	4	localIsoBaseMediaReader	IsoBaseMediaReader
    //   60	95	5	localObject5	Object
    //   1	185	6	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   80	85	98	java/lang/Throwable
    //   56	62	102	java/lang/Throwable
    //   69	75	102	java/lang/Throwable
    //   103	105	105	finally
    //   114	118	129	java/lang/Throwable
    //   56	62	178	finally
    //   69	75	178	finally
  }
  
  private void readMvhdBox(byte[] paramArrayOfByte)
  {
    this.m_MovieHeader = new IsoBaseMediaMovieHeader(paramArrayOfByte);
    setReadOnly(PROP_DATE_TIME_ORIGINAL, Long.valueOf(this.m_MovieHeader.getCreationTime()));
    setReadOnly(PROP_DURATION, Long.valueOf(this.m_MovieHeader.getDuration()));
  }
  
  private void readStblBox(IsoBaseMediaReader paramIsoBaseMediaReader)
  {
    while (paramIsoBaseMediaReader.read()) {
      if (paramIsoBaseMediaReader.currentBoxType() == 1937011556) {
        readStsdBox(paramIsoBaseMediaReader.getBoxData());
      }
    }
  }
  
  /* Error */
  private void readStsdBox(byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 8
    //   6: aload_1
    //   7: arraylength
    //   8: bipush 8
    //   10: if_icmpge +4 -> 14
    //   13: return
    //   14: aload_1
    //   15: iconst_4
    //   16: invokestatic 108	com/oneplus/media/IsoBaseMediaBox:getUInteger	([BI)J
    //   19: lconst_0
    //   20: lcmp
    //   21: ifgt +4 -> 25
    //   24: return
    //   25: aconst_null
    //   26: astore 6
    //   28: aconst_null
    //   29: astore 9
    //   31: new 23	com/oneplus/media/IsoBaseMediaReader
    //   34: dup
    //   35: new 110	java/io/ByteArrayInputStream
    //   38: dup
    //   39: aload_1
    //   40: bipush 8
    //   42: aload_1
    //   43: arraylength
    //   44: bipush 8
    //   46: isub
    //   47: invokespecial 113	java/io/ByteArrayInputStream:<init>	([BII)V
    //   50: invokespecial 25	com/oneplus/media/IsoBaseMediaReader:<init>	(Ljava/io/InputStream;)V
    //   53: astore_1
    //   54: aload_1
    //   55: invokevirtual 29	com/oneplus/media/IsoBaseMediaReader:read	()Z
    //   58: ifeq +93 -> 151
    //   61: aload_1
    //   62: invokevirtual 42	com/oneplus/media/IsoBaseMediaReader:getBoxData	()[B
    //   65: astore 6
    //   67: aload 6
    //   69: arraylength
    //   70: bipush 32
    //   72: if_icmplt +79 -> 151
    //   75: aload 6
    //   77: bipush 24
    //   79: baload
    //   80: istore_2
    //   81: aload 6
    //   83: bipush 25
    //   85: baload
    //   86: istore_3
    //   87: aload 6
    //   89: bipush 26
    //   91: baload
    //   92: istore 4
    //   94: aload 6
    //   96: bipush 27
    //   98: baload
    //   99: istore 5
    //   101: aload_0
    //   102: getstatic 116	com/oneplus/media/IsoBaseMediaMetadata:PROP_WIDTH	Lcom/oneplus/base/PropertyKey;
    //   105: iload_2
    //   106: bipush 8
    //   108: ishl
    //   109: ldc 117
    //   111: iand
    //   112: iload_3
    //   113: sipush 255
    //   116: iand
    //   117: ior
    //   118: invokestatic 122	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   121: invokevirtual 92	com/oneplus/media/IsoBaseMediaMetadata:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   124: pop
    //   125: aload_0
    //   126: getstatic 125	com/oneplus/media/IsoBaseMediaMetadata:PROP_HEIGHT	Lcom/oneplus/base/PropertyKey;
    //   129: iload 4
    //   131: bipush 8
    //   133: ishl
    //   134: ldc 117
    //   136: iand
    //   137: iload 5
    //   139: sipush 255
    //   142: iand
    //   143: ior
    //   144: invokestatic 122	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   147: invokevirtual 92	com/oneplus/media/IsoBaseMediaMetadata:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   150: pop
    //   151: aload 8
    //   153: astore 6
    //   155: aload_1
    //   156: ifnull +11 -> 167
    //   159: aload_1
    //   160: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   163: aload 8
    //   165: astore 6
    //   167: aload 6
    //   169: ifnull +91 -> 260
    //   172: aload 6
    //   174: athrow
    //   175: astore 6
    //   177: goto -10 -> 167
    //   180: astore 6
    //   182: aload 9
    //   184: astore_1
    //   185: aload 6
    //   187: athrow
    //   188: astore 8
    //   190: aload 6
    //   192: astore 7
    //   194: aload 8
    //   196: astore 6
    //   198: aload 7
    //   200: astore 8
    //   202: aload_1
    //   203: ifnull +11 -> 214
    //   206: aload_1
    //   207: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   210: aload 7
    //   212: astore 8
    //   214: aload 8
    //   216: ifnull +41 -> 257
    //   219: aload 8
    //   221: athrow
    //   222: astore_1
    //   223: aload 7
    //   225: ifnonnull +9 -> 234
    //   228: aload_1
    //   229: astore 8
    //   231: goto -17 -> 214
    //   234: aload 7
    //   236: astore 8
    //   238: aload 7
    //   240: aload_1
    //   241: if_acmpeq -27 -> 214
    //   244: aload 7
    //   246: aload_1
    //   247: invokevirtual 57	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   250: aload 7
    //   252: astore 8
    //   254: goto -40 -> 214
    //   257: aload 6
    //   259: athrow
    //   260: return
    //   261: astore 8
    //   263: aload 6
    //   265: astore_1
    //   266: aload 8
    //   268: astore 6
    //   270: goto -72 -> 198
    //   273: astore 6
    //   275: goto -77 -> 198
    //   278: astore 6
    //   280: goto -95 -> 185
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	283	0	this	IsoBaseMediaMetadata
    //   0	283	1	paramArrayOfByte	byte[]
    //   80	29	2	i	int
    //   86	31	3	j	int
    //   92	42	4	k	int
    //   99	44	5	m	int
    //   26	147	6	localObject1	Object
    //   175	1	6	localThrowable1	Throwable
    //   180	11	6	localThrowable2	Throwable
    //   196	73	6	localObject2	Object
    //   273	1	6	localObject3	Object
    //   278	1	6	localThrowable3	Throwable
    //   1	250	7	localObject4	Object
    //   4	160	8	localObject5	Object
    //   188	7	8	localObject6	Object
    //   200	53	8	localObject7	Object
    //   261	6	8	localObject8	Object
    //   29	154	9	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   159	163	175	java/lang/Throwable
    //   31	54	180	java/lang/Throwable
    //   185	188	188	finally
    //   206	210	222	java/lang/Throwable
    //   31	54	261	finally
    //   54	75	273	finally
    //   101	151	273	finally
    //   54	75	278	java/lang/Throwable
    //   101	151	278	java/lang/Throwable
  }
  
  private void readTkhdBox(byte[] paramArrayOfByte)
  {
    this.m_VideoTrackHeader = new IsoBaseMediaTrackHeader(paramArrayOfByte);
    setReadOnly(PROP_WIDTH, Integer.valueOf(this.m_VideoTrackHeader.getWidth()));
    setReadOnly(PROP_HEIGHT, Integer.valueOf(this.m_VideoTrackHeader.getHeight()));
    setReadOnly(PROP_ORIENTATION, Integer.valueOf(this.m_VideoTrackHeader.getOrientation()));
  }
  
  /* Error */
  private void readTrakBox(IsoBaseMediaReader paramIsoBaseMediaReader)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_0
    //   4: getfield 21	com/oneplus/media/IsoBaseMediaMetadata:m_ContinueReading	Z
    //   7: ifeq +157 -> 164
    //   10: aload_1
    //   11: invokevirtual 29	com/oneplus/media/IsoBaseMediaReader:read	()Z
    //   14: ifeq +150 -> 164
    //   17: aload_1
    //   18: invokevirtual 33	com/oneplus/media/IsoBaseMediaReader:currentBoxType	()I
    //   21: lookupswitch	default:+27->48, 1835297121:+30->51, 1953196132:+70->91
    //   48: goto -45 -> 3
    //   51: aconst_null
    //   52: astore_3
    //   53: aconst_null
    //   54: astore_2
    //   55: aload_1
    //   56: invokevirtual 38	com/oneplus/media/IsoBaseMediaReader:getBoxDataReader	()Lcom/oneplus/media/IsoBaseMediaReader;
    //   59: astore 4
    //   61: aload 4
    //   63: astore_2
    //   64: aload 4
    //   66: astore_3
    //   67: aload_0
    //   68: aload 4
    //   70: invokespecial 143	com/oneplus/media/IsoBaseMediaMetadata:readMdiaBox	(Lcom/oneplus/media/IsoBaseMediaReader;)V
    //   73: aload 4
    //   75: ifnull +8 -> 83
    //   78: aload 4
    //   80: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   83: aconst_null
    //   84: astore_2
    //   85: aload_2
    //   86: ifnull -83 -> 3
    //   89: aload_2
    //   90: athrow
    //   91: aload_0
    //   92: aload_1
    //   93: invokevirtual 42	com/oneplus/media/IsoBaseMediaReader:getBoxData	()[B
    //   96: invokespecial 145	com/oneplus/media/IsoBaseMediaMetadata:readTkhdBox	([B)V
    //   99: goto -96 -> 3
    //   102: astore_2
    //   103: goto -18 -> 85
    //   106: astore_1
    //   107: aload_1
    //   108: athrow
    //   109: astore_3
    //   110: aload_1
    //   111: astore 4
    //   113: aload_2
    //   114: ifnull +10 -> 124
    //   117: aload_2
    //   118: invokevirtual 49	com/oneplus/media/IsoBaseMediaReader:close	()V
    //   121: aload_1
    //   122: astore 4
    //   124: aload 4
    //   126: ifnull +36 -> 162
    //   129: aload 4
    //   131: athrow
    //   132: astore_2
    //   133: aload_1
    //   134: ifnonnull +9 -> 143
    //   137: aload_2
    //   138: astore 4
    //   140: goto -16 -> 124
    //   143: aload_1
    //   144: astore 4
    //   146: aload_1
    //   147: aload_2
    //   148: if_acmpeq -24 -> 124
    //   151: aload_1
    //   152: aload_2
    //   153: invokevirtual 57	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   156: aload_1
    //   157: astore 4
    //   159: goto -35 -> 124
    //   162: aload_3
    //   163: athrow
    //   164: return
    //   165: astore_1
    //   166: aload_3
    //   167: astore_2
    //   168: aload_1
    //   169: astore_3
    //   170: aload 5
    //   172: astore_1
    //   173: goto -63 -> 110
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	176	0	this	IsoBaseMediaMetadata
    //   0	176	1	paramIsoBaseMediaReader	IsoBaseMediaReader
    //   54	36	2	localObject1	Object
    //   102	16	2	localThrowable1	Throwable
    //   132	21	2	localThrowable2	Throwable
    //   167	1	2	localObject2	Object
    //   52	15	3	localObject3	Object
    //   109	58	3	localObject4	Object
    //   169	1	3	localIsoBaseMediaReader	IsoBaseMediaReader
    //   59	99	4	localObject5	Object
    //   1	170	5	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   78	83	102	java/lang/Throwable
    //   55	61	106	java/lang/Throwable
    //   67	73	106	java/lang/Throwable
    //   107	109	109	finally
    //   117	121	132	java/lang/Throwable
    //   55	61	165	finally
    //   67	73	165	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/IsoBaseMediaMetadata.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */