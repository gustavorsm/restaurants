package com.ucbcba.demo.Controllers;

import com.ucbcba.demo.entities.Category;
import com.ucbcba.demo.entities.City;
import com.ucbcba.demo.entities.Restaurant;
import com.ucbcba.demo.services.CityService;
import com.ucbcba.demo.services.RestaurantService;
import com.ucbcba.demo.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Struct;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final UserService userService;
    private final RestaurantService restaurantService;
    private final CityService cityService;

    public HomeController(UserService userService, RestaurantService restaurantService, CityService cityService) {
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.cityService = cityService;
    }

    @RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
    public String welcome(Model model, @RequestParam(value = "searchFilter", required = false, defaultValue = "") String searchFilter, @RequestParam(value = "cityDropdown", required = false, defaultValue = "") String cityDropdown,
                          @RequestParam(value = "showContent", required = false, defaultValue = "") String showContent ,@RequestParam(value = "scoreDropdown", required = false, defaultValue = "") String scoreDropdown) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Boolean logged = (!getUserRole(auth).equals("notLogged"));
        com.ucbcba.demo.entities.User user = new com.ucbcba.demo.entities.User();
        User u;
        if (logged) {
            u = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
            user = userService.findByUsername(u.getUsername());
        }
        model.addAttribute("user", user);
        model.addAttribute("role", getUserRole(auth));
        model.addAttribute("logged", logged);
        model.addAttribute("cities", cityService.listAllCities());

        class Score{
            String cad;
            Integer quantity;

            public Score(String cad,Integer quantity){
                this.cad=cad;
                this.quantity=quantity;
            }
        }

        Score[] scores = new Score[5];
        scores[0] = new Score("1", 1);
        scores[1] = new Score("2", 2);
        scores[2] = new Score("3", 3);
        scores[3] = new Score("4", 4);
        scores[4] = new Score("5", 5);

        model.addAttribute("scores", scores);

        String search = "";
        if (!searchFilter.equals("")) {
            search = searchFilter;
        }
        model.addAttribute("search", search);

        String citySelected = "";
        if (!cityDropdown.equals("All cities")) {
            citySelected = cityDropdown;
        }
        model.addAttribute("citySelected", citySelected);

        String scoreSelected = "";
        if (!scoreDropdown.equals("Any Score")) {
            scoreSelected = scoreDropdown;
        }
        model.addAttribute("scoreSelected", scoreSelected );

        String showTable = "table";
        if (showContent.equals("map")) {
            showTable = showContent;
        }
        model.addAttribute("showTable", showTable);

        List<Restaurant> allRestaurants = new ArrayList<>();
        List<Restaurant> filteredRestaurants;
        for (Restaurant restaurant : restaurantService.listAllRestaurants()) {
            allRestaurants.add(restaurant);
        }

        Integer score=0;
        if (!scoreDropdown.equals("") && !scoreDropdown.equals("Any Score")) {
            score = Integer.parseInt(scoreDropdown);
        }

        final Integer scoreDrop=score;

        if (scoreDropdown.equals("Any score")) {
            filteredRestaurants = allRestaurants.stream().filter(
                    p -> (p.getName().toLowerCase().contains(searchFilter.toLowerCase())
                            || searchCategories(p.getCategories(), searchFilter.toLowerCase())
                    )
            ).collect(Collectors.toList());
        } else {
            filteredRestaurants = allRestaurants.stream().filter(
                    p -> (
                            (p.getName().toLowerCase().contains(searchFilter.toLowerCase())
                                    || searchCategories(p.getCategories(), searchFilter.toLowerCase()))
                                    && restaurantService.getScore(p.getId()) >= scoreDrop
                    )
            ).collect(Collectors.toList());
        }

        if (cityDropdown.equals("All cities")) {
            filteredRestaurants = allRestaurants.stream().filter(
                    p -> (p.getName().toLowerCase().contains(searchFilter.toLowerCase())
                            || searchCategories(p.getCategories(), searchFilter.toLowerCase())
                    )
            ).collect(Collectors.toList());
        } else {
            filteredRestaurants = allRestaurants.stream().filter(
                    p -> (
                            (p.getName().toLowerCase().contains(searchFilter.toLowerCase())
                                    || searchCategories(p.getCategories(), searchFilter.toLowerCase()))
                                    && p.getCity().getName().toLowerCase().contains(cityDropdown.toLowerCase())
                    )
            ).collect(Collectors.toList());
        }

        filteredRestaurants.sort((r1, r2) -> {
            Integer s1, s2;
            s1 = restaurantService.getScore(r1.getId());
            s2 = restaurantService.getScore(r2.getId());
            return s2.compareTo(s1);
        });

        model.addAttribute("restaurants", filteredRestaurants);
        List<Restaurant> restaurantsList = new ArrayList<>();

        restaurantService.listAllRestaurants().forEach(r -> {
            Restaurant rest = new Restaurant();
            rest.setName(r.getName());
            rest.setLatitude(r.getLatitude());
            rest.setLongitude(r.getLongitude());
            restaurantsList.add(rest);
        });

        model.addAttribute("restaurantsList", restaurantsList);
        return "home";
    }

    private Boolean searchCategories(Set<Category> categories, String param) {
        for (Category category : categories) {
            if (category.getName().toLowerCase().contains(param))
                return true;
        }
        return false;
    }

    private String getUserRole(Authentication auth) {
        if (!auth.getPrincipal().equals("anonymousUser")) {
            User u = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
            com.ucbcba.demo.entities.User user = userService.findByUsername(u.getUsername());
            return user.getRole().toLowerCase();
        }
        return "notLogged";
    }
}
