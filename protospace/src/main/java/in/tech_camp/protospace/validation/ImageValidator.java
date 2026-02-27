package in.tech_camp.protospace.validation;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Override
    public void initialize(ValidImage constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("画像ファイルのサイズは10MB以下にしてください")
                    .addConstraintViolation();
            return false;
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("画像ファイルを選択してください")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
