package com.ucbcba.demo.Controllers;

import com.ucbcba.demo.entities.*;
import com.ucbcba.demo.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RestaurantController {

    private RestaurantService restaurantService;
    private CategoryService categoryService;
    private CityService cityService;
    private PhotoService photoService;
    private UserLikesService userLikesService;
    private UserService userService;
    private CommentService commentService;
    private NivelRestauranteService nivelRestauranteService;

    @Autowired
    public void setRestaurantService(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Autowired
    public void setCityService(CityService cityService) {
        this.cityService = cityService;
    }

    @Autowired
    public void setNivelRestauranteService(NivelRestauranteService nivelRestauranteService ) {
        this.nivelRestauranteService = nivelRestauranteService;
    }

    @Autowired
    public void setPhotoService(PhotoService photoService) {
        this.photoService = photoService;
    }

    @Autowired
    public void setUserLikeService(UserLikesService userLikesService) {
        this.userLikesService = userLikesService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    @RequestMapping(value = "admin/restaurants", method = RequestMethod.GET)
    public String listAllRestaurants(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Boolean logged = false;
        if (!auth.getPrincipal().equals("anonymousUser")) {
            logged = true;
        }
        model.addAttribute("logged", logged);
        model.addAttribute("restaurants", restaurantService.listAllRestaurants());
        return "restaurants";
    }

    @RequestMapping(value = "admin/restaurant/new")
    public String newRestaurant(Model model) {
        model.addAttribute("restaurantCategories", categoryService.listAllCategories());
        model.addAttribute("cities", cityService.listAllCities());
        model.addAttribute("nivelRestaurantes", nivelRestauranteService.listAllNivelRestaurantes());
        model.addAttribute("restaurant", new Restaurant());
        return "newRestaurant";
    }

    @RequestMapping(value = "admin/restaurant/save", method = RequestMethod.POST)
    public String saveRestaurant(Restaurant restaurant, @RequestParam("file") MultipartFile[] files) throws IOException {
        restaurantService.saveRestaurant(restaurant);
        byte[] pixel;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                pixel = file.getBytes();
                Photo photo = new Photo();
                photo.setRestaurant(restaurant);
                photo.setPhoto(pixel);
                photoService.savePhoto(photo);
            }
        }
        return "redirect:/admin/restaurants";
    }

    @RequestMapping("admin/restaurant/{id}")
    String showRestaurant(@PathVariable Integer id, Model model) throws UnsupportedEncodingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Boolean logged = false;
        if (!auth.getPrincipal().equals("anonymousUser")) {
            logged = true;
        }
        Integer califs[] = {1, 2, 3, 4, 5};
        com.ucbcba.demo.entities.User user = userService.findByUsername(((User) auth.getPrincipal()).getUsername());
        Restaurant restaurant = restaurantService.getRestaurant(id);
        List restaurantPhotos = new ArrayList();
        Integer likes = userLikesService.getLikes(id);
        List<Photo> photos = (List<Photo>) photoService.listAllPhotosById(id);
        byte[] encodeBase64;
        String base64Encoded;
        for (Photo photo : photos) {
            encodeBase64 = Base64.encode(photo.getPhoto());
            base64Encoded = new String(encodeBase64, "UTF-8");
            restaurantPhotos.add(base64Encoded);
        }
        model.addAttribute("logged", logged);
        model.addAttribute("likes", likes);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("calification", califs);
        model.addAttribute("user", user);
        model.addAttribute("averageScore", restaurantService.getScore(id));
        model.addAttribute("photos", restaurantPhotos);
        return "ShowRestaurant";
    }

    @RequestMapping(value = "admin/restaurant/edit/{id}")
    String editRestaurant(@PathVariable Integer id, Model model) throws UnsupportedEncodingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Boolean logged = false;
        if (!auth.getPrincipal().equals("anonymousUser")) {
            logged = true;
        }
        List restaurantPhotos = new ArrayList();
        List<Photo> photos = (List<Photo>) photoService.listAllPhotosById(id);
        byte[] encodeBase64;
        String base64Encoded;
        for (Photo photo : photos) {
            encodeBase64 = Base64.encode(photo.getPhoto());
            base64Encoded = new String(encodeBase64, "UTF-8");
            restaurantPhotos.add(base64Encoded);
        }
        model.addAttribute("logged", logged);
        model.addAttribute("restaurant", restaurantService.getRestaurant(id));
        model.addAttribute("restaurantCategories", categoryService.listAllCategories());
        model.addAttribute("cities", cityService.listAllCities());
        model.addAttribute("nivelRestaurantes", nivelRestauranteService.listAllNivelRestaurantes());
        model.addAttribute("photos", restaurantPhotos);
        model.addAttribute("images", photoService.listAllPhotosById(id));
        return "restaurantForm";
    }

    @RequestMapping(value = "admin/restaurant/delete/{id}")
    String delete(@PathVariable Integer id, Model model) {
        restaurantService.deleteRestaurant(id);
        return "redirect:/admin/restaurants";
    }

    @RequestMapping("/admin/delete/photo/{id}")
    String deletePhoto(@PathVariable Integer id) {
        Photo photo = photoService.getPhoto(id);
        photoService.deletePhoto(id);
        return "redirect:/admin/restaurant/edit/" + photo.getRestaurant().getId();
    }

    @RequestMapping(value = "/restaurant/like/{restaurantId}")
    public String like(@PathVariable Integer restaurantId) {
        UserLike ul = new UserLike();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User u = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        com.ucbcba.demo.entities.User user = userService.findByUsername(u.getUsername());
        ul.setUser(user);
        Restaurant r = restaurantService.getRestaurant(restaurantId);
        ul.setRestaurant(r);
        userLikesService.saveUserLike(ul);
        return "redirect:/restaurant/" + restaurantId;
    }

    @RequestMapping("restaurant/{id}")
    String showRestaurantUser(@PathVariable Integer id, Model model, com.ucbcba.demo.entities.User user) throws UnsupportedEncodingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Boolean logged = (!getUserRole(auth).equals("notLogged"));
        Integer califs[] = {1, 2, 3, 4, 5};
        Restaurant restaurant = restaurantService.getRestaurant(id);
        Comment comment=new Comment();
        Integer likes = userLikesService.getLikes(id);
        Boolean isLiked = false;

        List restaurantPhotos = new ArrayList();
        List<Photo> photos = (List<Photo>) photoService.listAllPhotosById(id);
        byte[] encodeBase64;
        String base64Encoded;
        for (Photo photo : photos) {
            encodeBase64 = Base64.encode(photo.getPhoto());
            base64Encoded = new String(encodeBase64, "UTF-8");
            restaurantPhotos.add(base64Encoded);
        }

        if (!auth.getPrincipal().equals("anonymousUser")) {
            user = userService.findByUsername(((User) auth.getPrincipal()).getUsername());
            isLiked = userLikesService.isLiked(user.getId(), id);
            model.addAttribute("user", user);
            Comment userComment = restaurantService.alreadyCommented(user.getId(), restaurant.getId());
            if(userComment!=null) {
                comment=userComment;
                model.addAttribute("comment", comment);
                model.addAttribute("userComment", userComment);
            }
            else
                comment=new Comment(restaurant,user);
            model.addAttribute("comment", comment);

        }
        model.addAttribute("role", getUserRole(auth));
        model.addAttribute("likes", likes);
        model.addAttribute("calification", califs);
        model.addAttribute("averageScore", restaurantService.getScore(id));
        model.addAttribute("commentError", "You can only add one comment by Restaurant");
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("logged", logged);
        model.addAttribute("photos", restaurantPhotos);

        Restaurant mapRestaurant = new Restaurant();
        mapRestaurant.setLatitude(restaurant.getLatitude());
        mapRestaurant.setLongitude(restaurant.getLongitude());
        mapRestaurant.setName(restaurant.getName());
        model.addAttribute("mapRestaurant",mapRestaurant);
        return "restaurantUserView";
    }

    @RequestMapping(value = "/restaurant/dislike/{restaurantId}")
    public String dislike(@PathVariable Integer restaurantId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User u = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        com.ucbcba.demo.entities.User user = userService.findByUsername(u.getUsername());
        userLikesService.deleteUserLike(user.getId(), restaurantId);
        return "redirect:/restaurant/" + restaurantId;
    }

    private String getUserRole(Authentication auth) {
        if (!auth.getPrincipal().equals("anonymousUser")) {
            User u = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
            com.ucbcba.demo.entities.User user = userService.findByUsername(u.getUsername());
            return user.getRole().toLowerCase();
        }
        return "notLogged";
    }
    @RequestMapping(value = "user/view/{userId}", method = RequestMethod.GET)
    public String userView(Model model,@PathVariable Integer userId ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User u = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        com.ucbcba.demo.entities.User principalUser = userService.findByUsername(u.getUsername());
        com.ucbcba.demo.entities.User user = userService.findById(userId);
        Boolean userPrincipal = false;
        if(user.getId() == principalUser.getId())
        {
            userPrincipal =true;
        }
        Boolean haveComments = true;
        List<Comment> comments = (List<Comment>) commentService.listAllCommentsByUser(user.getId());
        if(comments.isEmpty())
        {
            haveComments = false;
        }
        model.addAttribute("thisUser",user);
        model.addAttribute("flag",haveComments);
        model.addAttribute("comments",comments);
        model.addAttribute("userFlag",userPrincipal);
        return "userView";
    }
    @RequestMapping(value = "/user/save", method = RequestMethod.POST)
    public String userView(com.ucbcba.demo.entities.User user ) {
        userService.save2(user);
        return "redirect:/user/view/"+user.getId();
    }

    @RequestMapping(value = "/user/edit/{id}")
    public String userEdit(@PathVariable Integer id, Model model) {
        model.addAttribute("user",userService.findById(id));
        return "userEdit";
    }

    @RequestMapping(value = "/changePassword/{id}")
    public String changePassword(@PathVariable Integer id, Model model) {
        model.addAttribute("user",userService.findById(id));
        return "changePassword";
    }

    @RequestMapping(value = "/savePassword",method = RequestMethod.POST)
    public String savePassword(Model model, com.ucbcba.demo.entities.User user) {
        Boolean error = false;
        if(!user.getPassword().equals(user.getPasswordConfirm()))
        {
            error = true;
            model.addAttribute("error",error);
            model.addAttribute("user",user);
            return "changePassword";
        }
        userService.save(user);
        return "redirect:/user/view/"+user.getId();
    }
    @RequestMapping(value = "/user/delete/{id}")
    String userDelete(@PathVariable Integer id, Model model) {
        userService.deleteUser(id);
        return "redirect:/login";
    }

}