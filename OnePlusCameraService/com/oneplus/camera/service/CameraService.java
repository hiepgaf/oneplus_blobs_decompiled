package com.oneplus.camera.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import com.oneplus.base.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class CameraService
  extends Service
{
  private static final int BACKLIGHT_BRIGHTNESS_MAX = 0;
  private static final int BACKLIGHT_BRIGHTNESS_RESTORE = 1;
  private static final String FILE_BACKLIGHT = "/sys/class/leds/lcd-backlight/brightness";
  private static final String FILE_FLASH_TORCH = "/sys/class/leds/led:flash_torch/brightness";
  private static final String FILE_SWITCH = "/sys/class/leds/led:switch/brightness";
  private static final String FILE_SWITCH_0 = "/sys/class/leds/led:switch_0/brightness";
  private static final String FILE_TORCH_0 = "/sys/class/leds/led:torch_0/brightness";
  private static final String FILE_TORCH_1 = "/sys/class/leds/led:torch_1/brightness";
  private static final int MSG_CHECK_SUPPORTED_STATE = -1260010;
  private static final int MSG_RESULT_SUPPORTED_STATE = -1260060;
  private static final int MSG_SET_BACKLIGHT_BRIGHTNESS = -1200010;
  private static final int MSG_TORCH_FLASH = -1260100;
  private static final int SUPPORTED_STATE_TORCH = 30100;
  private static final String TAG = CameraService.class.getSimpleName();
  private static final int TORCH_FLASH_OFF = 0;
  private static final int TORCH_FLASH_ON = 1;
  private static final byte[] TORCH_OFF = { 48 };
  private static final byte[] TORCH_ON = { 49 };
  private Handler m_AsyncHandler;
  private HandlerThread m_AsyncHandlerThread;
  private boolean m_IsTorchFileOn;
  private Boolean m_IsTorchSupported;
  private Handler m_MainHandler;
  private Messenger m_Messenger;
  private int m_OriginalBacklightValue = -1;
  
  private boolean checkSupportedState(int paramInt)
  {
    boolean bool = false;
    switch (paramInt)
    {
    default: 
      return false;
    }
    if (this.m_IsTorchSupported != null) {
      return this.m_IsTorchSupported.booleanValue();
    }
    File localFile1 = new File("/sys/class/leds/led:torch_0/brightness");
    File localFile2 = new File("/sys/class/leds/led:flash_torch/brightness");
    if ((localFile1.canWrite()) || (localFile2.canWrite())) {
      bool = true;
    }
    this.m_IsTorchSupported = Boolean.valueOf(bool);
    return bool;
  }
  
  /* Error */
  private int getCurrentBrightness()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 6
    //   6: iconst_m1
    //   7: istore_1
    //   8: aconst_null
    //   9: astore 4
    //   11: aconst_null
    //   12: astore 7
    //   14: new 118	java/io/BufferedReader
    //   17: dup
    //   18: new 120	java/io/FileReader
    //   21: dup
    //   22: ldc 17
    //   24: invokespecial 121	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   27: invokespecial 124	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   30: astore_3
    //   31: aload_3
    //   32: invokevirtual 127	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   35: astore 4
    //   37: aload 4
    //   39: ifnull +40 -> 79
    //   42: getstatic 81	com/oneplus/camera/service/CameraService:TAG	Ljava/lang/String;
    //   45: new 129	java/lang/StringBuilder
    //   48: dup
    //   49: invokespecial 130	java/lang/StringBuilder:<init>	()V
    //   52: ldc -124
    //   54: invokevirtual 136	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: aload 4
    //   59: invokevirtual 136	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: invokevirtual 139	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   65: invokestatic 145	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   68: aload 4
    //   70: invokestatic 151	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   73: istore_2
    //   74: iload_2
    //   75: istore_1
    //   76: goto -45 -> 31
    //   79: aload 6
    //   81: astore 4
    //   83: aload_3
    //   84: ifnull +11 -> 95
    //   87: aload_3
    //   88: invokevirtual 154	java/io/BufferedReader:close	()V
    //   91: aload 6
    //   93: astore 4
    //   95: aload 4
    //   97: ifnull +23 -> 120
    //   100: aload 4
    //   102: athrow
    //   103: astore_3
    //   104: getstatic 81	com/oneplus/camera/service/CameraService:TAG	Ljava/lang/String;
    //   107: ldc -100
    //   109: aload_3
    //   110: invokestatic 160	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   113: iload_1
    //   114: ireturn
    //   115: astore 4
    //   117: goto -22 -> 95
    //   120: iload_1
    //   121: ireturn
    //   122: astore 4
    //   124: aload 7
    //   126: astore_3
    //   127: aload 4
    //   129: athrow
    //   130: astore 6
    //   132: aload 4
    //   134: astore 5
    //   136: aload 6
    //   138: astore 4
    //   140: aload 5
    //   142: astore 6
    //   144: aload_3
    //   145: ifnull +11 -> 156
    //   148: aload_3
    //   149: invokevirtual 154	java/io/BufferedReader:close	()V
    //   152: aload 5
    //   154: astore 6
    //   156: aload 6
    //   158: ifnull +29 -> 187
    //   161: aload 6
    //   163: athrow
    //   164: aload 5
    //   166: astore 6
    //   168: aload 5
    //   170: aload_3
    //   171: if_acmpeq -15 -> 156
    //   174: aload 5
    //   176: aload_3
    //   177: invokevirtual 164	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   180: aload 5
    //   182: astore 6
    //   184: goto -28 -> 156
    //   187: aload 4
    //   189: athrow
    //   190: astore 6
    //   192: aload 4
    //   194: astore_3
    //   195: aload 6
    //   197: astore 4
    //   199: goto -59 -> 140
    //   202: astore 4
    //   204: goto -64 -> 140
    //   207: astore 4
    //   209: goto -82 -> 127
    //   212: astore_3
    //   213: goto -109 -> 104
    //   216: astore_3
    //   217: aload 5
    //   219: ifnonnull -55 -> 164
    //   222: aload_3
    //   223: astore 6
    //   225: goto -69 -> 156
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	228	0	this	CameraService
    //   7	114	1	i	int
    //   73	2	2	j	int
    //   30	58	3	localBufferedReader	java.io.BufferedReader
    //   103	7	3	localThrowable1	Throwable
    //   126	69	3	localObject1	Object
    //   212	1	3	localThrowable2	Throwable
    //   216	7	3	localThrowable3	Throwable
    //   9	92	4	localObject2	Object
    //   115	1	4	localThrowable4	Throwable
    //   122	11	4	localThrowable5	Throwable
    //   138	60	4	localObject3	Object
    //   202	1	4	localObject4	Object
    //   207	1	4	localThrowable6	Throwable
    //   1	217	5	localObject5	Object
    //   4	88	6	localObject6	Object
    //   130	7	6	localObject7	Object
    //   142	41	6	localObject8	Object
    //   190	6	6	localObject9	Object
    //   223	1	6	localObject10	Object
    //   12	113	7	localObject11	Object
    // Exception table:
    //   from	to	target	type
    //   100	103	103	java/lang/Throwable
    //   87	91	115	java/lang/Throwable
    //   14	31	122	java/lang/Throwable
    //   127	130	130	finally
    //   14	31	190	finally
    //   31	37	202	finally
    //   42	74	202	finally
    //   31	37	207	java/lang/Throwable
    //   42	74	207	java/lang/Throwable
    //   161	164	212	java/lang/Throwable
    //   174	180	212	java/lang/Throwable
    //   187	190	212	java/lang/Throwable
    //   148	152	216	java/lang/Throwable
  }
  
  private boolean handleAsyncMessage(Message paramMessage)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    int i = 0;
    switch (paramMessage.what)
    {
    default: 
      return false;
    case -1260010: 
      bool1 = checkSupportedState(paramMessage.arg1);
      try
      {
        paramMessage = paramMessage.replyTo;
        if (bool1) {
          i = 1;
        }
        paramMessage.send(Message.obtain(null, -1260060, 30100, i));
        return true;
      }
      catch (Throwable paramMessage)
      {
        Log.e(TAG, "handleAsyncMessage() - Error when send reply of supported state");
        paramMessage.printStackTrace();
        return true;
      }
    case -1200010: 
      if (paramMessage.arg1 == 0) {
        bool1 = true;
      }
      setBacklightBrightnessFile(bool1);
      return true;
    }
    bool1 = bool2;
    if (paramMessage.arg1 == 1) {
      bool1 = true;
    }
    torchFlashByFile(bool1);
    return true;
  }
  
  private boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return false;
    }
    Message localMessage = Message.obtain(paramMessage);
    localMessage.replyTo = paramMessage.replyTo;
    this.m_AsyncHandler.sendMessage(localMessage);
    return true;
  }
  
  private boolean setBacklightBrightnessFile(boolean paramBoolean)
  {
    if ((!paramBoolean) && (this.m_OriginalBacklightValue < 0))
    {
      Log.w(TAG, "setBacklightBrightnessFile() - Backlight file is not modified");
      return false;
    }
    Object localObject = new File("/sys/class/leds/lcd-backlight/brightness");
    if (((File)localObject).exists())
    {
      if (paramBoolean) {}
      try
      {
        this.m_OriginalBacklightValue = getCurrentBrightness();
        if (this.m_OriginalBacklightValue < 0)
        {
          Log.e(TAG, "setBacklightBrightnessFile() - Invalid backlight value in file : " + this.m_OriginalBacklightValue);
          return false;
        }
        localObject = new BufferedWriter(new FileWriter((File)localObject, false));
        ((BufferedWriter)localObject).write("255");
        ((BufferedWriter)localObject).close();
      }
      catch (Throwable localThrowable)
      {
        Log.e(TAG, "setBacklightBrightnessFile() - Failed to update backlight file.", localThrowable);
        return false;
      }
      localObject = new BufferedWriter(new FileWriter((File)localObject, false));
      ((BufferedWriter)localObject).write(String.valueOf(this.m_OriginalBacklightValue));
      ((BufferedWriter)localObject).close();
    }
    else
    {
      Log.w(TAG, "setBacklightBrightnessFile() - There is no backlight file.");
      return false;
    }
    return true;
  }
  
  /* Error */
  private boolean torchFlashByFile(boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_3
    //   2: iconst_0
    //   3: istore_2
    //   4: iload_1
    //   5: ifeq +37 -> 42
    //   8: getstatic 85	com/oneplus/camera/service/CameraService:TORCH_ON	[B
    //   11: astore 5
    //   13: new 102	java/io/File
    //   16: dup
    //   17: ldc 29
    //   19: invokespecial 105	java/io/File:<init>	(Ljava/lang/String;)V
    //   22: astore 4
    //   24: aload 4
    //   26: invokevirtual 219	java/io/File:exists	()Z
    //   29: ifeq +609 -> 638
    //   32: iload_1
    //   33: aload_0
    //   34: getfield 253	com/oneplus/camera/service/CameraService:m_IsTorchFileOn	Z
    //   37: if_icmpne +13 -> 50
    //   40: iconst_1
    //   41: ireturn
    //   42: getstatic 83	com/oneplus/camera/service/CameraService:TORCH_OFF	[B
    //   45: astore 5
    //   47: goto -34 -> 13
    //   50: new 102	java/io/File
    //   53: dup
    //   54: ldc 32
    //   56: invokespecial 105	java/io/File:<init>	(Ljava/lang/String;)V
    //   59: astore 10
    //   61: aload 10
    //   63: invokevirtual 219	java/io/File:exists	()Z
    //   66: ifeq +171 -> 237
    //   69: aconst_null
    //   70: astore 6
    //   72: aconst_null
    //   73: astore 8
    //   75: aconst_null
    //   76: astore 9
    //   78: aconst_null
    //   79: astore 7
    //   81: new 255	java/io/FileOutputStream
    //   84: dup
    //   85: aload 10
    //   87: invokespecial 258	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   90: astore 4
    //   92: getstatic 81	com/oneplus/camera/service/CameraService:TAG	Ljava/lang/String;
    //   95: ldc_w 260
    //   98: aload 5
    //   100: invokestatic 265	java/util/Arrays:toString	([B)Ljava/lang/String;
    //   103: invokestatic 269	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   106: aload 4
    //   108: aload 5
    //   110: invokevirtual 272	java/io/FileOutputStream:write	([B)V
    //   113: aload_0
    //   114: iload_1
    //   115: putfield 253	com/oneplus/camera/service/CameraService:m_IsTorchFileOn	Z
    //   118: aload 8
    //   120: astore 6
    //   122: aload 4
    //   124: ifnull +12 -> 136
    //   127: aload 4
    //   129: invokevirtual 273	java/io/FileOutputStream:close	()V
    //   132: aload 8
    //   134: astore 6
    //   136: aload 6
    //   138: ifnull +267 -> 405
    //   141: aload 6
    //   143: athrow
    //   144: astore 4
    //   146: getstatic 81	com/oneplus/camera/service/CameraService:TAG	Ljava/lang/String;
    //   149: ldc_w 275
    //   152: aload 4
    //   154: invokestatic 160	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   157: iconst_0
    //   158: ireturn
    //   159: astore 6
    //   161: goto -25 -> 136
    //   164: astore 5
    //   166: aload 7
    //   168: astore 4
    //   170: aload 5
    //   172: athrow
    //   173: astore 7
    //   175: aload 5
    //   177: astore 6
    //   179: aload 7
    //   181: astore 5
    //   183: aload 6
    //   185: astore 7
    //   187: aload 4
    //   189: ifnull +12 -> 201
    //   192: aload 4
    //   194: invokevirtual 273	java/io/FileOutputStream:close	()V
    //   197: aload 6
    //   199: astore 7
    //   201: aload 7
    //   203: ifnull +31 -> 234
    //   206: aload 7
    //   208: athrow
    //   209: aload 6
    //   211: astore 7
    //   213: aload 6
    //   215: aload 4
    //   217: if_acmpeq -16 -> 201
    //   220: aload 6
    //   222: aload 4
    //   224: invokevirtual 164	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   227: aload 6
    //   229: astore 7
    //   231: goto -30 -> 201
    //   234: aload 5
    //   236: athrow
    //   237: aconst_null
    //   238: astore 6
    //   240: aconst_null
    //   241: astore 8
    //   243: aconst_null
    //   244: astore 9
    //   246: aconst_null
    //   247: astore 7
    //   249: new 255	java/io/FileOutputStream
    //   252: dup
    //   253: aload 4
    //   255: invokespecial 258	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   258: astore 4
    //   260: getstatic 81	com/oneplus/camera/service/CameraService:TAG	Ljava/lang/String;
    //   263: ldc_w 277
    //   266: aload 5
    //   268: invokestatic 265	java/util/Arrays:toString	([B)Ljava/lang/String;
    //   271: invokestatic 269	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   274: aload 4
    //   276: aload 5
    //   278: invokevirtual 272	java/io/FileOutputStream:write	([B)V
    //   281: aload_0
    //   282: iload_1
    //   283: putfield 253	com/oneplus/camera/service/CameraService:m_IsTorchFileOn	Z
    //   286: aload 8
    //   288: astore 6
    //   290: aload 4
    //   292: ifnull +12 -> 304
    //   295: aload 4
    //   297: invokevirtual 273	java/io/FileOutputStream:close	()V
    //   300: aload 8
    //   302: astore 6
    //   304: aload 6
    //   306: ifnull +99 -> 405
    //   309: aload 6
    //   311: athrow
    //   312: astore 4
    //   314: getstatic 81	com/oneplus/camera/service/CameraService:TAG	Ljava/lang/String;
    //   317: ldc_w 279
    //   320: aload 4
    //   322: invokestatic 160	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   325: iconst_0
    //   326: ireturn
    //   327: astore 6
    //   329: goto -25 -> 304
    //   332: astore 5
    //   334: aload 7
    //   336: astore 4
    //   338: aload 5
    //   340: athrow
    //   341: astore 7
    //   343: aload 5
    //   345: astore 6
    //   347: aload 7
    //   349: astore 5
    //   351: aload 6
    //   353: astore 7
    //   355: aload 4
    //   357: ifnull +12 -> 369
    //   360: aload 4
    //   362: invokevirtual 273	java/io/FileOutputStream:close	()V
    //   365: aload 6
    //   367: astore 7
    //   369: aload 7
    //   371: ifnull +31 -> 402
    //   374: aload 7
    //   376: athrow
    //   377: aload 6
    //   379: astore 7
    //   381: aload 6
    //   383: aload 4
    //   385: if_acmpeq -16 -> 369
    //   388: aload 6
    //   390: aload 4
    //   392: invokevirtual 164	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   395: aload 6
    //   397: astore 7
    //   399: goto -30 -> 369
    //   402: aload 5
    //   404: athrow
    //   405: iconst_1
    //   406: istore_2
    //   407: new 102	java/io/File
    //   410: dup
    //   411: ldc 23
    //   413: invokespecial 105	java/io/File:<init>	(Ljava/lang/String;)V
    //   416: astore 4
    //   418: new 102	java/io/File
    //   421: dup
    //   422: ldc 26
    //   424: invokespecial 105	java/io/File:<init>	(Ljava/lang/String;)V
    //   427: astore 6
    //   429: aconst_null
    //   430: astore 7
    //   432: aload 4
    //   434: invokevirtual 219	java/io/File:exists	()Z
    //   437: ifeq +93 -> 530
    //   440: aload 4
    //   442: ifnull +185 -> 627
    //   445: aconst_null
    //   446: astore 6
    //   448: aconst_null
    //   449: astore 8
    //   451: aconst_null
    //   452: astore 9
    //   454: aconst_null
    //   455: astore 7
    //   457: new 255	java/io/FileOutputStream
    //   460: dup
    //   461: aload 4
    //   463: invokespecial 258	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   466: astore 4
    //   468: getstatic 81	com/oneplus/camera/service/CameraService:TAG	Ljava/lang/String;
    //   471: ldc_w 281
    //   474: aload 5
    //   476: invokestatic 265	java/util/Arrays:toString	([B)Ljava/lang/String;
    //   479: invokestatic 269	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   482: aload 4
    //   484: aload 5
    //   486: invokevirtual 272	java/io/FileOutputStream:write	([B)V
    //   489: aload 8
    //   491: astore 5
    //   493: aload 4
    //   495: ifnull +12 -> 507
    //   498: aload 4
    //   500: invokevirtual 273	java/io/FileOutputStream:close	()V
    //   503: aload 8
    //   505: astore 5
    //   507: aload 5
    //   509: ifnull +127 -> 636
    //   512: aload 5
    //   514: athrow
    //   515: astore 4
    //   517: getstatic 81	com/oneplus/camera/service/CameraService:TAG	Ljava/lang/String;
    //   520: ldc_w 283
    //   523: aload 4
    //   525: invokestatic 160	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   528: iconst_0
    //   529: ireturn
    //   530: aload 7
    //   532: astore 4
    //   534: aload 6
    //   536: invokevirtual 219	java/io/File:exists	()Z
    //   539: ifeq -99 -> 440
    //   542: aload 6
    //   544: astore 4
    //   546: goto -106 -> 440
    //   549: astore 5
    //   551: goto -44 -> 507
    //   554: astore 5
    //   556: aload 7
    //   558: astore 4
    //   560: aload 5
    //   562: athrow
    //   563: astore 7
    //   565: aload 5
    //   567: astore 6
    //   569: aload 7
    //   571: astore 5
    //   573: aload 6
    //   575: astore 7
    //   577: aload 4
    //   579: ifnull +12 -> 591
    //   582: aload 4
    //   584: invokevirtual 273	java/io/FileOutputStream:close	()V
    //   587: aload 6
    //   589: astore 7
    //   591: aload 7
    //   593: ifnull +31 -> 624
    //   596: aload 7
    //   598: athrow
    //   599: aload 6
    //   601: astore 7
    //   603: aload 6
    //   605: aload 4
    //   607: if_acmpeq -16 -> 591
    //   610: aload 6
    //   612: aload 4
    //   614: invokevirtual 164	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   617: aload 6
    //   619: astore 7
    //   621: goto -30 -> 591
    //   624: aload 5
    //   626: athrow
    //   627: getstatic 81	com/oneplus/camera/service/CameraService:TAG	Ljava/lang/String;
    //   630: ldc_w 285
    //   633: invokestatic 191	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   636: iload_2
    //   637: ireturn
    //   638: new 102	java/io/File
    //   641: dup
    //   642: ldc 20
    //   644: invokespecial 105	java/io/File:<init>	(Ljava/lang/String;)V
    //   647: astore 4
    //   649: aload 4
    //   651: invokevirtual 219	java/io/File:exists	()Z
    //   654: ifeq -18 -> 636
    //   657: iload_1
    //   658: aload_0
    //   659: getfield 253	com/oneplus/camera/service/CameraService:m_IsTorchFileOn	Z
    //   662: if_icmpne +5 -> 667
    //   665: iconst_1
    //   666: ireturn
    //   667: aconst_null
    //   668: astore 6
    //   670: aconst_null
    //   671: astore 8
    //   673: aconst_null
    //   674: astore 9
    //   676: aconst_null
    //   677: astore 7
    //   679: new 255	java/io/FileOutputStream
    //   682: dup
    //   683: aload 4
    //   685: invokespecial 258	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   688: astore 4
    //   690: getstatic 81	com/oneplus/camera/service/CameraService:TAG	Ljava/lang/String;
    //   693: ldc_w 287
    //   696: aload 5
    //   698: invokestatic 265	java/util/Arrays:toString	([B)Ljava/lang/String;
    //   701: invokestatic 269	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   704: aload 4
    //   706: aload 5
    //   708: invokevirtual 272	java/io/FileOutputStream:write	([B)V
    //   711: aload_0
    //   712: iload_1
    //   713: putfield 253	com/oneplus/camera/service/CameraService:m_IsTorchFileOn	Z
    //   716: iconst_1
    //   717: istore_1
    //   718: iconst_1
    //   719: istore_2
    //   720: aload 8
    //   722: astore 5
    //   724: aload 4
    //   726: ifnull +12 -> 738
    //   729: aload 4
    //   731: invokevirtual 273	java/io/FileOutputStream:close	()V
    //   734: aload 8
    //   736: astore 5
    //   738: aload 5
    //   740: ifnull -104 -> 636
    //   743: aload 5
    //   745: athrow
    //   746: astore 4
    //   748: getstatic 81	com/oneplus/camera/service/CameraService:TAG	Ljava/lang/String;
    //   751: ldc_w 289
    //   754: aload 4
    //   756: invokestatic 160	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   759: iload_1
    //   760: ireturn
    //   761: astore 5
    //   763: goto -25 -> 738
    //   766: astore 5
    //   768: aload 7
    //   770: astore 4
    //   772: aload 5
    //   774: athrow
    //   775: astore 7
    //   777: aload 5
    //   779: astore 6
    //   781: aload 7
    //   783: astore 5
    //   785: aload 6
    //   787: astore 7
    //   789: aload 4
    //   791: ifnull +12 -> 803
    //   794: aload 4
    //   796: invokevirtual 273	java/io/FileOutputStream:close	()V
    //   799: aload 6
    //   801: astore 7
    //   803: aload 7
    //   805: ifnull +31 -> 836
    //   808: aload 7
    //   810: athrow
    //   811: aload 6
    //   813: astore 7
    //   815: aload 6
    //   817: aload 4
    //   819: if_acmpeq -16 -> 803
    //   822: aload 6
    //   824: aload 4
    //   826: invokevirtual 164	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   829: aload 6
    //   831: astore 7
    //   833: goto -30 -> 803
    //   836: aload 5
    //   838: athrow
    //   839: astore 5
    //   841: aload 9
    //   843: astore 4
    //   845: goto -60 -> 785
    //   848: astore 5
    //   850: goto -65 -> 785
    //   853: astore 5
    //   855: goto -83 -> 772
    //   858: astore 5
    //   860: aload 9
    //   862: astore 4
    //   864: goto -291 -> 573
    //   867: astore 5
    //   869: goto -296 -> 573
    //   872: astore 5
    //   874: goto -314 -> 560
    //   877: astore 5
    //   879: aload 9
    //   881: astore 4
    //   883: goto -532 -> 351
    //   886: astore 5
    //   888: goto -537 -> 351
    //   891: astore 5
    //   893: goto -555 -> 338
    //   896: astore 5
    //   898: aload 9
    //   900: astore 4
    //   902: goto -719 -> 183
    //   905: astore 5
    //   907: goto -724 -> 183
    //   910: astore 5
    //   912: goto -742 -> 170
    //   915: astore 4
    //   917: goto -771 -> 146
    //   920: astore 4
    //   922: aload 6
    //   924: ifnonnull -715 -> 209
    //   927: aload 4
    //   929: astore 7
    //   931: goto -730 -> 201
    //   934: astore 4
    //   936: goto -622 -> 314
    //   939: astore 4
    //   941: aload 6
    //   943: ifnonnull -566 -> 377
    //   946: aload 4
    //   948: astore 7
    //   950: goto -581 -> 369
    //   953: astore 4
    //   955: goto -438 -> 517
    //   958: astore 4
    //   960: aload 6
    //   962: ifnonnull -363 -> 599
    //   965: aload 4
    //   967: astore 7
    //   969: goto -378 -> 591
    //   972: astore 4
    //   974: iload_3
    //   975: istore_1
    //   976: goto -228 -> 748
    //   979: astore 4
    //   981: aload 6
    //   983: ifnonnull -172 -> 811
    //   986: aload 4
    //   988: astore 7
    //   990: goto -187 -> 803
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	993	0	this	CameraService
    //   0	993	1	paramBoolean	boolean
    //   3	717	2	bool1	boolean
    //   1	974	3	bool2	boolean
    //   22	106	4	localObject1	Object
    //   144	9	4	localThrowable1	Throwable
    //   168	128	4	localObject2	Object
    //   312	9	4	localThrowable2	Throwable
    //   336	163	4	localObject3	Object
    //   515	9	4	localThrowable3	Throwable
    //   532	198	4	localObject4	Object
    //   746	9	4	localThrowable4	Throwable
    //   770	131	4	localObject5	Object
    //   915	1	4	localThrowable5	Throwable
    //   920	8	4	localThrowable6	Throwable
    //   934	1	4	localThrowable7	Throwable
    //   939	8	4	localThrowable8	Throwable
    //   953	1	4	localThrowable9	Throwable
    //   958	8	4	localThrowable10	Throwable
    //   972	1	4	localThrowable11	Throwable
    //   979	8	4	localThrowable12	Throwable
    //   11	98	5	arrayOfByte	byte[]
    //   164	12	5	localThrowable13	Throwable
    //   181	96	5	localObject6	Object
    //   332	12	5	localThrowable14	Throwable
    //   349	164	5	localObject7	Object
    //   549	1	5	localThrowable15	Throwable
    //   554	12	5	localThrowable16	Throwable
    //   571	173	5	localObject8	Object
    //   761	1	5	localThrowable17	Throwable
    //   766	12	5	localThrowable18	Throwable
    //   783	54	5	localObject9	Object
    //   839	1	5	localObject10	Object
    //   848	1	5	localObject11	Object
    //   853	1	5	localThrowable19	Throwable
    //   858	1	5	localObject12	Object
    //   867	1	5	localObject13	Object
    //   872	1	5	localThrowable20	Throwable
    //   877	1	5	localObject14	Object
    //   886	1	5	localObject15	Object
    //   891	1	5	localThrowable21	Throwable
    //   896	1	5	localObject16	Object
    //   905	1	5	localObject17	Object
    //   910	1	5	localThrowable22	Throwable
    //   70	72	6	localObject18	Object
    //   159	1	6	localThrowable23	Throwable
    //   177	133	6	localObject19	Object
    //   327	1	6	localThrowable24	Throwable
    //   345	637	6	localObject20	Object
    //   79	88	7	localObject21	Object
    //   173	7	7	localObject22	Object
    //   185	150	7	localObject23	Object
    //   341	7	7	localObject24	Object
    //   353	204	7	localObject25	Object
    //   563	7	7	localObject26	Object
    //   575	194	7	localObject27	Object
    //   775	7	7	localObject28	Object
    //   787	202	7	localObject29	Object
    //   73	662	8	localObject30	Object
    //   76	823	9	localObject31	Object
    //   59	27	10	localFile	File
    // Exception table:
    //   from	to	target	type
    //   141	144	144	java/lang/Throwable
    //   127	132	159	java/lang/Throwable
    //   81	92	164	java/lang/Throwable
    //   170	173	173	finally
    //   309	312	312	java/lang/Throwable
    //   295	300	327	java/lang/Throwable
    //   249	260	332	java/lang/Throwable
    //   338	341	341	finally
    //   512	515	515	java/lang/Throwable
    //   498	503	549	java/lang/Throwable
    //   457	468	554	java/lang/Throwable
    //   560	563	563	finally
    //   743	746	746	java/lang/Throwable
    //   729	734	761	java/lang/Throwable
    //   679	690	766	java/lang/Throwable
    //   772	775	775	finally
    //   679	690	839	finally
    //   690	716	848	finally
    //   690	716	853	java/lang/Throwable
    //   457	468	858	finally
    //   468	489	867	finally
    //   468	489	872	java/lang/Throwable
    //   249	260	877	finally
    //   260	286	886	finally
    //   260	286	891	java/lang/Throwable
    //   81	92	896	finally
    //   92	118	905	finally
    //   92	118	910	java/lang/Throwable
    //   206	209	915	java/lang/Throwable
    //   220	227	915	java/lang/Throwable
    //   234	237	915	java/lang/Throwable
    //   192	197	920	java/lang/Throwable
    //   374	377	934	java/lang/Throwable
    //   388	395	934	java/lang/Throwable
    //   402	405	934	java/lang/Throwable
    //   360	365	939	java/lang/Throwable
    //   596	599	953	java/lang/Throwable
    //   610	617	953	java/lang/Throwable
    //   624	627	953	java/lang/Throwable
    //   582	587	958	java/lang/Throwable
    //   808	811	972	java/lang/Throwable
    //   822	829	972	java/lang/Throwable
    //   836	839	972	java/lang/Throwable
    //   794	799	979	java/lang/Throwable
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return this.m_Messenger.getBinder();
  }
  
  public void onCreate()
  {
    super.onCreate();
    this.m_MainHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        if (!CameraService.-wrap1(CameraService.this, paramAnonymousMessage)) {
          super.handleMessage(paramAnonymousMessage);
        }
      }
    };
    this.m_Messenger = new Messenger(this.m_MainHandler);
    this.m_AsyncHandlerThread = new HandlerThread(TAG, 10);
    this.m_AsyncHandlerThread.start();
    this.m_AsyncHandler = new Handler(this.m_AsyncHandlerThread.getLooper())
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        if (!CameraService.-wrap0(CameraService.this, paramAnonymousMessage)) {
          super.handleMessage(paramAnonymousMessage);
        }
      }
    };
  }
  
  public void onDestroy()
  {
    Log.v(TAG, "onDestroy()");
    if (this.m_AsyncHandlerThread != null) {
      this.m_AsyncHandlerThread.getLooper().quitSafely();
    }
    super.onDestroy();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/service/CameraService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */