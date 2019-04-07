/**
 * 
 * @author Mohammad Saiful Alam
 * Setting HomeWorkEntity
 *
 */
package com.microasset.saiful.entity;

import com.microasset.saiful.database.BaseEntity;

// TABLE ROW/ENTITY
public class HomeWorkEntity extends BaseEntity {

    /**
     * Return the create table script
     * @return
     */
    public static String getCreateTable() {

        String sqltblCreate = 		"CREATE TABLE " + Table.TABLE_NAME + "("
                + Table.ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ","
                + Table.BOOK_ID + " TEXT"+ ","
                + Table.PAGE_CONTENT + " TEXT"+ ","
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

    public HomeWorkEntity(long id) {
        super(id);
    }
    // Add the following lines getCreateTable() in -> Tables.createTableScript()
    // to ensure that table is created.
    public static class Table {
        // TABLE NAME
        public static final String TABLE_NAME = "HomeWorkEntity";
        // TABLE COLUMNS
        public static final String ID = "ID";
        public static final String BOOK_ID = "BOOK_ID";
        public static final String PAGE_CONTENT = "PAGE_CONTENT";
        public static final String INSERT_DATE = "INSERT_DATE";
        public static final String PAGE_NUMBER = "PAGE_NUMBER";
    }

    //
    public HomeWorkEntity(String bookID, String pageContent, String insertDate,String pageNumber) {
        super(-1);
        this.setBookId(bookID);
        this.setPageContent(pageContent);
        this.setInsertDate(insertDate);
        this.setPageNumber(pageNumber);
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

    public String getPageContent() {
        return this.getString(Table.PAGE_CONTENT);
    }

    /**
     * @param value the mValue to set
     */
    public void setPageContent(String value) {
        this.setValue(Table.PAGE_CONTENT, value);
    }

    public String getInsertDate() {
        return this.getString(Table.INSERT_DATE);
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
    /**
     * @param value the mValue to set
     */
    public void setInsertDate(String value) {
        this.setValue(Table.INSERT_DATE, value);
    }

    public HomeWorkEntity(long id, String value) {
        super(id);
    }
}



