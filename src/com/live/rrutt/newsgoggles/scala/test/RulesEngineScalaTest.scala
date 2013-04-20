package com.live.rrutt.newsgoggles.scala.test

import com.live.rrutt.newsgoggles.NewsGoggles;
import com.live.rrutt.newsgoggles.scala.RulesEngine._
import org.scalatest.FeatureSpec

class RulesEngineScalaTest extends FeatureSpec {
  new com.live.rrutt.newsgoggles.NewsGoggles().load()
  println("RulesEngineScalaTest NewsGoggles data loaded.")

  feature("The system can display subscriber feeds.") {

    scenario("The caller requests all subscriber feeds.") {
      val expected = "List(SubscriberFeed(Subscriber(@Alice),List(Article(1001,$ESPN,Tigers sign Prince Fielder.), Article(1002,$ESPN,Peyton Manning home team locker to be used by arch-nemesis Tom Brady?), Article(2001,$FOX,Newt nabs South Carolina.), Article(3001,$CNN,Obama visits Michigan.), Article(3002,$CNN,Obama urges congress to act in election year.), Article(4001,$MSNBC,Mitt makes moves in Florida.), Article(4002,$MSNBC,Celebrities attending Super Bowl parties.))), SubscriberFeed(Subscriber(@Bob),List(Article(1001,$ESPN,Tigers sign Prince Fielder.), Article(2001,$FOX,Newt nabs South Carolina.), Article(3001,$CNN,Obama visits Michigan.), Article(4001,$MSNBC,Mitt makes moves in Florida.))), SubscriberFeed(Subscriber(@Chris),List(Article(3001,$CNN,Obama visits Michigan.), Article(3002,$CNN,Obama urges congress to act in election year.), Article(4001,$MSNBC,Mitt makes moves in Florida.))), SubscriberFeed(Subscriber(@Pat),List(Article(1001,$ESPN,Tigers sign Prince Fielder.), Article(1002,$ESPN,Peyton Manning home team locker to be used by arch-nemesis Tom Brady?), Article(2001,$FOX,Newt nabs South Carolina.), Article(4001,$MSNBC,Mitt makes moves in Florida.))))"
      val actual = all_subscriber_feeds.toList.toString()
      assert(actual === expected)
    }
  }

  feature("The system can identify provider readers.") {

    scenario("The caller requests the readers for all providers.") {
      val expected = "List(ProviderReaders(Provider($CNN),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Chris))), ProviderReaders(Provider($ESPN),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Pat))), ProviderReaders(Provider($FOX),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Pat))), ProviderReaders(Provider($MSNBC),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Chris), Subscriber(@Pat))))"
      val actual = all_provider_readers.toList.toString()
      assert(actual === expected)
    }
  }

  feature("The system can identify article readers.") {

    scenario("The caller requests the readers for all articles.") {
    val expected = "List(ArticleReaders(Article(1001,$ESPN,Tigers sign Prince Fielder.),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Pat))), ArticleReaders(Article(1002,$ESPN,Peyton Manning home team locker to be used by arch-nemesis Tom Brady?),List(Subscriber(@Alice), Subscriber(@Pat))), ArticleReaders(Article(2001,$FOX,Newt nabs South Carolina.),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Pat))), ArticleReaders(Article(2002,$FOX,Do we need to go back to the moon?),List()), ArticleReaders(Article(3001,$CNN,Obama visits Michigan.),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Chris))), ArticleReaders(Article(3002,$CNN,Obama urges congress to act in election year.),List(Subscriber(@Alice), Subscriber(@Chris))), ArticleReaders(Article(4001,$MSNBC,Mitt makes moves in Florida.),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Chris), Subscriber(@Pat))), ArticleReaders(Article(4002,$MSNBC,Celebrities attending Super Bowl parties.),List(Subscriber(@Alice))))"
    val actual = all_article_readers.toList.toString()
      assert(actual === expected)
    }
  }
}
