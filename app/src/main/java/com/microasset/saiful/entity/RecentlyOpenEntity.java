/**
 * 
 * @author Mohammad Saiful Alam
 * Setting HomeWorkEntity
 *
 */
package com.microasset.saiful.entity;

import com.microasset.saiful.database.BaseEntity;

// TABLE ROW/ENTITY
public class RecentlyOpenEntity extends BaseEntity {

    /**
     * Return the create table script
     * @return
     */
    public static String getCreateTable() {

        String sqltblCreate = 		"CREATE TABLE " + Table.TABLE_NAME + "("
                + Table.ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ","
                + Table.BOOK_ID + " TEXT"+ ","
                + Table.CLASS_NAME + " TEXT"+ ","
                + Table.CLASS_VERSION + " TEXT"+ ","
                + Table.IMAGE_PATH + " TEXT"+ ","
                + Table.INSERT_DATE + " TEXT);";

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

    public RecentlyOpenEntity(long id) {
        super(id);
    }
    // Add the following lines getCreateTable() in -> Tables.createTableScript()
    // to ensure that table is created.
    public static class Table {
        // TABLE NAME
        public static final String TABLE_NAME = "RecentlyOpenEntity";
        // TABLE COLUMNS
        public static final String ID = "ID";
        public static final String BOOK_ID = "BOOK_ID";
        public static final String CLASS_NAME = "CLASS_NAME";
        public static final String CLASS_VERSION = "CLASS_VERSION";
        public static final String IMAGE_PATH = "IMAGE_PATH";
        public static final String INSERT_DATE = "INSERT_DATE";
    }

    //
    public RecentlyOpenEntity(String bookID, String className, String classVersion, String imagePath, String insertDate) {
        super(-1);
        this.setBookId(bookID);
        this.setClassName(className);
        this.setClassVersion(classVersion);
        this.setImagePath(imagePath);
        this.setInsertDate(insertDate);
    }

    public String getBookId() {
        return this.getString(Table.BOOK_ID);
    }


    public void setBookId(String value) {
        this.setValue(Table.BOOK_ID, value);
    }

    public String getClassName() {
        return this.getString(Table.CLASS_NAME);
    }

    public void setClassName(String value) {
        this.setValue(Table.CLASS_NAME, value);
    }

    public String getClassVersion() {
        return this.getString(Table.CLASS_VERSION);
    }

    public void setClassVersion(String value) {
        this.setValue(Table.CLASS_VERSION, value);
    }


    public String getImagePath() {
        return this.getString(Table.IMAGE_PATH);
    }

    public void setImagePath(String value) {
        this.setValue(Table.IMAGE_PATH, value);
    }

    public String getInsertDate() {
        return this.getString(Table.INSERT_DATE);
    }


    public void setInsertDate(String value) {
        this.setValue(Table.INSERT_DATE, value);
    }

    public RecentlyOpenEntity(long id, String value) {
        super(id);
    }
}



