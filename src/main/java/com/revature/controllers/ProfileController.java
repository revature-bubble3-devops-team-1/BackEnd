package com.revature.controllers;

import com.revature.aspects.annotations.NoAuthIn;
import com.revature.models.Profile;
import com.revature.services.ProfileService;
import com.revature.utilites.SecurityUtil;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Log4j2
@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * processes login attempt via http request from client
     *
     * @param username
     * @param password
     * @return secure token as json
     */
    @NoAuthIn
    public ResponseEntity<Profile> login(String username, String password) {
        Profile profile = profileService.login(username,password);
        if(profile != null){
            HttpHeaders headers = new HttpHeaders();
            String body = SecurityUtil.generateToken(profile);
            headers.set("Authorization" , body);
            return new ResponseEntity<>(profile, headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    /**
     * Post request that gets client profile registration info and then checks to see if information is not
     * a duplicate in the database. If info is not a duplicate, it sets Authorization headers
     * and calls profile service to add the request body profile to the database.
     *
     * @return aobject name profile, response header, Status code okay
     * @PARAM Profile
     */
    @PostMapping("/register")
    @NoAuthIn
    public ResponseEntity<Profile> addNewProfile(@Valid @RequestBody Profile profile){
        System.out.println("profile" + profile);
        Profile returnedUser = profileService.getProfileByEmail(profile.getEmail());
        System.out.println("returned" + returnedUser);
        if(returnedUser == null){
            HttpHeaders responseHeaders = new HttpHeaders();
            String token = SecurityUtil.generateToken(profile);
            responseHeaders.set("Authorization" , token);
            return new ResponseEntity<>(profileService.addNewProfile(profile),responseHeaders, HttpStatus.CREATED);

        } else {
            return new ResponseEntity<>(HttpStatus.IM_USED);
        }

    }

    /**
     * Get Mapping that grabs the profile by the path variable id. It then returns the profile if it is valid.
     *
     * @param id
     * @return Profile object with HttpStatusAccepted or HttpStatusBackRequest
     */
    @GetMapping("/profiles/{id}")
    public ResponseEntity<Profile> getProfileByPid(@PathVariable("id") int id) {
        Profile profile = profileService.getProfileByPid(id);
        if (profile != null) {
            return new ResponseEntity<>(profile, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Put mapping grabs the updated fields of profile and updates the profile in the database.
     * If no token is sent in the token it fails the Auth and doesn't update the profile.
     * @param profile
     * @return Updated profile with HttpStatus.ACCEPTED otherwise if invalid returns HttpStatus.BAD_REQUEST
     */
    @PutMapping("/profiles/{id}")
    public ResponseEntity<Profile> updateProfile(@RequestBody Profile profile, HttpServletRequest req){
        Profile result = profileService.updateProfile(profile);
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Adds profile to list of profiles being followed by user
     * @param email email of profile to follow
     * @param req http request including the user's authorization token in the "Authroization" header
     * @return
     */
    @PostMapping("/follow")
    public ResponseEntity<String> newFollower(String email, HttpServletRequest req) {
        System.out.println("incoming email: " + email);
        String token = req.getHeader("Authorization");

        Profile creator = SecurityUtil.validateToken(token);
        creator = profileService.getProfileByEmail(creator.getEmail());
        Profile newProfile = profileService.addFollowerByEmail(creator, email);
        if (newProfile != null) {
            HttpHeaders headers = new HttpHeaders();
            String newToken = SecurityUtil.generateToken(newProfile);
            String body = "{\"Authorization\":\"" +
                    newToken
                    + "\"}";
            return new ResponseEntity<>(body, headers, HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Removed profile from list of profiles being followed by the user
     * @param email email of the profile to be unfollowed
     * @param req http request including the user's authorization token in the "Authroization" header
     * @return OK response with new authorization token, bad request response if unsuccessful
     */
    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollow(String email, HttpServletRequest req) {
        Profile follower = (Profile) req.getAttribute("profile");
        follower = profileService.getProfileByEmail(follower.getEmail());
        if (follower != null) {
            follower = profileService.removeFollowByEmail(follower, email);
            if (follower != null) {
                log.info("Profile successfully unfollowed");
                HttpHeaders headers = new HttpHeaders();
                String newToken = SecurityUtil.generateToken(follower);
                String body = "{\"Authorization\":\"" +
                        newToken
                        + "\"}";
                return new ResponseEntity<>(body, headers, HttpStatus.ACCEPTED);
            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}