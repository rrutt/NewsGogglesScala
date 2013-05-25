package com.live.rrutt.newsgoggles.scala

import com.live.rrutt.newsgoggles.NewsGoggles
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

case class Article(id: String, Provider: String, Contents: String)
case class Subscriber(handle: String)
case class Provider(symbol: String)
case class SubscriberFeed(subscriber: Subscriber, articles: Seq[Article])
case class ArticleReaders(article: Article, readers: Seq[Subscriber])
case class ProviderReaders(provider: Provider, readers: Seq[Subscriber])

object RulesEngine {
  // TODO: Replace List's with Vector's.
  
  var mapArticleProviderContents = mutable.Map.empty[String, (String, String)]
  var mapArticleTopics = mutable.Map.empty[String, List[String]]
  
  var setSubscribers = mutable.Set.empty[String]
  var setProviders = mutable.Set.empty[String]
  
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
    subscriberTopicsMatchArticle(mapSubscriberLikesTopics get s, a)
  }  
  def subscriber_dislikes_article_topic(s: String, a: String): Boolean = {
    subscriberTopicsMatchArticle(mapSubscriberDislikesTopics get s, a)
  }
  def subscriberTopicsMatchArticle(sTopics: Option[List[String]], a: String): Boolean = {
    val aTopics = mapArticleTopics get a
    (aTopics, sTopics) match {
      case (Some(atList), Some(stList)) => !((atList intersect stList) isEmpty)
      case _ => false
    }
  }

  def subscriber_allows_article_provider_and_topic(s: String, a: String): Boolean = {
    subscriberProviderTopicsMatchArticle(mapSubscriberAllowsProviderTopics get s, a)
  }  
  def subscriber_blocks_article_provider_and_topic(s: String, a: String): Boolean = {
    subscriberProviderTopicsMatchArticle(mapSubscriberBlocksProviderTopics get s, a)
  }  
  def subscriberProviderTopicsMatchArticle(sProviderTopics: Option[List[(String, String)]], a: String): Boolean = {
    val aProviderContents = mapArticleProviderContents get a
    val aTopics = mapArticleTopics get a
    val aProviderTopics = (aProviderContents, aTopics) match {
      case (Some((ap, _)), Some(atList)) => atList.map{at => (ap, at)}
      case _ => List.empty[(String, String)]
    }
    (aProviderTopics, sProviderTopics) match {
      case (aptList, Some(sptList)) => !((aptList intersect sptList) isEmpty)
      case _ => false
    }
  }
  
  /*
   * Top-level rules engine predicates.
   */
  def article_is_visible_to_subscriber(a: String, s: String): Boolean = {
    (a, s) match {
      case (a, s) if subscriber_blocks_article_provider_and_topic(s, a) => false
      case (a, s) if subscriber_allows_article_provider_and_topic(s, a) => true
      case (a, s) if subscriber_dislikes_article_provider(s, a) => false
      case (a, s) if subscriber_dislikes_article_topic(s, a) => false
      case (a, s) if (
        subscriber_likes_article_provider(s, a) ||
      	subscriber_likes_article_topic(s, a)
      	) => true
      case _ => false
    }
  }
  
  /*
   * High-level result collection goal predicates.
   */
  
  // Given a Subscriber,
  // return the list of current Article identifiers that match their preferences.
  def subscriber_article_ids(s: String) = {
    subscriber_feed(s).map{case Article(a, _, _) => a}
  }
  
  // Given a Subscriber,
  // return the list of current Article details that match their preferences.
  def subscriber_feed(s: String) = {
    val filteredArticles = mapArticleProviderContents.iterator.filter{case (a, _) => article_is_visible_to_subscriber(a, s)}
    filteredArticles.toList.sortBy{case (a, _) => a}.map{case (a, (p, c)) => Article(a, p, c)}
  }
  
  // Get master "list of lists" for all Subscriber/Article feeds.
  def all_subscriber_feeds = {
    setSubscribers.toList.sortBy{s => s}.map{s => SubscriberFeed(Subscriber(s), subscriber_feed(s))}
  }
  
  // Given an Article, 
  // return the list of Subscribers that the Article will reach.	
  def article_readers(a: String) = {
    setSubscribers.iterator.filter{s => article_is_visible_to_subscriber(a, s)}.toList.sortBy{s => s}.map{s => Subscriber(s)}
  }
  
  // Get master "list of lists" for all Article/Subscriber links.
  def all_article_readers = {
    mapArticleProviderContents.toList.sortBy{case (a, _) => a}.map{case (a, (p, c)) => ArticleReaders(Article(a, p, c), article_readers(a))}
  }
  
  // Given a Provider, 
  // return the list of Subscribers that receive at least one of the Provider's Articles.
  // (Can be used to target advertising or premium direct content, etc.)
  def provider_readers(provider: String) = {
    val providerArticleIds = mapArticleProviderContents.iterator.filter{case (a, (p, c)) => p == provider}.map{case (a, (p, _)) => a}
    providerArticleIds.flatMap{a => article_readers(a)}.toList.distinct.sortBy{case Subscriber(s) => s}
  }
  
  // Get master "list of lists" for all Provider/Subscriber sets.
  def all_provider_readers = {
    setProviders.toList.sortBy{p => p}.map(p => ProviderReaders(Provider(p), provider_readers(p)))
  }
  
  // Print out the current Article feed for a Subscriber.	
  def show_news(s: String) = {
    println
	println("Articles subscribed by %s:".format(s))
	println(subscriber_feed(s).toList)
	println("-- End --")
  }

  // Print out the current Article feed for all Subscribers.
  def show_all_news = {
    println
    println("== All Subscriber Feeds ==")
    for (s <- setSubscribers.toList.sortBy{s => s}) show_news(s)
    println("====")
  }
  
  /*
   * Logic to load the dynamic data,
   * which consists of the current set of Articles
   * and the Subscriber preferences.
   */
  def loadData(rulesDataText: String): Boolean = {
    val p = new RulesDataParser
    val rulesDataCharSequence = new com.live.rrutt.newsgoggles.StringDecorator(rulesDataText)
    val parseResult = p.parseAll(p.rulesList, rulesDataCharSequence) 
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
              val a = articleId.toString()
              val p = provider.toString()
              val c = contents.toString()
              mapArticleProviderContents(a) = (p, c)
              setProviders += p
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
              setSubscribers += s
            }
            case (p.RuleType.subscriber_likes_topic, (subscriber, topic)) => {
              val s = subscriber.toString()
              val t = topic.toString()
              if (mapSubscriberLikesTopics.contains(s)) {
                mapSubscriberLikesTopics(s) ::= t
              } else {
                mapSubscriberLikesTopics(s) = List(t)
              }
              setSubscribers += s
            }
            case (p.RuleType.subscriber_dislikes_provider, (subscriber, provider)) => {
              val s = subscriber.toString()
              val p = provider.toString()
              if (mapSubscriberDislikesProviders.contains(s)) {
                mapSubscriberDislikesProviders(s) ::= p
              } else {
                mapSubscriberDislikesProviders(s) = List(p)
              }
              setSubscribers += s
            }
            case (p.RuleType.subscriber_dislikes_topic, (subscriber, topic)) => {
              val s = subscriber.toString()
              val t = topic.toString()
              if (mapSubscriberDislikesTopics.contains(s)) {
                mapSubscriberDislikesTopics(s) ::= t
              } else {
                mapSubscriberDislikesTopics(s) = List(t)
              }
              setSubscribers += s
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
              setSubscribers += s
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
              setSubscribers += s
            }
          }
        }
        
        println("# mapArticleProviderContents entries = %d".format(mapArticleProviderContents.size))
        println("# mapArticleTopics entries = %d".format(mapArticleTopics.size))
        println("# setSubscribers entries = %d".format(setSubscribers.size))
        println("# setProviders entries = %d".format(setProviders.size))
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