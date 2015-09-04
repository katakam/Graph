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
import net.juniper.titan.model.KbDao;
import net.juniper.titan.model.KnowledgeBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class KbController {
    @Autowired
    private KbDao kbDao;
    
    @RequestMapping("/kb_id.htm")
    @ResponseBody
    public String test(@RequestParam(value = "id") int id) {   
        StringBuilder sb=new StringBuilder();
        KnowledgeBase kb = kbDao.findKbById(id);
        //sb.append(kb.getPr()).append(" | ").append(kb.getPlatform()).append(" | ").append(kb.getCategory()).append(" | ").append(kb.getDescription());
        return sb.toString();
    }

    /**
     * @param kbDao the kbDao to set
     */
    public void setKbDao(KbDao kbDao) {
        this.kbDao = kbDao;
    }
    
  
}
