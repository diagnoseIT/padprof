package csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;

/**This class is used to read CSV files from YourKit.
 * 
 * @author Alexander Bran
 *
 */
public class CSVImporter {

	private String path;
	private List <long []> csvLongArr = new ArrayList<>();
	private List <String []> csvStrArr = new ArrayList<>();

	public CSVImporter(String path) {
		this.path = path;
		importCSV(path);
	}

	private void importCSV(String path) {
		CSVReader reader = null;

		try {
			reader = new CSVReader(new FileReader(path));
			csvStrArr = reader.readAll();
			csvLongArr = transformList(csvStrArr);
		} catch (FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	

	}
	
	public String getPath(){
		return path;
	}
	
	/**
	 * extracts the header from the file and transforms the string values into long ones.
	 * @param oldList
	 * @return
	 */
	private List <long[]> transformList (List <String[]> oldList){
		List <long[]> transformed = new ArrayList<>();
		//start at 1 because of the header.
		for (int i = 1; i < oldList.size(); i++) {
			String[] tempStringArr;
			tempStringArr = oldList.get(i);
			
			long[] transformTemp = new long [tempStringArr.length];
			for (int j = 0; j < tempStringArr.length; j++) {
				transformTemp[j] = Long.valueOf(tempStringArr[j]);
			}
			transformed.add(transformTemp);
		}
		
		return transformed;
		
	}
	
	public  ArrayList<Long> getFilteredCpuTimeStamps (int threshold){

		HashMap <Long, Long> cpuUsage = this.getValueFrom("CPU time (user + kernel) (%)");
		ArrayList<Long> timeStampList = new ArrayList<>();

		for (Map.Entry<Long, Long> entry : cpuUsage.entrySet()) {
			long value = entry.getValue();
			//above a certain threshold
			if(value >= threshold){
				timeStampList.add(entry.getKey());
			} 
		}
		return timeStampList;
	}
	
	/**
	 * calculates the average usage of a given variable from the heap-memory data
	 * @param filterThreshold threshold for the filtering regarding the CPU usage
	 * @param variableName exact string from the CSV.
	 * @return 
	 */
	public long getAverageHeapMemoryUsage(String variableName, ArrayList<Long> timestamps){
//		ArrayList<Long> timestamps = getFilteredCpuTimeStamps(filterThreshold);
		long average = 0;
		int count = timestamps.size();
		HashMap <Long, Long> heapUsage = getValueFrom(variableName);
		for (Long long1 : timestamps) {
			average += heapUsage.get(long1);
			
		}
		
		return (average/count)/1000000;
		
	}
	
	/**
	 * calculates the average CPU usage, also filters values by just using the ones that are above the 'filterThreshold' in order to skip idle times
	 * @param filterThreshold
	 * @param variableName exact string from the CSV ("CPU time (user + kernel) (%)" for the complete CPU utilization)
	 * @return
	 */
	public double getAverageCPUUsage(int filterThreshold, String variableName){
		ArrayList<Long> valueList = this.getFilteredList(filterThreshold, variableName);
		double averageUsage = 0;
		int count = 0;
		
		
		for (Long long1 : valueList) {
			averageUsage+= long1;
			count++;
		}
		
		return averageUsage/count;
	}
	
	/**
	 *  gets the values from a certain file
	 * @param from name of the column where the data is hold
	 * @return
	 */
	private HashMap<Long, Long> getValueFrom(String from){
		HashMap<Long, Long> returnList = new HashMap<>();
		String [] header = getHeader();
		int location = -1;
		for (int i = 0; i < header.length; i++) {
			if(header[i].toLowerCase().equals(from.toLowerCase()))
				location = i;
		}
		
		if(location != -1){
			for (long[] larr : csvLongArr) {
				returnList.put(larr[0], larr[location]);
			}
		}
		
		
		return returnList;
	}
	
	/**
	 * filters a list by deleting values that are below the threshold
	 * @param threshold
	 * @param variableName
	 * @return
	 */
	private ArrayList<Long> getFilteredList (int threshold, String variableName){
//		"CPU time (user + kernel) (%)"
		HashMap <Long, Long> reulstList = this.getValueFrom(variableName);
		

		ArrayList<Long> filteredList = new ArrayList<>();
//		ArrayList<Long> removeEntries = new ArrayList<>();
		for (Map.Entry<Long, Long> entry : reulstList.entrySet()) {
			long value = entry.getValue();
			if(value >= threshold){
				filteredList.add(value);
			} 
		}
		return filteredList;
	}
	
	/**
	 * gets the uptimes for a specific variable where the threshold is fulfilled
	 * @param threshold
	 * @param VariableName
	 * @return
	 */
	public ArrayList<Long> getUptimeList (int threshold, String VariableName){
//		"CPU time (user + kernel) (%)"
		HashMap <Long, Long> reulstList = this.getValueFrom(VariableName);
		

		ArrayList<Long> filteredUptimeList = new ArrayList<>();
//		ArrayList<Long> removeEntries = new ArrayList<>();
		for (Map.Entry<Long, Long> entry : reulstList.entrySet()) {
			long value = entry.getValue();
			if(value >= threshold){
				filteredUptimeList.add(entry.getKey());
			} 
		}
		return filteredUptimeList;
	}
	

	public String [] getHeader(){
		return csvStrArr.get(0);
	}
	
	

	public List<long[]> getCsvLongArr() {
		return csvLongArr;
	}

}
