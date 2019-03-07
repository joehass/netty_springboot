package com.weaver.plugin.group.IQ;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaver.netty.stanza.IQ;
import com.weaver.netty.stanza.JID;
import com.weaver.netty.xml.XMLElement;
import com.weaver.plugin.handler.IQHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Component
public class GroupIQHandler extends IQHandler implements Serializable {

    //唯一标识一个iqhandler
    private String moduleName = "http://weaver.com.cn/group";

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public IQ handleIQ(IQ packet) {

        HashMap<Object, Object> result = new HashMap<Object, Object>();
        JID fromJID = packet.getFrom();
        IQ reply = IQ.createResultIQ(packet);
        XMLElement elereply = reply.getXML();
        XMLElement ele = elereply.addChild("query");
        JSONObject json1 = packet.getJson();
        String method = (String) json1.get("method");

        if (method.equals("createGroup")){
            JSONArray members = json1.getJSONArray("members");
            String groupid = UUID.randomUUID().toString().toLowerCase();
            String groupName = (String) json1.get("groupName");
            List<String> mems = new ArrayList<String>();
            int count = 0;
            String tempGroupName = "";
            for (Object obj : members.toArray()) {
                String username = (String) obj;
                mems.add(username);
                // 群名称为空时，群名称值取群成员前5人名称，以逗号隔开
                if (StringUtils.isEmpty(groupName) && count <= 5) {
                    if (count < 5) {
                        //try {
//                            tempGroupName += UserManager.getInstance().getUser(username).getName();
//                        } catch (UserNotFoundException e) {
//
                       // }
                        if (count < 4) {
                            tempGroupName += ",";
                        }
                    } else {
                        tempGroupName += "...";
                    }
                    count++;
                }
            }
            if (StringUtils.isEmpty(groupName)) {
                groupName = tempGroupName;
            }

            List<String> adminis = new ArrayList<String>();
            adminis.add(fromJID.getNode());
            mems.remove(fromJID.getNode());
            if (members.size() + adminis.size() > 500) {
                return null;
            }

            String groupIconUrl = null;
            if (json1 != null && json1.containsKey("groupIconUrl")) {
                groupIconUrl = json1.getString("groupIconUrl");
            }
            String groupIconType = "1";
            if (json1 != null && json1.containsKey("groupIconType")) {
                groupIconType = json1.getString("groupIconType");
            }
            String groupIconUserIds = null;
            if (json1 != null && json1.containsKey("groupIconUserIds")) {
                groupIconUserIds = json1.getString("groupIconUserIds");
            }
            String content = null;
            if (json1 != null && json1.containsKey("content")) {
                content = json1.getString("content");
            }
//            Group group = createGroup(groupid, groupName, mems, adminis, groupIconUrl, groupIconType,
//                    groupIconUserIds, content);
            result.put("groupId", groupid);
            result.put("groupName", groupName);
            result.put("members", mems);
            result.put("admins", adminis);

            ele.setText(JSON.toJSONString(result).toString());

            return reply;
        }

        return reply;
    }
}
