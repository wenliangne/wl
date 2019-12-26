package com.wenliang.test.dao;

import com.wenliang.mapper.annotations.Delete;
import com.wenliang.mapper.annotations.Insert;
import com.wenliang.mapper.annotations.Select;
import com.wenliang.mapper.annotations.Update;
import com.wenliang.test.domain.User;

import java.util.List;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：
 */
public interface UserDao {

    @Select("select * from user")
    public List<User> findAll();

    @Select("select * from user where email=#{email}")
    public List<User> findByEmail(User user);

    @Select("select * from user where username=#{username}")
    public User findByUsername(String username);

    @Select("select * from user where password=#{password}")
    public List<User> findByPassword(User user);

    @Select("select * from user where username=#{username} and password=#{password}")
    public User findByUsernameAndPassword(User user);

    @Select("select * from user where username=#{username} and password=#{password}")
    public User findByUsernameAndPassword2(String username, String password);

    @Insert("insert into user values(null,#{username},#{password},#{gender},#{birthday},#{email},#{status})")
    public boolean insert(User user);

    @Update("update user set password=#{password},gender=#{gender} where username=#{username}")
    public int update(User user);

    @Delete("delete from user where username=#{username}")
    public void delete(User user);
}
