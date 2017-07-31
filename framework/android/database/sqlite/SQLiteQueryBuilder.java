package android.database.sqlite;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.CancellationSignal;
import android.text.TextUtils;
import android.util.Log;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLiteQueryBuilder
{
  private static final String TAG = "SQLiteQueryBuilder";
  private static final Pattern sLimitPattern = Pattern.compile("\\s*\\d+\\s*(,\\s*\\d+\\s*)?");
  private boolean mDistinct = false;
  private SQLiteDatabase.CursorFactory mFactory = null;
  private Map<String, String> mProjectionMap = null;
  private boolean mStrict;
  private String mTables = "";
  private StringBuilder mWhereClause = null;
  
  private static void appendClause(StringBuilder paramStringBuilder, String paramString1, String paramString2)
  {
    if (!TextUtils.isEmpty(paramString2))
    {
      paramStringBuilder.append(paramString1);
      paramStringBuilder.append(paramString2);
    }
  }
  
  public static void appendColumns(StringBuilder paramStringBuilder, String[] paramArrayOfString)
  {
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str = paramArrayOfString[i];
      if (str != null)
      {
        if (i > 0) {
          paramStringBuilder.append(", ");
        }
        paramStringBuilder.append(str);
      }
      i += 1;
    }
    paramStringBuilder.append(' ');
  }
  
  public static String buildQueryString(boolean paramBoolean, String paramString1, String[] paramArrayOfString, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    StringBuilder localStringBuilder;
    if ((!TextUtils.isEmpty(paramString3)) || (TextUtils.isEmpty(paramString4)))
    {
      if ((!TextUtils.isEmpty(paramString6)) && (!sLimitPattern.matcher(paramString6).matches())) {
        break label159;
      }
      localStringBuilder = new StringBuilder(120);
      localStringBuilder.append("SELECT ");
      if (paramBoolean) {
        localStringBuilder.append("DISTINCT ");
      }
      if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
        break label187;
      }
      appendColumns(localStringBuilder, paramArrayOfString);
    }
    for (;;)
    {
      localStringBuilder.append("FROM ");
      localStringBuilder.append(paramString1);
      appendClause(localStringBuilder, " WHERE ", paramString2);
      appendClause(localStringBuilder, " GROUP BY ", paramString3);
      appendClause(localStringBuilder, " HAVING ", paramString4);
      appendClause(localStringBuilder, " ORDER BY ", paramString5);
      appendClause(localStringBuilder, " LIMIT ", paramString6);
      return localStringBuilder.toString();
      throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
      label159:
      throw new IllegalArgumentException("invalid LIMIT clauses:" + paramString6);
      label187:
      localStringBuilder.append("* ");
    }
  }
  
  private String[] computeProjection(String[] paramArrayOfString)
  {
    Object localObject1;
    int i;
    Object localObject2;
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
    {
      if (this.mProjectionMap != null)
      {
        localObject1 = new String[paramArrayOfString.length];
        int j = paramArrayOfString.length;
        i = 0;
        if (i < j)
        {
          localObject2 = paramArrayOfString[i];
          String str = (String)this.mProjectionMap.get(localObject2);
          if (str != null) {
            localObject1[i] = str;
          }
          for (;;)
          {
            i += 1;
            break;
            if ((this.mStrict) || ((!((String)localObject2).contains(" AS ")) && (!((String)localObject2).contains(" as ")))) {
              break label108;
            }
            localObject1[i] = localObject2;
          }
          label108:
          throw new IllegalArgumentException("Invalid column " + paramArrayOfString[i]);
        }
        return (String[])localObject1;
      }
      return paramArrayOfString;
    }
    if (this.mProjectionMap != null)
    {
      localObject1 = this.mProjectionMap.entrySet();
      paramArrayOfString = new String[((Set)localObject1).size()];
      localObject1 = ((Set)localObject1).iterator();
      i = 0;
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        if (!((String)((Map.Entry)localObject2).getKey()).equals("_count"))
        {
          paramArrayOfString[i] = ((String)((Map.Entry)localObject2).getValue());
          i += 1;
        }
      }
      return paramArrayOfString;
    }
    return null;
  }
  
  public void appendWhere(CharSequence paramCharSequence)
  {
    if (this.mWhereClause == null) {
      this.mWhereClause = new StringBuilder(paramCharSequence.length() + 16);
    }
    if (this.mWhereClause.length() == 0) {
      this.mWhereClause.append('(');
    }
    this.mWhereClause.append(paramCharSequence);
  }
  
  public void appendWhereEscapeString(String paramString)
  {
    if (this.mWhereClause == null) {
      this.mWhereClause = new StringBuilder(paramString.length() + 16);
    }
    if (this.mWhereClause.length() == 0) {
      this.mWhereClause.append('(');
    }
    DatabaseUtils.appendEscapedSQLString(this.mWhereClause, paramString);
  }
  
  public String buildQuery(String[] paramArrayOfString, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    paramArrayOfString = computeProjection(paramArrayOfString);
    StringBuilder localStringBuilder = new StringBuilder();
    if ((this.mWhereClause != null) && (this.mWhereClause.length() > 0)) {}
    for (int i = 1;; i = 0)
    {
      if (i != 0)
      {
        localStringBuilder.append(this.mWhereClause.toString());
        localStringBuilder.append(')');
      }
      if ((paramString1 != null) && (paramString1.length() > 0))
      {
        if (i != 0) {
          localStringBuilder.append(" AND ");
        }
        localStringBuilder.append('(');
        localStringBuilder.append(paramString1);
        localStringBuilder.append(')');
      }
      return buildQueryString(this.mDistinct, this.mTables, paramArrayOfString, localStringBuilder.toString(), paramString2, paramString3, paramString4, paramString5);
    }
  }
  
  @Deprecated
  public String buildQuery(String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    return buildQuery(paramArrayOfString1, paramString1, paramString2, paramString3, paramString4, paramString5);
  }
  
  public String buildUnionQuery(String[] paramArrayOfString, String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    int j = paramArrayOfString.length;
    if (this.mDistinct) {}
    for (String str = " UNION ";; str = " UNION ALL ")
    {
      int i = 0;
      while (i < j)
      {
        if (i > 0) {
          localStringBuilder.append(str);
        }
        localStringBuilder.append(paramArrayOfString[i]);
        i += 1;
      }
    }
    appendClause(localStringBuilder, " ORDER BY ", paramString1);
    appendClause(localStringBuilder, " LIMIT ", paramString2);
    return localStringBuilder.toString();
  }
  
  public String buildUnionSubQuery(String paramString1, String[] paramArrayOfString, Set<String> paramSet, int paramInt, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    int j = paramArrayOfString.length;
    String[] arrayOfString = new String[j];
    int i = 0;
    if (i < j)
    {
      String str = paramArrayOfString[i];
      if (str.equals(paramString1)) {
        arrayOfString[i] = ("'" + paramString2 + "' AS " + paramString1);
      }
      for (;;)
      {
        i += 1;
        break;
        if ((i <= paramInt) || (paramSet.contains(str))) {
          arrayOfString[i] = str;
        } else {
          arrayOfString[i] = ("NULL AS " + str);
        }
      }
    }
    return buildQuery(arrayOfString, paramString3, paramString4, paramString5, null, null);
  }
  
  @Deprecated
  public String buildUnionSubQuery(String paramString1, String[] paramArrayOfString1, Set<String> paramSet, int paramInt, String paramString2, String paramString3, String[] paramArrayOfString2, String paramString4, String paramString5)
  {
    return buildUnionSubQuery(paramString1, paramArrayOfString1, paramSet, paramInt, paramString2, paramString3, paramString4, paramString5);
  }
  
  public String getTables()
  {
    return this.mTables;
  }
  
  public Cursor query(SQLiteDatabase paramSQLiteDatabase, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2, String paramString3, String paramString4)
  {
    return query(paramSQLiteDatabase, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2, paramString3, paramString4, null, null);
  }
  
  public Cursor query(SQLiteDatabase paramSQLiteDatabase, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    return query(paramSQLiteDatabase, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2, paramString3, paramString4, paramString5, null);
  }
  
  public Cursor query(SQLiteDatabase paramSQLiteDatabase, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2, String paramString3, String paramString4, String paramString5, CancellationSignal paramCancellationSignal)
  {
    if (this.mTables == null) {
      return null;
    }
    if ((this.mStrict) && (paramString1 != null) && (paramString1.length() > 0)) {
      paramSQLiteDatabase.validateSql(buildQuery(paramArrayOfString1, "(" + paramString1 + ")", paramString2, paramString3, paramString4, paramString5), paramCancellationSignal);
    }
    paramArrayOfString1 = buildQuery(paramArrayOfString1, paramString1, paramString2, paramString3, paramString4, paramString5);
    if (Log.isLoggable("SQLiteQueryBuilder", 3)) {
      Log.d("SQLiteQueryBuilder", "Performing query: " + paramArrayOfString1);
    }
    return paramSQLiteDatabase.rawQueryWithFactory(this.mFactory, paramArrayOfString1, paramArrayOfString2, SQLiteDatabase.findEditTable(this.mTables), paramCancellationSignal);
  }
  
  public void setCursorFactory(SQLiteDatabase.CursorFactory paramCursorFactory)
  {
    this.mFactory = paramCursorFactory;
  }
  
  public void setDistinct(boolean paramBoolean)
  {
    this.mDistinct = paramBoolean;
  }
  
  public void setProjectionMap(Map<String, String> paramMap)
  {
    this.mProjectionMap = paramMap;
  }
  
  public void setStrict(boolean paramBoolean)
  {
    this.mStrict = paramBoolean;
  }
  
  public void setTables(String paramString)
  {
    this.mTables = paramString;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/sqlite/SQLiteQueryBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */