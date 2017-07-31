package com.android.server.accounts;

import android.accounts.Account;
import android.accounts.AccountManagerInternal;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.UserHandle;
import android.util.PackageUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class AccountManagerBackupHelper
{
  private static final String ACCOUNT_ACCESS_GRANTS = "SELECT name, uid FROM accounts, grants WHERE accounts_id=_id";
  private static final String ATTR_ACCOUNT_SHA_256 = "account-sha-256";
  private static final String ATTR_DIGEST = "digest";
  private static final String ATTR_PACKAGE = "package";
  private static final long PENDING_RESTORE_TIMEOUT_MILLIS = 3600000L;
  private static final String TAG = "AccountManagerBackupHelper";
  private static final String TAG_PERMISSION = "permission";
  private static final String TAG_PERMISSIONS = "permissions";
  private final AccountManagerInternal mAccountManagerInternal;
  private final AccountManagerService mAccountManagerService;
  private final Object mLock = new Object();
  @GuardedBy("mLock")
  private Runnable mRestoreCancelCommand;
  @GuardedBy("mLock")
  private RestorePackageMonitor mRestorePackageMonitor;
  @GuardedBy("mLock")
  private List<PendingAppPermission> mRestorePendingAppPermissions;
  
  public AccountManagerBackupHelper(AccountManagerService paramAccountManagerService, AccountManagerInternal paramAccountManagerInternal)
  {
    this.mAccountManagerService = paramAccountManagerService;
    this.mAccountManagerInternal = paramAccountManagerInternal;
  }
  
  /* Error */
  public byte[] backupAccountAccessPermissions(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 63	com/android/server/accounts/AccountManagerBackupHelper:mAccountManagerService	Lcom/android/server/accounts/AccountManagerService;
    //   4: iload_1
    //   5: invokevirtual 102	com/android/server/accounts/AccountManagerService:getUserAccounts	(I)Lcom/android/server/accounts/AccountManagerService$UserAccounts;
    //   8: astore 6
    //   10: aload 6
    //   12: getfield 107	com/android/server/accounts/AccountManagerService$UserAccounts:cacheLock	Ljava/lang/Object;
    //   15: astore 13
    //   17: aload 13
    //   19: monitorenter
    //   20: aload 6
    //   22: getfield 111	com/android/server/accounts/AccountManagerService$UserAccounts:openHelper	Lcom/android/server/accounts/AccountManagerService$DeDatabaseHelper;
    //   25: invokevirtual 117	com/android/server/accounts/AccountManagerService$DeDatabaseHelper:getReadableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   28: astore 8
    //   30: aconst_null
    //   31: astore 12
    //   33: aconst_null
    //   34: astore 11
    //   36: aconst_null
    //   37: astore 9
    //   39: aconst_null
    //   40: astore 10
    //   42: aconst_null
    //   43: astore 7
    //   45: aconst_null
    //   46: astore 6
    //   48: aload 8
    //   50: ldc 17
    //   52: aconst_null
    //   53: invokevirtual 123	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   56: astore 8
    //   58: aload 8
    //   60: ifnull +339 -> 399
    //   63: aload 8
    //   65: astore 6
    //   67: aload 8
    //   69: astore 7
    //   71: aload 8
    //   73: invokeinterface 129 1 0
    //   78: ifeq +321 -> 399
    //   81: aload 8
    //   83: astore 6
    //   85: aload 8
    //   87: astore 7
    //   89: aload 8
    //   91: ldc -125
    //   93: invokeinterface 135 2 0
    //   98: istore_3
    //   99: aload 8
    //   101: astore 6
    //   103: aload 8
    //   105: astore 7
    //   107: aload 8
    //   109: ldc -119
    //   111: invokeinterface 135 2 0
    //   116: istore 4
    //   118: aload 8
    //   120: astore 6
    //   122: aload 8
    //   124: astore 7
    //   126: new 139	java/io/ByteArrayOutputStream
    //   129: dup
    //   130: invokespecial 140	java/io/ByteArrayOutputStream:<init>	()V
    //   133: astore 12
    //   135: aload 8
    //   137: astore 6
    //   139: aload 8
    //   141: astore 7
    //   143: new 142	com/android/internal/util/FastXmlSerializer
    //   146: dup
    //   147: invokespecial 143	com/android/internal/util/FastXmlSerializer:<init>	()V
    //   150: astore 14
    //   152: aload 8
    //   154: astore 6
    //   156: aload 8
    //   158: astore 7
    //   160: aload 14
    //   162: aload 12
    //   164: getstatic 149	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   167: invokevirtual 154	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   170: invokeinterface 160 3 0
    //   175: aload 8
    //   177: astore 6
    //   179: aload 8
    //   181: astore 7
    //   183: aload 14
    //   185: aconst_null
    //   186: iconst_1
    //   187: invokestatic 166	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   190: invokeinterface 170 3 0
    //   195: aload 8
    //   197: astore 6
    //   199: aload 8
    //   201: astore 7
    //   203: aload 14
    //   205: aconst_null
    //   206: ldc 39
    //   208: invokeinterface 174 3 0
    //   213: pop
    //   214: aload 8
    //   216: astore 6
    //   218: aload 8
    //   220: astore 7
    //   222: aload_0
    //   223: getfield 63	com/android/server/accounts/AccountManagerBackupHelper:mAccountManagerService	Lcom/android/server/accounts/AccountManagerService;
    //   226: getfield 178	com/android/server/accounts/AccountManagerService:mContext	Landroid/content/Context;
    //   229: invokevirtual 184	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   232: astore 15
    //   234: aload 8
    //   236: astore 6
    //   238: aload 8
    //   240: astore 7
    //   242: aload 8
    //   244: iload_3
    //   245: invokeinterface 188 2 0
    //   250: astore 16
    //   252: aload 8
    //   254: astore 6
    //   256: aload 8
    //   258: astore 7
    //   260: aload 15
    //   262: aload 8
    //   264: iload 4
    //   266: invokeinterface 192 2 0
    //   271: invokevirtual 198	android/content/pm/PackageManager:getPackagesForUid	(I)[Ljava/lang/String;
    //   274: astore 17
    //   276: aload 17
    //   278: ifnonnull +159 -> 437
    //   281: aload 8
    //   283: astore 6
    //   285: aload 8
    //   287: astore 7
    //   289: aload 8
    //   291: invokeinterface 201 1 0
    //   296: ifne -62 -> 234
    //   299: aload 8
    //   301: astore 6
    //   303: aload 8
    //   305: astore 7
    //   307: aload 14
    //   309: aconst_null
    //   310: ldc 39
    //   312: invokeinterface 204 3 0
    //   317: pop
    //   318: aload 8
    //   320: astore 6
    //   322: aload 8
    //   324: astore 7
    //   326: aload 14
    //   328: invokeinterface 207 1 0
    //   333: aload 8
    //   335: astore 6
    //   337: aload 8
    //   339: astore 7
    //   341: aload 14
    //   343: invokeinterface 210 1 0
    //   348: aload 8
    //   350: astore 6
    //   352: aload 8
    //   354: astore 7
    //   356: aload 12
    //   358: invokevirtual 214	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   361: astore 11
    //   363: aload 10
    //   365: astore 6
    //   367: aload 8
    //   369: ifnull +14 -> 383
    //   372: aload 8
    //   374: invokeinterface 217 1 0
    //   379: aload 10
    //   381: astore 6
    //   383: aload 6
    //   385: ifnull +279 -> 664
    //   388: aload 6
    //   390: athrow
    //   391: astore 6
    //   393: aload 13
    //   395: monitorexit
    //   396: aload 6
    //   398: athrow
    //   399: aload 12
    //   401: astore 6
    //   403: aload 8
    //   405: ifnull +14 -> 419
    //   408: aload 8
    //   410: invokeinterface 217 1 0
    //   415: aload 12
    //   417: astore 6
    //   419: aload 6
    //   421: ifnull +11 -> 432
    //   424: aload 6
    //   426: athrow
    //   427: astore 6
    //   429: goto -10 -> 419
    //   432: aload 13
    //   434: monitorexit
    //   435: aconst_null
    //   436: areturn
    //   437: iconst_0
    //   438: istore_2
    //   439: aload 8
    //   441: astore 6
    //   443: aload 8
    //   445: astore 7
    //   447: aload 17
    //   449: arraylength
    //   450: istore 5
    //   452: iload_2
    //   453: iload 5
    //   455: if_icmpge -174 -> 281
    //   458: aload 17
    //   460: iload_2
    //   461: aaload
    //   462: astore 18
    //   464: aload 8
    //   466: astore 6
    //   468: aload 8
    //   470: astore 7
    //   472: aload 15
    //   474: aload 18
    //   476: iload_1
    //   477: invokestatic 223	android/util/PackageUtils:computePackageCertSha256Digest	(Landroid/content/pm/PackageManager;Ljava/lang/String;I)Ljava/lang/String;
    //   480: astore 19
    //   482: aload 19
    //   484: ifnull +110 -> 594
    //   487: aload 8
    //   489: astore 6
    //   491: aload 8
    //   493: astore 7
    //   495: aload 14
    //   497: aconst_null
    //   498: ldc 36
    //   500: invokeinterface 174 3 0
    //   505: pop
    //   506: aload 8
    //   508: astore 6
    //   510: aload 8
    //   512: astore 7
    //   514: aload 14
    //   516: aconst_null
    //   517: ldc 20
    //   519: aload 16
    //   521: invokevirtual 228	java/lang/String:getBytes	()[B
    //   524: invokestatic 232	android/util/PackageUtils:computeSha256Digest	([B)Ljava/lang/String;
    //   527: invokeinterface 236 4 0
    //   532: pop
    //   533: aload 8
    //   535: astore 6
    //   537: aload 8
    //   539: astore 7
    //   541: aload 14
    //   543: aconst_null
    //   544: ldc 26
    //   546: aload 18
    //   548: invokeinterface 236 4 0
    //   553: pop
    //   554: aload 8
    //   556: astore 6
    //   558: aload 8
    //   560: astore 7
    //   562: aload 14
    //   564: aconst_null
    //   565: ldc 23
    //   567: aload 19
    //   569: invokeinterface 236 4 0
    //   574: pop
    //   575: aload 8
    //   577: astore 6
    //   579: aload 8
    //   581: astore 7
    //   583: aload 14
    //   585: aconst_null
    //   586: ldc 36
    //   588: invokeinterface 204 3 0
    //   593: pop
    //   594: iload_2
    //   595: iconst_1
    //   596: iadd
    //   597: istore_2
    //   598: goto -146 -> 452
    //   601: astore 10
    //   603: aload 8
    //   605: astore 6
    //   607: aload 8
    //   609: astore 7
    //   611: ldc 33
    //   613: ldc -18
    //   615: aload 10
    //   617: invokestatic 244	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   620: pop
    //   621: aload 11
    //   623: astore 6
    //   625: aload 8
    //   627: ifnull +14 -> 641
    //   630: aload 8
    //   632: invokeinterface 217 1 0
    //   637: aload 11
    //   639: astore 6
    //   641: aload 6
    //   643: ifnull +11 -> 654
    //   646: aload 6
    //   648: athrow
    //   649: astore 6
    //   651: goto -10 -> 641
    //   654: aload 13
    //   656: monitorexit
    //   657: aconst_null
    //   658: areturn
    //   659: astore 6
    //   661: goto -278 -> 383
    //   664: aload 13
    //   666: monitorexit
    //   667: aload 11
    //   669: areturn
    //   670: astore 7
    //   672: aload 7
    //   674: athrow
    //   675: astore 8
    //   677: aload 7
    //   679: astore 9
    //   681: aload 6
    //   683: ifnull +14 -> 697
    //   686: aload 6
    //   688: invokeinterface 217 1 0
    //   693: aload 7
    //   695: astore 9
    //   697: aload 9
    //   699: ifnull +31 -> 730
    //   702: aload 9
    //   704: athrow
    //   705: aload 7
    //   707: astore 9
    //   709: aload 7
    //   711: aload 6
    //   713: if_acmpeq -16 -> 697
    //   716: aload 7
    //   718: aload 6
    //   720: invokevirtual 248	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   723: aload 7
    //   725: astore 9
    //   727: goto -30 -> 697
    //   730: aload 8
    //   732: athrow
    //   733: astore 8
    //   735: aload 7
    //   737: astore 6
    //   739: aload 9
    //   741: astore 7
    //   743: goto -66 -> 677
    //   746: astore 6
    //   748: aload 7
    //   750: ifnonnull -45 -> 705
    //   753: aload 6
    //   755: astore 9
    //   757: goto -60 -> 697
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	760	0	this	AccountManagerBackupHelper
    //   0	760	1	paramInt	int
    //   438	160	2	i	int
    //   98	147	3	j	int
    //   116	149	4	k	int
    //   450	6	5	m	int
    //   8	381	6	localObject1	Object
    //   391	6	6	localObject2	Object
    //   401	24	6	localObject3	Object
    //   427	1	6	localThrowable1	Throwable
    //   441	206	6	localObject4	Object
    //   649	1	6	localThrowable2	Throwable
    //   659	60	6	localThrowable3	Throwable
    //   737	1	6	localObject5	Object
    //   746	8	6	localThrowable4	Throwable
    //   43	567	7	localObject6	Object
    //   670	66	7	localThrowable5	Throwable
    //   741	8	7	localObject7	Object
    //   28	603	8	localObject8	Object
    //   675	56	8	localObject9	Object
    //   733	1	8	localObject10	Object
    //   37	719	9	localObject11	Object
    //   40	340	10	localObject12	Object
    //   601	15	10	localIOException	java.io.IOException
    //   34	634	11	arrayOfByte	byte[]
    //   31	385	12	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   15	650	13	localObject13	Object
    //   150	434	14	localFastXmlSerializer	com.android.internal.util.FastXmlSerializer
    //   232	241	15	localPackageManager	PackageManager
    //   250	270	16	str1	String
    //   274	185	17	arrayOfString	String[]
    //   462	85	18	str2	String
    //   480	88	19	str3	String
    // Exception table:
    //   from	to	target	type
    //   20	30	391	finally
    //   372	379	391	finally
    //   388	391	391	finally
    //   408	415	391	finally
    //   424	427	391	finally
    //   630	637	391	finally
    //   646	649	391	finally
    //   686	693	391	finally
    //   702	705	391	finally
    //   716	723	391	finally
    //   730	733	391	finally
    //   408	415	427	java/lang/Throwable
    //   143	152	601	java/io/IOException
    //   160	175	601	java/io/IOException
    //   183	195	601	java/io/IOException
    //   203	214	601	java/io/IOException
    //   222	234	601	java/io/IOException
    //   242	252	601	java/io/IOException
    //   260	276	601	java/io/IOException
    //   289	299	601	java/io/IOException
    //   307	318	601	java/io/IOException
    //   326	333	601	java/io/IOException
    //   341	348	601	java/io/IOException
    //   447	452	601	java/io/IOException
    //   472	482	601	java/io/IOException
    //   495	506	601	java/io/IOException
    //   514	533	601	java/io/IOException
    //   541	554	601	java/io/IOException
    //   562	575	601	java/io/IOException
    //   583	594	601	java/io/IOException
    //   630	637	649	java/lang/Throwable
    //   372	379	659	java/lang/Throwable
    //   48	58	670	java/lang/Throwable
    //   71	81	670	java/lang/Throwable
    //   89	99	670	java/lang/Throwable
    //   107	118	670	java/lang/Throwable
    //   126	135	670	java/lang/Throwable
    //   143	152	670	java/lang/Throwable
    //   160	175	670	java/lang/Throwable
    //   183	195	670	java/lang/Throwable
    //   203	214	670	java/lang/Throwable
    //   222	234	670	java/lang/Throwable
    //   242	252	670	java/lang/Throwable
    //   260	276	670	java/lang/Throwable
    //   289	299	670	java/lang/Throwable
    //   307	318	670	java/lang/Throwable
    //   326	333	670	java/lang/Throwable
    //   341	348	670	java/lang/Throwable
    //   356	363	670	java/lang/Throwable
    //   447	452	670	java/lang/Throwable
    //   472	482	670	java/lang/Throwable
    //   495	506	670	java/lang/Throwable
    //   514	533	670	java/lang/Throwable
    //   541	554	670	java/lang/Throwable
    //   562	575	670	java/lang/Throwable
    //   583	594	670	java/lang/Throwable
    //   611	621	670	java/lang/Throwable
    //   672	675	675	finally
    //   48	58	733	finally
    //   71	81	733	finally
    //   89	99	733	finally
    //   107	118	733	finally
    //   126	135	733	finally
    //   143	152	733	finally
    //   160	175	733	finally
    //   183	195	733	finally
    //   203	214	733	finally
    //   222	234	733	finally
    //   242	252	733	finally
    //   260	276	733	finally
    //   289	299	733	finally
    //   307	318	733	finally
    //   326	333	733	finally
    //   341	348	733	finally
    //   356	363	733	finally
    //   447	452	733	finally
    //   472	482	733	finally
    //   495	506	733	finally
    //   514	533	733	finally
    //   541	554	733	finally
    //   562	575	733	finally
    //   583	594	733	finally
    //   611	621	733	finally
    //   686	693	746	java/lang/Throwable
  }
  
  /* Error */
  public void restoreAccountAccessPermissions(byte[] arg1, int paramInt)
  {
    // Byte code:
    //   0: new 254	java/io/ByteArrayInputStream
    //   3: dup
    //   4: aload_1
    //   5: invokespecial 257	java/io/ByteArrayInputStream:<init>	([B)V
    //   8: astore 5
    //   10: invokestatic 263	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   13: astore_1
    //   14: aload_1
    //   15: aload 5
    //   17: getstatic 149	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   20: invokevirtual 154	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   23: invokeinterface 269 3 0
    //   28: aload_0
    //   29: getfield 63	com/android/server/accounts/AccountManagerBackupHelper:mAccountManagerService	Lcom/android/server/accounts/AccountManagerService;
    //   32: getfield 178	com/android/server/accounts/AccountManagerService:mContext	Landroid/content/Context;
    //   35: invokevirtual 184	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   38: astore 5
    //   40: aload_1
    //   41: invokeinterface 273 1 0
    //   46: istore_3
    //   47: aload_1
    //   48: iload_3
    //   49: invokestatic 279	com/android/internal/util/XmlUtils:nextElementWithin	(Lorg/xmlpull/v1/XmlPullParser;I)Z
    //   52: ifeq +252 -> 304
    //   55: ldc 39
    //   57: aload_1
    //   58: invokeinterface 282 1 0
    //   63: invokevirtual 286	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   66: ifeq -19 -> 47
    //   69: aload_1
    //   70: invokeinterface 273 1 0
    //   75: istore 4
    //   77: aload_1
    //   78: iload 4
    //   80: invokestatic 279	com/android/internal/util/XmlUtils:nextElementWithin	(Lorg/xmlpull/v1/XmlPullParser;I)Z
    //   83: ifeq -36 -> 47
    //   86: ldc 36
    //   88: aload_1
    //   89: invokeinterface 282 1 0
    //   94: invokevirtual 286	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   97: ifeq -20 -> 77
    //   100: aload_1
    //   101: aconst_null
    //   102: ldc 20
    //   104: invokeinterface 290 3 0
    //   109: astore 6
    //   111: aload 6
    //   113: invokestatic 296	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   116: ifeq +7 -> 123
    //   119: aload_1
    //   120: invokestatic 300	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   123: aload_1
    //   124: aconst_null
    //   125: ldc 26
    //   127: invokeinterface 290 3 0
    //   132: astore 7
    //   134: aload 7
    //   136: invokestatic 296	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   139: ifeq +7 -> 146
    //   142: aload_1
    //   143: invokestatic 300	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   146: aload_1
    //   147: aconst_null
    //   148: ldc 23
    //   150: invokeinterface 290 3 0
    //   155: astore 8
    //   157: aload 8
    //   159: invokestatic 296	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   162: ifeq +7 -> 169
    //   165: aload_1
    //   166: invokestatic 300	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   169: new 9	com/android/server/accounts/AccountManagerBackupHelper$PendingAppPermission
    //   172: dup
    //   173: aload_0
    //   174: aload 6
    //   176: aload 7
    //   178: aload 8
    //   180: iload_2
    //   181: invokespecial 303	com/android/server/accounts/AccountManagerBackupHelper$PendingAppPermission:<init>	(Lcom/android/server/accounts/AccountManagerBackupHelper;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V
    //   184: astore 7
    //   186: aload 7
    //   188: aload 5
    //   190: invokevirtual 307	com/android/server/accounts/AccountManagerBackupHelper$PendingAppPermission:apply	(Landroid/content/pm/PackageManager;)Z
    //   193: ifne -116 -> 77
    //   196: aload_0
    //   197: getfield 67	com/android/server/accounts/AccountManagerBackupHelper:mLock	Ljava/lang/Object;
    //   200: astore 6
    //   202: aload 6
    //   204: monitorenter
    //   205: aload_0
    //   206: getfield 75	com/android/server/accounts/AccountManagerBackupHelper:mRestorePackageMonitor	Lcom/android/server/accounts/AccountManagerBackupHelper$RestorePackageMonitor;
    //   209: ifnonnull +41 -> 250
    //   212: aload_0
    //   213: new 12	com/android/server/accounts/AccountManagerBackupHelper$RestorePackageMonitor
    //   216: dup
    //   217: aload_0
    //   218: aconst_null
    //   219: invokespecial 310	com/android/server/accounts/AccountManagerBackupHelper$RestorePackageMonitor:<init>	(Lcom/android/server/accounts/AccountManagerBackupHelper;Lcom/android/server/accounts/AccountManagerBackupHelper$RestorePackageMonitor;)V
    //   222: putfield 75	com/android/server/accounts/AccountManagerBackupHelper:mRestorePackageMonitor	Lcom/android/server/accounts/AccountManagerBackupHelper$RestorePackageMonitor;
    //   225: aload_0
    //   226: getfield 75	com/android/server/accounts/AccountManagerBackupHelper:mRestorePackageMonitor	Lcom/android/server/accounts/AccountManagerBackupHelper$RestorePackageMonitor;
    //   229: aload_0
    //   230: getfield 63	com/android/server/accounts/AccountManagerBackupHelper:mAccountManagerService	Lcom/android/server/accounts/AccountManagerService;
    //   233: getfield 178	com/android/server/accounts/AccountManagerService:mContext	Landroid/content/Context;
    //   236: aload_0
    //   237: getfield 63	com/android/server/accounts/AccountManagerBackupHelper:mAccountManagerService	Lcom/android/server/accounts/AccountManagerService;
    //   240: getfield 314	com/android/server/accounts/AccountManagerService:mMessageHandler	Lcom/android/server/accounts/AccountManagerService$MessageHandler;
    //   243: invokevirtual 320	com/android/server/accounts/AccountManagerService$MessageHandler:getLooper	()Landroid/os/Looper;
    //   246: iconst_1
    //   247: invokevirtual 324	com/android/server/accounts/AccountManagerBackupHelper$RestorePackageMonitor:register	(Landroid/content/Context;Landroid/os/Looper;Z)V
    //   250: aload_0
    //   251: getfield 79	com/android/server/accounts/AccountManagerBackupHelper:mRestorePendingAppPermissions	Ljava/util/List;
    //   254: ifnonnull +14 -> 268
    //   257: aload_0
    //   258: new 326	java/util/ArrayList
    //   261: dup
    //   262: invokespecial 327	java/util/ArrayList:<init>	()V
    //   265: putfield 79	com/android/server/accounts/AccountManagerBackupHelper:mRestorePendingAppPermissions	Ljava/util/List;
    //   268: aload_0
    //   269: getfield 79	com/android/server/accounts/AccountManagerBackupHelper:mRestorePendingAppPermissions	Ljava/util/List;
    //   272: aload 7
    //   274: invokeinterface 332 2 0
    //   279: pop
    //   280: aload 6
    //   282: monitorexit
    //   283: goto -206 -> 77
    //   286: astore_1
    //   287: ldc 33
    //   289: ldc_w 334
    //   292: aload_1
    //   293: invokestatic 244	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   296: pop
    //   297: return
    //   298: astore_1
    //   299: aload 6
    //   301: monitorexit
    //   302: aload_1
    //   303: athrow
    //   304: aload_0
    //   305: getfield 67	com/android/server/accounts/AccountManagerBackupHelper:mLock	Ljava/lang/Object;
    //   308: astore_1
    //   309: aload_1
    //   310: monitorenter
    //   311: aload_0
    //   312: new 6	com/android/server/accounts/AccountManagerBackupHelper$CancelRestoreCommand
    //   315: dup
    //   316: aload_0
    //   317: aconst_null
    //   318: invokespecial 337	com/android/server/accounts/AccountManagerBackupHelper$CancelRestoreCommand:<init>	(Lcom/android/server/accounts/AccountManagerBackupHelper;Lcom/android/server/accounts/AccountManagerBackupHelper$CancelRestoreCommand;)V
    //   321: putfield 71	com/android/server/accounts/AccountManagerBackupHelper:mRestoreCancelCommand	Ljava/lang/Runnable;
    //   324: aload_1
    //   325: monitorexit
    //   326: aload_0
    //   327: getfield 63	com/android/server/accounts/AccountManagerBackupHelper:mAccountManagerService	Lcom/android/server/accounts/AccountManagerService;
    //   330: getfield 314	com/android/server/accounts/AccountManagerService:mMessageHandler	Lcom/android/server/accounts/AccountManagerService$MessageHandler;
    //   333: aload_0
    //   334: getfield 71	com/android/server/accounts/AccountManagerBackupHelper:mRestoreCancelCommand	Ljava/lang/Runnable;
    //   337: ldc2_w 29
    //   340: invokevirtual 341	com/android/server/accounts/AccountManagerService$MessageHandler:postDelayed	(Ljava/lang/Runnable;J)Z
    //   343: pop
    //   344: return
    //   345: astore 5
    //   347: aload_1
    //   348: monitorexit
    //   349: aload 5
    //   351: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	352	0	this	AccountManagerBackupHelper
    //   0	352	2	paramInt	int
    //   46	3	3	i	int
    //   75	4	4	j	int
    //   8	181	5	localObject1	Object
    //   345	5	5	localObject2	Object
    //   132	141	7	localObject4	Object
    //   155	24	8	str	String
    // Exception table:
    //   from	to	target	type
    //   0	47	286	org/xmlpull/v1/XmlPullParserException
    //   0	47	286	java/io/IOException
    //   47	77	286	org/xmlpull/v1/XmlPullParserException
    //   47	77	286	java/io/IOException
    //   77	123	286	org/xmlpull/v1/XmlPullParserException
    //   77	123	286	java/io/IOException
    //   123	146	286	org/xmlpull/v1/XmlPullParserException
    //   123	146	286	java/io/IOException
    //   146	169	286	org/xmlpull/v1/XmlPullParserException
    //   146	169	286	java/io/IOException
    //   169	205	286	org/xmlpull/v1/XmlPullParserException
    //   169	205	286	java/io/IOException
    //   280	283	286	org/xmlpull/v1/XmlPullParserException
    //   280	283	286	java/io/IOException
    //   299	304	286	org/xmlpull/v1/XmlPullParserException
    //   299	304	286	java/io/IOException
    //   304	311	286	org/xmlpull/v1/XmlPullParserException
    //   304	311	286	java/io/IOException
    //   324	344	286	org/xmlpull/v1/XmlPullParserException
    //   324	344	286	java/io/IOException
    //   347	352	286	org/xmlpull/v1/XmlPullParserException
    //   347	352	286	java/io/IOException
    //   205	250	298	finally
    //   250	268	298	finally
    //   268	280	298	finally
    //   311	324	345	finally
  }
  
  private final class CancelRestoreCommand
    implements Runnable
  {
    private CancelRestoreCommand() {}
    
    public void run()
    {
      synchronized (AccountManagerBackupHelper.-get2(AccountManagerBackupHelper.this))
      {
        AccountManagerBackupHelper.-set2(AccountManagerBackupHelper.this, null);
        if (AccountManagerBackupHelper.-get4(AccountManagerBackupHelper.this) != null)
        {
          AccountManagerBackupHelper.-get4(AccountManagerBackupHelper.this).unregister();
          AccountManagerBackupHelper.-set1(AccountManagerBackupHelper.this, null);
        }
        return;
      }
    }
  }
  
  private final class PendingAppPermission
  {
    private final String accountDigest;
    private final String certDigest;
    private final String packageName;
    private final int userId;
    
    public PendingAppPermission(String paramString1, String paramString2, String paramString3, int paramInt)
    {
      this.accountDigest = paramString1;
      this.packageName = paramString2;
      this.certDigest = paramString3;
      this.userId = paramInt;
    }
    
    public boolean apply(PackageManager paramPackageManager)
    {
      Object localObject1 = null;
      Object localObject2 = AccountManagerBackupHelper.-get1(AccountManagerBackupHelper.this).getUserAccounts(this.userId);
      synchronized (((AccountManagerService.UserAccounts)localObject2).cacheLock)
      {
        Iterator localIterator = ((AccountManagerService.UserAccounts)localObject2).accountCache.values().iterator();
        do
        {
          localObject2 = localObject1;
          if (!localIterator.hasNext()) {
            break;
          }
          Account[] arrayOfAccount = (Account[])localIterator.next();
          i = 0;
          int j = arrayOfAccount.length;
          localObject2 = localObject1;
          if (i < j)
          {
            localObject2 = arrayOfAccount[i];
            boolean bool = this.accountDigest.equals(PackageUtils.computeSha256Digest(((Account)localObject2).name.getBytes()));
            if (!bool) {
              break label135;
            }
          }
          localObject1 = localObject2;
        } while (localObject2 == null);
        if (localObject2 == null)
        {
          return false;
          label135:
          i += 1;
        }
      }
      try
      {
        paramPackageManager = paramPackageManager.getPackageInfoAsUser(this.packageName, 64, this.userId);
        localObject1 = PackageUtils.computeCertSha256Digest(paramPackageManager.signatures[0]);
        if (!this.certDigest.equals(localObject1)) {
          return false;
        }
      }
      catch (PackageManager.NameNotFoundException paramPackageManager)
      {
        return false;
      }
      int i = paramPackageManager.applicationInfo.uid;
      if (!AccountManagerBackupHelper.-get0(AccountManagerBackupHelper.this).hasAccountAccess((Account)localObject2, i)) {
        AccountManagerBackupHelper.-get1(AccountManagerBackupHelper.this).grantAppPermission((Account)localObject2, "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", i);
      }
      return true;
    }
  }
  
  private final class RestorePackageMonitor
    extends PackageMonitor
  {
    private RestorePackageMonitor() {}
    
    public void onPackageAdded(String paramString, int paramInt)
    {
      for (;;)
      {
        synchronized (AccountManagerBackupHelper.-get2(AccountManagerBackupHelper.this))
        {
          Object localObject2 = AccountManagerBackupHelper.-get5(AccountManagerBackupHelper.this);
          if (localObject2 == null) {
            return;
          }
          paramInt = UserHandle.getUserId(paramInt);
          if (paramInt != 0) {
            return;
          }
          paramInt = AccountManagerBackupHelper.-get5(AccountManagerBackupHelper.this).size() - 1;
          if (paramInt >= 0)
          {
            localObject2 = (AccountManagerBackupHelper.PendingAppPermission)AccountManagerBackupHelper.-get5(AccountManagerBackupHelper.this).get(paramInt);
            if ((!AccountManagerBackupHelper.PendingAppPermission.-get0((AccountManagerBackupHelper.PendingAppPermission)localObject2).equals(paramString)) || (!((AccountManagerBackupHelper.PendingAppPermission)localObject2).apply(AccountManagerBackupHelper.-get1(AccountManagerBackupHelper.this).mContext.getPackageManager()))) {
              break label203;
            }
            AccountManagerBackupHelper.-get5(AccountManagerBackupHelper.this).remove(paramInt);
          }
        }
        if ((AccountManagerBackupHelper.-get5(AccountManagerBackupHelper.this).isEmpty()) && (AccountManagerBackupHelper.-get3(AccountManagerBackupHelper.this) != null))
        {
          AccountManagerBackupHelper.-get1(AccountManagerBackupHelper.this).mMessageHandler.removeCallbacks(AccountManagerBackupHelper.-get3(AccountManagerBackupHelper.this));
          AccountManagerBackupHelper.-get3(AccountManagerBackupHelper.this).run();
          AccountManagerBackupHelper.-set0(AccountManagerBackupHelper.this, null);
        }
        return;
        label203:
        paramInt -= 1;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accounts/AccountManagerBackupHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */