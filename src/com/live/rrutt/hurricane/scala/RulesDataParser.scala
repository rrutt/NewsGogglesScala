package com.live.rrutt.hurricane.scala

import scala.util.parsing.combinator._

class RulesDataParser extends JavaTokenParsers {

  object RuleType extends Enumeration {
    type RuleType = Value
    val article, article_topic, 
    	subscriber_likes_provider, subscriber_dislikes_provider, 
    	subscriber_likes_topic, subscriber_dislikes_topic, 
    	subscriber_allows, subscriber_blocks = Value
  }
  import RuleType._

  def rulesList = rep(rule)
  
  def rule = article | article_topic | 
  	subscriber_likes | subscriber_dislikes |
  	subscriber_allows | subscriber_blocks
  	
  def article = "article" ~> articleBody ^^ {
    case (a, p, c) => (RuleType.article, (a, p, c))
  }
  def articleBody = "(" ~> articleId ~ articleProviderContents ^^ {
    case a ~ pc => {
      val (p, c) = pc
      (a, p, c)
    }
  }
  def articleProviderContents = "," ~> provider ~ articleContents ^^ {
    case p ~ c => (p, c)
  }
  def articleContents = "," ~> articleText <~ ")" <~ "."
  
  def article_topic = "article_topic" ~> article_topicBody ^^ {
    case (a, t) => (RuleType.article_topic, (a, t))
  }
  def article_topicBody = "(" ~> articleId ~ articleTopic ^^ {
    case a ~ t => (a, t)
  }
  def articleTopic = "," ~> topic <~ ")" <~ "."
  
  def subscriber_likes = "subscriber_likes" ~> subscriber_likes_dislikesBody ^^ {
    case (s, tp) => {
      if (tp.startsWith("$")) {
        (RuleType.subscriber_likes_provider, (s, tp))
      } else {
        (RuleType.subscriber_likes_topic, (s, tp))
      }
    }
  } 
  def subscriber_dislikes = "subscriber_dislikes" ~> subscriber_likes_dislikesBody ^^ {
    case (s, tp) => {
      if (tp.startsWith("$")) {
        (RuleType.subscriber_dislikes_provider, (s, tp))
      } else {
        (RuleType.subscriber_dislikes_topic, (s, tp))
      }
    }
  } 
  def subscriber_likes_dislikesBody = "(" ~> subscriber ~ topicOrProvider <~ ")" <~ "." ^^ {
    case s ~ tp => (s, tp)
  }
  def topicOrProvider = "," ~> textString
  
  def subscriber_allows = "subscriber_allows" ~> subscriber_allows_blocksBody ^^ {
    case (s, p, t) => (RuleType.subscriber_allows, (s, p, t))
  }
  def subscriber_blocks = "subscriber_blocks" ~> subscriber_allows_blocksBody ^^ {
    case (s, p, t) => (RuleType.subscriber_blocks, (s, p, t))
  }
  def subscriber_allows_blocksBody = "(" ~> subscriber ~ providerAndTopic ^^ {
    case s ~ pt => {
      val (p, t) = pt
      (s, p, t)
    }
  }
  def providerAndTopic = "," ~> provider ~ andTopic ^^ {
    case p ~ t => (p, t)
  }
  def andTopic = "," ~> topic <~ ")" <~ "."
  
  def articleId = wholeNumber
  def articleText = textString
  def topic = textString
  def provider = textString
  def subscriber = textString
  
  def textString = stringLiteral ^^ {
    case s if (s.startsWith("\"") && s.endsWith("\"")) => {
      s.substring(0, (s.length() - 1)).substring(1);
    }
    case s => s
  }
}