package com.github.kiwiwin.mybatis.mapper;

import com.github.kiwiwin.core.User;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface UserMapper {
    void insert(@Param("info") Map<String, Object> info);

    User uuid(@Param("uuid") String uuid);
}
