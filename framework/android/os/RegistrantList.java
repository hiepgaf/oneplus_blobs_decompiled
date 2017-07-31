package android.os;

import java.util.ArrayList;

public class RegistrantList
{
  ArrayList registrants = new ArrayList();
  
  private void internalNotifyRegistrants(Object paramObject, Throwable paramThrowable)
  {
    int i = 0;
    try
    {
      int j = this.registrants.size();
      while (i < j)
      {
        ((Registrant)this.registrants.get(i)).internalNotifyRegistrant(paramObject, paramThrowable);
        i += 1;
      }
      return;
    }
    finally {}
  }
  
  public void add(Handler paramHandler, int paramInt, Object paramObject)
  {
    try
    {
      add(new Registrant(paramHandler, paramInt, paramObject));
      return;
    }
    finally
    {
      paramHandler = finally;
      throw paramHandler;
    }
  }
  
  public void add(Registrant paramRegistrant)
  {
    try
    {
      removeCleared();
      this.registrants.add(paramRegistrant);
      return;
    }
    finally
    {
      paramRegistrant = finally;
      throw paramRegistrant;
    }
  }
  
  public void addUnique(Handler paramHandler, int paramInt, Object paramObject)
  {
    try
    {
      remove(paramHandler);
      add(new Registrant(paramHandler, paramInt, paramObject));
      return;
    }
    finally
    {
      paramHandler = finally;
      throw paramHandler;
    }
  }
  
  public Object get(int paramInt)
  {
    try
    {
      Object localObject1 = this.registrants.get(paramInt);
      return localObject1;
    }
    finally
    {
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  public void notifyException(Throwable paramThrowable)
  {
    internalNotifyRegistrants(null, paramThrowable);
  }
  
  public void notifyRegistrants()
  {
    internalNotifyRegistrants(null, null);
  }
  
  public void notifyRegistrants(AsyncResult paramAsyncResult)
  {
    internalNotifyRegistrants(paramAsyncResult.result, paramAsyncResult.exception);
  }
  
  public void notifyResult(Object paramObject)
  {
    internalNotifyRegistrants(paramObject, null);
  }
  
  public void remove(Handler paramHandler)
  {
    int i = 0;
    for (;;)
    {
      try
      {
        int j = this.registrants.size();
        if (i < j)
        {
          Registrant localRegistrant = (Registrant)this.registrants.get(i);
          Handler localHandler = localRegistrant.getHandler();
          if ((localHandler == null) || (localHandler == paramHandler)) {
            localRegistrant.clear();
          }
        }
        else
        {
          removeCleared();
          return;
        }
      }
      finally {}
      i += 1;
    }
  }
  
  public void removeCleared()
  {
    try
    {
      int i = this.registrants.size() - 1;
      while (i >= 0)
      {
        if (((Registrant)this.registrants.get(i)).refH == null) {
          this.registrants.remove(i);
        }
        i -= 1;
      }
      return;
    }
    finally {}
  }
  
  public int size()
  {
    try
    {
      int i = this.registrants.size();
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/RegistrantList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */