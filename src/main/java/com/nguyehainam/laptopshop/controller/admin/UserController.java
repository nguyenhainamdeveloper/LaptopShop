package com.nguyehainam.laptopshop.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String getUserPage(Model model,
            @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                // convert from String to int
                page = Integer.parseInt(pageOptional.get());
            } else {
                // page = 1
            }
        } catch (Exception e) {
            // page = 1
            // TODO: handle exception
        }

        Pageable pageable = PageRequest.of(page - 1, 5);
        Page<User> usersPage = this.userService.getAllUsers(pageable);
        List<User> users = usersPage.getContent();
        model.addAttribute("users1", users);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
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

    @GetMapping("/admin/user/update/{id}") // GET
    public String getUpdateUserPage(Model model, @PathVariable long id) {
        Optional<User> currentUser = this.userService.getUserById(id);

        if (currentUser.isEmpty()) {
            throw new RuntimeException("User not found with id " + id);
        }

        model.addAttribute("newUser", currentUser.get());
        return "admin/user/update";
    }

    @PostMapping("/admin/user/update")
    public String postUpdateUser(Model model, @ModelAttribute("newUser") User updatedUser) {
        Optional<User> currentUser = this.userService.getUserById(updatedUser.getId());

        if (currentUser.isEmpty()) {
            throw new RuntimeException("User not found with id " + updatedUser.getId());
        }

        User userToUpdate = currentUser.get();
        userToUpdate.setAddress(updatedUser.getAddress());
        userToUpdate.setFullName(updatedUser.getFullName());
        userToUpdate.setPhone(updatedUser.getPhone());

        this.userService.handleSaveUser(userToUpdate);

        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("newUser", new User());
        return "admin/user/delete";
    }

    @PostMapping("/admin/user/delete")
    public String postDeleteUser(Model model, @ModelAttribute("newUser") User deletedUser) {
        this.userService.deleteAUser(deletedUser.getId());
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        Optional<User> userOptional = this.userService.getUserById(id);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with id " + id);
        }

        model.addAttribute("user", userOptional.get());
        return "admin/user/detail";
    }

}
