/**
 * SymbolParseActionTest.java
 * @author Peter Dalbadie
 * 21-Sep-2004
 * 
 * Copyright (c) 2004-2007 ProRefactor.org.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.prorefactor.core.unittest;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.prorefactor.refactor.RefactorException;
import org.prorefactor.treeparser.Call;
import org.prorefactor.treeparser.ParseUnit;
import org.prorefactor.treeparser01.TP01Support;

/** Tests for Calls and Routines in the tree parser. */
public class TP01ProcessActionTest extends TestCase {
	private TP01Support symbolAction;


	/**
	 * @param name
	 */
	public TP01ProcessActionTest(String name) {
		super(name);
	}
	
	@Override
	public void setUp(){
		symbolAction = new TP01Support();
	}


	/**
	 * Parse compile-file.p and verify that all calls
	 * are registered correctly, for each scope.
	 * @throws RefactorException 
	 */
	public void testCompileFileCalls() throws Exception{
		File file = new File("data/tp01ProcessTests/compile-file.p");
		String externalName = file.getPath();

		ParseUnit pu = new ParseUnit(file);
		pu.treeParser01(symbolAction);

		// Define routine handlers for expected routines.
		RoutineHandler enableUi = new RoutineHandler("enable-ui", symbolAction);
		RoutineHandler userAction = new RoutineHandler("user-action", symbolAction);
		RoutineHandler disableUi = new RoutineHandler("disable-ui", symbolAction);
		RoutineHandler setState = new RoutineHandler("setState", symbolAction);
		RoutineHandler getCompileList = new RoutineHandler("get-compile-list", symbolAction);			

		// Define call objects for expected calls.
		Call enableUiCall = new Call(externalName,enableUi.getName());
		Call userActionCall = new Call(externalName,userAction.getName());
		Call disableUiCall = new Call(externalName,disableUi.getName());
		Call setStateCall = new Call(externalName,setState.getName());
		Call getCompileListCall = new Call(externalName,getCompileList.getName());
		
		// Create expected result set for root scope: enable-ui, user-action, disable-ui.
		ArrayList<Call> expectedRootCalls = new ArrayList<Call>();
		expectedRootCalls.add(disableUiCall);
		expectedRootCalls.add(enableUiCall);
		expectedRootCalls.add(userActionCall);

		// Get actual calls found in code and test against expected.
		ArrayList<Call> actualRootCalls = pu.getRootScope().getCallList();
		assertTrue(actualRootCalls.containsAll(expectedRootCalls));
		assertTrue(! actualRootCalls.contains(setStateCall));			
		assertTrue(! actualRootCalls.contains(getCompileListCall));			
		
		// Internal proc enable-ui calls: setState.
		ArrayList<Call> actualEnableUiCalls = enableUi.getRoutineScope().getCallList();
		assertTrue(actualEnableUiCalls.contains(setStateCall));
		
		// Internal proc user-action calls: get-compile-list.
		ArrayList<Call> actualUserActionCalls = userAction.getRoutineScope().getCallList();
		assertTrue(actualUserActionCalls.contains(getCompileListCall));
		
		// Internal proc get-compile-list calls: setState x 3.
		ArrayList<Call> actualGetCompileListCalls = getCompileList.getRoutineScope().getCallList();
		assertTrue(actualGetCompileListCalls.contains(setStateCall));
						
	}


	/**
	 * Parse persistent-run.p and verify that:
	 * a) run <proc1> persistent set <h> results in the
	 * handle variable being updated.
	 * b) run <proc2> in <h> is registered as a call to
	 * proc1.proc2.
	 */
	public void testPersistenProc() throws Exception {
		File file = new File("data/tp01ProcessTests/persistent-run.p");
		String externalName = file.getPath();

		ParseUnit pu = new ParseUnit(file);
		pu.treeParser01(symbolAction);
	
		assertTrue(symbolAction.getErrorList().size() == 0);

		// Define routines.
		RoutineHandler test01 = new RoutineHandler("test_01", symbolAction);
		RoutineHandler test02 = new RoutineHandler("test_02", symbolAction);
		
		// Define calls.
		String targetProc = "persistent-proc.p";
		Call persistentProcCall = new Call(targetProc,null);
		Call test01InHandleCall = new Call(targetProc,test01.getName());
		Call test02InHandleCall = new Call(targetProc,test02.getName());
		Call test01InternalCall = new Call(externalName,test01.getName());
		
		// Expected root procedure calls.
		ArrayList<Call> expectedRootCalls = new ArrayList<Call>();
		expectedRootCalls.add(persistentProcCall);
		expectedRootCalls.add(test01InHandleCall);
		expectedRootCalls.add(test01InternalCall);

		// Expected calls in procedure test_01
		ArrayList<Call> expectedTest01Calls = new ArrayList<Call>();
		expectedTest01Calls.add(test02InHandleCall);
		
		// Test actual root calls agains expected root calls.
		ArrayList<Call> actualRootCalls = pu.getRootScope().getCallList();
		assertTrue(actualRootCalls.containsAll(expectedRootCalls));
		assertTrue(! actualRootCalls.contains(test02InHandleCall));			

		// Test actual calls in test_01 against expected calls.
		ArrayList<Call> actualTest01Calls = test01.getRoutineScope().getCallList();
		assertTrue(actualTest01Calls.containsAll(expectedTest01Calls));
	}


}
