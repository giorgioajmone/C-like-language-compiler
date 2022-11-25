package classes;

public interface Visitor {
    public Object visitProgram(PROGRAM program, Object arg);
    public Object visitBlock(BLOCK block, Object arg);
    public Object visitCommandList(COMMAND_LIST commandList, Object arg);
    public Object visitCommand(COMMAND command, Object arg);
    public Object visitDeclarationList(DECLARATION_LIST declarationList, Object arg);
    public Object visitDeclarationStatement(DECLARATION_STATEMENT declarationStatement, Object arg);
    public Object visitFunctionStatement(FUNCTION_STATEMENT functionStatement, Object arg);
    public Object visitDeclaration(DECLARATION declaration, Object arg);
    public Object visitExpressionList(EXPRESSION_LIST expressionList, Object arg);
    public Object visitAssignment(ASSIGNMENT assignment, Object arg);
    public Object visitIdentifierList(IDENTIFIER_LIST identifierList, Object arg);
    public Object visitIdentifier(IDENTIFIER identifier, Object arg);
    public Object visitIntegerLiteral(INTEGERLITERAL integerLiteral, Object arg);
    public Object visitOperator(OPERATOR operator, Object arg);
    public Object visitArrayIdentifier(ARRAY_IDENTIFIER arrayIdentifier, Object arg);
    public Object visitExpression(EXPRESSION expression, Object arg);
    public Object visitSelectionStatement(SELECTION_STATEMENT selectionStatement, Object arg);
    public Object visitRepetitionStatement(REPETITION_STATEMENT repetitionStatement, Object arg);
    public Object visitTypeList(TYPE_LIST typeList, Object arg);
    public Object visitType(TYPE type, Object arg);
    public Object visitCallExpression(CALL_EXPRESSION callExpression, Object arg);
    public Object visitBinary(BINARY binary, Object arg);
    public Object visitIntLitExpression(INTLIT_EXPRESSION intLitExpression, Object arg);
    public Object visitFunction(FUNCTION function, Object arg);
    public Object visitVarExpression(VAR_EXPRESSION varExpression, Object arg);
    public Object visitSingleDeclaration(SINGLE_DECLARATION singleDeclaration, Object arg);
    public Object visitIdentifierItem(IDENTIFIER_ITEM identifierItem, Object arg);
}
