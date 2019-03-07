package com.weaver.OfflineMessage;

import com.weaver.manager.UserManager;
import com.weaver.mapper.OfOfflineMapper;
import com.weaver.netty.stanza.JID;
import com.weaver.netty.stanza.Message;
import com.weaver.pojo.OfOffline;
import com.weaver.util.FastDateFormat;
import com.weaver.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: 胡烨
 * @Date: 2019/1/29 10:28
 * @Version 1.0
 */
@Component
public class OfflineMessageStore {

    @Autowired
    private UserManager userManager;

    @Autowired
    private OfOfflineMapper ofOfflineMapper;

    public void addMessage(Message message){
        if (message == null){
            return;
        }
        JID recipient = message.getTo();
        String username = recipient.getNode();

        if (username == null || userManager.isRegisteredUser(recipient)){
            return;
        }else if (!recipient.getDomain().equals("weaver")){//不存储发送到外部的消息
            return ;
        }
        //随机生成一个messageid
        Long messageID = PasswordUtil.genRandomNum(19);

        String msgXML = message.getXML().toString();

        OfOffline ofOffline = new OfOffline();
        ofOffline.setUsername(username);
        ofOffline.setMessageID(messageID);
        String crteteDate = FastDateFormat.dateToMillis(new Date());
        ofOffline.setCreationDate(crteteDate);
        ofOffline.setMessageSize(msgXML.length());
        ofOffline.setStanza(msgXML);

        ofOfflineMapper.INSERT_OFFLINE(ofOffline);
    }
}
