package com.oneplus.camera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.oneplus.media.BitmapPool;

public class ClearImageCacheReceiver
  extends BroadcastReceiver
{
  public static final String TAG = ClearImageCacheReceiver.class.getSimpleName();
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = paramIntent.getExtras().getString("filePath");
    Log.v(TAG, "onReceive() - clear cache for filePath: " + paramContext);
    BitmapPool.DEFAULT_THUMBNAIL.invalidate(paramContext);
    BitmapPool.DEFAULT_THUMBNAIL_SMALL.invalidate(paramContext);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/receiver/ClearImageCacheReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */