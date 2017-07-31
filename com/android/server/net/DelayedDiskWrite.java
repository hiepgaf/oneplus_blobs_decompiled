package com.android.server.net;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DelayedDiskWrite
{
  private final String TAG = "DelayedDiskWrite";
  private Handler mDiskWriteHandler;
  private HandlerThread mDiskWriteHandlerThread;
  private int mWriteSequence = 0;
  
  private void doWrite(String paramString, Writer paramWriter, boolean paramBoolean)
  {
    localObject1 = null;
    localObject2 = null;
    DataOutputStream localDataOutputStream = null;
    if (paramBoolean) {}
    for (;;)
    {
      try
      {
        localDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(paramString)));
        localObject1 = localDataOutputStream;
        localObject2 = localDataOutputStream;
        paramWriter.onWriteCalled(localDataOutputStream);
        if (localDataOutputStream == null) {}
      }
      catch (IOException paramWriter)
      {
        localObject2 = localObject1;
        loge("Error writing data file " + paramString);
        if (localObject1 == null) {
          continue;
        }
        try
        {
          ((DataOutputStream)localObject1).close();
          try
          {
            i = this.mWriteSequence - 1;
            this.mWriteSequence = i;
            if (i != 0) {
              continue;
            }
            this.mDiskWriteHandler.getLooper().quit();
            this.mDiskWriteHandler = null;
            this.mDiskWriteHandlerThread = null;
            continue;
          }
          finally {}
        }
        catch (Exception paramString)
        {
          continue;
        }
      }
      finally
      {
        int i;
        if (localObject2 == null) {
          continue;
        }
        try
        {
          ((DataOutputStream)localObject2).close();
        }
        catch (Exception paramWriter)
        {
          try
          {
            i = this.mWriteSequence - 1;
            this.mWriteSequence = i;
            if (i == 0)
            {
              this.mDiskWriteHandler.getLooper().quit();
              this.mDiskWriteHandler = null;
              this.mDiskWriteHandlerThread = null;
            }
            throw paramString;
          }
          finally {}
          paramWriter = paramWriter;
          continue;
        }
      }
      try
      {
        localDataOutputStream.close();
      }
      catch (Exception paramString)
      {
        try
        {
          i = this.mWriteSequence - 1;
          this.mWriteSequence = i;
          if (i == 0)
          {
            this.mDiskWriteHandler.getLooper().quit();
            this.mDiskWriteHandler = null;
            this.mDiskWriteHandlerThread = null;
          }
          return;
        }
        finally {}
        paramString = paramString;
      }
    }
  }
  
  private void loge(String paramString)
  {
    Log.e("DelayedDiskWrite", paramString);
  }
  
  public void write(String paramString, Writer paramWriter)
  {
    write(paramString, paramWriter, true);
  }
  
  public void write(final String paramString, final Writer paramWriter, final boolean paramBoolean)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("empty file path");
    }
    try
    {
      int i = this.mWriteSequence + 1;
      this.mWriteSequence = i;
      if (i == 1)
      {
        this.mDiskWriteHandlerThread = new HandlerThread("DelayedDiskWriteThread");
        this.mDiskWriteHandlerThread.start();
        this.mDiskWriteHandler = new Handler(this.mDiskWriteHandlerThread.getLooper());
      }
      this.mDiskWriteHandler.post(new Runnable()
      {
        public void run()
        {
          DelayedDiskWrite.-wrap0(DelayedDiskWrite.this, paramString, paramWriter, paramBoolean);
        }
      });
      return;
    }
    finally {}
  }
  
  public static abstract interface Writer
  {
    public abstract void onWriteCalled(DataOutputStream paramDataOutputStream)
      throws IOException;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/net/DelayedDiskWrite.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */