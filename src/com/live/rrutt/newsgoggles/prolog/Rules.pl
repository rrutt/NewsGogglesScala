/* 
 * This is the original Prolog implementation.
 * It is provided here only as a reference.
 */
  
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
     - Providers are identified with a $ prefix. Examples: '$CNN' '$FOX' '$ESPN' '$CBC' '$NBC' '$Wired' '$NYTimes' 
     - Topics are identified with a # prefix. 
       Examples: '#world' '#usa' '#sports' '#entertainment' '#business' '#politics' '#opinion' 
     - Subscribers are identified with an @ prefix. 
       Examples: '@Alice' '@Bob' '@Chris' '@Pat' 
*/

/*
Helper "views" into the news feed data base and preferences rule base.
*/

% Given an Article identifier, fetch the Provider and one of its Topics.
% Back-track into this to obtain any alternate Topics.

article_provider_topic(A, P, T) :-
	article(A, P, _),
	article_topic(A, T),
	trace(' A:'), trace(A),
	trace(' P:'), trace(P),
	trace(' T:'), trace(T),
	trace_nl.

/*
Mid-level rules engine predicates.
*/
	
subscriber_likes_article(S, A) :-
	trace(S), trace(' likes P for A? '), trace(A), trace_nl,
	article(A, P, _),
	trace( 'A:'), trace(A),
	trace(' P:'), trace(P),
	trace_nl,
	subscriber_likes(S, P),
	trace('+ '), trace(S), trace(' likes P '), trace(P), trace_nl.
subscriber_likes_article(S, A) :-
	trace(S), trace(' likes T for A? '), trace(A), trace_nl,
	article_topic(A, T),
	trace( 'A:'), trace(A),
	trace(' T:'), trace(T),
	trace_nl,
	subscriber_likes(S, T),
	trace('+ '), trace(S), trace(' likes T '), trace(T), trace_nl.
	
subscriber_dislikes_article(S, A) :-
	trace(S), trace(' dislikes P for A? '), trace(A), trace_nl,
	article(A, P, _),
	trace( 'A:'), trace(A),
	trace(' P:'), trace(P),
	trace_nl,
	subscriber_dislikes(S, P),
	trace('- '), trace(S), trace(' dislikes P '), trace(P), trace_nl.
subscriber_dislikes_article(S, A) :-
	trace(S), trace(' dislikes T for A? '), trace(A), trace_nl,
	article_topic(A, T),
	trace( 'A:'), trace(A),
	trace(' T:'), trace(T),
	trace_nl,
	subscriber_dislikes(S, T),
	trace('- '), trace(S), trace(' dislikes T '), trace(T), trace_nl.
	
subscriber_allows_article(S, A) :-
	trace(S), trace(' allows P,T A? '), trace(A), trace_nl,
	article_provider_topic(A, P, T),
	subscriber_allows(S, P, T),
	trace('+ '), trace(S), trace(' allows P '), trace(P), trace(', T '), trace(T), trace_nl.
	
subscriber_blocks_article(S, A) :-
	trace(S), trace(' blocks P,T A? '), trace(A), trace_nl,
	article_provider_topic(A, P, T),
	subscriber_blocks(S, P, T),
	trace('- '), trace(S), trace(' blocks P '), trace(P), trace(', T '), trace(T), trace_nl.

/*
Top-level rules engine predicates.
*/

article_is_visible_to_subscriber(A, S) :-
	trace(A), trace(' allowed by S? '), trace(S), trace_nl,
	subscriber_blocks_article(S, A), !,
	trace('X '), trace(S), trace(' blocks A '), trace(A), trace_nl,
	fail.
article_is_visible_to_subscriber(A, S) :-
	subscriber_allows_article(S, A), !,
	trace('= '), trace(S), trace(' allows and does not block A '), trace(A), trace_nl.
article_is_visible_to_subscriber(A, S) :-
	subscriber_dislikes_article(S, A), !,
	trace('X '), trace(S), trace(' dislikes A '), trace(A), trace_nl,
	fail.
article_is_visible_to_subscriber(A, S) :-
	subscriber_likes_article(S, A), !,
	trace('= '), trace(S), trace(' likes and does not block nor dislike A '), trace(A), trace_nl.
article_is_visible_to_subscriber(A, S) :-
	trace('X '), trace(S), trace(' ignores A '), trace(A), trace_nl,
	fail.

/*
Helper predicates to work around apparent tuProlog bugs.
*/

% Workround for setof/3 when Goal contains _ "don't care" terms?

findall_nodups(Template, Goal, Instances) :-
	findall(Template, Goal, List),
	quicksort(List, '@<', OrderedList),
	no_duplicates(OrderedList, Instances).

/*
High-level result collection goal predicates.
*/

% Given a Subscriber,
% return the list of current Article identifiers that match their preferences.

subscriber_article_ids(S, ResultList) :-
	findall_nodups(A, article(A, _, _), AList),
	filter_articles_for_subscriber(AList, S, ResultList).

filter_articles_for_subscriber([], _, []).
filter_articles_for_subscriber([A | Tail], S, [A | TailList]) :-	
	article_is_visible_to_subscriber(A, S), !,
	filter_articles_for_subscriber(Tail, S, TailList).
filter_articles_for_subscriber([A | Tail], S, TailList) :-
	filter_articles_for_subscriber(Tail, S, TailList).

% Given a Subscriber,
% return the list of current Article details that match their preferences.

subscriber_articles(S, ResultList) :-
	subscriber_article_ids(S, ArticleIdList),
	load_article_list_details(ArticleIdList, ResultList).
	
load_article_list_details([], []).
load_article_list_details([Id | IdTail], [article(Id, Provider, Contents) | ATail]) :-
	article(Id, Provider, Contents),
	load_article_list_details(IdTail, ATail).
load_article_list_details([Id | IdTail], ATail) :-  % Discard any orphaned Article identifer.
	not(article(Id, _, _)),
	load_article_list_details(IdTail, ATail).
	
% Get master "list of lists" for all Subscriber/Article feeds.

all_subscriber_feeds(ResultList) :-
	findall_nodups(S, is_subscriber(S), SList),
	all_subscriber_feeds_for_list(SList, ResultList).

all_subscriber_feeds_for_list([], []).
all_subscriber_feeds_for_list([S | Tail], [subscriber_feed(S, AList) | TailList]) :-
	subscriber_articles(S, AList),
	all_subscriber_feeds_for_list(Tail, TailList).

is_subscriber(S) :-
	subscriber_likes(S, _);  % Semi-colon means OR.
	subscriber_allows(S, _, _).


% Given an Article, 
% return the list of Subscribers that the Article will reach.	

article_readers(A, ResultList) :-
	findall_nodups(S, is_subscriber(S), SList),
	filter_subscribers_for_article(SList, A, ResultList).

filter_subscribers_for_article([], _, []).
filter_subscribers_for_article([S | Tail], A, [S | TailList]) :-	
	article_is_visible_to_subscriber(A, S), !,
	filter_subscribers_for_article(Tail, A, TailList).
filter_subscribers_for_article([S | Tail], A, TailList) :-
	filter_subscribers_for_article(Tail, A, TailList).
	
% Get master "list of lists" for all Article/Subscriber links.

all_article_readers(ResultList) :-
	findall_nodups(A, article(A, _, _), AList),
	all_article_readers_for_list(AList, ResultList).

all_article_readers_for_list([], []).
all_article_readers_for_list([A | Tail], [article_reaches(A, SList) | TailList]) :-
	article_readers(A, SList),
	all_article_readers_for_list(Tail, TailList).


% Given a Provider, 
% return the list of Subscribers that receive at least one of the Provider's Articles.
% (Can be used to target advertising or premium direct content, etc.)	

provider_readers(P, ResultList) :-
	findall_nodups(S, is_subscriber(S), SList), !,
	trace(SList), trace_nl,
	find_provider_subscribers_for_list(P, SList, ResultList).

find_provider_subscribers_for_list(_, [], []).
find_provider_subscribers_for_list(P, [S | Tail], [S | NewTail]) :-
	provider_reaches_subscriber(P, S), !,
	find_provider_subscribers_for_list(P, Tail, NewTail).
find_provider_subscribers_for_list(P, [_ | Tail], NewTail) :-
	find_provider_subscribers_for_list(P, Tail, NewTail).

provider_reaches_subscriber(P, S) :-
	trace(P), trace(' reaches S? '), trace(S), trace_nl,
	article(Article, P, _),
	article_is_visible_to_subscriber(Article, S),
	trace(P), trace(' does reach '), trace(S), trace_nl.
		
% Get master "list of lists" for all Provider/Subscriber sets.

all_provider_readers(ResultList) :-
	findall_nodups(P, is_provider(P), PList),
	all_provider_readers_for_list(PList, ResultList).

all_provider_readers_for_list([], []).
all_provider_readers_for_list([P | Tail], [provider_subscribers(P, SList) | TailList]) :-
	provider_readers(P, SList),
	all_provider_readers_for_list(Tail, TailList).

is_provider(P) :-
	article(_, P, _).


% Print out the current Article feed for a Subscriber.	

show_news(S) :-
	nl, print('Articles subscribed by '), print(S), print(':'), nl,
	subscriber_articles(S, ArticleList),
	show_article_list(ArticleList).
	
show_article_list([]) :-
	print('-- End --'), nl.
show_article_list([article(_, Provider, Contents) | Tail]) :-
	print('  From: '), print(Provider), nl,
	print('    '), print(Contents), nl,
	show_article_list(Tail).

% Print out the current Article feed for all Subscribers.

show_all_news :-
	findall_nodups(S, is_subscriber(S), SList),
	show_news_for_list(SList).

show_news_for_list([]).
show_news_for_list([S | Tail]) :-
	show_news(S),
	show_news_for_list(Tail).
