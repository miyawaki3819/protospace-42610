package in.tech_camp.protospace.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import in.tech_camp.protospace.entity.PrototypeEntity;

@Mapper
public interface PrototypeRepository {

  @Select("SELECT * FROM prototypes")
  @Results(value = {
    @Result(property = "id", column = "id", id = true),
    @Result(property = "user", column = "user_id",
            one = @One(select = "in.tech_camp.protospace.repository.UserRepository.findById")),
    @Result(property = "comments", column = "id",
            many = @Many(select = "in.tech_camp.protospace.repository.CommentRepository.findByPrototypeId"))
  })
  List<PrototypeEntity> findAll();

  @Select("SELECT * FROM prototypes WHERE id = #{id}")
  @Results(value = {
    @Result(property = "id", column = "id", id = true),
    @Result(property = "user", column = "user_id",
            one = @One(select = "in.tech_camp.protospace.repository.UserRepository.findById")),
    @Result(property = "comments", column = "id",
            many = @Many(select = "in.tech_camp.protospace.repository.CommentRepository.findByPrototypeId"))
  })
  PrototypeEntity findById(Integer id);

  @Select("SELECT * FROM prototypes WHERE id = #{id}")
  @Results(value = {
    @Result(property = "id", column = "id", id = true),
    @Result(property = "user", column = "user_id",
            one = @One(select = "in.tech_camp.protospace.repository.UserRepository.findById"))
  })
  PrototypeEntity findByIdWithoutComments(Integer id);

  @Insert("INSERT INTO prototypes (title, catch_copy, concept, image_name, image_type, image_data, user_id) VALUES (#{title}, #{catchCopy}, #{concept}, #{imageName}, #{imageType}, #{imageData}, #{user.id})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(PrototypeEntity prototype);

  @Update("UPDATE prototypes SET title = #{title}, catch_copy = #{catchCopy}, concept = #{concept}, image_name = #{imageName}, image_type = #{imageType}, image_data = #{imageData} WHERE id = #{id}")
  void update(PrototypeEntity prototype);

  @Delete("DELETE FROM prototypes WHERE id = #{id}")
  void deleteById(Integer id);

  @Select("SELECT * FROM prototypes WHERE user_id = #{userId}")
  List<PrototypeEntity> findByUserId(Integer userId);
}
