import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Adaboost
 {
	static int[][] input = new int[80][23];
	static ArrayList<DecisionTree> trees = new ArrayList<DecisionTree>();
	static double[] weights = new double[80];

	static HashMap<Integer, DecisionTree> map = new HashMap<Integer, DecisionTree>();
	static int[] finaloutput = new int[187];
	static HashMap<Integer, Double> alphalist = new HashMap<Integer, Double>();

	static int M = 20;

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
		generateDT1();
		System.out.println(trees.size());
		br.close();

		boosting();

		// int count2=0; for(DecisionTree t: trees){ t.bfs(t.root); count2++;
		// if(count2>16){ break; } }

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
		int rows = 187;
		for (int i = 0; i <= rows - 1; i++) {

			for (int m = 1; m <= M; m++) {
				alp = alphalist.get(m);
				// System.out.println(getOutput(map.get(m).root, acctest, i));
				hm = (getOutput(map.get(m).root, acctest, i) == 0) ? -1 : 1;
				result += alp * hm;
			}

			// System.out.println(result);
			if (result > 0) {
				finaloutput[i] = 1;
			} else {
				finaloutput[i] = 0;
			}
			result = 0;
		}
		for (int i = 0; i <= rows - 1; i++) {
			// System.out.println(finaloutput[i]);
			if (acctest[i][0] == finaloutput[i]) {
				finalaccuracy++;
			}
		}

		System.out.println((double) finalaccuracy / rows);

		int countind = 0;
		double total = 0.0;
		for (DecisionTree t : trees) {
			for (int i = 0; i <= rows - 1; i++) {
				if (acctest[i][0] == getOutput(t.root, acctest, i)) {
					countind++;
				}
			}
			// System.out.println(t.depth+" "+countind/80.0);
			total += (double) countind / rows;
			countind = 0;
		}

		System.out.println((double) total / trees.size());

	}

	public static void generateDT1() {
		Node root;
		DecisionTree tree;
		Node leafchild0;
		Node leafchild1;
		String bin;
		for (int r = 1; r <= 22; r++) {

			for (int i = 0; i <= 3; i++) {

				bin = Integer.toBinaryString(i);
				if (bin.length() < 2)
					bin = "0" + bin;
				leafchild0 = new Node(100);
				leafchild0.parentCharacteristic = 0;
				leafchild0.yesOrNo = bin.charAt(0) - 48;
				leafchild1 = new Node(100);
				leafchild1.parentCharacteristic = 1;
				leafchild1.yesOrNo = bin.charAt(1) - 48;

				root = new Node(r);
				root.yesOrNo = 3;

				root.children.add(leafchild0);
				root.children.add(leafchild1);

				tree = new DecisionTree(root);
				// tree.bfs(tree.root);
				trees.add(tree);
			}

		}

	}

	public static void boosting() {
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
				if (indError <= error) {

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

		}

	}

	public static boolean traverse(Node root, int[][] example, int row) {
		if (root.yesOrNo != 3) {

			if (root.yesOrNo == 1) {
				if (example[row][0] == 1) {
					return true;

				} else
					return false;

			} else if (root.yesOrNo == 0) {
				if (example[row][0] == 0) {
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
