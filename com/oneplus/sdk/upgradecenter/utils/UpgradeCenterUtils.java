package com.oneplus.sdk.upgradecenter.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import java.util.Arrays;
import java.util.Iterator;

public class UpgradeCenterUtils
{
  private static final boolean DBG = true;
  private static final String OP_API_TAG_END = "OPAPI_END";
  private static final String OP_API_TAG_START = "OPAPI_START";
  private static final String OP_LIB_TAG = "oneplus_libs";
  private static final String OP_UPGRADE_TAG = "oneplus_upgrage";
  private static final String PACKAGE_URI_PREFIX = "package:";
  private static final String TAG = "UpgradeCenterUtils";
  
  public static String getFilteredReleaseNote(String paramString)
  {
    Log.v("UpgradeCenterUtils", "getFilteredReleaseNote: in = " + paramString);
    try
    {
      if ((paramString.contains("OPAPI_START")) && (paramString.contains("OPAPI_END")))
      {
        Log.d("UpgradeCenterUtils", "getFilteredReleaseNote: Start filter out OPAPI TAG");
        String str1 = "";
        if (paramString.split("OPAPI_START").length > 1) {
          str1 = paramString.split("OPAPI_START")[0];
        }
        String str2 = "";
        if (paramString.split("OPAPI_END").length > 1) {
          str2 = paramString.split("OPAPI_END")[1];
        }
        paramString = str1 + str2;
        Log.v("UpgradeCenterUtils", "getFilteredReleaseNote: out = " + paramString);
        return paramString;
      }
      if ((paramString.contains("OPAPI_START")) || (paramString.contains("OPAPI_END")))
      {
        Log.e("UpgradeCenterUtils", "getFilteredReleaseNote: OPAPI tag incompleted. please check release not.");
        return paramString;
      }
      Log.d("UpgradeCenterUtils", "getFilteredReleaseNote: No OP_API_TAG_START, no opapi dependency");
      return paramString;
    }
    catch (NullPointerException paramString)
    {
      Log.e("UpgradeCenterUtils", "Null release note?");
      paramString.printStackTrace();
    }
    return null;
  }
  
  public static boolean isOnePlusUpgradablePackage(Context paramContext, String paramString)
  {
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool2 = false;
    try
    {
      boolean bool1 = paramContext.getPackageManager().getApplicationInfo(paramString, 128).metaData.getBoolean("oneplus_upgrage");
      bool2 = bool1;
      bool3 = bool1;
      bool4 = bool1;
      Log.d("UpgradeCenterUtils", "OP_UPGRADE_TAG: " + bool1);
      return bool1;
    }
    catch (Exception paramContext)
    {
      Log.e("UpgradeCenterUtils", "Exception: " + paramContext);
      paramContext.printStackTrace();
      return bool2;
    }
    catch (NullPointerException paramContext)
    {
      Log.e("UpgradeCenterUtils", "Failed to load meta-data, NullPointer: " + paramContext.getMessage());
      return bool3;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.e("UpgradeCenterUtils", "Failed to load meta-data, NameNotFound: " + paramContext.getMessage());
    }
    return bool4;
  }
  
  public static boolean isRequiredOpApiCompatibleWithRom(Context paramContext, String paramString)
  {
    if (!isOnePlusUpgradablePackage(paramContext, paramString)) {
      return true;
    }
    try
    {
      paramContext = paramContext.getPackageManager().getApplicationInfo(paramString, 128).metaData.getString("oneplus_libs");
      Log.d("UpgradeCenterUtils", "OP_LIB_TAG: " + paramContext);
      paramContext = "OPAPI_START-" + paramContext + '-' + "OPAPI_END";
      Log.d("UpgradeCenterUtils", "opLibStr: " + paramContext);
      boolean bool = isUpgradable(paramContext);
      return bool;
    }
    catch (Exception paramContext)
    {
      Log.e("UpgradeCenterUtils", "Exception: " + paramContext);
      paramContext.printStackTrace();
      return false;
    }
    catch (NullPointerException paramContext)
    {
      Log.e("UpgradeCenterUtils", "Failed to load meta-data, NullPointer: " + paramContext.getMessage() + ", no meta data specified in manifest?");
      return false;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.e("UpgradeCenterUtils", "Failed to load meta-data, NameNotFound: " + paramContext.getMessage() + ", please make sure the apk exists.");
    }
    return false;
  }
  
  public static boolean isRequiredOpApiCompatibleWithRom(String paramString)
  {
    try
    {
      paramString = "OPAPI_START-" + paramString + '-' + "OPAPI_END";
      Log.d("UpgradeCenterUtils", "opLibStr: " + paramString);
      boolean bool = isUpgradable(paramString);
      return bool;
    }
    catch (Exception paramString)
    {
      Log.e("UpgradeCenterUtils", "Exception: " + paramString);
      paramString.printStackTrace();
    }
    return false;
  }
  
  public static boolean isUpgradable(String paramString)
  {
    boolean bool1 = true;
    Log.v("UpgradeCenterUtils", "isUpgradable: in = " + paramString);
    boolean bool2;
    try
    {
      if ((paramString.contains("OPAPI_START")) && (paramString.contains("OPAPI_END")))
      {
        Log.d("UpgradeCenterUtils", "isUpgradable: Start filter out OPAPI TAG");
        paramString = paramString.split("OPAPI_START-")[1].split("-OPAPI_END")[0];
        Log.v("UpgradeCenterUtils", "getFilteredReleaseNote: dependency string = " + paramString);
        paramString = Arrays.asList(paramString.split(";")).iterator();
        for (;;)
        {
          bool2 = bool1;
          if (!paramString.hasNext()) {
            break;
          }
          String str2 = (String)paramString.next();
          String str1 = str2.split(":")[0];
          Log.v("UpgradeCenterUtils", "checking dependency: " + str1);
          str2 = str2.split(":")[1];
          str1 = SystemProperties.get(str1);
          Log.i("UpgradeCenterUtils", "required: " + str2 + ", supported: " + str1);
          int i = Integer.parseInt(str2.split("\\.")[0]);
          int j = Integer.parseInt(str2.split("\\.")[1]);
          int k = Integer.parseInt(str1.split("\\.")[0]);
          int m = Integer.parseInt(str1.split("\\.")[1]);
          if (i != k)
          {
            Log.e("UpgradeCenterUtils", "version not compatible - required: " + str2 + ", supported: " + str1);
            bool1 = false;
          }
          if (j > m)
          {
            Log.e("UpgradeCenterUtils", "version not compatible - required: " + str2 + ", supported: " + str1);
            bool1 = false;
          }
        }
      }
      if ((paramString.contains("OPAPI_START")) || (paramString.contains("OPAPI_END")))
      {
        Log.e("UpgradeCenterUtils", "getFilteredReleaseNote: OPAPI tag incompleted. please check release not.");
        return false;
      }
      Log.d("UpgradeCenterUtils", "getFilteredReleaseNote: No OP_API_TAG_START, no opapi dependency");
      return true;
    }
    catch (Exception paramString)
    {
      Log.e("UpgradeCenterUtils", "Exception. Stop parsing.");
      paramString.printStackTrace();
      bool2 = false;
    }
    return bool2;
  }
  
  public static void showOpApiIncompatibleAlertDlg(Activity paramActivity, int paramInt1, int paramInt2, int paramInt3)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramActivity);
    localBuilder.setTitle(paramInt1);
    localBuilder.setMessage(paramInt2);
    localBuilder.setPositiveButton(paramInt3, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + this.val$activity.getPackageName()));
        this.val$activity.startActivity(paramAnonymousDialogInterface);
        this.val$activity.finish();
      }
    });
    localBuilder.setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        this.val$activity.finish();
      }
    });
    localBuilder.create().show();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/sdk/upgradecenter/utils/UpgradeCenterUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */