package org.launchcode.rest;

import org.launchcode.models.Cheese;
import org.launchcode.models.data.CheeseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class RestCheeseController {

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value="cheese")
    public List<Cheese> getAllCheese(){
            Iterable<Cheese> cheeses =  cheeseDao.findAll();

            return (List<Cheese>) cheeses;
    }

    @RequestMapping(value="cheese",method = RequestMethod.POST)
    public void addCheese(@RequestBody Cheese cheese){
        cheeseDao.save(cheese);
    }

    @RequestMapping(value="cheese/{id}")
    public Cheese getCheeseById(@PathVariable int id){
        Cheese cheese =  cheeseDao.findOne(id);
        return cheese;
    }

    @RequestMapping(value="cheese/{id}",method = RequestMethod.PUT)
    public void updateCheeseById(@PathVariable int id, @RequestBody Cheese cheese){
        cheeseDao.save(cheese);
    }

    @RequestMapping(value="cheese/{id}",method = RequestMethod.DELETE)
    public void deleteCheeseById(@PathVariable int id){
        cheeseDao.delete(id);
    }


}
