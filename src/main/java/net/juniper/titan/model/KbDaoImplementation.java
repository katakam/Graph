package net.juniper.titan.model;


import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rtejasvi
 */
@Repository
public class KbDaoImplementation extends JdbcDaoSupport implements KbDao{
@Autowired
    KbDaoImplementation(DataSource dataSource){
        setDataSource(dataSource);
    }
  



    @Override
    public KnowledgeBase findKbById(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
