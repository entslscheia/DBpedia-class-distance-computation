package name.zanbry.cdc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.*;

import name.zanbry.db.DBAgent;

/**
 * 
 * @author guyu
 *
 */
public class Ontology {
	public Concept root;
	public Concept[] allConcepts = new Concept[458];
	public int[][] distance = new int[458][458];
	public int[][] LCS = new int[458][458];
	int size = 0;
	public DBAgent dbAgent = DBAgent.getInstance();
	public Ontology(){
		root = new Concept(dbAgent.getId("<http://www.w3.org/2002/07/owl#Thing>"));
		allConcepts[0] = root;
		size = 1;
		for(int i = 0; i < 458; i++)
			for(int j = 0; j < 458; j ++){
				distance[i][j] = 10000;
				LCS[i][j] = -1;
			}
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
	/**
	 * 
	 * @param id
	 * @return the reference of object in allConcepts whose id equals id
	 */
	public Concept returnById(int id){
		for(int i = 0; i < allConcepts.length; i ++){
			if(allConcepts[i] != null && allConcepts[i].id == id)
				return allConcepts[i];
		}
		return null;
	}
	/**
	 * 
	 * @param c
	 * @return the index of concept c in array allConcepts,, if the target doesn't exist then return -1
	 */
	public int getIndex(Concept c){
		for(int i = 0; i < size; i ++){
			if(allConcepts[i] == c)
				return i;
		}
		return -1;
	}
	/**
	 * 
	 * @param root
	 * @return return the depth of tree rooted root
	 */
	public int depthByDFS(Concept root){
		int result = 1;
		if(root.lowerClasses.size() == 0)
			return result;
		else{
			int max = 0;
			for(Concept cpt: root.lowerClasses){
				int d = depthByDFS(cpt);
				if(d > max)
					max = d;
			}
			result += max;
		}
		return result;
	}
	/**
	 * 
	 * @param root
	 * @return all the descendants of node root
	 */
	@Deprecated
	public List<Concept> acqDescendant(Concept root){
		List<Concept> result = new ArrayList<Concept>();
		if(root.lowerClasses.size() == 0)
			return root.descendants;
		result.addAll(root.lowerClasses);
		for(Concept c: root.lowerClasses){
			result.addAll(acqDescendant(c));
		}
		return result;
	}
	/**
	 * set the data field descendant of all nodes in tree rooted root
	 * @param root
	 */
	@Deprecated
	public void setDescendant(Concept root){
		//if(root.lowerClasses.size() == 0)
		//	return;
		root.descendants = acqDescendant(root);
		for(Concept c: root.lowerClasses){
			setDescendant(c);
		}
	}
	/**
	 * set the data field descendant of all nodes in tree rooted root
	 * @param root
	 */
	public void Descendant(Concept root){
		for(Concept c: root.lowerClasses){
			Descendant(c);
			root.descendants.add(c);
			root.descendants.addAll(c.descendants);
		}
	}
	/**
	 * calculate the distance of every pairs of nodes by dfs
	 * @param root
	 */
	public void calculateDisByDFS(Concept root){
		int index0 = getIndex(root);
		distance[index0][index0] = 0;
		
		for(Concept c: root.lowerClasses){
			calculateDisByDFS(c);
			int indexc = getIndex(c);
			distance[index0][indexc] = 1;
			distance[indexc][index0] = 1;
			List<Concept> des = c.descendants;
			for(Concept d: des){
				int indexd = getIndex(d);
				distance[index0][indexd] = distance[indexc][indexd] + 1;
				distance[indexd][index0] = distance[index0][indexd];
			}
		}
		
		for(int i = 0; i < root.descendants.size(); i ++)
			for(int j = i + 1; j < root.descendants.size(); j ++){
				int i0 = getIndex(root.descendants.get(i));
				int j0 = getIndex(root.descendants.get(j));
				if(distance[i0][j0] == 10000){
					distance[i0][j0] = distance[i0][index0] + distance[j0][index0];
					distance[j0][i0] = distance[i0][j0];
						
				}
			}
		
	}
	public void calculateLCSByDFS(Concept root){
		int index0 = getIndex(root);
		LCS[index0][index0] = root.id;
		
		for(Concept c: root.lowerClasses){
			calculateLCSByDFS(c);
			int indexc = getIndex(c);
			LCS[index0][indexc] = LCS[indexc][index0] = root.id;
			for(Concept d: c.descendants){
				int indexd = getIndex(d);
				LCS[index0][indexd] = LCS[indexd][index0] = root.id;
			}
		}
		for(int i = 0; i < root.descendants.size(); i ++)
			for(int j = i + 1; j < root.descendants.size(); j ++){
				int i0 = getIndex(root.descendants.get(i));
				int j0 = getIndex(root.descendants.get(j));
				if(LCS[i0][j0] == -1){
					LCS[j0][i0] = LCS[i0][j0] = root.id;
						
				}
			}
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
		otlg.Descendant(otlg.root);
		otlg.calculateLCSByDFS(otlg.root);
		for(int i = 0; i < 8; i ++)
			for(int j = i + 1 ; j < 8; j ++){
				System.out.print(otlg.dbAgent.getUri(otlg.allConcepts[i].id) + " ");
				System.out.print(otlg.dbAgent.getUri(otlg.allConcepts[j].id) + " ");
				System.out.println("  " + otlg.dbAgent.getUri(otlg.LCS[j][i]) + "  " + otlg.dbAgent.getUri(otlg.LCS[i][j]));
			}
	}
}
