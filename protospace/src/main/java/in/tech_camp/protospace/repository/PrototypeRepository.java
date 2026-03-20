package in.tech_camp.protospace.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace.entity.PrototypeEntity;

@Mapper
public interface PrototypeRepository {

  @Select("SELECT * FROM prototypes")
  @Results(value = {
    @Result(property = "user", column = "user_id",
            one = @One(select = "in.tech_camp.protospace.repository.UserRepository.findById"))
  })
  List<PrototypeEntity> findAll();

  @Select("SELECT * FROM prototypes WHERE id = #{id}")
  @Results(value = {
    @Result(property = "user", column = "user_id",
            one = @One(select = "in.tech_camp.protospace.repository.UserRepository.findById"))
  })
  PrototypeEntity findById(Integer id);

  @Insert("INSERT INTO prototypes (title, catch_copy, concept, image_name, image_type, image_data, user_id) VALUES (#{title}, #{catchCopy}, #{concept}, #{imageName}, #{imageType}, #{imageData}, #{userId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(PrototypeEntity prototype);

  @Select("SELECT * FROM prototypes WHERE user_id = #{userId}")
  List<PrototypeEntity> findByUserId(Integer userId);
}
