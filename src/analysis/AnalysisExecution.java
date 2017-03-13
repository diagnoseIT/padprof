package analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import csv.CSVImporter;

/**
 * This class is responsible for the analysis. It detects the different anti-patterns.
 * 
 * @author Alexander Bran
 *
 */
public class AnalysisExecution {

	private List<File> comparisonFilesXML = new ArrayList<>();
	private List<File> problemFiles = new ArrayList<>();
	private File hotspotFile;
	private ArrayList<CallTree> comparisonByThread = new ArrayList<>();
	private ArrayList<CallTree> comparisonAllThreads = new ArrayList<>();
	private ArrayList<String> importantMethods = new ArrayList<>();

	private ArrayList<MonitorUsage> comparisonMonitor = new ArrayList<>();
	private MonitorUsage problemMonitor;

	private CallTree problemByThread;
	private CallTree problemAllThreads;
	private Hotspots hotspot;

	private ArrayList<CSVImporter> comparisonMemCSV = new ArrayList<>();
	private ArrayList<CSVImporter> comparisonCpuCSV = new ArrayList<>();
	private CSVImporter problemMemCSV;
	private CSVImporter problemCpuCSV;

	public AnalysisExecution(File hotspotFile, List<File> problemFiles, List<File> comparisonFiles) {
		this.hotspotFile = hotspotFile;
		this.problemFiles = problemFiles;
		this.comparisonFilesXML = comparisonFiles;
		generateObjects();
	}

	/**
	 * reads all the xml files and makes objects from it, in order to do further
	 * analysis
	 */
	private void generateObjects() {

		hotspot = new Hotspots(hotspotFile.getAbsolutePath());

		for (File file : problemFiles) {
			if (file.getName().toLowerCase().contains("by")) {
				problemByThread = new CallTree(file.getAbsolutePath());
			} else if (file.getName().toLowerCase().contains("all")) {
				problemAllThreads = new CallTree(file.getAbsolutePath());
			} else if (file.getName().toLowerCase().contains("cpu")) {
				problemCpuCSV = new CSVImporter(file.getAbsolutePath());
			} else if (file.getName().toLowerCase().contains("memory")) {
				problemMemCSV = new CSVImporter(file.getAbsolutePath());
			} else if (file.getName().toLowerCase().contains("monitor")) {
				problemMonitor = new MonitorUsage(file.getAbsolutePath());
			} else {
				System.out.println("Cannot assign file: " + file.getName());
			}
		}

		for (File file : comparisonFilesXML) {
			if (file.getName().toLowerCase().contains("by")) {
				comparisonByThread.add(new CallTree(file.getAbsolutePath()));
			} else if (file.getName().toLowerCase().contains("all")) {
				comparisonAllThreads.add(new CallTree(file.getAbsolutePath()));
			} else if (file.getName().toLowerCase().contains("cpu")) {
				comparisonCpuCSV.add(new CSVImporter(file.getAbsolutePath()));
			} else if (file.getName().toLowerCase().contains("memory")) {
				comparisonMemCSV.add(new CSVImporter(file.getAbsolutePath()));
			} else if (file.getName().toLowerCase().contains("monitor")) {
				comparisonMonitor.add(new MonitorUsage(file.getAbsolutePath()));
			} else {
				System.out.println("Cannot assign file: " + file.getName());
			}
		}

	}

	/**
	 * prepares the data before it can be processed
	 */
	public void doPrepearation() {
		// read and prepare the hotspot data
		 ArrayList<String> allHotspots = hotspot.getAllHotspots();
//		ArrayList<String> allHotspots = hotspot.findImportantMethods();
		importantMethods = getRightNames(allHotspots);

	}

	/**
	 * does the analysis for the Circuitous Treasure Hunt anti-pattern
	 */
	/**
	 * @param countThreshold
	 *            threshold for the method count
	 * @param cpuThreshold
	 *            threshold for the CPU usage threshold
	 * @param option
	 *            option from the drop down menu for the method call count
	 *            analysis
	 */
	public void cthAnalysis(double countThreshold, int cpuThreshold, String option) {
		System.out.println("__________________________Circuitous Treasure Hunt results___________________________");
		System.out.println("Selected analysis method for the Method Call Count: " + option);

		// CPU Usage
		double averageCpuUsageAll = 0;
		for (CSVImporter csvCpu : comparisonCpuCSV) {
			averageCpuUsageAll += csvCpu.getAverageCPUUsage(5, "CPU time (user + kernel) (%)");
		}
		double averageCpuUsageProb = problemCpuCSV.getAverageCPUUsage(5, "CPU time (user + kernel) (%)");
		averageCpuUsageAll = (averageCpuUsageAll + averageCpuUsageProb) / (comparisonCpuCSV.size() + 1);
		averageCpuUsageAll = Math.floor(averageCpuUsageAll * 100) / 100;
		averageCpuUsageProb = Math.floor(averageCpuUsageProb * 100) / 100;
		double deviationCpu = Math.floor((averageCpuUsageProb - averageCpuUsageAll) * 100) / 100;

		boolean foundAny = false;
		// first check if the cpu usage is high enough
		if ((averageCpuUsageProb > (averageCpuUsageAll + cpuThreshold))) {
			// first get the average from all files(including the problem file)
			// for
			// every hotspot method
			// Method Calls
			ArrayList<CallTree> tempByThreadAll = new ArrayList<>();
			ArrayList<CallTree> tempByThreadProb = new ArrayList<>();
			tempByThreadAll.addAll(comparisonByThread);
			tempByThreadAll.add(problemByThread);
			tempByThreadProb.add(problemByThread);
			// get the average method counts
			HashMap<String, Integer> averageMethodCountsAll = getAverageMethodCount(tempByThreadAll, option);
			HashMap<String, Integer> averageMethodCountsProb = getAverageMethodCount(tempByThreadProb, option);
			// do for every hotspot method the analysis
			for (String methodName : importantMethods) {
				if(averageMethodCountsAll.containsKey(methodName) && averageMethodCountsProb.containsKey(methodName)){
					double problemMethodcount = averageMethodCountsProb.get(methodName);
					double averageMethodCount = averageMethodCountsAll.get(methodName);
					
					if ((problemMethodcount > (averageMethodCount * countThreshold))) {
						double methodCountDeviation = ((problemMethodcount / averageMethodCount) - 1.00) * 100;
						System.out.println(" ");
						System.out.println("*" + methodName + "*" + " is detected!");
						System.out.println("Method count: " + problemMethodcount + " vs. " + averageMethodCount
								+ " deviation: " + (Math.floor(methodCountDeviation * 100) / 100) + "%");
						System.out.println("CPU Usage: " + averageCpuUsageProb + "% vs. " + averageCpuUsageAll
								+ "% deviation: " + deviationCpu + "%");
						
						System.out.println(" ");
						foundAny = true;
					}
					
				} else {
					System.out.println("*" + methodName + "* could not been analyzed due too few data! The method is either not available in the problem snapshot or the comparison snapshots.");
				}
					
				}
		}

		if (!foundAny)
			System.out.println("No Circuitous Treasure Hunt anti-pattern was found!");

		System.out.println("____________________End of Circuitous Treasure Hunt results_____________________");

	}

	/**
	 * does the analysis for the Extensive Processing anti-pattern
	 * 
	 * @param blockedThreadsThreshold
	 *            threshold for the amount of blocked threads in percent
	 * @param methodTimeThreshold
	 *            threshold for the method times
	 */
	public void epAnalysis(double blockedThreadsThreshold, int methodTimeThreshold) {

		// blocked threads analysis
		double blockedThreadsCountAll = 0;
		for (MonitorUsage monitorUsage : comparisonMonitor) {
			blockedThreadsCountAll = monitorUsage.getBlockedThreadCount();
		}
		double blockedThreadsCountProb = problemMonitor.getBlockedThreadCount();
		blockedThreadsCountAll = (blockedThreadsCountAll + blockedThreadsCountProb) / (comparisonMonitor.size() + 1);
		boolean foundAny = false;
		System.out.println("__________________________Extensive Processing results___________________________");
		// check if enough threads are blocked
		if (blockedThreadsCountProb > (blockedThreadsCountAll * blockedThreadsThreshold)) {
			// method time analysis
			ArrayList<CallTree> tempAllThreadsAll = new ArrayList<>();
			ArrayList<CallTree> tempAllThreadsProb = new ArrayList<>();
			tempAllThreadsAll.addAll(comparisonAllThreads);
			tempAllThreadsAll.add(problemAllThreads);
			tempAllThreadsProb.add(problemAllThreads);
			HashMap<String, Double> averageMethodPercentageAll = getAverageMethodPercentage(tempAllThreadsAll);
			HashMap<String, Double> averageMethodPercentageProb = getAverageMethodPercentage(tempAllThreadsProb);
			for (String methodName : importantMethods) {
				if(averageMethodPercentageAll.containsKey(methodName) && averageMethodPercentageProb.containsKey(methodName)){
					double percentageAll = averageMethodPercentageAll.get(methodName);
					double percentageProb = averageMethodPercentageProb.get(methodName);
					
					if (percentageProb > percentageAll + methodTimeThreshold) {
						double methodPercentageDeviation = Math.floor((percentageProb - percentageAll) * 100) / 100;
						double blockedThreadsDeviation = ((blockedThreadsCountProb / blockedThreadsCountAll) - 1.00) * 100;
						System.out.println("");
						System.out.println("*" + methodName + "*" + " is detected!");
						System.out.println("Method Time (percentage): " + percentageProb + "% vs. " + Math.floor(percentageAll * 100) / 100
								+ "% deviation: " + methodPercentageDeviation + "%");
						System.out.println(
								"Blocked Threads Count: " + blockedThreadsCountProb + " vs. " + blockedThreadsCountAll
								+ " deviation: " +  Math.floor((blockedThreadsDeviation) * 100) / 100 + "%");
						System.out.println("");
						foundAny = true;
					}
					
				} else {
					System.out.println("*" + methodName + "* could not been analyzed due too few data! The method is either not available in the problem snapshot or the comparison snapshots.");
				}

			}
		}
		if (!foundAny)
			System.out.println("No Extensive Processing anti-pattern was found!");

		System.out.println("_______________________End of Extensive Processing results_______________________");
	}

	/**
	 * does the analysis for the Wrong Cache strategy anti-pattern
	 * 
	 * @param methodTimeThreshold
	 *            threshold for the method time
	 */
	public void wcAnalysis(int methodTimeThreshold) {
		System.out.println("__________________________Wrong Cache strategy results___________________________");
		boolean foundAny = false;
		// method time analysis
		ArrayList<CallTree> tempAllThreadsAll = new ArrayList<>();
		ArrayList<CallTree> tempAllThreadsProb = new ArrayList<>();
		tempAllThreadsAll.addAll(comparisonAllThreads);
		tempAllThreadsAll.add(problemAllThreads);
		tempAllThreadsProb.add(problemAllThreads);
		HashMap<String, Double> averageMethodPercentageAll = getAverageMethodPercentage(tempAllThreadsAll);
		HashMap<String, Double> averageMethodPercentageProb = getAverageMethodPercentage(tempAllThreadsProb);
		for (String methodName : importantMethods) {
			//check if method is available
			if(averageMethodPercentageAll.containsKey(methodName) && averageMethodPercentageProb.containsKey(methodName)){
				
				double percentageAll = averageMethodPercentageAll.get(methodName);
				double percentageProb = averageMethodPercentageProb.get(methodName);
				
				if (percentageProb > percentageAll + methodTimeThreshold) {
					// additional heap memory usage information
					double averageHeapMemAll = 0;
					
					for(int i = 0; i < comparisonCpuCSV.size(); i++){
						
						ArrayList<Long> timestamps = comparisonCpuCSV.get(i).getFilteredCpuTimeStamps(5);
						averageHeapMemAll += comparisonMemCSV.get(i).getAverageHeapMemoryUsage("Used PS Eden Space (bytes)", timestamps);
					}
					
					
					ArrayList<Long> timestamps = problemCpuCSV.getFilteredCpuTimeStamps(5);
					double averageHeapMemProb = problemMemCSV.getAverageHeapMemoryUsage("Used PS Eden Space (bytes)", timestamps);
					averageHeapMemAll = (averageHeapMemAll + averageHeapMemProb) / (comparisonCpuCSV.size() + 1);
					averageHeapMemAll = Math.floor(averageHeapMemAll * 100) / 100;
					averageHeapMemProb = Math.floor(averageHeapMemProb * 100) / 100;
					
					double deviation = Math.floor((percentageProb - percentageAll) * 100) / 100;
					double heapMemDeviation = ((averageHeapMemProb / averageHeapMemAll) - 1.00) * 100;
					System.out.println("");
					System.out.println("*" + methodName + "*" + " is detected!");
					System.out.println("Method Time (percentage): " + percentageProb + "% vs. " + Math.floor(percentageAll * 100) / 100
							+ "% deviation: " + deviation + "%");
					System.out.println("Heap-Memory Usage: " + averageHeapMemProb + "MB vs. " + averageHeapMemAll
							+ "MB deviation: " + Math.floor(heapMemDeviation * 100) / 100 + "%");
					System.out.println("");
					foundAny = true;
				}
			} else {
				System.out.println("*" + methodName + "* could not been analyzed due too few data! The method is either not available in the problem snapshot or the comparison snapshots.");
			}

		}
		if (!foundAny)
			System.out.println("No Wrong Cache strategy anti-pattern was found!");

		System.out.println("_______________________End of Wrong Cache strategy results_______________________");
	}

	/**
	 * calculates for all hotspot methods the average method call count and puts
	 * the results in a hashmap
	 * 
	 * @param threadData
	 *            data from the by thread xml data.
	 * @return Hashmap with method names and the average call count over all
	 *         snapshot data
	 */
	public HashMap<String, Integer> getAverageMethodCount(ArrayList<CallTree> threadData, String option) {
		HashMap<String, Integer> averageCounts = new HashMap<>();

		for (String hotspotMethod : importantMethods) {
			int counter = 0;
			int average = -1;

			for (CallTree callTree : threadData) {
				int methodCount = 0;
				switch (option) {
				case "min":
					methodCount = callTree.getMinMethodCalls(hotspotMethod);
					break;
				case "max":
					methodCount = callTree.getMaxMethodCalls(hotspotMethod);
					break;
				case "average":
					methodCount = callTree.getAverageMethodCalls(hotspotMethod);
					break;

				}

				// just if the method exists add the method count
				if (methodCount != -1) {
					counter++;
					average += methodCount;
				}

			}
			// set average method count for each hotspotmethod
			if (counter != 0)
				averageCounts.put(hotspotMethod, (average / counter));

		}

		return averageCounts;
	}

	/**
	 * calculates for all hotspot methods the average percentage of method time
	 * in relation to the complete execution time. and puts the results in a
	 * hashmap
	 * 
	 * @param threadData
	 *            data from the all thread xml data.
	 * @return Hashmap with method names and the average percentage for it over
	 *         all snapshot data
	 */
	public HashMap<String, Double> getAverageMethodPercentage(ArrayList<CallTree> threadData) {
		HashMap<String, Double> averagePercentage = new HashMap<>();

		for (String hotspotMethod : importantMethods) {
			double counter = 0;
			double average = -1;

			for (CallTree callTree : threadData) {
				double methodPercantage = callTree.getPercentageOfTime(hotspotMethod);
				// just if the method exists add the method count
				if (methodPercantage != -1.0) {
					counter++;
					average += methodPercantage;
				}

			}
			if (counter != 0)
				averagePercentage.put(hotspotMethod, (average / counter));

		}

		return averagePercentage;
	}

	/**
	 * Name of the method needs to be corrected because the hot spot view also
	 * puts the name of the class in the name example:
	 * org.apache.commons.jexl3.JexlEngine.createExpression(String)
	 * JexlEngine.java -> "JexlEngine.java" needs to be deleted
	 * 
	 * @param methodList
	 * @return
	 */
	private ArrayList<String> getRightNames(ArrayList<String> methodList) {
		ArrayList<String> correctedNames = new ArrayList<>();

		for (String string : methodList) {
			int cut = string.lastIndexOf(")");
			String correctedString = string.substring(0, cut + 1);

			correctedNames.add(correctedString);
		}

		return correctedNames;
	}

}
