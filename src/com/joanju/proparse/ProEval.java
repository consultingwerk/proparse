// $ANTLR : "proeval.g" -> "ProEval.java"$

package com.joanju.proparse;
import static com.joanju.proparse.ProEvalSupport.*;

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;


public class ProEval extends antlr.TreeParser       implements ProEvalTokenTypes
 {
public ProEval() {
	tokenNames = _tokenNames;
}

	public final boolean  preproIfEval(AST _t) throws RecognitionException {
		boolean ret;
		
		AST preproIfEval_AST_in = (_t == ASTNULL) ? null : (AST)_t;
			Object a;
		
		
		a=expr(_t);
		_t = _retTree;
		ret = a!=null && getBool(a);
		_retTree = _t;
		return ret;
	}
	
	public final Object  expr(AST _t) throws RecognitionException {
		Object ret;
		
		AST expr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
		
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case OR:
		{
			AST __t21 = _t;
			AST tmp1_AST_in = (AST)_t;
			match(_t,OR);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t21;
			_t = _t.getNextSibling();
				ret = new Boolean(getBool(a) || getBool(b));
					
			break;
		}
		case AND:
		{
			AST __t22 = _t;
			AST tmp2_AST_in = (AST)_t;
			match(_t,AND);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t22;
			_t = _t.getNextSibling();
				ret = new Boolean(getBool(a) && getBool(b));
					
			break;
		}
		case BEGINS:
		case EQ:
		case GE:
		case GTHAN:
		case LE:
		case LTHAN:
		case MATCHES:
		case NE:
		{
			ret=comparisonop(_t);
			_t = _retTree;
			break;
		}
		case MODULO:
		case MINUS:
		case PLUS:
		case MULTIPLY:
		case DIVIDE:
		{
			ret=binaryop(_t);
			_t = _retTree;
			break;
		}
		case NOT:
		case UNARY_MINUS:
		case UNARY_PLUS:
		{
			ret=unaryop(_t);
			_t = _retTree;
			break;
		}
		case NUMBER:
		case QSTRING:
		case FALSE_KW:
		case NO:
		case TRUE_KW:
		case YES:
		case LEFTPAREN:
		case UNKNOWNVALUE:
		{
			ret=atom(_t);
			_t = _retTree;
			break;
		}
		case ABSOLUTE:
		case ASC:
		case DATE:
		case DAY:
		case DBTYPE:
		case DECIMAL:
		case ENCODE:
		case ENTRY:
		case ETIME_KW:
		case EXP:
		case FILL:
		case INDEX:
		case INTEGER:
		case KEYWORD:
		case KEYWORDALL:
		case LC:
		case LEFTTRIM:
		case LENGTH:
		case LIBRARY:
		case LOG:
		case LOOKUP:
		case MAXIMUM:
		case MEMBER:
		case MINIMUM:
		case MONTH:
		case NUMENTRIES:
		case OPSYS:
		case PROPATH:
		case PROVERSION:
		case RINDEX:
		case RANDOM:
		case REPLACE:
		case RIGHTTRIM:
		case ROUND:
		case SQRT:
		case STRING:
		case SUBSTITUTE:
		case SUBSTRING:
		case TIME:
		case TODAY:
		case TRIM:
		case TRUNCATE:
		case WEEKDAY:
		case YEAR:
		case INT64:
		{
			ret=function(_t);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return ret;
	}
	
	public final void program(AST _t) throws RecognitionException {
		
		AST program_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST __t3 = _t;
		AST tmp3_AST_in = (AST)_t;
		match(_t,Program_root);
		_t = _t.getFirstChild();
		{
		_loop5:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==DISPLAY||_t.getType()==DO||_t.getType()==IF)) {
				blockorstatement(_t);
				_t = _retTree;
			}
			else {
				break _loop5;
			}
			
		} while (true);
		}
		_t = __t3;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final void blockorstatement(AST _t) throws RecognitionException {
		
		AST blockorstatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case DO:
		{
			doblock(_t);
			_t = _retTree;
			break;
		}
		case IF:
		{
			ifblock(_t);
			_t = _retTree;
			break;
		}
		case DISPLAY:
		{
			displaystate(_t);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
	}
	
	public final void doblock(AST _t) throws RecognitionException {
		
		AST doblock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST __t10 = _t;
		AST tmp4_AST_in = (AST)_t;
		match(_t,DO);
		_t = _t.getFirstChild();
		AST tmp5_AST_in = (AST)_t;
		match(_t,LEXCOLON);
		_t = _t.getNextSibling();
		{
		_loop12:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==DISPLAY||_t.getType()==DO||_t.getType()==IF)) {
				blockorstatement(_t);
				_t = _retTree;
			}
			else {
				break _loop12;
			}
			
		} while (true);
		}
		AST tmp6_AST_in = (AST)_t;
		match(_t,END);
		_t = _t.getNextSibling();
		AST tmp7_AST_in = (AST)_t;
		match(_t,PERIOD);
		_t = _t.getNextSibling();
		_t = __t10;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final void ifblock(AST _t) throws RecognitionException {
		
		AST ifblock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST bs = null;
			Object a;
		
		
		AST __t8 = _t;
		AST tmp8_AST_in = (AST)_t;
		match(_t,IF);
		_t = _t.getFirstChild();
		a=expr(_t);
		_t = _retTree;
		AST tmp9_AST_in = (AST)_t;
		match(_t,THEN);
		_t = _t.getNextSibling();
		bs = (AST)_t;
		if ( _t==null ) throw new MismatchedTokenException();
		_t = _t.getNextSibling();
		_t = __t8;
		_t = _t.getNextSibling();
		
					if (a!=null && getBool(a))
						blockorstatement(bs);
				
		_retTree = _t;
	}
	
	public final void displaystate(AST _t) throws RecognitionException {
		
		AST displaystate_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST __t14 = _t;
		AST tmp10_AST_in = (AST)_t;
		match(_t,DISPLAY);
		_t = _t.getFirstChild();
		{
		_loop16:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==Form_item)) {
				formitem(_t);
				_t = _retTree;
			}
			else {
				break _loop16;
			}
			
		} while (true);
		}
		AST tmp11_AST_in = (AST)_t;
		match(_t,PERIOD);
		_t = _t.getNextSibling();
		_t = __t14;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final void formitem(AST _t) throws RecognitionException {
		
		AST formitem_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t18 = _t;
		AST tmp12_AST_in = (AST)_t;
		match(_t,Form_item);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case SKIP:
		{
			AST tmp13_AST_in = (AST)_t;
			match(_t,SKIP);
			_t = _t.getNextSibling();
			System.out.println();
			break;
		}
		case NUMBER:
		case QSTRING:
		case ABSOLUTE:
		case AND:
		case ASC:
		case BEGINS:
		case DATE:
		case DAY:
		case DBTYPE:
		case DECIMAL:
		case ENCODE:
		case ENTRY:
		case EQ:
		case ETIME_KW:
		case EXP:
		case FALSE_KW:
		case FILL:
		case GE:
		case GTHAN:
		case INDEX:
		case INTEGER:
		case KEYWORD:
		case KEYWORDALL:
		case LC:
		case LE:
		case LEFTTRIM:
		case LENGTH:
		case LIBRARY:
		case LOG:
		case LOOKUP:
		case LTHAN:
		case MATCHES:
		case MAXIMUM:
		case MEMBER:
		case MINIMUM:
		case MODULO:
		case MONTH:
		case NE:
		case NO:
		case NOT:
		case NUMENTRIES:
		case OPSYS:
		case OR:
		case PROPATH:
		case PROVERSION:
		case RINDEX:
		case RANDOM:
		case REPLACE:
		case RIGHTTRIM:
		case ROUND:
		case SQRT:
		case STRING:
		case SUBSTITUTE:
		case SUBSTRING:
		case TIME:
		case TODAY:
		case TRIM:
		case TRUE_KW:
		case TRUNCATE:
		case WEEKDAY:
		case YEAR:
		case YES:
		case MINUS:
		case PLUS:
		case LEFTPAREN:
		case UNKNOWNVALUE:
		case UNARY_MINUS:
		case UNARY_PLUS:
		case MULTIPLY:
		case DIVIDE:
		case INT64:
		{
			a=expr(_t);
			_t = _retTree;
			System.out.println(a);
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_t = __t18;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final Object  comparisonop(AST _t) throws RecognitionException {
		Object ret;
		
		AST comparisonop_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
		
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case MATCHES:
		{
			AST __t24 = _t;
			AST tmp14_AST_in = (AST)_t;
			match(_t,MATCHES);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t24;
			_t = _t.getNextSibling();
				ret = matches(a, b);
					
			break;
		}
		case BEGINS:
		{
			AST __t25 = _t;
			AST tmp15_AST_in = (AST)_t;
			match(_t,BEGINS);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t25;
			_t = _t.getNextSibling();
				String sa = ((String)a).toLowerCase();
						String sb = ((String)b).toLowerCase();
						ret = new Boolean(sa.startsWith(sb));
					
			break;
		}
		case EQ:
		{
			AST __t26 = _t;
			AST tmp16_AST_in = (AST)_t;
			match(_t,EQ);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t26;
			_t = _t.getNextSibling();
				ret = compare(a, b, Compare.EQ);
					
			break;
		}
		case NE:
		{
			AST __t27 = _t;
			AST tmp17_AST_in = (AST)_t;
			match(_t,NE);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t27;
			_t = _t.getNextSibling();
				ret = compare(a, b, Compare.NE);
					
			break;
		}
		case GTHAN:
		{
			AST __t28 = _t;
			AST tmp18_AST_in = (AST)_t;
			match(_t,GTHAN);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t28;
			_t = _t.getNextSibling();
				ret = compare(a, b, Compare.GT);
					
			break;
		}
		case GE:
		{
			AST __t29 = _t;
			AST tmp19_AST_in = (AST)_t;
			match(_t,GE);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t29;
			_t = _t.getNextSibling();
				ret = compare(a, b, Compare.GE);
					
			break;
		}
		case LTHAN:
		{
			AST __t30 = _t;
			AST tmp20_AST_in = (AST)_t;
			match(_t,LTHAN);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t30;
			_t = _t.getNextSibling();
				ret = compare(a, b, Compare.LT);
					
			break;
		}
		case LE:
		{
			AST __t31 = _t;
			AST tmp21_AST_in = (AST)_t;
			match(_t,LE);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t31;
			_t = _t.getNextSibling();
				ret = compare(a, b, Compare.LE);
					
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return ret;
	}
	
	public final Object  binaryop(AST _t) throws RecognitionException {
		Object ret;
		
		AST binaryop_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
		
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case PLUS:
		{
			AST __t33 = _t;
			AST tmp22_AST_in = (AST)_t;
			match(_t,PLUS);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t33;
			_t = _t.getNextSibling();
				ret = opPlus(a, b);
					
			break;
		}
		case MINUS:
		{
			AST __t34 = _t;
			AST tmp23_AST_in = (AST)_t;
			match(_t,MINUS);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t34;
			_t = _t.getNextSibling();
				ret = opMinus(a, b);
					
			break;
		}
		case MULTIPLY:
		{
			AST __t35 = _t;
			AST tmp24_AST_in = (AST)_t;
			match(_t,MULTIPLY);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t35;
			_t = _t.getNextSibling();
				ret = opMultiply(a, b);
					
			break;
		}
		case DIVIDE:
		{
			AST __t36 = _t;
			AST tmp25_AST_in = (AST)_t;
			match(_t,DIVIDE);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t36;
			_t = _t.getNextSibling();
				ret = opDivide(a, b);
					
			break;
		}
		case MODULO:
		{
			AST __t37 = _t;
			AST tmp26_AST_in = (AST)_t;
			match(_t,MODULO);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			b=expr(_t);
			_t = _retTree;
			_t = __t37;
			_t = _t.getNextSibling();
				Double m1 = getFloat(a) + .5;
						Double m2 = getFloat(b) + .5;
						ret = new Integer(m1.intValue() % m2.intValue());
					
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return ret;
	}
	
	public final Object  unaryop(AST _t) throws RecognitionException {
		Object ret;
		
		AST unaryop_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case NOT:
		{
			AST __t39 = _t;
			AST tmp27_AST_in = (AST)_t;
			match(_t,NOT);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			_t = __t39;
			_t = _t.getNextSibling();
				ret = new Boolean(!getBool(a));
					
			break;
		}
		case UNARY_MINUS:
		{
			AST __t40 = _t;
			AST tmp28_AST_in = (AST)_t;
			match(_t,UNARY_MINUS);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			_t = __t40;
			_t = _t.getNextSibling();
				if (a instanceof Integer)
							ret = (Integer)a * -1;
						else
							ret = (Float)a * -1;
					
			break;
		}
		case UNARY_PLUS:
		{
			AST __t41 = _t;
			AST tmp29_AST_in = (AST)_t;
			match(_t,UNARY_PLUS);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			_t = __t41;
			_t = _t.getNextSibling();
				ret = a;
					
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return ret;
	}
	
	public final Object  atom(AST _t) throws RecognitionException {
		Object ret;
		
		AST atom_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST n = null;
		AST s = null;
		
			Object a;
		
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case NUMBER:
		{
			n = (AST)_t;
			match(_t,NUMBER);
			_t = _t.getNextSibling();
				ret = getNumber(n.getText());
					
			break;
		}
		case QSTRING:
		{
			s = (AST)_t;
			match(_t,QSTRING);
			_t = _t.getNextSibling();
				ret = StringFuncs.qstringStrip(s.getText());
					
			break;
		}
		case TRUE_KW:
		case YES:
		{
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case YES:
			{
				AST tmp30_AST_in = (AST)_t;
				match(_t,YES);
				_t = _t.getNextSibling();
				break;
			}
			case TRUE_KW:
			{
				AST tmp31_AST_in = (AST)_t;
				match(_t,TRUE_KW);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
				ret = new Boolean(true);
					
			break;
		}
		case FALSE_KW:
		case NO:
		{
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NO:
			{
				AST tmp32_AST_in = (AST)_t;
				match(_t,NO);
				_t = _t.getNextSibling();
				break;
			}
			case FALSE_KW:
			{
				AST tmp33_AST_in = (AST)_t;
				match(_t,FALSE_KW);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
				ret = new Boolean(false);
					
			break;
		}
		case UNKNOWNVALUE:
		{
			AST tmp34_AST_in = (AST)_t;
			match(_t,UNKNOWNVALUE);
			_t = _t.getNextSibling();
				ret = null;
					
			break;
		}
		case LEFTPAREN:
		{
			AST __t45 = _t;
			AST tmp35_AST_in = (AST)_t;
			match(_t,LEFTPAREN);
			_t = _t.getFirstChild();
			a=expr(_t);
			_t = _retTree;
			AST tmp36_AST_in = (AST)_t;
			match(_t,RIGHTPAREN);
			_t = _t.getNextSibling();
			_t = __t45;
			_t = _t.getNextSibling();
				ret = a;
					
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return ret;
	}
	
	public final Object  function(AST _t) throws RecognitionException {
		Object r;
		
		AST function_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case ABSOLUTE:
		{
			r=abs_fun(_t);
			_t = _retTree;
			break;
		}
		case ASC:
		{
			r=asc_fun(_t);
			_t = _retTree;
			break;
		}
		case DATE:
		{
			r=date_fun(_t);
			_t = _retTree;
			break;
		}
		case DAY:
		{
			r=day_fun(_t);
			_t = _retTree;
			break;
		}
		case DBTYPE:
		{
			r=dbtype_fun(_t);
			_t = _retTree;
			break;
		}
		case DECIMAL:
		{
			r=decimal_fun(_t);
			_t = _retTree;
			break;
		}
		case ENCODE:
		{
			r=encode_fun(_t);
			_t = _retTree;
			break;
		}
		case ENTRY:
		{
			r=entry_fun(_t);
			_t = _retTree;
			break;
		}
		case ETIME_KW:
		{
			r=etime_fun(_t);
			_t = _retTree;
			break;
		}
		case EXP:
		{
			r=exp_fun(_t);
			_t = _retTree;
			break;
		}
		case FILL:
		{
			r=fill_fun(_t);
			_t = _retTree;
			break;
		}
		case INDEX:
		{
			r=index_fun(_t);
			_t = _retTree;
			break;
		}
		case INTEGER:
		{
			r=integer_fun(_t);
			_t = _retTree;
			break;
		}
		case INT64:
		{
			r=int64_fun(_t);
			_t = _retTree;
			break;
		}
		case KEYWORD:
		{
			r=keyword_fun(_t);
			_t = _retTree;
			break;
		}
		case KEYWORDALL:
		{
			r=keywordall_fun(_t);
			_t = _retTree;
			break;
		}
		case LC:
		{
			r=lc_fun(_t);
			_t = _retTree;
			break;
		}
		case LEFTTRIM:
		{
			r=lefttrim_fun(_t);
			_t = _retTree;
			break;
		}
		case LENGTH:
		{
			r=length_fun(_t);
			_t = _retTree;
			break;
		}
		case LIBRARY:
		{
			r=library_fun(_t);
			_t = _retTree;
			break;
		}
		case LOG:
		{
			r=log_fun(_t);
			_t = _retTree;
			break;
		}
		case LOOKUP:
		{
			r=lookup_fun(_t);
			_t = _retTree;
			break;
		}
		case MAXIMUM:
		{
			r=maximum_fun(_t);
			_t = _retTree;
			break;
		}
		case MEMBER:
		{
			r=member_fun(_t);
			_t = _retTree;
			break;
		}
		case MINIMUM:
		{
			r=minimum_fun(_t);
			_t = _retTree;
			break;
		}
		case MONTH:
		{
			r=month_fun(_t);
			_t = _retTree;
			break;
		}
		case NUMENTRIES:
		{
			r=numentries_fun(_t);
			_t = _retTree;
			break;
		}
		case OPSYS:
		{
			r=opsys_fun(_t);
			_t = _retTree;
			break;
		}
		case PROPATH:
		{
			r=propath_fun(_t);
			_t = _retTree;
			break;
		}
		case PROVERSION:
		{
			r=proversion_fun(_t);
			_t = _retTree;
			break;
		}
		case RINDEX:
		{
			r=rindex_fun(_t);
			_t = _retTree;
			break;
		}
		case RANDOM:
		{
			r=random_fun(_t);
			_t = _retTree;
			break;
		}
		case REPLACE:
		{
			r=replace_fun(_t);
			_t = _retTree;
			break;
		}
		case RIGHTTRIM:
		{
			r=righttrim_fun(_t);
			_t = _retTree;
			break;
		}
		case ROUND:
		{
			r=round_fun(_t);
			_t = _retTree;
			break;
		}
		case SQRT:
		{
			r=sqrt_fun(_t);
			_t = _retTree;
			break;
		}
		case STRING:
		{
			r=string_fun(_t);
			_t = _retTree;
			break;
		}
		case SUBSTITUTE:
		{
			r=substitute_fun(_t);
			_t = _retTree;
			break;
		}
		case SUBSTRING:
		{
			r=substring_fun(_t);
			_t = _retTree;
			break;
		}
		case TIME:
		{
			r=time_fun(_t);
			_t = _retTree;
			break;
		}
		case TODAY:
		{
			r=today_fun(_t);
			_t = _retTree;
			break;
		}
		case TRIM:
		{
			r=trim_fun(_t);
			_t = _retTree;
			break;
		}
		case TRUNCATE:
		{
			r=truncate_fun(_t);
			_t = _retTree;
			break;
		}
		case WEEKDAY:
		{
			r=weekday_fun(_t);
			_t = _retTree;
			break;
		}
		case YEAR:
		{
			r=year_fun(_t);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return r;
	}
	
	public final Object  abs_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST abs_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t48 = _t;
		AST tmp37_AST_in = (AST)_t;
		match(_t,ABSOLUTE);
		_t = _t.getFirstChild();
		AST tmp38_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp39_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t48;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("ABS function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  asc_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST asc_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b = null;
			Object c = null;
		
		
		AST __t50 = _t;
		AST tmp40_AST_in = (AST)_t;
		match(_t,ASC);
		_t = _t.getFirstChild();
		AST tmp41_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp42_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			b=expr(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case COMMA:
			{
				AST tmp43_AST_in = (AST)_t;
				match(_t,COMMA);
				_t = _t.getNextSibling();
				c=expr(_t);
				_t = _retTree;
				break;
			}
			case RIGHTPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp44_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t50;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("ASC function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  date_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST date_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b = null;
			Object c = null;
		
		
		AST __t54 = _t;
		AST tmp45_AST_in = (AST)_t;
		match(_t,DATE);
		_t = _t.getFirstChild();
		AST tmp46_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp47_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			b=expr(_t);
			_t = _retTree;
			AST tmp48_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			c=expr(_t);
			_t = _retTree;
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp49_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t54;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("DATE function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  day_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST day_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t57 = _t;
		AST tmp50_AST_in = (AST)_t;
		match(_t,DAY);
		_t = _t.getFirstChild();
		AST tmp51_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp52_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t57;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("DAY function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  dbtype_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST dbtype_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t59 = _t;
		AST tmp53_AST_in = (AST)_t;
		match(_t,DBTYPE);
		_t = _t.getFirstChild();
		AST tmp54_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp55_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t59;
		_t = _t.getNextSibling();
			ret = "PROGRESS";
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  decimal_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST decimal_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t61 = _t;
		AST tmp56_AST_in = (AST)_t;
		match(_t,DECIMAL);
		_t = _t.getFirstChild();
		AST tmp57_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp58_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t61;
		_t = _t.getNextSibling();
			ret = decimal(a);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  encode_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST encode_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t63 = _t;
		AST tmp59_AST_in = (AST)_t;
		match(_t,ENCODE);
		_t = _t.getFirstChild();
		AST tmp60_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp61_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t63;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("ENCODE function is not supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  entry_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST entry_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
			Object c = null;
		
		
		AST __t65 = _t;
		AST tmp62_AST_in = (AST)_t;
		match(_t,ENTRY);
		_t = _t.getFirstChild();
		AST tmp63_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp64_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getNextSibling();
		b=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp65_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			c=expr(_t);
			_t = _retTree;
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp66_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t65;
		_t = _t.getNextSibling();
			ret = entry(a, b, c);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  etime_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST etime_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a = null;
		
		
		AST __t68 = _t;
		AST tmp67_AST_in = (AST)_t;
		match(_t,ETIME_KW);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case LEFTPAREN:
		{
			AST tmp68_AST_in = (AST)_t;
			match(_t,LEFTPAREN);
			_t = _t.getNextSibling();
			a=expr(_t);
			_t = _retTree;
			AST tmp69_AST_in = (AST)_t;
			match(_t,RIGHTPAREN);
			_t = _t.getNextSibling();
			break;
		}
		case 3:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_t = __t68;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("ETIME function is not supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  exp_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST exp_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
		
		
		AST __t71 = _t;
		AST tmp70_AST_in = (AST)_t;
		match(_t,EXP);
		_t = _t.getFirstChild();
		AST tmp71_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp72_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getNextSibling();
		b=expr(_t);
		_t = _retTree;
		AST tmp73_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t71;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("EXP function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  fill_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST fill_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
		
		
		AST __t73 = _t;
		AST tmp74_AST_in = (AST)_t;
		match(_t,FILL);
		_t = _t.getFirstChild();
		AST tmp75_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp76_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getNextSibling();
		b=expr(_t);
		_t = _retTree;
		AST tmp77_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t73;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("FILL function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  index_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST index_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
			Object c = null;
		
		
		AST __t75 = _t;
		AST tmp78_AST_in = (AST)_t;
		match(_t,INDEX);
		_t = _t.getFirstChild();
		AST tmp79_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp80_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getNextSibling();
		b=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp81_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			c=expr(_t);
			_t = _retTree;
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp82_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t75;
		_t = _t.getNextSibling();
			ret = index(a, b, c);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  integer_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST integer_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t78 = _t;
		AST tmp83_AST_in = (AST)_t;
		match(_t,INTEGER);
		_t = _t.getFirstChild();
		AST tmp84_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp85_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t78;
		_t = _t.getNextSibling();
			ret = integer(a);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  int64_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST int64_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		Object a;
		
		
		AST __t80 = _t;
		AST tmp86_AST_in = (AST)_t;
		match(_t,INT64);
		_t = _t.getFirstChild();
		AST tmp87_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp88_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t80;
		_t = _t.getNextSibling();
		ret = integer(a);
		
		_retTree = _t;
		return ret;
	}
	
	public final Object  keyword_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST keyword_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t82 = _t;
		AST tmp89_AST_in = (AST)_t;
		match(_t,KEYWORD);
		_t = _t.getFirstChild();
		AST tmp90_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp91_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t82;
		_t = _t.getNextSibling();
			ret = keyword(a);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  keywordall_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST keywordall_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t84 = _t;
		AST tmp92_AST_in = (AST)_t;
		match(_t,KEYWORDALL);
		_t = _t.getFirstChild();
		AST tmp93_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp94_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t84;
		_t = _t.getNextSibling();
			ret = keywordall(a);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  lc_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST lc_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t86 = _t;
		AST tmp95_AST_in = (AST)_t;
		match(_t,LC);
		_t = _t.getFirstChild();
		AST tmp96_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp97_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t86;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("LC function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  lefttrim_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST lefttrim_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b = null;
		
		
		AST __t88 = _t;
		AST tmp98_AST_in = (AST)_t;
		match(_t,LEFTTRIM);
		_t = _t.getFirstChild();
		AST tmp99_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp100_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			b=expr(_t);
			_t = _retTree;
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp101_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t88;
		_t = _t.getNextSibling();
			ret = lefttrim(a, b);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  length_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST length_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b = null;
		
		
		AST __t91 = _t;
		AST tmp102_AST_in = (AST)_t;
		match(_t,LENGTH);
		_t = _t.getFirstChild();
		AST tmp103_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp104_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			b=expr(_t);
			_t = _retTree;
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp105_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t91;
		_t = _t.getNextSibling();
			if (b!=null)
						throw new ProEvalException("Type option of LENGTH function not yet supported.");
					ret = new Integer(getString(a).length());
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  library_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST library_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t94 = _t;
		AST tmp106_AST_in = (AST)_t;
		match(_t,LIBRARY);
		_t = _t.getFirstChild();
		AST tmp107_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp108_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t94;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("LIBRARY function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  log_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST log_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b = null;
		
		
		AST __t96 = _t;
		AST tmp109_AST_in = (AST)_t;
		match(_t,LOG);
		_t = _t.getFirstChild();
		AST tmp110_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp111_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			b=expr(_t);
			_t = _retTree;
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp112_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t96;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("LOG function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  lookup_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST lookup_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
			Object c = null;
		
		
		AST __t99 = _t;
		AST tmp113_AST_in = (AST)_t;
		match(_t,LOOKUP);
		_t = _t.getFirstChild();
		AST tmp114_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp115_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getNextSibling();
		b=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp116_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			c=expr(_t);
			_t = _retTree;
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp117_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t99;
		_t = _t.getNextSibling();
			ret = lookup(a, b, c);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  maximum_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST maximum_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
		
		
		AST __t102 = _t;
		AST tmp118_AST_in = (AST)_t;
		match(_t,MAXIMUM);
		_t = _t.getFirstChild();
		AST tmp119_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
			ret = a;
				
		{
		int _cnt104=0;
		_loop104:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==COMMA)) {
				AST tmp120_AST_in = (AST)_t;
				match(_t,COMMA);
				_t = _t.getNextSibling();
				b=expr(_t);
				_t = _retTree;
					if (ret==null || b==null)
									ret=null;
								else {
									if (compare(b, ret, Compare.GT))
										ret = b;
								}
							
			}
			else {
				if ( _cnt104>=1 ) { break _loop104; } else {throw new NoViableAltException(_t);}
			}
			
			_cnt104++;
		} while (true);
		}
		AST tmp121_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t102;
		_t = _t.getNextSibling();
		_retTree = _t;
		return ret;
	}
	
	public final Object  member_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST member_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t106 = _t;
		AST tmp122_AST_in = (AST)_t;
		match(_t,MEMBER);
		_t = _t.getFirstChild();
		AST tmp123_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp124_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t106;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("MEMBER function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  minimum_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST minimum_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
		
		
		AST __t108 = _t;
		AST tmp125_AST_in = (AST)_t;
		match(_t,MINIMUM);
		_t = _t.getFirstChild();
		AST tmp126_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
			ret = a;
				
		{
		int _cnt110=0;
		_loop110:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==COMMA)) {
				AST tmp127_AST_in = (AST)_t;
				match(_t,COMMA);
				_t = _t.getNextSibling();
				b=expr(_t);
				_t = _retTree;
					if (ret==null || b==null)
									ret=null;
								else {
									if (compare(b, ret, Compare.LT))
										ret = b;
								}
							
			}
			else {
				if ( _cnt110>=1 ) { break _loop110; } else {throw new NoViableAltException(_t);}
			}
			
			_cnt110++;
		} while (true);
		}
		AST tmp128_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t108;
		_t = _t.getNextSibling();
		_retTree = _t;
		return ret;
	}
	
	public final Object  month_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST month_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t112 = _t;
		AST tmp129_AST_in = (AST)_t;
		match(_t,MONTH);
		_t = _t.getFirstChild();
		AST tmp130_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp131_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t112;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("MONTH function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  numentries_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST numentries_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b = null;
		
		
		AST __t114 = _t;
		AST tmp132_AST_in = (AST)_t;
		match(_t,NUMENTRIES);
		_t = _t.getFirstChild();
		AST tmp133_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp134_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			b=expr(_t);
			_t = _retTree;
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp135_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t114;
		_t = _t.getNextSibling();
			ret = numentries(a, b);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  opsys_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST opsys_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST tmp136_AST_in = (AST)_t;
		match(_t,OPSYS);
		_t = _t.getNextSibling();
			String opsys = Environment.instance().opsys;
					if (opsys == null || opsys.length()==0)
						throw new ProEvalException("OPSYS has not been configured in Proparse.");
					ret = opsys;
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  propath_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST propath_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST tmp137_AST_in = (AST)_t;
		match(_t,PROPATH);
		_t = _t.getNextSibling();
			ret = propath();
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  proversion_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST proversion_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST tmp138_AST_in = (AST)_t;
		match(_t,PROVERSION);
		_t = _t.getNextSibling();
			String proversion = Environment.instance().proversion;
					if (proversion == null || proversion.length()==0)
						throw new ProEvalException("PROVERSION has not been configured in Proparse.");
					ret = proversion;
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  rindex_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST rindex_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
			Object c = null;
		
		
		AST __t120 = _t;
		AST tmp139_AST_in = (AST)_t;
		match(_t,RINDEX);
		_t = _t.getFirstChild();
		AST tmp140_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp141_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getNextSibling();
		b=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp142_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			c=expr(_t);
			_t = _retTree;
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp143_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t120;
		_t = _t.getNextSibling();
			ret = rindex(a, b, c);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  random_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST random_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
		
		
		AST __t123 = _t;
		AST tmp144_AST_in = (AST)_t;
		match(_t,RANDOM);
		_t = _t.getFirstChild();
		AST tmp145_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp146_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getNextSibling();
		b=expr(_t);
		_t = _retTree;
		AST tmp147_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t123;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("RANDOM function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  replace_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST replace_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
			Object c;
		
		
		AST __t125 = _t;
		AST tmp148_AST_in = (AST)_t;
		match(_t,REPLACE);
		_t = _t.getFirstChild();
		AST tmp149_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp150_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getNextSibling();
		b=expr(_t);
		_t = _retTree;
		AST tmp151_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getNextSibling();
		c=expr(_t);
		_t = _retTree;
		AST tmp152_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t125;
		_t = _t.getNextSibling();
			ret = replace(getString(a), getString(b), getString(c));
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  righttrim_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST righttrim_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b = null;
		
		
		AST __t127 = _t;
		AST tmp153_AST_in = (AST)_t;
		match(_t,RIGHTTRIM);
		_t = _t.getFirstChild();
		AST tmp154_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp155_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			b=expr(_t);
			_t = _retTree;
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp156_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t127;
		_t = _t.getNextSibling();
			String s = getString(a);
					if (b!=null)
						ret = StringFuncs.rtrim(s, getString(b));
					else
						ret = StringFuncs.rtrim(s);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  round_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST round_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
		
		
		AST __t130 = _t;
		AST tmp157_AST_in = (AST)_t;
		match(_t,ROUND);
		_t = _t.getFirstChild();
		AST tmp158_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp159_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getNextSibling();
		b=expr(_t);
		_t = _retTree;
		AST tmp160_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t130;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("ROUND function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  sqrt_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST sqrt_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t132 = _t;
		AST tmp161_AST_in = (AST)_t;
		match(_t,SQRT);
		_t = _t.getFirstChild();
		AST tmp162_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp163_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t132;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("SQRT function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  string_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST string_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b = null;
		
		
		AST __t134 = _t;
		AST tmp164_AST_in = (AST)_t;
		match(_t,STRING);
		_t = _t.getFirstChild();
		AST tmp165_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp166_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			b=expr(_t);
			_t = _retTree;
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp167_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t134;
		_t = _t.getNextSibling();
			if (b!=null)
						throw new ProEvalException("Format option of STRING function is not yet supported.");
					ret = string(a);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  substitute_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST substitute_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
		
		
		AST __t137 = _t;
		AST tmp168_AST_in = (AST)_t;
		match(_t,SUBSTITUTE);
		_t = _t.getFirstChild();
		AST tmp169_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		{
		_loop139:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==COMMA)) {
				AST tmp170_AST_in = (AST)_t;
				match(_t,COMMA);
				_t = _t.getNextSibling();
				b=expr(_t);
				_t = _retTree;
			}
			else {
				break _loop139;
			}
			
		} while (true);
		}
		AST tmp171_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t137;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("SUBSTITUTE function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  substring_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST substring_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
			Object c = null;
			Object d = null;
		
		
		AST __t141 = _t;
		AST tmp172_AST_in = (AST)_t;
		match(_t,SUBSTRING);
		_t = _t.getFirstChild();
		AST tmp173_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp174_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getNextSibling();
		b=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp175_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			c=expr(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case COMMA:
			{
				AST tmp176_AST_in = (AST)_t;
				match(_t,COMMA);
				_t = _t.getNextSibling();
				d=expr(_t);
				_t = _retTree;
				break;
			}
			case RIGHTPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp177_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t141;
		_t = _t.getNextSibling();
			if (d!=null)
						throw new ProEvalException("Type option of STRING function is not yet supported.");
					ret = substring(a, b, c);
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  time_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST time_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST tmp178_AST_in = (AST)_t;
		match(_t,TIME);
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("TIME function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  today_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST today_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST tmp179_AST_in = (AST)_t;
		match(_t,TODAY);
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("TODAY function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  trim_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST trim_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b = null;
		
		
		AST __t147 = _t;
		AST tmp180_AST_in = (AST)_t;
		match(_t,TRIM);
		_t = _t.getFirstChild();
		AST tmp181_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COMMA:
		{
			AST tmp182_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			b=expr(_t);
			_t = _retTree;
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		AST tmp183_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t147;
		_t = _t.getNextSibling();
			String s = getString(a);
					if (b != null)
						ret = StringFuncs.trim(s, getString(b));
					else
						ret = s.trim();
				
		_retTree = _t;
		return ret;
	}
	
	public final Object  truncate_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST truncate_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
			Object b;
		
		
		AST __t150 = _t;
		AST tmp184_AST_in = (AST)_t;
		match(_t,TRUNCATE);
		_t = _t.getFirstChild();
		AST tmp185_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp186_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getNextSibling();
		b=expr(_t);
		_t = _retTree;
		AST tmp187_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t150;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("TRUNCATE function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  weekday_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST weekday_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t152 = _t;
		AST tmp188_AST_in = (AST)_t;
		match(_t,WEEKDAY);
		_t = _t.getFirstChild();
		AST tmp189_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp190_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t152;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("WEEKDAY function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	public final Object  year_fun(AST _t) throws RecognitionException {
		Object ret;
		
		AST year_fun_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			Object a;
		
		
		AST __t154 = _t;
		AST tmp191_AST_in = (AST)_t;
		match(_t,YEAR);
		_t = _t.getFirstChild();
		AST tmp192_AST_in = (AST)_t;
		match(_t,LEFTPAREN);
		_t = _t.getNextSibling();
		a=expr(_t);
		_t = _retTree;
		AST tmp193_AST_in = (AST)_t;
		match(_t,RIGHTPAREN);
		_t = _t.getNextSibling();
		_t = __t154;
		_t = _t.getNextSibling();
		if(true) throw new ProEvalException("YEAR function not yet supported.");
		_retTree = _t;
		return ret;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"LEXDATE",
		"NAMEDOT",
		"NUMBER",
		"OBJCOLON",
		"QSTRING",
		"PERIOD",
		"PROPARSEDIRECTIVE",
		"LEXOTHER",
		"AACBIT",
		"AACONTROL",
		"AALIST",
		"AAMEMORY",
		"AAMSG",
		"AAPCONTROL",
		"AASERIAL",
		"AATRACE",
		"ABSOLUTE",
		"ACCELERATOR",
		"ACCUMULATE",
		"ACTIVEWINDOW",
		"ADD",
		"ADVISE",
		"ALERTBOX",
		"ALIAS",
		"ALL",
		"ALLOWREPLICATION",
		"ALTER",
		"ALTERNATEKEY",
		"AMBIGUOUS",
		"ANALYZE",
		"AND",
		"ANSIONLY",
		"ANY",
		"ANYWHERE",
		"APPEND",
		"APPLICATION",
		"APPLY",
		"ARRAYMESSAGE",
		"AS",
		"ASC",
		"ASCENDING",
		"ASKOVERWRITE",
		"ASSIGN",
		"ASYNCHRONOUS",
		"AT",
		"ATTACHMENT",
		"ATTRSPACE",
		"AUTHORIZATION",
		"AUTOCOMPLETION",
		"AUTOENDKEY",
		"AUTOGO",
		"AUTOMATIC",
		"AUTORETURN",
		"AVAILABLE",
		"AVERAGE",
		"AVG",
		"BACKGROUND",
		"BACKWARDS",
		"BASEKEY",
		"BEFOREHIDE",
		"BEGINS",
		"BELL",
		"BETWEEN",
		"BGCOLOR",
		"BIGENDIAN",
		"BINARY",
		"BINDWHERE",
		"BLANK",
		"BOTH",
		"BOTTOM",
		"BREAK",
		"BROWSE",
		"BTOS",
		"BUFFER",
		"BUFFERCHARS",
		"BUFFERCOMPARE",
		"BUFFERCOPY",
		"BUFFERLINES",
		"BUFFERNAME",
		"BUTTON",
		"BUTTONS",
		"BY",
		"BYPOINTER",
		"BYTE",
		"BYVARIANTPOINTER",
		"CACHE",
		"CACHESIZE",
		"CALL",
		"CANDO",
		"CANFIND",
		"CANQUERY",
		"CANSET",
		"CANCELBUTTON",
		"CAPS",
		"CASE",
		"CASESENSITIVE",
		"CDECL_KW",
		"CENTERED",
		"CHAINED",
		"CHARACTER",
		"CHARACTERLENGTH",
		"CHECK",
		"CHOOSE",
		"CHR",
		"CLEAR",
		"CLIPBOARD",
		"CLOSE",
		"CODEBASELOCATOR",
		"CODEPAGECONVERT",
		"COLLATE",
		"COLOF",
		"COLON",
		"COLONALIGNED",
		"COLOR",
		"COLORTABLE",
		"COLUMN",
		"COLUMNBGCOLOR",
		"COLUMNDCOLOR",
		"COLUMNFGCOLOR",
		"COLUMNFONT",
		"COLUMNLABEL",
		"COLUMNOF",
		"COLUMNPFCOLOR",
		"COLUMNS",
		"COMHANDLE",
		"COMBOBOX",
		"COMMAND",
		"COMPARE",
		"COMPARES",
		"COMPLETE",
		"COMPILE",
		"COMPILER",
		"COMSELF",
		"CONFIGNAME",
		"CONNECT",
		"CONNECTED",
		"CONTAINS",
		"CONTENTS",
		"CONTEXT",
		"CONTEXTHELP",
		"CONTEXTHELPFILE",
		"CONTEXTHELPID",
		"CONTEXTPOPUP",
		"CONTROL",
		"CONTROLFRAME",
		"CONVERT",
		"CONVERT3DCOLORS",
		"COUNT",
		"COUNTOF",
		"CREATE",
		"CREATETESTFILE",
		"CURRENT",
		"CURRENTCHANGED",
		"CURRENTENVIRONMENT",
		"CURRENTLANGUAGE",
		"CURRENTRESULTROW",
		"CURRENTVALUE",
		"CURRENTWINDOW",
		"CURSOR",
		"DATABASE",
		"DATABIND",
		"DATASERVERS",
		"DATE",
		"DAY",
		"DBCODEPAGE",
		"DBCOLLATION",
		"DBIMS",
		"DBNAME",
		"DBPARAM",
		"DBRESTRICTIONS",
		"DBTASKID",
		"DBTYPE",
		"DBVERSION",
		"DCOLOR",
		"DDE",
		"DEBLANK",
		"DEBUG",
		"DEBUGLIST",
		"DEBUGGER",
		"DECIMAL",
		"DECIMALS",
		"DECLARE",
		"DEFAULT",
		"DEFAULTBUTTON",
		"DEFAULTEXTENSION",
		"DEFAULTNOXLATE",
		"DEFAULTWINDOW",
		"DEFERLOBFETCH",
		"DEFINE",
		"DEFINED",
		"DELETE_KW",
		"<195>",
		"DELETERESULTLISTENTRY",
		"DELIMITER",
		"DESC",
		"DESCENDING",
		"DESELECTION",
		"DIALOGBOX",
		"DIALOGHELP",
		"DICTIONARY",
		"DIR",
		"DISABLE",
		"DISABLEAUTOZAP",
		"DISABLED",
		"DISCONNECT",
		"DISPLAY",
		"DISTINCT",
		"DO",
		"DOS",
		"DOUBLE",
		"DOWN",
		"DROP",
		"DROPDOWN",
		"DROPDOWNLIST",
		"DROPFILENOTIFY",
		"DROPTARGET",
		"DUMP",
		"DYNAMIC",
		"DYNAMICFUNCTION",
		"EACH",
		"ECHO",
		"EDGECHARS",
		"EDGEPIXELS",
		"EDITUNDO",
		"EDITING",
		"EDITOR",
		"ELSE",
		"EMPTY",
		"ENABLE",
		"ENCODE",
		"END",
		"ENDMOVE",
		"ENDRESIZE",
		"ENDROWRESIZE",
		"ENDKEY",
		"ENTERED",
		"ENTRY",
		"EQ",
		"ERROR",
		"ERRORSTATUS",
		"ESCAPE",
		"ETIME_KW",
		"EVENTPROCEDURE",
		"EVENTS",
		"EXCEPT",
		"EXCLUSIVEID",
		"EXCLUSIVELOCK",
		"EXCLUSIVEWEBUSER",
		"EXECUTE",
		"EXISTS",
		"EXP",
		"EXPAND",
		"EXPANDABLE",
		"EXPLICIT",
		"EXPORT",
		"EXTENDED",
		"EXTENT",
		"EXTERNAL",
		"FALSE_KW",
		"FETCH",
		"FGCOLOR",
		"FIELD",
		"FIELDS",
		"FILE",
		"FILEINFORMATION",
		"FILENAME",
		"FILL",
		"FILLIN",
		"FILTERS",
		"FIND",
		"FINDCASESENSITIVE",
		"FINDER",
		"FINDGLOBAL",
		"FINDNEXTOCCURRENCE",
		"FINDPREVOCCURRENCE",
		"FINDSELECT",
		"FINDWRAPAROUND",
		"FIRST",
		"FIRSTOF",
		"FITLASTCOLUMN",
		"FIXEDONLY",
		"FLATBUTTON",
		"FLOAT",
		"FOCUS",
		"FONT",
		"FONTBASEDLAYOUT",
		"FONTTABLE",
		"FOR",
		"FORCEFILE",
		"FORMINPUT",
		"FORMAT",
		"FORWARDS",
		"FRAME",
		"FRAMECOL",
		"FRAMEDB",
		"FRAMEDOWN",
		"FRAMEFIELD",
		"FRAMEFILE",
		"FRAMEINDEX",
		"FRAMELINE",
		"FRAMENAME",
		"FRAMEROW",
		"FRAMEVALUE",
		"FREQUENCY",
		"FROM",
		"FROMCURRENT",
		"FUNCTION",
		"GE",
		"GENERATEMD5",
		"GET",
		"GETBITS",
		"GETBUFFERHANDLE",
		"GETBYTE",
		"GETBYTES",
		"GETBYTEORDER",
		"GETCGILIST",
		"GETCGIVALUE",
		"GETCODEPAGES",
		"GETCOLLATIONS",
		"GETCONFIGVALUE",
		"GETDOUBLE",
		"GETFILE",
		"GETFLOAT",
		"GETKEYVALUE",
		"GETLICENSE",
		"GETLONG",
		"GETPOINTERVALUE",
		"GETSHORT",
		"GETSIZE",
		"GETSTRING",
		"GETUNSIGNEDSHORT",
		"GLOBAL",
		"GOON",
		"GOPENDING",
		"GRANT",
		"GRAPHICEDGE",
		"GROUP",
		"GTHAN",
		"HANDLE",
		"HAVING",
		"HEADER",
		"HEIGHT",
		"HELP",
		"HELPTOPIC",
		"HIDE",
		"HINT",
		"HORIZONTAL",
		"HOSTBYTEORDER",
		"HTMLENDOFLINE",
		"HTMLFRAMEBEGIN",
		"HTMLFRAMEEND",
		"HTMLHEADERBEGIN",
		"HTMLHEADEREND",
		"HTMLTITLEBEGIN",
		"HTMLTITLEEND",
		"IF",
		"IMAGE",
		"IMAGEDOWN",
		"IMAGEINSENSITIVE",
		"IMAGESIZE",
		"IMAGESIZECHARS",
		"IMAGESIZEPIXELS",
		"IMAGEUP",
		"IMPORT",
		"IN_KW",
		"INCREMENTEXCLUSIVEID",
		"INDEX",
		"INDEXHINT",
		"INDEXEDREPOSITION",
		"INDICATOR",
		"INFORMATION",
		"INITIAL",
		"INITIALDIR",
		"INITIALFILTER",
		"INITIATE",
		"INNER",
		"INNERCHARS",
		"INNERLINES",
		"INPUT",
		"INPUTOUTPUT",
		"INSERT",
		"INTEGER",
		"INTO",
		"IS",
		"ISATTRSPACE",
		"ISLEADBYTE",
		"ITEM",
		"JOIN",
		"JOINBYSQLDB",
		"KBLABEL",
		"KEEPMESSAGES",
		"KEEPTABORDER",
		"KEY",
		"KEYCODE",
		"KEYFUNCTION",
		"KEYLABEL",
		"KEYS",
		"KEYWORD",
		"KEYWORDALL",
		"LABEL",
		"LABELBGCOLOR",
		"LABELDCOLOR",
		"LABELFGCOLOR",
		"LABELFONT",
		"LANDSCAPE",
		"LANGUAGES",
		"LARGE",
		"LARGETOSMALL",
		"LAST",
		"LASTEVENT",
		"LASTOF",
		"LASTKEY",
		"LC",
		"LDBNAME",
		"LE",
		"LEAVE",
		"LEFT",
		"LEFTALIGNED",
		"LEFTTRIM",
		"LENGTH",
		"LIBRARY",
		"LIKE",
		"LINECOUNTER",
		"LISTEVENTS",
		"LISTITEMPAIRS",
		"LISTITEMS",
		"LISTQUERYATTRS",
		"LISTSETATTRS",
		"LISTWIDGETS",
		"LISTING",
		"LITTLEENDIAN",
		"LOAD",
		"LOADPICTURE",
		"LOCKED",
		"LOG",
		"LOGICAL",
		"LONG",
		"LOOKAHEAD",
		"LOOKUP",
		"LTHAN",
		"MACHINECLASS",
		"MAP",
		"MARGINEXTRA",
		"MATCHES",
		"MAX",
		"MAXCHARS",
		"MAXROWS",
		"MAXSIZE",
		"MAXVALUE",
		"MAXIMIZE",
		"MAXIMUM",
		"MEMBER",
		"MEMPTR",
		"MENU",
		"MENUITEM",
		"MENUBAR",
		"MESSAGE",
		"MESSAGELINE",
		"MESSAGELINES",
		"MIN",
		"MINSIZE",
		"MINVALUE",
		"MINIMUM",
		"MODULO",
		"MONTH",
		"MOUSE",
		"MOUSEPOINTER",
		"MPE",
		"MULTIPLE",
		"MULTIPLEKEY",
		"MUSTEXIST",
		"NATIVE",
		"NE",
		"NEW",
		"NEXT",
		"NEXTPROMPT",
		"NEXTVALUE",
		"NO",
		"NOAPPLY",
		"NOARRAYMESSAGE",
		"NOASSIGN",
		"NOATTRLIST",
		"NOATTRSPACE",
		"NOAUTOVALIDATE",
		"NOBINDWHERE",
		"NOBOX",
		"NOCOLUMNSCROLLING",
		"NOCONSOLE",
		"NOCONVERT",
		"NOCONVERT3DCOLORS",
		"NOCURRENTVALUE",
		"NODEBUG",
		"NODRAG",
		"NOECHO",
		"NOEMPTYSPACE",
		"NOERROR_KW",
		"NOFILL",
		"NOFOCUS",
		"NOHELP",
		"NOHIDE",
		"NOINDEXHINT",
		"NOJOINBYSQLDB",
		"NOLABELS",
		"NOLOCK",
		"NOLOOKAHEAD",
		"NOMAP",
		"NOMESSAGE",
		"NONE",
		"NOPAUSE",
		"NOPREFETCH",
		"NORETURNVALUE",
		"NORMAL",
		"NOROWMARKERS",
		"NOSCROLLBARVERTICAL",
		"NOSEPARATECONNECTION",
		"NOSEPARATORS",
		"NOTABSTOP",
		"NOUNDERLINE",
		"NOUNDO",
		"NOVALIDATE",
		"NOWAIT",
		"NOWORDWRAP",
		"NOT",
		"NULL_KW",
		"NUMALIASES",
		"NUMCOPIES",
		"NUMDBS",
		"NUMENTRIES",
		"NUMRESULTS",
		"NUMERIC",
		"OBJECT",
		"OCTETLENGTH",
		"OF",
		"OFF",
		"OK",
		"OKCANCEL",
		"OLD",
		"ON",
		"ONLY",
		"OPEN",
		"OPSYS",
		"OPTION",
		"OR",
		"ORDER",
		"ORDEREDJOIN",
		"ORDINAL",
		"OS2",
		"OS400",
		"OSAPPEND",
		"OSCOMMAND",
		"OSCOPY",
		"OSCREATEDIR",
		"OSDELETE",
		"OSDIR",
		"OSDRIVES",
		"OSERROR",
		"OSGETENV",
		"OSRENAME",
		"OTHERWISE",
		"OUTER",
		"OUTERJOIN",
		"OUTPUT",
		"OVERLAY",
		"OVERRIDE",
		"PAGE",
		"PAGEBOTTOM",
		"PAGENUMBER",
		"PAGESIZE_KW",
		"PAGETOP",
		"PAGEWIDTH",
		"PAGED",
		"PARAMETER",
		"PARENT",
		"PARTIALKEY",
		"PASCAL_KW",
		"PAUSE",
		"PDBNAME",
		"PERFORMANCE",
		"PERSISTENT",
		"PFCOLOR",
		"PINNABLE",
		"PORTRAIT",
		"POSITION",
		"PRECISION",
		"PREPROCESS",
		"PRESELECT",
		"PREV",
		"PRIMARY",
		"PRINTER",
		"PRINTERSETUP",
		"PRIVATE",
		"PRIVILEGES",
		"PROCTEXT",
		"PROCTEXTBUFFER",
		"PROCHANDLE",
		"PROCSTATUS",
		"PROCEDURE",
		"PROCESS",
		"PROFILER",
		"PROGRAMNAME",
		"PROGRESS",
		"PROMPT",
		"PROMPTFOR",
		"PROMSGS",
		"PROPATH",
		"PROVERSION",
		"PUBLIC",
		"PUBLISH",
		"PUT",
		"PUTBITS",
		"PUTBYTE",
		"PUTBYTES",
		"PUTDOUBLE",
		"PUTFLOAT",
		"PUTKEYVALUE",
		"PUTLONG",
		"PUTSHORT",
		"PUTSTRING",
		"PUTUNSIGNEDSHORT",
		"QUERY",
		"QUERYCLOSE",
		"QUERYOFFEND",
		"QUERYTUNING",
		"QUESTION",
		"QUIT",
		"QUOTER",
		"RINDEX",
		"RADIOBUTTONS",
		"RADIOSET",
		"RANDOM",
		"RAW",
		"RAWTRANSFER",
		"RCODEINFORMATION",
		"READ",
		"READAVAILABLE",
		"READEXACTNUM",
		"READONLY",
		"READKEY",
		"REAL",
		"RECID",
		"RECORDLENGTH",
		"RECTANGLE",
		"RECURSIVE",
		"RELEASE",
		"REPEAT",
		"REPLACE",
		"REPLICATIONCREATE",
		"REPLICATIONDELETE",
		"REPLICATIONWRITE",
		"REPOSITION",
		"REPOSITIONFORWARD",
		"REPOSITIONBACKWARD",
		"REPOSITIONTOROW",
		"REPOSITIONTOROWID",
		"REQUEST",
		"RESULT",
		"RETAIN",
		"RETAINSHAPE",
		"RETRY",
		"RETRYCANCEL",
		"RETURN",
		"RETURNTOSTARTDIR",
		"RETURNVALUE",
		"RETURNS",
		"REVERSEFROM",
		"REVERT",
		"REVOKE",
		"RGBVALUE",
		"RIGHT",
		"RIGHTALIGNED",
		"RIGHTTRIM",
		"ROUND",
		"ROW",
		"ROWHEIGHTCHARS",
		"ROWHEIGHTPIXELS",
		"ROWID",
		"ROWOF",
		"RULE",
		"RUN",
		"RUNPROCEDURE",
		"SAVE",
		"SAVECACHE",
		"SAVEAS",
		"SAXREADER",
		"SCHEMA",
		"SCREEN",
		"SCREENIO",
		"SCREENLINES",
		"SCROLL",
		"SCROLLABLE",
		"SCROLLBARHORIZONTAL",
		"SCROLLBARVERTICAL",
		"SCROLLING",
		"SDBNAME",
		"SEARCH",
		"SEARCHSELF",
		"SEARCHTARGET",
		"SECTION",
		"SEEK",
		"SELECT",
		"SELECTION",
		"SELECTIONLIST",
		"SELF",
		"SEND",
		"SENDSQLSTATEMENT",
		"SEPARATECONNECTION",
		"SEPARATORS",
		"SERVER",
		"SERVERSOCKET",
		"SESSION",
		"SET",
		"SETBYTEORDER",
		"SETCONTENTS",
		"SETCURRENTVALUE",
		"SETPOINTERVALUE",
		"SETSIZE",
		"SETUSERID",
		"SHARELOCK",
		"SHARED",
		"SHOWSTATS",
		"SIDELABELS",
		"SILENT",
		"SIMPLE",
		"SINGLE",
		"SIZE",
		"SIZECHARS",
		"SIZEPIXELS",
		"SKIP",
		"SKIPDELETEDRECORD",
		"SLIDER",
		"SMALLINT",
		"SOCKET",
		"SOME",
		"SORT",
		"SOURCE",
		"SOURCEPROCEDURE",
		"SPACE",
		"SQL",
		"SQRT",
		"START",
		"STARTMOVE",
		"STARTRESIZE",
		"STARTROWRESIZE",
		"STATUS",
		"STATUSBAR",
		"STDCALL_KW",
		"STRETCHTOFIT",
		"STOP",
		"STOREDPROCEDURE",
		"STREAM",
		"STREAMIO",
		"STRING",
		"STRINGXREF",
		"SUBAVERAGE",
		"SUBCOUNT",
		"SUBMAXIMUM",
		"SUBMENU",
		"SUBMENUHELP",
		"SUBMINIMUM",
		"SUBTOTAL",
		"SUBSCRIBE",
		"SUBSTITUTE",
		"SUBSTRING",
		"SUM",
		"SUMMARY",
		"SUPER",
		"SYSTEMDIALOG",
		"SYSTEMHELP",
		"TABLE",
		"TABLEHANDLE",
		"TABLENUMBER",
		"TARGET",
		"TARGETPROCEDURE",
		"TEMPTABLE",
		"TERMINAL",
		"TERMINATE",
		"TEXT",
		"TEXTCURSOR",
		"TEXTSEGGROW",
		"THEN",
		"THISPROCEDURE",
		"THREED",
		"THROUGH",
		"TICMARKS",
		"TIME",
		"TITLE",
		"TO",
		"TOOLBAR",
		"TOOLTIP",
		"TOROWID",
		"TODAY",
		"TOGGLEBOX",
		"TOP",
		"TOPONLY",
		"TOPIC",
		"TOTAL",
		"TRANSACTION",
		"TRANSACTIONMODE",
		"TRANSPARENT",
		"TRAILING",
		"TRIGGER",
		"TRIGGERS",
		"TRIM",
		"TRUE_KW",
		"TRUNCATE",
		"UNBUFFERED",
		"UNDERLINE",
		"UNDO",
		"UNFORMATTED",
		"UNION",
		"UNIQUE",
		"UNIQUEMATCH",
		"UNIX",
		"UNLESSHIDDEN",
		"UNLOAD",
		"UNSUBSCRIBE",
		"UP",
		"UPDATE",
		"URLDECODE",
		"URLENCODE",
		"USE",
		"USEDICTEXPS",
		"USEFILENAME",
		"USEINDEX",
		"USER",
		"USERID",
		"USEREVVIDEO",
		"USETEXT",
		"USEUNDERLINE",
		"USING",
		"V6FRAME",
		"VALIDEVENT",
		"VALIDHANDLE",
		"VALIDATE",
		"VALUE",
		"VALUECHANGED",
		"VALUES",
		"VARIABLE",
		"VERBOSE",
		"VERTICAL",
		"VIEW",
		"VIEWAS",
		"VMS",
		"WAIT",
		"WAITFOR",
		"WARNING",
		"WEBCONTEXT",
		"WEEKDAY",
		"WHEN",
		"WHERE",
		"WHILE",
		"WIDGET",
		"WIDGETHANDLE",
		"WIDGETPOOL",
		"WIDTH",
		"WIDTHCHARS",
		"WIDTHPIXELS",
		"WINDOW",
		"WINDOWDELAYEDMINIMIZE",
		"WINDOWMAXIMIZED",
		"WINDOWMINIMIZED",
		"WINDOWNAME",
		"WINDOWNORMAL",
		"WITH",
		"WORDINDEX",
		"WORKTABLE",
		"WRITE",
		"X",
		"XDOCUMENT",
		"XNODEREF",
		"XOF",
		"XCODE",
		"XREF",
		"Y",
		"YOF",
		"YEAR",
		"YES",
		"YESNO",
		"YESNOCANCEL",
		"LEFTANGLE",
		"RIGHTANGLE",
		"LEXAT",
		"LEFTBRACE",
		"RIGHTBRACE",
		"CARET",
		"COMMA",
		"EXCLAMATION",
		"GTOREQUAL",
		"GTORLT",
		"LTOREQUAL",
		"EQUAL",
		"MINUS",
		"PLUS",
		"LEFTPAREN",
		"RIGHTPAREN",
		"SEMI",
		"SLASH",
		"STAR",
		"SINGLEQUOTE",
		"UNKNOWNVALUE",
		"PIPE",
		"BACKTICK",
		"WS",
		"COMMENT",
		"DQSTRING",
		"SQSTRING",
		"DIGITSTART",
		"PLUSMINUSSTART",
		"PERIODSTART",
		"ID",
		"ID_TWO",
		"ID_THREE",
		"ESCAPED_QUOTE",
		"LEXCOLON",
		"PREPROCESSDIRECTIVE",
		"GLOBALDEFINE",
		"SCOPEDDEFINE",
		"PREPROCESSIF",
		"PREPROCESSELSEIF",
		"PREPROCESSELSE",
		"PREPROCESSENDIF",
		"IFCOND",
		"PREPROCESSUNDEFINE",
		"PREPROCESSMESSAGE",
		"PREPROCESSJMESSAGE",
		"DEFINETEXT",
		"PREPROCESSTOKEN",
		"INCLUDEREFARG",
		"DIGITS",
		"AMPANALYZESUSPEND",
		"AMPANALYZERESUME",
		"AMPGLOBALDEFINE",
		"AMPELSE",
		"AMPELSEIF",
		"AMPENDIF",
		"AMPIF",
		"AMPMESSAGE",
		"AMPTHEN",
		"AMPUNDEFINE",
		"AMPSCOPEDDEFINE",
		"Scanner_head",
		"Scanner_tail",
		"BACKSLASH",
		"COMMENTSTART",
		"COMMENTEND",
		"LEFTCURLY",
		"RIGHTCURLY",
		"CURLYAMP",
		"CURLYNUMBER",
		"CURLYSTAR",
		"DOUBLEQUOTE",
		"TILDE",
		"NEWLINE",
		"FREECHAR",
		"Aggregate_phrase",
		"Array_subscript",
		"Assign_from_buffer",
		"Automationobject",
		"Block_iterator",
		"Code_block",
		"Entered_func",
		"Editing_phrase",
		"Expr_statement",
		"Event_list",
		"Field_list",
		"Field_ref",
		"Form_item",
		"Format_phrase",
		"Inline_definition",
		"Loose_End_Keeper",
		"Method_parameter",
		"Method_param_list",
		"Not_casesens",
		"Not_null",
		"Parameter_list",
		"Program_root",
		"Program_tail",
		"Sql_begins",
		"Sql_between",
		"Sql_comp_query",
		"Sql_in",
		"Sql_like",
		"Sql_null_test",
		"Sql_select_what",
		"Widget_ref",
		"With_columns",
		"With_down",
		"UNARY_MINUS",
		"UNARY_PLUS",
		"MULTIPLY",
		"DIVIDE",
		"BLOCK_LABEL",
		"RECORD_NAME",
		"USER_FUNC",
		"TYPELESS_TOKEN",
		"IMPOSSIBLE_TOKEN",
		"<1000>",
		"GATEWAYS",
		"FUNCTIONCALLTYPE",
		"GETATTRCALLTYPE",
		"PROCEDURECALLTYPE",
		"SAXCOMPLETE",
		"SAXPARSERERROR",
		"SAXRUNNING",
		"SAXUNINITIALIZED",
		"SETATTRCALLTYPE",
		"UNQUOTEDSTRING",
		"COPYLOB",
		"DATARELATION",
		"DATASOURCE",
		"DATASET",
		"DATASETHANDLE",
		"LOGMANAGER",
		"NOLOBS",
		"NOW",
		"STARTING",
		"BASE64",
		"SOAPHEADER",
		"SOAPHEADERENTRYREF",
		"BLOB",
		"CLOB",
		"DATETIME",
		"DATETIMETZ",
		"LONGCHAR",
		"RELATIONFIELDS",
		"TTCODEPAGE",
		"COLUMNCODEPAGE",
		"DYNAMICCURRENTVALUE",
		"DYNAMICNEXTVALUE",
		"FIXCODEPAGE",
		"INTERVAL",
		"ISCODEPAGEFIXED",
		"ISCOLUMNCODEPAGE",
		"ISODATE",
		"MTIME",
		"LOBDIR",
		"TIMEZONE",
		"BYVALUE",
		"BYREFERENCE",
		"ADDINTERVAL",
		"GETDIR",
		"CURRENCY",
		"ERRORCODE",
		"IUNKNOWN",
		"SHORT",
		"UNSIGNEDBYTE",
		"UNSIGNEDSHORT",
		"CODEPAGE",
		"BASE64DECODE",
		"BASE64ENCODE",
		"BATCHSIZE",
		"BEFORETABLE",
		"COPYDATASET",
		"COPYTEMPTABLE",
		"DATASOURCEMODIFIED",
		"DECRYPT",
		"DELETECHARACTER",
		"ENABLEDFIELDS",
		"ENCRYPT",
		"ENCRYPTIONSALT",
		"<1064>",
		"FORMLONGINPUT",
		"GENERATEPBEKEY",
		"GENERATEPBESALT",
		"GENERATERANDOMKEY",
		"GETCGILONGVALUE",
		"LASTBATCH",
		"MD5DIGEST",
		"MERGEBYFIELD",
		"NORMALIZE",
		"PBEHASHALGORITHM",
		"PBEKEYROUNDS",
		"PREFERDATASET",
		"REJECTED",
		"REPOSITIONMODE",
		"ROWSTATE",
		"ROWUNMODIFIED",
		"ROWDELETED",
		"ROWMODIFIED",
		"ROWCREATED",
		"SECURITYPOLICY",
		"SHA1DIGEST",
		"SSLSERVERNAME",
		"SYMMETRICENCRYPTIONALGORITHM",
		"SYMMETRICENCRYPTIONIV",
		"SYMMETRICENCRYPTIONKEY",
		"SYMMETRICSUPPORT",
		"TRANSINITPROCEDURE",
		"BIGINT",
		"TIMESTAMP",
		"FIXCHAR",
		"DOT_COMMENT",
		"AUDITCONTROL",
		"AUDITENABLED",
		"AUDITPOLICY",
		"BIND",
		"CAST",
		"CLASS",
		"CLIENTPRINCIPAL",
		"CONSTRUCTOR",
		"FINAL",
		"GENERATEUUID",
		"GUID",
		"HEXDECODE",
		"HEXENCODE",
		"IMPLEMENTS",
		"INHERITS",
		"INTERFACE",
		"METHOD",
		"NAMESPACEPREFIX",
		"NAMESPACEURI",
		"NEWINSTANCE",
		"PROTECTED",
		"REFERENCEONLY",
		"SAXWRITER",
		"SETDBCLIENT",
		"THISOBJECT",
		"TYPEOF",
		"VALIDOBJECT",
		"XMLDATATYPE",
		"XMLNODETYPE",
		"TYPE_NAME",
		"WIDGETID",
		"DESTRUCTOR",
		"VOID",
		"LOCAL_METHOD_REF",
		"ANNOTATION",
		"DOUBLECOLON",
		"NESTED",
		"PASSWORDFIELD",
		"ROUNDED",
		"GROUPBOX",
		"<1136>",
		"FALSELEAKS",
		"LEAKDETECTION",
		"SAXWRITEBEGIN",
		"SAXWRITECOMPLETE",
		"SAXWRITECONTENT",
		"SAXWRITEELEMENT",
		"SAXWRITEERROR",
		"SAXWRITEIDLE",
		"SAXWRITETAG",
		"STOMPDETECTION",
		"STOMPFREQUENCY",
		"INT64",
		"PUTINT64",
		"GETINT64",
		"PUTUNSIGNEDLONG",
		"GETUNSIGNEDLONG",
		"PROPERTY",
		"SAXATTRIBUTES",
		"INHERITBGCOLOR",
		"NOINHERITBGCOLOR",
		"INHERITFGCOLOR",
		"NOINHERITFGCOLOR",
		"XREFXML",
		"Property_getter",
		"Property_setter",
		"USEWIDGETPOOL",
		"ACTIVEFORM",
		"ASSEMBLY",
		"CATCH",
		"CREATELIKESEQUENTIAL",
		"CURRENTQUERY",
		"DATASOURCEROWID",
		"DEFAULTVALUE",
		"ERRORSTACKTRACE",
		"FINALLY",
		"FIRSTFORM",
		"LASTFORM",
		"LIKESEQUENTIAL",
		"MARKNEW",
		"MARKROWSTATE",
		"MAXIMUMLEVEL",
		"NOTACTIVE",
		"RESTARTROW",
		"ROUTINELEVEL",
		"STATIC",
		"STREAMHANDLE",
		"THROW",
		"TOPNAVQUERY",
		"UNBOX",
		"BOX",
		"DBREMOTEHOST",
		"DYNAMICCAST",
		"XMLNODENAME",
		"ABSTRACT",
		"DELEGATE",
		"DYNAMICINVOKE",
		"DYNAMICNEW",
		"EVENT",
		"SERIALIZEHIDDEN",
		"SERIALIZENAME",
		"SIGNATURE",
		"STOPAFTER",
		"Assign_dynamic_new",
		"FOREIGNKEYHIDDEN",
		"BLOCKLEVEL",
		"SERIALIZABLE",
		"GETCLASS",
		"TABLESCAN",
		"MESSAGEDIGEST",
		"PARENTIDRELATION",
		"PARENTIDFIELD",
		"PARENTFIELDSBEFORE",
		"PARENTFIELDSAFTER",
		"ENUM",
		"FLAGS",
		"GETCODEPAGE",
		"HEIGHTCHARS",
		"HEIGHTPIXELS",
		"Last_Token_Number"
	};
	
	}
	
