package in.tech_camp.protospace.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.form.UserForm;
import in.tech_camp.protospace.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);
  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  public void createUser(UserForm form) {
        logger.info("Creating user with email: {}", form.getEmail());
        UserEntity user = new UserEntity();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setProfile(form.getProfile());
        user.setOccupation(form.getOccupation());
        user.setPosition(form.getPosition());

        logger.info("Inserting user: email={}, name={}", user.getEmail(), user.getName());
        userRepository.insert(user);
        logger.info("User created successfully with id: {}", user.getId());
    }
    
  public void createUserWithEncryptedPassword(UserEntity userEntity) {
    String encodedPassword = encodePassword(userEntity.getPassword());
    userEntity.setPassword(encodedPassword);
    userRepository.insert(userEntity);
  }

  private String encodePassword(String password) {
    return passwordEncoder.encode(password);
  }
}
