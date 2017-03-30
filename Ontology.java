package name.zanbry.cdc;

import java.util.ArrayList;
import java.util.List;

public class Ontology {
	public Concept root;
	public List<Concept> allConcepts;
	int startId = 0, endId = 100;
	public Ontology(){
		allConcepts = new ArrayList<Concept>();
		for(int i = startId; i <= endId; i ++){
			Concept con = new Concept(i);
			allConcepts.add(con);
		}
		root = allConcepts.get(0);
	}
	public void link(int parentId, int childId){
		Concept parent = allConcepts.get(parentId - startId);
		Concept child = allConcepts.get(childId - startId);
		parent.lowerClasses.add(child);
		child.upperClass = parent;
	}
}
