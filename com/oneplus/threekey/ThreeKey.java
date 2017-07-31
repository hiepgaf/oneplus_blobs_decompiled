package com.oneplus.threekey;

import android.content.Context;
import android.os.RemoteException;
import android.util.Slog;
import com.oem.os.IThreeKeyPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThreeKey
  extends ThreeKeyBase
{
  private static final boolean DEBUG = true;
  private static final String TAG = "ThreeKey";
  private List<IThreeKeyPolicy> mPolicys = new ArrayList();
  
  public ThreeKey(Context paramContext)
  {
    super(paramContext);
  }
  
  public void addThreeKeyPolicy(IThreeKeyPolicy paramIThreeKeyPolicy)
  {
    Slog.d("ThreeKey", "[addThreeKeyPolicy]" + paramIThreeKeyPolicy);
    if (paramIThreeKeyPolicy == null) {
      return;
    }
    this.mPolicys.add(paramIThreeKeyPolicy);
  }
  
  public void init(int paramInt)
  {
    Iterator localIterator = this.mPolicys.iterator();
    while (localIterator.hasNext())
    {
      IThreeKeyPolicy localIThreeKeyPolicy1 = (IThreeKeyPolicy)localIterator.next();
      try
      {
        localIThreeKeyPolicy1.setInitMode(true);
      }
      catch (RemoteException localRemoteException1) {}
    }
    super.init(paramInt);
    localIterator = this.mPolicys.iterator();
    while (localIterator.hasNext())
    {
      IThreeKeyPolicy localIThreeKeyPolicy2 = (IThreeKeyPolicy)localIterator.next();
      try
      {
        localIThreeKeyPolicy2.setInitMode(false);
      }
      catch (RemoteException localRemoteException2) {}
    }
  }
  
  public void removeThreeKeyPolicy(IThreeKeyPolicy paramIThreeKeyPolicy)
  {
    this.mPolicys.remove(paramIThreeKeyPolicy);
  }
  
  protected void setDown()
  {
    Iterator localIterator = this.mPolicys.iterator();
    while (localIterator.hasNext())
    {
      IThreeKeyPolicy localIThreeKeyPolicy = (IThreeKeyPolicy)localIterator.next();
      try
      {
        localIThreeKeyPolicy.setDown();
      }
      catch (RemoteException localRemoteException) {}
    }
  }
  
  protected void setMiddle()
  {
    Iterator localIterator = this.mPolicys.iterator();
    while (localIterator.hasNext())
    {
      IThreeKeyPolicy localIThreeKeyPolicy = (IThreeKeyPolicy)localIterator.next();
      try
      {
        localIThreeKeyPolicy.setMiddle();
      }
      catch (RemoteException localRemoteException) {}
    }
  }
  
  protected void setUp()
  {
    Iterator localIterator = this.mPolicys.iterator();
    while (localIterator.hasNext())
    {
      IThreeKeyPolicy localIThreeKeyPolicy = (IThreeKeyPolicy)localIterator.next();
      try
      {
        localIThreeKeyPolicy.setUp();
      }
      catch (RemoteException localRemoteException) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/threekey/ThreeKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */