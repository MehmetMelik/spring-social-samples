package tr.edu.boun.secretary.twitter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tr.edu.boun.secretary.service.TwitterService;

import javax.inject.Inject;

@Controller
public class TwitterFriendsController {


	private final TwitterService twitterService;
	
	@Inject
	public TwitterFriendsController(TwitterService twitterService) {
		this.twitterService = twitterService;
	}
	
	@RequestMapping(value="/twitter/friends", method=RequestMethod.GET)
	public String friends(Model model) {
		model.addAttribute("profiles", twitterService.getAllFriends());
		return "twitter/friends";
	}

	@RequestMapping(value="/twitter/followers", method=RequestMethod.GET)
	public String followers(Model model) {
		model.addAttribute("profiles", twitterService.getAllFollowers());
		return "twitter/friends";
	}

    @RequestMapping(value="/twitter/unfollowers", method=RequestMethod.GET)
    public String unfollowers(Model model) {
        model.addAttribute("profiles", twitterService.getUnfollowers());
        return "twitter/friends";
    }

}
