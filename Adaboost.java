import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Adaboost2 {
	static int[][] input = new int[80][23];
	static ArrayList<DecisionTree> trees = new ArrayList<DecisionTree>();
	static double[] weights = new double[80];

	static HashMap<Integer, DecisionTree> map = new HashMap<Integer, DecisionTree>();
	static int[] finaloutput = new int[187];
	static HashMap<Integer, Double> alphalist = new HashMap<Integer, Double>();

	static int M = 5;

	public static void main(String[] args) throws NumberFormatException,
			IOException {
		int count = 0;
		BufferedReader br = new BufferedReader(new FileReader(
				"/Users/veenac/Downloads/heart_train.data"));
		String line = null;
		int colcount = 0;
		while ((line = br.readLine()) != null) {
			String[] values = line.split(",");
			for (String str : values) {
				input[count][colcount] = Integer.parseInt(str);
				colcount++;
			}
			colcount = 0;
			count++;
		}
		System.out.println(count);

		for (int i = 0; i <= 79; i++) {
			weights[i] = 1 / 80.0;
		}
		
		
		generateDT2();
		System.out.println(trees.size());
		br.close();

		boosting();

		
		 //int count2=0; for(DecisionTree t: trees){ t.bfs(t.root); count2++;
		 //if(count2>16){ break; } }
		 

		int counttest = 0;
		br = new BufferedReader(new FileReader(
				"/Users/veenac/Downloads/heart_test.data"));
		line = null;
		colcount = 0;
		int[][] test = new int[187][23];
		while ((line = br.readLine()) != null) {
			String[] values = line.split(",");
			for (String str : values) {
				test[counttest][colcount] = Integer.parseInt(str);
				colcount++;
			}
			colcount = 0;
			counttest++;
		}
		br.close();
		double alp;
		int hm;
		double result = 0;
		int finalaccuracy = 0;
		
		int[][] acctest = test;
		int rows=187;
		first:for (int i = 0; i <= rows-1; i++) {

			for (int m = 1; m <= M; m++) {
				alp = alphalist.get(m);
				map.get(m).bfs(map.get(m).root);
				if(m==3){
					break first;
				}
				hm = (getOutput(map.get(m).root, acctest, i) == 0) ? -1 : 1;
				result += alp * hm;
			}
			if (result > 0) {
				finaloutput[i] = 1;
			} else {
				finaloutput[i] = 0;
			}
			result = 0;
		}
		for (int i = 0; i <= rows-1; i++) {
			//System.out.println(finaloutput[i]);
			if (acctest[i][0] == finaloutput[i]) {
				finalaccuracy++;
			}
		}

		System.out.println(finalaccuracy);

		
		/*
		int countind=0;
		double total=0.0;
		for(DecisionTree t: trees){
			for (int i = 0; i <= rows-1; i++) {
				if(acctest[i][0]==getOutput(t.root, acctest, i)){
					countind++;
				}
			}
		//	System.out.println(t.depth+" "+countind/80.0);
			total+=(double)countind/rows;
			countind=0;
		}
		
		
		System.out.println((double)total/trees.size());*/
		
	}

	public static void generateDT2() {
		String bin=null;
		Node root;
		DecisionTree tree;
		Node child1;
		Node child2;
		for (int r = 1; r <= 22; r++) {
			for (int c1 = 1; c1 <= 22; c1++) {
				if (c1 != r) {

					for (int c2 = 1; c2 <= 22; c2++) {
						if (c2 != r) {

						// for (int l = 0; l <= 15; l++) {

//							bin = Integer.toBinaryString(l);
							child1 = new Node(c1);
							child1.yesOrNo = 3;
							child1.parentCharacteristic = 0;

							child2 = new Node(c2);
							child2.yesOrNo = 3;
							child2.parentCharacteristic = 1;

							Node child1leaf0 = new Node(100);
							child1leaf0.parentCharacteristic = 0;
							Node child1leaf1 = new Node(100);
							child1leaf1.parentCharacteristic = 1;
							Node child2leaf0 = new Node(100);
							child2leaf0.parentCharacteristic = 0;
							Node child2leaf1 = new Node(100);
							child2leaf1.parentCharacteristic = 1;

							child1.children.add(child1leaf0);
							child1.children.add(child1leaf1);
							child2.children.add(child2leaf0);
							child2.children.add(child2leaf1);

							child1leaf0.yesOrNo = getLabel(r, 0, c1, 0);
							child1leaf1.yesOrNo = getLabel(r, 0, c1, 1);
							child2leaf0.yesOrNo = getLabel(r, 1, c2, 0);
							child2leaf1.yesOrNo = getLabel(r, 1, c2, 1);

							root = new Node(r);
							root.yesOrNo = 3;
							root.children.add(child1);
							root.children.add(child2);
							tree = new DecisionTree(root);
							// tree.bfs(tree.root);
							trees.add(tree);
						//	 }

						}
					}
				}
			}
		}
	}

	public static int getLabel(int root, int rootchar, int parent,
			int characteristic) {

		ArrayList<Integer> records = new ArrayList<Integer>();
		for (int i = 0; i <= 79; i++) {
			if (input[i][root] == rootchar) {
				if (input[i][parent] == characteristic) {
					records.add(i);
				}
			}
		}

		double countn = 0;
		double counta = 0;
		for (int i : records) {
			// if (input[i][0] == 0) countn++; else counta++;
			 
			 if (input[i][0] == 0) 
				 countn+=weights[i];
			 else 
				 counta+=weights[i];
		}

		
		  if (countn > counta) { return 0; } else return 1;
		 

		
	}

	public static void boosting() {

		/*for (int i = 0; i <= 79; i++) {
			weights[i] = 1 / 80.0;
		}*/
		double error;
		double alpha;
		double indError;
		boolean crctornot;
		double power;
		double zm;
		DecisionTree selected;

		for (int m = 1; m <= M; m++) {
			error = Double.MAX_VALUE;

			selected = null;
			for (DecisionTree t : trees) {
				indError = 0;
				for (int r = 0; r <= 79; r++) {
					if (!traverse(t.root, input, r)) {
						indError += weights[r];
						// System.out.println(indError+" "+weights[r]);
					}
				}
				if (indError < error) {

					error = indError;
					selected = t;
				}

			}
			alpha = 0.5 * Math.log((1 - error) / error);
			zm = 2 * Math.sqrt(error * (1 - error));
			for (int i = 0; i <= 79; i++) {
				crctornot = traverse(selected.root, input, i);

				if (crctornot) {
					power = -1 * alpha;
				} else
					power = 1 * alpha;

				weights[i] = weights[i] * Math.exp(power) / zm;
			}
			selected.bfs(selected.root);
			alphalist.put(m, alpha);
			map.put(m, selected);
			System.out.println("error for the tree: " + error + " alpha: "
					+ alpha);
			
			
			for(DecisionTree t: trees){
				//System.out.println(t.root.attributeNo+" "+t.root.children.get(0).attributeNo);
				t.root.children.get(0).children.get(0).yesOrNo=getLabel(t.root.attributeNo, 0, t.root.children.get(0).attributeNo, 0);
				t.root.children.get(0).children.get(1).yesOrNo=getLabel(t.root.attributeNo, 0, t.root.children.get(0).attributeNo, 1);
				t.root.children.get(1).children.get(0).yesOrNo=getLabel(t.root.attributeNo, 1, t.root.children.get(1).attributeNo, 0);
				t.root.children.get(1).children.get(1).yesOrNo=getLabel(t.root.attributeNo, 1, t.root.children.get(1).attributeNo, 1);
			}
		
		}
		
		

	}

	public static boolean traverse(Node root, int[][] example, int row) {

		// System.out.println(root.yesOrNo);
		if (root.yesOrNo != 3) {

			if (root.yesOrNo == 1) {
				if (example[row][0] == 1) {
					// System.out.println("y");
					return true;

				} else
					return false;

			} else if (root.yesOrNo == 0) {
				if (example[row][0] == 0) {
					// System.out.println("yy");
					return true;
				} else
					return false;

			} else {
				return false;
			}

		} else {
			int givenatt = example[row][root.attributeNo];

			for (Node n : root.children) {
				if (n.parentCharacteristic == givenatt) {
					// System.out.println("att: "+root.attributeNo+" given: "+givenatt);
					boolean b = traverse(n, example, row);
					return b;
				}
			}
		}
		return false;

	}

	public static int getOutput(Node root, int[][] example, int row) {

		// System.out.println(root.yesOrNo);
		if (root.yesOrNo != 3) {

			return root.yesOrNo;

		} else {
			int givenatt = example[row][root.attributeNo];

			System.out.println(givenatt);
			for (Node n : root.children) {
				if (n.parentCharacteristic == givenatt) {
					// System.out.println("att: "+root.attributeNo+" given: "+givenatt);
					int b = getOutput(n, example, row);
					return b;
				}
			}
		}
		return -1;

	}


}
