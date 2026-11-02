package io.github.ren1208.digital_library.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordForm {
    @NotEmpty(message = "Пароль не может быть пустым")
    @Size(min = 6, message = "Минимум 6 символов")
    private String newPassword;

    @NotEmpty
    private String confirmPassword;
}