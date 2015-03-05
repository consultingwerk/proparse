package org.proparse.api;

import java.util.HashMap;

import org.prorefactor.core.JPNode;
import org.prorefactor.core.PRCException;
import org.prorefactor.core.TreeUtils;


/** Provides a few methods for backward compatibility from ABL with the old C++ API.
 * Intended for use with the old proparse.i/proparse.p still in use by Prolint.
 * Mostly needed for processing queries, just because in ABL, a user defined
 * FUNCTION cannot use FIND etc against a temp-table when being called within
 * a CAN-FIND. (See error 7254). There's no HashMap/Dictionary equivalent in ABL,
 * only temp-tables, and implementing this stuff without temp-tables would be
 * more trouble and overhead than it's worth.
 */
public class Backward {
	
	int nextHandle = 1;
	HashMap<Integer, JPNode> handles = new HashMap<Integer, JPNode>();
	HashMap<String, JPNode[]> queries = new HashMap<String, JPNode[]>();


	
	private void checkHandle(int h) throws PRCException {
		if (! handles.containsKey(h))
			throw new PRCException("Reference to invalid node handle");
	}
	private void checkNode(int h) throws PRCException {
		checkHandle(h);
		if (handles.get(h) == null)
			throw new PRCException("Reference to handle with no node");
	}
	
	public void clear() {
		handles.clear();
		queries.clear();
		nextHandle = 1;
	}

	public void copyHandle(int from, int to) throws PRCException {
		checkHandle(from);
		checkHandle(to);
		handles.put(to, handles.get(from));
	}
	
	public int getHandle() {
		handles.put(nextHandle, null);
		return nextHandle++;
	}
	
	public JPNode getNode(int h) throws PRCException {
		checkNode(h);
		return handles.get(h);
	}
	
	public void setHandle(int h, JPNode n) {
		handles.put(h, n);
	}
	
	public void queryClear(String queryName) {
		if (queryName.equals(""))
			queries.clear();
		else
			queries.remove(queryName);
	}
	
	public JPNode [] queryCreate(int fromHandle, String queryName, String typeName) throws PRCException {
		String lname = queryName.toLowerCase();
		checkNode(fromHandle);
		JPNode [] ret;
		if (typeName.equals(""))
			ret = TreeUtils.flatListAsArray(handles.get(fromHandle));
		else
			ret = handles.get(fromHandle).query(typeName);
		queries.put(lname, ret);
		return ret;
	}
	
	public JPNode [] queryGet(String queryName) throws PRCException {
		String lname = queryName.toLowerCase();
		if (! queries.containsKey(lname))
			throw new PRCException("Invalid query name");
		return queries.get(lname);
	}

	public void queryGetResult(String queryName, int resultNum, int intoHandle) throws PRCException {
		JPNode[] nodes = queryGet(queryName);
		if (resultNum < 0 || resultNum > nodes.length)
			throw new PRCException("Request for query result number out of range");
		setHandle(intoHandle, nodes[resultNum - 1]); // ABL counts from 1.
	}
	
}
