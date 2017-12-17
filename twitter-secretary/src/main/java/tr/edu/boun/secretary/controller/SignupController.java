package tr.edu.boun.secretary.controller;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import tr.edu.boun.secretary.domain.Account;
import tr.edu.boun.secretary.message.Message;
import tr.edu.boun.secretary.message.MessageType;
import tr.edu.boun.secretary.service.AccountService;
import tr.edu.boun.secretary.signin.SignInUtils;
import tr.edu.boun.secretary.form.SignupForm;

import javax.inject.Inject;
import javax.validation.Valid;

@Controller
public class SignupController {

	private final AccountService accountService;
	private final ProviderSignInUtils providerSignInUtils;

	@Inject
	public SignupController(
			AccountService accountService,
			ConnectionFactoryLocator connectionFactoryLocator,
			UsersConnectionRepository connectionRepository) {
		this.accountService = accountService;
		this.providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);
	}

	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public SignupForm signupForm(WebRequest request) {
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
		if (connection != null) {
			request.setAttribute("message", new Message(MessageType.INFO, "Your " + StringUtils.capitalize(connection.getKey().getProviderId()) + " account is not associated with a Twitter Secretary account. If you're new, please sign up."), WebRequest.SCOPE_REQUEST);
			return SignupForm.fromProviderUser(connection.fetchUserProfile());
		} else {
			return new SignupForm();
		}
	}

	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String signup(@Valid SignupForm form, BindingResult formBinding, WebRequest request) {
		if (formBinding.hasErrors()) {
			return null;
		}
		Account account = accountService.createAccount(form, formBinding);
		if (account != null) {
			SignInUtils.signin(account.getUsername());
			providerSignInUtils.doPostSignUp(account.getUsername(), request);
			return "redirect:/";
		}
		return null;
	}

}
