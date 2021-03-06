package couplet.c1;

import java.util.List;
import java.util.ListIterator;

import couplet.c1.ast.AddExpression;
import couplet.c1.ast.AndExpression;
import couplet.c1.ast.AssignmentStatement;
import couplet.c1.ast.Block;
import couplet.c1.ast.EqualExpression;
import couplet.c1.ast.Expression;
import couplet.c1.ast.GreaterEqualExpression;
import couplet.c1.ast.GreaterExpression;
import couplet.c1.ast.Identifier;
import couplet.c1.ast.IfStatement;
import couplet.c1.ast.InputStatement;
import couplet.c1.ast.IntegerLiteral;
import couplet.c1.ast.LessEqualExpression;
import couplet.c1.ast.LessExpression;
import couplet.c1.ast.Literal;
import couplet.c1.ast.Location;
import couplet.c1.ast.MulExpression;
import couplet.c1.ast.NegExpression;
import couplet.c1.ast.NotEqualExpression;
import couplet.c1.ast.NotExpression;
import couplet.c1.ast.OrExpression;
import couplet.c1.ast.OutputStatement;
import couplet.c1.ast.PosExpression;
import couplet.c1.ast.Program;
import couplet.c1.ast.Statement;
import couplet.c1.ast.StatementList;
import couplet.c1.ast.SubExpression;
import couplet.c1.ast.WhileStatement;
import couplet.c1.error.CompileException;
import couplet.c1.error.ErrorRecord;
import couplet.c1.token.Token;


public class LLParser implements couplet.Parser {

	/** 生成的AST根结点 */
	private couplet.AbstractSyntaxTree treeRoot;
	
	/** 输入的Token链 */
	private List<couplet.Token> tokenList;
	
	/** 用于从前向后遍历Token链的迭代器 */
	private ListIterator<couplet.Token> tokenIterator;
	
	/** 存放待分析的下一个Token */
	private couplet.Token nextToken;
	
	/** 存放nextToken所在的行 */
	private int line;
	
	/** 存放nextToken所在的列 */
	private int column;
	
	/** 存放nextToken的类型编码 */
	private int tokenType;
	
	/** 语法分析中的异常，存储错误记录 */
	private CompileException exception;

	public LLParser() {
		exception = new CompileException();
	}
	
	@Override
	public couplet.AbstractSyntaxTree getAST() {
		return treeRoot;
	}

	@Override
	public void setTokenList(List<couplet.Token> tokenList) {
		this.tokenList = tokenList;
	}

	@Override
	public void parse() throws CompileException {
		try	{
			tokenIterator = tokenList.listIterator();
			move();		
			treeRoot = program();
		} catch (CompileException e) {
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
			exception.add(ErrorRecord.parseError());
		} finally {
			if (exception.containError()) throw exception;
		}
	}
	
	/**
	 * 向后移动一个Token
	 */
	private void move() {
		if (tokenIterator.hasNext()) {
			// 获取下一个Token
			nextToken = tokenIterator.next();
			// 获取下一个Token所在的行列号
			line = nextToken.getLine();
			column = nextToken.getColumn();
			// 获取下一个Token的类型编码
			tokenType = nextToken.getTokenType();
		} else if (nextToken != null) {
			nextToken = null;
		} else {
			exception.add(ErrorRecord.incompleteProgram(line, column));
		}
	}
	
	
	/**
	 * 匹配某一指定的Token，并且向后移动
	 * @param tokenType 指定的Token类型代码
	 * @throws ErrorRecord
	 */
	private void match(int tokenType) {
		if (nextToken != null && nextToken.getTokenType() == tokenType) {
			move();
		} else {
			exception.add(ErrorRecord.missingToken(line, column, tokenType));
		}
	}
	
	/**
	 * 向下推导非终极符program
	 * <program> ::= <block>
	 * @return 程序的AST结点
	 * @throws ErrorRecord
	 */
	private Program program() throws CompileException {
		Program program =  new Program(block(), line, column);
		// 判断确实已经分析到Token链尾
		if (nextToken != null) {
			exception.add(ErrorRecord.redundantTokens(line, column));
		}
		return program;
	}
	
	/**
	 * 向下推导非终极符block
	 * <block> ::= "{" <stmt_list> "}"
	 * @return BLOCK语句的AST结点
	 * @throws ErrorRecord
	 */
	private Block block() {
		match(Token.L_BRACE);
		Block block = new Block(stmt_list(), line, column);
		match(Token.R_BRACE);
		return block;
	}
	
	/**
	 * 向下推导非终极符stmt_list
	 * <stmt_list> ::= <stmt_list> <stmt> | ε
	 * @return 语句列表的AST结点
	 * @throws ErrorRecord
	 */
	private StatementList stmt_list() {
		StatementList stmt_list = new StatementList(line, column);
		while (true) {
			while (tokenType == Token.L_BRACE || tokenType == Token.ID || 
					tokenType == Token.IF || tokenType == Token.WHILE || 
					tokenType == Token.INPUT || tokenType == Token.OUTPUT) {
				stmt_list.add(stmt());
			}
			// 跳过一些字符，直到遇到"}"或者某个语句的开始字符
			while (tokenType != Token.R_BRACE 
					&& tokenType != Token.L_BRACE && tokenType != Token.ID 
					&& tokenType != Token.IF && tokenType != Token.WHILE 
					&& tokenType != Token.INPUT	&& tokenType != Token.OUTPUT) {
				exception.add(ErrorRecord.unexpectedToken(line, column, nextToken.getLexeme()));
				move();
			}
			if (tokenType == Token.R_BRACE) break;
		}
		return stmt_list;
	}
	
	/**
	 * 向下推导非终极符stmt
	 * <stmt> ::= <block> | <assign_stmt> | <if_stmt> | <while_stmt> | <input_stmt> | <output_stmt>
	 * @return 语句的AST结点
	 * @throws ErrorRecord
	 */
	private Statement stmt() {
		switch (tokenType) {
		case Token.L_BRACE :
			return block();
		case Token.ID :
			return assign_stmt();
		case Token.IF :
			return if_stmt();
		case Token.WHILE :
			return while_stmt();
		case Token.INPUT :			
			return input_stmt();
		case Token.OUTPUT :
			return output_stmt();
		default:
			exception.add(ErrorRecord.wrongStatement(line, column));
			moveStmtFollow();
			return null;
		}
	}
	
	/**
	 * 向下推导非终极符assign_stmt
	 * <assign_stmt> ::= <location> "=" <expr> ";"
	 * @return 赋值语句的AST结点
	 * @throws ErrorRecord
	 */
	private AssignmentStatement assign_stmt() {
		Location location = location();
		match(Token.EQUAL);
		AssignmentStatement stmt = new AssignmentStatement(location, expr(), line, column);
		match(Token.SEMI);
		return stmt;
	}
	
	/**
	 * 向下推导非终极符if_stmt
	 * "if" "(" <expr> ")" <stmt> | "if" "(" <expr> ")" <stmt> "else" <stmt>
	 * @return IF语句的AST结点
	 * @throws ErrorRecord
	 */
	private IfStatement if_stmt() {
		match(Token.IF);
		match(Token.L_PARENT);
		Expression expr = expr();
		match(Token.R_PARENT);
		Statement thenStmt = stmt();
		Statement elseStmt = null;
		if (tokenType == Token.ELSE) {
			match(Token.ELSE);
			elseStmt = stmt();
		}
		return new IfStatement(expr, thenStmt, elseStmt, line, column);		
	}
	
	/**
	 * 向下推导非终极符while_stmt
	 * <while_stmt> ::= "while" "(" <expr> ")" <stmt>
	 * @return WHILE语句的AST结点
	 * @throws ErrorRecord
	 */
	private WhileStatement while_stmt() {
		match(Token.WHILE);
		match(Token.L_PARENT);
		Expression expr = expr();
		match(Token.R_PARENT);
		Statement body = stmt();
		return new WhileStatement(expr, body, line, column);
	}

	/**
	 * 向下推导非终极符input_stmt
	 * <input_stmt> ::= "input" "(" <location> ")" ";"
	 * @return 输入语句的AST结点
	 * @throws ErrorRecord
	 */
	private InputStatement input_stmt() {
		match(Token.INPUT);
		match(Token.L_PARENT);
		Location location = location();
		match(Token.R_PARENT);
		match(Token.SEMI);
		return new InputStatement(location, line, column);
	}
	
	/**
	 * 向下推导非终极符output_stmt
	 * <output_stmt> ::= "output" "(" <expr> ")" ";"
	 * @return 输出语句的AST结点
	 * @throws ErrorRecord
	 */
	private OutputStatement output_stmt() {
		match(Token.OUTPUT);
		match(Token.L_PARENT);
		Expression expr = expr();
		match(Token.R_PARENT);
		match(Token.SEMI);
		return new OutputStatement(expr, line, column);
	}
	
	/**
	 * 向下推导非终极符expr
	 * <expr> ::= <expr> <bi_op_1> <expr_1> | <expr_1>
	 * @return 表达式的AST结点
	 * @throws ErrorRecord
	 */
	private Expression expr() {
		Expression expr = expr_1();
		while (tokenType == Token.OR_OR) {		
			move();
			expr = new OrExpression(expr, expr_1(), line, column); 
		}
		return expr;
	}
	
	/**
	 * 向下推导非终极符expr_1
	 * <expr_1> ::= <expr_1> <bi_op_2> <expr_2> | <expr_2>
	 * @return 表达式的AST结点
	 * @throws ErrorRecord
	 */	
	private Expression expr_1() {
		Expression expr;
		expr = expr_2();
		while (tokenType == Token.AND_AND) {	
			move();
			expr = new AndExpression(expr, expr_2(), line, column); 
		}
		return expr;
	}
	
	/**
	 * 向下推导非终极符expr_2
	 * <expr_2> ::= <expr_2> <bi_op_3> <expr_3> | <expr_3>
	 * @return 表达式的AST结点
	 * @throws ErrorRecord
	 */	
	private Expression expr_2() {
		Expression expr;
		expr = expr_3();
		boolean flag = true;
		while (flag) {
			switch (tokenType) {
			case Token.EQUAL_EQUAL :
				move();
				expr = new EqualExpression(expr, expr_3(), line, column); 
				break;
			case Token.NOT_EQUAL :
				move();
				expr = new NotEqualExpression(expr, expr_3(), line, column); 
				break;
			default :
				flag = false;
				break;	
			}			
		}
		return expr;
	}
	
	/**
	 * 向下推导非终极符expr_3
	 * <expr_3> ::= <expr_3> <bi_op_4> <expr_4> | <expr_4>
	 * @return 表达式的AST结点
	 * @throws ErrorRecord
	 */	
	private Expression expr_3() {
		Expression expr;
		expr = expr_4();
		boolean flag = true; 
		while (flag) {
			switch (tokenType) {
			case Token.LESS :
				move();
				expr = new LessExpression(expr, expr_4(), line, column);
				break;
			case Token.LESS_EQUAL :
				move();
				expr = new LessEqualExpression(expr, expr_4(), line, column);
				break;
			case Token.GREATER :
				move();
				expr = new GreaterExpression(expr, expr_4(), line, column);
				break;
			case Token.GREATER_EQUAL :
				move();
				expr = new GreaterEqualExpression(expr, expr_4(), line, column);
				break;
			default :
				flag = false;
				break;
			}			 
		}
		return expr;
	}
	
	/**
	 * 向下推导非终极符expr_4
	 * <expr_4> ::= <expr_4> <bi_op_5> <expr_5> | <expr_5>
	 * @return 表达式的AST结点
	 * @throws ErrorRecord
	 */	
	private Expression expr_4() {
		Expression expr;
		expr = expr_5();
		boolean flag = true;
		while (flag) {		
			switch (tokenType) {
			case Token.PLUS :
				move();
				expr = new AddExpression(expr, expr_5(), line, column); 
				break;
			case Token.MINUS :
				move();
				expr = new SubExpression(expr, expr_5(), line, column); 
				break;
			default :
				flag = false;
				break;
			}			
		}
		return expr;
	}
	
	/**
	 * 向下推导非终极符expr_5
	 * <expr_5> ::= <expr_5> <bi_op_6> <expr_6> | <expr_6>
	 * @return 表达式的AST结点
	 * @throws ErrorRecord
	 */	
	private Expression expr_5() {
		Expression expr;
		expr = expr_6();
		while (tokenType == Token.TIMES) {
			move();
			expr = new MulExpression(expr, expr_6(), line, column); 
		}
		return expr;
	}
	
	/**
	 * 向下推导非终极符expr_6
	 * <expr_6> ::= <un_op> <expr_7> | <expr_7>
	 * @return 表达式的AST结点
	 * @throws ErrorRecord
	 */	
	private Expression expr_6() {
		Expression expr;
		switch (tokenType) {
		case Token.PLUS :
			move();
			expr = new PosExpression(expr_7(), line, column);
			break;
		case Token.MINUS : 
			move();
			expr = new NegExpression(expr_7(), line, column);
			break;
		case Token.NOT :
			move();
			expr = new NotExpression(expr_7(), line, column);
			break;
		case Token.L_PARENT : case Token.ID : case Token.INTEGER_LITERAL :
			expr = expr_7();
			break;
		default :
			exception.add(ErrorRecord.wrongExpression(line, column));
			return null;
		}
		return expr;
	}
	
	/**
	 * 向下推导非终极符expr_7
	 * <expr_7> ::= "(" <expr> ")" | <location> | <literal>
	 * @return 表达式的AST结点
	 * @throws ErrorRecord
	 */	
	private Expression expr_7() {
		Expression expr;
		switch (tokenType) {
		case Token.L_PARENT :
			move();
			expr = expr();
			match(Token.R_PARENT);
			break;
		case Token.ID :
			expr = location();
			break;
		case Token.INTEGER_LITERAL :
			expr = literal();
			break;
		default :
			exception.add(ErrorRecord.wrongExpression(line, column));
			return null;
		}
		return expr;
	}
	
	/**
	 * 向下推导非终极符location
	 * <location> ::= id
	 * @return 
	 * @throws ErrorRecord
	 */
	private Location location() {
		if (tokenType == Token.ID) {
			return id();
		} else {
			exception.add(ErrorRecord.missingToken(line, column, tokenType));
			moveExprFollow();
			return null;
		}
	}
	
	/**
	 * 向下推导非终极符literal
	 * <literal> ::= integer_literal
	 * @return 常量的AST结点
	 * @throws ErrorRecord
	 */
	private Literal literal() {
		if (tokenType == Token.INTEGER_LITERAL) {
			Literal literal = new IntegerLiteral((Integer)nextToken.getAttribute(), line, column);
			move();
			return literal;
		} else {
			exception.add(ErrorRecord.missingToken(line, column, tokenType));
			moveExprFollow();
			return null;
		}
	}
	
	/**
	 * 读入非终极符id，将其转换化标识符的AST结点
	 * @return 标识符的AST结点
	 * @throws ErrorRecord
	 */
	private Identifier id() {
		if (tokenType == Token.ID) {
			Identifier id = new Identifier(nextToken.getLexeme(), line, column);
			move();
			return id;
		} else {
			exception.add(ErrorRecord.missingToken(line, column, tokenType));
			moveExprFollow();
			return null;
		}
	}
	
	/**
	 * 向后移动Token直到遇到一个Expr的Follow集中的一个元素
	 * @throws CompileException
	 */
	private void moveExprFollow() {
		while (tokenType != Token.EQUAL && tokenType != Token.SEMI 
				&& tokenType != Token.R_PARENT && tokenType != Token.PLUS
				&& tokenType != Token.MINUS && tokenType != Token.TIMES
				&& tokenType != Token.OR_OR && tokenType != Token.AND_AND
				&& tokenType != Token.EQUAL_EQUAL && tokenType != Token.NOT_EQUAL
				&& tokenType != Token.LESS && tokenType != Token.LESS_EQUAL
				&& tokenType != Token.GREATER && tokenType != Token.GREATER_EQUAL) {
			exception.add(ErrorRecord.unexpectedToken(line, column, nextToken.getLexeme()));
			move();
		}
	}
	
	/**
	 * 向后移动Token直到遇到一个Stmt的Follow集中的一个元素
	 * @throws CompileException
	 */
	private void moveStmtFollow() {
		while (tokenType != Token.L_BRACE && tokenType != Token.R_BRACE 
				&& tokenType != Token.IF && tokenType != Token.ELSE
				&& tokenType != Token.WHILE && tokenType != Token.INPUT
				&& tokenType != Token.OUTPUT && tokenType != Token.ID) {
			exception.add(ErrorRecord.unexpectedToken(line, column, nextToken.getLexeme()));
			move();
		}
	}
	
}
