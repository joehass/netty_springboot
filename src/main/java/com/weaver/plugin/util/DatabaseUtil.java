package com.weaver.plugin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weaver.mapper.HistoryMsgMapper;
import com.weaver.mapper.SocialHistoryRightMsgMapper;
import com.weaver.mapper.SocialIMConversationMapper;
import com.weaver.mapper.SocialIMRecentConverMapper;
import com.weaver.netty.stanza.JID;
import com.weaver.netty.stanza.Message;
import com.weaver.pojo.HistoryMsg;
import com.weaver.pojo.SocialHistoryMsgRight;
import com.weaver.pojo.SocialIMConversation;
import com.weaver.pojo.SocialIMRecentConver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: 胡烨
 * @Date: 2019/1/21 11:09
 * @Version 1.0
 */
@Component
public class DatabaseUtil {

    @Autowired
    private HistoryMsgMapper historyMsgMapper;

    @Autowired
    private SocialHistoryRightMsgMapper socialHistoryRightMsgMapper;

    @Autowired
    private SocialIMConversationMapper socialIMConversationMapper;

    @Autowired
    private SocialIMRecentConverMapper socialIMRecentConverMapper;

    public static final List<String> GRAY_BAR_TYPE = new ArrayList<>();
    static {
        GRAY_BAR_TYPE.add("RC:DizNtf");//群组小灰条
        GRAY_BAR_TYPE.add("RC:InfoNtf");//红包小灰条
        GRAY_BAR_TYPE.add("FW:InfoNtf");//云盘取消分享消息
        GRAY_BAR_TYPE.add("RC:InfoNtfVote");//投票消息
    }

    private static final List<String> PERSIST = new ArrayList<>();
    static {
        PERSIST.add("FW:CountMsg");
        PERSIST.add("FW:SyncQuitGroup");
        PERSIST.add("FW:SyncMsg");
        PERSIST.add("FW:SysMsg");
        PERSIST.add("FW:ClearUnreadCount");
    }

    /**
     * 存储历史消息
     * @param message
     * @return
     */
    public int recordHistoryMessage(Message message){
        JSONObject jsonObject = JSON.parseObject(message.getBody());
        if (jsonObject.containsKey("objectName")){
            String objectName = jsonObject.getString("objectName");
            if (GRAY_BAR_TYPE.contains(objectName)){
                return 0;
            }else if (!PERSIST.contains(objectName)){
                return dealNormalMsg(message, jsonObject);
            }
        }
        return 0;
    }

    private int dealNormalMsg(Message message,JSONObject bodyObject){
        int ret = 0;
        String domain = "weaver";
        JID toJID = message.getTo();
        JID fromJID = message.getFrom();
        String objectName = bodyObject.getString("objectName");
        Set<String> userRightList = new HashSet<>();
        Map<String,String>map = new HashMap<>();

        //extra中包含msgid,如果没有msgid，则不保存历史消息
        if (!bodyObject.containsKey("extra")){
            ret = 2503;
            return ret;
        }

        String extra = bodyObject.getString("extra");
        JSONObject jObject = null;
        if (extra != null && !"null".equals(extra.trim()) && extra.indexOf("\"msg_id\"") != -1){
            jObject = JSONObject.parseObject(extra);
            if (jObject.containsKey("msg_id")){
                String msgid = jObject.getString("msg_id");
                map.put("msgid",msgid);
            }
        }

        String msgid = map.get("msgid");
        if (msgid == null || msgid.isEmpty()){
            ret = 2503;
            return ret;
        }
        map.put("classname",objectName);
        map.put("fullAmount",bodyObject.toString());
        //判断是否是群聊消息，如果是群聊，则targetType是1，否则是0
        int targetType = ("group." + domain).equals(toJID.getDomain()) ? 1 : 0;
        if (jObject != null && jObject.containsKey("isPrivate")){//判断是否为密聊消息
            targetType = 9;
        }
        String fId = fromJID.getNode();//获得发送人的id
        //这些暂时不知道想判断什么
        if (fId != null && fId.startsWith("s_notice")) {
            targetType = 102;
        }
        if (fId != null && fId.startsWith("s_assist")) {
            targetType = 103;
        }
        if (fId != null && fId.startsWith("s_phone")) {
            targetType = 104;
        }
        if (fId != null && fId.startsWith("a_")) {
            targetType = 112;
        }

        String fromUserId = null;
        String targetId = null;
        String GroupId = null;

        if (targetType == 0 || targetType == 9 || targetType == 102 || targetType == 103 || targetType == 104 || targetType == 122){
            fromUserId = getRealUserId(fromJID.getNode());
            targetId = getRealUserId(toJID.getNode());
            GroupId = genaralSessionID(fromUserId,targetId);
            userRightList.add(fromUserId);
            userRightList.add(targetId);
        }else if (targetType == 1){//如果是群聊消息，群聊消息暂不处理

        }

        map.put("fromUserId",fromUserId);
        map.put("targetId",targetId);
        map.put("targetType",String.valueOf(targetType));
        map.put("GroupId",GroupId);
        String imageUri = null;
        if (bodyObject.containsKey("imgUrl")){//判断是否包含图片
            imageUri = bodyObject.getString("imgUrl");
        }else if (bodyObject.containsKey("imageUri")){
            imageUri = bodyObject.getString("imageUri");
        }
        map.put("imageUri",imageUri);
        map.put("extra",bodyObject.containsKey("extra") ? bodyObject.getString("extra") : null);
        map.put("msgContent",bodyObject.containsKey("content") ? bodyObject.getString("content") : null);
        map.put("xmppid",message.getId());

        return record2Db(map,userRightList);
    }

    private int record2Db(Map<String,String>map,Set<String> userRightList){
        String msgContent = map.get("msgContent");
        String groupId = map.get("GroupId");
        String msgid = map.get("msgid");
        String xmppid = map.get("xmppid");
        //如果消息已经存在
        if (isHIstoryMsgExisted(msgid)){
            return 0;
        }
        //如果消息长度大于4，那么中间的消息内容将其加密
        if (msgContent.length() > 4){
            String mskMsgContent = msgContent.substring(0,2) + "******" + msgContent.substring(msgContent.length()-2,msgContent.length());
        }

        SimpleDateFormat SDF_ALL_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String sendTime = SDF_ALL_TIME_FORMAT.format(System.currentTimeMillis());
        int exceptionPos = 0;
        int ret = 0;
        boolean abortTransaction = false;

        HistoryMsg historyMsg = new HistoryMsg();
        historyMsg.setFromUserId(map.get("fromUserId"));
        historyMsg.setTargetId(map.get("targetId"));
        historyMsg.setTargetType(map.get("targetType"));
        historyMsg.setGroupId(map.get("GroupId"));
        historyMsg.setClassname(map.get("classname"));
        historyMsg.setMsgContent(map.get("msgContent"));
        historyMsg.setExtra(map.get("extra"));
        historyMsg.setImageUri(map.get("imageUri"));
        historyMsg.setDateTime(sendTime);
        historyMsg.setMsgId(map.get("msgid"));
        historyMsg.setFullAmount(map.get("fullAmount"));

        historyMsgMapper.insertHistoryMsg(historyMsg);

        List<SocialHistoryMsgRight> list = new ArrayList<>();
        for (String useridString:userRightList){
            SocialHistoryMsgRight socialHistoryMsgRight = new SocialHistoryMsgRight();
            socialHistoryMsgRight.setUserid(useridString);
            socialHistoryMsgRight.setMsgid(map.get("msgId"));
            list.add(socialHistoryMsgRight);
        }

        socialHistoryRightMsgMapper.insertHistoryMsgRightBatch(list);
        return ret;
    }

    private boolean isHIstoryMsgExisted(String id){
        boolean result = false;
        List<HistoryMsg> historyMsgs = historyMsgMapper.selectHistoryMsgById(id);
        if (historyMsgs.size() != 0){
            result = true;
        }
        return result;
    }

    private String getRealUserId(String imUserId){
        String result = null;
        if (imUserId != null && !imUserId.isEmpty() && imUserId.indexOf("|") > 0){
            result = imUserId.substring(0,imUserId.indexOf("|"));
        }
        return result;
    }

    private String genaralSessionID(String str1,String str2){
        if (str1.compareToIgnoreCase(str2) >= 0)
            return str2 + "-" + str1;
        else
            return str1 + "-" + str2;
    }

    //更新会话
    public void updateCon(String userid,String targetid,String targettype,String targetname,String msgcontent,
                          Set<String> receiveIds,String msgId,boolean needRecordUnredCount){

        Long updatetime = new java.util.Date().getTime();

        socialIMConversationMapper.DELETE_IMConversation(targetid);

        List<String> userList = socialIMConversationMapper.SELECT_IMRecentConver(targetid);

        SocialIMConversation socialIMConversation = new SocialIMConversation();
        socialIMConversation.setUserid(userid);
        socialIMConversation.setTargetid(targetid);
        socialIMConversation.setTargetname(targetname);
        socialIMConversation.setLasttime(updatetime);
        socialIMConversation.setMsgcontent(msgcontent);
        socialIMConversation.setIsopenfire(1);
        socialIMConversation.setMsgid(msgId);

        //保存会话
        socialIMConversationMapper.INSERT_IMConversatio(socialIMConversation);

        Set<String> receiveIdsTemp = new HashSet<>();
        receiveIdsTemp.addAll(receiveIds);
        receiveIdsTemp.removeAll(userList);
        List<SocialIMRecentConver> list = new ArrayList<>();
        if (receiveIdsTemp.size() > 0){
            for (String receiveid : receiveIdsTemp){
                SocialIMRecentConver socialIMRecentConver = new SocialIMRecentConver();
                socialIMRecentConver.setUserid(receiveid);
                socialIMRecentConver.setTargetid(targetid);
                socialIMRecentConver.setIsdel(0);
                socialIMRecentConver.setUpdatetime(updatetime);
                socialIMRecentConver.setIstop(0);

                list.add(socialIMRecentConver);

            }
            socialIMRecentConverMapper.INSERT_IMRecentConver(list);
        }

        //是否需要更新未读数
        if (needRecordUnredCount){
            //更新未读数
            socialIMRecentConverMapper.UPDATE_IMRecentConver_UNReadCount_SQLServer(targetid,userid);
        }

    }
}
