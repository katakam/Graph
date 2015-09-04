/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.juniper.titan.controller;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanIndexQuery;
import com.thinkaurelius.titan.core.TitanIndexQuery.Result;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.juniper.titan.Constants;
import net.juniper.titan.JaxbHelper;
import net.juniper.titan.model.PrDao;
import net.juniper.titan.model.ProblemReport;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author rtejasvi
 */
@Controller
public class TitanController {
    
    @Autowired
    private PrDao prDao;
    
    private Configuration getTitanConf() {
        Configuration conf = new BaseConfiguration();
        conf.setProperty("index.search.backend", "elasticsearch");
        conf.setProperty("index.search.directory", "/tmp/searchindex");
        conf.setProperty("index.search.hostname", "127.0.0.1");
        conf.setProperty("index.search.client-only", "false"); 
        
        
        conf.setProperty("storage.backend","cassandrathrift");
        conf.setProperty("storage.hostname","10.81.53.213");
        conf.setProperty("cache.db-cache", true);
        conf.setProperty("cache.db-cache-clean-wait", 20);
        conf.setProperty("cache.db-cache-time", 180000);
        conf.setProperty("cache.db-cache-size", 0.25);
        conf.setProperty("index.search.backend", "elasticsearch");
        conf.setProperty("index.search.hostname", "10.81.53.213");
        conf.setProperty("index.search.directory", "/tmp/searchindex");
        conf.setProperty("index.search.elasticsearch.client-only", false);

        return conf;
    }
    
    private TitanGraph getTitanGraph() {
        //Please change this to a property later.
        
        TitanGraph g = TitanFactory.open("/home/ec2-user/titan-0.5.4-hadoop2/conf/titan-cassandra-es.properties");
//        TitanGraph g = TitanFactory.open(getTitanConf());
        return g;
    }
    
    @RequestMapping("/all_nodes")
    @ResponseBody
    public String test(){
        TitanGraph g = getTitanGraph();
        
        Iterable<Vertex> vertices = g.getVertices();
        if(vertices==null){
            return Constants.emptyGraphInfo+Constants.newLine;
        }
        else{
            StringBuilder output = new StringBuilder();
            for(Vertex x : vertices){
                
            Set<String> keys = x.getPropertyKeys();
            for(String key : keys){
                String value = ""+x.getProperty(key);
                output.append(key);
                output.append(" : ");
                output.append(value);
                output.append(Constants.newLine);
            }
            output.append("----------------");
            output.append(Constants.newLine);
            }
            return output.toString();
        }  
    }
    
    @RequestMapping("/search_ind.htm")
    @ResponseBody
    public String search_ind(@RequestParam(value = "type", required=true) String type,@RequestParam(value = "id",  required=false) String id, 
            @RequestParam(value = "keyw",  required=false) String keyw) {   
        TitanGraph g = getTitanGraph();
        Vertex node;
        StringBuilder sb=new StringBuilder();
        int i=0;
        
        if(type.equals("sr")){
            if(id!=null){
                for (Result<Vertex> verticeResult : g.indexQuery("SR_CASEIDind", "v.\"SR_CASEID\":" + "\"" + id + "\"").vertices()) {
                    i++;
                    node = verticeResult.getElement();
                    sb.append(" Case ").append(i);
                    sb.append("<table class=\"table table-hover\">");
                    Set<String> keys = node.getPropertyKeys();
                    for (String key : keys) {
                        sb.append("<tr>");
                        String value = "" + node.getProperty(key);
                        sb.append("<td>");
                        sb.append(key);
                        sb.append("</td>");

                        if (key.equals("SR_PRNUMBER")) {
                            if (!value.equals("EMPTY")) {
                                sb.append("<td>");
                                sb.append("<a href=\"/TitanSpring-1/pr.html?pr_id=" + value + "\">" + value + "</a>");
                                sb.append("</td>");
                            } else {
                                sb.append("<td>");
                                sb.append(value);
                                sb.append("</td>");
                            }
                        } else {
                            sb.append("<td>");
                            sb.append(value);
                            sb.append("</td>");
                        }
                    }
                    sb.append("</table>");
                    sb.append("</br>");
                    sb.append("</br>").append("</br>").append("</br>");
                }
            }
            else if(keyw!=null){
                for (Result<Vertex> verticeResult : g.indexQuery("SR_EXTENDED_DESCind", "v.\"SR_EXTENDED_DESC\":(" +keyw+")").vertices()) {
                    i++;
                    node = verticeResult.getElement();
                    sb.append(" Case ").append(i);
                    sb.append("<table class=\"table table-hover\">");
                    Set<String> keys = node.getPropertyKeys();
                    for (String key : keys) {
                        sb.append("<tr>");
                        String value = "" + node.getProperty(key);
                        sb.append("<td>");
                        sb.append(key);
                        sb.append("</td>");

                        if (key.equals("SR_PRNUMBER")) {
                            if (!value.equals("EMPTY")) {
                                sb.append("<td>");
                                sb.append("<a href=\"/TitanSpring-1/pr.html?pr_id=" + value + "\">" + value + "</a>");
                                sb.append("</td>");
                            } else {
                                sb.append("<td>");
                                sb.append(value);
                                sb.append("</td>");
                            }
                        } else {
                            sb.append("<td>");
                            sb.append(value);
                            sb.append("</td>");
                        }
                    }
                    sb.append("</table>");
                    sb.append("</br>");
                    sb.append("</br>").append("</br>").append("</br>");
                }
            }
            
        }
        else if(type.equals("pr")){
            if(id!=null){
                for (Result<Vertex> verticeResult : g.indexQuery("pr_idind", "v.\"pr_id\":" + id).vertices()) {
                    i++;
                    node = verticeResult.getElement();
                    sb.append(" Case ").append(i);
                    sb.append("<table class=\"table table-hover\">");
                    Set<String> keys = node.getPropertyKeys();
                    Iterable<Vertex> srNodes = node.getVertices(Direction.IN);
                    for (Vertex srNode : srNodes) {
                        sb.append("From Direcion in!");
                        sb.append(srNode.getProperty("SR_CASEID"));
                    }
                    sb.append("<tr>");
                    sb.append("<td>");
                    sb.append("Associated SR's");
                    sb.append("<td>");
                    for (Vertex srNode : srNodes) {
                        String value = srNode.getProperty("SR_CASEID");
                        if (!"EMPTY".equals(value));
                        sb.append("<a href=\"/TitanSpring-1/sr.html?sr_id=" + value + "\">" + value + "</a>");
                        sb.append(" ");
                    }
                    sb.append("</td>");
                    sb.append("</tr>");

                    for (String key : keys) {
                        sb.append("<tr>");
                        String value = "" + node.getProperty(key);
                        sb.append("<td>");
                        sb.append(key);
                        sb.append("</td>");
                    //To link to PR

                        sb.append("<td>");
                        sb.append(value);
                        sb.append("</td>");

                        sb.append("</tr>");
                    }
                    sb.append("</table>");
                    sb.append("</br>");
                    sb.append("</br>").append("</br>").append("</br>");

                }
            }
            else if(keyw!=null){
                for (Result<Vertex> verticeResult : g.indexQuery("keywordsind", "v.keywords:(" +keyw+")").vertices()) {
                    i++;
                    node = verticeResult.getElement();
                    sb.append(" Case ").append(i);
                    sb.append("<table class=\"table table-hover\">");
                    Set<String> keys = node.getPropertyKeys();
                    Iterable<Vertex> srNodes = node.getVertices(Direction.IN);
                    for (Vertex srNode : srNodes) {
                        sb.append("From Direcion in!");
                        sb.append(srNode.getProperty("SR_CASEID"));
                    }
                    sb.append("<tr>");
                    sb.append("<td>");
                    sb.append("Associated SR's");
                    sb.append("<td>");
                    for (Vertex srNode : srNodes) {
                        String value = srNode.getProperty("SR_CASEID");
                        if (!"EMPTY".equals(value));
                        sb.append("<a href=\"/TitanSpring-1/sr.html?sr_id=" + value + "\">" + value + "</a>");
                        sb.append(" ");
                    }
                    sb.append("</td>");
                    sb.append("</tr>");

                    for (String key : keys) {
                        sb.append("<tr>");
                        String value = "" + node.getProperty(key);
                        sb.append("<td>");
                        sb.append(key);
                        sb.append("</td>");
                        //To link to PR

                        sb.append("<td>");
                        sb.append(value);
                        sb.append("</td>");

                        sb.append("</tr>");
                    }
                    sb.append("</table>");
                    sb.append("</br>");
                    sb.append("</br>").append("</br>").append("</br>");

                }
            }
            
        }
        
        return sb.toString();
    }
    
 
    
    @RequestMapping("/add_vertex")
    @ResponseBody
    public String addVertex( @RequestParam(value="cname", required=true) String name,
                             @RequestParam(value="cid", required = true) String id,
                             @RequestParam(value="desc", required = true) String desc,
                             @RequestParam(value="status", required = true) String status){
        
        TitanGraph g = getTitanGraph();
        
        if(g==null){
            return "Graph not initialised";
        }
        else{
            Vertex a = g.addVertex(null);
            a.setProperty("Name",name);
            a.setProperty("cid",id);
            a.setProperty("Desc",desc);
            a.setProperty("Status",status);
            g.commit();
           
            return "Vertex with so and so properties were added.";
        }
        
    }
    
    @RequestMapping("/server_test")
    @ResponseBody
    public String server_test(){
        return "Working!";
    }
    
    @RequestMapping("/index_test")
    @ResponseBody
    public String index_test(){
        TitanGraph g = getTitanGraph();
        String location = "/home/ec2-user/gsafeed.xml";
        Logger.getLogger(TitanController.class.getName()).log(Level.INFO, "Location is {0}", location);
        JaxbHelper m = new JaxbHelper(location,  prDao);
        return m.checkAndAddIndex("lol", g);
      
    }
    
   
    
    @RequestMapping("/gremlin_test")
    @ResponseBody
    public String gremlin_test(){
        TitanGraph g = getTitanGraph();
        String location = "/home/ec2-user/gsafeed.xml";
        Logger.getLogger(TitanController.class.getName()).log(Level.INFO, "Location is {0}", location);
        JaxbHelper m = new JaxbHelper(location, prDao);
        return m.gremlinCheck(g);
    }
    
    @RequestMapping("/search.htm")
    @ResponseBody
    public String search(@RequestParam(value = "type", required = true) String type, @RequestParam(value = "pf", required = false) String platform,
            @RequestParam(value = "re", required = false) String release, @RequestParam(value = "se", required = false) String series,
            @RequestParam(value = "pr", required = false) String priority) {
        if(release == null || release.equalsIgnoreCase("")){
            release = null;
        }
        if(platform == null || platform.equalsIgnoreCase("")){
            platform = null;
        }
        if(series == null || series.equalsIgnoreCase("")){
            series = null;
        }
        if(priority == null || priority.equalsIgnoreCase("")){
            priority = null;
        }
        TitanGraph g = getTitanGraph();
        Vertex node;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        GremlinPipeline pipe = new GremlinPipeline();
        if (type.equals("sr")) {
            if(platform!=null){
                if(release!=null){
                    if(series!=null){
                        if(priority!=null){
                              pipe.start(g).V().has("SR_SERIES", series).has("SR_PLATFORM", platform)
                                      .has("SR_RELEASE", release).has("SR_PRIORITY", priority).cast(Vertex.class);
                        }
                        else{
                            //priority is null
                            pipe.start(g).V().has("SR_SERIES", series).has("SR_PLATFORM", platform)
                                      .has("SR_RELEASE", release).cast(Vertex.class);
                        }
                    }
                    else{
                        //series is null
                        if (priority != null) {
                            pipe.start(g).V().has("SR_PLATFORM", platform)
                                    .has("SR_RELEASE", release).has("SR_PRIORITY", priority).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V().has("SR_PLATFORM", platform)
                                    .has("SR_RELEASE", release).cast(Vertex.class);
                        }
                    }
                }
                else{
                    //release is null
                    if (series != null) {
                        if (priority != null) {
                            pipe.start(g).V().has("SR_SERIES", series).has("SR_PLATFORM", platform)
                                    .has("SR_PRIORITY", priority).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V().has("SR_SERIES", series).has("SR_PLATFORM", platform)
                                    .cast(Vertex.class);
                        }
                    } else {
                        //series is null
                        if (priority != null) {
                            pipe.start(g).V().has("SR_PLATFORM", platform)
                                    .has("SR_PRIORITY", priority).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V().has("SR_PLATFORM", platform)
                                    .cast(Vertex.class);
                        }
                    }
                }
            }
            else{
                //platform is null
                if (release != null) {
                    if (series != null) {
                        if (priority != null) {
                            pipe.start(g).V().has("SR_SERIES", series)
                                    .has("SR_RELEASE", release).has("SR_PRIORITY", priority).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V().has("SR_SERIES", series)
                                    .has("SR_RELEASE", release).cast(Vertex.class);
                        }
                    } else {
                        //series is null
                        if (priority != null) {
                            pipe.start(g).V()
                                    .has("SR_RELEASE", release).has("SR_PRIORITY", priority).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V()
                                    .has("SR_RELEASE", release).cast(Vertex.class);
                        }
                    }
                } else {
                    //release is null
                    if (series != null) {
                        if (priority != null) {
                            pipe.start(g).V().has("SR_SERIES", series)
                                    .has("SR_PRIORITY", priority).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V().has("SR_SERIES", series)
                                    .cast(Vertex.class);
                        }
                    } else {
                        //series is null
                        if (priority != null) {
                            pipe.start(g).V()
                                    .has("SR_PRIORITY", priority).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe = null;
                        }
                    }
                }
            }
            if (pipe != null) {
         
                while (pipe.hasNext()) {
                    i++;
                    node = (Vertex) pipe.next();
                    sb.append(" <h4>Case ").append(i).append("</h4>");
                    sb.append("<table class=\"table table-hover\">");
                    Set<String> keys = node.getPropertyKeys();
                    for (String key : keys) {
                        sb.append("<tr>");
                        String value = "" + node.getProperty(key);
                        sb.append("<td>");
                        sb.append(key);
                        sb.append("</td>");

                        if (key.equals("SR_PRNUMBER")) {
                            if (!value.equals("EMPTY")) {
                                sb.append("<td>");
                                sb.append("<a href=\"/TitanSpring-1/pr.html?pr_id=" + value + "\">" + value + "</a>");
                                sb.append("</td>");
                            } else {
                                sb.append("<td>");
                                sb.append(value);
                                sb.append("</td>");
                            }
                        } else {
                            sb.append("<td>");
                            sb.append(value);
                            sb.append("</td>");
                        }
                    }
                    sb.append("</table>");
                    sb.append("</br>");
                    sb.append("</br>").append("</br>").append("</br>");
                }
            } 
            else{
                return " ";
            }

        } else if (type.equals("pr")) {
            String functionalArea = release;
            String submitterId = priority;
            String problemLevel = series;
            if (platform != null) {
                if (functionalArea != null) {
                    if (problemLevel != null) {
                        if (submitterId != null) {
                            pipe.start(g).V().has("problemLevel", problemLevel).has("platform", platform)
                                    .has("functionalArea", functionalArea).has("submitterId", submitterId).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V().has("problemLevel", problemLevel).has("platform", platform)
                                    .has("functionalArea", functionalArea).cast(Vertex.class);
                        }
                    } else {
                        //series is null
                        if (submitterId != null) {
                            pipe.start(g).V().has("platform", platform)
                                    .has("functionalArea", functionalArea).has("submitterId", submitterId).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V().has("platform", platform)
                                    .has("functionalArea", functionalArea).cast(Vertex.class);
                        }
                    }
                } else {
                    //release is null
                    if (problemLevel != null) {
                        if (submitterId != null) {
                            pipe.start(g).V().has("problemLevel", problemLevel).has("platform", platform)
                                    .has("submitterId", submitterId).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V().has("problemLevel", problemLevel).has("platform", platform)
                                    .cast(Vertex.class);
                        }
                    } else {
                        //series is null
                        if (submitterId != null) {
                            pipe.start(g).V().has("platform", platform)
                                    .has("submitterId", submitterId).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V().has("platform", platform)
                                    .cast(Vertex.class);
                        }
                    }
                }
            } else {
                //platform is null
                if (release != null) {
                    if (problemLevel != null) {
                        if (submitterId != null) {
                            pipe.start(g).V().has("problemLevel", problemLevel)
                                    .has("functionalArea", functionalArea).has("submitterId", submitterId).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V().has("problemLevel", problemLevel)
                                    .has("functionalArea", functionalArea).cast(Vertex.class);
                        }
                    } else {
                        //series is null
                        if (submitterId != null) {
                            pipe.start(g).V()
                                    .has("functionalArea", functionalArea).has("submitterId", submitterId).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V()
                                    .has("functionalArea", functionalArea).cast(Vertex.class);
                        }
                    }
                } else {
                    //release is null
                    if (series != null) {
                        if (submitterId != null) {
                            pipe.start(g).V().has("problemLevel", problemLevel)
                                    .has("submitterId", submitterId).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe.start(g).V().has("problemLevel", problemLevel)
                                    .cast(Vertex.class);
                        }
                    } else {
                        //series is null
                        if (submitterId != null) {
                            pipe.start(g).V()
                                    .has("submitterId", submitterId).cast(Vertex.class);
                        } else {
                            //priority is null
                            pipe = null;
                        }
                    }
                }
            }
            
            
            if (pipe != null) {
                while (pipe.hasNext()) {
                    node = (Vertex) pipe.next();
                    i++;
                    sb.append(" <h4>Case ").append(i).append("</h4>");
                    sb.append("<table class=\"table table-hover\">");
                    Set<String> keys = node.getPropertyKeys();
                    Iterable<Vertex> srNodes = node.getVertices(Direction.IN);
//                    for (Vertex srNode : srNodes) {
//                        sb.append(srNode.getProperty("SR_CASEID"));
//                    }
                    sb.append("<tr>");
                    sb.append("<td>");
                    sb.append("Associated SR's");
                    sb.append("<td>");
                    for (Vertex srNode : srNodes) {
                        String value = srNode.getProperty("SR_CASEID");
                        if (!"EMPTY".equals(value));
                        sb.append("<a href=\"/TitanSpring-1/sr.html?sr_id=" + value + "\">" + value + "</a>");
                        sb.append(" ");
                    }
                    sb.append("</td>");
                    sb.append("</tr>");

                    for (String key : keys) {
                        sb.append("<tr>");
                        String value = "" + node.getProperty(key);
                        sb.append("<td>");
                        sb.append(key);
                        sb.append("</td>");
                        //To link to PR

                        sb.append("<td>");
                        sb.append(value);
                        sb.append("</td>");

                        sb.append("</tr>");
                    }
                    sb.append("</table>");
                    sb.append("</br>");
                    sb.append("</br>").append("</br>").append("</br>");

                }
            } 

        }

        return sb.toString();
    }
    
//    @RequestMapping("/jaxb_test")
//    @ResponseBody
//    public String jaxbyTest(){
//        //"/home/ec2-user/gsafeed.xml" in server.
//        // C:\\Users\\rtejasvi\\Documents\\gsafeed.xml in localhost
//        //String location = "C:\\\\Users\\\\rtejasvi\\\\Documents\\\\gsafeed.xml";
//        String location = "/home/ec2-user/gsafeed.xml";
//        Logger.getLogger(GnatsController.class.getName()).log(Level.INFO, "Location is {0}", location);
//        JaxbHelper m = new JaxbHelper(location);
//        return m.parseXml();
//    }

    @RequestMapping("/load_sr_into_graph")
    @ResponseBody
    public String loadSrGraph(){
        TitanGraph g = getTitanGraph();
        String location = "/home/ec2-user/gsafeeds";
        JaxbHelper m = new JaxbHelper(location, prDao);
        return m.loadSerReqFolderToGraph(g);
    }

    /**
     * @param prDao the prDao to set
     */
    public void setPrDao(PrDao prDao) {
        this.prDao = prDao;
    }
    
}
