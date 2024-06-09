package devops;

import java.io.Serializable;
import java.util.List;

public class Practice implements Serializable{
	
	private String id;
	private String name;

	private List<String> tools;
	
	public Practice() { }
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getTools() {
		return tools;
	}
	public void setTool(List<String> tool) {
		this.tools = tool;
	}
	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Practice id=" + id + ", name=" + name + " With: \n");
		
		for(int i = 0; i < tools.size(); i++) {
			sb.append(tools.get(i) + "\n");
		}
		
		return sb.toString();
	}
	
	
	
	

}
