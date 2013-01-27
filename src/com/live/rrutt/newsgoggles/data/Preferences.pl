/*
Rule-base of Subscriber preferences, designated as:
  <b>subscriber_likes(Subscriber, ProviderOrTopic).</b>
  <b>subscriber_dislikes(Subscriber, ProviderOrTopic).</b>
  <b>subscriber_allows(Subscriber, Provider, Topic).</b>
  <b>subscriber_blocks(Subscriber, Provider, Topic).</b>
*/

  subscriber_likes("@Alice", "#politics").
  subscriber_likes("@Alice", "#sports").
  subscriber_likes("@Alice", "$CNN").
  subscriber_likes("@Alice", "$MSNBC").
  
  subscriber_likes("@Bob", "#politics").
  subscriber_likes("@Bob", "#detroit").
  subscriber_dislikes("@Bob", "#democrats").
  subscriber_dislikes("@Bob", "$CNN").
  subscriber_allows("@Bob", "$CNN", "#detroit").
  
  subscriber_likes("@Chris", "#politics").
  subscriber_dislikes("@Chris", "$FOX").
  
  subscriber_likes("@Pat", "$FOX").
  subscriber_likes("@Pat", "#sports").
  subscriber_likes("@Pat", "#republicans").
  subscriber_blocks("@Pat", "$FOX", "#opinion").
  