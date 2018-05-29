package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model){

        model.addAttribute("title","Menu Items");
        model.addAttribute("menus", menuDao.findAll());

        return "menu/index";

    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model){
        model.addAttribute("title", "Add menu");
        model.addAttribute("menu", new Menu());

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(Model model, @ModelAttribute @Valid Menu menu, Errors errors){

        if(errors.hasErrors()){
            model.addAttribute("title", "Add menu");
            model.addAttribute("menu", menu);
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int id, Model model){
        Menu menu = menuDao.findOne(id);
        model.addAttribute("menu",menu);

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String displayAddItemForm(@PathVariable int id , Model model){
        Menu menu = menuDao.findOne(id);
        Iterable<Cheese> cheeses = cheeseDao.findAll();
        AddMenuItemForm addMenuItemForm = new AddMenuItemForm(menu , cheeses);
        model.addAttribute("title", "Add item to menu: " + menu.getName());
        model.addAttribute("form",addMenuItemForm);
        return "menu/add-item" ;
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String processAddItem(Model model, @ModelAttribute @Valid AddMenuItemForm addMenuItemForm, @RequestParam int cheeseId, @RequestParam int menuId,  Errors errors){
        if(errors.hasErrors()){
            model.addAttribute("title", "Add item to menu: " + addMenuItemForm.getMenu().getName());
            model.addAttribute("form", addMenuItemForm);
            return "menu/add-item";
        }
        Cheese cheese = cheeseDao.findOne(cheeseId);
        Menu menu = menuDao.findOne(menuId);
        menu.addItem(cheese);
        menuDao.save(menu);
        return "redirect:view/" + menu.getId();

    }
}
