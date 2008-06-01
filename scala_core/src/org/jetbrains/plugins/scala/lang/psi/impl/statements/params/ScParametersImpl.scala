package org.jetbrains.plugins.scala.lang.psi.impl.statements.params

import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.parser.ScalaElementTypes
import org.jetbrains.plugins.scala.lang.psi.ScalaPsiElementImpl
import com.intellij.psi.tree.TokenSet
import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType;
import com.intellij.psi._
import org.jetbrains.annotations._
import org.jetbrains.plugins.scala.lang.psi.impl.ScalaPsiElementFactory
import org.jetbrains.plugins.scala.icons.Icons
import org.jetbrains.plugins.scala.lang.psi.api.statements.params._
import com.intellij.psi._
import com.intellij.psi.search._

/**
* @author Alexander Podkhalyuzin
* Date: 22.02.2008
*/

class ScParametersImpl(node: ASTNode) extends ScalaPsiElementImpl(node) with ScParameters {

  override def toString: String = "Parameters"

  def params: Seq[ScParameter] = clauses.flatMap((clause: ScParameterClause) => clause.parameters)

  def clauses: Seq[ScParameterClause] = findChildrenByClass(classOf[ScParameterClause])

  def getParametersAsString: String = {
    val res: StringBuffer = new StringBuffer("")
    for (child <- clauses) {
      res.append("(")
      res.append(child.getParametersAsString)
      res.append(")")
    }
    return res.toString()
  }

  def getParameterIndex(p: PsiParameter) = params.indexOf(List(p))

  def getParametersCount = params.length

  // todo hack for applictiation running
  override def getParameters = getParent match {
    case m: ScFunctionImpl => {
      if (m.isMainMethod) {
        val ps = new Array[PsiParameter](1)
        val p = new ScParameterImpl(params(0).getNode) {
          override def getType(): PsiType = new PsiArrayType(
          JavaPsiFacade.getInstance(m.getProject).getElementFactory.createTypeByFQClassName(
          "java.lang.String",
          GlobalSearchScope.allScope(m.getProject)
          )
          )
        }
        ps(0) = p
        ps
      } else params.toArray
    }
    case _ => params.toArray
  }
}