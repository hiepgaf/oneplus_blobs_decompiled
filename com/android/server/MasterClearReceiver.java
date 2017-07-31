package com.android.server;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.RecoverySystem;
import android.os.storage.StorageManager;
import android.util.Log;
import android.util.Slog;
import android.view.Window;
import java.io.IOException;

public class MasterClearReceiver
  extends BroadcastReceiver
{
  private static final String TAG = "MasterClear";
  
  public void onReceive(final Context paramContext, Intent paramIntent)
  {
    if ((paramIntent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) && (!"google.com".equals(paramIntent.getStringExtra("from"))))
    {
      Slog.w("MasterClear", "Ignoring master clear request -- not from trusted server.");
      return;
    }
    final boolean bool1 = paramIntent.getBooleanExtra("shutdown", false);
    final String str = paramIntent.getStringExtra("android.intent.extra.REASON");
    boolean bool2 = paramIntent.getBooleanExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", false);
    final boolean bool3 = paramIntent.getBooleanExtra("android.intent.extra.FORCE_MASTER_CLEAR", false);
    Slog.w("MasterClear", "!!! FACTORY RESET !!!");
    paramIntent = new Thread("Reboot")
    {
      public void run()
      {
        try
        {
          RecoverySystem.rebootWipeUserData(paramContext, bool1, str, bool3);
          Log.wtf("MasterClear", "Still running after master clear?!");
          return;
        }
        catch (SecurityException localSecurityException)
        {
          Slog.e("MasterClear", "Can't perform master clear/factory reset", localSecurityException);
          return;
        }
        catch (IOException localIOException)
        {
          Slog.e("MasterClear", "Can't perform master clear/factory reset", localIOException);
        }
      }
    };
    if (bool2)
    {
      new WipeAdoptableDisksTask(paramContext, paramIntent).execute(new Void[0]);
      return;
    }
    paramIntent.start();
  }
  
  private class WipeAdoptableDisksTask
    extends AsyncTask<Void, Void, Void>
  {
    private final Thread mChainedTask;
    private final Context mContext;
    private final ProgressDialog mProgressDialog;
    
    public WipeAdoptableDisksTask(Context paramContext, Thread paramThread)
    {
      this.mContext = paramContext;
      this.mChainedTask = paramThread;
      this.mProgressDialog = new ProgressDialog(paramContext);
    }
    
    protected Void doInBackground(Void... paramVarArgs)
    {
      Slog.w("MasterClear", "Wiping adoptable disks");
      ((StorageManager)this.mContext.getSystemService("storage")).wipeAdoptableDisks();
      return null;
    }
    
    protected void onPostExecute(Void paramVoid)
    {
      this.mProgressDialog.dismiss();
      this.mChainedTask.start();
    }
    
    protected void onPreExecute()
    {
      this.mProgressDialog.setIndeterminate(true);
      this.mProgressDialog.getWindow().setType(2003);
      this.mProgressDialog.setMessage(this.mContext.getText(17040548));
      this.mProgressDialog.show();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/MasterClearReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */