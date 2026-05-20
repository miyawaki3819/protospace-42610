package in.tech_camp.protospace.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace.entity.CommentEntity;

@Mapper
public interface CommentRepository {

    @Select("SELECT * FROM comments WHERE prototype_id = #{prototypeId} ORDER BY id ASC")
    @Results(value = {
            @Result(property = "text", column = "content"),
            @Result(property = "user", column = "user_id",
                    one = @One(select = "in.tech_camp.protospace.repository.UserRepository.findById")),
            @Result(property = "prototype", column = "prototype_id",
                    one = @One(select = "in.tech_camp.protospace.repository.PrototypeRepository.findByIdWithoutComments"))
    })
    List<CommentEntity> findByPrototypeId(Integer prototypeId);

    @Insert("INSERT INTO comments (content, user_id, prototype_id) VALUES (#{text}, #{user.id}, #{prototype.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CommentEntity comment);
}
