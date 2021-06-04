package com.plunger.mapper.plunger;

import com.plunger.bean.plunger.Dict;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DictMapper {

    @Select("select * from dict order by type,name")
    List<Dict> findAll();

    @Select("select * from dict where unid = #{unid}")
    Dict findByUnid(@Param("unid") String unid);

    @Select("select * from dict where type = #{type} and name = #{name} limit 1")
    Dict findByTypeAndName(@Param("type") String type, @Param("name") String name);

    @Select("select * from dict where type = #{type}")
    List<Dict> findByType(@Param("type") String type);

    @Insert("insert into dict(unid, type, name, value) values(#{unid}, #{type}, #{name}, #{value})")
    int insert(Dict dict);

    @Update("update dict set type=#{type},name=#{name},value=#{value} where unid=#{unid}")
    void update(Dict dict);

    @Delete("delete from dict where unid = #{unid}")
    void deleteByUnid(String unid);

}
