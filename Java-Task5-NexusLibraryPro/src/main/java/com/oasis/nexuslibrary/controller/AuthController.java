package com.oasis.nexuslibrary.controller;

import com.oasis.nexuslibrary.dto.MemberDto;
import com.oasis.nexuslibrary.exception.BadRequestException;
import com.oasis.nexuslibrary.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("member", new MemberDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("member") MemberDto memberDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            memberService.save(memberDto);
            redirectAttributes.addFlashAttribute("successToast", "Registration successful! You can now login.");
            return "redirect:/login";
        } catch (BadRequestException ex) {
            model.addAttribute("registrationError", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }
    
    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }
}
