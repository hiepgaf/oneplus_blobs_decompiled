package android.content;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ContentProviderOperation
  implements Parcelable
{
  public static final Parcelable.Creator<ContentProviderOperation> CREATOR = new Parcelable.Creator()
  {
    public ContentProviderOperation createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ContentProviderOperation(paramAnonymousParcel, null);
    }
    
    public ContentProviderOperation[] newArray(int paramAnonymousInt)
    {
      return new ContentProviderOperation[paramAnonymousInt];
    }
  };
  private static final String TAG = "ContentProviderOperation";
  public static final int TYPE_ASSERT = 4;
  public static final int TYPE_DELETE = 3;
  public static final int TYPE_INSERT = 1;
  public static final int TYPE_UPDATE = 2;
  private final Integer mExpectedCount;
  private final String mSelection;
  private final String[] mSelectionArgs;
  private final Map<Integer, Integer> mSelectionArgsBackReferences;
  private final int mType;
  private final Uri mUri;
  private final ContentValues mValues;
  private final ContentValues mValuesBackReferences;
  private final boolean mYieldAllowed;
  
  private ContentProviderOperation(Builder paramBuilder)
  {
    this.mType = Builder.-get4(paramBuilder);
    this.mUri = Builder.-get5(paramBuilder);
    this.mValues = Builder.-get6(paramBuilder);
    this.mSelection = Builder.-get1(paramBuilder);
    this.mSelectionArgs = Builder.-get2(paramBuilder);
    this.mExpectedCount = Builder.-get0(paramBuilder);
    this.mSelectionArgsBackReferences = Builder.-get3(paramBuilder);
    this.mValuesBackReferences = Builder.-get7(paramBuilder);
    this.mYieldAllowed = Builder.-get8(paramBuilder);
  }
  
  public ContentProviderOperation(ContentProviderOperation paramContentProviderOperation, boolean paramBoolean)
  {
    this.mType = paramContentProviderOperation.mType;
    if (paramBoolean) {}
    for (this.mUri = ContentProvider.getUriWithoutUserId(paramContentProviderOperation.mUri);; this.mUri = paramContentProviderOperation.mUri)
    {
      this.mValues = paramContentProviderOperation.mValues;
      this.mSelection = paramContentProviderOperation.mSelection;
      this.mSelectionArgs = paramContentProviderOperation.mSelectionArgs;
      this.mExpectedCount = paramContentProviderOperation.mExpectedCount;
      this.mSelectionArgsBackReferences = paramContentProviderOperation.mSelectionArgsBackReferences;
      this.mValuesBackReferences = paramContentProviderOperation.mValuesBackReferences;
      this.mYieldAllowed = paramContentProviderOperation.mYieldAllowed;
      return;
    }
  }
  
  private ContentProviderOperation(Parcel paramParcel)
  {
    this.mType = paramParcel.readInt();
    this.mUri = ((Uri)Uri.CREATOR.createFromParcel(paramParcel));
    if (paramParcel.readInt() != 0)
    {
      localObject1 = (ContentValues)ContentValues.CREATOR.createFromParcel(paramParcel);
      this.mValues = ((ContentValues)localObject1);
      if (paramParcel.readInt() == 0) {
        break label227;
      }
      localObject1 = paramParcel.readString();
      label71:
      this.mSelection = ((String)localObject1);
      if (paramParcel.readInt() == 0) {
        break label233;
      }
      localObject1 = paramParcel.readStringArray();
      label90:
      this.mSelectionArgs = ((String[])localObject1);
      if (paramParcel.readInt() == 0) {
        break label239;
      }
      localObject1 = Integer.valueOf(paramParcel.readInt());
      label112:
      this.mExpectedCount = ((Integer)localObject1);
      if (paramParcel.readInt() == 0) {
        break label245;
      }
    }
    label227:
    label233:
    label239:
    label245:
    for (Object localObject1 = (ContentValues)ContentValues.CREATOR.createFromParcel(paramParcel);; localObject1 = null)
    {
      this.mValuesBackReferences = ((ContentValues)localObject1);
      localObject1 = localObject2;
      if (paramParcel.readInt() != 0) {
        localObject1 = new HashMap();
      }
      this.mSelectionArgsBackReferences = ((Map)localObject1);
      if (this.mSelectionArgsBackReferences == null) {
        break label251;
      }
      int j = paramParcel.readInt();
      int i = 0;
      while (i < j)
      {
        this.mSelectionArgsBackReferences.put(Integer.valueOf(paramParcel.readInt()), Integer.valueOf(paramParcel.readInt()));
        i += 1;
      }
      localObject1 = null;
      break;
      localObject1 = null;
      break label71;
      localObject1 = null;
      break label90;
      localObject1 = null;
      break label112;
    }
    label251:
    if (paramParcel.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      this.mYieldAllowed = bool;
      return;
    }
  }
  
  private long backRefToValue(ContentProviderResult[] paramArrayOfContentProviderResult, int paramInt, Integer paramInteger)
  {
    if (paramInteger.intValue() >= paramInt)
    {
      Log.e("ContentProviderOperation", toString());
      throw new ArrayIndexOutOfBoundsException("asked for back ref " + paramInteger + " but there are only " + paramInt + " back refs");
    }
    paramArrayOfContentProviderResult = paramArrayOfContentProviderResult[paramInteger.intValue()];
    if (paramArrayOfContentProviderResult.uri != null) {
      return ContentUris.parseId(paramArrayOfContentProviderResult.uri);
    }
    return paramArrayOfContentProviderResult.count.intValue();
  }
  
  public static Builder newAssertQuery(Uri paramUri)
  {
    return new Builder(4, paramUri, null);
  }
  
  public static Builder newDelete(Uri paramUri)
  {
    return new Builder(3, paramUri, null);
  }
  
  public static Builder newInsert(Uri paramUri)
  {
    return new Builder(1, paramUri, null);
  }
  
  public static Builder newUpdate(Uri paramUri)
  {
    return new Builder(2, paramUri, null);
  }
  
  public ContentProviderResult apply(ContentProvider paramContentProvider, ContentProviderResult[] paramArrayOfContentProviderResult, int paramInt)
    throws OperationApplicationException
  {
    ContentValues localContentValues = resolveValueBackReferences(paramArrayOfContentProviderResult, paramInt);
    Object localObject1 = resolveSelectionArgsBackReferences(paramArrayOfContentProviderResult, paramInt);
    if (this.mType == 1)
    {
      paramContentProvider = paramContentProvider.insert(this.mUri, localContentValues);
      if (paramContentProvider == null) {
        throw new OperationApplicationException("insert failed");
      }
      return new ContentProviderResult(paramContentProvider);
    }
    if (this.mType == 3) {
      paramInt = paramContentProvider.delete(this.mUri, this.mSelection, (String[])localObject1);
    }
    while ((this.mExpectedCount != null) && (this.mExpectedCount.intValue() != paramInt))
    {
      Log.e("ContentProviderOperation", toString());
      throw new OperationApplicationException("wrong number of rows: " + paramInt);
      if (this.mType == 2)
      {
        paramInt = paramContentProvider.update(this.mUri, localContentValues, this.mSelection, (String[])localObject1);
      }
      else if (this.mType == 4)
      {
        paramArrayOfContentProviderResult = null;
        Object localObject2;
        if (localContentValues != null)
        {
          paramArrayOfContentProviderResult = new ArrayList();
          localObject2 = localContentValues.valueSet().iterator();
          while (((Iterator)localObject2).hasNext()) {
            paramArrayOfContentProviderResult.add((String)((Map.Entry)((Iterator)localObject2).next()).getKey());
          }
          paramArrayOfContentProviderResult = (String[])paramArrayOfContentProviderResult.toArray(new String[paramArrayOfContentProviderResult.size()]);
        }
        paramContentProvider = paramContentProvider.query(this.mUri, paramArrayOfContentProviderResult, this.mSelection, (String[])localObject1, null);
        int i;
        for (;;)
        {
          try
          {
            i = paramContentProvider.getCount();
            if (paramArrayOfContentProviderResult == null) {
              break;
            }
            if (!paramContentProvider.moveToNext()) {
              break;
            }
            paramInt = 0;
            if (paramInt >= paramArrayOfContentProviderResult.length) {
              continue;
            }
            localObject1 = paramContentProvider.getString(paramInt);
            localObject2 = localContentValues.getAsString(paramArrayOfContentProviderResult[paramInt]);
            if (!TextUtils.equals((CharSequence)localObject1, (CharSequence)localObject2))
            {
              Log.e("ContentProviderOperation", toString());
              throw new OperationApplicationException("Found value " + (String)localObject1 + " when expected " + (String)localObject2 + " for column " + paramArrayOfContentProviderResult[paramInt]);
            }
          }
          finally
          {
            paramContentProvider.close();
          }
          paramInt += 1;
        }
        paramContentProvider.close();
        paramInt = i;
      }
      else
      {
        Log.e("ContentProviderOperation", toString());
        throw new IllegalStateException("bad type, " + this.mType);
      }
    }
    return new ContentProviderResult(paramInt);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public Uri getUri()
  {
    return this.mUri;
  }
  
  public ContentProviderOperation getWithoutUserIdInUri()
  {
    if (ContentProvider.uriHasUserId(this.mUri)) {
      return new ContentProviderOperation(this, true);
    }
    return this;
  }
  
  public boolean isAssertQuery()
  {
    return this.mType == 4;
  }
  
  public boolean isDelete()
  {
    return this.mType == 3;
  }
  
  public boolean isInsert()
  {
    return this.mType == 1;
  }
  
  public boolean isReadOperation()
  {
    return this.mType == 4;
  }
  
  public boolean isUpdate()
  {
    return this.mType == 2;
  }
  
  public boolean isWriteOperation()
  {
    if ((this.mType == 3) || (this.mType == 1)) {}
    while (this.mType == 2) {
      return true;
    }
    return false;
  }
  
  public boolean isYieldAllowed()
  {
    return this.mYieldAllowed;
  }
  
  public String[] resolveSelectionArgsBackReferences(ContentProviderResult[] paramArrayOfContentProviderResult, int paramInt)
  {
    if (this.mSelectionArgsBackReferences == null) {
      return this.mSelectionArgs;
    }
    String[] arrayOfString = new String[this.mSelectionArgs.length];
    System.arraycopy(this.mSelectionArgs, 0, arrayOfString, 0, this.mSelectionArgs.length);
    Iterator localIterator = this.mSelectionArgsBackReferences.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Integer localInteger = (Integer)localEntry.getKey();
      int i = ((Integer)localEntry.getValue()).intValue();
      arrayOfString[localInteger.intValue()] = String.valueOf(backRefToValue(paramArrayOfContentProviderResult, paramInt, Integer.valueOf(i)));
    }
    return arrayOfString;
  }
  
  public ContentValues resolveValueBackReferences(ContentProviderResult[] paramArrayOfContentProviderResult, int paramInt)
  {
    if (this.mValuesBackReferences == null) {
      return this.mValues;
    }
    ContentValues localContentValues;
    Iterator localIterator;
    if (this.mValues == null)
    {
      localContentValues = new ContentValues();
      localIterator = this.mValuesBackReferences.valueSet().iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return localContentValues;
      }
      String str = (String)((Map.Entry)localIterator.next()).getKey();
      Integer localInteger = this.mValuesBackReferences.getAsInteger(str);
      if (localInteger == null)
      {
        Log.e("ContentProviderOperation", toString());
        throw new IllegalArgumentException("values backref " + str + " is not an integer");
        localContentValues = new ContentValues(this.mValues);
        break;
      }
      localContentValues.put(str, Long.valueOf(backRefToValue(paramArrayOfContentProviderResult, paramInt, localInteger)));
    }
    return localContentValues;
  }
  
  public String toString()
  {
    return "mType: " + this.mType + ", mUri: " + this.mUri + ", mSelection: " + this.mSelection + ", mExpectedCount: " + this.mExpectedCount + ", mYieldAllowed: " + this.mYieldAllowed + ", mValues: " + this.mValues + ", mValuesBackReferences: " + this.mValuesBackReferences + ", mSelectionArgsBackReferences: " + this.mSelectionArgsBackReferences;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    Uri.writeToParcel(paramParcel, this.mUri);
    if (this.mValues != null)
    {
      paramParcel.writeInt(1);
      this.mValues.writeToParcel(paramParcel, 0);
      if (this.mSelection == null) {
        break label226;
      }
      paramParcel.writeInt(1);
      paramParcel.writeString(this.mSelection);
      label57:
      if (this.mSelectionArgs == null) {
        break label234;
      }
      paramParcel.writeInt(1);
      paramParcel.writeStringArray(this.mSelectionArgs);
      label77:
      if (this.mExpectedCount == null) {
        break label242;
      }
      paramParcel.writeInt(1);
      paramParcel.writeInt(this.mExpectedCount.intValue());
      label100:
      if (this.mValuesBackReferences == null) {
        break label250;
      }
      paramParcel.writeInt(1);
      this.mValuesBackReferences.writeToParcel(paramParcel, 0);
    }
    for (;;)
    {
      if (this.mSelectionArgsBackReferences == null) {
        break label258;
      }
      paramParcel.writeInt(1);
      paramParcel.writeInt(this.mSelectionArgsBackReferences.size());
      Iterator localIterator = this.mSelectionArgsBackReferences.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        paramParcel.writeInt(((Integer)localEntry.getKey()).intValue());
        paramParcel.writeInt(((Integer)localEntry.getValue()).intValue());
      }
      paramParcel.writeInt(0);
      break;
      label226:
      paramParcel.writeInt(0);
      break label57;
      label234:
      paramParcel.writeInt(0);
      break label77;
      label242:
      paramParcel.writeInt(0);
      break label100;
      label250:
      paramParcel.writeInt(0);
    }
    label258:
    paramParcel.writeInt(0);
    if (this.mYieldAllowed) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
    }
  }
  
  public static class Builder
  {
    private Integer mExpectedCount;
    private String mSelection;
    private String[] mSelectionArgs;
    private Map<Integer, Integer> mSelectionArgsBackReferences;
    private final int mType;
    private final Uri mUri;
    private ContentValues mValues;
    private ContentValues mValuesBackReferences;
    private boolean mYieldAllowed;
    
    private Builder(int paramInt, Uri paramUri)
    {
      if (paramUri == null) {
        throw new IllegalArgumentException("uri must not be null");
      }
      this.mType = paramInt;
      this.mUri = paramUri;
    }
    
    public ContentProviderOperation build()
    {
      if ((this.mType == 2) && ((this.mValues == null) || (this.mValues.size() == 0)) && ((this.mValuesBackReferences == null) || (this.mValuesBackReferences.size() == 0))) {
        throw new IllegalArgumentException("Empty values");
      }
      if ((this.mType == 4) && ((this.mValues == null) || (this.mValues.size() == 0)) && ((this.mValuesBackReferences == null) || (this.mValuesBackReferences.size() == 0)) && (this.mExpectedCount == null)) {
        throw new IllegalArgumentException("Empty values");
      }
      return new ContentProviderOperation(this, null);
    }
    
    public Builder withExpectedCount(int paramInt)
    {
      if ((this.mType != 2) && (this.mType != 3) && (this.mType != 4)) {
        throw new IllegalArgumentException("only updates, deletes, and asserts can have expected counts");
      }
      this.mExpectedCount = Integer.valueOf(paramInt);
      return this;
    }
    
    public Builder withSelection(String paramString, String[] paramArrayOfString)
    {
      if ((this.mType != 2) && (this.mType != 3) && (this.mType != 4)) {
        throw new IllegalArgumentException("only updates, deletes, and asserts can have selections");
      }
      this.mSelection = paramString;
      if (paramArrayOfString == null)
      {
        this.mSelectionArgs = null;
        return this;
      }
      this.mSelectionArgs = new String[paramArrayOfString.length];
      System.arraycopy(paramArrayOfString, 0, this.mSelectionArgs, 0, paramArrayOfString.length);
      return this;
    }
    
    public Builder withSelectionBackReference(int paramInt1, int paramInt2)
    {
      if ((this.mType != 2) && (this.mType != 3) && (this.mType != 4)) {
        throw new IllegalArgumentException("only updates, deletes, and asserts can have selection back-references");
      }
      if (this.mSelectionArgsBackReferences == null) {
        this.mSelectionArgsBackReferences = new HashMap();
      }
      this.mSelectionArgsBackReferences.put(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      return this;
    }
    
    public Builder withValue(String paramString, Object paramObject)
    {
      if ((this.mType != 1) && (this.mType != 2) && (this.mType != 4)) {
        throw new IllegalArgumentException("only inserts and updates can have values");
      }
      if (this.mValues == null) {
        this.mValues = new ContentValues();
      }
      if (paramObject == null)
      {
        this.mValues.putNull(paramString);
        return this;
      }
      if ((paramObject instanceof String))
      {
        this.mValues.put(paramString, (String)paramObject);
        return this;
      }
      if ((paramObject instanceof Byte))
      {
        this.mValues.put(paramString, (Byte)paramObject);
        return this;
      }
      if ((paramObject instanceof Short))
      {
        this.mValues.put(paramString, (Short)paramObject);
        return this;
      }
      if ((paramObject instanceof Integer))
      {
        this.mValues.put(paramString, (Integer)paramObject);
        return this;
      }
      if ((paramObject instanceof Long))
      {
        this.mValues.put(paramString, (Long)paramObject);
        return this;
      }
      if ((paramObject instanceof Float))
      {
        this.mValues.put(paramString, (Float)paramObject);
        return this;
      }
      if ((paramObject instanceof Double))
      {
        this.mValues.put(paramString, (Double)paramObject);
        return this;
      }
      if ((paramObject instanceof Boolean))
      {
        this.mValues.put(paramString, (Boolean)paramObject);
        return this;
      }
      if ((paramObject instanceof byte[]))
      {
        this.mValues.put(paramString, (byte[])paramObject);
        return this;
      }
      throw new IllegalArgumentException("bad value type: " + paramObject.getClass().getName());
    }
    
    public Builder withValueBackReference(String paramString, int paramInt)
    {
      if ((this.mType != 1) && (this.mType != 2) && (this.mType != 4)) {
        throw new IllegalArgumentException("only inserts, updates, and asserts can have value back-references");
      }
      if (this.mValuesBackReferences == null) {
        this.mValuesBackReferences = new ContentValues();
      }
      this.mValuesBackReferences.put(paramString, Integer.valueOf(paramInt));
      return this;
    }
    
    public Builder withValueBackReferences(ContentValues paramContentValues)
    {
      if ((this.mType != 1) && (this.mType != 2) && (this.mType != 4)) {
        throw new IllegalArgumentException("only inserts, updates, and asserts can have value back-references");
      }
      this.mValuesBackReferences = paramContentValues;
      return this;
    }
    
    public Builder withValues(ContentValues paramContentValues)
    {
      if ((this.mType != 1) && (this.mType != 2) && (this.mType != 4)) {
        throw new IllegalArgumentException("only inserts, updates, and asserts can have values");
      }
      if (this.mValues == null) {
        this.mValues = new ContentValues();
      }
      this.mValues.putAll(paramContentValues);
      return this;
    }
    
    public Builder withYieldAllowed(boolean paramBoolean)
    {
      this.mYieldAllowed = paramBoolean;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ContentProviderOperation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */