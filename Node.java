import java.util.ArrayList;


public class Node {
	
	   public int attributeNo;              // data item (key)
	   public int yesOrNo;           // data item
	   public ArrayList<Node> children= new ArrayList<Node>();         // this node's left child
	   public int parentCharacteristic;
	   public boolean visited;
	   public Node parent;
	   public Node(){
		   children= new ArrayList<Node>();
	   }
	public Node(int attributeNo, int yesOrNo, ArrayList<Node> children,
			char parentCharacteristic, boolean visited) {
		super();
		this.attributeNo = attributeNo;
		this.yesOrNo = yesOrNo;
		this.children = children;
		this.parentCharacteristic = parentCharacteristic;
		this.visited= visited;
	}
	public Node(int attributeNo){
		super();
		this.attributeNo=attributeNo;
	}
	public int getAttributeNo() {
		return attributeNo;
	}
	public void setAttributeNo(int attributeNo) {
		this.attributeNo = attributeNo;
	}
	public int getYesOrNo() {
		return yesOrNo;
	}
	public void setYesOrNo(int yesOrNo) {
		this.yesOrNo = yesOrNo;
	}
	public ArrayList<Node> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}
	public int getParentCharacteristic() {
		return parentCharacteristic;
	}
	public void setParentCharacteristic(char parentCharacteristic) {
		this.parentCharacteristic = parentCharacteristic;
	}
	  
	   
	   
}
