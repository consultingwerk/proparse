/* Created on Apr 29, 2005
 * Authors: John Green
 * 
 * Copyright (c) 2005 Joanju (www.joanju.com)
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.nodetypes;

import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.BufferScope;
import org.prorefactor.treeparser.TableBuffer;
import com.joanju.proparse.ProToken;


public class RecordNameNode extends JPNode {

	/** For creating from persistent storage */
	public RecordNameNode() { super(); }

	public RecordNameNode(int file, int line, int column) { super(file, line, column); }

	public RecordNameNode(ProToken t) {super(t);}

	public BufferScope getBufferScope() {
		BufferScope bufferScope = (BufferScope) getLink(JPNode.BUFFERSCOPE);
		assert bufferScope!=null;
		return bufferScope;
	}
	
	/** Every JPNode subtype has its own index. Used for persistent storage. */
	public int getSubtypeIndex() { return 4; }

	public TableBuffer getTableBuffer() {
		TableBuffer buffer = (TableBuffer) getLink(JPNode.SYMBOL);
		assert buffer != null;
		return buffer;
	}

	public void setBufferScope(BufferScope bufferScope) {
		assert bufferScope!=null;
		setLink(JPNode.BUFFERSCOPE, bufferScope);
	}
	
	public void setTableBuffer(TableBuffer buffer) {
		setLink(JPNode.SYMBOL, buffer);
	}

}
