package com.weaver.mapper;

import com.weaver.pojo.OfUser;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("ofUserMapper")
public interface OfUserMapper {

    @Select("SELECT * FROM ofUser ORDER BY username")
    List<OfUser>loadUser();

    @Select("SELECT salt, serverKey, storedKey, iterations, name, email, creationDate, modificationDate FROM ofUser WHERE username=#{username}")
    OfUser LOAD_USER(String username);

    @Select("SELECT plainPassword,encryptedPassword FROM ofUser WHERE username=#{username}")
    OfUser LOAD_PASSWORD(String username);
}
