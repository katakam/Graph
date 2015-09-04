package net.juniper.titan.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rtejasvi
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import net.juniper.titan.model.PrDao;
import net.juniper.titan.model.ProblemReport;

@Controller
public class PrController {
    @Autowired
    private PrDao prDao;
    
    @RequestMapping("/pr_id.htm")
    @ResponseBody
    public String test(@RequestParam(value = "id") int id) {   
        StringBuilder sb=new StringBuilder();
        ProblemReport pr = prDao.findPrById(id);
        sb.append(pr.getPr_id()).append(" | ").append(pr.getPlatform()).append(" | ").append(pr.getCategory()).append(" | ").append(pr.getDescription());
        return sb.toString();
    }

    
   

    /**
     * @param prDao the prDao to set
     */
    public void setPrDao(PrDao prDao) {
        this.prDao = prDao;
    }
    
    @RequestMapping("/")
    public String returnIndex() {   
        return "redirect:index.html";
    }
}
