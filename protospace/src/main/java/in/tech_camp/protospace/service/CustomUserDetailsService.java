package in.tech_camp.protospace.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.repository.UserRepository;
import in.tech_camp.protospace.custom_user.CustomUserDetail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        logger.info("Attempting to load user with email: {}", email);
        // email でユーザー取得
        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            logger.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found: " + email);
        }

        logger.info("User found: {} (ID: {})", user.getEmail(), user.getId());
        // UserDetails に変換して返す
        return new CustomUserDetail(user);
    }
}