package com.xcb.blog.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.xcb.blog.model.User;

@Mapper
public interface UserDao {
	String TABLE_NAME = " user ";
    String INSERT_FIELDS = " name, password, salt, head_url ,role ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;
    
    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values (#{name},#{password},#{salt},#{headUrl},#{role})"})
    public void insertUser(User user);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where id=#{id}"})
    public User seleteById(int id);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where name=#{name}"})
    public User seleteByName(@Param("name") String name);

    @Delete({"delete from",TABLE_NAME,"where id=#{id}"})
    public void deleteById(int id);
    
}
