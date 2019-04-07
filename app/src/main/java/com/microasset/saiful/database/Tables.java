/**
 * @author Mohammad Saiful Alam
 * The table information like list of create table and drop tables
 */
package com.microasset.saiful.database;
import com.microasset.saiful.entity.BookEntity;
import com.microasset.saiful.entity.DrawingObjectEntity;
import com.microasset.saiful.entity.HomeWorkEntity;
import com.microasset.saiful.entity.RecentlyOpenEntity;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Tables extends ArrayList<String> {

    // The list of drop tables
    protected ArrayList<String> mListDropTables = new ArrayList<String>();

    /**
     * Constructor function.
     */
    public Tables() {
        createTableScript();
        //dropTableScript();
    }

    /**
     * Return the drop table list
     *
     * @return
     */
    public ArrayList<String> getDropTableList() {

        return this.mListDropTables;
    }

    /**
     * Add all the create table script here.
     */
    public void createTableScript() {
        // TODO add more
        this.add(BookEntity.getCreateTable());
        this.add(DrawingObjectEntity.getCreateTable());
        this.add(HomeWorkEntity.getCreateTable());
        this.add(RecentlyOpenEntity.getCreateTable());
        // Add more tables here....
    }

    /**
     * Add all the drop table script here.
     */
    public void dropTableScript() {
        // TODO add more
        mListDropTables.add(BookEntity.getDropTable());
        mListDropTables.add(DrawingObjectEntity.getDropTable());
        mListDropTables.add(HomeWorkEntity.getDropTable());
        // Add more tables here....
    }
}
