package com.weaver.mapper;

import com.weaver.pojo.SocialIMRecentConver;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("socialIMRecentConverMapper")
public interface SocialIMRecentConverMapper {

    int INSERT_IMRecentConver(List<SocialIMRecentConver> socialIMRecentConvers);

    @Update("update social_IMRecentConver set unreadcount = isNULL(unreadcount,0)+1 where targetid = #{targetid}  and userid <> #{userid}")
    int UPDATE_IMRecentConver_UNReadCount_SQLServer(String targetid,String userid);

    List<String> SELECT_IMRecentConver(String targetid);
}
