package org.scalaide.play2.routeeditor.completion

import org.eclipse.jface.text.contentassist.ICompletionProposal

import org.eclipse.jface.text.contentassist.IContentAssistProcessor
import org.junit.Test
import org.scalaide.play2.routeeditor.lexical.HTTPKeywords

class HttpMethodCompletionComputerTest extends CompletionComputerTest {

  case class Proposal(httpMethod: String) extends ExpectedProposal

  implicit object Converter extends AsExpectedProposal[Proposal] {
    def apply(proposal: ICompletionProposal) = Proposal(proposal.getDisplayString)
  }

  override def createCompletionComputer: IContentAssistProcessor = new HttpMethodCompletionComputer

  private val AllHttpMethodsProposals = HTTPKeywords.Methods map Proposal

  @Test
  def HttpGET_completion() {
    val route = RouteFile { "G@" }

    route expectedCompletions Proposal("GET")
  }

  @Test
  def HttpGET_completion_is_case_insensitive() {
    val route = RouteFile { "g@" }

    route expectedCompletions Proposal("GET")
  }

  @Test
  def HTTP_PUT_POST_completion() {
    val route = RouteFile { "P@" }

    route expectedCompletions (Proposal("PATCH"), Proposal("POST"), Proposal("PUT"))
  }

  @Test
  def HTTP_HEAD_completion() {
    val route = RouteFile { "HeA@" }

    route expectedCompletions Proposal("HEAD")
  }

  @Test
  def HTTP_DELETE_completion() {
    val route = RouteFile { "de@" }

    route expectedCompletions Proposal("DELETE")
  }

  @Test
  def show_all_HTTP_methods_when_word_doesnt_match() {
    val route = RouteFile { "DET@" }

    route expectedCompletions (AllHttpMethodsProposals: _*)
  }

  @Test
  def all_Http_Method_completion_at_beginning_of_empty_line() {
    val route = RouteFile { "@" }

    route expectedCompletions (AllHttpMethodsProposals: _*)
  }

  @Test
  def all_Http_Method_completion_at_end_of_empty_line() {
    val route = RouteFile {
      // whitespaces before the '*' are relevant for this test!
      "   @"
    }

    route expectedCompletions (AllHttpMethodsProposals: _*)
  }

  @Test
  def all_Http_Method_completion_in_middle_of_empty_line() {
    val route = RouteFile {
      // whitespaces after the '*' are relevant for this test!
      "  @   "
    }

    route expectedCompletions (AllHttpMethodsProposals: _*)
  }

  @Test
  def all_Http_Method_completion_when_cursor_is_on_already_valid_Http_method() {
    val route = RouteFile { "G@ET" }

    route expectedCompletions (AllHttpMethodsProposals: _*)
  }

  @Test
  def GET_Http_Method_completion_returned() {
    val route = RouteFile {
      // whitespaces after the '*' is relevant for this test!
      "G@  "
    }

    route expectedCompletions Proposal("GET")
  }
}