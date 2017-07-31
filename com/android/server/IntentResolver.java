package com.android.server;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.AuthorityEntry;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.FastImmutableArraySet;
import android.util.LogPrinter;
import android.util.MutableInt;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import com.android.internal.util.FastPrintWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class IntentResolver<F extends IntentFilter, R>
{
  private static final boolean DEBUG = false;
  private static final String TAG = "IntentResolver";
  private static final boolean localLOGV = false;
  private static final boolean localVerificationLOGV = false;
  private static final Comparator mResolvePrioritySorter = new Comparator()
  {
    public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
    {
      int i = ((IntentFilter)paramAnonymousObject1).getPriority();
      int j = ((IntentFilter)paramAnonymousObject2).getPriority();
      if (i > j) {
        return -1;
      }
      if (i < j) {
        return 1;
      }
      return 0;
    }
  };
  private final ArrayMap<String, F[]> mActionToFilter = new ArrayMap();
  private final ArrayMap<String, F[]> mBaseTypeToFilter = new ArrayMap();
  private final ArraySet<F> mFilters = new ArraySet();
  private final ArrayMap<String, F[]> mSchemeToFilter = new ArrayMap();
  private final ArrayMap<String, F[]> mTypeToFilter = new ArrayMap();
  private final ArrayMap<String, F[]> mTypedActionToFilter = new ArrayMap();
  private final ArrayMap<String, F[]> mWildTypeToFilter = new ArrayMap();
  
  private final void addFilter(ArrayMap<String, F[]> paramArrayMap, String paramString, F paramF)
  {
    IntentFilter[] arrayOfIntentFilter1 = (IntentFilter[])paramArrayMap.get(paramString);
    if (arrayOfIntentFilter1 == null)
    {
      arrayOfIntentFilter1 = newArray(2);
      paramArrayMap.put(paramString, arrayOfIntentFilter1);
      arrayOfIntentFilter1[0] = paramF;
      return;
    }
    int j = arrayOfIntentFilter1.length;
    int i = j;
    while ((i > 0) && (arrayOfIntentFilter1[(i - 1)] == null)) {
      i -= 1;
    }
    if (i < j)
    {
      arrayOfIntentFilter1[i] = paramF;
      return;
    }
    IntentFilter[] arrayOfIntentFilter2 = newArray(j * 3 / 2);
    System.arraycopy(arrayOfIntentFilter1, 0, arrayOfIntentFilter2, 0, j);
    arrayOfIntentFilter2[j] = paramF;
    paramArrayMap.put(paramString, arrayOfIntentFilter2);
  }
  
  private void buildResolveList(Intent paramIntent, FastImmutableArraySet<String> paramFastImmutableArraySet, boolean paramBoolean1, boolean paramBoolean2, String paramString1, String paramString2, F[] paramArrayOfF, List<R> paramList, int paramInt)
  {
    String str1 = paramIntent.getAction();
    Uri localUri = paramIntent.getData();
    String str2 = paramIntent.getPackage();
    boolean bool = paramIntent.isExcludingStopped();
    LogPrinter localLogPrinter;
    FastPrintWriter localFastPrintWriter;
    int i;
    label62:
    int k;
    int j;
    label68:
    int m;
    if (paramBoolean1)
    {
      localLogPrinter = new LogPrinter(2, "IntentResolver", 3);
      localFastPrintWriter = new FastPrintWriter(localLogPrinter);
      if (paramArrayOfF == null) {
        break label171;
      }
      i = paramArrayOfF.length;
      k = 0;
      j = 0;
      if (j >= i) {
        break label623;
      }
      paramIntent = paramArrayOfF[j];
      if (paramIntent == null) {
        break label623;
      }
      if (paramBoolean1) {
        Slog.v("IntentResolver", "Matching against filter " + paramIntent);
      }
      if ((!bool) || (!isFilterStopped(paramIntent, paramInt))) {
        break label177;
      }
      m = k;
      if (paramBoolean1)
      {
        Slog.v("IntentResolver", "  Filter's target is stopped; skipping");
        m = k;
      }
    }
    label171:
    label177:
    int n;
    label525:
    do
    {
      for (;;)
      {
        j += 1;
        k = m;
        break label68;
        localLogPrinter = null;
        localFastPrintWriter = null;
        break;
        i = 0;
        break label62;
        if ((str2 == null) || (isPackageForFilter(str2, paramIntent)))
        {
          if ((paramIntent.getAutoVerify()) && (paramBoolean1))
          {
            Slog.v("IntentResolver", "  Filter verified: " + isFilterVerified(paramIntent));
            n = paramIntent.countDataAuthorities();
            m = 0;
          }
        }
        else {
          for (;;)
          {
            if (m < n)
            {
              Slog.v("IntentResolver", "   " + paramIntent.getDataAuthority(m).getHost());
              m += 1;
              continue;
              m = k;
              if (!paramBoolean1) {
                break;
              }
              Slog.v("IntentResolver", "  Filter is not from package " + str2 + "; skipping");
              m = k;
              break;
            }
          }
        }
        if (!allowFilterResult(paramIntent, paramList))
        {
          m = k;
          if (paramBoolean1)
          {
            Slog.v("IntentResolver", "  Filter's target already added");
            m = k;
          }
        }
        else
        {
          n = paramIntent.match(str1, paramString1, paramString2, localUri, paramFastImmutableArraySet, "IntentResolver");
          if (n < 0) {
            break label525;
          }
          if (paramBoolean1) {
            Slog.v("IntentResolver", "  Filter matched!  match=0x" + Integer.toHexString(n) + " hasDefault=" + paramIntent.hasCategory("android.intent.category.DEFAULT"));
          }
          if ((!paramBoolean2) || (paramIntent.hasCategory("android.intent.category.DEFAULT")))
          {
            Object localObject = newResult(paramIntent, n, paramInt);
            m = k;
            if (localObject != null)
            {
              paramList.add(localObject);
              m = k;
              if (paramBoolean1)
              {
                dumpFilter(localFastPrintWriter, "    ", paramIntent);
                localFastPrintWriter.flush();
                paramIntent.dump(localLogPrinter, "    ");
                m = k;
              }
            }
          }
          else
          {
            m = 1;
          }
        }
      }
      m = k;
    } while (!paramBoolean1);
    switch (n)
    {
    default: 
      paramIntent = "unknown reason";
    }
    for (;;)
    {
      Slog.v("IntentResolver", "  Filter did not match: " + paramIntent);
      m = k;
      break;
      paramIntent = "action";
      continue;
      paramIntent = "category";
      continue;
      paramIntent = "data";
      continue;
      paramIntent = "type";
    }
    label623:
    if ((paramBoolean1) && (k != 0))
    {
      if (paramList.size() != 0) {
        break label651;
      }
      Slog.v("IntentResolver", "resolveIntent failed: found match, but none with CATEGORY_DEFAULT");
    }
    label651:
    while (paramList.size() <= 1) {
      return;
    }
    Slog.v("IntentResolver", "resolveIntent: multiple matches, only some with CATEGORY_DEFAULT");
  }
  
  private ArrayList<F> collectFilters(F[] paramArrayOfF, IntentFilter paramIntentFilter)
  {
    Object localObject2 = null;
    Object localObject1 = null;
    int i;
    if (paramArrayOfF != null) {
      i = 0;
    }
    for (;;)
    {
      localObject2 = localObject1;
      F ?;
      if (i < paramArrayOfF.length)
      {
        ? = paramArrayOfF[i];
        if (? == null) {
          localObject2 = localObject1;
        }
      }
      else
      {
        return (ArrayList<F>)localObject2;
      }
      localObject2 = localObject1;
      if (filterEquals(?, paramIntentFilter))
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new ArrayList();
        }
        ((ArrayList)localObject2).add(?);
      }
      i += 1;
      localObject1 = localObject2;
    }
  }
  
  private boolean filterEquals(IntentFilter paramIntentFilter1, IntentFilter paramIntentFilter2)
  {
    int j = paramIntentFilter1.countActions();
    if (j != paramIntentFilter2.countActions()) {
      return false;
    }
    int i = 0;
    while (i < j)
    {
      if (!paramIntentFilter2.hasAction(paramIntentFilter1.getAction(i))) {
        return false;
      }
      i += 1;
    }
    j = paramIntentFilter1.countCategories();
    if (j != paramIntentFilter2.countCategories()) {
      return false;
    }
    i = 0;
    while (i < j)
    {
      if (!paramIntentFilter2.hasCategory(paramIntentFilter1.getCategory(i))) {
        return false;
      }
      i += 1;
    }
    j = paramIntentFilter1.countDataTypes();
    if (j != paramIntentFilter2.countDataTypes()) {
      return false;
    }
    i = 0;
    while (i < j)
    {
      if (!paramIntentFilter2.hasExactDataType(paramIntentFilter1.getDataType(i))) {
        return false;
      }
      i += 1;
    }
    j = paramIntentFilter1.countDataSchemes();
    if (j != paramIntentFilter2.countDataSchemes()) {
      return false;
    }
    i = 0;
    while (i < j)
    {
      if (!paramIntentFilter2.hasDataScheme(paramIntentFilter1.getDataScheme(i))) {
        return false;
      }
      i += 1;
    }
    j = paramIntentFilter1.countDataAuthorities();
    if (j != paramIntentFilter2.countDataAuthorities()) {
      return false;
    }
    i = 0;
    while (i < j)
    {
      if (!paramIntentFilter2.hasDataAuthority(paramIntentFilter1.getDataAuthority(i))) {
        return false;
      }
      i += 1;
    }
    j = paramIntentFilter1.countDataPaths();
    if (j != paramIntentFilter2.countDataPaths()) {
      return false;
    }
    i = 0;
    while (i < j)
    {
      if (!paramIntentFilter2.hasDataPath(paramIntentFilter1.getDataPath(i))) {
        return false;
      }
      i += 1;
    }
    j = paramIntentFilter1.countDataSchemeSpecificParts();
    if (j != paramIntentFilter2.countDataSchemeSpecificParts()) {
      return false;
    }
    i = 0;
    while (i < j)
    {
      if (!paramIntentFilter2.hasDataSchemeSpecificPart(paramIntentFilter1.getDataSchemeSpecificPart(i))) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private static FastImmutableArraySet<String> getFastIntentCategories(Intent paramIntent)
  {
    paramIntent = paramIntent.getCategories();
    if (paramIntent == null) {
      return null;
    }
    return new FastImmutableArraySet((String[])paramIntent.toArray(new String[paramIntent.size()]));
  }
  
  private final int register_intent_filter(F paramF, Iterator<String> paramIterator, ArrayMap<String, F[]> paramArrayMap, String paramString)
  {
    if (paramIterator == null) {
      return 0;
    }
    int i = 0;
    while (paramIterator.hasNext())
    {
      paramString = (String)paramIterator.next();
      i += 1;
      addFilter(paramArrayMap, paramString, paramF);
    }
    return i;
  }
  
  private final int register_mime_types(F paramF, String paramString)
  {
    Iterator localIterator = paramF.typesIterator();
    if (localIterator == null) {
      return 0;
    }
    int i = 0;
    while (localIterator.hasNext())
    {
      paramString = (String)localIterator.next();
      i += 1;
      String str = paramString;
      int j = paramString.indexOf('/');
      if (j > 0) {
        str = paramString.substring(0, j).intern();
      }
      for (;;)
      {
        addFilter(this.mTypeToFilter, paramString, paramF);
        if (j <= 0) {
          break label121;
        }
        addFilter(this.mBaseTypeToFilter, str, paramF);
        break;
        paramString = paramString + "/*";
      }
      label121:
      addFilter(this.mWildTypeToFilter, str, paramF);
    }
    return i;
  }
  
  private final void remove_all_objects(ArrayMap<String, F[]> paramArrayMap, String paramString, Object paramObject)
  {
    IntentFilter[] arrayOfIntentFilter = (IntentFilter[])paramArrayMap.get(paramString);
    int i;
    if (arrayOfIntentFilter != null)
    {
      i = arrayOfIntentFilter.length - 1;
      while ((i >= 0) && (arrayOfIntentFilter[i] == null)) {
        i -= 1;
      }
      int j = i;
      while (j >= 0)
      {
        int k = i;
        if (arrayOfIntentFilter[j] == paramObject)
        {
          k = i - j;
          if (k > 0) {
            System.arraycopy(arrayOfIntentFilter, j + 1, arrayOfIntentFilter, j, k);
          }
          arrayOfIntentFilter[i] = null;
          k = i - 1;
        }
        j -= 1;
        i = k;
      }
      if (i >= 0) {
        break label130;
      }
      paramArrayMap.remove(paramString);
    }
    label130:
    while (i >= arrayOfIntentFilter.length / 2) {
      return;
    }
    paramObject = newArray(i + 2);
    System.arraycopy(arrayOfIntentFilter, 0, paramObject, 0, i + 1);
    paramArrayMap.put(paramString, paramObject);
  }
  
  private final int unregister_intent_filter(F paramF, Iterator<String> paramIterator, ArrayMap<String, F[]> paramArrayMap, String paramString)
  {
    if (paramIterator == null) {
      return 0;
    }
    int i = 0;
    while (paramIterator.hasNext())
    {
      paramString = (String)paramIterator.next();
      i += 1;
      remove_all_objects(paramArrayMap, paramString, paramF);
    }
    return i;
  }
  
  private final int unregister_mime_types(F paramF, String paramString)
  {
    Iterator localIterator = paramF.typesIterator();
    if (localIterator == null) {
      return 0;
    }
    int i = 0;
    while (localIterator.hasNext())
    {
      paramString = (String)localIterator.next();
      i += 1;
      String str = paramString;
      int j = paramString.indexOf('/');
      if (j > 0) {
        str = paramString.substring(0, j).intern();
      }
      for (;;)
      {
        remove_all_objects(this.mTypeToFilter, paramString, paramF);
        if (j <= 0) {
          break label121;
        }
        remove_all_objects(this.mBaseTypeToFilter, str, paramF);
        break;
        paramString = paramString + "/*";
      }
      label121:
      remove_all_objects(this.mWildTypeToFilter, str, paramF);
    }
    return i;
  }
  
  public void addFilter(F paramF)
  {
    this.mFilters.add(paramF);
    int i = register_intent_filter(paramF, paramF.schemesIterator(), this.mSchemeToFilter, "      Scheme: ");
    int j = register_mime_types(paramF, "      Type: ");
    if ((i == 0) && (j == 0)) {
      register_intent_filter(paramF, paramF.actionsIterator(), this.mActionToFilter, "      Action: ");
    }
    if (j != 0) {
      register_intent_filter(paramF, paramF.actionsIterator(), this.mTypedActionToFilter, "      TypedAction: ");
    }
  }
  
  protected boolean allowFilterResult(F paramF, List<R> paramList)
  {
    return true;
  }
  
  public boolean dump(PrintWriter paramPrintWriter, String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2)
  {
    String str2 = paramString2 + "  ";
    String str1 = "\n" + paramString2;
    paramString2 = paramString1 + "\n" + paramString2;
    paramString1 = paramString2;
    if (dumpMap(paramPrintWriter, paramString2, "Full MIME Types:", str2, this.mTypeToFilter, paramString3, paramBoolean1, paramBoolean2)) {
      paramString1 = str1;
    }
    paramString2 = paramString1;
    if (dumpMap(paramPrintWriter, paramString1, "Base MIME Types:", str2, this.mBaseTypeToFilter, paramString3, paramBoolean1, paramBoolean2)) {
      paramString2 = str1;
    }
    paramString1 = paramString2;
    if (dumpMap(paramPrintWriter, paramString2, "Wild MIME Types:", str2, this.mWildTypeToFilter, paramString3, paramBoolean1, paramBoolean2)) {
      paramString1 = str1;
    }
    paramString2 = paramString1;
    if (dumpMap(paramPrintWriter, paramString1, "Schemes:", str2, this.mSchemeToFilter, paramString3, paramBoolean1, paramBoolean2)) {
      paramString2 = str1;
    }
    paramString1 = paramString2;
    if (dumpMap(paramPrintWriter, paramString2, "Non-Data Actions:", str2, this.mActionToFilter, paramString3, paramBoolean1, paramBoolean2)) {
      paramString1 = str1;
    }
    paramString2 = paramString1;
    if (dumpMap(paramPrintWriter, paramString1, "MIME Typed Actions:", str2, this.mTypedActionToFilter, paramString3, paramBoolean1, paramBoolean2)) {
      paramString2 = str1;
    }
    return paramString2 == str1;
  }
  
  protected void dumpFilter(PrintWriter paramPrintWriter, String paramString, F paramF)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.println(paramF);
  }
  
  protected void dumpFilterLabel(PrintWriter paramPrintWriter, String paramString, Object paramObject, int paramInt)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print(paramObject);
    paramPrintWriter.print(": ");
    paramPrintWriter.println(paramInt);
  }
  
  boolean dumpMap(PrintWriter paramPrintWriter, String paramString1, String paramString2, String paramString3, ArrayMap<String, F[]> paramArrayMap, String paramString4, boolean paramBoolean1, boolean paramBoolean2)
  {
    String str1 = paramString3 + "  ";
    String str2 = paramString3 + "    ";
    ArrayMap localArrayMap = new ArrayMap();
    boolean bool1 = false;
    Object localObject1 = null;
    int k = 0;
    while (k < paramArrayMap.size())
    {
      IntentFilter[] arrayOfIntentFilter = (IntentFilter[])paramArrayMap.valueAt(k);
      int i1 = arrayOfIntentFilter.length;
      int j = 0;
      int i = 0;
      int m;
      boolean bool2;
      Object localObject3;
      Object localObject2;
      if ((!paramBoolean2) || (paramBoolean1))
      {
        m = 0;
        paramString3 = paramString2;
        paramString2 = (String)localObject1;
        for (;;)
        {
          bool2 = bool1;
          localObject3 = paramString2;
          localObject2 = paramString3;
          if (m >= i1) {
            break;
          }
          IntentFilter localIntentFilter = arrayOfIntentFilter[m];
          bool2 = bool1;
          localObject3 = paramString2;
          localObject2 = paramString3;
          if (localIntentFilter == null) {
            break;
          }
          int n;
          if (paramString4 != null)
          {
            n = i;
            localObject3 = paramString2;
            localObject2 = paramString3;
            if (!isPackageForFilter(paramString4, localIntentFilter)) {}
          }
          else
          {
            localObject1 = paramString3;
            if (paramString3 != null)
            {
              paramPrintWriter.print(paramString1);
              paramPrintWriter.println(paramString3);
              localObject1 = null;
            }
            j = i;
            if (i == 0)
            {
              paramPrintWriter.print(str1);
              paramPrintWriter.print((String)paramArrayMap.keyAt(k));
              paramPrintWriter.println(":");
              j = 1;
            }
            bool2 = true;
            dumpFilter(paramPrintWriter, str2, localIntentFilter);
            n = j;
            bool1 = bool2;
            localObject3 = paramString2;
            localObject2 = localObject1;
            if (paramBoolean1)
            {
              paramString3 = paramString2;
              if (paramString2 == null) {
                paramString3 = new PrintWriterPrinter(paramPrintWriter);
              }
              localIntentFilter.dump(paramString3, str2 + "  ");
              localObject2 = localObject1;
              localObject3 = paramString3;
              bool1 = bool2;
              n = j;
            }
          }
          m += 1;
          i = n;
          paramString2 = (String)localObject3;
          paramString3 = (String)localObject2;
        }
      }
      localArrayMap.clear();
      i = 0;
      if (i < i1)
      {
        paramString3 = arrayOfIntentFilter[i];
        if (paramString3 != null)
        {
          if ((paramString4 == null) || (isPackageForFilter(paramString4, paramString3)))
          {
            paramString3 = filterToLabel(paramString3);
            m = localArrayMap.indexOfKey(paramString3);
            if (m >= 0) {
              break label448;
            }
            localArrayMap.put(paramString3, new MutableInt(1));
          }
          for (;;)
          {
            i += 1;
            break;
            label448:
            paramString3 = (MutableInt)localArrayMap.valueAt(m);
            paramString3.value += 1;
          }
        }
      }
      i = 0;
      for (;;)
      {
        bool2 = bool1;
        localObject3 = localObject1;
        localObject2 = paramString2;
        if (i >= localArrayMap.size()) {
          break;
        }
        paramString3 = paramString2;
        if (paramString2 != null)
        {
          paramPrintWriter.print(paramString1);
          paramPrintWriter.println(paramString2);
          paramString3 = null;
        }
        m = j;
        if (j == 0)
        {
          paramPrintWriter.print(str1);
          paramPrintWriter.print((String)paramArrayMap.keyAt(k));
          paramPrintWriter.println(":");
          m = 1;
        }
        bool1 = true;
        dumpFilterLabel(paramPrintWriter, str2, localArrayMap.keyAt(i), ((MutableInt)localArrayMap.valueAt(i)).value);
        i += 1;
        j = m;
        paramString2 = paramString3;
      }
      k += 1;
      bool1 = bool2;
      localObject1 = localObject3;
      paramString2 = (String)localObject2;
    }
    return bool1;
  }
  
  public Iterator<F> filterIterator()
  {
    return new IteratorWrapper(this.mFilters.iterator());
  }
  
  protected void filterResults(List<R> paramList) {}
  
  public Set<F> filterSet()
  {
    return Collections.unmodifiableSet(this.mFilters);
  }
  
  protected Object filterToLabel(F paramF)
  {
    return "IntentFilter";
  }
  
  public ArrayList<F> findFilters(IntentFilter paramIntentFilter)
  {
    if (paramIntentFilter.countDataSchemes() == 1) {
      return collectFilters((IntentFilter[])this.mSchemeToFilter.get(paramIntentFilter.getDataScheme(0)), paramIntentFilter);
    }
    if ((paramIntentFilter.countDataTypes() != 0) && (paramIntentFilter.countActions() == 1)) {
      return collectFilters((IntentFilter[])this.mTypedActionToFilter.get(paramIntentFilter.getAction(0)), paramIntentFilter);
    }
    if ((paramIntentFilter.countDataTypes() == 0) && (paramIntentFilter.countDataSchemes() == 0) && (paramIntentFilter.countActions() == 1)) {
      return collectFilters((IntentFilter[])this.mActionToFilter.get(paramIntentFilter.getAction(0)), paramIntentFilter);
    }
    Object localObject1 = null;
    Iterator localIterator = this.mFilters.iterator();
    while (localIterator.hasNext())
    {
      IntentFilter localIntentFilter = (IntentFilter)localIterator.next();
      if (filterEquals(localIntentFilter, paramIntentFilter))
      {
        Object localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new ArrayList();
        }
        ((ArrayList)localObject2).add(localIntentFilter);
        localObject1 = localObject2;
      }
    }
    return (ArrayList<F>)localObject1;
  }
  
  protected boolean isFilterStopped(F paramF, int paramInt)
  {
    return false;
  }
  
  protected boolean isFilterVerified(F paramF)
  {
    return paramF.isVerified();
  }
  
  protected abstract boolean isPackageForFilter(String paramString, F paramF);
  
  protected abstract F[] newArray(int paramInt);
  
  protected R newResult(F paramF, int paramInt1, int paramInt2)
  {
    return paramF;
  }
  
  public List<R> queryIntent(Intent paramIntent, String paramString, boolean paramBoolean, int paramInt)
  {
    String str = paramIntent.getScheme();
    ArrayList localArrayList = new ArrayList();
    boolean bool;
    IntentFilter[] arrayOfIntentFilter;
    Object localObject2;
    Object localObject3;
    Object localObject6;
    Object localObject1;
    Object localObject4;
    Object localObject5;
    if ((paramIntent.getFlags() & 0x8) != 0)
    {
      bool = true;
      if (bool) {
        Slog.v("IntentResolver", "Resolving type=" + paramString + " scheme=" + str + " defaultOnly=" + paramBoolean + " userId=" + paramInt + " of " + paramIntent);
      }
      arrayOfIntentFilter = null;
      localObject2 = null;
      localObject3 = null;
      localObject6 = null;
      localObject1 = arrayOfIntentFilter;
      localObject4 = localObject2;
      localObject5 = localObject3;
      if (paramString != null)
      {
        int i = paramString.indexOf('/');
        localObject1 = arrayOfIntentFilter;
        localObject4 = localObject2;
        localObject5 = localObject3;
        if (i > 0)
        {
          localObject4 = paramString.substring(0, i);
          if (((String)localObject4).equals("*")) {
            break label843;
          }
          if ((paramString.length() == i + 2) && (paramString.charAt(i + 1) == '*')) {
            break label726;
          }
          localObject1 = (IntentFilter[])this.mTypeToFilter.get(paramString);
          if (bool) {
            Slog.v("IntentResolver", "First type cut: " + Arrays.toString((Object[])localObject1));
          }
          localObject4 = (IntentFilter[])this.mWildTypeToFilter.get(localObject4);
          localObject2 = localObject1;
          localObject3 = localObject4;
          if (bool)
          {
            Slog.v("IntentResolver", "Second type cut: " + Arrays.toString((Object[])localObject4));
            localObject3 = localObject4;
            localObject2 = localObject1;
          }
          label311:
          arrayOfIntentFilter = (IntentFilter[])this.mWildTypeToFilter.get("*");
          localObject1 = localObject2;
          localObject4 = localObject3;
          localObject5 = arrayOfIntentFilter;
          if (bool)
          {
            Slog.v("IntentResolver", "Third type cut: " + Arrays.toString(arrayOfIntentFilter));
            localObject5 = arrayOfIntentFilter;
            localObject4 = localObject3;
            localObject1 = localObject2;
          }
        }
      }
    }
    for (;;)
    {
      localObject2 = localObject6;
      if (str != null)
      {
        localObject3 = (IntentFilter[])this.mSchemeToFilter.get(str);
        localObject2 = localObject3;
        if (bool)
        {
          Slog.v("IntentResolver", "Scheme list: " + Arrays.toString((Object[])localObject3));
          localObject2 = localObject3;
        }
      }
      localObject3 = localObject1;
      if (paramString == null)
      {
        localObject3 = localObject1;
        if (str == null)
        {
          localObject3 = localObject1;
          if (paramIntent.getAction() != null)
          {
            localObject1 = (IntentFilter[])this.mActionToFilter.get(paramIntent.getAction());
            localObject3 = localObject1;
            if (bool)
            {
              Slog.v("IntentResolver", "Action list: " + Arrays.toString((Object[])localObject1));
              localObject3 = localObject1;
            }
          }
        }
      }
      localObject1 = getFastIntentCategories(paramIntent);
      if (localObject3 != null) {
        buildResolveList(paramIntent, (FastImmutableArraySet)localObject1, bool, paramBoolean, paramString, str, (IntentFilter[])localObject3, localArrayList, paramInt);
      }
      if (localObject4 != null) {
        buildResolveList(paramIntent, (FastImmutableArraySet)localObject1, bool, paramBoolean, paramString, str, (IntentFilter[])localObject4, localArrayList, paramInt);
      }
      if (localObject5 != null) {
        buildResolveList(paramIntent, (FastImmutableArraySet)localObject1, bool, paramBoolean, paramString, str, (IntentFilter[])localObject5, localArrayList, paramInt);
      }
      if (localObject2 != null) {
        buildResolveList(paramIntent, (FastImmutableArraySet)localObject1, bool, paramBoolean, paramString, str, (IntentFilter[])localObject2, localArrayList, paramInt);
      }
      filterResults(localArrayList);
      sortResults(localArrayList);
      if (!bool) {
        break label940;
      }
      Slog.v("IntentResolver", "Final result list:");
      paramInt = 0;
      while (paramInt < localArrayList.size())
      {
        Slog.v("IntentResolver", "  " + localArrayList.get(paramInt));
        paramInt += 1;
      }
      bool = false;
      break;
      label726:
      localObject1 = (IntentFilter[])this.mBaseTypeToFilter.get(localObject4);
      if (bool) {
        Slog.v("IntentResolver", "First type cut: " + Arrays.toString((Object[])localObject1));
      }
      localObject4 = (IntentFilter[])this.mWildTypeToFilter.get(localObject4);
      localObject2 = localObject1;
      localObject3 = localObject4;
      if (!bool) {
        break label311;
      }
      Slog.v("IntentResolver", "Second type cut: " + Arrays.toString((Object[])localObject4));
      localObject2 = localObject1;
      localObject3 = localObject4;
      break label311;
      label843:
      localObject1 = arrayOfIntentFilter;
      localObject4 = localObject2;
      localObject5 = localObject3;
      if (paramIntent.getAction() != null)
      {
        arrayOfIntentFilter = (IntentFilter[])this.mTypedActionToFilter.get(paramIntent.getAction());
        localObject1 = arrayOfIntentFilter;
        localObject4 = localObject2;
        localObject5 = localObject3;
        if (bool)
        {
          Slog.v("IntentResolver", "Typed Action list: " + Arrays.toString(arrayOfIntentFilter));
          localObject1 = arrayOfIntentFilter;
          localObject4 = localObject2;
          localObject5 = localObject3;
        }
      }
    }
    label940:
    return localArrayList;
  }
  
  public List<R> queryIntentFromList(Intent paramIntent, String paramString, boolean paramBoolean, ArrayList<F[]> paramArrayList, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    if ((paramIntent.getFlags() & 0x8) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      FastImmutableArraySet localFastImmutableArraySet = getFastIntentCategories(paramIntent);
      String str = paramIntent.getScheme();
      int j = paramArrayList.size();
      int i = 0;
      while (i < j)
      {
        buildResolveList(paramIntent, localFastImmutableArraySet, bool, paramBoolean, paramString, str, (IntentFilter[])paramArrayList.get(i), localArrayList, paramInt);
        i += 1;
      }
    }
    filterResults(localArrayList);
    sortResults(localArrayList);
    return localArrayList;
  }
  
  public void removeFilter(F paramF)
  {
    removeFilterInternal(paramF);
    this.mFilters.remove(paramF);
  }
  
  void removeFilterInternal(F paramF)
  {
    int i = unregister_intent_filter(paramF, paramF.schemesIterator(), this.mSchemeToFilter, "      Scheme: ");
    int j = unregister_mime_types(paramF, "      Type: ");
    if ((i == 0) && (j == 0)) {
      unregister_intent_filter(paramF, paramF.actionsIterator(), this.mActionToFilter, "      Action: ");
    }
    if (j != 0) {
      unregister_intent_filter(paramF, paramF.actionsIterator(), this.mTypedActionToFilter, "      TypedAction: ");
    }
  }
  
  protected void sortResults(List<R> paramList)
  {
    Collections.sort(paramList, mResolvePrioritySorter);
  }
  
  private class IteratorWrapper
    implements Iterator<F>
  {
    private F mCur;
    private final Iterator<F> mI;
    
    IteratorWrapper()
    {
      Iterator localIterator;
      this.mI = localIterator;
    }
    
    public boolean hasNext()
    {
      return this.mI.hasNext();
    }
    
    public F next()
    {
      IntentFilter localIntentFilter = (IntentFilter)this.mI.next();
      this.mCur = localIntentFilter;
      return localIntentFilter;
    }
    
    public void remove()
    {
      if (this.mCur != null) {
        IntentResolver.this.removeFilterInternal(this.mCur);
      }
      this.mI.remove();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/IntentResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */