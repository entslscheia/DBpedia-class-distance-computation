package name.zanbry.cdc;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

import name.zanbry.db.DBAgent;

public class Ontology {
	public Concept root;
	public Concept[] allConcepts = new Concept[458];
	int size = 0;
	public DBAgent dbAgent = DBAgent.getInstance();
	public Ontology(){
		root = new Concept(dbAgent.getId("<http://www.w3.org/2002/07/owl#Thing>"));
		allConcepts[0] = root;
		size = 1;
		
	}
	public void constructByTuple(int parentId, int childId){
		Concept parent = returnById(parentId);
		if(parent == null){
			parent = new Concept(parentId);
			allConcepts[size] = parent;
			size ++;
		}
		
		Concept child = returnById(childId);
		if(child == null){
			child = new Concept(childId);
			allConcepts[size] = child;
			size ++;
		}
		
		parent.lowerClasses.add(child);
		child.upperClass = parent;
	}
	public Concept returnById(int id){
		for(int i = 0; i < allConcepts.length; i ++){
			if(allConcepts[i] != null && allConcepts[i].id == id)
				return allConcepts[i];
		}
		return null;
	}
	public int countByDFS(Concept root){
		int result = 1;
		if(root.lowerClasses.size() == 0)
			return result;
		else{
			for(Concept cpt: root.lowerClasses){
				result += countByDFS(cpt);
			}
			return result;
		}
	}
	/**
	 * 
	 * @param id0
	 * @param id1
	 * @return whether id0 is ancestor of id1 or not
	 */
	public boolean isAncestors(int id0, int id1){
		Concept c0 = returnById(id0);
		Concept c1 = returnById(id1);
		
		for(Concept c = c1; c != null; c = c.upperClass){
			if(c == c0){
				return true;
			}
				
		}
		return false;
	}
	@Deprecated
	public String LCS(String uri0, String uri1){
		int id0 = dbAgent.getId(uri0), id1 = dbAgent.getId(uri1);
		Concept c0 = returnById(id0), c1 = returnById(id1);
		String result = null;
		for(Concept c = c0; c != null; c = c.upperClass){
			if(isAncestors(c.id, id1)){
				result = dbAgent.getUri(c.id);
				break;
			}
				
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		Ontology otlg = new Ontology();
		File file = new File("data.txt");
		BufferedReader reader = null; 
		try {
	        reader = new BufferedReader(new FileReader(file));
	        String tempString = null;
	        while ((tempString = reader.readLine()) != null) {
	        	String tuple[] = tempString.split("____");
	        	int childId = otlg.dbAgent.getId(tuple[0]);
	        	int parentId = otlg.dbAgent.getId(tuple[2]);
	        	otlg.constructByTuple(parentId, childId);
	        }
	        reader.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        if (reader != null) {
	            try {
	                reader.close();
	            } catch (IOException e1) {
	            }
	        }
	    }
		
	}
}
