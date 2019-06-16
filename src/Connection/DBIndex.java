/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;

import java.util.List;

/**
 *
 * @author EROLULKER
 */
public class DBIndex {
    private String indexName;
    private String tableName;
    private String Uniqueness;
    private List<DBIndexColumn> cols;
    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getUniqueness() {
        return Uniqueness;
    }

    public void setUniqueness(String Uniqueness) {
        this.Uniqueness = Uniqueness;
    }

    public List<DBIndexColumn> getCols() {
        return cols;
    }

    public void setCols(List<DBIndexColumn> cols) {
        this.cols = cols;
    }
    
}
