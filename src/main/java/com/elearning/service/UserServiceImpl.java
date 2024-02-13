package com.elearning.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.elearning.entity.Course;
import com.elearning.entity.User;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.PaymentRepository;
import com.elearning.repository.UserRepo;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private CourseRepository courserepo;

	@Override
	public User saveUser(User user, String url) {

		String password = passwordEncoder.encode(user.getPassword());
		user.setPassword(password);
		user.setRole("ROLE_USER");
		user.setEnable(false);
		user.setVerificationCode(UUID.randomUUID().toString());
		User newuser = userRepo.save(user);
		if (newuser != null) {
			sendEmail(newuser, url);
		}
		return newuser;
	}

	@Override
	public void removeSessionMessage() {

		HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest()
				.getSession();

		session.removeAttribute("msg");
	}

	@Override
	public void sendEmail(User user, String url) {
		String from = "tejupadile@gmail.com";
		String to = user.getEmail();
		String subject = "Account Verification";
		String content = "Dear [[name]],<br>" + "Please click the link below to verify your registration:<br>"
				+ "<h3><a href=\"[[URL]]\"target=\"_self\">VERIFY</a>" + "<br>Thank you,<br>" + "Tejaswini";
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setFrom(from, "Tejaswini");
			helper.setTo(to);
			helper.setSubject(subject);

			content = content.replace("[[name]]", user.getName());
			String siteUrl = url + "/verify?code=" + user.getVerificationCode();
			content = content.replace("[[URL]]", siteUrl);
			helper.setText(content, true);
			mailSender.send(message);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public boolean verifyAccount(String verificationCode) {
		User user = userRepo.findByVerificationCode(verificationCode);
		if (user == null) {
			return false;
		} else {
			user.setEnable(true);
			user.setVerificationCode(null);
			userRepo.save(user);
			return true;
		}
	}

	@Override
	public User userUpdate(int userId, String name, String phone, String email, String password) {
		User existingUser = userRepo.findByEmail(email);

		if (existingUser != null) {
			String pass = passwordEncoder.encode(password);
			existingUser.setName(name);
			existingUser.setPhone(phone);
			existingUser.setPassword(pass);
			return userRepo.save(existingUser);
		}

		return null;
	}

	@Override
	public User resetPassword(String email, String password) {
		User existingUser = userRepo.findByEmail(email);
		if (existingUser != null) {
			String pass = passwordEncoder.encode(password);
			existingUser.setPassword(pass);
			return userRepo.save(existingUser);
		}
		return null;
	}

	@Override
	public Course courseUpdate(long id, String courseName, long coursePrice) {
		Optional<Course> cr=courserepo.findById(id);
		Course course=cr.get();
		if(course!=null) {
			course.setCourseName(courseName);
			course.setCoursePrice(coursePrice);
			return courserepo.save(course);
		}
		return null;
	}

	
	
}
