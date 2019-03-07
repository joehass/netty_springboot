package com.weaver.mapper;

import com.weaver.pojo.OfOffline;
import org.springframework.stereotype.Repository;


@Repository("ofOfflineMapper")
public interface OfOfflineMapper {

    int INSERT_OFFLINE(OfOffline ofOffline);
}
