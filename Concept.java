package name.zanbry.cdc;

import java.util.ArrayList;
import java.util.List;

public class Concept {
	public int id;
	public Concept upperClass;
	public List<Concept> lowerClasses;
	
	public Concept(int id){
		this.id = id;
		upperClass = null;
		lowerClasses = new ArrayList<Concept>();
	}
}
