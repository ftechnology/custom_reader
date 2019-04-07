/**
 * 
 * @author Mohammad Saiful Alam
 * Setting BookEntity
 *
 */
package com.microasset.saiful.entity;

import com.microasset.saiful.database.BaseEntity;

import java.lang.reflect.Type;

// TABLE ROW/ENTITY
public class BookEntity extends BaseEntity {

    //Type is Like Bookmark, LastReadingPpsition, Star, Line, Yes.ect..

    public static String Type_Bookmark = "Type_Bookmark";
    public static String Type_LastReadingPosition = "Type_LastReadingPosition";
    public static String Type_Star = "Type_Star";
    public static String Type_Line = "Type_Line";

    /**
     * Return the create table script
     * @return
     */
    public static String getCreateTable() {

        String sqltblCreate = 		"CREATE TABLE " + Table.TABLE_NAME + "("
                + Table.ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ","
                + Table.BOOK_ID + " TEXT"+ ","
                + Table.TYPE + " TEXT"+ ","
                + Table.INSERT_DATE + " TEXT"+ ","
                + Table.PAGE_NUMBER + " TEXT);";

        return sqltblCreate;
    }

    /**
     * Return the drop table script
     * @return
     */
    public static String getDropTable() {

        String sqltblCreate = "DROP TABLE IF EXISTS " + Table.TABLE_NAME;

        return sqltblCreate;
    }

    public BookEntity(long id) {
        super(id);
    }
    // Add the following lines getCreateTable() in -> Tables.createTableScript()
    // to ensure that table is created.
    public static class Table {
        // TABLE NAME
        public static final String TABLE_NAME = "BookmarkTable";
        // TABLE COLUMNS
        public static final String ID = "ID";
        public static final String BOOK_ID = "BOOK_ID";
        // Type is Like Bookmark, LastReadingPpsition, Star, Line, Yes.ect..
        public static final String TYPE = "TYPE";
        public static final String PAGE_NUMBER = "PAGE_NUMBER";
        public static final String INSERT_DATE = "INSERT_DATE";

    }

    //
    public BookEntity(String bookID, String pageNumber, String insertDate, String type) {
        super(-1);
        this.setBookId(bookID);
        this.setPageNumber(pageNumber);
        this.setInsertDate(insertDate);
        this.setType(type);
    }
    /**
     * @return the mValue
     */
    public String getBookId() {
        return this.getString(Table.BOOK_ID);
    }

    /**
     * @param value the mValue to set
     */
    public void setBookId(String value) {
        this.setValue(Table.BOOK_ID, value);
    }

    public String getPagenumber() {
        return this.getString(Table.PAGE_NUMBER);
    }

    /**
     * @param value the mValue to set
     */
    public void setPageNumber(String value) {
        this.setValue(Table.PAGE_NUMBER, value);
    }

    public String getInsertDate() {
        return this.getString(Table.INSERT_DATE);
    }

    /**
     * @param value the mValue to set
     */
    public void setInsertDate(String value) {
        this.setValue(Table.INSERT_DATE, value);
    }

    public BookEntity(long id, String value) {
        super(id);
    }

    public String getType() {
        return this.getString(Table.TYPE);
    }

    public void setType(String value) {
        this.setValue(Table.TYPE, value);
    }

}



