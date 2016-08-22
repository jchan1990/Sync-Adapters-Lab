package drewmahrt.generalassemb.ly.investingportfolio;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class MyContentProvider extends ContentProvider {
  private static final String TAG = "MyContentProvider";
  private MyDBHandler myDB;
  private static final String AUTHORITY = StockPortfolioContract.AUTHORITY;
  private static final String STOCKS_TABLE = StockPortfolioContract.Stocks.TABLE_STOCKS;
  public static final Uri CONTENT_URI = StockPortfolioContract.Stocks.CONTENT_URI;

  public static final int STOCKS = 1;
  public static final int STOCKS_ID = 2;
  public static final int STOCKS_SYMBOLS = 3;

  private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    sURIMatcher.addURI(AUTHORITY, STOCKS_TABLE, STOCKS);
    sURIMatcher.addURI(AUTHORITY, STOCKS_TABLE + "/#", STOCKS_ID);
    sURIMatcher.addURI(AUTHORITY, STOCKS_TABLE + "/symbols", STOCKS_SYMBOLS);
  }

  @Override
  public boolean onCreate() {
    myDB = MyDBHandler.getInstance(getContext());
    return false;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    int uriType = sURIMatcher.match(uri);

    long id = 0;
    switch (uriType) {
      case STOCKS:
        id = myDB.addStock(values);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return ContentUris.withAppendedId(CONTENT_URI,id);
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    int uriType = sURIMatcher.match(uri);
    Cursor cursor = null;

    switch (uriType) {
      case STOCKS_ID:
        break;
      case STOCKS:
        cursor = myDB.getStocks(selection);
        break;
      case STOCKS_SYMBOLS:
        cursor = myDB.getStockSymbols(selection);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI");
    }

    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    return cursor;
  }

  @Nullable
  @Override
  public String getType(Uri uri) {
    switch (sURIMatcher.match(uri)){
      case STOCKS:
        return StockPortfolioContract.Stocks.CONTENT_TYPE;
      case STOCKS_ID:
        return StockPortfolioContract.Stocks.CONTENT_ITEM_TYPE;
    }
    return null;
  }


  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {

    int uriType = sURIMatcher.match(uri);
    int rowsDeleted = 0;

    switch (uriType) {
      case STOCKS:
        break;
      case STOCKS_ID:
        String id = uri.getLastPathSegment();
        rowsDeleted = myDB.deleteStockById(id);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return rowsDeleted;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    Log.d(TAG, "update: ");
    int uriType = sURIMatcher.match(uri);
    int rowsUpdated = 0;

    switch (uriType) {
      case STOCKS:
      case STOCKS_ID:
        String id = uri.getLastPathSegment();
        rowsUpdated = myDB.updateStock(values,selection,selectionArgs);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return rowsUpdated;
  }
}
