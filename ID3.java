// ECS629/759 Assignment 2 - ID3 Skeleton Code
// Author: Simon Dixon

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

class ID3 {

	/** Each node of the tree contains either the attribute number (for non-leaf
	 *  nodes) or class number (for leaf nodes) in <b>value</b>, and an array of
	 *  tree nodes in <b>children</b> containing each of the children of the
	 *  node (for non-leaf nodes).
	 *  The attribute number corresponds to the column number in the training
	 *  and test files. The children are ordered in the same order as the
	 *  Strings in strings[][]. E.g., if value == 3, then the array of
	 *  children correspond to the branches for attribute 3 (named data[0][3]):
	 *      children[0] is the branch for attribute 3 == strings[3][0]
	 *      children[1] is the branch for attribute 3 == strings[3][1]
	 *      children[2] is the branch for attribute 3 == strings[3][2]
	 *      etc.
	 *  The class number (leaf nodes) also corresponds to the order of classes
	 *  in strings[][]. For example, a leaf with value == 3 corresponds
	 *  to the class label strings[attributes-1][3].
	 **/
	class TreeNode {

		TreeNode[] children;
		int value;

		public TreeNode(TreeNode[] ch, int val) {
			value = val;
			children = ch;
		} // constructor

		public String toString() {
			return toString("");
		} // toString()

		String toString(String indent) {
			if (children != null) {
				String s = "";
				for (int i = 0; i < children.length; i++)
					s += indent + data[0][value] + "=" +
							strings[value][i] + "\n" +
							children[i].toString(indent + '\t');
				return s;
			} else
				return indent + "Class: " + strings[attributes-1][value] + "\n";
		} // toString(String)

	} // inner class TreeNode






	private int attributes; 	// Number of attributes (including the class)
	private int examples;		// Number of training examples
	private TreeNode decisionTree;	// Tree learnt in training, used for classifying
	private String[][] data;	// Training data indexed by example, attribute
	private String[][] strings; // Unique strings for each attribute
	private int[] stringCount;  // Number of unique strings for each attribute

	private String[] classOptionsFinal;

	public ID3() {
		attributes = 0;
		examples = 0;
		decisionTree = null;
		data = null;
		strings = null;
		stringCount = null;
	} // constructor

	public void printTree() {
		if (decisionTree == null)
			error("Attempted to print null Tree");
		else
			System.out.println(decisionTree);
	} // printTree()

	/** Print error message and exit. **/
	static void error(String msg) {
		System.err.println("Error: " + msg);
		System.exit(1);
	} // error()

	static final double LOG2 = Math.log(2.0);

	static double xlogx(double x) {
		return x == 0? 0: x * Math.log(x) / LOG2;
	} // xlogx()

	static double entropy(int...values) {
		double entropy = 0;
		int numberOfOptions = 0;
		for (int i = 0; i < values.length; i++) {
			numberOfOptions += values[i];
		}
		// print(numberOfOptions + "numberOfOptions");
		for (double value: values) {
			entropy += -xlogx(value/numberOfOptions);
		}
		return entropy;
	}

	private double calcEntropy(String[][] subset) {
		//calculate class entropy
		int classIndex = attributes-1;
		//Fix this global BS array
		String[] classOptions = new String[stringCount[stringCount.length-1]];
		int[] countOptions = new int[stringCount[stringCount.length-1]];
		for (int i = 0; i<countOptions.length; i++) {
			countOptions[i] = 0;
			classOptions[i] = strings[strings.length-1][i];
		}

		for (int i = 0; i < subset.length; i++) {
			//Every option in the class
			for (int j = 0; j < classOptions.length; j++) {
				//Check if that option equals the other options
				if (classOptions[j].equals(subset[i][classIndex])) {
					countOptions[j]++;
				}
			}
		}

		if (classOptionsFinal == null) {
			classOptionsFinal = new String[classOptions.length];
			for (int i = 0; i<classOptions.length; i++) {
				classOptionsFinal[i] = classOptions[i];
			}
		}

		double classEntropy = entropy(countOptions);
		return classEntropy;
	}

	// private double infoGain(int attributeNum, String[][] trainingData) {
	// 	//calculate class entropy
	// 	double[] entropies = new double[stringCount[attributeNum]];
	// 	for (int i = 0; i < entropies.length; i++) {
	// 		//for each option
	//
	//
	// 		int count = 0;
	// 		for (int j = 0; j < trainingData.length; j++) {
	//
	// 		}
	// 	}
	// 	return 0;
	// }

	/** Execute the decision tree on the given examples in testData, and print
	 *  the resulting class names, one to a line, for each example in testData.
	 **/
	public void classify(String[][] testData) {
		if (decisionTree == null)
			error("Please run training phase before classification");
		// PUT  YOUR CODE HERE FOR CLASSIFICATION
		for (int i = 0; i < testData.length; i++) {
			print(classifying(testData[i], decisionTree));
		}
	} // classify()

	public String classifying(String[] testData, TreeNode tree) {
		int value = tree.value;
		if (tree.children == null) {
			//Terminating case, if no children take value as index for class
			return strings[attributes-1][value];
		}
		for (int i = 0; i < stringCount[value]; i++) {
			if (testData[value].equals(strings[value][i])) {
				//If class matches value in tree go down that tree
				return classifying(testData, tree.children[i]);
			}
		}
		//Exit case
		return "";
	}

	public void train(String[][] trainingData) {
		indexStrings(trainingData);
		// PUT  YOUR CODE HERE FOR TRAINING
		decisionTree = training(trainingData);
		//NOTE: If entropy is 0 data is perfectly classified
	} // train()

	public int timesOptionOccurs(String option, int attributeCol, String[][] trainingData) {
		int timesOccurred = 0;
		for (int i = 0; i < trainingData.length; i++) {
			if (option.equals(trainingData[i][attributeCol])) {
				timesOccurred++;
			}
		}
		return timesOccurred;
	}

	public String[][] subset(String option, int attributeCol, String[][] trainingData) {
		int timesOccurred = 0;
		for (int i = 0; i < trainingData.length; i++) {
			if (option.equals(trainingData[i][attributeCol])) {
				timesOccurred++;
			}
		}
		// System.out.println(timesOccurred + " timesOccurred " + option);
		// System.out.println(attributeCol + " attributeCol");
		String[][] subsetOfTrainingData = new String[timesOccurred][trainingData[0].length];
		int subsetCounter = 0;
		for (int i = 0; i < trainingData.length; i++) {
			if (option.equals(trainingData[i][attributeCol])) {
				for (int k =0; k < trainingData[i].length; k++) {
					subsetOfTrainingData[subsetCounter][k] = trainingData[i][k];
				}
				// subsetOfTrainingData[subsetCounter] = trainingData[i];
				// for (int j = 0; j < subsetOfTrainingData[subsetCounter].length; j++) {
				// 	System.out.print(subsetOfTrainingData[subsetCounter][j]+" ");
				// }
				// System.out.println();
				subsetCounter++;
			}
		}
		return subsetOfTrainingData;
	}

	public <T> void printArray(T[] array) {
		for (T t: array) {
			System.out.print(t + " ");
		}
		System.out.println();
	}

	public static void print(String s) {
		System.out.println(s);
	}

	public TreeNode training(String[][] trainingData) {
		TreeNode t = null;
		//Calculate Entropy of the trainingData
		Double targetEntropy = calcEntropy(trainingData);
		// System.out.println(targetEntropy);
		if (targetEntropy == 0) {
			//Perfectly Classified
			for (int i = 0; i < stringCount[attributes-1]; i++) {
				if (trainingData[0][attributes-1].equals(strings[attributes-1][i])) {
					//make children array null of tree node and save the index of the class
					t = new TreeNode(null, i);
					return t;
				}
			}
		}

		//calc info gain
		double maxGain = 0;
		int bestAttributeIndex = 0;
		for (int i = 0; i < trainingData[0].length-1; i++) {
			//Loop for every attribute
			double entropyGivenAttribute = 0;
			for (int j = 0; j < stringCount[i]; j++) {
				//Loop for the column of that attribute
				//Split the data so that for each attribute you get an entropy value for each option
				String[][] subsetOfTrainingData = subset(strings[i][j], i, trainingData);
				// String[][] subsetOfTrainingData = subset(strings[i][j], i, trainingData);
				double tempEntropy = calcEntropy(subsetOfTrainingData);
				int timesOccurred = timesOptionOccurs(strings[i][j], i, trainingData);
				entropyGivenAttribute += (tempEntropy * ((double)timesOccurred/trainingData.length));
				// System.out.println(tempEntropy + " " + timesOccurred + " / " + trainingData.length);
				// System.out.println("tempEntropy: " + tempEntropy);
				// System.out.println("Times that option appeared:" + timesOccurred + "total: " + trainingData.length);
				// System.out.println(entropyGivenAttribute + "-------");
			}
			//Subtract entropy with target entropy to get the infogain
			double infoGain = targetEntropy - entropyGivenAttribute;
			if (maxGain < infoGain) {
				//Set highest info gain and its corresponding aatribute column
				maxGain = infoGain;
				bestAttributeIndex = i;
			}
		}

		// print("Best attribute is: " + data[0][bestAttributeIndex]);

		//create tree node for highest
		t = new TreeNode(new TreeNode[stringCount[bestAttributeIndex]], bestAttributeIndex);
		for (int i = 0; i < stringCount[bestAttributeIndex]; i++) {
			//For each attribute subset the data and pass it down
			String[][] passingSubset = subset(strings[bestAttributeIndex][i], bestAttributeIndex, trainingData);
			t.children[i] = training(passingSubset);
		}
		return t;
		//loop through all options in the attributes
	}

	/** Given a 2-dimensional array containing the training data, numbers each
	 *  unique value that each attribute has, and stores these Strings in
	 *  instance variables; for example, for attribute 2, its first value
	 *  would be stored in strings[2][0], its second value in strings[2][1],
	 *  and so on; and the number of different values in stringCount[2].
	 **/
	void indexStrings(String[][] inputData) {
		data = inputData;
		examples = data.length;
		attributes = data[0].length;
		stringCount = new int[attributes];
		strings = new String[attributes][examples];// might not need all columns
		int index = 0;
		for (int attr = 0; attr < attributes; attr++) {
			stringCount[attr] = 0;
			for (int ex = 1; ex < examples; ex++) {
				for (index = 0; index < stringCount[attr]; index++)
					if (data[ex][attr].equals(strings[attr][index]))
						break;	// we've seen this String before
				if (index == stringCount[attr])		// if new String found
					strings[attr][stringCount[attr]++] = data[ex][attr];
			} // for each example
		} // for each attribute
	} // indexStrings()

	/** For debugging: prints the list of attribute values for each attribute
	 *  and their index values.
	 **/
	void printStrings() {
		for (int attr = 0; attr < attributes; attr++)
			for (int index = 0; index < stringCount[attr]; index++)
				System.out.println(data[0][attr] + " value " + index +
									" = " + strings[attr][index]);
	} // printStrings()

	/** Reads a text file containing a fixed number of comma-separated values
	 *  on each line, and returns a two dimensional array of these values,
	 *  indexed by line number and position in line.
	 **/
	static String[][] parseCSV(String fileName)
								throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String s = br.readLine();
		int fields = 1;
		int index = 0;
		while ((index = s.indexOf(',', index) + 1) > 0)
			fields++;
		int lines = 1;
		while (br.readLine() != null)
			lines++;
		br.close();
		String[][] data = new String[lines][fields];
		Scanner sc = new Scanner(new File(fileName));
		sc.useDelimiter("[,\n]");
		for (int l = 0; l < lines; l++)
			for (int f = 0; f < fields; f++)
				if (sc.hasNext())
					data[l][f] = sc.next();
				else
					error("Scan error in " + fileName + " at " + l + ":" + f);
		sc.close();
		return data;
	} // parseCSV()

	public static void main(String[] args) throws FileNotFoundException,
												  IOException {
		if (args.length != 2)
			error("Expected 2 arguments: file names of training and test data");
		String[][] trainingData = parseCSV(args[0]);
		String[][] testData = parseCSV(args[1]);
		ID3 classifier = new ID3();
		classifier.train(trainingData);
		classifier.printTree();
		classifier.classify(testData);
	} // main()

} // class ID3
