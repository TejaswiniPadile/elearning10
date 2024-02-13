package com.elearning.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.elearning.entity.Course;
import com.elearning.entity.CourseMaterial;
import com.elearning.entity.MaterialDetails;
import com.elearning.entity.Payment;
import com.elearning.entity.User;
import com.elearning.repository.CourseMaterialRepository;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.PaymentRepository;
import com.elearning.repository.UserRepo;
import com.elearning.service.UserService;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private CourseRepository courseRepo;
	
	@Autowired
	private PaymentRepository payRepo;
	
	@Autowired
	private CourseMaterialRepository courseMatrepo;
	
	@Autowired
	private UserService userService;
	

	@ModelAttribute
	public void commonUser(Principal p, Model m) {
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
	
	@GetMapping("/home")
	public String home(Principal p,Model model) {
		String email = p.getName();
		User user = userRepo.findByEmail(email);
		if(user!=null) {
			List<Payment> payments = payRepo.findAllByUser(user);
			List<Course> list=new LinkedList<Course>();
			Iterator<Payment> itr=payments.iterator();
			while(itr.hasNext()) {
				Payment pay=itr.next();
				list.add(pay.getCourse());
			}
			model.addAttribute("Course", list);
			return "UserHome";
		}
		else {
			return "UserHome";
		}
	}
	
	@PostMapping("/view")
	public String courseView(@RequestParam long id, Model model) {
		Optional<Course> courseOptional = courseRepo.findById(id);
		Course course = courseOptional.orElse(null);

		if (course != null) {
			List<CourseMaterial> courseMaterial = courseMatrepo.findAllByCourse_id(id);

			if (courseMaterial != null) {
				List<MaterialDetails> list=new LinkedList<MaterialDetails>();
				Iterator<CourseMaterial> itr=courseMaterial.iterator();
				while(itr.hasNext()) {
					CourseMaterial material=itr.next();
					byte [] video=material.getVideo();
					byte [] pdf=material.getPdf();
					list.add(new MaterialDetails(material.getCourseDetails(), Base64.getEncoder().encodeToString(video) , Base64.getEncoder().encodeToString(pdf)));
				}
				model.addAttribute("list", list);
				model.addAttribute("Course", course);
				return "UserCourseView";
			}
		}

		// Handle case where course or courseMaterial is not found
		return "redirect:/error";
	}
	
	@GetMapping("/profile")
	public String profile() {
		return "UserProfile";
	}
	
	@GetMapping("/courses")
	public String course() {
		return "UserCourse";
	}
	
	@GetMapping("/update")
	public String update() {
		return "UserUpdate";
	}

	@PostMapping("/userUpdate")
    public String updateUser(@ModelAttribute User user, @RequestParam String conf_password, HttpSession session, Model model) {
        // Check if the password and confirm password match
        if (!user.getPassword().equals(conf_password)) {
            session.setAttribute("msg", "Password and Confirm Password do not match.");
            return "updateUser"; 
        }

        // Update the user
        User updatedUser = userService.userUpdate(user.getId(), user.getName(), user.getPhone(),user.getEmail(), user.getPassword());

        if (updatedUser != null) {
            session.setAttribute("msg", "User details updated successfully.");
            return "redirect:/user/profile"; // Redirect to the user profile or another appropriate page
        } else {
             return "redirect:/error";
        }
    }
	
	@GetMapping("/test")
	public String test() {
		return "UserTest";
	}
	
	@PostMapping("/payment")
	public String payment(@RequestParam long id,Model model) {
		Optional<Course> courseOptional = courseRepo.findById(id);
		Course course = courseOptional.orElse(null);
		if(course!=null) {
			model.addAttribute("payment", course);
			return "UserPayment";
		}
		else {
			return "redirect/courses";
		}
		
	}
	
	@PostMapping("/pay")
	public String pay(@RequestParam("userId") int userId,
	                   @RequestParam("courseId") long courseId,
	                   Model model,HttpSession session) {
	    Optional<Course> courseOptional = courseRepo.findById(courseId);
	    Optional<User> userOptional = userRepo.findById(userId);

	    if (courseOptional.isPresent() && userOptional.isPresent()) {
	        Course course = courseOptional.get();
	        User user = userOptional.get();

	        Payment payment = new Payment();
	        payment.setUser(user);
	        payment.setCourse(course);
	        payment.setPaymentDate(LocalDateTime.now()); // Assuming you want to set the payment date to the current date and time

	        payRepo.save(payment);

	        session.setAttribute("msg", "Payment Successful");
	        return "UserPaymentSuccess"; // Replace with the actual success page name
	    } else {
	        return "redirect:/courses";
	    }
	}

	@GetMapping("/paymentSuccess")
	public String paySuccess() {
		return "UserPaymentSuccess";
	}
	
}
