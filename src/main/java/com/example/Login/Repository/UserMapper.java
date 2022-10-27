package com.example.Login.Repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.Login.Entity.User;

public interface UserMapper {
	
	@Insert("INSERT INTO USERS(emai, password, uuid) VALUES(email=#{email}, password=#{password}, uuid=#{uuid})")
	User insertUser(User user);
	
	@Select("SELECT * FROM USERS WHERE email = #{email}")
	Optional<User> getUser(@Param("email") String email);
	
	@Select("SELECT * FROM USERS")
	Optional<List<User>> getAllUser();
	
	@Update("UPDATE USERS SET password=#{user.password} WHERE email=#{user.email}")
	Optional<User> updateUser(User user);
	
	@Delete("DELETE * FROM USERS WHERE email = #{email}")
	void removeUser(@Param("email") String email);
}
