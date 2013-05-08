package org.scalaide.editor.util

import scala.tools.eclipse.ISourceViewerEditor

import org.eclipse.jface.text.link.LinkedModeModel
import org.eclipse.jface.text.link.LinkedModeUI
import org.eclipse.jface.text.link.LinkedPosition
import org.eclipse.jface.text.link.LinkedPositionGroup
import org.eclipse.ui.IEditorPart
import org.eclipse.ui.IWorkbenchPage
import org.eclipse.ui.IWorkbenchWindow
import org.eclipse.ui.PlatformUI

object EditorHelper {

  private def activeWorkbenchWindow: Option[IWorkbenchWindow] = Option(PlatformUI.getWorkbench.getActiveWorkbenchWindow)
  private def activePage(w: IWorkbenchWindow): Option[IWorkbenchPage] = Option(w.getActivePage)
  private def activeEditor(p: IWorkbenchPage): Option[IEditorPart] = if (p.isEditorAreaVisible) Some(p.getActiveEditor) else None
  private def textEditor(e: IEditorPart): Option[ISourceViewerEditor] = e match { case t: ISourceViewerEditor => Some(t) case _ => None }
   
  def doWithCurrentEditor(block: ISourceViewerEditor => Unit) {
    withCurrentEditor { editor =>
      block(editor)
      None
    }
  }

  def withCurrentEditor[T](block: ISourceViewerEditor => Option[T]): Option[T] = {
    activeWorkbenchWindow flatMap {
      activePage(_) flatMap {
        activeEditor(_) flatMap {
          textEditor(_) flatMap block
        }
      }
    }
  }

  /** Enters the editor in the LinkedModeUI with the given list of positions.
   *  A position is given as an offset and the length.
   */
  def enterLinkedModeUi(ps: List[(Int, Int)]) {

    doWithCurrentEditor { editor =>

      val model = new LinkedModeModel()
      val document = editor.getDocumentProvider.getDocument(editor.getEditorInput)

      val group = new LinkedPositionGroup()
      ps foreach (p => group.addPosition(new LinkedPosition(document, p._1, p._2)))
      
      model.addGroup(group)
      model.forceInstall

      new LinkedModeUI(model, editor.getViewer).enter()
    }
  }

}