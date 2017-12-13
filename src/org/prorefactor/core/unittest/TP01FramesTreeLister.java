/* Created on 3-Feb-2006
 * Authors: John Green
 * 
 * Copyright (c) 2006 Joanju (www.joanju.com).
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.prorefactor.core.unittest;

import org.prorefactor.core.JPNode;
import org.prorefactor.core.JPNodeLister;
import org.prorefactor.core.TokenTypesI;
import org.prorefactor.nodetypes.BlockNode;
import org.prorefactor.nodetypes.FieldRefNode;
import org.prorefactor.treeparser.Block;
import org.prorefactor.treeparser.FieldContainer;
import org.prorefactor.widgettypes.Frame;

public class TP01FramesTreeLister extends JPNodeLister {

	public TP01FramesTreeLister(JPNode topNode, String outfilename, TokenTypesI typesReader) {
		super(topNode, outfilename, typesReader);
	}

	

	private void appendName(StringBuffer buff, FieldContainer container) {
		if (container.getName().length()==0)
			buff.append('"').append(container.getName()).append('"');
		else
			buff.append(container.getName());
	}

	
	@Override
	protected String generateLineText(JPNode node) {
		StringBuffer buff = new StringBuffer();
		buff
			.append(indent())
			.append(tokenTypes.getName(node.getType()))
			.append('|')
			.append(node.getText())
			.append('|')
			;
		if (node instanceof BlockNode) blockNode(buff, (BlockNode)node);
		if (node instanceof FieldRefNode) {
			fieldRefNode(buff, (FieldRefNode)node);
			fieldContainer(buff, node);
		}
		if (node.isStateHead()) fieldContainer(buff, node);
		return buff.toString();
	}

	
	private void blockNode(StringBuffer buff, BlockNode blockNode) {
		Block block = blockNode.getBlock();
		if (block.getDefaultFrame()!=null) {
			buff
				.append(spacer)
				.append("defaultFrame:")
				;
			appendName(buff, block.getDefaultFrame());
		}
		buff
			.append(spacer)
			.append("frames:")
			;
		for (Frame frame : block.getFrames()) {
			buff.append(" ");
			appendName(buff, frame);
		}
	}
	
	
	private void fieldContainer(StringBuffer buff, JPNode node) {
		FieldContainer fieldContainer = node.getFieldContainer();
		if (fieldContainer==null) return;
		buff
			.append(spacer)
			.append(tokenTypes.getName(fieldContainer.getProgressType()))
			.append("=")
			;
		appendName(buff, fieldContainer);
	}

	
	private void fieldRefNode(StringBuffer buff, FieldRefNode refNode) {
		buff
			.append(spacer)
			.append(refNode.getSymbol().fullName())
			;
	}
	
	
}
