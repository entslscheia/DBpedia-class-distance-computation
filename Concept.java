package name.zanbry.cdc;

import java.util.ArrayList;
import java.util.List;

public class Concept {
	public int id;// the index of class in database
	public Concept upperClass;// concept is subclass of upperClass
	public List<Concept> lowerClasses;
	public List<Concept> descendants;
	public Concept(int id){
		this.id = id;
		upperClass = null;
		lowerClasses = new ArrayList<Concept>();
		descendants = new ArrayList<Concept>();
	}
}
