package net.juniper.titan;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.esotericsoftware.minlog.Log;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanIndexQuery.Result;
import com.thinkaurelius.titan.core.schema.Mapping;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.Pipe;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import jaxbBindings.*;
import jaxbBindings.Gsafeed.Group.Record;
import jaxbBindings.Gsafeed.Group.Record.Metadata;
import jaxbBindings.Gsafeed.Group.Record.Metadata.Meta;
import net.juniper.titan.model.PrDao;
import net.juniper.titan.model.ProblemReport;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
/**
 *
 * @author rtejasvi
 * 
 * Property Keys will have the same name as the key. Index will have the name of the property key suffixed with ind.
 * Example : Vertex key: name. Property key: name. Index name : nameind.
 */
public class JaxbHelper{
    JAXBContext jc;
    Unmarshaller u;
    String xmlLocation;
    XMLInputFactory xif;

    private PrDao prDao;
    
    
    public JaxbHelper(String location, PrDao prDao){
        try{
            jc = JAXBContext.newInstance("jaxbBindings");
            xmlLocation = location;
            xif = XMLInputFactory.newFactory();
            xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            this.prDao = prDao;
            u = jc.createUnmarshaller();  

        } catch (JAXBException ex) {
            Logger.getLogger(JaxbHelper.class.getName()).log(Level.SEVERE, "Jaxb context failed to initialise.", ex);
        }  
    }
    
    
    public String jaxbTest(){
        try {   
            StringBuilder sb = new StringBuilder();
            Gsafeed gf = (Gsafeed) u.unmarshal(new FileReader(this.xmlLocation));
            List<jaxbBindings.Gsafeed.Group.Record> records = gf.getGroup().getRecord();
            for (jaxbBindings.Gsafeed.Group.Record record : records) {
                sb.append(record.getAction());      
                sb.append("</br>");
            }
            return sb.toString();   
           
        } catch (JAXBException ex) {
            Logger.getLogger(JaxbHelper.class.getName()).log(Level.SEVERE, null, ex);
            return "JAXB error";
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JaxbHelper.class.getName()).log(Level.SEVERE,"The xml file "+ this.xmlLocation+" was not found.", ex);
            return "The xml file "+ this.xmlLocation+" was not found.";
        }
    }
    
   
    
    public String loadSerReqFolderToGraph(TitanGraph g){
            if (g == null) {
                return "Graph not initialised";
            } else {
                //Titan Graph
                TitanManagement mgmt = g.getManagementSystem();
                
                //Label Check for the vertex
                if(!mgmt.containsVertexLabel("service_request")){
                    mgmt.makeVertexLabel("service_request").make();
                    mgmt.commit();// Create a labeled vertex
                }
                String result = "";
                final File folder = new File(this.xmlLocation);
                result = listFilesForFolder(folder, g);
                
              return result;
            }
        
    }
    
    public String listFilesForFolder(File folder, TitanGraph g){
        String result = "";
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, g);
            } else {
                result += addSrToGraph(g, fileEntry.getAbsolutePath());
            }
        }
        return result;
    }
    
    public String addSrToGraph(TitanGraph g, String loc){
        XMLStreamReader xsr = null;
        try {
            //XML parsing
            xsr = xif.createXMLStreamReader(new StreamSource(loc));
        } catch (XMLStreamException ex) {
            Logger.getLogger(JaxbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
//        FileReader fr = null;
//        try {
//            fr = new FileReader(loc);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(JaxbHelper.class.getName()).log(Level.SEVERE, null, ex);
//        }
        Gsafeed gf = null;
        try {
            gf = (Gsafeed) u.unmarshal(xsr);
        } catch (JAXBException ex) {
            Logger.getLogger(JaxbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Record> records = gf.getGroup().getRecord();

        Metadata metadata;
        String encoding;
        String key;
        String value;
        Boolean first_record = true;
        String result;
        for (Record record : records) {
            //For indexing
            String id = record.getUrl();
            String[] splitResult = id.split("/");
            id = splitResult[splitResult.length - 1];
            Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "Id : {0}", id);
            Vertex prNode = null;
            if (first_record) {
                first_record = false;
                metadata = record.getMetadata();
                List<Meta> metaList = metadata.getMeta();
                if (!checkIfSrNodeExists(id, g, "SR")) {

                            //Node doens't exist in the graph so create!
                    Vertex srNode = g.addVertexWithLabel("service_request");
                    //New node.
                    for (Meta meta : metaList) {
                        encoding = meta.getEncoding();
                        if (encoding == null) {
                            key = meta.getName();
                            value = meta.getContent();
                            //Adding index. Note : index isn't added if it isn't the first record as we assume that the structure
                            // of record element stays the same throughout.
                            
                            //Check if it's associated with a non-empty PR
                            if("SR_PRNUMBER".equals(key) && !"EMPTY".equals(value)){
                                //Check if that pr node exists else create. 
                                //Edge will be added once all properties are filled in.
                               
                                prNode = checkAndAddPrNode(g, Integer.parseInt(value));
                                
                                
                            }
                           
                            result = checkAndAddIndex(key, g);
                            srNode.setProperty(key, value);
                            Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "The result of indexing is : {0}", result);
                        } else {
                            key = new String(Base64.decodeBase64(meta.getName().getBytes()));
                            value = new String(Base64.decodeBase64(meta.getContent().getBytes()));
                            result = checkAndAddIndex(key, g);
                            if ("SR_PRNUMBER".equals(key) && !"EMPTY".equals(value)) {
                                //Check if that pr node exists else create. 
                                //Edge will be added once all properties are filled in.
                                prNode = checkAndAddPrNode(g, Integer.parseInt(value));

                            }
                            srNode.setProperty(key, value);
                        }

                    }
                    
                    g.commit();
                    if (prNode != null) {
                        checkAndAddEdge(prNode, srNode);
                        g.commit();
                    }
                    
                } else {
                            //Node exists so update!
                    Vertex srNode = null;
                    for (Meta meta : metaList) {
                        encoding = meta.getEncoding();
                        if (encoding == null) {
                            key = meta.getName();
                            value = meta.getContent();
                            //Adding index
                            result = checkAndAddIndex(key, g);
                            Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "The result of indexing is : {0}", result);
                            if ("SR_PRNUMBER".equals(key) && !"EMPTY".equals(value)) {
                                //Check if that pr node exists else create. 
                                //Edge will be added once all properties are filled in.

                                prNode = checkAndAddPrNode(g, Integer.parseInt(value));

                            }
                            
                            for (Result<Vertex> verticeResult : g.indexQuery("SR_CASEIDind", "v.\"SR_CASEID\":" + "\"" + id + "\"").vertices()) {
                                srNode = verticeResult.getElement();
                                System.out.println(srNode + ": was updated.");
                            }
                            if (srNode != null) {
                                srNode.setProperty(key, value);
                            } else {
                                return "indexQuery may be failing, the node exists, but isn't retrievable to update.";
                            }

                        } else {
                            key = new String(Base64.decodeBase64(meta.getName().getBytes()));
                            value = new String(Base64.decodeBase64(meta.getContent().getBytes()));
                            result = checkAndAddIndex(key, g);
                            Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "The result of indexing is : {0}", result);
                           
                            for (Result<Vertex> verticeResult : g.indexQuery("SR_CASEIDind", "v.\"SR_CASEID\":" + "\"" + id + "\"").vertices()) {
                                srNode = verticeResult.getElement();
                                System.out.println(srNode + ": is updated." );
                            }
                            if (srNode != null) {
                                srNode.setProperty(key, value);
                                if ("SR_PRNUMBER".equals(key) && !"EMPTY".equals(value)) {
                                //Check if that pr node exists else create. 
                                    //Edge will be added once all properties are filled in.

                                    prNode = checkAndAddPrNode(g, Integer.parseInt(value));

                                }
                            } else {
                                return "indexQuery may be failing, the node exists, but isn't retrievable to update.";
                            }
                        }

                    }
                    
                    g.commit();
                    if (prNode != null && srNode!=null) {
                        checkAndAddEdge(prNode, srNode);
                        g.commit();
                    }
                }

            } else {
                        //Not the first record
                //So the checking and adding label is skipped.
                metadata = record.getMetadata();
                List<Meta> metaList = metadata.getMeta();
                if (!checkIfSrNodeExists(id, g, "SR")) {
                    //Node doens't exist in the graph so create!

                    Vertex srNode = g.addVertexWithLabel("service_request");
                    //New node.
                    for (Meta meta : metaList) {
                        encoding = meta.getEncoding();
                        if (encoding == null) {
                            key = meta.getName();
                            value = meta.getContent();
                            srNode.setProperty(key, value);
                            if ("SR_PRNUMBER".equals(key) && !"EMPTY".equals(value)) {
                                //Check if that pr node exists else create. 
                                //Edge will be added once all properties are filled in.

                                prNode = checkAndAddPrNode(g, Integer.parseInt(value));

                            }

                        } else {
                            key = new String(Base64.decodeBase64(meta.getName().getBytes()));
                            value = new String(Base64.decodeBase64(meta.getContent().getBytes()));
                            srNode.setProperty(key, value);
                            if ("SR_PRNUMBER".equals(key) && !"EMPTY".equals(value)) {
                                //Check if that pr node exists else create. 
                                //Edge will be added once all properties are filled in.

                                prNode = checkAndAddPrNode(g, Integer.parseInt(value));

                            }
                        }
                    }
                    g.commit();
                    if (prNode != null && srNode!=null) {
                        checkAndAddEdge(prNode, srNode);
                        g.commit();
                    }
                } else {
                    //Node exists so update!
                    Vertex srNode = null;
                    for (Meta meta : metaList) {
                        encoding = meta.getEncoding();
                        if (encoding == null) {
                            key = meta.getName();
                            value = meta.getContent();
                            
                            for (Result<Vertex> verticeResult : g.indexQuery("SR_CASEIDind", "v.\"SR_CASEID\":" + "\"" + id + "\"").vertices()) {
                                srNode = verticeResult.getElement();
                                System.out.println(srNode + ": " + verticeResult.getScore());
                            }
                            if (srNode != null) {
                                srNode.setProperty(key, value);
                                if ("SR_PRNUMBER".equals(key) && !"EMPTY".equals(value)) {
                                //Check if that pr node exists else create. 
                                    //Edge will be added once all properties are filled in.

                                    prNode = checkAndAddPrNode(g, Integer.parseInt(value));

                                }
                            } else {
                                return "indexQuery may be failing, the node exists, but isn't retrievable to update.";
                            }

                        } else {
                            key = new String(Base64.decodeBase64(meta.getName().getBytes()));
                            value = new String(Base64.decodeBase64(meta.getContent().getBytes()));
                            result = checkAndAddIndex(key, g);
                            Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "The result of indexing is : {0}", result);
                            
                            for (Result<Vertex> verticeResult : g.indexQuery("SR_CASEIDind", "v.\"SR_CASEID\":" + "\"" + id + "\"").vertices()) {
                                srNode = verticeResult.getElement();
                                System.out.println(srNode + ": " + verticeResult.getScore());
                            }
                            if (srNode != null) {
                                srNode.setProperty(key, value);
                                if ("SR_PRNUMBER".equals(key) && !"EMPTY".equals(value)) {
                                //Check if that pr node exists else create. 
                                    //Edge will be added once all properties are filled in.

                                    prNode = checkAndAddPrNode(g, Integer.parseInt(value));

                                }
                            } else {
                                return "indexQuery may be failing, the node exists, but isn't retrievable to update.";
                            }
                        }

                    }
                    g.commit();
                    if (prNode != null && srNode != null) {
                        checkAndAddEdge(prNode, srNode);
                        g.commit();
                    }
                }
            }
        }
        
        return "Entered into the graph!";
    }
    
    public String checkAndAddIndex(String keyName, TitanGraph g){
        try{
            //checks for property key and index, creates them if they aren't already present.
            TitanManagement mgmt = g.getManagementSystem();
            //If property key is absent, create it.
            PropertyKey key = mgmt.getPropertyKey(keyName);
            if(key ==null){
                Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "The {0} key isn't present", keyName);
                key = mgmt.makePropertyKey(keyName).dataType(String.class).make();
                Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "The {0} key created", keyName);
            }
            String indexName = keyName+"ind";
            if(mgmt.getGraphIndex(indexName)==null){
                Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "The {0} index isn't present", keyName);
                mgmt.buildIndex(indexName,Vertex.class).addKey(key, Mapping.TEXT.getParameter()).buildMixedIndex("search");
                Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "The {0} index created", keyName);
            }
            mgmt.commit();
            Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "Commited to the graph!", keyName);
            return "Worked";
        }
        catch(Exception e){
            return "Didn't work";
        }
        
    }

    private boolean checkIfSrNodeExists(String id, TitanGraph g, String nodeType) {
        //SR for service requests
        //PR for problem reports and KB for knowledge base
        Vertex srNode = null;
        TitanManagement mgmt = g.getManagementSystem();
        g.indexQuery(id, id);
        if(mgmt.getGraphIndex("SR_CASEIDind")==null){
            return false;
        }
        else{
            for (Result<Vertex> verticeResult : g.indexQuery("SR_CASEIDind", "v.\"SR_CASEID\":" + "\""+id+"\"").vertices()) {
                srNode = verticeResult.getElement();
                srNode.query();
                System.out.println(srNode + ": " + verticeResult.getScore());
            }
            if (srNode == null) {
                return false;
            } else {
                return true;
            }
        }
        
    }
    
    
    public String gremlinCheck(TitanGraph g){ 
        GremlinPipeline pipe = new GremlinPipeline();
        
        pipe.start(g).V().has("SR_SERIES", "M-Series").has("SR_SUBFEATURE", "FPC").cast(Vertex.class);
        String result = "Nothing yet ";
        Vertex a;
        while(pipe.hasNext()){
            a = (Vertex) pipe.next();
            for(String key:a.getPropertyKeys()){
                
                result+=key;
                result+=" : ";
                result+=a.getProperty(key);
                result+="</br>";
            }
            result+=( pipe.next().toString());
            result+=" : ";
           
        }
        return result;
    }

    private void checkAndAddEdge(Vertex prNode, Vertex srNode) {
        //Edge is out from sr to pr
        Vertex pr = null;
        Iterable<Edge> edges = srNode.getEdges(Direction.OUT);
        
        for(Edge e : edges){
            pr = e.getVertex(Direction.IN);
            if(pr==prNode){
                break;
            }
        }
        if(pr!=prNode){
            srNode.addEdge("linked to", prNode);
        }
        
    }

    private Vertex checkAndAddPrNode(TitanGraph g, int pr_id) {
        Vertex prNode=null;
        TitanManagement mgmt = g.getManagementSystem();
        
        
        if (mgmt.getGraphIndex("pr_idind") == null) {
            //Then create index and create the pr node.
            //1. Creating index.
            String keyName = "pr_id";
            //If property key is absent, create it.
            PropertyKey key = mgmt.getPropertyKey(keyName);
            if (key == null) {
                Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "The {0} key isn't present", keyName);
                key = mgmt.makePropertyKey(keyName).dataType(String.class).make();
                Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "The {0} key created", keyName);
            }
            String indexName = keyName + "ind";
            mgmt.buildIndex(indexName, Vertex.class).addKey(key, Mapping.TEXT.getParameter()).buildMixedIndex("search");
            Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "The {0} index created", keyName);
            mgmt.commit();
            Logger.getLogger(JaxbHelper.class.getName()).log(Level.INFO, "Commited to the graph!", keyName);
            
            //2. Create the prNode
            prNode = g.addVertexWithLabel("problem_report");    
            ProblemReport pr = prDao.findPrById(pr_id);
            prNode.setProperty("pr_id", pr.getPr_id());
            prNode.setProperty("description", pr.getDescription());
            prNode.setProperty("confidential", pr.getConfidential());
            prNode.setProperty("synopsis", pr.getSynopsis());
            prNode.setProperty("platform", pr.getPlatform());
            prNode.setProperty("product", pr.getProduct());
            prNode.setProperty("prClass", pr.getPrClass());
            prNode.setProperty("category", pr.getCategory());
            prNode.setProperty("functionalArea", pr.getFunctionalArea());
            prNode.setProperty("lastModified", pr.getLastModified());
            prNode.setProperty("problemLevel", pr.getProblemLevel());
            prNode.setProperty("keywords", pr.getKeywords());
            prNode.setProperty("fix", pr.getFix());
            prNode.setProperty("workaround", pr.getWorkaround());
            prNode.setProperty("submitterId", pr.getSubmitterId());
            prNode.setProperty("jtacCaseId", pr.getJtacCaseId());
            prNode.setProperty("externalId", pr.getExternalId());
            prNode.setProperty("legacyCustomer", pr.getLegacyCustomer());
            prNode.setProperty("environment", pr.getEnvironment());
            prNode.setProperty("releaseNote", pr.getReleaseNote());
            prNode.setProperty("externalDescription", pr.getExternalDescription());
            prNode.setProperty("externalTitle", pr.getExternalTitle());
            prNode.setProperty("supportNotes", pr.getSupportNotes());
            prNode.setProperty("resolvedIn", pr.getResolvedIn());
            prNode.setProperty("devOwnerBgBu", pr.getDevOwnerBgBu());
            prNode.setProperty("submitterBgBu", pr.getSubmitterBgBu());
            prNode.setProperty("state", pr.getState());
            prNode.setProperty("arrivalDate", pr.getArrivalDate());
            g.commit();
        } else {
            //Means index is present.
            for (Result<Vertex> verticeResult : g.indexQuery("pr_idind", "v.\"pr_id\":" + "\"" + pr_id + "\"").vertices()) {
                prNode = verticeResult.getElement();
            }
            if(prNode==null){
                prNode = g.addVertexWithLabel("problem_report");
            }
            if(prDao==null){
                Logger.getLogger(JaxbHelper.class.getName()).log(Level.SEVERE, "prDap is null");
            }
            else{
                Logger.getLogger(JaxbHelper.class.getName()).log(Level.SEVERE, "prDap is not null");
            }
            ProblemReport pr = prDao.findPrById(pr_id);
            prNode.setProperty("pr_id", pr.getPr_id());
            prNode.setProperty("description", pr.getDescription());
            prNode.setProperty("confidential", pr.getConfidential());
            prNode.setProperty("synopsis", pr.getSynopsis());
            prNode.setProperty("platform", pr.getPlatform());
            prNode.setProperty("product", pr.getProduct());
            prNode.setProperty("prClass", pr.getPrClass());
            prNode.setProperty("category", pr.getCategory());
            prNode.setProperty("functionalArea", pr.getFunctionalArea());
            prNode.setProperty("lastModified", pr.getLastModified());
            prNode.setProperty("problemLevel", pr.getProblemLevel());
            prNode.setProperty("keywords", pr.getKeywords());
            prNode.setProperty("fix", pr.getFix());
            prNode.setProperty("workaround", pr.getWorkaround());
            prNode.setProperty("submitterId", pr.getSubmitterId());
            prNode.setProperty("jtacCaseId", pr.getJtacCaseId());
            prNode.setProperty("externalId", pr.getExternalId());
            prNode.setProperty("legacyCustomer", pr.getLegacyCustomer());
            prNode.setProperty("environment", pr.getEnvironment());
            prNode.setProperty("releaseNote", pr.getReleaseNote());
            prNode.setProperty("externalDescription", pr.getExternalDescription());
            prNode.setProperty("externalTitle", pr.getExternalTitle());
            prNode.setProperty("supportNotes", pr.getSupportNotes());
            prNode.setProperty("resolvedIn", pr.getResolvedIn());
            prNode.setProperty("devOwnerBgBu", pr.getDevOwnerBgBu());
            prNode.setProperty("submitterBgBu", pr.getSubmitterBgBu());
            prNode.setProperty("state", pr.getState());
            prNode.setProperty("arrivalDate", pr.getArrivalDate());
            g.commit();
            
        }
        return prNode;
    }

  
}
