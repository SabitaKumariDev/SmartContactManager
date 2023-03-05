package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//Method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal)
	{
		String userName= principal.getName();
		System.out.println("USERNAME "+userName);
		
		User user= userRepository.getUserByUserName(userName);
		System.out.println("USER "+user);
		
		model.addAttribute("user", user);
	}
	
	//Dashboard home
	@RequestMapping("/index")
	//@GetMapping("/index")
	public String dashboard(Model model, Principal principal)
	{	
		//get the user using username(Email)
		return "normal/user_dashboard";
	}
	
	//Open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	//Processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session)
	{
		try
		{
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			
			if(file.isEmpty())
			{
				System.out.println("File is Empty");
				contact.setImage("contact.png");
			}
			else
			{
				//upload the file to folder and update the name to contact
				contact.setImage(file.getOriginalFilename());
				File saveFile= new ClassPathResource("static/img").getFile();
				Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("Image uploaded");
			}
			contact.setUser(user);
			user.getContacts().add(contact);
			
			this.userRepository.save(user);
			System.out.println("DATA" + contact);
			
			System.out.println("Added to database");
			
			//sucess message
			session.setAttribute("message", new Message("Your contact has been added sucessfully!!", "success"));
					
			//session.removeAttribute("message");
			
			//redirectAttribute.addFlashAttribute("redirectMessage", "Your contact has been added sucessfully!!");
		}
		catch(Exception e)
		{
			System.out.println("Error" + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong!!", "danger"));
		}
		return "normal/add_contact_form";
	}
	
	// Handler for Show contacts
	//per page =5[n]
	//current page = 0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal)
	{
		m.addAttribute("title","Show User Contacts");
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		Pageable pageable = PageRequest.of(page, 5);
		
		Page<Contact> contacts = this.contactRepository .findContactByUser(user.getId(), pageable);
		m.addAttribute("contacts",contacts);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",contacts.getTotalPages());
		
		return "normal/show_contacts";
	}	
	
	
	//Show particular contact detail
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal)
	{
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact= contactOptional.get();
		
		String userName= principal.getName();
		User user= this.userRepository.getUserByUserName(userName);
		
		if(user.getId()== contact.getUser().getId())
		{
			model.addAttribute("contact",contact);
			model.addAttribute("title", contact.getName());
		}
		return "normal/contact_detail";
	}

}
