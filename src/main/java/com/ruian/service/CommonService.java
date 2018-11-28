package com.ruian.service;

import com.ruian.bean.Banner;
import com.ruian.bean.Column;
import com.ruian.utils.DbUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.ruian.bean.Content.TITLE_IMG_PATH;

@Service
public class CommonService{

    public List<Column> getColumn(Integer superColumnId) {
        String sql;
        if(superColumnId == null){
            sql = "select id,name,super_menu_id from menu where 1=? and menu.super_menu_id is null";
            superColumnId = 1;
        }else {
            sql = "select id,name,super_menu_id from menu where super_menu_id = ?";
        }
//        System.out.println(sql);
        ResultSet result;
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            ps.setInt(1,superColumnId);
            result = ps.executeQuery();
//            System.out.println(result);
            if(result != null) {
                List<Column> columnList = new ArrayList<>();
                while(result.next()) {

                    Column column = new Column();
                    column.setId(result.getString("id"));
                    column.setName(result.getString("name"));
                   String superId=result.getString("super_menu_id");
                    if(superId == null){
                        superId="null";
                    }
                    column.setSuperId(superId);
                    columnList.add(column);
                }
//                System.out.println(columnList);
                return columnList;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getColumnName(Integer columnId) {
        String sql = "select name from menu where id = ?";
        ResultSet result;
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            ps.setInt(1,columnId);
            result = ps.executeQuery();
            if(result != null) {
                if(result.next()){
                    return result.getString("name");
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int getCount(String searchId) {
        String sql;
        if(searchId.equals("0")){
            sql = "select count(*) count from message where 0 = ?";
        }else {
            sql= "select count(*) count from message where menu_id = ?";
        }
        ResultSet result;
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            ps.setInt(1, Integer.parseInt(searchId));
            result = ps.executeQuery();
            if(result != null) {
                if(result.next()){
                    return result.getInt("count");
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Banner> selectBanner(int num) {
        String sql = "select message.id,title,title_img,super_menu_id,menu.id menu_id from message,menu where is_push = 1 and menu.id=message.menu_id order by id desc limit 0,?";
        ResultSet result;
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            ps.setInt(1, num);
            result = ps.executeQuery();
//        System.out.println(result);
            if(result != null) {
                List<Banner> bannerList = new ArrayList<>();
                while(result.next()) {
                    Banner banner = new Banner();
                    banner.setId(result.getString("id"));
                    banner.setTitle(result.getString("title"));
                    String titleImg = result.getString("title_img");
                    if(titleImg != null) {
                        banner.setTitleImg(TITLE_IMG_PATH + File.separator + titleImg);
                    }

                    String colId = result.getString("super_menu_id");
                    if(colId == null) {
                        colId = result.getString("menu_id");
                    }
                    banner.setColId(colId);
                    bannerList.add(banner);
                }
                return bannerList;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFileName(String messageId) {
        String sql = "select file from message where id=?";
        ResultSet result;
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            ps.setString(1, messageId);
            result = ps.executeQuery();
            if(result != null) {
                if(result.next()) {
                    String file = result.getString("file");
                    return file.substring(file.indexOf("_"));
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
