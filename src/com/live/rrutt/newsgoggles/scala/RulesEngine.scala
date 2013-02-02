package com.live.rrutt.newsgoggles.scala

import com.live.rrutt.newsgoggles.NewsGoggles
import com.live.rrutt.hurricane.scala._
import com.live.rrutt.ui._
import scala.collection.mutable._

object RulesEngine {
  var mapArticleProviderContents = Map.empty[String, (String, String)]
  var mapArticleTopics = Map.empty[String, List[String]]
  
  var mapSubscriberLikesProviders = Map.empty[String, List[String]]
  var mapSubscriberLikesTopics = Map.empty[String, List[String]]
  var mapSubscriberDislikesProviders = Map.empty[String, List[String]]
  var mapSubscriberDislikesTopics = Map.empty[String, List[String]]

  var mapSubscriberAllowsProviderTopics = Map.empty[String, List[(String, String)]]
  var mapSubscriberBlocksProviderTopics = Map.empty[String, List[(String, String)]]
  
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
        for (rule <- ast) {
          rule match {
            case (p.RuleType.article, (articleId, provider, contents)) => {
              mapArticleProviderContents(articleId.toString()) = (provider.toString(), contents.toString())
            }
            case (p.RuleType.article_topic, (articleId, topic)) => {
              val a = articleId.toString()
              val t = topic.toString()
              if (mapArticleTopics.contains(a)) {
                mapArticleTopics(a) ::= t
              } else {
                mapArticleTopics(a) = List(t)
              }
            }
            case (p.RuleType.subscriber_likes_provider, (subscriber, provider)) => {
              val s = subscriber.toString()
              val p = provider.toString()
              if (mapSubscriberLikesProviders.contains(s)) {
                mapSubscriberLikesProviders(s) ::= p
              } else {
                mapSubscriberLikesProviders(s) = List(p)
              }
            }
            case (p.RuleType.subscriber_likes_topic, (subscriber, topic)) => {
              val s = subscriber.toString()
              val t = topic.toString()
              if (mapSubscriberLikesTopics.contains(s)) {
                mapSubscriberLikesTopics(s) ::= t
              } else {
                mapSubscriberLikesTopics(s) = List(t)
              }
            }
            case (p.RuleType.subscriber_dislikes_provider, (subscriber, provider)) => {
              val s = subscriber.toString()
              val p = provider.toString()
              if (mapSubscriberDislikesProviders.contains(s)) {
                mapSubscriberDislikesProviders(s) ::= p
              } else {
                mapSubscriberDislikesProviders(s) = List(p)
              }
            }
            case (p.RuleType.subscriber_dislikes_topic, (subscriber, topic)) => {
              val s = subscriber.toString()
              val t = topic.toString()
              if (mapSubscriberDislikesTopics.contains(s)) {
                mapSubscriberDislikesTopics(s) ::= t
              } else {
                mapSubscriberDislikesTopics(s) = List(t)
              }
            }
            case (p.RuleType.subscriber_allows, (subscriber, provider, topic)) => {
              val s = subscriber.toString()
              val p = provider.toString()
              val t = topic.toString()
              if (mapSubscriberAllowsProviderTopics.contains(s)) {
                mapSubscriberAllowsProviderTopics(s) ::= (p, t)
              } else {
                mapSubscriberAllowsProviderTopics(s) = List((p, t))
              }
            }
            case (p.RuleType.subscriber_blocks, (subscriber, provider, topic)) => {
              val s = subscriber.toString()
              val p = provider.toString()
              val t = topic.toString()
              if (mapSubscriberBlocksProviderTopics.contains(s)) {
                mapSubscriberBlocksProviderTopics(s) ::= (p, t)
              } else {
                mapSubscriberBlocksProviderTopics(s) = List((p, t))
              }
            }
          }
        }
        println("# mapArticleProviderContents entries = %d".format(mapArticleProviderContents.size))
        println("# mapArticleTopics entries = %d".format(mapArticleTopics.size))
        println("# mapSubscriberLikesProviders entries = %d".format(mapSubscriberLikesProviders.size))
        println("# mapSubscriberLikesTopics entries = %d".format(mapSubscriberLikesTopics.size))
        println("# mapSubscriberDislikesProviders entries = %d".format(mapSubscriberDislikesProviders.size))
        println("# mapSubscriberDislikesTopics entries = %d".format(mapSubscriberDislikesTopics.size))
        println("# mapSubscriberAllowsProviderTopics entries = %d".format(mapSubscriberAllowsProviderTopics.size))
        println("# mapSubscriberBlocksProviderTopics entries = %d".format(mapSubscriberBlocksProviderTopics.size))
        true
      }
      case _ => false
    }
  }
}