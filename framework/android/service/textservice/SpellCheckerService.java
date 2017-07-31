package android.service.textservice;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.method.WordIterator;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import com.android.internal.textservice.ISpellCheckerService.Stub;
import com.android.internal.textservice.ISpellCheckerSession;
import com.android.internal.textservice.ISpellCheckerSession.Stub;
import com.android.internal.textservice.ISpellCheckerSessionListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

public abstract class SpellCheckerService
  extends Service
{
  private static final boolean DBG = false;
  public static final String SERVICE_INTERFACE = "android.service.textservice.SpellCheckerService";
  private static final String TAG = SpellCheckerService.class.getSimpleName();
  private final SpellCheckerServiceBinder mBinder = new SpellCheckerServiceBinder(this);
  
  public abstract Session createSession();
  
  public final IBinder onBind(Intent paramIntent)
  {
    return this.mBinder;
  }
  
  private static class InternalISpellCheckerSession
    extends ISpellCheckerSession.Stub
  {
    private final Bundle mBundle;
    private ISpellCheckerSessionListener mListener;
    private final String mLocale;
    private final SpellCheckerService.Session mSession;
    
    public InternalISpellCheckerSession(String paramString, ISpellCheckerSessionListener paramISpellCheckerSessionListener, Bundle paramBundle, SpellCheckerService.Session paramSession)
    {
      this.mListener = paramISpellCheckerSessionListener;
      this.mSession = paramSession;
      this.mLocale = paramString;
      this.mBundle = paramBundle;
      paramSession.setInternalISpellCheckerSession(this);
    }
    
    public Bundle getBundle()
    {
      return this.mBundle;
    }
    
    public String getLocale()
    {
      return this.mLocale;
    }
    
    public void onCancel()
    {
      int i = Process.getThreadPriority(Process.myTid());
      try
      {
        Process.setThreadPriority(10);
        this.mSession.onCancel();
        return;
      }
      finally
      {
        Process.setThreadPriority(i);
      }
    }
    
    public void onClose()
    {
      int i = Process.getThreadPriority(Process.myTid());
      try
      {
        Process.setThreadPriority(10);
        this.mSession.onClose();
        return;
      }
      finally
      {
        Process.setThreadPriority(i);
        this.mListener = null;
      }
    }
    
    public void onGetSentenceSuggestionsMultiple(TextInfo[] paramArrayOfTextInfo, int paramInt)
    {
      try
      {
        this.mListener.onGetSentenceSuggestions(this.mSession.onGetSentenceSuggestionsMultiple(paramArrayOfTextInfo, paramInt));
        return;
      }
      catch (RemoteException paramArrayOfTextInfo) {}
    }
    
    public void onGetSuggestionsMultiple(TextInfo[] paramArrayOfTextInfo, int paramInt, boolean paramBoolean)
    {
      int i = Process.getThreadPriority(Process.myTid());
      try
      {
        Process.setThreadPriority(10);
        this.mListener.onGetSuggestions(this.mSession.onGetSuggestionsMultiple(paramArrayOfTextInfo, paramInt, paramBoolean));
        Process.setThreadPriority(i);
        return;
      }
      catch (RemoteException paramArrayOfTextInfo)
      {
        paramArrayOfTextInfo = paramArrayOfTextInfo;
        Process.setThreadPriority(i);
        return;
      }
      finally
      {
        paramArrayOfTextInfo = finally;
        Process.setThreadPriority(i);
        throw paramArrayOfTextInfo;
      }
    }
  }
  
  private static class SentenceLevelAdapter
  {
    public static final SentenceSuggestionsInfo[] EMPTY_SENTENCE_SUGGESTIONS_INFOS = new SentenceSuggestionsInfo[0];
    private static final SuggestionsInfo EMPTY_SUGGESTIONS_INFO = new SuggestionsInfo(0, null);
    private final WordIterator mWordIterator;
    
    public SentenceLevelAdapter(Locale paramLocale)
    {
      this.mWordIterator = new WordIterator(paramLocale);
    }
    
    private SentenceTextInfoParams getSplitWords(TextInfo paramTextInfo)
    {
      WordIterator localWordIterator = this.mWordIterator;
      String str = paramTextInfo.getText();
      int k = paramTextInfo.getCookie();
      int m = str.length();
      ArrayList localArrayList = new ArrayList();
      localWordIterator.setCharSequence(str, 0, str.length());
      int i = localWordIterator.following(0);
      for (int j = localWordIterator.getBeginning(i);; j = localWordIterator.getBeginning(i)) {
        if ((j <= m) && (i != -1) && (j != -1))
        {
          if ((i >= 0) && (i > j))
          {
            CharSequence localCharSequence = str.subSequence(j, i);
            localArrayList.add(new SentenceWordItem(new TextInfo(localCharSequence, 0, localCharSequence.length(), k, localCharSequence.hashCode()), j, i));
          }
          i = localWordIterator.following(i);
          if (i != -1) {}
        }
        else
        {
          return new SentenceTextInfoParams(paramTextInfo, localArrayList);
        }
      }
    }
    
    public static SentenceSuggestionsInfo reconstructSuggestions(SentenceTextInfoParams paramSentenceTextInfoParams, SuggestionsInfo[] paramArrayOfSuggestionsInfo)
    {
      if ((paramArrayOfSuggestionsInfo == null) || (paramArrayOfSuggestionsInfo.length == 0)) {
        return null;
      }
      if (paramSentenceTextInfoParams == null) {
        return null;
      }
      int k = paramSentenceTextInfoParams.mOriginalTextInfo.getCookie();
      int m = paramSentenceTextInfoParams.mOriginalTextInfo.getSequence();
      int n = paramSentenceTextInfoParams.mSize;
      int[] arrayOfInt1 = new int[n];
      int[] arrayOfInt2 = new int[n];
      SuggestionsInfo[] arrayOfSuggestionsInfo = new SuggestionsInfo[n];
      int i = 0;
      if (i < n)
      {
        SentenceWordItem localSentenceWordItem = (SentenceWordItem)paramSentenceTextInfoParams.mItems.get(i);
        Object localObject2 = null;
        int j = 0;
        label86:
        Object localObject1 = localObject2;
        if (j < paramArrayOfSuggestionsInfo.length)
        {
          SuggestionsInfo localSuggestionsInfo = paramArrayOfSuggestionsInfo[j];
          if ((localSuggestionsInfo != null) && (localSuggestionsInfo.getSequence() == localSentenceWordItem.mTextInfo.getSequence()))
          {
            localObject1 = localSuggestionsInfo;
            localSuggestionsInfo.setCookieAndSequence(k, m);
          }
        }
        else
        {
          arrayOfInt1[i] = localSentenceWordItem.mStart;
          arrayOfInt2[i] = localSentenceWordItem.mLength;
          if (localObject1 == null) {
            break label178;
          }
        }
        for (;;)
        {
          arrayOfSuggestionsInfo[i] = localObject1;
          i += 1;
          break;
          j += 1;
          break label86;
          label178:
          localObject1 = EMPTY_SUGGESTIONS_INFO;
        }
      }
      return new SentenceSuggestionsInfo(arrayOfSuggestionsInfo, arrayOfInt1, arrayOfInt2);
    }
    
    public static class SentenceTextInfoParams
    {
      final ArrayList<SpellCheckerService.SentenceLevelAdapter.SentenceWordItem> mItems;
      final TextInfo mOriginalTextInfo;
      final int mSize;
      
      public SentenceTextInfoParams(TextInfo paramTextInfo, ArrayList<SpellCheckerService.SentenceLevelAdapter.SentenceWordItem> paramArrayList)
      {
        this.mOriginalTextInfo = paramTextInfo;
        this.mItems = paramArrayList;
        this.mSize = paramArrayList.size();
      }
    }
    
    public static class SentenceWordItem
    {
      public final int mLength;
      public final int mStart;
      public final TextInfo mTextInfo;
      
      public SentenceWordItem(TextInfo paramTextInfo, int paramInt1, int paramInt2)
      {
        this.mTextInfo = paramTextInfo;
        this.mStart = paramInt1;
        this.mLength = (paramInt2 - paramInt1);
      }
    }
  }
  
  public static abstract class Session
  {
    private SpellCheckerService.InternalISpellCheckerSession mInternalSession;
    private volatile SpellCheckerService.SentenceLevelAdapter mSentenceLevelAdapter;
    
    public Bundle getBundle()
    {
      return this.mInternalSession.getBundle();
    }
    
    public String getLocale()
    {
      return this.mInternalSession.getLocale();
    }
    
    public void onCancel() {}
    
    public void onClose() {}
    
    public abstract void onCreate();
    
    public SentenceSuggestionsInfo[] onGetSentenceSuggestionsMultiple(TextInfo[] paramArrayOfTextInfo, int paramInt)
    {
      if ((paramArrayOfTextInfo == null) || (paramArrayOfTextInfo.length == 0)) {
        return SpellCheckerService.SentenceLevelAdapter.EMPTY_SENTENCE_SUGGESTIONS_INFOS;
      }
      if (this.mSentenceLevelAdapter == null) {}
      try
      {
        if (this.mSentenceLevelAdapter == null)
        {
          localObject = getLocale();
          if (!TextUtils.isEmpty((CharSequence)localObject)) {
            this.mSentenceLevelAdapter = new SpellCheckerService.SentenceLevelAdapter(new Locale((String)localObject));
          }
        }
        if (this.mSentenceLevelAdapter == null) {
          return SpellCheckerService.SentenceLevelAdapter.EMPTY_SENTENCE_SUGGESTIONS_INFOS;
        }
      }
      finally {}
      int k = paramArrayOfTextInfo.length;
      Object localObject = new SentenceSuggestionsInfo[k];
      int i = 0;
      while (i < k)
      {
        SpellCheckerService.SentenceLevelAdapter.SentenceTextInfoParams localSentenceTextInfoParams = SpellCheckerService.SentenceLevelAdapter.-wrap0(this.mSentenceLevelAdapter, paramArrayOfTextInfo[i]);
        ArrayList localArrayList = localSentenceTextInfoParams.mItems;
        int m = localArrayList.size();
        TextInfo[] arrayOfTextInfo = new TextInfo[m];
        int j = 0;
        while (j < m)
        {
          arrayOfTextInfo[j] = ((SpellCheckerService.SentenceLevelAdapter.SentenceWordItem)localArrayList.get(j)).mTextInfo;
          j += 1;
        }
        localObject[i] = SpellCheckerService.SentenceLevelAdapter.reconstructSuggestions(localSentenceTextInfoParams, onGetSuggestionsMultiple(arrayOfTextInfo, paramInt, true));
        i += 1;
      }
      return (SentenceSuggestionsInfo[])localObject;
    }
    
    public abstract SuggestionsInfo onGetSuggestions(TextInfo paramTextInfo, int paramInt);
    
    public SuggestionsInfo[] onGetSuggestionsMultiple(TextInfo[] paramArrayOfTextInfo, int paramInt, boolean paramBoolean)
    {
      int j = paramArrayOfTextInfo.length;
      SuggestionsInfo[] arrayOfSuggestionsInfo = new SuggestionsInfo[j];
      int i = 0;
      while (i < j)
      {
        arrayOfSuggestionsInfo[i] = onGetSuggestions(paramArrayOfTextInfo[i], paramInt);
        arrayOfSuggestionsInfo[i].setCookieAndSequence(paramArrayOfTextInfo[i].getCookie(), paramArrayOfTextInfo[i].getSequence());
        i += 1;
      }
      return arrayOfSuggestionsInfo;
    }
    
    public final void setInternalISpellCheckerSession(SpellCheckerService.InternalISpellCheckerSession paramInternalISpellCheckerSession)
    {
      this.mInternalSession = paramInternalISpellCheckerSession;
    }
  }
  
  private static class SpellCheckerServiceBinder
    extends ISpellCheckerService.Stub
  {
    private final WeakReference<SpellCheckerService> mInternalServiceRef;
    
    public SpellCheckerServiceBinder(SpellCheckerService paramSpellCheckerService)
    {
      this.mInternalServiceRef = new WeakReference(paramSpellCheckerService);
    }
    
    public ISpellCheckerSession getISpellCheckerSession(String paramString, ISpellCheckerSessionListener paramISpellCheckerSessionListener, Bundle paramBundle)
    {
      Object localObject = (SpellCheckerService)this.mInternalServiceRef.get();
      if (localObject == null) {
        return null;
      }
      localObject = ((SpellCheckerService)localObject).createSession();
      paramString = new SpellCheckerService.InternalISpellCheckerSession(paramString, paramISpellCheckerSessionListener, paramBundle, (SpellCheckerService.Session)localObject);
      ((SpellCheckerService.Session)localObject).onCreate();
      return paramString;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/textservice/SpellCheckerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */