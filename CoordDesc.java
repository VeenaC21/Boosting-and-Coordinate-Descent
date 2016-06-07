import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CoordDesc{

	static int[][] input = new int[80][23];
	static ArrayList<DecisionTree> trees = new ArrayList<DecisionTree>();
	static double[] alphas = new double[88];
	static double[] alpcheck = new double[88];
	static int[] check = new int[88];

	static int[] finaloutput = new int[187];

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

		generateDT1();
		System.out.println(trees.size());
		coordDescent();
		br.close();

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

			for (int m = 1; m <= 87; m++) {
				alp = alphas[m];
				hm = (getOutput(trees.get(m).root, acctest, i) == 0) ? -1 : 1;
				result += alp * hm;
			}

			if (result > 0) {
				finaloutput[i] = 1;
			} else {
				finaloutput[i] = 0;
			}
			result = 0;
		}
		for (int i = 0; i <= rows - 1; i++) {
			if (acctest[i][0] == finaloutput[i]) {
				finalaccuracy++;
			}
		}

		System.out.println((double) finalaccuracy / rows);

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

	public static int getLabel(int parent, int characteristic) {

		ArrayList<Integer> records = new ArrayList<Integer>();
		for (int i = 0; i <= 79; i++) {

			if (input[i][parent] == characteristic) {
				records.add(i);
			}
		}

		int countn = 0;
		int counta = 0;
		for (int i : records) {
			if (input[i][0] == 0)
				countn++;
			else
				counta++;
		}

		if (countn > counta) {
			return 0;
		} else
			return 1;

	}

	public static void coordDescent() {

		DecisionTree ht;
		double num;
		double den;
		double innerSum = 0;
		double loss = 0;
		int alp;
		double alphat;
		int count = 0;
		int count2=0;

		int some = 0;
		Random random = new Random();
		first: while (true) {
			for(int i=0;i<=87;i++){
				alpcheck[i] = alphas[i];
			}
			count++;
			for (alp = 0; alp <= 87; alp++) {
				
				
				alphat = alphas[alp];
				ht = trees.get(alp);
				num = 0.0;
				den = 0.0;

				for (int i = 0; i <= 79; i++) {

					int yi = (input[i][0] == 0 ? -1 : 1);
					// int yi = input[i][0];
					for (int j = 0; j <= 87; j++) {
						if (j == alp)
							continue;
						else {
							innerSum += alphas[j]
									* (getOutput(trees.get(j).root, input, i) == 0 ? -1
											: 1);
						}
					}

					if (traverse(ht.root, input, i)) {
						num += Math.exp(-yi * innerSum);
					} else {
						den += Math.exp(-yi * innerSum);
					}

					innerSum = 0;

				}

				alphas[alp] = 0.5 * (Math.log(num / den));
				//System.out.println(alphas[alp]);
				loss = calcLoss();
				//System.out.println(loss);
				if (alphat - alphas[alp] == 0.0) {
					//System.out.println(count);
				}
			}
			
			if(Arrays.equals(alpcheck, alphas)){
				if(count==1)
					continue;
			//	System.out.println(count);
				break;
			}
			
			for(int i=0;i<=87;i++){
				if(alpcheck[i] == alphas[i]){
					
					count2++;
				}
			}
			//System.out.println(count2);
			count2=0;
			//System.out.println(loss);

		}

		System.out.println("over "+count2+" loss: "+loss);
		/*
		 * for (int a = 0; a <= 87; a++) { System.out.println(alphas[a]); }
		 */

	}

	public static double calcLoss() {

		double sum = 0.0;
		double innerSum = 0;
		int yi;
		for (int i = 0; i <= 79; i++) {

			 yi = (input[i][0] == 0 ? -1 : 1);

			for (int j = 0; j <= 87; j++) {
				innerSum += alphas[j]
						* (getOutput(trees.get(j).root, input, i) == 0 ? -1 : 1);
			}
			sum += Math.exp(-yi * innerSum);
			innerSum = 0;

		}

		return sum;
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
