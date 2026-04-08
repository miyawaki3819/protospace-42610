package in.tech_camp.protospace.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace.entity.UserEntity;

@Mapper
public interface UserRepository {
  @Insert("INSERT INTO users (name, email, encrypted_password, profile, occupation, position) VALUES (#{name}, #{email}, #{password}, #{profile}, #{occupation}, #{position})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(UserEntity user);

  @Select("SELECT EXISTS(SELECT 1 FROM users WHERE email = #{email})")
  boolean existsByEmail(String email);

  @Select("SELECT * FROM users WHERE email = #{email}")
  @Results(value = {
    @Result(property = "password", column = "encrypted_password")
  })
  UserEntity findByEmail(String email);

  /** プロトタイプの user 結合用（nested で prototypes を取らない／再帰関連の不整合を避ける）。 */
  @Select("SELECT id, name, email, profile, occupation, position FROM users WHERE id = #{id}")
  UserEntity findById(Integer id);
}