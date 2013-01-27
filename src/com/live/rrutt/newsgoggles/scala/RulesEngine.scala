package com.live.rrutt.newsgoggles.scala

import com.live.rrutt.newsgoggles.NewsGoggles
import com.live.rrutt.hurricane.scala._
import com.live.rrutt.ui._
//import scala.util.parsing.combinator._

object RulesEngine {
  def loadData(rulesDataText: String): Boolean = {
    //    RulesDataDsl.parseRules(rulesDataText)
    val p = new RulesDataParser
    val parseResult = p.parseAll(p.rulesList, rulesDataText) 
    val parsedRulesData = parseResult match {
      case p.Success(ast, _) => {
        println("----- Parse Result:")
        println(ast.toString())
        (true, ast)
      }
      case p.Failure(msg, next) => {
        println("----- Failure Message:")
        println(msg.toString())
        println("----- Parsed:")
        println(rulesDataText.substring(0, next.offset))
        println("----- Unparsed:")
        println(next.source)
        (false, null)
      }
      case p.Error(msg, next) => {
        println("----- Error Message:")
        println(msg.toString())
        println("----- Parsed:")
        println(rulesDataText.substring(0, next.offset))
        println("----- Unparsed:")
        println(next.source)
        (false, null)
      }
    }
    parsedRulesData match {
      case (true, ast) => {
        // TODO: Load data structures from abstract syntax tree.
        true
      }
      case _ => false
    }
  }
}