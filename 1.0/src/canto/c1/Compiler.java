/**
 * 
 */
package canto.c1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import canto.IntermediateCode;
import canto.AbstractSyntaxTree;
import canto.Token;

/**
 * @author basicthinker
 *
 */
public class Compiler implements canto.Compiler {

	private InputStreamReader sourceReader;
	private OutputStreamWriter targetWriter;
	Lexer lexer;

	/**
	 * 
	 */
	public Compiler() {
		sourceReader = null;
		targetWriter = null;
		lexer = new Lexer();
	}

	/* (non-Javadoc)
	 * @see canto.Compiler#compile()
	 */
	@Override
	public void compile() {
		// TODO Auto-generated method stub
		if (sourceReader == null) return;
		
		try {
			lexer.open(sourceReader);
			lexer.scan();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see canto.Compiler#getIntermediateCode()
	 */
	@Override
	public IntermediateCode getIntermediateCode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see canto.Compiler#getSyntaxTree()
	 */
	@Override
	public AbstractSyntaxTree getSyntaxTree() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see canto.Compiler#getTokenList()
	 */
	@Override
	public List<Token> getTokenList() {
		return lexer.getTokenList();
	}

	/* (non-Javadoc)
	 * @see canto.Compiler#outputErrors(java.io.OutputStream)
	 */
	@Override
	public void outputErrors(OutputStream outStrm) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see canto.Compiler#setSource(java.io.InputStream)
	 */
	@Override
	public void setSource(InputStream inStrm) {
		sourceReader = new InputStreamReader(inStrm);
	}

	/* (non-Javadoc)
	 * @see canto.Compiler#setTarget(java.io.OutputStream)
	 */
	@Override
	public void setTarget(OutputStream outStrm) {
		targetWriter = new OutputStreamWriter(outStrm);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FileInputStream inFile = new FileInputStream("source.c1");
			Compiler compiler = new Compiler();
			compiler.setSource(inFile);
			compiler.compile();
			List<Token> tokenList = compiler.getTokenList();
			
			for (Token token : tokenList) {
				
				System.out.print("Line Number: " + token.getLineNumber());
				
				switch (token.getType()) { 
					case canto.c1.Token.CONSTANT:
						System.out.print(" CONST:\t");
						break;
					case canto.c1.Token.ID:
						System.out.print(" ID:\t");
						break;
					case canto.c1.Token.KEYWORD:
						System.out.print(" KEYW:\t");
						break;
					case canto.c1.Token.OPERERATOR:
						System.out.print(" OPER:\t");
						break;
					case canto.c1.Token.PUNCTUATION:
						System.out.print(" PUNC:\t");
						break;
				}
				
				System.out.print(token.getLexeme() + "\twith ");
				if (token.getAttribute() == null) System.out.println("null");
				else System.out.println(token.getAttribute().toString());
				
			} // for
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
