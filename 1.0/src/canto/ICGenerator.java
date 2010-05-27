/**
 * 
 */
package canto;

/**
 * @author Goodness
 *
 */
public interface ICGenerator {

	/**
	 * 设置待分析的抽象语法树
	 * @param AST 待分析的抽象语法树
	 */
	public void setAST(AbstractSyntaxTree AST);
	
	/**
	 * 得到中间代码
	 * @return 生成的中间代码
	 */
	public IntermediateCode generateIC();
	
	/**
	 * 获取生成的中间代码
	 * @return 生成的中间代码
	 */
	public IntermediateCode getIC();
}
