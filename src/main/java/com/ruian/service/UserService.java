package com.ruian.service;

import com.ruian.bean.Message;
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
public class UserService{

    public List<Message> selectMessageByColumnId(String columnId,int index,int count) {
        String sql = "select mess.id,title,title_img,date,content,file from message mess,menu where mess.menu_id=? and mess.menu_id=menu.id order by mess.id desc limit "+index+","+count;
        return getMessageResult(columnId, sql);
    }

    public List<Message> selectMessageByMessageId(String messageId) {
        String sql = "select mess.id,title,title_img,date,content,file from message mess,menu where mess.id=? and mess.menu_id=menu.id";
        return getMessageResult(messageId, sql);
    }

    private List<Message> getMessageResult(String searchId, String sql) {
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
                    }else {
                        message.setTitleImgPath(TITLE_IMG_PATH + File.separator+"default.jpg");
                    }
                    message.setDate(result.getString("date"));
                    message.setContent(result.getString("content"));
                    String filePath = result.getString("file");
                    if(filePath!= null){
                        filePath = filePath.substring(filePath.indexOf("_")+1);
                    }else {
                        filePath = "";
                    }
                    message.setFilePath(filePath);

                    messageList.add(message);
                }

                return messageList;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Message> selectMessageBySuperId(String superId, int count) {
        String sql = "select mess.id,title,title_img,date,content,file from message mess,menu m1,menu m2 where m1.id=? and m1.id = m2.super_menu_id and mess.menu_id=m2.id ORDER BY mess.id DESC limit "+count;
        return getMessageResult(superId, sql);
    }
}
