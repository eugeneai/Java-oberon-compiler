// Generated from /home/eugeneai/IdeaProjects/Java-oberon-compiler/src/parser/Expr.g4 by ANTLR 4.7
package org.isu.oberon;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExprParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PLUS=1, MINUS=2, DIV=3, MUL=4, LPAR=5, RPAR=6, NUMBER=7, WS=8;
	public static final int
		RULE_expression = 0, RULE_pm = 1, RULE_mult = 2, RULE_md = 3, RULE_term = 4;
	public static final String[] ruleNames = {
		"expression", "pm", "mult", "md", "term"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'+'", "'-'", "'/'", "'*'", "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "PLUS", "MINUS", "DIV", "MUL", "LPAR", "RPAR", "NUMBER", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Expr.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ExprParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ExpressionContext extends ParserRuleContext {
		public int value;
		public MultContext mult;
		public PmContext pm;
		public List<MultContext> mult() {
			return getRuleContexts(MultContext.class);
		}
		public MultContext mult(int i) {
			return getRuleContext(MultContext.class,i);
		}
		public PmContext pm() {
			return getRuleContext(PmContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).exitExpression(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10);
			((ExpressionContext)_localctx).mult = mult();
			 int m1 = ((ExpressionContext)_localctx).mult.value; 
			setState(12);
			((ExpressionContext)_localctx).pm = pm();
			 int tt=((ExpressionContext)_localctx).pm.value; 
			setState(14);
			((ExpressionContext)_localctx).mult = mult();
			 ((ExpressionContext)_localctx).value =  ExprEvaluator.interp(m1, tt, ((ExpressionContext)_localctx).mult.value); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PmContext extends ParserRuleContext {
		public int value;
		public Token PLUS;
		public Token MINUS;
		public TerminalNode PLUS() { return getToken(ExprParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(ExprParser.MINUS, 0); }
		public PmContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).enterPm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).exitPm(this);
		}
	}

	public final PmContext pm() throws RecognitionException {
		PmContext _localctx = new PmContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_pm);
		try {
			setState(21);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
				enterOuterAlt(_localctx, 1);
				{
				setState(17);
				((PmContext)_localctx).PLUS = match(PLUS);
				 ((PmContext)_localctx).value =  (((PmContext)_localctx).PLUS!=null?((PmContext)_localctx).PLUS.getType():0); 
				}
				break;
			case MINUS:
				enterOuterAlt(_localctx, 2);
				{
				setState(19);
				((PmContext)_localctx).MINUS = match(MINUS);
				 ((PmContext)_localctx).value =  (((PmContext)_localctx).MINUS!=null?((PmContext)_localctx).MINUS.getType():0); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MultContext extends ParserRuleContext {
		public int value;
		public TermContext term;
		public MdContext md;
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public MdContext md() {
			return getRuleContext(MdContext.class,0);
		}
		public MultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mult; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).enterMult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).exitMult(this);
		}
	}

	public final MultContext mult() throws RecognitionException {
		MultContext _localctx = new MultContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_mult);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(23);
			((MultContext)_localctx).term = term();
			 int t1 = ((MultContext)_localctx).term.value; 
			setState(25);
			((MultContext)_localctx).md = md();
			 int tt=((MultContext)_localctx).md.value; 
			setState(27);
			((MultContext)_localctx).term = term();
			 ((MultContext)_localctx).value =  ExprEvaluator.interp(t1, tt, ((MultContext)_localctx).term.value); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MdContext extends ParserRuleContext {
		public int value;
		public Token MUL;
		public Token DIV;
		public TerminalNode MUL() { return getToken(ExprParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(ExprParser.DIV, 0); }
		public MdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_md; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).enterMd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).exitMd(this);
		}
	}

	public final MdContext md() throws RecognitionException {
		MdContext _localctx = new MdContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_md);
		try {
			setState(34);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MUL:
				enterOuterAlt(_localctx, 1);
				{
				setState(30);
				((MdContext)_localctx).MUL = match(MUL);
				 ((MdContext)_localctx).value =  (((MdContext)_localctx).MUL!=null?((MdContext)_localctx).MUL.getType():0); 
				}
				break;
			case DIV:
				enterOuterAlt(_localctx, 2);
				{
				setState(32);
				((MdContext)_localctx).DIV = match(DIV);
				 ((MdContext)_localctx).value =  (((MdContext)_localctx).DIV!=null?((MdContext)_localctx).DIV.getType():0); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public int value;
		public Token NUMBER;
		public ExpressionContext expression;
		public TerminalNode NUMBER() { return getToken(ExprParser.NUMBER, 0); }
		public TerminalNode LPAR() { return getToken(ExprParser.LPAR, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAR() { return getToken(ExprParser.RPAR, 0); }
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).exitTerm(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_term);
		try {
			setState(43);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(36);
				((TermContext)_localctx).NUMBER = match(NUMBER);
				 ((TermContext)_localctx).value =  (((TermContext)_localctx).NUMBER!=null?Integer.valueOf(((TermContext)_localctx).NUMBER.getText()):0); 
				}
				break;
			case LPAR:
				enterOuterAlt(_localctx, 2);
				{
				setState(38);
				match(LPAR);
				setState(39);
				((TermContext)_localctx).expression = expression();
				setState(40);
				match(RPAR);
				 ((TermContext)_localctx).value =  ((TermContext)_localctx).expression.value; 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\n\60\4\2\t\2\4\3"+
		"\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3"+
		"\3\5\3\30\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\5\5%\n\5\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\5\6.\n\6\3\6\2\2\7\2\4\6\b\n\2\2\2-\2\f\3\2\2"+
		"\2\4\27\3\2\2\2\6\31\3\2\2\2\b$\3\2\2\2\n-\3\2\2\2\f\r\5\6\4\2\r\16\b"+
		"\2\1\2\16\17\5\4\3\2\17\20\b\2\1\2\20\21\5\6\4\2\21\22\b\2\1\2\22\3\3"+
		"\2\2\2\23\24\7\3\2\2\24\30\b\3\1\2\25\26\7\4\2\2\26\30\b\3\1\2\27\23\3"+
		"\2\2\2\27\25\3\2\2\2\30\5\3\2\2\2\31\32\5\n\6\2\32\33\b\4\1\2\33\34\5"+
		"\b\5\2\34\35\b\4\1\2\35\36\5\n\6\2\36\37\b\4\1\2\37\7\3\2\2\2 !\7\6\2"+
		"\2!%\b\5\1\2\"#\7\5\2\2#%\b\5\1\2$ \3\2\2\2$\"\3\2\2\2%\t\3\2\2\2&\'\7"+
		"\t\2\2\'.\b\6\1\2()\7\7\2\2)*\5\2\2\2*+\7\b\2\2+,\b\6\1\2,.\3\2\2\2-&"+
		"\3\2\2\2-(\3\2\2\2.\13\3\2\2\2\5\27$-";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}