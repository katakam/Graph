package net.juniper.titan.model;


import java.util.List;
import net.juniper.titan.model.ProblemReport;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rtejasvi
 */
public interface PrDao {
    public ProblemReport findPrById(int Id);

//    public List<ProblemReport> findAllPrs(); 
}
