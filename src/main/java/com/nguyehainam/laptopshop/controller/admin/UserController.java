package com.nguyehainam.laptopshop.controller.admin;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.nguyehainam.laptopshop.domain.User;
import com.nguyehainam.laptopshop.service.UploadService;
import com.nguyehainam.laptopshop.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;
    private final UploadService uploadService;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            UploadService uploadService) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.uploadService = uploadService;

    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model) {
        List<User> users = this.userService.getAllUsers();
        model.addAttribute("users1", users);
        return "admin/user/show";
    }

    @GetMapping("/admin/user/create") // GET
    public String getCreateUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @PostMapping("/admin/user/create")
    public String createUserPage(Model model,
            @ModelAttribute("newUser") @Valid User newUserEntity,
            BindingResult newUserBindingResult,
            @RequestParam("avatarFile") MultipartFile avatarFile) {

        // validate
        if (newUserBindingResult.hasErrors()) {
            return "admin/user/create";
        }

        // xử lý upload file và mã hóa mật khẩu
        String avatar = this.uploadService.handleSaveUploadFile(avatarFile, "avatar");
        String hashPassword = this.passwordEncoder.encode(newUserEntity.getPassword());

        newUserEntity.setAvatar(avatar);
        newUserEntity.setPassword(hashPassword);
        newUserEntity.setRole(this.userService.getRoleByName(newUserEntity.getRole().getName()));

        // lưu user
        this.userService.handleSaveUser(newUserEntity);
        return "redirect:/admin/user";
    }

}
