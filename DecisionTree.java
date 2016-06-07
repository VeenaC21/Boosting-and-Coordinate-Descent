import java.util.ArrayList;

public class DecisionTree {

	public Node root;
	public int depth;
	public int size;

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public DecisionTree(Node root) {
		super();
		this.root = root;
	}

	public DecisionTree() {
		size = 0;
	}

	public void bfs(Node root) {
		// Since queue is a interface
		GenQueue<Node> queue = new GenQueue<Node>();

		if (root == null)
			return;

		root.visited = true;
		// Adds to end of queue
		queue.enqueue(root);

		while (queue.hasItems()) {
			// removes from front of queue
			Node r = queue.dequeue();
			if (r.yesOrNo == 1 || r.yesOrNo == 0) {
				System.out.print(r.yesOrNo+ "\t");
			} else
				System.out.print(r.attributeNo + " ( " + r.parentCharacteristic + " )" + "\t");

			size++;
			// Visit child first before grandchild
			for (Node n : r.children) {
				if (!n.visited) {
					queue.enqueue(n);
					n.visited = true;
				}
			}
			System.out.println();
		}

		reset();

	}
	
	

	public void reset() {
		GenQueue<Node> queue = new GenQueue<Node>();

		if (root == null)
			return;

		root.visited = false;
		// Adds to end of queue
		queue.enqueue(root);

		while (queue.hasItems()) {
			// removes from front of queue
			Node r = queue.dequeue();
			for (Node n : r.children) {
				if (n.visited) {
					queue.enqueue(n);
					;
					n.visited = false;
				}
			}
		}

	}

	public int recHeight(Node node) {

		if (node == null) {
			return 0;
		} else {

			int max = 0;
			ArrayList<Integer> deps = new ArrayList<Integer>();
			if (node.children.size() != 0) {

				for (Node child : node.children) {

					int h = recHeight(child);
					deps.add(h);
				}

			}
			for (int i = 0; i <deps.size(); i++) {
				if (deps.get(i) > max)
					max = deps.get(i);
			}
			return max + 1;
		}
	}

}
