package compiler;

	import java.awt.*;
	import javax.swing.*;
	import javax.swing.tree.*;

import classes.*;

// This class is used to display the AST in a JTree
public class ASTViewer extends JFrame
{
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private static final Font NODE_FONT = new Font( "Verdana", Font.PLAIN, 24 );
		
		
		public ASTViewer( AST ast )
		{
			super( "Abstract Syntax Tree" );
			
			DefaultMutableTreeNode root = createTree( ast );
			JTree tree = new JTree( root );
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			renderer.setFont( NODE_FONT );
			tree.setCellRenderer( renderer );
			
			add( new JScrollPane( tree ) );
			
			setSize( 1024, 768 );
			setVisible( true );
			
			setDefaultCloseOperation( EXIT_ON_CLOSE );
		}
		
		
		private DefaultMutableTreeNode createTree( AST ast )
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode( "*** WHAT??? ***" );
			
			if( ast == null )
				node.setUserObject( "*** NULL ***" );
			else if( ast instanceof PROGRAM ) {
				node.setUserObject( "Program" );
				node.setUserObject( "Block" );
				for( BLOCK p: ((PROGRAM)ast).blocks )
					node.add( createTree( p ) );
			} else if( ast instanceof BLOCK ) {
				node.setUserObject( "Block" );
				node.add( createTree( ((BLOCK)ast).TypeDec) );
				node.add( createTree( ((BLOCK)ast).FuncName) );
				node.add( createTree( ((BLOCK)ast).ParamDec) );
				node.add( createTree( ((BLOCK)ast).Commands) );
				node.add( createTree( ((BLOCK)ast).ReturnedValues) );
			} else if( ast instanceof COMMAND_LIST  ) {
				node.setUserObject( "Commands" );
				
				for( COMMAND d: ((COMMAND_LIST)ast).commands)
					node.add( createTree( d ) );
			} else if( ast instanceof ASSIGNMENT) {
				node.setUserObject( "Assignment" );
				node.add( createTree( ((ASSIGNMENT)ast).Variables) );
				node.add( createTree( ((ASSIGNMENT)ast).Values ) );
			} else if( ast instanceof DECLARATION_STATEMENT) {
				node.setUserObject( "Declaration  Statement" );
				node.add( createTree( ((DECLARATION_STATEMENT)ast).declaration ) );
			}  else if( ast instanceof FUNCTION_STATEMENT) {
				node.setUserObject( "Function Statement" );
				node.add( createTree( ((FUNCTION_STATEMENT)ast).function ) );
			}  else if( ast instanceof REPETITION_STATEMENT) {
				node.setUserObject( "Repetition Statement" );
				node.add( createTree( ((REPETITION_STATEMENT)ast).Condition ) );
				node.add( createTree( ((REPETITION_STATEMENT)ast).Commands ) );
			} else if( ast instanceof SELECTION_STATEMENT) {
				node.setUserObject( "Selection Statement" );
				node.add( createTree( ((SELECTION_STATEMENT)ast).Condition ) );
				node.add( createTree( ((SELECTION_STATEMENT)ast).Commands ) );
				node.add( createTree( ((SELECTION_STATEMENT)ast).Else_Commands ) );
			} else if( ast instanceof DECLARATION_LIST ) {
				node.setUserObject( "Declaration List" );
				
				for( DECLARATION s: ((DECLARATION_LIST)ast).declarations )
					node.add( createTree( s ) );
			} else if( ast instanceof DECLARATION ) {
				node.setUserObject( "Declaration" );
				for( SINGLE_DECLARATION s: ((DECLARATION)ast).singleDeclarations )
					node.add( createTree( s ) );
			} else if( ast instanceof	EXPRESSION_LIST  ) {
				node.setUserObject( "Expression List" );
				node.setUserObject(((EXPRESSION_LIST)ast).size);
				for( EXPRESSION s: ((EXPRESSION_LIST)ast).values )
					node.add( createTree( s ) );
				
			} else if( ast instanceof IDENTIFIER_LIST ) {
				node.setUserObject( "Identifier List" );
				for( IDENTIFIER_ITEM s: ((IDENTIFIER_LIST)ast).items )
					node.add( createTree( s ) );
			} else if( ast instanceof FUNCTION ) {
				node.setUserObject( "Function");
				node.add( createTree( ((FUNCTION)ast).name ) );
				node.add( createTree( ((FUNCTION)ast).args ) );
				node.add( createTree( ((FUNCTION)ast).declaration ) );
			} else if( ast instanceof TYPE_LIST ) {
				node.setUserObject( "Type List" );
				for( TYPE s: ((TYPE_LIST)ast).types )
					node.add( createTree( s ) );
			}  else if( ast instanceof BINARY ) {
				node.setUserObject( "Binary" );
				node.add( createTree( ((BINARY)ast).OperandOne ) );
				node.add( createTree( ((BINARY)ast).Operator ) );
				node.add( createTree( ((BINARY)ast).OperandTwo ) );
				node.setUserObject( ((BINARY)ast).size);
			} else if( ast instanceof VAR_EXPRESSION ) {
				node.setUserObject( "Var Expression" );
				node.add( createTree( ((VAR_EXPRESSION)ast).variable ) );
				node.setUserObject(((VAR_EXPRESSION)ast).size );
			} else if( ast instanceof CALL_EXPRESSION ) {
				node.setUserObject( "Call Expression" );
				node.add( createTree( ((CALL_EXPRESSION)ast).function ) );
				node.setUserObject(((CALL_EXPRESSION)ast).size );
			} else if( ast instanceof INTLIT_EXPRESSION ) {
				node.setUserObject( "IntLit Expression" );
				node.add( createTree( ((INTLIT_EXPRESSION)ast).value) );
				node.setUserObject(((INTLIT_EXPRESSION)ast).size );
			} else if( ast instanceof OPERATOR ) {
				node.setUserObject( "Operator " + ((OPERATOR)ast).spelling );
			} else if( ast instanceof INTEGERLITERAL ) {
				node.setUserObject( "IntegerLiteral " + ((INTEGERLITERAL)ast).spelling );
			}else if( ast instanceof TYPE ) {
				node.setUserObject( "TYPE " + ((TYPE)ast).spelling );
			}else if( ast instanceof ARRAY_IDENTIFIER ) {
				node.add( createTree( ((ARRAY_IDENTIFIER)ast).index ) );
				node.add( createTree( ((ARRAY_IDENTIFIER)ast).declaration ) );
				node.setUserObject( "Array Identifier " + ((ARRAY_IDENTIFIER)ast).spelling );
			}else if( ast instanceof IDENTIFIER ) {
				node.setUserObject( "Identifier " + ((IDENTIFIER)ast).spelling );
				node.add( createTree( ((IDENTIFIER)ast).declaration ) );
			}else if (ast instanceof SINGLE_DECLARATION) {
				node.setUserObject("Single Declaration");
				node.setUserObject(((SINGLE_DECLARATION)ast).name.spelling);
				node.add( createTree( ((SINGLE_DECLARATION)ast).type ) );
				node.setUserObject(((SINGLE_DECLARATION)ast).size);
			}
				
			return node;
		}
}
