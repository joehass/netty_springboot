package com.weaver.plugin.multiRouter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weaver.Interceptor.PacketInterceptor;
import com.weaver.InternalComponet;
import com.weaver.OfflineMessage.OfflineMessageListener;
import com.weaver.OfflineMessage.OfflineMessageStrategy;
import com.weaver.component.container.NettyPlugin;
import com.weaver.component.listener.PresenceEventDispatcher;
import com.weaver.component.listener.PresenceEventListener;
import com.weaver.manager.session.SessionManager;
import com.weaver.netty.stanza.JID;
import com.weaver.netty.stanza.Message;
import com.weaver.netty.stanza.Presence;
import com.weaver.netty.stanza.Stanza;
import com.weaver.netty.xml.XMLElement;
import com.weaver.plugin.util.DatabaseUtil;
import com.weaver.plugin.util.JIDUtil;
import com.weaver.pojo.RouterNode;
import com.weaver.session.LocalClientSession;
import com.weaver.util.XMPPDateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class MultiRouterPlugin implements PacketInterceptor, OfflineMessageListener, NettyPlugin, PresenceEventListener, Serializable {

    @Autowired
    private InternalComponet internalComponet;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private DatabaseUtil databaseUtil;

    @Autowired
    private OfflineMessageStrategy offlineMessageStrategy;

    private String sb = "private_";

    @Override
    public void interceptPacket(Stanza stanza, LocalClientSession session, Boolean incoming, Boolean processed) {

        if (stanza instanceof Message){
            Message message = (Message)stanza;
            JID toJID = message.getTo();
            JID fromJID = message.getFrom();
            String id = message.getId();
            //如果是第一次进入
            if (!processed && incoming){
                if (id != null && message.getType() == Message.Type.chat && fromJID.getResource() == null && message.getTo().getResource().startsWith("group.")){

                }
                if (id != null && message.getType() == Message.Type.chat && fromJID.getResource() != null && fromJID.getResource().equals(RouterNode.NODE_PC)){
                    //判断消息中是否带有时间戳
                    XMLElement time = message.getChildElement("time", "urn:xmpp:time");
                    //手动加入时间戳
                    if (time == null){
                        XMLElement element = message.addExtension("time", "urn:xmpp:time");
                        element.addChild("stamp");
                        element.setText(XMPPDateTimeFormat.format(new Date()));
                    }
                    String data = message.getBody();
                    JSONObject json = JSON.parseObject(data);
                    if(json.containsKey("objectName")&&!json.getString("objectName").equals("FW:CountMsg")&&
                            !json.getString("objectName").equals("FW:SysMsg")&&!message.getFrom().getDomain().startsWith("confirm.")){
                        //服务器向客户端发送回执
                        doAckSender(message,0);
                        //记录历史消息
                        databaseUtil.recordHistoryMessage(message);

                        //如果不是清除未读消息
                        //这里进行推送，暂时不理会
                        if (!json.getString("objectName").equals("FW:ClearUnreadCount")) {
                            String userid = JIDUtil.conNodeToID(message.getFrom().getNode());
                            String targetid = "";
                            String targettype = "";
                            String targetname = "";
                            String msgcontent = "";
                            Set<String> receverIds = new HashSet<>();

                            //如果是消息撤回小灰条或者是投票小灰条，暂不处理
                            if (json.getString("objectName").equals("RC:InfoNtf") || json.getString("objectName").equals("RC:InfoNtfVote")) {

                            } else {
                                json = JSON.parseObject(data);
                                if (json.containsKey("extra")) {
                                    String extra = json.getString("extra");
                                    json = JSON.parseObject("extra");

                                    if (json.containsKey("msg_id")) {
                                        String msg_id = json.getString("msg_id");
                                        //如果是私人消息
                                        if (json.containsKey("isPrivate")) {
                                            targettype = "0";
                                            targetid = sb + targetid;
                                        }
                                        String fId = fromJID.getNode();
                                        if (fId != null && fId.startsWith("s_notice")){
                                            targettype = "101";
                                        }
                                        databaseUtil.updateCon(userid,targetid,targettype,targetname,msgcontent,receverIds,msg_id,true);
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    public int doAckSender(Message message, int ret){
        JID ackFrom = JID.jid(message.getTo().getNode(),"confirm.weaver",null);
        Message ackSenderMessage = message.createCopy();
        ackSenderMessage.setTo(message.getFrom());
        ackSenderMessage.setType(Message.Type.headline);
        sendPacket(ackSenderMessage);
        return 0;

    }

    public void sendPacket(Stanza message){
        internalComponet.sendPacket(message);
    }

    @Override
    public void initializePlugin() {
        PresenceEventDispatcher instance = PresenceEventDispatcher.getInstance();
        instance.addListener(this);

        offlineMessageStrategy.addListener(this);
    }

    @Override
    public void availableSession(LocalClientSession session, Presence presence) {

        //通知用户在线
        int msgSize = 0;
        if (presence.getFrom().getResource().equals(RouterNode.NODE_PC)){//pc端上线
            JID fJID = JID.jid(presence.getFrom().getNode(),"weaver",RouterNode.NODE_MOBILE);//创建手机端的jid
            LocalClientSession session1 = sessionManager.getSession(fJID);
        }

    }

    @Override
    public void messageBounced(Message message) {

    }

    @Override
    public void messageStored(Message message) {
        if (message.getTo()!=null && message.getTo().getResource() == null){
            XMLElement delay = message.getChildElement("delay", "urn:xmpp:delay");
            if (delay == null){

            }

        }
    }
}