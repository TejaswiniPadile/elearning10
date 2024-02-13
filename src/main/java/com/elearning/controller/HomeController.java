package com.elearning.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.elearning.entity.Contact;
import com.elearning.entity.Course;
import com.elearning.entity.User;
import com.elearning.repository.ContactRepository;
import com.elearning.repository.CourseMaterialRepository;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.UserRepo;
import com.elearning.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private CourseRepository courseRepo;
	
	@Autowired
	private CourseMaterialRepository cmRepo;
	
	@Autowired
	private ContactRepository contactRepo;

	@ModelAttribute
	public void User(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User user = userRepo.findByEmail(email);
			m.addAttribute("user", user);
		}

	}

	@ModelAttribute
	public void course(Model m) {
		List<Course> list=courseRepo.findAll();
		m.addAttribute("course", list);
		System.out.println(list);
	}
	
	@ModelAttribute
	public void admin(Model m) {
		List<User> list=userRepo.findAllByRole("ROLE_ADMIN");
		m.addAttribute("admin", list);
		System.out.println(list);
	}
	
	@GetMapping("/")
	public String index() {
		return "Index";
	}
	
	@GetMapping("/courses")
	public String courses() {
		return "PublicCoursePage";
	}
	
	@GetMapping("/about")
	public String about() {
		return "PublicAboutPage";
	}
	
	@GetMapping("/contact")
	public String contact() {
		return "PublicContactPage";
	}
	
	@GetMapping("/register")
	public String register() {
		return "Register";
	}
	
	@GetMapping("/resetPass")
	public String resetpass() {
		return "ResetPassword";
	}
	
	@PostMapping("/resetpassword")
	public String resetpassword(HttpSession session, @RequestParam String username, Model model) {
		User user=userRepo.findByEmail(username);
		if(user!=null) {
			model.addAttribute("user", user);
			return "NewPassword";
		}
		else {
	        model.addAttribute("msg", "Email not found, register first");
	        return "ResetPassword";
	    }
	}

	@PostMapping("/reset")
	public String reset(@ModelAttribute User user, @RequestParam String conf_password, HttpSession session, Model model) {
        
        if (!user.getPassword().equals(conf_password)) {
            session.setAttribute("msg", "Password and Confirm Password do not match.");
            return "NewPassword"; 
        }

 
        User updatedUser = userService.resetPassword(user.getEmail(), user.getPassword());
        if (updatedUser != null) {
            model.addAttribute("msg1", "Password updated successfully.");
            return "Login"; 
        } else {
            return "redirect:/error";
        }
	}
	
	@GetMapping("/signin")
	public String login() {
		return "Login";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user, HttpSession session, @RequestParam String conf_password, Model m, HttpServletRequest request) {
		if (!user.getPassword().equals(conf_password)) {
	        session.setAttribute("msg", "Password and Confirm Password do not match.");
	        return "Register"; 
	    }
		
		String url = request.getRequestURL().toString();
		url = url.replace(request.getServletPath(), "");
		User u = userService.saveUser(user, url);
		if (u != null) {
			session.setAttribute("msg", "Register successfully");
		} else {
			session.setAttribute("msg", "Something wrong server");
		}
		return "redirect:/register";
	}
	
	@GetMapping("/verify")
	public String verifyAccount(@Param("code") String code, Model m) {
		boolean check = userService.verifyAccount(code);
		if (check) {
			m.addAttribute("msg", "Sucessfully Verified...");
		} else {
			m.addAttribute("msg", "Already Verified...");
		}
		return "Message";
	}
	
	@PostMapping("/contactUs")
	public String contactUs(@ModelAttribute Contact contact, HttpSession session, Model m, HttpServletRequest request) {
		Contact cont = contactRepo.save(contact);
		if (cont != null) {
			session.setAttribute("msg", "Thanks for Contact Us");
		} else {
			session.setAttribute("msg", "Something wrong server");
		}
		return "redirect:/contact";
	}
	
}
