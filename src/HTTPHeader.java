import java.util.ArrayList;

public class HTTPHeader {
	
	private ArrayList<String[]> hVals = null;
	private boolean valid = true;
	
	/**
	 * Attempt to parse a HTTP header
	 * @param rawData The raw request data
	 */
	public HTTPHeader(String rawData) {
		
		if(rawData == null) {
			valid = false;
			return;
		}
		
		// attempt to break down header fields
		try{
			hVals = new ArrayList<String[]>();
			
			String[] lines = rawData.split("\n");
			
			String[] temp = null;
			int col = 0;
			for(int i = 0; i < lines.length; i++) {
				temp = new String[2];
				col = lines[i].indexOf(':');
				temp[0] = lines[i].substring(0,col).trim();
				temp[1] = lines[i].substring(col+1).trim();
				hVals.add(temp);
			}
		}
		catch(Exception e) {
			valid = false;
		}
	}
	
	public String getAttribute(String name) {
		
		for(int i = 0; i < hVals.size(); i++)
			if(hVals.get(i)[0].equals(name))
				return hVals.get(i)[1];
		return null;
	}
	
	public ArrayList<String[]> getAll() {
		return hVals;
	}
}
