package in.tech_camp.protospace.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.form.UserForm;
import in.tech_camp.protospace.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * ユーザー登録のエントリポイント。
   * UserForm を受け取り、型変換・パスワード暗号化・DB保存を行う。
   */
  public void createUser(UserForm form) {
    UserEntity entity = toEntity(form);
    entity.setPassword(encodePassword(form.getPassword()));
    userRepository.insert(entity);
  }

  /**
   * UserForm を UserEntity に型変換する。
   * パスワードは平文のため、呼び出し元で暗号化してから setPassword すること。
   */
  public UserEntity toEntity(UserForm form) {
    UserEntity entity = new UserEntity();
    entity.setName(form.getName());
    entity.setEmail(form.getEmail());
    entity.setPassword(form.getPassword());
    entity.setProfile(form.getProfile());
    entity.setOccupation(form.getOccupation());
    entity.setPosition(form.getPosition());
    return entity;
  }

  /**
   * パスワードを暗号化して返す。
   */
  public String encodePassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }
}
