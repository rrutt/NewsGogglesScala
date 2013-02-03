package com.live.rrutt.newsgoggles.scala.test

import com.live.rrutt.newsgoggles.scala.RulesEngine._

object RulesEngineTest {
  
  def runAllTests: Boolean = {
    val finalResult =
      test1 &
	    test2 &
	    test3 &
	    true
    println("== Final Test Result: %s".format(finalResult.toString()))
    finalResult
  }
  
  def test1: Boolean = {
    val expected = "List(SubscriberFeed(Subscriber(@Alice),List(Article(1001,$ESPN,Tigers sign Prince Fielder.), Article(1002,$ESPN,Peyton Manning home team locker to be used by arch-nemesis Tom Brady?), Article(2001,$FOX,Newt nabs South Carolina.), Article(3001,$CNN,Obama visits Michigan.), Article(3002,$CNN,Obama urges congress to act in election year.), Article(4001,$MSNBC,Mitt makes moves in Florida.), Article(4002,$MSNBC,Celebrities attending Super Bowl parties.))), SubscriberFeed(Subscriber(@Bob),List(Article(1001,$ESPN,Tigers sign Prince Fielder.), Article(2001,$FOX,Newt nabs South Carolina.), Article(3001,$CNN,Obama visits Michigan.), Article(4001,$MSNBC,Mitt makes moves in Florida.))), SubscriberFeed(Subscriber(@Chris),List(Article(3001,$CNN,Obama visits Michigan.), Article(3002,$CNN,Obama urges congress to act in election year.), Article(4001,$MSNBC,Mitt makes moves in Florida.))), SubscriberFeed(Subscriber(@Pat),List(Article(1001,$ESPN,Tigers sign Prince Fielder.), Article(1002,$ESPN,Peyton Manning home team locker to be used by arch-nemesis Tom Brady?), Article(2001,$FOX,Newt nabs South Carolina.), Article(4001,$MSNBC,Mitt makes moves in Florida.))))"
    val actual = all_subscriber_feeds.toList.toString()
    println("++ test1 ++")
    println("Expected: %s".format(expected))
    println("Actual:   %s".format(actual))
    val result = (actual == expected)
    println("-- test1: %s".format(result.toString()))
    result
  }
  
  def test2: Boolean = {
    val expected = "List(ProviderReaders(Provider($CNN),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Chris))), ProviderReaders(Provider($ESPN),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Pat))), ProviderReaders(Provider($FOX),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Pat))), ProviderReaders(Provider($MSNBC),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Chris), Subscriber(@Pat))))"
    val actual = all_provider_readers.toList.toString()
    println("++ test2 ++")
    println("Expected: %s".format(expected))
    println("Actual:   %s".format(actual))
    val result = (actual == expected)
    println("-- test2: %s".format(result.toString()))
    result
  }
  
  def test3: Boolean = {
    val expected = "List(ArticleReaders(Article(1001,$ESPN,Tigers sign Prince Fielder.),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Pat))), ArticleReaders(Article(1002,$ESPN,Peyton Manning home team locker to be used by arch-nemesis Tom Brady?),List(Subscriber(@Alice), Subscriber(@Pat))), ArticleReaders(Article(2001,$FOX,Newt nabs South Carolina.),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Pat))), ArticleReaders(Article(2002,$FOX,Do we need to go back to the moon?),List()), ArticleReaders(Article(3001,$CNN,Obama visits Michigan.),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Chris))), ArticleReaders(Article(3002,$CNN,Obama urges congress to act in election year.),List(Subscriber(@Alice), Subscriber(@Chris))), ArticleReaders(Article(4001,$MSNBC,Mitt makes moves in Florida.),List(Subscriber(@Alice), Subscriber(@Bob), Subscriber(@Chris), Subscriber(@Pat))), ArticleReaders(Article(4002,$MSNBC,Celebrities attending Super Bowl parties.),List(Subscriber(@Alice))))"
    val actual = all_article_readers.toList.toString()
    println("++ test3 ++")
    println("Expected: %s".format(expected))
    println("Actual:   %s".format(actual))
    val result = (actual == expected)
    println("-- test3: %s".format(result.toString()))
    result
  }

}