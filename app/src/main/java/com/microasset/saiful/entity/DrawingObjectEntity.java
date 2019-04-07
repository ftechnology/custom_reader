/**
 * 
 * @author Mohammad Saiful Alam
 * Setting DrawingObjectEntity
 *
 */
package com.microasset.saiful.entity;

import com.microasset.saiful.database.BaseEntity;

// TABLE ROW/ENTITY
public class DrawingObjectEntity extends BookEntity {
    /**
     * Return the create table script
     * @return
     */
    public static String getCreateTable() {

        String sqltblCreate = 		"CREATE TABLE " + Table.TABLE_NAME + "("
                + Table.ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ","
                + Table.BOOK_ID + " TEXT"+ ","
                + Table.TYPE + " TEXT"+ ","
                + Table.TABLE_DATA_JSON + " TEXT"+ ","
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


    // Add the following lines getCreateTable() in -> Tables.createTableScript()
    // to ensure that table is created.
    public static class Table extends BookEntity.Table{
        // TABLE NAME
        public static final String TABLE_NAME = "DrawingObjectEntity";
        public static final String TABLE_DATA_JSON = "TABLE_DATA_JSON";
    }

    public DrawingObjectEntity(long id) {
        super(id);
    }
    //
    public DrawingObjectEntity(String bookID, String pageNumber, String insertDate, String type, String JSON_DATA) {
        super(-1);
        this.setBookId(bookID);
        this.setPageNumber(pageNumber);
        this.setInsertDate(insertDate);
        this.setType(type);
        this.setJsonData(JSON_DATA);
    }

    public String getJsonData() {
        return this.getString(Table.TABLE_DATA_JSON);
    }

    public void setJsonData(String value) {
        this.setValue(Table.TABLE_DATA_JSON, value);
    }

}



