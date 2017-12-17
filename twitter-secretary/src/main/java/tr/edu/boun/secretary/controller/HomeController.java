package tr.edu.boun.secretary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import tr.edu.boun.secretary.repository.AccountRepository;

import java.security.Principal;

@Controller
public class HomeController {

	private final AccountRepository accountRepository;

	@Autowired
	public HomeController( AccountRepository accountRepository) {

		this.accountRepository = accountRepository;
	}

	@RequestMapping("/")
	public String home(Principal currentUser, Model model) {
		if (currentUser != null) {
			model.addAttribute(accountRepository.findByUsername(currentUser.getName()));
		}
		return "home";
	}

}
