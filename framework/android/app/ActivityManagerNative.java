package android.app;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.IIntentReceiver.Stub;
import android.content.IIntentSender;
import android.content.IIntentSender.Stub;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageDataObserver.Stub;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IProgressListener.Stub;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode.ViolationInfo;
import android.service.voice.IVoiceInteractionSession;
import android.service.voice.IVoiceInteractionSession.Stub;
import android.text.TextUtils;
import android.util.Singleton;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.app.IVoiceInteractor.Stub;
import com.android.internal.os.IResultReceiver;
import com.android.internal.os.IResultReceiver.Stub;
import java.util.List;

public abstract class ActivityManagerNative
  extends Binder
  implements IActivityManager
{
  private static final Singleton<IActivityManager> gDefault = new Singleton()
  {
    protected IActivityManager create()
    {
      return ActivityManagerNative.asInterface(ServiceManager.getService("activity"));
    }
  };
  static volatile boolean sSystemReady = false;
  
  public ActivityManagerNative()
  {
    attachInterface(this, "android.app.IActivityManager");
  }
  
  public static IActivityManager asInterface(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return null;
    }
    IActivityManager localIActivityManager = (IActivityManager)paramIBinder.queryLocalInterface("android.app.IActivityManager");
    if (localIActivityManager != null) {
      return localIActivityManager;
    }
    return new ActivityManagerProxy(paramIBinder);
  }
  
  public static void broadcastStickyIntent(Intent paramIntent, String paramString, int paramInt)
  {
    broadcastStickyIntent(paramIntent, paramString, -1, paramInt);
  }
  
  public static void broadcastStickyIntent(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      getDefault().broadcastIntent(null, paramIntent, null, null, -1, null, null, null, paramInt1, null, false, true, paramInt2);
      return;
    }
    catch (RemoteException paramIntent) {}
  }
  
  public static IActivityManager getDefault()
  {
    return (IActivityManager)gDefault.get();
  }
  
  public static boolean isSystemReady()
  {
    if (!sSystemReady) {
      sSystemReady = getDefault().testIsSystemReady();
    }
    return sSystemReady;
  }
  
  public static void noteAlarmFinish(PendingIntent paramPendingIntent, int paramInt, String paramString)
  {
    IIntentSender localIIntentSender = null;
    try
    {
      IActivityManager localIActivityManager = getDefault();
      if (paramPendingIntent != null) {
        localIIntentSender = paramPendingIntent.getTarget();
      }
      localIActivityManager.noteAlarmFinish(localIIntentSender, paramInt, paramString);
      return;
    }
    catch (RemoteException paramPendingIntent) {}
  }
  
  public static void noteAlarmStart(PendingIntent paramPendingIntent, int paramInt, String paramString)
  {
    IIntentSender localIIntentSender = null;
    try
    {
      IActivityManager localIActivityManager = getDefault();
      if (paramPendingIntent != null) {
        localIIntentSender = paramPendingIntent.getTarget();
      }
      localIActivityManager.noteAlarmStart(localIIntentSender, paramInt, paramString);
      return;
    }
    catch (RemoteException paramPendingIntent) {}
  }
  
  public static void noteWakeupAlarm(PendingIntent paramPendingIntent, int paramInt, String paramString1, String paramString2)
  {
    IIntentSender localIIntentSender = null;
    try
    {
      IActivityManager localIActivityManager = getDefault();
      if (paramPendingIntent != null) {
        localIIntentSender = paramPendingIntent.getTarget();
      }
      localIActivityManager.noteWakeupAlarm(localIIntentSender, paramInt, paramString1, paramString2);
      return;
    }
    catch (RemoteException paramPendingIntent) {}
  }
  
  private int[] readIntArray(Parcel paramParcel)
  {
    int[] arrayOfInt = null;
    int i = paramParcel.readInt();
    if (i > 0)
    {
      arrayOfInt = new int[i];
      paramParcel.readIntArray(arrayOfInt);
    }
    return arrayOfInt;
  }
  
  public IBinder asBinder()
  {
    return this;
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    Object localObject6;
    Object localObject7;
    Object localObject1;
    label2991:
    Object localObject8;
    label3147:
    label3259:
    boolean bool1;
    label3316:
    label3322:
    label3480:
    int i;
    label3931:
    label4096:
    label4473:
    label4531:
    label4537:
    label4638:
    label4683:
    boolean bool2;
    label4742:
    label4748:
    label4754:
    label5859:
    label5970:
    label5982:
    boolean bool3;
    switch (paramInt1)
    {
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 25: 
    case 28: 
    case 40: 
    case 41: 
    case 139: 
    case 160: 
    case 188: 
    case 189: 
    case 190: 
    case 191: 
    case 192: 
    case 193: 
    case 194: 
    case 195: 
    case 196: 
    case 197: 
    case 198: 
    case 199: 
    case 200: 
    case 201: 
    case 202: 
    case 203: 
    case 204: 
    case 205: 
    case 206: 
    case 207: 
    case 208: 
    case 209: 
    case 210: 
    case 244: 
    case 245: 
    case 246: 
    case 247: 
    case 248: 
    case 249: 
    case 250: 
    case 251: 
    case 252: 
    case 253: 
    case 254: 
    case 255: 
    case 256: 
    case 257: 
    case 258: 
    case 259: 
    case 260: 
    case 261: 
    case 262: 
    case 263: 
    case 264: 
    case 265: 
    case 266: 
    case 267: 
    case 268: 
    case 269: 
    case 270: 
    case 271: 
    case 272: 
    case 273: 
    case 274: 
    case 275: 
    case 276: 
    case 277: 
    case 278: 
    case 279: 
    case 280: 
    case 306: 
    case 308: 
    case 309: 
    case 310: 
    case 311: 
    case 312: 
    case 313: 
    case 314: 
    case 315: 
    case 316: 
    case 317: 
    case 318: 
    case 319: 
    case 320: 
    case 329: 
    case 330: 
    case 335: 
    case 336: 
    case 337: 
    case 338: 
    case 339: 
    case 340: 
    case 382: 
    case 383: 
    case 384: 
    case 385: 
    case 386: 
    case 387: 
    case 388: 
    case 389: 
    case 390: 
    case 392: 
    case 393: 
    case 394: 
    case 395: 
    case 396: 
    case 397: 
    case 398: 
    case 399: 
    case 400: 
    case 401: 
    case 402: 
    case 403: 
    case 404: 
    case 405: 
    case 406: 
    case 407: 
    case 408: 
    case 409: 
    case 410: 
    case 411: 
    case 412: 
    case 413: 
    case 414: 
    case 415: 
    case 416: 
    case 417: 
    case 418: 
    case 419: 
    case 420: 
    case 421: 
    case 422: 
    case 423: 
    case 424: 
    case 425: 
    case 426: 
    case 427: 
    case 428: 
    case 429: 
    case 430: 
    case 431: 
    case 432: 
    case 433: 
    case 434: 
    case 435: 
    case 436: 
    case 437: 
    case 438: 
    case 439: 
    case 440: 
    case 441: 
    case 442: 
    case 443: 
    case 444: 
    case 445: 
    case 446: 
    case 447: 
    case 448: 
    case 449: 
    case 450: 
    case 451: 
    case 452: 
    case 453: 
    case 454: 
    case 455: 
    case 456: 
    case 457: 
    case 458: 
    case 459: 
    case 460: 
    case 461: 
    case 462: 
    case 463: 
    case 464: 
    case 465: 
    case 466: 
    case 467: 
    case 468: 
    case 469: 
    case 470: 
    case 471: 
    case 472: 
    case 473: 
    case 474: 
    case 475: 
    case 476: 
    case 477: 
    case 478: 
    case 479: 
    case 480: 
    case 481: 
    case 482: 
    case 483: 
    case 484: 
    case 485: 
    case 486: 
    case 487: 
    case 488: 
    case 489: 
    case 490: 
    case 491: 
    case 492: 
    case 493: 
    case 494: 
    case 495: 
    case 496: 
    case 497: 
    case 498: 
    case 499: 
    case 500: 
    case 501: 
    case 502: 
    case 503: 
    case 504: 
    case 505: 
    case 506: 
    case 507: 
    case 508: 
    case 509: 
    case 510: 
    case 511: 
    case 512: 
    case 513: 
    case 514: 
    case 515: 
    case 516: 
    case 517: 
    case 518: 
    case 519: 
    case 520: 
    case 521: 
    case 522: 
    case 523: 
    case 524: 
    case 525: 
    case 526: 
    case 527: 
    case 528: 
    case 529: 
    case 530: 
    case 531: 
    case 532: 
    case 533: 
    case 534: 
    case 535: 
    case 536: 
    case 537: 
    case 538: 
    case 539: 
    case 540: 
    case 541: 
    case 542: 
    case 543: 
    case 544: 
    case 545: 
    case 546: 
    case 547: 
    case 548: 
    case 549: 
    case 550: 
    case 551: 
    case 552: 
    case 553: 
    case 554: 
    case 555: 
    case 556: 
    case 557: 
    case 558: 
    case 559: 
    case 560: 
    case 561: 
    case 562: 
    case 563: 
    case 564: 
    case 565: 
    case 566: 
    case 567: 
    case 568: 
    case 569: 
    case 570: 
    case 571: 
    case 572: 
    case 573: 
    case 574: 
    case 575: 
    case 576: 
    case 577: 
    case 578: 
    case 579: 
    case 580: 
    case 581: 
    case 582: 
    case 583: 
    case 584: 
    case 585: 
    case 586: 
    case 587: 
    case 588: 
    case 589: 
    case 590: 
    case 591: 
    case 592: 
    case 593: 
    case 594: 
    case 595: 
    case 596: 
    case 597: 
    case 598: 
    case 599: 
    case 600: 
    case 601: 
    case 602: 
    case 603: 
    case 604: 
    case 605: 
    case 606: 
    case 607: 
    case 608: 
    case 609: 
    case 610: 
    case 611: 
    case 612: 
    case 613: 
    case 614: 
    case 615: 
    case 616: 
    case 617: 
    case 618: 
    case 619: 
    case 620: 
    case 621: 
    case 622: 
    case 623: 
    case 624: 
    case 625: 
    case 626: 
    case 627: 
    case 628: 
    case 629: 
    case 630: 
    case 631: 
    case 632: 
    case 633: 
    case 634: 
    case 635: 
    case 636: 
    case 637: 
    case 638: 
    case 639: 
    case 640: 
    case 641: 
    case 642: 
    case 643: 
    case 644: 
    case 645: 
    case 646: 
    case 647: 
    case 648: 
    case 649: 
    case 650: 
    case 651: 
    case 652: 
    case 653: 
    case 654: 
    case 655: 
    case 656: 
    case 657: 
    case 658: 
    case 659: 
    case 660: 
    case 661: 
    case 662: 
    case 663: 
    case 664: 
    case 665: 
    case 666: 
    case 667: 
    case 671: 
    case 672: 
    case 673: 
    case 674: 
    case 675: 
    case 676: 
    case 677: 
    case 678: 
    case 679: 
    case 680: 
    case 681: 
    case 682: 
    case 683: 
    case 684: 
    case 685: 
    case 686: 
    case 687: 
    case 688: 
    case 689: 
    case 690: 
    case 691: 
    case 692: 
    case 693: 
    case 694: 
    case 695: 
    case 696: 
    case 697: 
    case 698: 
    case 699: 
    case 700: 
    case 701: 
    default: 
      return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
    case 3: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject2 = ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder());
      localObject3 = paramParcel1.readString();
      localObject4 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      localObject5 = paramParcel1.readString();
      localObject6 = paramParcel1.readStrongBinder();
      localObject7 = paramParcel1.readString();
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        localObject1 = (ProfilerInfo)ProfilerInfo.CREATOR.createFromParcel(paramParcel1);
        if (paramParcel1.readInt() == 0) {
          break label2991;
        }
      }
      for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        paramInt1 = startActivity((IApplicationThread)localObject2, (String)localObject3, (Intent)localObject4, (String)localObject5, (IBinder)localObject6, (String)localObject7, paramInt1, paramInt2, (ProfilerInfo)localObject1, paramParcel1);
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
        localObject1 = null;
        break;
      }
    case 153: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject3 = ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder());
      localObject4 = paramParcel1.readString();
      localObject5 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      localObject6 = paramParcel1.readString();
      localObject7 = paramParcel1.readStrongBinder();
      localObject8 = paramParcel1.readString();
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        localObject1 = (ProfilerInfo)ProfilerInfo.CREATOR.createFromParcel(paramParcel1);
        if (paramParcel1.readInt() == 0) {
          break label3147;
        }
      }
      for (localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
      {
        paramInt1 = startActivityAsUser((IApplicationThread)localObject3, (String)localObject4, (Intent)localObject5, (String)localObject6, (IBinder)localObject7, (String)localObject8, paramInt1, paramInt2, (ProfilerInfo)localObject1, (Bundle)localObject2, paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
        localObject1 = null;
        break;
      }
    case 233: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject3 = ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder());
      localObject4 = paramParcel1.readString();
      localObject5 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      localObject6 = paramParcel1.readString();
      localObject7 = paramParcel1.readStrongBinder();
      localObject8 = paramParcel1.readString();
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        localObject1 = (ProfilerInfo)ProfilerInfo.CREATOR.createFromParcel(paramParcel1);
        if (paramParcel1.readInt() == 0) {
          break label3316;
        }
        localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
        if (paramParcel1.readInt() == 0) {
          break label3322;
        }
      }
      for (bool1 = true;; bool1 = false)
      {
        paramInt1 = startActivityAsCaller((IApplicationThread)localObject3, (String)localObject4, (Intent)localObject5, (String)localObject6, (IBinder)localObject7, (String)localObject8, paramInt1, paramInt2, (ProfilerInfo)localObject1, (Bundle)localObject2, bool1, paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
        localObject1 = null;
        break;
        localObject2 = null;
        break label3259;
      }
    case 105: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject3 = ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder());
      localObject4 = paramParcel1.readString();
      localObject5 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      localObject6 = paramParcel1.readString();
      localObject7 = paramParcel1.readStrongBinder();
      localObject8 = paramParcel1.readString();
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        localObject1 = (ProfilerInfo)ProfilerInfo.CREATOR.createFromParcel(paramParcel1);
        if (paramParcel1.readInt() == 0) {
          break label3480;
        }
      }
      for (localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
      {
        paramParcel1 = startActivityAndWait((IApplicationThread)localObject3, (String)localObject4, (Intent)localObject5, (String)localObject6, (IBinder)localObject7, (String)localObject8, paramInt1, paramInt2, (ProfilerInfo)localObject1, (Bundle)localObject2, paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel1.writeToParcel(paramParcel2, 0);
        return true;
        localObject1 = null;
        break;
      }
    case 107: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject2 = ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder());
      localObject3 = paramParcel1.readString();
      localObject4 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      localObject5 = paramParcel1.readString();
      localObject6 = paramParcel1.readStrongBinder();
      localObject7 = paramParcel1.readString();
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      localObject8 = (Configuration)Configuration.CREATOR.createFromParcel(paramParcel1);
      if (paramParcel1.readInt() != 0) {}
      for (localObject1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
      {
        paramInt1 = startActivityWithConfig((IApplicationThread)localObject2, (String)localObject3, (Intent)localObject4, (String)localObject5, (IBinder)localObject6, (String)localObject7, paramInt1, paramInt2, (Configuration)localObject8, (Bundle)localObject1, paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 100: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject2 = ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder());
      localObject3 = (IntentSender)IntentSender.CREATOR.createFromParcel(paramParcel1);
      localObject1 = null;
      if (paramParcel1.readInt() != 0) {
        localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      }
      localObject4 = paramParcel1.readString();
      localObject5 = paramParcel1.readStrongBinder();
      localObject6 = paramParcel1.readString();
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      i = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        paramInt1 = startActivityIntentSender((IApplicationThread)localObject2, (IntentSender)localObject3, (Intent)localObject1, (String)localObject4, (IBinder)localObject5, (String)localObject6, paramInt1, paramInt2, i, paramParcel1);
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 219: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject3 = paramParcel1.readString();
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      localObject4 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      localObject5 = paramParcel1.readString();
      localObject6 = IVoiceInteractionSession.Stub.asInterface(paramParcel1.readStrongBinder());
      localObject7 = IVoiceInteractor.Stub.asInterface(paramParcel1.readStrongBinder());
      i = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        localObject1 = (ProfilerInfo)ProfilerInfo.CREATOR.createFromParcel(paramParcel1);
        if (paramParcel1.readInt() == 0) {
          break label3931;
        }
      }
      for (localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
      {
        paramInt1 = startVoiceActivity((String)localObject3, paramInt1, paramInt2, (Intent)localObject4, (String)localObject5, (IVoiceInteractionSession)localObject6, (IVoiceInteractor)localObject7, i, (ProfilerInfo)localObject1, (Bundle)localObject2, paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
        localObject1 = null;
        break;
      }
    case 364: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      startLocalVoiceInteraction(paramParcel1.readStrongBinder(), paramParcel1.readBundle());
      paramParcel2.writeNoException();
      return true;
    case 365: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      stopLocalVoiceInteraction(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 366: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = supportsLocalVoiceInteraction();
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 67: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      localObject2 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      if (paramParcel1.readInt() != 0)
      {
        paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
        bool1 = startNextMatchingActivity((IBinder)localObject1, (Intent)localObject2, paramParcel1);
        paramParcel2.writeNoException();
        if (!bool1) {
          break label4096;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        paramParcel1 = null;
        break;
      }
    case 230: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() == 0) {}
      for (paramParcel1 = null;; paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1))
      {
        paramInt1 = startActivityFromRecents(paramInt1, paramParcel1);
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 11: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject2 = paramParcel1.readStrongBinder();
      localObject1 = null;
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {
        localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      }
      bool1 = finishActivity((IBinder)localObject2, paramInt1, (Intent)localObject1, paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 32: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      finishSubActivity(paramParcel1.readStrongBinder(), paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 149: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = finishActivityAffinity(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 224: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      finishVoiceTask(IVoiceInteractionSession.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    case 236: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = releaseActivityInstance(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 237: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      releaseSomeActivities(ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    case 106: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = willActivityBeVisible(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 12: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      if (localObject1 != null)
      {
        localObject1 = ApplicationThreadNative.asInterface((IBinder)localObject1);
        localObject3 = paramParcel1.readString();
        localObject2 = paramParcel1.readStrongBinder();
        if (localObject2 == null) {
          break label4531;
        }
        localObject2 = IIntentReceiver.Stub.asInterface((IBinder)localObject2);
        paramParcel1 = registerReceiver((IApplicationThread)localObject1, (String)localObject3, (IIntentReceiver)localObject2, (IntentFilter)IntentFilter.CREATOR.createFromParcel(paramParcel1), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 == null) {
          break label4537;
        }
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 0);
      }
      for (;;)
      {
        return true;
        localObject1 = null;
        break;
        localObject2 = null;
        break label4473;
        paramParcel2.writeInt(0);
      }
    case 13: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = paramParcel1.readStrongBinder();
      if (paramParcel1 == null) {
        return true;
      }
      unregisterReceiver(IIntentReceiver.Stub.asInterface(paramParcel1));
      paramParcel2.writeNoException();
      return true;
    case 14: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      if (localObject1 != null)
      {
        localObject1 = ApplicationThreadNative.asInterface((IBinder)localObject1);
        localObject3 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
        localObject4 = paramParcel1.readString();
        localObject2 = paramParcel1.readStrongBinder();
        if (localObject2 == null) {
          break label4742;
        }
        localObject2 = IIntentReceiver.Stub.asInterface((IBinder)localObject2);
        paramInt1 = paramParcel1.readInt();
        localObject5 = paramParcel1.readString();
        localObject6 = paramParcel1.readBundle();
        localObject7 = paramParcel1.readStringArray();
        paramInt2 = paramParcel1.readInt();
        localObject8 = paramParcel1.readBundle();
        if (paramParcel1.readInt() == 0) {
          break label4748;
        }
        bool1 = true;
        if (paramParcel1.readInt() == 0) {
          break label4754;
        }
      }
      for (bool2 = true;; bool2 = false)
      {
        paramInt1 = broadcastIntent((IApplicationThread)localObject1, (Intent)localObject3, (String)localObject4, (IIntentReceiver)localObject2, paramInt1, (String)localObject5, (Bundle)localObject6, (String[])localObject7, paramInt2, (Bundle)localObject8, bool1, bool2, paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
        localObject1 = null;
        break;
        localObject2 = null;
        break label4638;
        bool1 = false;
        break label4683;
      }
    case 15: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      if (localObject1 != null) {}
      for (localObject1 = ApplicationThreadNative.asInterface((IBinder)localObject1);; localObject1 = null)
      {
        unbroadcastIntent((IApplicationThread)localObject1, (Intent)Intent.CREATOR.createFromParcel(paramParcel1), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
    case 16: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      paramInt1 = paramParcel1.readInt();
      localObject2 = paramParcel1.readString();
      localObject3 = paramParcel1.readBundle();
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        paramInt2 = paramParcel1.readInt();
        if (localObject1 != null) {
          finishReceiver((IBinder)localObject1, paramInt1, (String)localObject2, (Bundle)localObject3, bool1, paramInt2);
        }
        paramParcel2.writeNoException();
        return true;
      }
    case 391: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      localObject2 = paramParcel1.readString();
      localObject3 = paramParcel1.readBundle();
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        if (localObject1 != null) {
          finishNotOrderReceiver((IBinder)localObject1, paramInt1, paramInt2, (String)localObject2, (Bundle)localObject3, bool1);
        }
        paramParcel2.writeNoException();
        return true;
      }
    case 17: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder());
      if (paramParcel1 != null) {
        attachApplication(paramParcel1);
      }
      paramParcel2.writeNoException();
      return true;
    case 18: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject2 = paramParcel1.readStrongBinder();
      localObject1 = null;
      if (paramParcel1.readInt() != 0) {
        localObject1 = (Configuration)Configuration.CREATOR.createFromParcel(paramParcel1);
      }
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        if (localObject2 != null) {
          activityIdle((IBinder)localObject2, (Configuration)localObject1, bool1);
        }
        paramParcel2.writeNoException();
        return true;
      }
    case 39: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      activityResumed(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 19: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      activityPaused(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 20: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      activityStopped(paramParcel1.readStrongBinder(), paramParcel1.readBundle(), paramParcel1.readPersistableBundle(), (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      return true;
    case 123: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      activitySlept(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 62: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      activityDestroyed(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 357: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      activityRelaunched(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 21: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = paramParcel1.readStrongBinder();
      if (paramParcel1 != null) {}
      for (paramParcel1 = getCallingPackage(paramParcel1);; paramParcel1 = null)
      {
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      }
    case 22: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getCallingActivity(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      ComponentName.writeToParcel(paramParcel1, paramParcel2);
      return true;
    case 221: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getAppTasks(paramParcel1.readString());
      paramParcel2.writeNoException();
      if (paramParcel1 != null) {}
      for (paramInt1 = paramParcel1.size();; paramInt1 = -1)
      {
        paramParcel2.writeInt(paramInt1);
        paramInt2 = 0;
        while (paramInt2 < paramInt1)
        {
          paramParcel2.writeStrongBinder(((IAppTask)paramParcel1.get(paramInt2)).asBinder());
          paramInt2 += 1;
        }
      }
      return true;
    case 234: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = addAppTask(paramParcel1.readStrongBinder(), (Intent)Intent.CREATOR.createFromParcel(paramParcel1), (ActivityManager.TaskDescription)ActivityManager.TaskDescription.CREATOR.createFromParcel(paramParcel1), (Bitmap)Bitmap.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 235: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getAppTaskThumbnailSize();
      paramParcel2.writeNoException();
      paramParcel1.writeToParcel(paramParcel2, 0);
      return true;
    case 23: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getTasks(paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (paramParcel1 != null) {}
      for (paramInt1 = paramParcel1.size();; paramInt1 = -1)
      {
        paramParcel2.writeInt(paramInt1);
        paramInt2 = 0;
        while (paramInt2 < paramInt1)
        {
          ((ActivityManager.RunningTaskInfo)paramParcel1.get(paramInt2)).writeToParcel(paramParcel2, 0);
          paramInt2 += 1;
        }
      }
      return true;
    case 60: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getRecentTasks(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel1.writeToParcel(paramParcel2, 1);
      return true;
    case 82: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getTaskThumbnail(paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
      }
      for (;;)
      {
        return true;
        paramParcel2.writeInt(0);
      }
    case 81: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getServices(paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (paramParcel1 != null) {}
      for (paramInt1 = paramParcel1.size();; paramInt1 = -1)
      {
        paramParcel2.writeInt(paramInt1);
        paramInt2 = 0;
        while (paramInt2 < paramInt1)
        {
          ((ActivityManager.RunningServiceInfo)paramParcel1.get(paramInt2)).writeToParcel(paramParcel2, 0);
          paramInt2 += 1;
        }
      }
      return true;
    case 77: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getProcessesInErrorState();
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      return true;
    case 83: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getRunningAppProcesses();
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      return true;
    case 108: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getRunningExternalApplications();
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      return true;
    case 24: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        moveTaskToFront(paramInt1, paramInt2, paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    case 75: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      if (paramParcel1.readInt() != 0)
      {
        bool1 = true;
        bool1 = moveActivityTaskToBack((IBinder)localObject1, bool1);
        paramParcel2.writeNoException();
        if (!bool1) {
          break label5859;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        bool1 = false;
        break;
      }
    case 26: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      moveTaskBackwards(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 169: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        moveTaskToStack(paramInt1, paramInt2, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 347: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt2 = paramParcel1.readInt();
      i = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        bool1 = true;
        if (paramParcel1.readInt() == 0) {
          break label6052;
        }
        bool2 = true;
        localObject1 = null;
        if (paramParcel1.readInt() == 0) {
          break label6058;
        }
        paramInt1 = 1;
        if (paramInt1 != 0) {
          localObject1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
        }
        if (paramParcel1.readInt() == 0) {
          break label6063;
        }
        bool3 = true;
        bool1 = moveTaskToDockedStack(paramInt2, i, bool1, bool2, (Rect)localObject1, bool3);
        paramParcel2.writeNoException();
        if (!bool1) {
          break label6069;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        bool1 = false;
        break;
        bool2 = false;
        break label5970;
        paramInt1 = 0;
        break label5982;
        bool3 = false;
        break label6010;
      }
    case 350: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = moveTopActivityToPinnedStack(paramParcel1.readInt(), (Rect)Rect.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 170: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt2 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        paramInt1 = 1;
        localObject1 = null;
        if (paramInt1 != 0) {
          localObject1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
        }
        if (paramParcel1.readInt() != 1) {
          break label6229;
        }
        bool1 = true;
        if (paramParcel1.readInt() != 1) {
          break label6235;
        }
        bool2 = true;
        if (paramParcel1.readInt() != 1) {
          break label6241;
        }
      }
      for (bool3 = true;; bool3 = false)
      {
        resizeStack(paramInt2, (Rect)localObject1, bool1, bool2, bool3, paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
        paramInt1 = 0;
        break;
        bool1 = false;
        break label6178;
        bool2 = false;
        break label6189;
      }
    case 371: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() != 0)
      {
        paramInt1 = 1;
        paramParcel2 = null;
        if (paramInt1 != 0) {
          paramParcel2 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
        }
        if (paramParcel1.readInt() == 0) {
          break label6325;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        localObject1 = null;
        if (paramInt1 != 0) {
          localObject1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
        }
        resizePinnedStack(paramParcel2, (Rect)localObject1);
        return true;
        paramInt1 = 0;
        break;
      }
    case 373: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      swapDockedAndFullscreenStack();
      paramParcel2.writeNoException();
      return true;
    case 359: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() != 0)
      {
        paramInt1 = 1;
        localObject1 = null;
        if (paramInt1 != 0) {
          localObject1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
        }
        if (paramParcel1.readInt() == 0) {
          break label6527;
        }
        paramInt1 = 1;
        localObject2 = null;
        if (paramInt1 != 0) {
          localObject2 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
        }
        if (paramParcel1.readInt() == 0) {
          break label6532;
        }
        paramInt1 = 1;
        localObject3 = null;
        if (paramInt1 != 0) {
          localObject3 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
        }
        if (paramParcel1.readInt() == 0) {
          break label6537;
        }
        paramInt1 = 1;
        localObject4 = null;
        if (paramInt1 != 0) {
          localObject4 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
        }
        if (paramParcel1.readInt() == 0) {
          break label6542;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        localObject5 = null;
        if (paramInt1 != 0) {
          localObject5 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
        }
        resizeDockedStack((Rect)localObject1, (Rect)localObject2, (Rect)localObject3, (Rect)localObject4, (Rect)localObject5);
        paramParcel2.writeNoException();
        return true;
        paramInt1 = 0;
        break;
        paramInt1 = 0;
        break label6391;
        paramInt1 = 0;
        break label6421;
        paramInt1 = 0;
        break label6451;
      }
    case 343: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      positionTaskInStack(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 171: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getAllStackInfos();
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      return true;
    case 173: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getStackInfo(paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 0);
      }
      for (;;)
      {
        return true;
        paramParcel2.writeInt(0);
      }
    case 213: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isInHomeStack(paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 172: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      setFocusedStack(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 283: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getFocusedStackId();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 131: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      setFocusedTask(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 243: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      registerTaskStackListener(ITaskStackListener.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    case 27: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      if (paramParcel1.readInt() != 0)
      {
        bool1 = true;
        if (localObject1 == null) {
          break label6818;
        }
      }
      for (paramInt1 = getTaskForActivity((IBinder)localObject1, bool1);; paramInt1 = -1)
      {
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
        bool1 = false;
        break;
      }
    case 29: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder());
      localObject2 = paramParcel1.readString();
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        bool1 = true;
        paramParcel1 = getContentProvider((IApplicationThread)localObject1, (String)localObject2, paramInt1, bool1);
        paramParcel2.writeNoException();
        if (paramParcel1 == null) {
          break label6898;
        }
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 0);
      }
      for (;;)
      {
        return true;
        bool1 = false;
        break;
        paramParcel2.writeInt(0);
      }
    case 141: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getContentProviderExternal(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 0);
      }
      for (;;)
      {
        return true;
        paramParcel2.writeInt(0);
      }
    case 30: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      publishContentProviders(ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder()), paramParcel1.createTypedArrayList(IActivityManager.ContentProviderHolder.CREATOR));
      paramParcel2.writeNoException();
      return true;
    case 31: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = refContentProvider(paramParcel1.readStrongBinder(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 151: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      unstableProviderDied(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 183: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      appNotRespondingViaProvider(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 69: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        removeContentProvider((IBinder)localObject1, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 142: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      removeContentProviderExternal(paramParcel1.readString(), paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 33: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getRunningServiceControlPanel((ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      PendingIntent.writePendingIntentOrNullToParcel(paramParcel1, paramParcel2);
      return true;
    case 34: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = startService(ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder()), (Intent)Intent.CREATOR.createFromParcel(paramParcel1), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      ComponentName.writeToParcel(paramParcel1, paramParcel2);
      return true;
    case 35: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = stopService(ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder()), (Intent)Intent.CREATOR.createFromParcel(paramParcel1), paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 48: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = stopServiceToken(ComponentName.readFromParcel(paramParcel1), paramParcel1.readStrongBinder(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 74: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject2 = ComponentName.readFromParcel(paramParcel1);
      localObject3 = paramParcel1.readStrongBinder();
      paramInt1 = paramParcel1.readInt();
      localObject1 = null;
      if (paramParcel1.readInt() != 0) {
        localObject1 = (Notification)Notification.CREATOR.createFromParcel(paramParcel1);
      }
      setServiceForeground((ComponentName)localObject2, (IBinder)localObject3, paramInt1, (Notification)localObject1, paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 36: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder());
      localObject2 = paramParcel1.readStrongBinder();
      localObject3 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      localObject4 = paramParcel1.readString();
      localObject5 = paramParcel1.readStrongBinder();
      paramInt1 = paramParcel1.readInt();
      localObject6 = paramParcel1.readString();
      paramInt2 = paramParcel1.readInt();
      paramInt1 = bindService((IApplicationThread)localObject1, (IBinder)localObject2, (Intent)localObject3, (String)localObject4, IServiceConnection.Stub.asInterface((IBinder)localObject5), paramInt1, (String)localObject6, paramInt2);
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 37: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = unbindService(IServiceConnection.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 38: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      publishService(paramParcel1.readStrongBinder(), (Intent)Intent.CREATOR.createFromParcel(paramParcel1), paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 72: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      localObject2 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        unbindFinished((IBinder)localObject1, (Intent)localObject2, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 61: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      serviceDoneExecuting(paramParcel1.readStrongBinder(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 44: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = startInstrumentation(ComponentName.readFromParcel(paramParcel1), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readBundle(), IInstrumentationWatcher.Stub.asInterface(paramParcel1.readStrongBinder()), IUiAutomationConnection.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readString());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 45: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      finishInstrumentation(ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readBundle());
      paramParcel2.writeNoException();
      return true;
    case 46: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getConfiguration();
      paramParcel2.writeNoException();
      paramParcel1.writeToParcel(paramParcel2, 0);
      return true;
    case 47: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      updateConfiguration((Configuration)Configuration.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      return true;
    case 70: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      setRequestedOrientation(paramParcel1.readStrongBinder(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 71: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getRequestedOrientation(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 49: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getActivityClassForToken(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      ComponentName.writeToParcel(paramParcel1, paramParcel2);
      return true;
    case 50: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = paramParcel1.readStrongBinder();
      paramParcel2.writeNoException();
      paramParcel2.writeString(getPackageForToken(paramParcel1));
      return true;
    case 63: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = paramParcel1.readInt();
      localObject4 = paramParcel1.readString();
      localObject5 = paramParcel1.readStrongBinder();
      localObject6 = paramParcel1.readString();
      paramInt2 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        localObject1 = (Intent[])paramParcel1.createTypedArray(Intent.CREATOR);
        localObject2 = paramParcel1.createStringArray();
        i = paramParcel1.readInt();
        if (paramParcel1.readInt() == 0) {
          break label8059;
        }
        localObject3 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
        paramParcel1 = getIntentSender(paramInt1, (String)localObject4, (IBinder)localObject5, (String)localObject6, paramInt2, (Intent[])localObject1, (String[])localObject2, i, (Bundle)localObject3, paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 == null) {
          break label8065;
        }
      }
      for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
      {
        paramParcel2.writeStrongBinder(paramParcel1);
        return true;
        localObject1 = null;
        localObject2 = null;
        break;
        localObject3 = null;
        break label8002;
      }
    case 64: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      cancelIntentSender(IIntentSender.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    case 65: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getPackageForIntentSender(IIntentSender.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      return true;
    case 93: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getUidForIntentSender(IIntentSender.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 94: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      i = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        bool1 = true;
        if (paramParcel1.readInt() == 0) {
          break label8233;
        }
      }
      for (bool2 = true;; bool2 = false)
      {
        paramInt1 = handleIncomingUser(paramInt1, paramInt2, i, bool1, bool2, paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
        bool1 = false;
        break;
      }
    case 51: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      setProcessLimit(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 52: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getProcessLimit();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 73: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        setProcessForeground((IBinder)localObject1, paramInt1, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 53: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = checkPermission(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 242: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = checkPermissionWithToken(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 54: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = checkUriPermission((Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 78: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = clearApplicationUserData(paramParcel1.readString(), IPackageDataObserver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 55: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      grantUriPermission(ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), (Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 56: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      revokeUriPermission(ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder()), (Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 180: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      takePersistableUriPermission((Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 181: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      releasePersistableUriPermission((Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 182: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readString();
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        paramParcel1 = getPersistedUriPermissions((String)localObject1, bool1);
        paramParcel2.writeNoException();
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      }
    case 361: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getGrantedUriPermissions(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel1.writeToParcel(paramParcel2, 1);
      return true;
    case 362: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      clearGrantedUriPermissions(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 58: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder());
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        showWaitingForDebugger((IApplicationThread)localObject1, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 76: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = new ActivityManager.MemoryInfo();
      getMemoryInfo(paramParcel1);
      paramParcel2.writeNoException();
      paramParcel1.writeToParcel(paramParcel2, 0);
      return true;
    case 4: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      unhandledBack();
      paramParcel2.writeNoException();
      return true;
    case 5: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = openContentUri(Uri.parse(paramParcel1.readString()));
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
      }
      for (;;)
      {
        return true;
        paramParcel2.writeInt(0);
      }
    case 148: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() != 0)
      {
        bool1 = true;
        if (paramParcel1.readInt() == 0) {
          break label8956;
        }
      }
      for (bool2 = true;; bool2 = false)
      {
        setLockScreenShown(bool1, bool2);
        paramParcel2.writeNoException();
        return true;
        bool1 = false;
        break;
      }
    case 42: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readString();
      if (paramParcel1.readInt() != 0)
      {
        bool1 = true;
        if (paramParcel1.readInt() == 0) {
          break label9016;
        }
      }
      for (bool2 = true;; bool2 = false)
      {
        setDebugApp((String)localObject1, bool1, bool2);
        paramParcel2.writeNoException();
        return true;
        bool1 = false;
        break;
      }
    case 43: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        setAlwaysFinish(bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 57: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = IActivityController.Stub.asInterface(paramParcel1.readStrongBinder());
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        setActivityController((IActivityController)localObject1, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 369: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        setLenientBackgroundCheck(bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 370: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getMemoryTrimLevel();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 66: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      enterSafeMode();
      paramParcel2.writeNoException();
      return true;
    case 68: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      noteWakeupAlarm(IIntentSender.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 292: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      noteAlarmStart(IIntentSender.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 293: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      noteAlarmFinish(IIntentSender.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 80: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.createIntArray();
      localObject2 = paramParcel1.readString();
      if (paramParcel1.readInt() != 0)
      {
        bool1 = true;
        bool1 = killPids((int[])localObject1, (String)localObject2, bool1);
        paramParcel2.writeNoException();
        if (!bool1) {
          break label9334;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        bool1 = false;
        break;
      }
    case 144: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = killProcessesBelowForeground(paramParcel1.readString());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 2: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      handleApplicationCrash(paramParcel1.readStrongBinder(), new ApplicationErrorReport.CrashInfo(paramParcel1));
      paramParcel2.writeNoException();
      return true;
    case 102: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      localObject2 = paramParcel1.readString();
      if (paramParcel1.readInt() != 0)
      {
        bool1 = true;
        bool1 = handleApplicationWtf((IBinder)localObject1, (String)localObject2, bool1, new ApplicationErrorReport.CrashInfo(paramParcel1));
        paramParcel2.writeNoException();
        if (!bool1) {
          break label9478;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        bool1 = false;
        break;
      }
    case 110: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      handleApplicationStrictModeViolation(paramParcel1.readStrongBinder(), paramParcel1.readInt(), new StrictMode.ViolationInfo(paramParcel1));
      paramParcel2.writeNoException();
      return true;
    case 59: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      signalPersistentProcesses(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 103: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      killBackgroundProcesses(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 140: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      killAllBackgroundProcesses();
      paramParcel2.writeNoException();
      return true;
    case 355: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      killPackageDependents(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 79: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      forceStopPackage(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 143: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = new ActivityManager.RunningAppProcessInfo();
      getMyMemoryState(paramParcel1);
      paramParcel2.writeNoException();
      paramParcel1.writeToParcel(paramParcel2, 0);
      return true;
    case 84: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getDeviceConfigurationInfo();
      paramParcel2.writeNoException();
      paramParcel1.writeToParcel(paramParcel2, 0);
      return true;
    case 86: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readString();
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        bool1 = true;
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() == 0) {
          break label9768;
        }
        paramParcel1 = (ProfilerInfo)ProfilerInfo.CREATOR.createFromParcel(paramParcel1);
        bool1 = profileControl((String)localObject1, paramInt1, bool1, paramParcel1, paramInt2);
        paramParcel2.writeNoException();
        if (!bool1) {
          break label9773;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        bool1 = false;
        break;
        paramParcel1 = null;
        break label9730;
      }
    case 87: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = shutdown(paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 88: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      stopAppSwitches();
      paramParcel2.writeNoException();
      return true;
    case 89: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      resumeAppSwitches();
      paramParcel2.writeNoException();
      return true;
    case 85: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = peekService((Intent)Intent.CREATOR.createFromParcel(paramParcel1), paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeStrongBinder(paramParcel1);
      return true;
    case 90: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = bindBackupAgent(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 91: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      backupAgentCreated(paramParcel1.readString(), paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 92: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      unbindBackupAgent((ApplicationInfo)ApplicationInfo.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      return true;
    case 95: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      addPackageDependency(paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 96: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      killApplication(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 97: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      closeSystemDialogs(paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 98: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getProcessMemoryInfo(paramParcel1.createIntArray());
      paramParcel2.writeNoException();
      paramParcel2.writeTypedArray(paramParcel1, 1);
      return true;
    case 99: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      killApplicationProcess(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 101: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      overridePendingTransition(paramParcel1.readStrongBinder(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 104: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isUserAMonkey();
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 166: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() == 1) {}
      for (bool1 = true;; bool1 = false)
      {
        setUserIsMonkey(bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 109: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      finishHeavyWeightApp();
      paramParcel2.writeNoException();
      return true;
    case 111: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isImmersive(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 225: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isTopOfTask(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 174: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = convertFromTranslucent(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 175: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      if (paramParcel1.readInt() == 0)
      {
        paramParcel1 = null;
        bool1 = convertToTranslucent((IBinder)localObject1, ActivityOptions.fromBundle(paramParcel1));
        paramParcel2.writeNoException();
        if (!bool1) {
          break label10407;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        paramParcel1 = paramParcel1.readBundle();
        break;
      }
    case 220: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getActivityOptions(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (paramParcel1 == null) {}
      for (paramParcel1 = null;; paramParcel1 = paramParcel1.toBundle())
      {
        paramParcel2.writeBundle(paramParcel1);
        return true;
      }
    case 112: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      if (paramParcel1.readInt() == 1) {}
      for (bool1 = true;; bool1 = false)
      {
        setImmersive((IBinder)localObject1, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 113: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isTopActivityImmersive();
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 114: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      crashApplication(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 115: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getProviderMimeType((Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      return true;
    case 116: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = newUriPermissionOwner(paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeStrongBinder(paramParcel1);
      return true;
    case 358: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getUriPermissionOwnerForActivity(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      paramParcel2.writeStrongBinder(paramParcel1);
      return true;
    case 117: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      grantUriPermissionFromOwner(paramParcel1.readStrongBinder(), paramParcel1.readInt(), paramParcel1.readString(), (Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 118: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject2 = paramParcel1.readStrongBinder();
      localObject1 = null;
      if (paramParcel1.readInt() != 0) {
        localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
      }
      revokeUriPermissionFromOwner((IBinder)localObject2, (Uri)localObject1, paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 119: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = checkGrantUriPermission(paramParcel1.readInt(), paramParcel1.readString(), (Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 120: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readString();
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        bool1 = true;
        localObject2 = paramParcel1.readString();
        if (paramParcel1.readInt() == 0) {
          break label10903;
        }
        paramParcel1 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);
        bool1 = dumpHeap((String)localObject1, paramInt1, bool1, (String)localObject2, paramParcel1);
        paramParcel2.writeNoException();
        if (!bool1) {
          break label10908;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        bool1 = false;
        break;
        paramParcel1 = null;
        break label10865;
      }
    case 121: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject2 = ApplicationThreadNative.asInterface(paramParcel1.readStrongBinder());
      localObject3 = paramParcel1.readString();
      localObject4 = (Intent[])paramParcel1.createTypedArray(Intent.CREATOR);
      localObject5 = paramParcel1.createStringArray();
      localObject6 = paramParcel1.readStrongBinder();
      if (paramParcel1.readInt() != 0) {}
      for (localObject1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
      {
        paramInt1 = startActivities((IApplicationThread)localObject2, (String)localObject3, (Intent[])localObject4, (String[])localObject5, (IBinder)localObject6, (Bundle)localObject1, paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 124: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getFrontActivityScreenCompatMode();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 125: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = paramParcel1.readInt();
      setFrontActivityScreenCompatMode(paramInt1);
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 126: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getPackageScreenCompatMode(paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 127: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      setPackageScreenCompatMode(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 130: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = switchUser(paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 212: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = startUserInBackground(paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 352: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = unlockUser(paramParcel1.readInt(), paramParcel1.createByteArray(), paramParcel1.createByteArray(), IProgressListener.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 154: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        paramInt1 = stopUser(paramInt1, bool1, IStopUserCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 145: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getCurrentUser();
      paramParcel2.writeNoException();
      paramParcel1.writeToParcel(paramParcel2, 0);
      return true;
    case 122: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isUserRunning(paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 157: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getRunningUserIds();
      paramParcel2.writeNoException();
      paramParcel2.writeIntArray(paramParcel1);
      return true;
    case 132: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = removeTask(paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 133: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      registerProcessObserver(IProcessObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
      return true;
    case 134: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      unregisterProcessObserver(IProcessObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
      return true;
    case 298: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      registerUidObserver(IUidObserver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
      return true;
    case 299: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      unregisterUidObserver(IUidObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
      return true;
    case 128: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = getPackageAskScreenCompat(paramParcel1.readString());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 129: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readString();
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        setPackageAskScreenCompat((String)localObject1, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 135: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isIntentSenderTargetedToPackage(IIntentSender.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 152: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isIntentSenderAnActivity(IIntentSender.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 161: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getIntentForIntentSender(IIntentSender.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
      }
      for (;;)
      {
        return true;
        paramParcel2.writeInt(0);
      }
    case 211: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getTagForIntentSender(IIntentSender.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      return true;
    case 136: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      updatePersistentConfiguration((Configuration)Configuration.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      return true;
    case 137: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getProcessPss(paramParcel1.createIntArray());
      paramParcel2.writeNoException();
      paramParcel2.writeLongArray(paramParcel1);
      return true;
    case 138: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        showBootMessage((CharSequence)localObject1, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 232: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      keyguardWaitingForActivityDrawn();
      paramParcel2.writeNoException();
      return true;
    case 297: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      keyguardGoingAway(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 146: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = shouldUpRecreateTask(paramParcel1.readStrongBinder(), paramParcel1.readString());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 147: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject2 = paramParcel1.readStrongBinder();
      localObject3 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      paramInt1 = paramParcel1.readInt();
      localObject1 = null;
      if (paramParcel1.readInt() != 0) {
        localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      }
      bool1 = navigateUpTo((IBinder)localObject2, (Intent)localObject3, paramInt1, (Intent)localObject1);
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 150: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getLaunchedFromUid(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 164: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getLaunchedFromPackage(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      return true;
    case 155: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      registerUserSwitchObserver(IUserSwitchObserver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 156: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      unregisterUserSwitchObserver(IUserSwitchObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    case 158: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      requestBugReport(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 159: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        long l = inputDispatchingTimedOut(paramInt1, bool1, paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      }
    case 162: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getAssistContextExtras(paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeBundle(paramParcel1);
      return true;
    case 285: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = paramParcel1.readInt();
      localObject1 = IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder());
      localObject2 = paramParcel1.readBundle();
      localObject3 = paramParcel1.readStrongBinder();
      if (paramParcel1.readInt() == 1)
      {
        bool1 = true;
        if (paramParcel1.readInt() != 1) {
          break label12322;
        }
        bool2 = true;
        bool1 = requestAssistContextExtras(paramInt1, (IResultReceiver)localObject1, (Bundle)localObject2, (IBinder)localObject3, bool1, bool2);
        paramParcel2.writeNoException();
        if (!bool1) {
          break label12328;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        bool1 = false;
        break;
        bool2 = false;
        break label12281;
      }
    case 163: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      localObject2 = paramParcel1.readBundle();
      localObject3 = (AssistStructure)AssistStructure.CREATOR.createFromParcel(paramParcel1);
      localObject4 = (AssistContent)AssistContent.CREATOR.createFromParcel(paramParcel1);
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        reportAssistContextExtras((IBinder)localObject1, (Bundle)localObject2, (AssistStructure)localObject3, (AssistContent)localObject4, paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    case 240: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = launchAssistIntent((Intent)Intent.CREATOR.createFromParcel(paramParcel1), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readBundle());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 300: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isAssistDataAllowedOnCurrentActivity();
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 301: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = showAssistFromActivity(paramParcel1.readStrongBinder(), paramParcel1.readBundle());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 165: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      killUid(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 167: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        hang((IBinder)localObject1, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 177: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      reportActivityFullyDrawn(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 176: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      notifyActivityDrawn(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 178: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      restart();
      paramParcel2.writeNoException();
      return true;
    case 179: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      performIdleMaintenance();
      paramParcel2.writeNoException();
      return true;
    case 168: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = createVirtualActivityContainer(paramParcel1.readStrongBinder(), IActivityContainerCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel2.writeStrongBinder(paramParcel1.asBinder());
      }
      for (;;)
      {
        return true;
        paramParcel2.writeInt(0);
      }
    case 186: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      deleteActivityContainer(IActivityContainer.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    case 282: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = createStackOnDisplay(paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel2.writeStrongBinder(paramParcel1.asBinder());
      }
      for (;;)
      {
        return true;
        paramParcel2.writeInt(0);
      }
    case 185: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getActivityDisplayId(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 214: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      startLockTaskMode(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 215: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      startLockTaskMode(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 222: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      startSystemLockTaskMode(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 216: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      stopLockTaskMode();
      paramParcel2.writeNoException();
      return true;
    case 223: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      stopSystemLockTaskMode();
      paramParcel2.writeNoException();
      return true;
    case 217: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isInLockTaskMode();
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 287: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getLockTaskModeState();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 295: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      showLockTaskEscapeMessage(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 218: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      setTaskDescription(paramParcel1.readStrongBinder(), (ActivityManager.TaskDescription)ActivityManager.TaskDescription.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      return true;
    case 284: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      setTaskResizeable(paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 286: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      resizeTask(paramInt1, (Rect)Rect.CREATOR.createFromParcel(paramParcel1), paramInt2);
      paramParcel2.writeNoException();
      return true;
    case 184: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getTaskBounds(paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel1.writeToParcel(paramParcel2, 0);
      return true;
    case 239: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getTaskDescriptionIcon(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (paramParcel1 == null) {
        paramParcel2.writeInt(0);
      }
      for (;;)
      {
        return true;
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 0);
      }
    case 241: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() == 0) {}
      for (paramParcel1 = null;; paramParcel1 = paramParcel1.readBundle())
      {
        startInPlaceAnimationOnFrontMostApplication(ActivityOptions.fromBundle(paramParcel1));
        paramParcel2.writeNoException();
        return true;
      }
    case 226: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      if (paramParcel1.readInt() > 0)
      {
        bool1 = true;
        bool1 = requestVisibleBehind((IBinder)localObject1, bool1);
        paramParcel2.writeNoException();
        if (!bool1) {
          break label13293;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        bool1 = false;
        break;
      }
    case 227: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isBackgroundVisibleBehind(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 228: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      backgroundResourcesReleased(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 229: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      notifyLaunchTaskBehindComplete(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 231: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      notifyEnterAnimationComplete(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 238: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bootAnimationComplete();
      paramParcel2.writeNoException();
      return true;
    case 281: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      notifyCleartextNetwork(paramParcel1.readInt(), paramParcel1.createByteArray());
      paramParcel2.writeNoException();
      return true;
    case 288: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      setDumpHeapDebugLimit(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readLong(), paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 289: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      dumpHeapFinished(paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 290: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = IVoiceInteractionSession.Stub.asInterface(paramParcel1.readStrongBinder());
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        setVoiceKeepAwake((IVoiceInteractionSession)localObject1, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 291: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      updateLockTaskPackages(paramParcel1.readInt(), paramParcel1.readStringArray());
      paramParcel2.writeNoException();
      return true;
    case 296: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      updateDeviceOwner(paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 294: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getPackageProcessState(paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 187: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = setProcessMemoryTrimLevel(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 302: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isRootVoiceInteraction(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 341: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = startBinderTracking();
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 342: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() != 0)
      {
        paramParcel1 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);
        bool1 = stopBinderTrackingAndDump(paramParcel1);
        paramParcel2.writeNoException();
        if (!bool1) {
          break label13785;
        }
      }
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        paramParcel1 = null;
        break;
      }
    case 344: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getActivityStackId(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 345: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      exitFreeformMode(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 346: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      reportSizeConfigurations(paramParcel1.readStrongBinder(), readIntArray(paramParcel1), readIntArray(paramParcel1), readIntArray(paramParcel1));
      return true;
    case 348: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() == 1) {}
      for (bool1 = true;; bool1 = false)
      {
        suppressResizeConfigChanges(bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 349: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() == 1) {}
      for (bool1 = true;; bool1 = false)
      {
        moveTasksToFullscreenStack(paramInt1, bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 351: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getAppStartMode(paramParcel1.readInt(), paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 353: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isInMultiWindowMode(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 354: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isInPictureInPictureMode(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 356: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      enterPictureInPictureMode(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    case 360: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject1 = paramParcel1.readStrongBinder();
      if (paramParcel1.readInt() == 1) {}
      for (bool1 = true;; bool1 = false)
      {
        paramInt1 = setVrMode((IBinder)localObject1, bool1, (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1));
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 372: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isVrModePackageEnabled((ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 363: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isAppForeground(paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 367: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel2.writeNoException();
      return true;
    case 368: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      removeStack(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 374: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      notifyLockedProfile(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 375: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      startConfirmDeviceCredentialIntent((Intent)Intent.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      return true;
    case 376: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      sendIdleJobTrigger();
      paramParcel2.writeNoException();
      return true;
    case 377: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      localObject2 = IIntentSender.Stub.asInterface(paramParcel1.readStrongBinder());
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0)
      {
        localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
        localObject3 = paramParcel1.readString();
        localObject4 = IIntentReceiver.Stub.asInterface(paramParcel1.readStrongBinder());
        localObject5 = paramParcel1.readString();
        if (paramParcel1.readInt() == 0) {
          break label14430;
        }
      }
      for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        paramInt1 = sendIntentSender((IIntentSender)localObject2, paramInt1, (Intent)localObject1, (String)localObject3, (IIntentReceiver)localObject4, (String)localObject5, paramParcel1);
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
        localObject1 = null;
        break;
      }
    case 378: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      setVrThread(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 379: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      setRenderThread(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 380: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        setHasTopUi(bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 381: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = canBypassWorkChallenge((PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 303: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getPermissionServiceBinderProxy(paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeStrongBinder(paramParcel1);
      return true;
    case 304: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      setPermissionServiceBinderProxy(paramParcel1.readStrongBinder(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 305: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      setIgnoredAnrProcess(paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    case 307: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        isRequestPermission(bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 322: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getAppBootMode(paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 323: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = setAppBootMode(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 324: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = getAppBootState();
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 325: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        setAppBootState(bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 321: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getAllAppBootModes(paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      return true;
    case 326: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getCallerPackageArray(paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeStringArray(paramParcel1);
      return true;
    case 327: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getCalleePackageArray(paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeStringArray(paramParcel1);
      return true;
    case 328: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      updateAccesibilityServiceFlag(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 331: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getBgPowerHungryList();
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      return true;
    case 332: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        setBgMonitorMode(bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 333: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      stopBgPowerHungryApp(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    case 334: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = getBgMonitorMode();
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 668: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      if (paramParcel1.readInt() != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        setKeyguardDone(bool1);
        paramParcel2.writeNoException();
        return true;
      }
    case 669: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isAppLocked(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 670: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      bool1 = isKeyguardDone();
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    case 702: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramParcel1 = getAllAppControlModes(paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      return true;
    case 703: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getAppControlMode(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 704: 
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = setAppControlMode(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 705: 
      label6010:
      label6052:
      label6058:
      label6063:
      label6069:
      label6178:
      label6189:
      label6229:
      label6235:
      label6241:
      label6325:
      label6391:
      label6421:
      label6451:
      label6527:
      label6532:
      label6537:
      label6542:
      label6818:
      label6898:
      label8002:
      label8059:
      label8065:
      label8233:
      label8956:
      label9016:
      label9334:
      label9478:
      label9730:
      label9768:
      label9773:
      label10407:
      label10865:
      label10903:
      label10908:
      label12281:
      label12322:
      label12328:
      label13293:
      label13785:
      label14430:
      paramParcel1.enforceInterface("android.app.IActivityManager");
      paramInt1 = getAppControlState(paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    paramParcel1.enforceInterface("android.app.IActivityManager");
    paramInt1 = setAppControlState(paramParcel1.readInt(), paramParcel1.readInt());
    paramParcel2.writeNoException();
    paramParcel2.writeInt(paramInt1);
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ActivityManagerNative.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */