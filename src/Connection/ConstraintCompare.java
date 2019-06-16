/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class ConstraintCompare {

    private final Connection sourceConn;
    private final Connection targetConn;

    public ConstraintCompare() throws SQLException, ClassNotFoundException {
        sourceConn = SourceConnection.getConnection();
        targetConn = TargetConnection.getConnection();
    }

    public List<String> compareConstraints() throws SQLException,IOException {
        List<String> result = new ArrayList<>();
        List<DBConstraint> allTargets = getTargetConstraintList();
        for (DBConstraint constr : getMissingConstraintsList()) {
            if("P".equals(constr.getConstraintType()))
            result.add(createConstraintString(constr, AlterType.ADD, ConstraintType.PRIMARY_KEY));
            else if("C".equals(constr.getConstraintType()))
            result.add(createConstraintString(constr, AlterType.ADD, ConstraintType.CHECK));
        }
        for (DBConstraint constr : getExistingConstraintsList()) {
            DBConstraint target = (DBConstraint) allTargets.stream().filter(trg -> constr.getConstraintName().equals(trg.getConstraintName())).findFirst().get();
            if (target != null) {
                result.addAll(compareConsrtaints(constr, target));
            }
        }
        return result;
    }

    private List<String> compareConsrtaints(DBConstraint source, DBConstraint target) throws SQLException {
        List<String> sqlList = new ArrayList<>();
        boolean isDropped = false;
        if (source.getColumnList().size() != target.getColumnList().size()) {
            if("C".equals(source.getConstraintType()))
            sqlList.add(createConstraintString(source, AlterType.DROP, ConstraintType.CHECK));
            else if("P".equals(source.getConstraintType()))
            sqlList.add(createConstraintString(source, AlterType.DROP, ConstraintType.PRIMARY_KEY));    
            isDropped = true;
        } else {
            boolean buyukFound = true;
            for (DBConstraintColumn srcCol : source.getColumnList()) {
                boolean found = false;
                for (DBConstraintColumn tarCol : target.getColumnList()) {
                    if (tarCol.getColumnName().equals(srcCol.getColumnName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    buyukFound = false;
                    break;
                }
            }
            if (!buyukFound) {
                if("C".equals(source.getConstraintType()))
            sqlList.add(createConstraintString(source, AlterType.DROP, ConstraintType.CHECK));
            else if("P".equals(source.getConstraintType()))
            sqlList.add(createConstraintString(source, AlterType.DROP, ConstraintType.PRIMARY_KEY));    
                isDropped = true;
            }
        }
        if (isDropped) {
            if("C".equals(source.getConstraintType()))
            sqlList.add(createConstraintString(source, AlterType.ADD, ConstraintType.CHECK));
            else if("P".equals(source.getConstraintType()))
            sqlList.add(createConstraintString(source, AlterType.ADD, ConstraintType.PRIMARY_KEY));
            
        }
        return sqlList;
    }

    private String createConstraintString(DBConstraint constr, AlterType alterType, ConstraintType constType) throws SQLException {
        String alterTypeStr = "ADD";
        String constTypeStr = "PRIMARY KEY";
        if (alterType == AlterType.MODIFY) {
            throw new SQLException("Constraint cannot be modified");
        } else if (alterType == AlterType.DROP) {
            alterTypeStr = "DROP";
        }
        if (constType == ConstraintType.CHECK) {
            constTypeStr = "CHECK";
        }
        String sql = "";
        if (alterType == AlterType.ADD) {
            String keys = "";
            if (constType == ConstraintType.PRIMARY_KEY) {
                keys = primaryKeyToString(constr.getColumnList());
            } else if (constType == ConstraintType.CHECK) {
                keys = checkToString(constr);
            }
            sql = String.format("ALTER TABLE %s %s CONSTRAINT %s %s (%s);", new Object[]{constr.getTableName(), alterTypeStr, constr.getConstraintName(), constTypeStr, keys});
        } else if (alterType == AlterType.DROP) {
            sql = String.format("ALTER TABLE %s %s CONSTRAINT %s;", new Object[]{constr.getTableName(), alterTypeStr, constr.getConstraintName()});
        }

        return sql;
    }

    private String checkToString(DBConstraint cons) {
        String res = cons.getSearchCondition();
        return res;
    }

    private String primaryKeyToString(List<DBConstraintColumn> cols) {
        String res = "";
        int i = 0;
        for (DBConstraintColumn col : cols) {
            if (i > 0) {
                res += "," + col.getColumnName();
            } else {
                res += col.getColumnName();
            }
            i++;
        }
        return res;
    }

    private List<DBConstraint> getMissingConstraintsList() throws SQLException, IOException {
        List<DBConstraint> source = getSourceConstraintList();
        List<DBConstraint> target = getTargetConstraintList();
        List<DBConstraint> resultList = new ArrayList<>();
        for (DBConstraint src : source) {
            boolean found = false;
            for (DBConstraint tar : target) {
                if (!found) {
                    if (src.getConstraintName().equals(tar.getConstraintName())) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                resultList.add(src);
            }
        }
        return resultList;
    }

    private List<DBConstraint> getExistingConstraintsList() throws SQLException, IOException {
        List<DBConstraint> source = getSourceConstraintList();
        List<DBConstraint> target = getTargetConstraintList();
        List<DBConstraint> resultList = new ArrayList<>();
        for (DBConstraint src : source) {
            boolean found = false;
            for (DBConstraint tar : target) {
                if (!found) {
                    if (src.getConstraintName().equals(tar.getConstraintName())) {
                        found = true;
                        break;
                    }
                }
            }
            if (found) {
                resultList.add(src);
            }
        }
        return resultList;
    }

    private List<DBConstraint> getSourceConstraintList() throws SQLException, IOException {
        List<DBConstraint> sourceConstList = new ArrayList<>();
        String sql = String.format("SELECT OWNER,CONSTRAINT_NAME,CONSTRAINT_TYPE,TABLE_NAME,STATUS,INDEX_NAME,STATUS,SEARCH_CONDITION "
                + "FROM ALL_CONSTRAINTS WHERE OWNER='%s' ",
                new Object[]{SourceConnection.getSchemaName()});
        PreparedStatement st = sourceConn.prepareCall(sql);
        ResultSet rs = st.executeQuery();
        st.setFetchSize(1000);
        if (rs != null) {
            DBConstraint sourceConst;
            while (rs.next()) {
                sourceConst = new DBConstraint();
                if ("C".equals(rs.getString("CONSTRAINT_TYPE"))) {
                    String text = "";
                    try {
                        InputStream textData = rs.getBinaryStream("SEARCH_CONDITION");
                        text = inputStreamToString(textData);
                        textData.close();
                    } catch (Exception e) {
                        String err = e.toString() + "getSourceConstraintList try";
                        System.out.println(err);
                    } finally {
                        sourceConst.setSearchCondition(text);
                    }
                } else if ("P".equals(rs.getString("CONSTRAINT_TYPE"))) {
                    sourceConst.setSearchCondition(rs.getString("SEARCH_CONDITION"));
                }
                sourceConst.setTableName(rs.getString("TABLE_NAME"));
                sourceConst.setConstraintName(rs.getString("CONSTRAINT_NAME"));
                sourceConst.setStatus(rs.getString("STATUS"));
                sourceConst.setConstraintType(rs.getString("CONSTRAINT_TYPE"));
                sourceConst.setIndexName(rs.getString("INDEX_NAME"));
                sourceConst.setColumnList(getConstraintColumns(sourceConst.getTableName(), sourceConst.getConstraintName(), sourceConn));
                sourceConstList.add(sourceConst);
            }
        }
        st.close();
        return sourceConstList;
    }

    private List<DBConstraint> getTargetConstraintList() throws SQLException, IOException {
        List<DBConstraint> targetConstList = new ArrayList<>();
        String sql = String.format("SELECT OWNER,CONSTRAINT_NAME,CONSTRAINT_TYPE,TABLE_NAME,STATUS,INDEX_NAME,STATUS,SEARCH_CONDITION "
                + "FROM ALL_CONSTRAINTS WHERE OWNER='%s'",
                new Object[]{SourceConnection.getSchemaName()});
        PreparedStatement st = targetConn.prepareCall(sql);
        ResultSet rs = st.executeQuery();
        st.setFetchSize(1000);
        if (rs != null) {
            DBConstraint targetConst;
            while (rs.next()) {
                targetConst = new DBConstraint();
                if ("C".equals(rs.getString("CONSTRAINT_TYPE"))) {
                    String text = "";
                    int i = 0;
                    try {
                        i=0;
                        InputStream textData = rs.getBinaryStream("SEARCH_CONDITION");
                        i=1;
                        text = inputStreamToString(textData);
                        i=2;
                        textData.close();
                        i=3;
                    } catch (SQLException e) {
                        String err = e.toString() + "getTargetConstraintList "+i;
                        System.out.println(err);
                    } finally {
                        targetConst.setSearchCondition(text);
                    }
                } else if ("P".equals(rs.getString("CONSTRAINT_TYPE"))) {
                    targetConst.setSearchCondition(rs.getString("SEARCH_CONDITION"));
                }

                targetConst.setTableName(rs.getString("TABLE_NAME"));
                targetConst.setConstraintName(rs.getString("CONSTRAINT_NAME"));
                targetConst.setStatus(rs.getString("STATUS"));
                targetConst.setConstraintType(rs.getString("CONSTRAINT_TYPE"));
                targetConst.setIndexName(rs.getString("INDEX_NAME"));

                targetConst.setColumnList(getConstraintColumns(targetConst.getTableName(), targetConst.getConstraintName(), targetConn));

                targetConstList.add(targetConst);
            }
        }
        st.close();
        return targetConstList;
    }

    private List<DBConstraintColumn> getConstraintColumns(String tableName, String constraintName, Connection conn) throws SQLException {
        List<DBConstraintColumn> columnList = new ArrayList<>();
        String sql = String.format("SELECT * FROM ALL_CONS_COLUMNS WHERE OWNER='%s' AND TABLE_NAME='%s' AND CONSTRAINT_NAME='%s'",
                new Object[]{SourceConnection.getSchemaName(), tableName, constraintName});
        PreparedStatement st = conn.prepareStatement(sql);
        ResultSet rs = st.executeQuery();
        if (rs != null) {
            DBConstraintColumn col;
            while (rs.next()) {
                col = new DBConstraintColumn();
                col.setConstraintName(rs.getString("CONSTRAINT_NAME"));
                col.setTableName(rs.getString("TABLE_NAME"));
                col.setColumnName(rs.getString("COLUMN_NAME"));
                col.setPosition(rs.getInt("POSITION"));
                columnList.add(col);
            }
        }
        st.close();
        return columnList;
    }

    public String inputStreamToString(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toString("UTF-8");
        }
    }
}
