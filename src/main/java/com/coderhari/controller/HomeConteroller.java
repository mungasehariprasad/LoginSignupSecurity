package com.coderhari.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.coderhari.entity.User;
import com.coderhari.repository.UserRepository;
import com.coderhari.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeConteroller {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void CommonUser(Principal p, Model model) {
        if (p != null) {
            String email = p.getName();
            User user = userRepository.findByEmail(email);
            model.addAttribute("user", user);
        }

    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/signin")
    public String login() {
        return "login";
    }
    // @GetMapping("/user/home")
    // public String home() {
    // return "home";
    // }
    // @GetMapping("/user/profile")
    // public String profile(Principal p, Model model) {
    // String email = p.getName();
    // User user = userRepository.findByEmail(email);
    // model.addAttribute("user", user);

    // return "profile";
    // }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, HttpSession session, Model model, HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        // System.out.println(url);
        url = url.replace(request.getServletPath(), "");
        // System.out.println(url); http://localhost:8080/verify?code=2343jvsddfdfj?

        User u = userService.saveUser(user, url);

        if (u != null) {

            session.setAttribute("msg", "Register successfully");

        } else {

            session.setAttribute("msg", "Something wrong server");
        }

        return "redirect:/register";
    }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code, Model model) {
        boolean f = userService.verifyAccount(code);
        if (f) {

            model.addAttribute("msg", "Sucessfully your accout is verified");

        } else {
            model.addAttribute("msg", "May be your verifcation code is incorrect or already verify");
        }
        return "message";

    }
}
