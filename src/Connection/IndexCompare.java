/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author EROLULKER
 */
public class IndexCompare {

    private final Connection sourceConn;
    private final Connection targetConn;

    public IndexCompare() throws SQLException, ClassNotFoundException {
        sourceConn = SourceConnection.getConnection();
        targetConn = TargetConnection.getConnection();
    }

    public String compareMissingIndexes(String indexName) throws SQLException {
        DBIndex ind = getIndexByName(indexName, sourceConn);
        return generateCreateIndexSql(ind);
    }

    public String compareIndexReverse(String indexName) throws SQLException {
        DBIndex index;
        index = getIndexByName(indexName, targetConn);
        return generateDropIndexSql(index);
    }

    public List<String> compareExistingIndex(String indexName) throws SQLException {
        List<String> existingSql = new ArrayList<>();
        DBIndex sourceIndex, targetIndex;
        sourceIndex = getIndexByName(indexName, sourceConn);
        targetIndex = getIndexByName(indexName, targetConn);
        existingSql.addAll(compareIndexes(sourceIndex, targetIndex));
        return existingSql;
    }

    private List<String> compareIndexes(DBIndex source, DBIndex target) {
        List<String> resultList = new ArrayList<>();
        if (source.getCols().size() != target.getCols().size()) {
            resultList.add(generateDropIndexSql(target));
            resultList.add(generateCreateIndexSql(source));
            return resultList;
        }
        for (DBIndexColumn srcCol : source.getCols()) {
            boolean found = false;
            for (DBIndexColumn tarCol : target.getCols()) {
                if (srcCol.getColumnName().equals(tarCol.getColumnName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                resultList.add(generateDropIndexSql(target));
                resultList.add(generateCreateIndexSql(source));
                return resultList;
            }
        }

        for (DBIndexColumn srcCol : target.getCols()) {
            boolean found = false;
            for (DBIndexColumn tarCol : source.getCols()) {
                if (srcCol.getColumnName().equals(tarCol.getColumnName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                resultList.add(generateDropIndexSql(target));
                resultList.add(generateCreateIndexSql(source));
                return resultList;
            }
        }
        return resultList;
    }

    private List<String> getAllSourceIndexes() throws SQLException {
        List<String> sourceList = new ArrayList<>();
        String sql = "SELECT INDEX_NAME FROM ALL_INDEXES WHERE OWNER='" + SourceConnection.getSchemaName() + "'";
        PreparedStatement st = sourceConn.prepareStatement(sql);
        ResultSet rs = st.executeQuery();
        if (rs != null) {
            while (rs.next()) {
                sourceList.add(rs.getString("INDEX_NAME"));
            }
        }
        st.close();
        return sourceList;
    }

    private List<String> getAllTargetIndexes() throws SQLException {
        List<String> targetList = new ArrayList<>();
        String sql = "SELECT INDEX_NAME FROM ALL_INDEXES WHERE OWNER='" + SourceConnection.getSchemaName() + "'";
        PreparedStatement st = targetConn.prepareStatement(sql);
        ResultSet rs = st.executeQuery();
        if (rs != null) {
            while (rs.next()) {
                targetList.add(rs.getString("INDEX_NAME"));
            }
        }
        st.close();
        return targetList;
    }

    public List<String> getMissingIndexes() throws SQLException {
        List<String> sourceList = getAllSourceIndexes();
        List<String> targetList = getAllTargetIndexes();
        sourceList.removeAll(targetList);
        return sourceList;
    }

    public List<String> getExistingIndexes() throws SQLException {
        List<String> sourceList = getAllSourceIndexes();
        List<String> targetList = getAllTargetIndexes();
        sourceList.retainAll(targetList);
        return sourceList;
    }

    public List<String> getReverseMissingIndexes() throws SQLException {
        List<String> sourceList = getAllSourceIndexes();
        List<String> targetList = getAllTargetIndexes();
        targetList.removeAll(sourceList);
        return targetList;
    }

    private DBIndex getIndexByName(String indexName, Connection conn) throws SQLException {
        String sql = "SELECT * FROM ALL_INDEXES WHERE OWNER='" + SourceConnection.getSchemaName() + "' AND INDEX_NAME='" + indexName + "'";
        PreparedStatement st = conn.prepareStatement(sql);
        ResultSet rs = st.executeQuery();
        DBIndex index = null;
        if (rs != null) {
            index = new DBIndex();
            if (rs.next()) {
                index.setIndexName(rs.getString("INDEX_NAME"));
                index.setTableName(rs.getString("TABLE_NAME"));
                index.setUniqueness(rs.getString("UNIQUENESS"));
                index.setCols(getColumnsByIndex(index, conn));
            }
        }
        st.close();
        return index;
    }

    private List<DBIndexColumn> getColumnsByIndex(DBIndex index, Connection conn) throws SQLException {
        String sql = "SELECT * FROM ALL_IND_COLUMNS WHERE INDEX_OWNER='" + SourceConnection.getSchemaName() + "' "
                + "AND TABLE_NAME='" + index.getTableName() + "' AND INDEX_NAME='" + index.getIndexName() + "'";
        PreparedStatement st = conn.prepareStatement(sql);
        ResultSet rs = st.executeQuery();
        List<DBIndexColumn> cols = new ArrayList<>();
        DBIndexColumn col;
        if (rs != null) {
            while (rs.next()) {
                col = new DBIndexColumn();
                col.setColumnName(rs.getString("COLUMN_NAME"));
                col.setIndexName(rs.getString("INDEX_NAME"));
                col.setTableName(rs.getString("TABLE_NAME"));
                cols.add(col);
            }
        }
        st.close();
        return cols;
    }

    private String generateDropIndexSql(DBIndex index) {
        String sql = String.format("DROP INDEX %s;", index.getIndexName());
        return sql;
    }

    private String generateCreateIndexSql(DBIndex index) {
        String sql;
        if ("UNIQUE".equals(index.getUniqueness())) {
            sql = String.format("CREATE %s INDEX %s ON %s (%s);", index.getUniqueness(), index.getIndexName(), index.getTableName(), generateColumnList(index.getCols()));
        } else {
            sql = String.format("CREATE INDEX %s ON %s (%s);", index.getIndexName(), index.getTableName(), generateColumnList(index.getCols()));
        }
        return sql;
    }

    private String generateColumnList(List<DBIndexColumn> cols) {
        String res = "";
        int i = 0;
        for (DBIndexColumn col : cols) {
            if (i > 0) {
                res += "," + "\"" + col.getColumnName() + "\"";
            } else {
                res += "\"" + col.getColumnName() + "\"";
            }
            i++;
        }
        return res;
    }
}
