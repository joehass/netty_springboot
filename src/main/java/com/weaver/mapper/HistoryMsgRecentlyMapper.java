package com.weaver.mapper;

import com.weaver.pojo.HistoryMsg;
import com.weaver.pojo.HistoryMsgRecently;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("HistoryMsgRecentlyMapper")
public interface HistoryMsgRecentlyMapper {

    @Select("select * from historyMsgRecently where msgid = #{msgid}")
    List<HistoryMsgRecently> selectHistoryMsgRecentlyById(@Param("msgid") String msgid);

    int insertHistoryMsgRecently(@Param("historyMsgRecently") HistoryMsg historyMsg);
}
