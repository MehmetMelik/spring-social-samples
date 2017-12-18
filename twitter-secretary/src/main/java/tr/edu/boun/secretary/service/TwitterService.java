package tr.edu.boun.secretary.service;

import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Service;
import tr.edu.boun.secretary.domain.Follower;
import tr.edu.boun.secretary.repository.FollowerRepository;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class TwitterService {

    private final Twitter twitter;

    @Resource
    private FollowerRepository followerRepository;

    @Inject
    public TwitterService (Twitter twitter) {
        this.twitter = twitter;
    }

    public List<TwitterProfile> getUnfollowers() {
        Long profileId = twitter.userOperations().getProfileId();
        CursoredList<Long> followerIds = twitter.friendOperations().getFollowerIds();
        List<Follower> followers = followerRepository.findByUserIdAndActive(profileId, true);
        List<Long> unfollowers = followers.stream()
                .filter(follower -> !followerIds.contains(follower.getFollowerId()))
                .map(Follower::getFollowerId)
                .collect(toList());
        saveNewFollowers(profileId, followerIds, followers);
        deactivate(profileId, unfollowers);
        return getProfiles(new ArrayList<>(followerRepository.findByUserIdAndActive(profileId, false).stream()
                .map(Follower::getFollowerId).collect(Collectors.toList())));
    }

    private void saveNewFollowers(Long profileId, CursoredList<Long> currentFollowers, List<Follower> followers) {

        List<Follower> deactiveFollowers = followerRepository.findByUserIdAndActive(profileId, false);

        //List<Follower> reactivatedFollowers =
        deactiveFollowers.stream()
                .filter(deactiveFollower -> currentFollowers.contains(deactiveFollower.getFollowerId()))
                .forEach(deactiveFollower -> {
                    currentFollowers.remove(deactiveFollower.getFollowerId());
                    deactiveFollower.setActive(true);
                    followerRepository.save(deactiveFollower);
                });
                //.collect(Collectors.toList());
        //followerRepository.save(reactivatedFollowers);

        List<Long> followerIds = followers.stream().map(Follower::getFollowerId).collect(toList());
        List<Follower> newFollowers = currentFollowers.stream().filter(id -> !followerIds.contains(id))
                .map(currentFollower -> Follower.builder().userId(profileId)
                       .followerId(currentFollower)
                       .active(true)
                       .build()).collect(toList());
        followerRepository.save(newFollowers);
    }

    private void deactivate(Long profileId, List<Long> unfollowers) {
        List<Follower> followers = followerRepository.findByUserIdAndFollowerIdIn(profileId, unfollowers);
        followers.forEach(follower -> follower.setActive(false));
        followerRepository.save(followers);
    }

    public List<TwitterProfile> getAllFriends() {
        CursoredList<Long> cursoredList;
        cursoredList = twitter.friendOperations().getFriendIds();
        return getProfiles(new ArrayList<>(cursoredList));
    }

    public List<TwitterProfile> getAllFollowers() {
        List<Long> existingFollowers = followerRepository.findByActive(true).stream()
                .map(Follower::getFollowerId).collect(toList());
        Long profileId = twitter.userOperations().getProfileId();
        CursoredList<Long> currentFollowers;
        currentFollowers = twitter.friendOperations().getFollowerIds();
        List<Follower> followers = new ArrayList<>();
        currentFollowers.forEach(item -> {
            Follower existingFollower = followerRepository.findByUserIdAndFollowerId(profileId, item);
            if(existingFollower == null) {
                Follower follower = Follower.builder().userId(profileId)
                        .followerId(item)
                        .active(true)
                        .build();
                followers.add(follower);
            } else if (!existingFollower.getActive()) {
                existingFollower.setActive(true);
                followers.add(existingFollower);
            }
        });

        List<Long> unfollowerIds = existingFollowers.stream()
                .filter(existingFollower -> !currentFollowers.contains(existingFollower)).collect(toList());
        List<Follower> unfollowers = unfollowerIds.stream().map(id -> {
            Follower follower = followerRepository.findByUserIdAndFollowerIdAndActive(profileId, id, true);
            follower.setActive(false);
            return follower;
        }).collect(toList());
        followers.addAll(unfollowers);
        followerRepository.save(followers);
        return getProfiles(new ArrayList<>(currentFollowers));
    }

    private List<TwitterProfile> getProfiles(ArrayList<Long> cursoredList) {
        if(cursoredList.isEmpty())
            return new ArrayList<>();
        List<TwitterProfile> followers = new ArrayList<>();
        for(int i = 0; i< cursoredList.size(); i+=100){
            followers.addAll(getProfiles(cursoredList, i));
        }
        return followers;
    }

    private List<TwitterProfile> getProfiles(ArrayList<Long> cursoredList, int start){
        int end = Math.min(start+100, cursoredList.size());
        Long[] ids = cursoredList.subList(start, end).toArray(new Long[0]);
        long[] idArray = Arrays.stream(ids).mapToLong(Long::longValue).toArray();
        return twitter.userOperations().getUsers(idArray);
    }
}
