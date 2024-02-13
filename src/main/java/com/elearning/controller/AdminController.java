package com.elearning.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.entity.Contact;
import com.elearning.entity.Course;
import com.elearning.entity.CourseMaterial;
import com.elearning.entity.MaterialDetails;
import com.elearning.entity.Payment;
import com.elearning.entity.User;
import com.elearning.repository.ContactRepository;
import com.elearning.repository.CourseMaterialRepository;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.PaymentRepository;
import com.elearning.repository.UserRepo;
import com.elearning.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private CourseRepository courseRepo;

	@Autowired
	private CourseMaterialRepository courseMatrepo;

	@Autowired
	private ContactRepository contactRepo;
	
	@Autowired
	private PaymentRepository payRepo;
	
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
	public void userList(Model m) {
		// List<User> user=userRepo.findAll();
		String role = "ROLE_USER";
		List<User> user = userRepo.findAllByRole(role);
		m.addAttribute("userList", user);
	}

	@ModelAttribute
	public void courseList(Model m) {
		List<Course> course = courseRepo.findAll();
		m.addAttribute("courseList", course);
	}

	@ModelAttribute
	public void messageList(Model m) {
		List<Contact> message = contactRepo.findAll();
		m.addAttribute("contactList", message);
	}

	@ModelAttribute
	public void payments(Model m) {
		List<Payment> order = payRepo.findAll();
//		List<User> user=new ArrayList<User>();
//		List<Course> course=new ArrayList<Course>();
//		Iterator<Payment> itr=order.iterator();
//		while(itr.hasNext()) {
//			Payment pay=itr.next();
//			user.add(pay.getUser());
//			course.add(pay.getCourse());
//		}
//		m.addAttribute("listUser", user);
		m.addAttribute("orderList", order);
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	private String deleteStudent(@RequestParam Integer id) {
		userRepo.deleteById(id);
		return "redirect:/admin/profile";
	}

	@GetMapping("/profile")
	public String profile(Model model) {
	    Long totalRevenue =payRepo.calculateTotalRevenue();
	    Long totalUser = userRepo.countUsersWithUserRole();
	    Long totalOrder= payRepo.count();
	    model.addAttribute("revenue", totalRevenue);
	    model.addAttribute("users", totalUser);
	    model.addAttribute("orders", totalOrder);
	    return "AdminProfile";
	}

	@GetMapping("/editProfile")
	public String editProfile() {
		return "AdminEditProfile";
	}
	
	@GetMapping("/edit")
	public String update() {
		return "AdminEdit";
 	}
	
	@PostMapping("/adminDataEdit")
    public String adminDataEdit(@ModelAttribute User user, @RequestParam String conf_password, HttpSession session, Model model) {
        // Check if the password and confirm password match
        if (!user.getPassword().equals(conf_password)) {
            session.setAttribute("msg", "Password and Confirm Password do not match.");
            return "/adminDataEdit"; 
        }

        User updatedUser = userService.userUpdate(user.getId(), user.getName(), user.getPhone(),user.getEmail(), user.getPassword());

        if (updatedUser != null) {
            session.setAttribute("msg", "User details updated successfully.");
            return "redirect:/admin/editProfile";
        } else {
             return "redirect:/error";
        }
    }
	
	@GetMapping("/addcourse")
	public String addCourse() {
		return "AddCourse";
	}

	@GetMapping("/contact")
	public String contact() {
		return "MessageAdmin";
	}

	@PostMapping("/saveCourse")
	public String SaveCourse(@ModelAttribute Course course, HttpSession session) {
		Course addcourse = courseRepo.save(course);
		if (addcourse != null) {
			session.setAttribute("msg", "Added successfully");
		} else {
			session.setAttribute("msg", "Something wrong server");
		}
		return "redirect:/admin/addcourse";
	}

	@GetMapping("/courseMaterial")
	public String addMaterial() {
		return "AddCourseMaterial";
	}

	@PostMapping("/saveCourseMat")
	public String saveCourseMaterial(@ModelAttribute CourseMaterial courseMat,
			@RequestParam("videoFile") MultipartFile videoFile, @RequestParam("pdfFile") MultipartFile pdfFile,
			HttpSession session, Model model) {
		try {
			byte[] videoBytes = videoFile.getBytes();
			courseMat.setVideo(videoBytes);

			byte[] pdfBytes = pdfFile.getBytes();
			courseMat.setPdf(pdfBytes);
			
			CourseMaterial addMaterial = courseMatrepo.save(courseMat);

			if (addMaterial != null) {
				session.setAttribute("msg", "Added successfully");
			} else {
				session.setAttribute("msg", "Something went wrong on the server");
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			session.setAttribute("msg", "Error processing file upload");
		}
		return "redirect:/admin/courseMaterial";
	}

	@GetMapping("/courses")
	public String course() {
		return "Courses";
	}

	@PostMapping("/view")
	public String courseView(@RequestParam long id, Model model) {
		Optional<Course> courseOptional = courseRepo.findById(id);
		Course course = courseOptional.orElse(null);

		if (course != null) {
			List<CourseMaterial> courseMaterial = courseMatrepo.findAllByCourse_id(id);

			if (courseMaterial != null) {
				List<MaterialDetails> list = new LinkedList<MaterialDetails>();
				Iterator<CourseMaterial> itr = courseMaterial.iterator();
				while (itr.hasNext()) {
					CourseMaterial material = itr.next();
					byte[] video = material.getVideo();
					byte[] pdf = material.getPdf();
					list.add(new MaterialDetails(material.getCourseDetails(), Base64.getEncoder().encodeToString(video),
							Base64.getEncoder().encodeToString(pdf)));
				}
				model.addAttribute("list", list);
				model.addAttribute("Course", course);
				return "CourseView";
			}
		}

		// Handle case where course or courseMaterial is not found
		return "redirect:/error";
	}

	@PostMapping("/deleteCourse")
	public String deleteCourse(@RequestParam("id") long courseId, Model model) {

		Optional<Course> course = courseRepo.findById(courseId);

		if (course != null) {
			courseRepo.deleteById(courseId);
			model.addAttribute("successMessage", "Course deleted successfully.");
		} else {
			model.addAttribute("errorMessage", "Course not found.");
		}

		return "redirect:/admin/courses";
	}

	@PostMapping("/courseUpdate")
	public String courseUpdate(@RequestParam("id") long courseId, Model model) {
		Optional<Course> course = courseRepo.findById(courseId);
		return course.map(cour -> {
			model.addAttribute("cr", cour);
			return "CourseUpdate";
		}).orElse("redirect:/courses?msg=Course not found");
	}

	
	@PostMapping("/updateCourse")
    public String updateUser(@ModelAttribute Course course, HttpSession session, Model model) {
        Course updatedcourse = userService.courseUpdate(course.getId(), course.getCourseName(), course.getCoursePrice());

        if (updatedcourse != null) {
            session.setAttribute("msg", "Course details updated successfully.");
            return "redirect:/admin/courseUpdatSuccess"; // Redirect to the user profile or another appropriate page
        } else {
             return "redirect:/error";
        }
    }
	
	@GetMapping("/courseUpdatSuccess")
	public String courseUpdatSuccess() {
		return "CourseUpdateSuccess";
	}
	
	@GetMapping("/report")
	public String report() {
		return "Reports";
 	}
	
	@GetMapping("/reportStudent")
	public String reportStudent() {
		return "ReportStudents";
 	}
	
	@GetMapping("/reportMessage")
	public String reportMessage() {
		return "ReportMessages";
 	}
	
	@GetMapping("/reportOrder")
	public String reportOrder() {
		return "ReportOrders";
 	}
}
