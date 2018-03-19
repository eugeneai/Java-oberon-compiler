// Generated from /home/eugeneai/IdeaProjects/Java-oberon-compiler/src/parser/Expr.g4 by ANTLR 4.7
package org.isu.oberon;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExprParser}.
 */
public interface ExprListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExprParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(ExprParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(ExprParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#pm}.
	 * @param ctx the parse tree
	 */
	void enterPm(ExprParser.PmContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#pm}.
	 * @param ctx the parse tree
	 */
	void exitPm(ExprParser.PmContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#mult}.
	 * @param ctx the parse tree
	 */
	void enterMult(ExprParser.MultContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#mult}.
	 * @param ctx the parse tree
	 */
	void exitMult(ExprParser.MultContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#md}.
	 * @param ctx the parse tree
	 */
	void enterMd(ExprParser.MdContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#md}.
	 * @param ctx the parse tree
	 */
	void exitMd(ExprParser.MdContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(ExprParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(ExprParser.TermContext ctx);
}