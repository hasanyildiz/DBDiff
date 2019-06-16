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


public class ViewCompare {

    private static Connection sourceConn = null;
    private static Connection targetConn = null;
    private List<String> sourceViewList;
    private List<String> targetViewList;

    public ViewCompare() throws SQLException, ClassNotFoundException {
        sourceConn = SourceConnection.getConnection();
        targetConn = TargetConnection.getConnection();

    }

    private List<String> getSourceViewNames() throws SQLException {
        String selectSql = String.format("SELECT * FROM ALL_VIEWS WHERE OWNER='"+SourceConnection.getSchemaName()+"'");
        PreparedStatement st = sourceConn.prepareStatement(selectSql);
        if (st != null) {
            ResultSet rs = st.executeQuery();
            if (rs != null) {
                sourceViewList = new ArrayList<>();
                while (rs.next()) {
                    sourceViewList.add(rs.getString("VIEW_NAME"));
                }
            }
        }
        return sourceViewList;
    }

    private List<String> getTargetViewNames() throws SQLException {
        String selectSql = String.format("SELECT * FROM ALL_VIEWS WHERE OWNER='"+TargetConnection.getSchemaName()+"'");
        PreparedStatement st = targetConn.prepareStatement(selectSql);
        if (st != null) {
            ResultSet rs = st.executeQuery();
            if (rs != null) {
                targetViewList = new ArrayList<>();
                while (rs.next()) {
                    targetViewList.add(rs.getString("VIEW_NAME"));
                }
            }
        }
        return targetViewList;
    }

    private List<String> getMissingViews() throws SQLException {
        List<String> source = getSourceViewNames();
        List<String> target = getTargetViewNames();
        source.removeAll(target);
        return source;
    }

    private List<String> getExistingViews() throws SQLException {
        List<String> source = getSourceViewNames();
        List<String> target = getTargetViewNames();
        source.retainAll(target);
        return source;
    }

    public List<String> compareViews() throws SQLException, ClassNotFoundException {
        List<String> missing = getMissingViews();
        System.out.println("missing views:"+missing);
        List<String> viewList = new ArrayList<>();
        if (missing != null && missing.size() > 0) {
            for (String misView : missing) {
                String sql = "SELECT * FROM ALL_VIEWS WHERE OWNER='"+SourceConnection.getSchemaName()+"' AND VIEW_NAME='" + misView + "'";
                PreparedStatement st = sourceConn.prepareStatement(sql);
                System.out.println("compare views");
                ResultSet rs = st.executeQuery();
                if (rs != null) {
                    if (rs.next()) {
                        String viewName = rs.getString("VIEW_NAME");
                        String viewText="";
                        try {
                            InputStream textData=rs.getBinaryStream("TEXT");
                            viewText = inputStreamToString(textData);
                        } catch (Exception e) {
                            String err = e.toString();
                            System.out.println(err);
                        }
                        String createSqlText = "CREATE OR REPLACE VIEW " + viewName + " AS " + viewText + "; ";
                        viewList.add(createSqlText);
                    }
                }
            }
        }
        return compareExistingViews(viewList);
    }

    private List<String> compareExistingViews(List<String> viewList) throws SQLException, ClassNotFoundException {
        List<String> existing = getExistingViews();
        List<DBView> sourceViewList = new ArrayList<>();
        List<DBView> targetViewList = new ArrayList<>();
        if (existing != null && existing.size() > 0) {
            for (String existView : existing) {
                String sql = "SELECT OWNER,TEXT,VIEW_NAME,TEXT_LENGTH FROM ALL_VIEWS WHERE OWNER='%s' AND VIEW_NAME='" + existView + "'";
                PreparedStatement sourceST = sourceConn.prepareStatement(String.format(sql, SourceConnection.getSchemaName()));
                PreparedStatement targetST = targetConn.prepareStatement(String.format(sql, TargetConnection.getSchemaName()));
                ResultSet sourceRS = sourceST.executeQuery();
                System.out.println("compareExistingViews");
                ResultSet targetRS = targetST.executeQuery();
                System.out.println("compareExistingViews");
                DBView sourceView = null;
                DBView targetView = null;
                
                
                if (targetRS != null) {
                    if (targetRS.next()) {
                        targetView = new DBView();
                         String text="";
                        try {
                            InputStream textData=targetRS.getBinaryStream("TEXT");
                            text = inputStreamToString(textData);
                            textData.close();
                        } catch (Exception e) {
                            String err = e.toString();
                            System.out.println(err);
                        }finally{
                        targetView.setText(text);    
                        }
                        targetView.setTextLength(targetRS.getInt("TEXT_LENGTH"));
                        targetView.setViewName(targetRS.getString("VIEW_NAME"));
                       
                        targetViewList.add(targetView);
                    }
                }
                
                
                if (sourceRS != null) {
                    if (sourceRS.next()) {
                        sourceView = new DBView();
                        String text="";
                        try {
                            InputStream textData=sourceRS.getBinaryStream("TEXT");
                            text = inputStreamToString(textData);
                            textData.close();
                        } catch (Exception e) {
                            String err = e.toString();
                            System.out.println(err);
                        }finally{
                        sourceView.setText(text);    
                        }
                        sourceView.setTextLength(sourceRS.getInt("TEXT_LENGTH"));
                        sourceView.setViewName(sourceRS.getString("VIEW_NAME"));
                        
                        sourceViewList.add(sourceView);
                    }
                }
                
                
                if (sourceView.getTextLength() != targetView.getTextLength()) {
                    String createSqlText = "CREATE OR REPLACE VIEW " + sourceView.getViewName() + " AS " + sourceView.getText() + "; ";
                    viewList.add(createSqlText);
                } else if (!sourceView.getText().equals(targetView.getText())) {
                    String createSqlText = "CREATE OR REPLACE VIEW " + sourceView.getViewName() + " AS " + sourceView.getText() + "; ";
                    viewList.add(createSqlText);
                }
                sourceST.close();
                targetST.close();
            }
        }
        return viewList;
    }
    public String inputStreamToString(InputStream inputStream) throws IOException {
    try(ByteArrayOutputStream result = new ByteArrayOutputStream()) {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString("UTF-8");
    }
}
}
