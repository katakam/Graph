package net.juniper.titan.model;


import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
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
@Repository
public class PrDaoImplementation extends JdbcDaoSupport implements PrDao{
@Autowired
    PrDaoImplementation(DataSource dataSource){
        setDataSource(dataSource);
    }
  
    @Override
    public ProblemReport findPrById(int prId) {
        ProblemReport pr = null;
        String query = "SELECT ID, PRNUM AS pr_id, PR_DESCRIPTION AS description, PR_CONFIDENTIAL AS confidential,"
                + "PR_SYNOPSIS AS synopsis, PR_PLATFORM AS platform, PR_PRODUCT AS product, PR_CLASS AS prClass,"
                + "PR_CATEGORY AS category, PR_FUNCTIONAL_AREA AS functionalArea, TO_DATE(PR_LAST_MODIFIED,'YYYY-MM-DD') AS lastModified,"
                + "PR_PROBLEM_LEVEL AS problemLevel, PR_KEYWORDS AS keywords, PR_FIX AS fix, PR_WORKAROUND AS workaround,"
                + "PR_SUBMITTER_ID AS submitterId, PR_JTAC_CASE_ID AS jtacCaseId, PR_EXTERNAL_ID AS externalId,"
                + "PR_LEGACY_CUSTOMER AS legacyCustomer, PR_ENVIRONMENT AS environment, PR_RELEASE_NOTE AS releaseNote,"
                + "PR_EXTERNAL_DESCRIPTION AS externalDescription, PR_EXTERNAL_TITLE AS externalTitle, PR_SUPPORT_NOTES AS supportNotes,"
                + "PR_RESOLVED_IN AS resolvedIn, PR_DEV_OWNER_BG_BU AS devOwnerBgBu, PR_SUBMITTER_BG_BU AS submitterBgBu, PR_STATE AS state,"
                + "TO_DATE(PR_ARRIVAL_DATE,'YYYY-MM-DD') AS arrivalDate FROM PR_GSA_VIEW WHERE ID=?";
        
        Object[] inputs = new Object[]{prId};
        pr = (ProblemReport) getJdbcTemplate().queryForObject(query, inputs,
                new BeanPropertyRowMapper(ProblemReport.class));
        return pr;
    }

//    @Override
//    public List<ProblemReport> findAllPrs() {
//        List<ProblemReport> prList = new ArrayList<>();
//        String query = "select * from employee";
//        prList = getJdbcTemplate().query(query,
//                new BeanPropertyRowMapper(ProblemReport.class));
//        return prList;
//    }


}
