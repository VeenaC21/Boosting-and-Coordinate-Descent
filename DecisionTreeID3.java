import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class DecisionTreeID3 {
	
	static int[][] input = new int[80][23];
	static HashMap<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
	static int[] check = new int[22];
	static DecisionTree tree = new DecisionTree();
	
    static double accCount;//accuracy count

	public static void main(String[] args) throws NumberFormatException, IOException {
		
		map.put(1, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(2, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(3, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(4, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(5, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(6, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(7, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(8, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(9, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(10, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(11, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(12, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(13, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(14,
				new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(15,
				new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(16, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(17, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(18, new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(19,
				new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(20,
				new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(21,
				new ArrayList<Integer>(Arrays.asList(0,1)));
		map.put(22,
				new ArrayList<Integer>(Arrays.asList(0,1)));

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
		br.close();
		ArrayList<Integer> records = new ArrayList<Integer>();
		ArrayList<Integer> attributes = new ArrayList<Integer>();
		for (int i = 0; i <= count - 1; i++) {
			records.add(i);
		}
		for (int i = 1; i <= 22; i++) {
			attributes.add(i);
		}

		Node root = createDecisionTree(records, attributes, -5, -1);
		tree.setRoot(root);
		tree.bfs(root);
		int height=tree.recHeight(root);
		for (int i = 0; i <= count-1; i++) {
			traverse(root,input, i);
		}
		System.out.println(height+" "+accCount/count);
		
		accCount=0.0;
		int counttest=0;
		br = new BufferedReader(new FileReader(
				"/Users/veenac/Downloads/heart_test.data"));
		line = null;
		colcount = 0;
		int[][] test=new int[187][23];
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
		System.out.println("count: "+count+" counttest: "+counttest);
		for (int i = 0; i <= counttest-1; i++) {
			traverse(root,test, i);
		}
		
		System.out.println(accCount/counttest);
	}

	public static Node createDecisionTree(ArrayList<Integer> records,
			ArrayList<Integer> attributes, int attToDivide, int branchValue) {
		// create root node
		Node root = new Node();
		root.attributeNo = attToDivide;
		root.parentCharacteristic = branchValue;
		int count1 = 0;
		// check for all edibles or all poisonous
		for (int record : records) {
			if (input[record][0] == 1)
				count1++;
		}
		int def;// default label
		if (count1 > records.size() / 2) {
			def = 1;
		} else
			def = 0;

		if (count1 == records.size()) {
			root.yesOrNo = 1;
			return root;
		} else if (count1 == 0) {
			root.yesOrNo = 0;
			return root;
		}

		if (attributes.size() == 0) {
			root.yesOrNo = def;
			return root;
		} else {
			int bestAttribute = chooseAttribute(records, attributes,
					attToDivide);
			root.attributeNo = bestAttribute;
			root.parentCharacteristic = branchValue;
			for (int value : map.get(bestAttribute)) {
				// create a subset record
				ArrayList<Integer> nwRecords = new ArrayList<Integer>();
				for (int i : records) {
					if (input[i][bestAttribute] == value)
						nwRecords.add(i);
				}

				if (nwRecords.size() == 0) {
					Node leafNode = new Node();
					leafNode.parentCharacteristic = value;
					leafNode.attributeNo = bestAttribute;// no attribute since its a leaf
												// node
					leafNode.yesOrNo = def;
					root.children.add(leafNode);
				} else {
					attributes.remove(new Integer(bestAttribute));
					// System.out.println(bestAttribute);
					Node subNode = createDecisionTree(nwRecords, attributes,
							bestAttribute, value);
					root.yesOrNo=3;
					root.children.add(subNode);
				}

			}

		}
		return root;

	}

	public static int chooseAttribute(ArrayList<Integer> records,
			ArrayList<Integer> attributes, int attToDivide) {

		double entropy = calcEntropy(records);
		double subsetEntropy = 0;
		double gain = Double.NEGATIVE_INFINITY;
		int bestAttribute = -1;
		for (int attribute : attributes) {
			subsetEntropy = calcGain(records, attToDivide, attribute);
			double indGain = entropy - subsetEntropy;
			//System.out.println(indGain+" "+gain);
			if (indGain >= gain) {
				gain = indGain;
				bestAttribute = attribute;
			}
		}

		//System.out.println(bestAttribute);
		return bestAttribute;

	}

	public static double calcEntropy(ArrayList<Integer> records) {
		int count = records.size();
		double count1 = 0;
		for (int i : records) {

			if (input[i][0] == 1)
				count1++;
		}

		double count0 = count - count1;

		double part1 = 0;
		double part2 = 0;
		if (count1 != 0) {
			part1 = -((count1 / count) * Math.log10(count1 / count));
		}

		if (count0 != 0) {
			part2 = -((count0 / count) * Math.log10(count0 / count));
		}
		double entropy = part1 + part2;
		//System.out.println("1: "+entropy);
		return entropy;
	}

	public static double calcGain(ArrayList<Integer> records, int attToDivide, int otherAttribute) {

		double count = records.size();

		ArrayList<Integer> varieties = map.get(otherAttribute);
		double sum = 0;

		for (int v : varieties) {
			// System.out.print(v);
			double countV = 0;
			double count1 = 0;
			for (int record : records) {

				if (input[record][otherAttribute] == v) {
					// System.out.print(" "+entry.getKey());
					countV++;
					if (input[record][0] == 1)
						count1++;
				}
			}
			double count0 = countV - count1;
			double part1 = 0;
			double part2 = 0;
			if (countV == 0 || count == 0)
				continue;
			if (count1 != 0) {
				part1 = -((count1 / countV) * Math.log10(count1 / countV));
			}

			if (count0 != 0) {
				part2 = -((count0 / countV) * Math.log10(count0 / countV));
			}
			// System.out.println("checking: "+countE+" p: "+countP+" v: "+countV);
			sum += (countV / count) * (part1 + part2);

		}
		 //System.out.println("2: "+sum);
		return sum;
	}

	public static void traverse(Node root,int[][] example, int row) {

	//	System.out.println(root.yesOrNo);
		if (root.yesOrNo != 3) {

			if (root.yesOrNo == 1) {
				if (example[row][0] == 1)
					accCount++;

			} else if (root.yesOrNo == 0) {
				if (example[row][0] == 0)
					accCount++;

			} else {
				System.out.println("wrong");
			}

		} else {
			int givenatt = example[row][root.attributeNo];
			
			for (Node n : root.children) {
				if (n.parentCharacteristic == givenatt) {
				//	System.out.println("att: "+root.attributeNo+" given: "+givenatt);
					traverse(n,example, row);
				}
			}
		}

	}
}
