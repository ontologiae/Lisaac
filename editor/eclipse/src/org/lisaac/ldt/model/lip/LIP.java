package org.lisaac.ldt.model.lip;

import java.util.ArrayList;
import java.util.HashMap;

public class LIP {
	
	private static final String PROJECT_PATH_SLOT = "project_path";
	private static final String LISAAC_PATH_SLOT = "path";

	private String filename;
	
	/** parent lip files */
	protected ArrayList<String> listParent = new ArrayList<String>();
	
	/** lip method list */
	protected ArrayList<LIPSlotCode> listMethod = new ArrayList<LIPSlotCode>();
	
	/** lip data */
	protected HashMap<String,LIPSlotData> listData = new HashMap<String,LIPSlotData>();

	
	public LIP(String filename) {
		this.filename = filename;
	}
	
	public String getFileName() {
		return filename;
	}
	
	public void addParent(String string) {
		listParent.add(string);
	}
	public void addMethod(LIPSlotCode m) {
		listMethod.add(m);
	}
	public void addData(LIPSlotData data) {
		listData.put(data.getName(),data);
	}
	
	public ArrayList<String> getParentList() {
		return listParent;
	}
	
	public void clearParents() {
		listParent = null;
	}
	
	public LIPSlotCode getMethod(int i) {
		return listMethod.get(i);
	}
	public int getMethodCount() {
		return listMethod.size();
	}
	
	public LIPSlotCode getPublicMethod(int index) {
		int count = 0;
		for (int i=0; i<getMethodCount(); i++) {
			LIPSlotCode method = getMethod(i);
			if (method.isPublic()) {
				if (index == count) {
					return method;
				}
				count++;
			}
		}
		return null;
	}
	
	public int getPublicMethodCount() {
		int count = 0;
		for (int i=0; i<getMethodCount(); i++) {
			LIPSlotCode method = getMethod(i);
			if (method.isPublic()) {
				count++;
			}
		}
		return count;
	}

	public void addProjectPath(String projectPath) {
		for (int i=0; i<getMethodCount(); i++) {
			LIPSlotCode method = getMethod(i);
			if (method.getName().compareTo(PROJECT_PATH_SLOT) == 0) {
				
				// lip code: path (projectPath);
				method.addLastCode(new LIPCall(LISAAC_PATH_SLOT, new LIPValue(new LIPString(projectPath))));
				break;
			}
		}
	}
	
}
