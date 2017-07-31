package android.app;

import android.os.Binder;
import android.os.IBinder;

public abstract class ApplicationThreadNative
  extends Binder
  implements IApplicationThread
{
  public ApplicationThreadNative()
  {
    attachInterface(this, "android.app.IApplicationThread");
  }
  
  public static IApplicationThread asInterface(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return null;
    }
    IApplicationThread localIApplicationThread = (IApplicationThread)paramIBinder.queryLocalInterface("android.app.IApplicationThread");
    if (localIApplicationThread != null) {
      return localIApplicationThread;
    }
    return new ApplicationThreadProxy(paramIBinder);
  }
  
  public IBinder asBinder()
  {
    return this;
  }
  
  /* Error */
  public boolean onTransact(int paramInt1, android.os.Parcel paramParcel1, android.os.Parcel paramParcel2, int paramInt2)
    throws android.os.RemoteException
  {
    // Byte code:
    //   0: iload_1
    //   1: tableswitch	default:+427->428, 1:+437->438, 2:+427->428, 3:+515->516, 4:+555->556, 5:+627->628, 6:+673->674, 7:+696->697, 8:+1060->1061, 9:+1108->1109, 10:+1148->1149, 11:+1258->1259, 12:+1462->1463, 13:+1478->1479, 14:+1717->1718, 15:+427->428, 16:+1741->1742, 17:+1386->1387, 18:+1765->1766, 19:+1825->1826, 20:+1302->1303, 21:+1358->1359, 22:+1837->1838, 23:+1931->1932, 24:+2034->2035, 25:+2046->2047, 26:+936->937, 27:+591->592, 28:+2131->2132, 29:+2193->2194, 30:+2209->2210, 31:+2249->2250, 32:+2876->2877, 33:+1729->1730, 34:+2285->2286, 35:+2305->2306, 36:+2321->2322, 37:+2383->2384, 38:+1777->1778, 39:+1789->1790, 40:+2438->2439, 41:+2454->2455, 42:+2482->2483, 43:+2498->2499, 44:+2663->2664, 45:+1884->1885, 46:+2722->2723, 47:+2781->2782, 48:+2801->2802, 49:+2833->2834, 50:+2907->2908, 51:+2927->2928, 52:+2955->2956, 53:+2990->2991, 54:+3010->3011, 55:+3052->3053, 56:+3072->3073, 57:+3092->3093, 58:+3104->3105, 59:+3137->3138, 60:+3173->3174, 61:+2108->2109, 62:+427->428, 63:+427->428, 64:+427->428, 65:+427->428, 66:+427->428, 67:+427->428, 68:+427->428, 69:+427->428, 70:+427->428, 71:+427->428, 72:+427->428, 73:+427->428, 74:+427->428, 75:+427->428, 76:+427->428, 77:+427->428, 78:+427->428, 79:+427->428, 80:+427->428, 81:+427->428, 82:+427->428, 83:+427->428, 84:+427->428, 85:+427->428, 86:+427->428, 87:+427->428, 88:+427->428, 89:+427->428, 90:+427->428, 91:+427->428, 92:+427->428, 93:+427->428, 94:+427->428, 95:+427->428, 96:+427->428, 97:+427->428, 98:+427->428, 99:+427->428, 100:+427->428, 101:+427->428, 102:+427->428, 103:+3209->3210
    //   428: aload_0
    //   429: iload_1
    //   430: aload_2
    //   431: aload_3
    //   432: iload 4
    //   434: invokespecial 40	android/os/Binder:onTransact	(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z
    //   437: ireturn
    //   438: aload_2
    //   439: ldc 12
    //   441: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   444: aload_2
    //   445: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   448: astore_3
    //   449: aload_2
    //   450: invokevirtual 53	android/os/Parcel:readInt	()I
    //   453: ifeq +45 -> 498
    //   456: iconst_1
    //   457: istore 5
    //   459: aload_2
    //   460: invokevirtual 53	android/os/Parcel:readInt	()I
    //   463: ifeq +41 -> 504
    //   466: iconst_1
    //   467: istore 6
    //   469: aload_2
    //   470: invokevirtual 53	android/os/Parcel:readInt	()I
    //   473: istore_1
    //   474: aload_2
    //   475: invokevirtual 53	android/os/Parcel:readInt	()I
    //   478: ifeq +32 -> 510
    //   481: iconst_1
    //   482: istore 7
    //   484: aload_0
    //   485: aload_3
    //   486: iload 5
    //   488: iload 6
    //   490: iload_1
    //   491: iload 7
    //   493: invokevirtual 57	android/app/ApplicationThreadNative:schedulePauseActivity	(Landroid/os/IBinder;ZZIZ)V
    //   496: iconst_1
    //   497: ireturn
    //   498: iconst_0
    //   499: istore 5
    //   501: goto -42 -> 459
    //   504: iconst_0
    //   505: istore 6
    //   507: goto -38 -> 469
    //   510: iconst_0
    //   511: istore 7
    //   513: goto -29 -> 484
    //   516: aload_2
    //   517: ldc 12
    //   519: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   522: aload_2
    //   523: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   526: astore_3
    //   527: aload_2
    //   528: invokevirtual 53	android/os/Parcel:readInt	()I
    //   531: ifeq +19 -> 550
    //   534: iconst_1
    //   535: istore 5
    //   537: aload_0
    //   538: aload_3
    //   539: iload 5
    //   541: aload_2
    //   542: invokevirtual 53	android/os/Parcel:readInt	()I
    //   545: invokevirtual 61	android/app/ApplicationThreadNative:scheduleStopActivity	(Landroid/os/IBinder;ZI)V
    //   548: iconst_1
    //   549: ireturn
    //   550: iconst_0
    //   551: istore 5
    //   553: goto -16 -> 537
    //   556: aload_2
    //   557: ldc 12
    //   559: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   562: aload_2
    //   563: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   566: astore_3
    //   567: aload_2
    //   568: invokevirtual 53	android/os/Parcel:readInt	()I
    //   571: ifeq +15 -> 586
    //   574: iconst_1
    //   575: istore 5
    //   577: aload_0
    //   578: aload_3
    //   579: iload 5
    //   581: invokevirtual 65	android/app/ApplicationThreadNative:scheduleWindowVisibility	(Landroid/os/IBinder;Z)V
    //   584: iconst_1
    //   585: ireturn
    //   586: iconst_0
    //   587: istore 5
    //   589: goto -12 -> 577
    //   592: aload_2
    //   593: ldc 12
    //   595: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   598: aload_2
    //   599: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   602: astore_3
    //   603: aload_2
    //   604: invokevirtual 53	android/os/Parcel:readInt	()I
    //   607: ifeq +15 -> 622
    //   610: iconst_1
    //   611: istore 5
    //   613: aload_0
    //   614: aload_3
    //   615: iload 5
    //   617: invokevirtual 68	android/app/ApplicationThreadNative:scheduleSleeping	(Landroid/os/IBinder;Z)V
    //   620: iconst_1
    //   621: ireturn
    //   622: iconst_0
    //   623: istore 5
    //   625: goto -12 -> 613
    //   628: aload_2
    //   629: ldc 12
    //   631: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   634: aload_2
    //   635: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   638: astore_3
    //   639: aload_2
    //   640: invokevirtual 53	android/os/Parcel:readInt	()I
    //   643: istore_1
    //   644: aload_2
    //   645: invokevirtual 53	android/os/Parcel:readInt	()I
    //   648: ifeq +20 -> 668
    //   651: iconst_1
    //   652: istore 5
    //   654: aload_0
    //   655: aload_3
    //   656: iload_1
    //   657: iload 5
    //   659: aload_2
    //   660: invokevirtual 72	android/os/Parcel:readBundle	()Landroid/os/Bundle;
    //   663: invokevirtual 76	android/app/ApplicationThreadNative:scheduleResumeActivity	(Landroid/os/IBinder;IZLandroid/os/Bundle;)V
    //   666: iconst_1
    //   667: ireturn
    //   668: iconst_0
    //   669: istore 5
    //   671: goto -17 -> 654
    //   674: aload_2
    //   675: ldc 12
    //   677: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   680: aload_0
    //   681: aload_2
    //   682: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   685: aload_2
    //   686: getstatic 82	android/app/ResultInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   689: invokevirtual 86	android/os/Parcel:createTypedArrayList	(Landroid/os/Parcelable$Creator;)Ljava/util/ArrayList;
    //   692: invokevirtual 90	android/app/ApplicationThreadNative:scheduleSendResult	(Landroid/os/IBinder;Ljava/util/List;)V
    //   695: iconst_1
    //   696: ireturn
    //   697: aload_2
    //   698: ldc 12
    //   700: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   703: getstatic 93	android/content/Intent:CREATOR	Landroid/os/Parcelable$Creator;
    //   706: aload_2
    //   707: invokeinterface 99 2 0
    //   712: checkcast 92	android/content/Intent
    //   715: astore 10
    //   717: aload_2
    //   718: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   721: astore 11
    //   723: aload_2
    //   724: invokevirtual 53	android/os/Parcel:readInt	()I
    //   727: istore_1
    //   728: getstatic 102	android/content/pm/ActivityInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   731: aload_2
    //   732: invokeinterface 99 2 0
    //   737: checkcast 101	android/content/pm/ActivityInfo
    //   740: astore 12
    //   742: getstatic 105	android/content/res/Configuration:CREATOR	Landroid/os/Parcelable$Creator;
    //   745: aload_2
    //   746: invokeinterface 99 2 0
    //   751: checkcast 104	android/content/res/Configuration
    //   754: astore 13
    //   756: aconst_null
    //   757: astore_3
    //   758: aload_2
    //   759: invokevirtual 53	android/os/Parcel:readInt	()I
    //   762: ifeq +16 -> 778
    //   765: getstatic 105	android/content/res/Configuration:CREATOR	Landroid/os/Parcelable$Creator;
    //   768: aload_2
    //   769: invokeinterface 99 2 0
    //   774: checkcast 104	android/content/res/Configuration
    //   777: astore_3
    //   778: getstatic 108	android/content/res/CompatibilityInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   781: aload_2
    //   782: invokeinterface 99 2 0
    //   787: checkcast 107	android/content/res/CompatibilityInfo
    //   790: astore 14
    //   792: aload_2
    //   793: invokevirtual 112	android/os/Parcel:readString	()Ljava/lang/String;
    //   796: astore 15
    //   798: aload_2
    //   799: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   802: invokestatic 117	com/android/internal/app/IVoiceInteractor$Stub:asInterface	(Landroid/os/IBinder;)Lcom/android/internal/app/IVoiceInteractor;
    //   805: astore 16
    //   807: aload_2
    //   808: invokevirtual 53	android/os/Parcel:readInt	()I
    //   811: istore 4
    //   813: aload_2
    //   814: invokevirtual 72	android/os/Parcel:readBundle	()Landroid/os/Bundle;
    //   817: astore 17
    //   819: aload_2
    //   820: invokevirtual 121	android/os/Parcel:readPersistableBundle	()Landroid/os/PersistableBundle;
    //   823: astore 18
    //   825: aload_2
    //   826: getstatic 82	android/app/ResultInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   829: invokevirtual 86	android/os/Parcel:createTypedArrayList	(Landroid/os/Parcelable$Creator;)Ljava/util/ArrayList;
    //   832: astore 19
    //   834: aload_2
    //   835: getstatic 124	com/android/internal/content/ReferrerIntent:CREATOR	Landroid/os/Parcelable$Creator;
    //   838: invokevirtual 86	android/os/Parcel:createTypedArrayList	(Landroid/os/Parcelable$Creator;)Ljava/util/ArrayList;
    //   841: astore 20
    //   843: aload_2
    //   844: invokevirtual 53	android/os/Parcel:readInt	()I
    //   847: ifeq +73 -> 920
    //   850: iconst_1
    //   851: istore 5
    //   853: aload_2
    //   854: invokevirtual 53	android/os/Parcel:readInt	()I
    //   857: ifeq +69 -> 926
    //   860: iconst_1
    //   861: istore 6
    //   863: aload_2
    //   864: invokevirtual 53	android/os/Parcel:readInt	()I
    //   867: ifeq +65 -> 932
    //   870: getstatic 127	android/app/ProfilerInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   873: aload_2
    //   874: invokeinterface 99 2 0
    //   879: checkcast 126	android/app/ProfilerInfo
    //   882: astore_2
    //   883: aload_0
    //   884: aload 10
    //   886: aload 11
    //   888: iload_1
    //   889: aload 12
    //   891: aload 13
    //   893: aload_3
    //   894: aload 14
    //   896: aload 15
    //   898: aload 16
    //   900: iload 4
    //   902: aload 17
    //   904: aload 18
    //   906: aload 19
    //   908: aload 20
    //   910: iload 5
    //   912: iload 6
    //   914: aload_2
    //   915: invokevirtual 131	android/app/ApplicationThreadNative:scheduleLaunchActivity	(Landroid/content/Intent;Landroid/os/IBinder;ILandroid/content/pm/ActivityInfo;Landroid/content/res/Configuration;Landroid/content/res/Configuration;Landroid/content/res/CompatibilityInfo;Ljava/lang/String;Lcom/android/internal/app/IVoiceInteractor;ILandroid/os/Bundle;Landroid/os/PersistableBundle;Ljava/util/List;Ljava/util/List;ZZLandroid/app/ProfilerInfo;)V
    //   918: iconst_1
    //   919: ireturn
    //   920: iconst_0
    //   921: istore 5
    //   923: goto -70 -> 853
    //   926: iconst_0
    //   927: istore 6
    //   929: goto -66 -> 863
    //   932: aconst_null
    //   933: astore_2
    //   934: goto -51 -> 883
    //   937: aload_2
    //   938: ldc 12
    //   940: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   943: aload_2
    //   944: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   947: astore 10
    //   949: aload_2
    //   950: getstatic 82	android/app/ResultInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   953: invokevirtual 86	android/os/Parcel:createTypedArrayList	(Landroid/os/Parcelable$Creator;)Ljava/util/ArrayList;
    //   956: astore 11
    //   958: aload_2
    //   959: getstatic 124	com/android/internal/content/ReferrerIntent:CREATOR	Landroid/os/Parcelable$Creator;
    //   962: invokevirtual 86	android/os/Parcel:createTypedArrayList	(Landroid/os/Parcelable$Creator;)Ljava/util/ArrayList;
    //   965: astore 12
    //   967: aload_2
    //   968: invokevirtual 53	android/os/Parcel:readInt	()I
    //   971: istore_1
    //   972: aload_2
    //   973: invokevirtual 53	android/os/Parcel:readInt	()I
    //   976: ifeq +73 -> 1049
    //   979: iconst_1
    //   980: istore 5
    //   982: getstatic 105	android/content/res/Configuration:CREATOR	Landroid/os/Parcelable$Creator;
    //   985: aload_2
    //   986: invokeinterface 99 2 0
    //   991: checkcast 104	android/content/res/Configuration
    //   994: astore 13
    //   996: aconst_null
    //   997: astore_3
    //   998: aload_2
    //   999: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1002: ifeq +16 -> 1018
    //   1005: getstatic 105	android/content/res/Configuration:CREATOR	Landroid/os/Parcelable$Creator;
    //   1008: aload_2
    //   1009: invokeinterface 99 2 0
    //   1014: checkcast 104	android/content/res/Configuration
    //   1017: astore_3
    //   1018: aload_2
    //   1019: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1022: iconst_1
    //   1023: if_icmpne +32 -> 1055
    //   1026: iconst_1
    //   1027: istore 6
    //   1029: aload_0
    //   1030: aload 10
    //   1032: aload 11
    //   1034: aload 12
    //   1036: iload_1
    //   1037: iload 5
    //   1039: aload 13
    //   1041: aload_3
    //   1042: iload 6
    //   1044: invokevirtual 135	android/app/ApplicationThreadNative:scheduleRelaunchActivity	(Landroid/os/IBinder;Ljava/util/List;Ljava/util/List;IZLandroid/content/res/Configuration;Landroid/content/res/Configuration;Z)V
    //   1047: iconst_1
    //   1048: ireturn
    //   1049: iconst_0
    //   1050: istore 5
    //   1052: goto -70 -> 982
    //   1055: iconst_0
    //   1056: istore 6
    //   1058: goto -29 -> 1029
    //   1061: aload_2
    //   1062: ldc 12
    //   1064: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1067: aload_2
    //   1068: getstatic 124	com/android/internal/content/ReferrerIntent:CREATOR	Landroid/os/Parcelable$Creator;
    //   1071: invokevirtual 86	android/os/Parcel:createTypedArrayList	(Landroid/os/Parcelable$Creator;)Ljava/util/ArrayList;
    //   1074: astore_3
    //   1075: aload_2
    //   1076: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   1079: astore 10
    //   1081: aload_2
    //   1082: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1085: iconst_1
    //   1086: if_icmpne +17 -> 1103
    //   1089: iconst_1
    //   1090: istore 5
    //   1092: aload_0
    //   1093: aload_3
    //   1094: aload 10
    //   1096: iload 5
    //   1098: invokevirtual 139	android/app/ApplicationThreadNative:scheduleNewIntent	(Ljava/util/List;Landroid/os/IBinder;Z)V
    //   1101: iconst_1
    //   1102: ireturn
    //   1103: iconst_0
    //   1104: istore 5
    //   1106: goto -14 -> 1092
    //   1109: aload_2
    //   1110: ldc 12
    //   1112: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1115: aload_2
    //   1116: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   1119: astore_3
    //   1120: aload_2
    //   1121: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1124: ifeq +19 -> 1143
    //   1127: iconst_1
    //   1128: istore 5
    //   1130: aload_0
    //   1131: aload_3
    //   1132: iload 5
    //   1134: aload_2
    //   1135: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1138: invokevirtual 142	android/app/ApplicationThreadNative:scheduleDestroyActivity	(Landroid/os/IBinder;ZI)V
    //   1141: iconst_1
    //   1142: ireturn
    //   1143: iconst_0
    //   1144: istore 5
    //   1146: goto -16 -> 1130
    //   1149: aload_2
    //   1150: ldc 12
    //   1152: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1155: getstatic 93	android/content/Intent:CREATOR	Landroid/os/Parcelable$Creator;
    //   1158: aload_2
    //   1159: invokeinterface 99 2 0
    //   1164: checkcast 92	android/content/Intent
    //   1167: astore_3
    //   1168: getstatic 102	android/content/pm/ActivityInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   1171: aload_2
    //   1172: invokeinterface 99 2 0
    //   1177: checkcast 101	android/content/pm/ActivityInfo
    //   1180: astore 10
    //   1182: getstatic 108	android/content/res/CompatibilityInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   1185: aload_2
    //   1186: invokeinterface 99 2 0
    //   1191: checkcast 107	android/content/res/CompatibilityInfo
    //   1194: astore 11
    //   1196: aload_2
    //   1197: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1200: istore_1
    //   1201: aload_2
    //   1202: invokevirtual 112	android/os/Parcel:readString	()Ljava/lang/String;
    //   1205: astore 12
    //   1207: aload_2
    //   1208: invokevirtual 72	android/os/Parcel:readBundle	()Landroid/os/Bundle;
    //   1211: astore 13
    //   1213: aload_2
    //   1214: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1217: ifeq +36 -> 1253
    //   1220: iconst_1
    //   1221: istore 5
    //   1223: aload_0
    //   1224: aload_3
    //   1225: aload 10
    //   1227: aload 11
    //   1229: iload_1
    //   1230: aload 12
    //   1232: aload 13
    //   1234: iload 5
    //   1236: aload_2
    //   1237: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1240: aload_2
    //   1241: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1244: aload_2
    //   1245: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1248: invokevirtual 146	android/app/ApplicationThreadNative:scheduleReceiver	(Landroid/content/Intent;Landroid/content/pm/ActivityInfo;Landroid/content/res/CompatibilityInfo;ILjava/lang/String;Landroid/os/Bundle;ZIII)V
    //   1251: iconst_1
    //   1252: ireturn
    //   1253: iconst_0
    //   1254: istore 5
    //   1256: goto -33 -> 1223
    //   1259: aload_2
    //   1260: ldc 12
    //   1262: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1265: aload_0
    //   1266: aload_2
    //   1267: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   1270: getstatic 149	android/content/pm/ServiceInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   1273: aload_2
    //   1274: invokeinterface 99 2 0
    //   1279: checkcast 148	android/content/pm/ServiceInfo
    //   1282: getstatic 108	android/content/res/CompatibilityInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   1285: aload_2
    //   1286: invokeinterface 99 2 0
    //   1291: checkcast 107	android/content/res/CompatibilityInfo
    //   1294: aload_2
    //   1295: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1298: invokevirtual 153	android/app/ApplicationThreadNative:scheduleCreateService	(Landroid/os/IBinder;Landroid/content/pm/ServiceInfo;Landroid/content/res/CompatibilityInfo;I)V
    //   1301: iconst_1
    //   1302: ireturn
    //   1303: aload_2
    //   1304: ldc 12
    //   1306: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1309: aload_2
    //   1310: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   1313: astore_3
    //   1314: getstatic 93	android/content/Intent:CREATOR	Landroid/os/Parcelable$Creator;
    //   1317: aload_2
    //   1318: invokeinterface 99 2 0
    //   1323: checkcast 92	android/content/Intent
    //   1326: astore 10
    //   1328: aload_2
    //   1329: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1332: ifeq +21 -> 1353
    //   1335: iconst_1
    //   1336: istore 5
    //   1338: aload_0
    //   1339: aload_3
    //   1340: aload 10
    //   1342: iload 5
    //   1344: aload_2
    //   1345: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1348: invokevirtual 157	android/app/ApplicationThreadNative:scheduleBindService	(Landroid/os/IBinder;Landroid/content/Intent;ZI)V
    //   1351: iconst_1
    //   1352: ireturn
    //   1353: iconst_0
    //   1354: istore 5
    //   1356: goto -18 -> 1338
    //   1359: aload_2
    //   1360: ldc 12
    //   1362: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1365: aload_0
    //   1366: aload_2
    //   1367: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   1370: getstatic 93	android/content/Intent:CREATOR	Landroid/os/Parcelable$Creator;
    //   1373: aload_2
    //   1374: invokeinterface 99 2 0
    //   1379: checkcast 92	android/content/Intent
    //   1382: invokevirtual 161	android/app/ApplicationThreadNative:scheduleUnbindService	(Landroid/os/IBinder;Landroid/content/Intent;)V
    //   1385: iconst_1
    //   1386: ireturn
    //   1387: aload_2
    //   1388: ldc 12
    //   1390: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1393: aload_2
    //   1394: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   1397: astore_3
    //   1398: aload_2
    //   1399: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1402: ifeq +50 -> 1452
    //   1405: iconst_1
    //   1406: istore 5
    //   1408: aload_2
    //   1409: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1412: istore_1
    //   1413: aload_2
    //   1414: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1417: istore 4
    //   1419: aload_2
    //   1420: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1423: ifeq +35 -> 1458
    //   1426: getstatic 93	android/content/Intent:CREATOR	Landroid/os/Parcelable$Creator;
    //   1429: aload_2
    //   1430: invokeinterface 99 2 0
    //   1435: checkcast 92	android/content/Intent
    //   1438: astore_2
    //   1439: aload_0
    //   1440: aload_3
    //   1441: iload 5
    //   1443: iload_1
    //   1444: iload 4
    //   1446: aload_2
    //   1447: invokevirtual 165	android/app/ApplicationThreadNative:scheduleServiceArgs	(Landroid/os/IBinder;ZIILandroid/content/Intent;)V
    //   1450: iconst_1
    //   1451: ireturn
    //   1452: iconst_0
    //   1453: istore 5
    //   1455: goto -47 -> 1408
    //   1458: aconst_null
    //   1459: astore_2
    //   1460: goto -21 -> 1439
    //   1463: aload_2
    //   1464: ldc 12
    //   1466: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1469: aload_0
    //   1470: aload_2
    //   1471: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   1474: invokevirtual 168	android/app/ApplicationThreadNative:scheduleStopService	(Landroid/os/IBinder;)V
    //   1477: iconst_1
    //   1478: ireturn
    //   1479: aload_2
    //   1480: ldc 12
    //   1482: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1485: aload_2
    //   1486: invokevirtual 112	android/os/Parcel:readString	()Ljava/lang/String;
    //   1489: astore 11
    //   1491: getstatic 171	android/content/pm/ApplicationInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   1494: aload_2
    //   1495: invokeinterface 99 2 0
    //   1500: checkcast 170	android/content/pm/ApplicationInfo
    //   1503: astore 12
    //   1505: aload_2
    //   1506: getstatic 174	android/content/pm/ProviderInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   1509: invokevirtual 86	android/os/Parcel:createTypedArrayList	(Landroid/os/Parcelable$Creator;)Ljava/util/ArrayList;
    //   1512: astore 13
    //   1514: aload_2
    //   1515: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1518: ifeq +165 -> 1683
    //   1521: new 176	android/content/ComponentName
    //   1524: dup
    //   1525: aload_2
    //   1526: invokespecial 179	android/content/ComponentName:<init>	(Landroid/os/Parcel;)V
    //   1529: astore_3
    //   1530: aload_2
    //   1531: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1534: ifeq +154 -> 1688
    //   1537: getstatic 127	android/app/ProfilerInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   1540: aload_2
    //   1541: invokeinterface 99 2 0
    //   1546: checkcast 126	android/app/ProfilerInfo
    //   1549: astore 10
    //   1551: aload_2
    //   1552: invokevirtual 72	android/os/Parcel:readBundle	()Landroid/os/Bundle;
    //   1555: astore 14
    //   1557: aload_2
    //   1558: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   1561: invokestatic 184	android/app/IInstrumentationWatcher$Stub:asInterface	(Landroid/os/IBinder;)Landroid/app/IInstrumentationWatcher;
    //   1564: astore 15
    //   1566: aload_2
    //   1567: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   1570: invokestatic 189	android/app/IUiAutomationConnection$Stub:asInterface	(Landroid/os/IBinder;)Landroid/app/IUiAutomationConnection;
    //   1573: astore 16
    //   1575: aload_2
    //   1576: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1579: istore_1
    //   1580: aload_2
    //   1581: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1584: ifeq +110 -> 1694
    //   1587: iconst_1
    //   1588: istore 5
    //   1590: aload_2
    //   1591: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1594: ifeq +106 -> 1700
    //   1597: iconst_1
    //   1598: istore 6
    //   1600: aload_2
    //   1601: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1604: ifeq +102 -> 1706
    //   1607: iconst_1
    //   1608: istore 7
    //   1610: aload_2
    //   1611: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1614: ifeq +98 -> 1712
    //   1617: iconst_1
    //   1618: istore 8
    //   1620: aload_0
    //   1621: aload 11
    //   1623: aload 12
    //   1625: aload 13
    //   1627: aload_3
    //   1628: aload 10
    //   1630: aload 14
    //   1632: aload 15
    //   1634: aload 16
    //   1636: iload_1
    //   1637: iload 5
    //   1639: iload 6
    //   1641: iload 7
    //   1643: iload 8
    //   1645: getstatic 105	android/content/res/Configuration:CREATOR	Landroid/os/Parcelable$Creator;
    //   1648: aload_2
    //   1649: invokeinterface 99 2 0
    //   1654: checkcast 104	android/content/res/Configuration
    //   1657: getstatic 108	android/content/res/CompatibilityInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   1660: aload_2
    //   1661: invokeinterface 99 2 0
    //   1666: checkcast 107	android/content/res/CompatibilityInfo
    //   1669: aload_2
    //   1670: aconst_null
    //   1671: invokevirtual 193	android/os/Parcel:readHashMap	(Ljava/lang/ClassLoader;)Ljava/util/HashMap;
    //   1674: aload_2
    //   1675: invokevirtual 72	android/os/Parcel:readBundle	()Landroid/os/Bundle;
    //   1678: invokevirtual 197	android/app/ApplicationThreadNative:bindApplication	(Ljava/lang/String;Landroid/content/pm/ApplicationInfo;Ljava/util/List;Landroid/content/ComponentName;Landroid/app/ProfilerInfo;Landroid/os/Bundle;Landroid/app/IInstrumentationWatcher;Landroid/app/IUiAutomationConnection;IZZZZLandroid/content/res/Configuration;Landroid/content/res/CompatibilityInfo;Ljava/util/Map;Landroid/os/Bundle;)V
    //   1681: iconst_1
    //   1682: ireturn
    //   1683: aconst_null
    //   1684: astore_3
    //   1685: goto -155 -> 1530
    //   1688: aconst_null
    //   1689: astore 10
    //   1691: goto -140 -> 1551
    //   1694: iconst_0
    //   1695: istore 5
    //   1697: goto -107 -> 1590
    //   1700: iconst_0
    //   1701: istore 6
    //   1703: goto -103 -> 1600
    //   1706: iconst_0
    //   1707: istore 7
    //   1709: goto -99 -> 1610
    //   1712: iconst_0
    //   1713: istore 8
    //   1715: goto -95 -> 1620
    //   1718: aload_2
    //   1719: ldc 12
    //   1721: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1724: aload_0
    //   1725: invokevirtual 200	android/app/ApplicationThreadNative:scheduleExit	()V
    //   1728: iconst_1
    //   1729: ireturn
    //   1730: aload_2
    //   1731: ldc 12
    //   1733: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1736: aload_0
    //   1737: invokevirtual 203	android/app/ApplicationThreadNative:scheduleSuicide	()V
    //   1740: iconst_1
    //   1741: ireturn
    //   1742: aload_2
    //   1743: ldc 12
    //   1745: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1748: aload_0
    //   1749: getstatic 105	android/content/res/Configuration:CREATOR	Landroid/os/Parcelable$Creator;
    //   1752: aload_2
    //   1753: invokeinterface 99 2 0
    //   1758: checkcast 104	android/content/res/Configuration
    //   1761: invokevirtual 207	android/app/ApplicationThreadNative:scheduleConfigurationChanged	(Landroid/content/res/Configuration;)V
    //   1764: iconst_1
    //   1765: ireturn
    //   1766: aload_2
    //   1767: ldc 12
    //   1769: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1772: aload_0
    //   1773: invokevirtual 210	android/app/ApplicationThreadNative:updateTimeZone	()V
    //   1776: iconst_1
    //   1777: ireturn
    //   1778: aload_2
    //   1779: ldc 12
    //   1781: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1784: aload_0
    //   1785: invokevirtual 213	android/app/ApplicationThreadNative:clearDnsCache	()V
    //   1788: iconst_1
    //   1789: ireturn
    //   1790: aload_2
    //   1791: ldc 12
    //   1793: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1796: aload_0
    //   1797: aload_2
    //   1798: invokevirtual 112	android/os/Parcel:readString	()Ljava/lang/String;
    //   1801: aload_2
    //   1802: invokevirtual 112	android/os/Parcel:readString	()Ljava/lang/String;
    //   1805: aload_2
    //   1806: invokevirtual 112	android/os/Parcel:readString	()Ljava/lang/String;
    //   1809: getstatic 216	android/net/Uri:CREATOR	Landroid/os/Parcelable$Creator;
    //   1812: aload_2
    //   1813: invokeinterface 99 2 0
    //   1818: checkcast 215	android/net/Uri
    //   1821: invokevirtual 220	android/app/ApplicationThreadNative:setHttpProxy	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/net/Uri;)V
    //   1824: iconst_1
    //   1825: ireturn
    //   1826: aload_2
    //   1827: ldc 12
    //   1829: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1832: aload_0
    //   1833: invokevirtual 223	android/app/ApplicationThreadNative:processInBackground	()V
    //   1836: iconst_1
    //   1837: ireturn
    //   1838: aload_2
    //   1839: ldc 12
    //   1841: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1844: aload_2
    //   1845: invokevirtual 227	android/os/Parcel:readFileDescriptor	()Landroid/os/ParcelFileDescriptor;
    //   1848: astore_3
    //   1849: aload_2
    //   1850: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   1853: astore 10
    //   1855: aload_2
    //   1856: invokevirtual 231	android/os/Parcel:readStringArray	()[Ljava/lang/String;
    //   1859: astore_2
    //   1860: aload_3
    //   1861: ifnull +18 -> 1879
    //   1864: aload_0
    //   1865: aload_3
    //   1866: invokevirtual 237	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   1869: aload 10
    //   1871: aload_2
    //   1872: invokevirtual 241	android/app/ApplicationThreadNative:dumpService	(Ljava/io/FileDescriptor;Landroid/os/IBinder;[Ljava/lang/String;)V
    //   1875: aload_3
    //   1876: invokevirtual 244	android/os/ParcelFileDescriptor:close	()V
    //   1879: iconst_1
    //   1880: ireturn
    //   1881: astore_2
    //   1882: goto -3 -> 1879
    //   1885: aload_2
    //   1886: ldc 12
    //   1888: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1891: aload_2
    //   1892: invokevirtual 227	android/os/Parcel:readFileDescriptor	()Landroid/os/ParcelFileDescriptor;
    //   1895: astore_3
    //   1896: aload_2
    //   1897: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   1900: astore 10
    //   1902: aload_2
    //   1903: invokevirtual 231	android/os/Parcel:readStringArray	()[Ljava/lang/String;
    //   1906: astore_2
    //   1907: aload_3
    //   1908: ifnull +18 -> 1926
    //   1911: aload_0
    //   1912: aload_3
    //   1913: invokevirtual 237	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   1916: aload 10
    //   1918: aload_2
    //   1919: invokevirtual 247	android/app/ApplicationThreadNative:dumpProvider	(Ljava/io/FileDescriptor;Landroid/os/IBinder;[Ljava/lang/String;)V
    //   1922: aload_3
    //   1923: invokevirtual 244	android/os/ParcelFileDescriptor:close	()V
    //   1926: iconst_1
    //   1927: ireturn
    //   1928: astore_2
    //   1929: goto -3 -> 1926
    //   1932: aload_2
    //   1933: ldc 12
    //   1935: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   1938: aload_2
    //   1939: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   1942: invokestatic 252	android/content/IIntentReceiver$Stub:asInterface	(Landroid/os/IBinder;)Landroid/content/IIntentReceiver;
    //   1945: astore_3
    //   1946: getstatic 93	android/content/Intent:CREATOR	Landroid/os/Parcelable$Creator;
    //   1949: aload_2
    //   1950: invokeinterface 99 2 0
    //   1955: checkcast 92	android/content/Intent
    //   1958: astore 10
    //   1960: aload_2
    //   1961: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1964: istore_1
    //   1965: aload_2
    //   1966: invokevirtual 112	android/os/Parcel:readString	()Ljava/lang/String;
    //   1969: astore 11
    //   1971: aload_2
    //   1972: invokevirtual 72	android/os/Parcel:readBundle	()Landroid/os/Bundle;
    //   1975: astore 12
    //   1977: aload_2
    //   1978: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1981: ifeq +42 -> 2023
    //   1984: iconst_1
    //   1985: istore 5
    //   1987: aload_2
    //   1988: invokevirtual 53	android/os/Parcel:readInt	()I
    //   1991: ifeq +38 -> 2029
    //   1994: iconst_1
    //   1995: istore 6
    //   1997: aload_0
    //   1998: aload_3
    //   1999: aload 10
    //   2001: iload_1
    //   2002: aload 11
    //   2004: aload 12
    //   2006: iload 5
    //   2008: iload 6
    //   2010: aload_2
    //   2011: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2014: aload_2
    //   2015: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2018: invokevirtual 256	android/app/ApplicationThreadNative:scheduleRegisteredReceiver	(Landroid/content/IIntentReceiver;Landroid/content/Intent;ILjava/lang/String;Landroid/os/Bundle;ZZII)V
    //   2021: iconst_1
    //   2022: ireturn
    //   2023: iconst_0
    //   2024: istore 5
    //   2026: goto -39 -> 1987
    //   2029: iconst_0
    //   2030: istore 6
    //   2032: goto -35 -> 1997
    //   2035: aload_2
    //   2036: ldc 12
    //   2038: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2041: aload_0
    //   2042: invokevirtual 259	android/app/ApplicationThreadNative:scheduleLowMemory	()V
    //   2045: iconst_1
    //   2046: ireturn
    //   2047: aload_2
    //   2048: ldc 12
    //   2050: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2053: aload_2
    //   2054: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   2057: astore 10
    //   2059: aconst_null
    //   2060: astore_3
    //   2061: aload_2
    //   2062: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2065: ifeq +16 -> 2081
    //   2068: getstatic 105	android/content/res/Configuration:CREATOR	Landroid/os/Parcelable$Creator;
    //   2071: aload_2
    //   2072: invokeinterface 99 2 0
    //   2077: checkcast 104	android/content/res/Configuration
    //   2080: astore_3
    //   2081: aload_2
    //   2082: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2085: iconst_1
    //   2086: if_icmpne +17 -> 2103
    //   2089: iconst_1
    //   2090: istore 5
    //   2092: aload_0
    //   2093: aload 10
    //   2095: aload_3
    //   2096: iload 5
    //   2098: invokevirtual 263	android/app/ApplicationThreadNative:scheduleActivityConfigurationChanged	(Landroid/os/IBinder;Landroid/content/res/Configuration;Z)V
    //   2101: iconst_1
    //   2102: ireturn
    //   2103: iconst_0
    //   2104: istore 5
    //   2106: goto -14 -> 2092
    //   2109: aload_2
    //   2110: ldc 12
    //   2112: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2115: aload_0
    //   2116: aload_2
    //   2117: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   2120: aload_2
    //   2121: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   2124: invokestatic 117	com/android/internal/app/IVoiceInteractor$Stub:asInterface	(Landroid/os/IBinder;)Lcom/android/internal/app/IVoiceInteractor;
    //   2127: invokevirtual 267	android/app/ApplicationThreadNative:scheduleLocalVoiceInteractionStarted	(Landroid/os/IBinder;Lcom/android/internal/app/IVoiceInteractor;)V
    //   2130: iconst_1
    //   2131: ireturn
    //   2132: aload_2
    //   2133: ldc 12
    //   2135: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2138: aload_2
    //   2139: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2142: ifeq +41 -> 2183
    //   2145: iconst_1
    //   2146: istore 5
    //   2148: aload_2
    //   2149: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2152: istore_1
    //   2153: aload_2
    //   2154: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2157: ifeq +32 -> 2189
    //   2160: getstatic 127	android/app/ProfilerInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   2163: aload_2
    //   2164: invokeinterface 99 2 0
    //   2169: checkcast 126	android/app/ProfilerInfo
    //   2172: astore_2
    //   2173: aload_0
    //   2174: iload 5
    //   2176: aload_2
    //   2177: iload_1
    //   2178: invokevirtual 271	android/app/ApplicationThreadNative:profilerControl	(ZLandroid/app/ProfilerInfo;I)V
    //   2181: iconst_1
    //   2182: ireturn
    //   2183: iconst_0
    //   2184: istore 5
    //   2186: goto -38 -> 2148
    //   2189: aconst_null
    //   2190: astore_2
    //   2191: goto -18 -> 2173
    //   2194: aload_2
    //   2195: ldc 12
    //   2197: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2200: aload_0
    //   2201: aload_2
    //   2202: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2205: invokevirtual 275	android/app/ApplicationThreadNative:setSchedulingGroup	(I)V
    //   2208: iconst_1
    //   2209: ireturn
    //   2210: aload_2
    //   2211: ldc 12
    //   2213: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2216: aload_0
    //   2217: getstatic 171	android/content/pm/ApplicationInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   2220: aload_2
    //   2221: invokeinterface 99 2 0
    //   2226: checkcast 170	android/content/pm/ApplicationInfo
    //   2229: getstatic 108	android/content/res/CompatibilityInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   2232: aload_2
    //   2233: invokeinterface 99 2 0
    //   2238: checkcast 107	android/content/res/CompatibilityInfo
    //   2241: aload_2
    //   2242: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2245: invokevirtual 279	android/app/ApplicationThreadNative:scheduleCreateBackupAgent	(Landroid/content/pm/ApplicationInfo;Landroid/content/res/CompatibilityInfo;I)V
    //   2248: iconst_1
    //   2249: ireturn
    //   2250: aload_2
    //   2251: ldc 12
    //   2253: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2256: aload_0
    //   2257: getstatic 171	android/content/pm/ApplicationInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   2260: aload_2
    //   2261: invokeinterface 99 2 0
    //   2266: checkcast 170	android/content/pm/ApplicationInfo
    //   2269: getstatic 108	android/content/res/CompatibilityInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   2272: aload_2
    //   2273: invokeinterface 99 2 0
    //   2278: checkcast 107	android/content/res/CompatibilityInfo
    //   2281: invokevirtual 283	android/app/ApplicationThreadNative:scheduleDestroyBackupAgent	(Landroid/content/pm/ApplicationInfo;Landroid/content/res/CompatibilityInfo;)V
    //   2284: iconst_1
    //   2285: ireturn
    //   2286: aload_2
    //   2287: ldc 12
    //   2289: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2292: aload_0
    //   2293: aload_2
    //   2294: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2297: aload_2
    //   2298: invokevirtual 231	android/os/Parcel:readStringArray	()[Ljava/lang/String;
    //   2301: invokevirtual 287	android/app/ApplicationThreadNative:dispatchPackageBroadcast	(I[Ljava/lang/String;)V
    //   2304: iconst_1
    //   2305: ireturn
    //   2306: aload_2
    //   2307: ldc 12
    //   2309: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2312: aload_0
    //   2313: aload_2
    //   2314: invokevirtual 112	android/os/Parcel:readString	()Ljava/lang/String;
    //   2317: invokevirtual 290	android/app/ApplicationThreadNative:scheduleCrash	(Ljava/lang/String;)V
    //   2320: iconst_1
    //   2321: ireturn
    //   2322: aload_2
    //   2323: ldc 12
    //   2325: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2328: aload_2
    //   2329: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2332: ifeq +41 -> 2373
    //   2335: iconst_1
    //   2336: istore 5
    //   2338: aload_2
    //   2339: invokevirtual 112	android/os/Parcel:readString	()Ljava/lang/String;
    //   2342: astore_3
    //   2343: aload_2
    //   2344: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2347: ifeq +32 -> 2379
    //   2350: getstatic 291	android/os/ParcelFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
    //   2353: aload_2
    //   2354: invokeinterface 99 2 0
    //   2359: checkcast 233	android/os/ParcelFileDescriptor
    //   2362: astore_2
    //   2363: aload_0
    //   2364: iload 5
    //   2366: aload_3
    //   2367: aload_2
    //   2368: invokevirtual 295	android/app/ApplicationThreadNative:dumpHeap	(ZLjava/lang/String;Landroid/os/ParcelFileDescriptor;)V
    //   2371: iconst_1
    //   2372: ireturn
    //   2373: iconst_0
    //   2374: istore 5
    //   2376: goto -38 -> 2338
    //   2379: aconst_null
    //   2380: astore_2
    //   2381: goto -18 -> 2363
    //   2384: aload_2
    //   2385: ldc 12
    //   2387: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2390: aload_2
    //   2391: invokevirtual 227	android/os/Parcel:readFileDescriptor	()Landroid/os/ParcelFileDescriptor;
    //   2394: astore_3
    //   2395: aload_2
    //   2396: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   2399: astore 10
    //   2401: aload_2
    //   2402: invokevirtual 112	android/os/Parcel:readString	()Ljava/lang/String;
    //   2405: astore 11
    //   2407: aload_2
    //   2408: invokevirtual 231	android/os/Parcel:readStringArray	()[Ljava/lang/String;
    //   2411: astore_2
    //   2412: aload_3
    //   2413: ifnull +20 -> 2433
    //   2416: aload_0
    //   2417: aload_3
    //   2418: invokevirtual 237	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   2421: aload 10
    //   2423: aload 11
    //   2425: aload_2
    //   2426: invokevirtual 299	android/app/ApplicationThreadNative:dumpActivity	(Ljava/io/FileDescriptor;Landroid/os/IBinder;Ljava/lang/String;[Ljava/lang/String;)V
    //   2429: aload_3
    //   2430: invokevirtual 244	android/os/ParcelFileDescriptor:close	()V
    //   2433: iconst_1
    //   2434: ireturn
    //   2435: astore_2
    //   2436: goto -3 -> 2433
    //   2439: aload_2
    //   2440: ldc 12
    //   2442: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2445: aload_0
    //   2446: aload_2
    //   2447: invokevirtual 72	android/os/Parcel:readBundle	()Landroid/os/Bundle;
    //   2450: invokevirtual 303	android/app/ApplicationThreadNative:setCoreSettings	(Landroid/os/Bundle;)V
    //   2453: iconst_1
    //   2454: ireturn
    //   2455: aload_2
    //   2456: ldc 12
    //   2458: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2461: aload_0
    //   2462: aload_2
    //   2463: invokevirtual 112	android/os/Parcel:readString	()Ljava/lang/String;
    //   2466: getstatic 108	android/content/res/CompatibilityInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   2469: aload_2
    //   2470: invokeinterface 99 2 0
    //   2475: checkcast 107	android/content/res/CompatibilityInfo
    //   2478: invokevirtual 307	android/app/ApplicationThreadNative:updatePackageCompatibilityInfo	(Ljava/lang/String;Landroid/content/res/CompatibilityInfo;)V
    //   2481: iconst_1
    //   2482: ireturn
    //   2483: aload_2
    //   2484: ldc 12
    //   2486: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2489: aload_0
    //   2490: aload_2
    //   2491: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2494: invokevirtual 310	android/app/ApplicationThreadNative:scheduleTrimMemory	(I)V
    //   2497: iconst_1
    //   2498: ireturn
    //   2499: aload_2
    //   2500: ldc 12
    //   2502: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2505: aload_2
    //   2506: invokevirtual 227	android/os/Parcel:readFileDescriptor	()Landroid/os/ParcelFileDescriptor;
    //   2509: astore 10
    //   2511: getstatic 313	android/os/Debug$MemoryInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   2514: aload_2
    //   2515: invokeinterface 99 2 0
    //   2520: checkcast 312	android/os/Debug$MemoryInfo
    //   2523: astore 11
    //   2525: aload_2
    //   2526: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2529: ifeq +89 -> 2618
    //   2532: iconst_1
    //   2533: istore 5
    //   2535: aload_2
    //   2536: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2539: ifeq +85 -> 2624
    //   2542: iconst_1
    //   2543: istore 6
    //   2545: aload_2
    //   2546: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2549: ifeq +81 -> 2630
    //   2552: iconst_1
    //   2553: istore 7
    //   2555: aload_2
    //   2556: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2559: ifeq +77 -> 2636
    //   2562: iconst_1
    //   2563: istore 8
    //   2565: aload_2
    //   2566: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2569: ifeq +73 -> 2642
    //   2572: iconst_1
    //   2573: istore 9
    //   2575: aload_2
    //   2576: invokevirtual 231	android/os/Parcel:readStringArray	()[Ljava/lang/String;
    //   2579: astore_2
    //   2580: aload 10
    //   2582: ifnull +30 -> 2612
    //   2585: aload_0
    //   2586: aload 10
    //   2588: invokevirtual 237	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   2591: aload 11
    //   2593: iload 5
    //   2595: iload 6
    //   2597: iload 7
    //   2599: iload 8
    //   2601: iload 9
    //   2603: aload_2
    //   2604: invokevirtual 317	android/app/ApplicationThreadNative:dumpMemInfo	(Ljava/io/FileDescriptor;Landroid/os/Debug$MemoryInfo;ZZZZZ[Ljava/lang/String;)V
    //   2607: aload 10
    //   2609: invokevirtual 244	android/os/ParcelFileDescriptor:close	()V
    //   2612: aload_3
    //   2613: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   2616: iconst_1
    //   2617: ireturn
    //   2618: iconst_0
    //   2619: istore 5
    //   2621: goto -86 -> 2535
    //   2624: iconst_0
    //   2625: istore 6
    //   2627: goto -82 -> 2545
    //   2630: iconst_0
    //   2631: istore 7
    //   2633: goto -78 -> 2555
    //   2636: iconst_0
    //   2637: istore 8
    //   2639: goto -74 -> 2565
    //   2642: iconst_0
    //   2643: istore 9
    //   2645: goto -70 -> 2575
    //   2648: astore_2
    //   2649: goto -37 -> 2612
    //   2652: astore_2
    //   2653: aload 10
    //   2655: invokevirtual 244	android/os/ParcelFileDescriptor:close	()V
    //   2658: aload_2
    //   2659: athrow
    //   2660: astore_3
    //   2661: goto -3 -> 2658
    //   2664: aload_2
    //   2665: ldc 12
    //   2667: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2670: aload_2
    //   2671: invokevirtual 227	android/os/Parcel:readFileDescriptor	()Landroid/os/ParcelFileDescriptor;
    //   2674: astore 10
    //   2676: aload_2
    //   2677: invokevirtual 231	android/os/Parcel:readStringArray	()[Ljava/lang/String;
    //   2680: astore_2
    //   2681: aload 10
    //   2683: ifnull +18 -> 2701
    //   2686: aload_0
    //   2687: aload 10
    //   2689: invokevirtual 237	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   2692: aload_2
    //   2693: invokevirtual 324	android/app/ApplicationThreadNative:dumpGfxInfo	(Ljava/io/FileDescriptor;[Ljava/lang/String;)V
    //   2696: aload 10
    //   2698: invokevirtual 244	android/os/ParcelFileDescriptor:close	()V
    //   2701: aload_3
    //   2702: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   2705: iconst_1
    //   2706: ireturn
    //   2707: astore_2
    //   2708: goto -7 -> 2701
    //   2711: astore_2
    //   2712: aload 10
    //   2714: invokevirtual 244	android/os/ParcelFileDescriptor:close	()V
    //   2717: aload_2
    //   2718: athrow
    //   2719: astore_3
    //   2720: goto -3 -> 2717
    //   2723: aload_2
    //   2724: ldc 12
    //   2726: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2729: aload_2
    //   2730: invokevirtual 227	android/os/Parcel:readFileDescriptor	()Landroid/os/ParcelFileDescriptor;
    //   2733: astore 10
    //   2735: aload_2
    //   2736: invokevirtual 231	android/os/Parcel:readStringArray	()[Ljava/lang/String;
    //   2739: astore_2
    //   2740: aload 10
    //   2742: ifnull +18 -> 2760
    //   2745: aload_0
    //   2746: aload 10
    //   2748: invokevirtual 237	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   2751: aload_2
    //   2752: invokevirtual 327	android/app/ApplicationThreadNative:dumpDbInfo	(Ljava/io/FileDescriptor;[Ljava/lang/String;)V
    //   2755: aload 10
    //   2757: invokevirtual 244	android/os/ParcelFileDescriptor:close	()V
    //   2760: aload_3
    //   2761: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   2764: iconst_1
    //   2765: ireturn
    //   2766: astore_2
    //   2767: goto -7 -> 2760
    //   2770: astore_2
    //   2771: aload 10
    //   2773: invokevirtual 244	android/os/ParcelFileDescriptor:close	()V
    //   2776: aload_2
    //   2777: athrow
    //   2778: astore_3
    //   2779: goto -3 -> 2776
    //   2782: aload_2
    //   2783: ldc 12
    //   2785: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2788: aload_0
    //   2789: aload_2
    //   2790: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   2793: invokevirtual 330	android/app/ApplicationThreadNative:unstableProviderDied	(Landroid/os/IBinder;)V
    //   2796: aload_3
    //   2797: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   2800: iconst_1
    //   2801: ireturn
    //   2802: aload_2
    //   2803: ldc 12
    //   2805: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2808: aload_0
    //   2809: aload_2
    //   2810: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   2813: aload_2
    //   2814: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   2817: aload_2
    //   2818: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2821: aload_2
    //   2822: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2825: invokevirtual 334	android/app/ApplicationThreadNative:requestAssistContextExtras	(Landroid/os/IBinder;Landroid/os/IBinder;II)V
    //   2828: aload_3
    //   2829: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   2832: iconst_1
    //   2833: ireturn
    //   2834: aload_2
    //   2835: ldc 12
    //   2837: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2840: aload_2
    //   2841: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   2844: astore 10
    //   2846: aload_2
    //   2847: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2850: iconst_1
    //   2851: if_icmpne +20 -> 2871
    //   2854: iconst_1
    //   2855: istore 5
    //   2857: aload_0
    //   2858: aload 10
    //   2860: iload 5
    //   2862: invokevirtual 337	android/app/ApplicationThreadNative:scheduleTranslucentConversionComplete	(Landroid/os/IBinder;Z)V
    //   2865: aload_3
    //   2866: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   2869: iconst_1
    //   2870: ireturn
    //   2871: iconst_0
    //   2872: istore 5
    //   2874: goto -17 -> 2857
    //   2877: aload_2
    //   2878: ldc 12
    //   2880: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2883: aload_0
    //   2884: aload_2
    //   2885: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   2888: new 339	android/app/ActivityOptions
    //   2891: dup
    //   2892: aload_2
    //   2893: invokevirtual 72	android/os/Parcel:readBundle	()Landroid/os/Bundle;
    //   2896: invokespecial 341	android/app/ActivityOptions:<init>	(Landroid/os/Bundle;)V
    //   2899: invokevirtual 345	android/app/ApplicationThreadNative:scheduleOnNewActivityOptions	(Landroid/os/IBinder;Landroid/app/ActivityOptions;)V
    //   2902: aload_3
    //   2903: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   2906: iconst_1
    //   2907: ireturn
    //   2908: aload_2
    //   2909: ldc 12
    //   2911: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2914: aload_0
    //   2915: aload_2
    //   2916: invokevirtual 53	android/os/Parcel:readInt	()I
    //   2919: invokevirtual 348	android/app/ApplicationThreadNative:setProcessState	(I)V
    //   2922: aload_3
    //   2923: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   2926: iconst_1
    //   2927: ireturn
    //   2928: aload_2
    //   2929: ldc 12
    //   2931: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2934: aload_0
    //   2935: getstatic 174	android/content/pm/ProviderInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   2938: aload_2
    //   2939: invokeinterface 99 2 0
    //   2944: checkcast 173	android/content/pm/ProviderInfo
    //   2947: invokevirtual 352	android/app/ApplicationThreadNative:scheduleInstallProvider	(Landroid/content/pm/ProviderInfo;)V
    //   2950: aload_3
    //   2951: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   2954: iconst_1
    //   2955: ireturn
    //   2956: aload_2
    //   2957: ldc 12
    //   2959: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2962: aload_2
    //   2963: invokevirtual 356	android/os/Parcel:readByte	()B
    //   2966: iconst_1
    //   2967: if_icmpne +18 -> 2985
    //   2970: iconst_1
    //   2971: istore 5
    //   2973: aload_0
    //   2974: iload 5
    //   2976: invokevirtual 360	android/app/ApplicationThreadNative:updateTimePrefs	(Z)V
    //   2979: aload_3
    //   2980: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   2983: iconst_1
    //   2984: ireturn
    //   2985: iconst_0
    //   2986: istore 5
    //   2988: goto -15 -> 2973
    //   2991: aload_2
    //   2992: ldc 12
    //   2994: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   2997: aload_0
    //   2998: aload_2
    //   2999: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   3002: invokevirtual 363	android/app/ApplicationThreadNative:scheduleCancelVisibleBehind	(Landroid/os/IBinder;)V
    //   3005: aload_3
    //   3006: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   3009: iconst_1
    //   3010: ireturn
    //   3011: aload_2
    //   3012: ldc 12
    //   3014: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   3017: aload_2
    //   3018: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   3021: astore 10
    //   3023: aload_2
    //   3024: invokevirtual 53	android/os/Parcel:readInt	()I
    //   3027: ifle +20 -> 3047
    //   3030: iconst_1
    //   3031: istore 5
    //   3033: aload_0
    //   3034: aload 10
    //   3036: iload 5
    //   3038: invokevirtual 366	android/app/ApplicationThreadNative:scheduleBackgroundVisibleBehindChanged	(Landroid/os/IBinder;Z)V
    //   3041: aload_3
    //   3042: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   3045: iconst_1
    //   3046: ireturn
    //   3047: iconst_0
    //   3048: istore 5
    //   3050: goto -17 -> 3033
    //   3053: aload_2
    //   3054: ldc 12
    //   3056: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   3059: aload_0
    //   3060: aload_2
    //   3061: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   3064: invokevirtual 369	android/app/ApplicationThreadNative:scheduleEnterAnimationComplete	(Landroid/os/IBinder;)V
    //   3067: aload_3
    //   3068: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   3071: iconst_1
    //   3072: ireturn
    //   3073: aload_2
    //   3074: ldc 12
    //   3076: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   3079: aload_0
    //   3080: aload_2
    //   3081: invokevirtual 373	android/os/Parcel:createByteArray	()[B
    //   3084: invokevirtual 377	android/app/ApplicationThreadNative:notifyCleartextNetwork	([B)V
    //   3087: aload_3
    //   3088: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   3091: iconst_1
    //   3092: ireturn
    //   3093: aload_2
    //   3094: ldc 12
    //   3096: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   3099: aload_0
    //   3100: invokevirtual 380	android/app/ApplicationThreadNative:startBinderTracking	()V
    //   3103: iconst_1
    //   3104: ireturn
    //   3105: aload_2
    //   3106: ldc 12
    //   3108: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   3111: aload_2
    //   3112: invokevirtual 227	android/os/Parcel:readFileDescriptor	()Landroid/os/ParcelFileDescriptor;
    //   3115: astore_2
    //   3116: aload_2
    //   3117: ifnull +15 -> 3132
    //   3120: aload_0
    //   3121: aload_2
    //   3122: invokevirtual 237	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   3125: invokevirtual 384	android/app/ApplicationThreadNative:stopBinderTrackingAndDump	(Ljava/io/FileDescriptor;)V
    //   3128: aload_2
    //   3129: invokevirtual 244	android/os/ParcelFileDescriptor:close	()V
    //   3132: iconst_1
    //   3133: ireturn
    //   3134: astore_2
    //   3135: goto -3 -> 3132
    //   3138: aload_2
    //   3139: ldc 12
    //   3141: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   3144: aload_2
    //   3145: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   3148: astore_3
    //   3149: aload_2
    //   3150: invokevirtual 53	android/os/Parcel:readInt	()I
    //   3153: ifeq +15 -> 3168
    //   3156: iconst_1
    //   3157: istore 5
    //   3159: aload_0
    //   3160: aload_3
    //   3161: iload 5
    //   3163: invokevirtual 387	android/app/ApplicationThreadNative:scheduleMultiWindowModeChanged	(Landroid/os/IBinder;Z)V
    //   3166: iconst_1
    //   3167: ireturn
    //   3168: iconst_0
    //   3169: istore 5
    //   3171: goto -12 -> 3159
    //   3174: aload_2
    //   3175: ldc 12
    //   3177: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   3180: aload_2
    //   3181: invokevirtual 49	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
    //   3184: astore_3
    //   3185: aload_2
    //   3186: invokevirtual 53	android/os/Parcel:readInt	()I
    //   3189: ifeq +15 -> 3204
    //   3192: iconst_1
    //   3193: istore 5
    //   3195: aload_0
    //   3196: aload_3
    //   3197: iload 5
    //   3199: invokevirtual 390	android/app/ApplicationThreadNative:schedulePictureInPictureModeChanged	(Landroid/os/IBinder;Z)V
    //   3202: iconst_1
    //   3203: ireturn
    //   3204: iconst_0
    //   3205: istore 5
    //   3207: goto -12 -> 3195
    //   3210: aload_2
    //   3211: ldc 12
    //   3213: invokevirtual 46	android/os/Parcel:enforceInterface	(Ljava/lang/String;)V
    //   3216: aload_0
    //   3217: getstatic 171	android/content/pm/ApplicationInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   3220: aload_2
    //   3221: invokeinterface 99 2 0
    //   3226: checkcast 170	android/content/pm/ApplicationInfo
    //   3229: getstatic 108	android/content/res/CompatibilityInfo:CREATOR	Landroid/os/Parcelable$Creator;
    //   3232: aload_2
    //   3233: invokeinterface 99 2 0
    //   3238: checkcast 107	android/content/res/CompatibilityInfo
    //   3241: getstatic 105	android/content/res/Configuration:CREATOR	Landroid/os/Parcelable$Creator;
    //   3244: aload_2
    //   3245: invokeinterface 99 2 0
    //   3250: checkcast 104	android/content/res/Configuration
    //   3253: aload_2
    //   3254: aconst_null
    //   3255: invokevirtual 193	android/os/Parcel:readHashMap	(Ljava/lang/ClassLoader;)Ljava/util/HashMap;
    //   3258: invokevirtual 394	android/app/ApplicationThreadNative:schedulePreload	(Landroid/content/pm/ApplicationInfo;Landroid/content/res/CompatibilityInfo;Landroid/content/res/Configuration;Ljava/util/Map;)V
    //   3261: aload_3
    //   3262: invokevirtual 320	android/os/Parcel:writeNoException	()V
    //   3265: iconst_1
    //   3266: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	3267	0	this	ApplicationThreadNative
    //   0	3267	1	paramInt1	int
    //   0	3267	2	paramParcel1	android.os.Parcel
    //   0	3267	3	paramParcel2	android.os.Parcel
    //   0	3267	4	paramInt2	int
    //   457	2749	5	bool1	boolean
    //   467	2159	6	bool2	boolean
    //   482	2150	7	bool3	boolean
    //   1618	1020	8	bool4	boolean
    //   2573	71	9	bool5	boolean
    //   715	2320	10	localObject1	Object
    //   721	1871	11	localObject2	Object
    //   740	1265	12	localObject3	Object
    //   754	872	13	localObject4	Object
    //   790	841	14	localObject5	Object
    //   796	837	15	localObject6	Object
    //   805	830	16	localObject7	Object
    //   817	86	17	localBundle	android.os.Bundle
    //   823	82	18	localPersistableBundle	android.os.PersistableBundle
    //   832	75	19	localArrayList1	java.util.ArrayList
    //   841	68	20	localArrayList2	java.util.ArrayList
    // Exception table:
    //   from	to	target	type
    //   1875	1879	1881	java/io/IOException
    //   1922	1926	1928	java/io/IOException
    //   2429	2433	2435	java/io/IOException
    //   2607	2612	2648	java/io/IOException
    //   2585	2607	2652	finally
    //   2653	2658	2660	java/io/IOException
    //   2696	2701	2707	java/io/IOException
    //   2686	2696	2711	finally
    //   2712	2717	2719	java/io/IOException
    //   2755	2760	2766	java/io/IOException
    //   2745	2755	2770	finally
    //   2771	2776	2778	java/io/IOException
    //   3128	3132	3134	java/io/IOException
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ApplicationThreadNative.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */