package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.forms.EditCheeseForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

/**
 * Created by LaunchCode
 */
@Controller
@RequestMapping("cheese")
public class CheeseController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private CategoryDao categoryDao;

    // Request path: /cheese
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "My Cheeses");

        return "cheese/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddCheeseForm(Model model) {
        model.addAttribute("title", "Add Cheese");
        model.addAttribute(new Cheese());
        model.addAttribute("categories", categoryDao.findAll());
        return "cheese/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddCheeseForm(@ModelAttribute  @Valid Cheese newCheese,
                                       Errors errors, @RequestParam int categoryId, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Cheese");
            //model.addAttribute("cheese",newCheese);
            model.addAttribute("categories", categoryDao.findAll());
            return "cheese/add";
        }
        Category cat = categoryDao.findOne(categoryId);
        newCheese.setCategory(cat);
        cheeseDao.save(newCheese);
        return "redirect:";
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveCheeseForm(Model model) {
        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "Remove Cheese");
        return "cheese/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveCheeseForm(@RequestParam int[] cheeseIds, Model model) {
        for (int cheeseId : cheeseIds) {
            try {
                cheeseDao.delete(cheeseId);
            }
            catch(Exception exception){
                model.addAttribute("cheeses", cheeseDao.findAll());
                String cn = cheeseDao.findOne(cheeseId).getName();
                model.addAttribute("title", "Cannot Remove  " + cn + " Cheese because it is added in menu.");
                return "cheese/remove";

            }
        }
        return "redirect:";
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String displayEditCheeseForm(@PathVariable int id , Model model) {

        Cheese cheese = cheeseDao.findOne(id);
        EditCheeseForm editCheeseForm = new EditCheeseForm(id, cheese.getName(),cheese.getDescription(), cheese.getCategory().getId());
        model.addAttribute("title", "Edit Cheese " + cheese.getName() + "(" + cheese.getId()+")");
        model.addAttribute("editCheeseForm", editCheeseForm);
        model.addAttribute("categories", categoryDao.findAll());
        return "cheese/edit";
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.POST)
    public String displayEditCheeseForm(@ModelAttribute  @Valid EditCheeseForm editCheeseForm,
                                        Errors errors, @PathVariable int id, @RequestParam int categoryId, Model model) {

        if(errors.hasErrors()){
            model.addAttribute("title", "Edit Cheese " );
            model.addAttribute("categories", categoryDao.findAll());
            return "cheese/edit";
        }

        Cheese cheese = cheeseDao.findOne(id);
        cheese.setCategory(categoryDao.findOne(categoryId));
        cheese.setDescription(editCheeseForm.getDescription());
        cheese.setName(editCheeseForm.getName());
        cheeseDao.save(cheese);

        return "redirect:/cheese";
    }

    @RequestMapping(value="category/{id}", method = RequestMethod.GET)
    public String listAllCheeseOfSameCategory(@PathVariable int id, Model model){

        Iterable<Cheese> cheeses = cheeseDao.findAll();
        ArrayList<Cheese> cheeseOfSameCategory = new ArrayList<>();
        Category category = categoryDao.findOne(id);
        for(Cheese cheese : cheeses){
            if(cheese.getCategory().getId() == category.getId()) {
                cheeseOfSameCategory.add(cheese);
            }
        }

        model.addAttribute("cheeses", cheeseOfSameCategory);
        model.addAttribute("title", "Cheeses of category " + category.getName());

        return "cheese/index";
    }
}
