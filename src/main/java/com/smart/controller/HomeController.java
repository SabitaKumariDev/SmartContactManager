package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;



@Controller
public class HomeController 
{
	@Autowired
	private UserRepository userRepository;
	
	//This is home page handler
	@RequestMapping("/")
	public String home(Model model) 
	{
		model.addAttribute("title","Home- Smart Contact Manager");
		return "home";
	}
	
	//This is about page handler
	@RequestMapping("/about")
	public String about(Model model) 
	{
		model.addAttribute("title","About- Smart Contact Manager");
		return "about";
	}
	
	//This is sign up page handler
	@RequestMapping("/signup")
	public String signup(Model model) 
	{
		model.addAttribute("title","Register- Smart Contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	//This is handler for register user	
	@RequestMapping(value= "/do_register", method = RequestMethod.POST)
	public String registerUser(@jakarta.validation.Valid @ModelAttribute("user") User user, BindingResult resultBind, @RequestParam(value="agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session) 
	{
		try
		{
			if(!agreement)
			{
				System.out.println("You have not agreed terms and conditions");
				throw new Exception("You have not agreed terms and conditions");
			}
			
			if(resultBind.hasErrors())
			{
				System.out.println("Error " + resultBind.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			
			System.out.println("Agreement" + agreement);
			System.out.println("USER" + user);
			
			User result= this.userRepository.save(user);
			
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("Sucessfully Registered!!", "aler-error"));
			return "signup";
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong!!"+ e.getMessage(), "aler-danger"));
			return "signup";
		}
		
		
	}	
}
