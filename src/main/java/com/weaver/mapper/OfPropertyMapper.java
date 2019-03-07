package com.weaver.mapper;

import com.weaver.pojo.OfProperty;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OfPropertyMapper {

    @Insert("insert into ofProperty values(#{property},#{value})")
    int insertProperty(@Param("property")String property, @Param("value") String value);

    @Select("select * from ofProperty")
    List<OfProperty> selectAll();
}
