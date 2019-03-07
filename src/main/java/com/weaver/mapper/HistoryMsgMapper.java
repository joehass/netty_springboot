package com.weaver.mapper;

import com.weaver.pojo.HistoryMsg;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("historyMsgMapper")
public interface HistoryMsgMapper {

    @Select("select * from historyMsg where msgid = #{msgid}")
    List<HistoryMsg> selectHistoryMsgById (@Param("msgid")String msgid);

    int insertHistoryMsg(@Param("historyMsg")HistoryMsg historyMsg);
}
