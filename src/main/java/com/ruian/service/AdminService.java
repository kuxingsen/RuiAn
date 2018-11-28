package com.ruian.service;

import com.ruian.bean.Column;
import com.ruian.bean.Message;
import com.ruian.utils.DateUtils;
import com.ruian.utils.DbUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.ruian.bean.Content.*;

@Service
public class AdminService{
    @Autowired
    private CommonService commonService;
    public int getAdminId(String username, String oldPassword) {
        ResultSet result;
        String sql = "select id from admin where name=? and password=?";
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            ps.setString(1, username);
            ps.setString(2, oldPassword);
            result = ps.executeQuery();
            if(result != null && result.next()) {
//                System.out.println(result.getInt("id"));
                return result.getInt("id");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<Message> getMessageResult(String searchId, String sql) {
        ResultSet result;
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            ps.setInt(1, Integer.parseInt(searchId));
            result = ps.executeQuery();
            if(result != null) {
                List<Message> messageList = new ArrayList<>();
                while(result.next()) {
                    Message message = new Message();
                    message.setId(result.getString("id"));
                    message.setTitle(result.getString("title"));
                    if(result.getString("title_img") != null){
                        message.setTitleImgPath(TITLE_IMG_PATH + File.separator+result.getString("title_img"));
                    }
                    message.setDate(result.getString("date"));
                    message.setColumnId(String.valueOf(result.getInt("menu_id")));
                    message.setColumnName(result.getString("name"));
                    message.setContent(result.getString("content"));

                    message.setIsPushed(result.getInt("is_push")==1?"��":"��");
                    String filePath = result.getString("file");
                    if(filePath!= null){
                        filePath = filePath.substring(filePath.indexOf("_")+1);
                    }
                    message.setFilePath(filePath);

                    messageList.add(message);
                }
                return messageList.size()>0?messageList:null;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int updateAdmin(Integer adminId,String username,String newPassword) {
        int result = 0;
        String sql = "update admin set name=?,password=? where id=?";
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            ps.setString(1, username);
            ps.setString(2, newPassword);
            ps.setInt(3, adminId);
            result = ps.executeUpdate();
//            System.out.println(result);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int insertMessage(Integer adminId,Message message,String imgPath,String filePath){
        int result = 0;
        String sql = "insert into message(title,title_img,date,content,file,is_push,menu_id,admin_id) values(?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            ps.setString(1, message.getTitle());
            ps.setString(2, imgPath);
            ps.setString(3, DateUtils.today("yyyy-MM-dd"));
            ps.setString(4, message.getContent());
            ps.setString(5, filePath);
            ps.setInt(6, Integer.parseInt(message.getIsPushed()));
            ps.setInt(7, Integer.parseInt(message.getColumnId()));
            ps.setInt(8, adminId);
            result = ps.executeUpdate();
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public int updateMessage(Integer adminId,Message message){
        int result = 0;
        String sql = "update message set title=?,title_img=?,content=?,file=?,is_push=?,menu_id=?,admin_id=? where id=?";
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            ps.setString(1, message.getTitle());
            ps.setString(2, message.getTitleImgPath());
            ps.setString(3, message.getContent());
            ps.setString(4, message.getFilePath());
            ps.setInt(5, Integer.parseInt(message.getIsPushed()));
            ps.setInt(6, Integer.parseInt(message.getColumnId()));
            ps.setInt(7, adminId);
            ps.setString(8, message.getId());
            result = ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String saveFile(MultipartFile file) {
        String millis = System.currentTimeMillis() + "";
        File dir = new File(REAL_FILE_PATH);
        if(!dir.getParentFile().exists()) {
            boolean mk = dir.getParentFile().mkdirs();
            System.out.println("create: ruian_dir " + mk);
        }
        if(!dir.exists()) {
            boolean mk = dir.mkdirs();
            System.out.println("create file_path_dir:" + mk);
        }
        String fileName = millis + "_" + file.getOriginalFilename();
        File uploadFile = new File(REAL_FILE_PATH, fileName);
        if(copyFile(file, uploadFile)) return fileName;
        return null;
    }

    public String saveTitleImg(MultipartFile titleImg) {
        String millis = System.currentTimeMillis() + "";
        File dir = new File(REAL_TITLE_IMG_PATH);
        if(!dir.getParentFile().exists()) {
            boolean mk = dir.getParentFile().mkdirs();
            System.out.println("create: ruian_dir " + mk);
        }
        if(!dir.exists()) {
            boolean mk = dir.mkdirs();
            System.out.println("create title_img_path_dir:" + mk);
        }
        String originalFileName = titleImg.getOriginalFilename();
        String fileName = millis + originalFileName.substring(originalFileName.lastIndexOf("."));
        File uploadFile = new File(REAL_TITLE_IMG_PATH, fileName);
//        System.out.println(fileName);
        if(copyFile(titleImg, uploadFile)) return fileName;
        return null;
    }

    private boolean copyFile(MultipartFile titleImg, File uploadFile) {
        try {
            FileUtils.copyInputStreamToFile(titleImg.getInputStream(), uploadFile);
        } catch(IOException e) {
            e.printStackTrace();
            System.err.println("filePath is not save");
            return false;
        }
        return true;
    }

    public Message updatePathInMessage(Message message, String imgPath,String filePath) {
        ResultSet result1;
        String sql = "select title_img,file from message where id =?";
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            ps.setInt(1, Integer.valueOf(message.getId()));
            result1 = ps.executeQuery();
            if(result1 != null) {
                while(result1.next()){
                    String imgTmp = result1.getString("title_img");
                    String fileTmp = result1.getString("file");
                    if(imgPath != null){
                        deleteFileOrImg(imgTmp,REAL_TITLE_IMG_PATH);
                        message.setTitleImgPath(imgPath);
                    }else {
                        message.setTitleImgPath(imgTmp);
                    }
                    if(filePath != null){
                        deleteFileOrImg(fileTmp,REAL_FILE_PATH);
                        message.setFilePath(filePath);
                    }else {
                        message.setFilePath(fileTmp);
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
//        System.out.println(message.getTitleImgPath()+":::::"+message.getFilePath());
        return message;
    }

    private int deleteFileOrImg(String fileTmp,String path) {
        if(fileTmp != null){
            File dir = new File(path,fileTmp);
            if (dir.exists()) {
                boolean res = dir.delete();
//                System.out.println(dir.getAbsolutePath()+res);
                return res?1:0;
            }
//            System.out.println(dir.getAbsolutePath());
        }
        return 0;
    }


    public int deleteMessage(List<String> messageId){
        int result=0;
        int size = messageId.size();
        StringBuilder sql = new StringBuilder("delete from message where id in( ?");
        for(int i = 1;i < size;i++){
            sql.append(",?");
        }
        sql.append(")");
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql.toString());
            for(int i = 1;i <= size;i++){
                ps.setInt(i, Integer.valueOf(messageId.get(i-1)));
            }
            result = ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void deleteImgAndFile(List<String> messageId) {
        int size = messageId.size();
        ResultSet result;
        StringBuilder sql = new StringBuilder("select title_img,file from message where id in( ?");
        for(int i = 1;i < size;i++){
            sql.append(",?");
        }
        sql.append(")");
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql.toString());
            for(int i = 1;i <= size;i++){
                ps.setInt(i, Integer.valueOf(messageId.get(i-1)));
            }
            result = ps.executeQuery();
            if(result != null) {
                while(result.next()){
                    String titleImg = result.getString("title_img");
                    String file = result.getString("file");
                    deleteFileOrImg(titleImg,REAL_TITLE_IMG_PATH);
                    deleteFileOrImg(file,REAL_FILE_PATH);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Column> getColumn(int i) {
        String sql = "select m1.id id,m1.name name,m2.name supername from menu m1,menu m2 where m1.super_menu_id = m2.id ";
        ResultSet result;
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            result = ps.executeQuery();
//            System.out.println(result);
            if(result != null) {
                List<Column> columnList = new ArrayList<>(getOneColumn(i));
                while(result.next()) {
                    Column column = new Column();
                    column.setId(result.getString("id"));
                    column.setName(result.getString("name")+"-"+result.getString("supername"));

                    columnList.add(column);
                }
                return columnList;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Column>  getOneColumn(int i) {
        String sql="select m1.id,m1.name from menu m1 where m1.super_menu_id is null ";
        if(i == 1){
            sql += "and m1.id not in(select m2.super_menu_id from menu m2 where m2.super_menu_id is not null)";
        }else {
            sql += "";
        }
        ResultSet result;
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            result = ps.executeQuery();
//            System.out.println(result);
            if(result != null) {
                List<Column> columnList = new ArrayList<>();
                while(result.next()) {
                    Column column = new Column();
                    column.setId(result.getString("id"));
                    column.setName(result.getString("name"));

                    columnList.add(column);
                }
                return columnList;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int deleteOldFileOrImg(int messageId,String w) {
        String file=null;
        String sql="select "+w+" as need from message where id = ?";
        ResultSet result;
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
//            ps.setString(1,w);
            ps.setInt(1,messageId);
            result = ps.executeQuery();
            if(result != null) {
                if(result.next()){
                    file = result.getString("need");
//                    System.out.println(file);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
//        System.out.println(file);
        if(file != null && !file.equals("")){
            setPathNull(w,messageId);
            if(w.equals("title_img")){
                deleteFileOrImg(file,REAL_TITLE_IMG_PATH);
            }else {
                deleteFileOrImg(file,REAL_FILE_PATH);
            }
            return 1;
        }
        return -1;//-1�����idû����Ӧ��title_img
    }
    private int setPathNull(String path,Integer messageId){
        String sql="update message set "+path+"=? where id = ?";
        int result=0;
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
//            ps.setString(1,w);
            ps.setString(1,null);
            ps.setInt(2,messageId);
            result = ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int updateColumn(Column column){
        String sql = "update menu set name=? where id=?";
        int result=0;
        try {
            PreparedStatement ps = DbUtil.executePreparedStatement(sql);
            ps.setString(1, column.getName());
            ps.setInt(2, Integer.parseInt(column.getId()));
            result = ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
