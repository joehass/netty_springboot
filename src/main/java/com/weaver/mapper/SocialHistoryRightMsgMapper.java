package com.weaver.mapper;

import com.weaver.pojo.HistoryMsg;
import com.weaver.pojo.SocialHistoryMsgRight;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("socialHistoryRightMsgMapper")
public interface SocialHistoryRightMsgMapper {

    @Select("select * from historyMsg where msgid = #{msgid}")
    List<HistoryMsg> selectHistoryMsgById(@Param("msgid") String msgid);

    int insertHistoryMsgRightBatch(List<SocialHistoryMsgRight> historyMsg);
}
