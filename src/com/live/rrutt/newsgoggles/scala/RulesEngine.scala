package com.live.rrutt.newsgoggles.scala

import com.live.rrutt.newsgoggles.NewsGoggles
import com.live.rrutt.hurricane.scala._
import com.live.rrutt.ui._
import scala.collection._

/*
Static logic predicates to apply the Subscriber preferences dynamic rules
to the current contents of the Articles database.

Here are the requirements for News Goggles:

  1. Providers publish Articles. 
  2. Subscribers open the web site to review Articles. 
  3. The Providers tag each Article with one or more Topics. 
  4. Each Subscriber registers a set of Subscription Rules: 
     - A Subscriber can Like any number of Providers and/or Topics. 
     - A Subscriber can also Dislike any number of Providers and/or Topics. 
     - Dislike rules override Like rules. 
  5. A Subscriber can also enter Exception Rules that override their Subscription Rules: 
     - A Subscriber can Allow a specific combination of Provider covering a Topic. 
     - A Subscriber can Block a specific combination of Provider covering a Topic. 
     -Block rules override Allow rules. 
  6. Identifier conventions: 
     Except for Articles, identifiers are Prolog apostrophe-quoted atoms. 
     - Articles are identified by arbitrary unique integers. The content of each Article is a quote-delimited Prolog string. 
     - Providers are identified with a $ prefix. Examples: "$CNN" "$FOX" "$ESPN" "$CBC" "$NBC" "$Wired" "$NYTimes" 
     - Topics are identified with a # prefix. 
       Examples: "#world" "#usa" "#sports" "#entertainment" "#business" "#politics" "#opinion"
     - Subscribers are identified with an @ prefix. 
       Examples: "@Alice" "@Bob" "@Chris" "@Pat" 
*/

object RulesEngine {
  var mapArticleProviderContents = mutable.Map.empty[String, (String, String)]
  var mapArticleTopics = mutable.Map.empty[String, List[String]]
  
  var mapSubscriberLikesProviders = mutable.Map.empty[String, List[String]]
  var mapSubscriberLikesTopics = mutable.Map.empty[String, List[String]]
  var mapSubscriberDislikesProviders = mutable.Map.empty[String, List[String]]
  var mapSubscriberDislikesTopics = mutable.Map.empty[String, List[String]]

  var mapSubscriberAllowsProviderTopics = mutable.Map.empty[String, List[(String, String)]]
  var mapSubscriberBlocksProviderTopics = mutable.Map.empty[String, List[(String, String)]]


  /*
   * Mid-level rules engine predicates.
   */
  def subscriber_likes_article_provider(s: String, a: String): Boolean = {
    subscriberProviderMapMatchesArticle(s, mapSubscriberLikesProviders, a)
  }  
  def subscriber_dislikes_article_provider(s: String, a: String): Boolean = {
    subscriberProviderMapMatchesArticle(s, mapSubscriberDislikesProviders, a)
  }
  def subscriberProviderMapMatchesArticle(s: String, providerMap: Map[String, List[String]], a: String): Boolean = {
    mapArticleProviderContents get a match {
      case None => false
      case Some((p, _)) => {
        providerMap get s match {
          case Some(pList) => pList.contains(p)
          case _ => false
        }
      }
    } 
  }

  def subscriber_likes_article_topic(s: String, a: String): Boolean = {
    subscriberTopicMapMatchesArticle(s, mapSubscriberLikesTopics, a)
  }  
  def subscriber_dislikes_article_topic(s: String, a: String): Boolean = {
    subscriberTopicMapMatchesArticle(s, mapSubscriberDislikesTopics, a)
  }
  def subscriberTopicMapMatchesArticle(s: String, topicMap: Map[String, List[String]], a: String): Boolean = {
    val aTopics = mapArticleTopics get a
    val sTopics = topicMap get s
    (aTopics, sTopics) match {
      case (Some(atList), Some(stList)) => !((atList intersect stList) isEmpty)
      case _ => false
    }
  }

  def subscriber_allows_article_provider_and_topic(s: String, a: String): Boolean = {
    subscriberProviderTopicMapMatchesArticle(s, mapSubscriberAllowsProviderTopics, a)
  }  
  def subscriber_blocks_article_provider_and_topic(s: String, a: String): Boolean = {
    subscriberProviderTopicMapMatchesArticle(s, mapSubscriberBlocksProviderTopics, a)
  }  
  def subscriberProviderTopicMapMatchesArticle(s: String, providerTopicMap: Map[String, List[(String, String)]], a: String): Boolean = {
    val aProviderContents = mapArticleProviderContents get a
    val aTopics = mapArticleTopics get a
    val aProviderTopics = (aProviderContents, aTopics) match {
      case (Some((ap, _)), Some(atList)) => atList.map{at => (ap, at)}
      case _ => List.empty[(String, String)]
    }
    val sProviderTopics = providerTopicMap get s
    (aProviderTopics, sProviderTopics) match {
      case (aptList, Some(sptList)) => !((aptList intersect sptList) isEmpty)
      case _ => false
    }
  }  
  
  /*
   * Logic to load the dynamic data,
   * which consists of the current set of Articles
   * and the Subscriber preferences.
   */
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