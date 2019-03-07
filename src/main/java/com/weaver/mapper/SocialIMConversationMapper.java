package com.weaver.mapper;

import com.weaver.pojo.SocialIMConversation;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("socialIMConversationMapper")
public interface SocialIMConversationMapper {

    int DELETE_IMConversation(String targetId);
    List<String> SELECT_IMRecentConver(String targetId);

    int INSERT_IMConversatio(SocialIMConversation socialIMConversation);
}
