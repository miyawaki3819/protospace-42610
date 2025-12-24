package in.tech_camp.protospace.mapper;

import in.tech_camp.protospace.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO users (email, encrypted_password, name, profile, occupation, position) " +
            "VALUES (#{email}, #{encryptedPassword}, #{name}, #{profile}, #{occupation}, #{position})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);
}

