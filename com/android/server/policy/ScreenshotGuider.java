package com.android.server.policy;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ScreenshotGuider
{
  private Context mContext;
  private Button mGuideButton;
  private LayoutInflater mInflater;
  private int mRotation = 0;
  private RelativeLayout mScreenshotGuideLayout;
  private WindowManager mWindowManager;
  
  public ScreenshotGuider(Context paramContext)
  {
    this.mContext = paramContext;
    this.mWindowManager = ((WindowManager)this.mContext.getSystemService("window"));
    this.mInflater = ((LayoutInflater)this.mContext.getSystemService("layout_inflater"));
  }
  
  private void initView(ViewGroup paramViewGroup)
  {
    this.mGuideButton = ((Button)paramViewGroup.findViewById(84672525));
    this.mGuideButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (ScreenshotGuider.-get0(ScreenshotGuider.this) != null) {}
        try
        {
          ScreenshotGuider.-get1(ScreenshotGuider.this).removeView(ScreenshotGuider.-get0(ScreenshotGuider.this));
          ScreenshotGuider.-set0(ScreenshotGuider.this, null);
          return;
        }
        catch (Exception paramAnonymousView)
        {
          for (;;) {}
        }
      }
    });
  }
  
  protected void hide()
  {
    if (this.mScreenshotGuideLayout != null) {}
    try
    {
      this.mWindowManager.removeView(this.mScreenshotGuideLayout);
      this.mScreenshotGuideLayout = null;
      return;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  protected void show()
  {
    for (;;)
    {
      Object localObject1;
      try
      {
        localObject1 = this.mScreenshotGuideLayout;
        if (localObject1 != null) {
          return;
        }
        localObject1 = new WindowManager.LayoutParams();
        ((WindowManager.LayoutParams)localObject1).type = 2009;
        ((WindowManager.LayoutParams)localObject1).setTitle("ScreenShotGuider");
        ((WindowManager.LayoutParams)localObject1).flags = 66816;
        ((WindowManager.LayoutParams)localObject1).format = -3;
        ((WindowManager.LayoutParams)localObject1).width = -1;
        ((WindowManager.LayoutParams)localObject1).height = -1;
        ((WindowManager.LayoutParams)localObject1).gravity = 17;
        this.mRotation = this.mWindowManager.getDefaultDisplay().getRotation();
        switch (this.mRotation)
        {
        case 2: 
          initView(this.mScreenshotGuideLayout);
          this.mWindowManager.addView(this.mScreenshotGuideLayout, (ViewGroup.LayoutParams)localObject1);
          return;
        }
      }
      finally {}
      ((WindowManager.LayoutParams)localObject1).screenOrientation = 1;
      this.mScreenshotGuideLayout = ((RelativeLayout)this.mInflater.inflate(84082695, null));
      continue;
      ((WindowManager.LayoutParams)localObject2).screenOrientation = 0;
      this.mScreenshotGuideLayout = ((RelativeLayout)this.mInflater.inflate(84082696, null));
      continue;
      ((WindowManager.LayoutParams)localObject2).screenOrientation = 8;
      this.mScreenshotGuideLayout = ((RelativeLayout)this.mInflater.inflate(84082696, null));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/ScreenshotGuider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */